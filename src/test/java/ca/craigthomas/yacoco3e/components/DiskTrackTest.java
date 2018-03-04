/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import org.junit.Test;

import static org.junit.Assert.*;

public class DiskTrackTest
{
    private DiskTrack track;

    @Test
    public void testIsReadTrackFinished() {
        track = new DiskTrack(35, true);
        track.readTrackFinished = true;
        assertTrue(track.isReadTrackFinished());
    }

    @Test
    public void testIsWriteTrackFinished() {
        track = new DiskTrack(35, true);
        track.writeTrackFinished = true;
        assertTrue(track.isWriteTrackFinished());
    }

    @Test
    public void testReadWriteData() {
        track = new DiskTrack(35, true);
        track.setCommand(1, DiskCommand.WRITE_SECTOR);
        track.writeData(1, new UnsignedByte(0x11));
        track.setCommand(1, DiskCommand.READ_SECTOR);
        UnsignedByte result = track.readData(1);
        assertEquals(new UnsignedByte(0x11), result);
    }

    @Test
    public void testStartReadTrack() {
        track = new DiskTrack(35, true);
        track.readTrackFinished = true;
        track.startReadTrack();
        assertFalse(track.isReadTrackFinished());
        assertEquals(DiskCommand.READ_TRACK, track.sectors[0].getCommand());
    }

    @Test
    public void testStartWriteTrack() {
        track = new DiskTrack(35, true);
        track.writeTrackFinished = true;
        track.startWriteTrack();
        assertFalse(track.isWriteTrackFinished());
        assertEquals(DiskCommand.WRITE_TRACK, track.sectors[0].getCommand());
    }

    @Test
    public void testWriteDataMark() {
        track = new DiskTrack(35, true);
        track.writeDataMark(0, new UnsignedByte(0xFB));
        track.setCommand(0, DiskCommand.READ_SECTOR);
        assertTrue(track.dataAddressMarkFound(0));
    }


}
