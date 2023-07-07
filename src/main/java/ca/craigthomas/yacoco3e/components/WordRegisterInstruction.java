/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.MemoryResult;
import ca.craigthomas.yacoco3e.datatypes.Register;
import ca.craigthomas.yacoco3e.datatypes.UnsignedWord;

import java.util.function.Function;

import static ca.craigthomas.yacoco3e.datatypes.AddressingMode.IMMEDIATE;
import static ca.craigthomas.yacoco3e.datatypes.AddressingMode.INDEXED;
import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;
import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.CC_N;

/**
 * The Opcode class stores information related to a specific machine operation.
 * The opcode value itself, the mnemonic representation, the addressing mode,
 * the size of any immediate values read, and whether the size of input is
 * a word length.
 */
public class WordRegisterInstruction extends Instruction
{
    protected Function<InstructionBundle, UnsignedWord> operation;
    protected Register register;

    public WordRegisterInstruction(int opcode,
                                   int ticks,
                                   String mnemonic,
                                   int addressingMode,
                                   Function<InstructionBundle, UnsignedWord> operation,
                                   Register register
    ) {
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.addressingMode = addressingMode;
        this.immediateByte = false;
        this.ticks = ticks;
        this.operation = operation;
        this.register = register;
    }

    public int call(MemoryResult memoryResult, IOController io) throws MalformedInstructionException {
        UnsignedWord word2 = (addressingMode == IMMEDIATE) ? memoryResult.value : io.readWord(memoryResult.value);
        switch(register) {
            case D:
                io.regs.setD(operation.apply(new InstructionBundle(memoryResult, io, io.regs.getD(), word2)));
                break;

            case S:
                io.regs.s.set(operation.apply(new InstructionBundle(memoryResult, io, io.regs.s, word2)));
                break;

            case U:
                io.regs.u.set(operation.apply(new InstructionBundle(memoryResult, io, io.regs.u, word2)));
                break;

            case X:
                io.regs.x.set(operation.apply(new InstructionBundle(memoryResult, io, io.regs.x, word2)));
                break;

            case Y:
                io.regs.y.set(operation.apply(new InstructionBundle(memoryResult, io, io.regs.y, word2)));
                break;

            default:
                throw new MalformedInstructionException(
                        "Register " + register + " unsupported for WordRegisterInstruction"
                );
        }
        return (addressingMode == INDEXED) ? ticks + memoryResult.bytesConsumed : ticks;
    }

    /**
     * Loads the effective address into the specified register.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedWord loadEffectiveAddressXY(InstructionBundle bundle) {
        bundle.io.regs.cc.and(~(CC_Z));
        bundle.io.regs.cc.or(bundle.memoryResult.value.isZero() ? CC_Z : 0);
        return bundle.memoryResult.value;
    }

    /**
     * Loads the effective address into the specified register.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedWord loadEffectiveAddressUS(InstructionBundle bundle) {
        return bundle.memoryResult.value;
    }

    /**
     * Adds B to X and returns the sum.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedWord addBtoX(InstructionBundle bundle) {
        return new UnsignedWord(bundle.io.regs.x.getInt() + bundle.io.regs.b.getShort());
    }

    /**
     * Performs an unsigned integer multiplication between A and B.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedWord multiply(InstructionBundle bundle) {
        UnsignedWord result = new UnsignedWord(bundle.io.regs.a.getShort() * bundle.io.regs.b.getShort());
        bundle.io.regs.cc.and(~(CC_Z | CC_C));
        bundle.io.regs.cc.or(result.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(result.isMasked(0x80) ? CC_C : 0);
        return result;
    }

    /**
     * Subtracts the word value from the specified word.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedWord subtractWord(InstructionBundle bundle) {
        boolean word1WasNegative = bundle.word1.isNegative();
        boolean word2WasNegative = bundle.word2.isNegative();
        bundle.io.regs.cc.and(~(CC_N | CC_V | CC_Z | CC_C));
        bundle.io.regs.cc.or(bundle.word1.getInt() < bundle.word2.getInt() ? CC_C : 0);
        bundle.word1 = new UnsignedWord(bundle.word1.getInt() + bundle.word2.twosCompliment().getInt());
        bundle.io.regs.cc.or(bundle.word1.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(bundle.word1.isNegative() ? CC_N : 0);
        boolean overflow = (!word1WasNegative && word2WasNegative && bundle.word1.isNegative()) || (word1WasNegative && !word2WasNegative && !bundle.word1.isNegative());
        bundle.io.regs.cc.or(overflow ? CC_V : 0);
        return bundle.word1;
    }

    /**
     * Adds the specified word value to the word.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedWord addWord(InstructionBundle bundle) {
        bundle.io.regs.cc.and(~(CC_N | CC_V | CC_Z | CC_C));
        bundle.io.regs.cc.or(((bundle.word1.getInt() + bundle.word2.getInt()) & 0x10000) > 0 ? CC_C : 0);
        int signedResult = bundle.word1.getSignedInt() + bundle.word2.getSignedInt();
        bundle.word1 = new UnsignedWord(bundle.word1.getInt() + bundle.word2.getInt());
        bundle.io.regs.cc.or(bundle.word1.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(bundle.word1.isNegative() ? CC_N : 0);
        bundle.io.regs.cc.or(signedResult > 32767 || signedResult < -32767 ? CC_V : 0);
        return bundle.word1;
    }

    /**
     * Compares the two words and sets the appropriate register sets.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedWord compareWord(InstructionBundle bundle) {
        boolean word1WasNegative = bundle.word1.isNegative();
        boolean word2WasNegative = bundle.word2.isNegative();
        bundle.io.regs.cc.and(~(CC_N | CC_Z | CC_V | CC_C));
        bundle.io.regs.cc.or(bundle.word1.getInt() < bundle.word2.getInt() ? CC_C : 0);
        UnsignedWord result = new UnsignedWord(bundle.word1.getInt() + bundle.word2.twosCompliment().getInt());
        bundle.io.regs.cc.or(result.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(result.isNegative() ? CC_N : 0);
        boolean overflow = (!word1WasNegative && word2WasNegative && result.isNegative()) || (word1WasNegative && !word2WasNegative && !result.isNegative());
        bundle.io.regs.cc.or(overflow ? CC_V : 0);
        return bundle.word1;
    }

    /**
     * Loads the word into the specified register.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedWord loadWordRegister(InstructionBundle bundle) {
        bundle.io.regs.cc.and(~(CC_V | CC_N | CC_Z));
        bundle.io.regs.cc.or(bundle.word2.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(bundle.word2.isNegative() ? CC_N : 0);
        return bundle.word2;
    }

    /**
     * Stores the register in the memory location.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedWord storeWordRegister(InstructionBundle bundle) {
        bundle.io.writeWord(bundle.memoryResult.value, bundle.word1);
        bundle.io.regs.cc.and(~(CC_V | CC_N | CC_Z));
        bundle.io.regs.cc.or(bundle.word1.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(bundle.word1.isNegative() ? CC_N : 0);
        return bundle.word1;
    }
}
