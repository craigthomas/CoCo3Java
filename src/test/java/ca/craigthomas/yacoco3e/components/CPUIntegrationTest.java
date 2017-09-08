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
    public void setUp() throws IllegalIndexedPostbyteException {
        memory = new Memory();
        memorySpy = spy(memory);

        registerSet = new RegisterSet();
        registerSetSpy = spy(registerSet);

        io = new IOController(memorySpy, registerSetSpy, new Keyboard());
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

        ioSpy.setX(new UnsignedWord(0xB000));
        memorySpy.enableAllRAMMode();
    }

    @Test
    public void testNegateDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).negate(expectedDirectByte);
    }

    @Test
    public void testNegateIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x6080));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).negate(new UnsignedByte(0));
    }

    @Test
    public void testNegateExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x70));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).negate(expectedExtendedByte);
    }

    @Test
    public void testComplementDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x03));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).compliment(expectedDirectByte);
    }

    @Test
    public void testComplementIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x63));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).compliment(new UnsignedByte(0));
    }

    @Test
    public void testComplementExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x73));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).compliment(expectedExtendedByte);
    }

    @Test
    public void testLogicalShiftRightDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x04));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).logicalShiftRight(expectedDirectByte);
    }

    @Test
    public void testLogicalShiftRightIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x64));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).logicalShiftRight(new UnsignedByte(0));
    }

    @Test
    public void testLogicalShiftRightExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x74));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).logicalShiftRight(expectedExtendedByte);
    }

    @Test
    public void testRotateRightDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x06));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).rotateRight(expectedDirectByte);
    }

    @Test
    public void testRotateRightIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x66));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).rotateRight(new UnsignedByte(0));
    }

    @Test
    public void testRotateRightExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x76));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).rotateRight(expectedExtendedByte);
    }

    @Test
    public void testArithmeticShiftRightDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x07));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).arithmeticShiftRight(expectedDirectByte);
    }

    @Test
    public void testArithmeticShiftIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x67));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).arithmeticShiftRight(new UnsignedByte(0));
    }

    @Test
    public void testArithmeticShiftRightExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x77));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).arithmeticShiftRight(expectedExtendedByte);
    }

    @Test
    public void testArithmeticShiftLeftDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x08));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).arithmeticShiftLeft(expectedDirectByte);
    }

    @Test
    public void testArithmeticShiftLeftIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x68));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).arithmeticShiftLeft(new UnsignedByte(0));
    }

    @Test
    public void testArithmeticShiftLeftExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x78));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).arithmeticShiftLeft(expectedExtendedByte);
    }

    @Test
    public void testRotateLeftDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x09));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).rotateLeft(expectedDirectByte);
    }

    @Test
    public void testRotateLeftIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x69));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).rotateLeft(new UnsignedByte(0));
    }

    @Test
    public void testRotateLeftExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x79));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).rotateLeft(expectedExtendedByte);
    }

    @Test
    public void testDecrementDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0A));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).decrement(expectedDirectByte);
    }

    @Test
    public void testDecrementIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6A));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).decrement(new UnsignedByte(0));
    }

    @Test
    public void testDecrementExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7A));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).decrement(expectedExtendedByte);
    }

    @Test
    public void testIncrementDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0C));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).increment(expectedDirectByte);
    }

    @Test
    public void testIncrementIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6C));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).increment(new UnsignedByte(0));
    }

    @Test
    public void testIncrementExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7C));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).increment(expectedExtendedByte);
    }

    @Test
    public void testTestDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0D));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).test(expectedDirectByte);
    }

    @Test
    public void testTestIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6D));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).test(new UnsignedByte(0));
    }

    @Test
    public void testTestExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7D));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).test(expectedExtendedByte);
    }

    @Test
    public void testJumpDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0E));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).jump(new UnsignedWord(0xA000));
    }

    @Test
    public void testJumpIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x6E80));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).jump(new UnsignedWord(0xB000));
    }

    @Test
    public void testJumpExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7E));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).jump(extendedAddress);
    }

    @Test
    public void testClearDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x0F));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).clear(expectedDirectByte);
    }

    @Test
    public void testClearIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x6F));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).clear(new UnsignedByte(0x0));
    }

    @Test
    public void testClearExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x7F));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).clear(expectedExtendedByte);
    }

    @Test
    public void testLongBranchAlways() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x16));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchNeverDoesNothing() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1021));
        cpuSpy.executeInstruction();
        assertEquals(0x4, registerSet.getPC().getInt());
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0x0000));
    }

    @Test
    public void testLongBranchOnHighCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1022));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnHighNotCalledWhenCarrySet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCCarry();
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x10));
        ioSpy.writeByte(new UnsignedWord(0x1), new UnsignedByte(0x22));
        ioSpy.writeByte(new UnsignedWord(0x2), new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x3), new UnsignedByte(0xEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnHighNotCalledWhenZeroSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCZero();
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x10));
        ioSpy.writeByte(new UnsignedWord(0x1), new UnsignedByte(0x22));
        ioSpy.writeByte(new UnsignedWord(0x2), new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x3), new UnsignedByte(0xEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLowerCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.setCCZero();
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1023));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnHighNotCalledWhenCarryAndZeroNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1023));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnCarryClearCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1024));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnCarryClearNotCalledWhenCarrySet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1024));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnCarrySetCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1025));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnCarrySetNotCalledWhenCarryNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1025));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnNotEqualCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1026));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnNotEqualNotCalledWhenZeroSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1026));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnEqualCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1027));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnEqualNotCalledWhenZeroNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1027));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnOverflowClearCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1028));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnOverflowClearNotCalledWhenOverflowSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1028));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnOverflowSetCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1029));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnOverflowSetNotCalledWhenOverflowNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1029));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnPlusCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102A));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnPlusNotCalledWhenNegativeSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102A));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnMinusCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102B));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnMinusNotCalledWhenNegativeNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102B));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLTCalledCorrectWhenOverflowSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102D));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLTCalledCorrectWhenNegativeSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102D));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }


    @Test
    public void testLongBranchOnLTNotCalledWhenNegativeAndOverflowNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102D));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLTNotCalledWhenNegativeAndOverflowSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102D));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGECalledCorrectWhenOverflowSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102C));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGECalledCorrectWhenNegativeSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102C));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }


    @Test
    public void testLongBranchOnGENotCalledWhenNegativeAndOverflowNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102C));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGENotCalledWhenNegativeAndOverflowSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102C));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGTCalledCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102E));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGTNotCalledIfZeroSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCZero();
        ioSpy.setCCOverflow();
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102E));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnGTNotCalledIfAllSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCZero();
        ioSpy.setCCOverflow();
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102E));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLECalledCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102F));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLECalledIfNotZeroAndOverflow() throws IllegalIndexedPostbyteException {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102F));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLECalledIfNotZeroAndNegative() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102F));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLongBranchOnLENotCalledIfOverflowAndNegative() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x102F));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchLong(new UnsignedWord(0xBEEF));
    }

    @Test
    public void testSWI3PushesAllValues() throws IllegalIndexedPostbyteException {
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
    public void testSWI2PushesAllValues() throws IllegalIndexedPostbyteException {
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
    public void testSWIPushesAllValues() throws IllegalIndexedPostbyteException {
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
    public void testCompareDImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1083));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareDDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1093));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareDIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10A3));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareDExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10B3));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), expectedExtendedWord);
    }

    @Test
    public void testCompareUImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setU(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1183));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareUDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setU(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1193));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareUIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setU(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x11A3));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareUExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setU(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x11B3));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), expectedExtendedWord);
    }

    @Test
    public void testCompareYImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x108C));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareYDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x109C));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareYIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10AC));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareYExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10BC));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), expectedExtendedWord);
    }

    @Test
    public void testCompareSImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setS(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x118C));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareSDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setS(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x119C));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareSIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setS(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x11AC));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareSExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setS(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x11BC));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), expectedExtendedWord);
    }

    @Test
    public void testCompareXImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x8C00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareXDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9C00));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testCompareXIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAC00));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), new UnsignedWord(0x00));
    }

    @Test
    public void testCompareXExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0x10));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xBC));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).compareWord(new UnsignedWord(0x10), expectedExtendedWord);
    }

    @Test
    public void testLoadYImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x108E));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.Y, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadYDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x109E));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).loadRegister(Register.Y, new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testLoadYIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10AE));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).loadRegister(Register.Y, new UnsignedWord());
    }

    @Test
    public void testLoadYExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10BE));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).loadRegister(Register.Y, expectedExtendedWord);
    }

    @Test
    public void testLoadSImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10CE));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.S, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadSDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10DE));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).loadRegister(Register.S, new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testLoadSIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10EE));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).loadRegister(Register.S, new UnsignedWord());
    }

    @Test
    public void testLoadSExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10FE));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).loadRegister(Register.S, expectedExtendedWord);
    }

    @Test
    public void testLoadXImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0x10));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x8E));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.X, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadXDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9E00));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).loadRegister(Register.X, new UnsignedWord(expectedDirectByte, new UnsignedByte(0)));
    }

    @Test
    public void testLoadXIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAE00));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).loadRegister(Register.X, new UnsignedWord());
    }

    @Test
    public void testLoadXExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).loadRegister(Register.X, expectedExtendedWord);
    }

    @Test
    public void testStoreYDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x109F));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).storeWordRegister(Register.Y, new UnsignedWord(0xA000));
    }

    @Test
    public void testStoreYIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10AF));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).storeWordRegister(Register.Y, new UnsignedWord(0xB000));
    }

    @Test
    public void testStoreYExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10BF));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).storeWordRegister(Register.Y, extendedAddress);
    }

    @Test
    public void testStoreXDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9F00));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).storeWordRegister(Register.X, new UnsignedWord(0xA000));
    }

    @Test
    public void testStoreXIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAF00));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).storeWordRegister(Register.X, new UnsignedWord(0xB000));
    }

    @Test
    public void testStoreXExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xBF));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).storeWordRegister(Register.X, extendedAddress);
    }

    @Test
    public void testStoreSDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10DF));
        cpuSpy.executeInstruction();
        verify(ioSpy).getDirect();
        verify(cpuSpy).storeWordRegister(Register.S, new UnsignedWord(0xA000));
    }

    @Test
    public void testStoreSIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10EF));
        cpuSpy.executeInstruction();
        verify(ioSpy).getIndexed();
        verify(cpuSpy).storeWordRegister(Register.S, new UnsignedWord(0xB000));
    }

    @Test
    public void testStoreSExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x10FF));
        ioSpy.writeWord(new UnsignedWord(0x2), extendedAddress);
        cpuSpy.executeInstruction();
        verify(ioSpy).getExtended();
        verify(cpuSpy).storeWordRegister(Register.S, extendedAddress);
    }

    @Test
    public void testDecimalAdditionAdjustCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x19));
        cpuSpy.executeInstruction();
        verify(cpuSpy).decimalAdditionAdjust();
    }

    @Test
    public void testORConditionCodeWorksCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.setCC(new UnsignedByte(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1A01));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0x11), registerSetSpy.getCC());
    }

    @Test
    public void testANDConditionCodeWorksCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.setCC(new UnsignedByte(0x10));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1C11));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0x10), registerSetSpy.getCC());
    }

    @Test
    public void testSignExtendWorksCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0x81));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x1D));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xFF), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0x81), registerSetSpy.getB());
    }

    @Test
    public void testSignExtendWorksCorrectlyWithNoExtension() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0x7F));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x1D));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0x00), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0x7F), registerSetSpy.getB());
    }

    @Test
    public void testExchangeDandX() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setX(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E01));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testExchangeDandY() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E02));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testExchangeDandU() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E03));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testExchangeDandS() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E04));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testExchangeDandPC() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E05));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getD());
    }

    @Test
    public void testExchangeXandY() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E12));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testExchangeXandU() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E13));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testExchangeXandS() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E14));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testExchangeXandPC() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E15));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testExchangeYandU() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E23));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testExchangeYandS() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E24));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testExchangeYandPC() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E25));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testExchangeUandS() throws IllegalIndexedPostbyteException {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E34));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testExchangeUandPC() throws IllegalIndexedPostbyteException {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E35));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testExchangeSandPC() throws IllegalIndexedPostbyteException {
        ioSpy.setS(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E45));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testExchangeAandB() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xDE));
        ioSpy.setB(new UnsignedByte(0xAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E89));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testExchangeAandDP() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xDE));
        ioSpy.setDP(new UnsignedByte(0xAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E8B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testExchangeAandCC() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xDE));
        ioSpy.setCC(new UnsignedByte(0xAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E8A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testExchangeBandCC() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xDE));
        ioSpy.setCC(new UnsignedByte(0xAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E9A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testExchangeBandDP() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xDE));
        ioSpy.setDP(new UnsignedByte(0xAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1E9B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testExchangeCCandDP() throws IllegalIndexedPostbyteException {
        ioSpy.setCC(new UnsignedByte(0xDE));
        ioSpy.setDP(new UnsignedByte(0xAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1EAB));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xAD), registerSetSpy.getCC());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testTransferDtoX() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setX(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F01));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testTransferXtoD() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setD(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F10));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
    }

    @Test
    public void testTransferDtoY() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F02));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testTransferYtoD() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.setD(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F20));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
    }

    @Test
    public void testTransferDtoU() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F03));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testTransferUtoD() throws IllegalIndexedPostbyteException {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.setD(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F30));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
    }

    @Test
    public void testTransferDtoS() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F04));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testTransferStoD() throws IllegalIndexedPostbyteException {
        ioSpy.setS(new UnsignedWord(0xDEAD));
        ioSpy.setD(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F40));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
    }

    @Test
    public void testTransferDtoPC() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F05));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getD());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoD() throws IllegalIndexedPostbyteException {
        ioSpy.setD(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F50));
        cpuSpy.executeInstruction();
        /* PC will have advanced */
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getD());
    }

    @Test
    public void testTransferXtoY() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F12));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testTransferYtoX() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.setX(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F21));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testTransferXtoU() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F13));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testTransferUtoX() throws IllegalIndexedPostbyteException {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.setX(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F31));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testTransferXtoS() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F14));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testTransferStoX() throws IllegalIndexedPostbyteException {
        ioSpy.setS(new UnsignedWord(0xDEAD));
        ioSpy.setX(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F41));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
    }

    @Test
    public void testTransferXtoPC() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F15));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoX() throws IllegalIndexedPostbyteException {
        ioSpy.setX(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F51));
        cpuSpy.executeInstruction();
        /* PC will have advanced */
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getX());
    }

    @Test
    public void testTransferYtoU() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F23));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testTransferUtoY() throws IllegalIndexedPostbyteException {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F32));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testTransferYtoS() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F24));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testTransferStoY() throws IllegalIndexedPostbyteException {
        ioSpy.setS(new UnsignedWord(0xDEAD));
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F42));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
    }

    @Test
    public void testTransferYtoPC() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F25));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoY() throws IllegalIndexedPostbyteException {
        ioSpy.setY(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F52));
        cpuSpy.executeInstruction();
        /* PC will have advanced */
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getY());
    }

    @Test
    public void testTransferUtoS() throws IllegalIndexedPostbyteException {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F34));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
    }

    @Test
    public void testTransferStoU() throws IllegalIndexedPostbyteException {
        ioSpy.setS(new UnsignedWord(0xDEAD));
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F43));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
    }

    @Test
    public void testTransferUtoPC() throws IllegalIndexedPostbyteException {
        ioSpy.setU(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F35));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoU() throws IllegalIndexedPostbyteException {
        ioSpy.setU(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F53));
        cpuSpy.executeInstruction();
        /* PC will have advanced */
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getU());
    }

    @Test
    public void testTransferStoPC() throws IllegalIndexedPostbyteException {
        ioSpy.setS(new UnsignedWord(0xDEAD));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F45));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getS());
        assertEquals(new UnsignedWord(0xDEAD), registerSetSpy.getPC());
    }

    @Test
    public void testTransferPCtoS() throws IllegalIndexedPostbyteException {
        ioSpy.setS(new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F54));
        cpuSpy.executeInstruction();
        /* PC will have advanced */
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getPC());
        assertEquals(new UnsignedWord(0x0002), registerSetSpy.getS());
    }

    @Test
    public void testTransferAtoB() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xDE));
        ioSpy.setB(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F89));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testTransferBtoA() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xDE));
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F98));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testTransferAtoCC() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xDE));
        ioSpy.setCC(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F8A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testTransferCCtoA() throws IllegalIndexedPostbyteException {
        ioSpy.setCC(new UnsignedByte(0xDE));
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FA8));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
    }

    @Test
    public void testTransferAtoDP() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xDE));
        ioSpy.setDP(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F8B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testTransferDPtoA() throws IllegalIndexedPostbyteException {
        ioSpy.setDP(new UnsignedByte(0xDE));
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FB8));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getA());
    }

    @Test
    public void testTransferBtoCC() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xDE));
        ioSpy.setCC(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F9A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testTransferCCtoB() throws IllegalIndexedPostbyteException {
        ioSpy.setCC(new UnsignedByte(0xDE));
        ioSpy.setB(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FA9));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testTransferBtoDP() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xDE));
        ioSpy.setDP(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1F9B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testTransferDPtoB() throws IllegalIndexedPostbyteException {
        ioSpy.setDP(new UnsignedByte(0xDE));
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FB9));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getB());
    }

    @Test
    public void testTransferCCtoDP() throws IllegalIndexedPostbyteException {
        ioSpy.setCC(new UnsignedByte(0xDE));
        ioSpy.setDP(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FAB));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
    }

    @Test
    public void testTransferDPtoCC() throws IllegalIndexedPostbyteException {
        ioSpy.setDP(new UnsignedByte(0xDE));
        ioSpy.setCC(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1FBA));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getDP());
        assertEquals(new UnsignedByte(0xDE), registerSetSpy.getCC());
    }

    @Test
    public void testBranchAlways() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x20EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchNeverDoesNothing() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x21EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnHighCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x22EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnHighNotCalledWhenCarrySet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x22EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnHighNotCalledWhenZeroSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x22EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLowerCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.setCCZero();
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x23EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnHighNotCalledWhenCarryAndZeroNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x23EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnCarryClearCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x24EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnCarryClearNotCalledWhenCarrySet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x24EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnCarrySetCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.setCCCarry();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x25EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnCarrySetNotCalledWhenCarryNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x25EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnNotEqualCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x26EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnNotEqualNotCalledWhenZeroSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x26EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnEqualCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x27EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnEqualNotCalledWhenZeroNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x27EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnOverflowClearCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x28EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnOverflowClearNotCalledWhenOverflowSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x28EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnOverflowSetCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x29EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnOverflowSetNotCalledWhenOverflowNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x29EF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnPlusCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2AEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnPlusNotCalledWhenNegativeSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2AEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnMinusCalledCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2BEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnMinusNotCalledWhenNegativeNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2BEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLTCalledCorrectWhenOverflowSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2DEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLTCalledCorrectWhenNegativeSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2DEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLTNotCalledWhenNegativeAndOverflowNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2DEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLTNotCalledWhenNegativeAndOverflowSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2DEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGECalledCorrectWhenOverflowSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2CEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGECalledCorrectWhenNegativeSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2CEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGENotCalledWhenNegativeAndOverflowNotSet() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2CEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGENotCalledWhenNegativeAndOverflowSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2CEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGTCalledCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2EEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGTNotCalledIfZeroSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2EEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnGTNotCalledIfAllSet() throws IllegalIndexedPostbyteException {
        ioSpy.setCCZero();
        ioSpy.setCCOverflow();
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2EEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLECalledCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.setCCZero();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2FEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLECalledIfNotZeroAndOverflow() throws IllegalIndexedPostbyteException {
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2FEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLECalledIfNotZeroAndNegative() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2FEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testBranchOnLENotCalledIfOverflowAndNegative() throws IllegalIndexedPostbyteException {
        ioSpy.setCCNegative();
        ioSpy.setCCOverflow();
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x2FEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy, never()).branchShort(new UnsignedByte(0xEF));
    }

    @Test
    public void testLoadEffectiveAddressXCalledCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0xA000), new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x309F));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xA000));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadEffectiveAddress(Register.X, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadEffectiveAddressYCalledCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0xA000), new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x319F));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xA000));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadEffectiveAddress(Register.Y, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadEffectiveAddressSCalledCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0xA000), new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x329F));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xA000));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadEffectiveAddress(Register.S, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadEffectiveAddressUCalledCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0xA000), new UnsignedWord(0xBEEF));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x339F));
        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xA000));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadEffectiveAddress(Register.U, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testABXWorksCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0x8));
        ioSpy.setX(new UnsignedWord(0x0020));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3A));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0028), registerSetSpy.getX());
    }

    @Test
    public void testRTSWorksCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.setS(new UnsignedWord(0x0020));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x39));
        ioSpy.writeWord(new UnsignedWord(0x0020), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getPC());
    }

    @Test
    public void testPushSCalledCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3401));
        cpuSpy.executeInstruction();
        verify(cpuSpy).pushStack(Register.S, new UnsignedByte(0x01));
    }

    @Test
    public void testPushUCalledCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3601));
        cpuSpy.executeInstruction();
        verify(cpuSpy).pushStack(Register.U, new UnsignedByte(0x01));
    }

    @Test
    public void testPullSCalledCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3501));
        cpuSpy.executeInstruction();
        verify(cpuSpy).popStack(Register.S, new UnsignedByte(0x01));
    }

    @Test
    public void testPullUCalledCorrectly() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3701));
        cpuSpy.executeInstruction();
        verify(cpuSpy).popStack(Register.U, new UnsignedByte(0x01));
    }

    @Test
    public void testRTIEverything() throws IllegalIndexedPostbyteException {
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
        ioSpy.writeByte(new UnsignedWord(0x9FF4), new UnsignedByte(IOController.CC_E));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedByte(0x1), registerSetSpy.getA());
        assertEquals(new UnsignedByte(0x2), registerSetSpy.getB());
        assertEquals(new UnsignedByte(0x3), registerSetSpy.getDP());
        assertEquals(new UnsignedByte(IOController.CC_E), registerSetSpy.getCC());
        assertEquals(new UnsignedWord(0x5566), registerSetSpy.getX());
        assertEquals(new UnsignedWord(0x7788), registerSetSpy.getY());
        assertEquals(new UnsignedWord(0x99AA), registerSetSpy.getU());
        assertEquals(new UnsignedWord(0xBBCC), registerSetSpy.getPC());
    }

    @Test
    public void testRTIPCOnly() throws IllegalIndexedPostbyteException {
        ioSpy.setS(new UnsignedWord(0x9FF3));
        ioSpy.writeByte(new UnsignedWord(0x9FF5), new UnsignedByte(0xEF));
        ioSpy.writeByte(new UnsignedWord(0x9FF4), new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x9FF3), new UnsignedByte(0x0));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3B));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), registerSetSpy.getPC());
    }
    
    @Test
    public void testCWAIWorksCorrect() throws IllegalIndexedPostbyteException {
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
        assertEquals(new UnsignedByte(0x02), memorySpy.readByte(new UnsignedWord(0x9FFF)));
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
    public void testMULWorksCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0x4B));
        ioSpy.setB(new UnsignedByte(0x0C));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3D));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0384), registerSetSpy.getD());
        assertFalse(ioSpy.ccNegativeSet());
        assertFalse(ioSpy.ccZeroSet());
        assertTrue(ioSpy.ccCarrySet());
    }

    @Test
    public void testMULZeroSetsZero() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0x2));
        ioSpy.setB(new UnsignedByte(0x0));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x3D));
        cpuSpy.executeInstruction();
        assertEquals(new UnsignedWord(0x0), registerSetSpy.getD());
        assertFalse(ioSpy.ccNegativeSet());
        assertTrue(ioSpy.ccZeroSet());
    }

    @Test
    public void testNegateAWorksCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0x1));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x40));
        cpuSpy.executeInstruction();
        verify(cpuSpy).negate(new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0xFF), registerSetSpy.getA());
    }

    @Test
    public void testNegateBWorksCorrect() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0x1));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x50));
        cpuSpy.executeInstruction();
        verify(cpuSpy).negate(new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0xFF), registerSetSpy.getB());
    }

    @Test
    public void testComplimentACalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x43));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compliment(new UnsignedByte(0xAA));
    }

    @Test
    public void testLogicalShiftRightACalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x44));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalShiftRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testRotateRightACalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x46));
        cpuSpy.executeInstruction();
        verify(cpuSpy).rotateRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testArithmeticShiftRightACalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x47));
        cpuSpy.executeInstruction();
        verify(cpuSpy).arithmeticShiftRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testArithmeticShiftLeftACalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x48));
        cpuSpy.executeInstruction();
        verify(cpuSpy).arithmeticShiftLeft(new UnsignedByte(0xAA));
    }

    @Test
    public void testRotateLeftACalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x49));
        cpuSpy.executeInstruction();
        verify(cpuSpy).rotateLeft(new UnsignedByte(0xAA));
    }

    @Test
    public void testDecrementACalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4A));
        cpuSpy.executeInstruction();
        verify(cpuSpy).decrement(new UnsignedByte(0xAA));
    }

    @Test
    public void testIncrementACalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4C));
        cpuSpy.executeInstruction();
        verify(cpuSpy).increment(new UnsignedByte(0xAA));
    }

    @Test
    public void testTestACalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4D));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0xAA));
    }

    @Test
    public void testClearACalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4F));
        cpuSpy.executeInstruction();
        verify(cpuSpy).clear(new UnsignedByte(0xAA));
    }

    @Test
    public void testComplimentBCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x53));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compliment(new UnsignedByte(0xAA));
    }

    @Test
    public void testLogicalShiftRightBCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x54));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalShiftRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testRotateRightBCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x56));
        cpuSpy.executeInstruction();
        verify(cpuSpy).rotateRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testArithmeticShiftRightBCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x57));
        cpuSpy.executeInstruction();
        verify(cpuSpy).arithmeticShiftRight(new UnsignedByte(0xAA));
    }

    @Test
    public void testArithmeticShiftLeftBCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x58));
        cpuSpy.executeInstruction();
        verify(cpuSpy).arithmeticShiftLeft(new UnsignedByte(0xAA));
    }

    @Test
    public void testRotateLeftBCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x59));
        cpuSpy.executeInstruction();
        verify(cpuSpy).rotateLeft(new UnsignedByte(0xAA));
    }

    @Test
    public void testDecrementBCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5A));
        cpuSpy.executeInstruction();
        verify(cpuSpy).decrement(new UnsignedByte(0xAA));
    }

    @Test
    public void testIncrementBCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5C));
        cpuSpy.executeInstruction();
        verify(cpuSpy).increment(new UnsignedByte(0xAA));
    }

    @Test
    public void testTestBCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5D));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0xAA));
    }

    @Test
    public void testClearBCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5F));
        cpuSpy.executeInstruction();
        verify(cpuSpy).clear(new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMACalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x80AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testCompareACalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x81AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMCACalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x82AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMADirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x90));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.A, expectedDirectByte);
    }

    @Test
    public void testCompareADirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x91));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedDirectByte);
    }

    @Test
    public void testSubtractMCADirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x92));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.A, expectedDirectByte);
    }

    @Test
    public void testSubtractMAIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA0));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.A, new UnsignedByte(0x00));
    }

    @Test
    public void testCompareAIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA1));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMCAIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA2));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.A, new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMAExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB0));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.A, expectedExtendedByte);
    }

    @Test
    public void testCompareAExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB1));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedExtendedByte);
    }

    @Test
    public void testSubtractMCAExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB2));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.A, expectedExtendedByte);
    }

    @Test
    public void testSubtractMBCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC0AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testCompareBCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xBE));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC1AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMCBCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC2AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testSubtractMBDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD0));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.B, expectedDirectByte);
    }

    @Test
    public void testCompareBDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD1));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedDirectByte);
    }

    @Test
    public void testSubtractMCBDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD2));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.B, expectedDirectByte);
    }

    @Test
    public void testSubtractMBIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE0));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.B, new UnsignedByte(0x00));
    }

    @Test
    public void testCompareBIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE1));
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMCBIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE2));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.B, new UnsignedByte(0x0));
    }

    @Test
    public void testSubtractMBExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF0));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractM(Register.B, expectedExtendedByte);
    }

    @Test
    public void testCompareBExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xBE));
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF1));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedExtendedByte);
    }

    @Test
    public void testSubtractMCBExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF2));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractMC(Register.B, expectedExtendedByte);
    }

    @Test
    public void testSubtractDImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x83));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractD(new UnsignedWord(0xBEEF));

    }

    @Test
    public void testSubtractDDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x93));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractD(new UnsignedWord(0xBA00));

    }

    @Test
    public void testSubtractDIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA3));
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractD(new UnsignedWord(0x0));
    }

    @Test
    public void testSubtractDExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB3));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).subtractD(expectedExtendedWord);
    }

    @Test
    public void testLogicalAndAImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x84AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testLogicalAndADirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x94));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.A, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testLogicalAndAIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA4));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.A, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testLogicalAndAExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB4));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.A, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testLogicalAndBImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC4AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testLogicalAndBDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD4));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.B, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testLogicalAndBIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE4));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.B, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testLogicalAndBExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF4));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalAnd(Register.B, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testTestAImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(new UnsignedByte(0xAA));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x85AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0xAA));
    }

    @Test
    public void testTestADirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setA(expectedDirectByte);
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9500));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testTestAIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA500));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testTestAExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB5));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(registerSetSpy.getA().getShort() & expectedExtendedByte.getShort()));
        verify(ioSpy).getExtended();
    }

    @Test
    public void testTestBImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(new UnsignedByte(0xAA));
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC5AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0xAA));
    }

    @Test
    public void testTestBDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.setB(expectedDirectByte);
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD500));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testTestBIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE580));
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testTestBExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF5));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).test(new UnsignedByte(registerSetSpy.getB().getShort() & expectedExtendedByte.getShort()));
        verify(ioSpy).getExtended();
    }

    @Test
    public void testLoadAImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x86AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testLoadADirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.A, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testLoadAIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.A, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testLoadAExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB6));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.A, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testLoadBImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC6AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testLoadBDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.B, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testLoadBIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE600));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.B, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testLoadBExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF6));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadByteRegister(Register.B, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testLoadDImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xCC));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.D, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadDDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDC00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.D, new UnsignedWord(expectedDirectByte, new UnsignedByte(0x0)));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testLoadDIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEC00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.D, new UnsignedWord(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testLoadDExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xFC));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.D, expectedExtendedWord);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testLoadUImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xCE));
        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.U, new UnsignedWord(0xBEEF));
    }

    @Test
    public void testLoadUDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDE00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.U, new UnsignedWord(expectedDirectByte, new UnsignedByte(0x0)));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testLoadUIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEE00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.U, new UnsignedWord(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testLoadUExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xFE));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).loadRegister(Register.U, expectedExtendedWord);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testEORAImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x88AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testEORADirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.A, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testEORAIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.A, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testEORAExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB8));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.A, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testEORBImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC8AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testEORBDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.B, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testEORBIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE800));
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.B, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testEORBExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF8));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).exclusiveOr(Register.B, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testADCAImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x89AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testADCADirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.A, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testADCAIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.A, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testADCAExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB9));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.A, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testADCBImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC9AA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testADCBDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.B, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testADCBIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE900));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.B, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testADCBExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF9));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).addWithCarry(Register.B, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testORAImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x8AAA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testORADirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9A00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.A, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testORAIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAA00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.A, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testORAExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xBA));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.A, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testORBImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xCAAA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testORBDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDA00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.B, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testORBIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEA00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.B, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testORBExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xFA));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).logicalOr(Register.B, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testSTADirectCalled() throws IllegalIndexedPostbyteException{
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.A, new UnsignedWord(0xA000));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testSTAIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xA700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.A, new UnsignedWord(0xB000));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testSTAExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB7));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.A, extendedAddress);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testSTBDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.B, new UnsignedWord(0xA000));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testSTBIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE700));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.B, new UnsignedWord(0xB000));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testSTBExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF7));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeByteRegister(Register.B, extendedAddress);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testJSRDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9D00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).jumpToSubroutine(new UnsignedWord(0xA000));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testJSRIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAD80));
        cpuSpy.executeInstruction();
        verify(cpuSpy).jumpToSubroutine(new UnsignedWord(0xB000));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testJSRExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xBD));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).jumpToSubroutine(extendedAddress);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testSTDDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDD00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.D, new UnsignedWord(0xA000));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testSTDIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xED00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.D, new UnsignedWord(0xB000));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testSTDExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xFD));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.D, extendedAddress);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testSTUDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDF00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.U, new UnsignedWord(0xA000));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testSTUIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEF80));
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.U, new UnsignedWord(0xB000));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testSTUExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xFF));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).storeWordRegister(Register.U, extendedAddress);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testADDDDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD300));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addD(new UnsignedWord(0xBA00));
        verify(ioSpy).getDirect();
    }

    @Test
    public void testADDDIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xE380));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addD(new UnsignedWord(0x0000));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testADDDExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF3));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).addD(expectedExtendedWord);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testADDAImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x8BAA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.A, new UnsignedByte(0xAA));
    }

    @Test
    public void testADDADirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x9B00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.A, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testADDAIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xAB00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.A, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testADDAExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xBB));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.A, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }

    @Test
    public void testADDBImmediateCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xCBAA));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.B, new UnsignedByte(0xAA));
    }

    @Test
    public void testADDBDirectCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xDB00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.B, expectedDirectByte);
        verify(ioSpy).getDirect();
    }

    @Test
    public void testADDBIndexedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xEB00));
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.B, new UnsignedByte(0x0));
        verify(ioSpy).getIndexed();
    }

    @Test
    public void testADDBExtendedCalled() throws IllegalIndexedPostbyteException {
        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xFB));
        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
        cpuSpy.executeInstruction();
        verify(cpuSpy).addByteRegister(Register.B, expectedExtendedByte);
        verify(ioSpy).getExtended();
    }
}