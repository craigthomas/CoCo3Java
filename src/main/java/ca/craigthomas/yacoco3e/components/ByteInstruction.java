/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

import java.util.function.Function;

import static ca.craigthomas.yacoco3e.datatypes.AddressingMode.INDEXED;
import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;

/**
 * The Opcode class stores information related to a specific machine operation.
 * The opcode value itself, the mnemonic representation, the addressing mode,
 * the size of any immediate values read, and whether the size of input is
 * a word length.
 */
public class ByteInstruction extends Instruction
{
    protected Function<InstructionBundle, UnsignedByte> operation;

    public ByteInstruction(int opcode,
                           int ticks,
                           String mnemonic,
                           int addressingMode,
                           Function<InstructionBundle, UnsignedByte> operation
    ) {
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.addressingMode = addressingMode;
        this.ticks = ticks;
        this.operation = operation;
        this.immediateByte = true;
    }

    public int call(MemoryResult memoryResult, IOController io) {
        UnsignedWord address = memoryResult.value;
        UnsignedByte byte1 = io.readByte(address);
        byte1 = operation.apply(new InstructionBundle(memoryResult, io, byte1));
        io.writeByte(address, byte1);
        return ticks + ((addressingMode == INDEXED) ? memoryResult.bytesConsumed : 0);
    }

    /**
     * Applies the two's compliment value to the contents in the specified
     * memory address.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the negated byte
     */
    public static UnsignedByte negate(InstructionBundle bundle) {
        UnsignedByte result = bundle.byte1.twosCompliment();
        bundle.io.regs.cc.and(~(CC_N | CC_Z | CC_V | CC_C));

        if (bundle.byte1.equalsInt(0x80)) {
            bundle.io.regs.cc.or(CC_V);
            bundle.io.regs.cc.or(CC_N);
            bundle.io.regs.cc.or(CC_C);
            return new UnsignedByte(0x80);
        }

        if (bundle.byte1.equalsInt(0x00)) {
            bundle.io.regs.cc.or(CC_Z);
            return new UnsignedByte(0x00);
        }

        bundle.io.regs.cc.or(result.isMasked(0x80) ? CC_V : 0);
        bundle.io.regs.cc.or(result.isNegative() ? CC_N : 0);
        bundle.io.regs.cc.or(CC_C);
        return result;
    }

    /**
     * Inverts all bits in the byte. Returns the complimented value as the
     * result.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the complimented value
     */
    public static UnsignedByte compliment(InstructionBundle bundle) {
        UnsignedByte result = new UnsignedByte(~bundle.byte1.getShort());
        bundle.io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        bundle.io.regs.cc.or(CC_C);
        bundle.io.regs.cc.or(result.isNegative() ? CC_N : 0);
        bundle.io.regs.cc.or(result.isZero() ? CC_Z : 0);
        return result;
    }

    /**
     * Shifts all the bits in the byte to the right by one bit. Returns the
     * result of the operation, while impacting the condition code register.
     * The lowest bit of the byte is shifted into the condition code carry
     * bit.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the shifted byte value
     */
    public static UnsignedByte logicalShiftRight(InstructionBundle bundle) {
        UnsignedByte result = new UnsignedByte(bundle.byte1.getShort() >> 1);
        bundle.io.regs.cc.and(~(CC_N | CC_Z | CC_C));
        bundle.io.regs.cc.or(bundle.byte1.isMasked(0x1) ? CC_C : 0);
        bundle.io.regs.cc.or(result.isZero() ? CC_Z : 0);
        return result;
    }

