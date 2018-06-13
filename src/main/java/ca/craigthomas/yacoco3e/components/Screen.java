/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Screen
{
    // The current screen mode
    private ScreenMode screenMode;
    // The IO controller for the computer
    private IOController io;
    // Whether the resolution changed
    private boolean resolutionChanged;
    // The current memory offset for video
    private int memoryOffset;
    // The current scale factor
    private int scale;
    // The current mode
    private ScreenMode.Mode currentMode;
    // The saved color set
    private int colorSet;

    public Screen(int scale) {
        this.scale = scale;
        resolutionChanged = false;
        currentMode = ScreenMode.Mode.SG6;
        colorSet = 0;
        setMode(ScreenMode.Mode.SG4, colorSet);
    }

    /**
     * Sets the current video mode.
     *
     * @param mode the video mode to set
     */
    public void setMode(ScreenMode.Mode mode, int colorSet) {
        if (currentMode.equals(mode) && colorSet == this.colorSet) {
            return;
        }

        System.out.println("Set screen mode " + mode);
        switch (mode) {
            case SG4:
                this.screenMode = new SG4ScreenMode(scale);
                break;

            case SG6:
                this.screenMode = new SG6ScreenMode(scale, colorSet);
                break;

            case SG8:
                this.screenMode = new SG8ScreenMode(scale);
                break;

            case SG12:
                this.screenMode = new SG12ScreenMode(scale);
                break;

            case SG24:
                this.screenMode = new SG24ScreenMode(scale);
                break;

            case G1C:
                this.screenMode = new G1CScreenMode(scale, colorSet);
                break;

            case G1R:
                this.screenMode = new G1RScreenMode(scale, colorSet);
                break;

            case G2C:
                this.screenMode = new G2CScreenMode(scale, colorSet);
                break;

            case G2R:
                this.screenMode = new G2RScreenMode(scale, colorSet);
                break;

            case G3C:
                this.screenMode = new G3CScreenMode(scale, colorSet);
                break;

            case G3R:
                this.screenMode = new G3RScreenMode(scale, colorSet);
                break;

            case G6C:
                this.screenMode = new G6CScreenMode(scale, colorSet);
                break;

            case G6R:
                this.screenMode = new G6RScreenMode(scale, colorSet);
                break;

            default:
                break;
        }
        this.screenMode.setMemoryOffset(memoryOffset);
        this.screenMode.setIOController(io);
        this.colorSet = colorSet;
        resolutionChanged = true;
        currentMode = mode;
    }

    /**
     * Sets an IO controller.
     *
     * @param ioController the IO controller associated with the screen
     */
    public void setIOController(IOController ioController) {
        this.io = ioController;
        screenMode.setIOController(ioController);
    }

    /**
     * Sets the offset in Physical RAM where the screen buffer should
     * read from.
     *
     * @param offset the physical offset in ram (19-bit offset)
     */
    public void setMemoryOffset(int offset) {
        memoryOffset = offset;
        screenMode.setMemoryOffset(offset);
    }

    /**
     * Returns true if the memory offset of the backbuffer has changed.
     *
     * @return true if the memory offset window has changed
     */
    public int getMemoryOffset() {
        return screenMode.getMemoryOffset();
    }

    /**
     * Returns true if a video command changed the screen resolution.
     *
     * @return true if the resolution changed
     */
    public boolean getResolutionChanged() {
        return resolutionChanged;
    }

    /**
     * Acknowledges that the screen resolution changed.
     */
    public void clearResolutionChanged() {
        resolutionChanged = false;
    }

    /**
     * Refreshes the current screen.
     */
    public void refreshScreen() {
        screenMode.refreshScreen();
    }

    /**
     * Returns the current back buffer.
     *
     * @return the current back buffer
     */
    public BufferedImage getBackBuffer() {
        return screenMode.getBackBuffer();
    }

    /**
     * Returns the current scale factor.
     *
     * @return the current scale factor
     */
    public int getScale() {
        return screenMode.getScale();
    }

    /**
     * Returns the current width of the screen.
     *
     * @return the current width of the screen
     */
    public int getWidth() {
        return screenMode.getWidth();
    }

    /**
     * Returns the current height of the screen.
     *
     * @return the current height of the screen
     */
    public int getHeight() {
        return screenMode.getHeight();
    }
}
