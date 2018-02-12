/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.RegisterSet;
import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class DiskDriveTest
{

    private Memory memory;
    private RegisterSet regs;
    private Keyboard keyboard;
    private Screen screen;
    private Cassette cassette;
    private IOController io;
    private IOController ioSpy;
    private CPU cpu;
    private DiskDrive drive;

    @Before
    public void setUp() throws IllegalIndexedPostbyteException{
        memory = new Memory();
        regs = new RegisterSet();
        keyboard = new Keyboard();
        screen = new Screen(1);
        cassette = new Cassette();
        io = new IOController(memory, regs, keyboard, screen, cassette);
        ioSpy = spy(io);
        cpu = new CPU(io);
        ioSpy.setCPU(cpu);
        drive = new DiskDrive(ioSpy);
    }

    @Test
    public void testSetTrackWorksCorrectly() {
        drive.setTrack(new UnsignedByte(17));
        assertEquals(17, drive.getTrack());
    }

    @Test
    public void testSetSectorWorksCorrectly() {
        drive.setSector(new UnsignedByte(17));
        assertEquals(17, drive.getSector());
    }

    @Test
    public void testTurnMotorOnWorksCorrectly() {
        drive.turnMotorOn();
        assertTrue(drive.motorOn);
    }

    @Test
    public void testTurnMotorOffWorksCorrectly() {
        drive.turnMotorOn();
        drive.turnMotorOff();
        assertFalse(drive.motorOn);
    }

    @Test
    public void testEnableHaltWorksCorrectly() {
        drive.enableHalt();
        assertTrue(drive.haltEnabled);
    }

    @Test
    public void testDisableHaltWorksCorrectly() {
        drive.enableHalt();
        drive.disableHalt();
        assertFalse(drive.haltEnabled);
    }

    @Test
    public void testSetBusyWorks() {
        drive.setBusy();
        assertEquals(new UnsignedByte(1), drive.getStatusRegister());
    }

    @Test
    public void testSetNotBusyWorks() {
        drive.setBusy();
        drive.setNotBusy();
        assertEquals(new UnsignedByte(0), drive.getStatusRegister());
    }

    @Test
    public void testSetDRQWorks() {
        drive.setDRQ();
        assertEquals(new UnsignedByte(2), drive.getStatusRegister());
    }

    @Test
    public void testClearDRQWorks() {
        drive.setDRQ();
        drive.clearDRQ();
        assertEquals(new UnsignedByte(0), drive.getStatusRegister());
    }

    @Test
    public void testSetRecordNotFoundWorks() {
        drive.setRecordNotFound();
        assertEquals(new UnsignedByte(0x10), drive.getStatusRegister());
    }

    @Test
    public void testClearRecordNotFoundWorks() {
        drive.setRecordNotFound();
        drive.clearRecordNotFound();
        assertEquals(new UnsignedByte(0), drive.getStatusRegister());
    }

    @Test
    public void testSetDataMarkNotFoundWorks() {
        drive.setDataMarkNotFound();
        assertEquals(new UnsignedByte(0x20), drive.getStatusRegister());
    }

    @Test
    public void testClearDataMarkNotNotFoundWorks() {
        drive.setDataMarkNotFound();
        drive.clearDataMarkNotFound();
        assertEquals(new UnsignedByte(0), drive.getStatusRegister());
    }

    @Test
    public void testFireInterruptWorksCorrectly() {
        drive.fireInterrupt();
        verify(ioSpy).nonMaskableInterrupt();
    }

    @Test
    public void testRestoreWorksCorrectly() {
        drive.setTrack(new UnsignedByte(17));
        drive.restore(false);
        assertEquals(0, drive.getTrack());
    }

    @Test
    public void testSeekWorksCorrectly() {
        drive.setTrack(new UnsignedByte(17));
        drive.setDataRegister(new UnsignedByte(0x1));
        drive.seek(true);
        assertEquals(1, drive.getTrack());

        drive.setTrack(new UnsignedByte(17));
        drive.setDataRegister(new UnsignedByte(0x19));
        drive.seek(true);
        assertEquals(0x19, drive.getTrack());
    }
}
