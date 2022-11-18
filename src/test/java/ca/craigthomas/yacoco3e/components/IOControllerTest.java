/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.awt.event.KeyEvent;

import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;
import static org.junit.Assert.*;

public class IOControllerTest
{
    private Memory memory;
    private RegisterSet regs;
    private IOController io;
    private Keyboard keyboard;
    private Screen screen;
    private Cassette cassette;
    private CPU cpu;

    @Before
    public void setup() throws MalformedInstructionException {
        memory = new Memory();
        regs = new RegisterSet();
        keyboard = new EmulatedKeyboard();
        screen = new Screen(1);
        cassette = new Cassette();
        io = new IOController(memory, regs, keyboard, screen, cassette);
        cpu = new CPU(io);
        io.setCPU(cpu);
    }

    @Test
    public void testReadByteReadsCorrectByte() {
        memory.memory[0x7BEEF] = 0xAB;
        UnsignedByte result = io.readByte(new UnsignedWord(0xBEEF));
        assertEquals(new UnsignedByte(0xAB), result);
    }

    @Test
    public void testReadIOByteReadsCorrectByte() {
        io.pia1CRA = new UnsignedByte(0x1122);
        UnsignedByte result = io.readByte(new UnsignedWord(0xFF01));
        assertEquals(new UnsignedByte(0x1122), result);

        io.pia1DRB = new UnsignedByte(0x3344);
        result = io.readByte(new UnsignedWord(0xFF02));
        assertEquals(new UnsignedByte(0x3344), result);

        io.pia1CRB = new UnsignedByte(0x5566);
        result = io.readByte(new UnsignedWord(0xFF03));
        assertEquals(new UnsignedByte(0x5566), result);

        io.pia2CRA = new UnsignedByte(0x7788);
        result = io.readByte(new UnsignedWord(0xFF21));
        assertEquals(new UnsignedByte(0x7788), result);

        io.pia2CRB = new UnsignedByte(0x99AA);
        result = io.readByte(new UnsignedWord(0xFF23));
        assertEquals(new UnsignedByte(0x99AA), result);

        io.irqStatus = new UnsignedByte(0xBB);
        result = io.readByte(new UnsignedWord(0xFF92));
        assertEquals(new UnsignedByte(0xBB), result);

        io.firqStatus = new UnsignedByte(0xCC);
        result = io.readByte(new UnsignedWord(0xFF93));
        assertEquals(new UnsignedByte(0xCC), result);

        io.timerResetValue = new UnsignedWord(0xBEEF);
        result = io.readByte(new UnsignedWord(0xFF94));
        assertEquals(new UnsignedByte(0xBE), result);
        result = io.readByte(new UnsignedWord(0xFF95));
        assertEquals(new UnsignedByte(0xEF), result);
    }

    @Test
    public void testPIA1CRASetTimerTo635Micros() {
        io.writeByte(new UnsignedWord(0xFF01), new UnsignedByte(0x1));
        UnsignedByte result = io.readByte(new UnsignedWord(0xFF01));
        assertEquals(new UnsignedByte(0x1), result);
        assertTrue(io.pia1FastTimerEnabled);
    }

    @Test
    public void testPIA1CRASetTimerTo166Millis() {
        io.writeByte(new UnsignedWord(0xFF03), new UnsignedByte(0x1));
        UnsignedByte result = io.readByte(new UnsignedWord(0xFF03));
        assertEquals(new UnsignedByte(0x1), result);
        assertTrue(io.pia1SlowTimerEnabled);
    }

    @Test
    public void testIRQStatusSetCorrectly() {
        io.writeByte(new UnsignedWord(0xFF92), new UnsignedByte(0xFF));
        assertEquals(new UnsignedByte(0x3F), io.readByte(new UnsignedWord(0xFF92)));
    }

    @Test
    public void testFIRQStatusSetCorrectly() {
        io.writeByte(new UnsignedWord(0xFF93), new UnsignedByte(0xFF));
        assertEquals(new UnsignedByte(0x3F), io.readByte(new UnsignedWord(0xFF93)));
    }

    @Test
    public void testTimer1SetCorrectly() {
        io.writeByte(new UnsignedWord(0xFF94), new UnsignedByte(0xFF));
        assertEquals(new UnsignedByte(0x0F), io.readByte(new UnsignedWord(0xFF94)));
        assertEquals(io.timerResetValue, new UnsignedWord(0x0F00));
    }

