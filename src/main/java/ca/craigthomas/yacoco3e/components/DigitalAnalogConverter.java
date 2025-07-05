/*
 * Copyright (C) 2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.util.Arrays;

public class DigitalAnalogConverter extends Thread
{
    public static final int LINE_BUFFER_SIZE = 2000;
    public static final int AUDIO_BUFFER_SIZE = 100;
    public static final float AUDIO_PLAYBACK_RATE = 44100.0f;

    protected AudioFormat audioFormat;
    protected SourceDataLine audioOutputLine;
    protected volatile byte [] audioBuffer;
    protected Cassette cassette;
    protected volatile byte sample;
    protected boolean play;
    protected boolean running;

    public DigitalAnalogConverter() {
        sample = 0;
        audioFormat = new AudioFormat(AUDIO_PLAYBACK_RATE, 8, 1, true, false);
        audioBuffer = new byte [AUDIO_BUFFER_SIZE];
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        if (!AudioSystem.isLineSupported(dataLineInfo)) {
            System.out.println("SourceDataLine type not supported");
        }
        try {
            audioOutputLine = AudioSystem.getSourceDataLine(audioFormat);
            audioOutputLine.open(audioFormat, LINE_BUFFER_SIZE);
            audioOutputLine.start();
            System.out.println("Instantiated DAC");
        } catch (Exception e) {
            System.out.println("Could not get source data line");
            audioOutputLine = null;
        }
        play = true;
    }

    public void writeByte(byte digitalSample) {
        if (digitalSample == 0) {
            setPlay(false);
            return;
        }
        setPlay(true);
        Arrays.fill(audioBuffer, digitalSample);
    }

    public void setPlay(boolean newPlayStatus) {
        play = newPlayStatus;
        if (!play && audioOutputLine != null) {
            audioOutputLine.flush();
        }
    }

    public void stopRunning() {
        running = false;
    }

    public void run() {
        running = true;
        while (running) {
            if (play && audioOutputLine != null) {
//                audioOutputLine.flush();
                audioOutputLine.write(audioBuffer, 0, AUDIO_BUFFER_SIZE);
            }
        }
        shutdown();
    }

    /**
     * Close down audio resources.
     */
    public void shutdown() {
        if (audioOutputLine != null) {
            audioOutputLine.flush();
            audioOutputLine.stop();
            audioOutputLine.close();
        }
    }
}
