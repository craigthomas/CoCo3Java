/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.common.IO;
import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

import java.io.*;
import java.util.logging.Logger;

public class Cassette
{
    public enum Mode {
        PLAY, RECORD
    }

    /* Whether the drive motor is on */
    private boolean motorOn;

    /* The current mode */
    private Mode mode;

    /* The offset into the current cassette data */
    private int offset;

    /* The name of the file to open or save */
    private String filename;

    /* The bytes that are contained within the cassette file */
    protected byte [] cassetteBytes;

    /* The bit in the byte that we are transmitting */
    private int bitOffset;

    /* The sequences of 0s or 1s we will send to the emulator when we see a 0 or 1 */
    private byte [] outputBuffer;

    /* How far along the output buffer we have moved */
    private int outputBufferPtr;

    /* The input buffer for the cassette */
    protected byte [] inputBuffer;

    /* How many high values we have seen when outputting to cassette */
    private int inputHighCounter;

    /* Where in the inputBuffer we are storing the current byte */
    private int inputByteOffset;

    /* How many bits along an input byte we have seen */
    private int inputBitOffset;

    /* The actual byte we have output to the cassette */
    private byte inputByte;

    /* The last input bit we saw */
    private byte lastInputBit;

    /* A logger for the cassette drive */
    private static final Logger LOGGER = Logger.getLogger(Cassette.class.getName());

    public Cassette() {
        mode = Mode.PLAY;
        rewind();
    }

    /**
     * Turns the cassette motor on or off.
     */
    public void setMotorOn(boolean turnOn) {
        motorOn = turnOn;
    }

    public boolean isMotorOn() {
        return motorOn;
    }

    /**
     * Sets the cassette recorder to playback mode.
     */
    public void play() {
        this.mode = Mode.PLAY;
    }

    /**
     * Rewinds the current tape to the start.
     */
    public void rewind() {
        offset = 0;
        bitOffset = 0x1;
        refillBuffer();
    }

    /**
     * Sets the cassette recorder to record mode.
     */
    public void record() {
        this.mode = Mode.RECORD;
        inputBuffer = new byte [0x8000];
        inputBitOffset = 0;
        inputByte = 0;
        inputHighCounter = 0;
    }

