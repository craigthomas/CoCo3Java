/*
 * Copyright (C) 2023 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import org.junit.Test;

public class NotImplementedInstructionTest {
    @Test(expected = MalformedInstructionException.class)
    public void testNotImplementedInstructionThrowsRuntimeExceptionWithUnsupportedRegister() throws MalformedInstructionException {
        NotImplementedInstruction instruction = new NotImplementedInstruction(0);
        instruction.call(null, null);
    }
}
