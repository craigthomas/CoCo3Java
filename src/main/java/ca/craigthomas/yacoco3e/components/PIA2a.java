/*
 * Copyright (C) 2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

public class PIA2a extends PIA
{
    protected Cassette cassette;
    protected float voltage;
    protected DigitalAnalogConverter dac;
    protected boolean useDAC;

    public PIA2a(Cassette newCassette, boolean useDAC) {
        super();
        cassette = newCassette;
        voltage = 0.0f;
        if (useDAC) {
            dac = new DigitalAnalogConverter();
            dac.start();
        }
        this.useDAC = useDAC;
    }

    public void shutdown() {
        if (useDAC) {
            dac.stopRunning();
        }
    }

    /**
     * In PIA 2 side A, multiple sources are potentially connected to the various
     * address lines.
     */
    @Override
    public UnsignedByte getDataRegister() {
        dataRegister.and(0);
        dataRegister.or(cassette.nextBit());
        return dataRegister.copy();
    }

    /**
     * In the Color Computer line of computers, PIA 2 side A is always configured for
     * output, not for input. Writing to the data register writes a value to the
     * digital-to-analog converter. A corresponding voltage is generated from the
     * values written from bits 2 through 7 of the data register. The resulting
     * voltage ranges from 0 to +4.5V. Each bit has an additive contribution to
     * the overall voltage as follows:
     *   bit 7 set = 2.250V
     *   bit 6 set = 1.125V
     *   bit 5 set = 0.563V
     *   bit 4 set = 0.281V
     *   bit 3 set = 0.140V
     *   bit 2 set = 0.070V
     *
     * @param newDataRegister the new value for the data register
     */
    @Override
    public void setDataRegister(UnsignedByte newDataRegister) {
        voltage = 0.0f;
        voltage += newDataRegister.isMasked(0x80) ? 2.250f : 0.0f;
        voltage += newDataRegister.isMasked(0x40) ? 1.125f : 0.0f;
        voltage += newDataRegister.isMasked(0x20) ? 0.563f : 0.0f;
        voltage += newDataRegister.isMasked(0x10) ? 0.281f : 0.0f;
        voltage += newDataRegister.isMasked(0x08) ? 0.140f : 0.0f;
        voltage += newDataRegister.isMasked(0x04) ? 0.070f : 0.0f;
        byte audioByte = (byte)(((voltage / 4.429f) * 128.0f));
        if (useDAC) {
            dac.writeByte(audioByte);
        }
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

        /* Bit 0 = FIRQ from serial I/O port */

        /* Bit 1 = hi/lo edge triggered */

        cassette.setMotorOn(controlRegister.isMasked(0x08));
    }
}
