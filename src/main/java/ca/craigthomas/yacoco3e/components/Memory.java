/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

import org.apache.commons.io.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.logging.Logger;

/**
 * The Memory class controls access to and from memory locations in the memory
 * array. Additionally, the memory class controls access to and from memory
 * mapped IO address routines.
 */
public class Memory
{
    /* 512K memory definition */
    public static final int MEM_512K = 0x80000;

    /* The main memory array */
    protected short [] memory;

    // The logger for the class
    private final static Logger LOGGER = Logger.getLogger(Memory.class.getName());

    public Memory() {
        this(MEM_512K);
    }

    /**
     * Initializes the memory module with the number of bytes specified.
     *
     * @param size the number of bytes to initialize in main memory
     */
    public Memory(int size) {
        memory = new short[size];
    }

    /**
     * Given the current registers, will return an UnsignedWord from
     * the memory location of the direct pointer as the setHigh byte,
     * and the setLow byte pointed to by the PC.
     *
     * @param regs the RegisterSet containing the current CPU state
     * @return a MemoryResult with the data from the DP:PC location
     */
    public MemoryResult getDirect(RegisterSet regs) {
        return new MemoryResult(
                1,
                new UnsignedWord(regs.getDP(), readByte(regs.getPC()))
        );
    }

    /**
     * Given the current registers, and an address into memory, return an
     * UnsignedWord that is based on the value of the byte that occurs at
     * one byte past the PC. This address contains a "post-byte" value that
     * encodes one of several different meanings. See the inline comments for
     * how the post-byte value is checked.
     *
     * @param regs the current state of the registers
     *
     * @return the value pointed to by the post-byte (or indexed location)
     * @throws IllegalIndexedPostbyteException
     */
    public MemoryResult getIndexed(RegisterSet regs) {
        UnsignedWord address = regs.getPC().next().copy();
        UnsignedByte postByte = readByte(address).copy();
        UnsignedWord register = new UnsignedWord(0);
        UnsignedWord result;
        UnsignedByte tempByte;
        UnsignedWord tempWord;
        Register registerFlag = Register.UNKNOWN;
        int bytesConsumed = 1;
        int offset = 0;
        int addToReg = 0;

        /* Check to see if there is a 5-bit signed offset to apply */
        if (!postByte.isMasked(0x80)) {
            postByte.and(0x1F);
            if (postByte.isMasked(0x10)) {
                postByte.and(0xF);
                offset -= postByte.getShort();
            } else {
                offset += postByte.getShort();
            }
            result = address.copy();
            result.add(offset);
            return new MemoryResult(bytesConsumed, result);
        }

        /* Check to see if a register should be modified */
        switch (postByte.getShort() & 0x60) {
            case 0x00:
                register = regs.getX();
                registerFlag = Register.X;
                break;

            case 0x20:
                register = regs.getY();
                registerFlag = Register.Y;
                break;

            case 0x40:
                register = regs.getU();
                registerFlag = Register.U;
                break;

            case 0x60:
                register = regs.getS();
                registerFlag = Register.S;
                break;

            default:
                break;
        }

        /* Check the post-byte for the offset codes */
        switch (postByte.getShort() & 0xF) {
            case 0x0:
                /* ,R+ */
                result = register.copy();
                addToReg = 1;
                break;

            case 0x1:
                /* ,R++ */
                result = register.copy();
                addToReg = 2;
                break;

            case 0x2:
                /* ,R- */
                result = register.copy();
                addToReg = -1;
                break;

            case 0x3:
                /* ,R-- */
                result = register.copy();
                addToReg = -2;
                break;

            case 0x4:
                /* Nothing */
                result = register.copy();
                break;

            case 0x5:
                /* B,R */
                result = register.copy();
                result.add(regs.getB().getSignedShort());
                break;

            case 0x6:
                /* A,R */
                result = register.copy();
                result.add(regs.getA().getSignedShort());
                break;

            case 0x8:
                /* nn,R - 8-bit offset */
                tempByte = readByte(address);
                result = register.copy();
                result.add(tempByte.getSignedShort());
                bytesConsumed++;
                break;

            case 0x9:
                /* nnnn,R - 16-bit offset */
                tempWord = readWord(address);
                tempByte = readByte(tempWord);
                result = register.copy();
                result.add(tempByte.getSignedShort());
                bytesConsumed += 2;
                break;

            case 0xB:
                /* D,R */
                result = regs.binaryAdd(register, regs.getD(), false, false, false);
                break;

            case 0xC:
                /* nn,PC - 8-bit offset */
                tempByte = readByte(address);
                result = regs.getPC().copy();
                result.add(tempByte.getSignedShort());
                bytesConsumed = 1;
                break;

            case 0xD:
                /* nnnn,PC - 16-bit offset */
                tempWord = readWord(address);
                result = regs.getPC().copy();
                result.add(tempWord.getSignedInt());
                bytesConsumed = 2;
                break;

            default:
                /* TODO: better error handling */
                System.out.println("Illegal post-byte " + postByte.toString());
                result = new UnsignedWord(0);
        }

        /* If the 5-bit code starts with 1, do indirect addressing */
        if (postByte.isMasked(0x10)) {
            return new MemoryResult(bytesConsumed, readWord(result));
        }

        /* Before we calculate the address, post increment the registers */
        switch (registerFlag) {
            case X:
                regs.getX().add(addToReg);
                break;

            case Y:
                regs.getY().add(addToReg);
                break;

            case U:
                regs.getU().add(addToReg);
                break;

            case S:
                regs.getS().add(addToReg);
                break;
        }

        return new MemoryResult(bytesConsumed, result);
    }

