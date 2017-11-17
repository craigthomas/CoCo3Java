/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

public class IOController
{
    /* The number of IO memory addresses */
    public final static int IO_ADDRESS_SIZE = 256;

    /* The IO memory address space */
    protected short [] ioMemory;

    /* IO Devices */
    protected Memory memory;
    protected RegisterSet regs;
    protected Keyboard keyboard;
    protected Screen screen;
    protected Cassette cassette;
    protected CPU cpu;
    protected DiskDrive [] disk;

    /* Disk Drive Selector */
    protected int diskDriveSelect;

    public static final int NUM_DISK_DRIVES = 4;

    /* CoCo Compatible Mode */
    protected boolean cocoCompatibleMode;

    /* Vertical Offset Register */
    protected UnsignedWord verticalOffsetRegister;

    /* SAM Display Offset Register */
    protected UnsignedByte samDisplayOffsetRegister;

    /* PIA1 DRB */
    protected UnsignedByte pia1DRA;
    protected UnsignedByte pia1CRA;

    protected UnsignedByte pia1DRB;
    protected UnsignedByte pia1CRB;

    /* PIA2 Data Register and Control Register */
    protected UnsignedByte pia2DRA;
    protected UnsignedByte pia2CRA;

    protected UnsignedByte pia2DRB;
    protected UnsignedByte pia2CRB;

    /* Condition Code - Carry */
    public static final short CC_C = 0x01;

    /* Condition Code - Overflow */
    public static final short CC_V = 0x02;

    /* Condition Code - Zero */
    public static final short CC_Z = 0x04;

    /* Condition Code - Negative */
    public static final short CC_N = 0x08;

    /* Condition Code - Interrupt Request */
    public static final short CC_I = 0x10;

    /* Condition Code - Half Carry */
    public static final short CC_H = 0x20;

    /* Condition Code - Fast Interrupt Request */
    public static final short CC_F = 0x40;

    /* Condition Code - Everything */
    public static final short CC_E = 0x80;

    /* GIME IRQ enabled / disabled */
    public boolean irqEnabled;

    /* GIME IRQ status */
    public UnsignedByte irqStatus;

    /* GIME FIRQ enabled / disabled */
    public boolean firqEnabled;

    /* GIME FIRQ status */
    public UnsignedByte firqStatus;

    /* The number of ticks to pass in 63.5 microseconds */
    public static final int TIMER_63_5_MICROS = 56;

    /* The number of ticks to pass in 16.6 milliseconds */
    public static final int TIMER_16_6_MILLIS = 14833;

    /* The timer tick threshold */
    public int timerTickThreshold;

    /* The number of ticks that has passed for the timer */
    public int timerTickCounter;

    public UnsignedWord timerResetValue;

    public UnsignedWord timerValue;

    public int horizontalBorderTickValue;

    public int verticalBorderTickValue;

    /* PIA interrupt values */
    public int pia1FastTimer;
    public int pia1SlowTimer;

    public boolean pia1FastTimerEnabled;
    public boolean pia1SlowTimerEnabled;


    public IOController(Memory memory, RegisterSet registerSet, Keyboard keyboard, Screen screen, Cassette cassette) {
        ioMemory = new short[IO_ADDRESS_SIZE];
        this.memory = memory;
        this.regs = registerSet;
        this.keyboard = keyboard;
        this.cocoCompatibleMode = false;
        this.screen = screen;
        this.cassette = cassette;

        /* Display registers */
        verticalOffsetRegister = new UnsignedWord(0x0400);
        samDisplayOffsetRegister = new UnsignedByte(0x0);

        /* PIAs */
        pia1CRA = new UnsignedByte(0);
        pia1DRA = new UnsignedByte(0);

        pia1CRB = new UnsignedByte(0);
        pia1DRB = new UnsignedByte(0);

        pia2CRA = new UnsignedByte(0);
        pia2DRA = new UnsignedByte(0);

        pia2CRB = new UnsignedByte(0);
        pia2DRB = new UnsignedByte(0);

        /* Interrupts */
        irqStatus = new UnsignedByte(0);
        firqStatus = new UnsignedByte(0);

        /* Timer related values */
        timerTickThreshold = TIMER_63_5_MICROS;
        timerResetValue = new UnsignedWord(0);
        timerValue = new UnsignedWord(0);

        /* Disks */
        diskDriveSelect = 0;

        /* Initialize drive data */
        disk = new DiskDrive[NUM_DISK_DRIVES];
        for (int i = 0; i < NUM_DISK_DRIVES; i++) {
            disk[i] = new DiskDrive(this);
        }

        screen.setIOController(this);
    }

    /**
     * Creates a back-reference to the CPU.
     *
     * @param cpu the CPU the io controller controls
     */
    public void setCPU(CPU cpu) {
        this.cpu = cpu;
    }

    /**
     * Reads a byte from RAM, bypassing the MMU, and reading only from
     * the physical RAM array. This method should be used by devices
     * which need to read from RAM such as the screen.
     *
     * @param address the address into memory to read
     * @return the byte value read from the physical address
     */
    public UnsignedByte readPhysicalByte(int address) {
        return new UnsignedByte(memory.memory[address]);
    }

