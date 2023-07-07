/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;
import static org.junit.Assert.*;

import ca.craigthomas.yacoco3e.datatypes.*;
import org.junit.Before;
import org.junit.Test;

public class ByteInstructionTest {
    private IOController io;
    private RegisterSet regs;
    private CPU cpu;
    private int extendedAddress;

    @Before
    public void setUp() {
        Screen screen = new Screen(1);
        Cassette cassette = new Cassette();
        regs = new RegisterSet();
        io = new IOController(new Memory(), regs, new EmulatedKeyboard(), screen, cassette);
        cpu = new CPU(io);
        extendedAddress = 0xC0A0;
    }

    @Test
    public void testNegateCorrect() {
        UnsignedByte result = ByteInstruction.negate(new InstructionBundle(io, 0xFC));
        assertEquals(new UnsignedByte(0x04), result);
        assertTrue(regs.cc.isMasked(CC_C));
        assertFalse(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testNegateAllOnes() {
        UnsignedByte result = ByteInstruction.negate(new InstructionBundle(io, 0xFF));
        assertEquals(new UnsignedByte(1), result);
    }

    @Test
    public void testNegateOne() {
        UnsignedByte result = ByteInstruction.negate(new InstructionBundle(io, 0x01));
        assertEquals(new UnsignedByte(0xFF), result);
    }

    @Test
    public void testNegateSetsOverflowFlag() {
        ByteInstruction.negate(new InstructionBundle(io, 0x01));
        assertTrue(regs.cc.isMasked(CC_V));
    }

    @Test
    public void testNegateSetsNegativeFlag() {
        ByteInstruction.negate(new InstructionBundle(io, 0x01));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testNegateEdgeCase() {
        UnsignedByte result = ByteInstruction.negate(new InstructionBundle(io, 0x80));
        assertEquals(new UnsignedByte(0x80), result);
        assertTrue(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testNegateEdgeCase1() {
        UnsignedByte result = ByteInstruction.negate(new InstructionBundle(io, 0x0));
        assertEquals(new UnsignedByte(0x0), result);
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testNegateDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        io.writeByte(0x0000, 0x00);
        io.writeByte(0x0001, 0x0A);
        io.writeByte(0x000A, 0x55);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xAB), io.readByte(0x000A));
        assertTrue(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testNegateIndexedCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x6080);
        regs.x.set(extendedAddress);
        io.writeWord(extendedAddress, 0x0200);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xFE), io.readByte(extendedAddress));
        assertTrue(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testNegateExtendedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x70);
        io.writeWord(0x0001, extendedAddress);
        io.writeWord(extendedAddress, 0x0100);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xFF), io.readByte(extendedAddress));
        assertTrue(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testComplimentWorksCorrectly() {
        UnsignedByte result = ByteInstruction.compliment(new InstructionBundle(io, 0xE6));
        assertEquals(new UnsignedByte(0x19), result);
        assertTrue(regs.cc.isMasked(CC_C));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testComplementAllOnes() {
        UnsignedByte result = ByteInstruction.compliment(new InstructionBundle(io, 0xFF));
        assertEquals(new UnsignedByte(0), result);
    }

    @Test
    public void testComplementOne() {
        UnsignedByte result = ByteInstruction.compliment(new InstructionBundle(io, 0x01));
        assertEquals(new UnsignedByte(0xFE), result);
    }

    @Test
    public void testComplementSetsCarryFlag() {
        ByteInstruction.compliment(new InstructionBundle(io, 0x01));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testComplementSetsNegativeFlagCorrect() {
        ByteInstruction.compliment(new InstructionBundle(io, 0x01));
        assertTrue(regs.cc.isMasked(CC_N));

        ByteInstruction.compliment(new InstructionBundle(io, 0xFE));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testComplementSetsZeroFlagCorrect() {
        UnsignedByte result = ByteInstruction.compliment(new InstructionBundle(io, 0xFF));
        assertEquals(0, result.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testComplementDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        io.writeWord(0x0000, 0x030A);
        io.writeByte(0x000A, 0x55);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xAA), io.readByte(0x000A));
        assertTrue(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testComplementIndexedCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x6380);
        regs.x.set(extendedAddress);
        io.writeByte(extendedAddress, 0x02);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xFD), io.readByte(extendedAddress));
    }

    @Test
    public void testComplementExtendedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x73);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x02);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xFD), io.readByte(extendedAddress));
        assertTrue(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalShiftRightCorrect() {
        UnsignedByte result = ByteInstruction.logicalShiftRight(new InstructionBundle(io, 0x9E));
        assertEquals(new UnsignedByte(0x4F), result);
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testLogicalShiftRightMovesOneBitCorrect() {
        UnsignedByte result = ByteInstruction.logicalShiftRight(new InstructionBundle(io, 0x2));
        assertEquals(new UnsignedByte(1), result);
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testLogicalShiftRightMovesOneBitToZero() {
        UnsignedByte result = ByteInstruction.logicalShiftRight(new InstructionBundle(io, 0x1));
        assertEquals(new UnsignedByte(0), result);
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testLogicalShiftRightSetsZeroBit() {
        UnsignedByte result = ByteInstruction.logicalShiftRight(new InstructionBundle(io, 0x1));
        assertEquals(new UnsignedByte(0), result);
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testLogicalShiftRightDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        io.writeWord(0x0000, 0x040A);
        io.writeByte(0x000A, 0x55);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x2A), io.readByte(0x000A));
    }

    @Test
    public void testLogicalShiftRightIndexedCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x6480);
        regs.x.set(extendedAddress);
        io.writeByte(extendedAddress, 0x55);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x2A), io.readByte(extendedAddress));
    }

    @Test
    public void testLogicalShiftRightExtendedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x74);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x55);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x2A), io.readByte(extendedAddress));
    }

    @Test
    public void testRotateRightMovesOneBitCorrect() {
        UnsignedByte result = ByteInstruction.rotateRight(new InstructionBundle(io, 0x2));
        assertEquals(new UnsignedByte(1), result);
        assertFalse(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testRotateRightMovesOneBitCorrectWithCarry() {
        regs.cc.or(CC_C);
        UnsignedByte result = ByteInstruction.rotateRight(new InstructionBundle(io, 0x2));
        assertEquals(new UnsignedByte(0x81), result);
        assertFalse(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testRotateRightMovesOneBitToZero() {
        UnsignedByte result = ByteInstruction.rotateRight(new InstructionBundle(io, 0x1));
        assertEquals(new UnsignedByte(0), result);
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testRotateRightSetsZeroBit() {
        UnsignedByte result = ByteInstruction.rotateRight(new InstructionBundle(io, 0x1));
        assertEquals(new UnsignedByte(0), result);
        assertTrue(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testRotateRightSetsNegativeBit() {
        regs.cc.or(CC_C);
        UnsignedByte result = ByteInstruction.rotateRight(new InstructionBundle(io, 0x1));
        assertEquals(new UnsignedByte(0x80), result);
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testRotateRightDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        io.writeWord(0x0000, 0x060A);
        io.writeByte(0x000A, 0x55);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x2A), io.readByte(0x000A));
    }

    @Test
    public void testRotateRightIndexedCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x6680);
        regs.x.set(extendedAddress);
        io.writeByte(extendedAddress, 0x55);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x2A), io.readByte(extendedAddress));
    }

    @Test
    public void testRotateRightExtendedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x76);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x55);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x2A), io.readByte(extendedAddress));
    }

    @Test
    public void testArithmeticShiftRightCorrect() {
        UnsignedByte result = ByteInstruction.arithmeticShiftRight(new InstructionBundle(io, 0x85));
        assertEquals(0xC2, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftRightOneCorrect() {
        UnsignedByte result = ByteInstruction.arithmeticShiftRight(new InstructionBundle(io, 0x1));
        assertEquals(0, result.getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftRightHighBitRetained() {
        UnsignedByte result = ByteInstruction.arithmeticShiftRight(new InstructionBundle(io, 0x81));
        assertEquals(0xC0, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftRightHighBitRetainedCarryCleared() {
        UnsignedByte result = ByteInstruction.arithmeticShiftRight(new InstructionBundle(io, 0x80));
        assertEquals(0xC0, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftRightDirectCalled() throws MalformedInstructionException {
        regs.dp.set(0x00);
        io.writeWord(0x0000, 0x070A);
        io.writeByte(0x000A, 0x85);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC2), io.readByte(0x000A));
    }

    @Test
    public void testArithmeticShiftRightIndexedCalled() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x67);
        regs.x.set(extendedAddress);
        io.writeByte(extendedAddress, 0x85);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC2), io.readByte(extendedAddress));
    }

    @Test
    public void testArithmeticShiftRightExtendedCalled() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x77);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x85);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC2), io.readByte(extendedAddress));
    }

    @Test
    public void testArithmeticShiftLeftCorrect() {
        UnsignedByte result = ByteInstruction.arithmeticShiftLeft(new InstructionBundle(io, 0x55));
        assertEquals(0xAA, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftLeftDirectCalled() throws MalformedInstructionException {
        regs.dp.set(0x00);
        io.writeWord(0x0000, 0x080A);
        io.writeByte(0x000A, 0x55);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xAA), io.readByte(0x000A));
    }

    @Test
    public void testArithmeticShiftLeftIndexedCalled() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x68);
        regs.x.set(extendedAddress);
        io.writeByte(extendedAddress, 0x55);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xAA), io.readByte(extendedAddress));
    }

    @Test
    public void testArithmeticShiftLeftExtendedCalled() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x78);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x55);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xAA), io.readByte(extendedAddress));
    }

    @Test
    public void testArithmeticShiftLeftCorrectSecondTest() {
        UnsignedByte result = ByteInstruction.arithmeticShiftLeft(new InstructionBundle(io, 0x8A));
        assertEquals(0x14, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftLeftOneCorrect() {
        UnsignedByte result = ByteInstruction.arithmeticShiftLeft(new InstructionBundle(io, 0x1));
        assertEquals(0x2, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftLeftHighBitShiftedToCarry() {
        UnsignedByte result = ByteInstruction.arithmeticShiftLeft(new InstructionBundle(io, 0x81));
        assertEquals(0x2, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftLeftHighBitShiftedToCarryZeroRemainder() {
        UnsignedByte result = ByteInstruction.arithmeticShiftLeft(new InstructionBundle(io, 0x80));
        assertEquals(0x0, result.getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testRotateLeftOneCorrect() {
        UnsignedByte result = ByteInstruction.rotateLeft(new InstructionBundle(io, 0x1));
        assertEquals(0x2, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testRotateLeftDirectCalled() throws MalformedInstructionException {
        regs.dp.set(0x00);
        io.writeWord(0x0000, 0x090A);
        io.writeByte(0x000A, 0x01);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x02), io.readByte(0x000A));
    }

    @Test
    public void testRotateLeftIndexedCalled() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x69);
        regs.x.set(extendedAddress);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x02), io.readByte(extendedAddress));
    }

    @Test
    public void testRotateLeftExtendedCalled() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x79);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x02), io.readByte(extendedAddress));
    }

    @Test
    public void testRotateLeftSetsCarry() {
        UnsignedByte result = ByteInstruction.rotateLeft(new InstructionBundle(io, 0x80));
        assertEquals(0x0, result.getShort());
        assertTrue(io.regs.cc.isMasked(CC_C));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testRotateLeftRotatesCarryToLowestBit() {
        io.regs.cc.or(CC_C);
        UnsignedByte result = ByteInstruction.rotateLeft(new InstructionBundle(io, 0x1));
        assertEquals(0x3, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testRotateLeftClearsOverflow() {
        UnsignedByte result = ByteInstruction.rotateLeft(new InstructionBundle(io, 0xC0));
        assertEquals(0x80, result.getShort());
        assertTrue(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testDecrementWorksCorrectly() {
        UnsignedByte result = ByteInstruction.decrement(new InstructionBundle(io, 0xC4));
        assertEquals(0xC3, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testDecrementDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        io.writeWord(0x0000, 0x0A0A);
        io.writeByte(0x000A, 0xC4);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC3), io.readByte(0x000A));
    }

    @Test
    public void testDecrementIndexedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x6A);
        regs.x.set(extendedAddress);
        io.writeByte(extendedAddress, 0xC4);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC3), io.readByte(extendedAddress));
    }

    @Test
    public void testDecrementExtendedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x7A);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0xC4);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC3), io.readByte(extendedAddress));
    }

    @Test
    public void testDecrementOneCorrect() {
        UnsignedByte result = ByteInstruction.decrement(new InstructionBundle(io, 0x1));
        assertEquals(0x0, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testDecrementZeroCorrect() {
        UnsignedByte result = ByteInstruction.decrement(new InstructionBundle(io, 0x0));
        assertEquals(0xFF, result.getShort());
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testDecrementHighValueCorrect() {
        UnsignedByte result = ByteInstruction.decrement(new InstructionBundle(io, 0xFF));
        assertEquals(0xFE, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testIncrementOneCorrect() {
        UnsignedByte result = ByteInstruction.increment(new InstructionBundle(io, 0x1));
        assertEquals(0x2, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testIncrememtDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        io.writeWord(0x0000, 0x0C0A);
        io.writeByte(0x000A, 0xC4);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC5), io.readByte(0x000A));
    }

    @Test
    public void testIncrememtIndexedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x6C);
        regs.x.set(extendedAddress);
        io.writeByte(extendedAddress, 0xC4);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC5), io.readByte(extendedAddress));
    }

    @Test
    public void testIncrememtExtendedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x7C);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0xC4);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC5), io.readByte(extendedAddress));
    }

    @Test
    public void testIncrementSetsOverflow() {
        UnsignedByte result = ByteInstruction.increment(new InstructionBundle(io, 0x7F));
        assertEquals(0x80, result.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testIncrementSetsZero() {
        UnsignedByte result = ByteInstruction.increment(new InstructionBundle(io, 0xFF));
        assertEquals(0x0, result.getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testTestZeroCorrect() {
        ByteInstruction.testByte(new InstructionBundle(io, 0x0, 0x0));
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testTestDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        io.writeWord(0x0000, 0x0D0A);
        io.writeByte(0x000A, 0x80);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x80), io.readByte(0x000A));
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testTestIndexedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x6D);
        regs.x.set(extendedAddress);
        io.writeByte(extendedAddress, 0x80);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x80), io.readByte(extendedAddress));
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testTestExtendedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x7D);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x80);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x80), io.readByte(extendedAddress));
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testTestNegativeCorrect() {
        ByteInstruction.testByte(new InstructionBundle(io, 0x81, 0x81));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testClearWorksCorrect() {
        UnsignedByte result = ByteInstruction.clear(new InstructionBundle(io, 0x4));
        assertEquals(0, result.getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testClearDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        io.writeWord(0x0000, 0x0F0A);
        io.writeByte(0x000A, 0x80);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x00), io.readByte(0x000A));
    }

    @Test
    public void testClearIndexedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x6F);
        regs.x.set(extendedAddress);
        io.writeByte(extendedAddress, 0x80);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x00), io.readByte(extendedAddress));
    }

    @Test
    public void testClearExtendedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x7F);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x80);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x00), io.readByte(extendedAddress));
    }



