/*
 * Copyright (C) 2022-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import static ca.craigthomas.yacoco3e.datatypes.AddressingMode.*;
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
        io.regs.pc.set(0);
    }

    @Test
    public void testNegateCorrect() {
        io.writeByte(0xCAFE, 0xFC);
        ByteInstruction.negate(io, new UnsignedByte(0xFC), new UnsignedWord(0xCAFE));
        assertEquals(0x04, io.readByte(0xCAFE).getShort());
        assertTrue(regs.cc.isMasked(CC_C));
        assertFalse(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testNegateAllOnes() {
        io.writeByte(0xCAFE, 0xFF);
        ByteInstruction.negate(io, new UnsignedByte(0xFF), new UnsignedWord(0xCAFE));
        assertEquals(1, io.readByte(0xCAFE).getShort());
    }

    @Test
    public void testNegateOne() {
        io.writeByte(0xCAFE, 0x1);
        ByteInstruction.negate(io, new UnsignedByte(1), new UnsignedWord(0xCAFE));
        assertEquals(0xFF, io.readByte(0xCAFE).getShort());
    }

    @Test
    public void testNegateSetsOverflowFlag() {
        ByteInstruction.negate(io, new UnsignedByte(1), new UnsignedWord(0xCAFE));
        assertTrue(regs.cc.isMasked(CC_V));
    }

    @Test
    public void testNegateSetsNegativeFlag() {
        ByteInstruction.negate(io, new UnsignedByte(1), new UnsignedWord(0xCAFE));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testNegateEdgeCase() {
        io.writeByte(0xCAFE, 0x80);
        ByteInstruction.negate(io, new UnsignedByte(0x80), new UnsignedWord(0xCAFE));
        assertEquals(0x80, io.readByte(0xCAFE).getShort());
        assertTrue(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testNegateEdgeCase1() {
        io.writeByte(0xCAFE, 0x0);
        ByteInstruction.negate(io, new UnsignedByte(0x0), new UnsignedWord(0xCAFE));
        assertEquals(0, io.readByte(0xCAFE).getShort());
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
        io.writeByte(0xCAFE, 0xE6);
        ByteInstruction.compliment(io, new UnsignedByte(0xE6), new UnsignedWord(0xCAFE));
        assertEquals(0x19, io.readByte(0xCAFE).getShort());
        assertTrue(regs.cc.isMasked(CC_C));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testComplementAllOnes() {
        io.writeByte(0xCAFE, 0xFF);
        ByteInstruction.compliment(io, new UnsignedByte(0xFF), new UnsignedWord(0xCAFE));
        assertEquals(0, io.readByte(0xCAFE).getShort());
    }

    @Test
    public void testComplementOne() {
        io.writeByte(0xCAFE, 1);
        ByteInstruction.compliment(io, new UnsignedByte(0x01), new UnsignedWord(0xCAFE));
        assertEquals(0xFE, io.readByte(0xCAFE).getShort());
    }

    @Test
    public void testComplementSetsCarryFlag() {
        io.writeByte(0xCAFE, 0x01);
        ByteInstruction.compliment(io, new UnsignedByte(0x01), new UnsignedWord(0xCAFE));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testComplementSetsNegativeFlagCorrect() {
        io.writeByte(0xCAFE, 0x01);
        ByteInstruction.compliment(io, new UnsignedByte(0x01), new UnsignedWord(0xCAFE));
        assertTrue(regs.cc.isMasked(CC_N));

        io.writeByte(0xCAFE, 0xFE);
        ByteInstruction.compliment(io, new UnsignedByte(0xFE), new UnsignedWord(0xCAFE));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testComplementSetsZeroFlagCorrect() {
        io.writeByte(0xCAFE, 0xFF);
        ByteInstruction.compliment(io, new UnsignedByte(0xFF), new UnsignedWord(0xCAFE));
        assertEquals(0, io.readByte(0xCAFE).getShort());
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
        io.writeByte(0xCAFE, 0x9E);
        ByteInstruction.logicalShiftRight(io, new UnsignedByte(0x9E), new UnsignedWord(0xCAFE));
        assertEquals(0x4F, io.readByte(0xCAFE).getShort());
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testLogicalShiftRightMovesOneBitCorrect() {
        io.writeByte(0xCAFE, 0x02);
        ByteInstruction.logicalShiftRight(io, new UnsignedByte(0x2), new UnsignedWord(0xCAFE));
        assertEquals(1, io.readByte(0xCAFE).getShort());
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testLogicalShiftRightMovesOneBitToZero() {
        io.writeByte(0xCAFE, 0x01);
        ByteInstruction.logicalShiftRight(io, new UnsignedByte(0x1), new UnsignedWord(0xCAFE));
        assertEquals(0, io.readByte(0xCAFE).getShort());
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testLogicalShiftRightSetsZeroBit() {
        io.writeByte(0xCAFE, 0x01);
        ByteInstruction.logicalShiftRight(io, new UnsignedByte(0x1), new UnsignedWord(0xCAFE));
        assertEquals(0, io.readByte(0xCAFE).getShort());
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
        io.writeByte(0xCAFE, 0x02);
        ByteInstruction.rotateRight(io, new UnsignedByte(0x02), new UnsignedWord(0xCAFE));
        assertEquals(1, io.readByte(0xCAFE).getShort());
        assertFalse(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testRotateRightMovesOneBitCorrectWithCarry() {
        regs.cc.or(CC_C);
        io.writeByte(0xCAFE, 0x02);
        ByteInstruction.rotateRight(io, new UnsignedByte(0x02), new UnsignedWord(0xCAFE));
        assertEquals(0x81, io.readByte(0xCAFE).getShort());
        assertFalse(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testRotateRightMovesOneBitToZero() {
        io.writeByte(0xCAFE, 0x01);
        ByteInstruction.rotateRight(io, new UnsignedByte(0x01), new UnsignedWord(0xCAFE));
        assertEquals(0, io.readByte(0xCAFE).getShort());
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testRotateRightSetsZeroBit() {
        io.writeByte(0xCAFE, 0x01);
        ByteInstruction.rotateRight(io, new UnsignedByte(0x01), new UnsignedWord(0xCAFE));
        assertEquals(0, io.readByte(0xCAFE).getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testRotateRightSetsNegativeBit() {
        regs.cc.or(CC_C);
        io.writeByte(0xCAFE, 0x01);
        ByteInstruction.rotateRight(io, new UnsignedByte(0x01), new UnsignedWord(0xCAFE));
        assertEquals(0x80, io.readByte(0xCAFE).getShort());
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
        io.writeByte(0xCAFE, 0x85);
        ByteInstruction.arithmeticShiftRight(io, new UnsignedByte(0x85), new UnsignedWord(0xCAFE));
        assertEquals(0xC2, io.readByte(0xCAFE).getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftRightOneCorrect() {
        io.writeByte(0xCAFE, 0x1);
        ByteInstruction.arithmeticShiftRight(io, new UnsignedByte(0x1), new UnsignedWord(0xCAFE));
        assertEquals(0, io.readByte(0xCAFE).getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftRightHighBitRetained() {
        io.writeByte(0xCAFE, 0x81);
        ByteInstruction.arithmeticShiftRight(io, new UnsignedByte(0x81), new UnsignedWord(0xCAFE));
        assertEquals(0xC0, io.readByte(0xCAFE).getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftRightHighBitRetainedCarryCleared() {
        io.writeByte(0xCAFE, 0x80);
        ByteInstruction.arithmeticShiftRight(io, new UnsignedByte(0x80), new UnsignedWord(0xCAFE));
        assertEquals(0xC0, io.readByte(0xCAFE).getShort());
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
        io.writeByte(0xCAFE, 0x55);
        ByteInstruction.arithmeticShiftLeft(io, new UnsignedByte(0x55), new UnsignedWord(0xCAFE));
        assertEquals(0xAA, io.readByte(0xCAFE).getShort());
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
        io.writeByte(0xCAFE, 0x8A);
        ByteInstruction.arithmeticShiftLeft(io, new UnsignedByte(0x8A), new UnsignedWord(0xCAFE));
        assertEquals(0x14, io.readByte(0xCAFE).getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftLeftOneCorrect() {
        io.writeByte(0xCAFE, 0x1);
        ByteInstruction.arithmeticShiftLeft(io, new UnsignedByte(0x1), new UnsignedWord(0xCAFE));
        assertEquals(0x2, io.readByte(0xCAFE).getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftLeftHighBitShiftedToCarry() {
        io.writeByte(0xCAFE, 0x81);
        ByteInstruction.arithmeticShiftLeft(io, new UnsignedByte(0x81), new UnsignedWord(0xCAFE));
        assertEquals(0x2, io.readByte(0xCAFE).getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testArithmeticShiftLeftHighBitShiftedToCarryZeroRemainder() {
        io.writeByte(0xCAFE, 0x80);
        ByteInstruction.arithmeticShiftLeft(io, new UnsignedByte(0x80), new UnsignedWord(0xCAFE));
        assertEquals(0x0, io.readByte(0xCAFE).getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testRotateLeftOneCorrect() {
        io.writeByte(0xCAFE, 0x1);
        ByteInstruction.rotateLeft(io, new UnsignedByte(0x1), new UnsignedWord(0xCAFE));
        assertEquals(0x2, io.readByte(0xCAFE).getShort());
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
        io.writeByte(0xCAFE, 0x80);
        ByteInstruction.rotateLeft(io, new UnsignedByte(0x80), new UnsignedWord(0xCAFE));
        assertEquals(0x0, io.readByte(0xCAFE).getShort());
        assertTrue(io.regs.cc.isMasked(CC_C));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testRotateLeftRotatesCarryToLowestBit() {
        io.regs.cc.or(CC_C);
        io.writeByte(0xCAFE, 0x1);
        ByteInstruction.rotateLeft(io, new UnsignedByte(0x1), new UnsignedWord(0xCAFE));
        assertEquals(0x3, io.readByte(0xCAFE).getShort());
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testRotateLeftClearsOverflow() {
        io.writeByte(0xCAFE, 0xC);
        ByteInstruction.rotateLeft(io, new UnsignedByte(0xC0), new UnsignedWord(0xCAFE));
        assertEquals(0x80, io.readByte(0xCAFE).getShort());
        assertTrue(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testDecrementWorksCorrectly() {
        io.writeByte(0xCAFE, 0xC4);
        ByteInstruction.decrement(io, new UnsignedByte(0xC4), new UnsignedWord(0xCAFE));
        assertEquals(0xC3, io.readByte(0xCAFE).getShort());
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
        io.writeByte(0xCAFE, 0x1);
        ByteInstruction.decrement(io, new UnsignedByte(0x1), new UnsignedWord(0xCAFE));
        assertEquals(0x0, io.readByte(0xCAFE).getShort());
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testDecrementZeroCorrect() {
        io.writeByte(0xCAFE, 0x0);
        ByteInstruction.decrement(io, new UnsignedByte(0), new UnsignedWord(0xCAFE));
        assertEquals(0xFF, io.readByte(0xCAFE).getShort());
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testDecrementHighValueCorrect() {
        io.writeByte(0xCAFE, 0xFF);
        ByteInstruction.decrement(io, new UnsignedByte(0xFF), new UnsignedWord(0xCAFE));
        assertEquals(0xFE, io.readByte(0xCAFE).getShort());
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testIncrementOneCorrect() {
        io.writeByte(0xCAFE, 0x1);
        ByteInstruction.increment(io, new UnsignedByte(0x1), new UnsignedWord(0xCAFE));
        assertEquals(0x2, io.readByte(0xCAFE).getShort());
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testIncrementDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        io.writeWord(0x0000, 0x0C0A);
        io.writeByte(0x000A, 0xC4);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC5), io.readByte(0x000A));
    }

    @Test
    public void testIncrementIndexedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x6C);
        regs.x.set(extendedAddress);
        io.writeByte(extendedAddress, 0xC4);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC5), io.readByte(extendedAddress));
    }

    @Test
    public void testIncrementExtendedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x7C);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0xC4);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC5), io.readByte(extendedAddress));
    }

    @Test
    public void testIncrementSetsOverflow() {
        io.writeByte(0xCAFE, 0x7F);
        ByteInstruction.increment(io, new UnsignedByte(0x7F), new UnsignedWord(0xCAFE));
        assertEquals(0x80, io.readByte(0xCAFE).getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testIncrementSetsZero() {
        io.writeByte(0xCAFE, 0xFF);
        ByteInstruction.increment(io, new UnsignedByte(0xFF), new UnsignedWord(0xCAFE));
        assertEquals(0x0, io.readByte(0xCAFE).getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testTestZeroCorrect() {
        ByteInstruction.testByte(io, new UnsignedByte(0), null);
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
        ByteInstruction.testByte(io, new UnsignedByte(0x81), null);
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testClearWorksCorrect() {
        io.writeByte(0xCAFE, 0xFF);
        ByteInstruction.clear(io, new UnsignedByte(0x1), new UnsignedWord(0xCAFE));
        assertEquals(0, io.readByte(0xCAFE).getShort());
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

    @Test
    public void testGetDirectReadsAddressFromDPAndPC() {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        io.writeByte(0x00BE, 0xCD);
        io.writeByte(0xABCD, 0xFF);
        regs.pc.set(0xBE);
        regs.dp.set(0xAB);
        instruction.getDirect(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xABCD, instruction.addressRead.getInt());
        assertEquals(0xFF, io.readByte(0xABCD).getShort());
    }


    @Test
    public void testGetImmediateReadsAddressFromPC() {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        io.writeByte(0xBEEE, 0xAB);
        regs.pc.set(0xBEEE);
        instruction.getImmediate(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xAB, instruction.byteRead.getShort());
        assertEquals(0xBEEE, instruction.addressRead.getInt());
    }

    @Test
    public void testGetImmediateWordReadsAddressFromPC() {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        instruction.isByteSized = false;
        io.writeWord(0xBEEE, 0xABCD);
        regs.pc.set(0xBEEE);
        instruction.getImmediate(io);
        assertEquals(2, instruction.numBytesRead);
        assertEquals(0xABCD, instruction.wordRead.getInt());
        assertEquals(0xBEEE, instruction.addressRead.getInt());
    }

    @Test
    public void testGetExtendedWorksCorrectly() {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", EXTENDED, ByteInstruction::clear);
        instruction.isByteSized = false;
        io.writeWord(0xBEEE, 0xABCD);
        io.writeWord(0xABCD, 0xDEAD);
        regs.pc.set(0xBEEE);
        instruction.getExtended(io);
        assertEquals(2, instruction.numBytesRead);
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0xABCD, instruction.addressRead.getInt());
    }

//    @Test
//    public void testGetIndexedZeroOffset() throws MalformedInstructionException {
//        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
//        instruction.getIndexed(io);
//        regs.x.set(0xB000);
//        io.writeByte(0x0000, 0x84);
//        assertEquals(0xB000, instruction.byteRead.getShort());
//    }
//
//    @Test
//    public void testGetIndexedZeroOffsetIndirect() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeWord(new UnsignedWord(0xB000), new UnsignedWord(0xBEEF));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x94));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexed5BitPositiveOffset() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x01));
//        assertEquals(new UnsignedWord(0xB001), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexed5BitNegativeOffset() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x1F));
//        assertEquals(new UnsignedWord(0xAFFF), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedRPostIncrement() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x80));
//        assertEquals(new UnsignedWord(0xB000), io.getIndexed().value);
//        assertEquals(new UnsignedWord(0xB001), regs.x);
//    }
//
//    @Test
//    public void testGetIndexedRPostIncrementTwice() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x81));
//        assertEquals(new UnsignedWord(0xB000), io.getIndexed().value);
//        assertEquals(new UnsignedWord(0xB002), regs.x);
//    }
//
//    @Test
//    public void testGetIndexedRPostIncrementTwiceIndirect() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeWord(new UnsignedWord(0xB000), new UnsignedWord(0xBEEF));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x91));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//        assertEquals(new UnsignedWord(0xB002), regs.x);
//    }
//
//    @Test
//    public void testGetIndexedRDecrement() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x82));
//        assertEquals(new UnsignedWord(0xAFFF), io.getIndexed().value);
//        assertEquals(new UnsignedWord(0xAFFF), regs.x);
//    }
//
//    @Test
//    public void testGetIndexedRDecrementTwice() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x83));
//        assertEquals(new UnsignedWord(0xAFFE), io.getIndexed().value);
//        assertEquals(new UnsignedWord(0xAFFE), regs.x);
//    }
//
//    @Test
//    public void testGetIndexedRDecrementTwiceIndirect() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeWord(new UnsignedWord(0xAFFE), new UnsignedWord(0xBEEF));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x93));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//        assertEquals(new UnsignedWord(0xAFFE), regs.x);
//    }
//
//    @Test
//    public void testGetIndexedRWithBOffset() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        regs.b.set(new UnsignedByte(0x0B));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x85));
//        assertEquals(new UnsignedWord(0xB00B), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedRWithBOffsetIndirect() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        regs.b.set(new UnsignedByte(0x0B));
//        io.writeWord(new UnsignedWord(0xB00B), new UnsignedWord(0xBEEF));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x95));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedRWithAOffset() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        regs.a.set(new UnsignedByte(0x0A));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x86));
//        assertEquals(new UnsignedWord(0xB00A), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedRWithAOffsetIndirect() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        regs.a.set(new UnsignedByte(0x0A));
//        io.writeWord(new UnsignedWord(0xB00A), new UnsignedWord(0xBEEF));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x96));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexed8BitPositiveOffset() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x8802));
//        assertEquals(new UnsignedWord(0xB002), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexed8BitNegativeOffset() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x88FE));
//        assertEquals(new UnsignedWord(0xAFFE), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexed8BitPositiveOffsetIndirect() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeWord(new UnsignedWord(0xB002), new UnsignedWord(0xBEEF));
//        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x9802));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexed8BitNegativeOffsetIndirect() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeWord(new UnsignedWord(0xAFFE), new UnsignedWord(0xBEEF));
//        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x98FE));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexed16BitPositiveOffset() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x89));
//        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0x0200));
//        assertEquals(new UnsignedWord(0xB200), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexed16BitNegativeOffset() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x89));
//        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0xFE00));
//        assertEquals(new UnsignedWord(0xAE00), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexed16BitPositiveOffsetIndirect() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeWord(new UnsignedWord(0xB200), new UnsignedWord(0xBEEF));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x99));
//        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0x0200));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexed16BitNegativeOffsetIndirect() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeWord(new UnsignedWord(0xAE00), new UnsignedWord(0xBEEF));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x99));
//        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0xFE00));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedRWithDOffset() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        regs.setD(new UnsignedWord(0x0200));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x8B));
//        assertEquals(new UnsignedWord(0xB200), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedRWithDOffsetIndirect() throws MalformedInstructionException {
//        regs.x.set(new UnsignedWord(0xB000));
//        regs.setD(new UnsignedWord(0x0200));
//        io.writeWord(new UnsignedWord(0xB200), new UnsignedWord(0xBEEF));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x9B));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedPCWith8BitPositiveOffset() throws MalformedInstructionException {
//        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x8C0A));
//        assertEquals(new UnsignedWord(0x000C), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedPCWith8BitNegativeOffset() throws MalformedInstructionException {
//        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x8CFC));
//        assertEquals(new UnsignedWord(0xFFFE), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedPCWith8BitPositiveOffsetIndexed() throws MalformedInstructionException {
//        io.writeWord(new UnsignedWord(0x000C), new UnsignedWord(0xBEEF));
//        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x9C0A));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedPCWith8BitNegativeOffsetIndexed() throws MalformedInstructionException {
//        memory.rom = new short [0x4000];
//        memory.rom[0x3FFC] = (short) 0xBE;
//        memory.rom[0x3FFD] = (short) 0xEF;
//
//        io.writeWord(new UnsignedWord(0x0000), new UnsignedWord(0x9CFA));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedPCWith16BitPositiveOffset() throws MalformedInstructionException {
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x8D));
//        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0x0200));
//        assertEquals(new UnsignedWord(0x0203), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedPCWith16BitNegativeOffset() throws MalformedInstructionException {
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x8D));
//        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0xFE00));
//        assertEquals(new UnsignedWord(0xFE03), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedPCWith16BitPositiveOffsetIndexed() throws MalformedInstructionException {
//        io.writeWord(new UnsignedWord(0x0203), new UnsignedWord(0xBEEF));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x9D));
//        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0x0200));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedPCWith16BitNegativeOffsetIndexed() throws MalformedInstructionException {
//        io.writeWord(new UnsignedWord(0xFE03), new UnsignedWord(0xBEEF));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x9D));
//        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0xFE00));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//    }
//
//    @Test
//    public void testGetIndexedExtendedIndirect() throws MalformedInstructionException {
//        io.writeWord(new UnsignedWord(0xB000), new UnsignedWord(0xBEEF));
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x9F));
//        io.writeWord(new UnsignedWord(0x0001), new UnsignedWord(0xB000));
//        assertEquals(new UnsignedWord(0xBEEF), io.getIndexed().value);
//    }
//
//    @Test(expected = MalformedInstructionException.class)
//    public void testGetIndexedIllegalPostByteExceptionOnRPostIncrement() throws MalformedInstructionException {
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x90));
//        io.getIndexed();
//    }
//
//    @Test(expected = MalformedInstructionException.class)
//    public void testGetIndexedIllegalPostByteExceptionOnRPostDecrement() throws MalformedInstructionException {
//        io.writeByte(new UnsignedWord(0x0000), new UnsignedByte(0x92));
//        io.getIndexed();
//    }
}
