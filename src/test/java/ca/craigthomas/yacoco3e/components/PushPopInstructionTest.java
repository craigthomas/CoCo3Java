/*
 * Copyright (C) 2022-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PushPopInstructionTest {
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
        io = new IOController(memory, regs, new EmulatedKeyboard(), screen, cassette, false);
        cpu = new CPU(io);
        io.regs.pc.set(0);
    }

    @After
    public void tearDown() {
        io.shutdown();
    }

    @Test
    public void testPushPCToS() throws MalformedInstructionException {
        regs.s.set(0xFF);
        regs.pc.set(0x1234);
        regs.u.set(0xE563);
        regs.y.set(0xA3C2);
        regs.x.set(0xF524);
        regs.dp.set(0xD1);
        regs.b.set(0xB9);
        regs.a.set(0xA8);
        regs.cc.set(0xCC);
        io.writeWord(0x1234, 0x3480);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x36), io.readByte(0xFE));
        assertEquals(new UnsignedByte(0x12), io.readByte(0xFD));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFC));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFB));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFA));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF9));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF8));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF7));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF6));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF5));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF4));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF3));
        assertEquals(7, ticks);
    }

    @Test
    public void testPushUToS() throws MalformedInstructionException {
        regs.s.set(0xFF);
        regs.pc.set(0x1234);
        regs.u.set(0xE563);
        regs.y.set(0xA3C2);
        regs.x.set(0xF524);
        regs.dp.set(0xD1);
        regs.b.set(0xB9);
        regs.a.set(0xA8);
        regs.cc.set(0xCC);
        io.writeWord(0x1234, 0x3440);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x63), io.readByte(0xFE));
        assertEquals(new UnsignedByte(0xE5), io.readByte(0xFD));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFC));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFB));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFA));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF9));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF8));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF7));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF6));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF5));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF4));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF3));
        assertEquals(7, ticks);
    }

    @Test
    public void testPushYToS() throws MalformedInstructionException {
        regs.s.set(0xFF);
        regs.pc.set(0x1234);
        regs.u.set(0xE563);
        regs.y.set(0xA3C2);
        regs.x.set(0xF524);
        regs.dp.set(0xD1);
        regs.b.set(0xB9);
        regs.a.set(0xA8);
        regs.cc.set(0xCC);
        io.writeWord(0x1234, 0x3420);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xC2), io.readByte(0xFE));
        assertEquals(new UnsignedByte(0xA3), io.readByte(0xFD));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFC));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFB));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFA));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF9));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF8));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF7));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF6));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF5));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF4));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF3));
        assertEquals(7, ticks);
    }

    @Test
    public void testPushXToS() throws MalformedInstructionException {
        regs.s.set(0xFF);
        regs.pc.set(0x1234);
        regs.u.set(0xE563);
        regs.y.set(0xA3C2);
        regs.x.set(0xF524);
        regs.dp.set(0xD1);
        regs.b.set(0xB9);
        regs.a.set(0xA8);
        regs.cc.set(0xCC);
        io.writeWord(0x1234, 0x3410);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x24), io.readByte(0xFE));
        assertEquals(new UnsignedByte(0xF5), io.readByte(0xFD));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFC));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFB));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFA));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF9));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF8));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF7));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF6));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF5));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF4));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF3));
        assertEquals(7, ticks);
    }

    @Test
    public void testPushDPToS() throws MalformedInstructionException {
        regs.s.set(0xFF);
        regs.pc.set(0x1234);
        regs.u.set(0xE563);
        regs.y.set(0xA3C2);
        regs.x.set(0xF524);
        regs.dp.set(0xD1);
        regs.b.set(0xB9);
        regs.a.set(0xA8);
        regs.cc.set(0xCC);
        io.writeWord(0x1234, 0x3408);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xD1), io.readByte(0xFE));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFD));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFC));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFB));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFA));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF9));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF8));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF7));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF6));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF5));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF4));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF3));
        assertEquals(6, ticks);
    }

    @Test
    public void testPushBToS() throws MalformedInstructionException {
        regs.s.set(0xFF);
        regs.pc.set(0x1234);
        regs.u.set(0xE563);
        regs.y.set(0xA3C2);
        regs.x.set(0xF524);
        regs.dp.set(0xD1);
        regs.b.set(0xB9);
        regs.a.set(0xA8);
        regs.cc.set(0xCC);
        io.writeWord(0x1234, 0x3404);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xB9), io.readByte(0xFE));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFD));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFC));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFB));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFA));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF9));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF8));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF7));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF6));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF5));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF4));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF3));
        assertEquals(6, ticks);
    }

    @Test
    public void testPushAToS() throws MalformedInstructionException {
        regs.s.set(0xFF);
        regs.pc.set(0x1234);
        regs.u.set(0xE563);
        regs.y.set(0xA3C2);
        regs.x.set(0xF524);
        regs.dp.set(0xD1);
        regs.b.set(0xB9);
        regs.a.set(0xA8);
        regs.cc.set(0xCC);
        io.writeWord(0x1234, 0x3402);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xA8), io.readByte(0xFE));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFD));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFC));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFB));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFA));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF9));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF8));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF7));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF6));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF5));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF4));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF3));
        assertEquals(6, ticks);
    }

    @Test
    public void testPushCCToS() throws MalformedInstructionException {
        regs.s.set(0xFF);
        regs.pc.set(0x1234);
        regs.u.set(0xE563);
        regs.y.set(0xA3C2);
        regs.x.set(0xF524);
        regs.dp.set(0xD1);
        regs.b.set(0xB9);
        regs.a.set(0xA8);
        regs.cc.set(0xCC);
        io.writeWord(0x1234, 0x3401);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedByte(0xCC), io.readByte(0xFE));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFD));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFC));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFB));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xFA));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF9));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF8));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF7));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF6));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF5));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF4));
        assertEquals(new UnsignedByte(0x00), io.readByte(0xF3));
        assertEquals(6, ticks);
    }

    @Test
    public void testPushAllRegsToS() throws MalformedInstructionException {
        regs.s.set(0xFF);
        regs.pc.set(0x1234);
        regs.u.set(0xE563);
        regs.y.set(0xA3C2);
        regs.x.set(0xF524);
        regs.dp.set(0xD1);
        regs.b.set(0xB9);
        regs.a.set(0xA8);
        regs.cc.set(0xCC);
        io.writeWord(0x1234, 0x34FF);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x36), io.readByte(0xFE));
        assertEquals(new UnsignedByte(0x12), io.readByte(0xFD));
        assertEquals(new UnsignedByte(0x63), io.readByte(0xFC));
        assertEquals(new UnsignedByte(0xE5), io.readByte(0xFB));
        assertEquals(new UnsignedByte(0xC2), io.readByte(0xFA));
        assertEquals(new UnsignedByte(0xA3), io.readByte(0xF9));
        assertEquals(new UnsignedByte(0x24), io.readByte(0xF8));
        assertEquals(new UnsignedByte(0xF5), io.readByte(0xF7));
        assertEquals(new UnsignedByte(0xD1), io.readByte(0xF6));
        assertEquals(new UnsignedByte(0xB9), io.readByte(0xF5));
        assertEquals(new UnsignedByte(0xA8), io.readByte(0xF4));
        assertEquals(new UnsignedByte(0xCC), io.readByte(0xF3));
        assertEquals(17, ticks);
    }

    @Test
    public void testPullPCFromS() throws MalformedInstructionException {
        regs.s.set(0xFD);
        regs.pc.set(0x1200);
        io.writeWord(0x00F3, 0xCCA8);
        io.writeWord(0x00F5, 0xB9D1);
        io.writeWord(0x00F7, 0xF524);
        io.writeWord(0x00F9, 0xA3C2);
        io.writeWord(0x00FB, 0xE563);
        io.writeWord(0x00FD, 0x1236);
        io.writeWord(0x1200, 0x3580);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x1236), regs.pc);
        assertEquals(new UnsignedWord(0), regs.u);
        assertEquals(new UnsignedWord(0), regs.y);
        assertEquals(new UnsignedWord(0), regs.x);
        assertEquals(new UnsignedByte(0), regs.dp);
        assertEquals(new UnsignedByte(0), regs.b);
        assertEquals(new UnsignedByte(0), regs.a);
        assertEquals(new UnsignedByte(0), regs.cc);
        assertEquals(7, ticks);
    }

    @Test
    public void testPullUFromS() throws MalformedInstructionException {
        regs.s.set(0xFB);
        regs.pc.set(0x1200);
        io.writeWord(0x00F3, 0xCCA8);
        io.writeWord(0x00F5, 0xB9D1);
        io.writeWord(0x00F7, 0xF524);
        io.writeWord(0x00F9, 0xA3C2);
        io.writeWord(0x00FB, 0xE563);
        io.writeWord(0x00FD, 0x1236);
        io.writeWord(0x1200, 0x3540);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x1202), regs.pc);
        assertEquals(new UnsignedWord(0xE563), regs.u);
        assertEquals(new UnsignedWord(0), regs.y);
        assertEquals(new UnsignedWord(0), regs.x);
        assertEquals(new UnsignedByte(0), regs.dp);
        assertEquals(new UnsignedByte(0), regs.b);
        assertEquals(new UnsignedByte(0), regs.a);
        assertEquals(new UnsignedByte(0), regs.cc);
        assertEquals(7, ticks);
    }

    @Test
    public void testPullYFromS() throws MalformedInstructionException {
        regs.s.set(0xF9);
        regs.pc.set(0x1200);
        io.writeWord(0x00F3, 0xCCA8);
        io.writeWord(0x00F5, 0xB9D1);
        io.writeWord(0x00F7, 0xF524);
        io.writeWord(0x00F9, 0xA3C2);
        io.writeWord(0x00FB, 0xE563);
        io.writeWord(0x00FD, 0x1236);
        io.writeWord(0x1200, 0x3520);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x1202), regs.pc);
        assertEquals(new UnsignedWord(0), regs.u);
        assertEquals(new UnsignedWord(0xA3C2), regs.y);
        assertEquals(new UnsignedWord(0), regs.x);
        assertEquals(new UnsignedByte(0), regs.dp);
        assertEquals(new UnsignedByte(0), regs.b);
        assertEquals(new UnsignedByte(0), regs.a);
        assertEquals(new UnsignedByte(0), regs.cc);
        assertEquals(7, ticks);
    }

    @Test
    public void testPullXFromS() throws MalformedInstructionException {
        regs.s.set(0xF7);
        regs.pc.set(0x1200);
        io.writeWord(0x00F3, 0xCCA8);
        io.writeWord(0x00F5, 0xB9D1);
        io.writeWord(0x00F7, 0xF524);
        io.writeWord(0x00F9, 0xA3C2);
        io.writeWord(0x00FB, 0xE563);
        io.writeWord(0x00FD, 0x1236);
        io.writeWord(0x1200, 0x3510);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x1202), regs.pc);
        assertEquals(new UnsignedWord(0), regs.u);
        assertEquals(new UnsignedWord(0), regs.y);
        assertEquals(new UnsignedWord(0xF524), regs.x);
        assertEquals(new UnsignedByte(0), regs.dp);
        assertEquals(new UnsignedByte(0), regs.b);
        assertEquals(new UnsignedByte(0), regs.a);
        assertEquals(new UnsignedByte(0), regs.cc);
        assertEquals(7, ticks);
    }

    @Test
    public void testPullDPFromS() throws MalformedInstructionException {
        regs.s.set(0xF6);
        regs.pc.set(0x1200);
        io.writeWord(0x00F3, 0xCCA8);
        io.writeWord(0x00F5, 0xB9D1);
        io.writeWord(0x00F7, 0xF524);
        io.writeWord(0x00F9, 0xA3C2);
        io.writeWord(0x00FB, 0xE563);
        io.writeWord(0x00FD, 0x1236);
        io.writeWord(0x1200, 0x3508);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x1202), regs.pc);
        assertEquals(new UnsignedWord(0), regs.u);
        assertEquals(new UnsignedWord(0), regs.y);
        assertEquals(new UnsignedWord(0), regs.x);
        assertEquals(new UnsignedByte(0xD1), regs.dp);
        assertEquals(new UnsignedByte(0), regs.b);
        assertEquals(new UnsignedByte(0), regs.a);
        assertEquals(new UnsignedByte(0), regs.cc);
        assertEquals(6, ticks);
    }

    @Test
    public void testPullBFromS() throws MalformedInstructionException {
        regs.s.set(0xF5);
        regs.pc.set(0x1200);
        io.writeWord(0x00F3, 0xCCA8);
        io.writeWord(0x00F5, 0xB9D1);
        io.writeWord(0x00F7, 0xF524);
        io.writeWord(0x00F9, 0xA3C2);
        io.writeWord(0x00FB, 0xE563);
        io.writeWord(0x00FD, 0x1236);
        io.writeWord(0x1200, 0x3504);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x1202), regs.pc);
        assertEquals(new UnsignedWord(0), regs.u);
        assertEquals(new UnsignedWord(0), regs.y);
        assertEquals(new UnsignedWord(0), regs.x);
        assertEquals(new UnsignedByte(0), regs.dp);
        assertEquals(new UnsignedByte(0xB9), regs.b);
        assertEquals(new UnsignedByte(0), regs.a);
        assertEquals(new UnsignedByte(0), regs.cc);
        assertEquals(6, ticks);
    }

    @Test
    public void testPullAFromS() throws MalformedInstructionException {
        regs.s.set(0xF4);
        regs.pc.set(0x1200);
        io.writeWord(0x00F3, 0xCCA8);
        io.writeWord(0x00F5, 0xB9D1);
        io.writeWord(0x00F7, 0xF524);
        io.writeWord(0x00F9, 0xA3C2);
        io.writeWord(0x00FB, 0xE563);
        io.writeWord(0x00FD, 0x1236);
        io.writeWord(0x1200, 0x3502);
        cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x1202), regs.pc);
        assertEquals(new UnsignedWord(0), regs.u);
        assertEquals(new UnsignedWord(0), regs.y);
        assertEquals(new UnsignedWord(0), regs.x);
        assertEquals(new UnsignedByte(0), regs.dp);
        assertEquals(new UnsignedByte(0), regs.b);
        assertEquals(new UnsignedByte(0xA8), regs.a);
        assertEquals(new UnsignedByte(0x00), regs.cc);
    }

    @Test
    public void testPullCCFromS() throws MalformedInstructionException {
        regs.s.set(0xF3);
        regs.pc.set(0x1200);
        io.writeWord(0x00F3, 0xCCA8);
        io.writeWord(0x00F5, 0xB9D1);
        io.writeWord(0x00F7, 0xF524);
        io.writeWord(0x00F9, 0xA3C2);
        io.writeWord(0x00FB, 0xE563);
        io.writeWord(0x00FD, 0x1236);
        io.writeWord(0x1200, 0x3501);
        cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x1202), regs.pc);
        assertEquals(new UnsignedWord(0), regs.u);
        assertEquals(new UnsignedWord(0), regs.y);
        assertEquals(new UnsignedWord(0), regs.x);
        assertEquals(new UnsignedByte(0), regs.a);
        assertEquals(new UnsignedByte(0), regs.b);
        assertEquals(new UnsignedByte(0), regs.a);
        assertEquals(new UnsignedByte(0xCC), regs.cc);
    }

    @Test
    public void testPullAllRegsFromS() throws MalformedInstructionException {
        regs.s.set(0xF3);
        regs.pc.set(0x1200);
        io.writeWord(0x00F3, 0xCCA8);
        io.writeWord(0x00F5, 0xB9D1);
        io.writeWord(0x00F7, 0xF524);
        io.writeWord(0x00F9, 0xA3C2);
        io.writeWord(0x00FB, 0xE563);
        io.writeWord(0x00FD, 0x1236);
        io.writeWord(0x1200, 0x35FF);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedWord(0x1236), regs.pc);
        assertEquals(new UnsignedWord(0xE563), regs.u);
        assertEquals(new UnsignedWord(0xA3C2), regs.y);
        assertEquals(new UnsignedWord(0xF524), regs.x);
        assertEquals(new UnsignedByte(0xD1), regs.dp);
        assertEquals(new UnsignedByte(0xB9), regs.b);
        assertEquals(new UnsignedByte(0xA8), regs.a);
        assertEquals(new UnsignedByte(0xCC), regs.cc);
        assertEquals(17, ticks);
    }

    @Test
    public void testPushAllRegsToU() throws MalformedInstructionException {
        regs.u.set(0xFF);
        regs.pc.set(0x1234);
        regs.s.set(0xE563);
        regs.y.set(0xA3C2);
        regs.x.set(0xF524);
        regs.dp.set(0xD1);
        regs.b.set(0xB9);
        regs.a.set(0xA8);
        regs.cc.set(0xCC);
        io.writeWord(0x1234, 0x36FF);
        int ticks = cpu.executeInstruction();
        assertEquals(new UnsignedByte(0x36), io.readByte(0xFE));
        assertEquals(new UnsignedByte(0x12), io.readByte(0xFD));
        assertEquals(new UnsignedByte(0x63), io.readByte(0xFC));
        assertEquals(new UnsignedByte(0xE5), io.readByte(0xFB));
        assertEquals(new UnsignedByte(0xC2), io.readByte(0xFA));
        assertEquals(new UnsignedByte(0xA3), io.readByte(0xF9));
        assertEquals(new UnsignedByte(0x24), io.readByte(0xF8));
        assertEquals(new UnsignedByte(0xF5), io.readByte(0xF7));
        assertEquals(new UnsignedByte(0xD1), io.readByte(0xF6));
        assertEquals(new UnsignedByte(0xB9), io.readByte(0xF5));
        assertEquals(new UnsignedByte(0xA8), io.readByte(0xF4));
        assertEquals(new UnsignedByte(0xCC), io.readByte(0xF3));
        assertEquals(17, ticks);
    }

    @Test
    public void testPushPullRoundTripU() throws MalformedInstructionException {
        regs.u.set(0xFF);
        regs.pc.set(0x1234);
        regs.s.set(0xE563);
        regs.y.set(0xA3C2);
        regs.x.set(0xF524);
        regs.dp.set(0xD1);
        regs.b.set(0xB9);
        regs.a.set(0xA8);
        regs.cc.set(0xCC);

        io.writeWord(0x1236, 0x37FF);
        io.writeWord(0x1234, 0x36FF);
        cpu.executeInstruction();

        regs.s.set(0x0000);
        regs.y.set(0x0000);
        regs.x.set(0x0000);
        regs.dp.set(0x00);
        regs.b.set(0x00);
        regs.a.set(0x00);
        regs.cc.set(0x00);
        cpu.executeInstruction();

        assertEquals(new UnsignedWord(0x1236), regs.pc);
        assertEquals(new UnsignedWord(0xE563), regs.s);
        assertEquals(new UnsignedWord(0xA3C2), regs.y);
        assertEquals(new UnsignedWord(0xF524), regs.x);
        assertEquals(new UnsignedByte(0xD1), regs.dp);
        assertEquals(new UnsignedByte(0xB9), regs.b);
        assertEquals(new UnsignedByte(0xA8), regs.a);
        assertEquals(new UnsignedByte(0xCC), regs.cc);
    }

}
