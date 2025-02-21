/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes.screen;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

import java.awt.*;

public class G6CScreenMode extends ScreenMode
{
    /* Screen size for the mode */
    private static final int SCREEN_WIDTH = 320;
    private static final int SCREEN_HEIGHT = 240;

    /* Block definitions */
    private static final int BLOCK_WIDTH = 2;
    private static final int BLOCK_HEIGHT = 1;
    private static final int BLOCKS_PER_BYTE = 4;

    /* Color definitions for graphics G6C mode */
    private final Color colors[][] = {
        {
            // Color Mode 0
            new Color(40, 224, 40, 255),   /* Green */
            new Color(240, 240, 112, 255), /* Yellow */
            new Color(32, 32, 216, 255),   /* Blue */
            new Color(168, 32, 32, 255),   /* Red */
        }, {
            // Color Mode 1
            new Color(240, 240, 240, 255), /* White */
            new Color(40, 168, 168, 255),  /* Cyan */
            new Color(211, 97, 250, 255),  /* Magenta */
            new Color(240, 136, 40, 255),  /* Orange */
        }
    };

    // The color mode to apply
    private int colorMode;

    public G6CScreenMode(int scale, int colorMode) {
        this.scale = scale;
        this.width = SCREEN_WIDTH;
        this.height = SCREEN_HEIGHT;
        this.colorMode = colorMode;
        createBackBuffer();
    }

    @Override
    public void refreshScreen() {
        Graphics2D graphics = backBuffer.createGraphics();
        graphics.setColor(colors[colorMode][0]);
        graphics.fillRect(0, 0, width * scale, height * scale);

        int memoryPointer = memoryOffset;

        for (int y = 0; y < 192; y++) {
            for (int x = 0; x < (128 / BLOCKS_PER_BYTE); x++) {
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
        int x = 32 + (col * (BLOCK_WIDTH * BLOCKS_PER_BYTE));
        int y = 24 + (row * BLOCK_HEIGHT);

        /* Pixel 1 */
        int color = (value.getShort() & 0xC0) >> 6;
        drawBlock(x, y, color);

        /* Pixel 2 */
        color = (value.getShort() & 0x30) >> 4;
        drawBlock(x + BLOCK_WIDTH, y, color);

        /* Pixel 3 */
        color = (value.getShort() & 0xC) >> 2;
        drawBlock(x + (BLOCK_WIDTH * 2), y, color);

        /* Pixel 4 */
        color = (value.getShort() & 0x3);
        drawBlock(x + (BLOCK_WIDTH * 3), y, color);
    }
}
