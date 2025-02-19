/*
 * Copyright (C) 2022-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

import static ca.craigthomas.yacoco3e.datatypes.AddressingMode.INDEXED;
import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.CC_E;

/**
 * The Opcode class stores information related to a specific machine operation.
 * The opcode value itself, the mnemonic representation, the addressing mode,
 * the size of any immediate values read, and whether the size of input is
 * a word length.
 */
public class VoidInstruction extends Instruction
{
    @FunctionalInterface
    public interface VoidInterface
    {
        void apply(IOController io, UnsignedByte memoryByte, UnsignedWord address);
    }

    protected VoidInterface operation;

    public VoidInstruction(int opcode,
                           int ticks,
                           String mnemonic,
                           AddressingMode addressingMode,
                           VoidInterface operation
    ) {
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.ticks = ticks;
        this.addressingMode = addressingMode;
        this.operation = operation;
        this.isByteSized = true;
        this.isValidInstruction = true;
        this.addressRead = new UnsignedWord();
    }

    public int call(IOController io) {
        operation.apply(io, byteRead, addressRead);
        if (mnemonic.equals("RTI")) {
            return 6 + (io.regs.cc.isMasked(CC_E) ? 9 : 0);
        }
        return addressingMode == INDEXED ? ticks + numBytesRead : ticks;
    }

    /**
     * Waits for an interrupt to occur.
     */
    public static void sync(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        io.waitForIRQ = true;
    }

    /**
     * Jumps to the specified address.
     */
    public static void unconditionalJump(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.pc.set(address);
    }

    /**
     * Pushes machine state onto the stack, and waits for an interrupt.
     */
    public static void callAndWaitForInterrupt(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.and(memoryByte);
        io.regs.cc.or(CC_E);
        io.pushStack(Register.S, io.regs.pc);
        io.pushStack(Register.S, io.regs.u);
        io.pushStack(Register.S, io.regs.y);
        io.pushStack(Register.S, io.regs.x);
        io.pushStack(Register.S, io.regs.dp);
        io.pushStack(Register.S, io.regs.b);
        io.pushStack(Register.S, io.regs.a);
        io.pushStack(Register.S, io.regs.cc);
        io.waitForIRQ = true;
    }

    /**
     * Restores register flags from the stack.
     */
    public static void returnFromInterrupt(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.set(io.popStack(Register.S));
        if (io.regs.cc.isMasked(CC_E)) {
            io.regs.a.set(io.popStack(Register.S));
            io.regs.b.set(io.popStack(Register.S));
            io.regs.dp.set(io.popStack(Register.S));
            io.regs.x.set(new UnsignedWord(io.popStack(Register.S), io.popStack(Register.S)));
            io.regs.y.set(new UnsignedWord(io.popStack(Register.S), io.popStack(Register.S)));
            io.regs.u.set(new UnsignedWord(io.popStack(Register.S), io.popStack(Register.S)));
        }
        io.regs.pc.set(new UnsignedWord(io.popStack(Register.S), io.popStack(Register.S)));
    }

    /**
     * Restores the program counter from the stack.
     */
    public static void returnFromSubroutine(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.pc.set(new UnsignedWord(io.popStack(Register.S), io.popStack(Register.S)));
    }

    /**
     * Does nothing (nop).
     */
    public static void noOperation(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
    }

    /**
     * Jumps to the specified address, pushing the value of the PC onto the S
     * stack before jumping.
     */
    public static void jumpToSubroutine(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        io.pushStack(Register.S, io.regs.pc);
        io.regs.pc.set(address);
    }

    /**
     * Saves all registers to the stack, and jumps to the memory location
     * read at the specified address.
     */
    public static void softwareInterrupt(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.or(CC_E);
        io.pushStack(Register.S, io.regs.pc);
        io.pushStack(Register.S, io.regs.u);
        io.pushStack(Register.S, io.regs.y);
        io.pushStack(Register.S, io.regs.x);
        io.pushStack(Register.S, io.regs.dp);
        io.pushStack(Register.S, io.regs.b);
        io.pushStack(Register.S, io.regs.a);
        io.pushStack(Register.S, io.regs.cc);
        io.regs.pc.set(io.readWord(Instruction.SWI));
    }

    /**
     * Saves all registers to the stack, and jumps to the memory location
     * read at the specified address.
     */
    public static void softwareInterrupt2(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.or(CC_E);
        io.pushStack(Register.S, io.regs.pc);
        io.pushStack(Register.S, io.regs.u);
        io.pushStack(Register.S, io.regs.y);
        io.pushStack(Register.S, io.regs.x);
        io.pushStack(Register.S, io.regs.dp);
        io.pushStack(Register.S, io.regs.b);
        io.pushStack(Register.S, io.regs.a);
        io.pushStack(Register.S, io.regs.cc);
        io.regs.pc.set(io.readWord(Instruction.SWI2));
    }

