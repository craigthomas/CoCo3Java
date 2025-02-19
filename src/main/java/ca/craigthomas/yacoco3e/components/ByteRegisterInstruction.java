/*
 * Copyright (C) 2023-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

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
    @FunctionalInterface
    public interface ByteRegisterInterface
    {
        void apply(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address);
    }

    private final ByteRegisterInterface operation;
    protected Register register;

    public ByteRegisterInstruction(int opcode,
                                   int ticks,
                                   String mnemonic,
                                   AddressingMode addressingMode,
                                   ByteRegisterInterface operation,
                                   Register register
    ) {
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.addressingMode = addressingMode;
        this.ticks = ticks;
        this.operation = operation;
        this.register = register;
        this.isByteSized = true;
        this.isValidInstruction = true;
        this.addressRead = new UnsignedWord();
    }

    public int call(IOController io) throws MalformedInstructionException {
        switch(register) {
            case A:
                operation.apply(io, io.regs.a, byteRead, addressRead);
                break;

            case B:
                operation.apply(io, io.regs.b, byteRead, addressRead);
                break;

            case CC:
                operation.apply(io, io.regs.cc, byteRead, addressRead);
                break;

            default:
                throw new MalformedInstructionException(
                        "Register " + register + " unsupported for ByteRegisterInstruction"
                );
        }
        return (addressingMode == INDEXED) ? ticks + numBytesRead : ticks;
    }

    /**
     * Loads the specified register with the value. The byte2 values is from the
     * memory location to be loaded into the register.
     */
    public static void loadByteRegister(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.and(~(CC_N | CC_V | CC_Z));
        io.regs.cc.or(memoryByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(memoryByte.isNegative() ? CC_N : 0);
        registerByte.set(memoryByte);
    }

    /**
     * Stores the byte register in the memory location.
     */
    public static void storeByteRegister(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        io.writeByte(address, registerByte);
        io.regs.cc.and(~(CC_V | CC_N | CC_Z));
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
    }

    /**
     * Performs a logical OR of the byte register and the value.
     */
    public static void logicalOr(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        registerByte.or(memoryByte);
        io.regs.cc.and(~(CC_N | CC_V | CC_Z));
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
    }

    /**
     * Performs a logical OR on the memory result and the condition code register.
     */
    public static void logicalOrCC(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.or(memoryByte);
    }

    /**
     * Performs a logical AND of the byte register and the value.
     */
    public static void logicalAnd(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        registerByte.and(memoryByte);
        io.regs.cc.and(~(CC_N | CC_V | CC_Z));
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
    }

    /**
     * Performs a logical AND on the memory result and the condition code register.
     * Will return the condition code register.
     */
    public static void logicalAndCC(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.and(memoryByte);
    }

    /**
     * Adds the specified value to the specified register.
     */
    public static void addByte(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        int signedResult = registerByte.getSignedShort() + memoryByte.getSignedShort();
        UnsignedByte result = new UnsignedByte(registerByte.getShort() + memoryByte.getShort());
        io.regs.cc.and(~(CC_N | CC_V | CC_Z | CC_C | CC_H));
        io.regs.cc.or(((registerByte.getShort() ^ memoryByte.getShort() ^ result.getShort()) & 0x10) > 0 ? CC_H : 0);
        io.regs.cc.or(result.isZero() ? CC_Z : 0);
        io.regs.cc.or(result.isNegative() ? CC_N : 0);
        io.regs.cc.or(signedResult > 127 || signedResult < -127 ? CC_V : 0);
        io.regs.cc.or(((registerByte.getShort() + memoryByte.getShort()) & 0x100) > 0 ? CC_C : 0);
        registerByte.set(result);
    }

    /**
     * Performs an addition of the specified register and the value together,
     * plus the value of the carry bit (0 or 1). Stores the result in the
     * specified register.
     */
    public static void addWithCarry(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        UnsignedByte carry = new UnsignedByte((io.regs.cc.isMasked(CC_C)) ? 1 : 0);
        io.regs.cc.and(~(CC_N | CC_V | CC_Z | CC_C | CC_H));

        // Check for overflow condition
        int signedResult = registerByte.getSignedShort() + carry.getSignedShort();
        int overflow = signedResult > 127 || signedResult < -127 ? CC_V : 0;

        // Check for carry condition if we add the carry value
        if (((registerByte.getShort() + carry.getShort()) & 0x100) > 0) {
            io.regs.cc.or(CC_C);
        }

        // Check for half carry condition on the carry bit addition
        UnsignedByte test = new UnsignedByte((registerByte.getShort() & 0xF) + (carry.getShort() & 0xF));
        if (test.isMasked(0x10)) {
            io.regs.cc.or(CC_H);
        }

        // Add the carry bit
        registerByte.set(new UnsignedByte(registerByte.getShort() + carry.getShort()));

        // Check for overflow condition of the actual byte
        signedResult = registerByte.getSignedShort() + memoryByte.getSignedShort();
        overflow |= signedResult > 127 || signedResult < -127 ? CC_V : 0;

        // Check for carry condition if we add the actual byte now
        if (((registerByte.getShort() + memoryByte.getShort()) & 0x100) > 0) {
            io.regs.cc.or(CC_C);
        }

        // Check for half carry condition on the second byte addition
        test = new UnsignedByte((registerByte.getShort() & 0xF) + (memoryByte.getShort() & 0xF));
        if (test.isMasked(0x10)) {
            io.regs.cc.or(CC_H);
        }

        // Add the second byte
        registerByte.set(new UnsignedByte(registerByte.getShort() + memoryByte.getShort()));
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
        io.regs.cc.or(overflow);
    }

    /**
     * Logically AND the register byte with a memory byte and tests the byte for zero
     * condition or negative condition. Register contents are left unchanged.
     */
    public static void bitTest(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        UnsignedByte value = new UnsignedByte(registerByte.getShort());
        value.and(memoryByte.getShort());
        io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        io.regs.cc.or(value.isZero() ? CC_Z : 0);
        io.regs.cc.or(value.isNegative() ? CC_N : 0);
    }

    /**
     * Compares the two bytes and sets the appropriate register sets.
     */
    public static void compareByte(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.and(~(CC_N | CC_Z | CC_V | CC_C));
        boolean byte1WasNegative = registerByte.isNegative();
        boolean byte2WasNegative = memoryByte.isNegative();
        UnsignedByte result = new UnsignedByte(registerByte.getShort() + memoryByte.twosCompliment().getShort());
        io.regs.cc.or(registerByte.getShort() < memoryByte.getShort() ? CC_C : 0);
        io.regs.cc.or(result.isZero() ? CC_Z : 0);
        io.regs.cc.or(result.isNegative() ? CC_N : 0);
        boolean overflow = (!byte1WasNegative && byte2WasNegative && result.isNegative()) || (byte1WasNegative && !byte2WasNegative && !result.isNegative());
        io.regs.cc.or(overflow ? CC_V : 0);
    }

    /**
     * Performs a correction to the A register to transform the value into
     * a proper BCD form.
     */
    public static void decimalAdditionAdjust(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        int fullByte = registerByte.getShort();
        int mostSignificantNibble = fullByte & 0xF0;
        int leastSignificantNibble = fullByte & 0x0F;
        int adjustment = 0;
        boolean carryPreviouslySet = io.regs.cc.isMasked(CC_C);

        if (carryPreviouslySet || mostSignificantNibble > 0x90 || (mostSignificantNibble > 0x80 && leastSignificantNibble > 0x09)) {
            adjustment |= 0x60;
        }

        if (io.regs.cc.isMasked(CC_H) || leastSignificantNibble > 0x09) {
            adjustment |= 0x06;
        }

        UnsignedByte result = new UnsignedByte(adjustment);
        io.regs.cc.and(~(CC_C | CC_N | CC_Z));
        if (((io.regs.a.getShort() + result.getShort()) & 0x100) > 0) {
            io.regs.cc.or(CC_C);
        }
        io.regs.a.set(new UnsignedByte(io.regs.a.getShort() + result.getShort()));
        io.regs.cc.or(io.regs.a.isZero() ? CC_Z : 0);
        io.regs.cc.or(io.regs.a.isNegative() ? CC_N : 0);
    }


    /**
     * Subtracts the byte value from the specified register.
     */
    public static void subtractByte(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.and(~(CC_N | CC_V | CC_Z | CC_C));
        boolean byte1WasNegative = registerByte.isNegative();
        boolean byte2WasNegative = memoryByte.isNegative();
        io.regs.cc.or(registerByte.getShort() < memoryByte.getShort() ? CC_C : 0);
        registerByte.set(new UnsignedByte(registerByte.getShort() + memoryByte.twosCompliment().getShort()));
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
        boolean overflow = (!byte1WasNegative && byte2WasNegative && registerByte.isNegative()) || (byte1WasNegative && !byte2WasNegative && !memoryByte.isNegative());
        io.regs.cc.or(overflow ? CC_V : 0);
    }

    /**
     * Subtracts the byte value and the carry from the specified value.
     */
    public static void subtractByteWithCarry(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        UnsignedByte carry = new UnsignedByte(io.regs.cc.isMasked(CC_C) ? 1 : 0);
        io.regs.cc.and(~(CC_N | CC_V | CC_Z | CC_C));
        boolean byte1WasNegative = registerByte.isNegative();
        boolean byte2WasNegative = memoryByte.isNegative();
        io.regs.cc.or(registerByte.getShort() < memoryByte.getShort() ? CC_C : 0);
        registerByte.set(new UnsignedByte(registerByte.getShort() + memoryByte.twosCompliment().getShort()));
        boolean overflow = (!byte1WasNegative && byte2WasNegative && registerByte.isNegative()) || (byte1WasNegative && !byte2WasNegative && !registerByte.isNegative());
        byte1WasNegative = registerByte.isNegative();
        io.regs.cc.or(registerByte.getShort() < carry.getShort() ? CC_C : 0);
        registerByte.set(new UnsignedByte(registerByte.getShort() + carry.twosCompliment().getShort()));
        overflow |= !byte1WasNegative && registerByte.isNegative() || byte1WasNegative && !registerByte.isNegative();
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
        io.regs.cc.or(overflow ? CC_V : 0);
    }

    /**
     * Performs an exclusive OR of the register and the byte value.
     */
    public static void exclusiveOr(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        registerByte.set(new UnsignedByte(registerByte.getShort() ^ memoryByte.getShort()));
        io.regs.cc.and(~(CC_N | CC_V | CC_Z));
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
    }

    /**
     * Extends bit 8 of the B register into the register.
     */
    public static void signExtend(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        registerByte.set(io.regs.b.isMasked(0x80) ? 0xFF : 0x00);
        io.regs.cc.and(~(CC_N | CC_V | CC_Z));
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
    }

    /**
     * Clears the specified register.
     */
    public static void clear(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        registerByte.set(0);
        io.regs.cc.and(~(CC_N | CC_C | CC_V));
        io.regs.cc.or(CC_Z);
    }

    /**
     * Increments the register value by one.
     */
    public static void increment(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        boolean wasNegative = registerByte.isNegative();
        registerByte.add(1);
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
        io.regs.cc.or(registerByte.isNegative() != wasNegative ? CC_V : 0);
    }

    /**
     * Decrements the byte value by one.
     */
    public static void decrement(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        io.regs.cc.or(registerByte.isZero() ? CC_V : 0);
        registerByte.add(0xFF);
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
    }

    /**
     * Rotates the bits of a register one place to the left. Will rotate the
     * carry bit into the lowest bit of the byte if set.
     */
    public static void rotateLeft(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        boolean bit7 = registerByte.isMasked(0x80);
        boolean bit6 = registerByte.isMasked(0x40);
        boolean carry = io.regs.cc.isMasked(CC_C);

        registerByte.shiftLeft();
        registerByte.or(carry ? 1 : 0);
        io.regs.cc.and(~(CC_N | CC_Z | CC_C | CC_V));
        io.regs.cc.or(bit7 ? CC_C : 0);
        io.regs.cc.or(bit7 ^ bit6 ? CC_V : 0);
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
    }

    /**
     * Shifts the bits of a register one place to the left. Bit 0 will be filled
     * with a zero, while bit 7 will be shifted into the carry bit.
     */
    public static void arithmeticShiftLeft(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        boolean bit7 = registerByte.isMasked(0x80);
        boolean bit6 = registerByte.isMasked(0x40);

        registerByte.shiftLeft();
        io.regs.cc.and(~(CC_N | CC_Z | CC_V | CC_C));
        io.regs.cc.or(bit7 ? CC_C : 0);
        io.regs.cc.or(bit7 ^ bit6 ? CC_V : 0);
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
    }

    /**
     * Rotates the bits of a register one place to the right. Will rotate the
     * carry bit into the highest bit of the byte if set.
     */
    public static void rotateRight(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        boolean bitZero = registerByte.isMasked(0x1);
        registerByte.shiftRight();
        registerByte.or(io.regs.cc.isMasked(CC_C) ? 0x80 : 0x0);
        io.regs.cc.and(~(CC_N | CC_Z | CC_C));
        io.regs.cc.or(bitZero ? CC_C : 0);
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
    }

    /**
     * Shifts all the bits in the register to the right by one bit. Returns the
     * result of the operation, while impacting the condition code register.
     * The lowest bit of the byte is shifted into the condition code carry
     * bit.
     */
    public static void logicalShiftRight(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        boolean bit0 = registerByte.isMasked(0x1);
        registerByte.shiftRight();
        io.regs.cc.and(~(CC_N | CC_Z | CC_C));
        io.regs.cc.or(bit0 ? CC_C : 0);
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
    }

    /**
     * Shifts the bits of a register one place to the right. Will maintain a copy
     * of bit 7 in the 7th bit. Bit 0 will be shifted into the carry bit.
     */
    public static void arithmeticShiftRight(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        boolean bit7 = registerByte.isMasked(0x80);
        boolean bit0 = registerByte.isMasked(0x1);
        registerByte.shiftRight();
        registerByte.or(bit7 ? 0x80 : 0);
        io.regs.cc.and(~(CC_N | CC_Z | CC_C));
        io.regs.cc.or(bit0 ? CC_C : 0);
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
    }

    /**
     * Inverts all bits in the register.
     */
    public static void compliment(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        registerByte.compliment();
        io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        io.regs.cc.or(CC_C);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
    }

    /**
     * Tests the register for zero condition or negative condition.
     */
    public static void testByte(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        io.regs.cc.or(registerByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
    }

    /**
     * Applies the two's compliment value to the contents in the register.
     */
    public static void negate(IOController io, UnsignedByte registerByte, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.and(~(CC_N | CC_Z | CC_V | CC_C));

        if (registerByte.equalsInt(0x80)) {
            io.regs.cc.or(CC_V);
            io.regs.cc.or(CC_N);
            io.regs.cc.or(CC_C);
            registerByte.set(0x80);
            return;
        }

        if (registerByte.equalsInt(0x00)) {
            io.regs.cc.or(CC_Z);
            registerByte.set(0);
            return;
        }

        registerByte.compliment();
        registerByte.add(1);
        io.regs.cc.or(registerByte.isMasked(0x80) ? CC_V : 0);
        io.regs.cc.or(registerByte.isNegative() ? CC_N : 0);
        io.regs.cc.or(CC_C);
    }
}
