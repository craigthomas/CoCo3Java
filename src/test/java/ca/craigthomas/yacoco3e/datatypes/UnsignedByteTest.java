/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

import static org.junit.Assert.*;

import org.junit.Test;

public class UnsignedByteTest
{
    @Test
    public void testShortValueOnlyContains8Bits() {
        UnsignedByte result = new UnsignedByte(0xFFFF);
        assertEquals(0xFF, result.getShort());
    }

    @Test
    public void testTwosComplimentZero() {
        UnsignedByte result = new UnsignedByte(0);
        assertEquals(0, result.twosCompliment().getShort());
    }

    @Test
    public void testTwosComplimentOne() {
        UnsignedByte result = new UnsignedByte(1);
        assertEquals(0xFF, result.twosCompliment().getShort());
    }

    @Test
    public void testTwosComplimentTwo() {
        UnsignedByte result = new UnsignedByte(2);
        assertEquals(0xFE, result.twosCompliment().getShort());
    }

    @Test
    public void testIsMaskedZeroValue() {
        UnsignedByte result = new UnsignedByte(0x00);
        assertFalse(result.isMasked(0x01));
    }

    @Test
    public void testIsMaskedOneValue() {
        UnsignedByte result = new UnsignedByte(0x01);
        assertTrue(result.isMasked(0x01));
    }

    @Test
    public void testMaskLargeValueNonMatching() {
        UnsignedByte result = new UnsignedByte(0x01);
        assertFalse(result.isMasked(0x80));
    }

    @Test
    public void testMaskLargeValueMatching() {
        UnsignedByte result = new UnsignedByte(0x80);
        assertTrue(result.isMasked(0x80));
    }

    @Test
    public void testAndMaskZeroValue() {
        UnsignedByte result = new UnsignedByte(0x00);
        result.and(0x01);
        assertEquals(new UnsignedByte(0x00), result);
    }

    @Test
    public void testAndMaskOneValue() {
        UnsignedByte result = new UnsignedByte(0x01);
        result.and(0x01);
        assertEquals(new UnsignedByte(0x01), result);
    }

    @Test
    public void testAndMaskAlternatingValues() {
        UnsignedByte result = new UnsignedByte(0xFF);
        result.and(0xAA);
        assertEquals(new UnsignedByte(0xAA), result);
    }

    @Test
    public void testOrMaskZeroValue() {
        UnsignedByte result = new UnsignedByte(0x00);
        result.or(0x01);
        assertEquals(new UnsignedByte(0x01), result);
    }

    @Test
    public void testOrMaskOneValue() {
        UnsignedByte result = new UnsignedByte(0x01);
        result.or(0x01);
        assertEquals(new UnsignedByte(0x01), result);
    }

    @Test
    public void testOrMaskAlternatingValues() {
        UnsignedByte result = new UnsignedByte(0xFF);
        result.or(0xAA);
        assertEquals(new UnsignedByte(0xFF), result);
    }

    @Test
    public void testIsZero() {
        UnsignedByte result = new UnsignedByte(1);
        assertFalse(result.isZero());

        result = new UnsignedByte(0);
        assertTrue(result.isZero());
    }

    @Test
    public void testIsNegativeWithZeroValue() {
        UnsignedByte result = new UnsignedByte(0);
        assertFalse(result.isNegative());
    }

    @Test
    public void testIsNegativeWithoutHighBitSet() {
        UnsignedByte result = new UnsignedByte(0x7F);
        assertFalse(result.isNegative());
    }

    @Test
    public void testIsNegativeHighBitSet() {
        UnsignedByte result = new UnsignedByte(0x8F);
        assertTrue(result.isNegative());
    }

    @Test
    public void testEqualsAgainstNull() {
        UnsignedByte first = new UnsignedByte(1);
        assertFalse(first.equals(null));
    }

    @Test
    public void testEqualsAgainstDifferentClass() {
        UnsignedByte first = new UnsignedByte(1);
        UnsignedWord second = new UnsignedWord(1);
        assertFalse(first.equals(second));
    }

    @Test
    public void testEqualsAgainstSameClass() {
        UnsignedByte first = new UnsignedByte(1);
        assertTrue(first.equals(first));
    }

    @Test
    public void testEqualsAgainstSameValues() {
        UnsignedByte first = new UnsignedByte(1);
        UnsignedByte second = new UnsignedByte(1);
        assertTrue(first.equals(second));
    }

    @Test
    public void testEqualsAgainstDifferentValues() {
        UnsignedByte first = new UnsignedByte(1);
        UnsignedByte second = new UnsignedByte(2);
        assertFalse(first.equals(second));
    }

    @Test
    public void testHashCodeIsValue() {
        UnsignedByte result = new UnsignedByte(0xBE);
        assertEquals(0xBE, result.hashCode());
    }
}
