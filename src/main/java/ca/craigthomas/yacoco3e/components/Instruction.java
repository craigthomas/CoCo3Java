/*
 * Copyright (C) 2023-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

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
    protected AddressingMode addressingMode;
    protected boolean isByteSized;
    protected UnsignedByte byteRead;
    protected UnsignedWord wordRead;
    protected UnsignedWord addressRead;
    protected int ticks;
    protected int numBytesRead;
    protected boolean isValidInstruction;

    /* Software Interrupt Vectors */
    public static final UnsignedWord SWI3 = new UnsignedWord(0xFFF2);
    public static final UnsignedWord SWI2 = new UnsignedWord(0xFFF4);
    public static final UnsignedWord SWI = new UnsignedWord(0xFFFA);

    public abstract int call(IOController io) throws MalformedInstructionException;

    public String getShortDescription() {
        String result = "";
        if (this.opcodeValue > 255) {
            result += String.format("%04X", this.opcodeValue);
        } else {
            result += String.format("  %02X", this.opcodeValue);
        }
        result += String.format(" %s", this.mnemonic);

        if (this.addressingMode != AddressingMode.INHERENT) {
            result += String.format(" %s %04X [%04X]", this.addressingMode, this.wordRead.getInt(), this.addressRead.getInt());
        }
        return result;
    }

    /**
     * Executes the instruction.
     *
     * @param io the IOController object that interfaces with memory and other system devices
     * @return the number of ticks it took to execute the instruction
     * @throws MalformedInstructionException if the instruction is not properly formed
     */
    public int execute(IOController io) throws MalformedInstructionException {
        io.incrementPC();
        if (opcodeValue > 255) {
            io.incrementPC();
        }

        if (isValidInstruction) {
            switch (addressingMode) {
                case IMMEDIATE:
                    getImmediate(io);
                    break;

                case INDEXED:
                    getIndexed(io);
                    break;

                case DIRECT:
                    getDirect(io);
                    break;

                case EXTENDED:
                    getExtended(io);
                    break;

                default:
                    addressRead = new UnsignedWord(0);
                    wordRead = new UnsignedWord(0);
                    byteRead = new UnsignedByte(0);
                    break;
            }
        }

        return call(io);
    }


    /**
     * Reads one or more bytes at the memory location where the program
     * counter is currently pointed to. Advances the program counter
     * beyond the byte read.
     */
    public void getImmediate(IOController io) {
        if (isByteSized) {
            addressRead = io.regs.pc.copy();
            wordRead = io.readWord(addressRead);
            byteRead = wordRead.getHigh();
            io.regs.incrementPC();
            numBytesRead = 1;
        } else {
            addressRead = io.regs.pc.copy();
            wordRead = io.readWord(addressRead);
            byteRead = wordRead.getHigh();
            io.regs.incrementPC();
            io.regs.incrementPC();
            numBytesRead = 2;
        }
    }

    /**
     * Fetch the byte from memory location [dp:immediatebyte]. Return
     * in byteRead the value read from the specified memory location, and
     * in wordRead, the address that was read from.
     */
    public void getDirect(IOController io) {
        addressRead = new UnsignedWord(io.regs.dp, io.readByte(io.regs.pc));
        io.regs.incrementPC();
        wordRead = io.readWord(addressRead);
        byteRead = wordRead.getHigh();
        numBytesRead = 1;
    }

    /**
     * Given the current registers, will return the value that is
     * pointed to by the value that is pointed to by the program
     * counter value.
     */
    public void getExtended(IOController io) {
        addressRead = io.readWord(io.regs.pc).copy();
        wordRead = io.readWord(addressRead);
        byteRead = wordRead.getHigh();
        io.regs.incrementPC();
        io.regs.incrementPC();
        numBytesRead = 2;
    }

    /**
     * The getIndexed function reads the byte following the current PC word, and
     * interprets the byte. Depending on the value of the byte, a new value is
     * returned. May throw an MalformedInstructionException.
     */
    public void getIndexed(IOController io) throws MalformedInstructionException {
        UnsignedByte postByte = io.readByte(io.regs.pc);
        io.regs.incrementPC();
        UnsignedWord r;
        UnsignedWord d;
        UnsignedByte n;
        UnsignedWord w;
        numBytesRead = 1;

        /* 5-bit offset - check for signed values */
        if (!postByte.isMasked(0x80)) {
            r = io.getWordRegister(io.getIndexedRegister(postByte));
            UnsignedByte offset = new UnsignedByte(postByte.getShort() & 0x1F);
            addressRead = new UnsignedWord(r.getInt() + offset.getShort());

            if (offset.isMasked(0x10)) {
                offset.and(0xF);
                UnsignedByte newOffset = offset.twosCompliment();
                newOffset.and(0xF);
                addressRead = new UnsignedWord(r.getInt() - newOffset.getShort());
            }

            wordRead = io.readWord(addressRead);
            byteRead = wordRead.getHigh();
            return;
        }

        switch (postByte.getShort() & 0x1F) {
            /* ,R+ -> R, then increment R */
            case 0x00:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                addressRead = new UnsignedWord(r.getInt());
                r.add(1);
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                return;

            /* ,R++ -> R, then increment R by two */
            case 0x01:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                addressRead = new UnsignedWord(r.getInt());
                r.add(2);
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                return;

            /* ,-R -> Decrement R, then R */
            case 0x02:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                r.add(-1);
                addressRead = r.copy();
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                return;

            /* ,--R -> Decrement R by two, then R */
            case 0x03:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                r.add(-2);
                addressRead = r.copy();
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                return;

            /* ,R -> No offset, just R */
            case 0x04:
                addressRead = io.getWordRegister(io.getIndexedRegister(postByte));
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                return;

            /* B,R -> B offset from R */
            case 0x05:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                addressRead = new UnsignedWord(r.getInt() + io.regs.b.getSignedShort());
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                return;

            /* A,R -> A offset from R */
            case 0x06:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                addressRead = new UnsignedWord(r.getInt() + io.regs.a.getSignedShort());
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                return;

            /* n,R -> 8-bit offset from R */
            case 0x08:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                n = io.readByte(io.regs.pc);
                io.regs.incrementPC();
                addressRead = new UnsignedWord(r.getInt() + n.getSignedShort());
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                numBytesRead = 2;
                return;

            /* n,R -> 16-bit offset from R */
            case 0x09:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                w = io.readWord(io.regs.pc);
                io.regs.incrementPC();
                io.regs.incrementPC();
                addressRead = new UnsignedWord(r.getInt() + w.getSignedInt());
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                numBytesRead = 3;
                return;

            /* D,R -> D offset from R */
            case 0x0B:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                d = io.getWordRegister(Register.D);
                addressRead = new UnsignedWord(r.getInt() + d.getSignedInt());
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                return;

            /* n,PC -> 8-bit offset from PC */
            case 0x0C:
                r = io.getWordRegister(Register.PC);
                n = io.readByte(io.regs.pc);
                io.regs.incrementPC();
                addressRead = new UnsignedWord(r.getInt() + n.getSignedShort());
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                numBytesRead = 2;
                return;

            /* n,PC -> 16-bit offset from PC */
            case 0x0D:
                r = io.getWordRegister(Register.PC);
                w = io.readWord(io.regs.pc);
                io.regs.incrementPC();
                io.regs.incrementPC();
                addressRead = new UnsignedWord(r.getInt() + w.getSignedInt());
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                numBytesRead = 3;
                return;

            /* [,R++] -> R, then increment R by two - indirect*/
            case 0x11:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                addressRead = io.readWord(new UnsignedWord(r.getInt()));
                r.add(2);
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                numBytesRead = 2;
                return;

            /* [,--R] -> Decrement R by two, then R - indirect*/
            case 0x13:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                r.add(-2);
                addressRead = io.readWord(new UnsignedWord(r.getInt()));
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                return;

            /* [,R] -> No offset, just R - indirect */
            case 0x14:
                addressRead = io.readWord(io.getWordRegister(io.getIndexedRegister(postByte)));
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                return;

            /* [B,R] -> B offset from R - indirect */
            case 0x15:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                addressRead = io.readWord(new UnsignedWord(r.getInt() + io.regs.b.getSignedShort()));
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                return;

            /* [A,R] -> A offset from R - indirect */
            case 0x16:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                addressRead = io.readWord(new UnsignedWord(r.getInt() + io.regs.a.getSignedShort()));
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                return;

            /* [n,R] -> 8-bit offset from R - indirect */
            case 0x18:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                n = io.readByte(io.regs.pc);
                io.regs.incrementPC();
                addressRead = io.readWord(new UnsignedWord(r.getInt() + n.getSignedShort()));
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                numBytesRead = 2;
                return;

            /* [n,R] -> 16-bit offset from R - indirect */
            case 0x19:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                w = io.readWord(io.regs.pc);
                io.regs.incrementPC();
                io.regs.incrementPC();
                addressRead = io.readWord(new UnsignedWord(r.getInt() + w.getSignedInt()));
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                numBytesRead = 3;
                return;

            /* [D,R] -> D offset from R - indirect*/
            case 0x1B:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                addressRead = io.readWord(new UnsignedWord(r.getInt() + io.regs.getD().getSignedInt()));
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                return;

            /* [n,PC] -> 8-bit offset from PC - indirect */
            case 0x1C:
                r = io.getWordRegister(Register.PC);
                n = io.readByte(io.regs.pc);
                io.regs.incrementPC();
                addressRead = io.readWord(new UnsignedWord(r.getInt() + n.getSignedShort()));
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                numBytesRead = 2;
                return;

            /* [n,PC] -> 16-bit offset from PC - indirect */
            case 0x1D:
                r = io.getWordRegister(Register.PC);
                w = io.readWord(io.regs.pc);
                io.regs.incrementPC();
                io.regs.incrementPC();
                addressRead = io.readWord(new UnsignedWord(r.getInt() + w.getSignedInt()));
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                numBytesRead = 3;
                return;

            /* [n] -> extended indirect */
            case 0x1F:
                w = io.readWord(io.regs.pc);
                io.regs.incrementPC();
                io.regs.incrementPC();
                addressRead = io.readWord(w);
                wordRead = io.readWord(addressRead);
                byteRead = wordRead.getHigh();
                numBytesRead = 3;
                return;

            default:
                throw new MalformedInstructionException("Invalid postbyte " + postByte);
        }
    }
}
