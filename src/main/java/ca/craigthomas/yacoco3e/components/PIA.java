/*
 * Copyright (C) 2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

public abstract class PIA
{
    public UnsignedByte controlRegister;
    public UnsignedByte dataRegister;
    public UnsignedByte dataDirectionRegister;

    public PIA() {
        controlRegister = new UnsignedByte();
        dataDirectionRegister = new UnsignedByte();
        dataRegister = new UnsignedByte();
    }

    /**
     * Returns the data register for this portion of the PIA.
     *
     * @return the contents of the data register
     */
    public abstract UnsignedByte getDataRegister();

    /**
     * Sets the data register for this portion of the PIA.
     *
     * @param newDataRegister the new value for the data register
     */
    public abstract void setDataRegister(UnsignedByte newDataRegister);

    /**
     * Gets the contents of the data direction register.
     *
     * @return the data direction register contents
     */
    public UnsignedByte getDataDirectionRegister() {
        return dataDirectionRegister.copy();
    }

    /**
     * Sets the value of the data direction register.
     *
     * @param newDataDirectionRegister the new contents fo the data direction register
     */
    public void setDataDirectionRegister(UnsignedByte newDataDirectionRegister) {
        dataDirectionRegister = newDataDirectionRegister.copy();
    }

    /**
     * Returns the contents of the control register.
     *
     * @return the contents of the control register
     */
    public UnsignedByte getControlRegister() {
        return controlRegister.copy();
    }

    /**
     * Set the contents of the control register.
     *
     * @param newControlRegister the new control register value
     */
    public void setControlRegister(UnsignedByte newControlRegister) {
        controlRegister = newControlRegister.copy();
    }

    /**
     * Gets either the data register, or the data direction register. The register
     * returned depends on the value of bit 2 in the control register. If it is set,
     * then the data register is returned. If it is clear, then the data direction
     * register is returned. If the data register is returned, will also clear
     * bits 7 and 6 of the control register.
     *
     * @return the data register or the data direction register
     */
    public UnsignedByte getRegister() {
        controlRegister.and(controlRegister.isMasked(0x04) ? ~0xC0 : ~0x00);
        return (controlRegister.isMasked(0x04)) ? getDataRegister() : getDataDirectionRegister();
    }

    /**
     * Sets the value of the data register, or the data direction register. The
     * register returned depends on the value of bit 1 in the control register. If it is set,
     * then the data register is set. If it is clear, then the data direction register
     * is set.
     *
     * @param newRegisterValue the new value for the data register or data direction register
     */
    public void setRegister(UnsignedByte newRegisterValue) {
        if (controlRegister.isMasked(0x04)) {
            setDataRegister(newRegisterValue);
            return;
        }
        setDataDirectionRegister(newRegisterValue);
    }
}
