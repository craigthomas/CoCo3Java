/*
 * Copyright (C) 2022-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;
import org.junit.Before;
import org.junit.Test;

import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;
import static org.junit.Assert.*;

public class IOControllerTest
{
    private Memory memory;
    private RegisterSet regs;
    private IOController io;
    private Screen screen;
    private Cassette cassette;
    private CPU cpu;

    @Before
    public void setup() throws MalformedInstructionException {
        memory = new Memory();
        regs = new RegisterSet();
        screen = new Screen(1);
        cassette = new Cassette();
        io = new IOController(memory, regs, new EmulatedKeyboard(), screen, cassette);
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
        io.writeIOByte(new UnsignedWord(0xFF01), new UnsignedByte(0x11));
        UnsignedByte result = io.readByte(new UnsignedWord(0xFF01));
        assertEquals(new UnsignedByte(0x11), result);

        io.writeIOByte(new UnsignedWord(0xFF03), new UnsignedByte(0x55));
        result = io.readByte(new UnsignedWord(0xFF03));
        assertEquals(new UnsignedByte(0x55), result);

        io.writeIOByte(new UnsignedWord(0xFF21), new UnsignedByte(0x77));
        result = io.readByte(new UnsignedWord(0xFF21));
        assertEquals(new UnsignedByte(0x77), result);

        io.writeIOByte(new UnsignedWord(0xFF23), new UnsignedByte(0x99));
        result = io.readByte(new UnsignedWord(0xFF23));
        assertEquals(new UnsignedByte(0x99), result);

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
    }

    @Test
    public void testPIA1CRASetTimerTo166Millis() {
        io.writeByte(new UnsignedWord(0xFF03), new UnsignedByte(0x1));
        UnsignedByte result = io.readByte(new UnsignedWord(0xFF03));
        assertEquals(new UnsignedByte(0x1), result);
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
        assertEquals(new UnsignedWord(0x0F00), io.timerResetValue);
    }

    @Test
    public void testTimer0SetCorrectly() {
        io.writeByte(new UnsignedWord(0xFF95), new UnsignedByte(0xFF));
        assertEquals(new UnsignedByte(0xFF), io.readByte(new UnsignedWord(0xFF95)));
        assertEquals(new UnsignedWord(0x00FF), io.timerResetValue);
    }

    @Test
    public void testVerticalOffsetRegister1SetCorrectly() {
        io.writeByte(new UnsignedWord(0xFF9D), new UnsignedByte(0xFA));
        assertEquals(new UnsignedByte(0xFA), io.readByte(new UnsignedWord(0xFF9D)));
        assertEquals(new UnsignedWord(0xFA00), io.verticalOffsetRegister);
    }

    @Test
    public void testVerticalOffsetRegister0SetCorrectly() {
        io.verticalOffsetRegister = new UnsignedWord(0);
        io.writeByte(new UnsignedWord(0xFF9E), new UnsignedByte(0xFA));
        assertEquals(new UnsignedByte(0xFA), io.readByte(new UnsignedWord(0xFF9E)));
        assertEquals(new UnsignedWord(0x00FA), io.verticalOffsetRegister);
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
        io.pia1b.timerValue = 99999;
        io.regs.cc.and(~CC_I);
        io.timerTick(1);
        assertEquals(0, io.pia1b.timerValue);
    }

    @Test
    public void testInterruptThrownOnPIAInterruptsIfInterruptsTurnedOn() {
        io.pia1b.setControlRegister(new UnsignedByte(0x1));
        io.pia1b.timerValue = 99999;
        io.regs.cc.or(CC_I);
        io.timerTick(1);
        assertEquals(0, io.pia1b.timerValue);
        assertTrue(io.pia1b.getControlRegister().isMasked(0x80));
        assertFalse(io.pia1a.getControlRegister().isMasked(0x80));
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

        regs.s.set(0x0300);
        io.horizontalBorderTickValue = 999999;
        io.irqEnabled = true;
        io.irqStatus = new UnsignedByte(0x10);
        io.timerTick(1);
        cpu.executeInstruction();
        cpu.serviceInterrupts();

        assertEquals(0xDEAD, io.getWordRegister(Register.PC).getInt());
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
        assertEquals(0xAB, memory.memory[0x7BEEF]);
    }

    @Test
    public void testWriteIOByteWritesCorrectByte() {
        io.writeByte(new UnsignedWord(0xFF00), new UnsignedByte(0xAB));
        assertEquals(0xAB, io.ioMemory[0x0]);
    }

    @Test
    public void testReadWordReadsCorrectWord() {
        memory.memory[0x7BEEE] = 0xAB;
        memory.memory[0x7BEEF] = 0xCD;
        UnsignedWord result = io.readWord(new UnsignedWord(0xBEEE));
        assertEquals(new UnsignedWord(0xABCD), result);
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

//    @Test
//    public void testKeyboardIOWorksCorrectly() {
//        KeyEvent event = Mockito.mock(KeyEvent.class);
//        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_D);
//        keyboard.keyPressed(event);
//        io.writeByte(new UnsignedWord(0xFF01), new UnsignedByte(0x02));
//        io.writeByte(new UnsignedWord(0xFF03), new UnsignedByte(0x02));
//        io.writeByte(new UnsignedWord(0xFF02), new UnsignedByte(0xEF));
//        UnsignedByte highByte = io.readByte(new UnsignedWord(0xFF00));
//        assertEquals(new UnsignedByte(0xFE), highByte);
//
//        keyboard.keyReleased(event);
//        highByte = io.readByte(new UnsignedWord(0xFF00));
//        io.writeByte(new UnsignedWord(0xFF02), new UnsignedByte(0xFF));
//        assertEquals(new UnsignedByte(0xFF), highByte);
//    }

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