    @Test
    public void testTimer0SetCorrectly() {
        io.writeByte(new UnsignedWord(0xFF95), new UnsignedByte(0xFF));
        assertEquals(new UnsignedByte(0xFF), io.readByte(new UnsignedWord(0xFF95)));
        assertEquals(io.timerResetValue, new UnsignedWord(0x00FF));
    }

    @Test
    public void testVerticalOffsetRegister1SetCorrectly() {
        io.writeByte(new UnsignedWord(0xFF9D), new UnsignedByte(0xFA));
        assertEquals(new UnsignedByte(0xFA), io.readByte(new UnsignedWord(0xFF9D)));
        assertEquals(io.verticalOffsetRegister, new UnsignedWord(0xFA00));
    }

    @Test
    public void testVerticalOffsetRegister0SetCorrectly() {
        io.verticalOffsetRegister = new UnsignedWord(0);
        io.writeByte(new UnsignedWord(0xFF9E), new UnsignedByte(0xFA));
        assertEquals(new UnsignedByte(0xFA), io.readByte(new UnsignedWord(0xFF9E)));
        assertEquals(io.verticalOffsetRegister, new UnsignedWord(0x00FA));
    }

    @Test
    public void testSAMDisplayOffsetRegisterClearBit0() {
        io.samDisplayOffsetRegister = new UnsignedByte(0xFF);
        io.writeByte(new UnsignedWord(0xFFC6), new UnsignedByte(0x1));
        assertFalse(io.samDisplayOffsetRegister.isMasked(0x1));
    }

    @Test
    public void testSAMDisplayOffsetRegisterSetBit0() {
        io.samDisplayOffsetRegister = new UnsignedByte(0x00);
        io.writeByte(new UnsignedWord(0xFFC7), new UnsignedByte(0x1));
        assertTrue(io.samDisplayOffsetRegister.isMasked(0x1));
    }

    @Test
    public void testSAMDisplayOffsetRegisterClearBit1() {
        io.samDisplayOffsetRegister = new UnsignedByte(0xFF);
        io.writeByte(new UnsignedWord(0xFFC8), new UnsignedByte(0x1));
        assertFalse(io.samDisplayOffsetRegister.isMasked(0x2));
    }

    @Test
    public void testSAMDisplayOffsetRegisterSetBit1() {
        io.samDisplayOffsetRegister = new UnsignedByte(0x00);
        io.writeByte(new UnsignedWord(0xFFC9), new UnsignedByte(0x1));
        assertTrue(io.samDisplayOffsetRegister.isMasked(0x2));
    }

    @Test
    public void testSAMDisplayOffsetRegisterClearBit2() {
        io.samDisplayOffsetRegister = new UnsignedByte(0xFF);
        io.writeByte(new UnsignedWord(0xFFCA), new UnsignedByte(0x1));
        assertFalse(io.samDisplayOffsetRegister.isMasked(0x4));
    }

    @Test
    public void testSAMDisplayOffsetRegisterSetBit2() {
        io.samDisplayOffsetRegister = new UnsignedByte(0x00);
        io.writeByte(new UnsignedWord(0xFFCB), new UnsignedByte(0x1));
        assertTrue(io.samDisplayOffsetRegister.isMasked(0x4));
    }

    @Test
    public void testSAMDisplayOffsetRegisterClearBit3() {
        io.samDisplayOffsetRegister = new UnsignedByte(0xFF);
        io.writeByte(new UnsignedWord(0xFFCC), new UnsignedByte(0x1));
        assertFalse(io.samDisplayOffsetRegister.isMasked(0x8));
    }

    @Test
    public void testSAMDisplayOffsetRegisterSetBit3() {
        io.samDisplayOffsetRegister = new UnsignedByte(0x00);
        io.writeByte(new UnsignedWord(0xFFCD), new UnsignedByte(0x1));
        assertTrue(io.samDisplayOffsetRegister.isMasked(0x8));
    }

    @Test
    public void testSAMDisplayOffsetRegisterClearBit4() {
        io.samDisplayOffsetRegister = new UnsignedByte(0xFF);
        io.writeByte(new UnsignedWord(0xFFCE), new UnsignedByte(0x1));
        assertFalse(io.samDisplayOffsetRegister.isMasked(0x10));
    }

