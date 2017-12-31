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
    private Field gap4;

    private boolean doubleDensity;

    private DiskCommand command;

    private boolean dataAddressMark;

    private FIELD currentField;

    public enum FIELD {
        GAP1, ID, GAP2, GAP3, DATA, GAP4, NONE
    }

    public class Field {
        private byte [] data;
        private int gapSize;
        private int pointer;
        private int oldPointer;

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

        public void push() {
            oldPointer = pointer;
        }

        public void pop() {
            pointer = oldPointer;
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

        /**
         * Fills the data portion with zeros.
         */
        public void zeroFill() {
            for (int i=0; i < data.length; i++) {
                data[i] = 0;
            }
        }
    }

    public DiskSector(boolean doubleDensity) {
        if (doubleDensity) {
            gap1 = new Field(12);
            id = new Field(7, 3);
            gap2 = new Field(22);
            gap3 = new Field(12);
            data = new Field(258, 4);
            gap4 = new Field(24);
        } else {
            gap1 = new Field(6);
            id = new Field(7);
            gap2 = new Field(11);
            gap3 = new Field(6);
            data = new Field(130, 1);
            gap4 = new Field(10);
        }
        this.doubleDensity = doubleDensity;
        reset();
    }

    public void reset() {
        setCommand(DiskCommand.NONE);
        dataAddressMark = false;
        currentField = FIELD.GAP1;
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

            case WRITE_SECTOR:
                data.zeroFill();
                data.restorePastGap();
                break;

            case READ_TRACK:
                gap1.restore();
                id.restore();
                gap2.restore();
                gap3.restore();
                data.restore();
                gap4.restore();
                currentField = FIELD.GAP1;
        }

        this.command = command;
    }

    public void writeData(byte value) {
        data.write(value);
    }

    /**
     * Writes out the gap2 field. Depending on whether it is double density or
     * not, will write out $4E or $FF.
     */
    public void writeGap2() {
        gap2.restore();
        while (gap2.hasMoreBytes()) {
            gap2.write(doubleDensity ? (byte) 0x4E : (byte) 0xFF);
        }
    }

    /**
     * Writes out the gap3 field. Always set to $00.
     */
    public void writeGap3() {
        gap3.restore();
        while (gap3.hasMoreBytes()) {
            gap3.write((byte) 0x0);
        }
    }

    public void writeDataMark(byte mark) {
        data.push();
        if (doubleDensity) {
            data.write((byte) 0xA1);
            data.write((byte) 0xA1);
            data.write((byte) 0xA1);
            data.write(mark);
        } else {
            data.write(mark);
        }
        data.pop();
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

    public byte readTrack() {
        if (currentField.equals(FIELD.GAP1) && gap1.hasMoreBytes()) {
            return gap1.read();
        } else {
            currentField = FIELD.ID;
        }

        if (currentField.equals(FIELD.ID) && id.hasMoreBytes()) {
            return id.read();
        } else {
            currentField = FIELD.GAP2;
        }

        if (currentField.equals(FIELD.GAP2) && gap2.hasMoreBytes()) {
            return gap2.read();
        } else {
            currentField = FIELD.GAP3;
        }

        if (currentField.equals(FIELD.GAP3) && gap3.hasMoreBytes()) {
            return gap3.read();
        } else {
            currentField = FIELD.DATA;
        }

        if (currentField.equals(FIELD.DATA) && data.hasMoreBytes()) {
            return data.read();
        } else {
            currentField = FIELD.GAP4;
        }

        if (currentField.equals(FIELD.GAP4) && gap4.hasMoreBytes()) {
            return gap4.read();
        } else {
            currentField = FIELD.NONE;
        }

        return 0;
    }

    public boolean readTrackFinished() {
        return currentField.equals(FIELD.NONE);
    }
}
