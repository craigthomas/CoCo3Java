/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

public enum EmulatorStatus
{
    RUNNING("running"), PAUSED("paused"), STOPPED("stopped"), KILLED("killed");

    private String value;

    EmulatorStatus(String desc) {
        value = desc;
    }

    public String toString() {
        return value;
    }
}
