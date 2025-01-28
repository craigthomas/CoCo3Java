/*
 * Copyright (C) 2023-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import java.util.function.Function;

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
                                 Function<IOController, Boolean> operation
    ) {
        super(opcode, ticks, mnemonic, operation);
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.ticks = ticks;
        this.operation = operation;
        this.isByteSized = false;
    }

    @Override
    public int call(IOController io) {
        if (operation.apply(io).equals(true)) {
            io.regs.pc.add(wordRead.isNegative() ? wordRead.getSignedInt() : wordRead.getInt());
            return ticks + 1;
        }
        return ticks;
    }
}
