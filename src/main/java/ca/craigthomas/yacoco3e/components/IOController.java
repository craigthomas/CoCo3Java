/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;
import ca.craigthomas.yacoco3e.datatypes.screen.ScreenMode.Mode;

import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;

public class IOController
{
    /* The number of IO memory addresses */
    public static final int IO_ADDRESS_SIZE = 256;

    /* The IO memory address space */
    protected short [] ioMemory;

    /* IO Devices */
    protected Memory memory;
    public RegisterSet regs;
    protected Keyboard keyboard;
    protected Screen screen;
    protected Cassette cassette;
    protected CPU cpu;
    protected DiskDrive [] disk;

    /* Disk Drive Selector */
    protected int diskDriveSelect;

    public static final int NUM_DISK_DRIVES = 4;

    /* The display resolution - 1 = old CoCo resolutions active, 0 = CoCo3 high resolutions active */
    protected boolean lowResolutionDisplayActive;

    /* Vertical Offset Registers */
    protected UnsignedByte verticalOffsetRegister0;
    protected UnsignedByte verticalOffsetRegister1;

    /* SAM Display Offset Register */
    protected UnsignedByte samDisplayOffsetRegister;

    /* Video Mode Register */
    protected UnsignedByte videoModeRegister;

    /* Video Resolution Register */
    protected UnsignedByte videoResolutionRegister;

    /* Border Color Register */
    protected UnsignedByte borderRegister;

    /* PIA1 */
    protected PIA1a pia1a;
    protected PIA1b pia1b;
    protected PIA2a pia2a;
    protected PIA2b pia2b;

    /* Device Selector */
    protected DeviceSelectorSwitch deviceSelectorSwitch;

    /* Video mode related functions */
    protected UnsignedByte samControlBits;

    /* GIME IRQ enabled / disabled */
    public boolean irqEnabled;

    /* GIME IRQ status */
    public UnsignedByte irqStatus;

    /* GIME FIRQ enabled / disabled */
    public boolean firqEnabled;

    /* GIME FIRQ status */
    public UnsignedByte firqStatus;

    /* Whether the CPU should wait for an interrupt request */
    public boolean waitForIRQ;

    /* The number of ticks to pass in 63.5 microseconds */
    public static final int TIMER_63_5_MICROS = 56;

    /* The number of ticks to pass in 16.6 milliseconds */
    public static final int TIMER_16_6_MILLIS = 14833;

    /* The number of ticks to pass before poking disks */
    public static final int TIMER_DISK_COUNTER = 5000;

    /* The number of ticks that is allowed to be processed in 0.89MHz mode and 1.78 MHz mode */
    public static final int LOW_SPEED_CLOCK_FREQUENCY = 14917;
    public static final int HIGH_SPEED_CLOCK_FREQUENCY = 29834;
    public UnsignedByte samClockSpeed;

    /* The timer tick threshold */
    public int timerTickThreshold;

    /* The number of ticks that has passed for the timer */
    public int timerTickCounter;

    public int diskTickCounter;

    public UnsignedWord timerResetValue;

    public UnsignedWord timerValue;

    public int horizontalBorderTickValue;

    public int verticalBorderTickValue;

    public volatile int tickRefreshAmount;


    public IOController(Memory memory, RegisterSet registerSet, Keyboard keyboard, Screen screen, Cassette cassette, boolean useDAC) {
        ioMemory = new short[IO_ADDRESS_SIZE];
        this.memory = memory;
        this.regs = registerSet;
        this.keyboard = keyboard;
        this.lowResolutionDisplayActive = false;
        this.screen = screen;
        this.cassette = cassette;

        /* Screen controls */
        samControlBits = new UnsignedByte();

        /* Device Selector */
        deviceSelectorSwitch = new DeviceSelectorSwitch();

        /* PIAs */
        pia1a = new PIA1a(keyboard, deviceSelectorSwitch);
        pia1b = new PIA1b(keyboard, deviceSelectorSwitch);
        pia2a = new PIA2a(cassette, useDAC);
        pia2b = new PIA2b(this);

        /* Display registers */
        verticalOffsetRegister1 = new UnsignedByte(0x04);
        verticalOffsetRegister0 = new UnsignedByte(0x00);
        samDisplayOffsetRegister = new UnsignedByte(0x00);

        /* CoCo3 specific video registers */
        videoModeRegister = new UnsignedByte(0x00);
        videoResolutionRegister = new UnsignedByte(0x00);
        borderRegister = new UnsignedByte(0x00);

        /* Interrupts */
        irqStatus = new UnsignedByte();
        firqStatus = new UnsignedByte();

        /* Timer related values */
        timerTickThreshold = TIMER_63_5_MICROS;
        timerResetValue = new UnsignedWord();
        timerValue = new UnsignedWord();

        /* Disks */
        diskDriveSelect = 0;

        /* Initialize drive data */
        disk = new DiskDrive[NUM_DISK_DRIVES];
        for (int i = 0; i < NUM_DISK_DRIVES; i++) {
            disk[i] = new DiskDrive(this);
        }

        screen.setIOController(this);

        samClockSpeed = new UnsignedByte();
        tickRefreshAmount = LOW_SPEED_CLOCK_FREQUENCY;
        waitForIRQ = false;
    }