    /**
     * Saves all registers to the stack, and jumps to the memory location
     * read at the specified address.
     */
    public static void softwareInterrupt3(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.or(CC_E);
        io.pushStack(Register.S, io.regs.pc);
        io.pushStack(Register.S, io.regs.u);
        io.pushStack(Register.S, io.regs.y);
        io.pushStack(Register.S, io.regs.x);
        io.pushStack(Register.S, io.regs.dp);
        io.pushStack(Register.S, io.regs.b);
        io.pushStack(Register.S, io.regs.a);
        io.pushStack(Register.S, io.regs.cc);
        io.regs.pc.set(io.readWord(Instruction.SWI3));
    }

    /**
     * Exchanges the contents of one register with another. The registers to exchange
     * depend on the post byte that is read.
     */
    public static void exchangeRegister(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        UnsignedWord temp = new UnsignedWord();
        UnsignedByte tempByte = new UnsignedByte();
        switch (memoryByte.getShort()) {
            /* A:B <-> X */
            case 0x01:
            case 0x10:
                temp.set(io.regs.x);
                io.regs.x.set(io.regs.getD());
                io.regs.setD(temp);
                break;

            /* A:B <-> Y */
            case 0x02:
            case 0x20:
                temp.set(io.regs.y);
                io.regs.y.set(io.regs.getD());
                io.regs.setD(temp);
                break;

            /* A:B <-> U */
            case 0x03:
            case 0x30:
                temp.set(io.regs.u);
                io.regs.u.set(io.regs.getD());
                io.regs.setD(temp);
                break;

            /* A:B <-> S */
            case 0x04:
            case 0x40:
                temp.set(io.regs.s);
                io.regs.s.set(io.regs.getD());
                io.regs.setD(temp);
                break;

            /* A:B <-> PC */
            case 0x05:
            case 0x50:
                temp.set(io.regs.pc);
                io.regs.pc.set(io.regs.getD());
                io.regs.setD(temp);
                break;

            /* X <-> Y */
            case 0x12:
            case 0x21:
                temp.set(io.regs.x);
                io.regs.x.set(io.regs.y);
                io.regs.y.set(temp);
                break;

            /* X <-> U */
            case 0x13:
            case 0x31:
                temp.set(io.regs.x);
                io.regs.x.set(io.regs.u);
                io.regs.u.set(temp);
                break;

            /* X <-> S */
            case 0x14:
            case 0x41:
                temp.set(io.regs.x);
                io.regs.x.set(io.regs.s);
                io.regs.s.set(temp);
                break;

            /* X <-> PC */
            case 0x15:
            case 0x51:
                temp.set(io.regs.x);
                io.regs.x.set(io.regs.pc);
                io.regs.pc.set(temp);
                break;

            /* Y <-> U */
            case 0x23:
            case 0x32:
                temp.set(io.regs.y);
                io.regs.y.set(io.regs.u);
                io.regs.u.set(temp);
                break;

            /* Y <-> S */
            case 0x24:
            case 0x42:
                temp.set(io.regs.s);
                io.regs.s.set(io.regs.y);
                io.regs.y.set(temp);
                break;

            /* Y <-> PC */
            case 0x25:
            case 0x52:
                temp.set(io.regs.y);
                io.regs.y.set(io.regs.pc);
                io.regs.pc.set(temp);
                break;

            /* U <-> S */
            case 0x34:
            case 0x43:
                temp.set(io.regs.u);
                io.regs.u.set(io.regs.s);
                io.regs.s.set(temp);
                break;

            /* U <-> PC */
            case 0x35:
            case 0x53:
                temp.set(io.regs.u);
                io.regs.u.set(io.regs.pc);
                io.regs.pc.set(temp);
                break;

            /* S <-> PC */
            case 0x45:
            case 0x54:
                temp.set(io.regs.s);
                io.regs.s.set(io.regs.pc);
                io.regs.pc.set(temp);
                break;

            /* A <-> B */
            case 0x89:
            case 0x98:
                tempByte.set(io.regs.a);
                io.regs.a.set(io.regs.b);
                io.regs.b.set(tempByte);
                break;

            /* A <-> CC */
            case 0x8A:
            case 0xA8:
                tempByte.set(io.regs.a);
                io.regs.a.set(io.regs.cc);
                io.regs.cc.set(tempByte);
                break;

            /* A <-> DP */
            case 0x8B:
            case 0xB8:
                tempByte.set(io.regs.a);
                io.regs.a.set(io.regs.dp);
                io.regs.dp.set(tempByte);
                break;

            /* B <-> CC */
            case 0x9A:
            case 0xA9:
                tempByte.set(io.regs.b);
                io.regs.b.set(io.regs.cc);
                io.regs.cc.set(tempByte);
                break;

            /* B <-> DP */
            case 0x9B:
            case 0xB9:
                tempByte.set(io.regs.b);
                io.regs.b.set(io.regs.dp);
                io.regs.dp.set(tempByte);
                break;

            /* CC <-> DP */
            case 0xAB:
            case 0xBA:
                tempByte.set(io.regs.cc);
                io.regs.cc.set(io.regs.dp);
                io.regs.dp.set(tempByte);
                break;

            /* Self to self - ignored */
            case 0x00:
            case 0x11:
            case 0x22:
            case 0x33:
            case 0x44:
            case 0x55:
            case 0x88:
            case 0x99:
            case 0xAA:
            case 0xBB:
                break;

            default:
                throw new RuntimeException("Illegal register exchange " + memoryByte);
        }
    }

