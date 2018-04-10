/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

import ca.craigthomas.yacoco3e.common.IO;
import ca.craigthomas.yacoco3e.components.DiskCommand;
import ca.craigthomas.yacoco3e.components.DiskDrive;
import ca.craigthomas.yacoco3e.components.DiskTrack;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public class JV1Disk implements VirtualDisk
{
    // A byte array that holds the raw file data
    private byte [] data;
    // A pointer into the raw file data
    private int pointer;
    // A logger for the class
    private final static Logger LOGGER = Logger.getLogger(DiskDrive.class.getName());

    public JV1Disk() {
        pointer = 0;
    }

    /**
     * The JV1 file format is a flat file that contains 35 tracks
     * with 18 sectors per track, with 256 bytes per sector. This
     * means that files are exactly 161,280 bytes long. The check
     * simply queries the file to ensure the length matches what
     * it should be. Will return true if the file is 161,280 bytes
     * long, false otherwise.
     *
     * @param diskFile the File where the disk image is located
     * @return true if the file contains the JV1 format
     */
    @Override
    public boolean isCorrectFormat(File diskFile) {
        return diskFile.length() == 161280;
    }

    @Override
    public boolean loadFile(String diskFilename) {
        InputStream stream = IO.openInputStream(diskFilename);
        if (stream == null) {
            return false;
        }
        data = IO.loadStream(stream);
        IO.closeStream(stream);
        return true;
    }

    @Override
    public DiskTrack[] readTracks() {
        DiskTrack [] tracks = new DiskTrack[DiskDrive.DEFAULT_NUM_TRACKS];
        for (int i = 0; i < DiskDrive.DEFAULT_NUM_TRACKS; i++) {
            tracks[i] = new DiskTrack(DiskDrive.DEFAULT_SECTORS_PER_TRACK, true);
        }

        // Write out each track
        for (int trackNum = 0; trackNum < DiskDrive.DEFAULT_NUM_TRACKS; trackNum++) {
            for (int sectorNum = 0; sectorNum < DiskDrive.DEFAULT_SECTORS_PER_TRACK; sectorNum++) {
                System.out.println("Writing track " + trackNum + ", sector " + sectorNum);
                tracks[trackNum].setCommand(sectorNum, DiskCommand.LOAD_VIRTUAL_DISK);

                // Write out the sector ID
                tracks[trackNum].writeSectorId(sectorNum, new UnsignedByte(0xA1));
                tracks[trackNum].writeSectorId(sectorNum, new UnsignedByte(0xA1));
                tracks[trackNum].writeSectorId(sectorNum, new UnsignedByte(0xA1));
                tracks[trackNum].writeSectorId(sectorNum, new UnsignedByte(0xFE));

                // Track Number
                tracks[trackNum].writeSectorId(sectorNum, new UnsignedByte(trackNum));

                // Side number
                tracks[trackNum].writeSectorId(sectorNum, new UnsignedByte(0x00));

                // Sector number
                tracks[trackNum].writeSectorId(sectorNum, new UnsignedByte(sectorNum + 1));

                // Length of sector
                tracks[trackNum].writeSectorId(sectorNum, new UnsignedByte(255));

                // CRC 1 and 2
                tracks[trackNum].writeSectorId(sectorNum, new UnsignedByte(0x00));
                tracks[trackNum].writeSectorId(sectorNum, new UnsignedByte(0x00));

                // Write out sector data
                tracks[trackNum].writeData(sectorNum, new UnsignedByte(0xA1));
                tracks[trackNum].writeData(sectorNum, new UnsignedByte(0xA1));
                tracks[trackNum].writeData(sectorNum, new UnsignedByte(0xA1));
                tracks[trackNum].writeData(sectorNum, new UnsignedByte(0xFB));

                // Write out raw data
                for (int j = 0; j < 256; j++) {
                    tracks[trackNum].writeData(sectorNum, new UnsignedByte(data[pointer]));
                    pointer++;
                }

                // Write out CRC 1 and 2
                tracks[trackNum].writeData(sectorNum, new UnsignedByte(0x00));
                tracks[trackNum].writeData(sectorNum, new UnsignedByte(0x00));

                tracks[trackNum].setCommand(sectorNum, DiskCommand.NONE);
            }
        }
        pointer = 0;
        return tracks;
    }

    @Override
    public void writeTracks(File diskFile) {
    }

    @Override
    public int tracksPerDisk() {
        return DiskDrive.DEFAULT_NUM_TRACKS;
    }

    @Override
    public int sectorsPerTrack() {
        return DiskDrive.DEFAULT_SECTORS_PER_TRACK;
    }
}
