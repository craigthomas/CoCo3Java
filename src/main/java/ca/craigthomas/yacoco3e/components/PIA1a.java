/*
 * Copyright (C) 2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.RegisterSet;
import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.CC_I;

public class PIA1a extends PIA
{
    protected Keyboard keyboard;
    protected DeviceSelectorSwitch deviceSelectorSwitch;
    protected int timerValue;

    public PIA1a(Keyboard newKeyboard, DeviceSelectorSwitch newDeviceSelectorSwitch) {
        super();
        keyboard = newKeyboard;
        timerValue = 0;
        deviceSelectorSwitch = newDeviceSelectorSwitch;
    }

    /**
     * In PIA 1 side A, the data register is connected to the keyboard. The high
     * byte pattern of the keyboard is returned as the contents of the data register.
     *
     * @return the high byte of the keyboard matrix
     */
    @Override
    public UnsignedByte getDataRegister() {
        return keyboard.getHighByte();
    }

    /**
     * In the Color Computer line of computers, PIA 1 side A is always configured for
     * input, not for output. Writing to the data register does nothing.
     *
     * @param newDataRegister the new value for the data register
     */
    @Override
    public void setDataRegister(UnsignedByte newDataRegister) {
    }

    /**
     * When the control register is written to, interrupts are potentially enabled,
     * and bits 7 and 6 are set.
     *
     * @param newControlRegister the new control register value
     */
    @Override
    public void setControlRegister(UnsignedByte newControlRegister) {
        controlRegister = new UnsignedByte(
                newControlRegister.get() +
                        (controlRegister.isMasked(0x80) ? 0x80 : 0) +
                        (controlRegister.isMasked(0x40) ? 0x40 : 0)
        );

        // Bit 5=1 - CA2 is an output
        if (controlRegister.isMasked(0x10)) {
            // Bit 4=1 - Output a signal directly on CA2 based on Bit 3 value
            if (controlRegister.isMasked(0x8)) {
                deviceSelectorSwitch.setCA2(controlRegister.isMasked(0x4));
            }
        }
    }

    /**
     * Adds the specified number of ticks to the timer.
     *
     * @param ticks the number of ticks to add to the timer
     * @param cpu the CPU on which to generate an interrupt
     * @param regs the RegisterSet for the CPU
     */
    public void addTicks(int ticks, CPU cpu, RegisterSet regs) {
        timerValue += ticks;
        if (timerValue >= IOController.TIMER_63_5_MICROS) {
            controlRegister.or(0x80);
            if (!regs.cc.isMasked(CC_I) && controlRegister.isMasked(0x01)) {
                cpu.scheduleIRQ();
            }
            timerValue = 0;
        }
    }
}
