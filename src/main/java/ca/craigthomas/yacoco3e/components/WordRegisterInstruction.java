/*
 * Copyright (C) 2022-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

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
    @FunctionalInterface
    public interface WordRegisterInterface
    {
        void apply(IOController io, UnsignedWord register, UnsignedWord memoryWord, UnsignedWord address, boolean isD);
    }

    protected WordRegisterInterface operation;
    protected Register register;

    public WordRegisterInstruction(int opcode,
                                   int ticks,
                                   String mnemonic,
                                   AddressingMode addressingMode,
                                   WordRegisterInterface operation,
                                   Register register
    ) {
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.addressingMode = addressingMode;
        this.isByteSized = false;
        this.isValidInstruction = true;
        this.ticks = ticks;
        this.operation = operation;
        this.register = register;
        this.addressRead = new UnsignedWord();
    }

    public int call(IOController io) throws MalformedInstructionException {
        switch(register) {
            case D:
                operation.apply(io, io.regs.getD(), wordRead, addressRead, true);
                break;

            case S:
                operation.apply(io, io.regs.s, wordRead, addressRead, false);
                break;

            case U:
                operation.apply(io, io.regs.u, wordRead, addressRead, false);
                break;

            case X:
                operation.apply(io, io.regs.x, wordRead, addressRead, false);
                break;

            case Y:
                operation.apply(io, io.regs.y, wordRead, addressRead, false);
                break;

            default:
                throw new MalformedInstructionException(
                        "Register " + register + " unsupported for WordRegisterInstruction"
                );
        }
        return (addressingMode == INDEXED) ? ticks + numBytesRead : ticks;
    }

    /**
     * Sets the register to the specified value. Since the D register is a special register,
     * we have to flag whether we are setting the value on it using isD.
     */
    public static void setRegister(IOController io, UnsignedWord register, UnsignedWord value, boolean isD) {
        if (isD) {
            io.regs.setD(value);
        } else {
            register.set(value);
        }
    }

    /**
     * Loads the effective address into the specified register.
     */
    public static void loadEffectiveAddressXY(IOController io, UnsignedWord register, UnsignedWord memoryWord, UnsignedWord address, boolean isD) {
        io.regs.cc.and(~(CC_Z));
        io.regs.cc.or(address.isZero() ? CC_Z : 0);
        setRegister(io, register, address, isD);
    }

    /**
     * Loads the effective address into the specified register.
     */
    public static void loadEffectiveAddressUS(IOController io, UnsignedWord register, UnsignedWord memoryWord, UnsignedWord address, boolean isD) {
        setRegister(io, register, address, isD);
    }

    /**
     * Adds B to X and returns the sum.
     */
    public static void addBtoX(IOController io, UnsignedWord register, UnsignedWord memoryWord, UnsignedWord address, boolean isD) {
        setRegister(io, register, new UnsignedWord(io.regs.x.getInt() + io.regs.b.getShort()), isD);
    }

    /**
     * Performs an unsigned integer multiplication between A and B.
     */
    public static void multiply(IOController io, UnsignedWord register, UnsignedWord memoryWord, UnsignedWord address, boolean isD) {
        UnsignedWord result = new UnsignedWord(io.regs.a.getShort() * io.regs.b.getShort());
        io.regs.cc.and(~(CC_Z | CC_C));
        io.regs.cc.or(result.isZero() ? CC_Z : 0);
        io.regs.cc.or(result.isMasked(0x80) ? CC_C : 0);
        setRegister(io, register, result, isD);
    }

    /**
     * Subtracts the word value from the specified word.
     */
    public static void subtractWord(IOController io, UnsignedWord register, UnsignedWord memoryWord, UnsignedWord address, boolean isD) {
        boolean word1WasNegative = register.isNegative();
        boolean word2WasNegative = memoryWord.isNegative();
        io.regs.cc.and(~(CC_N | CC_V | CC_Z | CC_C));
        io.regs.cc.or(register.getInt() < memoryWord.getInt() ? CC_C : 0);
        UnsignedWord result = new UnsignedWord(register.getInt() + memoryWord.twosCompliment().getInt());
        io.regs.cc.or(result.isZero() ? CC_Z : 0);
        io.regs.cc.or(result.isNegative() ? CC_N : 0);
        boolean overflow = (!word1WasNegative && word2WasNegative && result.isNegative()) || (word1WasNegative && !word2WasNegative && !result.isNegative());
        io.regs.cc.or(overflow ? CC_V : 0);
        setRegister(io, register, result, isD);
    }

    /**
     * Adds the specified word value to the word.
     */
    public static void addWord(IOController io, UnsignedWord register, UnsignedWord memoryWord, UnsignedWord address, boolean isD) {
        io.regs.cc.and(~(CC_N | CC_V | CC_Z | CC_C));
        io.regs.cc.or(((register.getInt() + memoryWord.getInt()) & 0x10000) > 0 ? CC_C : 0);
        int signedResult = register.getSignedInt() + memoryWord.getSignedInt();
        UnsignedWord result = new UnsignedWord(register.getInt() + memoryWord.getInt());
        io.regs.cc.or(result.isZero() ? CC_Z : 0);
        io.regs.cc.or(result.isNegative() ? CC_N : 0);
        io.regs.cc.or(signedResult > 32767 || signedResult < -32767 ? CC_V : 0);
        setRegister(io, register, result, isD);
    }

    /**
     * Compares the two words and sets the appropriate register sets.
     */
    public static void compareWord(IOController io, UnsignedWord register, UnsignedWord memoryWord, UnsignedWord address, boolean isD) {
        boolean word1WasNegative = register.isNegative();
        boolean word2WasNegative = memoryWord.isNegative();
        io.regs.cc.and(~(CC_N | CC_Z | CC_V | CC_C));
        io.regs.cc.or(register.getInt() < memoryWord.getInt() ? CC_C : 0);
        UnsignedWord result = new UnsignedWord(register.getInt() + memoryWord.twosCompliment().getInt());
        io.regs.cc.or(result.isZero() ? CC_Z : 0);
        io.regs.cc.or(result.isNegative() ? CC_N : 0);
        boolean overflow = (!word1WasNegative && word2WasNegative && result.isNegative()) || (word1WasNegative && !word2WasNegative && !result.isNegative());
        io.regs.cc.or(overflow ? CC_V : 0);
    }

    /**
     * Loads the word into the specified register.
     */
    public static void loadWordRegister(IOController io, UnsignedWord register, UnsignedWord memoryWord, UnsignedWord address, boolean isD) {
        io.regs.cc.and(~(CC_V | CC_N | CC_Z));
        io.regs.cc.or(memoryWord.isZero() ? CC_Z : 0);
        io.regs.cc.or(memoryWord.isNegative() ? CC_N : 0);
        setRegister(io, register, memoryWord, isD);
    }

    /**
     * Stores the register in the memory location.
     */
    public static void storeWordRegister(IOController io, UnsignedWord register, UnsignedWord memoryWord, UnsignedWord address, boolean isD) {
        io.regs.cc.and(~(CC_V | CC_N | CC_Z));
        io.regs.cc.or(register.isZero() ? CC_Z : 0);
        io.regs.cc.or(register.isNegative() ? CC_N : 0);
        io.writeWord(address, register);
    }
}
