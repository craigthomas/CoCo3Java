/*
 * Copyright (C) 2022 Craig Thomas
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
        assertEquals(new UnsignedByte(0xDE), regs.a);
        assertEquals(new UnsignedByte(0xAD), regs.b);
    }

    @Test
    public void testAAndBSetsDCorrectly() {
        regs.a.set(new UnsignedByte(0xDE));
        regs.b.set(new UnsignedByte(0xAD));
        assertEquals(new UnsignedWord(0xDEAD), regs.getD());
    }

    @Test
    public void testRegisterSetToStringWorksCorrectly() {
        regs.a.set(0x01);
        regs.b.set(0x02);
        regs.x.set(0x0003);
        regs.y.set(0x0004);
        regs.u.set(0x0005);
        regs.s.set(0x0006);
        regs.cc.set(0x07);
        regs.dp.set(0x08);
        assertEquals("PC:$0000 A:$01 B:$02 D:$0102 X:$0003 Y:$0004 U:$0005 S:$0006 CC:$07 DP:$08", regs.toString());
    }
}
