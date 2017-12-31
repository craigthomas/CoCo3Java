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
    public static final int DEFAULT_BYTES_PER_SECTOR = 256;

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

    protected int currentCommand;

    protected int bytesPerSector;

    protected int sectorsPerTrack;

    protected int tracksPerDisk;

    private int readAddressByte;

    public DiskDrive(IOController io) {
        this(io, DEFAULT_NUM_TRACKS, DEFAULT_SECTORS_PER_TRACK, DEFAULT_BYTES_PER_SECTOR);
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
        currentCommand = -1;
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

                case 0x0C:
                    return readAddress();
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
            currentBytePointer = 0;
            currentCommand = intCommand;
            inCommand = true;
            setBusy();
            setDRQ();
            System.out.println("Read single sector");
            return;
        }

        /* Read multiple sectors */
        if (intCommand == 0x09) {
            currentBytePointer = 0;
            currentCommand = intCommand;
            inCommand = true;
            setBusy();
            setDRQ();
            System.out.println("Read multiple sectors");
            return;
        }

        /* Write single sector */
        if (intCommand == 0x0A) {
            currentBytePointer = 0;
            currentCommand = intCommand;
            inCommand = true;
            dataMark = new UnsignedByte(command.isMasked(0x1) ? 1 : 0);
            setBusy();
            setDRQ();
            System.out.println("Write sector");
            return;
        }

        /* Write multiple sectors */
        if (intCommand == 0x0B) {
            currentBytePointer = 0;
            currentCommand = intCommand;
            inCommand = true;
            dataMark = new UnsignedByte(command.isMasked(0x1) ? 1 : 0);
            setBusy();
            setDRQ();
            System.out.println("Write multiple sectors");

            return;
        }

        /* Read address */
        if (intCommand == 0x0C) {
            readAddressByte = 0;
            currentCommand = intCommand;
            inCommand = true;
            setBusy();
            setDRQ();
            System.out.println("Read address");
            return;
        }

        /* Read track */
        if (intCommand == 0x0D) {
            System.out.println("Unsupported disk command: read track");
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
            inCommand = false;
            currentCommand = -1;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            return new UnsignedByte(0);
        }

        /* Check to see if we are ready to read more bytes */
        if (!tracks[currentTrack].hasMoreBytes(currentSector)) {
            inCommand = false;
            currentCommand = -1;
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
        /* Check to see if we've finished reading the current sector */
        if (currentBytePointer < bytesPerSector) {
            currentBytePointer++;
            return tracks[currentTrack].read(currentSector, currentBytePointer - 1);
        }

        /* Advance the sector counter if necessary */
        if (currentSector < sectorsPerTrack) {
            UnsignedByte dataMark = tracks[currentTrack].getDataMark(currentSector);
            statusRegister.or(dataMark.isZero() ? 0x0 : 0x20);
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

    /**
     * Writes a byte of data to the current sector.
     *
     * @param value the byte value to write
     */
    public void writeSector(UnsignedByte value) {
        /* Write the data mark first */
        tracks[currentTrack].setDataMark(currentSector, dataMark);

        /* Check to see if we can write more bytes on this sector */
        if (currentBytePointer < bytesPerSector) {
            tracks[currentTrack].write(currentSector, value);
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

    /**
     * Writes a byte of data to the current sector.
     *
     * @param value the byte value to write
     */
    public void writeMultipleSectors(UnsignedByte value) {
        /* Write the data mark first */
        tracks[currentTrack].setDataMark(currentSector, dataMark);

        /* Check to see if we can write more bytes on this sector */
        if (currentBytePointer < bytesPerSector) {
            tracks[currentTrack].write(currentSector, value);
            currentBytePointer++;
            return;
        }

        /* Advance the sector counter if necessary */
        if (currentSector < sectorsPerTrack) {
            UnsignedByte dataMark = tracks[currentTrack].getDataMark(currentSector);
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
        if (!tracks[currentTrack].hasMoreBytes(currentSector)) {
            inCommand = false;
            currentCommand = -1;
            setNotBusy();
            clearDRQ();
            fireInterrupt();
            return new UnsignedByte(0);
        }

        /* Read a byte */
        return tracks[currentTrack].readAddress(currentSector);
    }
}
