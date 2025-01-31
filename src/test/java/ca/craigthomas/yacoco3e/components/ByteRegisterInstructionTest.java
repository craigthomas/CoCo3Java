/*
 * Copyright (C) 2023-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;
import org.junit.Before;
import org.junit.Test;

import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;
import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.CC_Z;
import static org.junit.Assert.*;

public class ByteRegisterInstructionTest {
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

    @Test(expected = MalformedInstructionException.class)
    public void testByteRegisterInstructionThrowsRuntimeExceptionWithUnsupportedRegister() throws MalformedInstructionException {
        ByteRegisterInstruction instruction = new ByteRegisterInstruction(0, 0, "None", AddressingMode.INHERENT, null, Register.PC);
        instruction.call(io);
    }

    @Test
    public void testORConditionCodeWorksCorrectly() throws MalformedInstructionException {
        regs.cc.set(0x10);
        io.writeWord(0x00, 0x1A01);
        cpu.executeInstruction();
        assertEquals(0x11, regs.cc.getShort());
    }

    @Test
    public void testORConditionCodeWorksCorrectly1() throws MalformedInstructionException {
        regs.cc.set(0x22);
        io.writeWord(0x0000, 0x1A50);
        cpu.executeInstruction();
        assertEquals(0x72, regs.cc.getShort());
    }

    @Test
    public void testANDConditionCodeWorksCorrectly() throws MalformedInstructionException {
        regs.cc.set(0x10);
        io.writeWord(0x0000, 0x1C11);
        cpu.executeInstruction();
        assertEquals(0x10, regs.cc.getShort());
    }

    @Test
    public void testANDConditionCodeWorksCorrectly1() throws MalformedInstructionException {
        regs.cc.set(0x11);
        io.writeWord(0x0000, 0x1C11);
        cpu.executeInstruction();
        assertEquals(0x11, regs.cc.getShort());
    }

    @Test
    public void testSignExtendWorksCorrectly() throws MalformedInstructionException {
        regs.b.set(0x81);
        io.writeByte(0x0000, 0x1D);
        cpu.executeInstruction();
        assertEquals(0xFF, regs.a.getShort());
        assertEquals(0x81, regs.b.getShort());
    }

    @Test
    public void testSignExtendWorksCorrectlyWithNoExtension() throws MalformedInstructionException {
        regs.b.set(0x7F);
        io.writeByte(0x0000, 0x1D);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertEquals(0x7F, regs.b.getShort());
    }

    @Test
    public void testNegateAIntegration() throws MalformedInstructionException {
        regs.a.set(0x01);
        io.writeByte(0x0000, 0x40);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xFF), regs.a);
    }

    @Test
    public void testNegateACorrect() {
        regs.a.set(0xFC);
        ByteRegisterInstruction.negate(io, regs.a, null, null);
        assertEquals(0x04, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_C));
        assertFalse(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testNegateAAllOnes() {
        regs.a.set(0xFF);
        ByteRegisterInstruction.negate(io, regs.a, null, null);
        assertEquals(1, regs.a.getShort());
    }

    @Test
    public void testNegateAOne() {
        regs.a.set(1);
        ByteRegisterInstruction.negate(io, regs.a, null, null);
        assertEquals(0xFF, regs.a.getShort());
    }

    @Test
    public void testNegateASetsOverflowFlag() {
        regs.a.set(1);
        ByteRegisterInstruction.negate(io, regs.a, null, null);
        assertTrue(regs.cc.isMasked(CC_V));
    }

    @Test
    public void testNegateASetsNegativeFlag() {
        regs.a.set(1);
        ByteRegisterInstruction.negate(io, regs.a, null, null);
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testNegateAEdgeCase() {
        regs.a.set(0x80);
        ByteRegisterInstruction.negate(io, regs.a, null, null);
        assertEquals(0x80, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testNegateAEdgeCase1() {
        regs.a.set(0);
        ByteRegisterInstruction.negate(io, regs.a, null, null);
        assertEquals(0, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testComplimentAWorksCorrectly() throws MalformedInstructionException {
        regs.a.set(0xAA);
        io.writeByte(0x0000, 0x43);
        cpu.executeInstruction();
        assertEquals(0x55, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_C));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testComplementAAllOnes() {
        regs.a.set(0xFF);
        ByteRegisterInstruction.compliment(io, regs.a, null, null);
        assertEquals(0, regs.a.getShort());
    }

    @Test
    public void testComplementAOne() {
        regs.a.set(1);
        ByteRegisterInstruction.compliment(io, regs.a, null, null);
        assertEquals(0xFE, regs.a.getShort());
    }

    @Test
    public void testComplementASetsCarryFlag() {
        regs.a.set(1);
        ByteRegisterInstruction.compliment(io, regs.a, null, null);
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testComplementASetsNegativeFlagCorrect() {
        regs.a.set(1);
        ByteRegisterInstruction.compliment(io, regs.a, null, null);
        assertTrue(regs.cc.isMasked(CC_N));

        io.writeByte(0xCAFE, 0xFE);
        ByteInstruction.compliment(io, new UnsignedByte(0xFE), new UnsignedWord(0xCAFE));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testComplementASetsZeroFlagCorrect() {
        regs.a.set(0xFF);
        ByteRegisterInstruction.compliment(io, regs.a, null, null);
        assertEquals(0, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testLogicalShiftARightMovesOneBitCorrect() {
        regs.a.set(2);
        ByteRegisterInstruction.logicalShiftRight(io, regs.a, null, null);
        assertEquals(1, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testLogicalShiftRightAMovesOneBitToZero() {
        regs.a.set(1);
        ByteRegisterInstruction.logicalShiftRight(io, regs.a, null, null);
        assertEquals(0, regs.a.getShort());
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testLogicalShiftRightASetsZeroBit() {
        regs.a.set(1);
        ByteRegisterInstruction.logicalShiftRight(io, regs.a, null, null);
        assertEquals(0, regs.a.getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testLogicalShiftRightACorrect() throws MalformedInstructionException {
        regs.a.set(0x55);
        io.writeByte(0x0000, 0x44);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x2A), regs.a);
    }

    @Test
    public void testRotateRightACorrect() throws MalformedInstructionException {
        regs.a.set(0x55);
        io.writeByte(0x0000, 0x46);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x2A), regs.a);
    }

    @Test
    public void testArithmeticShiftRightACorrect() throws MalformedInstructionException {
        regs.a.set(0x85);
        io.writeByte(0x0000, 0x47);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC2), regs.a);
    }

    @Test
    public void testArithmeticShiftLeftACorrect() throws MalformedInstructionException {
        regs.a.set(0x55);
        io.writeByte(0x0000, 0x48);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xAA), regs.a);
    }

    @Test
    public void testRotateLeftACorrect() throws MalformedInstructionException {
        regs.a.set(0x01);
        io.writeByte(0x0000, 0x49);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x02), regs.a);
    }

    @Test
    public void testDecrementACorrect() throws MalformedInstructionException {
        regs.a.set(0x01);
        io.writeByte(0x0000, 0x4A);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x00), regs.a);
    }

    @Test
    public void testIncrementACorrect() throws MalformedInstructionException {
        regs.a.set(0x01);
        io.writeByte(0x0000, 0x4C);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x02), regs.a);
    }

    @Test
    public void testTestACorrect() throws MalformedInstructionException {
        regs.a.set(0x80);
        io.writeByte(0x0000, 0x4D);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x80), regs.a);
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    // No operation for instruction 0x4E

    @Test
    public void testClearACorrect() throws MalformedInstructionException {
        regs.a.set(0x80);
        io.writeByte(0x0000, 0x4F);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x00), regs.a);
    }

    @Test
    public void testNegateBCorrect() throws MalformedInstructionException {
        regs.b.set(0xAB);
        io.writeByte(0x0000, 0x50);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x55), regs.b);
    }

    // No operation for instruction 0x51

    // No operation for instruction 0x52

    @Test
    public void testComplimentBWorksCorrectly() throws MalformedInstructionException {
        regs.b.set(0x77);
        io.writeByte(0x0000, 0x53);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x88), regs.b);
        assertTrue(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testLogicalShiftRightBCorrect() throws MalformedInstructionException {
        regs.b.set(0x55);
        io.writeByte(0x0000, 0x54);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x2A), regs.b);
    }

    // No operation for instruction 0x55

    @Test
    public void testRotateRightBCorrect() throws MalformedInstructionException {
        regs.b.set(0x55);
        io.writeByte(0x0000, 0x56);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x2A), regs.b);
    }

    @Test
    public void testArithmeticShiftRightBCorrect() throws MalformedInstructionException {
        regs.b.set(0x85);
        io.writeByte(0x0000, 0x57);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC2), regs.b);
    }

    @Test
    public void testArithmeticShiftLeftBCorrect() throws MalformedInstructionException {
        regs.b.set(0x55);
        io.writeByte(0x0000, 0x58);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xAA), regs.b);
    }

    @Test
    public void testRotateLeftBCorrect() throws MalformedInstructionException {
        regs.b.set(0x01);
        io.writeByte(0x0000, 0x59);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x02), regs.b);
    }

    @Test
    public void testDecrementBCorrect() throws MalformedInstructionException {
        regs.b.set(0x01);
        io.writeByte(0x0000, 0x5A);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x00), regs.b);
    }

    // No operation for instruction 0x5B

    @Test
    public void testIncrementBCorrect() throws MalformedInstructionException {
        regs.b.set(0x01);
        io.writeByte(0x0000, 0x5C);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x02), regs.b);
    }

    @Test
    public void testTestBCorrect() throws MalformedInstructionException {
        regs.b.set(0x80);
        io.writeByte(0x0000, 0x5D);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x80), regs.b);
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    // No operation for instruction 0x5E

    @Test
    public void testClearBCorrect() throws MalformedInstructionException {
        regs.b.set(0x80);
        io.writeByte(0x0000, 0x5F);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x00), regs.b);
    }

    @Test
    public void testCompareAImmediateCorrect() throws MalformedInstructionException {
        regs.a.set(0xA3);
        io.writeWord(0x0000, 0x8111);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xA3), regs.a);
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCompareAImmediateCorrect1() throws MalformedInstructionException {
        regs.a.set(0x80);
        io.writeWord(0x0000, 0x817E);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x80), regs.a);
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCompareBImmediateCorrect() throws MalformedInstructionException {
        regs.b.set(0xA3);
        io.writeWord(0x0000, 0xC111);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xA3), regs.b);
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCompareADirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        regs.a.set(0xA3);
        io.writeWord(0x0000, 0x910A);
        io.writeByte(0x000A, 0x11);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xA3), regs.a);
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCompareBDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        regs.b.set(0xA3);
        io.writeWord(0x0000, 0xD10A);
        io.writeByte(0x000A, 0x11);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xA3), regs.b);
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCompareAIndexedCorrect() throws MalformedInstructionException {
        regs.a.set(0xA3);
        io.writeWord(0x0000, 0xA180);
        regs.x.set(extendedAddress);
        io.writeByte(extendedAddress, 0x11);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xA3), regs.a);
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCompareBIndexedCorrect() throws MalformedInstructionException {
        regs.b.set(0xA3);
        io.writeWord(0x0000, 0xE180);
        regs.x.set(extendedAddress);
        io.writeByte(extendedAddress, 0x11);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xA3), regs.b);
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCompareAExtendedCorrect() throws MalformedInstructionException {
        regs.a.set(0xA3);
        io.writeByte(0x0000, 0xB1);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x11);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xA3), regs.a);
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCompareBExtendedCorrect() throws MalformedInstructionException {
        regs.b.set(0xA3);
        io.writeByte(0x0000, 0xF1);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x11);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xA3), regs.b);
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testStoreByteRegisterDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        regs.a.set(0xBE);
        io.writeWord(0x0000, 0x970A);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xBE), io.readByte(0x000A));
    }

    @Test
    public void testStoreByteRegisterIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.a.set(0xBE);
        io.writeWord(0x0000, 0xA780);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xBE), io.readByte(extendedAddress));
    }

    @Test
    public void testStoreByteRegisterExtendedCorrect() throws MalformedInstructionException {
        regs.a.set(0xBE);
        io.writeByte(0x0000, 0xB7);
        io.writeWord(0x0001, extendedAddress);
        cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xBE), io.readByte(extendedAddress));
    }

    @Test
    public void testStoreByteRegisterSetsZero() {
        regs.a.set(new UnsignedByte(0x0));
        ByteRegisterInstruction.storeByteRegister(io, regs.a, null, new UnsignedWord(0xA000));
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testStoreByteRegisterSetsNegative() {
        regs.a.set(new UnsignedByte(0x81));
        ByteRegisterInstruction.storeByteRegister(io, regs.a, null, new UnsignedWord(0xA000));
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLoadByteRegisterWorksCorrectly() {
        ByteRegisterInstruction.loadByteRegister(io, regs.a, new UnsignedByte(0xAA), null);
        assertEquals(0xAA, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLoadByteRegisterSetsZero() {
        regs.a.set(0xFF);
        ByteRegisterInstruction.loadByteRegister(io, regs.a, new UnsignedByte(0), null);
        assertEquals(0x00, regs.a.getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLoadAImmediateCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x86AB);
        cpu.executeInstruction();
        assertEquals(0xAB, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLoadADirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        regs.a.set(0xBE);
        io.writeWord(0x0000, 0x960A);
        io.writeByte(0x000A, 0xAB);
        cpu.executeInstruction();
        assertEquals(0xAB, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLoadAIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.a.set(0xBE);
        io.writeWord(0x0000, 0xA680);
        io.writeByte(extendedAddress, 0xAB);
        cpu.executeInstruction();
        assertEquals(0xAB, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLoadAExtendedCorrect() throws MalformedInstructionException {
        regs.a.set(0xBE);
        io.writeByte(0x0000, 0xB6);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0xAB);
        cpu.executeInstruction();
        assertEquals(0xAB, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testStoreADirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.a.set(0xBE);
        io.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x970A));
        io.writeByte(0x000A, 0xAB);
        cpu.executeInstruction();
        assertEquals(0xBE, io.readByte(0x100A).getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testStoreAIndexedCalled() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.a.set(0xBE);
        io.writeWord(0x0000, 0xA780);
        io.writeByte(extendedAddress, 0xAB);
        cpu.executeInstruction();
        assertEquals(0xBE, io.readByte(extendedAddress).getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testSTAExtendedCalled() throws MalformedInstructionException {
        regs.a.set(0xBE);
        io.writeByte(0x0000, 0xB7);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0xAB);
        cpu.executeInstruction();
        assertEquals(0xBE, io.readByte(extendedAddress).getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testStoreBDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.b.set(0xBE);
        io.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xD70A));
        io.writeByte(0x000A, 0xAB);
        cpu.executeInstruction();
        assertEquals(0xBE, io.readByte(0x100A).getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testStoreBIndexedCalled() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.b.set(0xBE);
        io.writeWord(0x0000, 0xE780);
        io.writeByte(extendedAddress, 0xAB);
        cpu.executeInstruction();
        assertEquals(0xBE, io.readByte(extendedAddress).getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testSTBExtendedCalled() throws MalformedInstructionException {
        regs.b.set(0xBE);
        io.writeByte(0x0000, 0xF7);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0xAB);
        cpu.executeInstruction();
        assertEquals(0xBE, io.readByte(extendedAddress).getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLoadBImmediateCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0xC6AB);
        cpu.executeInstruction();
        assertEquals(0xAB, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLoadBDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        regs.b.set(0xBE);
        io.writeWord(0x0000, 0xD60A);
        io.writeByte(0x000A, 0xAB);
        cpu.executeInstruction();
        assertEquals(0xAB, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLoadBIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.b.set(0xBE);
        io.writeWord(0x0000, 0xE680);
        io.writeByte(extendedAddress, 0xAB);
        cpu.executeInstruction();
        assertEquals(0xAB, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLoadBExtendedCorrect() throws MalformedInstructionException {
        regs.b.set(0xBE);
        io.writeByte(0x0000, 0xF6);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0xAB);
        cpu.executeInstruction();
        assertEquals(0xAB, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalOrSetsZero() {
        regs.cc.set(CC_N);
        regs.a.set(0x0);
        ByteRegisterInstruction.logicalOr(io, regs.a, new UnsignedByte(0x00), null);
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalOrSetsNegative() {
        regs.cc.set(CC_Z);
        regs.a.set(0x0);
        ByteRegisterInstruction.logicalOr(io, regs.a, new UnsignedByte(0x80), null);
        assertEquals(0x80, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testORAImmediateCorrect() throws MalformedInstructionException {
        regs.a.set(0x10);
        io.writeWord(0x00, 0x8A01);
        cpu.executeInstruction();
        assertEquals(0x11, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testORBImmediateCorrect() throws MalformedInstructionException {
        regs.b.set(0x10);
        io.writeWord(0x00, 0xCA01);
        cpu.executeInstruction();
        assertEquals(0x11, regs.b.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testORADirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.a.set(0x10);
        io.writeWord(0x00, 0x9A10);
        io.writeByte(0x1010, 0x01);
        cpu.executeInstruction();
        assertEquals(0x11, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testORBDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.b.set(0x10);
        io.writeWord(0x00, 0xDA10);
        io.writeByte(0x1010, 0x01);
        cpu.executeInstruction();
        assertEquals(0x11, regs.b.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testORAIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.a.set(0x10);
        io.writeWord(0x00, 0xAA80);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(0x11, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testORBIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.b.set(0x10);
        io.writeWord(0x00, 0xEA80);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(0x11, regs.b.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testORAExtendedCorrect() throws MalformedInstructionException {
        regs.a.set(0x10);
        io.writeByte(0x0000, 0xBA);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(0x11, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testORBExtendedCorrect() throws MalformedInstructionException {
        regs.b.set(0x10);
        io.writeByte(0x0000, 0xFA);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(0x11, regs.b.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalOrWorksCorrectly() throws MalformedInstructionException {
        regs.a.set(0x40);
        regs.b.set(0x01);
        io.writeWord(0x0000, 0x8A11);
        cpu.executeInstruction();
        assertEquals(0x51, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));

        regs.pc.set(0x0000);
        regs.a.set(0x01);
        regs.b.set(0x01);
        io.writeWord(0x0000, 0x8A00);
        cpu.executeInstruction();
        assertEquals(0x01, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));

        regs.pc.set(0x0000);
        regs.a.set(0x00);
        regs.b.set(0x01);
        io.writeWord(0x0000, 0x8A00);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));

        regs.pc.set(0x0000);
        regs.a.set(0x01);
        regs.b.set(0x01);
        io.writeWord(0x0000, 0xCA00);
        cpu.executeInstruction();
        assertEquals(0x01, regs.b.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));

        regs.pc.set(0x0000);
        regs.a.set(0x01);
        regs.b.set(0x00);
        io.writeWord(0x0000, 0xCA00);
        cpu.executeInstruction();
        assertEquals(0x00, regs.b.getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testExclusiveOrWorksCorrectly() {
        regs.a.set(0x0);
        ByteRegisterInstruction.exclusiveOr(io, regs.a, new UnsignedByte(0x0), null);
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));

        regs.a.set(0x0);
        ByteRegisterInstruction.exclusiveOr(io, regs.a, new UnsignedByte(0x1), null);
        assertEquals(0x01, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));

        regs.a.set(0x1);
        ByteRegisterInstruction.exclusiveOr(io, regs.a, new UnsignedByte(0x0), null);
        assertEquals(0x01, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));

        regs.a.set(0x1);
        ByteRegisterInstruction.exclusiveOr(io, regs.a, new UnsignedByte(0x1), null);
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));

        regs.a.set(0x80);
        ByteRegisterInstruction.exclusiveOr(io, regs.a, new UnsignedByte(0x01), null);
        assertEquals(0x81, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testEORAImmediateCorrect() throws MalformedInstructionException {
        regs.a.set(0x01);
        io.writeWord(0x0000, 0x8801);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testEORBImmediateCorrect() throws MalformedInstructionException {
        regs.b.set(0x01);
        io.writeWord(0x0000, 0xC801);
        cpu.executeInstruction();
        assertEquals(0x00, regs.b.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testEORADirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.a.set(0x01);
        io.writeWord(0x0000, 0x9801);
        io.writeByte(0x1001, 0x01);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testEORBDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.b.set(0x01);
        io.writeWord(0x0000, 0xD801);
        io.writeByte(0x1001, 0x01);
        cpu.executeInstruction();
        assertEquals(0x00, regs.b.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testEORAIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.a.set(0x01);
        io.writeWord(0x0000, 0xA880);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testEORBIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.b.set(0x01);
        io.writeWord(0x0000, 0xE880);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(0x00, regs.b.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testEORAExtendedCorrect() throws MalformedInstructionException {
        regs.a.set(0x01);
        io.writeByte(0x0000, 0xB8);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testEORBExtendedCorrect() throws MalformedInstructionException {
        regs.b.set(0x01);
        io.writeByte(0x0000, 0xF8);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(0x00, regs.b.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testAddWithCarryWorksCorrectly() {
        regs.cc.set(0x00);
        regs.a.set(0x01);
        ByteRegisterInstruction.addWithCarry(io, regs.a, new UnsignedByte(0x0), null);
        assertEquals(0x01, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_C));
        assertFalse(regs.cc.isMasked(CC_H));

        regs.cc.set(CC_C);
        regs.a.set(0x01);
        ByteRegisterInstruction.addWithCarry(io, regs.a, new UnsignedByte(0x0), null);
        assertEquals(0x02, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_C));
        assertFalse(regs.cc.isMasked(CC_H));

        regs.cc.set(0x00);
        regs.a.set(0x00);
        ByteRegisterInstruction.addWithCarry(io, regs.a, new UnsignedByte(0x0), null);
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_C));
        assertFalse(regs.cc.isMasked(CC_H));

        regs.cc.set(0x01);
        regs.a.set(0x7F);
        ByteRegisterInstruction.addWithCarry(io, regs.a, new UnsignedByte(0x0), null);
        assertEquals(0x80, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_H));

        regs.cc.set(0x00);
        regs.a.set(0xFF);
        ByteRegisterInstruction.addWithCarry(io, regs.a, new UnsignedByte(0x01), null);
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_H));
    }

        @Test
    public void testAddWithCarryWorksCorrectlyTest1() {
        regs.cc.set(0x00);
        regs.a.set(0x68);
        ByteRegisterInstruction.addWithCarry(io, regs.a, new UnsignedByte(0xA5), null);
        assertEquals(0x0D, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
        assertFalse(regs.cc.isMasked(CC_H));
    }

    @Test
    public void testAddWithCarryWorksCorrectlyTest2() {
        regs.cc.set(CC_C);
        regs.a.set(0x30);
        ByteRegisterInstruction.addWithCarry(io, regs.a, new UnsignedByte(0xC3), null);
        assertEquals(0xF4, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_C));
        assertFalse(regs.cc.isMasked(CC_H));
    }

    @Test
    public void testAddWithCarryWorksCorrectlyTest3() {
        regs.cc.set(CC_C);
        regs.a.set(0x01);
        ByteRegisterInstruction.addWithCarry(io, regs.a, new UnsignedByte(0xFF), null);
        assertEquals(0x01, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_H));
    }

    @Test
    public void testAddWithCarryWorksCorrectlyTest4() {
        regs.cc.set(0x00);
        regs.a.set(0x01);
        ByteRegisterInstruction.addWithCarry(io, regs.a, new UnsignedByte(0xFF), null);
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_H));
    }

    @Test
    public void testAddWithCarryZeroWithCarryTriggersCarryBit() {
        regs.cc.set(CC_C);
        regs.a.set(0xFF);
        ByteRegisterInstruction.addWithCarry(io, regs.a, new UnsignedByte(0x00), null);
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_H));
    }

    @Test
    public void testAddWithCarryAImmediateCorrect() throws MalformedInstructionException {
        regs.a.set(0xFE);
        regs.cc.set(CC_C);
        io.writeWord(0x0000, 0x8901);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddWithCarryADirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.a.set(0xFE);
        regs.cc.set(CC_C);
        io.writeWord(0x0000, 0x9901);
        io.writeByte(0x1001, 0x01);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddWithCarryAIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.a.set(0xFE);
        regs.cc.set(CC_C);
        io.writeWord(0x0000, 0xA980);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddWithCarryAExtendedCorrect() throws MalformedInstructionException {
        regs.a.set(0xFE);
        regs.cc.set(CC_C);
        io.writeByte(0x0000, 0xB9);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddWithCarryBImmediateCorrect() throws MalformedInstructionException {
        regs.b.set(0xFE);
        regs.cc.set(CC_C);
        io.writeWord(0x0000, 0xC901);
        cpu.executeInstruction();
        assertEquals(0x00, regs.b.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddWithCarryBDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.b.set(0xFE);
        regs.cc.set(CC_C);
        io.writeWord(0x0000, 0xD901);
        io.writeByte(0x1001, 0x01);
        cpu.executeInstruction();
        assertEquals(0x00, regs.b.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddWithCarryBIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.b.set(0xFE);
        regs.cc.set(CC_C);
        io.writeWord(0x0000, 0xE980);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(0x00, regs.b.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddWithCarryBExtendedCorrect() throws MalformedInstructionException {
        regs.b.set(0xFE);
        regs.cc.set(CC_C);
        io.writeByte(0x0000, 0xF9);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x01);
        cpu.executeInstruction();
        assertEquals(0x00, regs.b.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddByteWorksCorrectly() {
        regs.a.set(0x11);
        ByteRegisterInstruction.addByte(io, regs.a, new UnsignedByte(0x11), null);
        assertEquals(0x22, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_H));
    }

    @Test
    public void testAddByteSetsHalfCarry() {
        regs.a.set(0x0F);
        ByteRegisterInstruction.addByte(io, regs.a, new UnsignedByte(0x01), null);
        assertEquals(0x10, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_H));
    }

    @Test
    public void testAddByteSetsCarryOverflow() {
        regs.a.set(0x7F);
        ByteRegisterInstruction.addByte(io, regs.a, new UnsignedByte(0x01), null);
        assertEquals(0x80, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_H));
    }

    @Test
    public void testAddByteSetsNegative() {
        regs.a.set(0x7E);
        ByteRegisterInstruction.addByte(io, regs.a, new UnsignedByte(0x02), null);
        assertEquals(0x80, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_H));
    }

    @Test
    public void testAddByteRegression1() {
        regs.a.set(0xF9);
        ByteRegisterInstruction.addByte(io, regs.a, new UnsignedByte(0x08), null);
        assertEquals(0x01, regs.a.getShort());
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
        assertTrue(regs.cc.isMasked(CC_H));
    }

    @Test
    public void testAddAImmediateCorrect() throws MalformedInstructionException {
        regs.a.set(0xFF);
        io.writeWord(0x0000, 0x8B01);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddADirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.a.set(0xFE);
        io.writeWord(0x0000, 0x9B01);
        io.writeByte(0x1001, 0x02);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddAIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.a.set(0xFE);
        io.writeWord(0x0000, 0xAB80);
        io.writeByte(extendedAddress, 0x02);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddAExtendedCorrect() throws MalformedInstructionException {
        regs.a.set(0xFE);
        io.writeByte(0x0000, 0xBB);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x02);
        cpu.executeInstruction();
        assertEquals(0x00, regs.a.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddBImmediateCorrect() throws MalformedInstructionException {
        regs.b.set(0xFE);
        io.writeWord(0x0000, 0xCB02);
        cpu.executeInstruction();
        assertEquals(0x00, regs.b.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddBDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.b.set(0xFE);
        io.writeWord(0x0000, 0xDB01);
        io.writeByte(0x1001, 0x02);
        cpu.executeInstruction();
        assertEquals(0x00, regs.b.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddBIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.b.set(0xFE);
        io.writeWord(0x0000, 0xEB80);
        io.writeByte(extendedAddress, 0x02);
        cpu.executeInstruction();
        assertEquals(0x00, regs.b.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddBExtendedCorrect() throws MalformedInstructionException {
        regs.b.set(0xFE);
        io.writeByte(0x0000, 0xFB);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x02);
        cpu.executeInstruction();
        assertEquals(0x00, regs.b.getShort());
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_H));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testLogicalAndWorksCorrectly() {
        regs.a.set(0x20);
        ByteRegisterInstruction.logicalAnd(io, regs.a, new UnsignedByte(0x21), null);
        assertEquals(0x20, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalAndSetsZero() {
        regs.a.set(0x1);
        ByteRegisterInstruction.logicalAnd(io, regs.a, new UnsignedByte(0x20), null);
        assertEquals(0, regs.a.getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalAndSetsNegative() {
        regs.a.set(0x81);
        ByteRegisterInstruction.logicalAnd(io, regs.a, new UnsignedByte(0x81), null);
        assertEquals(0x81, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalAndAImmediateCorrect() throws MalformedInstructionException {
        regs.a.set(0x20);
        io.writeWord(0x0000, 0x8421);
        cpu.executeInstruction();
        assertEquals(0x20, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalAndADirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.a.set(0x20);
        io.writeWord(0x0000, 0x9401);
        io.writeByte(0x1001, 0x21);
        cpu.executeInstruction();
        assertEquals(0x20, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalAndAIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.a.set(0x20);
        io.writeWord(0x0000, 0xA480);
        io.writeByte(extendedAddress, 0x21);
        cpu.executeInstruction();
        assertEquals(0x20, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalAndAExtendedCorrect() throws MalformedInstructionException {
        regs.a.set(0x20);
        io.writeByte(0x0000, 0xB4);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x21);
        cpu.executeInstruction();
        assertEquals(0x20, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalAndBImmediateCorrect() throws MalformedInstructionException {
        regs.b.set(0x20);
        io.writeWord(0x0000, 0xC421);
        cpu.executeInstruction();
        assertEquals(0x20, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalAndBDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.b.set(0x20);
        io.writeWord(0x0000, 0xD401);
        io.writeByte(0x1001, 0x21);
        cpu.executeInstruction();
        assertEquals(0x20, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalAndBIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.b.set(0x20);
        io.writeWord(0x0000, 0xE480);
        io.writeByte(extendedAddress, 0x21);
        cpu.executeInstruction();
        assertEquals(0x20, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLogicalAndBExtendedCorrect() throws MalformedInstructionException {
        regs.b.set(0x20);
        io.writeByte(0x0000, 0xF4);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x21);
        cpu.executeInstruction();
        assertEquals(0x20, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testBitTestWorksCorrectly() {
        regs.a.set(0x20);
        ByteRegisterInstruction.bitTest(io, regs.a, new UnsignedByte(0x21), null);
        assertEquals(0x20, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testBitTestSetsZero() {
        regs.a.set(0x1);
        ByteRegisterInstruction.bitTest(io, regs.a, new UnsignedByte(0x20), null);
        assertEquals(0x01, regs.a.getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testBitTestSetsNegative() {
        regs.a.set(0x81);
        ByteRegisterInstruction.bitTest(io, regs.a, new UnsignedByte(0x81), null);
        assertEquals(0x81, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testBitTestAImmediateCorrect() throws MalformedInstructionException {
        regs.a.set(0x20);
        io.writeWord(0x0000, 0x8521);
        cpu.executeInstruction();
        assertEquals(0x20, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testBitTestADirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.a.set(0x20);
        io.writeWord(0x0000, 0x9501);
        io.writeByte(0x1001, 0x21);
        cpu.executeInstruction();
        assertEquals(0x20, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testBitTestAIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.a.set(0x20);
        io.writeWord(0x0000, 0xA580);
        io.writeByte(extendedAddress, 0x21);
        cpu.executeInstruction();
        assertEquals(0x20, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testBitTestAExtendedCorrect() throws MalformedInstructionException {
        regs.a.set(0x20);
        io.writeByte(0x0000, 0xB5);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x21);
        cpu.executeInstruction();
        assertEquals(0x20, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testBitTestBImmediateCorrect() throws MalformedInstructionException {
        regs.b.set(0x20);
        io.writeWord(0x0000, 0xC521);
        cpu.executeInstruction();
        assertEquals(0x20, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testBitTestBDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.b.set(0x20);
        io.writeWord(0x0000, 0xD501);
        io.writeByte(0x1001, 0x21);
        cpu.executeInstruction();
        assertEquals(0x20, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testBitTestBIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.b.set(0x20);
        io.writeWord(0x0000, 0xE580);
        io.writeByte(extendedAddress, 0x21);
        cpu.executeInstruction();
        assertEquals(0x20, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testBitTestBExtendedCorrect() throws MalformedInstructionException {
        regs.b.set(0x20);
        io.writeByte(0x0000, 0xF5);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x21);
        cpu.executeInstruction();
        assertEquals(0x20, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCompareByteWorksCorrectly() {
        regs.a.set(0xA3);
        ByteRegisterInstruction.compareByte(io, regs.a, new UnsignedByte(0x11), null);
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCompareByteWorksSetsZero() {
        regs.a.set(0x1);
        ByteRegisterInstruction.compareByte(io, regs.a, new UnsignedByte(0x1), null);
        assertEquals(0x01, regs.a.getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testCompareByteWorksSetsNegativeSetsCarry() {
        regs.a.set(0x80);
        ByteRegisterInstruction.compareByte(io, regs.a, new UnsignedByte(0x81), null);
        assertEquals(0x80, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_V));
    }

    @Test
    public void testCompareByteWorksSetsOverflow() {
        regs.a.set(0x80);
        ByteRegisterInstruction.compareByte(io, regs.a, new UnsignedByte(0x7E), null);
        assertEquals(0x80, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testCompareByteRegression1() {
        regs.a.set(0xFE);
        ByteRegisterInstruction.compareByte(io, regs.a, new UnsignedByte(0xF8), null);
        assertEquals(0xFE, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCompareByteADirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.a.set(0x80);
        io.writeWord(0x0000, 0x9101);
        io.writeByte(0x1001, 0x7F);
        cpu.executeInstruction();
        assertEquals(0x80, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testCompareByteAIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.a.set(0x80);
        io.writeWord(0x0000, 0xA180);
        io.writeByte(extendedAddress, 0x7F);
        cpu.executeInstruction();
        assertEquals(0x80, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testCompareByteAExtendedCorrect() throws MalformedInstructionException {
        regs.a.set(0x80);
        io.writeByte(0x0000, 0xB1);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x7F);
        cpu.executeInstruction();
        assertEquals(0x80, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testCompareByteBImmediateCorrect() throws MalformedInstructionException {
        regs.b.set(0x80);
        io.writeWord(0x0000, 0xC17F);
        cpu.executeInstruction();
        assertEquals(0x80, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testCompareByteBDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.b.set(0x80);
        io.writeWord(0x0000, 0xD101);
        io.writeByte(0x1001, 0x7F);
        cpu.executeInstruction();
        assertEquals(0x80, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testCompareByteBIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.b.set(0x80);
        io.writeWord(0x0000, 0xE180);
        io.writeByte(extendedAddress, 0x7F);
        cpu.executeInstruction();
        assertEquals(0x80, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testCompareByteBExtendedCorrect() throws MalformedInstructionException {
        regs.b.set(0x80);
        io.writeByte(0x0000, 0xF1);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x7F);
        cpu.executeInstruction();
        assertEquals(0x80, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }


    @Test
    public void testCompareByteWorksSetsOverflow1() {
        regs.a.set(0x80);
        ByteRegisterInstruction.compareByte(io, regs.a, new UnsignedByte(0x7F), null);
        assertEquals(0x80, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testCompareByteAImmediateCorrect() throws MalformedInstructionException {
        regs.a.set(0x80);
        io.writeWord(0x0000, 0x817F);
        cpu.executeInstruction();
        assertEquals(0x80, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteWorksSetsOverflow() {
        regs.a.set(0x80);
        ByteRegisterInstruction.subtractByte(io, regs.a, new UnsignedByte(0x7F), null);
        assertEquals(0x01, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteWorksSetsCarry() {
        regs.a.set(0x7F);
        ByteRegisterInstruction.subtractByte(io, regs.a, new UnsignedByte(0x80), null);
        assertEquals(0xFF, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }
    @Test
    public void testSubtractByteWorksCorrectly() {
        regs.a.set(0x6A);
        ByteRegisterInstruction.subtractByte(io, regs.a, new UnsignedByte(0x27), null);
        assertEquals(0x43, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
    }

    @Test
    public void testSubtractByteWorksSetsZero() {
        regs.a.set(0x1);
        ByteRegisterInstruction.subtractByte(io, regs.a, new UnsignedByte(0x1), null);
        assertEquals(0, regs.a.getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteWorksSetsNegativeSetsCarry() {
        regs.a.set(0x80);
        ByteRegisterInstruction.subtractByte(io, regs.a, new UnsignedByte(0x81), null);
        assertEquals(0xFF, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_V));
    }

    @Test
    public void testSubtractByteRegression1() {
        regs.a.set(0xFE);
        ByteRegisterInstruction.subtractByte(io, regs.a, new UnsignedByte(0xF8), null);
        assertEquals(0x06, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testSubtractByteAImmediateCorrect() throws MalformedInstructionException {
        regs.a.set(0x80);
        io.writeWord(0x0000, 0x807F);
        cpu.executeInstruction();
        assertEquals(0x01, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteADirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.a.set(0x80);
        io.writeWord(0x0000, 0x9001);
        io.writeByte(0x1001, 0x7F);
        cpu.executeInstruction();
        assertEquals(0x01, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteAIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.a.set(0x80);
        io.writeWord(0x0000, 0xA080);
        io.writeByte(extendedAddress, 0x7F);
        cpu.executeInstruction();
        assertEquals(0x01, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteAExtendedCorrect() throws MalformedInstructionException {
        regs.a.set(0x80);
        io.writeByte(0x0000, 0xB0);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x7F);
        cpu.executeInstruction();
        assertEquals(0x01, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteBImmediateCorrect() throws MalformedInstructionException {
        regs.b.set(0x80);
        io.writeWord(0x0000, 0xC07F);
        cpu.executeInstruction();
        assertEquals(0x01, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteBDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.b.set(0x80);
        io.writeWord(0x0000, 0xD001);
        io.writeByte(0x1001, 0x7F);
        cpu.executeInstruction();
        assertEquals(0x01, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteBIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.b.set(0x80);
        io.writeWord(0x0000, 0xE080);
        io.writeByte(extendedAddress, 0x7F);
        cpu.executeInstruction();
        assertEquals(0x01, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteBExtendedCorrect() throws MalformedInstructionException {
        regs.b.set(0x80);
        io.writeByte(0x0000, 0xF0);
        io.writeWord(0x0001, extendedAddress);
        io.writeByte(extendedAddress, 0x7F);
        cpu.executeInstruction();
        assertEquals(0x01, regs.b.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteWithCarryWorksSetsOverflowNoCarry() {
        regs.a.set(0x80);
        ByteRegisterInstruction.subtractByteWithCarry(io, regs.a, new UnsignedByte(0x7F), null);
        assertEquals(0x01, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteWithCarryWorksSetsCarryNoCarry() {
        regs.a.set(0x7F);
        ByteRegisterInstruction.subtractByteWithCarry(io, regs.a, new UnsignedByte(0x80), null);
        assertEquals(0xFF, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }
    @Test
    public void testSubtractByteWithCarryWorksCorrectlyNoCarry() {
        regs.a.set(0x6A);
        ByteRegisterInstruction.subtractByteWithCarry(io, regs.a, new UnsignedByte(0x27), null);
        assertEquals(0x43, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteWithcarryWorksSetsZeroNoCarry() {
        regs.a.set(0x1);
        ByteRegisterInstruction.subtractByteWithCarry(io, regs.a, new UnsignedByte(0x1), null);
        assertEquals(0, regs.a.getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteWithCarryWorksSetsNegativeSetsCarryNoCarry() {
        regs.a.set(0x80);
        ByteRegisterInstruction.subtractByteWithCarry(io, regs.a, new UnsignedByte(0x81), null);
        assertEquals(0xFF, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_V));
    }

    @Test
    public void testSubtractByteWithCarryWorksSetsOverflowAndZeroWithCarry() {
        regs.cc.set(CC_C);
        regs.a.set(0x80);
        ByteRegisterInstruction.subtractByteWithCarry(io, regs.a, new UnsignedByte(0x7F), null);
        assertEquals(0x00, regs.a.getShort());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteWithCarryWorksSetsCarryWithCarry() {
        regs.cc.set(CC_C);
        regs.a.set(0x7F);
        ByteRegisterInstruction.subtractByteWithCarry(io, regs.a, new UnsignedByte(0x80), null);
        assertEquals(0xFE, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }
    @Test
    public void testSubtractByteWithCarryWorksCorrectlyWithCarry() {
        regs.cc.set(CC_C);
        regs.a.set(0x6A);
        ByteRegisterInstruction.subtractByteWithCarry(io, regs.a, new UnsignedByte(0x27), null);
        assertEquals(0x42, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteWithcarryWorksSetsOverflowNegativeWithCarry() {
        regs.cc.set(CC_C);
        regs.a.set(0x1);
        ByteRegisterInstruction.subtractByteWithCarry(io, regs.a, new UnsignedByte(0x1), null);
        assertEquals(0xFF, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractByteWithCarryWorksSetsNegativeSetsCarryWithCarry() {
        regs.cc.set(CC_C);
        regs.a.set(0x80);
        ByteRegisterInstruction.subtractByteWithCarry(io, regs.a, new UnsignedByte(0x81), null);
        assertEquals(0xFE, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_V));
    }

    @Test
    public void testSubtractByteWithCarryNoCarryRegression1() {
        regs.a.set(0xFE);
        ByteRegisterInstruction.subtractByteWithCarry(io, regs.a, new UnsignedByte(0xF8), null);
        assertEquals(0x06, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testSubtractByteWithCarryCarrySetRegression1() {
        regs.cc.set(CC_C);
        regs.a.set(0xFE);
        ByteRegisterInstruction.subtractByteWithCarry(io, regs.a, new UnsignedByte(0xF8), null);
        assertEquals(0x05, regs.a.getShort());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }
}
