/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

import static org.junit.Assert.*;

import org.junit.Test;

public class UnsignedWordTest
{
    @Test
    public void testValuePreserved() {
        UnsignedWord result = new UnsignedWord(0xFFFF);
        assertEquals(0xFFFF, result.getInt());
    }

    @Test
    public void testNextLowValue() {
        UnsignedWord result = new UnsignedWord(0x0001);
        assertEquals(0x0002, result.next().getInt());
    }

    @Test
    public void testNextRollsOver() {
        UnsignedWord result = new UnsignedWord(0xFFFF);
        assertEquals(0x0000, result.next().getInt());
    }

    @Test
    public void testDefaultConstructorSetsZero() {
        UnsignedWord result = new UnsignedWord();
        assertEquals(0, result.getInt());
    }

    @Test
    public void testHighSetsHighByteOnly() {
        UnsignedWord result = new UnsignedWord();
        result.setHigh(new UnsignedByte(0xFF));
        assertEquals(0xFF00, result.getInt());
    }

    @Test
    public void testLowSetsLowByteOnly() {
        UnsignedWord result = new UnsignedWord();
        result.setLow(new UnsignedByte(0xFF));
        assertEquals(0x00FF, result.getInt());
    }

    @Test
    public void testToString() {
        UnsignedWord result = new UnsignedWord(0xABCD);
        assertEquals("0xABCD", result.toString());
    }

    @Test
    public void testEqualsSameObject() {
        UnsignedWord result = new UnsignedWord(0xBEEF);
        assertTrue(result.equals(result));
    }

    @Test
    public void testEqualsNullObject() {
        UnsignedWord result = new UnsignedWord(0xDEAD);
        assertFalse(result.equals(null));
    }

    @Test
    public void testEqualsDifferentObject() {
        UnsignedWord result = new UnsignedWord(0xDEAD);
        assertFalse(result.equals(new UnsignedByte(0)));
    }

    @Test
    public void testEqualsDifferentWordsDifferentValue() {
        UnsignedWord first = new UnsignedWord(0xDEAD);
        UnsignedWord second = new UnsignedWord(0xBEEF);
        assertFalse(first.equals(second));
    }

    @Test
    public void testEqualsDifferentWordsSameValue() {
        UnsignedWord first = new UnsignedWord(0xDEAD);
        UnsignedWord second = new UnsignedWord(0xDEAD);
        assertTrue(first.equals(second));
    }

    @Test
    public void testHashCodeIsIntValue() {
        UnsignedWord result = new UnsignedWord(0xBEEF);
        assertEquals(0xBEEF, result.hashCode());
    }

    @Test
    public void testGetLowIsCorrect() {
        UnsignedWord result = new UnsignedWord(0xABCD);
        assertEquals(new UnsignedByte(0xCD), result.getLow());
    }

    @Test
    public void testGTEWorksCorrectly() {
        UnsignedWord word1 = new UnsignedWord(0xBEEF);
        UnsignedWord word2 = new UnsignedWord(0xABEE);
        assertTrue(word1.gte(word2));
    }

    @Test
    public void testLTEWorksCorrectly() {
        UnsignedWord word1 = new UnsignedWord(0xBEEF);
        UnsignedWord word2 = new UnsignedWord(0xABEE);
        assertTrue(word2.lte(word1));
    }

    @Test
    public void testORMaskWorksCorrectly() {
        UnsignedWord word1 = new UnsignedWord(0x2);
        word1.or(0x1);
        assertEquals(0x3, word1.getInt());
    }

    @Test
    public void testInverseWorksCorrectly() {
        UnsignedWord word1 = new UnsignedWord(0xFFFF);
        assertEquals(0x0, word1.inverse().getInt());
    }
}