    @Test
    public void testSAMDisplayOffsetRegisterSetBit4() {
        io.samDisplayOffsetRegister = new UnsignedByte(0x00);
        io.writeByte(new UnsignedWord(0xFFCF), new UnsignedByte(0x1));
        assertTrue(io.samDisplayOffsetRegister.isMasked(0x10));
    }

    @Test
    public void testSAMDisplayOffsetRegisterClearBit5() {
        io.samDisplayOffsetRegister = new UnsignedByte(0xFF);
        io.writeByte(new UnsignedWord(0xFFD0), new UnsignedByte(0x1));
        assertFalse(io.samDisplayOffsetRegister.isMasked(0x20));
    }

    @Test
    public void testSAMDisplayOffsetRegisterSetBit5() {
        io.samDisplayOffsetRegister = new UnsignedByte(0x00);
        io.writeByte(new UnsignedWord(0xFFD1), new UnsignedByte(0x1));
        assertTrue(io.samDisplayOffsetRegister.isMasked(0x20));
    }

    @Test
    public void testSAMDisplayOffsetRegisterClearBit6() {
        io.samDisplayOffsetRegister = new UnsignedByte(0xFF);
        io.writeByte(new UnsignedWord(0xFFD2), new UnsignedByte(0x1));
        assertFalse(io.samDisplayOffsetRegister.isMasked(0x40));
    }

    @Test
    public void testSAMDisplayOffsetRegisterSetBit6() {
        io.samDisplayOffsetRegister = new UnsignedByte(0x00);
        io.writeByte(new UnsignedWord(0xFFD3), new UnsignedByte(0x1));
        assertTrue(io.samDisplayOffsetRegister.isMasked(0x40));
    }

    @Test
    public void testUpdateVerticalOffsetCoCoCompatibleWorksCorrectly() {
        io.cocoCompatibleMode = true;
        io.verticalOffsetRegister = new UnsignedWord(0x0402);
        io.samDisplayOffsetRegister = new UnsignedByte(0x20);
        io.updateVerticalOffset();
        assertEquals(16392, screen.getMemoryOffset());
    }

    @Test
    public void testNoInterruptThrownOnPIAInterruptsIfInterruptsTurnedOff() {
        io.pia1SlowTimer = 99999;
        io.pia1SlowTimerEnabled = true;
        io.regs.cc.and(~CC_I);
        io.timerTick(1);
        assertEquals(0, io.pia1SlowTimer);
    }

    @Test
    public void testInterruptThrownOnPIAInterruptsIfInterruptsTurnedOn() {
        io.pia1CRB = new UnsignedByte(0x1);
        io.pia1SlowTimer = 99999;
        io.pia1SlowTimerEnabled = true;
        io.regs.cc.or(CC_I);
        io.timerTick(1);
        assertEquals(0, io.pia1SlowTimer);
        assertFalse(io.pia1CRA.isMasked(0x80));
        assertTrue(io.pia1CRB.isMasked(0x80));
    }

    @Test
    public void testInterruptThrownOnPIAInterruptsIfInterruptsTurnedOnWithCorrectFlag() {
        io.pia1FastTimer = 99999;
        io.pia1FastTimerEnabled = true;
        io.pia1CRB = new UnsignedByte(0);
        io.pia1CRA = new UnsignedByte(0x1);
        io.regs.cc.or(CC_I);
        io.timerTick(1);
        assertEquals(0, io.pia1FastTimer);
        assertTrue(io.pia1CRA.isMasked(0x80));
        assertFalse(io.pia1CRB.isMasked(0x80));
    }

    @Test
    public void testGIMETimerInterruptFiresCorrectly() throws MalformedInstructionException {
        memory.enableAllRAMMode();
        memory.rom = new short [0x4000];
        memory.rom[0x3FF8] = (short) 0xDE;
        memory.rom[0x3FF9] = (short) 0xAD;

        regs.s.set(new UnsignedWord(0x0300));
        io.timerTickCounter = 999999;
        io.timerResetValue = new UnsignedWord(0xBEEF);
        io.timerValue = new UnsignedWord(0x1);
        io.irqEnabled = true;
        io.irqStatus = new UnsignedByte(0x20);
        io.timerTick(1);
        cpu.executeInstruction();
        cpu.serviceInterrupts();

        assertEquals(new UnsignedWord(0xBEEF), io.timerValue);
        assertEquals(new UnsignedWord(0xDEAD), io.getWordRegister(Register.PC));
    }

