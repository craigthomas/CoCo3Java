/*
 * Copyright (C) 2017 Craig Thomas
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
    private UnsignedByte a;
    private UnsignedByte b;
    private UnsignedByte dp;
    private UnsignedByte cc;
    private UnsignedWord pc;
    private UnsignedWord x;
    private UnsignedWord y;
    private UnsignedWord s;
    private UnsignedWord u;

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

    public UnsignedWord getPC() {
        return pc;
    }

    public void setPC(UnsignedWord pc) {
        this.pc = pc.copy();
    }

    public void setDP(UnsignedByte dp) {
        this.dp = dp.copy();
    }

    public UnsignedByte getDP() {
        return dp;
    }

    public UnsignedWord getX() {
        return x;
    }

    public UnsignedWord getY() {
        return y;
    }

    public UnsignedWord getU() {
        return u;
    }

    public UnsignedWord getS() {
        return s;
    }

    public void setA(UnsignedByte a) {
        this.a = a.copy();
    }

    public void setB(UnsignedByte b) {
        this.b = b.copy();
    }

    public void setCC(UnsignedByte cc) {
        this.cc = cc.copy();
    }

    public void setX(UnsignedWord x) {
        this.x = x.copy();
    }

    public void setY(UnsignedWord y) {
        this.y = y.copy();
    }

    public void setU(UnsignedWord u) {
        this.u = u.copy();
    }

    public void setS(UnsignedWord s) {
        this.s = s.copy();
    }

    /**
     * Returns the value of the A register.
     *
     * @return the value of the A register
     */
    public UnsignedByte getA() {
        return a;
    }

    /**
     * Returns the value of the B register.
     *
     * @return the value of the B register
     */
    public UnsignedByte getB() {
        return b;
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

    /**
     * Gets the value of the condition code register.
     *
     * @return the value of the condition code register
     */
    public UnsignedByte getCC() {
        return cc;
    }

    public void incrementPC() {
        pc.add(1);
    }
}
