/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.common;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;

public class IOTest
{
    private String testStreamFileBytes = "This is a test";

    @Test
    public void testLoadStreamReturnsZeroLengthArrayOnNull() {
        byte[] result = IO.loadStream(null);
        assertEquals(0, result.length);
    }

    @Test
    public void testLoadStreamReturnsZeroLengthArrayOnClosedStream() throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("test_stream_file.bin");
        stream.close();
        byte[] result = IO.loadStream(stream);
        assertEquals(0, result.length);
    }

    @Test
    public void testLoadStreamReturnsCorrectBytes() {
        byte[] expected = testStreamFileBytes.getBytes();
        byte[] result = IO.loadStream(getClass().getClassLoader().getResourceAsStream("test_stream_file.bin"));
        assertNotNull(result);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testOpenInputStreamReturnsNotNull() {
        File resourceFile = new File(getClass().getClassLoader().getResource("test_stream_file.bin").getFile());
        InputStream result = IO.openInputStream(resourceFile.getPath());
        assertNotNull(result);
    }

    @Test
    public void testOpenInputStreamReturnsNullWithBadFilename() {
        InputStream result = IO.openInputStream("this_file_does_not_exist.bin");
        assertNull(result);
    }

    @Test
    public void testOpenOutputStreamReturnsNotNull() {
        OutputStream result = IO.openOutputStream("this_file_should_be_okay.bin");
        assertNotNull(result);
    }

    @Test
    public void testOpenOutputStreamReturnsNull() {
        OutputStream result = IO.openOutputStream("/this_directory_does_not_exist/this_file_should_not_be_okay.bin");
        assertNull(result);
    }

    @Test
    public void testFlushToStreamWorksCorrectly() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(14);
        byte[] output = testStreamFileBytes.getBytes();
        boolean returnCode = IO.flushToStream(stream, output);
        assertTrue(returnCode);
        String result = stream.toString();
        assertEquals(testStreamFileBytes, result);
    }

    @Test
    public void testFlushToStreamFailsOnNullStream() {
        byte[] output = testStreamFileBytes.getBytes();
        boolean returnCode = IO.flushToStream(null, output);
        assertFalse(returnCode);
    }

    @Test
    public void testCloseStreamWorksCorrectly() throws IOException {
        ByteArrayOutputStream stream = spy(ByteArrayOutputStream.class);
        IO.closeStream(stream);
        Mockito.verify(stream).close();
    }

    @Test
    public void testCloseStreamOnNullStreamDoesNotThrowException() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(14);
        IO.closeStream(stream);
    }

    @Test
    public void testCopyByteArrayFailsWhenSourceIsNull() {
        short[] target = new short[14];
        assertFalse(IO.copyByteArrayToShortArray(null, target));
    }

    @Test
    public void testCopyByteArrayFailsWhenTargetIsNull() {
        byte[] source = new byte[14];
        assertFalse(IO.copyByteArrayToShortArray(source, null));
    }

    @Test
    public void testCopyByteArrayFailsWhenSourceBiggerThanTarget() {
        byte[] source = new byte[14];
        short[] target = new short[10];
        assertFalse(IO.copyByteArrayToShortArray(source, target));
    }

    @Test
    public void testCopyByteArrayWorksCorrectly() {
        short[] expected = {0x54, 0x68, 0x69, 0x73, 0x20, 0x69, 0x73, 0x20, 0x61, 0x20, 0x74, 0x65, 0x73, 0x74};
        byte[] source = testStreamFileBytes.getBytes();
        short[] target = new short[14];
        assertTrue(IO.copyByteArrayToShortArray(source, target));
        assertArrayEquals(expected, target);
    }
}
