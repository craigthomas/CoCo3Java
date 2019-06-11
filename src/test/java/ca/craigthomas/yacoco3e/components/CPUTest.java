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
import org.junit.Ignore;
import org.junit.Test;

public class CPUTest
{
    private CPU cpu;
    private Memory memory;
    private IOController io;
    private RegisterSet registerSet;

    @Before
    public void setUp() {
        memory = new Memory();
        registerSet = new RegisterSet();
        Screen screen = new Screen(1);
        Cassette cassette = new Cassette();
        io = new IOController(memory, registerSet, new Keyboard(), screen, cassette);
        cpu = new CPU(io);
    }

    @Test
    public void testNegateCorrect() {
        UnsignedByte result = cpu.negate(new UnsignedByte(0xFC));
        assertEquals(new UnsignedByte(0x04), result);
        assertTrue(io.ccCarrySet());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccNegativeSet());
        assertFalse(io.ccZeroSet());
    }

    @Test
    public void testNegateAllOnes() {
        UnsignedByte result = cpu.negate(new UnsignedByte(0xFF));
        assertEquals(new UnsignedByte(1), result);
    }

    @Test
    public void testNegateOne() {
        UnsignedByte result = cpu.negate(new UnsignedByte(0x01));
        assertEquals(new UnsignedByte(0xFF), result);
    }

    @Test
    public void testNegateSetsOverflowFlag() {
        cpu.negate(new UnsignedByte(0x01));
        assertTrue(io.ccOverflowSet());
    }

    @Test
    public void testNegateSetsNegativeFlag() {
        cpu.negate(new UnsignedByte(0x01));
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testNegateEdgeCase() {
        UnsignedByte result = cpu.negate(new UnsignedByte(0x80));
        assertEquals(new UnsignedByte(0x80), result);
        assertTrue(io.ccNegativeSet());
        assertTrue(io.ccOverflowSet());
        assertTrue(io.ccCarrySet());
    }

    @Test
    public void testNegateEdgeCase1() {
        UnsignedByte result = cpu.negate(new UnsignedByte(0x0));
        assertEquals(new UnsignedByte(0x0), result);
        assertFalse(io.ccNegativeSet());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccCarrySet());
        assertTrue(io.ccZeroSet());
    }

    @Test
    public void testComplimentWorksCorrectly() {
        UnsignedByte value = new UnsignedByte(0xE6);
        UnsignedByte result = cpu.compliment(value);
        assertEquals(new UnsignedByte(0x19), result);
        assertTrue(io.ccCarrySet());
        assertFalse(io.ccNegativeSet());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccZeroSet());
    }

    @Test
    public void testComplementAllOnes() {
        UnsignedByte result = cpu.compliment(new UnsignedByte(0xFF));
        assertEquals(new UnsignedByte(0), result);
    }

    @Test
    public void testComplementOne() {
        UnsignedByte result = cpu.compliment(new UnsignedByte(0x01));
        assertEquals(new UnsignedByte(0xFE), result);
    }

    @Test
    public void testComplementSetsCarryFlag() {
        cpu.compliment(new UnsignedByte(0x01));
        assertTrue(io.ccCarrySet());
    }

    @Test
    public void testComplementSetsNegativeFlagCorrect() {
        cpu.compliment(new UnsignedByte(0x01));
        assertTrue(io.ccNegativeSet());

        cpu.compliment(new UnsignedByte(0xFE));
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testComplementSetsZeroFlagCorrect() {
        UnsignedByte result = cpu.compliment(new UnsignedByte(0xFF));
        assertEquals(0, result.getShort());
        assertTrue(io.ccZeroSet());
    }

    @Test
    public void testLogicalShiftRightCorrect() {
        UnsignedByte result = cpu.logicalShiftRight(new UnsignedByte(0x9E));
        assertEquals(new UnsignedByte(0x4F), result);
        assertFalse(io.ccCarrySet());
        assertFalse(io.ccNegativeSet());
        assertFalse(io.ccZeroSet());
    }

    @Test
    public void testLogicalShiftRightMovesOneBitCorrect() {
        UnsignedByte result = cpu.logicalShiftRight(new UnsignedByte(0x2));
        assertEquals(new UnsignedByte(1), result);
        assertFalse(io.ccCarrySet());
    }

    @Test
    public void testLogicalShiftRightMovesOneBitToZero() {
        UnsignedByte result = cpu.logicalShiftRight(new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0), result);
        assertTrue(io.ccCarrySet());
    }

    @Test
    public void testLogicalShiftRightSetsZeroBit() {
        UnsignedByte result = cpu.logicalShiftRight(new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0), result);
        assertTrue(io.ccZeroSet());
        assertTrue(io.ccCarrySet());
    }

    @Test
    public void testRotateRightMovesOneBitCorrect() {
        UnsignedByte result = cpu.rotateRight(new UnsignedByte(0x2));
        assertEquals(new UnsignedByte(1), result);
        assertFalse(io.ccCarrySet());
    }

    @Test
    public void testRotateRightMovesOneBitCorrectWithCarry() {
        io.setCCCarry();
        UnsignedByte result = cpu.rotateRight(new UnsignedByte(0x2));
        assertEquals(new UnsignedByte(0x81), result);
        assertFalse(io.ccCarrySet());
    }

    @Test
    public void testRotateRightMovesOneBitToZero() {
        UnsignedByte result = cpu.rotateRight(new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0), result);
        assertTrue(io.ccCarrySet());
    }

    @Test
    public void testRotateRightSetsZeroBit() {
        UnsignedByte result = cpu.rotateRight(new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0), result);
        assertTrue(io.ccZeroSet());
        assertTrue(io.ccCarrySet());
    }

    @Test
    public void testRotateRightSetsNegativeBit() {
        io.setCCCarry();
        UnsignedByte result = cpu.rotateRight(new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0x80), result);
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccCarrySet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testArithmeticShiftRightCorrect() {
        UnsignedByte result = cpu.arithmeticShiftRight(new UnsignedByte(0x85));
        assertEquals(0xC2, result.getShort());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
        assertTrue(io.ccCarrySet());
    }

    @Test
    public void testArithmeticShiftRightOneCorrect() {
        UnsignedByte result = cpu.arithmeticShiftRight(new UnsignedByte(0x1));
        assertEquals(0, result.getShort());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
        assertTrue(io.ccCarrySet());
    }

    @Test
    public void testArithmeticShiftRightHighBitRetained() {
        UnsignedByte result = cpu.arithmeticShiftRight(new UnsignedByte(0x81));
        assertEquals(0xC0, result.getShort());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
        assertTrue(io.ccCarrySet());
    }

    @Test
    public void testArithmeticShiftLeftCorrect() {
        UnsignedByte result = cpu.arithmeticShiftLeft(new UnsignedByte(0x55));
        assertEquals(0xAA, result.getShort());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccOverflowSet());
        assertTrue(io.ccNegativeSet());
        assertFalse(io.ccCarrySet());
    }

    @Test
    public void testArithmeticShiftLeftCorrectSecondTest() {
        UnsignedByte result = cpu.arithmeticShiftLeft(new UnsignedByte(0x8A));
        assertEquals(0x14, result.getShort());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccOverflowSet());
        assertFalse(io.ccNegativeSet());
        assertTrue(io.ccCarrySet());
    }

    @Test
    public void testArithmeticShiftLeftOneCorrect() {
        UnsignedByte result = cpu.arithmeticShiftLeft(new UnsignedByte(0x1));
        assertEquals(0x2, result.getShort());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccNegativeSet());
        assertFalse(io.ccCarrySet());
    }

    @Test
    public void testArithmeticShiftLeftHighBitShiftedToCarry() {
        UnsignedByte result = cpu.arithmeticShiftLeft(new UnsignedByte(0x81));
        assertEquals(0x2, result.getShort());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccOverflowSet());
        assertFalse(io.ccNegativeSet());
        assertTrue(io.ccCarrySet());
    }

    @Test
    public void testRotateLeftOneCorrect() {
        UnsignedByte result = cpu.rotateLeft(new UnsignedByte(0x1));
        assertEquals(0x2, result.getShort());
        assertFalse(io.ccCarrySet());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccNegativeSet());
        assertFalse(io.ccZeroSet());
    }

    @Test
    public void testRotateLeftSetsCarry() {
        UnsignedByte result = cpu.rotateLeft(new UnsignedByte(0x80));
        assertEquals(0x0, result.getShort());
        assertTrue(io.ccCarrySet());
        assertTrue(io.ccOverflowSet());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testRotateLeftRotatesCarryToLowestBit() {
        io.setCCCarry();
        UnsignedByte result = cpu.rotateLeft(new UnsignedByte(0x1));
        assertEquals(0x3, result.getShort());
        assertFalse(io.ccCarrySet());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testRotateLeftClearsOverflow() {
        UnsignedByte result = cpu.rotateLeft(new UnsignedByte(0xC0));
        assertEquals(0x80, result.getShort());
        assertTrue(io.ccCarrySet());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testDecrementWorksCorrectly() {
        UnsignedByte result = cpu.decrement(new UnsignedByte(0xC4));
        assertEquals(0xC3, result.getShort());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testDecrementOneCorrect() {
        UnsignedByte result = cpu.decrement(new UnsignedByte(0x1));
        assertEquals(0x0, result.getShort());
        assertFalse(io.ccOverflowSet());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testDecrementZeroCorrect() {
        UnsignedByte result = cpu.decrement(new UnsignedByte(0x0));
        assertEquals(0xFF, result.getShort());
        assertTrue(io.ccOverflowSet());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testDecrementHighValueCorrect() {
        UnsignedByte result = cpu.decrement(new UnsignedByte(0xFF));
        assertEquals(0xFE, result.getShort());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testIncrementOneCorrect() {
        UnsignedByte result = cpu.increment(new UnsignedByte(0x1));
        assertEquals(0x2, result.getShort());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testIncrementSetsOverflow() {
        UnsignedByte result = cpu.increment(new UnsignedByte(0x7F));
        assertEquals(0x80, result.getShort());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccOverflowSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testIncrementSetsZero() {
        UnsignedByte result = cpu.increment(new UnsignedByte(0xFF));
        assertEquals(0x0, result.getShort());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testTestZeroCorrect() {
        cpu.test(new UnsignedByte(0x0));
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testTestNegativeCorrect() {
        cpu.test(new UnsignedByte(0x81));
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testJumpUpdatesPCCorrect() {
        UnsignedWord address = new UnsignedWord(0xABCD);
        cpu.jump(address);
        assertEquals(address, registerSet.getPC());
    }

    @Test
    public void testClearWorksCorrect() {
        UnsignedByte result = cpu.clear(new UnsignedByte(0x4));
        assertEquals(0, result.getShort());
        assertTrue(io.ccZeroSet());
    }

    @Test
    public void testCompareByteWorksCorrectly() {
        UnsignedByte byte1 = new UnsignedByte(0xA3);
        UnsignedByte byte2 = new UnsignedByte(0x11);
        cpu.compareByte(byte1, byte2);
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccCarrySet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testCompareWordWorksCorrectly() {
        UnsignedWord word1 = new UnsignedWord(0x021A);
        UnsignedWord word2 = new UnsignedWord(0x072E);
        cpu.compareWord(word1, word2);
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccOverflowSet());
        assertTrue(io.ccNegativeSet());
        assertTrue(io.ccCarrySet());
    }

    @Test
    public void testCompareWordSetsZero() {
        UnsignedWord word1 = new UnsignedWord();
        UnsignedWord word2 = new UnsignedWord();
        cpu.compareWord(word1, word2);
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testCompareWordSetsNegative() {
        UnsignedWord word1 = new UnsignedWord(0x1);
        UnsignedWord word2 = new UnsignedWord(0x7F);
        cpu.compareWord(word1, word2);
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testLoadRegisterWorksCorrectly() {
        UnsignedWord word1 = new UnsignedWord(0xBEEF);
        cpu.loadRegister(Register.S, word1);
        assertEquals(new UnsignedWord(0xBEEF), registerSet.getS());

        cpu.loadRegister(Register.U, word1);
        assertEquals(new UnsignedWord(0xBEEF), registerSet.getU());

        cpu.loadRegister(Register.Y, word1);
        assertEquals(new UnsignedWord(0xBEEF), registerSet.getY());

        cpu.loadRegister(Register.X, word1);
        assertEquals(new UnsignedWord(0xBEEF), registerSet.getX());
    }

    @Test
    public void testLoadRegisterSetsZero() {
        UnsignedWord word1 = new UnsignedWord();
        cpu.loadRegister(Register.S, word1);
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testLoadRegisterSetsZeroRegisterD() {
        io.setD(new UnsignedWord(1));
        UnsignedWord word1 = new UnsignedWord();
        cpu.loadRegister(Register.D, word1);
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testLoadRegisterSetsNegative() {
        UnsignedWord word1 = new UnsignedWord(0x8100);
        cpu.loadRegister(Register.S, word1);
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testStoreRegisterWorksCorrectly() {
        UnsignedWord address = new UnsignedWord(0xA000);
        registerSet.getS().set(new UnsignedWord(0xBEEF));
        cpu.storeWordRegister(Register.S, address);
        assertEquals(new UnsignedWord(0xBEEF), io.readWord(address));

        io.writeWord(address, new UnsignedWord());
        registerSet.getU().set(new UnsignedWord(0xBEEF));
        cpu.storeWordRegister(Register.U, address);
        assertEquals(new UnsignedWord(0xBEEF), io.readWord(address));

        io.writeWord(address, new UnsignedWord());
        registerSet.getY().set(new UnsignedWord(0xBEEF));
        cpu.storeWordRegister(Register.Y, address);
        assertEquals(new UnsignedWord(0xBEEF), io.readWord(address));

        io.writeWord(address, new UnsignedWord());
        registerSet.getX().set(new UnsignedWord(0xBEEF));
        cpu.storeWordRegister(Register.X, address);
        assertEquals(new UnsignedWord(0xBEEF), io.readWord(address));
    }

    @Test
    public void testStoreRegisterSetsZero() {
        UnsignedWord address = new UnsignedWord(0xA000);
        registerSet.getS().set(new UnsignedWord(0x0));
        cpu.storeWordRegister(Register.S, address);
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testStoreRegisterSetsNegative() {
        UnsignedWord address = new UnsignedWord(0xA000);
        registerSet.getS().set(new UnsignedWord(0x8100));
        cpu.storeWordRegister(Register.S, address);
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testLoadEffectiveAddressWorksCorrectly() {
        UnsignedWord value = new UnsignedWord(0xBEEF);
        cpu.loadEffectiveAddress(Register.X, value);
        assertEquals(value, registerSet.getX());
        assertFalse(io.ccZeroSet());
    }

    @Test
    public void testLoadEffectiveAddressSetsZero() {
        UnsignedWord value = new UnsignedWord(0x0);
        cpu.loadEffectiveAddress(Register.X, value);
        assertEquals(value, registerSet.getX());
        assertTrue(io.ccZeroSet());
    }

    @Test
    public void testPushStackAll() {
        registerSet.setS(new UnsignedWord(0xA000));
        registerSet.setA(new UnsignedByte(0x1));
        registerSet.setB(new UnsignedByte(0x2));
        registerSet.setDP(new UnsignedByte(0x3));
        registerSet.setCC(new UnsignedByte(0x4));
        registerSet.setX(new UnsignedWord(0x5566));
        registerSet.setY(new UnsignedWord(0x7788));
        registerSet.setU(new UnsignedWord(0x99AA));
        registerSet.setPC(new UnsignedWord(0xBBCC));
        int bytes = cpu.pushStack(Register.S, new UnsignedByte(0xFF));
        assertEquals(12, bytes);
        assertEquals(new UnsignedByte(0xCC), memory.readByte(new UnsignedWord(0x9FFF)));
        assertEquals(new UnsignedByte(0xBB), memory.readByte(new UnsignedWord(0x9FFE)));
        assertEquals(new UnsignedByte(0xAA), memory.readByte(new UnsignedWord(0x9FFD)));
        assertEquals(new UnsignedByte(0x99), memory.readByte(new UnsignedWord(0x9FFC)));
        assertEquals(new UnsignedByte(0x88), memory.readByte(new UnsignedWord(0x9FFB)));
        assertEquals(new UnsignedByte(0x77), memory.readByte(new UnsignedWord(0x9FFA)));
        assertEquals(new UnsignedByte(0x66), memory.readByte(new UnsignedWord(0x9FF9)));
        assertEquals(new UnsignedByte(0x55), memory.readByte(new UnsignedWord(0x9FF8)));
        assertEquals(new UnsignedByte(0x03), memory.readByte(new UnsignedWord(0x9FF7)));
        assertEquals(new UnsignedByte(0x02), memory.readByte(new UnsignedWord(0x9FF6)));
        assertEquals(new UnsignedByte(0x01), memory.readByte(new UnsignedWord(0x9FF5)));
        assertEquals(new UnsignedByte(0x04), memory.readByte(new UnsignedWord(0x9FF4)));
    }

    @Test
    public void testPopStackAll() {
        registerSet.setS(new UnsignedWord(0x9FF4));
        memory.writeByte(new UnsignedWord(0x9FFF), new UnsignedByte(0xCC));
        memory.writeByte(new UnsignedWord(0x9FFE), new UnsignedByte(0xBB));
        memory.writeByte(new UnsignedWord(0x9FFD), new UnsignedByte(0xAA));
        memory.writeByte(new UnsignedWord(0x9FFC), new UnsignedByte(0x99));
        memory.writeByte(new UnsignedWord(0x9FFB), new UnsignedByte(0x88));
        memory.writeByte(new UnsignedWord(0x9FFA), new UnsignedByte(0x77));
        memory.writeByte(new UnsignedWord(0x9FF9), new UnsignedByte(0x66));
        memory.writeByte(new UnsignedWord(0x9FF8), new UnsignedByte(0x55));
        memory.writeByte(new UnsignedWord(0x9FF7), new UnsignedByte(0x03));
        memory.writeByte(new UnsignedWord(0x9FF6), new UnsignedByte(0x02));
        memory.writeByte(new UnsignedWord(0x9FF5), new UnsignedByte(0x01));
        memory.writeByte(new UnsignedWord(0x9FF4), new UnsignedByte(0x04));
        int bytes = cpu.popStack(Register.S, new UnsignedByte(0xFF));
        assertEquals(12, bytes);
        assertEquals(new UnsignedByte(0x1), registerSet.getA());
        assertEquals(new UnsignedByte(0x2), registerSet.getB());
        assertEquals(new UnsignedByte(0x3), registerSet.getDP());
        assertEquals(new UnsignedByte(0x4), registerSet.getCC());
        assertEquals(new UnsignedWord(0x5566), registerSet.getX());
        assertEquals(new UnsignedWord(0x7788), registerSet.getY());
        assertEquals(new UnsignedWord(0x99AA), registerSet.getU());
        assertEquals(new UnsignedWord(0xBBCC), registerSet.getPC());
    }

    @Test
    public void testSubtractMWorksCorrectly() {
        registerSet.setA(new UnsignedByte(0x6A));
        cpu.subtractM(Register.A, new UnsignedByte(0x27));
        assertEquals(new UnsignedByte(0x43), registerSet.getA());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
        assertFalse(io.ccOverflowSet());
    }

    @Test
    public void testSubtractMWorksSetsZero() {
        registerSet.setA(new UnsignedByte(0x1));
        registerSet.setB(new UnsignedByte(0x1));
        cpu.subtractM(Register.A, new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0x0), registerSet.getA());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());

        registerSet.setA(new UnsignedByte(0x1));
        registerSet.setB(new UnsignedByte(0x1));
        cpu.subtractM(Register.B, new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0x0), registerSet.getB());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testSubtractMWorksSetsNegative() {
        registerSet.setA(new UnsignedByte(0x81));
        cpu.subtractM(Register.A, new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0x80), registerSet.getA());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testSubtractMCWorksCorrectly() {
        registerSet.setA(new UnsignedByte(0x67));
        registerSet.setCC(new UnsignedByte(IOController.CC_C));
        cpu.subtractMC(Register.A, new UnsignedByte(0x24));
        assertEquals(new UnsignedByte(0x42), registerSet.getA());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testSubtractMCWorksSetsZero() {
        registerSet.setA(new UnsignedByte(0x1));
        registerSet.setB(new UnsignedByte(0x1));
        cpu.subtractMC(Register.A, new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0x0), registerSet.getA());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());

        registerSet.setA(new UnsignedByte(0x1));
        registerSet.setB(new UnsignedByte(0x1));
        cpu.subtractMC(Register.B, new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0x0), registerSet.getB());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testSubtractMCWorksSetsNegative() {
        registerSet.setA(new UnsignedByte(0x81));
        cpu.subtractMC(Register.A, new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0x80), registerSet.getA());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testLogicalAndWorksCorrectly() {
        UnsignedByte byte1 = new UnsignedByte(0x21);
        registerSet.setA(new UnsignedByte(0x20));
        cpu.logicalAnd(Register.A, byte1);
        assertEquals(new UnsignedByte(0x20), registerSet.getA());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testLogicalAndSetsZero() {
        UnsignedByte byte1 = new UnsignedByte(0x1);
        registerSet.setA(new UnsignedByte(0x20));
        registerSet.setB(new UnsignedByte(0x20));
        cpu.logicalAnd(Register.A, byte1);
        assertEquals(new UnsignedByte(0x0), registerSet.getA());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());

        registerSet.setA(new UnsignedByte(0x20));
        registerSet.setB(new UnsignedByte(0x20));
        cpu.logicalAnd(Register.B, byte1);
        assertEquals(new UnsignedByte(0x0), registerSet.getB());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testLogicalAndSetsNegative() {
        UnsignedByte byte1 = new UnsignedByte(0x81);
        registerSet.setA(new UnsignedByte(0x81));
        cpu.logicalAnd(Register.A, byte1);
        assertEquals(new UnsignedByte(0x81), registerSet.getA());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testSubtractMWordWorksCorrectly() {
        registerSet.setD(new UnsignedWord(0x8000));
        cpu.subtractD(new UnsignedWord(0x1));
        assertEquals(new UnsignedWord(0x7FFF), registerSet.getD());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testSubtractMWordSetsZero() {
        registerSet.setD(new UnsignedWord(0x1));
        cpu.subtractD(new UnsignedWord(0x1));
        assertEquals(new UnsignedWord(0x0), registerSet.getD());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testSubtractMWordSetsNegative() {
        registerSet.setD(new UnsignedWord(0x8100));
        cpu.subtractD(new UnsignedWord(0x100));
        assertEquals(new UnsignedWord(0x8000), registerSet.getD());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testLoadByteRegisterWorksCorrectly() {
        cpu.loadByteRegister(Register.A, new UnsignedByte(0xAA));
        assertEquals(new UnsignedByte(0xAA), registerSet.getA());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());

        cpu.loadByteRegister(Register.B, new UnsignedByte(0xBB));
        assertEquals(new UnsignedByte(0xBB), registerSet.getB());
        assertFalse(io.ccZeroSet());
        assertTrue(io.ccNegativeSet());
    }

    @Test
    public void testLoadByteRegisterSetsZero() {
        registerSet.setA(new UnsignedByte(0xAA));
        registerSet.setB(new UnsignedByte(0xBB));
        cpu.loadByteRegister(Register.A, new UnsignedByte(0x0));
        assertEquals(new UnsignedByte(0x0), registerSet.getA());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());

        registerSet.setA(new UnsignedByte(0xAA));
        registerSet.setB(new UnsignedByte(0xBB));
        cpu.loadByteRegister(Register.B, new UnsignedByte(0x0));
        assertEquals(new UnsignedByte(0x0), registerSet.getB());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testExclusiveOrWorksCorrectly() {
        registerSet.setA(new UnsignedByte(0x1));
        registerSet.setB(new UnsignedByte(0x1));
        cpu.exclusiveOr(Register.A, new UnsignedByte(0x0));
        assertEquals(new UnsignedByte(0x1), registerSet.getA());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());

        registerSet.setA(new UnsignedByte(0x1));
        registerSet.setB(new UnsignedByte(0x1));
        cpu.exclusiveOr(Register.A, new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0x0), registerSet.getA());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());

        registerSet.setA(new UnsignedByte(0x1));
        registerSet.setB(new UnsignedByte(0x1));
        cpu.exclusiveOr(Register.B, new UnsignedByte(0x0));
        assertEquals(new UnsignedByte(0x1), registerSet.getB());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());

        registerSet.setA(new UnsignedByte(0x1));
        registerSet.setB(new UnsignedByte(0x1));
        cpu.exclusiveOr(Register.B, new UnsignedByte(0x1));
        assertEquals(new UnsignedByte(0x0), registerSet.getB());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testLogicalOrWorksCorrectly() {
        registerSet.setA(new UnsignedByte(0x40));
        registerSet.setB(new UnsignedByte(0x1));
        cpu.logicalOr(Register.A, new UnsignedByte(0x11));
        assertEquals(new UnsignedByte(0x51), registerSet.getA());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
        assertFalse(io.ccOverflowSet());

        registerSet.setA(new UnsignedByte(0x1));
        registerSet.setB(new UnsignedByte(0x1));
        cpu.logicalOr(Register.A, new UnsignedByte(0x0));
        assertEquals(new UnsignedByte(0x1), registerSet.getA());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());

        registerSet.setA(new UnsignedByte(0x0));
        registerSet.setB(new UnsignedByte(0x1));
        cpu.logicalOr(Register.A, new UnsignedByte(0x0));
        assertEquals(new UnsignedByte(0x0), registerSet.getA());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());

        registerSet.setA(new UnsignedByte(0x1));
        registerSet.setB(new UnsignedByte(0x1));
        cpu.logicalOr(Register.B, new UnsignedByte(0x0));
        assertEquals(new UnsignedByte(0x1), registerSet.getB());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());

        registerSet.setA(new UnsignedByte(0x1));
        registerSet.setB(new UnsignedByte(0x0));
        cpu.logicalOr(Register.B, new UnsignedByte(0x0));
        assertEquals(new UnsignedByte(0x0), registerSet.getB());
        assertTrue(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testStoreByteRegisterWorksCorrectly() {
        registerSet.setA(new UnsignedByte(0xAA));
        cpu.storeByteRegister(Register.A, new UnsignedWord(0xA000));
        assertEquals(new UnsignedByte(0xAA), memory.readByte(new UnsignedWord(0xA000)));
    }

    @Test
    public void testJumpToSubroutineWorksCorrectly() {
        registerSet.setS(new UnsignedWord(0xA000));
        registerSet.setPC(new UnsignedWord(0xDEAD));
        cpu.jumpToSubroutine(new UnsignedWord(0xBEEF));
        assertEquals(new UnsignedByte(0xAD), memory.readByte(new UnsignedWord(0x9FFF)));
        assertEquals(new UnsignedByte(0xDE), memory.readByte(new UnsignedWord(0x9FFE)));
    }

    @Test
    public void testAddDWorksCorrectly() {
        registerSet.setD(new UnsignedWord(0x0101));
        cpu.addD(new UnsignedWord(0x0101));
        assertEquals(new UnsignedWord(0x0202), registerSet.getD());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testBranchLongWorksCorrectly() {
        registerSet.setPC(new UnsignedWord(0x1000));
        cpu.branchLong(new UnsignedWord(0x7FFF));
        assertEquals(new UnsignedWord(0x8FFF), registerSet.getPC());
    }

    @Test
    public void testBranchLongNegativeNumber() {
        registerSet.setPC(new UnsignedWord(0x5700));
        cpu.branchLong(new UnsignedWord(0xAA00));
        assertEquals(new UnsignedWord(0x0100), registerSet.getPC());
    }

    @Test
    public void testBranchShortWorksCorrectly() {
        registerSet.setPC(new UnsignedWord(0x0010));
        cpu.branchShort(new UnsignedByte(0x7F));
        assertEquals(new UnsignedWord(0x008F), registerSet.getPC());
    }

    @Test
    public void testBranchShortNegativeNumber() {
        registerSet.setPC(new UnsignedWord(0x57));
        cpu.branchShort(new UnsignedByte(0xAA));
        assertEquals(new UnsignedWord(0x1), registerSet.getPC());
    }

    @Test
    public void testAddByteRegisterWorksCorrectly() {
        registerSet.setA(new UnsignedByte(0x11));
        cpu.addByteRegister(Register.A, new UnsignedByte(0x11));
        assertEquals(new UnsignedByte(0x22), registerSet.getA());
        assertFalse(io.ccZeroSet());
        assertFalse(io.ccNegativeSet());
    }

    @Test
    public void testAddWithCarryWorksCorrectly() {
        registerSet.setA(new UnsignedByte(0x68));
        cpu.addWithCarry(Register.A, new UnsignedByte(0xA5));
        assertEquals(new UnsignedByte(0x0D), registerSet.getA());
        assertTrue(io.ccCarrySet());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccHalfCarrySet());
        assertFalse(io.ccZeroSet());
    }

    @Test
    public void testAddWithCarryWorksCorrectlyTest2() {
        registerSet.setA(new UnsignedByte(0x30));
        io.setCCCarry();
        cpu.addWithCarry(Register.A, new UnsignedByte(0xC3));
        assertEquals(new UnsignedByte(0xF4), registerSet.getA());
        assertFalse(io.ccCarrySet());
        assertFalse(io.ccOverflowSet());
        assertFalse(io.ccHalfCarrySet());
        assertFalse(io.ccZeroSet());
    }

    @Test
    public void testAddWithCarryWorksCorrectlyTest3() {
        registerSet.setA(new UnsignedByte(0x1));
        io.setCCCarry();
        cpu.addWithCarry(Register.A, new UnsignedByte(0xFF));
        assertEquals(new UnsignedByte(0x01), registerSet.getA());
        assertTrue(io.ccCarrySet());
        assertTrue(io.ccOverflowSet());
        assertFalse(io.ccHalfCarrySet());
        assertFalse(io.ccZeroSet());
    }

    @Test
    public void testDecimalAdditionAdjustWorksCorrectly() {
        registerSet.setA(new UnsignedByte(0x55));
        cpu.addWithCarry(Register.A, new UnsignedByte(0x17));
        cpu.decimalAdditionAdjust();
        assertEquals(new UnsignedByte(0x72), registerSet.getA());
    }
}
