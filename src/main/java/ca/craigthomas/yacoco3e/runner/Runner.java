/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.runner;

import ca.craigthomas.yacoco3e.components.Emulator;
import com.beust.jcommander.JCommander;

public class Runner
{
    public static void main(String [] argv) {
        Arguments arguments = new Arguments();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(arguments)
                .build();
        jCommander.setProgramName("yacoco3e");
        jCommander.parse(argv);

        Emulator emulator = new Emulator(arguments.scale, arguments.romFile);
        emulator.start();
    }
}