    @Test
    public void testGIMETimerFastInterruptFiresCorrectly() throws MalformedInstructionException {
        memory.enableAllRAMMode();
        memory.rom = new short [0x4000];
        memory.rom[0x3FF6] = (short) 0xDE;
        memory.rom[0x3FF7] = (short) 0xAD;

        regs.s.set(new UnsignedWord(0x0300));
        io.timerTickCounter = 999999;
        io.timerResetValue = new UnsignedWord(0xBEEF);
        io.timerValue = new UnsignedWord(0x1);
        io.firqEnabled = true;
        io.firqStatus = new UnsignedByte(0x20);
        io.timerTick(1);
        cpu.executeInstruction();
        cpu.serviceInterrupts();

        assertEquals(new UnsignedWord(0xBEEF), io.timerValue);
        assertEquals(new UnsignedWord(0xDEAD), io.getWordRegister(Register.PC));
    }

    @Test
    public void testGIMEHorizontalBorderInterruptFiresCorrectly() throws MalformedInstructionException {
        memory.enableAllRAMMode();
        memory.rom = new short [0x4000];
        memory.rom[0x3FF8] = (short) 0xDE;
        memory.rom[0x3FF9] = (short) 0xAD;

        regs.s.set(new UnsignedWord(0x0300));
        io.horizontalBorderTickValue = 999999;
        io.irqEnabled = true;
        io.irqStatus = new UnsignedByte(0x10);
        io.timerTick(1);
        cpu.executeInstruction();
        cpu.serviceInterrupts();

        assertEquals(new UnsignedWord(0xDEAD), io.getWordRegister(Register.PC));
    }

    @Test
    public void testGIMEHorizontalBorderFastInterruptFiresCorrectly() throws MalformedInstructionException {
        memory.enableAllRAMMode();
        memory.rom = new short [0x4000];
        memory.rom[0x3FF6] = (short) 0xDE;
        memory.rom[0x3FF7] = (short) 0xAD;

        regs.s.set(new UnsignedWord(0x0300));
        io.horizontalBorderTickValue = 999999;
        io.firqEnabled = true;
        io.firqStatus = new UnsignedByte(0x10);
        io.timerTick(1);
        cpu.executeInstruction();
        cpu.serviceInterrupts();
        assertEquals(new UnsignedWord(0xDEAD), io.getWordRegister(Register.PC));
    }

    @Test
    public void testGIMEVerticalBorderInterruptFiresCorrectly() throws MalformedInstructionException {
        memory.enableAllRAMMode();
        memory.rom = new short [0x4000];
        memory.rom[0x3FF8] = (short) 0xDE;
        memory.rom[0x3FF9] = (short) 0xAD;

        regs.s.set(new UnsignedWord(0x0300));
        io.verticalBorderTickValue = 999999;
        io.irqEnabled = true;
        io.irqStatus = new UnsignedByte(0x8);
        io.timerTick(1);
        cpu.executeInstruction();
        cpu.serviceInterrupts();
        assertEquals(new UnsignedWord(0xDEAD), io.getWordRegister(Register.PC));
    }

    @Test
    public void testGIMEVerticalBorderFastInterruptFiresCorrectly() throws MalformedInstructionException {
        memory.enableAllRAMMode();
        memory.rom = new short [0x4000];
        memory.rom[0x3FF6] = (short) 0xDE;
        memory.rom[0x3FF7] = (short) 0xAD;

        regs.s.set(new UnsignedWord(0x0300));
        io.verticalBorderTickValue = 999999;
        io.firqEnabled = true;
        io.firqStatus = new UnsignedByte(0x8);
        io.timerTick(1);
        cpu.executeInstruction();
        cpu.serviceInterrupts();
        assertEquals(new UnsignedWord(0xDEAD), io.getWordRegister(Register.PC));
    }

    @Test
    public void testWriteByteWritesCorrectByte() {
        io.writeByte(new UnsignedWord(0xBEEF), new UnsignedByte(0xAB));
        assertEquals(memory.memory[0x7BEEF], 0xAB);
    }

    @Test
    public void testWriteIOByteWritesCorrectByte() {
        io.writeByte(new UnsignedWord(0xFF00), new UnsignedByte(0xAB));
        assertEquals(io.ioMemory[0x0], 0xAB);
    }

