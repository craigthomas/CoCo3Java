/*
 * Copyright (C) 2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AddressingModeTest
{
    @Test
    public void testAddressingModeInherentToStringCorrect() {
        assertEquals("INH", AddressingMode.INHERENT.toString());
    }

    @Test
    public void testAddressingModeImmediateToStringCorrect() {
        assertEquals("IMM", AddressingMode.IMMEDIATE.toString());
    }

    @Test
    public void testAddressingModeDirectToStringCorrect() {
        assertEquals("DIR", AddressingMode.DIRECT.toString());
    }

    @Test
    public void testAddressingModeIndexedToStringCorrect() {
        assertEquals("IND", AddressingMode.INDEXED.toString());
    }

    @Test
    public void testAddressingModeExtendedToStringCorrect() {
        assertEquals("EXT", AddressingMode.EXTENDED.toString());
    }
}
