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

    private IOController io;
    private IOController ioSpy;

    private RegisterSet registerSet;
    private RegisterSet registerSetSpy;

    private UnsignedByte expectedDirectByte;

    private UnsignedWord extendedAddress;
    private UnsignedByte expectedExtendedByte;
    private UnsignedWord expectedExtendedWord;

    @Before
    public void setUp() {
        memory = new Memory();
        memorySpy = spy(memory);

        registerSet = new RegisterSet();
        registerSetSpy = spy(registerSet);

        io = new IOController(memorySpy, registerSetSpy);
        ioSpy = spy(io);

        cpu = new CPU(ioSpy);
        cpuSpy = spy(cpu);

        ioSpy.setDP(new UnsignedByte(0xA0));
        expectedDirectByte = new UnsignedByte(0xBA);
        ioSpy.writeByte(new UnsignedWord(0xA000), new UnsignedByte(0xBA));

        extendedAddress = new UnsignedWord(0xC0A0);
        expectedExtendedByte = new UnsignedByte(0xCF);
        expectedExtendedWord = new UnsignedWord(0xCFDA);
        ioSpy.writeWord(extendedAddress, expectedExtendedWord);
    }

    @Test
    public void testNegateDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).negate(expectedDirectByte);
    }

    @Test
    public void testNegateIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x60));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).negate(new UnsignedByte(0));
    }

    @Test
    public void testNegateExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x70));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).negate(expectedExtendedByte);
    }

    @Test
    public void testComplementDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x03));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).compliment(expectedDirectByte);
    }

    @Test
    public void testComplementIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x63));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).compliment(new UnsignedByte(0));
    }

    @Test
    public void testComplementExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x73));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).compliment(expectedExtendedByte);
    }

    @Test
    public void testLogicalShiftRightDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x04));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).logicalShiftRight(expectedDirectByte);
    }

    @Test
    public void testLogicalShiftRightIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x64));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).logicalShiftRight(new UnsignedByte(0));
    }

    @Test
    public void testLogicalShiftRightExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x74));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).logicalShiftRight(expectedExtendedByte);
    }

    @Test
    public void testRotateRightDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x06));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).rotateRight(expectedDirectByte);
    }

    @Test
    public void testRotateRightIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x66));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).rotateRight(new UnsignedByte(0));
    }

    @Test
    public void testRotateRightExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x76));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).rotateRight(expectedExtendedByte);
    }

    @Test
    public void testArithmeticShiftRightDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x07));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).arithmeticShiftRight(expectedDirectByte);
    }

    @Test
    public void testArithmeticShiftIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x67));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).arithmeticShiftRight(new UnsignedByte(0));
    }

    @Test
    public void testArithmeticShiftRightExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x77));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).arithmeticShiftRight(expectedExtendedByte);
    }

    @Test
    public void testArithmeticShiftLeftDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x08));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).arithmeticShiftLeft(expectedDirectByte);
    }

    @Test
    public void testArithmeticShiftLeftIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x68));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).arithmeticShiftLeft(new UnsignedByte(0));
    }

    @Test
    public void testArithmeticShiftLeftExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x78));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).arithmeticShiftLeft(expectedExtendedByte);
    }

    @Test
    public void testRotateLeftDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x09));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).rotateLeft(expectedDirectByte);
    }

    @Test
    public void testRotateLeftIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x69));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).rotateLeft(new UnsignedByte(0));
    }

    @Test
    public void testRotateLeftExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x79));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).rotateLeft(expectedExtendedByte);
    }

    @Test
    public void testDecrementDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0A));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).decrement(expectedDirectByte);
    }

    @Test
    public void testDecrementIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6A));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).decrement(new UnsignedByte(0));
    }

    @Test
    public void testDecrementExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7A));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).decrement(expectedExtendedByte);
    }

    @Test
    public void testIncrementDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0C));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).increment(expectedDirectByte);
    }

    @Test
    public void testIncrementIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6C));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).increment(new UnsignedByte(0));
    }

    @Test
    public void testIncrementExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7C));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).increment(expectedExtendedByte);
    }

    @Test
    public void testTestDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0D));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).test(expectedDirectByte);
    }

    @Test
    public void testTestIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6D));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).test(new UnsignedByte(0));
    }

    @Test
    public void testTestExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7D));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).test(expectedExtendedByte);
    }

    @Test
    public void testJumpDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0E));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).jump(new UnsignedWord(0xA000));
    }

    @Test
    public void testJumpIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6E));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).jump(new UnsignedWord(0x2));
    }

    @Test
    public void testJumpExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7E));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).jump(expectedExtendedWord);
    }

    @Test
    public void testClearDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0F));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).clear(expectedDirectByte);
    }

    @Test
    public void testClearIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6F));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).clear(new UnsignedByte(0x0));
    }

    @Test
    public void testClearExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7F));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).clear(expectedExtendedByte);
    }

    @Test
    public void testLongBranchAlways() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x16));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchNeverDoesNothing() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1021));
        cpuSpy.executeInstruction();
        assertEquals(0x4, registerSet.getPC().getInt());
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0x0000));
    }

    @Test
    public void testLongBranchOnHighCalledCorrect() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1022));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnHighNotCalledWhenCarrySet() {
        ioSpy.setCCCarry();
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x10));
        ioSpy.writeByte(new UnsignedWord(0x1), new UnsignedByte(0x22));
        ioSpy.writeByte(new UnsignedWord(0x2), new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x3), new UnsignedByte(0xEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnHighNotCalledWhenZeroSet() {
        ioSpy.setCCZero();
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x10));
        ioSpy.writeByte(new UnsignedWord(0x1), new UnsignedByte(0x22));
        ioSpy.writeByte(new UnsignedWord(0x2), new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x3), new UnsignedByte(0xEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLowerCalledCorrect() {
        ioSpy.setCCZero();
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1023));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnHighNotCalledWhenCarryAndZeroNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1023));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnCarryClearCalledCorrect() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1024));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnCarryClearNotCalledWhenCarrySet() {
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1024));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnCarrySetCalledCorrect() {
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1025));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnCarrySetNotCalledWhenCarryNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1025));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnNotEqualCalledCorrect() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1026));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnNotEqualNotCalledWhenZeroSet() {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1026));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnEqualCalledCorrect() {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1027));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnEqualNotCalledWhenZeroNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1027));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnOverflowClearCalledCorrect() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1028));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnOverflowClearNotCalledWhenOverflowSet() {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1028));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnOverflowSetCalledCorrect() {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1029));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnOverflowSetNotCalledWhenOverflowNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1029));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnPlusCalledCorrect() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102A));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnPlusNotCalledWhenNegativeSet() {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102A));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnMinusCalledCorrect() {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102B));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnMinusNotCalledWhenNegativeNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102B));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLTCalledCorrectWhenOverflowSet() {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102D));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLTCalledCorrectWhenNegativeSet() {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102D));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }


    @Test
    public void testLongBranchOnLTNotCalledWhenNegativeAndOverflowNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102D));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLTNotCalledWhenNegativeAndOverflowSet() {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102D));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGECalledCorrectWhenOverflowSet() {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102C));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGECalledCorrectWhenNegativeSet() {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102C));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }


    @Test
    public void testLongBranchOnGENotCalledWhenNegativeAndOverflowNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102C));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGENotCalledWhenNegativeAndOverflowSet() {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102C));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGTCalledCorrectly() {
        ioSpy.setCCZero();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102E));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGTNotCalledIfNotZeroSet() {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102E));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGTNotCalledIfAllSet() {
        ioSpy.setCCZero();
        ioSpy.setCCOverflow();
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102E));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLECalledCorrectly() {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102F));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLECalledIfNotZeroAndOverflow() {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102F));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLECalledIfNotZeroAndNegative() {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102F));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLENotCalledIfOverflowAndNegative() {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102F));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testSWI3PushesAllValues() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x103F));
        ioSpy.writeByte(CPU.SWI3, new UnsignedByte(0x56));
        ioSpy.writeByte(CPU.SWI3.next(), new UnsignedByte(0x78));
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.setS(new UnsignedWord(0xA000));
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.setX(new UnsignedWord(0xCAFE));
        ioSpy.setDP(new UnsignedByte(0xAB));
        ioSpy.setB(new UnsignedByte(0xCD));
        ioSpy.setA(new UnsignedByte(0xEF));
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
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x113F));
        ioSpy.writeByte(CPU.SWI3, new UnsignedByte(0x56));
        ioSpy.writeByte(CPU.SWI3.next(), new UnsignedByte(0x78));
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.setS(new UnsignedWord(0xA000));
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.setX(new UnsignedWord(0xCAFE));
        ioSpy.setDP(new UnsignedByte(0xAB));
        ioSpy.setB(new UnsignedByte(0xCD));
        ioSpy.setA(new UnsignedByte(0xEF));
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
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3F));
        ioSpy.writeByte(CPU.SWI, new UnsignedByte(0x56));
        ioSpy.writeByte(CPU.SWI.next(), new UnsignedByte(0x78));
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.setS(new UnsignedWord(0xA000));
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.setX(new UnsignedWord(0xCAFE));
        ioSpy.setDP(new UnsignedByte(0xAB));
        ioSpy.setB(new UnsignedByte(0xCD));
        ioSpy.setA(new UnsignedByte(0xEF));
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
        ioSpy.setD(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1083));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareDDirectCalled() {
        ioSpy.setD(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1093));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareDIndexedCalled() {
        ioSpy.setD(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10A3));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareDExtendedCalled() {
        ioSpy.setD(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10B3));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), expectedExtendedWord);
    }

    @Test
    public void testCompareUImmediateCalled() {
        ioSpy.setU(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1183));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareUDirectCalled() {
        ioSpy.setU(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1193));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareUIndexedCalled() {
        ioSpy.setU(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x11A3));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareUExtendedCalled() {
        ioSpy.setU(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x11B3));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), expectedExtendedWord);
    }

    @Test
    public void testCompareYImmediateCalled() {
        ioSpy.setY(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x108C));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareYDirectCalled() {
        ioSpy.setY(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x109C));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareYIndexedCalled() {
        ioSpy.setY(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10AC));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareYExtendedCalled() {
        ioSpy.setY(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10BC));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), expectedExtendedWord);
    }

    @Test
    public void testCompareSImmediateCalled() {
        ioSpy.setS(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x118C));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareSDirectCalled() {
        ioSpy.setS(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x119C));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareSIndexedCalled() {
        ioSpy.setS(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x11AC));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareSExtendedCalled() {
        ioSpy.setS(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x11BC));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), expectedExtendedWord);
    }

    @Test
    public void testCompareXImmediateCalled() {
        ioSpy.setX(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x8C00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareXDirectCalled() {
        ioSpy.setX(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9C00));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareXIndexedCalled() {
        ioSpy.setX(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAC00));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareXExtendedCalled() {
        ioSpy.setX(new UnsignedWord(0x10));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xBC));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), expectedExtendedWord);
    }

    @Test
    public void testLoadYImmediateCalled() {
        ioSpy.setY(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x108E));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.Y, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadYDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x109E));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).loadRegister(Register.Y, new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testLoadYIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10AE));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).loadRegister(Register.Y, new UnsignedWord());
    }

    @Test
    public void testLoadYExtendedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10BE));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).loadRegister(Register.Y, expectedExtendedWord);
    }

    @Test
    public void testLoadSImmediateCalled() {
        ioSpy.setY(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10CE));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.S, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadSDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10DE));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).loadRegister(Register.S, new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testLoadSIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10EE));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).loadRegister(Register.S, new UnsignedWord());
    }

    @Test
    public void testLoadSExtendedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10FE));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).loadRegister(Register.S, expectedExtendedWord);
    }

    @Test
    public void testLoadXImmediateCalled() {
        ioSpy.setX(new UnsignedWord(0x10));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x8E));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.X, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadXDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9E00));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).loadRegister(Register.X, new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testLoadXIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAE00));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).loadRegister(Register.X, new UnsignedWord(0x0002));
    }

    @Test
    public void testLoadXExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).loadRegister(Register.X, expectedExtendedWord);
    }

    @Test
    public void testStoreYDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x109F));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).storeWordRegister(Register.Y, new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testStoreYIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10AF));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).storeWordRegister(Register.Y, new UnsignedWord());
    }

    @Test
    public void testStoreYExtendedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10BF));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).storeWordRegister(Register.Y, expectedExtendedWord);
    }

    @Test
    public void testStoreXDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9F00));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).storeWordRegister(Register.X, new UnsignedWord(0xA000));
    }

    @Test
    public void testStoreXIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAF00));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).storeWordRegister(Register.X, new UnsignedWord(0x0002));
    }

    @Test
    public void testStoreXExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xBF));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).storeWordRegister(Register.X, expectedExtendedWord);
    }

    @Test
    public void testStoreSDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10DF));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).storeWordRegister(Register.S, new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testStoreSIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10EF));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).storeWordRegister(Register.S, new UnsignedWord());
    }

    @Test
    public void testStoreSExtendedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10FF));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).storeWordRegister(Register.S, expectedExtendedWord);
    }

    @Test
    public void testDecimalAdditionAdjustCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x19));
        cpuSpy.executeInstruction();
        verify(cpuSpy).decimalAdditionAdjust();
    }

    @Test
    public void testORConditionCodeWorksCorrectly() {
        ioSpy.setCC(new UnsignedByte(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1A01));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0x11), registerSetSpy.getCC());
    }

    @Test
    public void testANDConditionCodeWorksCorrectly() {
        ioSpy.setCC(new UnsignedByte(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1C11));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0x10), registerSetSpy.getCC());
    }

    @Test
    public void testSignExtendWorksCorrectly() {
        ioSpy.setB(new UnsignedByte(0x81));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x1D));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xFF), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0x81), registerSetSpy.getB());
    }

    @Test
    public void testSignExtendWorksCorrectlyWithNoExtension() {
        ioSpy.setB(new UnsignedByte(0x7F));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x1D));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0x00), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0x7F), registerSetSpy.getB());
    }

    @Test
    public void testExchangeDandX() {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setX(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E01));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testExchangeDandY() {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E02));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testExchangeDandU() {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E03));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testExchangeDandS() {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E04));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testExchangeDandPC() {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E05));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getD());
    }

    @Test
    public void testExchangeXandY() {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E12));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testExchangeXandU() {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E13));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testExchangeXandS() {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E14));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testExchangeXandPC() {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E15));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testExchangeYandU() {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E23));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testExchangeYandS() {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E24));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testExchangeYandPC() {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E25));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testExchangeUandS() {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E34));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testExchangeUandPC() {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E35));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testExchangeSandPC() {
        ioSpy.setS(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E45));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testExchangeAandB() {
        ioSpy.setA(new UnsignedByte(0xDE));
        ioSpy.setB(new UnsignedByte(0xAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E89));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testExchangeAandDP() {
        ioSpy.setA(new UnsignedByte(0xDE));
        ioSpy.setDP(new UnsignedByte(0xAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E8B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testExchangeAandCC() {
        ioSpy.setA(new UnsignedByte(0xDE));
        ioSpy.setCC(new UnsignedByte(0xAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E8A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testExchangeBandCC() {
        ioSpy.setB(new UnsignedByte(0xDE));
        ioSpy.setCC(new UnsignedByte(0xAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E9A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testExchangeBandDP() {
        ioSpy.setB(new UnsignedByte(0xDE));
        ioSpy.setDP(new UnsignedByte(0xAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E9B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testExchangeCCandDP() {
        ioSpy.setCC(new UnsignedByte(0xDE));
        ioSpy.setDP(new UnsignedByte(0xAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1EAB));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getCC());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testTransferDtoX() {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setX(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F01));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testTransferXtoD() {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setD(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F10));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
    }

    @Test
    public void testTransferDtoY() {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F02));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testTransferYtoD() {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.setD(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F20));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
    }

    @Test
    public void testTransferDtoU() {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F03));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testTransferUtoD() {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.setD(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F30));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
    }

    @Test
    public void testTransferDtoS() {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F04));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testTransferStoD() {
        ioSpy.setS(new UnsignedWord(0xDEAD));
        ioSpy.setD(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F40));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
    }

    @Test
    public void testTransferDtoPC() {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F05));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoD() {
        ioSpy.setD(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F50));
        cpuSpy.executeInstruction();
        /* PC will have advanced */
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getD());
    }

    @Test
    public void testTransferXtoY() {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F12));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testTransferYtoX() {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.setX(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F21));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testTransferXtoU() {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F13));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testTransferUtoX() {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.setX(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F31));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testTransferXtoS() {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F14));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testTransferStoX() {
        ioSpy.setS(new UnsignedWord(0xDEAD));
        ioSpy.setX(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F41));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testTransferXtoPC() {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F15));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoX() {
        ioSpy.setX(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F51));
        cpuSpy.executeInstruction();
        /* PC will have advanced */
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getX());
    }

    @Test
    public void testTransferYtoU() {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F23));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testTransferUtoY() {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F32));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testTransferYtoS() {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F24));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testTransferStoY() {
        ioSpy.setS(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F42));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testTransferYtoPC() {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F25));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoY() {
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F52));
        cpuSpy.executeInstruction();
        /* PC will have advanced */
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getY());
    }

    @Test
    public void testTransferUtoS() {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F34));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testTransferStoU() {
        ioSpy.setS(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F43));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testTransferUtoPC() {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F35));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoU() {
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F53));
        cpuSpy.executeInstruction();
        /* PC will have advanced */
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getU());
    }

    @Test
    public void testTransferStoPC() {
        ioSpy.setS(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F45));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoS() {
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F54));
        cpuSpy.executeInstruction();
        /* PC will have advanced */
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0001), registerSetSpy.getS());
    }

    @Test
    public void testTransferAtoB() {
        ioSpy.setA(new UnsignedByte(0xDE));
        ioSpy.setB(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F89));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testTransferBtoA() {
        ioSpy.setB(new UnsignedByte(0xDE));
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F98));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testTransferAtoCC() {
        ioSpy.setA(new UnsignedByte(0xDE));
        ioSpy.setCC(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F8A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testTransferCCtoA() {
        ioSpy.setCC(new UnsignedByte(0xDE));
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FA8));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
    }

    @Test
    public void testTransferAtoDP() {
        ioSpy.setA(new UnsignedByte(0xDE));
        ioSpy.setDP(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F8B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testTransferDPtoA() {
        ioSpy.setDP(new UnsignedByte(0xDE));
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FB8));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
    }

    @Test
    public void testTransferBtoCC() {
        ioSpy.setB(new UnsignedByte(0xDE));
        ioSpy.setCC(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F9A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testTransferCCtoB() {
        ioSpy.setCC(new UnsignedByte(0xDE));
        ioSpy.setB(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FA9));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testTransferBtoDP() {
        ioSpy.setB(new UnsignedByte(0xDE));
        ioSpy.setDP(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F9B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testTransferDPtoB() {
        ioSpy.setDP(new UnsignedByte(0xDE));
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FB9));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testTransferCCtoDP() {
        ioSpy.setCC(new UnsignedByte(0xDE));
        ioSpy.setDP(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FAB));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testTransferDPtoCC() {
        ioSpy.setDP(new UnsignedByte(0xDE));
        ioSpy.setCC(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FBA));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testBranchAlways() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x20EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchNeverDoesNothing() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x21EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnHighCalledCorrect() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x22EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnHighNotCalledWhenCarrySet() {
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x22EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnHighNotCalledWhenZeroSet() {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x22EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLowerCalledCorrect() {
        ioSpy.setCCZero();
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x23EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnHighNotCalledWhenCarryAndZeroNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x23EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnCarryClearCalledCorrect() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x24EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnCarryClearNotCalledWhenCarrySet() {
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x24EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnCarrySetCalledCorrect() {
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x25EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnCarrySetNotCalledWhenCarryNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x25EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnNotEqualCalledCorrect() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x26EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnNotEqualNotCalledWhenZeroSet() {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x26EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnEqualCalledCorrect() {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x27EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnEqualNotCalledWhenZeroNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x27EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnOverflowClearCalledCorrect() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x28EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnOverflowClearNotCalledWhenOverflowSet() {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x28EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnOverflowSetCalledCorrect() {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x29EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnOverflowSetNotCalledWhenOverflowNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x29EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnPlusCalledCorrect() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2AEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnPlusNotCalledWhenNegativeSet() {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2AEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnMinusCalledCorrect() {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2BEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnMinusNotCalledWhenNegativeNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2BEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLTCalledCorrectWhenOverflowSet() {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2DEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLTCalledCorrectWhenNegativeSet() {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2DEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLTNotCalledWhenNegativeAndOverflowNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2DEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLTNotCalledWhenNegativeAndOverflowSet() {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2DEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGECalledCorrectWhenOverflowSet() {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2CEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGECalledCorrectWhenNegativeSet() {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2CEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGENotCalledWhenNegativeAndOverflowNotSet() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2CEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGENotCalledWhenNegativeAndOverflowSet() {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2CEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGTCalledCorrectly() {
        ioSpy.setCCZero();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2EEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGTNotCalledIfNotZeroSet() {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2EEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGTNotCalledIfAllSet() {
        ioSpy.setCCZero();
        ioSpy.setCCOverflow();
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2EEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLECalledCorrectly() {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2FEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLECalledIfNotZeroAndOverflow() {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2FEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLECalledIfNotZeroAndNegative() {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2FEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLENotCalledIfOverflowAndNegative() {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2FEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testLoadEffectiveAddressXCalledCorrectly() {
        ioSpy.writeWord(new UnsignedWord(0xA000), new UnsignedWord(0xBEEF));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x30));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xA000));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadEffectiveAddress(Register.X, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadEffectiveAddressYCalledCorrectly() {
        ioSpy.writeWord(new UnsignedWord(0xA000), new UnsignedWord(0xBEEF));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x31));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xA000));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadEffectiveAddress(Register.Y, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadEffectiveAddressSCalledCorrectly() {
        ioSpy.writeWord(new UnsignedWord(0xA000), new UnsignedWord(0xBEEF));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x32));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xA000));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadEffectiveAddress(Register.S, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadEffectiveAddressUCalledCorrectly() {
        ioSpy.writeWord(new UnsignedWord(0xA000), new UnsignedWord(0xBEEF));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x33));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xA000));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadEffectiveAddress(Register.U, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testABXWorksCorrectly() {
        ioSpy.setB(new UnsignedByte(0x8));
        ioSpy.setX(new UnsignedWord(0x0020));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0028), registerSetSpy.getX());
    }

    @Test
    public void testRTSWorksCorrectly() {
        ioSpy.setS(new UnsignedWord(0x0020));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x39));
        ioSpy.writeWord(new UnsignedWord(0x0020), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getPC());
    }

    @Test
    public void testPushSCalledCorrectly() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3401));
        cpuSpy.executeInstruction();
        verify(cpuSpy).pushStack(Register.S, new UnsignedByte(0x01));
    }

    @Test
    public void testPushUCalledCorrectly() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3601));
        cpuSpy.executeInstruction();
        verify(cpuSpy).pushStack(Register.U, new UnsignedByte(0x01));
    }

    @Test
    public void testPullSCalledCorrectly() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3501));
        cpuSpy.executeInstruction();
        verify(cpuSpy).popStack(Register.S, new UnsignedByte(0x01));
    }

    @Test
    public void testPullUCalledCorrectly() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3701));
        cpuSpy.executeInstruction();
        verify(cpuSpy).popStack(Register.U, new UnsignedByte(0x01));
    }

    @Test
    public void testRTIEverything() {
        ioSpy.setCCEverything();
        ioSpy.setS(new UnsignedWord(0x9FF4));
        ioSpy.writeByte(new UnsignedWord(0x9FFF), new UnsignedByte(0xCC));
        ioSpy.writeByte(new UnsignedWord(0x9FFE), new UnsignedByte(0xBB));
        ioSpy.writeByte(new UnsignedWord(0x9FFD), new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x9FFC), new UnsignedByte(0x99));
        ioSpy.writeByte(new UnsignedWord(0x9FFB), new UnsignedByte(0x88));
        ioSpy.writeByte(new UnsignedWord(0x9FFA), new UnsignedByte(0x77));
        ioSpy.writeByte(new UnsignedWord(0x9FF9), new UnsignedByte(0x66));
        ioSpy.writeByte(new UnsignedWord(0x9FF8), new UnsignedByte(0x55));
        ioSpy.writeByte(new UnsignedWord(0x9FF7), new UnsignedByte(0x03));
        ioSpy.writeByte(new UnsignedWord(0x9FF6), new UnsignedByte(0x02));
        ioSpy.writeByte(new UnsignedWord(0x9FF5), new UnsignedByte(0x01));
        ioSpy.writeByte(new UnsignedWord(0x9FF4), new UnsignedByte(0x04));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3B));
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
        ioSpy.setS(new UnsignedWord(0x9FF4));
        ioSpy.writeByte(new UnsignedWord(0x9FF5), new UnsignedByte(0xEF));
        ioSpy.writeByte(new UnsignedWord(0x9FF4), new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getPC());
    }
    
    @Test
    public void testCWAIWorksCorrect() {
        ioSpy.setS(new UnsignedWord(0xA000));
        ioSpy.setA(new UnsignedByte(0x1));
        ioSpy.setB(new UnsignedByte(0x2));
        ioSpy.setDP(new UnsignedByte(0x3));
        ioSpy.setCC(new UnsignedByte(0x4));
        ioSpy.setX(new UnsignedWord(0x5566));
        ioSpy.setY(new UnsignedWord(0x7788));
        ioSpy.setU(new UnsignedWord(0x99AA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3C));
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
        ioSpy.setA(new UnsignedByte(0x2));
        ioSpy.setB(new UnsignedByte(0x3));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3D));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x6), registerSetSpy.getD());
        assertFalse(ioSpy.ccNegativeSet());
        assertFalse(ioSpy.ccZeroSet());
    }

    @Test
    public void testMULZeroSetsZero() {
        ioSpy.setA(new UnsignedByte(0x2));
        ioSpy.setB(new UnsignedByte(0x0));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3D));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0), registerSetSpy.getD());
        assertFalse(ioSpy.ccNegativeSet());
        assertTrue(ioSpy.ccZeroSet());
    }

    @Test
    public void testNegateAWorksCorrect() {
        ioSpy.setA(new UnsignedByte(0x1));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x40));
        cpuSpy.executeInstruction();
        verify(cpuSpy).negate(new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0xFF), registerSetSpy.getA());
    }

    @Test
    public void testNegateBWorksCorrect() {
        ioSpy.setB(new UnsignedByte(0x1));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x50));
        cpuSpy.executeInstruction();
        verify(cpuSpy).negate(new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0xFF), registerSetSpy.getB());
    }

    @Test
    public void testComplimentACalled() {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x43));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compliment(new UnsignedByte(0xAA));
    }

    @Test
    public void testLogicalShiftRightACalled() {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x44));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalShiftRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testRotateRightACalled() {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x46));
        cpuSpy.executeInstruction();
        verify(cpuSpy).rotateRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testArithmeticShiftRightACalled() {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x47));
        cpuSpy.executeInstruction();
        verify(cpuSpy).arithmeticShiftRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testArithmeticShiftLeftACalled() {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x48));
        cpuSpy.executeInstruction();
        verify(cpuSpy).arithmeticShiftLeft(new UnsignedByte(0xAA));
    }

    @Test
    public void testRotateLeftACalled() {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x49));
        cpuSpy.executeInstruction();
        verify(cpuSpy).rotateLeft(new UnsignedByte(0xAA));
    }

    @Test
    public void testDecrementACalled() {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4A));
        cpuSpy.executeInstruction();
        verify(cpuSpy).decrement(new UnsignedByte(0xAA));
    }

    @Test
    public void testIncrementACalled() {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4C));
        cpuSpy.executeInstruction();
        verify(cpuSpy).increment(new UnsignedByte(0xAA));
    }

    @Test
    public void testTestACalled() {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4D));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0xAA));
    }

    @Test
    public void testClearACalled() {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4F));
        cpuSpy.executeInstruction();
        verify(cpuSpy).clear(new UnsignedByte(0xAA));
    }

    @Test
    public void testComplimentBCalled() {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x53));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compliment(new UnsignedByte(0xAA));
    }

    @Test
    public void testLogicalShiftRightBCalled() {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x54));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalShiftRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testRotateRightBCalled() {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x56));
        cpuSpy.executeInstruction();
        verify(cpuSpy).rotateRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testArithmeticShiftRightBCalled() {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x57));
        cpuSpy.executeInstruction();
        verify(cpuSpy).arithmeticShiftRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testArithmeticShiftLeftBCalled() {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x58));
        cpuSpy.executeInstruction();
        verify(cpuSpy).arithmeticShiftLeft(new UnsignedByte(0xAA));
    }

    @Test
    public void testRotateLeftBCalled() {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x59));
        cpuSpy.executeInstruction();
        verify(cpuSpy).rotateLeft(new UnsignedByte(0xAA));
    }

    @Test
    public void testDecrementBCalled() {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5A));
        cpuSpy.executeInstruction();
        verify(cpuSpy).decrement(new UnsignedByte(0xAA));
    }

    @Test
    public void testIncrementBCalled() {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5C));
        cpuSpy.executeInstruction();
        verify(cpuSpy).increment(new UnsignedByte(0xAA));
    }

    @Test
    public void testTestBCalled() {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5D));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0xAA));
    }

    @Test
    public void testClearBCalled() {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5F));
        cpuSpy.executeInstruction();
        verify(cpuSpy).clear(new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMACalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x80AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testCompareACalled() {
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x81AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMCACalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x82AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMADirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x90));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.A, expectedDirectByte);
    }

    @Test
    public void testCompareADirectCalled() {
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x91));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedDirectByte);
    }

    @Test
    public void testSubtractMCADirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x92));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.A, expectedDirectByte);
    }

    @Test
    public void testSubtractMAIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA0));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.A, new UnsignedByte(0x00));
    }

    @Test
    public void testCompareAIndexedCalled() {
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA1));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMCAIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA2));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.A, new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMAExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB0));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.A, expectedExtendedByte);
    }

    @Test
    public void testCompareAExtendedCalled() {
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB1));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedExtendedByte);
    }

    @Test
    public void testSubtractMCAExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB2));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.A, expectedExtendedByte);
    }

    @Test
    public void testSubtractMBCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC0AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testCompareBCalled() {
        ioSpy.setB(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC1AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMCBCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC2AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMBDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD0));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.B, expectedDirectByte);
    }

    @Test
    public void testCompareBDirectCalled() {
        ioSpy.setB(new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD1));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedDirectByte);
    }

    @Test
    public void testSubtractMCBDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD2));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.B, expectedDirectByte);
    }

    @Test
    public void testSubtractMBIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE0));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.B, new UnsignedByte(0x00));
    }

    @Test
    public void testCompareBIndexedCalled() {
        ioSpy.setB(new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE1));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMCBIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE2));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.B, new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMBExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF0));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.B, expectedExtendedByte);
    }

    @Test
    public void testCompareBExtendedCalled() {
        ioSpy.setB(new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF1));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedExtendedByte);
    }

    @Test
    public void testSubtractMCBExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF2));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.B, expectedExtendedByte);
    }

    @Test
    public void testSubtractDImmediateCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x83));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractD(new UnsignedWord(0xBEEF));

    }

    @Test
    public void testSubtractDDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x93));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractD(new UnsignedWord(0xBA00));

    }

    @Test
    public void testSubtractDIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA3));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractD(new UnsignedWord(0x0));
    }

    @Test
    public void testSubtractDExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB3));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractD(expectedExtendedWord);
    }

    @Test
    public void testLogicalAndAImmediateCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x84AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testLogicalAndADirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x94));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.A, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testLogicalAndAIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA4));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.A, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testLogicalAndAExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB4));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.A, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testLogicalAndBImmediateCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC4AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testLogicalAndBDirectCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD4));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.B, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testLogicalAndBIndexedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE4));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.B, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testLogicalAndBExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF4));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.B, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testTestAImmediateCalled() {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x85AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0xAA));
    }

    @Test
    public void testTestADirectCalled() {
        ioSpy.setA(expectedDirectByte);
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9500));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testTestAIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA500));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testTestAExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB5));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(registerSetSpy.getA().getShort() & expectedExtendedByte.getShort()));
        verify(ioSpy).getExtended();
    }

    @Test
    public void testTestBImmediateCalled() {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC5AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0xAA));
    }

    @Test
    public void testTestBDirectCalled() {
        ioSpy.setB(expectedDirectByte);
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD500));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testTestBIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE500));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testTestBExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF5));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(registerSetSpy.getB().getShort() & expectedExtendedByte.getShort()));
        verify(ioSpy).getExtended();
    }

    @Test
    public void testLoadAImmediateCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x86AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testLoadADirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.A, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testLoadAIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.A, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testLoadAExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB6));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.A, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testLoadBImmediateCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC6AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testLoadBDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.B, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testLoadBIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.B, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testLoadBExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF6));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.B, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testLoadDImmediateCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xCC));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.D, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadDDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDC00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.D, new UnsignedWord(expectedDirectByte, new UnsignedByte(0x0)));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testLoadDIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEC00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.D, new UnsignedWord(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testLoadDExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xFC));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.D, expectedExtendedWord);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testLoadUImmediateCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xCE));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.U, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadUDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDE00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.U, new UnsignedWord(expectedDirectByte, new UnsignedByte(0x0)));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testLoadUIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEE00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.U, new UnsignedWord(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testLoadUExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xFE));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.U, expectedExtendedWord);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testEORAImmediateCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x88AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testEORADirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.A, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testEORAIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.A, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testEORAExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB8));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.A, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testEORBImmediateCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC8AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testEORBDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.B, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testEORBIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.B, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testEORBExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF8));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.B, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testADCAImmediateCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x89AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testADCADirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.A, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testADCAIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.A, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testADCAExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB9));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.A, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testADCBImmediateCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC9AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testADCBDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.B, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testADCBIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.B, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testADCBExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF9));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.B, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testORAImmediateCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x8AAA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testORADirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9A00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.A, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testORAIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAA00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.A, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testORAExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xBA));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.A, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testORBImmediateCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xCAAA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testORBDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDA00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.B, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testORBIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEA00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.B, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testORBExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xFA));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.B, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testSTADirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.A, new UnsignedWord(0xA000));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testSTAIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.A, new UnsignedWord(0x0002));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testSTAExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB7));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.A, extendedAddress);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testSTBDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.B, new UnsignedWord(0xA000));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testSTBIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.B, new UnsignedWord(0x0002));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testSTBExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF7));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.B, extendedAddress);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testJSRDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9D00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).jumpToSubroutine(new UnsignedWord(0xA000));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testJSRIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAD00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).jumpToSubroutine(new UnsignedWord(0x0002));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testJSRExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xBD));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).jumpToSubroutine(expectedExtendedWord);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testSTDDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDD00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.D, new UnsignedWord(0xA000));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testSTDIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xED00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.D, new UnsignedWord(0x0002));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testSTDExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xFD));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.D, expectedExtendedWord);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testSTUDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDF00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.U, new UnsignedWord(0xA000));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testSTUIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEF00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.U, new UnsignedWord(0x0002));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testSTUExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xFF));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.U, expectedExtendedWord);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testADDDDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD300));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addD(new UnsignedWord(0xBA00));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testADDDIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE300));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addD(new UnsignedWord(0x0000));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testADDDExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF3));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).addD(expectedExtendedWord);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testADDAImmediateCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x8BAA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testADDADirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9B00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.A, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testADDAIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAB00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.A, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testADDAExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xBB));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.A, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testADDBImmediateCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xCBAA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testADDBDirectCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDB00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.B, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testADDBIndexedCalled() {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEB00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.B, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testADDBExtendedCalled() {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xFB));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.B, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }
}