    /**
     * Reads an UnsignedByte from the specified address.
     *
     * @param address the UnsignedWord location to read from
     * @return an UnsignedByte from the specified location
     */
    public UnsignedByte readByte(UnsignedWord address) {
        int intAddress = address.getInt();
        if (intAddress < 0xFF00) {
            return memory.readByte(address);
        }
        return readIOByte(intAddress);
    }

    /**
     * Reads an IO byte from memory.
     *
     * @param address the address to read from
     * @return the IO byte read
     */
    public UnsignedByte readIOByte(int address) {
        switch (address) {
            /* PIA 1 Data Register A */
            case 0xFF00:
                /* Clear PIA 1 CRA bits 7 and 6 for interrupts */
                pia1CRA.and(~0xC0);
                return keyboard.getHighByte(pia1DRB);

            /* PIA 1 Control Register A */
            case 0xFF01:
                return pia1CRA;

            /* PIA 1 Data Register B */
            case 0xFF02:
                /* Clear PIA 1 CRB bits 7 and 6 for interrupts */
                pia1CRB.and(~0xC0);
                return pia1DRB;

            /* PIA 1 Control Register B */
            case 0xFF03:
                return pia1CRB;

            /* PIA 2 Data Register A */
            case 0xFF20:
                pia2DRA.and(0);

                /* Bit 0 = Cassette Data Input */
                pia2DRA.or(cassette.nextBit());
                return pia2DRA;

            /* PIA 2 Control Register A */
            case 0xFF21:
                return pia2CRA;

            /* PIA 2 Control Register B */
            case 0xFF23:
                return pia2CRB;

            /* Disk Drive Status Register */
            case 0xFF48:
                return disk[diskDriveSelect].getStatusRegister();

            /* Disk Track Status Register */
            case 0xFF49:
                return new UnsignedByte(disk[diskDriveSelect].getTrack());

            /* Disk Sector Status Register */
            case 0xFF4A:
                return new UnsignedByte(disk[diskDriveSelect].getSector());

            /* IRQs Enabled Register */
            case 0xFF92:
                return irqStatus;

            /* FIRQs Enabled Register */
            case 0xFF93:
                return firqStatus;

            /* Timer 1 */
            case 0xFF94:
                return timerResetValue.getHigh();

            /* Timer 0 */
            case 0xFF95:
                return timerResetValue.getLow();

            /* Vertical Offset Register 1 */
            case 0xFF9D:
                return verticalOffsetRegister.getHigh();

            /* Vertical Offset Register 0 */
            case 0xFF9E:
                return verticalOffsetRegister.getLow();

            /* Interrupt vectors */
            case 0xFFF2:
            case 0xFFF3:
            case 0xFFF4:
            case 0xFFF5:
            case 0xFFF6:
            case 0xFFF7:
            case 0xFFF8:
            case 0xFFF9:
            case 0xFFFA:
            case 0xFFFB:
            case 0xFFFC:
            case 0xFFFD:
            case 0xFFFE:
            case 0xFFFF:
                return memory.readROMByte(address);
        }

        return memory.readByte(new UnsignedWord(address));
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
        int intAddress = address.getInt();
        if (intAddress < 0xFF00) {
            memory.writeByte(address, value);
        } else {
            writeIOByte(address, value);
        }
    }

