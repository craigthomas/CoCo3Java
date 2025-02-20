/*
 * Copyright (C) 2018 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes.screen;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;

import java.awt.*;

public class SG6ScreenMode extends ScreenMode
{
    /* Semi-graphics color constants */
    private static final int GREEN = 0;
    private static final int BLACK = 8;

    /* Screen size for semi-graphics 4 mode */
    private static final int SCREEN_WIDTH = 320;
    private static final int SCREEN_HEIGHT = 240;

    /* Block definitions */
    private static final int BLOCK_WIDTH = 4;
    private static final int BLOCK_HEIGHT = 4;

    /* Color definitions for semi-graphics 4 mode */
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

    // The foreground color
    private int foreColor;
    // The background color
    private int backColor;
    // The color set to use
    private int colorSet;

    public SG6ScreenMode(int scale, int colorSet) {
        this.scale = scale;
        this.width = SCREEN_WIDTH;
        this.height = SCREEN_HEIGHT;
        this.colorSet = colorSet;
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
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 32; x++) {
                UnsignedByte value = io.readPhysicalByte(memoryPointer);
                drawCharacter(value, x, y);
                memoryPointer++;
            }
        }

        graphics.dispose();
    }

    /**
     * Draws an SG6 block. Blocks are of width by height in size and
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
     * column and row. Each character is 8 x 12 in size. The character
     * cell is further broken down into 6 subcells of 4 x 4 blocks each
     * in the following configuration:
     *
     *    +----+----+
     *    |  A |  B |
     *    +----+----+
     *    |  C |  D |
     *    +----+----+
     *    |  E |  F |
     *    +----+----+
     *
     * @param value the value of the byte to write
     * @param col the column to write at
     * @param row the row to write at
     */
    private void drawCharacter(UnsignedByte value, int col, int row) {
        /* Translated position in pixels */
        int x = 32 + (col * 8);
        int y = 24 + (row * 12);

        /* Background colors */
        int back = BLACK;

        int color = (value.getShort() & 0xC0) >> 6;
        if (colorSet == 1) {
            color = color + 4;
        }

        /* Subcell A */
        int on = value.isMasked(0x20) ? 1 : 0;
        drawBlock(x, y, on == 1 ? color : back);

        /* Subcell B */
        on = value.isMasked(0x10) ? 1 : 0;
        drawBlock(x + BLOCK_WIDTH, y, on == 1 ? color : back);

        /* Subcell C */
        on = value.isMasked(0x8) ? 1 : 0;
        drawBlock(x, y + BLOCK_HEIGHT, on == 1 ? color : back);

        /* Subcell D */
        on = value.isMasked(0x4) ? 1 : 0;
        drawBlock(x + BLOCK_WIDTH, y + BLOCK_HEIGHT, on == 1 ? color : back);

        /* Subcell E */
        on = value.isMasked(0x2) ? 1 : 0;
        drawBlock(x, y + (BLOCK_HEIGHT * 2), on == 1 ? color : back);

        /* Subcell F */
        on = value.isMasked(0x1) ? 1 : 0;
        drawBlock(x + BLOCK_WIDTH, y + (BLOCK_HEIGHT * 2), on == 1 ? color : back);
    }
}
