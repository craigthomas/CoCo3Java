/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import static org.junit.Assert.*;

import ca.craigthomas.yacoco3e.datatypes.Register;
import ca.craigthomas.yacoco3e.datatypes.RegisterSet;
import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import ca.craigthomas.yacoco3e.datatypes.UnsignedWord;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class CPUIntegrationTest
{
    private CPU cpu;
    private CPU cpuSpy;

    private Memory memory;
    private Memory memorySpy;

    private RegisterSet registerSet;
    private RegisterSet registerSetSpy;

    private UnsignedByte expectedDirectByte;

    @Before
    public void setUp() {
        memory = new Memory();
        memorySpy = spy(memory);

        registerSet = new RegisterSet();
        registerSetSpy = spy(registerSet);

        cpu = new CPU(registerSetSpy, memorySpy);
        cpuSpy = spy(cpu);

        registerSetSpy.setDP(new UnsignedByte(0xA0));
        expectedDirectByte = new UnsignedByte(0xBA);
        memorySpy.writeByte(new UnsignedWord(0xA000), new UnsignedByte(0xBA));
    }

    @Test
    public void testNegateDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).negate(expectedDirectByte);
    }

    @Test
    public void testNegateIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x60));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).negate(new UnsignedByte(0));
    }

    @Test
    public void testNegateExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x70));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).negate(new UnsignedByte(0));
    }

    @Test
    public void testComplementDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x03));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).compliment(expectedDirectByte);
    }

    @Test
    public void testComplementIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x63));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).compliment(new UnsignedByte(0));
    }

    @Test
    public void testComplementExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x73));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).compliment(new UnsignedByte(0));
    }

    @Test
    public void testLogicalShiftRightDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x04));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).logicalShiftRight(expectedDirectByte);
    }

    @Test
    public void testLogicalShiftRightIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x64));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).logicalShiftRight(new UnsignedByte(0));
    }

    @Test
    public void testLogicalShiftRightExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x74));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).logicalShiftRight(new UnsignedByte(0));
    }

    @Test
    public void testRotateRightDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x06));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).rotateRight(expectedDirectByte);
    }

    @Test
    public void testRotateRightIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x66));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).rotateRight(new UnsignedByte(0));
    }

    @Test
    public void testRotateRightExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x76));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).rotateRight(new UnsignedByte(0));
    }

    @Test
    public void testArithmeticShiftRightDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x07));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).arithmeticShiftRight(expectedDirectByte);
    }

    @Test
    public void testArithmeticShiftIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x67));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).arithmeticShiftRight(new UnsignedByte(0));
    }

    @Test
    public void testArithmeticShiftRightExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x77));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).arithmeticShiftRight(new UnsignedByte(0));
    }

    @Test
    public void testArithmeticShiftLeftDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x08));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).arithmeticShiftLeft(expectedDirectByte);
    }

    @Test
    public void testArithmeticShiftLeftIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x68));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).arithmeticShiftLeft(new UnsignedByte(0));
    }

    @Test
    public void testArithmeticShiftLeftExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x78));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).arithmeticShiftLeft(new UnsignedByte(0));
    }

    @Test
    public void testRotateLeftDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x09));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).rotateLeft(expectedDirectByte);
    }

    @Test
    public void testRotateLeftIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x69));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).rotateLeft(new UnsignedByte(0));
    }

    @Test
    public void testRotateLeftExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x79));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).rotateLeft(new UnsignedByte(0));
    }

    @Test
    public void testDecrementDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0A));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).decrement(expectedDirectByte);
    }

    @Test
    public void testDecrementIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6A));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).decrement(new UnsignedByte(0));
    }

    @Test
    public void testDecrementExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7A));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).decrement(new UnsignedByte(0));
    }

    @Test
    public void testIncrementDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0C));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).increment(expectedDirectByte);
    }

    @Test
    public void testIncrementIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6C));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).increment(new UnsignedByte(0));
    }

    @Test
    public void testIncrementExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7C));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).increment(new UnsignedByte(0));
    }

    @Test
    public void testTestDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0D));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).test(expectedDirectByte);
    }

    @Test
    public void testTestIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6D));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).test(new UnsignedByte(0));
    }

    @Test
    public void testTestExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7D));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).test(new UnsignedByte(0));
    }

    @Test
    public void testJumpDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0E));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).jump(new UnsignedWord(0xA000));
    }

    @Test
    public void testJumpIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6E));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).jump(new UnsignedWord(0x2));
    }

    @Test
    public void testJumpExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7E));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).jump(new UnsignedWord(0x7E00));
    }

    @Test
    public void testClearDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0F));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).clear(expectedDirectByte);
    }

    @Test
    public void testClearIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6F));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).clear(new UnsignedByte(0x0));
    }

    @Test
    public void testClearExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7F));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).clear(new UnsignedByte(0));
    }

    @Test
    public void testLongBranchAlways() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x16));
        memorySpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchNeverDoesNothing() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1021));
        cpuSpy.executeInstruction();
        assertEquals(0x2, registerSet.getPC().getInt());
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0x0000));
    }

    @Test
    public void testLongBranchOnHighCalledCorrect() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1022));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnHighNotCalledWhenCarrySet() {
        registerSetSpy.setCCCarry();
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x10));
        memorySpy.writeByte(new UnsignedWord(0x1), new UnsignedByte(0x22));
        memorySpy.writeByte(new UnsignedWord(0x2), new UnsignedByte(0xBE));
        memorySpy.writeByte(new UnsignedWord(0x3), new UnsignedByte(0xEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnHighNotCalledWhenZeroSet() {
        registerSetSpy.setCCZero();
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x10));
        memorySpy.writeByte(new UnsignedWord(0x1), new UnsignedByte(0x22));
        memorySpy.writeByte(new UnsignedWord(0x2), new UnsignedByte(0xBE));
        memorySpy.writeByte(new UnsignedWord(0x3), new UnsignedByte(0xEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLowerCalledCorrect() {
        registerSetSpy.setCCZero();
        registerSetSpy.setCCCarry();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1023));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnHighNotCalledWhenCarryAndZeroNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1023));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnCarryClearCalledCorrect() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1024));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnCarryClearNotCalledWhenCarrySet() {
        registerSetSpy.setCCCarry();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1024));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnCarrySetCalledCorrect() {
        registerSetSpy.setCCCarry();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1025));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnCarrySetNotCalledWhenCarryNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1025));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnNotEqualCalledCorrect() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1026));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnNotEqualNotCalledWhenZeroSet() {
        registerSetSpy.setCCZero();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1026));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnEqualCalledCorrect() {
        registerSetSpy.setCCZero();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1027));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnEqualNotCalledWhenZeroNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1027));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnOverflowClearCalledCorrect() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1028));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnOverflowClearNotCalledWhenOverflowSet() {
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1028));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnOverflowSetCalledCorrect() {
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1029));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnOverflowSetNotCalledWhenOverflowNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1029));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnPlusCalledCorrect() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102A));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnPlusNotCalledWhenNegativeSet() {
        registerSetSpy.setCCNegative();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102A));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnMinusCalledCorrect() {
        registerSetSpy.setCCNegative();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102B));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnMinusNotCalledWhenNegativeNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102B));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLTCalledCorrectWhenOverflowSet() {
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102D));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLTCalledCorrectWhenNegativeSet() {
        registerSetSpy.setCCNegative();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102D));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }


    @Test
    public void testLongBranchOnLTNotCalledWhenNegativeAndOverflowNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102D));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLTNotCalledWhenNegativeAndOverflowSet() {
        registerSetSpy.setCCNegative();
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102D));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGECalledCorrectWhenOverflowSet() {
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102C));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGECalledCorrectWhenNegativeSet() {
        registerSetSpy.setCCNegative();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102C));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }


    @Test
    public void testLongBranchOnGENotCalledWhenNegativeAndOverflowNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102C));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGENotCalledWhenNegativeAndOverflowSet() {
        registerSetSpy.setCCNegative();
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102C));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGTCalledCorrectly() {
        registerSetSpy.setCCZero();
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102E));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGTNotCalledIfNotZeroSet() {
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102E));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGTNotCalledIfAllSet() {
        registerSetSpy.setCCZero();
        registerSetSpy.setCCOverflow();
        registerSetSpy.setCCNegative();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102E));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLECalledCorrectly() {
        registerSetSpy.setCCZero();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102F));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLECalledIfNotZeroAndOverflow() {
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102F));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLECalledIfNotZeroAndNegative() {
        registerSetSpy.setCCNegative();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102F));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLENotCalledIfOverflowAndNegative() {
        registerSetSpy.setCCNegative();
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102F));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testSWI3PushesAllValues() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x103F));
        memorySpy.writeByte(CPU.SWI3, new UnsignedByte(0x56));
        memorySpy.writeByte(CPU.SWI3.next(), new UnsignedByte(0x78));
        registerSetSpy.setCCNegative();
        registerSetSpy.setCCOverflow();
        registerSetSpy.setS(new UnsignedWord(0xA000));
        registerSetSpy.setU(new UnsignedWord(0xDEAD));
        registerSetSpy.setY(new UnsignedWord(0xBEEF));
        registerSetSpy.setX(new UnsignedWord(0xCAFE));
        registerSetSpy.setDP(new UnsignedByte(0xAB));
        registerSetSpy.setB(new UnsignedByte(0xCD));
        registerSetSpy.setA(new UnsignedByte(0xEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).softwareInterrupt(CPU.SWI3);
        assertEquals(new UnsignedByte(0x02), memorySpy.readByte(new UnsignedWord(0x9FFF)));
        assertEquals(new UnsignedByte(0x00), memorySpy.readByte(new UnsignedWord(0x9FFE)));
        assertEquals(new UnsignedByte(0xAD), memorySpy.readByte(new UnsignedWord(0x9FFD)));
        assertEquals(new UnsignedByte(0xDE), memorySpy.readByte(new UnsignedWord(0x9FFC)));
        assertEquals(new UnsignedByte(0xEF), memorySpy.readByte(new UnsignedWord(0x9FFB)));
        assertEquals(new UnsignedByte(0xBE), memorySpy.readByte(new UnsignedWord(0x9FFA)));
        assertEquals(new UnsignedByte(0xFE), memorySpy.readByte(new UnsignedWord(0x9FF9)));
        assertEquals(new UnsignedByte(0xCA), memorySpy.readByte(new UnsignedWord(0x9FF8)));
        assertEquals(new UnsignedByte(0xAB), memorySpy.readByte(new UnsignedWord(0x9FF7)));
        assertEquals(new UnsignedByte(0xCD), memorySpy.readByte(new UnsignedWord(0x9FF6)));
        assertEquals(new UnsignedByte(0xEF), memorySpy.readByte(new UnsignedWord(0x9FF5)));
        assertEquals(new UnsignedByte(0x8A), memorySpy.readByte(new UnsignedWord(0x9FF4)));
    }

    @Test
    public void testSWI2PushesAllValues() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x113F));
        memorySpy.writeByte(CPU.SWI3, new UnsignedByte(0x56));
        memorySpy.writeByte(CPU.SWI3.next(), new UnsignedByte(0x78));
        registerSetSpy.setCCNegative();
        registerSetSpy.setCCOverflow();
        registerSetSpy.setS(new UnsignedWord(0xA000));
        registerSetSpy.setU(new UnsignedWord(0xDEAD));
        registerSetSpy.setY(new UnsignedWord(0xBEEF));
        registerSetSpy.setX(new UnsignedWord(0xCAFE));
        registerSetSpy.setDP(new UnsignedByte(0xAB));
        registerSetSpy.setB(new UnsignedByte(0xCD));
        registerSetSpy.setA(new UnsignedByte(0xEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).softwareInterrupt(CPU.SWI2);
        assertEquals(new UnsignedByte(0x02), memorySpy.readByte(new UnsignedWord(0x9FFF)));
        assertEquals(new UnsignedByte(0x00), memorySpy.readByte(new UnsignedWord(0x9FFE)));
        assertEquals(new UnsignedByte(0xAD), memorySpy.readByte(new UnsignedWord(0x9FFD)));
        assertEquals(new UnsignedByte(0xDE), memorySpy.readByte(new UnsignedWord(0x9FFC)));
        assertEquals(new UnsignedByte(0xEF), memorySpy.readByte(new UnsignedWord(0x9FFB)));
        assertEquals(new UnsignedByte(0xBE), memorySpy.readByte(new UnsignedWord(0x9FFA)));
        assertEquals(new UnsignedByte(0xFE), memorySpy.readByte(new UnsignedWord(0x9FF9)));
        assertEquals(new UnsignedByte(0xCA), memorySpy.readByte(new UnsignedWord(0x9FF8)));
        assertEquals(new UnsignedByte(0xAB), memorySpy.readByte(new UnsignedWord(0x9FF7)));
        assertEquals(new UnsignedByte(0xCD), memorySpy.readByte(new UnsignedWord(0x9FF6)));
        assertEquals(new UnsignedByte(0xEF), memorySpy.readByte(new UnsignedWord(0x9FF5)));
        assertEquals(new UnsignedByte(0x8A), memorySpy.readByte(new UnsignedWord(0x9FF4)));
    }

    @Test
    public void testSWIPushesAllValues() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3F));
        memorySpy.writeByte(CPU.SWI, new UnsignedByte(0x56));
        memorySpy.writeByte(CPU.SWI.next(), new UnsignedByte(0x78));
        registerSetSpy.setCCNegative();
        registerSetSpy.setCCOverflow();
        registerSetSpy.setS(new UnsignedWord(0xA000));
        registerSetSpy.setU(new UnsignedWord(0xDEAD));
        registerSetSpy.setY(new UnsignedWord(0xBEEF));
        registerSetSpy.setX(new UnsignedWord(0xCAFE));
        registerSetSpy.setDP(new UnsignedByte(0xAB));
        registerSetSpy.setB(new UnsignedByte(0xCD));
        registerSetSpy.setA(new UnsignedByte(0xEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).softwareInterrupt(CPU.SWI);
        assertEquals(new UnsignedByte(0x01), memorySpy.readByte(new UnsignedWord(0x9FFF)));
        assertEquals(new UnsignedByte(0x00), memorySpy.readByte(new UnsignedWord(0x9FFE)));
        assertEquals(new UnsignedByte(0xAD), memorySpy.readByte(new UnsignedWord(0x9FFD)));
        assertEquals(new UnsignedByte(0xDE), memorySpy.readByte(new UnsignedWord(0x9FFC)));
        assertEquals(new UnsignedByte(0xEF), memorySpy.readByte(new UnsignedWord(0x9FFB)));
        assertEquals(new UnsignedByte(0xBE), memorySpy.readByte(new UnsignedWord(0x9FFA)));
        assertEquals(new UnsignedByte(0xFE), memorySpy.readByte(new UnsignedWord(0x9FF9)));
        assertEquals(new UnsignedByte(0xCA), memorySpy.readByte(new UnsignedWord(0x9FF8)));
        assertEquals(new UnsignedByte(0xAB), memorySpy.readByte(new UnsignedWord(0x9FF7)));
        assertEquals(new UnsignedByte(0xCD), memorySpy.readByte(new UnsignedWord(0x9FF6)));
        assertEquals(new UnsignedByte(0xEF), memorySpy.readByte(new UnsignedWord(0x9FF5)));
        assertEquals(new UnsignedByte(0x8A), memorySpy.readByte(new UnsignedWord(0x9FF4)));
    }

    @Test
    public void testCompareDImmediateCalled() {
        registerSetSpy.setD(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1083));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareDDirectCalled() {
        registerSetSpy.setD(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1093));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareDIndexedCalled() {
        registerSetSpy.setD(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10A3));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareDExtendedCalled() {
        registerSetSpy.setD(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10B3));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareUImmediateCalled() {
        registerSetSpy.setU(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1183));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareUDirectCalled() {
        registerSetSpy.setU(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1193));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareUIndexedCalled() {
        registerSetSpy.setU(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x11A3));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareUExtendedCalled() {
        registerSetSpy.setU(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x11B3));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareYImmediateCalled() {
        registerSetSpy.setY(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x108C));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareYDirectCalled() {
        registerSetSpy.setY(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x109C));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareYIndexedCalled() {
        registerSetSpy.setY(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10AC));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareYExtendedCalled() {
        registerSetSpy.setY(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10BC));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareSImmediateCalled() {
        registerSetSpy.setS(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x118C));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareSDirectCalled() {
        registerSetSpy.setS(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x119C));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareSIndexedCalled() {
        registerSetSpy.setS(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x11AC));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareSExtendedCalled() {
        registerSetSpy.setS(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x11BC));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareXImmediateCalled() {
        registerSetSpy.setX(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x8C00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareXDirectCalled() {
        registerSetSpy.setX(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9C00));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareXIndexedCalled() {
        registerSetSpy.setX(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAC00));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareXExtendedCalled() {
        registerSetSpy.setX(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xBC00));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testLoadYImmediateCalled() {
        registerSetSpy.setY(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x108E));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.Y, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadYDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x109E));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).loadRegister(Register.Y, new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testLoadYIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10AE));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).loadRegister(Register.Y, new UnsignedWord());
    }

    @Test
    public void testLoadYExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10BE));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).loadRegister(Register.Y, new UnsignedWord());
    }

    @Test
    public void testLoadSImmediateCalled() {
        registerSetSpy.setY(new UnsignedWord(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10CE));
        memorySpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.S, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadSDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10DE));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).loadRegister(Register.S, new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testLoadSIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10EE));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).loadRegister(Register.S, new UnsignedWord());
    }

    @Test
    public void testLoadSExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10FE));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).loadRegister(Register.S, new UnsignedWord());
    }

    @Test
    public void testLoadXImmediateCalled() {
        registerSetSpy.setX(new UnsignedWord(0x10));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x8E));
        memorySpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.X, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadXDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9E00));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).loadRegister(Register.X, new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testLoadXIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAE00));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).loadRegister(Register.X, new UnsignedWord(0x0002));
    }

    @Test
    public void testLoadXExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xBE00));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).loadRegister(Register.X, new UnsignedWord(0xBE00));
    }

    @Test
    public void testStoreYDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x109F));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).storeWordRegister(Register.Y, new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testStoreYIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10AF));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).storeWordRegister(Register.Y, new UnsignedWord());
    }

    @Test
    public void testStoreYExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10BF));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).storeWordRegister(Register.Y, new UnsignedWord());
    }

    @Test
    public void testStoreXDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9F00));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).storeWordRegister(Register.X, new UnsignedWord(0xA000));
    }

    @Test
    public void testStoreXIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAF00));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).storeWordRegister(Register.X, new UnsignedWord(0x0002));
    }

    @Test
    public void testStoreXExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xBF00));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).storeWordRegister(Register.X, new UnsignedWord(0xBF00));
    }

    @Test
    public void testStoreSDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10DF));
        cpuSpy.executeInstruction();
        verify(memorySpy).getDirect(registerSetSpy);
        verify(cpuSpy).storeWordRegister(Register.S, new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testStoreSIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10EF));
        cpuSpy.executeInstruction();
        verify(memorySpy).getIndexed(registerSetSpy);
        verify(cpuSpy).storeWordRegister(Register.S, new UnsignedWord());
    }

    @Test
    public void testStoreSExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10FF));
        cpuSpy.executeInstruction();
        verify(memorySpy).getExtended(registerSetSpy);
        verify(cpuSpy).storeWordRegister(Register.S, new UnsignedWord());
    }

    @Test
    public void testDecimalAdditionAdjustCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x19));
        cpuSpy.executeInstruction();
        verify(cpuSpy).decimalAdditionAdjust();
    }

    @Test
    public void testORConditionCodeWorksCorrectly() {
        registerSetSpy.setCC(new UnsignedByte(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1A01));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0x11), registerSetSpy.getCC());
    }

    @Test
    public void testANDConditionCodeWorksCorrectly() {
        registerSetSpy.setCC(new UnsignedByte(0x10));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1C11));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0x10), registerSetSpy.getCC());
    }

    @Test
    public void testSignExtendWorksCorrectly() {
        registerSetSpy.setB(new UnsignedByte(0x81));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x1D));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xFF), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0x81), registerSetSpy.getB());
    }

    @Test
    public void testSignExtendWorksCorrectlyWithNoExtension() {
        registerSetSpy.setB(new UnsignedByte(0x7F));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x1D));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0x00), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0x7F), registerSetSpy.getB());
    }

    @Test
    public void testExchangeDandX() {
        registerSetSpy.setD(new UnsignedWord(0xDEAD));
        registerSetSpy.setX(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E01));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testExchangeDandY() {
        registerSetSpy.setD(new UnsignedWord(0xDEAD));
        registerSetSpy.setY(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E02));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testExchangeDandU() {
        registerSetSpy.setD(new UnsignedWord(0xDEAD));
        registerSetSpy.setU(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E03));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testExchangeDandS() {
        registerSetSpy.setD(new UnsignedWord(0xDEAD));
        registerSetSpy.setS(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E04));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testExchangeDandPC() {
        registerSetSpy.setD(new UnsignedWord(0xDEAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E05));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getD());
    }

    @Test
    public void testExchangeXandY() {
        registerSetSpy.setX(new UnsignedWord(0xDEAD));
        registerSetSpy.setY(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E12));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testExchangeXandU() {
        registerSetSpy.setX(new UnsignedWord(0xDEAD));
        registerSetSpy.setU(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E13));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testExchangeXandS() {
        registerSetSpy.setX(new UnsignedWord(0xDEAD));
        registerSetSpy.setS(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E14));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testExchangeXandPC() {
        registerSetSpy.setX(new UnsignedWord(0xDEAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E15));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testExchangeYandU() {
        registerSetSpy.setY(new UnsignedWord(0xDEAD));
        registerSetSpy.setU(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E23));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testExchangeYandS() {
        registerSetSpy.setY(new UnsignedWord(0xDEAD));
        registerSetSpy.setS(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E24));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testExchangeYandPC() {
        registerSetSpy.setY(new UnsignedWord(0xDEAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E25));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testExchangeUandS() {
        registerSetSpy.setU(new UnsignedWord(0xDEAD));
        registerSetSpy.setS(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E34));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testExchangeUandPC() {
        registerSetSpy.setU(new UnsignedWord(0xDEAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E35));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testExchangeSandPC() {
        registerSetSpy.setS(new UnsignedWord(0xDEAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E45));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testExchangeAandB() {
        registerSetSpy.setA(new UnsignedByte(0xDE));
        registerSetSpy.setB(new UnsignedByte(0xAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E89));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testExchangeAandDP() {
        registerSetSpy.setA(new UnsignedByte(0xDE));
        registerSetSpy.setDP(new UnsignedByte(0xAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E8B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testExchangeAandCC() {
        registerSetSpy.setA(new UnsignedByte(0xDE));
        registerSetSpy.setCC(new UnsignedByte(0xAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E8A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testExchangeBandCC() {
        registerSetSpy.setB(new UnsignedByte(0xDE));
        registerSetSpy.setCC(new UnsignedByte(0xAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E9A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testExchangeBandDP() {
        registerSetSpy.setB(new UnsignedByte(0xDE));
        registerSetSpy.setDP(new UnsignedByte(0xAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E9B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testExchangeCCandDP() {
        registerSetSpy.setCC(new UnsignedByte(0xDE));
        registerSetSpy.setDP(new UnsignedByte(0xAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1EAB));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getCC());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testTransferDtoX() {
        registerSetSpy.setD(new UnsignedWord(0xDEAD));
        registerSetSpy.setX(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F01));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testTransferXtoD() {
        registerSetSpy.setX(new UnsignedWord(0xDEAD));
        registerSetSpy.setD(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F10));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
    }

    @Test
    public void testTransferDtoY() {
        registerSetSpy.setD(new UnsignedWord(0xDEAD));
        registerSetSpy.setY(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F02));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testTransferYtoD() {
        registerSetSpy.setY(new UnsignedWord(0xDEAD));
        registerSetSpy.setD(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F20));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
    }

    @Test
    public void testTransferDtoU() {
        registerSetSpy.setD(new UnsignedWord(0xDEAD));
        registerSetSpy.setU(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F03));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testTransferUtoD() {
        registerSetSpy.setU(new UnsignedWord(0xDEAD));
        registerSetSpy.setD(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F30));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
    }

    @Test
    public void testTransferDtoS() {
        registerSetSpy.setD(new UnsignedWord(0xDEAD));
        registerSetSpy.setS(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F04));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testTransferStoD() {
        registerSetSpy.setS(new UnsignedWord(0xDEAD));
        registerSetSpy.setD(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F40));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
    }

    @Test
    public void testTransferDtoPC() {
        registerSetSpy.setD(new UnsignedWord(0xDEAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F05));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoD() {
        registerSetSpy.setD(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F50));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getD());
    }

    @Test
    public void testTransferXtoY() {
        registerSetSpy.setX(new UnsignedWord(0xDEAD));
        registerSetSpy.setY(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F12));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testTransferYtoX() {
        registerSetSpy.setY(new UnsignedWord(0xDEAD));
        registerSetSpy.setX(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F21));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testTransferXtoU() {
        registerSetSpy.setX(new UnsignedWord(0xDEAD));
        registerSetSpy.setU(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F13));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testTransferUtoX() {
        registerSetSpy.setU(new UnsignedWord(0xDEAD));
        registerSetSpy.setX(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F31));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testTransferXtoS() {
        registerSetSpy.setX(new UnsignedWord(0xDEAD));
        registerSetSpy.setS(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F14));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testTransferStoX() {
        registerSetSpy.setS(new UnsignedWord(0xDEAD));
        registerSetSpy.setX(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F41));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testTransferXtoPC() {
        registerSetSpy.setX(new UnsignedWord(0xDEAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F15));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoX() {
        registerSetSpy.setX(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F51));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getX());
    }

    @Test
    public void testTransferYtoU() {
        registerSetSpy.setY(new UnsignedWord(0xDEAD));
        registerSetSpy.setU(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F23));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testTransferUtoY() {
        registerSetSpy.setU(new UnsignedWord(0xDEAD));
        registerSetSpy.setY(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F32));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testTransferYtoS() {
        registerSetSpy.setY(new UnsignedWord(0xDEAD));
        registerSetSpy.setS(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F24));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testTransferStoY() {
        registerSetSpy.setS(new UnsignedWord(0xDEAD));
        registerSetSpy.setY(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F42));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testTransferYtoPC() {
        registerSetSpy.setY(new UnsignedWord(0xDEAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F25));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoY() {
        registerSetSpy.setY(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F52));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getY());
    }

    @Test
    public void testTransferUtoS() {
        registerSetSpy.setU(new UnsignedWord(0xDEAD));
        registerSetSpy.setS(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F34));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testTransferStoU() {
        registerSetSpy.setS(new UnsignedWord(0xDEAD));
        registerSetSpy.setU(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F43));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testTransferUtoPC() {
        registerSetSpy.setU(new UnsignedWord(0xDEAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F35));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoU() {
        registerSetSpy.setU(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F53));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getU());
    }

    @Test
    public void testTransferStoPC() {
        registerSetSpy.setS(new UnsignedWord(0xDEAD));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F45));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoS() {
        registerSetSpy.setS(new UnsignedWord(0xBEEF));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F54));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getS());
    }

    @Test
    public void testTransferAtoB() {
        registerSetSpy.setA(new UnsignedByte(0xDE));
        registerSetSpy.setB(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F89));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testTransferBtoA() {
        registerSetSpy.setB(new UnsignedByte(0xDE));
        registerSetSpy.setA(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F98));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testTransferAtoCC() {
        registerSetSpy.setA(new UnsignedByte(0xDE));
        registerSetSpy.setCC(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F8A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testTransferCCtoA() {
        registerSetSpy.setCC(new UnsignedByte(0xDE));
        registerSetSpy.setA(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FA8));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
    }

    @Test
    public void testTransferAtoDP() {
        registerSetSpy.setA(new UnsignedByte(0xDE));
        registerSetSpy.setDP(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F8B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testTransferDPtoA() {
        registerSetSpy.setDP(new UnsignedByte(0xDE));
        registerSetSpy.setA(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FB8));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
    }

    @Test
    public void testTransferBtoCC() {
        registerSetSpy.setB(new UnsignedByte(0xDE));
        registerSetSpy.setCC(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F9A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testTransferCCtoB() {
        registerSetSpy.setCC(new UnsignedByte(0xDE));
        registerSetSpy.setB(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FA9));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testTransferBtoDP() {
        registerSetSpy.setB(new UnsignedByte(0xDE));
        registerSetSpy.setDP(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F9B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testTransferDPtoB() {
        registerSetSpy.setDP(new UnsignedByte(0xDE));
        registerSetSpy.setA(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FB9));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testTransferCCtoDP() {
        registerSetSpy.setCC(new UnsignedByte(0xDE));
        registerSetSpy.setDP(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FAB));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testTransferDPtoCC() {
        registerSetSpy.setDP(new UnsignedByte(0xDE));
        registerSetSpy.setCC(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FBA));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testBranchAlways() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x20EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchNeverDoesNothing() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x21EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnHighCalledCorrect() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x22EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnHighNotCalledWhenCarrySet() {
        registerSetSpy.setCCCarry();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x22EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnHighNotCalledWhenZeroSet() {
        registerSetSpy.setCCZero();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x22EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLowerCalledCorrect() {
        registerSetSpy.setCCZero();
        registerSetSpy.setCCCarry();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x23EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnHighNotCalledWhenCarryAndZeroNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x23EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnCarryClearCalledCorrect() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x24EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnCarryClearNotCalledWhenCarrySet() {
        registerSetSpy.setCCCarry();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x24EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnCarrySetCalledCorrect() {
        registerSetSpy.setCCCarry();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x25EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnCarrySetNotCalledWhenCarryNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x25EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnNotEqualCalledCorrect() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x26EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnNotEqualNotCalledWhenZeroSet() {
        registerSetSpy.setCCZero();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x26EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnEqualCalledCorrect() {
        registerSetSpy.setCCZero();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x27EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnEqualNotCalledWhenZeroNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x27EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnOverflowClearCalledCorrect() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x28EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnOverflowClearNotCalledWhenOverflowSet() {
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x28EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnOverflowSetCalledCorrect() {
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x29EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnOverflowSetNotCalledWhenOverflowNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x29EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnPlusCalledCorrect() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2AEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnPlusNotCalledWhenNegativeSet() {
        registerSetSpy.setCCNegative();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2AEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnMinusCalledCorrect() {
        registerSetSpy.setCCNegative();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2BEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnMinusNotCalledWhenNegativeNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2BEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLTCalledCorrectWhenOverflowSet() {
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2DEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLTCalledCorrectWhenNegativeSet() {
        registerSetSpy.setCCNegative();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2DEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLTNotCalledWhenNegativeAndOverflowNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2DEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLTNotCalledWhenNegativeAndOverflowSet() {
        registerSetSpy.setCCNegative();
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2DEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGECalledCorrectWhenOverflowSet() {
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2CEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGECalledCorrectWhenNegativeSet() {
        registerSetSpy.setCCNegative();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2CEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGENotCalledWhenNegativeAndOverflowNotSet() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2CEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGENotCalledWhenNegativeAndOverflowSet() {
        registerSetSpy.setCCNegative();
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2CEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGTCalledCorrectly() {
        registerSetSpy.setCCZero();
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2EEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGTNotCalledIfNotZeroSet() {
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2EEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGTNotCalledIfAllSet() {
        registerSetSpy.setCCZero();
        registerSetSpy.setCCOverflow();
        registerSetSpy.setCCNegative();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2EEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLECalledCorrectly() {
        registerSetSpy.setCCZero();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2FEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLECalledIfNotZeroAndOverflow() {
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2FEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLECalledIfNotZeroAndNegative() {
        registerSetSpy.setCCNegative();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2FEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLENotCalledIfOverflowAndNegative() {
        registerSetSpy.setCCNegative();
        registerSetSpy.setCCOverflow();
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2FEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testLoadEffectiveAddressXCalledCorrectly() {
        memorySpy.writeWord(new UnsignedWord(0xA000), new UnsignedWord(0xBEEF));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x30));
        memorySpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xA000));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadEffectiveAddress(Register.X, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadEffectiveAddressYCalledCorrectly() {
        memorySpy.writeWord(new UnsignedWord(0xA000), new UnsignedWord(0xBEEF));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x31));
        memorySpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xA000));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadEffectiveAddress(Register.Y, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadEffectiveAddressSCalledCorrectly() {
        memorySpy.writeWord(new UnsignedWord(0xA000), new UnsignedWord(0xBEEF));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x32));
        memorySpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xA000));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadEffectiveAddress(Register.S, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadEffectiveAddressUCalledCorrectly() {
        memorySpy.writeWord(new UnsignedWord(0xA000), new UnsignedWord(0xBEEF));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x33));
        memorySpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xA000));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadEffectiveAddress(Register.U, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testABXWorksCorrectly() {
        registerSetSpy.setB(new UnsignedByte(0x8));
        registerSetSpy.setX(new UnsignedWord(0x0020));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0028), registerSetSpy.getX());
    }

    @Test
    public void testRTSWorksCorrectly() {
        registerSetSpy.setS(new UnsignedWord(0x0020));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x39));
        memorySpy.writeWord(new UnsignedWord(0x0020), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getPC());
    }

    @Test
    public void testPushSCalledCorrectly() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3401));
        cpuSpy.executeInstruction();
        verify(cpuSpy).pushStack(Register.S, new UnsignedByte(0x01));
    }

    @Test
    public void testPushUCalledCorrectly() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3601));
        cpuSpy.executeInstruction();
        verify(cpuSpy).pushStack(Register.U, new UnsignedByte(0x01));
    }

    @Test
    public void testPullSCalledCorrectly() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3501));
        cpuSpy.executeInstruction();
        verify(cpuSpy).popStack(Register.S, new UnsignedByte(0x01));
    }

    @Test
    public void testPullUCalledCorrectly() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3701));
        cpuSpy.executeInstruction();
        verify(cpuSpy).popStack(Register.U, new UnsignedByte(0x01));
    }

    @Test
    public void testRTIEverything() {
        registerSetSpy.setCCEverything();
        registerSetSpy.setS(new UnsignedWord(0x9FF4));
        memorySpy.writeByte(new UnsignedWord(0x9FFF), new UnsignedByte(0xCC));
        memorySpy.writeByte(new UnsignedWord(0x9FFE), new UnsignedByte(0xBB));
        memorySpy.writeByte(new UnsignedWord(0x9FFD), new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x9FFC), new UnsignedByte(0x99));
        memorySpy.writeByte(new UnsignedWord(0x9FFB), new UnsignedByte(0x88));
        memorySpy.writeByte(new UnsignedWord(0x9FFA), new UnsignedByte(0x77));
        memorySpy.writeByte(new UnsignedWord(0x9FF9), new UnsignedByte(0x66));
        memorySpy.writeByte(new UnsignedWord(0x9FF8), new UnsignedByte(0x55));
        memorySpy.writeByte(new UnsignedWord(0x9FF7), new UnsignedByte(0x03));
        memorySpy.writeByte(new UnsignedWord(0x9FF6), new UnsignedByte(0x02));
        memorySpy.writeByte(new UnsignedWord(0x9FF5), new UnsignedByte(0x01));
        memorySpy.writeByte(new UnsignedWord(0x9FF4), new UnsignedByte(0x04));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0x1), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0x2), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0x3), registerSetSpy.getDP());
        assertEquals(new UnsignedByte(0x4), registerSetSpy.getCC());
        assertEquals(new UnsignedWord(0x5566), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0x7788), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0x99AA), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xBBCC), registerSetSpy.getPC());
    }

    @Test
    public void testRTIPCOnly() {
        registerSetSpy.setS(new UnsignedWord(0x9FF4));
        memorySpy.writeByte(new UnsignedWord(0x9FF5), new UnsignedByte(0xEF));
        memorySpy.writeByte(new UnsignedWord(0x9FF4), new UnsignedByte(0xBE));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getPC());
    }
    
    @Test
    public void testCWAIWorksCorrect() {
        registerSetSpy.setS(new UnsignedWord(0xA000));
        registerSetSpy.setA(new UnsignedByte(0x1));
        registerSetSpy.setB(new UnsignedByte(0x2));
        registerSetSpy.setDP(new UnsignedByte(0x3));
        registerSetSpy.setCC(new UnsignedByte(0x4));
        registerSetSpy.setX(new UnsignedWord(0x5566));
        registerSetSpy.setY(new UnsignedWord(0x7788));
        registerSetSpy.setU(new UnsignedWord(0x99AA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3C));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0x01), memorySpy.readByte(new UnsignedWord(0x9FFF)));
        assertEquals(new UnsignedByte(0x00), memorySpy.readByte(new UnsignedWord(0x9FFE)));
        assertEquals(new UnsignedByte(0xAA), memorySpy.readByte(new UnsignedWord(0x9FFD)));
        assertEquals(new UnsignedByte(0x99), memorySpy.readByte(new UnsignedWord(0x9FFC)));
        assertEquals(new UnsignedByte(0x88), memorySpy.readByte(new UnsignedWord(0x9FFB)));
        assertEquals(new UnsignedByte(0x77), memorySpy.readByte(new UnsignedWord(0x9FFA)));
        assertEquals(new UnsignedByte(0x66), memorySpy.readByte(new UnsignedWord(0x9FF9)));
        assertEquals(new UnsignedByte(0x55), memorySpy.readByte(new UnsignedWord(0x9FF8)));
        assertEquals(new UnsignedByte(0x03), memorySpy.readByte(new UnsignedWord(0x9FF7)));
        assertEquals(new UnsignedByte(0x02), memorySpy.readByte(new UnsignedWord(0x9FF6)));
        assertEquals(new UnsignedByte(0x01), memorySpy.readByte(new UnsignedWord(0x9FF5)));
        assertEquals(new UnsignedByte(0x80), memorySpy.readByte(new UnsignedWord(0x9FF4)));
    }

    @Test
    public void testMULWorksCorrect() {
        registerSetSpy.setA(new UnsignedByte(0x2));
        registerSetSpy.setB(new UnsignedByte(0x3));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3D));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x6), registerSetSpy.getD());
        assertFalse(registerSetSpy.ccNegativeSet());
        assertFalse(registerSetSpy.ccZeroSet());
    }

    @Test
    public void testMULZeroSetsZero() {
        registerSetSpy.setA(new UnsignedByte(0x2));
        registerSetSpy.setB(new UnsignedByte(0x0));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3D));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0), registerSetSpy.getD());
        assertFalse(registerSetSpy.ccNegativeSet());
        assertTrue(registerSetSpy.ccZeroSet());
    }

    @Test
    public void testNegateAWorksCorrect() {
        registerSetSpy.setA(new UnsignedByte(0x1));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x40));
        cpuSpy.executeInstruction();
        verify(cpuSpy).negate(new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0xFF), registerSetSpy.getA());
    }

    @Test
    public void testNegateBWorksCorrect() {
        registerSetSpy.setB(new UnsignedByte(0x1));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x50));
        cpuSpy.executeInstruction();
        verify(cpuSpy).negate(new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0xFF), registerSetSpy.getB());
    }

    @Test
    public void testComplimentACalled() {
        registerSetSpy.setA(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x43));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compliment(new UnsignedByte(0xAA));
    }

    @Test
    public void testLogicalShiftRightACalled() {
        registerSetSpy.setA(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x44));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalShiftRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testRotateRightACalled() {
        registerSetSpy.setA(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x46));
        cpuSpy.executeInstruction();
        verify(cpuSpy).rotateRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testArithmeticShiftRightACalled() {
        registerSetSpy.setA(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x47));
        cpuSpy.executeInstruction();
        verify(cpuSpy).arithmeticShiftRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testArithmeticShiftLeftACalled() {
        registerSetSpy.setA(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x48));
        cpuSpy.executeInstruction();
        verify(cpuSpy).arithmeticShiftLeft(new UnsignedByte(0xAA));
    }

    @Test
    public void testRotateLeftACalled() {
        registerSetSpy.setA(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x49));
        cpuSpy.executeInstruction();
        verify(cpuSpy).rotateLeft(new UnsignedByte(0xAA));
    }

    @Test
    public void testDecrementACalled() {
        registerSetSpy.setA(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4A));
        cpuSpy.executeInstruction();
        verify(cpuSpy).decrement(new UnsignedByte(0xAA));
    }

    @Test
    public void testIncrementACalled() {
        registerSetSpy.setA(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4C));
        cpuSpy.executeInstruction();
        verify(cpuSpy).increment(new UnsignedByte(0xAA));
    }

    @Test
    public void testTestACalled() {
        registerSetSpy.setA(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4D));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0xAA));
    }

    @Test
    public void testClearACalled() {
        registerSetSpy.setA(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4F));
        cpuSpy.executeInstruction();
        verify(cpuSpy).clear(new UnsignedByte(0xAA));
    }

    @Test
    public void testComplimentBCalled() {
        registerSetSpy.setB(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x53));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compliment(new UnsignedByte(0xAA));
    }

    @Test
    public void testLogicalShiftRightBCalled() {
        registerSetSpy.setB(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x54));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalShiftRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testRotateRightBCalled() {
        registerSetSpy.setB(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x56));
        cpuSpy.executeInstruction();
        verify(cpuSpy).rotateRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testArithmeticShiftRightBCalled() {
        registerSetSpy.setB(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x57));
        cpuSpy.executeInstruction();
        verify(cpuSpy).arithmeticShiftRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testArithmeticShiftLeftBCalled() {
        registerSetSpy.setB(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x58));
        cpuSpy.executeInstruction();
        verify(cpuSpy).arithmeticShiftLeft(new UnsignedByte(0xAA));
    }

    @Test
    public void testRotateLeftBCalled() {
        registerSetSpy.setB(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x59));
        cpuSpy.executeInstruction();
        verify(cpuSpy).rotateLeft(new UnsignedByte(0xAA));
    }

    @Test
    public void testDecrementBCalled() {
        registerSetSpy.setB(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5A));
        cpuSpy.executeInstruction();
        verify(cpuSpy).decrement(new UnsignedByte(0xAA));
    }

    @Test
    public void testIncrementBCalled() {
        registerSetSpy.setB(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5C));
        cpuSpy.executeInstruction();
        verify(cpuSpy).increment(new UnsignedByte(0xAA));
    }

    @Test
    public void testTestBCalled() {
        registerSetSpy.setB(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5D));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0xAA));
    }

    @Test
    public void testClearBCalled() {
        registerSetSpy.setB(new UnsignedByte(0xAA));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5F));
        cpuSpy.executeInstruction();
        verify(cpuSpy).clear(new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMACalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x80AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testCompareACalled() {
        registerSetSpy.setA(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x81AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMCACalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x82AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMADirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x90));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.A, expectedDirectByte);
    }

    @Test
    public void testCompareADirectCalled() {
        registerSetSpy.setA(new UnsignedByte(0xBE));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x91));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedDirectByte);
    }

    @Test
    public void testSubtractMCADirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x92));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.A, expectedDirectByte);
    }

    @Test
    public void testSubtractMAIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA0));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.A, new UnsignedByte(0x00));
    }

    @Test
    public void testCompareAIndexedCalled() {
        registerSetSpy.setA(new UnsignedByte(0xBE));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA1));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMCAIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA2));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.A, new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMAExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB0));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.A, new UnsignedByte(0x00));
    }

    @Test
    public void testCompareAExtendedCalled() {
        registerSetSpy.setA(new UnsignedByte(0xBE));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB1));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMCAExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB2));
        memorySpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.A, new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMBCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC0AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testCompareBCalled() {
        registerSetSpy.setB(new UnsignedByte(0xBE));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC1AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMCBCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC2AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMBDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD0));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.B, expectedDirectByte);
    }

    @Test
    public void testCompareBDirectCalled() {
        registerSetSpy.setB(new UnsignedByte(0xBE));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD1));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedDirectByte);
    }

    @Test
    public void testSubtractMCBDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD2));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.B, expectedDirectByte);
    }

    @Test
    public void testSubtractMBIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE0));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.B, new UnsignedByte(0x00));
    }

    @Test
    public void testCompareBIndexedCalled() {
        registerSetSpy.setB(new UnsignedByte(0xBE));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE1));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMCBIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE2));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.B, new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMBExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF0));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.B, new UnsignedByte(0x00));
    }

    @Test
    public void testCompareBExtendedCalled() {
        registerSetSpy.setB(new UnsignedByte(0xBE));
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF1));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMCBExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF2));
        memorySpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.B, new UnsignedByte(0xF2));
    }

    @Test
    public void testSubtractDImmediateCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x83));
        memorySpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractD(new UnsignedWord(0xBEEF));

    }

    @Test
    public void testSubtractDDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x93));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractD(new UnsignedWord(0xBA00));

    }

    @Test
    public void testSubtractDIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA3));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractD(new UnsignedWord(0x0));
    }

    @Test
    public void testSubtractDExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB3));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractD(new UnsignedWord(0x0));
    }

    @Test
    public void testLogicalAndAImmediateCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x84AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testLogicalAndADirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x94));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.A, expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testLogicalAndAIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA4));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.A, new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testLogicalAndAExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB4));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.A, new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testLogicalAndBImmediateCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC4AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testLogicalAndBDirectCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD4));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.B, expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testLogicalAndBIndexedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE4));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.B, new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testLogicalAndBExtendedCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF4));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.B, new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testTestAImmediateCalled() {
        registerSetSpy.setA(new UnsignedByte(0xAA));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x85AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0xAA));
    }

    @Test
    public void testTestADirectCalled() {
        registerSetSpy.setA(expectedDirectByte);
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9500));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testTestAIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA500));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testTestAExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xB500));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testTestBImmediateCalled() {
        registerSetSpy.setB(new UnsignedByte(0xAA));
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC5AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0xAA));
    }

    @Test
    public void testTestBDirectCalled() {
        registerSetSpy.setB(expectedDirectByte);
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD500));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testTestBIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE500));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testTestBExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xF500));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testLoadAImmediateCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x86AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testLoadADirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.A, expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testLoadAIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.A, new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testLoadAExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xB600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.A, new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testLoadBImmediateCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC6AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testLoadBDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.B, expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testLoadBIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.B, new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testLoadBExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xF600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.B, new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testLoadDImmediateCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xCC));
        memorySpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.D, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadDDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDC00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.D, new UnsignedWord(expectedDirectByte, new UnsignedByte(0x0)));
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testLoadDIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEC00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.D, new UnsignedWord(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testLoadDExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xFC00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.D, new UnsignedWord(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testLoadUImmediateCalled() {
        memorySpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xCE));
        memorySpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.U, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadUDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDE00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.U, new UnsignedWord(expectedDirectByte, new UnsignedByte(0x0)));
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testLoadUIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEE00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.U, new UnsignedWord(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testLoadUExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xFE00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.U, new UnsignedWord(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testEORAImmediateCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x88AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testEORADirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.A, expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testEORAIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.A, new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testEORAExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xB800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.A, new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testEORBImmediateCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC8AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testEORBDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.B, expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testEORBIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.B, new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testEORBExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xF800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.B, new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testADCAImmediateCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x89AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testADCADirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.A, expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testADCAIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.A, new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testADCAExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xB900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.A, new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testADCBImmediateCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC9AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testADCBDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.B, expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testADCBIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.B, new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testADCBExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xF900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.B, new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testORAImmediateCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x8AAA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testORADirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9A00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.A, expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testORAIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAA00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.A, new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testORAExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xBA00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.A, new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testORBImmediateCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xCAAA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testORBDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDA00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.B, expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testORBIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEA00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.B, new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testORBExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xFA00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.B, new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testSTADirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.A, new UnsignedWord(0xA000));
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testSTAIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.A, new UnsignedWord(0x0002));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testSTAExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xB700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.A, new UnsignedWord(0xB700));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testSTBDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.B, new UnsignedWord(0xA000));
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testSTBIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.B, new UnsignedWord(0x0002));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testSTBExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xF700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.B, new UnsignedWord(0xF700));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testJSRDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9D00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).jumpToSubroutine(new UnsignedWord(0xA000));
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testJSRIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAD00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).jumpToSubroutine(new UnsignedWord(0x0002));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testJSRExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xBD00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).jumpToSubroutine(new UnsignedWord(0xBD00));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testSTDDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDD00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.D, new UnsignedWord(0xA000));
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testSTDIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xED00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.D, new UnsignedWord(0x0002));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testSTDExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xFD00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.D, new UnsignedWord(0xFD00));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testSTUDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDF00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.U, new UnsignedWord(0xA000));
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testSTUIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEF00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.U, new UnsignedWord(0x0002));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testSTUExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xFF00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.U, new UnsignedWord(0xFF00));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testADDDDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD300));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addD(new UnsignedWord(0xBA00));
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testADDDIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE300));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addD(new UnsignedWord(0x0000));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testADDDExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xF300));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addD(new UnsignedWord(0x0000));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testADDAImmediateCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x8BAA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testADDADirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9B00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.A, expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testADDAIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAB00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.A, new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testADDAExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xBB00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.A, new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }

    @Test
    public void testADDBImmediateCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xCBAA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testADDBDirectCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDB00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.B, expectedDirectByte);
        verify(memorySpy).getDirect(registerSetSpy);
    }

    @Test
    public void testADDBIndexedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEB00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.B, new UnsignedByte(0x0));
        verify(memorySpy).getIndexed(registerSetSpy);
    }

    @Test
    public void testADDBExtendedCalled() {
        memorySpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xFB00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.B, new UnsignedByte(0x0));
        verify(memorySpy).getExtended(registerSetSpy);
    }
}