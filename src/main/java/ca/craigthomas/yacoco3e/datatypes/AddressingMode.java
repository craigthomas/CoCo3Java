/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

public class AddressingMode
{
    public static final int NONE = 0;
    public static final int INHERENT = 1;
    public static final int IMMEDIATE = 2;
    public static final int DIRECT = 3;
    public static final int INDEXED = 4;
    public static final int EXTENDED = 5;

    public static String decode(int mode) {
        switch(mode) {
            case INHERENT:
                return "INH";

            case IMMEDIATE:
                return "IMM";

            case DIRECT:
                return "DIR";

            case INDEXED:
                return "IND";

            case EXTENDED:
                return "EXT";

            default:
                return "UNK";
        }
    }
}
