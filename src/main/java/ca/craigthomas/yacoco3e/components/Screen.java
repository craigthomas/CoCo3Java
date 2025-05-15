/*
 * Copyright (C) 2017-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.screen.*;
import ca.craigthomas.yacoco3e.datatypes.screen.ScreenMode.Mode;

import java.awt.image.BufferedImage;

public class Screen
{
    private ScreenMode screenMode;
    private IOController io;
    private boolean resolutionChanged;
    private int memoryOffset;
    private int scale;
    private int colorSet;
    private ScreenMode.Mode currentMode;

    public Screen(int newScale) {
        scale = newScale;
        setMode(Mode.SG4, 0);
    }

    /**
     * Sets the current video mode.
     *
     * @param mode the video mode to set
     * @param newColorSet the new color set to use
     */
    public void setMode(ScreenMode.Mode mode, int newColorSet) {
        if (currentMode == mode && colorSet == newColorSet) {
            return;
        }

        colorSet = newColorSet;
        currentMode = mode;

        switch (mode) {
            case SG4:
                screenMode = new SG4ScreenMode(scale);
                break;

            case SG6:
                screenMode = new SG6ScreenMode(scale, colorSet);
                break;

            case SG8:
                screenMode = new SG8ScreenMode(scale);
                break;

            case SG12:
                screenMode = new SG12ScreenMode(scale);
                break;

            case SG24:
                screenMode = new SG24ScreenMode(scale);
                break;

            case G1C:
                screenMode = new G1CScreenMode(scale, colorSet);
                break;

            case G1R:
                screenMode = new G1RScreenMode(scale, colorSet);
                break;

            case G2C:
                screenMode = new G2CScreenMode(scale, colorSet);
                break;

            case G2R:
                screenMode = new G2RScreenMode(scale, colorSet);
                break;

            case G3C:
                screenMode = new G3CScreenMode(scale, colorSet);
                break;

            case G3R:
                screenMode = new G3RScreenMode(scale, colorSet);
                break;

            case G6C:
                screenMode = new G6CScreenMode(scale, colorSet);
                break;

            case G6R:
                screenMode = new G6RScreenMode(scale, colorSet);
                break;

            default:
                break;
        }
        screenMode.setMemoryOffset(memoryOffset);
        screenMode.setIOController(io);
        resolutionChanged = true;
    }

    /**
     * Sets an IO controller.
     *
     * @param ioController the IO controller associated with the screen
     */
    public void setIOController(IOController ioController) {
        io = ioController;
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
     * Returns the area in physical memory where the screen should draw from.
     *
     * @return the offset in physical memory where the screen should draw from
     */
    public int getMemoryOffset() {
        return memoryOffset;
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
