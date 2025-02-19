/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DiskTrackTest
{
    private DiskTrack track;

    @Before
    public void setUp() {
        track = new DiskTrack(35, true);
    }

    @Test
    public void testIsReadTrackFinished() {
        track.readTrackFinished = true;
        assertTrue(track.isReadTrackFinished());
    }

    @Test
    public void testIsWriteTrackFinished() {
        track.writeTrackFinished = true;
        assertTrue(track.isWriteTrackFinished());
    }

    @Test
    public void testReadWriteData() {
        track.setCommand(1, DiskCommand.WRITE_SECTOR);
        track.writeData(1, new UnsignedByte(0x11));
        track.setCommand(1, DiskCommand.READ_SECTOR);
        assertEquals(0x11, track.readData(1));
    }

    @Test
    public void testStartReadTrack() {
        track.readTrackFinished = true;
        track.startReadTrack();
        assertFalse(track.isReadTrackFinished());
        assertEquals(DiskCommand.READ_TRACK, track.sectors[0].getCommand());
    }

    @Test
    public void testStartWriteTrack() {
        track.writeTrackFinished = true;
        track.startWriteTrack();
        assertFalse(track.isWriteTrackFinished());
        assertEquals(DiskCommand.WRITE_TRACK, track.sectors[0].getCommand());
    }

    @Test
    public void testWriteDataMark() {
        track.writeDataMark(0, new UnsignedByte(0xFB));
        track.setCommand(0, DiskCommand.READ_SECTOR);
        assertTrue(track.dataAddressMarkFound(0));
    }

    @Test
    public void testHasMoreDataBytes() {
        track.setCommand(0, DiskCommand.WRITE_SECTOR);
        for (int i = 0; i < 255; i++) {
            track.writeData(0, new UnsignedByte());
            assertTrue(track.hasMoreDataBytes(0));
        }
        track.writeData(0, new UnsignedByte());
        assertFalse(track.hasMoreDataBytes(0));
    }

    @Test
    public void testWriteTrackAtEndOfTrack() {
        track.startWriteTrack();
        track.currentSector = 36;
        track.writeTrack(new UnsignedByte());
        assertTrue(track.isWriteTrackFinished());
    }
}
