/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes.screen;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

import java.awt.*;

public class G2RScreenMode extends ScreenMode
{
    /* Screen size for the mode */
    private static final int SCREEN_WIDTH = 320;
    private static final int SCREEN_HEIGHT = 240;

    /* Block definitions */
    private static final int BLOCK_WIDTH = 2;
    private static final int BLOCK_HEIGHT = 2;
    private static final int PIXELS_PER_BYTE = 8;

    /* Color definitions for graphics G2R mode */
    private final Color colors[][] = {
        {
            // Color Mode 0
            new Color(0, 0, 0, 255),        /* Black */
            new Color(40, 224, 40, 255),    /* Green */
        }, {
            // Color Mode 1
            new Color(0, 0, 0, 255),        /* Black */
            new Color(240, 240, 240, 255),  /* White */
        }
    };

    // The color mode to apply
    private int colorMode;

    public G2RScreenMode(int scale, int colorMode) {
        this.scale = scale;
        this.width = SCREEN_WIDTH;
        this.height = SCREEN_HEIGHT;
        this.colorMode = colorMode;
        createBackBuffer();
    }

    @Override
    public void refreshScreen() {
        Graphics2D graphics = backBuffer.createGraphics();
        graphics.setColor(colors[colorMode][1]);
        graphics.fillRect(0, 0, width * scale, height * scale);

        int memoryPointer = memoryOffset;

        for (int y = 0; y < 96; y++) {
            for (int x = 0; x < 16; x++) {
                UnsignedByte value = io.readPhysicalByte(memoryPointer);
                drawCharacter(value, x, y);
                memoryPointer++;
            }
        }

        graphics.dispose();
    }

    private void drawBlock(int col, int row, int color) {
        for (int x = col; x < col + BLOCK_WIDTH; x++) {
            for (int y = row; y < row + BLOCK_HEIGHT; y++) {
                drawPixel(x, y, colors[colorMode][color]);
            }
        }
    }

    private void drawCharacter(UnsignedByte value, int col, int row) {
        /* Translated position in pixels */
        int x = 32 + (col * (PIXELS_PER_BYTE * BLOCK_WIDTH));
        int y = 24 + (row * BLOCK_HEIGHT);

        /* Pixel 1 */
        int color = (value.getShort() & 0x80) >> 7;
        drawBlock(x, y, color);

        /* Pixel 2 */
        color = (value.getShort() & 0x40) >> 6;
        drawBlock(x + BLOCK_WIDTH, y, color);

        /* Pixel 3 */
        color = (value.getShort() & 0x20) >> 5;
        drawBlock(x + (2 * BLOCK_WIDTH), y, color);

        /* Pixel 4 */
        color = (value.getShort() & 0x10) >> 4;
        drawBlock(x + (3 * BLOCK_WIDTH), y, color);

        /* Pixel 5 */
        color = (value.getShort() & 0x8) >> 3;
        drawBlock(x + (4 * BLOCK_WIDTH), y, color);

        /* Pixel 6 */
        color = (value.getShort() & 0x4) >> 2;
        drawBlock(x + (5 * BLOCK_WIDTH), y, color);

        /* Pixel 7 */
        color = (value.getShort() & 0x2) >> 1;
        drawBlock(x + (6 * BLOCK_WIDTH), y, color);

        /* Pixel 8 */
        color = (value.getShort() & 0x1);
        drawBlock(x + (7 * BLOCK_WIDTH), y, color);
    }
}
