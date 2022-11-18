/*
 * Copyright (C) 2023 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

/**
 * The UnsignedWord class holds a single 16 bit value. The value is held in
 * a setLow byte and a setHigh byte, which are represented as
 */
public class UnsignedWord
{
    /* The value of the word */
    private int value;

    public UnsignedWord(int value) {
        set(value);
    }

    public UnsignedWord(int high, UnsignedByte low) {
        value = 0;
        setHigh(new UnsignedByte(high));
        setLow(low);
    }

    public UnsignedWord(UnsignedByte high, UnsignedByte low) {
        value = 0;
        setHigh(high);
        setLow(low);
    }

    public UnsignedWord() {
        value = 0;
    }

    /**
     * Returns the high byte of the word.
     *
     * @return the high byte of the word
     */
    public UnsignedByte getHigh() {
        return new UnsignedByte((value & 0xFF00) >> 8);
    }

    /**
     * The setHigh function sets the setHigh byte for the word.
     *
     * @param high the setHigh byte for the word
     */
    public void setHigh(UnsignedByte high) {
        value &= 0x00FF;
        value += (high.getShort() << 8);
    }

    /**
     * Returns the low byte of the word.
     *
     * @return the low byte of the word
     */
    public UnsignedByte getLow() {
        return new UnsignedByte(value & 0xFF);
    }

    /**
     * The setLow function sets the low byte for the word.
     *
     * @param low the low byte for the word
     */
    public void setLow(UnsignedByte low) {
        value &= 0xFF00;
        value += low.getShort();
    }

    /**
     * Returns the next byte along the sequence to read.
     *
     * @return the next byte to read
     */
    public UnsignedWord next() {
        return new UnsignedWord(value + 1);
    }

    /**
     * Adds the specified value to the current word.
     *
     * @param value the additional value to add
     */
    public void add(int value) {
        this.value += value;
        and(0xFFFF);
    }

    /**
     * Returns a new UnsignedWord that is the twos compliment
     * value of the current Word.
     *
     * @return a new UnsignedWord with the twos compliment value
     */
    public UnsignedWord twosCompliment() {
        return new UnsignedWord((~value + 1));
    }

    /**
     * Returns the inverse bit value of the UnsignedWord.
     *
     * @return the inverse bit value of the word
     */
    public UnsignedWord inverse() {
        return new UnsignedWord(~value);
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
     * Will return true if the highest bit of the word is set (negative).
     *
     * @return True if the signed value of the byte would be negative
     */
    public boolean isNegative() {
        return isMasked(0x8000);
    }

    /**
     * Returns true if the specified mask results in a non-zero value when
     * ANDed to the current value of the word.
     *
     * @param mask the bitmask to apply
     * @return True if applying the mask would result in a non-zero value
     */
    public boolean isMasked(int mask) {
        return (getInt() & mask) == mask;
    }

    /**
     * Sets the current value for the UnsignedWord.
     *
     * @param value the new value to set
     */
    public void set(int value) {
        this.value = (value & 0x0FFFF);
    }

    /**
     * Sets the value of this word using another UnsignedWord.
     *
     * @param value the value to set
     */
    public void set(UnsignedWord value) {
        this.value = value.getInt();
    }

    /**
     * Returns the integer representation of the word.
     *
     * @return the integer representation of the word
     */
    public int getInt() {
        return value;
    }

    /**
     * Returns the signed integer representation of the word. The integer is
     * negative if the setHigh byte has the 8th bit set.
     *
     * @return the signed integer representation of the word
     */
    public int getSignedInt() {
        return (isNegative()) ? -(twosCompliment().getInt()) : value;
    }

    /**
     * Creates a copy of the current unsigned word.
     *
     * @return a new copy of the UnsignedWord
     */
    public UnsignedWord copy() {
        return new UnsignedWord(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnsignedWord that = (UnsignedWord) o;

        return this.getInt() == that.getInt();
    }

    @Override
    public int hashCode() {
        return getInt();
    }

    @Override
    public String toString() {
        return String.format("$%04X", value);
    }
}
