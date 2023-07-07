/*
 * Copyright (C) 2023 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

import java.util.function.Function;

import static ca.craigthomas.yacoco3e.datatypes.AddressingMode.IMMEDIATE;

/**
 * The Opcode class stores information related to a specific machine operation.
 * The opcode value itself, the mnemonic representation, the addressing mode,
 * the size of any immediate values read, and whether the size of input is
 * a word length.
 */
public class LongBranchInstruction extends BranchInstruction
{
    public LongBranchInstruction(int opcode,
                                 int ticks,
                                 String mnemonic,
                                 Function<InstructionBundle, Boolean> operation
    ) {
        super(opcode, ticks, mnemonic, operation);
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.ticks = ticks;
        this.operation = operation;
        this.addressingMode = IMMEDIATE;
        this.immediateByte = false;
    }

    @Override
    public int call(MemoryResult memoryResult, IOController io) {
        if (operation.apply(new InstructionBundle(memoryResult, io)).equals(true)) {
            UnsignedWord offset = memoryResult.value;
            io.regs.pc.add(offset.isNegative() ? offset.getSignedInt() : offset.getInt());
            return ticks + 1;
        }
        return ticks;
    }
}