    @Test
    public void testReadWordReadsCorrectWord() {
        memory.memory[0x7BEEE] = 0xAB;
        memory.memory[0x7BEEF] = 0xCD;
        UnsignedWord result = io.readWord(new UnsignedWord(0xBEEE));
        assertEquals(new UnsignedWord(0xABCD), result);
    }

    @Test
    public void testGetImmediateReadsAddressFromPC() {
        memory.memory[0x7BEEE] = 0xAB;
        memory.memory[0x7BEEF] = 0xCD;
        regs.pc.set(new UnsignedWord(0xBEEE));
        MemoryResult result = io.getImmediateWord();
        assertEquals(2, result.bytesConsumed);
        assertEquals(new UnsignedWord(0xABCD), result.value);
    }

    @Test
    public void testGetDirectReadsAddressFromDPAndPC() {
        memory.memory[0x7BEEE] = 0xCD;
        regs.pc.set(new UnsignedWord(0xBEEE));
        regs.dp.set(new UnsignedByte(0xAB));
        MemoryResult result = io.getDirect();
        assertEquals(1, result.bytesConsumed);
        assertEquals(new UnsignedWord(0xABCD), result.value);
    }

    @Test
    public void testPushStackWritesToMemoryLocation() {
        regs.s.set(new UnsignedWord(0xA000));
        io.pushStack(Register.S, new UnsignedByte(0x98));
        assertEquals(memory.memory[0x79FFF], new UnsignedByte(0x98).getShort());
    }

    @Test
    public void testPushStackWritesToMemoryLocationUsingUStack() {
        regs.u.set(new UnsignedWord(0xA000));
        io.pushStack(Register.U, new UnsignedByte(0x98));
        assertEquals(memory.memory[0x79FFF], new UnsignedByte(0x98).getShort());
    }

    @Test
    public void testPopStackReadsMemoryLocation() {
        regs.s.set(new UnsignedWord(0xA000));
        memory.memory[0x7A000] = 0x98;
        UnsignedByte result = io.popStack(Register.S);
        assertEquals(new UnsignedByte(0x98), result);
        assertEquals(new UnsignedWord(0xA001), regs.s);
    }

    @Test
    public void testPopStackReadsMemoryLocationFromU() {
        regs.u.set(new UnsignedWord(0xA000));
        memory.memory[0x7A000] = 0x98;
        UnsignedByte result = io.popStack(Register.U);
        assertEquals(new UnsignedByte(0x98), result);
        assertEquals(new UnsignedWord(0xA001), regs.u);
    }

    @Test
    public void testGetWordRegisterWorksCorrectly() {
        regs.y.set(new UnsignedWord(0xA));
        regs.x.set(new UnsignedWord(0xB));
        regs.u.set(new UnsignedWord(0xC));
        regs.s.set(new UnsignedWord(0xD));
        regs.setD(new UnsignedWord(0xE));
        regs.pc.set(new UnsignedWord(0xF));
        assertEquals(new UnsignedWord(0xA), io.getWordRegister(Register.Y));
        assertEquals(new UnsignedWord(0xB), io.getWordRegister(Register.X));
        assertEquals(new UnsignedWord(0xC), io.getWordRegister(Register.U));
        assertEquals(new UnsignedWord(0xD), io.getWordRegister(Register.S));
        assertEquals(new UnsignedWord(0xE), io.getWordRegister(Register.D));
        assertEquals(new UnsignedWord(0xF), io.getWordRegister(Register.PC));
        assertNull(io.getWordRegister(Register.UNKNOWN));
    }

