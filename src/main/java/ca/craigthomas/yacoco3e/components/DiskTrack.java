/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */

package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

public class DiskTrack
{
    private DiskSector [] sectors;

    public DiskTrack(int numSectors, boolean doubleDensity) {
        sectors = new DiskSector[numSectors];
        for (int i=0; i < numSectors; i++) {
            sectors[i] = new DiskSector(doubleDensity);
        }
    }

    public void seekSector(int sectorNum) {
        //sectors[sectorNum];
    }

    public void write(int sectorNum, UnsignedByte value) {
        //sectors[sectorNum].writeByte(value);
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

    public void setCommand(int sector, DiskCommand command) {
        sectors[sector].setCommand(command);
    }
}
