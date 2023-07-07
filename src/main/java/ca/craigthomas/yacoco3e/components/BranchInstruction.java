/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

import java.util.function.Function;

import static ca.craigthomas.yacoco3e.datatypes.AddressingMode.IMMEDIATE;
import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;

/**
 * The Opcode class stores information related to a specific machine operation.
 * The opcode value itself, the mnemonic representation, the addressing mode,
 * the size of any immediate values read, and whether the size of input is
 * a word length.
 */
public class BranchInstruction extends Instruction
{
    protected Function<InstructionBundle, Boolean> operation;

    public BranchInstruction(int opcode,
                             int ticks,
                             String mnemonic,
                             Function<InstructionBundle, Boolean> operation
    ) {
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.ticks = ticks;
        this.operation = operation;
        this.addressingMode = IMMEDIATE;
        this.immediateByte = true;
    }

    public int call(MemoryResult memoryResult, IOController io) {
        if (operation.apply(new InstructionBundle(memoryResult, io)).equals(true)) {
            UnsignedByte offset = memoryResult.value.getHigh();
            io.regs.pc.add(offset.isNegative() ? offset.getSignedShort() : offset.getShort());
        }
        return ticks;
    }

    /**
     * Branches to the specified address, pushing the value of the PC onto the S
     * stack before branching.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static Boolean branchToSubroutine(InstructionBundle bundle) {
        bundle.io.pushStack(Register.S, bundle.io.regs.pc);
        return true;
    }

    /**
     * Branch always will always return true when called.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchAlways(InstructionBundle bundle) {
        return true;
    }

    /**
     * Branch never will always return false when called.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchNever(InstructionBundle bundle) {
        return false;
    }

    /**
     * Branch on high will return true if carry is not set, and zero is not set.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnHigh(InstructionBundle bundle) {
        return !bundle.io.regs.cc.isMasked(CC_C) && !bundle.io.regs.cc.isMasked(CC_Z);
    }

    /**
     * Branch on lower will return true if carry is set or zero is set.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnLower(InstructionBundle bundle) {
        return bundle.io.regs.cc.isMasked(CC_C) || bundle.io.regs.cc.isMasked(CC_Z);
    }

    /**
     * Branch on carry clear will return true if carry is not set.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnCarryClear(InstructionBundle bundle) {
        return !bundle.io.regs.cc.isMasked(CC_C);
    }

    /**
     * Branch on carry set will return true if carry is set.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnCarrySet(InstructionBundle bundle) {
        return bundle.io.regs.cc.isMasked(CC_C);
    }

    /**
     * Branch on not equal will return true if the zero flag is set.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnNotEqual(InstructionBundle bundle) {
        return !bundle.io.regs.cc.isMasked(CC_Z);
    }

    /**
     * Branch on equal will return true if the zero flag is set.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnEqual(InstructionBundle bundle) {
        return bundle.io.regs.cc.isMasked(CC_Z);
    }

    /**
     * Branch on overflow clear will return true if the overflow flag is clear.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnOverflowClear(InstructionBundle bundle) {
        return !bundle.io.regs.cc.isMasked(CC_V);
    }

    /**
     * Branch on overflow set will return true if the overflow flag is set.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnOverflowSet(InstructionBundle bundle) {
        return bundle.io.regs.cc.isMasked(CC_V);
    }

    /**
     * Branch on plus will return true if the negative flag is clear.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnPlus(InstructionBundle bundle) {
        return !bundle.io.regs.cc.isMasked(CC_N);
    }

    /**
     * Branch on minus will return true if the negative flag is set.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnMinus(InstructionBundle bundle) {
        return bundle.io.regs.cc.isMasked(CC_N);
    }

    /**
     * Branch on greater than equal to zero will return true if the negative and overflow
     * flags are both set or both clear.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnGreaterThanEqualZero(InstructionBundle bundle) {
        return bundle.io.regs.cc.isMasked(CC_N) == bundle.io.regs.cc.isMasked(CC_V);
    }

    /**
     * Branch on less than zero will return true if the negative and overflow are not equal.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnLessThanZero(InstructionBundle bundle) {
        return bundle.io.regs.cc.isMasked(CC_N) != bundle.io.regs.cc.isMasked(CC_V);
    }

    /**
     * Branch on greater than zero will return true if the zero flag is clear,
     * and if negative and overflow are both set or both clear.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnGreaterThanZero(InstructionBundle bundle) {
        return !bundle.io.regs.cc.isMasked(CC_Z) && (bundle.io.regs.cc.isMasked(CC_N) == bundle.io.regs.cc.isMasked(CC_V));
    }

    /**
     * Branch on less than equal to zero will return true if the zero flag is set,
     * or if overflow and negative are not equal.
     *
     * @param bundle the InstructionBundle with the information to process.
     */
    public static Boolean branchOnLessThanEqualZero(InstructionBundle bundle) {
        return bundle.io.regs.cc.isMasked(CC_Z) || (bundle.io.regs.cc.isMasked(CC_N) != bundle.io.regs.cc.isMasked(CC_V));
    }

}
