/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

public enum MemoryType
{
    CARTRIDGE("cartridge ROM"), ROM("system ROM"), MEMORY("RAM");

    private String value;

    MemoryType(String desc) {
        value = desc;
    }

    public String toString() {
        return value;
    }
}
