/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.MemoryResult;
import ca.craigthomas.yacoco3e.datatypes.Register;
import ca.craigthomas.yacoco3e.datatypes.UnsignedWord;

import java.util.function.Function;

import static ca.craigthomas.yacoco3e.datatypes.AddressingMode.IMMEDIATE;

/**
 * The Opcode class stores information related to a specific machine operation.
 * The opcode value itself, the mnemonic representation, the addressing mode,
 * the size of any immediate values read, and whether the size of input is
 * a word length.
 */
public class PushPopInstruction extends Instruction
{
    protected Function<InstructionBundle, Integer> operation;
    protected Register register;

    public PushPopInstruction(int opcode,
                              int ticks,
                              String mnemonic,
                              Function<InstructionBundle, Integer> operation,
                              Register register
    ) {
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.ticks = ticks;
        this.operation = operation;
        this.register = register;
        this.addressingMode = IMMEDIATE;
        this.immediateByte = true;
    }

    public int call(MemoryResult memoryResult, IOController io) {
        int bytes = operation.apply(new InstructionBundle(io, memoryResult.value.getHigh(), register));
        return ticks + bytes;
    }

    /**
     * Pushes the values of one or more registers onto the specified stack
     * according to the post byte (byte1) that is passed. Will return the number
     * of bytes that were pushed onto the stack.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the number of bytes pushed
     */
    public static Integer pushRegsToStack(InstructionBundle bundle) {
        int bytes = 0;
        if (bundle.byte1.isMasked(0x80)) {
            bundle.io.pushStack(bundle.register1, bundle.io.regs.pc);
            bytes += 2;
        }

        if (bundle.byte1.isMasked(0x40)) {
            if (bundle.register1 == Register.U) {
                bundle.io.pushStack(bundle.register1, bundle.io.regs.s);
            } else {
                bundle.io.pushStack(bundle.register1, bundle.io.regs.u);
            }
            bytes += 2;
        }

        if (bundle.byte1.isMasked(0x20)) {
            bundle.io.pushStack(bundle.register1, bundle.io.regs.y);
            bytes += 2;
        }

        if (bundle.byte1.isMasked(0x10)) {
            bundle.io.pushStack(bundle.register1, bundle.io.regs.x);
            bytes += 2;
        }

        if (bundle.byte1.isMasked(0x08)) {
            bundle.io.pushStack(bundle.register1, bundle.io.regs.dp);
            bytes += 1;
        }

        if (bundle.byte1.isMasked(0x04)) {
            bundle.io.pushStack(bundle.register1, bundle.io.regs.b);
            bytes += 1;
        }

        if (bundle.byte1.isMasked(0x02)) {
            bundle.io.pushStack(bundle.register1, bundle.io.regs.a);
            bytes += 1;
        }

        if (bundle.byte1.isMasked(0x01)) {
            bundle.io.pushStack(bundle.register1, bundle.io.regs.cc);
            bytes += 1;
        }
        return bytes;
    }

    /**
     * Pops bytes from a stack back into registers based on a postbyte
     * value. Will return the number of bytes popped from the stack.
     *
     * @param bundle the InstructionBundle that contains information to process
     * @return the number of bytes popped
     */
    public static Integer pullRegsFromStack(InstructionBundle bundle) {
        int bytes = 0;

        if (bundle.byte1.isMasked(0x01)) {
            bundle.io.regs.cc.set(bundle.io.popStack(bundle.register1));
            bytes += 1;
        }

        if (bundle.byte1.isMasked(0x02)) {
            bundle.io.regs.a.set(bundle.io.popStack(bundle.register1));
            bytes += 1;
        }

        if (bundle.byte1.isMasked(0x04)) {
            bundle.io.regs.b.set(bundle.io.popStack(bundle.register1));
            bytes += 1;
        }

        if (bundle.byte1.isMasked(0x08)) {
            bundle.io.regs.dp.set(bundle.io.popStack(bundle.register1));
            bytes += 1;
        }

        if (bundle.byte1.isMasked(0x10)) {
            bundle.io.regs.x.set(
                    new UnsignedWord(
                            bundle.io.popStack(bundle.register1),
                            bundle.io.popStack(bundle.register1)
                    )
            );
            bytes += 2;
        }

        if (bundle.byte1.isMasked(0x20)) {
            bundle.io.regs.y.set(
                    new UnsignedWord(
                            bundle.io.popStack(bundle.register1),
                            bundle.io.popStack(bundle.register1)
                    )
            );
            bytes += 2;
        }

        if (bundle.byte1.isMasked(0x40)) {
            if (bundle.register1 == Register.S) {
                bundle.io.regs.u.set(
                        new UnsignedWord(
                                bundle.io.popStack(bundle.register1),
                                bundle.io.popStack(bundle.register1)
                        )
                );
            } else {
                bundle.io.regs.s.set(
                        new UnsignedWord(
                                bundle.io.popStack(bundle.register1),
                                bundle.io.popStack(bundle.register1)
                        )
                );
            }
            bytes += 2;
        }

        if (bundle.byte1.isMasked(0x80)) {
            bundle.io.regs.pc.set(
                    new UnsignedWord(
                            bundle.io.popStack(bundle.register1),
                            bundle.io.popStack(bundle.register1)
                    )
            );
            bytes += 2;
        }

        return bytes;
    }
}
