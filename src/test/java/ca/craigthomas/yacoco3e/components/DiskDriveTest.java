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
    private IOController ioSpy;
    private DiskDrive drive;

    @Before
    public void setUp() {
        Memory memory = new Memory();
        RegisterSet regs = new RegisterSet();
        Keyboard keyboard = new EmulatedKeyboard();
        Screen screen = new Screen(1);
        Cassette cassette = new Cassette();
        IOController io = new IOController(memory, regs, keyboard, screen, cassette, null);
        ioSpy = spy(io);
        CPU cpu = new CPU(io);
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

    @Test
    public void testStepWorksCorrectly() {
        drive.setTrack(new UnsignedByte(17));
        drive.direction = -1;
        drive.step(true, true);
        assertEquals(16, drive.getTrack());
    }

    @Test
    public void testStepInWorksCorrectly() {
        drive.setTrack(new UnsignedByte(17));
        drive.stepIn(true, true);
        assertEquals(18, drive.getTrack());
    }

    @Test
    public void testStepOutWorksCorrectly() {
        drive.setTrack(new UnsignedByte(17));
        drive.stepOut(true, true);
        assertEquals(16, drive.getTrack());
    }

    @Test
    public void testExecuteCommandRestore() {
        drive.setTrack(new UnsignedByte(17));
        drive.executeCommand(new UnsignedByte(0));
        assertEquals(0, drive.getTrack());
        assertEquals(new UnsignedByte(0x0), drive.getStatusRegister());
    }

    @Test
    public void testExecuteCommandSeekNoVerify() {
        drive.setTrack(new UnsignedByte(17));
        drive.setDataRegister(new UnsignedByte(1));
        drive.executeCommand(new UnsignedByte(0x10));
        assertEquals(1, drive.getTrack());
        assertEquals(new UnsignedByte(0), drive.getStatusRegister());
    }

    @Test
    public void testExecuteCommandStepWithoutUpdate() {
        drive.setTrack(new UnsignedByte(17));
        drive.direction = 1;
        drive.setDataRegister(new UnsignedByte(1));
        drive.executeCommand(new UnsignedByte(0x20));
        assertEquals(18, drive.getTrack());
        assertEquals(new UnsignedByte(0), drive.getStatusRegister());
    }

    @Test
    public void testExecuteCommandStepWithUpdate() {
        drive.setTrack(new UnsignedByte(17));
        drive.direction = 1;
        drive.executeCommand(new UnsignedByte(0x30));
        assertEquals(18, drive.getTrack());
        assertEquals(new UnsignedByte(0), drive.getStatusRegister());
    }

    @Test
    public void testExecuteCommandStepInWithUpdate() {
        drive.setTrack(new UnsignedByte(17));
        drive.executeCommand(new UnsignedByte(0x40));
        assertEquals(18, drive.getTrack());
        assertEquals(new UnsignedByte(0), drive.getStatusRegister());
    }

    @Test
    public void testExecuteCommandStepInWithoutUpdate() {
        drive.setTrack(new UnsignedByte(17));
        drive.executeCommand(new UnsignedByte(0x50));
        assertEquals(18, drive.getTrack());
        assertEquals(new UnsignedByte(0), drive.getStatusRegister());
    }

    @Test
    public void testExecuteCommandStepOutWithUpdate() {
        drive.setTrack(new UnsignedByte(17));
        drive.executeCommand(new UnsignedByte(0x60));
        assertEquals(16, drive.getTrack());
        assertEquals(new UnsignedByte(0), drive.getStatusRegister());
    }

    @Test
    public void testExecuteCommandStepOutWithoutUpdate() {
        drive.setTrack(new UnsignedByte(17));
        drive.executeCommand(new UnsignedByte(0x70));
        assertEquals(16, drive.getTrack());
        assertEquals(new UnsignedByte(0), drive.getStatusRegister());
    }

    @Test
    public void testExecuteCommandReadSingleSectorBadSectorNumber() {
        drive.currentSector = 99;
        drive.executeCommand(new UnsignedByte(0x80));
        assertEquals(new UnsignedByte(0x10), drive.getStatusRegister());
    }

    @Test
    public void testExecuteCommandReadSingleSector() {
        drive.executeCommand(new UnsignedByte(0x80));
        assertEquals(new UnsignedByte(0x3), drive.getStatusRegister());
        assertEquals(DiskCommand.READ_SECTOR, drive.currentCommand);
    }

    @Test
    public void testExecuteCommandWriteSingleSectorBadSectorNumber() {
        drive.currentSector = 99;
        drive.executeCommand(new UnsignedByte(0xA0));
        assertEquals(new UnsignedByte(0x10), drive.getStatusRegister());
    }

    @Test
    public void testExecuteCommandWriteSingleSector() {
        drive.executeCommand(new UnsignedByte(0xA0));
        assertEquals(new UnsignedByte(0x3), drive.getStatusRegister());
        assertEquals(DiskCommand.WRITE_SECTOR, drive.currentCommand);
        assertEquals(new UnsignedByte(0xFB), drive.dataMark);
    }

    @Test
    public void testExecuteCommandReadAddress() {
        drive.executeCommand(new UnsignedByte(0xC0));
        assertEquals(new UnsignedByte(0x3), drive.getStatusRegister());
        assertEquals(DiskCommand.READ_ADDRESS, drive.currentCommand);
    }

    @Test
    public void testExecuteCommandReadTrack() {
        drive.executeCommand(new UnsignedByte(0xE0));
        assertEquals(new UnsignedByte(0x3), drive.getStatusRegister());
        assertEquals(DiskCommand.READ_TRACK, drive.currentCommand);
    }

    @Test
    public void testExecuteCommandWriteTrack() {
        drive.executeCommand(new UnsignedByte(0xF0));
        assertEquals(new UnsignedByte(0x1), drive.getStatusRegister());
        assertEquals(DiskCommand.WRITE_TRACK, drive.currentCommand);
    }
}
