/*
 * Copyright (C) 2017-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

public enum MemoryType
{
    CARTRIDGE {
        @Override
        public String toString() {
            return "cartridge ROM";
        }
    },
    ROM {
        @Override
        public String toString() {
            return "system ROM";
        }
    },
    MEMORY {
        @Override
        public String toString() {
            return "RAM";
        }
    }
}
