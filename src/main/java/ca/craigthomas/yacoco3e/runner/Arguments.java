/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.runner;

import com.beust.jcommander.Parameter;

/**
 * A data class that stores the arguments that may be passed to the emulator.
 */
public class Arguments
{
    @Parameter(names="--system", description="system ROM")
    public String systemROM;

    @Parameter(names="--scale", description="scale factor")
    public Integer scale = 2;

    @Parameter(names="--trace", description="trace output")
    public Boolean trace = false;

    @Parameter(names="--verbose", description="verbose output")
    public Boolean verbose = false;

    @Parameter(names="--cassette", description="cassette file")
    public String cassetteFile;

    @Parameter(names="--cartridge", description="cartridge ROM")
    public String cartridgeROM;

    @Parameter(names="--config", description="path to config file")
    public String configFile;
}
