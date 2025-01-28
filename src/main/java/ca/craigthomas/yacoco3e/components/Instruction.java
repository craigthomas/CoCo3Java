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
    protected int ticks;
    protected int numBytesRead;

    /* Software Interrupt Vectors */
    public static final UnsignedWord SWI3 = new UnsignedWord(0xFFF2);
    public static final UnsignedWord SWI2 = new UnsignedWord(0xFFF4);
    public static final UnsignedWord SWI = new UnsignedWord(0xFFFA);

    public abstract int call(IOController io) throws MalformedInstructionException;

    public String getShortDescription() {
        if (this.opcodeValue > 255) {
            return String.format("%04X %-5s %s", this.opcodeValue, this.mnemonic, this.addressingMode);
        }
        return String.format("%02X %-5s %s", this.opcodeValue, this.mnemonic, this.addressingMode);
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
                break;
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
            byteRead = io.readByte(io.regs.pc);
            io.regs.incrementPC();
            numBytesRead = 1;
        } else {
            wordRead = io.readWord(io.regs.pc);
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
        wordRead = new UnsignedWord(io.regs.dp, io.readByte(io.regs.pc));
        io.regs.incrementPC();
        byteRead = io.readByte(wordRead);
        numBytesRead = 1;
    }

    /**
     * Given the current registers, will return the value that is
     * pointed to by the value that is pointed to by the program
     * counter value.
     */
    public void getExtended(IOController io) {
        wordRead = io.readWord(io.regs.pc);
        byteRead = io.readByte(wordRead);
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
        UnsignedWord nWord;
        numBytesRead = 1;

        /* 5-bit offset - check for signed values */
        if (!postByte.isMasked(0x80)) {
            r = io.getWordRegister(io.getIndexedRegister(postByte));
            UnsignedByte offset = new UnsignedByte(postByte.getShort() & 0x1F);
            wordRead = new UnsignedWord(r.getInt() + offset.getShort());

            if (offset.isMasked(0x10)) {
                offset.and(0xF);
                UnsignedByte newOffset = offset.twosCompliment();
                newOffset.and(0xF);
                wordRead = new UnsignedWord(r.getInt() - newOffset.getShort());
            }

            byteRead = io.readByte(wordRead);
            return;
        }

        switch (postByte.getShort() & 0x1F) {
            /* ,R+ -> R, then increment R */
            case 0x00:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                wordRead = new UnsignedWord(r.getInt());
                r.add(1);
                byteRead = io.readByte(wordRead);
                return;

            /* ,R++ -> R, then increment R by two */
            case 0x01:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                wordRead = new UnsignedWord(r.getInt());
                r.add(2);
                byteRead = io.readByte(wordRead);
                return;

            /* ,-R -> Decrement R, then R */
            case 0x02:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                r.add(-1);
                wordRead = r.copy();
                byteRead = io.readByte(r);
                return;

            /* ,--R -> Decrement R by two, then R */
            case 0x03:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                r.add(-2);
                wordRead = r.copy();
                byteRead = io.readByte(r);
                return;

            /* ,R -> No offset, just R */
            case 0x04:
                wordRead = io.getWordRegister(io.getIndexedRegister(postByte));
                byteRead = io.readByte(wordRead);
                return;

            /* B,R -> B offset from R */
            case 0x05:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                wordRead = new UnsignedWord(r.getInt() + io.regs.b.getSignedShort());
                byteRead = io.readByte(wordRead);
                return;

            /* A,R -> A offset from R */
            case 0x06:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                wordRead = new UnsignedWord(r.getInt() + io.regs.a.getSignedShort());
                byteRead = io.readByte(wordRead);
                return;

            /* n,R -> 8-bit offset from R */
            case 0x08:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                n = io.readByte(io.regs.pc);
                io.regs.incrementPC();
                wordRead = new UnsignedWord(r.getInt() + n.getSignedShort());
                byteRead = io.readByte(wordRead);
                numBytesRead = 2;
                return;

            /* n,R -> 16-bit offset from R */
            case 0x09:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                nWord = io.readWord(io.regs.pc);
                io.regs.incrementPC();
                io.regs.incrementPC();
                wordRead = new UnsignedWord(r.getInt() + nWord.getSignedInt());
                byteRead = io.readByte(wordRead);
                numBytesRead = 3;
                return;

            /* D,R -> D offset from R */
            case 0x0B:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                d = io.getWordRegister(Register.D);
                wordRead = new UnsignedWord(r.getInt() + d.getSignedInt());
                byteRead = io.readByte(wordRead);
                return;

            /* n,PC -> 8-bit offset from PC */
            case 0x0C:
                r = io.getWordRegister(Register.PC);
                n = io.readByte(io.regs.pc);
                io.regs.incrementPC();
                wordRead = new UnsignedWord(r.getInt() + n.getSignedShort());
                byteRead = io.readByte(wordRead);
                numBytesRead = 2;
                return;

            /* n,PC -> 16-bit offset from PC */
            case 0x0D:
                r = io.getWordRegister(Register.PC);
                nWord = io.readWord(io.regs.pc);
                io.regs.incrementPC();
                io.regs.incrementPC();
                wordRead = new UnsignedWord(r.getInt() + nWord.getSignedInt());
                byteRead = io.readByte(wordRead);
                numBytesRead = 3;
                return;

            /* [,R++] -> R, then increment R by two - indirect*/
            case 0x11:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                wordRead = io.readWord(new UnsignedWord(r.getInt()));
                r.add(2);
                byteRead = io.readByte(wordRead);
                numBytesRead = 2;
                return;

            /* [,--R] -> Decrement R by two, then R - indirect*/
            case 0x13:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                r.add(-2);
                wordRead = io.readWord(new UnsignedWord(r.getInt()));
                byteRead = io.readByte(wordRead);
                return;

            /* [,R] -> No offset, just R - indirect */
            case 0x14:
                wordRead = io.readWord(io.getWordRegister(io.getIndexedRegister(postByte)));
                byteRead = io.readByte(wordRead);
                return;

            /* [B,R] -> B offset from R - indirect */
            case 0x15:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                wordRead = io.readWord(new UnsignedWord(r.getInt() + io.regs.b.getSignedShort()));
                byteRead = io.readByte(wordRead);
                return;

            /* [A,R] -> A offset from R - indirect */
            case 0x16:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                wordRead = io.readWord(new UnsignedWord(r.getInt() + io.regs.a.getSignedShort()));
                byteRead = io.readByte(wordRead);
                return;

            /* [n,R] -> 8-bit offset from R - indirect */
            case 0x18:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                n = io.readByte(io.regs.pc);
                io.regs.incrementPC();
                wordRead = io.readWord(new UnsignedWord(r.getInt() + n.getSignedShort()));
                byteRead = io.readByte(wordRead);
                numBytesRead = 2;
                return;

            /* [n,R] -> 16-bit offset from R - indirect */
            case 0x19:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                nWord = io.readWord(io.regs.pc);
                io.regs.incrementPC();
                io.regs.incrementPC();
                wordRead = io.readWord(new UnsignedWord(r.getInt() + nWord.getSignedInt()));
                byteRead = io.readByte(wordRead);
                numBytesRead = 3;
                return;

            /* [D,R] -> D offset from R - indirect*/
            case 0x1B:
                r = io.getWordRegister(io.getIndexedRegister(postByte));
                wordRead = io.readWord(new UnsignedWord(r.getInt() + io.regs.getD().getSignedInt()));
                byteRead = io.readByte(wordRead);
                return;

            /* [n,PC] -> 8-bit offset from PC - indirect */
            case 0x1C:
                r = io.getWordRegister(Register.PC);
                n = io.readByte(io.regs.pc);
                io.regs.incrementPC();
                wordRead = io.readWord(new UnsignedWord(r.getInt() + n.getSignedShort()));
                byteRead = io.readByte(wordRead);
                numBytesRead = 2;
                return;

            /* [n,PC] -> 16-bit offset from PC - indirect */
            case 0x1D:
                r = io.getWordRegister(Register.PC);
                nWord = io.readWord(io.regs.pc);
                io.regs.incrementPC();
                io.regs.incrementPC();
                wordRead = io.readWord(new UnsignedWord(r.getInt() + nWord.getSignedInt()));
                byteRead = io.readByte(wordRead);
                numBytesRead = 3;
                return;

            /* [n] -> extended indirect */
            case 0x1F:
                nWord = io.readWord(io.regs.pc);
                io.regs.incrementPC();
                io.regs.incrementPC();
                wordRead = io.readWord(nWord);
                byteRead = io.readByte(wordRead);
                numBytesRead = 3;
                return;

            default:
                throw new MalformedInstructionException("Invalid postbyte " + postByte);
        }
    }
}
