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

public class WordRegisterInstructionTest {
    private IOController io;
    private RegisterSet regs;
    private CPU cpu;
    private int extendedAddress;
    private UnsignedWord address;

    @Before
    public void setUp() {
        Screen screen = new Screen(1);
        Cassette cassette = new Cassette();
        regs = new RegisterSet();
        io = new IOController(new Memory(), regs, new EmulatedKeyboard(), screen, cassette);
        cpu = new CPU(io);
        extendedAddress = 0xC0A0;
        address = new UnsignedWord(0xA000);
        io.regs.pc.set(0);
    }

    @Test(expected = MalformedInstructionException.class)
    public void testWordRegisterInstructionThrowsRuntimeExceptionWithUnsupportedRegister() throws MalformedInstructionException {
        WordRegisterInstruction instruction = new WordRegisterInstruction(0, 0, "None", AddressingMode.INHERENT, null, Register.PC);
        instruction.call(io);
    }

    @Test
    public void testStoreWordRegisterWorksCorrectly() {
        regs.s.set(0xBEEF);
        WordRegisterInstruction.storeWordRegister(io, regs.s, null, address, false);
        assertEquals(0xBEEF, io.readWord(address).getInt());

        io.writeWord(address, 0);
        regs.u.set(0xBEEF);
        WordRegisterInstruction.storeWordRegister(io, regs.u, null, address, false);
        assertEquals(0xBEEF, io.readWord(address).getInt());

        io.writeWord(address, 0);
        regs.y.set(0xBEEF);
        WordRegisterInstruction.storeWordRegister(io, regs.y, null, address, false);
        assertEquals(0xBEEF, io.readWord(address).getInt());

        io.writeWord(address, 0);
        regs.x.set(new UnsignedWord(0xBEEF));
        WordRegisterInstruction.storeWordRegister(io, regs.x, null, address, false);
        assertEquals(0xBEEF, io.readWord(address).getInt());

        io.writeWord(address, 0);
        regs.setD(0xBEEF);
        WordRegisterInstruction.storeWordRegister(io, regs.getD(), null, address, true);
        assertEquals(0xBEEF, io.readWord(address).getInt());
    }

