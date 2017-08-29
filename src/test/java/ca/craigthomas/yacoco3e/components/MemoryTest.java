/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import static org.junit.Assert.*;

import ca.craigthomas.yacoco3e.datatypes.*;
import org.junit.Before;
import org.junit.Test;

public class MemoryTest
{
    private Memory memory;

    @Before
    public void setUp() {
        memory = new Memory();
    }

    @Test
    public void testDefaultConstructorSetsSizeTo512K() {
        assertEquals(memory.memory.length, Memory.MEM_512K);
    }

    @Test
    public void testReadByteReadsCorrectByte() {
        memory.memory[0x7BEEF] = 0xAB;
        UnsignedByte result = memory.readByte(new UnsignedWord(0xBEEF));
        assertEquals(new UnsignedByte(0xAB), result);
    }

    @Test
    public void testWriteByteWritesCorrectByte() {
        memory.writeByte(new UnsignedWord(0xBEEF), new UnsignedByte(0xAB));
        assertEquals(memory.memory[0x7BEEF], 0xAB);
    }

    @Test
    public void testGetPhysicalAddressWorksCorrectly() {
        assertEquals(0x70412, memory.getPhysicalAddress(new UnsignedWord(0x0412)));
    }
    
    @Test
    public void testSetExecutivePARWorksCorrectly() {
        memory.setExecutivePAR(0, new UnsignedByte(0xB1));
        assertEquals(0xB1, memory.executivePAR[0]);

        memory.setExecutivePAR(1, new UnsignedByte(0xB2));
        assertEquals(0xB2, memory.executivePAR[1]);

        memory.setExecutivePAR(2, new UnsignedByte(0xB3));
        assertEquals(0xB3, memory.executivePAR[2]);

        memory.setExecutivePAR(3, new UnsignedByte(0xB4));
        assertEquals(0xB4, memory.executivePAR[3]);

        memory.setExecutivePAR(4, new UnsignedByte(0xB5));
        assertEquals(0xB5, memory.executivePAR[4]);

        memory.setExecutivePAR(5, new UnsignedByte(0xB6));
        assertEquals(0xB6, memory.executivePAR[5]);

        memory.setExecutivePAR(6, new UnsignedByte(0xB7));
        assertEquals(0xB7, memory.executivePAR[6]);
        
        memory.setExecutivePAR(7, new UnsignedByte(0xB8));
        assertEquals(0xB8, memory.executivePAR[7]);
    }

    @Test
    public void testSetTaskPARWorksCorrectly() {
        memory.setTaskPAR(0, new UnsignedByte(0xB1));
        assertEquals(0xB1, memory.taskPAR[0]);

        memory.setTaskPAR(1, new UnsignedByte(0xB2));
        assertEquals(0xB2, memory.taskPAR[1]);

        memory.setTaskPAR(2, new UnsignedByte(0xB3));
        assertEquals(0xB3, memory.taskPAR[2]);

        memory.setTaskPAR(3, new UnsignedByte(0xB4));
        assertEquals(0xB4, memory.taskPAR[3]);

        memory.setTaskPAR(4, new UnsignedByte(0xB5));
        assertEquals(0xB5, memory.taskPAR[4]);

        memory.setTaskPAR(5, new UnsignedByte(0xB6));
        assertEquals(0xB6, memory.taskPAR[5]);

        memory.setTaskPAR(6, new UnsignedByte(0xB7));
        assertEquals(0xB7, memory.taskPAR[6]);

        memory.setTaskPAR(7, new UnsignedByte(0xB8));
        assertEquals(0xB8, memory.taskPAR[7]);
    }
}
