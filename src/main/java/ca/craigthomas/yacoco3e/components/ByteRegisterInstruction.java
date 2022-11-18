/*
 * Copyright (C) 2023 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

import java.util.function.Function;

import static ca.craigthomas.yacoco3e.datatypes.AddressingMode.*;
import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;

/**
 * The Opcode class stores information related to a specific machine operation.
 * The opcode value itself, the mnemonic representation, the addressing mode,
 * the size of any immediate values read, and whether the size of input is
 * a word length.
 */
public class ByteRegisterInstruction extends Instruction
{
    protected Function<InstructionBundle, UnsignedByte> operation;
    protected Register register;

    public ByteRegisterInstruction(int opcode,
                                   int ticks,
                                   String mnemonic,
                                   int addressingMode,
                                   Function<InstructionBundle, UnsignedByte> operation,
                                   Register register
    ) {
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.addressingMode = addressingMode;
        this.ticks = ticks;
        this.operation = operation;
        this.register = register;
        this.immediateByte = true;
    }

    public int call(MemoryResult memoryResult, IOController io) throws MalformedInstructionException {
        UnsignedByte byte2 = (addressingMode == IMMEDIATE) ? memoryResult.value.getHigh() : io.readByte(memoryResult.value);
        UnsignedWord word1 = memoryResult.value;
        switch(register) {
            case A:
                io.regs.a.set(operation.apply(new InstructionBundle(memoryResult, io, io.regs.a, byte2, word1)));
                break;

            case B:
                io.regs.b.set(operation.apply(new InstructionBundle(memoryResult, io, io.regs.b, byte2, word1)));
                break;

            case CC:
                io.regs.cc.set(operation.apply(new InstructionBundle(memoryResult, io, io.regs.cc, byte2, word1)));
                break;

            default:
                throw new MalformedInstructionException(
                        "Register " + register + " unsupported for ByteRegisterInstruction"
                );
        }
        return (addressingMode == INDEXED) ? ticks + memoryResult.bytesConsumed : ticks;
    }