    /**
     * Loads the drive with the contents of the virtual disk.
     *
     * @param diskDriveNum the disk drive number to load into
     * @param virtualDisk the virtual disk contents to load
     */
    public void loadVirtualDisk(int diskDriveNum, VirtualDisk virtualDisk) {
        disk[diskDriveNum].loadFromVirtualDisk(virtualDisk);
    }

    /**
     * Saves the data in the selected drive number to the virtual disk interface
     * specified.
     *
     * @param diskDriveNum the drive number to save
     * @param virtualDisk the virtual disk image to save into
     * @return the virtual disk with the drive contents
     */
    public VirtualDisk saveVirtualDisk(int diskDriveNum, VirtualDisk virtualDisk) {
        return disk[diskDriveNum].convertToVirtualDisk(virtualDisk);
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
     * Creates a back-reference to the Keyboard.
     *
     * @param keyboard the new keyboard the io controller will interface with
     */
    public void setKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
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
     * Convenience function allowing to read a byte using an integer address.
     *
     * @param address the address to read from
     * @return the UnsignedByte at that address
     */
    public UnsignedByte readByte(int address) {
        return readByte(new UnsignedWord(address));
    }

    /**
     * Reads an UnsignedByte from the specified address.
     *
     * @param address the UnsignedWord location to read from
     * @return an UnsignedByte from the specified location
     */
    public UnsignedByte readByte(UnsignedWord address) {
        int intAddress = address.get();
        if (intAddress < 0xFF00) {
            return memory.readByte(address).copy();
        }
        return readIOByte(intAddress);
    }

    /**
     * This function periodically updates the status of the disk drives
     * so that they do not get stuck in a single command. This should be
     * called periodically by the CPU to ensure the drives don't get
     * stale in a read or write operation that the computer does not
     * service (e.g. Read Address in Tandy Disk Basic 1.1).
     */
    public void pokeDisks() {
        for (int i = 0; i < NUM_DISK_DRIVES; i++) {
            disk[i].tickUpdate();
        }
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
            case 0xFF04:
            case 0xFF08:
            case 0xFF0C:
            case 0xFF10:
            case 0xFF14:
            case 0xFF18:
            case 0xFF1C:
                return pia1a.getRegister();

            /* PIA 1 Control Register A */
            case 0xFF01:
            case 0xFF05:
            case 0xFF09:
            case 0xFF0D:
            case 0xFF11:
            case 0xFF15:
            case 0xFF19:
            case 0xFF1D:
                return pia1a.getControlRegister();

            /* PIA 1 Data Register B */
            case 0xFF02:
            case 0xFF06:
            case 0xFF0A:
            case 0xFF0E:
            case 0xFF12:
            case 0xFF16:
            case 0xFF1A:
            case 0xFF1E:
                return pia1b.getRegister();

            /* PIA 1 Control Register B */
            case 0xFF03:
            case 0xFF07:
            case 0xFF0B:
            case 0xFF0F:
            case 0xFF13:
            case 0xFF17:
            case 0xFF1B:
            case 0xFF1F:
                return pia1b.getControlRegister();

            /* PIA 2 Data Register A */
            case 0xFF20:
            case 0xFF24:
            case 0xFF28:
            case 0xFF2C:
            case 0xFF30:
            case 0xFF34:
            case 0xFF38:
            case 0xFF3C:
                return pia2a.getRegister();

            /* PIA 2 Control Register A */
            case 0xFF21:
            case 0xFF25:
            case 0xFF29:
            case 0xFF2D:
            case 0xFF31:
            case 0xFF35:
            case 0xFF39:
            case 0xFF3D:
                return pia2a.getControlRegister();

            /* PIA 2 Data Register B */
            case 0xFF22:
            case 0xFF2A:
            case 0xFF2E:
            case 0xFF32:
            case 0xFF36:
            case 0xFF3A:
            case 0xFF3E:
                return pia2b.getRegister();

            /* PIA 2 Control Register B */
            case 0xFF23:
            case 0xFF2B:
            case 0xFF2F:
            case 0xFF33:
            case 0xFF37:
            case 0xFF3B:
            case 0xFF3F:
                return pia2b.getControlRegister();

            /* Disk Drive Status Register */
            case 0xFF48:
                return disk[diskDriveSelect].getStatusRegister();

            /* Disk Track Status Register */
            case 0xFF49:
                return new UnsignedByte(disk[diskDriveSelect].getTrack());

            /* Disk Sector Status Register */
            case 0xFF4A:
                return new UnsignedByte(disk[diskDriveSelect].getSector());

            /* Disk Data Register */
            case 0xFF4B:
                return new UnsignedByte(disk[diskDriveSelect].getDataRegister());

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

            /* Video Mode Register */
            case 0xFF98:
                return videoModeRegister.copy();

            /* Video Resolution */
            case 0xFF99:
                return videoResolutionRegister.copy();

            /* Border Color Register */
            case 0xFF9A:
                return borderRegister.copy();

            /* Vertical Offset Register 1 */
            case 0xFF9D:
                return verticalOffsetRegister1.copy();

            /* Vertical Offset Register 0 */
            case 0xFF9E:
                return verticalOffsetRegister0.copy();

            /* SAM Clock Speed R1 */
            case 0xFFD8:
            case 0xFFD9:
                return new UnsignedByte();

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

            default:
                return memory.readByte(new UnsignedWord(address));
        }
    }