    /**
     * Writes an UnsignedByte to the specified memory address.
     *
     * @param address the address in IO space to write to
     * @param value the value to write
     */
    public void writeIOByte(UnsignedWord address, UnsignedByte value) {
        int intAddress = address.getInt() - 0xFF00;
        ioMemory[intAddress] = value.getShort();

        switch (address.getInt()) {
            /* PIA 1 Data Register A */
            case 0xFF00:
                pia1DRA = value.copy();
                break;

            /* PIA 1 Control Register A */
            case 0xFF01:
                /* Bit 0 = IRQ 63.5 microseconds */
                if (value.isMasked(0x1)) {
                    pia1FastTimerEnabled = true;
                    pia1FastTimer = 0;
                } else {
                    pia1FastTimerEnabled = false;
                    pia1FastTimer = 0;
                }

                /* Bit 1 = hi/lo edge trigger (ignored) */

                /* Bit 7 = IRQ triggered */

                pia1CRA = new UnsignedByte(value.getShort() +
                        (pia1CRA.isMasked(0x80) ? 0x80 : 0) +
                        (pia1CRA.isMasked(0x40) ? 0x40 : 0));
                break;

            /* PIA 1 Data Register B */
            case 0xFF02:
                pia1DRB = value.copy();
                break;

            /* PIA 1 Control Register B */
            case 0xFF03:
                /* Bit 0 = IRQ 16 milliseconds */
                if (value.isMasked(0x1)) {
                    pia1SlowTimerEnabled = true;
                    pia1SlowTimer = 0;
                } else {
                    pia1SlowTimerEnabled = false;
                    pia1SlowTimer = 0;
                }

                /* Bit 1 = hi/lo edge trigger (ignored) */

                /* Bit 7 = IRQ triggered */

                pia1CRB = new UnsignedByte(value.getShort() +
                        (pia1CRB.isMasked(0x80) ? 0x80 : 0) +
                        (pia1CRB.isMasked(0x40) ? 0x40 : 0));
                break;

            /* PIA 2 Data Register A */
            case 0xFF20:
                /* Bits 2-7 = digital analog values */
                cassette.byteInput(value);
                break;

            /* PIA 2 Control Register A */
            case 0xFF21:
                /* Bit 0 = FIRQ from serial I/O port */

                /* Bit 1 = hi/lo edge triggered */

                /* Bit 3 = Cassette Motor Control */
                if (value.isMasked(0x08)) {
                    cassette.motorOn();
                } else {
                    cassette.motorOff();
                }
                pia2CRA = value.copy();
                break;

            /* PIA 2 Control Register B */
            case 0xFF23:
                /* Bit 0 = FIRQ from cartridge ROM */

                /* Bit 1 = hi/lo edge triggered */

                pia2CRB = value.copy();
                break;


            /* Disk Drive Control Register */
            case 0xFF40:
                /* Bit 2-0 = Disk drive select */
                diskDriveSelect = (value.isMasked(0x1)) ? 0 : diskDriveSelect;
                diskDriveSelect = (value.isMasked(0x2)) ? 1 : diskDriveSelect;
                diskDriveSelect = (value.isMasked(0x4)) ? 2 : diskDriveSelect;

                /* Bit 6 = Disk drive select */
                diskDriveSelect = (value.isMasked(0x40)) ? 3 : diskDriveSelect;

                /* Bit 3 = Disk drive motor on */
                if (value.isMasked(0x08)) {
                    disk[diskDriveSelect].turnMotorOn();
                } else {
                    disk[diskDriveSelect].turnMotorOff();
                }

                /* Bit 4 = Write pre-compensation (ignored) */

                /* Bit 5 = Density flag (ignored) */

                /* Bit 7 = Halt flag */
                if (value.isMasked(0x80)) {
                    disk[diskDriveSelect].enableHalt();
                } else {
                    disk[diskDriveSelect].disableHalt();
                }
                break;

            /* Disk Command Register */
            case 0xFF48:
                disk[diskDriveSelect].executeCommand(value);
                break;

            /* Track Status Register */
            case 0xFF49:
                disk[diskDriveSelect].setTrack(value);
                break;

            /* Sector Status Register */
            case 0xFF4A:
                disk[diskDriveSelect].setSector(value);
                break;

            /* INIT 0 */
            case 0xFF90:
                /* Bit 1 & 0 = ROM memory mapping */
                memory.setROMMode(new UnsignedByte(value.getShort() & 0x3));

                /* Bit 4 = FIRQ - 0 disabled, 1 enabled */
                firqEnabled = value.isMasked(0x10);

                /* Bit 5 = IRQ - 0 disabled, 1 enabled */
                irqEnabled = value.isMasked(0x20);

                /* Bit 6 = MMU - disable or enable */
                if (value.isMasked(0x40)) {
                    memory.enableMMU();
                } else {
                    memory.disableMMU();
                }

                /* Bit 7 = CoCo Compatible Mode - enable or disable */
                cocoCompatibleMode = (value.isMasked(0x80));
                updateVerticalOffset();
                break;

            /* INIT 1 */
            case 0xFF91:
                /* Bit 0 = PAR selection - Task or Executive */
                if (value.isMasked(0x1)) {
                    memory.enableExecutivePAR();
                } else {
                    memory.enableTaskPAR();
                }

                /* Bit 5 = Timer Rate - 0 is 63.5 microseconds, 1 is 70 nanoseconds */
                timerTickThreshold = (value.isMasked(0x20)) ? TIMER_63_5_MICROS : TIMER_63_5_MICROS;
                break;

            /* IRQs Enabled Register */
            case 0xFF92:
                irqStatus = value.copy();
                irqStatus.and(0x3F);
                break;

            /* FIRQs Enabled Register */
            case 0xFF93:
                firqStatus = value.copy();
                firqStatus.and(0x3F);
                break;

            /* Timer 1 */
            case 0xFF94:
                timerResetValue.setHigh(value);
                timerResetValue.and(0x0FFF);
                timerValue.set(timerResetValue);
                break;

            /* Timer 0 */
            case 0xFF95:
                timerResetValue.setLow(value);
                timerValue.set(timerResetValue);
                break;

            /* Vertical Offset Register 1 */
            case 0xFF9D:
                verticalOffsetRegister.setHigh(value);
                updateVerticalOffset();
                break;

            /* Vertical Offset Register 0 */
            case 0xFF9E:
                verticalOffsetRegister.setLow(value);
                updateVerticalOffset();
                break;

            /* EXEC PAR 0 */
            case 0xFFA0:
                memory.setExecutivePAR(0, value);
                break;

            /* EXEC PAR 1 */
            case 0xFFA1:
                memory.setExecutivePAR(1, value);
                break;

            /* EXEC PAR 2 */
            case 0xFFA2:
                memory.setExecutivePAR(2, value);
                break;

            /* EXEC PAR 3 */
            case 0xFFA3:
                memory.setExecutivePAR(3, value);
                break;

            /* EXEC PAR 4 */
            case 0xFFA4:
                memory.setExecutivePAR(4, value);
                break;

            /* EXEC PAR 5 */
            case 0xFFA5:
                memory.setExecutivePAR(5, value);
                break;

            /* EXEC PAR 6 */
            case 0xFFA6:
                memory.setExecutivePAR(6, value);
                break;

            /* EXEC PAR 7 */
            case 0xFFA7:
                memory.setExecutivePAR(7, value);
                break;

            /* TASK PAR 0 */
            case 0xFFA8:
                memory.setTaskPAR(0, value);
                break;

            /* TASK PAR 1 */
            case 0xFFA9:
                memory.setTaskPAR(1, value);
                break;

            /* TASK PAR 2 */
            case 0xFFAA:
                memory.setTaskPAR(2, value);
                break;

            /* TASK PAR 3 */
            case 0xFFAB:
                memory.setTaskPAR(3, value);
                break;

            /* TASK PAR 4 */
            case 0xFFAC:
                memory.setTaskPAR(4, value);
                break;

            /* TASK PAR 5 */
            case 0xFFAD:
                memory.setTaskPAR(5, value);
                break;

            /* TASK PAR 6 */
            case 0xFFAE:
                memory.setTaskPAR(6, value);
                break;

            /* TASK PAR 7 */
            case 0xFFAF:
                memory.setTaskPAR(7, value);
                break;

            /* SAM - Display Offset Register - Bit 0 - Clear */
            case 0xFFC6:
                samDisplayOffsetRegister.and(~0x01);
                updateVerticalOffset();
                break;

            /* SAM - Display Offset Register - Bit 0 - Set */
            case 0xFFC7:
                samDisplayOffsetRegister.or(0x01);
                updateVerticalOffset();
                break;

            /* SAM - Display Offset Register - Bit 1 - Clear */
            case 0xFFC8:
                samDisplayOffsetRegister.and(~0x02);
                updateVerticalOffset();
                break;

            /* SAM - Display Offset Register - Bit 1 - Set */
            case 0xFFC9:
                samDisplayOffsetRegister.or(0x02);
                updateVerticalOffset();
                break;

            /* SAM - Display Offset Register - Bit 2 - Clear */
            case 0xFFCA:
                samDisplayOffsetRegister.and(~0x04);
                updateVerticalOffset();
                break;

            /* SAM - Display Offset Register - Bit 2 - Set */
            case 0xFFCB:
                samDisplayOffsetRegister.or(0x04);
                updateVerticalOffset();
                break;

            /* SAM - Display Offset Register - Bit 3 - Clear */
            case 0xFFCC:
                samDisplayOffsetRegister.and(~0x08);
                updateVerticalOffset();
                break;

            /* SAM - Display Offset Register - Bit 3 - Set */
            case 0xFFCD:
                samDisplayOffsetRegister.or(0x08);
                updateVerticalOffset();
                break;

            /* SAM - Display Offset Register - Bit 4 - Clear */
            case 0xFFCE:
                samDisplayOffsetRegister.and(~0x10);
                updateVerticalOffset();
                break;

            /* SAM - Display Offset Register - Bit 4 - Set */
            case 0xFFCF:
                samDisplayOffsetRegister.or(0x10);
                updateVerticalOffset();
                break;

            /* SAM - Display Offset Register - Bit 5 - Clear */
            case 0xFFD0:
                samDisplayOffsetRegister.and(~0x20);
                updateVerticalOffset();
                break;

            /* SAM - Display Offset Register - Bit 5 - Set */
            case 0xFFD1:
                samDisplayOffsetRegister.or(0x20);
                updateVerticalOffset();
                break;

            /* SAM - Display Offset Register - Bit 6 - Clear */
            case 0xFFD2:
                samDisplayOffsetRegister.and(~0x40);
                updateVerticalOffset();
                break;

            /* SAM - Display Offset Register - Bit 6 - Set */
            case 0xFFD3:
                samDisplayOffsetRegister.or(0x40);
                updateVerticalOffset();
                break;

            /* Clear SAM TY Bit - ROM/RAM mode */
            case 0xFFDE:
                memory.disableAllRAMMode();
                break;

            /* Set SAM TY Bit - all RAM mode */
            case 0xFFDF:
                memory.enableAllRAMMode();
                break;

            default:
                memory.writeByte(address, value);
                break;
        }
    }

