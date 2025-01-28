/*
 * Copyright (C) 2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

import org.junit.Test;

import static org.junit.Assert.*;

public class EmulatorStatusTest
{
    @Test
    public void testEmulatorStatusRunningToStringCorrect() {
        assertEquals("running", EmulatorStatus.RUNNING.toString());
    }

    @Test
    public void testEmulatorStatusPausedToStringCorrect() {
        assertEquals("paused", EmulatorStatus.PAUSED.toString());
    }

    @Test
    public void testEmulatorStoppedPausedToStringCorrect() {
        assertEquals("stopped", EmulatorStatus.STOPPED.toString());
    }

    @Test
    public void testEmulatorKilledPausedToStringCorrect() {
        assertEquals("killed", EmulatorStatus.KILLED.toString());
    }
}
