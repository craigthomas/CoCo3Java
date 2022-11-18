/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

/**
 * The RegisterSet class contains the full set of CPU registers as used by the
 * CoCo 3. It also contains helper functions to modify the registers, as well
 * as common register flags.
 */
public class RegisterSet
{
    public static final short CC_C = 0x01;
    public static final short CC_V = 0x02;
    public static final short CC_Z = 0x04;
    public static final short CC_N = 0x08;
    public static final short CC_I = 0x10;
    public static final short CC_H = 0x20;
    public static final short CC_F = 0x40;
    public static final short CC_E = 0x80;

    public UnsignedByte a;
    public UnsignedByte b;
    public UnsignedByte dp;
    public UnsignedByte cc;
    public UnsignedWord pc;
    public UnsignedWord x;
    public UnsignedWord y;
    public UnsignedWord s;
    public UnsignedWord u;

    public RegisterSet() {
        cc = new UnsignedByte();
        dp = new UnsignedByte();
        pc = new UnsignedWord();
        y = new UnsignedWord();
        x = new UnsignedWord();
        u = new UnsignedWord();
        s = new UnsignedWord();
        a = new UnsignedByte();
        b = new UnsignedByte();
    }

    /**
     * Returns the combined value of the A and B registers as a 16-bit word.
     *
     * @return the value of the D register
     */
    public UnsignedWord getD() {
        return new UnsignedWord(a, b);
    }

    /**
     * Sets the value of the D register, which is equivalent to
     * the A and B registers combined.
     *
     * @param d the new value for the D register
     */
    public void setD(UnsignedWord d) {
        a = d.getHigh();
        b = d.getLow();
    }

    public void setD(int d) {
        this.setD(new UnsignedWord(d));
    }

    public void incrementPC() {
        pc.add(1);
    }

    public String toString() {
        return "PC:" + pc + " A:" + a + " B:" + b + " D:" + getD() + " X:" + x + " Y:" + y +
                " U:" + u + " S:" + s + " CC:" + cc + " DP:" + dp;
    }
}
