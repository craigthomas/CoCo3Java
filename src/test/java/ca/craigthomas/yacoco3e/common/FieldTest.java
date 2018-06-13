/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class FieldTest
{
    private Field field;

    @Test
    public void testReadWriteWorksCorrectly() {
        field = new Field(2);
        field.write((byte) 0x11);
        field.restore();
        assertEquals(0x11, field.read());
    }

    @Test
    public void testRestorePastGapWorks() {
        field = new Field(2, 2, (short)-1);
        field.write((byte) 0x11);
        field.write((byte) 0x11);
        field.write((byte) 0x22);
        field.write((byte) 0x22);
        field.restorePastGap();
        assertEquals(0x22, field.read());
    }

    @Test
    public void testPushWorks() {
        field = new Field(2);
        field.write((byte) 0x11);
        field.write((byte) 0x22);
        field.push();
        assertEquals(0x11, field.read());
    }

    @Test
    public void testHasMoreBytesWhenStillSpace() {
        field = new Field(2);
        assertTrue(field.hasMoreBytes());
    }

    @Test
    public void testHasMoreBytesWhenNoSpace() {
        field = new Field(2);
        field.write((byte) 0x11);
        field.write((byte) 0x11);
        assertFalse(field.hasMoreBytes());
    }

    @Test
    public void testZeroFillWorks() {
        field = new Field(2);
        field.write((byte) 0x11);
        field.write((byte) 0x11);
        field.zeroFill();
        field.restore();
        assertEquals(0x0, field.read());
        assertEquals(0x0, field.read());
    }

    @Test
    public void testNextWorks() {
        field = new Field(2);
        field.write((byte) 0x11);
        field.write((byte) 0x22);
        field.restore();
        field.next();
        assertEquals(0x22, field.read());
    }

    @Test
    public void testReadAtWorks() {
        field = new Field(2);
        field.write((byte) 0x11);
        field.write((byte) 0x22);
        assertEquals(0x11, field.readAt(0));
        assertEquals(0x22, field.readAt(1));
    }

    @Test
    public void testSetFilledWorks() {
        field = new Field(22);
        field.setFilled();
        assertFalse(field.hasMoreBytes());
    }

    @Test
    public void testIsExpectedWorks() {
        field = new Field(2, 0, (short) 0x22);
        assertTrue(field.isExpected((byte) 0x22));
    }

    @Test
    public void testIsExpectedAlwaysReturnsTrueWhenNotSet() {
        field = new Field(2);
        assertTrue(field.isExpected((byte) 0x22));
    }

    @Test
    public void testPopWorks() {
        field = new Field(2);
        field.write((byte) 0x11);
        field.write((byte) 0x22);
        field.restore();
        field.read();
        field.push();
        field.pop();
        assertEquals(0x22, field.read());
    }
}
