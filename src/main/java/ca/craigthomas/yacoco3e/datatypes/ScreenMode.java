/*
 * Copyright (C) 2017-2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

import ca.craigthomas.yacoco3e.components.IOController;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class ScreenMode
{
    // The image that represents the screen contents
    protected BufferedImage backBuffer;
    // The scale factor applied to the screen
    protected int scale;
    // The width of the screen
    protected int width;
    // The height of the screen
    protected int height;
    // The offset where screen memory is located
    protected int memoryOffset;
    // The IO controller for the computer
    protected IOController io;

    public enum Mode {
        SG4, SG6, SG8, SG12
    }

    /**
     * Refreshes the content of the screen.
     */
    public abstract void refreshScreen();

    /**
     * Sets the IOController for the screen.
     *
     * @param ioController the IOController for the screen
     */
    public void setIOController(IOController ioController) {
        this.io = ioController;
    }

    /**
     * Generates a new backbuffer for the screen.
     */
    protected void createBackBuffer() {
        backBuffer = new BufferedImage(
                width * scale,
                height * scale,
                BufferedImage.TYPE_4BYTE_ABGR);
    }

    /**
     * Returns the current back buffer.
     *
     * @return the current back buffer
     */
    public BufferedImage getBackBuffer() {
        return backBuffer;
    }

    /**
     * Low level routine to draw a pixel to the screen. Takes into account the
     * scaling factor applied to the screen. The top-left corner of the screen
     * is at coordinate (0, 0).
     *
     * @param x     The x coordinate to place the pixel
     * @param y     The y coordinate to place the pixel
     * @param color The Color of the pixel to draw
     */
    protected void drawPixel(int x, int y, Color color) {
        Graphics2D graphics = backBuffer.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(
                x * scale,
                y * scale,
                scale,
                scale);
        graphics.dispose();
    }

    /**
     * Returns the current scale factor.
     *
     * @return the current scale factor
     */
    public int getScale() {
        return scale;
    }

    /**
     * Returns the current width of the screen.
     *
     * @return the current width of the screen
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the current height of the screen.
     *
     * @return the current height of the screen
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the offset in Physical RAM where the screen buffer should
     * read from.
     *
     * @param offset the physical offset in ram (19-bit offset)
     */
    public void setMemoryOffset(int offset) {
        memoryOffset = offset;
    }

    /**
     * Returns true if the memory offset of the back buffer has changed.
     *
     * @return true if the memory offset window has changed
     */
    public int getMemoryOffset() {
        return memoryOffset;
    }
}


