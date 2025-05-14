/*
 * Copyright (C) 2023-2025 Craig Thomas
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
    private CPU cpu;
    private RegisterSet regs;
    private Memory memory;

    @Before
    public void setUp() {
        Screen screen = new Screen(1);
        Cassette cassette = new Cassette();
        memory = new Memory();
        regs = new RegisterSet();
        io = new IOController(memory, regs, new EmulatedKeyboard(), screen, cassette, null);
        cpu = new CPU(io);
    }

    @Test
    public void testBranchAlwaysReturnsTrue() {
        assertTrue(BranchInstruction.branchAlways(null));
    }

    @Test
    public void testBranchAlways() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x201F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchAlwaysNegativeOffset() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x20FE);
        cpu.executeInstruction();
        assertEquals(0x0000, regs.pc.get());
    }

    @Test
    public void testBranchAlwaysNegativeOffsetLoopsAround() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x20FD);
        cpu.executeInstruction();
        assertEquals(0xFFFF, regs.pc.get());
    }

    @Test
    public void testBranchAlwaysSimpleCaseWorksCorrectly() throws MalformedInstructionException {
        regs.pc.set(0x0009);
        io.writeWord(0x0009, 0x207F);
        cpu.executeInstruction();
        assertEquals(0x008A, regs.pc.get());
    }

    @Test
    public void testBranchAlwaysSimpleNegativeNumber() throws MalformedInstructionException {
        regs.pc.set(0x0056);
        io.writeWord(0x0056, 0x20AA);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchNeverReturnsFalse() {
        assertFalse(BranchInstruction.branchNever(null));
    }

    @Test
    public void testBranchNever() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x21FF);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchToSubroutineSavesPCAndReturnsTrue() {
        regs.s.set(0x0200);
        regs.pc.set(0xBEEF);
        assertTrue(BranchInstruction.branchToSubroutine(io));
        assertEquals(0xEF, io.readByte(new UnsignedWord(0x01FF)).get());
        assertEquals(0xBE, io.readByte(new UnsignedWord(0x01FE)).get());
    }

    @Test
    public void testBranchSubroutine() throws MalformedInstructionException {
        io.regs.s.set(0x3000);
        regs.pc.set(0x1021);
        io.writeWord(0x1021, 0x8D1F);
        cpu.executeInstruction();
        assertEquals(0x1042, regs.pc.get());
        assertEquals(0x23, memory.readByte(0x2FFF).get());
        assertEquals(0x10, memory.readByte(0x2FFE).get());
    }

    @Test
    public void testBranchSubroutineNegativeOffsetCorrect() throws MalformedInstructionException {
        io.regs.s.set(0x3000);
        regs.pc.set(0x1021);
        io.writeWord(0x1021, 0x8DDD);
        cpu.executeInstruction();
        assertEquals(0x1000, regs.pc.get());
        assertEquals(0x23, memory.readByte(0x2FFF).get());
        assertEquals(0x10, memory.readByte(0x2FFE).get());
    }

    @Test
    public void testBranchOnHighReturnsTrueIfBothCarryAndZeroNotSet() {
        assertTrue(BranchInstruction.branchOnHigh(io));
    }

    @Test
    public void testBranchOnHighReturnsFalseIfCarrySet() {
        regs.cc.or(CC_C);
        assertFalse(BranchInstruction.branchOnHigh(io));
    }

    @Test
    public void testBranchOnHighReturnsFalseIfZeroSet() {
        regs.cc.or(CC_Z);
        assertFalse(BranchInstruction.branchOnHigh(io));
    }

    @Test
    public void testBranchOnHighCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x221F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnHighNotCalledWhenCarrySet() throws MalformedInstructionException {
        io.regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x221F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnHighNotCalledWhenZeroSet() throws MalformedInstructionException {
        io.regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x221F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnLowerReturnsTrueIfCarrySet() {
        regs.cc.or(CC_C);
        assertTrue(BranchInstruction.branchOnLower(io));
    }

    @Test
    public void testBranchOnLowerReturnsTrueIfZeroSet() {
        regs.cc.or(CC_Z);
        assertTrue(BranchInstruction.branchOnLower(io));
    }

    @Test
    public void testBranchOnLowerReturnsTrueIfZeroAndCarrySet() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_C);
        assertTrue(BranchInstruction.branchOnLower(io));
    }

    @Test
    public void testBranchOnLowerReturnsFalseIfCarryAndZeroClear() {
        assertFalse(BranchInstruction.branchOnLower(io));
    }

    @Test
    public void testBranchOnLowerNotCalledWhenZeroCarryClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x231F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnLowerCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x231F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnLowerCalledWhenCarryAndZeroSet() throws MalformedInstructionException {
        io.regs.cc.or(CC_Z);
        io.regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x231F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnLowerCalledCorrectWithZeroOnly() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x231F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnLowerCalledCorrectWithCarryOnly() throws MalformedInstructionException {
        regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x231F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnCarryClearReturnsTrueIfCarryClear() {
        assertTrue(BranchInstruction.branchOnCarryClear(io));
    }

    @Test
    public void testBranchOnCarryClearReturnsFalseIfCarrySet() {
        regs.cc.or(CC_C);
        assertFalse(BranchInstruction.branchOnCarryClear(io));
    }

    @Test
    public void testBranchOnCarryClearCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x241F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnCarryClearDoesNotBranchIfCarrySet() throws MalformedInstructionException {
        regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x241F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnCarrySetReturnsFalseIfCarryClear() {
        assertFalse(BranchInstruction.branchOnCarrySet(io));
    }

    @Test
    public void testBranchOnCarrySetReturnsTrueIfCarrySet() {
        regs.cc.or(CC_C);
        assertTrue(BranchInstruction.branchOnCarrySet(io));
    }

    @Test
    public void testBranchOnCarrySetCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x251F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnCarrySetDoesNotBranchIfCarryClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x251F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnNotEqualReturnsTrueIfZeroClear() {
        assertTrue(BranchInstruction.branchOnNotEqual(io));
    }

    @Test
    public void testBranchOnNotEqualReturnsFalseIfZeroSet() {
        regs.cc.or(CC_Z);
        assertFalse(BranchInstruction.branchOnNotEqual(io));
    }

    @Test
    public void testBranchOnNotEqualCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x261F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnNotEqualDoesNotBranchIfZeroSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x261F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnEqualReturnsFalseIfZeroClear() {
        assertFalse(BranchInstruction.branchOnEqual(io));
    }

    @Test
    public void testBranchOnEqualReturnsTrueIfZeroSet() {
        regs.cc.or(CC_Z);
        assertTrue(BranchInstruction.branchOnEqual(io));
    }

    @Test
    public void testBranchOnEqualCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x271F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnEqualDoesNotBranchIfZeroClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x271F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnOverflowClearReturnsTrueIfOverflowClear() {
        assertTrue(BranchInstruction.branchOnOverflowClear(io));
    }

    @Test
    public void testBranchOnOverflowClearReturnsFalseIfOverflowSet() {
        regs.cc.or(CC_V);
        assertFalse(BranchInstruction.branchOnOverflowClear(io));
    }

    @Test
    public void testBranchOnOverflowClearCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x281F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnOverflowClearDoesNotBranchIfOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x281F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnOverflowSetReturnsFalseIfOverflowClear() {
        assertFalse(BranchInstruction.branchOnOverflowSet(io));
    }

    @Test
    public void testBranchOnOverflowSetReturnsTrueIfOverflowSet() {
        regs.cc.or(CC_V);
        assertTrue(BranchInstruction.branchOnOverflowSet(io));
    }

    @Test
    public void testBranchOnOverflowSetCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x291F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnOverflowSetDoesNotBranchIfOverflowClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x291F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnPlusReturnsTrueIfNegativeClear() {
        assertTrue(BranchInstruction.branchOnPlus(io));
    }

    @Test
    public void testBranchOnPlusReturnsFalseIfNegativeSet() {
        regs.cc.or(CC_N);
        assertFalse(BranchInstruction.branchOnPlus(io));
    }

    @Test
    public void testBranchOnPlusCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x2A1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnPlusDoesNotBranchIfNegativeSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2A1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnMinusReturnsFalseIfNegativeClear() {
        assertFalse(BranchInstruction.branchOnMinus(io));
    }

    @Test
    public void testBranchOnMinusReturnsTrueIfNegativeSet() {
        regs.cc.or(CC_N);
        assertTrue(BranchInstruction.branchOnMinus(io));
    }

    @Test
    public void testBranchOnMinusCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2B1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnMinusDoesNotBranchIfNegativeClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x2B1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroReturnsTrueIfNegativeOverflowSet() {
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        assertTrue(BranchInstruction.branchOnGreaterThanEqualZero(io));
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroReturnsTrueIfNegativeOverflowClear() {
        assertTrue(BranchInstruction.branchOnGreaterThanEqualZero(io));
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroReturnsFalseIfNegativeSetOverflowClear() {
        regs.cc.or(CC_N);
        assertFalse(BranchInstruction.branchOnGreaterThanEqualZero(io));
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroReturnsFalseIfNegativeClearOverflowSet() {
        regs.cc.or(CC_V);
        assertFalse(BranchInstruction.branchOnGreaterThanEqualZero(io));
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x2C1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroCalledIfNegativeSetOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2C1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroDoesNotBranchIfNegativeSetOverflowClear() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2C1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnGreaterThanEqualZeroDoesNotBranchIfOverflowSetNegativeClear() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2C1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnLessThanZeroReturnsFalseWhenNegativeOverflowClear() {
        assertFalse(BranchInstruction.branchOnLessThanZero(io));
    }

    @Test
    public void testBranchOnLessThanZeroReturnsFalseWhenNegativeOverflowSet() {
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        assertFalse(BranchInstruction.branchOnLessThanZero(io));
    }

    @Test
    public void testBranchOnLessThanZeroReturnsTrueWhenNegativeSetOverflowClear() {
        regs.cc.or(CC_N);
        assertTrue(BranchInstruction.branchOnLessThanZero(io));
    }

    @Test
    public void testBranchOnLessThanZeroReturnsTrueWhenNegativeClearOverflowSet() {
        regs.cc.or(CC_V);
        assertTrue(BranchInstruction.branchOnLessThanZero(io));
    }

    @Test
    public void testBranchOnLessThanZeroNotCalledIfNegativeClearOverflowClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x2D1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnLessThanZeroNotCalledIfNegativeSetOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2D1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfNegativeSetOverflowClear() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2D1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfNegativeClearOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2D1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsFalseIfZeroSet() {
        regs.cc.or(CC_Z);
        assertFalse(BranchInstruction.branchOnGreaterThanZero(io));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsFalseIfZeroOverflowNegativeSet() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        assertFalse(BranchInstruction.branchOnGreaterThanZero(io));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsFalseIfZeroOverflowSetNegativeClear() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        assertFalse(BranchInstruction.branchOnGreaterThanZero(io));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsFalseIfZeroNegativeSetOverflowClear() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        assertFalse(BranchInstruction.branchOnGreaterThanZero(io));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsTrueIfZeroNegativeOverflowClear() {
        assertTrue(BranchInstruction.branchOnGreaterThanZero(io));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsTrueIfZeroClearNegativeOverflowSet() {
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        assertTrue(BranchInstruction.branchOnGreaterThanZero(io));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsFalseIfZeroNegativeClearOverflowSet() {
        regs.cc.or(CC_V);
        assertFalse(BranchInstruction.branchOnGreaterThanZero(io));
    }

    @Test
    public void testBranchOnGreaterThanZeroReturnsFalseIfZeroOverflowClearNegativeSet() {
        regs.cc.or(CC_N);
        assertFalse(BranchInstruction.branchOnGreaterThanZero(io));
    }

    @Test
    public void testBranchOnGreaterThanZeroNotCalledIfZeroSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnGreaterThanZeroNotCalledIfZeroNegativeOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnGreaterThanZeroNotCalledIfZeroNegativeSetOverflowClear() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnGreaterThanZeroNotCalledIfZeroOverflowSetNegativeClear() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnGreaterThanZeroNotCalledIfZeroNegativeClearOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnGreaterThanZeroNotCalledIfZeroOverflowClearNegativeSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnGreaterThanZeroCalledIfZeroOverflowNegativeClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnGreaterThanZeroCalledIfZeroClearOverflowNegativeSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2E1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsTrueIfZeroSetNegativeOverflowClear() {
        regs.cc.or(CC_Z);
        assertTrue(BranchInstruction.branchOnLessThanEqualZero(io));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsTrueIfZeroSetNegativeOverflowSet() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        assertTrue(BranchInstruction.branchOnLessThanEqualZero(io));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsTrueIfZeroSetNegativeSetOverflowClear() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        assertTrue(BranchInstruction.branchOnLessThanEqualZero(io));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsTrueIfZeroSetNegativeClearOverflowSet() {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        assertTrue(BranchInstruction.branchOnLessThanEqualZero(io));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsFalseIfZeroClearNegativeOverflowClear() {
        assertFalse(BranchInstruction.branchOnLessThanEqualZero(io));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsFalseIfZeroClearNegativeOverflowSet() {
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        assertFalse(BranchInstruction.branchOnLessThanEqualZero(io));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsTrueIfZeroClearNegativeClearOverflowSet() {
        regs.cc.or(CC_V);
        assertTrue(BranchInstruction.branchOnLessThanEqualZero(io));
    }

    @Test
    public void testBranchOnLessThanEqualZeroReturnsTrueIfZeroClearNegativeSetOverflowClear() {
        regs.cc.or(CC_N);
        assertTrue(BranchInstruction.branchOnLessThanEqualZero(io));
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfZeroSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfZeroNegativeOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfZeroNegativeSetOverflowClear() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfZeroOverflowSetNegativeClear() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2F1F);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfZeroNegativeClearOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnLessThanZeroCalledIfZeroOverflowClearNegativeSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0021, regs.pc.get());
    }

    @Test
    public void testBranchOnLessThanZeroNotCalledIfZeroNegativeOverflowClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testBranchOnLessThanZeroNotCalledIfZeroClearNegativeOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x2F1F);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

}