    public UnsignedWord readWord(int address) {
        return readWord(new UnsignedWord(address));
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
     * Convenience function allowing to write a byte using an integer
     * address and an integer value.
     *
     * @param address the address to write to
     * @param value the value to write
     */
    public void writeByte(int address, int value) {
        writeByte(new UnsignedWord(address), new UnsignedByte(value));
    }

    /**
     * Writes an UnsignedByte to the specified memory address.
     *
     * @param address the UnsignedWord location to write to
     * @param value the UnsignedByte to write
     */
    public void writeByte(UnsignedWord address, UnsignedByte value) {
        int intAddress = address.get();
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
        int intAddress = address.get() - 0xFF00;
        ioMemory[intAddress] = value.get();

        switch (address.get()) {
            /* PIA 1 Data Register A */
            case 0xFF00:
            case 0xFF04:
            case 0xFF08:
            case 0xFF0C:
            case 0xFF10:
            case 0xFF14:
            case 0xFF18:
            case 0xFF1C:
                pia1a.setRegister(value);
                break;

            /* PIA 1 Control Register A */
            case 0xFF01:
            case 0xFF05:
            case 0xFF09:
            case 0xFF0D:
            case 0xFF11:
            case 0xFF15:
            case 0xFF19:
            case 0xFF1D:
                pia1a.setControlRegister(value);
                break;

            /* PIA 1 Data Register B */
            case 0xFF02:
            case 0xFF06:
            case 0xFF0A:
            case 0xFF0E:
            case 0xFF12:
            case 0xFF16:
            case 0xFF1A:
            case 0xFF1E:
                pia1b.setRegister(value);
                break;

            /* PIA 1 Control Register B */
            case 0xFF03:
            case 0xFF07:
            case 0xFF0B:
            case 0xFF0F:
            case 0xFF13:
            case 0xFF17:
            case 0xFF1B:
            case 0xFF1F:
                pia1b.setControlRegister(value);
                break;

            /* PIA 2 Data Register A / Data Direction Register A */
            case 0xFF20:
            case 0xFF24:
            case 0xFF28:
            case 0xFF2C:
            case 0xFF30:
            case 0xFF34:
            case 0xFF38:
            case 0xFF3C:
                pia2a.setRegister(value);
                /* Bits 2-7 = digital analog values */
                // cassette.byteInput(value);
                break;

            /* PIA 2 Control Register A */
            case 0xFF21:
            case 0xFF25:
            case 0xFF29:
            case 0xFF2D:
            case 0xFF31:
            case 0xFF35:
            case 0xFF39:
            case 0xFF3D:
                pia2a.setControlRegister(value);
                break;

            /* PIA 2 Data Register B / Data Direction Register B */
            case 0xFF22:
            case 0xFF26:
            case 0xFF2A:
            case 0xFF2E:
            case 0xFF32:
            case 0xFF36:
            case 0xFF3A:
            case 0xFF3E:
                pia2b.setRegister(value);
                break;

            /* PIA 2 Control Register B */
            case 0xFF23:
            case 0xFF27:
            case 0xFF2B:
            case 0xFF2F:
            case 0xFF33:
            case 0xFF37:
            case 0xFF3B:
            case 0xFF3F:
                pia2b.setControlRegister(value);
                break;


            /* Disk Drive Control Register */
            case 0xFF40:
                /* Bit 2-0 = Disk drive select */
                diskDriveSelect = (value.isMasked(0x1)) ? 0 : diskDriveSelect;
                diskDriveSelect = (value.isMasked(0x2)) ? 1 : diskDriveSelect;
                diskDriveSelect = (value.isMasked(0x4)) ? 2 : diskDriveSelect;

                /* Bit 3 = Disk drive motor on */
                if (value.isMasked(0x08)) {
                    disk[diskDriveSelect].turnMotorOn();
                } else {
                    disk[diskDriveSelect].turnMotorOff();
                }

                /* Bit 4 = Write pre-compensation (ignored) */

                /* Bit 5 = Density flag (ignored) */

                /* Bit 6 = Disk drive select */
                diskDriveSelect = (value.isMasked(0x40)) ? 3 : diskDriveSelect;

                /* Bit 7 = Halt flag */
                if (value.isMasked(0x80)) {
                    disk[diskDriveSelect].enableHalt();
                } else {
                    disk[diskDriveSelect].disableHalt();
                }
                break;

            /* Disk Command Register */
            case 0xFF48:
            case 0xFF4C:
            case 0xFF58:
            case 0xFF5C:
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

            /* Disk Data Register */
            case 0xFF4B:
                disk[diskDriveSelect].setDataRegister(value);
                break;
                
            /* INIT 0 */
            case 0xFF90:
                /* Bit 1 & 0 = ROM memory mapping */
                memory.setROMMode(new UnsignedByte(value.get() & 0x3));

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

                /* Bit 7 = set - low resolution display active, clear - high resolution display active */
                lowResolutionDisplayActive = (value.isMasked(0x80));
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
                timerTickThreshold = TIMER_63_5_MICROS;
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

            /* Video Mode Register */
            case 0xFF98:
                videoModeRegister.set(value);
                break;

            /* Video Resolution Register */
            case 0xFF99:
                videoResolutionRegister.set(value);
                break;

            /* Border Color Register */
            case 0xFF9A:
                borderRegister.set(value);
                break;

            /* Vertical Offset Register 1 */
            case 0xFF9D:
                verticalOffsetRegister1.set(value);
                updateVerticalOffset();
                break;

            /* Vertical Offset Register 0 */
            case 0xFF9E:
                verticalOffsetRegister0.set(value);
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

            /* SAM - Video Display - V0 - Clear */
            case 0xFFC0:
                samControlBits.and(~0x1);
                updateVideoMode(pia2b.getVdgMode());
                break;

            /* SAM - Video Display - V0 - Set */
            case 0xFFC1:
                samControlBits.or(0x1);
                updateVideoMode(pia2b.getVdgMode());
                break;

            /* SAM - Video Display - V1 - Clear */
            case 0xFFC2:
                samControlBits.and(~0x2);
                updateVideoMode(pia2b.getVdgMode());
                break;

            /* SAM - Video Display - V1 - Set */
            case 0xFFC3:
                samControlBits.or(0x2);
                updateVideoMode(pia2b.getVdgMode());
                break;

            /* SAM - Video Display - V2 - Clear */
            case 0xFFC4:
                samControlBits.and(~0x4);
                updateVideoMode(pia2b.getVdgMode());
                break;

            /* SAM - Video Display - V2 - Set */
            case 0xFFC5:
                samControlBits.or(0x4);
                updateVideoMode(pia2b.getVdgMode());
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

            /* Clear SAM R1 Bit - Clock Speed */
            case 0xFFD8:
                samClockSpeed.and(~0x2);
                updateClockSpeed();
                break;

            /* Set SAM R1 Bit - Clock Speed */
            case 0xFFD9:
                samClockSpeed.or(0x2);
                updateClockSpeed();
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
                break;
        }
    }

    /**
     * Updates where in physical memory the screen should be read from. When the low resolution
     * modes are active, the screen information is read from a combination of the vertical
     * offset registers (V1, V0), and the SAM display offset register (SAM). The offset is a
     * 19-bit offset value into physical memory. The low resolution offset is calculated as
     * follows:
     *
     * Offset = x x x  x x x x x x x  x x x x x x 0 0 0
     *          -----  -------------  -----------
     *     V1   7 6 5
     *    SAM          6 5 4 3 2 1 0
     *     V0                         5 4 3 2 1 0
     *
     *  When the high resolution displays are selected, the offset is still a 19-bit offset
     *  value into physical memory, but only the vertical offset registers (v1, v0) are used
     *  to calculate the offset. The high resolution offset is calculated as follows:
     *
     *  Offset = x x x x x x x x  x x x x x x x x 0 0 0
     *           ---------------  ---------------
     *      V1   7 6 5 4 3 2 1 0
     *      V0                    7 6 5 4 3 2 1 0
     */
    public void updateVerticalOffset() {
        int newOffset = 0;
        if (lowResolutionDisplayActive) {
            newOffset = ((verticalOffsetRegister1.get() & 0xE0) >> 5) << 16;
            newOffset += (samDisplayOffsetRegister.get() & 0x7F) << 9;
            newOffset += (verticalOffsetRegister0.get() & 0x3F) << 3;
        } else {
            newOffset = verticalOffsetRegister1.get() << 11;
            newOffset += verticalOffsetRegister0.get() << 3;
        }
        screen.setMemoryOffset(newOffset);
    }

    /**
     * Updates the number of ticks available for a tick refresh. When a CPU operation
     * occurs, the number of ticks it consumes are counted. This count is deducted from
     * a set number of ticks the CPU is allowed to execute every 60th of a second.
     * When the SAM R0 or SAM R1 bits are modified, the number of ticks available changes,
     * as the processor can be switched into 0.895 MHz mode or 1.78 MHz mode.
     */
    public void updateClockSpeed() {
        tickRefreshAmount = (samClockSpeed.isMasked(0x2)) ? HIGH_SPEED_CLOCK_FREQUENCY : LOW_SPEED_CLOCK_FREQUENCY;
    }

    /**
     * Sets the Video Display Generator operating mode based on the value of PIA2B
     * data register, plus the SAM control bits.
     *
     * @param vdgOperatingMode the Video Display Generator mode to set
     */
    public void updateVideoMode(UnsignedByte vdgOperatingMode) {
        Mode mode;
        int colorSet = vdgOperatingMode.isMasked(0x8) ? 1 : 0;

        if (vdgOperatingMode.isMasked(0x80)) {
            switch (samControlBits.get()) {
                case 0x1:
                    mode = vdgOperatingMode.isMasked(0x10) ? Mode.G1R : Mode.G1C;
                    break;

                case 0x2:
                    mode = Mode.G2C;
                    break;

                case 0x3:
                    mode = Mode.G2R;
                    break;

                case 0x4:
                    mode = Mode.G3C;
                    break;

                case 0x5:
                    mode = Mode.G3R;
                    break;

                case 0x6:
                    mode = vdgOperatingMode.isMasked(0x10) ? Mode.G6R : Mode.G6C;
                    break;

                default:
                    return;
            }
        } else {
            switch (samControlBits.get()) {
                case 0x0:
                    mode = vdgOperatingMode.isMasked(0x20) ? Mode.SG6 : Mode.SG4;
                    break;

                case 0x2:
                    mode = Mode.SG8;
                    break;

                case 0x4:
                    mode = Mode.SG12;
                    break;

                case 0x6:
                    mode = Mode.SG24;
                    break;

                default:
                    return;
            }
        }

        int memoryOffset = screen.getMemoryOffset();
        screen.setMode(mode, colorSet);
        screen.setMemoryOffset(memoryOffset);
    }

    /**
     * Convenience function allowing the address and values to be written
     * as integers instead of UnsignedWord objects.
     *
     * @param address the address to write to
     * @param value the value to write
     */
    public void writeWord(int address, int value) {
        writeWord(new UnsignedWord(address), new UnsignedWord(value));
    }

    /**
     * Convenience function allowing the address to be a word, while the
     * value to be an integer, instead of UnsignedWord objects.
     *
     * @param address the address to write to
     * @param value the value to write
     */
    public void writeWord(UnsignedWord address, int value) {
        writeWord(address, new UnsignedWord(value));
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
            regs.s.add(-1);
            writeByte(regs.s, value);
        } else {
            regs.u.add(-1);
            writeByte(regs.u, value);
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
            result.set(readByte(regs.s));
            regs.s.add(1);
        } else {
            result.set(readByte(regs.u));
            regs.u.add(1);
        }
        return result;
    }

