/*
 * Copyright (C) 2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

public class PIA2b extends PIA
{
    protected UnsignedByte vdgMode;
    protected IOController io;

    public PIA2b(IOController newIO) {
        super();
        vdgMode = new UnsignedByte();
        io = newIO;
    }

    /**
     * In PIA 2 side A, multiple sources are potentially connected to the various
     * address lines. 
     *
     * @return the high byte of the keyboard matrix
     */
    @Override
    public UnsignedByte getDataRegister() {
        return dataRegister.copy();
    }

    /**
     * In the Color Computer line of computers, the PIA 2 side B is connected to
     * the Video Display Generator. Writing to the data register will set the
     * video mode. Bits 3-7 are connected to the VDG. The bits must be set for
     * output based on the data direction register.
     *
     * @param newDataRegister the new value for the data register
     */
    @Override
    public void setDataRegister(UnsignedByte newDataRegister) {
        vdgMode = newDataRegister.copy();
        vdgMode.and(dataDirectionRegister);
        vdgMode.and(~0x7);
        io.updateVideoMode(vdgMode);
        dataRegister = newDataRegister.copy();
    }

    /**
     * When the control register is written to, interrupts are potentially enabled,
     * and bits 7 and 6 are set.
     *
     * @param newControlRegister the new control register value
     */
    @Override
    public void setControlRegister(UnsignedByte newControlRegister) {
        controlRegister = new UnsignedByte(newControlRegister.get() +
                        (controlRegister.isMasked(0x80) ? 0x80 : 0) +
                        (controlRegister.isMasked(0x40) ? 0x40 : 0));
        /* Bit 2 = Control whether Data Register or Data Direction Register active */
        /* Bit 1 = hi/lo edge triggered */
        /* Bit 0 = FIRQ from cartridge ROM */
    }

    public UnsignedByte getVdgMode() {
        return vdgMode;
    }
}
