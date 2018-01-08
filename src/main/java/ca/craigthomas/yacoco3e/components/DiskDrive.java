/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

import java.util.logging.Logger;

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

    /* A logger for the disk drive */
    private final static Logger LOGGER = Logger.getLogger(DiskDrive.class.getName());


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

            case WRITE_TRACK:
                writeTrack(value);
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
                LOGGER.info("reading address");
                return readAddress();

            case READ_TRACK:
                LOGGER.info("reading track");
                return readTrack();

            default:
                throw new RuntimeException("getDataRegister unrecognized command " + currentCommand);
        }
    }

    /**
     * This function is used to advance the current disk drive operation
     * periodically by the CPU. With most read and write commands, the
     * disk drive will remain locked in a certain command until it
     * exhausts the available data to read or write on disk. This function
     * should be called periodically by the IO controller so that the
     * disk does not remain stale within an operation when no read or
     * write is forthcoming.
     */
    public void tickUpdate() {
        switch (currentCommand) {
            case READ_ADDRESS:
                readSector();
                break;

            case NONE:
                break;

            default:
                LOGGER.warning("doing nothing on tickUpdate with command " + currentCommand);
                break;
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

    public void setRecordNotFound() {
        statusRegister.or(0x10);
    }

    public void clearRecordNotFound() {
        statusRegister.and(~0x10);
    }

    public void setDataMarkNotFound() {
        statusRegister.or(0x20);
    }

    public void clearDataMarkNotFound() {
        statusRegister.and(~0x20);
    }

    public void fireInterrupt() {
        setNotBusy();
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
        clearRecordNotFound();
        clearDataMarkNotFound();

        /* Restore - seek track 0 */
        if (intCommand == 0x0) {
            LOGGER.info("Restore");
            restore(verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Seek - seek to track specified in track register */
        if (intCommand == 0x1) {
            LOGGER.info("Seek");
            seek(verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Step without Update - steps once in the last direction */
        if (intCommand == 0x2) {
            LOGGER.info("Step (without update)");
            step(false, verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Step with Update - steps once in the last direction */
        if (intCommand == 0x3) {
            LOGGER.info("Step (with update)");
            step(true, verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Step In without Update - steps once towards track 76 */
        if (intCommand == 0x4) {
            LOGGER.info("Step in (without update)");
            stepIn(false, verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Step In with Update - steps once towards track 76 */
        if (intCommand == 0x5) {
            LOGGER.info("Step in (with update)");
            stepIn(true, verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Step Out without Update - steps once towards track 0 */
        if (intCommand == 0x6) {
            LOGGER.info("Step out (without update)");
            stepOut(false, verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Step Out with Update - steps once towards track 0 */
        if (intCommand == 0x7) {
            LOGGER.info("Step out (with update)");
            stepOut(true, verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Read single sector */
        if (intCommand == 0x8) {
            int logicalSector = tracks[track].getLogicalSector(sector);
            LOGGER.info("Read single sector - Track " + track + ", Sector " + sector + ", Logical Sector " + logicalSector);
            currentTrack = track;
            if (logicalSector == -1) {
                setNotBusy();
                setRecordNotFound();
            } else {
                setDRQ();
                tracks[track].setCommand(logicalSector, DiskCommand.READ_SECTOR);
                currentCommand = DiskCommand.READ_SECTOR;
                currentSector = logicalSector;
            }
            return;
        }

        /* Read multiple sectors */
        if (intCommand == 0x9) {
            LOGGER.info("Read multiple sectors");
            currentCommand = DiskCommand.READ_MULTIPLE_SECTORS;
            setDRQ();
            return;
        }

        /* Write single sector */
        if (intCommand == 0xA) {
            LOGGER.info("Write sector - Track " + track + ", Sector " + sector);
            tracks[track].setCommand(sector, DiskCommand.WRITE_SECTOR);
            currentCommand = DiskCommand.WRITE_SECTOR;
            dataMark = new UnsignedByte(command.isMasked(0x1) ? 0xF8 : 0xFB);
            setDRQ();
            return;
        }

        /* Write multiple sectors */
        if (intCommand == 0xB) {
            LOGGER.info("Write multiple sectors");
            currentCommand = DiskCommand.WRITE_MULTIPLE_SECTORS;
            dataMark = new UnsignedByte(command.isMasked(0x1) ? 1 : 0);
            setDRQ();
            return;
        }

        /* Read address */
        if (intCommand == 0xC) {
            LOGGER.info("Read address");
            tracks[currentTrack].setCommand(currentSector, DiskCommand.READ_ADDRESS);
            currentCommand = DiskCommand.READ_ADDRESS;
            setDRQ();
            return;
        }

        /* Force interrupt */
        if (intCommand == 0xD) {
            LOGGER.info("Force interrupt");
            fireInterrupt();
            return;
        }

        /* Read track */
        if (intCommand == 0xE) {
            LOGGER.info("Read track");
            tracks[currentTrack].startReadTrack();
            currentCommand = DiskCommand.READ_TRACK;
            setDRQ();
            return;
        }

        /* Write track */
        if (intCommand == 0xF) {
            LOGGER.info("Write track - Track " + currentTrack);
            tracks[currentTrack].startWriteTrack();
            currentCommand = DiskCommand.WRITE_TRACK;
            setBusy();
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
        /* Check to make sure the logical sector exists */
        if (currentSector == -1) {
            setRecordNotFound();
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            System.out.println("Read Sector - not found!");
            return new UnsignedByte(0);
        }

        /* Check to see if there is a data address mark */
        if (!tracks[currentTrack].dataAddressMarkFound(currentSector)) {
            setDataMarkNotFound();
            currentCommand = DiskCommand.NONE;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            System.out.println("Read sector - no data address mark!");
            return new UnsignedByte(0);
        }

        /* Check to see if we are ready to read more bytes */
        if (!tracks[currentTrack].hasMoreBytes(currentSector)) {
            inCommand = false;
            currentCommand = DiskCommand.NONE;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            System.out.println("Read sector - no more bytes!");
            return new UnsignedByte(0);
        }

        /* Read a byte */
        System.out.println("Reading track " + currentTrack + ", logical sector " + currentSector + " value " + tracks[currentTrack].read(currentSector));
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
            LOGGER.info("Last address byte read, setting not busy");
            return new UnsignedByte(0);
        }

        /* Read an ID byte */
        LOGGER.info("Read address byte");
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

    public void writeTrack(UnsignedByte value) {
        if (tracks[currentTrack].isWriteTrackFinished()) {
            currentCommand = DiskCommand.NONE;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
        }

        tracks[currentTrack].writeTrack(value);
    }
}
