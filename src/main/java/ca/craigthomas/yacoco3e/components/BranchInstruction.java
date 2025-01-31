/*
 * Copyright (C) 2022-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

import java.util.function.Function;

import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;

/**
 * The Opcode class stores information related to a specific machine operation.
 * The opcode value itself, the mnemonic representation, the addressing mode,
 * the size of any immediate values read, and whether the size of input is
 * a word length.
 */
public class BranchInstruction extends Instruction
{
    protected Function<IOController, Boolean> operation;

    public BranchInstruction(int opcode,
                             int ticks,
                             String mnemonic,
                             Function<IOController, Boolean> operation
    ) {
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.ticks = ticks;
        this.operation = operation;
        this.addressingMode = AddressingMode.IMMEDIATE;
        this.isByteSized = true;
        this.isValidInstruction = true;
        this.addressRead = new UnsignedWord();
    }

    public int call(IOController io) {
        if (operation.apply(io).equals(true)) {
            io.regs.pc.add(byteRead.isNegative() ? byteRead.getSignedShort() : byteRead.getShort());
        }

        return ticks;
    }

    /**
     * Branches to the specified address, pushing the value of the PC onto the S
     * stack before branching.
     */
    public static Boolean branchToSubroutine(IOController io) {
        io.pushStack(Register.S, io.regs.pc);
        return true;
    }

    /**
     * Branch always will always return true when called.
     */
    public static Boolean branchAlways(IOController io) {
        return true;
    }

    /**
     * Branch never will always return false when called.
     */
    public static Boolean branchNever(IOController io) {
        return false;
    }

    /**
     * Branch on high will return true if carry is not set, and zero is not set.
     */
    public static Boolean branchOnHigh(IOController io) {
        return !io.regs.cc.isMasked(CC_C) && !io.regs.cc.isMasked(CC_Z);
    }

    /**
     * Branch on lower will return true if carry is set or zero is set.
     */
    public static Boolean branchOnLower(IOController io) {
        return io.regs.cc.isMasked(CC_C) || io.regs.cc.isMasked(CC_Z);
    }

    /**
     * Branch on carry clear will return true if carry is not set.
     */
    public static Boolean branchOnCarryClear(IOController io) {
        return !io.regs.cc.isMasked(CC_C);
    }

    /**
     * Branch on carry set will return true if carry is set.
     */
    public static Boolean branchOnCarrySet(IOController io) {
        return io.regs.cc.isMasked(CC_C);
    }

    /**
     * Branch on not equal will return true if the zero flag is set.
     */
    public static Boolean branchOnNotEqual(IOController io) {
        return !io.regs.cc.isMasked(CC_Z);
    }

    /**
     * Branch on equal will return true if the zero flag is set.
     */
    public static Boolean branchOnEqual(IOController io) {
        return io.regs.cc.isMasked(CC_Z);
    }

    /**
     * Branch on overflow clear will return true if the overflow flag is clear.
     */
    public static Boolean branchOnOverflowClear(IOController io) {
        return !io.regs.cc.isMasked(CC_V);
    }

    /**
     * Branch on overflow set will return true if the overflow flag is set.
     */
    public static Boolean branchOnOverflowSet(IOController io) {
        return io.regs.cc.isMasked(CC_V);
    }

    /**
     * Branch on plus will return true if the negative flag is clear.
     */
    public static Boolean branchOnPlus(IOController io) {
        return !io.regs.cc.isMasked(CC_N);
    }

    /**
     * Branch on minus will return true if the negative flag is set.
     */
    public static Boolean branchOnMinus(IOController io) {
        return io.regs.cc.isMasked(CC_N);
    }

    /**
     * Branch on greater than equal to zero will return true if the negative and overflow
     * flags are both set or both clear.
     */
    public static Boolean branchOnGreaterThanEqualZero(IOController io) {
        return io.regs.cc.isMasked(CC_N) == io.regs.cc.isMasked(CC_V);
    }

    /**
     * Branch on less than zero will return true if the negative and overflow are not equal.
     */
    public static Boolean branchOnLessThanZero(IOController io) {
        return io.regs.cc.isMasked(CC_N) != io.regs.cc.isMasked(CC_V);
    }

    /**
     * Branch on greater than zero will return true if the zero flag is clear,
     * and if negative and overflow are both set or both clear.
     */
    public static Boolean branchOnGreaterThanZero(IOController io) {
        return !io.regs.cc.isMasked(CC_Z) && (io.regs.cc.isMasked(CC_N) == io.regs.cc.isMasked(CC_V));
    }

    /**
     * Branch on less than equal to zero will return true if the zero flag is set,
     * or if overflow and negative are not equal.
     */
    public static Boolean branchOnLessThanEqualZero(IOController io) {
        return io.regs.cc.isMasked(CC_Z) || (io.regs.cc.isMasked(CC_N) != io.regs.cc.isMasked(CC_V));
    }

}
