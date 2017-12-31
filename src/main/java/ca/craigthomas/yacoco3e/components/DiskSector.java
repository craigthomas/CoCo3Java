/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

public class DiskSector
{
    private Field gap1;
    private Field id;
    private Field gap2;
    private Field data;
    private Field gap3;

    private boolean doubleDensity;

    private DiskCommand command;

    private boolean dataAddressMark;

    public class Field {
        private byte [] data;
        private int gapSize;
        private int pointer;

        /**
         * A constructor for a field that will generate a field with
         * the specified size, and no gap.
         *
         * @param size the size of the field to create
         */
        public Field(int size) {
            this(size, 0);
        }

        public Field(int size, int gap) {
            gapSize = gap;
            data = new byte [size + gap];
            restore();
        }

        /**
         * Moves the pointer to the beginning of the field, starting
         * at any gap.
         */
        public void restore() {
            pointer = 0;
        }

        /**
         * Moves the pointer so that it is past the gap.
         */
        public void restorePastGap() {
            pointer = gapSize;
        }

        /**
         * Reads the current byte of data pointed to by the pointer.
         *
         * @return the current byte
         */
        public byte read() {
            byte result = data[pointer];
            pointer++;
            return result;
        }

        /**
         * Writes a byte of data pointed to by the pointer.
         *
         * @param value the byte to write
         */
        public void write(byte value) {
            data[pointer] = value;
            pointer++;
        }

        /**
         * Returns true if there is space for more bytes in this field.
         *
         * @return true if more bytes can be read or written
         */
        public boolean hasMoreBytes() {
            return pointer < data.length;
        }

        /**
         * Advances the field pointer by 1.
         */
        public void next() {
            pointer++;
        }
    }

    public DiskSector(boolean doubleDensity) {
        if (doubleDensity) {
            gap1 = new Field(12);
            id = new Field(7, 3);
            gap2 = new Field(34);
            data = new Field(258, 4);
            gap3 = new Field(24);
        } else {
            gap1 = new Field(6);
            id = new Field(7);
            gap2 = new Field(17);
            data = new Field(130, 1);
            gap3 = new Field(10);
        }
        this.doubleDensity = doubleDensity;
        reset();
    }

    public void reset() {
        setCommand(DiskCommand.NONE);
        dataAddressMark = false;
    }

    public void setCommand(DiskCommand command) {
        switch (command) {
            case NONE:
                dataAddressMark = false;
                break;

            case READ_ADDRESS:
                id.restorePastGap();
                id.next();
                break;

            case READ_SECTOR:
                data.restorePastGap();
                dataAddressMark = (data.read() == (byte) 0xFB);
                break;
        }

        this.command = command;
    }

    /**
     * Reads the address information (id field) for the sector. Reads from
     * the id field, while ignoring gap information.
     *
     * @return the next byte in the address to read
     */
    public byte readAddress() {
        return id.read();
    }

    public boolean dataAddressMarkFound() {
        return dataAddressMark;
    }

    public byte readSector() {
        return data.read();
    }

    public boolean hasMoreBytes() {
        return !command.equals(DiskCommand.NONE) && data.hasMoreBytes();
    }

    public boolean hasMoreIdBytes() {
        return !command.equals(DiskCommand.NONE) && id.hasMoreBytes();
    }
}
