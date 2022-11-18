/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.MemoryResult;
import ca.craigthomas.yacoco3e.datatypes.Register;
import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import ca.craigthomas.yacoco3e.datatypes.UnsignedWord;

import java.util.function.Function;

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
    protected Function<InstructionBundle, Void> operation;
    protected boolean isRTI;

    public VoidInstruction(int opcode,
                           int ticks,
                           String mnemonic,
                           int addressingMode,
                           Function<InstructionBundle, Void> operation,
                           boolean isByteSize,
                           boolean isRTI
    ) {
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.ticks = ticks;
        this.addressingMode = addressingMode;
        this.operation = operation;
        this.isRTI = isRTI;
        this.immediateByte = isByteSize;
    }

    public int call(MemoryResult memoryResult, IOController io) {
        operation.apply(new InstructionBundle(memoryResult, io));
        if (isRTI) {
            return 6 + (io.regs.cc.isMasked(CC_E) ? 9 : 0);
        }
        return addressingMode == INDEXED ? ticks + memoryResult.bytesConsumed : ticks;
    }

    /**
     * Waits for an interrupt to occur.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static Void sync(InstructionBundle bundle) {
        return null;
    }

    /**
     * Jumps to the specified address.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static Void unconditionalJump(InstructionBundle bundle) {
        bundle.io.regs.pc = bundle.memoryResult.value;
        return null;
    }

    /**
     * Pushes machine state onto the stack, and waits for an interrupt.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static Void callAndWaitForInterrupt(InstructionBundle bundle) {
        bundle.io.regs.cc.and(bundle.memoryResult.value.getHigh().getShort());
        bundle.io.regs.cc.or(CC_E);
        bundle.io.pushStack(Register.S, bundle.io.regs.pc);
        bundle.io.pushStack(Register.S, bundle.io.regs.u);
        bundle.io.pushStack(Register.S, bundle.io.regs.y);
        bundle.io.pushStack(Register.S, bundle.io.regs.x);
        bundle.io.pushStack(Register.S, bundle.io.regs.dp);
        bundle.io.pushStack(Register.S, bundle.io.regs.b);
        bundle.io.pushStack(Register.S, bundle.io.regs.a);
        bundle.io.pushStack(Register.S, bundle.io.regs.cc);
        return null;
    }

    /**
     * Restores register flags from the stack.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static Void returnFromInterrupt(InstructionBundle bundle) {
        bundle.io.regs.cc = bundle.io.popStack(Register.S);
        if (bundle.io.regs.cc.isMasked(CC_E)) {
            bundle.io.regs.a = bundle.io.popStack(Register.S);
            bundle.io.regs.b = bundle.io.popStack(Register.S);
            bundle.io.regs.dp = bundle.io.popStack(Register.S);
            bundle.io.regs.x = new UnsignedWord(bundle.io.popStack(Register.S), bundle.io.popStack(Register.S));
            bundle.io.regs.y = new UnsignedWord(bundle.io.popStack(Register.S), bundle.io.popStack(Register.S));
            bundle.io.regs.u = new UnsignedWord(bundle.io.popStack(Register.S), bundle.io.popStack(Register.S));
        }
        bundle.io.regs.pc = new UnsignedWord(bundle.io.popStack(Register.S), bundle.io.popStack(Register.S));
        return null;
    }

    /**
     * Restores the program counter from the stack.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static Void returnFromSubroutine(InstructionBundle bundle) {
        bundle.io.regs.pc = new UnsignedWord(bundle.io.popStack(Register.S), bundle.io.popStack(Register.S));
        return null;
    }

    /**
     * Does nothing (nop).
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static Void noOperation(InstructionBundle bundle) {
        return null;
    }

    /**
     * Jumps to the specified address, pushing the value of the PC onto the S
     * stack before jumping.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static Void jumpToSubroutine(InstructionBundle bundle) {
        bundle.io.pushStack(Register.S, bundle.io.regs.pc);
        bundle.io.regs.pc.set(bundle.memoryResult.value);
        return null;
    }

    /**
     * Saves all registers to the stack, and jumps to the memory location
     * read at the specified address.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static Void softwareInterrupt(InstructionBundle bundle) {
        bundle.io.regs.cc.or(CC_E);
        bundle.io.pushStack(Register.S, bundle.io.regs.pc);
        bundle.io.pushStack(Register.S, bundle.io.regs.u);
        bundle.io.pushStack(Register.S, bundle.io.regs.y);
        bundle.io.pushStack(Register.S, bundle.io.regs.x);
        bundle.io.pushStack(Register.S, bundle.io.regs.dp);
        bundle.io.pushStack(Register.S, bundle.io.regs.b);
        bundle.io.pushStack(Register.S, bundle.io.regs.a);
        bundle.io.pushStack(Register.S, bundle.io.regs.cc);
        bundle.io.regs.pc = bundle.io.readWord(Instruction.SWI);
        return null;
    }

    /**
     * Saves all registers to the stack, and jumps to the memory location
     * read at the specified address.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static Void softwareInterrupt2(InstructionBundle bundle) {
        bundle.io.regs.cc.or(CC_E);
        bundle.io.pushStack(Register.S, bundle.io.regs.pc);
        bundle.io.pushStack(Register.S, bundle.io.regs.u);
        bundle.io.pushStack(Register.S, bundle.io.regs.y);
        bundle.io.pushStack(Register.S, bundle.io.regs.x);
        bundle.io.pushStack(Register.S, bundle.io.regs.dp);
        bundle.io.pushStack(Register.S, bundle.io.regs.b);
        bundle.io.pushStack(Register.S, bundle.io.regs.a);
        bundle.io.pushStack(Register.S, bundle.io.regs.cc);
        bundle.io.regs.pc = bundle.io.readWord(Instruction.SWI2);
        return null;
    }

    /**
     * Saves all registers to the stack, and jumps to the memory location
     * read at the specified address.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static Void softwareInterrupt3(InstructionBundle bundle) {
        bundle.io.regs.cc.or(CC_E);
        bundle.io.pushStack(Register.S, bundle.io.regs.pc);
        bundle.io.pushStack(Register.S, bundle.io.regs.u);
        bundle.io.pushStack(Register.S, bundle.io.regs.y);
        bundle.io.pushStack(Register.S, bundle.io.regs.x);
        bundle.io.pushStack(Register.S, bundle.io.regs.dp);
        bundle.io.pushStack(Register.S, bundle.io.regs.b);
        bundle.io.pushStack(Register.S, bundle.io.regs.a);
        bundle.io.pushStack(Register.S, bundle.io.regs.cc);
        bundle.io.regs.pc.set(bundle.io.readWord(Instruction.SWI3));
        return null;
    }

    /**
     * Exchanges the contents of one register with another. The registers to exchange
     * depend on the postbyte that is read within the memoryResult passed to the
     * function in the bundle.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static Void exchangeRegister(InstructionBundle bundle) {
        UnsignedByte postByte = bundle.memoryResult.value.getHigh();
        UnsignedWord temp = new UnsignedWord();
        UnsignedByte tempByte = new UnsignedByte();
        switch (postByte.getShort()) {
            /* A:B <-> X */
            case 0x01:
            case 0x10:
                temp.set(bundle.io.regs.x);
                bundle.io.regs.x = bundle.io.regs.getD();
                bundle.io.regs.setD(temp);
                break;

            /* A:B <-> Y */
            case 0x02:
            case 0x20:
                temp.set(bundle.io.regs.y);
                bundle.io.regs.y = bundle.io.regs.getD();
                bundle.io.regs.setD(temp);
                break;

            /* A:B <-> U */
            case 0x03:
            case 0x30:
                temp.set(bundle.io.regs.u);
                bundle.io.regs.u = bundle.io.regs.getD();
                bundle.io.regs.setD(temp);
                break;

            /* A:B <-> S */
            case 0x04:
            case 0x40:
                temp.set(bundle.io.regs.s);
                bundle.io.regs.s = bundle.io.regs.getD();
                bundle.io.regs.setD(temp);
                break;

            /* A:B <-> PC */
            case 0x05:
            case 0x50:
                temp.set(bundle.io.regs.pc);
                bundle.io.regs.pc = bundle.io.regs.getD();
                bundle.io.regs.setD(temp);
                break;

            /* X <-> Y */
            case 0x12:
            case 0x21:
                temp.set(bundle.io.regs.x);
                bundle.io.regs.x = bundle.io.regs.y.copy();
                bundle.io.regs.y = temp;
                break;

            /* X <-> U */
            case 0x13:
            case 0x31:
                temp.set(bundle.io.regs.x);
                bundle.io.regs.x = bundle.io.regs.u.copy();
                bundle.io.regs.u = temp;
                break;

            /* X <-> S */
            case 0x14:
            case 0x41:
                temp.set(bundle.io.regs.x);
                bundle.io.regs.x = bundle.io.regs.s.copy();
                bundle.io.regs.s = temp;
                break;

            /* X <-> PC */
            case 0x15:
            case 0x51:
                temp.set(bundle.io.regs.x);
                bundle.io.regs.x = bundle.io.regs.pc.copy();
                bundle.io.regs.pc = temp;
                break;

            /* Y <-> U */
            case 0x23:
            case 0x32:
                temp.set(bundle.io.regs.y);
                bundle.io.regs.y = bundle.io.regs.u.copy();
                bundle.io.regs.u = temp;
                break;

            /* Y <-> S */
            case 0x24:
            case 0x42:
                temp.set(bundle.io.regs.s);
                bundle.io.regs.s = bundle.io.regs.y.copy();
                bundle.io.regs.y = temp;
                break;

            /* Y <-> PC */
            case 0x25:
            case 0x52:
                temp.set(bundle.io.regs.y);
                bundle.io.regs.y = bundle.io.regs.pc.copy();
                bundle.io.regs.pc = temp;
                break;

            /* U <-> S */
            case 0x34:
            case 0x43:
                temp.set(bundle.io.regs.u);
                bundle.io.regs.u = bundle.io.regs.s.copy();
                bundle.io.regs.s = temp;
                break;

            /* U <-> PC */
            case 0x35:
            case 0x53:
                temp.set(bundle.io.regs.u);
                bundle.io.regs.u = bundle.io.regs.pc.copy();
                bundle.io.regs.pc = temp;
                break;

            /* S <-> PC */
            case 0x45:
            case 0x54:
                temp.set(bundle.io.regs.s);
                bundle.io.regs.s = bundle.io.regs.pc.copy();
                bundle.io.regs.pc = temp;
                break;

            /* A <-> B */
            case 0x89:
            case 0x98:
                tempByte.set(bundle.io.regs.a);
                bundle.io.regs.a = bundle.io.regs.b.copy();
                bundle.io.regs.b = tempByte;
                break;

            /* A <-> CC */
            case 0x8A:
            case 0xA8:
                tempByte.set(bundle.io.regs.a);
                bundle.io.regs.a = bundle.io.regs.cc.copy();
                bundle.io.regs.cc = tempByte;
                break;

            /* A <-> DP */
            case 0x8B:
            case 0xB8:
                tempByte.set(bundle.io.regs.a);
                bundle.io.regs.a = bundle.io.regs.dp.copy();
                bundle.io.regs.dp = tempByte;
                break;

            /* B <-> CC */
            case 0x9A:
            case 0xA9:
                tempByte.set(bundle.io.regs.b);
                bundle.io.regs.b = bundle.io.regs.cc.copy();
                bundle.io.regs.cc = tempByte;
                break;

            /* B <-> DP */
            case 0x9B:
            case 0xB9:
                tempByte.set(bundle.io.regs.b);
                bundle.io.regs.b = bundle.io.regs.dp.copy();
                bundle.io.regs.dp = tempByte;
                break;

            /* CC <-> DP */
            case 0xAB:
            case 0xBA:
                tempByte.set(bundle.io.regs.cc);
                bundle.io.regs.cc = bundle.io.regs.dp.copy();
                bundle.io.regs.dp = tempByte;
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
                throw new RuntimeException("Illegal register exchange " + postByte);
        }
        return null;
    }

    /**
     * Transfers the value of one register to another. The register to transfer
     * to and from is encoded by the postByte read by memoryResult.
     *
     * @param bundle the InstructionBundle with the information to process
     */
    public static Void transferRegister(InstructionBundle bundle) {
        UnsignedByte postByte = bundle.memoryResult.value.getHigh();
        switch (postByte.getShort()) {
            /* A:B -> X */
            case 0x01:
                bundle.io.regs.x = bundle.io.regs.getD();
                break;

            /* A:B -> Y */
            case 0x02:
                bundle.io.regs.y = bundle.io.regs.getD();
                break;

            /* A:B -> U */
            case 0x03:
                bundle.io.regs.u = bundle.io.regs.getD();
                break;

            /* A:B -> S */
            case 0x04:
                bundle.io.regs.s = bundle.io.regs.getD();
                break;

            /* A:B -> PC */
            case 0x05:
                bundle.io.regs.pc = bundle.io.regs.getD();
                break;

            /* X -> A:B */
            case 0x10:
                bundle.io.regs.setD(bundle.io.regs.x);
                break;

            /* X -> Y */
            case 0x12:
                bundle.io.regs.y = bundle.io.regs.x.copy();
                break;

            /* X -> U */
            case 0x13:
                bundle.io.regs.u = bundle.io.regs.x.copy();
                break;

            /* X -> S */
            case 0x14:
                bundle.io.regs.s = bundle.io.regs.x.copy();
                break;

            /* X -> PC */
            case 0x15:
                bundle.io.regs.pc = bundle.io.regs.x.copy();
                break;

            /* Y -> A:B */
            case 0x20:
                bundle.io.regs.setD(bundle.io.regs.y.copy());
                break;

            /* Y -> X */
            case 0x21:
                bundle.io.regs.x = bundle.io.regs.y.copy();
                break;

            /* Y -> U */
            case 0x23:
                bundle.io.regs.u = bundle.io.regs.y.copy();
                break;

            /* Y -> S */
            case 0x24:
                bundle.io.regs.s = bundle.io.regs.y.copy();
                break;

            /* Y -> PC */
            case 0x25:
                bundle.io.regs.pc = bundle.io.regs.y.copy();
                break;

            /* U -> A:B */
            case 0x30:
                bundle.io.regs.setD(bundle.io.regs.u.copy());
                break;

            /* U -> X */
            case 0x31:
                bundle.io.regs.x = bundle.io.regs.u.copy();
                break;

            /* U -> Y */
            case 0x32:
                bundle.io.regs.y = bundle.io.regs.u.copy();
                break;

            /* U -> S */
            case 0x34:
                bundle.io.regs.s = bundle.io.regs.u.copy();
                break;

            /* U -> PC */
            case 0x35:
                bundle.io.regs.pc = bundle.io.regs.u.copy();
                break;

            /* S -> A:B */
            case 0x40:
                bundle.io.regs.setD(bundle.io.regs.s.copy());
                break;

            /* S -> X */
            case 0x41:
                bundle.io.regs.x = bundle.io.regs.s.copy();
                break;

            /* S -> Y */
            case 0x42:
                bundle.io.regs.y = bundle.io.regs.s.copy();
                break;

            /* S -> U */
            case 0x43:
                bundle.io.regs.u = bundle.io.regs.s.copy();
                break;

            /* S -> PC */
            case 0x45:
                bundle.io.regs.pc = bundle.io.regs.s.copy();
                break;

            /* PC -> A:B */
            case 0x50:
                bundle.io.regs.setD(bundle.io.regs.pc.copy());
                break;

            /* PC -> X */
            case 0x51:
                bundle.io.regs.x = bundle.io.regs.pc.copy();
                break;

            /* PC -> Y */
            case 0x52:
                bundle.io.regs.y = bundle.io.regs.pc.copy();
                break;

            /* PC -> U */
            case 0x53:
                bundle.io.regs.u = bundle.io.regs.pc.copy();
                break;

            /* PC -> S */
            case 0x54:
                bundle.io.regs.s = bundle.io.regs.pc.copy();
                break;

            /* A -> B */
            case 0x89:
                bundle.io.regs.b = bundle.io.regs.a.copy();
                break;

            /* A -> CC */
            case 0x8A:
                bundle.io.regs.cc = bundle.io.regs.a.copy();
                break;

            /* A -> DP */
            case 0x8B:
                bundle.io.regs.dp = bundle.io.regs.a.copy();
                break;

            /* B -> A */
            case 0x98:
                bundle.io.regs.a = bundle.io.regs.b.copy();
                break;

            /* B -> CC */
            case 0x9A:
                bundle.io.regs.cc = bundle.io.regs.b.copy();
                break;

            /* B -> DP */
            case 0x9B:
                bundle.io.regs.dp = bundle.io.regs.b.copy();
                break;

            /* CC -> A */
            case 0xA8:
                bundle.io.regs.a = bundle.io.regs.cc.copy();
                break;

            /* CC -> B */
            case 0xA9:
                bundle.io.regs.b = bundle.io.regs.cc.copy();
                break;

            /* CC -> DP */
            case 0xAB:
                bundle.io.regs.dp = bundle.io.regs.cc.copy();
                break;

            /* DP -> A */
            case 0xB8:
                bundle.io.regs.a = bundle.io.regs.dp.copy();
                break;

            /* DP -> B */
            case 0xB9:
                bundle.io.regs.b = bundle.io.regs.dp.copy();
                break;

            /* DP -> CC */
            case 0xBA:
                bundle.io.regs.cc = bundle.io.regs.dp.copy();
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
                throw new RuntimeException("Illegal register transfer " + postByte);
        }
        return null;
    }
}
