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

    public DiskTrack(int numSectors, boolean doubleDensity) {
        sectors = new DiskSector[numSectors];
        for (int i=0; i < numSectors; i++) {
            sectors[i] = new DiskSector(doubleDensity);
        }
    }

    public void write(int sector, UnsignedByte value) {
        sectors[sector].writeData((byte) value.getShort());
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
}
