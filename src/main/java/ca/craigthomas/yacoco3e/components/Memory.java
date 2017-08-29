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

    /* Page address registers - controls mapping between virtual and physical memory */
    public static final int PAR_COUNT = 8;
    protected short [] executivePAR;
    protected short [] taskPAR;
    protected short [] defaultPAR;
    boolean executiveParEnabled;
    boolean mmuEnabled;

    protected static final int TOTAL_PAGES = 0x3F;

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
        enableExecutivePAR();
        executivePAR = new short[PAR_COUNT];
        taskPAR = new short[PAR_COUNT];
        defaultPAR = new short[PAR_COUNT];
        mmuEnabled = true;

        /* Initial PAR values */
        executivePAR[0] = taskPAR[0] = defaultPAR[0] = 0x38;
        executivePAR[1] = taskPAR[1] = defaultPAR[1] = 0x39;
        executivePAR[2] = taskPAR[2] = defaultPAR[2] = 0x3A;
        executivePAR[3] = taskPAR[3] = defaultPAR[3] = 0x3B;
        executivePAR[4] = taskPAR[4] = defaultPAR[4] = 0x3C;
        executivePAR[5] = taskPAR[5] = defaultPAR[5] = 0x3D;
        executivePAR[6] = taskPAR[6] = defaultPAR[6] = 0x3E;
        executivePAR[7] = taskPAR[7] = defaultPAR[7] = 0x3F;
    }

    /**
     * Converts the 16-bit virtual address into a 19-bit physical address.
     * The address is obtained by taking the top 3 bits of the address and
     * translating them into a PAR number. Then, the low 6 bits of the
     * corresponding PAR are combined with the other 13-bits of the virtual
     * address to give an address in physical memory. Note that the PAR that
     * is read is either from the Executive set or the Task set, depending
     * on which is active in the MMU. If the MMU is not enabled, then the
     * defaultPAR set is used, which maps virtual memory to the top 64K
     * of physical memory.
     *
     * @param address the address to translate
     * @return the physical address in memory based on the pars
     */
    public int getPhysicalAddress(UnsignedWord address) {
        int intAddress = address.getInt();
        int par = intAddress >> 13;
        int parAddress;
        if (mmuEnabled) {
            parAddress = (executiveParEnabled ? executivePAR[par] : taskPAR[par]) & TOTAL_PAGES;
        } else {
            parAddress = defaultPAR[par] & TOTAL_PAGES;
        }
        parAddress = parAddress << 13;
        return parAddress | (intAddress & 0x1FFF);
    }

    /**
     * Enables the Executive Page Address Register.
     */
    public void enableExecutivePAR() {
        executiveParEnabled = true;
    }

    /**
     * Enables the Task Page Address Register.
     */
    public void enableTaskPAR() {
        executiveParEnabled = false;
    }

    /**
     * Enables the MMU.
     */
    public void enableMMU() {
        mmuEnabled = true;
    }

    /**
     * Disables the MMU.
     */
    public void disableMMU() {
        mmuEnabled = false;
    }

    /**
     * Sets the EXECUTIVE page address register to the specified value.
     *
     * @param par the PAR number to set
     * @param value the value to set it to
     */
    public void setExecutivePAR(int par, UnsignedByte value) {
        executivePAR[par] = value.getShort();
    }

    /**
     * Sets the TASK page address register to the specified value.
     *
     * @param par the PAR number to set
     * @param value the value to set it to
     */
    public void setTaskPAR(int par, UnsignedByte value) {
        taskPAR[par] = value.getShort();
    }

    /**
     * Reads an UnsignedByte from the specified address.
     *
     * @param address the UnsignedWord location to read from
     * @return an UnsignedByte from the specified location
     */
    public UnsignedByte readByte(UnsignedWord address) {
        return new UnsignedByte(memory[getPhysicalAddress(address)]);
    }

    /**
     * Writes an UnsignedByte to the specified memory address.
     *
     * @param address the UnsignedWord location to write to
     * @param value the UnsignedByte to write
     */
    public void writeByte(UnsignedWord address, UnsignedByte value) {
        memory[getPhysicalAddress(address)] = value.getShort();
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