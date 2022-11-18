/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import ca.craigthomas.yacoco3e.datatypes.UnsignedWord;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CassetteTest
{
    private Cassette cassette;

    @Before
    public void setUp() {
        cassette = new Cassette();
    }

    @Test
    public void testMotorOn() {
        assertFalse(cassette.isMotorOn());
        cassette.motorOn();
        assertTrue(cassette.isMotorOn());
    }

    @Test
    public void testMotorOff() {
        assertFalse(cassette.isMotorOn());
        cassette.motorOn();
        cassette.motorOff();
        assertFalse(cassette.isMotorOn());
    }

    @Test
    public void testPlayModeWhenInitialized() {
        assertEquals(Cassette.Mode.PLAY, cassette.getMode());
    }

    @Test
    public void testSetRecordMode() {
        cassette.record();
        assertEquals(Cassette.Mode.RECORD, cassette.getMode());
    }

    @Test
    public void testSetPlayMode() {
        cassette.record();
        cassette.play();
        assertEquals(Cassette.Mode.PLAY, cassette.getMode());
    }

    @Test
    public void testNextBitReturnsZeroWhenNoFileLoaded() {
        for (int i=0; i < 100; i++) {
            assertEquals(0, cassette.nextBit());
        }
    }

    @Test
    public void testNextBitReturnsZeroWhenNoFileLoadedByteByteArrayInitialized() {
        cassette.cassetteBytes = new byte[] {};
        for (int i=0; i < 100; i++) {
            assertEquals(0, cassette.nextBit());
        }
    }

    @Test
    public void testNextBitReturnsZeroWhenFileButMotorOff() {
        cassette.cassetteBytes = new byte[] {1};
        cassette.motorOff();
        for (int i=0; i < 100; i++) {
            assertEquals(0, cassette.nextBit());
        }
    }

    @Test
    public void testNextBitReturnsZeroWhenFileButNotPlayMode() {
        cassette.cassetteBytes = new byte[] {1};
        cassette.motorOn();
        cassette.record();
        for (int i=0; i < 100; i++) {
            assertEquals(0, cassette.nextBit());
        }
    }

    @Test
    public void testReadCorrectBitPatterns() {
        cassette.cassetteBytes = new byte[] {0x4D};
        cassette.rewind();
        cassette.motorOn();

        /* Byte 0, bit 0 = 1 */
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());

        /* Byte 0, bit 1 = 0 */
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());

        /* Byte 0, bit 2 = 1 */
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());

        /* Byte 0, bit 3 = 1 */
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());

        /* Byte 0, bit 4 = 0 */
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());

        /* Byte 0, bit 5 = 0 */
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());

        /* Byte 0, bit 6 = 1 */
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());

        /* Byte 0, bit 7 = 0 */
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(1, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
        assertEquals(0, cassette.nextBit());
    }

    @Test
    public void testByteInputRecordsCorrectByte() {
        cassette.inputBuffer = new byte[2];
        cassette.rewind();
        cassette.record();
        cassette.motorOn();

        /* Record the pattern F0 */

        /* Bit 0 = 0 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 1 = 0 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 2 = 0 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 3 = 0 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 4 = 1 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 5 = 1 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 6 = 1 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 7 = 1 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        assertEquals((byte) 0xF0, cassette.inputBuffer[0]);
    }

    @Test
    public void testByteInputShortPulsesIgnored() {
        cassette.inputBuffer = new byte[2];
        cassette.rewind();
        cassette.record();
        cassette.motorOn();

        /* Record the pattern F0 */

        /* Short pulse, ignored */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Short pulse, ignored */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 0 = 0 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 1 = 0 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Short pulse, ignored */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 2 = 0 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 3 = 0 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 4 = 1 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 5 = 1 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 6 = 1 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        /* Bit 7 = 1 */
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x80));
        cassette.byteInput(new UnsignedByte(0x00));

        assertEquals((byte) 0xF0, cassette.inputBuffer[0]);
    }

    @Test
    public void testOpenFileDoesNothingWhenInRecordMode() {
        cassette.record();
        assertTrue(cassette.openFile("this-file-does-not-exist-yet.cas"));
    }

//    @Test
//    public void testOpenFileReturnsFalseWhenNullFile() {
//        assertFalse(cassette.openFile("this-file-should-not-exist.cas"));
//    }

    @Test
    public void testCheckForEOFDoesNothingOnCorrectEOFBlock() {
        byte [] goodBytes = {0x55, 0x55, 0x3C, (byte) 0xFF, 0x00, (byte) 0xFF, 0x55};
        cassette.cassetteBytes = goodBytes;
        assertTrue(cassette.checkForEOF());
        assertArrayEquals(goodBytes, cassette.cassetteBytes);
    }

    @Test
    public void testCheckForEOFCannotCorrectBadByteSequence() {
        byte [] badBytes = {0x00, 0x55, 0x3C, (byte) 0xFF, 0x00, (byte) 0xFF};
        cassette.cassetteBytes = badBytes;
        assertFalse(cassette.checkForEOF());
        assertArrayEquals(badBytes, cassette.cassetteBytes);
    }

    @Test
    public void testCheckForEOFCorrectsBadByteSequence() {
        byte [] badBytes = {0x55, 0x55, 0x3C, (byte) 0xFF, 0x00, (byte) 0xFF};
        byte [] goodBytes = {0x55, 0x55, 0x3C, (byte) 0xFF, 0x00, (byte) 0xFF, 0x55};
        cassette.cassetteBytes = badBytes;
        assertTrue(cassette.checkForEOF());
        assertArrayEquals(goodBytes, cassette.cassetteBytes);
    }
}
