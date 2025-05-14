/*
 * Copyright (C) 2022-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.RegisterSet;
import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import ca.craigthomas.yacoco3e.datatypes.UnsignedWord;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CPUTest
{
    private Memory memory;
    private RegisterSet regs;
    private CPU cpu;

    @Before
    public void setup() throws MalformedInstructionException {
        memory = new Memory();
        regs = new RegisterSet();
        IOController io = new IOController(memory, regs, new EmulatedKeyboard(), new Screen(1), new Cassette(), null);
        cpu = new CPU(io);
        io.setCPU(cpu);
    }

    @Test
    public void testExecuteThrowsMalformedInstructionException() {
        regs.pc.set(0);
        memory.writeByte(0x0000, 0x6F); // CLRM
        memory.writeByte(0x0001, 0x97); // Bad postbyte
        assertThrows(MalformedInstructionException.class, () -> cpu.executeInstruction());
    }

    @Test
    public void testInterrruptRequestWorksCorrectly() {
        regs.s.set(0x5000);
        regs.pc.set(0x1234);
        regs.u.set(0x5678);
        regs.y.set(0x9ABC);
        regs.x.set(0xDEF1);
        regs.dp.set(0x32);
        regs.b.set(0x54);
        regs.a.set(0x76);
        regs.cc.set(0x01);
        cpu.interruptRequest();
        assertEquals(new UnsignedByte(0x34), memory.readByte(0x4FFF));
        assertEquals(new UnsignedByte(0x12), memory.readByte(0x4FFE));
        assertEquals(new UnsignedByte(0x78), memory.readByte(0x4FFD));
        assertEquals(new UnsignedByte(0x56), memory.readByte(0x4FFC));
        assertEquals(new UnsignedByte(0xBC), memory.readByte(0x4FFB));
        assertEquals(new UnsignedByte(0x9A), memory.readByte(0x4FFA));
        assertEquals(new UnsignedByte(0xF1), memory.readByte(0x4FF9));
        assertEquals(new UnsignedByte(0xDE), memory.readByte(0x4FF8));
        assertEquals(new UnsignedByte(0x32), memory.readByte(0x4FF7));
        assertEquals(new UnsignedByte(0x54), memory.readByte(0x4FF6));
        assertEquals(new UnsignedByte(0x76), memory.readByte(0x4FF5));
        assertEquals(new UnsignedByte(0x81), memory.readByte(0x4FF4));
        assertEquals(new UnsignedWord(0x0000), regs.pc);
        assertEquals(new UnsignedByte(0x91), regs.cc);
    }

    @Test
    public void testServiceInterruptsFiresIRQ() {
        cpu.fireIRQ = true;
        regs.s.set(0x5000);
        regs.pc.set(0x1234);
        regs.u.set(0x5678);
        regs.y.set(0x9ABC);
        regs.x.set(0xDEF1);
        regs.dp.set(0x32);
        regs.b.set(0x54);
        regs.a.set(0x76);
        regs.cc.set(0x01);
        cpu.serviceInterrupts();
        assertEquals(new UnsignedByte(0x34), memory.readByte(0x4FFF));
        assertEquals(new UnsignedByte(0x12), memory.readByte(0x4FFE));
        assertEquals(new UnsignedByte(0x78), memory.readByte(0x4FFD));
        assertEquals(new UnsignedByte(0x56), memory.readByte(0x4FFC));
        assertEquals(new UnsignedByte(0xBC), memory.readByte(0x4FFB));
        assertEquals(new UnsignedByte(0x9A), memory.readByte(0x4FFA));
        assertEquals(new UnsignedByte(0xF1), memory.readByte(0x4FF9));
        assertEquals(new UnsignedByte(0xDE), memory.readByte(0x4FF8));
        assertEquals(new UnsignedByte(0x32), memory.readByte(0x4FF7));
        assertEquals(new UnsignedByte(0x54), memory.readByte(0x4FF6));
        assertEquals(new UnsignedByte(0x76), memory.readByte(0x4FF5));
        assertEquals(new UnsignedByte(0x81), memory.readByte(0x4FF4));
        assertEquals(new UnsignedWord(0x0000), regs.pc);
        assertEquals(new UnsignedByte(0x91), regs.cc);
    }

    @Test
    public void testFastInterrruptRequestWorksCorrectly() {
        regs.s.set(0x5000);
        regs.pc.set(0x1234);
        regs.cc.set(0x01);
        cpu.fastInterruptRequest();
        assertEquals(new UnsignedByte(0x34), memory.readByte(0x4FFF));
        assertEquals(new UnsignedByte(0x12), memory.readByte(0x4FFE));
        assertEquals(new UnsignedByte(0x01), memory.readByte(0x4FFD));
        assertEquals(new UnsignedWord(0x0000), regs.pc);
        assertEquals(new UnsignedByte(0x51), regs.cc);
    }

    @Test
    public void testServiceInterruptsFiresFIRQ() {
        cpu.fireFIRQ = true;
        regs.s.set(0x5000);
        regs.pc.set(0x1234);
        regs.cc.set(0x01);
        cpu.serviceInterrupts();
        assertEquals(new UnsignedByte(0x34), memory.readByte(0x4FFF));
        assertEquals(new UnsignedByte(0x12), memory.readByte(0x4FFE));
        assertEquals(new UnsignedByte(0x01), memory.readByte(0x4FFD));
        assertEquals(new UnsignedWord(0x0000), regs.pc);
        assertEquals(new UnsignedByte(0x51), regs.cc);
    }

    @Test
    public void testNMIRequestWorksCorrectly() {
        regs.s.set(0x5000);
        regs.pc.set(0x1234);
        regs.u.set(0x5678);
        regs.y.set(0x9ABC);
        regs.x.set(0xDEF1);
        regs.dp.set(0x32);
        regs.b.set(0x54);
        regs.a.set(0x76);
        regs.cc.set(0x01);
        cpu.nonMaskableInterruptRequest();
        assertEquals(new UnsignedByte(0x34), memory.readByte(0x4FFF));
        assertEquals(new UnsignedByte(0x12), memory.readByte(0x4FFE));
        assertEquals(new UnsignedByte(0x78), memory.readByte(0x4FFD));
        assertEquals(new UnsignedByte(0x56), memory.readByte(0x4FFC));
        assertEquals(new UnsignedByte(0xBC), memory.readByte(0x4FFB));
        assertEquals(new UnsignedByte(0x9A), memory.readByte(0x4FFA));
        assertEquals(new UnsignedByte(0xF1), memory.readByte(0x4FF9));
        assertEquals(new UnsignedByte(0xDE), memory.readByte(0x4FF8));
        assertEquals(new UnsignedByte(0x32), memory.readByte(0x4FF7));
        assertEquals(new UnsignedByte(0x54), memory.readByte(0x4FF6));
        assertEquals(new UnsignedByte(0x76), memory.readByte(0x4FF5));
        assertEquals(new UnsignedByte(0x81), memory.readByte(0x4FF4));
        assertEquals(new UnsignedWord(0x0000), regs.pc);
        assertEquals(new UnsignedByte(0xD1), regs.cc);
    }

    @Test
    public void testServiceInterruptsFiresNMI() {
        cpu.fireNMI = true;
        regs.s.set(0x5000);
        regs.pc.set(0x1234);
        regs.u.set(0x5678);
        regs.y.set(0x9ABC);
        regs.x.set(0xDEF1);
        regs.dp.set(0x32);
        regs.b.set(0x54);
        regs.a.set(0x76);
        regs.cc.set(0x01);
        cpu.serviceInterrupts();
        assertEquals(new UnsignedByte(0x34), memory.readByte(0x4FFF));
        assertEquals(new UnsignedByte(0x12), memory.readByte(0x4FFE));
        assertEquals(new UnsignedByte(0x78), memory.readByte(0x4FFD));
        assertEquals(new UnsignedByte(0x56), memory.readByte(0x4FFC));
        assertEquals(new UnsignedByte(0xBC), memory.readByte(0x4FFB));
        assertEquals(new UnsignedByte(0x9A), memory.readByte(0x4FFA));
        assertEquals(new UnsignedByte(0xF1), memory.readByte(0x4FF9));
        assertEquals(new UnsignedByte(0xDE), memory.readByte(0x4FF8));
        assertEquals(new UnsignedByte(0x32), memory.readByte(0x4FF7));
        assertEquals(new UnsignedByte(0x54), memory.readByte(0x4FF6));
        assertEquals(new UnsignedByte(0x76), memory.readByte(0x4FF5));
        assertEquals(new UnsignedByte(0x81), memory.readByte(0x4FF4));
        assertEquals(new UnsignedWord(0x0000), regs.pc);
        assertEquals(new UnsignedByte(0xD1), regs.cc);
    }

    @Test
    public void testScheduleIRQ() {
        assertFalse(cpu.fireIRQ);
        cpu.scheduleIRQ();
        assertTrue(cpu.fireIRQ);
    }

    @Test
    public void testScheduleFIRQ() {
        assertFalse(cpu.fireFIRQ);
        cpu.scheduleFIRQ();
        assertTrue(cpu.fireFIRQ);
    }

    @Test
    public void testScheduleNMI() {
        assertFalse(cpu.fireNMI);
        cpu.scheduleNMI();
        assertTrue(cpu.fireNMI);
    }
}
