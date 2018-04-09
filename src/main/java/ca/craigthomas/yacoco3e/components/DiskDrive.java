/*
 * Copyright (C) 2017-2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import ca.craigthomas.yacoco3e.datatypes.VirtualDisk;

import java.util.logging.Logger;

public class DiskDrive
{
    // Default number of tracks on the disk
    public static final int DEFAULT_NUM_TRACKS = 35;
    // Default number of sectors on each track
    public static final int DEFAULT_SECTORS_PER_TRACK = 18;
    // Whether the drive motor is turned on
    protected boolean motorOn;
    // The array of tracks on the disk
    protected DiskTrack [] tracks;
    // The current track the disk is positioned on
    protected int currentTrack;
    // The current sector the disk is positioned on
    protected int currentSector;
    // The value to output on the data register
    protected UnsignedByte dataRegisterOut;
    // The value to set on the data register
    protected UnsignedByte dataRegisterIn;
    // The direction the read / write head is moving
    protected int direction;
    // The value of the status register
    protected UnsignedByte statusRegister;
    // The data mark value to write to the disk
    protected UnsignedByte dataMark;
    // Whether the disk drive can halt the CPU
    protected boolean haltEnabled;
    // The byte the disk drive is currently pointing to
    protected int currentBytePointer;
    // The IO controller associated with the disk drive
    protected IOController io;
    // The command being executed by the disk drive
    protected DiskCommand currentCommand;
    // How many sectors per track exist on the disk
    protected int sectorsPerTrack;
    // How many tracks exist on the disk
    protected int tracksPerDisk;
    // A logger for the class
    private final static Logger LOGGER = Logger.getLogger(DiskDrive.class.getName());

    /**
     * A convenience constructor that will set up a disk with the
     * default values. It will have 35 tracks, 18 sectors per track, and will
     * be double density (256 bytes per sector).
     *
     * @param io the IO controller for the disk drive
     */
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

    /**
     * Loads a set of virtual disk tracks into the disk drive.
     *
     * @param disk the virtual disk to load from
     */
    public void loadFromVirtualDisk(VirtualDisk disk) {
        tracks = disk.readTracks();
        tracksPerDisk = disk.tracksPerDisk();
        sectorsPerTrack = disk.sectorsPerTrack();
        motorOn = false;
        direction = -1;
        statusRegister = new UnsignedByte();
        dataRegisterOut = new UnsignedByte();
        dataRegisterIn = new UnsignedByte();
        currentCommand = DiskCommand.NONE;
        currentBytePointer = -1;
    }

    /**
     * Returns the track the read/write head is positioned above.
     *
     * @return the current track number
     */
    public int getTrack() {
        return currentTrack;
    }

    /**
     * Positions the read/write head above the specified track.
     *
     * @param track the track to move to
     */
    public void setTrack(UnsignedByte track) {
        currentTrack = track.getShort();
    }

    /**
     * Returns the sector the disk read/write head is positioned above.
     *
     * @return the current sector
     */
    public int getSector() {
        return currentSector;
    }

    /**
     * Sets the sector that the read/write head should be positioned above.
     *
     * @param sector the current sector we should move to
     */
    public void setSector(UnsignedByte sector) {
        currentSector = sector.getShort();
    }

    /**
     * Turns the drive motor on.
     */
    public void turnMotorOn() {
        motorOn = true;
    }

    /**
     * Turns the drive motor off.
     */
    public void turnMotorOff() {
        motorOn = false;
    }

    /**
     * Enables the disk drive to halt the CPU.
     */
    public void enableHalt() {
        haltEnabled = true;
    }

    /**
     * Disables the disk drive to halt the CPU.
     */
    public void disableHalt() {
        haltEnabled = false;
    }

    /**
     * Sets the contents of the data register. Depending on the command,
     * may also write data to the disk.
     *
     * @param value the value to write to the data register
     */
    public void setDataRegister(UnsignedByte value) {
        dataRegisterIn.set(value);
        switch (currentCommand) {
            case WRITE_SECTOR:
                writeSector(value);
                return;

            case WRITE_TRACK:
                writeTrack(value);
                return;

            default:
                break;
        }
    }

    /**
     * Returns the contents of the data register, based on the command that
     * the disk drive is currently executing.
     *
     * @return the UnsignedByte of the data register
     */
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
                readAddress();
                break;

            default:
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

    /**
     * Sets busy on the drive.
     */
    public void setBusy() {
        statusRegister.or(0x01);
    }

    /**
     * Clears busy on the drive.
     */
    public void setNotBusy() {
        statusRegister.and(~0x01);
    }

    /**
     * Turns on a DRQ for the drive.
     */
    public void setDRQ() {
        statusRegister.or(0x02);
    }

    /**
     * Clears a DRQ for the drive.
     */
    public void clearDRQ() {
        statusRegister.and(~0x02);
    }

    /**
     * Sets that a record lookup was not found.
     */
    public void setRecordNotFound() {
        statusRegister.or(0x10);
    }

    /**
     * Clears that a record lookup was not found.
     */
    public void clearRecordNotFound() {
        statusRegister.and(~0x10);
    }

    /**
     * Sets that a data mark was not found.
     */
    public void setDataMarkNotFound() {
        statusRegister.or(0x20);
    }

    /**
     * Clears that a data mark was not found.
     */
    public void clearDataMarkNotFound() {
        statusRegister.and(~0x20);
    }

    /**
     * Fires an NMI on the IO (which will forward it to the CPU).
     */
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
            restore(verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Seek - seek to track specified in the data register */
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

        /* Step Out with Update - steps once towards track 0 */
        if (intCommand == 0x7) {
            stepOut(true, verify);
            setNotBusy();
            fireInterrupt();
            return;
        }

        /* Read single sector */
        if (intCommand == 0x8) {
            int logicalSector = tracks[currentTrack].getLogicalSector(currentSector);
            LOGGER.fine("Read single sector - Track " + currentTrack + ", Sector " + currentSector + ", Logical Sector " + logicalSector);
            if (logicalSector == -1) {
                setNotBusy();
                setRecordNotFound();
            } else {
                setDRQ();
                tracks[currentTrack].setCommand(logicalSector, DiskCommand.READ_SECTOR);
                currentCommand = DiskCommand.READ_SECTOR;
                currentSector = logicalSector;
            }
            return;
        }

        /* Read multiple sectors */
        if (intCommand == 0x9) {
            LOGGER.fine("Read multiple sectors");
            currentCommand = DiskCommand.READ_MULTIPLE_SECTORS;
            setDRQ();
            return;
        }

        /* Write single sector */
        if (intCommand == 0xA) {
            int logicalSector = tracks[currentTrack].getLogicalSector(currentSector);
            LOGGER.fine("Write single sector - Track " + currentTrack + ", Sector " + currentSector + ", Logical Sector " + logicalSector);
            if (logicalSector == -1) {
                setNotBusy();
                setRecordNotFound();
            } else {
                tracks[currentTrack].setCommand(logicalSector, DiskCommand.WRITE_SECTOR);
                currentCommand = DiskCommand.WRITE_SECTOR;
                dataMark = new UnsignedByte(command.isMasked(0x1) ? 0xF8 : 0xFB);
                currentSector = logicalSector;
                setDRQ();
            }
            return;
        }

        /* Write multiple sectors */
        if (intCommand == 0xB) {
            LOGGER.warning("Writing multiple sectors not supported");
            return;
        }

        /* Read address */
        if (intCommand == 0xC) {
            LOGGER.fine("Read address");
            tracks[currentTrack].setCommand(currentSector, DiskCommand.READ_ADDRESS);
            currentCommand = DiskCommand.READ_ADDRESS;
            setDRQ();
            return;
        }

        /* Force interrupt */
        if (intCommand == 0xD) {
            LOGGER.fine("Force interrupt");
            currentCommand = DiskCommand.NONE;
            fireInterrupt();
            return;
        }

        /* Read track */
        if (intCommand == 0xE) {
            LOGGER.fine("Read track");
            tracks[currentTrack].startReadTrack();
            currentCommand = DiskCommand.READ_TRACK;
            setDRQ();
            return;
        }

        /* Write track */
        if (intCommand == 0xF) {
            LOGGER.fine("Write track - Track " + currentTrack);
            tracks[currentTrack].startWriteTrack();
            currentCommand = DiskCommand.WRITE_TRACK;
            setBusy();
        }
    }

    /**
     * Positions the drive read/write head to track 0. The verify
     * flag will verify that the restore completed. Note however,
     * that the verify will always be ignored.
     *
     * @param verify whether a verify should be run
     */
    public void restore(boolean verify) {
        currentTrack = 0;
        direction = -1;
        setTrack(new UnsignedByte(0));
    }

    /**
     * Seeks to the track specified in the data register. The verify
     * flag will verify that the seek completed. Note however, that
     * the verify will always be ignored.
     *
     * @param verify whether a verify should be run
     */
    public void seek(boolean verify) {
        int trackToSeek = dataRegisterIn.getShort();
        if (currentTrack > trackToSeek) {
            direction = -1;
        }

        if (currentTrack < trackToSeek) {
            direction = 1;
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
            return new UnsignedByte(0);
        }

        /* Check to see if there is a data address mark */
        if (!tracks[currentTrack].dataAddressMarkFound(currentSector)) {
            setDataMarkNotFound();
            currentCommand = DiskCommand.NONE;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            return new UnsignedByte(0);
        }

        /* Check to see if we are ready to read more bytes */
        if (!tracks[currentTrack].hasMoreDataBytes(currentSector)) {
            currentCommand = DiskCommand.NONE;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            return new UnsignedByte(0);
        }

        /* Read a byte */
        return tracks[currentTrack].readData(currentSector);
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
        if (!tracks[currentTrack].hasMoreDataBytes(currentSector)) {
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
        return tracks[currentTrack].readData(currentSector);
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
        if (tracks[currentTrack].hasMoreDataBytes(currentSector)) {
            tracks[currentTrack].writeData(currentSector, value);
            return;
        }

        /* We've written the entire sector, set not busy, and interrupt */
        currentCommand = DiskCommand.NONE;
        setNotBusy();
        clearDRQ();
        fireInterrupt();
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
     *  returning the next byte in the sequence. If there are no more
     *  bytes to read, will clear the command, set not busy, clear any
     *  DRQs and fire an interrupt.
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

    /**
     * Returns the next byte that is read from the track. If the read is
     * finished, will reset the current command, set not busy on the
     * disk, clear any DRQs that are set, and will fire an interrupt.
     *
     * @return the next byte read from the track
     */
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

    /**
     * Writes an UnsignedByte to the current track. If the write is finished,
     * will reset the current command, set not busy on the disk, clear any
     * DRQs that are set, and will fire an interrupt.
     *
     * @param value the value to write to the track
     */
    public void writeTrack(UnsignedByte value) {
        if (tracks[currentTrack].isWriteTrackFinished()) {
            currentCommand = DiskCommand.NONE;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            return;
        }

        tracks[currentTrack].writeTrack(value);
    }
}
