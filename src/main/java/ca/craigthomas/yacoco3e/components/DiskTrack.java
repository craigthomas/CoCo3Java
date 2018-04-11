/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */

package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

/**
 * Represents a track on a disk.
 */
public class DiskTrack
{
    // The sectors on the track
    protected DiskSector [] sectors;
    // The sector the drive read/write head is positioned above
    protected int currentSector;
    // Whether the read track operation is finished
    protected boolean readTrackFinished;
    // Whether the write track operation is finished
    protected boolean writeTrackFinished;
    // If the disk is double density
    private boolean doubleDensity;

    public DiskTrack(int numSectors, boolean doubleDensity) {
        sectors = new DiskSector[numSectors];
        for (int i=0; i < numSectors; i++) {
            sectors[i] = new DiskSector(doubleDensity);
        }
        this.doubleDensity = doubleDensity;
    }

    /**
     * Returns the number of sectors on the track.
     *
     * @return the number of sectors on the track
     */
    public int getNumberOfSectors() {
        return sectors.length;
    }

    /**
     * Writes a single byte to the specified sector on the track.
     * The sector must have the WRITE_SECTOR command applied to it
     * before the start of a write operation (subsequent writes do
     * not have to set the write command).
     *
     * @param sector the sector number to write to
     * @param value the value to write to the sector
     */
    public void writeData(int sector, UnsignedByte value) {
        sectors[sector].writeSectorData((byte) value.getShort());
    }

    /**
     * Reads a single byte from the specified sector on the track.
     * The sector must have the READ_SECTOR command applied to it
     * before the start of a read operation (subsequent reads do not
     * have to set the read command)
     *
     * @param sector the sector to read
     * @return the byte at the current sector location
     */
    public UnsignedByte readData(int sector) {
        return new UnsignedByte(sectors[sector].readSectorData());
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
     * Returns true if there are more data bytes to be read from a data field
     * of a sector, false otherwise.
     *
     * @param sector the sector to read
     * @return true if there are more data bytes
     */
    public boolean hasMoreDataBytes(int sector) {
        return sectors[sector].hasMoreDataBytes();
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

    /**
     * Writes a data mark value to the disk.
     *
     * @param sector the sector number to write to
     * @param mark the value of the mark
     */
    public void writeDataMark(int sector, UnsignedByte mark) {
        sectors[sector].writeDataMark((byte) mark.getShort());
    }

    /**
     * Sets the current command to run on the sector.
     *
     * @param sector the sector to operate on
     * @param command the command to run
     */
    public void setCommand(int sector, DiskCommand command) {
        sectors[sector].setCommand(command);
    }

    /**
     * Starts a read track operation.
     */
    public void startReadTrack() {
        currentSector = 0;
        sectors[currentSector].setCommand(DiskCommand.READ_TRACK);
        readTrackFinished = false;
    }

    /**
     * Starts a write track operation.
     */
    public void startWriteTrack() {
        currentSector = 0;
        sectors[currentSector].setCommand(DiskCommand.WRITE_TRACK);
        writeTrackFinished = false;
    }

    /**
     * Reads a byte from a track. The function keeps track of the
     * current sector that is currently being read from.
     *
     * @return the next byte from the track
     */
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

    /**
     * Writes a byte to a track. Will keep track of successive sectors
     * that it is writing to.
     *
     * @param value the value to write to the track
     */
    public void writeTrack(UnsignedByte value) {
        /* Check to see if no more sectors */
        if (currentSector >= sectors.length) {
            writeTrackFinished = true;
            return;
        }

        /* Check to see if we are done with the current sector */
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
     * Returns true if the read track operation is finished (no
     * more bytes to read), or false otherwise.
     *
     * @return returns true if read track is finished
     */
    public boolean isReadTrackFinished() {
        return readTrackFinished;
    }

    /**
     * Returns true if the write track operation is finished (no
     * more bytes to write), or false otherwise.
     *
     * @return returns true if the write track is finished
     */
    public boolean isWriteTrackFinished() {
        return writeTrackFinished;
    }

    /**
     * This function will return the logical sector number as recorded on the
     * sector by the OS. It uses the sector number on the ID field of the sector
     * to return the correct mapping. Will return -1 if the logical sector number
     * did not exist anywhere in the collection of sectors.
     *
     * @param sector the logical sector number to search for
     * @return the actual sector number where the logical sector is stored
     */
    public int getLogicalSector(int sector) {
        for (int i = 0; i < sectors.length; i++) {
            if (sector == sectors[i].getSectorId()) {
                return i;
            }
        }
        return -1;
    }

    public void writeSectorId(int sector, UnsignedByte value) {
        sectors[sector].writeId((byte) value.getShort());
    }
}
