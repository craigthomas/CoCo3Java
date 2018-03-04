/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.common.Field;

public class DiskSector
{
    // Contains the index pulse information
    private Field index;
    // The gap between index and id
    private Field gap1;
    // The id information for the sector
    private Field id;
    // The gap between the id and the data
    private Field gap2;
    // The data on the sector
    private Field data;
    // The gap between the data and the final gap
    private Field gap3;
    // The final gap on the drive
    private Field gap4;
    // Whether the sector should be double density
    private boolean doubleDensity;
    // The command being run on the sector
    private DiskCommand command;
    // Whether the data address mark was found
    private boolean dataAddressMark;
    // The field the sector read/write head is currently pointing to
    protected FIELD currentField;
    // The byte the read/write head is currently pointing to
    private int pointer;

    /**
     * Stores information about which field the read/write head is
     * pointing to.
     */
    public enum FIELD {
        INDEX, GAP1, ID, GAP2, GAP3, DATA, GAP4, NONE
    }

    public DiskSector(boolean doubleDensity) {
        if (doubleDensity) {
            index = new Field(60, (short) 0x4E);
            gap1 = new Field(12, (short) 0x00);
            id = new Field(7, 3, (short) -1);
            gap2 = new Field(22, (short) 0x4E);
            gap3 = new Field(12, (short) 0x00);
            data = new Field(258, 4, (short) -1);
            gap4 = new Field(24, (short) 0x4E);
        } else {
            index = new Field(40);
            gap1 = new Field(6);
            id = new Field(7);
            gap2 = new Field(11);
            gap3 = new Field(6);
            data = new Field(130, 1, (short) -1);
            gap4 = new Field(10);
        }
        pointer = 0;
        this.doubleDensity = doubleDensity;
        reset();
    }

    /**
     * Resets any operation currently running on the sector. Will position
     * the field to the first gap, and will set the data address mark to
     * false.
     */
    public void reset() {
        setCommand(DiskCommand.NONE);
        dataAddressMark = false;
        currentField = FIELD.GAP1;
        pointer = 0;
    }

    /**
     * Returns the currently executing command.
     *
     * @return the command that is executing
     */
    public DiskCommand getCommand() {
        return command;
    }

    /**
     * Sets the command that the sector is currently running.
     *
     * @param command the command to run on the field
     */
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
                pointer = 0;
                byte tempByte = data.readAt(doubleDensity ? 3 : 0);
                dataAddressMark = tempByte == (byte) 0xFB;
                break;

            case WRITE_SECTOR:
                data.zeroFill();
                data.restorePastGap();
                pointer = 0;
                break;

            case READ_TRACK:
            case WRITE_TRACK:
                index.restore();
                gap1.restore();
                id.restore();
                gap2.restore();
                gap3.restore();
                data.restore();
                gap4.restore();
                currentField = FIELD.INDEX;
        }

        this.command = command;
    }

//    public void writeData(byte value) {
//        data.write(value);
//    }

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

    public byte readSectorData() {
        if (doubleDensity) {
            if (pointer < 256) {
                pointer++;
                return data.read();
            }
        } else {
            if (pointer < 128) {
                pointer++;
                return data.read();
            }
        }
        return 0;
    }

    public void writeSectorData(byte value) {
        if (doubleDensity) {
            if (pointer < 256) {
                data.write(value);
                pointer++;
            }
        } else {
            if (pointer < 128) {
                data.write(value);
                pointer++;
            }
        }
    }

    public boolean hasMoreIdBytes() {
        return !command.equals(DiskCommand.NONE) && id.hasMoreBytes();
    }

    public boolean hasMoreDataBytes() {
        if (!command.equals(DiskCommand.NONE)) {
            if (doubleDensity) {
                if (pointer < 256) {
                    return true;
                }
            } else {
                if (pointer < 128) {
                    return true;
                }
            }
        }
        return false;
    }

    public byte readTrack() {
        if (currentField.equals(FIELD.INDEX) && index.hasMoreBytes()) {
            return index.read();
        } else {
            currentField = FIELD.GAP1;
        }

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

    public void writeTrack(byte value) {
        if (currentField.equals(FIELD.INDEX) && index.hasMoreBytes() && index.isExpected(value)) {
            index.write(value);
            return;
        } else {
            currentField = FIELD.GAP1;
            index.setFilled();
        }

        if (currentField.equals(FIELD.GAP1) && gap1.hasMoreBytes() && gap1.isExpected(value)) {
            gap1.write(value);
            return;
        } else {
            currentField = FIELD.ID;
            gap1.setFilled();
        }

        if (currentField.equals(FIELD.ID) && id.hasMoreBytes()) {
            id.write(value);
            return;
        } else {
            currentField = FIELD.GAP2;
        }

        if (currentField.equals(FIELD.GAP2) && gap2.hasMoreBytes() && gap2.isExpected(value)) {
            gap2.write(value);
            return;
        } else {
            currentField = FIELD.GAP3;
            gap2.setFilled();
        }

        if (currentField.equals(FIELD.GAP3) && gap3.hasMoreBytes() && gap3.isExpected(value)) {
            gap3.write(value);
            return;
        } else {
            currentField = FIELD.DATA;
            gap3.setFilled();
        }

        if (currentField.equals(FIELD.DATA) && data.hasMoreBytes()) {
            data.write(value);
            return;
        } else {
            currentField = FIELD.GAP4;
        }

        if (currentField.equals(FIELD.GAP4) && gap4.hasMoreBytes() && gap4.isExpected(value)) {
            gap4.write(value);
            return;
        } else {
            currentField = FIELD.NONE;
            gap4.setFilled();
        }
    }

    public boolean writeTrackFinished() {
        return currentField.equals(FIELD.NONE);
    }

    public boolean readTrackFinished() {
        return currentField.equals(FIELD.NONE);
    }

    /**
     * Returns the logical sector identifier as written to by the OS.
     *
     * @return the sector identifier
     */
    public byte getSectorId() {
        if (doubleDensity) {
            return id.readAt(6);
        } else {
            return id.readAt(3);
        }
    }
}
