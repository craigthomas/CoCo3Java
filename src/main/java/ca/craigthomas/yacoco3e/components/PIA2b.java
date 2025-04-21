/*
 * Copyright (C) 2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

public class PIA2b extends PIA
{
    protected UnsignedByte vdgOperatingMode;

    public PIA2b(UnsignedByte newVdgOperatingMode) {
        super();
        vdgOperatingMode = newVdgOperatingMode;
    }

    /**
     * In PIA 2 side A, multiple sources are potentiall connected to the various
     * address lines. 
     *
     * @return the high byte of the keyboard matrix
     */
    @Override
    public UnsignedByte getDataRegister() {
        return dataRegister.copy();
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
        interruptEnabled = newControlRegister.isMasked(0x1);
        controlRegister = new UnsignedByte(newControlRegister.getShort() +
                        (controlRegister.isMasked(0x80) ? 0x80 : 0) +
                        (controlRegister.isMasked(0x40) ? 0x40 : 0));
    }
}