    /**
     * Rotates the bits of a byte one place to the right. Will rotate the
     * carry bit into the highest bit of the byte if set.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the rotated value
     */
    public static UnsignedByte rotateRight(InstructionBundle bundle) {
        UnsignedByte result = new UnsignedByte(bundle.byte1.getShort() >> 1);
        result.add(bundle.io.regs.cc.isMasked(CC_C) ? 0x80 : 0x0);
        bundle.io.regs.cc.and(~(CC_N | CC_Z | CC_C));
        bundle.io.regs.cc.or(bundle.byte1.isMasked(0x1) ? CC_C : 0);
        bundle.io.regs.cc.or(result.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(result.isNegative() ? CC_N : 0);
        return result;
    }

    /**
     * Shifts the bits of a byte one place to the right. Will maintain a copy
     * of bit 7 in the 7th bit. Bit 0 will be shifted into the carry bit.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the shifted value
     */
    public static UnsignedByte arithmeticShiftRight(InstructionBundle bundle) {
        UnsignedByte result = new UnsignedByte(bundle.byte1.getShort() >> 1);
        result.add(bundle.byte1.isMasked(0x80) ? 0x80 : 0);
        bundle.io.regs.cc.and(~(CC_N | CC_Z | CC_C));
        bundle.io.regs.cc.or(bundle.byte1.isMasked(0x1) ? CC_C : 0);
        bundle.io.regs.cc.or(result.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(result.isNegative() ? CC_N : 0);
        return result;
    }

    /**
     * Shifts the bits of a byte one place to the left. Bit 0 will be filled
     * with a zero, while bit 7 will be shifted into the carry bit.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the shifted value
     */
    public static UnsignedByte arithmeticShiftLeft(InstructionBundle bundle) {
        UnsignedByte result = new UnsignedByte(bundle.byte1.getShort() << 1);
        bundle.io.regs.cc.and(~(CC_N | CC_Z | CC_V | CC_C));
        bundle.io.regs.cc.or(bundle.byte1.isMasked(0x80) ? CC_C : 0);
        boolean bit7 = bundle.byte1.isMasked(0x80);
        boolean bit6 = bundle.byte1.isMasked(0x40);
        bundle.io.regs.cc.or(bit7 ^ bit6 ? CC_V : 0);
        bundle.io.regs.cc.or(result.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(result.isNegative() ? CC_N : 0);
        return result;
    }

    /**
     * Rotates the bits of a byte one place to the left. Will rotate the
     * carry bit into the lowest bit of the byte if set.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the rotated value
     */
    public static UnsignedByte rotateLeft(InstructionBundle bundle) {
        UnsignedByte result = new UnsignedByte(bundle.byte1.getShort() << 1);
        result.add(bundle.io.regs.cc.isMasked(CC_C) ? 0x1 : 0x0);
        bundle.io.regs.cc.and(~(CC_N | CC_Z | CC_C | CC_V));
        bundle.io.regs.cc.or(bundle.byte1.isMasked(0x80) ? CC_C : 0);
        boolean bit7 = bundle.byte1.isMasked(0x80);
        boolean bit6 = bundle.byte1.isMasked(0x40);
        bundle.io.regs.cc.or(CC_V);
        if (bit7 == bit6) {
            bundle.io.regs.cc.and(~CC_V);
        }
        bundle.io.regs.cc.or(result.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(result.isNegative() ? CC_N : 0);
        return result;
    }

    /**
     * Decrements the byte value by one.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the decremented byte value
     */
    public static UnsignedByte decrement(InstructionBundle bundle) {
        UnsignedByte result = new UnsignedByte(bundle.byte1.getShort() + 0xFF);
        bundle.io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        bundle.io.regs.cc.or(bundle.byte1.isZero() ? CC_V : 0);
        bundle.io.regs.cc.or(result.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(result.isNegative() ? CC_N : 0);
        return result;
    }

    /**
     * Increments the byte value by one.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the incremented byte value
     */
    public static UnsignedByte increment(InstructionBundle bundle) {
        bundle.io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        boolean wasNegative = bundle.byte1.isNegative();
        UnsignedByte result = new UnsignedByte(bundle.byte1.getShort() + 1);
        bundle.io.regs.cc.or(result.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(result.isNegative() ? CC_N : 0);
        bundle.io.regs.cc.or(result.isNegative() != wasNegative ? CC_V : 0);
        return result;
    }

    /**
     * Tests the byte for zero condition or negative condition.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the original byte value
     */
    public static UnsignedByte testByte(InstructionBundle bundle) {
        UnsignedByte value = new UnsignedByte(bundle.byte1.getShort());
        bundle.io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        bundle.io.regs.cc.or(value.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(value.isNegative() ? CC_N : 0);
        return bundle.byte1;
    }

    /**
     * Clears the specified byte.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the cleared byte
     */
    public static UnsignedByte clear(InstructionBundle bundle) {
        bundle.io.regs.cc.and(~(CC_N | CC_C | CC_V));
        bundle.io.regs.cc.or(CC_Z);
        return new UnsignedByte(0);
    }

    /**
     * Extends bit 8 of the B register into the return byte.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte signExtend(InstructionBundle bundle) {
        return new UnsignedByte(bundle.io.regs.b.isMasked(0x80) ? 0xFF : 0x00);
    }

    /**
     * Performs an exclusive OR of the register and the byte value.
     *
     * @param bundle the InstructionBundle that contains information to process
     */
    public static UnsignedByte exclusiveOr(InstructionBundle bundle) {
        UnsignedByte result = new UnsignedByte(bundle.byte1.getShort() ^ bundle.byte2.getShort());
        bundle.io.regs.cc.and(~(CC_N | CC_V | CC_Z));
        bundle.io.regs.cc.or(result.isZero() ? CC_Z : 0);
        bundle.io.regs.cc.or(result.isNegative() ? CC_N : 0);
        return result;
    }
}
