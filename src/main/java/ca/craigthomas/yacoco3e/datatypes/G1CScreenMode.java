/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

import java.awt.*;

public class G1CScreenMode extends ScreenMode
{
    /* Semi-graphics color constants */
    private static final int GREEN = 0;
    private static final int WHITE = 4;

    /* Screen size for the mode */
    private static final int SCREEN_WIDTH = 320;
    private static final int SCREEN_HEIGHT = 240;

    /* Block definitions */
    private static final int BLOCK_WIDTH = 4;
    private static final int BLOCK_HEIGHT = 3;

    /* Color definitions for graphics G1C mode */
    private final Color colors[] = {
            new Color(40, 224, 40, 255),   /* Green */
            new Color(240, 240, 112, 255), /* Yellow */
            new Color(32, 32, 216, 255),   /* Blue */
            new Color(168, 32, 32, 255),   /* Red */
            new Color(240, 240, 240, 255), /* White */
            new Color(40, 168, 168, 255),  /* Cyan */
            new Color(211, 97, 250, 255),  /* Magenta */
            new Color(240, 136, 40, 255),  /* Orange */
            new Color(0, 0, 0, 255)        /* Black */
    };

    // The background color
    private int backColor;
    // The color mode to apply
    private int colorMode;

    public G1CScreenMode(int scale, int colorMode) {
        this.scale = scale;
        this.width = SCREEN_WIDTH;
        this.height = SCREEN_HEIGHT;
        this.colorMode = colorMode;
        if (colorMode == 0) {
            backColor = GREEN;
        } else {
            backColor = WHITE;
        }
        createBackBuffer();
    }

    @Override
    public void refreshScreen() {
        Graphics2D graphics = backBuffer.createGraphics();
        graphics.setColor(colors[backColor]);
        graphics.fillRect(0, 0, width * scale, height * scale);

        int memoryPointer = memoryOffset;

        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 16; x++) {
                UnsignedByte value = io.readPhysicalByte(memoryPointer);
                drawCharacter(value, x, y);
                memoryPointer++;
            }
        }

        graphics.dispose();
    }

    /**
     * Draws an SG24 block. Blocks are of width by height in size and
     * have a foreground color.
     *
     * @param col the column to draw the block in
     * @param row the row to draw the block in
     * @param color the color of the block
     */
    private void drawBlock(int col, int row, int color) {
        for (int x = col; x < col + BLOCK_WIDTH; x++) {
            for (int y = row; y < row + BLOCK_HEIGHT; y++) {
                drawPixel(x, y, colors[color]);
            }
        }
    }

    /**
     * Draw a Semi Graphics 24 character on the screen at the specified
     * column and row. Each character is 4 x 1 in size. The character
     * cell is further broken down into 2 subcells of 2 x 1 blocks each
     * in the following configuration:
     *
     *    +----+----+
     *    |  A |  B |
     *    +----+----+
     *
     * @param value the value of the byte to write
     * @param col the column to write at
     * @param row the row to write at
     */
    private void drawCharacter(UnsignedByte value, int col, int row) {
        /* Translated position in pixels */
        int x = 32 + (col * 16);
        int y = 24 + (row * BLOCK_HEIGHT);

        /* Pixel 1 */
        int color = (value.getShort() & 0xC0) >> 6;
        color = colorMode == 0 ? color : color + 4;
        drawBlock(x, y, color);

        /* Pixel 2 */
        color = (value.getShort() & 0x30) >> 4;
        color = colorMode == 0 ? color : color + 4;
        drawBlock(x + BLOCK_WIDTH, y, color);

        /* Pixel 3 */
        color = (value.getShort() & 0xC) >> 2;
        color = colorMode == 0 ? color : color + 4;
        drawBlock(x + (BLOCK_WIDTH * 2), y, color);

        /* Pixel 4 */
        color = (value.getShort() & 0x3);
        color = colorMode == 0 ? color : color + 4;
        drawBlock(x + (BLOCK_WIDTH * 3), y, color);
    }
}