    /**
     * Transfers the value of one register to another. The register to transfer
     * to and from is encoded by the post byte read.
     */
    public static void transferRegister(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        switch (memoryByte.getShort()) {
            /* A:B -> X */
            case 0x01:
                io.regs.x.set(io.regs.getD());
                break;

            /* A:B -> Y */
            case 0x02:
                io.regs.y.set(io.regs.getD());
                break;

            /* A:B -> U */
            case 0x03:
                io.regs.u.set(io.regs.getD());
                break;

            /* A:B -> S */
            case 0x04:
                io.regs.s.set(io.regs.getD());
                break;

            /* A:B -> PC */
            case 0x05:
                io.regs.pc.set(io.regs.getD());
                break;

            /* X -> A:B */
            case 0x10:
                io.regs.setD(io.regs.x);
                break;

            /* X -> Y */
            case 0x12:
                io.regs.y.set(io.regs.x);
                break;

            /* X -> U */
            case 0x13:
                io.regs.u.set(io.regs.x);
                break;

            /* X -> S */
            case 0x14:
                io.regs.s.set(io.regs.x);
                break;

            /* X -> PC */
            case 0x15:
                io.regs.pc.set(io.regs.x);
                break;

            /* Y -> A:B */
            case 0x20:
                io.regs.setD(io.regs.y);
                break;

            /* Y -> X */
            case 0x21:
                io.regs.x.set(io.regs.y);
                break;

            /* Y -> U */
            case 0x23:
                io.regs.u.set(io.regs.y);
                break;

            /* Y -> S */
            case 0x24:
                io.regs.s.set(io.regs.y);
                break;

            /* Y -> PC */
            case 0x25:
                io.regs.pc.set(io.regs.y);
                break;

            /* U -> A:B */
            case 0x30:
                io.regs.setD(io.regs.u);
                break;

            /* U -> X */
            case 0x31:
                io.regs.x.set(io.regs.u);
                break;

            /* U -> Y */
            case 0x32:
                io.regs.y.set(io.regs.u);
                break;

            /* U -> S */
            case 0x34:
                io.regs.s.set(io.regs.u);
                break;

            /* U -> PC */
            case 0x35:
                io.regs.pc.set(io.regs.u);
                break;

            /* S -> A:B */
            case 0x40:
                io.regs.setD(io.regs.s);
                break;

            /* S -> X */
            case 0x41:
                io.regs.x.set(io.regs.s);
                break;

            /* S -> Y */
            case 0x42:
                io.regs.y.set(io.regs.s);
                break;

            /* S -> U */
            case 0x43:
                io.regs.u.set(io.regs.s);
                break;

            /* S -> PC */
            case 0x45:
                io.regs.pc.set(io.regs.s);
                break;

            /* PC -> A:B */
            case 0x50:
                io.regs.setD(io.regs.pc);
                break;

            /* PC -> X */
            case 0x51:
                io.regs.x.set(io.regs.pc);
                break;

            /* PC -> Y */
            case 0x52:
                io.regs.y.set(io.regs.pc);
                break;

            /* PC -> U */
            case 0x53:
                io.regs.u.set(io.regs.pc);
                break;

            /* PC -> S */
            case 0x54:
                io.regs.s.set(io.regs.pc);
                break;

            /* A -> B */
            case 0x89:
                io.regs.b.set(io.regs.a);
                break;

            /* A -> CC */
            case 0x8A:
                io.regs.cc.set(io.regs.a);
                break;

            /* A -> DP */
            case 0x8B:
                io.regs.dp.set(io.regs.a);
                break;

            /* B -> A */
            case 0x98:
                io.regs.a.set(io.regs.b);
                break;

            /* B -> CC */
            case 0x9A:
                io.regs.cc.set(io.regs.b);
                break;

            /* B -> DP */
            case 0x9B:
                io.regs.dp.set(io.regs.b);
                break;

            /* CC -> A */
            case 0xA8:
                io.regs.a.set(io.regs.cc);
                break;

            /* CC -> B */
            case 0xA9:
                io.regs.b.set(io.regs.cc);
                break;

            /* CC -> DP */
            case 0xAB:
                io.regs.dp.set(io.regs.cc);
                break;

            /* DP -> A */
            case 0xB8:
                io.regs.a.set(io.regs.dp);
                break;

            /* DP -> B */
            case 0xB9:
                io.regs.b.set(io.regs.dp);
                break;

            /* DP -> CC */
            case 0xBA:
                io.regs.cc.set(io.regs.dp);
                break;

            /* Self to self - ignored */
            case 0x00:
            case 0x11:
            case 0x22:
            case 0x33:
            case 0x44:
            case 0x55:
            case 0x88:
            case 0x99:
            case 0xAA:
            case 0xBB:
                break;

            default:
                throw new RuntimeException("Illegal register transfer " + memoryByte);
        }
    }
}
