/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

import java.awt.*;

public class SG8ScreenMode extends ScreenMode
{
    // The foreground color
    private int foreColor;
    // The background color
    private int backColor;

    public SG8ScreenMode(int scale) {
        this.scale = scale;
        this.width = SCREEN_WIDTH;
        this.height = SCREEN_HEIGHT;
        foreColor = GREEN;
        backColor = BLACK;
        createBackBuffer();
    }

    @Override
    public void refreshScreen() {
        Graphics2D graphics = backBuffer.createGraphics();
        graphics.setColor(colors[backColor]);
        graphics.fillRect(0, 0, width * scale, height * scale);

        graphics.setColor(colors[foreColor]);
        graphics.fillRect(
                32 * scale,
                24 * scale,
                256 * scale,
                192 * scale
        );

        int memoryPointer = memoryOffset;
        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 16; x++) {
                UnsignedByte value = io.readPhysicalByte(memoryPointer);
                drawCharacter(value, x, y);
                memoryPointer++;
            }
        }

        graphics.dispose();
    }

    /**
     * Draws an SG4 block. Blocks are of width by height in size and
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
     * Draw a Semi Graphics 6 character on the screen at the specified
     * column and row. Each character is 8 x 6 in size. The character
     * cell is further broken down into 2 subcells of 4 x 3 blocks each
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
        int x = 32 + (col * 8);
        int y = 24 + (row * 6);

        /* Background colors */
        int back = backColor;
        int color = (value.getShort() & 0x70) >> 4;

        /* Subcell B */
        int on = value.isMasked(0x8) ? 1 : 0;
        drawBlock(x, y, on == 1 ? color : back);

        /* Subcell B */
        on = value.isMasked(0x4) ? 1 : 0;
        drawBlock(x + BLOCK_WIDTH, y, on == 1 ? color : back);
    }

    /* Semi-graphics color constants */
    private static final int GREEN = 0;
    private static final int BLACK = 8;

    /* Screen size for semi-graphics 4 mode */
    private static final int SCREEN_WIDTH = 320;
    private static final int SCREEN_HEIGHT = 240;

    /* Block definitions */
    private static final int BLOCK_WIDTH = 4;
    private static final int BLOCK_HEIGHT = 3;

    /* Color definitions for semi-graphics 8 mode */
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
}
