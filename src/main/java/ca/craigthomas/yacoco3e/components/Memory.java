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

import static ca.craigthomas.yacoco3e.common.IO.loadStream;

/**
 * The Memory class controls access to and from memory locations in the memory
 * array. Additionally, the memory class controls access to and from memory
 * mapped IO address routines.
 */
public class Memory
{
    /* 512K memory definition */
    public static final int MEM_512K = 0x80000;
    public static final int MEM_32K = 0x8000;

    /* The main memory arrays */
    protected short [] memory;
    protected short [] rom;
    protected short [] cartROM;

    /* Page address registers - controls mapping between virtual and physical memory */
    public static final int PAR_COUNT = 8;
    protected short [] executivePAR;
    protected short [] taskPAR;
    protected short [] defaultPAR;
    boolean executiveParEnabled;
    boolean mmuEnabled;
    boolean allRAMMode;
    UnsignedByte romMode;

    protected static final int TOTAL_PAGES = 0x3F;

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

        /* ROM memory sizes */
        cartROM = new short[MEM_32K];
        rom = new short[MEM_32K];

        /* Setup RAM/ROM mode variables */
        allRAMMode = true;
        romMode = new UnsignedByte(0x2);

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
     * In addition to the MMU translation, cartridge ROM and machine ROM
     * are mapped. The mapping depends on the RAM mode that is set. In ROM/RAM
     * mode, machine and cartridge ROMs are mapped to various physical pages.
     *
     * @param address the address to translate
     * @return the physical address in memory based on the pars
     */

    public int getPAR(UnsignedWord address) {
        return address.getInt() >> 13;
    }

    /**
     * Reads a byte from either the computer's RAM, ROM or the cartridge ROM.
     *
     * @param address the address to read from
     * @return the short value of the byte read
     */
    public short readPhysicalByte(UnsignedWord address) {
        int intAddress = address.getInt();
        int par = getPAR(address);

        /* Grab the PAR to read from (if MMU enabled) */
        int parValue = defaultPAR[par];
        if (mmuEnabled) {
            parValue = (executiveParEnabled) ? executivePAR[par] : taskPAR[par];
        }

        /* RAM only */
        if (allRAMMode) {
            return memory[getPhysicalAddress(par, intAddress)];
        }

        /* RAM + ROM */
        if (romMode.equals(new UnsignedByte(0x2))) {
            switch (parValue) {
                case 0x3C:
                    return rom[intAddress & 0x1FFF];

                case 0x3D:
                    return rom[0x2000 + (intAddress & 0x1FFF)];

                case 0x3E:
                    return rom[0x4000 + (intAddress & 0x1FFF)];

                case 0x3F:
                    return rom[0x6000 + (intAddress & 0x1FFF)];

                default:
                    return memory[getPhysicalAddress(par, intAddress)];
            }
        }

        /* RAM + CART ROM */
        if (romMode.equals(new UnsignedByte(0x3))) {
            switch (parValue) {
                case 0x3C:
                    return cartROM[intAddress & 0x1FFF];

                case 0x3D:
                    return cartROM[0x2000 + (intAddress & 0x1FFF)];

                case 0x3E:
                    return cartROM[0x4000 + (intAddress & 0x1FFF)];

                case 0x3F:
                    return cartROM[0x6000 + (intAddress & 0x1FFF)];

                default:
                    return memory[getPhysicalAddress(par, intAddress)];
            }
        }

        /* RAM, ROM, and CART ROM - lower 16K = ROM, upper 16K = CART ROM */
        switch (parValue) {
            case 0x3C:
                return rom[intAddress & 0x1FFF];

            case 0x3D:
                return rom[0x2000 + (intAddress & 0x1FFF)];

            case 0x3E:
                return cartROM[intAddress & 0x1FFF];

            case 0x3F:
                return cartROM[0x2000 + (intAddress & 0x1FFF)];

            default:
                return memory[getPhysicalAddress(par, intAddress)];
        }
    }

    /**
     * Writes an UnsignedByte to the specified memory address. If the system
     * is in a ROM mode, will not write bytes to a ROM location.
     *
     * @param address the UnsignedWord location to write to
     * @param value the UnsignedByte to write
     */
    public void writeByte(UnsignedWord address, UnsignedByte value) {
        int intAddress = address.getInt();
        int par = getPAR(address);

        /* RAM only */
        if (allRAMMode) {
            memory[getPhysicalAddress(par, intAddress)] = value.getShort();
            return;
        }

        /* RAM + ROM modes - don't write anything to 3C, 3D, 3E, 3F*/
        switch (par) {
            case 0x3C:
            case 0x3D:
            case 0x3E:
            case 0x3F:
                return;

            default:
                memory[getPhysicalAddress(par, intAddress)] = value.getShort();
        }
    }

    /**
     * Given a PAR and an address, translates the address into a 19-bit
     * address into physical RAM.
     *
     * @param par the page to read from
     * @param address the actual address to read
     * @return the address in physical memory to read from
     */
    public int getPhysicalAddress(int par, int address) {
        int parAddress;
        if (mmuEnabled) {
            parAddress = (executiveParEnabled ? executivePAR[par] : taskPAR[par]) & TOTAL_PAGES;
        } else {
            parAddress = defaultPAR[par] & TOTAL_PAGES;
        }
        parAddress = parAddress << 13;
        return parAddress | (address & 0x1FFF);
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
     * Enables all RAM mode operation.
     */
    public void enableAllRAMMode() {
        allRAMMode = true;
    }

    /**
     * Disables all RAM mode operation.
     */
    public void disableAllRAMMode() {
        allRAMMode = false;
    }

    /**
     * Sets the operating mode of ROM. Values are as follows:
     *
     *   bit1 bit0
     *    0    x     - 3C = Ext Basic, 3D = Basic, 3E & 3F = Cart ROM
     *    1    0     - 3C = Ext Basic, 3D = Basic, 3E = Reset, 3F = Sup Ext Basic
     *    1    1     - 3C & 3D & 3E & 3F = Cart ROM
     *
     * @param mode the mode to operate in
     */
    public void setROMMode(UnsignedByte mode) {
        romMode = mode;
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
        return new UnsignedByte(readPhysicalByte(address));
    }

    boolean loadROM(byte[] streamData) {
        int byteCounter = 0;
        if (streamData != null) {
            for (byte data : streamData) {
                rom[byteCounter] = (short) data;
                byteCounter++;
            }
            return true;
        }
        return false;
    }

    boolean loadCartROM(byte[] streamData) {
        int byteCounter = 0;
        if (streamData != null) {
            for (byte data : streamData) {
                cartROM[byteCounter] = (short) data;
                byteCounter++;
            }
            return true;
        }
        return false;
    }

    /**
     * Load a file full of bytes into emulator memory.
     *
     * @param stream The open stream to read from
     * @param offset The memory location to start loading the file into
     */
    boolean loadStreamIntoMemory(InputStream stream, UnsignedWord offset) {
        byte[] data = loadStream(stream);
        UnsignedWord currentOffset = offset.copy();

        if (data == null) {
            return false;
        }

        /* Read data from the buffer */
        for (byte theByte : data) {
            writeByte(currentOffset, new UnsignedByte(theByte));
            currentOffset.add(1);
        }

        return true;
    }
}