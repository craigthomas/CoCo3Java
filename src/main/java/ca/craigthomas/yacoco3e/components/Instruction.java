/*
 * Copyright (C) 2023 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.AddressingMode;
import ca.craigthomas.yacoco3e.datatypes.MemoryResult;
import ca.craigthomas.yacoco3e.datatypes.UnsignedWord;

/**
 * The Opcode class stores information related to a specific machine operation.
 * The opcode value itself, the mnemonic representation, the addressing mode,
 * the size of any immediate values read, and whether the size of input is
 * a word length.
 */
abstract class Instruction
{
    protected int opcodeValue;
    protected String mnemonic;
    protected int addressingMode;
    protected boolean immediateByte;
    protected MemoryResult memoryResult;
    protected int ticks;

    /* Software Interrupt Vectors */
    public static final int SWI3 = 0xFFF2;
    public static final UnsignedWord SWI2 = new UnsignedWord(0xFFF4);
    public static final UnsignedWord SWI = new UnsignedWord(0xFFFA);

    public abstract int call(MemoryResult memoryResult, IOController io) throws MalformedInstructionException;

    public String getShortDescription() {
        if (this.opcodeValue > 255) {
            return String.format("%04X %-5s %s", this.opcodeValue, this.mnemonic, AddressingMode.decode(this.addressingMode));
        }
        return String.format("%02X %-5s %s", this.opcodeValue, this.mnemonic, AddressingMode.decode(this.addressingMode));
    }
}
