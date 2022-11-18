/*
 * Copyright (C) 2023 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.MemoryResult;
import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

/**
 * The Opcode class stores information related to a specific machine operation.
 * The opcode value itself, the mnemonic representation, the addressing mode,
 * the size of any immediate values read, and whether the size of input is
 * a word length.
 */
public class NotImplementedInstruction extends Instruction
{
    public NotImplementedInstruction(int opcode) {
        this.opcodeValue = opcode;
    }

    public int call(MemoryResult memoryResult, IOController io) throws MalformedInstructionException {
        throw new MalformedInstructionException("Instruction not implemented - opcode " + new UnsignedByte(opcodeValue));
    }
}
