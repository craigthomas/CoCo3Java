/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegisterSetTest
{
    private RegisterSet regs;

    @Before
    public void setUp() {
        regs = new RegisterSet();
    }

    @Test
    public void testSetDSetsAAndBCorrectly() {
        regs.setD(new UnsignedWord(0xDEAD));
        assertEquals(new UnsignedByte(0xDE), regs.getA());
        assertEquals(new UnsignedByte(0xAD), regs.getB());
    }

    @Test
    public void testAAndBSetsDCorrectly() {
        regs.setA(new UnsignedByte(0xDE));
        regs.setB(new UnsignedByte(0xAD));
        assertEquals(new UnsignedWord(0xDEAD), regs.getD());
    }

    @Test
    public void testBinaryAddWordZeroValues() {
        UnsignedWord result = regs.binaryAdd(new UnsignedWord(0), new UnsignedWord(0), false, false, false);
        assertEquals(new UnsignedWord(0), result);
        assertEquals(new UnsignedByte(0), regs.getCC());
    }

    @Test
    public void testBinaryAddWordOnes() {
        UnsignedWord result = regs.binaryAdd(new UnsignedWord(1), new UnsignedWord(1), false, false, false);
        assertEquals(new UnsignedWord(2), result);
        assertEquals(new UnsignedByte(0), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesNotChangedOnFalse() {
        UnsignedWord result = regs.binaryAdd(new UnsignedWord(  0xFFFF), new UnsignedWord(1), false, false, false);
        assertEquals(new UnsignedWord(0), result);
        assertEquals(new UnsignedByte(0), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesOverflow() {
        UnsignedWord result = regs.binaryAdd(new UnsignedWord(  0xFFFF), new UnsignedWord(1), false, false, true);
        assertEquals(new UnsignedWord(0), result);
        assertEquals(new UnsignedByte(RegisterSet.CC_V), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesNoOverflow() {
        UnsignedWord result = regs.binaryAdd(new UnsignedWord(  1), new UnsignedWord(1), false, false, true);
        assertEquals(new UnsignedWord(2), result);
        assertEquals(new UnsignedByte(0), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesCarry() {
        UnsignedWord result = regs.binaryAdd(new UnsignedWord(  0xFFFF), new UnsignedWord(1), false, true, false);
        assertEquals(new UnsignedWord(0), result);
        assertEquals(new UnsignedByte(RegisterSet.CC_C), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesNoCarry() {
        UnsignedWord result = regs.binaryAdd(new UnsignedWord(  1), new UnsignedWord(1), false, true, false);
        assertEquals(new UnsignedWord(2), result);
        assertEquals(new UnsignedByte(0), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesHalfCarry() {
        UnsignedWord result = regs.binaryAdd(new UnsignedWord(  0xFFFF), new UnsignedWord(1), true, false, false);
        assertEquals(new UnsignedWord(0), result);
        assertEquals(new UnsignedByte(RegisterSet.CC_H), regs.getCC());
    }

    @Test
    public void testBinaryAddWordConditionCodesNoHalfCarry() {
        UnsignedWord result = regs.binaryAdd(new UnsignedWord(  0), new UnsignedWord(1), true, false, false);
        assertEquals(new UnsignedWord(1), result);
        assertEquals(new UnsignedByte(0), regs.getCC());
    }
}
