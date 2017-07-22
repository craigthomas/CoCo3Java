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
        memory.memory[0xBEEF] = 0xAB;
        UnsignedByte result = memory.readByte(new UnsignedWord(0xBEEF));
        assertEquals(new UnsignedByte(0xAB), result);
    }

    @Test
    public void testWriteByteWritesCorrectByte() {
        memory.writeByte(new UnsignedWord(0xBEEF), new UnsignedByte(0xAB));
        assertEquals(memory.memory[0xBEEF], 0xAB);
    }

    @Test
    public void testReadWordReadsCorrectWord() {
        memory.memory[0xBEEE] = 0xAB;
        memory.memory[0xBEEF] = 0xCD;
        UnsignedWord result = memory.readWord(new UnsignedWord(0xBEEE));
        assertEquals(new UnsignedWord(0xABCD), result);
    }

    @Test
    public void testGetImmediateReadsAddressFromPC() {
        memory.memory[0xBEEE] = 0xAB;
        memory.memory[0xBEEF] = 0xCD;
        RegisterSet regs = new RegisterSet();
        regs.setPC(new UnsignedWord(0xBEEE));
        MemoryResult result = memory.getImmediateWord(regs);
        assertEquals(2, result.getBytesConsumed());
        assertEquals(new UnsignedWord(0xABCD), result.getResult());
    }

    @Test
    public void testGetDirectReadsAddressFromDPAndPC() {
        memory.memory[0xBEEE] = 0xCD;
        RegisterSet regs = new RegisterSet();
        regs.setPC(new UnsignedWord(0xBEEE));
        regs.setDP(new UnsignedByte(0xAB));
        MemoryResult result = memory.getDirect(regs);
        assertEquals(1, result.getBytesConsumed());
        assertEquals(new UnsignedWord(0xABCD), result.getResult());
    }

    @Test
    public void testPushStackWritesToMemoryLocation() {
        RegisterSet regs = new RegisterSet();
        regs.setS(new UnsignedWord(0xA000));
        memory.pushStack(regs, Register.S, new UnsignedByte(0x98));
        assertEquals(memory.memory[0x9FFF], new UnsignedByte(0x98).getShort());
    }

    @Test
    public void testPushStackWritesToMemoryLocationUsingUStack() {
        RegisterSet regs = new RegisterSet();
        regs.setU(new UnsignedWord(0xA000));
        memory.pushStack(regs, Register.U, new UnsignedByte(0x98));
        assertEquals(memory.memory[0x9FFF], new UnsignedByte(0x98).getShort());
    }

    @Test
    public void testPopStackReadsMemoryLocation() {
        RegisterSet regs = new RegisterSet();
        regs.setS(new UnsignedWord(0xA000));
        memory.memory[0xA000] = 0x98;
        UnsignedByte result = memory.popStack(regs, Register.S);
        assertEquals(new UnsignedByte(0x98), result);
        assertEquals(new UnsignedWord(0xA001), regs.getS());
    }

    @Test
    public void testPopStackReadsMemoryLocationFromU() {
        RegisterSet regs = new RegisterSet();
        regs.setU(new UnsignedWord(0xA000));
        memory.memory[0xA000] = 0x98;
        UnsignedByte result = memory.popStack(regs, Register.U);
        assertEquals(new UnsignedByte(0x98), result);
        assertEquals(new UnsignedWord(0xA001), regs.getU());
    }

    @Test
    public void testWriteWordWorksCorrectly() {
        memory.writeWord(new UnsignedWord(0xA000), new UnsignedWord(0xBEEF));
        assertEquals(memory.memory[0xA000], new UnsignedByte(0xBE).getShort());
        assertEquals(memory.memory[0xA001], new UnsignedByte(0xEF).getShort());
    }
}
