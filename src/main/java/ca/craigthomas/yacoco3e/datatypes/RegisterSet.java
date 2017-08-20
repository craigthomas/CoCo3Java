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

    UnsignedByte a;
    UnsignedByte b;
    UnsignedByte dp;
    public UnsignedByte cc;
    UnsignedWord pc;
    UnsignedWord x;
    UnsignedWord y;
    UnsignedWord s;
    UnsignedWord u;

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
        this.pc = pc;
    }

    public void setDP(UnsignedByte dp) {
        this.dp = dp;
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
        this.a = a;
    }

    public void setB(UnsignedByte b) {
        this.b = b;
    }

    public void setCC(UnsignedByte cc) {
        this.cc = cc;
    }

    public void setX(UnsignedWord x) {
        this.x = x;
    }

    public void setY(UnsignedWord y) {
        this.y = y;
    }

    public void setU(UnsignedWord u) {
        this.u = u;
    }

    public void setS(UnsignedWord s) {
        this.s = s;
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
            UnsignedWord test = new UnsignedWord(value1 & 0xFF);
            test.add(value2 & 0xFF);
            if (test.isMasked(0x100) && flagCarry) {
                setCCCarry();
            }
        }

        /* Check to see if overflow occurred and we should flag it */
        if (flagOverflow) {
            if ((value1 + value2) > 0xFFFF) {
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
        /* TODO: Check for correctness here */
        if (flagCarry) {
            UnsignedByte test = new UnsignedByte(value1 & 0xFF);
            test.add(value2 & 0xFF);
            if (test.isMasked(0x100)) {
                setCCCarry();
            }
        }

        /* Check for overflow */
        if (flagOverflow) {
            if ((value1 + value2) > 255) {
                setCCOverflow();
            }
        }

        return new UnsignedByte(value1 + value2);
    }

    public boolean ccCarrySet() {
        return cc.isMasked(CC_C);
    }

    public void setCCCarry() {
        cc.or(CC_C);
    }

    public boolean ccOverflowSet() {
        return cc.isMasked(CC_V);
    }

    public void setCCOverflow() {
        cc.or(CC_V);
    }

    public boolean ccZeroSet() {
        return cc.isMasked(CC_Z);
    }

    public void setCCZero() {
        cc.or(CC_Z);
    }

    public boolean ccNegativeSet() {
        return cc.isMasked(CC_N);
    }

    public void setCCNegative() {
        cc.or(CC_N);
    }

    public boolean ccInterruptSet() {
        return cc.isMasked(CC_I);
    }

    public void setCCInterrupt() {
        cc.or(CC_I);
    }

    public boolean ccHalfCarrySet() {
        return cc.isMasked(CC_H);
    }

    public void setCCHalfCarry() {
        cc.or(CC_H);
    }

    public boolean ccFastInterruptSet() {
        return cc.isMasked(CC_F);
    }

    public void setCCFastInterrupt() {
        cc.or(CC_F);
    }

    public void setCCEverything() {
        cc.or(CC_E);
    }

    public boolean ccEverythingSet() {
        return cc.isMasked(CC_E);
    }

    public void incrementPC() {
        pc.add(1);
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
                return y;

            case X:
                return x;

            case S:
                return s;

            case U:
                return u;

            case D:
                return getD();
        }

        return null;
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
                return a;

            case B:
                return b;
        }

        return null;
    }
}
