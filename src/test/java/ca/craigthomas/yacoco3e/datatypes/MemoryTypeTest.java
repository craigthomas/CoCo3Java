/*
 * Copyright (C) 2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MemoryTypeTest
{
    @Test
    public void testMemoryTypeCartridgeToStringCorrect() {
        assertEquals("cartridge ROM", MemoryType.CARTRIDGE.toString());
    }

    @Test
    public void testMemoryTypeROMToStringCorrect() {
        assertEquals("system ROM", MemoryType.ROM.toString());
    }

    @Test
    public void testMemoryTypeMemoryToStringCorrect() {
        assertEquals("RAM", MemoryType.MEMORY.toString());
    }
}
