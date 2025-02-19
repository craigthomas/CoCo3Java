/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.Register;
import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import ca.craigthomas.yacoco3e.datatypes.UnsignedWord;

import static ca.craigthomas.yacoco3e.datatypes.AddressingMode.IMMEDIATE;

/**
 * The Opcode class stores information related to a specific machine operation.
 * The opcode value itself, the mnemonic representation, the addressing mode,
 * the size of any immediate values read, and whether the size of input is
 * a word length.
 */
public class PushPopInstruction extends Instruction
{
    @FunctionalInterface
    public interface PushPopInterface
    {
        int apply(IOController io, UnsignedByte memoryByte, Register register);
    }

    protected PushPopInterface operation;
    protected Register register;

    public PushPopInstruction(int opcode,
                              int ticks,
                              String mnemonic,
                              PushPopInterface operation,
                              Register register
    ) {
        this.opcodeValue = opcode;
        this.mnemonic = mnemonic;
        this.ticks = ticks;
        this.operation = operation;
        this.register = register;
        this.addressingMode = IMMEDIATE;
        this.isByteSized = true;
        this.isValidInstruction = true;
        this.addressRead = new UnsignedWord();
    }

    public int call(IOController io) {
        return ticks + operation.apply(io, byteRead, register);
    }

    /**
     * Pushes the values of one or more registers onto the specified stack
     * according to the post byte (byte1) that is passed. Will return the number
     * of bytes that were pushed onto the stack.
     */
    public static int pushRegsToStack(IOController io, UnsignedByte memoryByte, Register register) {
        int bytes = 0;
        if (memoryByte.isMasked(0x80)) {
            io.pushStack(register, io.regs.pc);
            bytes += 2;
        }

        if (memoryByte.isMasked(0x40)) {
            if (register == Register.U) {
                io.pushStack(register, io.regs.s);
            } else {
                io.pushStack(register, io.regs.u);
            }
            bytes += 2;
        }

        if (memoryByte.isMasked(0x20)) {
            io.pushStack(register, io.regs.y);
            bytes += 2;
        }

        if (memoryByte.isMasked(0x10)) {
            io.pushStack(register, io.regs.x);
            bytes += 2;
        }

        if (memoryByte.isMasked(0x08)) {
            io.pushStack(register, io.regs.dp);
            bytes += 1;
        }

        if (memoryByte.isMasked(0x04)) {
            io.pushStack(register, io.regs.b);
            bytes += 1;
        }

        if (memoryByte.isMasked(0x02)) {
            io.pushStack(register, io.regs.a);
            bytes += 1;
        }

        if (memoryByte.isMasked(0x01)) {
            io.pushStack(register, io.regs.cc);
            bytes += 1;
        }
        return bytes;
    }

    /**
     * Pops bytes from a stack back into registers based on a postbyte
     * value. Will return the number of bytes popped from the stack.
     */
    public static int pullRegsFromStack(IOController io, UnsignedByte memoryByte, Register register) {
        int bytes = 0;

        if (memoryByte.isMasked(0x01)) {
            io.regs.cc.set(io.popStack(register));
            bytes += 1;
        }

        if (memoryByte.isMasked(0x02)) {
            io.regs.a.set(io.popStack(register));
            bytes += 1;
        }

        if (memoryByte.isMasked(0x04)) {
            io.regs.b.set(io.popStack(register));
            bytes += 1;
        }

        if (memoryByte.isMasked(0x08)) {
            io.regs.dp.set(io.popStack(register));
            bytes += 1;
        }

        if (memoryByte.isMasked(0x10)) {
            io.regs.x.set(
                    new UnsignedWord(
                            io.popStack(register),
                            io.popStack(register)
                    )
            );
            bytes += 2;
        }

        if (memoryByte.isMasked(0x20)) {
            io.regs.y.set(
                    new UnsignedWord(
                            io.popStack(register),
                            io.popStack(register)
                    )
            );
            bytes += 2;
        }

        if (memoryByte.isMasked(0x40)) {
            if (register == Register.S) {
                io.regs.u.set(
                        new UnsignedWord(
                                io.popStack(register),
                                io.popStack(register)
                        )
                );
            } else {
                io.regs.s.set(
                        new UnsignedWord(
                                io.popStack(register),
                                io.popStack(register)
                        )
                );
            }
            bytes += 2;
        }

        if (memoryByte.isMasked(0x80)) {
            io.regs.pc.set(
                    new UnsignedWord(
                            io.popStack(register),
                            io.popStack(register)
                    )
            );
            bytes += 2;
        }

        return bytes;
    }
}
