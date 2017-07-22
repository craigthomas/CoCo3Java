/*
 * Copyright (C) 2013-2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

public class IllegalIndexedPostbyteException extends Exception
{
    public IllegalIndexedPostbyteException(UnsignedByte postByte) {
        super(String.format("Illegal postbyte %s", postByte.toString()));
    }
}
