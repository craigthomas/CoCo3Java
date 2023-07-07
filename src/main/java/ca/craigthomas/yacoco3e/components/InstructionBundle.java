package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.MemoryResult;
import ca.craigthomas.yacoco3e.datatypes.Register;
import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import ca.craigthomas.yacoco3e.datatypes.UnsignedWord;

public class InstructionBundle
{
    public MemoryResult memoryResult;
    public IOController io;
    public UnsignedByte byte1;
    public UnsignedByte byte2;
    public UnsignedWord word1;
    public UnsignedWord word2;
    public Register register1;

    public InstructionBundle(MemoryResult memoryResult, IOController io, UnsignedByte byte1) {
        this.byte1 = byte1;
        this.memoryResult = memoryResult;
        this.io = io;
    }

    public InstructionBundle(MemoryResult memoryResult, IOController io, UnsignedWord word1) {
        this.word1 = word1;
        this.memoryResult = memoryResult;
        this.io = io;
    }

    public InstructionBundle(MemoryResult memoryResult, IOController io, UnsignedWord word1, UnsignedWord word2) {
        this.word1 = word1;
        this.word2 = word2;
        this.memoryResult = memoryResult;
        this.io = io;
    }

    public InstructionBundle(IOController io, UnsignedByte byte1, UnsignedByte byte2) {
        this.byte1 = byte1;
        this.byte2 = byte2;
        this.io = io;
    }

    public InstructionBundle(MemoryResult memoryResult, IOController io, UnsignedByte byte1, UnsignedByte byte2, UnsignedWord word1) {
        this.byte1 = byte1;
        this.byte2 = byte2;
        this.word1 = word1;
        this.memoryResult = memoryResult;
        this.io = io;
    }

    public InstructionBundle(IOController io, UnsignedByte byte1, Register register1) {
        this.io = io;
        this.byte1 = byte1;
        this.register1 = register1;
    }

    public InstructionBundle(IOController io, int byte1) {
        this.io = io;
        this.byte1 = new UnsignedByte(byte1);
    }

    public InstructionBundle(IOController io, int byte1, int byte2) {
        this.io = io;
        this.byte1 = new UnsignedByte(byte1);
        this.byte2 = new UnsignedByte(byte2);
    }

    public InstructionBundle(IOController io, UnsignedWord word1, UnsignedWord word2) {
        this.io = io;
        this.word1 = word1;
        this.word2 = word2;
    }

    public InstructionBundle(MemoryResult memoryResult, IOController io) {
        this.io = io;
        this.memoryResult = memoryResult;
    }
}
