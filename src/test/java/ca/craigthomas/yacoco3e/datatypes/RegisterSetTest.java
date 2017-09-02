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

}