    /**
     * Given the current registers, will return the value that is
     * pointed to by the program counter.
     *
     * @param regs the RegisterSet containing the current CPU state
     * @return a MemoryResult with the data from the PC location
     */
    public MemoryResult getImmediateWord(RegisterSet regs) {
        return new MemoryResult(
                2,
                readWord(regs.getPC())
        );
    }

    /**
     * Reads a single byte at the current value that is the program
     * counter. Will store the byte in the high byte of the resultant
     * word.
     *
     * @param regs the RegisterSet containing the current CPU state
     * @return a MemoryResult with the data from the PC location
     */
    public MemoryResult getImmediateByte(RegisterSet regs) {
        return new MemoryResult(
                1,
                new UnsignedWord(readByte(regs.getPC()), new UnsignedByte())
        );
    }

    /**
     * Given the current registers, will return the value that is
     * pointed to by the value that is pointed to by the program
     * counter value.
     *
     * @param regs the RegisterSet containing the current CPU state
     * @return a MemoryResult with the data from the PC location
     */
    public MemoryResult getExtended(RegisterSet regs) {
        return new MemoryResult(
                2,
                readWord(regs.getPC())
        );
    }

    /**
     * Reads an UnsignedByte from the specified address.
     *
     * @param address the UnsignedWord location to read from
     * @return an UnsignedByte from the specified location
     */
    public UnsignedByte readByte(UnsignedWord address) {
        return new UnsignedByte(memory[address.getInt()]);
    }

    /**
     * Reads an UnsignedWord from the specified address.
     *
     * @param address the UnsignedWord location to read from
     * @return an UnsignedWord from the specified location
     */
    public UnsignedWord readWord(UnsignedWord address) {
        UnsignedWord result = new UnsignedWord();
        result.setHigh(readByte(address));
        result.setLow(readByte(address.next()));
        return result;
    }

    /**
     * Writes an UnsignedByte to the specified memory address.
     *
     * @param address the UnsignedWord location to write to
     * @param value the UnsignedByte to write
     */
    public void writeByte(UnsignedWord address, UnsignedByte value) {
        memory[address.getInt()] = value.getShort();
    }

    /**
     * Writes an UnsignedWord to the specified memory address.
     *
     * @param address the UnsignedWord location to write to
     * @param value the UnsignedWord to write
     */
    public void writeWord(UnsignedWord address, UnsignedWord value) {
        writeByte(address, value.getHigh());
        writeByte(address.next(), value.getLow());
    }

    public UnsignedByte getPCByte(RegisterSet regs) {
        return readByte(regs.getPC());
    }
    /**
     * Reads the next program counter byte. Does not increment the program
     * counter.
     *
     * @param regs the current state of the registers
     * @return the next byte read from the program counter address
     */
    public UnsignedByte nextPCByte(RegisterSet regs) {
        UnsignedWord address = regs.getPC().next();
        return readByte(address);
    }

    /**
     * Pushes the specified byte onto the specified stack. Will decrement the
     * stack pointer prior to performing the push.
     *
     * @param regs the state of the current registers
     * @param register the stack to use
     * @param value the value to push
     */
    public void pushStack(RegisterSet regs, Register register, UnsignedByte value) {
        if (register == Register.S) {
            regs.getS().add(-1);
            writeByte(regs.getS(), value);
        } else {
            regs.getU().add(-1);
            writeByte(regs.getU(), value);
        }
    }

    /**
     * Pops a byte off of a stack. Will increment the stack pointer after
     * performing the pop.
     *
     * @param regs the state of the current registers
     * @param register the stack to use
     * @return the value popped
     */
    public UnsignedByte popStack(RegisterSet regs, Register register) {
        UnsignedByte result = new UnsignedByte();
        if (register == Register.S) {
            result.set(readByte(regs.getS()));
            regs.getS().add(1);
        } else {
            result.set(readByte(regs.getU()));
            regs.getU().add(1);
        }
        return result;
    }

    /**
     * Load a file full of bytes into emulator memory.
     *
     * @param stream The open stream to read from
     * @param offset The memory location to start loading the file into
     */
    boolean loadStreamIntoMemory(InputStream stream, UnsignedWord offset) {
        try {
            UnsignedWord currentOffset = offset.copy();
            byte[] data;

            /* Determine appropriate endianess */
            if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                LOGGER.info("little endian system detected");
                ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
                byte[] bytes = new byte[2];
                int bytesRead = stream.read(bytes);
                while (bytesRead != -1) {
                    tempStream.write(bytes);
                    bytesRead = stream.read(bytes);
                }
                data = tempStream.toByteArray();
                tempStream.close();
            } else {
                LOGGER.info("big endian system detected");
                data = IOUtils.toByteArray(stream);
            }

            /* Read data from the buffer */
            for (byte theByte : data) {
                writeByte(currentOffset, new UnsignedByte(theByte));
                currentOffset.add(1);
            }

            return true;
        } catch (Exception e) {
            LOGGER.severe("error reading from stream");
            LOGGER.severe(e.getMessage());
            return false;
        }
    }
}