    /**
     * The refillBuffer function will place the next set of signals to read
     * in the outputBuffer. If the next bit to output is a 1, it will output
     * 5 ones followed by 5 zeros. If the next bit to output is a 0, it will
     * output 11 ones followed by 11 zeros.
     */
    private void refillBuffer() {
        /* If we have read all 8 bits, advance the offset and start from bit 0 */
        if (bitOffset > 0x80) {
            bitOffset = 0x1;
            offset++;
        }

        /* Check to make sure the offset is within range, and refill the buffer */
        if (cassetteBytes != null && offset < cassetteBytes.length) {
            UnsignedByte currentByte = new UnsignedByte(cassetteBytes[offset]);
            outputBuffer = (currentByte.isMasked(bitOffset)) ?
                    new byte[] {1, 1, 1, 1, 1, 0, 0, 0, 0, 0} :
                    new byte[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        }
        outputBufferPtr = 0;
    }

    /**
     * Returns the next bit read from the cassette data file. Will
     * return 0 if the motor is off, if not in playback mode, or
     * the tape has run past the end of the cassette data.
     *
     * The cassette works by outputting a tone at a certain frequency.
     * At 1200 Hz, it is assumed to be a 0, and at 2400 Hz it is assumed
     * to be a 1. This is for the Basic ROM only. Since we don't have
     * exact timing data, we fake it based on the counter basic uses
     * to determine if there is a zero or a one. Essentially, we need
     * to transition from high to low at a certain rate. For a zero, we
     * have 5 read cycles of ones followed by a transition from 1 to 0
     * and 5 further read cycles of 0. For a one, we have 11 read cycles
     * of ones followed by a transition of 1 to 0 and 11 further read
     * cycles of 0. This trick will work only with Basic. Any other
     * method of reading back tape data will fail.
     *
     * @return the next available cassette bit
     */
    public int nextBit() {
        /* If we are past the end of the cassette, return zero */
        if (cassetteBytes == null || offset >= cassetteBytes.length) {
            return 0;
        }

        /* If we are not in playback mode, or the motor is off, return zero */
        if (mode != Mode.PLAY || !motorOn) {
            return 0;
        }

        /* Grab the next signal to output */
        int signal = outputBuffer[outputBufferPtr];
        outputBufferPtr++;

        /* Check to see if the buffer should be refilled */
        if (outputBufferPtr >= outputBuffer.length) {
            bitOffset = bitOffset << 1;
            refillBuffer();
        }

        return signal;
    }

    /**
     * Receives a byte, and translates it to a voltage. Instead of
     * recording raw voltage, translate back to a digital signal. Only
     * converts bits 7 - 2 as the incoming voltage.
     *
     * @param newByte the byte to interpret
     */
    public void byteInput(UnsignedByte newByte) {
        /* Make sure we are set to record */
        if (mode != Mode.RECORD) {
            return;
        }

        float voltage = 0.0f;

        /* Interpret the voltage based on bit value set */
        voltage += (newByte.isMasked(0x80)) ? 2.25f  : 0.0f;
        voltage += (newByte.isMasked(0x40)) ? 1.125f : 0.0f;
        voltage += (newByte.isMasked(0x20)) ? 0.563f : 0.0f;
        voltage += (newByte.isMasked(0x10)) ? 0.281f : 0.0f;
        voltage += (newByte.isMasked(0x08)) ? 0.14f  : 0.0f;
        voltage += (newByte.isMasked(0x04)) ? 0.07f  : 0.0f;

        /* Increment the number of 1s that we saw */
        if (voltage < 1.0f) {
            if (lastInputBit == 1) {
                if (inputHighCounter < 2) {
                    lastInputBit = 0;
                    inputHighCounter = 0;
                } else {
                    int value = (inputHighCounter >= 18) ? 0 : 1;
                    value = value << inputBitOffset;
                    inputByte = (byte) (inputByte + value);
                    inputBitOffset++;
                    lastInputBit = 0;
                    inputHighCounter = 0;
                }
            }
        } else {
            inputHighCounter++;
            lastInputBit = 1;
        }

        /* Check to see if we should move on to the next byte */
        if (inputBitOffset >= 8) {
            inputBitOffset = 0;
            inputBuffer[inputByteOffset] = inputByte;
            inputByteOffset++;
            inputByte = 0;
        }
    }

    /**
     * Attempts to open the specified file. If the current mode is set to
     * PLAY, then the file will be opened in read only mode. If the current
     * mode is RECORD, then the file will be opened for writing.
     *
     * @param cassetteFile the name of the file to open
     * @return true if the file could be opened, false otherwise
     */
    public boolean openFile(String cassetteFile) {
        filename = cassetteFile;
        if (mode == Mode.RECORD) {
            return true;
        }

        cassetteBytes = IO.loadStream(IO.openInputStream(cassetteFile));
        if (cassetteBytes.length == 0) {
            this.record();
            return true;
        }

        rewind();
        checkForEOF();
        return true;
    }

    /**
     * Flushes the current input buffer to the cassette file.
     */
    public void flushBufferToFile() {
        OutputStream stream = IO.openOutputStream(filename);
        IO.flushToStream(stream, inputBuffer);
        IO.closeStream(stream);
    }

    /**
     * Returns the current mode of the cassette player.
     *
     * @return will return the current cassette player mode.
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Checks to make sure that the buffer contains a proper EOF block.
     * If it does not, will correct it to ensure that it does.
     *
     * @return true if proper EOF found or EOF corrected, false otherwise
     */
    public boolean checkForEOF() {
        int length = cassetteBytes.length;

        if (cassetteBytes[length - 1] == 0x55 && cassetteBytes[length - 6] == 0x55) {
            return true;
        }

        LOGGER.warning("cassette data contains malformed EOF block - correcting");

        // Backup until we hit the last 0x55 characters
        int lastIndexOf55 = 0;
        for (int ptr = length -1; ptr > 0; ptr--) {
            if (cassetteBytes[ptr] == 0x55) {
                lastIndexOf55 = ptr;
                break;
            }
        }

        // Check to see if there is another 0x55 before it
        if (cassetteBytes[lastIndexOf55 - 1] != 0x55) {
            LOGGER.severe("unable to correct tape pattern, please check data manually");
            return false;
        }

        // Create a new array for the cassette bytes and copy old to new
        int truncatedLength = length - (length - lastIndexOf55);
        byte [] newCassetteBytes = new byte[truncatedLength + 6];
        System.arraycopy(cassetteBytes, 0, newCassetteBytes, 0, lastIndexOf55);

        // Add the trailing information for EOF
        newCassetteBytes[truncatedLength] = 0x55;
        newCassetteBytes[truncatedLength + 1] = 0x3C;
        newCassetteBytes[truncatedLength + 2] = (byte) 0xFF;
        newCassetteBytes[truncatedLength + 3] = 0x00;
        newCassetteBytes[truncatedLength + 4] = (byte) 0xFF;
        newCassetteBytes[truncatedLength + 5] = 0x55;
        cassetteBytes = newCassetteBytes;
        return true;
    }
}