    @Test
    public void testStoreWordRegisterDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        regs.u.set(0xBEEF);
        io.writeWord(0x0000, 0xDF0A);
        cpu.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), io.readWord(0x000A));
    }

    @Test
    public void testStoreWordRegisterIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.u.set(0xBEEF);
        io.writeWord(0x0000, 0xEF80);
        cpu.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), io.readWord(extendedAddress));
    }

    @Test
    public void testClearExtendedCorrect() throws MalformedInstructionException {
        regs.u.set(0xBEEF);
        io.writeByte(0x0000, 0xFF);
        io.writeWord(0x0001, extendedAddress);
        cpu.executeInstruction();
        assertEquals(new UnsignedWord(0xBEEF), io.readWord(extendedAddress));
    }

    @Test
    public void testStoreWordRegisterSetsZero() {
        io.writeWord(address, 0xA000);
        regs.s.set(0);
        WordRegisterInstruction.storeWordRegister(io, regs.s, null, address, false);
        assertEquals(0, io.readWord(address).getInt());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testStoreWordRegisterSetsNegative() {
        regs.s.set(0x8100);
        WordRegisterInstruction.storeWordRegister(io, regs.s, null, address, false);
        assertEquals(0x8100, io.readWord(address).getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testSTDDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        regs.a.set(0xAA);
        regs.b.set(0xBB);
        io.writeWord(0x0000, 0xDD0A);
        cpu.executeInstruction();
        assertEquals(0xAABB, io.readWord(0x000A).getInt());
    }

    @Test
    public void testSTDIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.a.set(0xAA);
        regs.b.set(0xBB);
        io.writeWord(0x0000, 0xED80);
        cpu.executeInstruction();
        assertEquals(0xAABB, io.readWord(extendedAddress).getInt());
    }

    @Test
    public void testSTDExtendedCorrect() throws MalformedInstructionException {
        regs.a.set(0xAA);
        regs.b.set(0xBB);
        io.writeByte(0x0000, 0xFD);
        io.writeWord(0x0001, extendedAddress);
        cpu.executeInstruction();
        assertEquals(0xAABB, io.readWord(extendedAddress).getInt());
    }

    @Test
    public void testSTUDirectCorrect() throws MalformedInstructionException {
        regs.u.set(0x1234);
        io.writeWord(0x0000, 0xDF0A);
        cpu.executeInstruction();
        assertEquals(0x1234, io.readWord(0x000A).getInt());
    }

    @Test
    public void testSTUIndexedCorrect() throws MalformedInstructionException {
        regs.u.set(0x1234);
        regs.x.set(extendedAddress);
        io.writeWord(0x0000, 0xEF80);
        cpu.executeInstruction();
        assertEquals(0x1234, io.readWord(extendedAddress).getInt());
    }

    @Test
    public void testSTUExtendedCorrect() throws MalformedInstructionException {
        regs.u.set(0x1234);
        io.writeByte(0x0000, 0xFF);
        io.writeWord(0x0001, extendedAddress);
        cpu.executeInstruction();
        assertEquals(0x1234, io.readWord(extendedAddress).getInt());
    }

    @Test
    public void testSTXDirectCorrect() throws MalformedInstructionException {
        regs.x.set(0x1234);
        io.writeWord(0x0000, 0x9F0A);
        cpu.executeInstruction();
        assertEquals(0x1234, io.readWord(0x000A).getInt());
    }

    @Test
    public void testSTXIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(0x1234);
        regs.y.set(extendedAddress);
        io.writeWord(0x0000, 0xAFA0);
        cpu.executeInstruction();
        assertEquals(0x1234, io.readWord(extendedAddress).getInt());
    }

    @Test
    public void testSTXExtendedCorrect() throws MalformedInstructionException {
        regs.x.set(0x1234);
        io.writeByte(0x0000, 0xBF);
        io.writeWord(0x0001, extendedAddress);
        cpu.executeInstruction();
        assertEquals(0x1234, io.readWord(extendedAddress).getInt());
    }

    @Test
    public void testSTYDirectCorrect() throws MalformedInstructionException {
        regs.y.set(0x1234);
        io.writeWord(0x0000, 0x109F);
        io.writeByte(0x0002, 0x0A);
        cpu.executeInstruction();
        assertEquals(0x1234, io.readWord(0x000A).getInt());
    }

    @Test
    public void testSTYIndexedCorrect() throws MalformedInstructionException {
        regs.y.set(0x1234);
        regs.x.set(extendedAddress);
        io.writeWord(0x0000, 0x10AF);
        io.writeByte(0x0002, 0x80);
        cpu.executeInstruction();
        assertEquals(0x1234, io.readWord(extendedAddress).getInt());
    }

    @Test
    public void testSTYExtendedCorrect() throws MalformedInstructionException {
        regs.y.set(0x1234);
        io.writeWord(0x0000, 0x10BF);
        io.writeWord(0x0002, extendedAddress);
        cpu.executeInstruction();
        assertEquals(0x1234, io.readWord(extendedAddress).getInt());
    }

    @Test
    public void testSTSDirectCorrect() throws MalformedInstructionException {
        regs.s.set(0x1234);
        io.writeWord(0x0000, 0x10DF);
        io.writeByte(0x0002, 0x0A);
        cpu.executeInstruction();
        assertEquals(0x1234, io.readWord(0x000A).getInt());
    }

    @Test
    public void testSTSIndexedCorrect() throws MalformedInstructionException {
        regs.s.set(0x1234);
        regs.x.set(extendedAddress);
        io.writeWord(0x0000, 0x10EF);
        io.writeByte(0x0002, 0x80);
        cpu.executeInstruction();
        assertEquals(0x1234, io.readWord(extendedAddress).getInt());
    }

    @Test
    public void testSTSExtendedCorrect() throws MalformedInstructionException {
        regs.s.set(0x1234);
        io.writeWord(0x0000, 0x10FF);
        io.writeWord(0x0002, extendedAddress);
        cpu.executeInstruction();
        assertEquals(0x1234, io.readWord(extendedAddress).getInt());
    }

    @Test
    public void testABXWorksCorrectly() throws MalformedInstructionException {
        regs.b.set(0x08);
        regs.x.set(0x0020);
        io.writeByte(0, 0x3A);
        cpu.executeInstruction();
        assertEquals(0x0028, regs.x.getInt());
    }

    @Test
    public void testABXWorksCorrectly1() throws MalformedInstructionException {
        regs.b.set(0x21);
        regs.x.set(0x1091);
        io.writeByte(0x0000, 0x3A);
        cpu.executeInstruction();
        assertEquals(0x10B2, regs.x.getInt());
    }

    @Test
    public void testMULWorksCorrect() throws MalformedInstructionException {
        regs.a.set(0x4B);
        regs.b.set(0x0C);
        io.writeByte(0x0000, 0x3D);
        cpu.executeInstruction();
        assertEquals(0x0384, regs.getD().getInt());
        assertEquals(0x03, regs.a.getShort());
        assertEquals(0x84, regs.b.getShort());
        assertFalse(regs.cc.isMasked(CC_N));
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testMULZeroSetZero() throws MalformedInstructionException {
        regs.a.set(0x2);
        regs.b.set(0x0);
        io.writeByte(0x0000, 0x3D);
        cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x0), regs.getD());
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testLoadWordRegisterSetsZero() {
        io.regs.u.set(0xFFFF);
        WordRegisterInstruction.loadWordRegister(io, io.regs.u, new UnsignedWord(0x0000), null, false);
        assertEquals(0, io.regs.u.getInt());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLoadWordRegisterSetsNegative() {
        io.regs.u.set(0xFFFF);
        WordRegisterInstruction.loadWordRegister(io, io.regs.u, new UnsignedWord(0x8100), null, false);
        assertEquals(0x8100, io.regs.u.getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLoadWordRegisterSetsNegativeZero() {
        io.regs.u.set(0xFFFF);
        WordRegisterInstruction.loadWordRegister(io, io.regs.u, new UnsignedWord(0x8000), null, false);
        assertEquals(0x8000, io.regs.u.getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLDSImmediateCalled() throws MalformedInstructionException {
        regs.s.set(0x1234);
        io.writeWord(0x0000, 0x10CE);
        io.writeWord(0x0002, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.s.getInt());
    }

    @Test
    public void testLDSDirectCalled() throws MalformedInstructionException {
        regs.s.set(0x1234);
        io.writeWord(0x0000, 0x10DE);
        io.writeByte(0x0002, 0x0A);
        io.writeWord(0x000A, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.s.getInt());
    }

    @Test
    public void testLDSIndexedCalled() throws MalformedInstructionException {
        regs.s.set(0x1234);
        regs.x.set(extendedAddress);
        io.writeWord(0x0000, 0x10EE);
        io.writeByte(0x0002, 0x80);
        io.writeWord(extendedAddress, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.s.getInt());
    }

    @Test
    public void testLDSExtendedCalled() throws MalformedInstructionException {
        regs.s.set(0x1234);
        io.writeWord(0x0000, 0x10FE);
        io.writeWord(0x0002, extendedAddress);
        io.writeWord(extendedAddress, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.s.getInt());
    }

    @Test
    public void testLDYImmediateCalled() throws MalformedInstructionException {
        regs.y.set(0x1234);
        io.writeWord(0x0000, 0x108E);
        io.writeWord(0x0002, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.y.getInt());
    }

    @Test
    public void testLDYDirectCalled() throws MalformedInstructionException {
        regs.y.set(0x1234);
        io.writeWord(0x0000, 0x109E);
        io.writeByte(0x0002, 0x0A);
        io.writeWord(0x000A, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.y.getInt());
    }

    @Test
    public void testLDYIndexedCalled() throws MalformedInstructionException {
        regs.y.set(0x1234);
        regs.x.set(extendedAddress);
        io.writeWord(0x0000, 0x10AE);
        io.writeByte(0x0002, 0x80);
        io.writeWord(extendedAddress, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.y.getInt());
    }

    @Test
    public void testLDYExtendedCalled() throws MalformedInstructionException {
        regs.s.set(0x1234);
        io.writeWord(0x0000, 0x10BE);
        io.writeWord(0x0002, extendedAddress);
        io.writeWord(extendedAddress, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.y.getInt());
    }

    @Test
    public void testLDDImmediateCalled() throws MalformedInstructionException {
        regs.setD(0x1234);
        io.writeByte(0x0000, 0xCC);
        io.writeWord(0x0001, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.getD().getInt());
    }

    @Test
    public void testLDDDirectCalled() throws MalformedInstructionException {
        regs.setD(0x1234);
        io.writeWord(0x0000, 0xDC0A);
        io.writeWord(0x000A, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.getD().getInt());
    }

    @Test
    public void testLDDIndexedCalled() throws MalformedInstructionException {
        regs.setD(0x1234);
        regs.x.set(extendedAddress);
        io.writeWord(0x0000, 0xEC80);
        io.writeWord(extendedAddress, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.getD().getInt());
    }

    @Test
    public void testLDDExtendedCalled() throws MalformedInstructionException {
        regs.setD(0x1234);
        io.writeByte(0x0000, 0xFC);
        io.writeWord(0x0001, extendedAddress);
        io.writeWord(extendedAddress, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.getD().getInt());
    }

    @Test
    public void testLDUImmediateCalled() throws MalformedInstructionException {
        regs.u.set(0x1234);
        io.writeByte(0x0000, 0xCE);
        io.writeWord(0x0001, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.u.getInt());
    }

    @Test
    public void testLDUDirectCalled() throws MalformedInstructionException {
        regs.u.set(0x1234);
        io.writeWord(0x0000, 0xDE0A);
        io.writeWord(0x000A, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.u.getInt());
    }

    @Test
    public void testLDUIndexedCalled() throws MalformedInstructionException {
        regs.u.set(0x1234);
        regs.x.set(extendedAddress);
        io.writeWord(0x0000, 0xEE80);
        io.writeWord(extendedAddress, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.u.getInt());
    }

    @Test
    public void testLDUExtendedCalled() throws MalformedInstructionException {
        regs.u.set(0x1234);
        io.writeByte(0x0000, 0xFE);
        io.writeWord(0x0001, extendedAddress);
        io.writeWord(extendedAddress, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.u.getInt());
    }

    @Test
    public void testLDXImmediateCalled() throws MalformedInstructionException {
        regs.x.set(0x1234);
        io.writeByte(0x0000, 0x8E);
        io.writeWord(0x0001, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.x.getInt());
    }

    @Test
    public void testLDXDirectCalled() throws MalformedInstructionException {
        regs.x.set(0x1234);
        io.writeWord(0x0000, 0x9E0A);
        io.writeWord(0x000A, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.x.getInt());
    }

    @Test
    public void testLDXIndexedCalled() throws MalformedInstructionException {
        regs.x.set(0x1234);
        regs.y.set(extendedAddress);
        io.writeWord(0x0000, 0xAEA0);
        io.writeWord(extendedAddress, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.x.getInt());
    }

    @Test
    public void testLDXExtendedCalled() throws MalformedInstructionException {
        regs.x.set(0x1234);
        io.writeByte(0x0000, 0xBE);
        io.writeWord(0x0001, extendedAddress);
        io.writeWord(extendedAddress, 0xDDDD);
        cpu.executeInstruction();
        assertEquals(0xDDDD, regs.x.getInt());
    }

    @Test
    public void testCompareWordWorksCorrectly() {
        io.regs.u.set(0x021A);
        WordRegisterInstruction.compareWord(io, io.regs.u, new UnsignedWord(0x072E), null, false);
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_V));
        assertTrue(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testCompareWordSetsZero() {
        io.regs.u.set(0x021A);
        WordRegisterInstruction.compareWord(io, io.regs.u, new UnsignedWord(0x021A), null, false);
        assertTrue(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCompareWordSetsNegative() {
        io.regs.u.set(0x0001);
        WordRegisterInstruction.compareWord(io, io.regs.u, new UnsignedWord(0x0002), null, false);
        assertFalse(regs.cc.isMasked(CC_Z));
        assertTrue(regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCompareWordSetsOverflow() {
        io.regs.u.set(0x8000);
        WordRegisterInstruction.compareWord(io, io.regs.u, new UnsignedWord(0x7FFE), null, false);
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testCompareWordSetsOverflow1() {
        io.regs.u.set(0x8000);
        WordRegisterInstruction.compareWord(io, io.regs.u, new UnsignedWord(0x7FFF), null, false);
        assertFalse(regs.cc.isMasked(CC_Z));
        assertFalse(regs.cc.isMasked(CC_N));
        assertTrue(regs.cc.isMasked(CC_V));
        assertFalse(regs.cc.isMasked(CC_C));
    }

    @Test
    public void testCompareWordRegression1() {
        io.regs.u.set(0xFE00);
        WordRegisterInstruction.compareWord(io, io.regs.u, new UnsignedWord(0xF800), null, false);
        assertEquals(0xFE00, io.regs.u.getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPUImmediateCorrect() throws MalformedInstructionException {
        regs.u.set(0xFE00);
        io.writeWord(0x0000, 0x1183);
        io.writeWord(0x0002, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPUDirectCorrect() throws MalformedInstructionException {
        regs.u.set(0xFE00);
        io.writeWord(0x0000, 0x1193);
        io.writeByte(0x0002, 0x0A);
        io.writeWord(0x000A, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPUIndexedCalled() throws MalformedInstructionException {
        regs.u.set(0xFE00);
        regs.x.set(extendedAddress);
        io.writeWord(0x0000, 0x11A3);
        io.writeByte(0x0002, 0x80);
        io.writeWord(extendedAddress, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPUExtendedCalled() throws MalformedInstructionException {
        regs.u.set(0xFE00);
        io.writeWord(0x0000, 0x11B3);
        io.writeWord(0x0002, extendedAddress);
        io.writeWord(extendedAddress, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPSImmediateCorrect() throws MalformedInstructionException {
        regs.s.set(0xFE00);
        io.writeWord(0x0000, 0x118C);
        io.writeWord(0x0002, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPSDirectCorrect() throws MalformedInstructionException {
        regs.s.set(0xFE00);
        io.writeWord(0x0000, 0x119C);
        io.writeByte(0x0002, 0x0A);
        io.writeWord(0x000A, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPSIndexedCalled() throws MalformedInstructionException {
        regs.s.set(0xFE00);
        regs.x.set(extendedAddress);
        io.writeWord(0x0000, 0x11AC);
        io.writeByte(0x0002, 0x80);
        io.writeWord(extendedAddress, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPSExtendedCalled() throws MalformedInstructionException {
        regs.s.set(0xFE00);
        io.writeWord(0x0000, 0x11BC);
        io.writeWord(0x0002, extendedAddress);
        io.writeWord(extendedAddress, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPYImmediateCorrect() throws MalformedInstructionException {
        regs.y.set(0xFE00);
        io.writeWord(0x0000, 0x108C);
        io.writeWord(0x0002, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPYDirectCorrect() throws MalformedInstructionException {
        regs.y.set(0xFE00);
        io.writeWord(0x0000, 0x109C);
        io.writeByte(0x0002, 0x0A);
        io.writeWord(0x000A, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPYIndexedCalled() throws MalformedInstructionException {
        regs.y.set(0xFE00);
        regs.x.set(extendedAddress);
        io.writeWord(0x0000, 0x10AC);
        io.writeByte(0x0002, 0x80);
        io.writeWord(extendedAddress, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPYExtendedCalled() throws MalformedInstructionException {
        regs.y.set(0xFE00);
        io.writeWord(0x0000, 0x10BC);
        io.writeWord(0x0002, extendedAddress);
        io.writeWord(extendedAddress, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPXImmediateCorrect() throws MalformedInstructionException {
        regs.x.set(0xFE00);
        io.writeByte(0x0000, 0x8C);
        io.writeWord(0x0001, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPXDirectCorrect() throws MalformedInstructionException {
        regs.x.set(0xFE00);
        io.writeWord(0x0000, 0x9C0A);
        io.writeWord(0x000A, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPXIndexedCalled() throws MalformedInstructionException {
        regs.x.set(0xFE00);
        regs.y.set(extendedAddress);
        io.writeWord(0x0000, 0xACA0);
        io.writeWord(extendedAddress, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPXExtendedCalled() throws MalformedInstructionException {
        regs.x.set(0xFE00);
        io.writeByte(0x0000, 0xBC);
        io.writeWord(0x0001, extendedAddress);
        io.writeWord(extendedAddress, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPDImmediateCorrect() throws MalformedInstructionException {
        regs.setD(0xFE00);
        io.writeWord(0x0000, 0x1083);
        io.writeWord(0x0002, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPDDirectCorrect() throws MalformedInstructionException {
        regs.setD(0xFE00);
        io.writeWord(0x0000, 0x1093);
        io.writeByte(0x0002, 0x0A);
        io.writeWord(0x000A, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPDIndexedCalled() throws MalformedInstructionException {
        regs.setD(0xFE00);
        regs.x.set(extendedAddress);
        io.writeWord(0x0000, 0x10A3);
        io.writeByte(0x0002, 0x80);
        io.writeWord(extendedAddress, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testCMPDExtendedCalled() throws MalformedInstructionException {
        regs.setD(0xFE00);
        io.writeWord(0x0000, 0x10B3);
        io.writeWord(0x0002, extendedAddress);
        io.writeWord(extendedAddress, 0xF800);
        cpu.executeInstruction();
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testLoadEffectiveAddressXWorksCorrectly() throws MalformedInstructionException {
        regs.y.set(extendedAddress);
        regs.x.set(0xBEEF);
        io.writeWord(0x0000, 0x30A0);
        io.writeWord(extendedAddress, 0x072E);
        cpu.executeInstruction();
        assertEquals(extendedAddress, regs.x.getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testLoadEffectiveAddressYWorksCorrectly() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.y.set(0xBEEF);
        io.writeWord(0x0000, 0x3180);
        io.writeWord(extendedAddress, 0x072E);
        cpu.executeInstruction();
        assertEquals(extendedAddress, regs.y.getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testLoadEffectiveAddressYWorksCorrectlySetsZero() throws MalformedInstructionException {
        regs.x.set(0x0000);
        regs.y.set(0xBEEF);
        io.writeWord(0x0000, 0x3180);
        io.writeWord(extendedAddress, 0x072E);
        cpu.executeInstruction();
        assertEquals(0x0000, regs.y.getInt());
        assertTrue(io.regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testLoadEffectiveAddressSWorksCorrectly() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.s.set(0xBEEF);
        io.writeWord(0x0000, 0x3280);
        io.writeWord(extendedAddress, 0x072E);
        cpu.executeInstruction();
        assertEquals(extendedAddress, regs.s.getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testLoadEffectiveAddressUWorksCorrectly() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.u.set(0xBEEF);
        io.writeWord(0x0000, 0x3380);
        io.writeWord(extendedAddress, 0x072E);
        cpu.executeInstruction();
        assertEquals(extendedAddress, regs.u.getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
    }

    @Test
    public void testAddWordWorksCorrectly() {
        WordRegisterInstruction.addWord(io, new UnsignedWord(0x0101), new UnsignedWord(0x0101), null, true);
        assertEquals(0x0202, io.regs.getD().getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddWordSetsNegativeOverflow() {
        WordRegisterInstruction.addWord(io, new UnsignedWord(0x7FFF), new UnsignedWord(0x0001), null, true);
        assertEquals(0x8000, io.regs.getD().getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddWordSetsZeroOverflowCarry() {
        WordRegisterInstruction.addWord(io, new UnsignedWord(0xFFFF), new UnsignedWord(0x0001), null, true);
        assertEquals(0x0000, io.regs.getD().getInt());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddDImmediateCorrect() throws MalformedInstructionException {
        regs.setD(0xFFFF);
        io.writeByte(0x0000, 0xC3);
        io.writeWord(0x0001, 0x0001);
        cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x0000), regs.getD());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddDDirectCorrect() throws MalformedInstructionException {
        regs.setD(0xFFFF);
        io.writeWord(0x0000, 0xD30A);
        io.writeWord(0x000A, 0x0001);
        cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x0000), regs.getD());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddDIndexedCorrect() throws MalformedInstructionException {
        regs.setD(0xFFFF);
        regs.x.set(extendedAddress);
        io.writeWord(0x0000, 0xE380);
        io.writeWord(extendedAddress, 0x0001);
        cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x0000), regs.getD());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testAddDExtendedCorrect() throws MalformedInstructionException {
        regs.setD(0xFFFF);
        io.writeByte(0x0000, 0xF3);
        io.writeWord(0x0001, extendedAddress);
        io.writeWord(extendedAddress, 0x0001);
        cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x0000), regs.getD());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractDWorksSetsOverflow() {
        WordRegisterInstruction.subtractWord(io, new UnsignedWord(0x8000), new UnsignedWord(0x7FFF), null, true);
        assertEquals(0x01, io.regs.getD().getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractDWorksSetsCarry() {
        WordRegisterInstruction.subtractWord(io, new UnsignedWord(0x7FFF), new UnsignedWord(0x8000), null, true);
        assertEquals(0xFFFF, io.regs.getD().getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertTrue(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractDWorksCorrectly() {
        WordRegisterInstruction.subtractWord(io, new UnsignedWord(0x6A00), new UnsignedWord(0x2700), null, true);
        assertEquals(0x4300, io.regs.getD().getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractDWorksSetsZero() {
        WordRegisterInstruction.subtractWord(io, new UnsignedWord(0x0001), new UnsignedWord(0x0001), null, true);
        assertEquals(0x0000, io.regs.getD().getInt());
        assertTrue(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractDWorksSetsNegativeSetsCarry() {
        WordRegisterInstruction.subtractWord(io, new UnsignedWord(0x8000), new UnsignedWord(0x8001), null, true);
        assertEquals(0xFFFF, io.regs.getD().getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertTrue(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_V));
    }

    @Test
    public void testSubtractDRegression1() {
        WordRegisterInstruction.subtractWord(io, new UnsignedWord(0xFE00), new UnsignedWord(0xF800), null, true);
        assertEquals(0x0600, regs.getD().getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
        assertFalse(io.regs.cc.isMasked(CC_N));
    }

    @Test
    public void testSubtractDImmediateCorrect() throws MalformedInstructionException {
        regs.setD(0x8000);
        io.writeByte(0x0000, 0x83);
        io.writeWord(0x0001, 0x7FFF);
        cpu.executeInstruction();
        assertEquals(0x01, regs.getD().getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractDDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x10);
        regs.setD(0x8000);
        io.writeWord(0x0000, 0x9301);
        io.writeWord(0x1001, 0x7FFF);
        cpu.executeInstruction();
        assertEquals(0x01, regs.getD().getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractDIndexedCorrect() throws MalformedInstructionException {
        regs.x.set(extendedAddress);
        regs.setD(0x8000);
        io.writeWord(0x0000, 0xA380);
        io.writeWord(extendedAddress, 0x7FFF);
        cpu.executeInstruction();
        assertEquals(0x01, regs.getD().getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }

    @Test
    public void testSubtractDExtendedCorrect() throws MalformedInstructionException {
        regs.setD(0x8000);
        io.writeByte(0x0000, 0xB3);
        io.writeWord(0x0001, extendedAddress);
        io.writeWord(extendedAddress, 0x7FFF);
        cpu.executeInstruction();
        assertEquals(0x01, regs.getD().getInt());
        assertFalse(io.regs.cc.isMasked(CC_Z));
        assertFalse(io.regs.cc.isMasked(CC_N));
        assertTrue(io.regs.cc.isMasked(CC_V));
        assertFalse(io.regs.cc.isMasked(CC_C));
    }
}
