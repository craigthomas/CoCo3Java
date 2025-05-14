/*
 * Copyright (C) 2023-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;
import org.junit.Before;
import org.junit.Test;

import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;
import static org.junit.Assert.*;

public class VoidInstructionTest {
    private IOController io;
    private RegisterSet regs;
    private CPU cpu;
    private int extendedAddress;

    @Before
    public void setUp() {
        Screen screen = new Screen(1);
        Cassette cassette = new Cassette();
        regs = new RegisterSet();
        io = new IOController(new Memory(), regs, new EmulatedKeyboard(), screen, cassette, null);
        cpu = new CPU(io);
        extendedAddress = 0xC0A0;
        io.regs.pc.set(0);
    }

    @Test
    public void testSync() {
        VoidInstruction.sync(io, null, null);
        assertTrue(io.waitForIRQ);
    }

    @Test
    public void testUnconditionalJumpCorrect() {
        VoidInstruction.unconditionalJump(io, null, new UnsignedWord(0xBEEF));
        assertEquals(0xBEEF, regs.pc.get());
    }

    @Test
    public void testUnconditionalJumpDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0x00);
        io.writeByte(0x0000, 0x0E);
        io.writeByte(0x0001, 0x0A);
        cpu.executeInstruction();
        assertEquals(0x000A, regs.pc.get());
    }

    @Test
    public void testUnconditionalJumpIndexedCorrect() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x6E80);
        regs.x.set(extendedAddress);
        cpu.executeInstruction();
        assertEquals(extendedAddress, regs.pc.get());
    }

    @Test
    public void testUnconditionalJumpExtendedCorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x7E);
        io.writeWord(0x0001, extendedAddress);
        cpu.executeInstruction();
        assertEquals(extendedAddress, regs.pc.get());
    }

    @Test
    public void testJumpToSubroutineSavesPCSetsPCReturnsCorrect() {
        regs.s.set(0x0200);
        regs.pc.set(0xBEEF);
        VoidInstruction.jumpToSubroutine(io, null, new UnsignedWord(0xCAFE));
        assertEquals(0xEF, io.readByte(new UnsignedWord(0x01FF)).get());
        assertEquals(0xBE, io.readByte(new UnsignedWord(0x01FE)).get());
        assertEquals(0xCAFE, regs.pc.get());
    }

    @Test
    public void testJSRDirectCorrect() throws MalformedInstructionException {
        regs.dp.set(0xCA);
        io.writeWord(0x0000, 0x9DFE);
        cpu.executeInstruction();
        assertEquals(0xCAFE, regs.pc.get());
    }

    @Test
    public void testJSRIndexedCorrect() throws MalformedInstructionException {
        regs.y.set(extendedAddress);
        io.writeWord(0x0000, 0xADA0);
        cpu.executeInstruction();
        assertEquals(extendedAddress, regs.pc.get());
    }

    @Test
    public void testJSRExtendedCalled() throws MalformedInstructionException {
        io.writeByte(0x0000, 0xBD);
        io.writeWord(0x0001, extendedAddress);
        cpu.executeInstruction();
        assertEquals(extendedAddress, regs.pc.get());
    }

    @Test
    public void testSWI2Correct() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x103F);
        io.writeByte(Instruction.SWI2.get(), 0x56);
        io.writeByte(Instruction.SWI2.next().get(), 0x78);
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        regs.s.set(0xA000);
        regs.u.set(0xDEAD);
        regs.y.set(0xBEEF);
        regs.x.set(0xCAFE);
        regs.dp.set(0xAB);
        regs.b.set(0xCD);
        regs.a.set(0xEF);
        cpu.executeInstruction();
        assertEquals(0x02, io.readByte(0x9FFF).get());
        assertEquals(0x00, io.readByte(0x9FFE).get());
        assertEquals(0xAD, io.readByte(0x9FFD).get());
        assertEquals(0xDE, io.readByte(0x9FFC).get());
        assertEquals(0xEF, io.readByte(0x9FFB).get());
        assertEquals(0xBE, io.readByte(0x9FFA).get());
        assertEquals(0xFE, io.readByte(0x9FF9).get());
        assertEquals(0xCA, io.readByte(0x9FF8).get());
        assertEquals(0xAB, io.readByte(0x9FF7).get());
        assertEquals(0xCD, io.readByte(0x9FF6).get());
        assertEquals(0xEF, io.readByte(0x9FF5).get());
        assertEquals(0x8A, io.readByte(0x9FF4).get());
//        assertEquals(0x5678, regs.pc.getInt());
    }

    @Test
    public void testSWI3Correct() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x113F);
        io.writeWord(Instruction.SWI3, 0x5678);
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        regs.s.set(0xA000);
        regs.u.set(0xDEAD);
        regs.y.set(0xBEEF);
        regs.x.set(0xCAFE);
        regs.dp.set(0xAB);
        regs.b.set(0xCD);
        regs.a.set(0xEF);
        cpu.executeInstruction();
        assertEquals(0x02, io.readByte(0x9FFF).get());
        assertEquals(0x00, io.readByte(0x9FFE).get());
        assertEquals(0xAD, io.readByte(0x9FFD).get());
        assertEquals(0xDE, io.readByte(0x9FFC).get());
        assertEquals(0xEF, io.readByte(0x9FFB).get());
        assertEquals(0xBE, io.readByte(0x9FFA).get());
        assertEquals(0xFE, io.readByte(0x9FF9).get());
        assertEquals(0xCA, io.readByte(0x9FF8).get());
        assertEquals(0xAB, io.readByte(0x9FF7).get());
        assertEquals(0xCD, io.readByte(0x9FF6).get());
        assertEquals(0xEF, io.readByte(0x9FF5).get());
        assertEquals(0x8A, io.readByte(0x9FF4).get());
//        assertEquals(0x5678, regs.pc.getInt());
    }

    @Test
    public void testSWICorrect() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x3F);
        io.writeWord(Instruction.SWI3, 0x5678);
        regs.cc.or(CC_N);
        regs.cc.or(CC_V);
        regs.s.set(0xA000);
        regs.u.set(0xDEAD);
        regs.y.set(0xBEEF);
        regs.x.set(0xCAFE);
        regs.dp.set(0xAB);
        regs.b.set(0xCD);
        regs.a.set(0xEF);
        cpu.executeInstruction();
        assertEquals(0x01, io.readByte(0x9FFF).get());
        assertEquals(0x00, io.readByte(0x9FFE).get());
        assertEquals(0xAD, io.readByte(0x9FFD).get());
        assertEquals(0xDE, io.readByte(0x9FFC).get());
        assertEquals(0xEF, io.readByte(0x9FFB).get());
        assertEquals(0xBE, io.readByte(0x9FFA).get());
        assertEquals(0xFE, io.readByte(0x9FF9).get());
        assertEquals(0xCA, io.readByte(0x9FF8).get());
        assertEquals(0xAB, io.readByte(0x9FF7).get());
        assertEquals(0xCD, io.readByte(0x9FF6).get());
        assertEquals(0xEF, io.readByte(0x9FF5).get());
        assertEquals(0x8A, io.readByte(0x9FF4).get());
//        assertEquals(0x5678, regs.pc.getInt());
    }

    @Test
    public void testRTSWorksCorrectly() throws MalformedInstructionException {
        regs.s.set(0x0020);
        io.writeByte(0x0000, 0x39);
        io.writeWord(0x0020, 0xBEEF);
        cpu.executeInstruction();
        assertEquals(0xBEEF, regs.pc.get());
    }

    @Test
    public void testRTIEverything() throws MalformedInstructionException {
        regs.cc.or(CC_E);
        regs.s.set(0x9FF4);
        io.writeByte(0x9FFF, 0xCC);
        io.writeByte(0x9FFE, 0xBB);
        io.writeByte(0x9FFD, 0xAA);
        io.writeByte(0x9FFC, 0x99);
        io.writeByte(0x9FFB, 0x88);
        io.writeByte(0x9FFA, 0x77);
        io.writeByte(0x9FF9, 0x66);
        io.writeByte(0x9FF8, 0x55);
        io.writeByte(0x9FF7, 0x03);
        io.writeByte(0x9FF6, 0x02);
        io.writeByte(0x9FF5, 0x01);
        io.writeByte(0x9FF4, CC_E);
        io.writeByte(0x00, 0x3B);
        cpu.executeInstruction();
        assertEquals(0x01, regs.a.get());
        assertEquals(0x02, regs.b.get());
        assertEquals(0x03, regs.dp.get());
        assertEquals(CC_E, regs.cc.get());
        assertEquals(0x5566, regs.x.get());
        assertEquals(0x7788, regs.y.get());
        assertEquals(0x99AA, regs.u.get());
        assertEquals(0xBBCC, regs.pc.get());
    }

    @Test
    public void testRTIPCOnly() throws MalformedInstructionException {
        regs.s.set(0x9FF3);
        io.writeByte(0x9FF5, 0xEF);
        io.writeByte(0x9FF4, 0xBE);
        io.writeByte(0x9FF3, 0x00);
        io.writeByte(0x0000, 0x3B);
        cpu.executeInstruction();
        assertEquals(0xBEEF, regs.pc.get());
    }

    @Test
    public void testTransferDtoX() throws MalformedInstructionException {
        regs.setD(0xDEAD);
        regs.x.set(0xBEEF);
        io.writeWord(0x0000, 0x1F01);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.getD().get());
        assertEquals(0xDEAD, regs.x.get());
    }

    @Test
    public void testTransferXtoD() throws MalformedInstructionException {
        regs.x.set(0xDEAD);
        regs.setD(0xBEEF);
        io.writeWord(0x0000, 0x1F10);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.x.get());
        assertEquals(0xDEAD, regs.getD().get());
    }

    @Test
    public void testTransferDtoY() throws MalformedInstructionException {
        regs.setD(0xDEAD);
        regs.y.set(0xBEEF);
        io.writeWord(0x0000, 0x1F02);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.getD().get());
        assertEquals(0xDEAD, regs.y.get());
    }

    @Test
    public void testTransferYtoD() throws MalformedInstructionException {
        regs.y.set(0xDEAD);
        regs.setD(0xBEEF);
        io.writeWord(0x0000, 0x1F20);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.y.get());
        assertEquals(0xDEAD, regs.getD().get());
    }

    @Test
    public void testTransferDtoU() throws MalformedInstructionException {
        regs.setD(0xDEAD);
        regs.u.set(0xBEEF);
        io.writeWord(0x0000, 0x1F03);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.getD().get());
        assertEquals(0xDEAD, regs.u.get());
    }

    @Test
    public void testTransferUtoD() throws MalformedInstructionException {
        regs.u.set(0xDEAD);
        regs.setD(0xBEEF);
        io.writeWord(0x0000, 0x1F30);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.u.get());
        assertEquals(0xDEAD, regs.getD().get());
    }

    @Test
    public void testTransferDtoS() throws MalformedInstructionException {
        regs.setD(0xDEAD);
        regs.s.set(0xBEEF);
        io.writeWord(0x0000, 0x1F04);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.getD().get());
        assertEquals(0xDEAD, regs.s.get());
    }

    @Test
    public void testTransferStoD() throws MalformedInstructionException {
        regs.s.set(0xDEAD);
        regs.setD(0xBEEF);
        io.writeWord(0x0000, 0x1F40);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.s.get());
        assertEquals(0xDEAD, regs.getD().get());
    }

    @Test
    public void testTransferDtoPC() throws MalformedInstructionException {
        regs.setD(0xDEAD);
        io.writeWord(0x0000, 0x1F05);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.getD().get());
        assertEquals(0xDEAD, regs.pc.get());
    }

    @Test
    public void testTransferPCtoD() throws MalformedInstructionException {
        regs.setD(0xBEEF);
        io.writeWord(0x0000, 0x1F50);
        cpu.executeInstruction();
        /* PC will have advanced */
        assertEquals(0x0002, regs.pc.get());
        assertEquals(0x0002, regs.getD().get());
    }

    @Test
    public void testTransferXtoY() throws MalformedInstructionException {
        regs.x.set(0xDEAD);
        regs.y.set(0xBEEF);
        io.writeWord(0x0000, 0x1F12);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.x.get());
        assertEquals(0xDEAD, regs.y.get());
    }

    @Test
    public void testTransferYtoX() throws MalformedInstructionException {
        regs.y.set(0xDEAD);
        regs.x.set(0xBEEF);
        io.writeWord(0x0000, 0x1F21);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.y.get());
        assertEquals(0xDEAD, regs.x.get());
    }

    @Test
    public void testTransferXtoU() throws MalformedInstructionException {
        regs.x.set(0xDEAD);
        regs.u.set(0xBEEF);
        io.writeWord(0x0000, 0x1F13);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.x.get());
        assertEquals(0xDEAD, regs.u.get());
    }

    @Test
    public void testTransferUtoX() throws MalformedInstructionException {
        regs.u.set(0xDEAD);
        regs.x.set(0xBEEF);
        io.writeWord(0x0000, 0x1F31);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.u.get());
        assertEquals(0xDEAD, regs.x.get());
    }

    @Test
    public void testTransferXtoS() throws MalformedInstructionException {
        regs.x.set(0xDEAD);
        regs.s.set(0xBEEF);
        io.writeWord(0x0000, 0x1F14);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.x.get());
        assertEquals(0xDEAD, regs.s.get());
    }

    @Test
    public void testTransferStoX() throws MalformedInstructionException {
        regs.s.set(0xDEAD);
        regs.x.set(0xBEEF);
        io.writeWord(0x0000, 0x1F41);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.s.get());
        assertEquals(0xDEAD, regs.x.get());
    }

    @Test
    public void testTransferXtoPC() throws MalformedInstructionException {
        regs.x.set(0xDEAD);
        io.writeWord(0x0000, 0x1F15);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.x.get());
        assertEquals(0xDEAD, regs.pc.get());
    }

    @Test
    public void testTransferPCtoX() throws MalformedInstructionException {
        regs.x.set(0xBEEF);
        io.writeWord(0x0000, 0x1F51);
        cpu.executeInstruction();
        /* PC will have advanced */
        assertEquals(0x0002, regs.pc.get());
        assertEquals(0x0002, regs.x.get());
    }

    @Test
    public void testTransferYtoU() throws MalformedInstructionException {
        regs.y.set(0xDEAD);
        regs.u.set(0xBEEF);
        io.writeWord(0x0000, 0x1F23);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.y.get());
        assertEquals(0xDEAD, regs.u.get());
    }

    @Test
    public void testTransferUtoY() throws MalformedInstructionException {
        regs.u.set(0xDEAD);
        regs.y.set(0xBEEF);
        io.writeWord(0x0000, 0x1F32);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.u.get());
        assertEquals(0xDEAD, regs.y.get());
    }

    @Test
    public void testTransferYtoS() throws MalformedInstructionException {
        regs.y.set(0xDEAD);
        regs.s.set(0xBEEF);
        io.writeWord(0x0000, 0x1F24);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.y.get());
        assertEquals(0xDEAD, regs.s.get());
    }

    @Test
    public void testTransferStoY() throws MalformedInstructionException {
        regs.s.set(0xDEAD);
        regs.y.set(0xBEEF);
        io.writeWord(0x0000, 0x1F42);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.s.get());
        assertEquals(0xDEAD, regs.y.get());
    }

    @Test
    public void testTransferYtoPC() throws MalformedInstructionException {
        regs.y.set(0xDEAD);
        io.writeWord(0x0000, 0x1F25);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.y.get());
        assertEquals(0xDEAD, regs.pc.get());
    }

    @Test
    public void testTransferPCtoY() throws MalformedInstructionException {
        regs.y.set(0xBEEF);
        io.writeWord(0x0000, 0x1F52);
        cpu.executeInstruction();
        /* PC will have advanced */
        assertEquals(0x0002, regs.pc.get());
        assertEquals(0x0002, regs.y.get());
    }

    @Test
    public void testTransferUtoS() throws MalformedInstructionException {
        regs.u.set(0xDEAD);
        io.regs.s.set(0xBEEF);
        io.writeWord(0x0000, 0x1F34);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.u.get());
        assertEquals(0xDEAD, regs.s.get());
    }

    @Test
    public void testTransferStoU() throws MalformedInstructionException {
        io.regs.s.set(0xDEAD);
        regs.u.set(0xBEEF);
        io.writeWord(0x0000, 0x1F43);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.s.get());
        assertEquals(0xDEAD, regs.u.get());
    }

    @Test
    public void testTransferUtoPC() throws MalformedInstructionException {
        regs.u.set(0xDEAD);
        io.writeWord(0x0000, 0x1F35);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.u.get());
        assertEquals(0xDEAD, regs.pc.get());
    }

    @Test
    public void testTransferPCtoU() throws MalformedInstructionException {
        regs.u.set(0xBEEF);
        io.writeWord(0x0000, 0x1F53);
        cpu.executeInstruction();
        /* PC will have advanced */
        assertEquals(0x0002, regs.pc.get());
        assertEquals(0x0002, regs.u.get());
    }

    @Test
    public void testTransferStoPC() throws MalformedInstructionException {
        regs.s.set(0xDEAD);
        io.writeWord(0x0000, 0x1F45);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.s.get());
        assertEquals(0xDEAD, regs.pc.get());
    }

    @Test
    public void testTransferPCtoS() throws MalformedInstructionException {
        regs.s.set(0xBEEF);
        io.writeWord(0x0000, 0x1F54);
        cpu.executeInstruction();
        /* PC will have advanced */
        assertEquals(0x0002, regs.pc.get());
        assertEquals(0x0002, regs.s.get());
    }

    @Test
    public void testTransferAtoB() throws MalformedInstructionException {
        regs.a.set(0xDE);
        regs.b.set(0xBE);
        io.writeWord(0x0000, 0x1F89);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.a.get());
        assertEquals(0xDE, regs.b.get());
    }

    @Test
    public void testTransferBtoA() throws MalformedInstructionException {
        regs.b.set(0xDE);
        regs.a.set(0xBE);
        io.writeWord(0x0000, 0x1F98);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.a.get());
        assertEquals(0xDE, regs.b.get());
    }

    @Test
    public void testTransferAtoCC() throws MalformedInstructionException {
        regs.a.set(0xDE);
        regs.cc.set(0xBE);
        io.writeWord(0x0000, 0x1F8A);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.a.get());
        assertEquals(0xDE, regs.cc.get());
    }

    @Test
    public void testTransferCCtoA() throws MalformedInstructionException {
        regs.cc.set(0xDE);
        regs.a.set(0xBE);
        io.writeWord(0x0000, 0x1FA8);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.cc.get());
        assertEquals(0xDE, regs.a.get());
    }

    @Test
    public void testTransferAtoDP() throws MalformedInstructionException {
        regs.a.set(0xDE);
        regs.dp.set(0xBE);
        io.writeWord(0x0000, 0x1F8B);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.a.get());
        assertEquals(0xDE, regs.dp.get());
    }

    @Test
    public void testTransferDPtoA() throws MalformedInstructionException {
        regs.dp.set(0xDE);
        regs.a.set(0xBE);
        io.writeWord(0x0000, 0x1FB8);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.dp.get());
        assertEquals(0xDE, regs.a.get());
    }

    @Test
    public void testTransferBtoCC() throws MalformedInstructionException {
        regs.b.set(0xDE);
        regs.cc.set(0xBE);
        io.writeWord(0x0000, 0x1F9A);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.b.get());
        assertEquals(0xDE, regs.cc.get());
    }

    @Test
    public void testTransferCCtoB() throws MalformedInstructionException {
        regs.cc.set(0xDE);
        regs.b.set(0xBE);
        io.writeWord(0x0000, 0x1FA9);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.cc.get());
        assertEquals(0xDE, regs.b.get());
    }

    @Test
    public void testTransferBtoDP() throws MalformedInstructionException {
        regs.b.set(0xDE);
        regs.dp.set(0xBE);
        io.writeWord(0x0000, 0x1F9B);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.b.get());
        assertEquals(0xDE, regs.dp.get());
    }

    @Test
    public void testTransferDPtoB() throws MalformedInstructionException {
        regs.dp.set(0xDE);
        regs.a.set(0xBE);
        io.writeWord(0x0000, 0x1FB9);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.dp.get());
        assertEquals(0xDE, regs.b.get());
    }

    @Test
    public void testTransferCCtoDP() throws MalformedInstructionException {
        regs.cc.set(0xDE);
        regs.dp.set(0xBE);
        io.writeWord(0x0000, 0x1FAB);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.cc.get());
        assertEquals(0xDE, regs.dp.get());
    }

    @Test
    public void testTransferDPtoCC() throws MalformedInstructionException {
        regs.dp.set(0xDE);
        regs.cc.set(0xBE);
        io.writeWord(0x0000, 0x1FBA);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.dp.get());
        assertEquals(0xDE, regs.cc.get());
    }

    @Test
    public void testTransferAtoA() throws MalformedInstructionException {
        regs.a.set(0xDE);
        io.writeWord(0x0000, 0x1F88);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.a.get());
    }

    @Test
    public void testTransferDtoD() throws MalformedInstructionException {
        regs.setD(0xDEAD);
        io.writeWord(0x0000, 0x1F00);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.getD().get());
    }

    @Test
    public void testTransferXtoX() throws MalformedInstructionException {
        regs.x.set(0xDEAD);
        io.writeWord(0x0000, 0x1F11);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.x.get());
    }

    @Test
    public void testTransferYtoY() throws MalformedInstructionException {
        regs.y.set(0xDEAD);
        io.writeWord(0x0000, 0x1F22);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.y.get());
    }

    @Test
    public void testTransferUtoU() throws MalformedInstructionException {
        regs.u.set(0xDEAD);
        io.writeWord(0x0000, 0x1F33);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.u.get());
    }

    @Test
    public void testTransferStoS() throws MalformedInstructionException {
        regs.s.set(0xDEAD);
        io.writeWord(0x0000, 0x1F44);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.s.get());
    }

    @Test
    public void testTransferPCtoPC() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1F55);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testTransferBtoB() throws MalformedInstructionException {
        regs.b.set(0xDE);
        io.writeWord(0x0000, 0x1F99);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.b.get());
    }

    @Test
    public void testTransferCCtoCC() throws MalformedInstructionException {
        regs.cc.set(0xDE);
        io.writeWord(0x0000, 0x1FAA);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.cc.get());
    }

    @Test
    public void testTransferDPtoDP() throws MalformedInstructionException {
        regs.dp.set(0xDE);
        io.writeWord(0x0000, 0x1FBB);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.dp.get());
    }

    @Test(expected = RuntimeException.class)
    public void testTransferIllegalThrowsRuntimeException() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1FBF);
        cpu.executeInstruction();
    }

    @Test
    public void testExchangeDandX() throws MalformedInstructionException {
        regs.setD(0xDEAD);
        regs.x.set(0xBEEF);
        io.writeWord(0x0000, 0x1E01);
        cpu.executeInstruction();
        assertEquals(0xBEEF, regs.getD().get());
        assertEquals(0xDEAD, regs.x.get());
    }

    @Test
    public void testExchangeDandY() throws MalformedInstructionException {
        regs.setD(0xDEAD);
        regs.y.set(0xBEEF);
        io.writeWord(0x0000, 0x1E02);
        cpu.executeInstruction();
        assertEquals(0xBEEF, regs.getD().get());
        assertEquals(0xDEAD, regs.y.get());
    }

    @Test
    public void testExchangeDandU() throws MalformedInstructionException {
        regs.setD(0xDEAD);
        regs.u.set(0xBEEF);
        io.writeWord(0x0000, 0x1E03);
        cpu.executeInstruction();
        assertEquals(0xBEEF, regs.getD().get());
        assertEquals(0xDEAD, regs.u.get());
    }

    @Test
    public void testExchangeDandS() throws MalformedInstructionException {
        regs.setD(0xDEAD);
        regs.s.set(0xBEEF);
        io.writeWord(0x0000, 0x1E04);
        cpu.executeInstruction();
        assertEquals(0xBEEF, regs.getD().get());
        assertEquals(0xDEAD, regs.s.get());
    }

    @Test
    public void testExchangeDandPC() throws MalformedInstructionException {
        regs.setD(0xDEAD);
        io.writeWord(0x0000, 0x1E05);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.pc.get());
        assertEquals(0x0002, regs.getD().get());
    }

    @Test
    public void testExchangeXandY() throws MalformedInstructionException {
        regs.x.set(0xDEAD);
        regs.y.set(0xBEEF);
        io.writeWord(0x0000, 0x1E12);
        cpu.executeInstruction();
        assertEquals(0xBEEF, regs.x.get());
        assertEquals(0xDEAD, regs.y.get());
    }

    @Test
    public void testExchangeXandU() throws MalformedInstructionException {
        regs.x.set(0xDEAD);
        regs.u.set(0xBEEF);
        io.writeWord(0x0000, 0x1E13);
        cpu.executeInstruction();
        assertEquals(0xBEEF, regs.x.get());
        assertEquals(0xDEAD, regs.u.get());
    }

    @Test
    public void testExchangeXandS() throws MalformedInstructionException {
        regs.x.set(0xDEAD);
        regs.s.set(0xBEEF);
        io.writeWord(0x0000, 0x1E14);
        cpu.executeInstruction();
        assertEquals(0xBEEF, regs.x.get());
        assertEquals(0xDEAD, regs.s.get());
    }

    @Test
    public void testExchangeXandPC() throws MalformedInstructionException {
        regs.x.set(0xDEAD);
        io.writeWord(0x0000, 0x1E15);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.x.get());
        assertEquals(0xDEAD, regs.pc.get());
    }

    @Test
    public void testExchangeYandU() throws MalformedInstructionException {
        regs.y.set(0xDEAD);
        regs.u.set(0xBEEF);
        io.writeWord(0x0000, 0x1E23);
        cpu.executeInstruction();
        assertEquals(0xBEEF, regs.y.get());
        assertEquals(0xDEAD, regs.u.get());
    }

    @Test
    public void testExchangeYandS() throws MalformedInstructionException {
        regs.y.set(0xDEAD);
        regs.s.set(0xBEEF);
        io.writeWord(0x0000, 0x1E24);
        cpu.executeInstruction();
        assertEquals(0xBEEF, regs.y.get());
        assertEquals(0xDEAD, regs.s.get());
    }

    @Test
    public void testExchangeYandPC() throws MalformedInstructionException {
        regs.y.set(0xDEAD);
        io.writeWord(0x0000, 0x1E25);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.y.get());
        assertEquals(0xDEAD, regs.pc.get());
    }

    @Test
    public void testExchangeUandS() throws MalformedInstructionException {
        regs.u.set(0xDEAD);
        regs.s.set(0xBEEF);
        io.writeWord(0x0000, 0x1E34);
        cpu.executeInstruction();
        assertEquals(0xBEEF, regs.u.get());
        assertEquals(0xDEAD, regs.s.get());
    }

    @Test
    public void testExchangeUandPC() throws MalformedInstructionException {
        regs.u.set(0xDEAD);
        io.writeWord(0x0000, 0x1E35);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.u.get());
        assertEquals(0xDEAD, regs.pc.get());
    }

    @Test
    public void testExchangeSandPC() throws MalformedInstructionException {
        regs.s.set(0xDEAD);
        io.writeWord(0x0000, 0x1E45);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.s.get());
        assertEquals(0xDEAD, regs.pc.get());
    }

    @Test
    public void testExchangeAandB() throws MalformedInstructionException {
        regs.a.set(0xDE);
        regs.b.set(0xAD);
        io.writeWord(0x0000, 0x1E89);
        cpu.executeInstruction();
        assertEquals(0xAD, regs.a.get());
        assertEquals(0xDE, regs.b.get());
    }

    @Test
    public void testExchangeAandDP() throws MalformedInstructionException {
        regs.a.set(0xDE);
        regs.dp.set(0xAD);
        io.writeWord(0x0000, 0x1E8B);
        cpu.executeInstruction();
        assertEquals(0xAD, regs.a.get());
        assertEquals(0xDE, regs.dp.get());
    }

    @Test
    public void testExchangeAandCC() throws MalformedInstructionException {
        regs.a.set(0xDE);
        regs.cc.set(0xAD);
        io.writeWord(0x0000, 0x1E8A);
        cpu.executeInstruction();
        assertEquals(0xAD, regs.a.get());
        assertEquals(0xDE, regs.cc.get());
    }

    @Test
    public void testExchangeBandCC() throws MalformedInstructionException {
        regs.b.set(0xDE);
        regs.cc.set(0xAD);
        io.writeWord(0x0000, 0x1E9A);
        cpu.executeInstruction();
        assertEquals(0xAD, regs.b.get());
        assertEquals(0xDE, regs.cc.get());
    }

    @Test
    public void testExchangeBandDP() throws MalformedInstructionException {
        regs.b.set(0xDE);
        regs.dp.set(0xAD);
        io.writeWord(0x0000, 0x1E9B);
        cpu.executeInstruction();
        assertEquals(0xAD, regs.b.get());
        assertEquals(0xDE, regs.dp.get());
    }

    @Test
    public void testExchangeCCandDP() throws MalformedInstructionException {
        regs.cc.set(0xDE);
        regs.dp.set(0xAD);
        io.writeWord(0x0000, 0x1EAB);
        cpu.executeInstruction();
        assertEquals(0xAD, regs.cc.get());
        assertEquals(0xDE, regs.dp.get());
    }

    @Test
    public void testExchangeAtoA() throws MalformedInstructionException {
        regs.a.set(0xDE);
        io.writeWord(0x0000, 0x1E88);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.a.get());
    }

    @Test
    public void testExchangeDtoD() throws MalformedInstructionException {
        regs.setD(0xDEAD);
        io.writeWord(0x0000, 0x1E00);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.getD().get());
    }

    @Test
    public void testExchangeXtoX() throws MalformedInstructionException {
        regs.x.set(0xDEAD);
        io.writeWord(0x0000, 0x1E11);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.x.get());
    }

    @Test
    public void testExchangeYtoY() throws MalformedInstructionException {
        regs.y.set(0xDEAD);
        io.writeWord(0x0000, 0x1E22);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.y.get());
    }

    @Test
    public void testExchangeUtoU() throws MalformedInstructionException {
        regs.u.set(0xDEAD);
        io.writeWord(0x0000, 0x1E33);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.u.get());
    }

    @Test
    public void testExchangeStoS() throws MalformedInstructionException {
        regs.s.set(0xDEAD);
        io.writeWord(0x0000, 0x1E44);
        cpu.executeInstruction();
        assertEquals(0xDEAD, regs.s.get());
    }

    @Test
    public void testExchangePCtoPC() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1E55);
        cpu.executeInstruction();
        assertEquals(0x0002, regs.pc.get());
    }

    @Test
    public void testTExchangeBtoB() throws MalformedInstructionException {
        regs.b.set(0xDE);
        io.writeWord(0x0000, 0x1E99);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.b.get());
    }

    @Test
    public void testExchangeCCtoCC() throws MalformedInstructionException {
        regs.cc.set(0xDE);
        io.writeWord(0x0000, 0x1EAA);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.cc.get());
    }

    @Test
    public void testExchangeDPtoDP() throws MalformedInstructionException {
        regs.dp.set(0xDE);
        io.writeWord(0x0000, 0x1EBB);
        cpu.executeInstruction();
        assertEquals(0xDE, regs.dp.get());
    }

    @Test(expected = RuntimeException.class)
    public void testExchangeIllegalThrowsRuntimeException() throws MalformedInstructionException {
        io.writeWord(0x0000, 0x1EBF);
        cpu.executeInstruction();
    }

    @Test
    public void testNopIncrementsPC() throws MalformedInstructionException {
        io.writeByte(0x0000, 0x12);
        cpu.executeInstruction();
        assertEquals(0x0001, regs.pc.get());
    }
}
