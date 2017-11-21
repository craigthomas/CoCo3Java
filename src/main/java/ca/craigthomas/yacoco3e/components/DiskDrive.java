/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

public class DiskDrive
{
    public static final int TRACKS = 35;
    public static final int SECTORS = 18;
    public static final int BYTES_PER_SECTOR = 256;

    protected boolean motorOn;

    protected int track;
    protected int currentTrack;
    protected int sector;
    protected int currentSector;
    protected byte [] diskData;
    protected UnsignedByte dataRegisterOut;
    protected UnsignedByte dataRegisterIn;
    protected int direction;
    protected UnsignedByte statusRegister;
    protected int dataMark;

    protected boolean haltEnabled;

    protected int currentBytePointer;

    protected IOController io;

    protected boolean inCommand;

    protected int currentCommand;

    public DiskDrive(IOController io) {
        diskData = new byte [TRACKS * SECTORS * (BYTES_PER_SECTOR + 1)];
        motorOn = false;
        this.io = io;
        direction = -1;
        statusRegister = new UnsignedByte();
        dataRegisterOut = new UnsignedByte();
        dataRegisterIn = new UnsignedByte();
        currentCommand = -1;
        currentBytePointer = -1;
        dataMark = 0;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(UnsignedByte track) {
        this.track = track.getShort();
    }

    public int getSector() {
        return sector;
    }

    public void setSector(UnsignedByte sector) {
        this.sector = sector.getShort();
    }

    public void turnMotorOn() {
        motorOn = true;
    }

    public void turnMotorOff() {
        motorOn = false;
    }

    public void enableHalt() {
        haltEnabled = true;
    }

    public void disableHalt() {
        haltEnabled = false;
    }

    public void setDataRegister(UnsignedByte value) {
        if (inCommand) {
            switch (currentCommand) {
                case 0x0A:
                    writeSector(value);
                    return;

                case 0x0B:
                    writeMultipleSectors(value);
                    return;
            }
        }
    }

    public UnsignedByte getDataRegister() {
        if (inCommand) {
            switch (currentCommand) {
                case 0x08:
                    return readSector();

                case 0x09:
                    return readMultipleSectors();
            }
        }
        throw new RuntimeException("getDataRegister unrecognized command " + new UnsignedByte(currentCommand));
    }

    /**
     * Returns the contents of the status register.
     *
     * @return the status register
     */
    public UnsignedByte getStatusRegister() {
        return statusRegister;
    }

    public void setBusy() {
        statusRegister.or(0x01);
    }

    public void setNotBusy() {
        statusRegister.and(~0x01);
    }

    public void setDRQ() {
        statusRegister.or(0x02);
    }

    public void clearDRQ() {
        statusRegister.and(~0x02);
    }

    public void fireInterrupt() {
        io.nonMaskableInterrupt();
    }

    /**
     * Executes the specified command on the disk.
     *
     * @param command the command to execute
     */
    public void executeCommand(UnsignedByte command) {
        int intCommand = command.getShort() >> 4;
        boolean verify = command.isMasked(0x04);

        /* Set busy flag on the status register */
        setBusy();

        /* Restore - seek track 0 */
        if (intCommand == 0x0) {
            restore(verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Seek - seek to track specified in track register */
        if (intCommand == 0x1) {
            seek(verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Step without Update - steps once in the last direction */
        if (intCommand == 0x2) {
            step(false, verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Step with Update - steps once in the last direction */
        if (intCommand == 0x3) {
            step(true, verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Step In without Update - steps once towards track 76 */
        if (intCommand == 0x4) {
            stepIn(false, verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Step In with Update - steps once towards track 76 */
        if (intCommand == 0x5) {
            stepIn(true, verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Step Out without Update - steps once towards track 0 */
        if (intCommand == 0x6) {
            stepOut(false, verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Step In with Update - steps once towards track 0 */
        if (intCommand == 0x7) {
            stepOut(true, verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Read single sector */
        if (intCommand == 0x08) {
            currentBytePointer = 0;
            currentCommand = intCommand;
            inCommand = true;
            setBusy();
            setDRQ();
            return;
        }

        /* Read multiple sectors */
        if (intCommand == 0x09) {
            currentBytePointer = 0;
            currentCommand = intCommand;
            inCommand = true;
            setBusy();
            setDRQ();
            return;
        }

        /* Write single sector */
        if (intCommand == 0x0A) {
            currentBytePointer = 0;
            currentCommand = intCommand;
            inCommand = true;
            dataMark = command.isMasked(0x1) ? 1 : 0;
            setBusy();
            setDRQ();
            return;
        }

        /* Write multiple sectors */
        if (intCommand == 0x0B) {
            currentBytePointer = 0;
            currentCommand = intCommand;
            inCommand = true;
            dataMark = command.isMasked(0x1) ? 1 : 0;
            setBusy();
            setDRQ();
            return;
        }
    }

    /**
     * Seeks to track 0.
     */
    public void restore(boolean verify) {
        currentTrack = 0;
        direction = -1;
        setTrack(new UnsignedByte(0));
    }

    /**
     * Seeks to the track specified in the data register.
     */
    public void seek(boolean verify) {
        int trackToSeek = dataRegisterIn.getShort();
        while (currentTrack > trackToSeek) {
            direction = -1;
            currentTrack--;
        }

        while (currentTrack < trackToSeek) {
            direction = 1;
            currentTrack++;
        }

        setTrack(dataRegisterIn);
    }

    /**
     * Steps once in the last direction.
     *
     * @param update if true, update the track status register
     * @param verify if true, verifies the last operation succeeded
     */
    public void step(boolean update, boolean verify) {
        currentTrack += direction;
        if (update) {
            setTrack(new UnsignedByte(currentTrack));
        }
    }

    /**
     * Steps once towards track 76.
     *
     * @param update if true, update the track status register
     * @param verify if true, verifies the last operation succeeded
     */
    public void stepIn(boolean update, boolean verify) {
        direction = 1;
        step(update, verify);
    }

    /**
     * Steps once towards track 0.
     *
     * @param update if true, update the track status register
     * @param verify if true, verifies the last operation succeeded
     */
    public void stepOut(boolean update, boolean verify) {
        direction = -1;
        step(update, verify);
    }

    /**
     * Reads the current byte of data on the disk.
     *
     * @return returns the current byte of data under the drive head
     */
    public UnsignedByte getCurrentByte() {
        int currentLocation = currentTrack * SECTORS * (BYTES_PER_SECTOR + 1);
        currentLocation += currentSector * (BYTES_PER_SECTOR + 1);
        currentLocation += currentBytePointer;
        return new UnsignedByte(diskData[currentLocation]);
    }

    /**
     * Reads a single sector worth of bytes.
     *
     * @return the next byte in the sector
     */
    public UnsignedByte readSector() {
        /* Check to see if we can read more bytes on this sector */
        if (currentBytePointer < BYTES_PER_SECTOR) {
            UnsignedByte result = getCurrentByte();
            currentBytePointer++;
            return result;
        }

        /* We've read the entire sector, set not busy, and interrupt */
        UnsignedByte dataMark = getCurrentByte();
        if (!dataMark.isZero()) {
            statusRegister.or(0x20);
        }
        currentBytePointer = 0;
        inCommand = false;
        currentCommand = -1;
        setNotBusy();
        clearDRQ();
        fireInterrupt();
        return new UnsignedByte(0);
    }

    /**
     * Reads multiple sectors. Will continue reading sectors sequentially until
     * all of the sectors on the track have been read.
     *
     * @return the next byte in the sector
     */
    public UnsignedByte readMultipleSectors() {
        /* Check to see if we've finished reading the current sector */
        if (currentBytePointer < BYTES_PER_SECTOR) {
            currentBytePointer++;
            return getCurrentByte();
        }

        /* Advance the sector counter if necessary */
        if (currentSector < SECTORS) {
            UnsignedByte dataMark = getCurrentByte();
            if (!dataMark.isZero()) {
                statusRegister.or(0x20);
            }
            currentSector++;
            currentBytePointer = 0;
            return readMultipleSectors();
        }

        /* We've read all the sectors, set not busy, and interrupt */
        currentSector--;
        currentBytePointer = 0;
        inCommand = false;
        currentCommand = -1;
        setNotBusy();
        clearDRQ();
        fireInterrupt();
        return new UnsignedByte(0);
    }

    public void writeCurrentByte(UnsignedByte value) {
        int currentLocation = currentTrack * SECTORS * (BYTES_PER_SECTOR + 1);
        currentLocation += currentSector * (BYTES_PER_SECTOR + 1);
        currentLocation += currentBytePointer;
        diskData[currentLocation] = (byte) value.getShort();
    }

    public void writeDataMark() {
        int markLocation = currentTrack * SECTORS * (BYTES_PER_SECTOR + 1);
        markLocation += currentSector * (BYTES_PER_SECTOR + 1);
        markLocation += BYTES_PER_SECTOR;
        diskData[markLocation] = (byte) dataMark;
    }

    public void writeSector(UnsignedByte value) {
        /* Write the data mark first */
        writeDataMark();

        /* Check to see if we can write more bytes on this sector */
        if (currentBytePointer < BYTES_PER_SECTOR) {
            writeCurrentByte(value);
            currentBytePointer++;
            return;
        }

        /* We've written the entire sector, set not busy, and interrupt */
        currentBytePointer = 0;
        inCommand = false;
        currentCommand = -1;
        setNotBusy();
        clearDRQ();
        fireInterrupt();
    }

    public void writeMultipleSectors(UnsignedByte value) {
        /* Write the data mark first */
        writeDataMark();

        /* Check to see if we can write more bytes on this sector */
        if (currentBytePointer < BYTES_PER_SECTOR) {
            writeCurrentByte(value);
            currentBytePointer++;
            return;
        }

        /* Advance the sector counter if necessary */
        if (currentSector < SECTORS) {
            UnsignedByte dataMark = getCurrentByte();
            if (!dataMark.isZero()) {
                statusRegister.or(0x20);
            }
            currentSector++;
            currentBytePointer = 0;
            writeMultipleSectors(value);
            return;
        }

        /* We've written the entire sector, set not busy, and interrupt */
        currentSector--;
        currentBytePointer = 0;
        inCommand = false;
        currentCommand = -1;
        setNotBusy();
        clearDRQ();
        fireInterrupt();
    }
}