    /**
     * Gets the register of the specified type.
     *
     * @param register the register to get
     * @return the register
     */
    public UnsignedWord getWordRegister(Register register) {
        return switch (register) {
            case Y -> regs.y;
            case X -> regs.x;
            case S -> regs.s;
            case U -> regs.u;
            case D -> regs.getD();
            case PC -> regs.pc;
            default -> null;
        };
    }

    /**
     * Returns the register based on the post byte code.
     *
     * @param postByte the post byte to decode
     * @return the specified register, UNKNOWN if not a valid code
     */
    public Register getIndexedRegister(UnsignedByte postByte) {
        int value = postByte.get();
        value &= 0x60;
        value = value >> 5;

        return switch (value) {
            case 0x0 -> Register.X;
            case 0x1 -> Register.Y;
            case 0x2 -> Register.U;
            case 0x3 -> Register.S;
            default -> Register.UNKNOWN;
        };
    }

    /**
     * Increments the Program Counter by 1 byte.
     */
    public void incrementPC() {
        regs.incrementPC();
    }

    /**
     * Resets the computer state.
     */
    public void reset() {
        /* Reset Condition Code register */
        regs.cc = new UnsignedByte(0);
        regs.cc.or(CC_I);
        regs.cc.or(CC_F);

        /* Disable MMU */
        memory.disableMMU();

        /* Set ROM/RAM mode */
        memory.setROMMode(new UnsignedByte(0x2));
        memory.disableAllRAMMode();

        /* Load PC with Reset Interrupt Vector */
        regs.pc = readWord(0xFFFE);
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
        diskTickCounter += ticks;
        timerTickCounter += ticks;
        horizontalBorderTickValue += ticks;
        verticalBorderTickValue += ticks;

        /* Check for old interrupts via PIAs */
        /* Increment pia1 FastTimer, trigger IRQ if interrupts on */
        pia1a.addTicks(ticks, cpu, regs);

        /* Increment pia1 SlowTimer, trigger IRQ if interrupts on */
        pia1b.addTicks(ticks, cpu, regs);

        /* Check for GIME timer related interrupts */
        if (timerTickCounter >= timerTickThreshold) {
            timerTickCounter = 0;
            timerValue.add(-1);
            if (timerValue.isZero()) {
                if (irqEnabled && irqStatus.isMasked(0x20)) {
                    cpu.scheduleIRQ();
                }
                if (firqEnabled && firqStatus.isMasked(0x20)) {
                    cpu.scheduleFIRQ();
                }
                timerValue.set(timerResetValue);
            }
        }

        /* Check for GIME horizontal border related interrupts */
        if (horizontalBorderTickValue >= TIMER_63_5_MICROS) {
            horizontalBorderTickValue = 0;
            if (irqEnabled && irqStatus.isMasked(0x10)) {
                cpu.scheduleIRQ();
            }
            if (firqEnabled && firqStatus.isMasked(0x10)) {
                cpu.scheduleFIRQ();
            }
        }

        /* Check for GIME vertical border related interrupts */
        if (verticalBorderTickValue >= TIMER_16_6_MILLIS) {
            verticalBorderTickValue = 0;
            if (irqEnabled && irqStatus.isMasked(0x08)) {
                cpu.scheduleIRQ();
            }
            if (firqEnabled && firqStatus.isMasked(0x08)) {
                cpu.scheduleFIRQ();
            }
        }

        /* Check to see if we should poke disks */
        if (diskTickCounter >= TIMER_DISK_COUNTER) {
            pokeDisks();
            diskTickCounter = 0;
        }
    }

    /**
     * Fires a non-maskable interrupt on the CPU.
     */
    public void nonMaskableInterrupt() {
        cpu.scheduleNMI();
    }

    public void shutdown() {
        if (pia2a != null) {
            pia2a.shutdown();
        }
    }
}
