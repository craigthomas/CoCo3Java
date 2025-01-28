/*
 * Copyright (C) 2017-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

public enum EmulatorStatus
{
    RUNNING {
        @Override
        public String toString() {
            return "running";
        }
    },
    PAUSED {
        @Override
        public String toString() {
            return "paused";
        }
    },
    STOPPED {
        @Override
        public String toString() {
            return "stopped";
        }
    },
    KILLED {
        @Override
        public String toString() {
            return "killed";
        }
    }
}
