/*
 * Copyright (C) 2017-2025 Craig Thomas
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
        assertEquals(Memory.MEM_512K, memory.memory.length);
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
        assertEquals(0xAB, memory.memory[0x7BEEF]);
    }

    @Test
    public void testGetPhysicalAddressWorksCorrectly() {
        UnsignedWord address = new UnsignedWord(0x0412);
        int par = memory.getPAR(address);
        assertEquals(0x70412, memory.getPhysicalAddress(par, address.get()));
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
    public void testSetExecutiveParROMPagesQuirks() {
        UnsignedByte requestedPage = new UnsignedByte(0x3D);
        memory.setExecutivePAR(2, requestedPage);
        assertEquals(0x3E, memory.executivePAR[2]);
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

    @Test
    public void testSetTaskParROMPagesQuirks() {
        UnsignedByte requestedPage = new UnsignedByte(0x3D);
        memory.setTaskPAR(2, requestedPage);
        assertEquals(0x3E, memory.taskPAR[2]);
    }

    @Test
    public void testReadPhysicalByteReadsFromRAMOnly() {
        memory.enableAllRAMMode();
        memory.memory[0x78000] = 0xBE;
        memory.rom[0] = 0xCE;
        assertEquals(0xBE, memory.readPhysicalByte(new UnsignedWord(0x8000)));
    }

    @Test
    public void testReadPhysicalByteReadsFrom32KROMCorrectly() {
        memory.disableAllRAMMode();
        memory.setROMMode(new UnsignedByte(0x2));

        /* First 8K segment */
        memory.memory[0x78000] = 0xBE;
        memory.rom[0] = 0xCE;
        assertEquals(0xCE, memory.readPhysicalByte(new UnsignedWord(0x8000)));

        /* Second 8K segment */
        memory.memory[0x7A000] = 0xBE;
        memory.rom[0x2000] = 0xCE;
        assertEquals(0xCE, memory.readPhysicalByte(new UnsignedWord(0xA000)));

        /* Third 8K segment */
        memory.memory[0x7C000] = 0xBE;
        memory.rom[0x4000] = 0xCE;
        assertEquals(0xCE, memory.readPhysicalByte(new UnsignedWord(0xC000)));

        /* Fourth 8K segment */
        memory.memory[0x7E000] = 0xBE;
        memory.rom[0x6000] = 0xCE;
        assertEquals(0xCE, memory.readPhysicalByte(new UnsignedWord(0xE000)));

        /* Anything Else */
        memory.memory[0x74000] = 0xBE;
        assertEquals(0xBE, memory.readPhysicalByte(new UnsignedWord(0x4000)));
    }

    @Test
    public void testReadPhysicalByteReadsFrom32KCartROMCorrectly() {
        memory.disableAllRAMMode();
        memory.setROMMode(new UnsignedByte(0x3));

        /* First 8K segment */
        memory.memory[0x78000] = 0xBE;
        memory.cartROM[0] = 0xCE;
        assertEquals(0xCE, memory.readPhysicalByte(new UnsignedWord(0x8000)));

        /* Second 8K segment */
        memory.memory[0x7A000] = 0xBE;
        memory.cartROM[0x2000] = 0xCE;
        assertEquals(0xCE, memory.readPhysicalByte(new UnsignedWord(0xA000)));

        /* Third 8K segment */
        memory.memory[0x7C000] = 0xBE;
        memory.cartROM[0x4000] = 0xCE;
        assertEquals(0xCE, memory.readPhysicalByte(new UnsignedWord(0xC000)));

        /* Fourth 8K segment */
        memory.memory[0x7E000] = 0xBE;
        memory.cartROM[0x6000] = 0xCE;
        assertEquals(0xCE, memory.readPhysicalByte(new UnsignedWord(0xE000)));

        /* Anything Else */
        memory.memory[0x74000] = 0xBE;
        assertEquals(0xBE, memory.readPhysicalByte(new UnsignedWord(0x4000)));
    }

    @Test
    public void testReadPhysicalByteReadsFrom16KCart16KROMCorrectly() {
        memory.disableAllRAMMode();
        memory.setROMMode(new UnsignedByte(0x1));

        /* First 8K segment */
        memory.memory[0x78000] = 0xBE;
        memory.rom[0] = 0xCE;
        assertEquals(0xCE, memory.readPhysicalByte(new UnsignedWord(0x8000)));

        /* Second 8K segment */
        memory.memory[0x7A000] = 0xBE;
        memory.rom[0x2000] = 0xCE;
        assertEquals(0xCE, memory.readPhysicalByte(new UnsignedWord(0xA000)));

        /* Third 8K segment */
        memory.memory[0x7C000] = 0xBE;
        memory.cartROM[0x0000] = 0xDE;
        assertEquals(0xDE, memory.readPhysicalByte(new UnsignedWord(0xC000)));

        /* Fourth 8K segment */
        memory.memory[0x7E000] = 0xBE;
        memory.cartROM[0x2000] = 0xDE;
        assertEquals(0xDE, memory.readPhysicalByte(new UnsignedWord(0xE000)));

        /* Anything Else */
        memory.memory[0x74000] = 0xBE;
        assertEquals(0xBE, memory.readPhysicalByte(new UnsignedWord(0x4000)));
    }

    @Test
    public void testWriteByteToROMDoesNotWrite() {
        memory.disableAllRAMMode();
        memory.setROMMode(new UnsignedByte(0x1));

        /* First 8K segment */
        memory.rom[0] = 0xCE;
        memory.writeByte(new UnsignedWord(0x8000), new UnsignedByte(0xAA));
        assertEquals(new UnsignedByte(0xCE), memory.readByte(new UnsignedWord(0x8000)));

        /* Second 8K segment */
        memory.rom[0x2000] = 0xCE;
        memory.writeByte(new UnsignedWord(0xA000), new UnsignedByte(0xAA));
        assertEquals(new UnsignedByte(0xCE), memory.readByte(new UnsignedWord(0xA000)));

        /* Third 8K segment */
        memory.cartROM[0x0000] = 0xDE;
        memory.writeByte(new UnsignedWord(0xC000), new UnsignedByte(0xAA));
        assertEquals(new UnsignedByte(0xDE), memory.readByte(new UnsignedWord(0xC000)));

        /* Fourth 8K segment */
        memory.cartROM[0x2000] = 0xDE;
        memory.writeByte(new UnsignedWord(0xE000), new UnsignedByte(0xAA));
        assertEquals(new UnsignedByte(0xDE), memory.readByte(new UnsignedWord(0xE000)));

        /* Anything Else */
        memory.memory[0x74000] = 0xBE;
        memory.writeByte(new UnsignedWord(0x4000), new UnsignedByte(0xAA));
        assertEquals(0xAA, memory.readPhysicalByte(new UnsignedWord(0x4000)));
    }
}