    /**
     * Loads the specified register with the value. The byte2 values is from the
     * memory location to be loaded into the register.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte loadByteRegister(InstructionBundle bundle) {
        bundle.io.regs.cc.and(~(CC_N | CC_V | CC_Z));
        bundle.io.regs.cc.or(bundle.byte2.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(bundle.byte2.isNegative() ? CC_N : 0);
        return bundle.byte2;
    }

    /**
     * Stores the byte register in the memory location.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte storeByteRegister(InstructionBundle bundle) {
        bundle.io.writeByte(bundle.memoryResult.value, bundle.byte1);
        bundle.io.regs.cc.and(~(CC_V | CC_N | CC_Z));
        bundle.io.regs.cc.or(bundle.byte1.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(bundle.byte1.isNegative() ? CC_N : 0);
        return bundle.byte1;
    }

    /**
     * Performs a logical OR of the byte register and the value.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte logicalOr(InstructionBundle bundle) {
        bundle.io.regs.cc.and(~(CC_N | CC_V | CC_Z));
        bundle.byte1.or(bundle.byte2.getShort());
        bundle.io.regs.cc.or(bundle.byte1.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(bundle.byte1.isNegative() ? CC_N : 0);
        return bundle.byte1;
    }

    /**
     * Performs a logical OR on the memory result and the condition code register.
     * Will return the condition code register.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte logicalOrCC(InstructionBundle bundle) {
        bundle.io.regs.cc.or(bundle.memoryResult.value.getHigh().getShort());
        return bundle.io.regs.cc;
    }

    /**
     * Performs a logical AND of the byte register and the value. The byte1 value is from
     * the register, and the byte2 value is from the memory location.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte logicalAnd(InstructionBundle bundle) {
        bundle.io.regs.cc.and(~(CC_N | CC_V | CC_Z));
        bundle.byte1.and(bundle.byte2.getShort());
        bundle.io.regs.cc.or(bundle.byte1.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(bundle.byte1.isNegative() ? CC_N : 0);
        return bundle.byte1;
    }

    /**
     * Performs a logical AND on the memory result and the condition code register.
     * Will return the condition code regsiter.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte logicalAndCC(InstructionBundle bundle) {
        bundle.io.regs.cc.and(bundle.memoryResult.value.getHigh().getShort());
        return bundle.io.regs.cc;
    }

    /**
     * Adds the specified value to the specified register.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte addByte(InstructionBundle bundle) {
        bundle.io.regs.cc.and(~(CC_N | CC_V | CC_Z | CC_C | CC_H));
        int signedResult = bundle.byte1.getSignedShort() + bundle.byte2.getSignedShort();
        UnsignedByte result = new UnsignedByte(bundle.byte1.getShort() + bundle.byte2.getShort());
        bundle.io.regs.cc.or(((bundle.byte1.getShort() ^ bundle.byte2.getShort() ^ result.getShort()) & 0x10) > 0 ? CC_H : 0);
        bundle.io.regs.cc.or(result.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(result.isNegative() ? CC_N : 0);
        bundle.io.regs.cc.or(signedResult > 127 || signedResult < -127 ? CC_V : 0);
        bundle.io.regs.cc.or(((bundle.byte1.getShort() + bundle.byte2.getShort()) & 0x100) > 0 ? CC_C : 0);
        return result;
    }

    /**
     * Performs an addition of the specified register and the value together,
     * plus the value of the carry bit (0 or 1). Stores the result in the
     * specified register.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte addWithCarry(InstructionBundle bundle) {
        UnsignedByte carry = new UnsignedByte((bundle.io.regs.cc.isMasked(CC_C)) ? 1 : 0);
        bundle.io.regs.cc.and(~(CC_N | CC_V | CC_Z | CC_C | CC_H));

        // Check for overflow condition
        int signedResult = bundle.byte1.getSignedShort() + carry.getSignedShort();
        int overflow = signedResult > 127 || signedResult < -127 ? CC_V : 0;

        // Check for carry condition if we add the carry value
        if (((bundle.byte1.getShort() + carry.getShort()) & 0x100) > 0) {
            bundle.io.regs.cc.or(CC_C);
        }

        // Check for half carry condition on the carry bit addition
        UnsignedByte test = new UnsignedByte((bundle.byte1.getShort() & 0xF) + (carry.getShort() & 0xF));
        if (test.isMasked(0x10)) {
            bundle.io.regs.cc.or(CC_H);
        }

        // Add the carry bit
        bundle.byte1 = new UnsignedByte(bundle.byte1.getShort() + carry.getShort());

        // Check for overflow condition of the actual byte
        signedResult = bundle.byte1.getSignedShort() + bundle.byte2.getSignedShort();
        overflow |= signedResult > 127 || signedResult < -127 ? CC_V : 0;

        // Check for carry condition if we add the actual byte now
        if (((bundle.byte1.getShort() + bundle.byte2.getShort()) & 0x100) > 0) {
            bundle.io.regs.cc.or(CC_C);
        }

        // Check for half carry condition on the second byte addition
        test = new UnsignedByte((bundle.byte1.getShort() & 0xF) + (bundle.byte2.getShort() & 0xF));
        if (test.isMasked(0x10)) {
            bundle.io.regs.cc.or(CC_H);
        }

        // Add the second byte
        bundle.byte1 = new UnsignedByte(bundle.byte1.getShort() + bundle.byte2.getShort());
        bundle.io.regs.cc.or(bundle.byte1.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(bundle.byte1.isNegative() ? CC_N : 0);
        bundle.io.regs.cc.or(overflow);
        return bundle.byte1;
    }

    /**
     * Logically AND the register byte with a memory byte and tests the byte for zero
     * condition or negative condition. Register contents are left unchanged.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the original byte value
     */
    public static UnsignedByte bitTest(InstructionBundle bundle) {
        UnsignedByte value = new UnsignedByte(bundle.byte1.getShort());
        value.and(bundle.byte2.getShort());
        bundle.io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        bundle.io.regs.cc.or(value.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(value.isNegative() ? CC_N : 0);
        return bundle.byte1;
    }

    /**
     * Compares the two bytes and sets the appropriate register sets.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte compareByte(InstructionBundle bundle) {
        bundle.io.regs.cc.and(~(CC_N | CC_Z | CC_V | CC_C));
        boolean byte1WasNegative = bundle.byte1.isNegative();
        boolean byte2WasNegative = bundle.byte2.isNegative();
        UnsignedByte result = new UnsignedByte(bundle.byte1.getShort() + bundle.byte2.twosCompliment().getShort());
        bundle.io.regs.cc.or(bundle.byte1.getShort() < bundle.byte2.getShort() ? CC_C : 0);
        bundle.io.regs.cc.or(result.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(result.isNegative() ? CC_N : 0);
        boolean overflow = (!byte1WasNegative && byte2WasNegative && result.isNegative()) || (byte1WasNegative && !byte2WasNegative && !result.isNegative());
        bundle.io.regs.cc.or(overflow ? CC_V : 0);
        return bundle.byte1;
    }

    /**
     * Performs a correction to the A register to transform the value into
     * a proper BCD form.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte decimalAdditionAdjust(InstructionBundle bundle) {
        int fullByte = bundle.byte1.getShort();
        int mostSignificantNibble = fullByte & 0xF0;
        int leastSignificantNibble = fullByte & 0x0F;
        int adjustment = 0;
        boolean carryPreviouslySet = bundle.io.regs.cc.isMasked(CC_C);

        if (carryPreviouslySet || mostSignificantNibble > 0x90 || (mostSignificantNibble > 0x80 && leastSignificantNibble > 0x09)) {
            adjustment |= 0x60;
        }

        if (bundle.io.regs.cc.isMasked(CC_H) || leastSignificantNibble > 0x09) {
            adjustment |= 0x06;
        }

        UnsignedByte result = new UnsignedByte(adjustment);
        bundle.io.regs.cc.and(~(CC_C | CC_N | CC_Z));
        if (((bundle.io.regs.a.getShort() + result.getShort()) & 0x100) > 0) {
            bundle.io.regs.cc.or(CC_C);
        }
        bundle.io.regs.a = new UnsignedByte(bundle.io.regs.a.getShort() + result.getShort());
        bundle.io.regs.cc.or(bundle.io.regs.a.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(carryPreviouslySet ? CC_C : 0);
        bundle.io.regs.cc.or(result.isNegative() ? CC_N : 0);
        return result;
    }


    /**
     * Subtracts the byte value from the specified register.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte subtractByte(InstructionBundle bundle) {
        bundle.io.regs.cc.and(~(CC_N | CC_V | CC_Z | CC_C));
        boolean byte1WasNegative = bundle.byte1.isNegative();
        boolean byte2WasNegative = bundle.byte2.isNegative();
        bundle.io.regs.cc.or(bundle.byte1.getShort() < bundle.byte2.getShort() ? CC_C : 0);
        bundle.byte1 = new UnsignedByte(bundle.byte1.getShort() + bundle.byte2.twosCompliment().getShort());
        bundle.io.regs.cc.or(bundle.byte1.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(bundle.byte1.isNegative() ? CC_N : 0);
        boolean overflow = (!byte1WasNegative && byte2WasNegative && bundle.byte1.isNegative()) || (byte1WasNegative && !byte2WasNegative && !bundle.byte1.isNegative());
        bundle.io.regs.cc.or(overflow ? CC_V : 0);
        return bundle.byte1;
    }

    /**
     * Subtracts the byte value and the carry from the specified value.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte subtractByteWithCarry(InstructionBundle bundle) {
        UnsignedByte carry = new UnsignedByte(bundle.io.regs.cc.isMasked(CC_C) ? 1 : 0);
        bundle.io.regs.cc.and(~(CC_N | CC_V | CC_Z | CC_C));
        boolean byte1WasNegative = bundle.byte1.isNegative();
        boolean byte2WasNegative = bundle.byte2.isNegative();
        bundle.io.regs.cc.or(bundle.byte1.getShort() < bundle.byte2.getShort() ? CC_C : 0);
        bundle.byte1 = new UnsignedByte(bundle.byte1.getShort() + bundle.byte2.twosCompliment().getShort());
        boolean overflow = (!byte1WasNegative && byte2WasNegative && bundle.byte1.isNegative()) || (byte1WasNegative && !byte2WasNegative && !bundle.byte1.isNegative());
        byte1WasNegative = bundle.byte1.isNegative();
        bundle.io.regs.cc.or(bundle.byte1.getShort() < carry.getShort() ? CC_C : 0);
        bundle.byte1 = new UnsignedByte(bundle.byte1.getShort() + carry.twosCompliment().getShort());
        overflow |= !byte1WasNegative && bundle.byte1.isNegative() || byte1WasNegative && !bundle.byte1.isNegative();
        bundle.io.regs.cc.or(bundle.byte1.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(bundle.byte1.isNegative() ? CC_N : 0);
        bundle.io.regs.cc.or(overflow ? CC_V : 0);
        return bundle.byte1;
    }
}
