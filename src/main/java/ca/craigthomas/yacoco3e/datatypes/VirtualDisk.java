/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

import ca.craigthomas.yacoco3e.components.DiskTrack;

import java.io.File;

public interface VirtualDisk
{
    /**
     * Will return true if the format of the file matches the type
     * of virtual disk that the interface supports.
     *
     * @param diskFile the File where the disk image is located
     * @return true if the file supports the specified format
     */
    boolean isCorrectFormat(File diskFile);

    /**
     * Loads a virtual disk drive. Will return true if the load worked
     * successfully, false otherwise.
     *
     * @param diskFilename the name of the disk file to read
     * @return true if the load succeeded, false otherwise
     */
    boolean loadFile(String diskFilename);

    /**
     * Returns an array of DiskTrack objects that contain information
     * from the disk.
     *
     * @return an array of DiskTracks that contain the disk information
     */
    DiskTrack[] readTracks();

    /**
     * Returns the total number of tracks on the virtual disk.
     *
     * @return the total number of tracks on the disk
     */
    int tracksPerDisk();

    /**
     * Returns the total number of sectors per track.
     *
     * @return the total number of sectors per track
     */
    int sectorsPerTrack();
}
