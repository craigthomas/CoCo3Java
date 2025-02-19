/*
 * Copyright (C) 2022-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

public enum AddressingMode
{
    INHERENT {
        @Override
        public String toString() {
            return "INH";
        }
    },
    IMMEDIATE {
        @Override
        public String toString() {
            return "IMM";
        }
    },
    DIRECT {
        @Override
        public String toString() {
            return "DIR";
        }
    },
    INDEXED {
        @Override
        public String toString() {
            return "IND";
        }
    },
    EXTENDED {
        @Override
        public String toString() {
            return "EXT";
        }
    }
}
