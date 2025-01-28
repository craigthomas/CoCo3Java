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
    @FunctionalInterface
    public interface ByteInterface
    {
        void apply(IOController io, UnsignedByte memoryByte, UnsignedWord address);
    }
    protected ByteInterface operation;

    public ByteInstruction(int opcode,
                           int ticks,
                           String mnemonic,
                           AddressingMode addressingMode,
                           ByteInterface operation
    ) {
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.addressingMode = addressingMode;
        this.ticks = ticks;
        this.operation = operation;
        this.isByteSized = true;
    }

    public int call(IOController io) {
        operation.apply(io, byteRead, wordRead);
        return ticks + ((addressingMode == INDEXED) ? numBytesRead : 0);
    }

    /**
     * Applies the two's compliment value to the contents in the specified
     * memory address.
     */
    public static void negate(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.and(~(CC_N | CC_Z | CC_V | CC_C));

        if (memoryByte.equalsInt(0x80)) {
            io.regs.cc.or(CC_V);
            io.regs.cc.or(CC_N);
            io.regs.cc.or(CC_C);
            memoryByte.set(0x80);
            io.writeByte(address, memoryByte);
            return;
        }

        if (memoryByte.equalsInt(0x00)) {
            io.regs.cc.or(CC_Z);
            memoryByte.set(0);
            io.writeByte(address, memoryByte);
            return;
        }

        memoryByte.compliment();
        memoryByte.add(1);
        io.regs.cc.or(memoryByte.isMasked(0x80) ? CC_V : 0);
        io.regs.cc.or(memoryByte.isNegative() ? CC_N : 0);
        io.regs.cc.or(CC_C);
        io.writeByte(address, memoryByte);
    }

    /**
     * Inverts all bits in the byte. Returns the complimented value as the
     * result.
     */
    public static void compliment(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        memoryByte.compliment();
        io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        io.regs.cc.or(CC_C);
        io.regs.cc.or(memoryByte.isNegative() ? CC_N : 0);
        io.regs.cc.or(memoryByte.isZero() ? CC_Z : 0);
        io.writeByte(address, memoryByte);
    }

    /**
     * Shifts all the bits in the byte to the right by one bit. Returns the
     * result of the operation, while impacting the condition code register.
     * The lowest bit of the byte is shifted into the condition code carry
     * bit.
     */
    public static void logicalShiftRight(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        boolean bit0 = memoryByte.isMasked(0x80);
        memoryByte.shiftRight();
        memoryByte.and(~0x80);
        io.regs.cc.and(~(CC_N | CC_Z | CC_C));
        io.regs.cc.or(bit0 ? CC_C : 0);
        io.regs.cc.or(memoryByte.isZero() ? CC_Z : 0);
        io.writeByte(address, memoryByte);
    }

    /**
     * Rotates the bits of a byte one place to the right. Will rotate the
     * carry bit into the highest bit of the byte if set.
     */
    public static void rotateRight(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        boolean bitZero = memoryByte.isMasked(0x1);
        memoryByte.shiftRight();
        memoryByte.or(io.regs.cc.isMasked(CC_C) ? 0x80 : 0x0);
        io.regs.cc.and(~(CC_N | CC_Z | CC_C));
        io.regs.cc.or(bitZero ? CC_C : 0);
        io.regs.cc.or(memoryByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(memoryByte.isNegative() ? CC_N : 0);
        io.writeByte(address, memoryByte);
    }

    /**
     * Shifts the bits of a byte one place to the right. Will maintain a copy
     * of bit 7 in the 7th bit. Bit 0 will be shifted into the carry bit.
     */
    public static void arithmeticShiftRight(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        boolean bit7 = memoryByte.isMasked(0x80);
        boolean bit0 = memoryByte.isMasked(0x1);
        memoryByte.shiftRight();
        memoryByte.or(bit7 ? 0x80 : 0);
        io.regs.cc.and(~(CC_N | CC_Z | CC_C));
        io.regs.cc.or(bit0 ? CC_C : 0);
        io.regs.cc.or(memoryByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(memoryByte.isNegative() ? CC_N : 0);
        io.writeByte(address, memoryByte);
    }

    /**
     * Shifts the bits of a byte one place to the left. Bit 0 will be filled
     * with a zero, while bit 7 will be shifted into the carry bit.
     */
    public static void arithmeticShiftLeft(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        boolean bit7 = memoryByte.isMasked(0x80);
        boolean bit6 = memoryByte.isMasked(0x40);

        memoryByte.shiftLeft();
        io.regs.cc.and(~(CC_N | CC_Z | CC_V | CC_C));
        io.regs.cc.or(bit7 ? CC_C : 0);
        io.regs.cc.or(bit7 ^ bit6 ? CC_V : 0);
        io.regs.cc.or(memoryByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(memoryByte.isNegative() ? CC_N : 0);
        io.writeByte(address, memoryByte);
    }

    /**
     * Rotates the bits of a byte one place to the left. Will rotate the
     * carry bit into the lowest bit of the byte if set.
     */
    public static void rotateLeft(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        boolean bit7 = memoryByte.isMasked(0x80);
        boolean bit6 = memoryByte.isMasked(0x40);
        boolean carry = io.regs.cc.isMasked(CC_C);

        memoryByte.shiftLeft();
        io.regs.cc.and(~(CC_N | CC_Z | CC_C | CC_V));
        io.regs.cc.or(bit7 ? CC_C : 0);
        memoryByte.or(carry ? 1 : 0);
        io.regs.cc.or(CC_V);
        if (bit7 == bit6) {
            io.regs.cc.and(~CC_V);
        }
        io.regs.cc.or(memoryByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(memoryByte.isNegative() ? CC_N : 0);
        io.writeByte(address, memoryByte);
    }

    /**
     * Decrements the byte value by one.
     */
    public static void decrement(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        io.regs.cc.or(memoryByte.isZero() ? CC_V : 0);
        memoryByte.add(0xFF);
        io.regs.cc.or(memoryByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(memoryByte.isNegative() ? CC_N : 0);
        io.writeByte(address, memoryByte);
    }

    /**
     * Increments the byte value by one.
     */
    public static void increment(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        boolean wasNegative = memoryByte.isNegative();
        memoryByte.add(1);
        io.regs.cc.or(memoryByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(memoryByte.isNegative() ? CC_N : 0);
        io.regs.cc.or(memoryByte.isNegative() != wasNegative ? CC_V : 0);
        io.writeByte(address, memoryByte);
    }

    /**
     * Tests the byte for zero condition or negative condition.
     */
    public static void testByte(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        io.regs.cc.and(~(CC_N | CC_Z | CC_V));
        io.regs.cc.or(memoryByte.isZero() ? CC_Z : 0);
        io.regs.cc.or(memoryByte.isNegative() ? CC_N : 0);
    }

    /**
     * Clears the specified byte.
     */
    public static void clear(IOController io, UnsignedByte memoryByte, UnsignedWord address) {
        memoryByte.set(0);
        io.regs.cc.and(~(CC_N | CC_C | CC_V));
        io.regs.cc.or(CC_Z);
        io.writeByte(address, memoryByte);
    }
}
