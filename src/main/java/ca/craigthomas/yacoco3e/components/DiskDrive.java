/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

public class DiskDrive
{
    public static final int DEFAULT_NUM_TRACKS = 35;
    public static final int DEFAULT_SECTORS_PER_TRACK = 18;

    protected boolean motorOn;

    protected DiskTrack [] tracks;
    protected int track;
    protected int currentTrack;
    protected int sector;
    protected int currentSector;
    protected UnsignedByte dataRegisterOut;
    protected UnsignedByte dataRegisterIn;
    protected int direction;
    protected UnsignedByte statusRegister;
    protected UnsignedByte dataMark;

    protected boolean haltEnabled;

    protected int currentBytePointer;

    protected IOController io;

    protected boolean inCommand;

    protected DiskCommand currentCommand;

    protected int sectorsPerTrack;

    protected int tracksPerDisk;

    public DiskDrive(IOController io) {
        this(io, DEFAULT_NUM_TRACKS, DEFAULT_SECTORS_PER_TRACK, true);
    }

    public DiskDrive(IOController io, int numTracks, int numSectors, boolean doubleDensity) {
        tracks = new DiskTrack[numTracks];
        for (int i=0; i < numTracks; i++) {
            tracks[i] = new DiskTrack(numSectors, doubleDensity);
        }
        tracksPerDisk = numTracks;
        sectorsPerTrack = numSectors;
        motorOn = false;
        this.io = io;
        direction = -1;
        statusRegister = new UnsignedByte();
        dataRegisterOut = new UnsignedByte();
        dataRegisterIn = new UnsignedByte();
        currentCommand = DiskCommand.NONE;
        currentBytePointer = -1;
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
        switch (currentCommand) {
            case WRITE_SECTOR:
                writeSector(value);
                return;

            case WRITE_MULTIPLE_SECTORS:
                writeMultipleSectors(value);
                return;
        }
    }

    public UnsignedByte getDataRegister() {
        switch (currentCommand) {
            case READ_SECTOR:
                return readSector();

            case READ_MULTIPLE_SECTORS:
                return readMultipleSectors();

            case READ_ADDRESS:
                return readAddress();

            case READ_TRACK:
                return readTrack();

            default:
                throw new RuntimeException("getDataRegister unrecognized command " + currentCommand);
        }
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
            System.out.println("Restore");
            return;
        }

        /* Seek - seek to track specified in track register */
        if (intCommand == 0x1) {
            seek(verify);
            setNotBusy();
            fireInterrupt();
            System.out.println("Seek");
            return;
        }

        /* Step without Update - steps once in the last direction */
        if (intCommand == 0x2) {
            step(false, verify);
            setNotBusy();
            fireInterrupt();
            System.out.println("Step (without update)");
            return;
        }

        /* Step with Update - steps once in the last direction */
        if (intCommand == 0x3) {
            step(true, verify);
            setNotBusy();
            fireInterrupt();
            System.out.println("Step (with update)");
            return;
        }

        /* Step In without Update - steps once towards track 76 */
        if (intCommand == 0x4) {
            stepIn(false, verify);
            setNotBusy();
            fireInterrupt();
            System.out.println("Step in (without update)");
            return;
        }

        /* Step In with Update - steps once towards track 76 */
        if (intCommand == 0x5) {
            stepIn(true, verify);
            setNotBusy();
            fireInterrupt();
            System.out.println("Step in (with update)");
            return;
        }

        /* Step Out without Update - steps once towards track 0 */
        if (intCommand == 0x6) {
            stepOut(false, verify);
            setNotBusy();
            fireInterrupt();
            System.out.println("Step out (without update)");
            return;
        }

        /* Step Out with Update - steps once towards track 0 */
        if (intCommand == 0x7) {
            stepOut(true, verify);
            setNotBusy();
            fireInterrupt();
            System.out.println("Step out (with update)");
            return;
        }

        /* Read single sector */
        if (intCommand == 0x08) {
            tracks[currentTrack].setCommand(currentSector, DiskCommand.READ_SECTOR);
            currentCommand = DiskCommand.READ_SECTOR;
            setBusy();
            setDRQ();
            System.out.println("Read single sector");
            return;
        }

        /* Read multiple sectors */
        if (intCommand == 0x09) {
            currentCommand = DiskCommand.READ_MULTIPLE_SECTORS;
            setBusy();
            setDRQ();
            System.out.println("Read multiple sectors");
            return;
        }

        /* Write single sector */
        if (intCommand == 0x0A) {
            tracks[currentTrack].setCommand(currentSector, DiskCommand.WRITE_SECTOR);
            currentCommand = DiskCommand.WRITE_SECTOR;
            dataMark = new UnsignedByte(command.isMasked(0x1) ? 0xF8 : 0xFB);
            setBusy();
            setDRQ();
            System.out.println("Write sector");
            return;
        }

        /* Write multiple sectors */
        if (intCommand == 0x0B) {
            currentCommand = DiskCommand.WRITE_MULTIPLE_SECTORS;
            dataMark = new UnsignedByte(command.isMasked(0x1) ? 1 : 0);
            setBusy();
            setDRQ();
            System.out.println("Write multiple sectors");

            return;
        }