//    @Test
//    public void testAddByteWorksCorrectlyByCall() {
//        Instruction instruction = new ByteRegisterInstruction(0x0, 5, "ADDA", DIRECT, 0, ByteInstruction::addByte, Register.A);
//        MemoryResult memoryResult = new MemoryResult(5, new UnsignedWord(0x1100));
//        io.regs.a.set(new UnsignedByte(0x11));
//        instruction.call(memoryResult, io);
//        assertEquals(new UnsignedByte(0x22), io.regs.a);
//        assertFalse(io.regs.cc.isMasked(CC_Z));
//        assertFalse(io.regs.cc.isMasked(CC_N));
//    }


//    @Test
//    public void testDecimalAdditionAdjustWorksCorrectly() {
//        registerSet.setA(new UnsignedByte(0x55));
//        cpu.addWithCarry(Register.A, new UnsignedByte(0x17));
//        assertEquals(new UnsignedByte(0x6C), registerSet.getA());
//        cpu.decimalAdditionAdjust();
//        assertEquals(new UnsignedByte(0x72), registerSet.getA());
//        assertFalse(io.regs.cc.isMasked(CC_C));
//    }
//
//    @Test
//    public void testDecimalAdditionAdjustWorksCorrectlyWhenCarrySet() {
//        registerSet.setA(new UnsignedByte(0x55));
//        cpu.addWithCarry(Register.A, new UnsignedByte(0x17));
//        io.setCCCarry();
//        assertEquals(new UnsignedByte(0x6C), registerSet.getA());
//        cpu.decimalAdditionAdjust();
//        assertEquals(new UnsignedByte(0xD2), registerSet.getA());
//        assertTrue(io.regs.cc.isMasked(CC_C));
//    }
}
