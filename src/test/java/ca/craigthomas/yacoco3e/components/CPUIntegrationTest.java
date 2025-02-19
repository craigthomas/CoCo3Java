/*
 * Copyright (C) 2017-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import static org.junit.Assert.*;

import ca.craigthomas.yacoco3e.datatypes.RegisterSet;
import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import ca.craigthomas.yacoco3e.datatypes.UnsignedWord;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class CPUIntegrationTest
{
    private CPU cpuSpy;

    private Memory memorySpy;

    private IOController ioSpy;

    private RegisterSet registerSet;
    private RegisterSet registerSpy;

    private UnsignedByte expectedDirectByte;

    private UnsignedByte expectedExtendedByte;
    private int expectedExtendedWord;

    @Before
    public void setUp() {
        Memory memory = new Memory();
        memorySpy = spy(memory);

        registerSet = new RegisterSet();
        registerSpy = spy(registerSet);

        Cassette cassette = new Cassette();
        Cassette cassetteSpy = spy(cassette);

        Screen screen = new Screen(1);

        IOController io = new IOController(memorySpy, registerSpy, new EmulatedKeyboard(), screen, cassetteSpy);
        ioSpy = spy(io);

        CPU cpu = new CPU(ioSpy);
        cpuSpy = spy(cpu);

        ioSpy.regs.dp.set(new UnsignedByte(0xA0));
        expectedDirectByte = new UnsignedByte(0xBA);
        ioSpy.writeByte(new UnsignedWord(0xA000), new UnsignedByte(0xBA));

        expectedExtendedByte = new UnsignedByte(0xCF);
        expectedExtendedWord = 0xCFDA;
//        ioSpy.writeWord(extendedAddress, expectedExtendedWord);

        ioSpy.regs.x.set(new UnsignedWord(0xB000));
        memorySpy.enableAllRAMMode();
    }

//    @Test
//    public void testDecimalAdditionAdjustCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x19));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).decimalAdditionAdjust();
//    }
//



//    @Test
//    public void testPushSCalledCorrectly() throws IllegalIndexedPostbyteException {
//        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3401));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).pushStack(Register.S, new UnsignedByte(0x01));
//    }
//
//    @Test
//    public void testPushUCalledCorrectly() throws IllegalIndexedPostbyteException {
//        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3601));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).pushStack(Register.U, new UnsignedByte(0x01));
//    }
//
//    @Test
//    public void testPullSCalledCorrectly() throws IllegalIndexedPostbyteException {
//        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3501));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).popStack(Register.S, new UnsignedByte(0x01));
//    }
//
//    @Test
//    public void testPullUCalledCorrectly() throws IllegalIndexedPostbyteException {
//        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x3701));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).popStack(Register.U, new UnsignedByte(0x01));
//    }
//


    @Test
    public void testCWAIWorksCorrect() throws MalformedInstructionException {
        ioSpy.regs.s.set(new UnsignedWord(0xA000));
        ioSpy.regs.a.set(new UnsignedByte(0x1));
        ioSpy.regs.b.set(new UnsignedByte(0x2));
        ioSpy.regs.dp.set(new UnsignedByte(0x3));
        ioSpy.regs.cc.set(new UnsignedByte(0x4));
        ioSpy.regs.x.set(new UnsignedWord(0x5566));
        ioSpy.regs.y.set(new UnsignedWord(0x7788));
        ioSpy.regs.u.set(new UnsignedWord(0x99AA));
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

//    @Test
//    public void testLogicalShiftRightACalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.a.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x44));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).logicalShiftRight(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testRotateRightACalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.a.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x46));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).rotateRight(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testArithmeticShiftRightACalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.a.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x47));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).arithmeticShiftRight(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testArithmeticShiftLeftACalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.a.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x48));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).arithmeticShiftLeft(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testRotateLeftACalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.a.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x49));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).rotateLeft(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testDecrementACalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.a.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4A));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).decrement(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testIncrementACalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.a.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4C));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).increment(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testTestACalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.a.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4D));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).test(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testClearACalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.a.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x4F));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).clear(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testComplimentBCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x53));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).compliment(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testLogicalShiftRightBCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x54));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).logicalShiftRight(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testRotateRightBCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x56));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).rotateRight(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testArithmeticShiftRightBCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x57));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).arithmeticShiftRight(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testArithmeticShiftLeftBCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x58));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).arithmeticShiftLeft(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testRotateLeftBCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x59));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).rotateLeft(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testDecrementBCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5A));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).decrement(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testIncrementBCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5C));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).increment(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testTestBCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5D));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).test(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testClearBCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xAA));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x5F));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).clear(new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testSubtractMACalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x80AA));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractM(Register.A, new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testCompareACalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.a.set(new UnsignedByte(0xBE));
//        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x81AA));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testSubtractMCACalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x82AA));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractMC(Register.A, new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testSubtractMADirectCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x90));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractM(Register.A, expectedDirectByte);
//    }
//
//    @Test
//    public void testCompareADirectCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.a.set(new UnsignedByte(0xBE));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x91));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedDirectByte);
//    }
//
//    @Test
//    public void testSubtractMCADirectCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x92));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractMC(Register.A, expectedDirectByte);
//    }
//
//    @Test
//    public void testSubtractMAIndexedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA0));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractM(Register.A, new UnsignedByte(0x00));
//    }
//
//    @Test
//    public void testCompareAIndexedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.a.set(new UnsignedByte(0xBE));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA1));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0x0));
//    }
//
//    @Test
//    public void testSubtractMCAIndexedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA2));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractMC(Register.A, new UnsignedByte(0x0));
//    }
//
//    @Test
//    public void testSubtractMAExtendedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB0));
//        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractM(Register.A, expectedExtendedByte);
//    }
//
//    @Test
//    public void testCompareAExtendedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.a.set(new UnsignedByte(0xBE));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB1));
//        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedExtendedByte);
//    }
//
//    @Test
//    public void testSubtractMCAExtendedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB2));
//        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractMC(Register.A, expectedExtendedByte);
//    }
//
//    @Test
//    public void testSubtractMBCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC0AA));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractM(Register.B, new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testCompareBCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xBE));
//        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC1AA));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testSubtractMCBCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0xC2AA));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractMC(Register.B, new UnsignedByte(0xAA));
//    }
//
//    @Test
//    public void testSubtractMBDirectCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD0));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractM(Register.B, expectedDirectByte);
//    }
//
//    @Test
//    public void testCompareBDirectCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xBE));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD1));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedDirectByte);
//    }
//
//    @Test
//    public void testSubtractMCBDirectCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xD2));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractMC(Register.B, expectedDirectByte);
//    }
//
//    @Test
//    public void testSubtractMBIndexedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE0));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractM(Register.B, new UnsignedByte(0x00));
//    }
//
//    @Test
//    public void testCompareBIndexedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xBE));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE1));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), new UnsignedByte(0x0));
//    }
//
//    @Test
//    public void testSubtractMCBIndexedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xE2));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractMC(Register.B, new UnsignedByte(0x0));
//    }
//
//    @Test
//    public void testSubtractMBExtendedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF0));
//        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractM(Register.B, expectedExtendedByte);
//    }
//
//    @Test
//    public void testCompareBExtendedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.regs.b.set(new UnsignedByte(0xBE));
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF1));
//        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).compareByte(new UnsignedByte(0xBE), expectedExtendedByte);
//    }
//
//    @Test
//    public void testSubtractMCBExtendedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xF2));
//        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractMC(Register.B, expectedExtendedByte);
//    }
//
//    @Test
//    public void testSubtractDImmediateCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x83));
//        ioSpy.writeWord(new UnsignedWord(0x1), new UnsignedWord(0xBEEF));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractD(new UnsignedWord(0xBEEF));
//
//    }
//
//    @Test
//    public void testSubtractDDirectCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0x93));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractD(new UnsignedWord(0xBA00));
//
//    }
//
//    @Test
//    public void testSubtractDIndexedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xA3));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractD(new UnsignedWord(0x0));
//    }
//
//    @Test
//    public void testSubtractDExtendedCalled() throws IllegalIndexedPostbyteException {
//        ioSpy.writeByte(new UnsignedWord(0x0), new UnsignedByte(0xB3));
//        ioSpy.writeWord(new UnsignedWord(0x1), extendedAddress);
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).subtractD(expectedExtendedWord);
//    }
//

//
//    @Test
//    public void testLongBranchOnLowerCarryOnlySet() throws IllegalIndexedPostbyteException {
//        ioSpy.setCCCarry();
//        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1023));
//        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
//    }
//
//    @Test
//    public void testLongBranchOnLowerZeroOnlySet() throws IllegalIndexedPostbyteException {
//        ioSpy.setCCZero();
//        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1023));
//        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
//        cpuSpy.executeInstruction();
//        verify(cpuSpy).branchLong(new UnsignedWord(0xBEEF));
//    }
//
//    @Test
//    public void testLongBranchOnLowerNeitherSet() throws IllegalIndexedPostbyteException {
//        ioSpy.writeWord(new UnsignedWord(0x0), new UnsignedWord(0x1023));
//        ioSpy.writeWord(new UnsignedWord(0x2), new UnsignedWord(0xBEEF));
//        cpuSpy.executeInstruction();
//        assertEquals(new UnsignedWord(0x04), ioSpy.regs.getPC());
//    }

    //    @Test
//    public void testSubtractMWordWorksCorrectly() {
//        registerSet.setD(new UnsignedWord(0x8000));
//        cpu.subtractD(new UnsignedWord(0x1));
//        assertEquals(new UnsignedWord(0x7FFF), registerSet.getD());
//        assertFalse(io.regs.cc.isMasked(CC_Z));
//        assertFalse(io.regs.cc.isMasked(CC_N));
//    }
//
//    @Test
//    public void testSubtractMWordSetsZero() {
//        registerSet.setD(new UnsignedWord(0x1));
//        cpu.subtractD(new UnsignedWord(0x1));
//        assertEquals(new UnsignedWord(0x0), registerSet.getD());
//        assertTrue(io.regs.cc.isMasked(CC_Z));
//        assertFalse(io.regs.cc.isMasked(CC_N));
//    }
//
//    @Test
//    public void testSubtractMWordSetsNegative() {
//        registerSet.setD(new UnsignedWord(0x8100));
//        cpu.subtractD(new UnsignedWord(0x100));
//        assertEquals(new UnsignedWord(0x8000), registerSet.getD());
//        assertFalse(io.regs.cc.isMasked(CC_Z));
//        assertTrue(io.regs.cc.isMasked(CC_N));
//    }
//

//
//
}