/*
 * Copyright (C) 2017-2025 Craig Thomas
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

    /**
     * Convenience constructor initializing the byte value to zero
     */
    public UnsignedByte() {
        this(0);
    }

    /**
     * Constructor that initializes the byte value to the specified
     * value. The value is clipped so that only the lower 8-bits
     * of the integer value.
     *
     * @param x the value to initialize the byte to
     */
    public UnsignedByte(int x) {
        value = (short) (x & 0xFF);
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
        return (isMasked(0x80)) ? (short) -(twosCompliment().getShort()) : getShort();
    }

    /**
     * Returns a new UnsignedByte that is the twos compliment
     * value of the current byte.
     *
     * @return a new UnsignedByte with the twos compliment value
     */
    public UnsignedByte twosCompliment() {
        return new UnsignedByte(~value + 1);
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
     * @param x the additional value to add
     */
    public void add(int x) {
        value = (short) ((value + (x & 0xFF)) & 0xFF);
    }

    public void shiftRight() {
        value = (short) ((value >> 1) & 0xFF);
    }

    public void shiftLeft() {
        value = (short) ((value << 1) & 0xFF);
    }

    public void compliment() {
        value = (short) (~value & 0xFF);
    }

    /**
     * Applies the specified mask using an AND operation.
     *
     * @param mask the mask to apply
     */
    public void and(int mask) {
        value = (short) (value & (mask & 0xFF));
    }

    public void and(UnsignedByte mask) {
        and(mask.getShort());
    }

    /**
     * Applies the specified mask using an OR operation.
     *
     * @param mask the mask to apply
     */
    public void or(int mask) {
        value = (short) (value | (mask & 0xFF));
    }

    public void or(UnsignedByte mask) {
        or(mask.getShort());
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
     * Convenience function allowing to set the value of the byte
     * using an integer value.
     *
     * @param x the value to make the byte
     */
    public void set(int x) {
        value = (short) (x & 0xFF);
    }

    /**
     * Returns a copy of the unsigned byte.
     *
     * @return a copy of the unsigned byte
     */
    public UnsignedByte copy() {
        return new UnsignedByte(value);
    }

    /**
     * Sets the value for this byte.
     *
     * @param x the new byte value to set
     */
    public void set(UnsignedByte x) {
        value = x.getShort();
    }

    public boolean equalsInt(int value) {
        return this.value == value;
    }

    public String toString() {
        return String.format("$%02X", value).replace("0x", "");
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
        return value;
    }
}
