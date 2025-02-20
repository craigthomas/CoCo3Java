/*
 * Copyright (C) 2022-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;
import org.junit.Before;
import org.junit.Test;

import static ca.craigthomas.yacoco3e.datatypes.AddressingMode.*;
import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;
import static org.junit.Assert.*;

public class InstructionTest {
    private IOController io;
    private RegisterSet regs;
    private CPU cpu;
    private Memory memory;
    private int extendedAddress;

    @Before
    public void setUp() {
        Screen screen = new Screen(1);
        Cassette cassette = new Cassette();
        regs = new RegisterSet();
        memory = new Memory();
        io = new IOController(memory, regs, new EmulatedKeyboard(), screen, cassette);
        cpu = new CPU(io);
        extendedAddress = 0xC0A0;
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

//    @Test
//    public void testGetIndexedZeroOffsetIndirect() throws MalformedInstructionException {
//        Instruction instruction = new ByteInstruction(0x6F, 4, "CLRM", INDEXED, ByteInstruction::clear);
//        regs.x.set(new UnsignedWord(0xB000));
//        io.writeWord(0xB000, 0xBEEF);
//        io.writeByte(0x0000, 0x94);
//        instruction.getIndexed(io);
//        System.out.println(instruction.addressRead);
//        assertEquals(1, instruction.numBytesRead);
////        assertEquals(0xB000, instruction.addressRead.getInt());
//        assertEquals(0xBE, instruction.byteRead.getShort());
//        assertEquals(0xBEEF, instruction.wordRead.getInt());
//        assertEquals(0x0001, regs.pc.getInt());
//    }

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
