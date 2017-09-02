/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IOControllerTest
{
    private Memory memory;
    private RegisterSet regs;
    private IOController io;

    @Before
    public void setUp() {
        memory = new Memory();
        regs = new RegisterSet();
        io = new IOController(memory, regs);
    }

    @Test
    public void testReadByteReadsCorrectByte() {
        memory.memory[0x7BEEF] = 0xAB;
        UnsignedByte result = io.readByte(new UnsignedWord(0xBEEF));
        assertEquals(new UnsignedByte(0xAB), result);
    }

    @Test
    public void testReadIOByteReadsCorrectByte() {
        io.ioMemory[0x0] = 0xAB;
        UnsignedByte result = io.readByte(new UnsignedWord(0xFF00));
        assertEquals(new UnsignedByte(0xAB), result);
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
    public void testReadIOWordReadsCorrectWord() {
        io.ioMemory[0x0] = 0xAB;
        io.ioMemory[0x1] = 0xCD;
        UnsignedWord result = io.readWord(new UnsignedWord(0xFF00));
        assertEquals(new UnsignedWord(0xABCD), result);
    }

    @Test
    public void testGetImmediateReadsAddressFromPC() {
        memory.memory[0x7BEEE] = 0xAB;
        memory.memory[0x7BEEF] = 0xCD;
        regs.setPC(new UnsignedWord(0xBEEE));
        MemoryResult result = io.getImmediateWord();
        assertEquals(2, result.getBytesConsumed());
        assertEquals(new UnsignedWord(0xABCD), result.get());
    }

    @Test
    public void testGetDirectReadsAddressFromDPAndPC() {
        memory.memory[0x7BEEE] = 0xCD;
        regs.setPC(new UnsignedWord(0xBEEE));
        regs.setDP(new UnsignedByte(0xAB));
        MemoryResult result = io.getDirect();
        assertEquals(1, result.getBytesConsumed());
        assertEquals(new UnsignedWord(0xABCD), result.get());
    }

    @Test
    public void testPushStackWritesToMemoryLocation() {
        regs.setS(new UnsignedWord(0xA000));
        io.pushStack(Register.S, new UnsignedByte(0x98));
        assertEquals(memory.memory[0x79FFF], new UnsignedByte(0x98).getShort());
    }

    @Test
    public void testPushStackWritesToMemoryLocationUsingUStack() {
        regs.setU(new UnsignedWord(0xA000));
        io.pushStack(Register.U, new UnsignedByte(0x98));
        assertEquals(memory.memory[0x79FFF], new UnsignedByte(0x98).getShort());
    }

    @Test
    public void testPopStackReadsMemoryLocation() {
        regs.setS(new UnsignedWord(0xA000));
        memory.memory[0x7A000] = 0x98;
        UnsignedByte result = io.popStack(Register.S);
        assertEquals(new UnsignedByte(0x98), result);
        assertEquals(new UnsignedWord(0xA001), regs.getS());
    }

    @Test
    public void testPopStackReadsMemoryLocationFromU() {
        regs.setU(new UnsignedWord(0xA000));
        memory.memory[0x7A000] = 0x98;
        UnsignedByte result = io.popStack(Register.U);
        assertEquals(new UnsignedByte(0x98), result);
        assertEquals(new UnsignedWord(0xA001), regs.getU());
    }

    @Test
    public void testBinaryAddWordZeroValues() {
        UnsignedWord result = io.binaryAdd(new UnsignedWord(0), new UnsignedWord(0), false, false, false);
        assertEquals(new UnsignedWord(0), result);
        assertEquals(new UnsignedByte(0), regs.getCC());
    }

    @Test
    public void testBinaryAddWordOnes() {
        UnsignedWord result = io.binaryAdd(new UnsignedWord(1), new UnsignedWord(1), false, false, false);
        assertEquals(new UnsignedWord(2), result);
        assertEquals(new UnsignedByte(0), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesNotChangedOnFalse() {
        UnsignedWord result = io.binaryAdd(new UnsignedWord(  0xFFFF), new UnsignedWord(1), false, false, false);
        assertEquals(new UnsignedWord(0), result);
        assertEquals(new UnsignedByte(0), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesOverflow() {
        UnsignedWord result = io.binaryAdd(new UnsignedWord(  0xFFFF), new UnsignedWord(1), false, false, true);
        assertEquals(new UnsignedWord(0), result);
        assertEquals(new UnsignedByte(IOController.CC_V), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesNoOverflow() {
        UnsignedWord result = io.binaryAdd(new UnsignedWord(  1), new UnsignedWord(1), false, false, true);
        assertEquals(new UnsignedWord(2), result);
        assertEquals(new UnsignedByte(0), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesCarry() {
        UnsignedWord result = io.binaryAdd(new UnsignedWord(  0xFFFF), new UnsignedWord(1), false, true, false);
        assertEquals(new UnsignedWord(0), result);
        assertEquals(new UnsignedByte(IOController.CC_C), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesNoCarry() {
        UnsignedWord result = io.binaryAdd(new UnsignedWord(  1), new UnsignedWord(1), false, true, false);
        assertEquals(new UnsignedWord(2), result);
        assertEquals(new UnsignedByte(0), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesHalfCarry() {
        UnsignedWord result = io.binaryAdd(new UnsignedWord(  0xFFFF), new UnsignedWord(1), true, false, false);
        assertEquals(new UnsignedWord(0), result);
        assertEquals(new UnsignedByte(IOController.CC_H), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesNoHalfCarry() {
        UnsignedWord result = io.binaryAdd(new UnsignedWord(  0), new UnsignedWord(1), true, false, false);
        assertEquals(new UnsignedWord(1), result);
        assertEquals(new UnsignedByte(0), regs.getCC());
    }

    @Test
    public void testGetWordRegisterWorksCorrectly() {
        regs.setY(new UnsignedWord(0xA));
        regs.setX(new UnsignedWord(0xB));
        regs.setU(new UnsignedWord(0xC));
        regs.setS(new UnsignedWord(0xD));
        regs.setD(new UnsignedWord(0xE));
        regs.setPC(new UnsignedWord(0xF));
        assertEquals(new UnsignedWord(0xA), io.getWordRegister(Register.Y));
        assertEquals(new UnsignedWord(0xB), io.getWordRegister(Register.X));
        assertEquals(new UnsignedWord(0xC), io.getWordRegister(Register.U));
        assertEquals(new UnsignedWord(0xD), io.getWordRegister(Register.S));
        assertEquals(new UnsignedWord(0xE), io.getWordRegister(Register.D));
        assertEquals(new UnsignedWord(0xF), io.getWordRegister(Register.PC));
        assertNull(io.getWordRegister(Register.UNKNOWN));
    }

    @Test
    public void testGetByteRegisterWorksCorrectly() {
        regs.setA(new UnsignedByte(0xA));
        regs.setB(new UnsignedByte(0xB));
        regs.setDP(new UnsignedByte(0xC));
        regs.setCC(new UnsignedByte(0xD));
        assertEquals(new UnsignedByte(0xA), io.getByteRegister(Register.A));
        assertEquals(new UnsignedByte(0xB), io.getByteRegister(Register.B));
        assertEquals(new UnsignedByte(0xC), io.getByteRegister(Register.DP));
        assertEquals(new UnsignedByte(0xD), io.getByteRegister(Register.CC));
        assertNull(io.getByteRegister(Register.UNKNOWN));
    }

    @Test
    public void testCCInterruptSet() {
        assertFalse(io.ccInterruptSet());
        io.setCCInterrupt();
        assertTrue(io.ccInterruptSet());
    }

    @Test
    public void testCCHalfCarrySet() {
        assertFalse(io.ccHalfCarrySet());
        io.setCCHalfCarry();
        assertTrue(io.ccHalfCarrySet());
    }

    @Test
    public void testCCFastInterruptSet() {
        assertFalse(io.ccFastInterruptSet());
        io.setCCFastInterrupt();
        assertTrue(io.ccFastInterruptSet());
    }

    @Test
    public void testCCEverythingSet() {
        assertFalse(io.ccEverythingSet());
        io.setCCEverything();
        assertTrue(io.ccEverythingSet());
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
}
