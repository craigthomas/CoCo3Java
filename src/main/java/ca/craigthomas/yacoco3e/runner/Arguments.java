/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.runner;

import com.beust.jcommander.Parameter;

public class Arguments
{
    @Parameter(names="--rom", description="ROM file")
    public String romFile;

    @Parameter(names="--scale", description="scale factor")
    public Integer scale = 2;
}
