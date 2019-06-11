/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.runner;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * A data class that stores the arguments that may be passed to the emulator.
 */
@Parameters(commandDescription = "Runs the ROM and outputs traces to STDOUT")
public class SimulateCommand
{
    @Parameter(description="ROM file")
    public String romFile;

    @Parameter(names="--scale", description="scale factor")
    public Integer scale = 2;

    @Parameter(names="--trace", description="trace output")
    public Boolean trace = false;

    @Parameter(names="--cassette", description="cassette file")
    public String cassetteFile;

    @Parameter(names="--diskbas", description="Disk Basic ROM")
    public String diskBasicROM;
}
