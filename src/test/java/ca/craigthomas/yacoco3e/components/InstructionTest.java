/*
 * Copyright (C) 2022-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;
import org.junit.Before;
import org.junit.Test;

import static ca.craigthomas.yacoco3e.datatypes.AddressingMode.*;
import static org.junit.Assert.*;

public class InstructionTest {
    private IOController io;
    private RegisterSet regs;

    @Before
    public void setUp() {
        Screen screen = new Screen(1);
        Cassette cassette = new Cassette();
        regs = new RegisterSet();
        Memory memory = new Memory();
        io = new IOController(memory, regs, new EmulatedKeyboard(), screen, cassette);
        io.regs.pc.set(0);
    }

    @Test
    public void testGetImmediateByteCorrect() {
        Instruction instruction = new ByteRegisterInstruction(0x85, 2, "BITA", IMMEDIATE, ByteRegisterInstruction::bitTest, Register.A);
        io.writeWord(0x1234, 0xAABB);
        regs.pc.set(0x1234);
        instruction.getImmediate(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xAA, instruction.byteRead.getShort());
        assertEquals(0xAABB, instruction.wordRead.getInt());
        assertEquals(0x1235, regs.pc.getInt());
        assertEquals(0x1234, instruction.addressRead.getInt());
    }

    @Test
    public void testGetImmediateWordCorrect() {
        Instruction instruction = new WordRegisterInstruction(0x8E, 3, "LDX",  IMMEDIATE, WordRegisterInstruction::loadWordRegister, Register.X);
        io.writeWord(0x1234, 0xAABB);
        regs.pc.set(0x1234);
        instruction.getImmediate(io);
        assertEquals(2, instruction.numBytesRead);
        assertEquals(0xAA, instruction.byteRead.getShort());
        assertEquals(0xAABB, instruction.wordRead.getInt());
        assertEquals(0x1236, regs.pc.getInt());
        assertEquals(0x1234, instruction.addressRead.getInt());
    }

    @Test
    public void testGetDirectReadsAddressFromDPAndPC() {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        io.writeByte(0x00BE, 0xCD);
        io.writeByte(0xABCD, 0xFF);
        io.writeByte(0xABCE, 0xEE);
        regs.pc.set(0xBE);
        regs.dp.set(0xAB);
        instruction.getDirect(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xFF, instruction.byteRead.getShort());
        assertEquals(0xFFEE, instruction.wordRead.getInt());
        assertEquals(0x00BF, regs.pc.getInt());
        assertEquals(0xABCD, instruction.addressRead.getInt());
    }

    @Test
    public void testGetExtendedWorksCorrectly() {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", EXTENDED, ByteInstruction::clear);
        instruction.isByteSized = false;
        io.writeWord(0x1234, 0xABCD);
        io.writeWord(0xABCD, 0xDEAD);
        regs.pc.set(0x1234);
        instruction.getExtended(io);
        assertEquals(2, instruction.numBytesRead);
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x1236, regs.pc.getInt());
        assertEquals(0xABCD, instruction.addressRead.getInt());
    }

    @Test
    public void testGetIndexedZeroOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeWord(0xB000, 0xAABB);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xAA, instruction.byteRead.getShort());
        assertEquals(0xAABB, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xB000, instruction.addressRead.getInt());
    }

    @Test
    public void testGetIndexedZeroOffsetIndirect() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeWord(0xB000, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        io.writeByte(0x0000, 0x94);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
    }

    @Test
    public void testGetIndexed5BitPositiveOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeWord(0xB001, 0xDEAD);
        io.writeByte(0x0000, 0x01);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xB001, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
    }

    @Test
    public void testGetIndexed5BitNegativeOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeByte(0x0000, 0x1F);
        io.writeWord(0xAFFF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xAFFF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
    }

    @Test
    public void testGetIndexedRPostIncrement() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeByte(0x0000, 0x80);
        io.writeWord(0xB000, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xB000, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xB001, regs.x.getInt());
    }

    @Test
    public void testGetIndexedRPostIncrementTwice() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeByte(0x0000, 0x81);
        io.writeWord(0xB000, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xB000, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xB002, regs.x.getInt());
    }

    @Test
    public void testGetIndexedRPostIncrementTwiceIndirect() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeByte(0x0000, 0x91);
        io.writeWord(0xB000, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(2, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xB002, regs.x.getInt());
    }

    @Test
    public void testGetIndexedRDecrement() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeByte(0x0000, 0x82);
        io.writeWord(0xAFFF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xAFFF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xAFFF, regs.x.getInt());
    }

    @Test
    public void testGetIndexedRDecrementTwice() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeByte(0x0000, 0x83);
        io.writeWord(0xAFFE, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xAFFE, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xAFFE, regs.x.getInt());
    }

    @Test
    public void testGetIndexedRDecrementTwiceIndirect() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeByte(0x0000, 0x93);
        io.writeWord(0xAFFE, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xAFFE, regs.x.getInt());
    }

    @Test
    public void testGetIndexedROnly() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeByte(0x0000, 0x84);
        io.writeWord(0xB000, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xB000, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexedRWithBOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        regs.b.set(0x0B);
        io.writeByte(0x0000, 0x85);
        io.writeWord(0xB00B, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xB00B, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexedRWithBOffsetIndirect() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        regs.b.set(0x0B);
        io.writeByte(0x0000, 0x95);
        io.writeWord(0xB00B, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexedRWithAOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        regs.a.set(0x0A);
        io.writeByte(0x0000, 0x86);
        io.writeWord(0xB00A, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xB00A, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexedRWithAOffsetIndirect() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        regs.a.set(0x0A);
        io.writeByte(0x0000, 0x96);
        io.writeWord(0xB00A, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexed8BitPositiveOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeWord(0x0000, 0x8802);
        io.writeWord(0xB002, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(2, instruction.numBytesRead);
        assertEquals(0xB002, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0002, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexed8BitNegativeOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeWord(0x0000, 0x88FE);
        io.writeWord(0xAFFE, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(2, instruction.numBytesRead);
        assertEquals(0xAFFE, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0002, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexed8BitPositiveOffsetIndirect() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeWord(0x0000, 0x9802);
        io.writeWord(0xB002, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(2, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0002, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexed8BitNegativeOffsetIndirect() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeWord(0x0000, 0x98FE);
        io.writeWord(0xAFFE, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(2, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0002, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexed16BitPositiveOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeByte(0x0000, 0x89);
        io.writeWord(0x0001, 0x0200);
        io.writeWord(0xB200, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(3, instruction.numBytesRead);
        assertEquals(0xB200, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0003, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexed16BitNegativeOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeByte(0x0000, 0x89);
        io.writeWord(0x0001, 0xFE00);
        io.writeWord(0xAE00, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(3, instruction.numBytesRead);
        assertEquals(0xAE00, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0003, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexed16BitPositiveOffsetIndirect() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeByte(0x0000, 0x99);
        io.writeWord(0x0001, 0x0200);
        io.writeWord(0xB200, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(3, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0003, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexed16BitNegativeOffsetIndirect() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        io.writeByte(0x0000, 0x99);
        io.writeWord(0x0001, 0xFE00);
        io.writeWord(0xAE00, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(3, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0003, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexedRWithDOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        regs.setD(0x0200);
        io.writeByte(0x0000, 0x8B);
        io.writeWord(0xB200, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xB200, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexedRWithDOffsetIndirect() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.x.set(0xB000);
        regs.setD(0x0200);
        io.writeByte(0x0000, 0x9B);
        io.writeWord(0xB200, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(1, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0001, regs.pc.getInt());
        assertEquals(0xB000, regs.x.getInt());
    }

    @Test
    public void testGetIndexedPCWith8BitPositiveOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        io.writeWord(0x0000, 0x8C0A);
        io.writeWord(0x000C, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(2, instruction.numBytesRead);
        assertEquals(0x000C, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testGetIndexedPCWith8BitNegativeOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.pc.set(0x000A);
        io.writeWord(0x000A, 0x8CFC);
        io.writeWord(0x0008, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(2, instruction.numBytesRead);
        assertEquals(0x0008, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x000C, regs.pc.getInt());
    }

    @Test
    public void testGetIndexedPCWith8BitPositiveOffsetIndexed() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        io.writeWord(0x0000, 0x9C0A);
        io.writeWord(0x000C, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(2, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0002, regs.pc.getInt());
    }

    @Test
    public void testGetIndexedPCWith8BitNegativeOffsetIndexed() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.pc.set(0x000A);
        io.writeWord(0x000A, 0x9CFC);
        io.writeWord(0x0008, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(2, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x000C, regs.pc.getInt());
    }

    @Test
    public void testGetIndexedPCWith16BitPositiveOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        io.writeByte(0x0000, 0x8D);
        io.writeWord(0x0001, 0x0200);
        io.writeWord(0x0203, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(3, instruction.numBytesRead);
        assertEquals(0x0203, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0003, regs.pc.getInt());
    }

    @Test
    public void testGetIndexedPCWith16BitNegativeOffset() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.pc.set(0xA000);
        io.writeByte(0xA000, 0x8D);
        io.writeWord(0xA001, 0xFE00);
        io.writeWord(0x9E03, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(3, instruction.numBytesRead);
        assertEquals(0x9E03, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0xA003, regs.pc.getInt());
    }

    @Test
    public void testGetIndexedPCWith16BitPositiveOffsetIndexed() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        io.writeByte(0x0000, 0x9D);
        io.writeWord(0x0001, 0x0200);
        io.writeWord(0x0203, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(3, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0003, regs.pc.getInt());
    }

    @Test
    public void testGetIndexedPCWith16BitNegativeOffsetIndexed() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        regs.pc.set(0xA000);
        io.writeByte(0xA000, 0x9D);
        io.writeWord(0xA001, 0xFE00);
        io.writeWord(0x9E03, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(3, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0xA003, regs.pc.getInt());
    }

    @Test
    public void testGetIndexedExtendedIndirect() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        io.writeByte(0x0000, 0x9F);
        io.writeWord(0x0001, 0xB000);
        io.writeWord(0xB000, 0xBEEF);
        io.writeWord(0xBEEF, 0xDEAD);
        instruction.getIndexed(io);
        assertEquals(3, instruction.numBytesRead);
        assertEquals(0xBEEF, instruction.addressRead.getInt());
        assertEquals(0xDE, instruction.byteRead.getShort());
        assertEquals(0xDEAD, instruction.wordRead.getInt());
        assertEquals(0x0003, regs.pc.getInt());
    }

    @Test(expected = MalformedInstructionException.class)
    public void testGetIndexedIllegalPostByteExceptionOnRPostIncrement() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        io.writeByte(0x0000, 0x90);
        instruction.getIndexed(io);
    }

    @Test(expected = MalformedInstructionException.class)
    public void testGetIndexedIllegalPostByteExceptionOnRPostDecrement() throws MalformedInstructionException {
        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
        io.writeByte(0x0000, 0x92);
        instruction.getIndexed(io);
    }
}
