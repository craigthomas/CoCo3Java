/*
 * Copyright (C) 2023 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;
import org.junit.Before;
import org.junit.Test;

import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;
import static org.junit.Assert.*;

public class BranchInstructionTest {
    private IOController io;
    private InstructionBundle bundle;
    private CPU cpu;
    private RegisterSet regs;
    private Memory memory;

    @Before
    public void setUp() {
        Screen screen = new Screen(1);
        Cassette cassette = new Cassette();
        memory = new Memory();
        regs = new RegisterSet();
        MemoryResult memoryResult = new MemoryResult(2, new UnsignedWord(0x000A));
        io = new IOController(memory, regs, new EmulatedKeyboard(), screen, cassette);
        cpu = new CPU(io);
        bundle = new InstructionBundle(memoryResult, io);
    }

    @Test
    public void testBranchAlwaysReturnsTrue() {
        assertTrue(BranchInstruction.branchAlways(null));
    }

    @Test
    public void testBranchAlways() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x201F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchAlwaysNegativeOffset() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x20FE);
        cpu.executeInstruction();
        assertEquals(0x0000, regs.pc.getInt());
    }

    @Test
    public void testBranchAlwaysNegativeOffsetLoopsAround() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x20FD);
        cpu.executeInstruction();
        assertEquals(0xFFFF, regs.pc.getInt());
    }

    @Test
    public void testBranchAlwaysSimpleCaseWorksCorrectly() throws MalformedInstructionException {
        regs.pc.set(0x0009);
        io.writeWord(0x0009, 0x207F);
        cpu.executeInstruction();
        assertEquals(0x008A, regs.pc.getInt());
    }

    @Test
    public void testBranchAlwaysSimpleNegativeNumber() throws MalformedInstructionException {
        regs.pc.set(0x0056);
        io.writeWord(0x0056, 0x20AA);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchNeverReturnsFalse() {
        assertFalse(BranchInstruction.branchNever(null));
    }

    @Test
    public void testBranchNever() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x21FF);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchToSubroutineSavesPCAndReturnsTrue() {
        regs.s.set(0x0200);
        regs.pc.set(0xBEEF);
        assertTrue(BranchInstruction.branchToSubroutine(bundle));
        assertEquals(0xEF, io.readByte(new UnsignedWord(0x01FF)).getShort());
        assertEquals(0xBE, io.readByte(new UnsignedWord(0x01FE)).getShort());
    }

    @Test
    public void testBranchSubroutine() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x8D1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
        assertEquals(0x02, memory.readByte(io.getWordRegister(Register.S).next()).getShort());
    }

    @Test
    public void testBranchSubroutineNegativeOffsetCorrect() throws MalformedInstructionException {
        regs.pc.set(0x0021);
        io.writeWord(0x0021, 0x8DDD);
        cpu.executeInstruction();
        assertEquals(0x0000, regs.pc.getInt());
        assertEquals(0x23, memory.readByte(io.getWordRegister(Register.S).next()).getShort());
    }

    @Test
    public void testBranchOnHighReturnsTrueIfBothCarryAndZeroNotSet() {
        assertTrue(BranchInstruction.branchOnHigh(bundle));
    }

    @Test
    public void testBranchOnHighReturnsFalseIfCarrySet() {
        regs.cc.or(CC_C);
        assertFalse(BranchInstruction.branchOnHigh(bundle));
    }

    @Test
    public void testBranchOnHighReturnsFalseIfZeroSet() {
        regs.cc.or(CC_Z);
        assertFalse(BranchInstruction.branchOnHigh(bundle));
    }

    @Test
    public void testBranchOnHighCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x221F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnHighNotCalledWhenCarrySet() throws MalformedInstructionException {
        io.regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x221F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnHighNotCalledWhenZeroSet() throws MalformedInstructionException {
        io.regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x221F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLowerReturnsTrueIfCarrySet() {
        regs.cc.or(CC_C);
        assertTrue(BranchInstruction.branchOnLower(bundle));
    }

    @Test
    public void testBranchOnLowerReturnsTrueIfZeroSet() {
        regs.cc.or(CC_Z);
        assertTrue(BranchInstruction.branchOnLower(bundle));
    }

    @Test
    public void testBranchOnLowerReturnsTrueIfZeroAndCarrySet() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_C);
        assertTrue(BranchInstruction.branchOnLower(bundle));
    }

    @Test
    public void testBranchOnLowerReturnsFalseIfCarryAndZeroClear() {
        assertFalse(BranchInstruction.branchOnLower(bundle));
    }

    @Test
    public void testBranchOnLowerNotCalledWhenZeroCarryClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x231F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLowerCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x231F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLowerCalledWhenCarryAndZeroSet() throws MalformedInstructionException {
        io.regs.cc.or(CC_Z);
        io.regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x231F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLowerCalledCorrectWithZeroOnly() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x231F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLowerCalledCorrectWithCarryOnly() throws MalformedInstructionException {
        regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x231F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnCarryClearReturnsTrueIfCarryClear() {
        assertTrue(BranchInstruction.branchOnCarryClear(bundle));
    }

    @Test
    public void testBranchOnCarryClearReturnsFalseIfCarrySet() {
        regs.cc.or(CC_C);
        assertFalse(BranchInstruction.branchOnCarryClear(bundle));
    }

    @Test
    public void testBranchOnCarryClearCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x241F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnCarryClearDoesNotBranchIfCarrySet() throws MalformedInstructionException {
        regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x241F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnCarrySetReturnsFalseIfCarryClear() {
        assertFalse(BranchInstruction.branchOnCarrySet(bundle));
    }

    @Test
    public void testBranchOnCarrySetReturnsTrueIfCarrySet() {
        regs.cc.or(CC_C);
        assertTrue(BranchInstruction.branchOnCarrySet(bundle));
    }

    @Test
    public void testBranchOnCarrySetCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x251F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnCarrySetDoesNotBranchIfCarryClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x251F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnNotEqualReturnsTrueIfZeroClear() {
        assertTrue(BranchInstruction.branchOnNotEqual(bundle));
    }

    @Test
    public void testBranchOnNotEqualReturnsFalseIfZeroSet() {
        regs.cc.or(CC_Z);
        assertFalse(BranchInstruction.branchOnNotEqual(bundle));
    }

    @Test
    public void testBranchOnNotEqualCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x261F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnNotEqualDoesNotBranchIfZeroSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x261F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnEqualReturnsFalseIfZeroClear() {
        assertFalse(BranchInstruction.branchOnEqual(bundle));
    }

    @Test
    public void testBranchOnEqualReturnsTrueIfZeroSet() {
        regs.cc.or(CC_Z);
        assertTrue(BranchInstruction.branchOnEqual(bundle));
    }

    @Test
    public void testBranchOnEqualCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x271F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnEqualDoesNotBranchIfZeroClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x271F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnOverflowClearReturnsTrueIfOverflowClear() {
        assertTrue(BranchInstruction.branchOnOverflowClear(bundle));
    }

    @Test
    public void testBranchOnOverflowClearReturnsFalseIfOverflowSet() {
        regs.cc.or(CC_V);
        assertFalse(BranchInstruction.branchOnOverflowClear(bundle));
    }

    @Test
    public void testBranchOnOverflowClearCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x281F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnOverflowClearDoesNotBranchIfOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x281F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnOverflowSetReturnsFalseIfOverflowClear() {
        assertFalse(BranchInstruction.branchOnOverflowSet(bundle));
    }

    @Test
    public void testBranchOnOverflowSetReturnsTrueIfOverflowSet() {
        regs.cc.or(CC_V);
        assertTrue(BranchInstruction.branchOnOverflowSet(bundle));
    }

    @Test
    public void testBranchOnOverflowSetCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x291F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnOverflowSetDoesNotBranchIfOverflowClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x291F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnPlusReturnsTrueIfNegativeClear() {
        assertTrue(BranchInstruction.branchOnPlus(bundle));
    }

    @Test
    public void testBranchOnPlusReturnsFalseIfNegativeSet() {
        regs.cc.or(CC_N);
        assertFalse(BranchInstruction.branchOnPlus(bundle));
    }

    @Test
    public void testBranchOnPlusCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x2A1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnPlusDoesNotBranchIfNegativeSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2A1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnMinusReturnsFalseIfNegativeClear() {
        assertFalse(BranchInstruction.branchOnMinus(bundle));
    }

    @Test
    public void testBranchOnMinusReturnsTrueIfNegativeSet() {
        regs.cc.or(CC_N);
        assertTrue(BranchInstruction.branchOnMinus(bundle));
    }

    @Test
    public void testBranchOnMinusCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2B1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnMinusDoesNotBranchIfNegativeClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x2B1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroReturnsTrueIfNegativeOverflowSet() {
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        assertTrue(BranchInstruction.branchOnGreaterThanEqualZero(bundle));
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroReturnsTrueIfNegativeOverflowClear() {
        assertTrue(BranchInstruction.branchOnGreaterThanEqualZero(bundle));
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroReturnsFalseIfNegativeSetOverflowClear() {
        regs.cc.or(CC_N);
        assertFalse(BranchInstruction.branchOnGreaterThanEqualZero(bundle));
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroReturnsFalseIfNegativeClearOverflowSet() {
        regs.cc.or(CC_V);
        assertFalse(BranchInstruction.branchOnGreaterThanEqualZero(bundle));
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x2C1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroCalledIfNegativeSetOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2C1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroDoesNotBranchIfNegativeSetOverflowClear() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2C1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroDoesNotBranchIfOverflowSetNegativeClear() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2C1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLessThanZeroReturnsFalseWhenNegativeOverflowClear() {
        assertFalse(BranchInstruction.branchOnLessThanZero(bundle));
    }

    @Test
    public void testBranchOnLessThanZeroReturnsFalseWhenNegativeOverflowSet() {
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        assertFalse(BranchInstruction.branchOnLessThanZero(bundle));
    }

    @Test
    public void testBranchOnLessThanZeroReturnsTrueWhenNegativeSetOverflowClear() {
        regs.cc.or(CC_N);
        assertTrue(BranchInstruction.branchOnLessThanZero(bundle));
    }

    @Test
    public void testBranchOnLessThanZeroReturnsTrueWhenNegativeClearOverflowSet() {
        regs.cc.or(CC_V);
        assertTrue(BranchInstruction.branchOnLessThanZero(bundle));
    }

    @Test
    public void testBranchOnLessThanZeroNotCalledIfNegativeClearOverflowClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x2D1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLessThanZeroNotCalledIfNegativeSetOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2D1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfNegativeSetOverflowClear() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2D1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfNegativeClearOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2D1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsFalseIfZeroSet() {
        regs.cc.or(CC_Z);
        assertFalse(BranchInstruction.branchOnGreaterThanZero(bundle));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsFalseIfZeroOverflowNegativeSet() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        assertFalse(BranchInstruction.branchOnGreaterThanZero(bundle));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsFalseIfZeroOverflowSetNegativeClear() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        assertFalse(BranchInstruction.branchOnGreaterThanZero(bundle));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsFalseIfZeroNegativeSetOverflowClear() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        assertFalse(BranchInstruction.branchOnGreaterThanZero(bundle));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsTrueIfZeroNegativeOverflowClear() {
        assertTrue(BranchInstruction.branchOnGreaterThanZero(bundle));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsTrueIfZeroClearNegativeOverflowSet() {
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        assertTrue(BranchInstruction.branchOnGreaterThanZero(bundle));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsFalseIfZeroNegativeClearOverflowSet() {
        regs.cc.or(CC_V);
        assertFalse(BranchInstruction.branchOnGreaterThanZero(bundle));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsFalseIfZeroOverflowClearNegativeSet() {
        regs.cc.or(CC_N);
        assertFalse(BranchInstruction.branchOnGreaterThanZero(bundle));
    }

    @Test
    public void testBranchOnGreaterThanZeroNotCalledIfZeroSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnGreaterThanZeroNotCalledIfZeroNegativeOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnGreaterThanZeroNotCalledIfZeroNegativeSetOverflowClear() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnGreaterThanZeroNotCalledIfZeroOverflowSetNegativeClear() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnGreaterThanZeroNotCalledIfZeroNegativeClearOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnGreaterThanZeroNotCalledIfZeroOverflowClearNegativeSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnGreaterThanZeroCalledIfZeroOverflowNegativeClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnGreaterThanZeroCalledIfZeroClearOverflowNegativeSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsTrueIfZeroSetNegativeOverflowClear() {
        regs.cc.or(CC_Z);
        assertTrue(BranchInstruction.branchOnLessThanEqualZero(bundle));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsTrueIfZeroSetNegativeOverflowSet() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        assertTrue(BranchInstruction.branchOnLessThanEqualZero(bundle));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsTrueIfZeroSetNegativeSetOverflowClear() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        assertTrue(BranchInstruction.branchOnLessThanEqualZero(bundle));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsTrueIfZeroSetNegativeClearOverflowSet() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        assertTrue(BranchInstruction.branchOnLessThanEqualZero(bundle));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsFalseIfZeroClearNegativeOverflowClear() {
        assertFalse(BranchInstruction.branchOnLessThanEqualZero(bundle));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsFalseIfZeroClearNegativeOverflowSet() {
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        assertFalse(BranchInstruction.branchOnLessThanEqualZero(bundle));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsTrueIfZeroClearNegativeClearOverflowSet() {
        regs.cc.or(CC_V);
        assertTrue(BranchInstruction.branchOnLessThanEqualZero(bundle));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsTrueIfZeroClearNegativeSetOverflowClear() {
        regs.cc.or(CC_N);
        assertTrue(BranchInstruction.branchOnLessThanEqualZero(bundle));
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfZeroSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfZeroNegativeOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfZeroNegativeSetOverflowClear() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfZeroOverflowSetNegativeClear() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfZeroNegativeClearOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfZeroOverflowClearNegativeSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLessThanZeroNotCalledIfZeroNegativeOverflowClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testBranchOnLessThanZeroNotCalledIfZeroClearNegativeOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.getInt());
    }

}
