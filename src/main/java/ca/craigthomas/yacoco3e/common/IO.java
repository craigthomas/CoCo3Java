/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.common;

import ca.craigthomas.yacoco3e.components.Memory;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.ByteOrder;
import java.util.logging.Logger;

/**
 * The IO class provides support for reading and writing various
 * streams.
 */
public class IO
{
    // The logger for the class
    private final static Logger LOGGER = Logger.getLogger(Memory.class.getName());

    /**
     * Loads a stream into an array of bytes.
     *
     * @param stream the stream to read from
     * @return an array of bytes, null on error
     */
    public static byte[] loadStream(InputStream stream) {
        if (stream == null) {
            LOGGER.severe("Error reading input stream: stream is empty");
            return null;
        }

        try {
            byte[] data;

            /* Determine appropriate endianess */
            if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
                byte[] bytes = new byte[2];
                int bytesRead = stream.read(bytes);
                while (bytesRead != -1) {
                    tempStream.write(bytes);
                    bytesRead = stream.read(bytes);
                }
                data = tempStream.toByteArray();
                tempStream.close();
                stream.close();
            } else {
                data = IOUtils.toByteArray(stream);
                stream.close();
            }
            return data;
        } catch (Exception e) {
            LOGGER.severe("Error reading from stream: " + e.getMessage());
            return null;
        }
    }

    /**
     * Attempts to open the specified filename as an InputStream. Will return null if there is
     * an error.
     *
     * @param filename The String containing the full path to the filename to open
     * @return An opened InputStream, or null if there is an error
     */
    public static InputStream openInputStream(String filename) {
        try {
            return new FileInputStream(new File(filename));
        } catch (FileNotFoundException e) {
            LOGGER.severe("Error opening file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Attempts to open the specified filename as an OutputStream. Will return null if there is
     * an error.
     *
     * @param filename The String containing the full path to the filename to open
     * @return An opened OutputStream, or null if there is an error
     */
    public static OutputStream openOutputStream(String filename) {
        try {
            return new FileOutputStream(new File(filename));
        } catch (FileNotFoundException e) {
            LOGGER.severe("Error creating file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Flushes an array of bytes to an OutputStream. Will return true if the flush
     * succeeded, or false on failure.
     *
     * @param stream the OutputStream to flush to
     * @param byteArray the array of bytes to flush
     * @return true if the flush succeeded, false otherwise
     */
    public static boolean flushToStream(OutputStream stream, byte[] byteArray) {
        try {
            stream.write(byteArray);
        } catch(Exception e) {
            LOGGER.severe("Error flushing buffer to file: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Closes an open input or output stream.
     *
     * @param stream the stream to close
     */
    public static void closeStream(Closeable stream) {
        try {
            stream.close();
        } catch (Exception e) {
            LOGGER.severe("Error closing stream: " + e.getMessage());
        }
    }

    /**
     * Copies an array of bytes to an array of shorts.
     *
     * @param source the source byte array
     * @param target the target short array
     * @return true if the source was not null, false otherwise
     */
    public static boolean copyByteArrayToShortArray(byte[] source, short[] target) {
        int byteCounter = 0;

        if (source == null || target == null) {
            return false;
        }

        if (source.length > target.length) {
            return false;
        }

        for (byte data : source) {
            target[byteCounter] = data;
            byteCounter++;
        }
        return true;
    }
}
