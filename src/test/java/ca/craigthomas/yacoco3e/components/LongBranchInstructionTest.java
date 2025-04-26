/*
 * Copyright (C) 2023 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;
import org.junit.Before;
import org.junit.Test;

import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;
import static org.junit.Assert.*;

public class LongBranchInstructionTest {
    private IOController io;
    private CPU cpu;
    private RegisterSet regs;
    private Memory memory;

    @Before
    public void setUp() {
        Screen screen = new Screen(1);
        Cassette cassette = new Cassette();
        memory = new Memory();
        regs = new RegisterSet();
        io = new IOController(memory, regs, new EmulatedKeyboard(), screen, cassette);
        cpu = new CPU(io);
    }

    @Test
    public void testLongBranchNeverDoesNothing() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1021);
        cpu.executeInstruction();
        assertEquals(0x4, regs.pc.getInt());
    }
    @Test
    public void testLongBranchAlways() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x16);
        io.writeWord(0x0001, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0022, regs.pc.getInt());
    }

    @Test
    public void testLongBranchAlwaysNegativeOffset() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x16);
        io.writeWord(0x0001, 0xFFFD);
        cpu.executeInstruction();
        assertEquals(0x0000, regs.pc.getInt());
    }

    @Test
    public void testLongBranchAlwaysNegativeOffsetLoopsAround() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x16);
        io.writeWord(0x0001, 0xFFFC);
        cpu.executeInstruction();
        assertEquals(0xFFFF, regs.pc.getInt());
    }

    @Test
    public void testLongBranchAlwaysSimpleCaseWorksCorrectly() throws MalformedInstructionException {
        regs.pc.set(0x0009);
        io.writeByte(0x0009, 0x16);
        io.writeWord(0x000A, 0x007F);
        cpu.executeInstruction();
        assertEquals(0x008B, regs.pc.getInt());
    }

    @Test
    public void testBranchAlwaysSimpleNegativeNumber() throws MalformedInstructionException {
        regs.pc.set(0x0056);
        io.writeByte(0x0056, 0x16);
        io.writeWord(0x0057, 0xFFAA);
        cpu.executeInstruction();
        assertEquals(0x0003, regs.pc.getInt());
    }

    @Test
    public void testLongBranchNever() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1021);
        io.writeWord(0x0002, 0x00FF);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchSubroutine1() throws MalformedInstructionException {
        io.regs.s.set(0x3000);
        io.regs.pc.set(0x1000);
        io.writeByte(0x1000, 0x17);
        io.writeWord(0x1001, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x1022, regs.pc.getInt());
        assertEquals(0x03, memory.readByte(0x2FFF).getShort());
        assertEquals(0x10, memory.readByte(0x2FFE).getShort());
    }

    @Test
    public void testLongBranchSubroutineNegativeOffsetCorrect() throws MalformedInstructionException {
        regs.s.set(0x3000);
        regs.pc.set(0x1021);
        io.writeByte(0x1021, 0x17);
        io.writeWord(0x1022, 0xFFDC);
        cpu.executeInstruction();
        assertEquals(0x1000, regs.pc.getInt());
        assertEquals(0x24, memory.readByte(0x2FFF).getShort());
        assertEquals(0x10, memory.readByte(0x2FFE).getShort());
    }

    @Test
    public void testLongBranchOnHighCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1022);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnHighNotCalledWhenCarrySet() throws MalformedInstructionException {
        io.regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x1022);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnHighNotCalledWhenZeroSet() throws MalformedInstructionException {
        io.regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x1022);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLowerNotCalledWhenZeroCarryClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1023);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLowerCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x1023);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLowerCalledWhenCarryAndZeroSet() throws MalformedInstructionException {
        io.regs.cc.or(CC_Z);
        io.regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x1023);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLowerCalledCorrectWithZeroOnly() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x1023);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLowerCalledCorrectWithCarryOnly() throws MalformedInstructionException {
        regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x1023);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnCarryClearCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1024);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnCarryClearDoesNotBranchIfCarrySet() throws MalformedInstructionException {
        regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x1024);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnCarrySetCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_C);
        io.writeWord(0x0000, 0x1025);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnCarrySetDoesNotBranchIfCarryClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1025);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnNotEqualCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1026);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnNotEqualDoesNotBranchIfZeroSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x1026);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnEqualCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x1027);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnEqualDoesNotBranchIfZeroClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1027);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnOverflowClearCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1028);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnOverflowClearDoesNotBranchIfOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x1028);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnOverflowSetCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x1029);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnOverflowSetDoesNotBranchIfOverflowClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1029);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnPlusCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x102A);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnPlusDoesNotBranchIfNegativeSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x102A);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnMinusCalledCorrect() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x102B);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnMinusDoesNotBranchIfNegativeClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x102B);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnGreaterThanEqualZeroCalledCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x102C);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnGreaterThanEqualZeroCalledIfNegativeSetOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x102C);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnGreaterThanEqualZeroDoesNotBranchIfNegativeSetOverflowClear() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x102C);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnGreaterThanEqualZeroDoesNotBranchIfOverflowSetNegativeClear() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x102C);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLessThanZeroNotCalledIfNegativeClearOverflowClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x102D);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLessThanZeroNotCalledIfNegativeSetOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x102D);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLessThanZeroCalledIfNegativeSetOverflowClear() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x102D);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLessThanZeroCalledIfNegativeClearOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x102D);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnGreaterThanZeroNotCalledIfZeroSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x102E);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnGreaterThanZeroNotCalledIfZeroNegativeOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x102E);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnGreaterThanZeroNotCalledIfZeroNegativeSetOverflowClear() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x102E);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnGreaterThanZeroNotCalledIfZeroOverflowSetNegativeClear() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x102E);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnGreaterThanZeroNotCalledIfZeroNegativeClearOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x102E);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnGreaterThanZeroNotCalledIfZeroOverflowClearNegativeSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x102E);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnGreaterThanZeroCalledIfZeroOverflowNegativeClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x102E);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnGreaterThanZeroCalledIfZeroClearOverflowNegativeSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x102E);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLessThanZeroCalledIfZeroSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        io.writeWord(0x0000, 0x102F);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLessThanZeroCalledIfZeroNegativeOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x102F);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLessThanZeroCalledIfZeroNegativeSetOverflowClear() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x102F);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLessThanZeroCalledIfZeroOverflowSetNegativeClear() throws MalformedInstructionException {
        regs.cc.or(CC_Z);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x102F);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLessThanZeroCalledIfZeroNegativeClearOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x102F);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLessThanZeroCalledIfZeroOverflowClearNegativeSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        io.writeWord(0x0000, 0x102F);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0023, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLessThanZeroNotCalledIfZeroNegativeOverflowClear() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x102F);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }

    @Test
    public void testLongBranchOnLessThanZeroNotCalledIfZeroClearNegativeOverflowSet() throws MalformedInstructionException {
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        io.writeWord(0x0000, 0x102F);
        io.writeWord(0x0002, 0x001F);
        cpu.executeInstruction();
        assertEquals(0x0004, regs.pc.getInt());
    }
}
