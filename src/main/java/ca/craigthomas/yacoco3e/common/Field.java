/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.common;

/**
 * A Field is used to store information of variable size. Fields
 * typically have two sections:
 *
 * - a gap at the beginning with a specific size
 * - a data section with a specific size
 *
 * The Field class is typically used within a DiskSector. Class
 * methods exist to manage the gap, store the current position
 * in the field, and read or write bytes into a gap or data. Some
 * fields are filled with a specific pattern that is 'expected'.
 */
public class Field
{
    // Holds bytes relating to the data portion of a field
    private byte [] data;
    // Holds gap information
    private int gapSize;
    // Where the field is currently pointing to
    private int pointer;
    // A save state value for the pointer during push or pop
    private int oldPointer;
    // How many bytes are used in the field
    private int usedSize;
    // How many bytes the field was initialized to
    private short expected;

    /**
     * A constructor for a field that will generate a field with
     * the specified size, and no gap.
     *
     * @param size the size of the field to create
     */
    public Field(int size) {
        this(size, 0, (short) -1);
    }

    /**
     * A constructor for a field that will generate a field with
     * the specified size, and no gap.
     *
     * @param size the size of the field to create
     * @param expected the number of bytes expected to see in the field
     */
    public Field(int size, short expected) {
        this(size, 0, expected);
    }

    public Field(int size, int gap, short expected) {
        gapSize = gap;
        data = new byte [size + gap];
        usedSize = -1;
        this.expected = expected;
        restore();
    }

    /**
     * Moves the pointer to the beginning of the field, starting
     * at any gap.
     */
    public void restore() {
        pointer = 0;
    }

    /**
     * Moves the pointer so that it is past the gap.
     */
    public void restorePastGap() {
        pointer = gapSize;
    }

    /**
     * Saves the current pointer position, and rewinds it back to zero.
     */
    public void push() {
        oldPointer = pointer;
        restore();
    }

    /**
     * Restores the pointer position to the previously pushed position.
     */
    public void pop() {
        pointer = oldPointer;
    }

    /**
     * Reads the current byte of data pointed to by the pointer.
     *
     * @return the current byte
     */
    public byte read() {
        byte result = data[pointer];
        pointer++;
        return result;
    }

    /**
     * Writes a byte of data pointed to by the pointer.
     *
     * @param value the byte to write
     */
    public void write(byte value) {
        data[pointer] = value;
        pointer++;
    }

    /**
     * Returns true if there is space for more bytes in this field.
     *
     * @return true if more bytes can be read or written
     */
    public boolean hasMoreBytes() {
        if (usedSize != -1 && pointer >= usedSize) {
            return false;
        }

        return pointer < data.length;
    }

    /**
     * Advances the field pointer by 1.
     */
    public void next() {
        pointer++;
    }

    /**
     * Fills the data portion with zeros.
     */
    public void zeroFill() {
        for (int i=0; i < data.length; i++) {
            data[i] = 0;
        }
    }

    /**
     * Whether the byte value is expected to be written to the field
     * or not.
     *
     * @param value the value that is about to be written
     * @return true if the byte value is expected, false otherwise
     */
    public boolean isExpected(byte value) {
        return (expected == -1) || (value == expected);
    }

    /**
     * Marks the field as being full.
     */
    public void setFilled() {
        usedSize = pointer;
    }

    /**
     * Reads the field at the specified location in the data section.
     *
     * @param location the location to read from
     * @return the byte read at the location
     */
    public byte readAt(int location) {
        return data[location];
    }
}
