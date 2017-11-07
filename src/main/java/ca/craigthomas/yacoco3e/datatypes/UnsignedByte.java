/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

/**
 * The UnsignedByte class is used to store the contents of a byte. It contains
 * several helper functions designed to make accessing and manipulating the
 * byte simpler.
 */
public class UnsignedByte
{
    /* The underlying value of the byte */
    private short value;

    public UnsignedByte() {
        this(0);
    }

    public UnsignedByte(int value) {
        this.value = (short) (value & 0xFF);
    }

    /**
     * Returns the actual value of the byte.
     *
     * @return the short value of the byte
     */
    public short getShort() {
        return (short) (value & 0xFF);
    }

    /**
     * Returns the signed value of the byte. The signed value is calculated by
     * looking at the 8th bit of the byte. If it is 1, then it is considered
     * to be negative.
     *
     * @return the signed short value of the byte
     */
    public short getSignedShort() {
        if (isMasked(0x80)) {
            return (short) (int)-(twosCompliment().getShort());
        }
        return getShort();
    }
    /**
     * Returns a new UnsignedByte that is the twos compliment
     * value of the current byte.
     *
     * @return a new UnsignedByte with the twos compliment value
     */
    public UnsignedByte twosCompliment() {
        return new UnsignedByte((~value + 1));
    }

    /**
     * Returns the inverse bit value of the byte.
     *
     * @return the inverse bit value of the byte
     */
    public UnsignedByte inverse() {
        return new UnsignedByte(~value);
    }
    /**
     * Returns true if the specified mask results in a non-zero value when
     * ANDed to the current value of the byte.
     *
     * @param mask the bitmask to apply
     * @return True if applying the mask would result in a non-zero value
     */
    public boolean isMasked(int mask) {
        return (value & mask) == mask;
    }

    /**
     * Adds the specified value to the current byte.
     *
     * @param value the additional value to add
     */
    public void add(int value) {
        this.value += value;
        and(0xFF);
    }

    /**
     * Applies the specified mask using an AND operation.
     *
     * @param mask the mask to apply
     */
    public void and(int mask) {
        value &= mask;
    }

    /**
     * Applies the specified mask using an OR operation.
     *
     * @param mask the mask to apply
     */
    public void or(int mask) {
        value |= mask;
    }

    /**
     * Will return true if the byte value is zero.
     *
     * @return True if the value is zero
     */
    public boolean isZero() {
        return value == 0;
    }

    /**
     * Will return true if the highest bit of the byte is set (negative).
     *
     * @return True if the signed value of the byte would be negative
     */
    public boolean isNegative() {
        return isMasked(0x80);
    }

    /**
     * Returns a copy of the unsigned byte.
     *
     * @return a copy of the unsigned byte
     */
    public UnsignedByte copy() {
        return new UnsignedByte(getShort());
    }

    /**
     * Sets the value for this byte.
     *
     * @param value the new byte value to set
     */
    public void set(UnsignedByte value) {
        this.value = value.getShort();
    }

    /**
     * Swaps the high nibble and the low nibble of the byte.
     */
    public void swapNibbles() {
        int low = value & 0x0F;
        int high = value & 0xF0;

        low = low << 4;
        high = high >> 4;

        value = (short) (low + high);
    }

    public String toString() {
        return String.format("%02X", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnsignedByte that = (UnsignedByte) o;

        return this.getShort() == that.getShort();
    }

    @Override
    public int hashCode() {
        return (int) value;
    }
}
