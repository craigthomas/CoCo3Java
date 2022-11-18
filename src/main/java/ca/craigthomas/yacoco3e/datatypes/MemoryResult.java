/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

/**
 * A memory storage result for memory operations. Will store the number of
 * bytes that were read by the memory operation, as well as the result.
 */
public class MemoryResult
{
    /* The number of bytes that were read */
    public int bytesConsumed;

    /* The result of the memory operation */
    public UnsignedWord value;

    public MemoryResult() {
        this(0, new UnsignedWord());
    }

    public MemoryResult(int bytesConsumed, UnsignedWord result) {
        this.bytesConsumed = bytesConsumed;
        this.value = result;
    }

    /**
     * A more user-friendly representation of the memory result.
     *
     * @return a string representation of the memory result
     */
    public String toString() {
        return String.format("[%d bytes read, %s]", bytesConsumed, value.toString());
    }
}
