/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */

package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

public class DiskTrack
{
    private DiskSector [] sectors;
    private int currentSector;
    private boolean readTrackFinished;
    private boolean writeTrackFinished;
    private boolean doubleDensity;

    public DiskTrack(int numSectors, boolean doubleDensity) {
        sectors = new DiskSector[numSectors];
        for (int i=0; i < numSectors; i++) {
            sectors[i] = new DiskSector(doubleDensity);
        }
        this.doubleDensity = doubleDensity;
    }

    public void write(int sector, UnsignedByte value) {
        sectors[sector].writeData((byte) value.getShort());
    }

    public void writeTrack(UnsignedByte value) {
        if (currentSector >= sectors.length) {
            writeTrackFinished = true;
            return;
        }

        if (sectors[currentSector].writeTrackFinished()) {
            sectors[currentSector].setCommand(DiskCommand.NONE);
            currentSector++;
            if (currentSector < sectors.length) {
                sectors[currentSector].setCommand(DiskCommand.WRITE_TRACK);
            } else {
                writeTrackFinished = true;
                return;
            }
        }

        /* Check to see if we have a byte that has a different interpretation */
        byte byteValue = (byte) value.getShort();
        if (doubleDensity) {
            switch (byteValue) {
                case (byte) 0xF5:
                    sectors[currentSector].writeTrack((byte) 0xA1);
                    /* TODO: Turn on CRC calculation */
                    break;

                case (byte) 0xF6:
                    sectors[currentSector].writeTrack((byte) 0xC2);
                    break;

                case (byte) 0xF7:
                    /* Below should be CRC byte 1, CRC byte 2 */
                    sectors[currentSector].writeTrack((byte) 0x0);
                    sectors[currentSector].writeTrack((byte) 0x0);
                    break;

                default:
                    sectors[currentSector].writeTrack(byteValue);
                    break;
            }
        } else {
            sectors[currentSector].writeTrack(byteValue);
        }
    }

    /**
     * Attempts to read a byte from the specified sector.
     *
     * @param sector the sector to read
     * @return the byte at the current sector location
     */
    public UnsignedByte read(int sector) {
        return new UnsignedByte(sectors[sector].readSector());
    }

    /**
     * Reads the address of the specified sector.
     *
     * @param sector the sector read from
     * @return the next byte in the sequence of reading from the specified address
     */
    public UnsignedByte readAddress(int sector) {
        return new UnsignedByte(sectors[sector].readAddress());
    }

    /**
     * Returns true if there is a data address mark for the sector, false otherwise.
     *
     * @param sector the sector to read from
     * @return true if there is a data address mark
     */
    public boolean dataAddressMarkFound(int sector) {
        return sectors[sector].dataAddressMarkFound();
    }

    /**
     * Returns true if there are more bytes to be read from a data field of a
     * sector, false otherwise.
     *
     * @param sector the sector to read from
     * @return true if there are more data bytes to be read
     */
    public boolean hasMoreBytes(int sector) {
        return sectors[sector].hasMoreBytes();
    }

    /**
     * Returns true if there are more bytes to be read from an ID field of a
     * sector, false otherwise.
     *
     * @param sector the sector to read from
     * @return true if there are more id bytes to be read
     */
    public boolean hasMoreIdBytes(int sector) {
        return sectors[sector].hasMoreIdBytes();
    }

    public void writeDataMark(int sector, UnsignedByte mark) {
        sectors[sector].writeDataMark((byte) mark.getShort());
    }

    /**
     * Sets the current command to run on the sector.
     *
     * @param sector
     * @param command
     */
    public void setCommand(int sector, DiskCommand command) {
        sectors[sector].setCommand(command);
    }

    public void startReadTrack() {
        currentSector = 0;
        sectors[currentSector].setCommand(DiskCommand.READ_TRACK);
        readTrackFinished = false;
    }

    public void startWriteTrack() {
        currentSector = 0;
        sectors[currentSector].setCommand(DiskCommand.WRITE_TRACK);
        writeTrackFinished = false;
    }

    public UnsignedByte readTrack() {
        if (sectors[currentSector].readTrackFinished()) {
            currentSector++;
            if (currentSector >= sectors.length) {
                readTrackFinished = true;
                return new UnsignedByte(0);
            }
            sectors[currentSector].setCommand(DiskCommand.READ_TRACK);
        }

        UnsignedByte result = new UnsignedByte(sectors[currentSector].readTrack());
        if (sectors[currentSector].readTrackFinished()) {
            currentSector++;
            if (currentSector >= sectors.length) {
                readTrackFinished = true;
            } else {
                sectors[currentSector].setCommand(DiskCommand.READ_TRACK);
            }
        }

        return result;
    }

    public boolean isReadTrackFinished() {
        return readTrackFinished;
    }

    public boolean isWriteTrackFinished() {
        return writeTrackFinished;
    }
}
