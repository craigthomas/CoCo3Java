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
    protected int sector;
    protected byte [] diskData;

    protected boolean haltEnabled;

    protected IOController io;

    public DiskDrive(IOController io) {
        diskData = new byte [TRACKS * SECTORS * BYTES_PER_SECTOR];
        motorOn = false;
        this.io = io;
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
}
