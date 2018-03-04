/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DiskSectorTest
{
    private DiskSector sector;

    @Test
    public void testWriteDataMark() {
        sector = new DiskSector(true);
        sector.writeDataMark((byte) 0xFB);
        sector.setCommand(DiskCommand.READ_SECTOR);
        assertTrue(sector.dataAddressMarkFound());
    }

    @Test
    public void testWriteDeletedDataMark() {
        sector = new DiskSector(true);
        sector.writeDataMark((byte) 0xFA);
        sector.setCommand(DiskCommand.READ_SECTOR);
        assertFalse(sector.dataAddressMarkFound());
    }

    @Test
    public void testWriteDataMarkSingleDensity() {
        sector = new DiskSector(false);
        sector.writeDataMark((byte) 0xFB);
        sector.setCommand(DiskCommand.READ_SECTOR);
        assertTrue(sector.dataAddressMarkFound());
    }

    @Test
    public void testWriteDeletedDataMarkSingleDensity() {
        sector = new DiskSector(false);
        sector.writeDataMark((byte) 0xFA);
        sector.setCommand(DiskCommand.READ_SECTOR);
        assertFalse(sector.dataAddressMarkFound());
    }

    @Test
    public void testWriteSectorReadSector() {
        sector = new DiskSector(true);
        sector.setCommand(DiskCommand.WRITE_SECTOR);
        sector.writeSectorData((byte) 0x11);
        sector.writeSectorData((byte) 0x22);
        sector.writeSectorData((byte) 0x33);
        sector.writeSectorData((byte) 0x44);
        sector.setCommand(DiskCommand.READ_SECTOR);
        assertEquals(0x11, sector.readSectorData());
        assertEquals(0x22, sector.readSectorData());
        assertEquals(0x33, sector.readSectorData());
        assertEquals(0x44, sector.readSectorData());
    }

    @Test
    public void testWriteSectorReadSectorSingleDensity() {
        sector = new DiskSector(false);
        sector.setCommand(DiskCommand.WRITE_SECTOR);
        sector.writeSectorData((byte) 0x11);
        sector.writeSectorData((byte) 0x22);
        sector.writeSectorData((byte) 0x33);
        sector.writeSectorData((byte) 0x44);
        sector.setCommand(DiskCommand.READ_SECTOR);
        assertEquals(0x11, sector.readSectorData());
        assertEquals(0x22, sector.readSectorData());
        assertEquals(0x33, sector.readSectorData());
        assertEquals(0x44, sector.readSectorData());
    }

    @Test
    public void testHasMoreDataBytesFalseWhenNoCommand() {
        sector = new DiskSector(true);
        sector.setCommand(DiskCommand.NONE);
        assertFalse(sector.hasMoreDataBytes());
    }

    @Test
    public void testHasMoreBytesTrueWhenInCommandAndSpace() {
        sector = new DiskSector(true);
        sector.setCommand(DiskCommand.WRITE_SECTOR);
        assertTrue(sector.hasMoreDataBytes());
    }

    @Test
    public void testHasMoreDataBytesFalseWhenInCommandAndNoSpace() {
        sector = new DiskSector(true);
        sector.setCommand(DiskCommand.WRITE_SECTOR);
        for (int i=0; i < 258; i++) {
            sector.writeSectorData((byte) 0x11);
        }
        assertFalse(sector.hasMoreDataBytes());
    }

    @Test
    public void testHasMoreDataBytesTrueWhenInCommandAndSpace() {
        sector = new DiskSector(true);
        sector.setCommand(DiskCommand.WRITE_SECTOR);
        for (int i=0; i < 10; i++) {
            sector.writeSectorData((byte) 0x11);
        }
        assertTrue(sector.hasMoreDataBytes());
    }
}