    @Test
    public void testWriteIOByteWritesToPARs() {
        io.writeIOByte(new UnsignedWord(0xFFA0), new UnsignedByte(0xA0));
        assertEquals(0xA0, memory.executivePAR[0]);
        
        io.writeIOByte(new UnsignedWord(0xFFA1), new UnsignedByte(0xA1));
        assertEquals(0xA1, memory.executivePAR[1]);

        io.writeIOByte(new UnsignedWord(0xFFA2), new UnsignedByte(0xA2));
        assertEquals(0xA2, memory.executivePAR[2]);

        io.writeIOByte(new UnsignedWord(0xFFA3), new UnsignedByte(0xA3));
        assertEquals(0xA3, memory.executivePAR[3]);

        io.writeIOByte(new UnsignedWord(0xFFA4), new UnsignedByte(0xA4));
        assertEquals(0xA4, memory.executivePAR[4]);

        io.writeIOByte(new UnsignedWord(0xFFA5), new UnsignedByte(0xA5));
        assertEquals(0xA5, memory.executivePAR[5]);

        io.writeIOByte(new UnsignedWord(0xFFA6), new UnsignedByte(0xA6));
        assertEquals(0xA6, memory.executivePAR[6]);

        io.writeIOByte(new UnsignedWord(0xFFA7), new UnsignedByte(0xA7));
        assertEquals(0xA7, memory.executivePAR[7]);

        io.writeIOByte(new UnsignedWord(0xFFA8), new UnsignedByte(0xA8));
        assertEquals(0xA8, memory.taskPAR[0]);

        io.writeIOByte(new UnsignedWord(0xFFA9), new UnsignedByte(0xA9));
        assertEquals(0xA9, memory.taskPAR[1]);

        io.writeIOByte(new UnsignedWord(0xFFAA), new UnsignedByte(0xAA));
        assertEquals(0xAA, memory.taskPAR[2]);

        io.writeIOByte(new UnsignedWord(0xFFAB), new UnsignedByte(0xAB));
        assertEquals(0xAB, memory.taskPAR[3]);

        io.writeIOByte(new UnsignedWord(0xFFAC), new UnsignedByte(0xAC));
        assertEquals(0xAC, memory.taskPAR[4]);

        io.writeIOByte(new UnsignedWord(0xFFAD), new UnsignedByte(0xAD));
        assertEquals(0xAD, memory.taskPAR[5]);

        io.writeIOByte(new UnsignedWord(0xFFAE), new UnsignedByte(0xAE));
        assertEquals(0xAE, memory.taskPAR[6]);

        io.writeIOByte(new UnsignedWord(0xFFAF), new UnsignedByte(0xAF));
        assertEquals(0xAF, memory.taskPAR[7]);
    }

    @Test
    public void testMMUEnableDisableWorksCorrectly() {
        io.writeIOByte(new UnsignedWord(0xFF90), new UnsignedByte(0x0));
        assertFalse(memory.mmuEnabled);

        io.writeIOByte(new UnsignedWord(0xFF90), new UnsignedByte(0x40));
        assertTrue(memory.mmuEnabled);
    }

    @Test
    public void testPARSelectWorksCorrectly() {
        io.writeIOByte(new UnsignedWord(0xFF91), new UnsignedByte(0x0));
        assertFalse(memory.executiveParEnabled);

        io.writeIOByte(new UnsignedWord(0xFF91), new UnsignedByte(0x1));
        assertTrue(memory.executiveParEnabled);
    }

    @Test
    public void testGetIndexedRegisterWorksCorrectly() {
        UnsignedByte postByte = new UnsignedByte(0x00);
        assertEquals(Register.X, io.getIndexedRegister(postByte));

        postByte = new UnsignedByte(0x20);
        assertEquals(Register.Y, io.getIndexedRegister(postByte));

        postByte = new UnsignedByte(0x40);
        assertEquals(Register.U, io.getIndexedRegister(postByte));

        postByte = new UnsignedByte(0x60);
        assertEquals(Register.S, io.getIndexedRegister(postByte));
    }

