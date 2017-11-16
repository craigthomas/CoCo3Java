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
    protected UnsignedByte dataRegister;
    protected int direction;

    protected boolean haltEnabled;

    protected IOController io;

    public DiskDrive(IOController io) {
        diskData = new byte [TRACKS * SECTORS * BYTES_PER_SECTOR];
        motorOn = false;
        this.io = io;
        direction = -1;
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

    /**
     * Executes the specified command on the disk.
     *
     * @param command the command to execute
     */
    public void executeCommand(UnsignedByte command) {
        int intCommand = command.getShort() >> 4;
        boolean verify = command.isMasked(0x04);

        /* Restore - seek track 0 */
        if (intCommand == 0x0) {
            restore(verify);
            return;
        }

        /* Seek - seek to track specified in track register */
        if (intCommand == 0x1) {
            seek(verify);
            return;
        }

        /* Step without Update - steps once in the last direction */
        if (intCommand == 0x2) {
            step(false, verify);
            return;
        }

        /* Step with Update - steps once in the last direction */
        if (intCommand == 0x3) {
            step(true, verify);
            return;
        }

        /* Step In without Update - steps once towards track 76 */
        if (intCommand == 0x4) {
            stepIn(false, verify);
            return;
        }

        /* Step In with Update - steps once towards track 76 */
        if (intCommand == 0x5) {
            stepIn(true, verify);
            return;
        }

        /* Step Out without Update - steps once towards track 0 */
        if (intCommand == 0x6) {
            stepOut(false, verify);
            return;
        }

        /* Step In with Update - steps once towards track 0 */
        if (intCommand == 0x7) {
            stepOut(true, verify);
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
        io.nonMaskableInterrupt();
    }

    /**
     * Seeks to the track specified in the data register.
     */
    public void seek(boolean verify) {
        int trackToSeek = dataRegister.getShort();
        while (currentTrack > trackToSeek) {
            direction = -1;
            currentTrack--;
        }

        while (currentTrack < trackToSeek) {
            direction = 1;
            currentTrack++;
        }

        setTrack(dataRegister);
        io.nonMaskableInterrupt();
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
        io.nonMaskableInterrupt();
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
}
