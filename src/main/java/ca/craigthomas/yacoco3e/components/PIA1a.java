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
    protected boolean leftJoystickFire;
    protected float leftJoystickX;
    protected float leftJoystickY;
    protected boolean rightJoystickFire;
    protected float rightJoystickX;
    protected float rightJoystickY;
    protected PIA2a pia2a;

    public PIA1a(Keyboard newKeyboard, DeviceSelectorSwitch newDeviceSelectorSwitch, PIA2a pia2a) {
        super();
        keyboard = newKeyboard;
        timerValue = 0;
        deviceSelectorSwitch = newDeviceSelectorSwitch;
        leftJoystickFire = false;
        leftJoystickX = 2.25f;
        leftJoystickY = 2.25f;
        rightJoystickFire = false;
        rightJoystickX = 2.25f;
        rightJoystickY = 2.25f;
        this.pia2a = pia2a;
    }

    public void setLeftJoystickState(float x, float y, boolean fire) {
        leftJoystickFire = fire;
        leftJoystickX = x;
        leftJoystickY = y;
    }

    public void setRightJoystickState(float x, float y, boolean fire) {
        rightJoystickFire = fire;
        rightJoystickX = x;
        rightJoystickY = y;
    }

    /**
     * In PIA 1 side A, the data register is connected to the keyboard. The high
     * byte pattern of the keyboard is returned as the contents of the data register.
     * Also mix in the joystick fire buttons as appropriate.
     *
     * @return the high byte of the keyboard matrix
     */
    @Override
    public UnsignedByte getDataRegister() {
        // Check to see if we should output CA2 low on read
        if (!controlRegister.isMasked(0x8)) {
            deviceSelectorSwitch.setCA2(false);
        }

        // Check to see if our joystick values are higher than PIA2 voltage
        boolean fireComparator = false;
        switch (deviceSelectorSwitch.switchPosition) {
            case 0:
                fireComparator = rightJoystickX > pia2a.getVoltage();
                break;

            case 1:
                fireComparator = rightJoystickY > pia2a.getVoltage();
                break;

            case 2:
                fireComparator = leftJoystickX > pia2a.getVoltage();
                break;

            case 3:
                fireComparator = leftJoystickY > pia2a.getVoltage();
                break;
        }

        UnsignedByte result = keyboard.getHighByte();
        result.and(leftJoystickFire ? ~0x2 : ~0x0);
        result.and(rightJoystickFire ? ~0x1 : ~0x0);
        result.and(~0x80);
        result.or(fireComparator ? 0x80 : 0x0);

        // Check to see if we should transition CA2 high again
        if (!controlRegister.isMasked(0x8)) {
            if (controlRegister.isMasked(0x4)) {
                deviceSelectorSwitch.setCA2(true);
            }
        }
        return result;
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
