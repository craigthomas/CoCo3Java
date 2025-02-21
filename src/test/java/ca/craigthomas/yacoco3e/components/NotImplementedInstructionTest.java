/*
 * Copyright (C) 2023-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.RegisterSet;
import org.junit.Before;
import org.junit.Test;

public class NotImplementedInstructionTest {
    private IOController io;

    @Before
    public void setup() throws MalformedInstructionException {
        io = new IOController(new Memory(), new RegisterSet(), new EmulatedKeyboard(), new Screen(1), new Cassette());
        CPU cpu = new CPU(io);
        io.setCPU(cpu);
    }

    @Test(expected = MalformedInstructionException.class)
    public void testNotImplementedInstructionThrowsWithUnsupportedRegister() throws MalformedInstructionException {
        NotImplementedInstruction instruction = new NotImplementedInstruction(0);
        instruction.call(null);
    }

    @Test(expected = MalformedInstructionException.class)
    public void testNotImplementedInstructionThrowsExceptionOnExecute() throws MalformedInstructionException {
        NotImplementedInstruction instruction = new NotImplementedInstruction(0);
        instruction.execute(io);
    }
}