    /**
     * Updates where in physical memory the screen should be read from. When
     * CoCo compatible mode is turned off, uses the verticalOffsetRegister to
     * set the physical address (shifted by 3 bits). When in CoCo compatible mode,
     * uses a combination of the vertical offset register plus the samDisplayRegister
     * to determine where in physical memory to read from. The SAM calculation is:
     *
     *   Vertical Offset = VO
     *   Sam Display Register = SAM
     *
     * Offset = (VO15 VO14 VO13) * 64K + SAM * 512 + (VO5 VO4 VO3 VO2 VO1 VO0) * 8
     */
    public void updateVerticalOffset() {
        if (cocoCompatibleMode) {
            int newOffset = (verticalOffsetRegister.getHigh().getShort() & 0xE0);
            newOffset = newOffset >> 5;
            newOffset *= 65536;
            newOffset += (samDisplayOffsetRegister.getShort() * 0x200);
            newOffset += ((verticalOffsetRegister.getLow().getShort() & 0x3F) * 0x04);
            screen.setMemoryOffset(newOffset);
        } else {
            int newOffset = verticalOffsetRegister.getInt() << 3;
            screen.setMemoryOffset(newOffset);
        }
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

    /**
     * Pushes the specified byte onto the specified stack. Will decrement the
     * stack pointer prior to performing the push.
     *
     * @param register the stack to use
     * @param value the value to push
     */
    public void pushStack(Register register, UnsignedByte value) {
        if (register == Register.S) {
            regs.getS().add(-1);
            writeByte(regs.getS(), value);
        } else {
            regs.getU().add(-1);
            writeByte(regs.getU(), value);
        }
    }

    /**
     * Pushes the specified word onto the specified stack. Will decrement the
     * stack pointer prior to performing the push.
     *
     * @param register the stack to use
     * @param value the value to push
     */
    public void pushStack(Register register, UnsignedWord value) {
        pushStack(register, value.getLow());
        pushStack(register, value.getHigh());
    }

    /**
     * Pops a byte off of a stack. Will increment the stack pointer after
     * performing the pop.
     *
     * @param register the stack to use
     * @return the value popped
     */
    public UnsignedByte popStack(Register register) {
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
     * Reads a single byte at the current value that is the program
     * counter. Will store the byte in the high byte of the resultant
     * word.
     *
     * @return a MemoryResult with the data from the PC location
     */
    public MemoryResult getImmediateByte() {
        UnsignedByte theByte = readByte(regs.getPC());
        regs.incrementPC();
        return new MemoryResult(
                1,
                new UnsignedWord(theByte, new UnsignedByte())
        );
    }

    /**
     * Given the current registers, will return the value that is
     * pointed to by the program counter.
     *
     * @return a MemoryResult with the data from the PC location
     */
    public MemoryResult getImmediateWord() {
        UnsignedWord theWord = readWord(regs.getPC());
        regs.incrementPC();
        regs.incrementPC();
        return new MemoryResult(
                2,
                theWord
        );
    }

    /**
     * Given the current registers, will return an UnsignedWord from
     * the memory location of the direct pointer as the setHigh byte,
     * and the setLow byte pointed to by the PC.
     *
     * @return a MemoryResult with the data from the DP:PC location
     */
    public MemoryResult getDirect() {
        UnsignedByte lowByte = readByte(regs.getPC());
        regs.incrementPC();
        return new MemoryResult(
                1,
                new UnsignedWord(regs.getDP(), lowByte)
        );
    }

    /**
     * Returns the register based on the post byte code.
     *
     * @param postByte the post byte to decode
     * @return the specified register, UNKNOWN if not a valid code
     */
    public Register getIndexedRegister(UnsignedByte postByte) {
        int value = postByte.getShort();
        value &= 0x60;
        value = value >> 5;

        switch (value) {
            case 0x0:
                return Register.X;

            case 0x1:
                return Register.Y;

            case 0x2:
                return Register.U;

            case 0x3:
                return Register.S;
        }
        return Register.UNKNOWN;
    }

    /**
     * The getIndexed function reads the byte following the current PC word, and
     * interprets the byte. Depending on the value of the byte, a new value is
     * returned. May throw an IllegalIndexedPostbyteException.
     *
     * @return a new MemoryResult with the indexed value
     */
    public MemoryResult getIndexed() throws IllegalIndexedPostbyteException {
        UnsignedByte postByte = readByte(regs.getPC());
        regs.incrementPC();
        UnsignedWord r;
        UnsignedByte a;
        UnsignedByte b;
        UnsignedWord d;
        UnsignedByte nByte;
        UnsignedWord nWord;
        UnsignedWord result;

        /* 5-bit offset - check for signed values */
        if (!postByte.isMasked(0x80)) {
            r = getWordRegister(getIndexedRegister(postByte));
            UnsignedByte offset = new UnsignedByte(postByte.getShort() & 0x1F);
            if (offset.isMasked(0x10)) {
                offset.and(0xF);
                UnsignedByte newOffset = offset.twosCompliment();
                newOffset.and(0xF);
                result = new UnsignedWord(r.getInt() - newOffset.getShort());
            } else {
                result = new UnsignedWord(r.getInt() + offset.getShort());
            }
            return new MemoryResult(1, result);
        }

        switch (postByte.getShort() & 0x1F) {
            /* ,R+ -> R, then increment R */
            case 0x00:
                r = getWordRegister(getIndexedRegister(postByte));
                result = new UnsignedWord(r.getInt());
                r.add(1);
                return new MemoryResult(1, result);

            /* ,R++ -> R, then increment R by two */
            case 0x01:
                r = getWordRegister(getIndexedRegister(postByte));
                result = new UnsignedWord(r.getInt());
                r.add(2);
                return new MemoryResult(1, result);

            /* ,-R -> Decrement R, then R */
            case 0x02:
                r = getWordRegister(getIndexedRegister(postByte));
                r.add(-1);
                result = new UnsignedWord(r.getInt());
                return new MemoryResult(1, result);

            /* ,--R -> Decrement R by two, then R */
            case 0x03:
                r = getWordRegister(getIndexedRegister(postByte));
                r.add(-2);
                result = new UnsignedWord(r.getInt());
                return new MemoryResult(1, result);

            /* ,R -> No offset, just R */
            case 0x04:
                r = getWordRegister(getIndexedRegister(postByte));
                return new MemoryResult(1, r.copy());

            /* B,R -> B offset from R */
            case 0x05:
                r = getWordRegister(getIndexedRegister(postByte));
                b = getByteRegister(Register.B);
                result = new UnsignedWord(r.getInt() + b.getSignedShort());
                return new MemoryResult(1, result);

            /* A,R -> A offset from R */
            case 0x06:
                r = getWordRegister(getIndexedRegister(postByte));
                a = getByteRegister(Register.A);
                result = new UnsignedWord(r.getInt() + a.getSignedShort());
                return new MemoryResult(1, result);

            /* n,R -> 8-bit offset from R */
            case 0x08:
                r = getWordRegister(getIndexedRegister(postByte));
                nByte = readByte(regs.getPC());
                regs.incrementPC();
                result = new UnsignedWord(r.getInt() + nByte.getSignedShort());
                return new MemoryResult(2, result);

            /* n,R -> 16-bit offset from R */
            case 0x09:
                r = getWordRegister(getIndexedRegister(postByte));
                nWord = readWord(regs.getPC());
                regs.incrementPC();
                regs.incrementPC();
                result = new UnsignedWord(r.getInt() + nWord.getSignedInt());
                return new MemoryResult(3, result);

            /* D,R -> D offset from R */
            case 0x0B:
                r = getWordRegister(getIndexedRegister(postByte));
                d = getWordRegister(Register.D);
                result = new UnsignedWord(r.getInt() + d.getSignedInt());
                return new MemoryResult(1, result);

            /* n,PC -> 8-bit offset from PC */
            case 0x0C:
                r = getWordRegister(Register.PC);
                nByte = readByte(regs.getPC());
                regs.incrementPC();
                result = new UnsignedWord(r.getInt() + nByte.getSignedShort());
                return new MemoryResult(2, result);

            /* n,PC -> 16-bit offset from PC */
            case 0x0D:
                r = getWordRegister(Register.PC);
                nWord = readWord(regs.getPC());
                regs.incrementPC();
                regs.incrementPC();
                result = new UnsignedWord(r.getInt() + nWord.getSignedInt());
                return new MemoryResult(3, result);

            /* [,R++] -> R, then increment R by two - indirect*/
            case 0x11:
                r = getWordRegister(getIndexedRegister(postByte));
                result = readWord(new UnsignedWord(r.getInt()));
                r.add(2);
                return new MemoryResult(1, result);

            /* [,--R] -> Decrement R by two, then R - indirect*/
            case 0x13:
                r = getWordRegister(getIndexedRegister(postByte));
                r.add(-2);
                result = readWord(new UnsignedWord(r.getInt()));
                return new MemoryResult(1, result);

            /* [,R] -> No offset, just R - indirect */
            case 0x14:
                r = readWord(getWordRegister(getIndexedRegister(postByte)));
                return new MemoryResult(1, r);

            /* [B,R] -> B offset from R - indirect */
            case 0x15:
                r = getWordRegister(getIndexedRegister(postByte));
                b = getByteRegister(Register.B);
                result = readWord(new UnsignedWord(r.getInt() + b.getSignedShort()));
                return new MemoryResult(1, result);

            /* [A,R] -> A offset from R - indirect */
            case 0x16:
                r = getWordRegister(getIndexedRegister(postByte));
                a = getByteRegister(Register.A);
                result = readWord(new UnsignedWord(r.getInt() + a.getSignedShort()));
                return new MemoryResult(1, result);

            /* [n,R] -> 8-bit offset from R - indirect */
            case 0x18:
                r = getWordRegister(getIndexedRegister(postByte));
                nByte = readByte(regs.getPC());
                regs.incrementPC();
                result = readWord(new UnsignedWord(r.getInt() + nByte.getSignedShort()));
                return new MemoryResult(2, result);

            /* [n,R] -> 16-bit offset from R - indirect */
            case 0x19:
                r = getWordRegister(getIndexedRegister(postByte));
                nWord = readWord(regs.getPC());
                regs.incrementPC();
                regs.incrementPC();
                result = readWord(new UnsignedWord(r.getInt() + nWord.getSignedInt()));
                return new MemoryResult(3, result);

            /* [D,R] -> D offset from R - indirect*/
            case 0x1B:
                r = getWordRegister(getIndexedRegister(postByte));
                d = getWordRegister(Register.D);
                result = readWord(new UnsignedWord(r.getInt() + d.getSignedInt()));
                return new MemoryResult(1, result);

            /* [n,PC] -> 8-bit offset from PC - indirect */
            case 0x1C:
                r = getWordRegister(Register.PC);
                nByte = readByte(regs.getPC());
                regs.incrementPC();
                result = readWord(new UnsignedWord(r.getInt() + nByte.getSignedShort()));
                return new MemoryResult(2, result);

            /* [n,PC] -> 16-bit offset from PC - indirect */
            case 0x1D:
                r = getWordRegister(Register.PC);
                nWord = readWord(regs.getPC());
                regs.incrementPC();
                regs.incrementPC();
                result = readWord(new UnsignedWord(r.getInt() + nWord.getSignedInt()));
                return new MemoryResult(3, result);

            /* [n] -> extended indirect */
            case 0x1F:
                nWord = readWord(regs.getPC());
                regs.incrementPC();
                regs.incrementPC();
                result = readWord(nWord);
                return new MemoryResult(3, result);
        }

        throw new IllegalIndexedPostbyteException(postByte);
    }

    /**
     * Given the current registers, will return the value that is
     * pointed to by the value that is pointed to by the program
     * counter value.
     *
     * @return a MemoryResult with the data from the PC location
     */
    public MemoryResult getExtended() {
        UnsignedWord address = readWord(regs.getPC());
        regs.incrementPC();
        regs.incrementPC();
        return new MemoryResult(
                2,
                address
        );
    }

    /**
     * Returns the byte that immediately available by the program counter.
     *
     * @return the current program counter byte
     */
    public UnsignedByte getPCByte() {
        return readByte(regs.getPC());
    }

    /**
     * Increments the Program Counter by 1 byte.
     */
    public void incrementPC() {
        regs.incrementPC();
    }

    /**
     * Returns the Condition Code register.
     *
     * @return the Condition Code register
     */
    public UnsignedByte getCC() {
        return regs.getCC();
    }

    /**
     * Gets the byte register of the specified type.
     *
     * @param register the register to get
     * @return the register
     */
    public UnsignedByte getByteRegister(Register register) {
        switch (register) {
            case A:
                return regs.getA();

            case B:
                return regs.getB();

            case DP:
                return regs.getDP();

            case CC:
                return regs.getCC();
        }

        return null;
    }

    /**
     * Gets the register of the specified type.
     *
     * @param register the register to get
     * @return the register
     */
    public UnsignedWord getWordRegister(Register register) {
        switch (register) {
            case Y:
                return regs.getY();

            case X:
                return regs.getX();

            case S:
                return regs.getS();

            case U:
                return regs.getU();

            case D:
                return regs.getD();

            case PC:
                return regs.getPC();
        }

        return null;
    }

    /**
     * Returns true if the carry flag is set on the condition code register,
     * false otherwise.
     *
     * @return true if the carry flag is set
     */
    public boolean ccCarrySet() {
        return regs.getCC().isMasked(CC_C);
    }

    /**
     * Sets the carry flag on the condition code register.
     */
    public void setCCCarry() {
        regs.getCC().or(CC_C);
    }

    public boolean ccOverflowSet() {
        return regs.getCC().isMasked(CC_V);
    }

    public void setCCOverflow() {
        regs.getCC().or(CC_V);
    }

    public boolean ccZeroSet() {
        return regs.getCC().isMasked(CC_Z);
    }

    public void setCCZero() {
        regs.getCC().or(CC_Z);
    }

    public boolean ccNegativeSet() {
        return regs.getCC().isMasked(CC_N);
    }

    public void setCCNegative() {
        regs.getCC().or(CC_N);
    }

    public boolean ccInterruptSet() {
        return regs.getCC().isMasked(CC_I);
    }

    public void setCCInterrupt() {
        regs.getCC().or(CC_I);
    }

    public boolean ccHalfCarrySet() {
        return regs.getCC().isMasked(CC_H);
    }

    public void setCCHalfCarry() {
        regs.getCC().or(CC_H);
    }

    public boolean ccFastInterruptSet() {
        return regs.getCC().isMasked(CC_F);
    }

    public void setCCFastInterrupt() {
        regs.getCC().or(CC_F);
    }

    public void setCCEverything() {
        regs.getCC().or(CC_E);
    }

    public boolean ccEverythingSet() {
        return regs.getCC().isMasked(CC_E);
    }

    /**
     * Performs a binary add of the two values, setting flags on the condition
     * code register where required.
     *
     * @param val1 the first value to add
     * @param val2 the second value to add
     * @param flagHalfCarry whether to flag half carries
     * @param flagCarry whether to flag full carries
     * @param flagOverflow whether to flag overflow
     *
     * @return the addition of the two values
     */
    public UnsignedWord binaryAdd(UnsignedWord val1, UnsignedWord val2,
                                  boolean flagHalfCarry, boolean flagCarry,
                                  boolean flagOverflow) {
        int value1 = val1.getInt();
        int value2 = val2.getInt();

        /* Check to see if a half carry occurred and we should flag it */
        if (flagHalfCarry) {
            UnsignedWord test = new UnsignedWord(value1 & 0xF);
            test.add(value2 & 0xF);
            if (test.isMasked(0x10)) {
                setCCHalfCarry();
            }
        }

        /* Check to see if a full carry occurred and we should flag it */
        if (flagCarry) {
            if (((value1 + value2) & 0x10000) > 0) {
                setCCCarry();
            }
        }

        /* Check to see if overflow occurred and we should flag it */
        if (flagOverflow) {
            int signedResult = val1.getSignedInt() + val2.getSignedInt();
            if (signedResult > 32767 || signedResult < -32767) {
                setCCOverflow();
            }
        }

        return new UnsignedWord(value1 + value2);
    }

    /**
     * Performs a binary add of the two values, setting flags on the condition
     * code register where required.
     *
     * @param val1 the first value to add
     * @param val2 the second value to add
     * @param flagHalfCarry whether to flag half carries
     * @param flagCarry whether to flag full carries
     * @param flagOverflow whether to flag overflow
     *
     * @return the addition of the two values
     */
    public UnsignedByte binaryAdd(UnsignedByte val1, UnsignedByte val2,
                                  boolean flagHalfCarry, boolean flagCarry,
                                  boolean flagOverflow) {
        int value1 = val1.getShort();
        int value2 = val2.getShort();

        /* Check for half carries */
        if (flagHalfCarry) {
            UnsignedByte test = new UnsignedByte(value1 & 0xF);
            test.add(value2 & 0xF);
            if (test.isMasked(0x10)) {
                setCCHalfCarry();
            }
        }

        /* Check for full carries */
        if (flagCarry) {
            if (((value1 + value2) & 0x100) > 0) {
                setCCCarry();
            }
        }

        /* Check for overflow */
        if (flagOverflow) {
            int signedResult = val1.getSignedShort() + val2.getSignedShort();
            if (signedResult > 127 || signedResult < -127) {
                setCCOverflow();
            }
        }

        return new UnsignedByte(value1 + value2);
    }

    /**
     * Sets the value of the A register.
     *
     * @param a the new value of the A register
     */
    public void setA(UnsignedByte a) {
        regs.setA(a);
    }

    /**
     * Sets the value of the B register.
     *
     * @param b the new value of the B register
     */
    public void setB(UnsignedByte b) {
        regs.setB(b);
    }

    public void setCC(UnsignedByte cc) {
        regs.setCC(cc);
    }

    public void setDP(UnsignedByte dp) {
        regs.setDP(dp);
    }

    public void setX(UnsignedWord x) {
        regs.setX(x);
    }

    public void setY(UnsignedWord y) {
        regs.setY(y);
    }

    public void setU(UnsignedWord u) {
        regs.setU(u);
    }

    public void setS(UnsignedWord s) {
        regs.setS(s);
    }

    public void setD(UnsignedWord d) {
        regs.setD(d);
    }

    public void setPC(UnsignedWord pc) {
        regs.setPC(pc);
    }

    /**
     * Resets the computer state.
     */
    public void reset() {
        /* Reset Condition Code register */
        regs.setCC(new UnsignedByte(0));
        regs.cc.or(IOController.CC_I);
        regs.cc.or(IOController.CC_F);

        /* Disable MMU */
        memory.disableMMU();

        /* Set ROM/RAM mode */
        memory.setROMMode(new UnsignedByte(0x2));
        memory.disableAllRAMMode();

        /* Load PC with Reset Interrupt Vector */
        regs.setPC(readWord(new UnsignedWord(0xFFFE)));
    }

    /**
     * Increments the timer by the specified number of ticks. If
     * the number of ticks exceeds the threshold for when the timer
     * should be decremented, will decrement the timer value by 1
     * and reset the tick counter.
     *
     * @param ticks the number of ticks to increment
     */
    public void timerTick(int ticks) {
        timerTickCounter += ticks;
        horizontalBorderTickValue += ticks;
        verticalBorderTickValue += ticks;

        /* Check for old interrupts via PIAs */
        if (pia1FastTimerEnabled) {
            pia1FastTimer += ticks;
            if (pia1FastTimer >= TIMER_63_5_MICROS) {
                if (!ccInterruptSet() && !pia1CRA.isMasked(0x80)) {
                    cpu.interruptRequest();
                }
                pia1CRA.or(0x80);
                pia1FastTimer = 0;
            }
        }

        if (pia1SlowTimerEnabled) {
            pia1SlowTimer += ticks;
            if (pia1SlowTimer >= TIMER_16_6_MILLIS) {
                if (!ccInterruptSet() && !pia1CRB.isMasked(0x80)) {
                    cpu.interruptRequest();
                }
                pia1CRB.or(0x80);
                pia1SlowTimer = 0;
            }
        }

        /* Check for GIME timer related interrupts */
        if (timerTickCounter >= timerTickThreshold) {
            timerTickCounter = 0;
            timerValue.add(-1);
            if (timerValue.isZero()) {
                if (irqEnabled && irqStatus.isMasked(0x20)) {
                    cpu.interruptRequest();
                }
                if (firqEnabled && firqStatus.isMasked(0x20)) {
                    cpu.fastInterruptRequest();
                }
                timerValue.set(timerResetValue);
            }
        }

        /* Check for GIME horizontal border related interrupts */
        if (horizontalBorderTickValue >= TIMER_63_5_MICROS) {
            horizontalBorderTickValue = 0;
            if (irqEnabled && irqStatus.isMasked(0x10)) {
                cpu.interruptRequest();
            }
            if (firqEnabled && firqStatus.isMasked(0x10)) {
                cpu.fastInterruptRequest();
            }
        }

        /* Check for GIME vertical border related interrupts */
        if (verticalBorderTickValue >= TIMER_16_6_MILLIS) {
            verticalBorderTickValue = 0;
            if (irqEnabled && irqStatus.isMasked(0x08)) {
                cpu.interruptRequest();
            }
            if (firqEnabled && firqStatus.isMasked(0x08)) {
                cpu.fastInterruptRequest();
            }
        }
    }

    /**
     * Fires a non-maskable interrupt on the CPU.
     */
    public void nonMaskableInterrupt() {
        cpu.nonMaskableInterruptRequest();
    }
}