    @Test
    public void testGetIndexedZeroOffset() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x84));
        assertEquals(new UnsignedWord(0xB000), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedZeroOffsetIndirect() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeWord(new UnsignedWord(0xB000), new UnsignedWord(0xBEEF));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x94));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
    }

    @Test
    public void testGetIndexed5BitPositiveOffset() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x01));
        assertEquals(new UnsignedWord(0xB001), io.getIndexed().value);
    }

    @Test
    public void testGetIndexed5BitNegativeOffset() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x1F));
        assertEquals(new UnsignedWord(0xAFFF), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedRPostIncrement() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x80));
        assertEquals(new UnsignedWord(0xB000), io.getIndexed().value);
        assertEquals(new UnsignedWord(0xB001), regs.x);
    }

    @Test
    public void testGetIndexedRPostIncrementTwice() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x81));
        assertEquals(new UnsignedWord(0xB000), io.getIndexed().value);
        assertEquals(new UnsignedWord(0xB002), regs.x);
    }

    @Test
    public void testGetIndexedRPostIncrementTwiceIndirect() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeWord(new UnsignedWord(0xB000), new UnsignedWord(0xBEEF));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x91));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
        assertEquals(new UnsignedWord(0xB002), regs.x);
    }

    @Test
    public void testGetIndexedRDecrement() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x82));
        assertEquals(new UnsignedWord(0xAFFF), io.getIndexed().value);
        assertEquals(new UnsignedWord(0xAFFF), regs.x);
    }

    @Test
    public void testGetIndexedRDecrementTwice() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x83));
        assertEquals(new UnsignedWord(0xAFFE), io.getIndexed().value);
        assertEquals(new UnsignedWord(0xAFFE), regs.x);
    }

    @Test
    public void testGetIndexedRDecrementTwiceIndirect() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeWord(new UnsignedWord(0xAFFE), new UnsignedWord(0xBEEF));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x93));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
        assertEquals(new UnsignedWord(0xAFFE), regs.x);
    }

    @Test
    public void testGetIndexedRWithBOffset() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        regs.b.set(new UnsignedByte(0x0B));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x85));
        assertEquals(new UnsignedWord(0xB00B), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedRWithBOffsetIndirect() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        regs.b.set(new UnsignedByte(0x0B));
        io.writeWord(new UnsignedWord(0xB00B), new UnsignedWord(0xBEEF));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x95));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedRWithAOffset() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        regs.a.set(new UnsignedByte(0x0A));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x86));
        assertEquals(new UnsignedWord(0xB00A), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedRWithAOffsetIndirect() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        regs.a.set(new UnsignedByte(0x0A));
        io.writeWord(new UnsignedWord(0xB00A), new UnsignedWord(0xBEEF));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x96));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
    }

    @Test
    public void testGetIndexed8BitPositiveOffset() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x8802));
        assertEquals(new UnsignedWord(0xB002), io.getIndexed().value);
    }

    @Test
    public void testGetIndexed8BitNegativeOffset() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x88FE));
        assertEquals(new UnsignedWord(0xAFFE), io.getIndexed().value);
    }

    @Test
    public void testGetIndexed8BitPositiveOffsetIndirect() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeWord(new UnsignedWord(0xB002), new UnsignedWord(0xBEEF));
        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x9802));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
    }

    @Test
    public void testGetIndexed8BitNegativeOffsetIndirect() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeWord(new UnsignedWord(0xAFFE), new UnsignedWord(0xBEEF));
        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x98FE));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
    }

    @Test
    public void testGetIndexed16BitPositiveOffset() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x89));
        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0x0200));
        assertEquals(new UnsignedWord(0xB200), io.getIndexed().value);
    }

    @Test
    public void testGetIndexed16BitNegativeOffset() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x89));
        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0xFE00));
        assertEquals(new UnsignedWord(0xAE00), io.getIndexed().value);
    }

    @Test
    public void testGetIndexed16BitPositiveOffsetIndirect() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeWord(new UnsignedWord(0xB200), new UnsignedWord(0xBEEF));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x99));
        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0x0200));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
    }

    @Test
    public void testGetIndexed16BitNegativeOffsetIndirect() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        io.writeWord(new UnsignedWord(0xAE00), new UnsignedWord(0xBEEF));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x99));
        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0xFE00));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedRWithDOffset() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        regs.setD(new UnsignedWord(0x0200));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x8B));
        assertEquals(new UnsignedWord(0xB200), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedRWithDOffsetIndirect() throws MalformedInstructionException {
        regs.x.set(new UnsignedWord(0xB000));
        regs.setD(new UnsignedWord(0x0200));
        io.writeWord(new UnsignedWord(0xB200), new UnsignedWord(0xBEEF));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x9B));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedPCWith8BitPositiveOffset() throws MalformedInstructionException {
        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x8C0A));
        assertEquals(new UnsignedWord(0x000C), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedPCWith8BitNegativeOffset() throws MalformedInstructionException {
        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x8CFC));
        assertEquals(new UnsignedWord(0xFFFE), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedPCWith8BitPositiveOffsetIndexed() throws MalformedInstructionException {
        io.writeWord(new UnsignedWord(0x000C), new UnsignedWord(0xBEEF));
        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x9C0A));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedPCWith8BitNegativeOffsetIndexed() throws MalformedInstructionException {
        memory.rom = new short [0x4000];
        memory.rom[0x3FFC] = (short) 0xBE;
        memory.rom[0x3FFD] = (short) 0xEF;

        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x9CFA));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedPCWith16BitPositiveOffset() throws MalformedInstructionException {
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x8D));
        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0x0200));
        assertEquals(new UnsignedWord(0x0203), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedPCWith16BitNegativeOffset() throws MalformedInstructionException {
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x8D));
        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0xFE00));
        assertEquals(new UnsignedWord(0xFE03), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedPCWith16BitPositiveOffsetIndexed() throws MalformedInstructionException {
        io.writeWord(new UnsignedWord(0x0203), new UnsignedWord(0xBEEF));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x9D));
        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0x0200));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedPCWith16BitNegativeOffsetIndexed() throws MalformedInstructionException {
        io.writeWord(new UnsignedWord(0xFE03), new UnsignedWord(0xBEEF));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x9D));
        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0xFE00));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
    }

    @Test
    public void testGetIndexedExtendedIndirect() throws MalformedInstructionException {
        io.writeWord(new UnsignedWord(0xB000), new UnsignedWord(0xBEEF));
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x9F));
        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0xB000));
        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
    }

    @Test(expected = MalformedInstructionException.class)
    public void testGetIndexedIllegalPostByteExceptionOnRPostIncrement() throws MalformedInstructionException {
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x90));
        io.getIndexed();
    }

    @Test(expected = MalformedInstructionException.class)
    public void testGetIndexedIllegalPostByteExceptionOnRPostDecrement() throws MalformedInstructionException {
        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x92));
        io.getIndexed();
    }

    @Test
    public void testKeyboardIOWorksCorrectly() {
        KeyEvent event = Mockito.mock(KeyEvent.class);
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_D);
        keyboard.keyPressed(event);
        io.writeByte(new UnsignedWord(0xFF02), new UnsignedByte(0xEF));
        UnsignedByte highByte = io.readByte(new UnsignedWord(0xFF00));
        assertEquals(new UnsignedByte(0xFE), highByte);

        keyboard.keyReleased(event);
        highByte = io.readByte(new UnsignedWord(0xFF00));
        io.writeByte(new UnsignedWord(0xFF02), new UnsignedByte(0xFF));
        assertEquals(new UnsignedByte(0xFF), highByte);
    }

    @Test
    public void testDisableRAMMode() {
        memory.enableAllRAMMode();
        io.writeByte(new UnsignedWord(0xFFDE), new UnsignedByte(0));
        assertFalse(memory.allRAMMode);
    }

    @Test
    public void testEnableRAMMode() {
        memory.disableAllRAMMode();
        io.writeByte(new UnsignedWord(0xFFDF), new UnsignedByte(0));
        assertTrue(memory.allRAMMode);
    }

    @Test
    public void testSAMR1SetHighSpeed() {
        io.writeByte(new UnsignedWord(0xFFD9), new UnsignedByte(0));
        assertEquals(new UnsignedByte(0x02), io.samClockSpeed);
        assertEquals(IOController.HIGH_SPEED_CLOCK_FREQUENCY, io.tickRefreshAmount);
    }

    @Test
    public void testSAMR1SetClearHighSpeed() {
        io.writeByte(new UnsignedWord(0xFFD9), new UnsignedByte(0));
        io.writeByte(new UnsignedWord(0xFFD8), new UnsignedByte(0));
        assertEquals(new UnsignedByte(0x00), io.samClockSpeed);
        assertEquals(IOController.LOW_SPEED_CLOCK_FREQUENCY, io.tickRefreshAmount);
    }

    @Test
    public void testResetSetsCorrectValues() {
        memory.rom = new short [0x4000];
        memory.rom[0x3FFE] = (short) 0xC0;

        io.reset();
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_H));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertTrue(io.regs.cc.isMasked(CC_F));
        assertTrue(io.regs.cc.isMasked(CC_I));

        assertEquals(new UnsignedWord(0xC000), regs.pc);
        assertFalse(memory.mmuEnabled);
    }

    @Test
    public void testTurnCassetteMotorOnOff() {
        io.writeByte(new UnsignedWord(0xFF21), new UnsignedByte(0x08));
        assertTrue(cassette.isMotorOn());

        io.writeByte(new UnsignedWord(0xFF21), new UnsignedByte(0x00));
        assertFalse(cassette.isMotorOn());
    }
}