        /* Read address */
        if (intCommand == 0x0C) {
            tracks[currentTrack].setCommand(currentSector, DiskCommand.READ_ADDRESS);
            currentCommand = DiskCommand.READ_ADDRESS;
            setBusy();
            setDRQ();
            System.out.println("Read address");
            return;
        }

        /* Read track */
        if (intCommand == 0x0D) {
            tracks[currentTrack].startReadTrack();
            currentCommand = DiskCommand.READ_TRACK;
            setBusy();
            setDRQ();
            System.out.println("Read track");
            return;
        }

        /* Write track */
        if (intCommand == 0x0E) {
            System.out.println("Unsupported disk command: write track");
            return;
        }

        /* Force interrupt */
        if (intCommand == 0x0F) {
            System.out.println("Unsupported disk command: force interrupt");
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
     * Reads a single sector worth of bytes.
     *
     * @return the next byte in the sector
     */
    public UnsignedByte readSector() {
        /* Check to see if there is a data address mark */
        if (!tracks[currentTrack].dataAddressMarkFound(currentSector)) {
            statusRegister.or(0x20);
            currentCommand = DiskCommand.NONE;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            return new UnsignedByte(0);
        }

        /* Check to see if we are ready to read more bytes */
        if (!tracks[currentTrack].hasMoreBytes(currentSector)) {
            inCommand = false;
            currentCommand = DiskCommand.NONE;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            return new UnsignedByte(0);
        }

        /* Read a byte */
        return tracks[currentTrack].read(currentSector);
    }

    /**
     * Reads multiple sectors. Will continue reading sectors sequentially until
     * all of the sectors on the track have been read.
     *
     * @return the next byte in the sector
     */
    public UnsignedByte readMultipleSectors() {
        /* Check to see if there is a data address mark */
        if (!tracks[currentTrack].dataAddressMarkFound(currentSector)) {
            statusRegister.or(0x20);
            currentCommand = DiskCommand.NONE;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            return new UnsignedByte(0);
        }

        /* Check to see if we are ready to read more bytes from this sector */
        if (!tracks[currentTrack].hasMoreBytes(currentSector)) {
            tracks[currentTrack].setCommand(currentSector, DiskCommand.NONE);
            currentSector++;
            if (currentSector >= sectorsPerTrack) {
                currentCommand = DiskCommand.NONE;
                setNotBusy();
                clearDRQ();
                fireInterrupt();
                return new UnsignedByte(0);
            }
            return readMultipleSectors();
        }

        /* Otherwise, return the next byte in the sector */
        return tracks[currentTrack].read(currentSector);
    }

    /**
     * Writes a byte of data to the current sector.
     *
     * @param value the byte value to write
     */
    public void writeSector(UnsignedByte value) {
        /* Write the data mark (always) */
        tracks[currentTrack].writeDataMark(currentSector, dataMark);

        /* Check to see if we can write more bytes on this sector */
        if (tracks[currentTrack].hasMoreBytes(currentSector)) {
            tracks[currentTrack].write(currentSector, value);
            return;
        }

        /* We've written the entire sector, set not busy, and interrupt */
        currentCommand = DiskCommand.NONE;
        setNotBusy();
        clearDRQ();
        fireInterrupt();
    }

    /**
     * Writes a byte of data to the current sector.
     *
     * @param value the byte value to write
     */
    public void writeMultipleSectors(UnsignedByte value) {
        /* Write the data mark */
        tracks[currentTrack].writeDataMark(currentSector, dataMark);

        /* Check to see if we can write more bytes on this sector */
        if (tracks[currentTrack].hasMoreBytes(currentSector)) {
            tracks[currentTrack].write(currentSector, value);
            return;
        }

        /* Otherwise, advance the sector counter */
        tracks[currentTrack].setCommand(currentSector, DiskCommand.NONE);
        currentSector++;

        /* Check to see if we are past the maximum number of sectors */
        if (currentSector >= sectorsPerTrack) {
            currentSector--;
            tracks[currentTrack].setCommand(currentSector, DiskCommand.NONE);
            currentCommand = DiskCommand.NONE;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            return;
        }

        /* Prep the new sector */
        tracks[currentTrack].setCommand(currentSector, DiskCommand.WRITE_SECTOR);
        writeMultipleSectors(value);
    }

    /**
     * Outputs a sequence of 6 bytes to the data register:
     *
     *   Track address
     *   Side number
     *   Sector address
     *   Sector length
     *   CRC 1
     *   CRC 2
     *
     *  The bytes are read one at a time, with each call to readAddress
     *  returning the next byte in the sequence.
     *
     * @return the byte to output
     */
    public UnsignedByte readAddress() {
        /* Check to see if we are ready to read more bytes */
        if (!tracks[currentTrack].hasMoreIdBytes(currentSector)) {
            currentCommand = DiskCommand.NONE;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            return new UnsignedByte(0);
        }

        /* Read an ID byte */
        return tracks[currentTrack].readAddress(currentSector);
    }

    public UnsignedByte readTrack() {
        if (tracks[currentTrack].isReadTrackFinished()) {
            currentCommand = DiskCommand.NONE;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            return new UnsignedByte(0);
        }

        return tracks[currentTrack].readTrack();
    }
}
