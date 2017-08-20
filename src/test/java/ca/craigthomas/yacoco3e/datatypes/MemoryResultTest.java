/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

import org.junit.Test;

import static org.junit.Assert.*;

public class MemoryResultTest
{
    @Test
    public void testMemoryResultSetsValuesCorrect() {
        MemoryResult result = new MemoryResult(3, new UnsignedWord(0xBEEF));
        assertEquals(3, result.getBytesConsumed());
        assertEquals(new UnsignedWord(0xBEEF), result.getResult());
    }

    @Test
    public void testMemoryResultToStringCorrect() {
        MemoryResult result = new MemoryResult(3, new UnsignedWord(0xBEEF));
        assertEquals(3, result.getBytesConsumed());
        assertEquals("[3 bytes read, 0xBEEF]", result.toString());
    }
}
