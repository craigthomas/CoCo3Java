/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.runner;

import ca.craigthomas.yacoco3e.components.Emulator;
import com.beust.jcommander.JCommander;

/**
 * This class is responsible for starting up the emulator with the
 * specified command line options.
 */
public class Runner
{
    public static void main(String [] argv) {

        /* Parse the arguments */
        Arguments arguments = new Arguments();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(arguments)
                .build();
        jCommander.setProgramName("CoCo3Java");
        jCommander.parse(argv);

        /* Create the emulator and start it running */
        Emulator emulator = new Emulator.Builder()
                .setScale(arguments.scale)
                .setSystemROM(arguments.systemROM)
                .setTrace(arguments.trace)
                .setVerbose(arguments.verbose)
                .setCassetteFile(arguments.cassetteFile)
                .setCartridgeROM(arguments.cartridgeROM)
                .setConfigFile(arguments.configFile)
                .setDAC(arguments.useDAC)
                .build();
        emulator.start();
    }
}
