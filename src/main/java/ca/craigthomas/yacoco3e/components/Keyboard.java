/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import ca.craigthomas.yacoco3e.datatypes.UnsignedWord;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * This class records key-presses on the emulated keyboard, and stores
 * them in the correct memory locations for I/O routines.
 *
 * The keys on the COCO3 keyboard are as follows (symbols in brackets
 * represent shifted values):
 *
 * 1(!)  2(")  3(#)  4($)  5(%)  6(&)  7(')  8(()  9())  0( )  :(*)  -(=)
 * ALT   Q     W     E     R     T     Y     U     I     O     P     @
 * CTRL  A     S     D     F     G     H     J     L     ;(+)  ENTER
 * SHIFT Z     X     C     V     B     N     M     ,(<)  .(>)  /(?)  SHIFT
 * UP    DOWN  LEFT  RIGHT F1    F2    BREAK ESC
 *
 * A single word is used to encode what keys are pressed. The HIGH byte of
 * the word represents the ROW of the key in the table below. The LOW byte
 * of the word represents the COLUMN of the key in the table below. To get
 * the value of the keyboard, this keyboard word is available at memory
 * addresses: FF00 (HIGH) and FF02 (LOW).
 *
 * HIGH
 *   7
 *   6   SHIFT   F2    F1   CTRL   ALT   BRK   CLR   ENTER
 *   5     /     .     -     ,      ;     :     9      8
 *   4     7     6     5     4      3     2     1      0
 *   3   SPACE   RIGHT LEFT  DOW    UP    z     y      x
 *   2     w     v     u     t      s     r     q      p
 *   1     o     n     m     l      k     j     i      h
 *   0     g     f     e     d      c     b     a      @
 *
 *         7     6     5     4      3     2     1      0
 *                          LOW
 *
 *  The keypresses on the CoCo are recorded as active low values, therefore
 *  the byte values are inversed.
 */
public class Keyboard extends KeyAdapter
{
    private UnsignedByte highByte;
    private UnsignedByte lowByte;

    public Keyboard() {
        super();
        this.highByte = new UnsignedByte(0x0);
        this.lowByte = new UnsignedByte(0x0);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyPressed = e.getKeyCode();

        switch (keyPressed) {

            /* SHIFT */
            case KeyEvent.VK_SHIFT:
                highByte.or(0x40);
                lowByte.or(0x80);
                break;

            /* F2 */
            case KeyEvent.VK_F2:
                highByte.or(0x40);
                lowByte.or(0x40);
                break;

            /* F1 */
            case KeyEvent.VK_F1:
                highByte.or(0x40);
                lowByte.or(0x20);
                break;

            /* CTRL */
            case KeyEvent.VK_CONTROL:
                highByte.or(0x40);
                lowByte.or(0x10);
                break;

            /* ALT */
            case KeyEvent.VK_ALT:
                highByte.or(0x40);
                lowByte.or(0x08);
                break;

            /* BREAK */
            case KeyEvent.VK_ESCAPE:
                highByte.or(0x40);
                lowByte.or(0x04);
                break;

            /* CLR */
            case KeyEvent.VK_HOME:
                highByte.or(0x40);
                lowByte.or(0x02);
                break;

            /* ENTER */
            case KeyEvent.VK_ENTER:
                highByte.or(0x40);
                lowByte.or(0x01);
                break;

            /* / */
            case KeyEvent.VK_SLASH:
                highByte.or(0x20);
                lowByte.or(0x80);
                break;

            /* . */
            case KeyEvent.VK_PERIOD:
                highByte.or(0x20);
                lowByte.or(0x40);
                break;

            /* - */
            case KeyEvent.VK_MINUS:
                highByte.or(0x20);
                lowByte.or(0x20);
                break;

            /* , */
            case KeyEvent.VK_COMMA:
                highByte.or(0x20);
                lowByte.or(0x10);
                break;

            /* ; */
            case KeyEvent.VK_SEMICOLON:
                highByte.or(0x20);
                lowByte.or(0x08);
                break;

            /* : */
            case KeyEvent.VK_COLON:
                highByte.or(0x20);
                lowByte.or(0x04);
                break;

            /* 9 */
            case KeyEvent.VK_9:
                highByte.or(0x20);
                lowByte.or(0x02);
                break;

            /* 8 */
            case KeyEvent.VK_8:
                highByte.or(0x20);
                lowByte.or(0x01);
                break;

            /* 7 */
            case KeyEvent.VK_7:
                highByte.or(0x10);
                lowByte.or(0x80);
                break;

            /* 6 */
            case KeyEvent.VK_6:
                highByte.or(0x10);
                lowByte.or(0x40);
                break;

            /* 5 */
            case KeyEvent.VK_5:
                highByte.or(0x10);
                lowByte.or(0x20);
                break;

            /* 4 */
            case KeyEvent.VK_4:
                highByte.or(0x10);
                lowByte.or(0x10);
                break;

            /* 3 */
            case KeyEvent.VK_3:
                highByte.or(0x10);
                lowByte.or(0x08);
                break;

            /* 2 */
            case KeyEvent.VK_2:
                highByte.or(0x10);
                lowByte.or(0x04);
                break;

            /* 1 */
            case KeyEvent.VK_1:
                highByte.or(0x10);
                lowByte.or(0x02);
                break;

            /* 0 */
            case KeyEvent.VK_0:
                highByte.or(0x10);
                lowByte.or(0x01);
                break;

            /* SPACE */
            case KeyEvent.VK_SPACE:
                highByte.or(0x08);
                lowByte.or(0x80);
                break;

            /* RIGHT ARROW */
            case KeyEvent.VK_RIGHT:
                highByte.or(0x08);
                lowByte.or(0x40);
                break;

            /* LEFT ARROW */
            case KeyEvent.VK_LEFT:
                highByte.or(0x08);
                lowByte.or(0x20);
                break;

            /* DOWN ARROW */
            case KeyEvent.VK_DOWN:
                highByte.or(0x08);
                lowByte.or(0x10);
                break;

            /* UP ARROW */
            case KeyEvent.VK_UP:
                highByte.or(0x08);
                lowByte.or(0x08);
                break;

            /* Z */
            case KeyEvent.VK_Z:
                highByte.or(0x08);
                lowByte.or(0x04);
                break;

            /* Y */
            case KeyEvent.VK_Y:
                highByte.or(0x08);
                lowByte.or(0x02);
                break;

            /* X */
            case KeyEvent.VK_X:
                highByte.or(0x08);
                lowByte.or(0x01);
                break;

            /* W */
            case KeyEvent.VK_W:
                highByte.or(0x04);
                lowByte.or(0x80);
                break;

            /* V */
            case KeyEvent.VK_V:
                highByte.or(0x04);
                lowByte.or(0x40);
                break;

            /* U */
            case KeyEvent.VK_U:
                highByte.or(0x04);
                lowByte.or(0x20);
                break;

            /* T */
            case KeyEvent.VK_T:
                highByte.or(0x04);
                lowByte.or(0x10);
                break;

            /* S */
            case KeyEvent.VK_S:
                highByte.or(0x04);
                lowByte.or(0x08);
                break;

            /* R */
            case KeyEvent.VK_R:
                highByte.or(0x04);
                lowByte.or(0x04);
                break;

            /* Q */
            case KeyEvent.VK_Q:
                highByte.or(0x04);
                lowByte.or(0x02);
                break;

            /* P */
            case KeyEvent.VK_P:
                highByte.or(0x04);
                lowByte.or(0x01);
                break;

            /* O */
            case KeyEvent.VK_O:
                highByte.or(0x02);
                lowByte.or(0x80);
                break;

            /* N */
            case KeyEvent.VK_N:
                highByte.or(0x02);
                lowByte.or(0x40);
                break;

            /* M */
            case KeyEvent.VK_M:
                highByte.or(0x02);
                lowByte.or(0x20);
                break;

            /* L */
            case KeyEvent.VK_L:
                highByte.or(0x02);
                lowByte.or(0x10);
                break;

            /* K */
            case KeyEvent.VK_K:
                highByte.or(0x02);
                lowByte.or(0x08);
                break;

            /* J */
            case KeyEvent.VK_J:
                highByte.or(0x02);
                lowByte.or(0x04);
                break;

            /* I */
            case KeyEvent.VK_I:
                highByte.or(0x02);
                lowByte.or(0x02);
                break;

            /* H */
            case KeyEvent.VK_H:
                highByte.or(0x02);
                lowByte.or(0x01);
                break;

            /* G */
            case KeyEvent.VK_G:
                highByte.or(0x01);
                lowByte.or(0x80);
                break;

            /* F */
            case KeyEvent.VK_F:
                highByte.or(0x01);
                lowByte.or(0x40);
                break;

            /* E */
            case KeyEvent.VK_E:
                highByte.or(0x01);
                lowByte.or(0x20);
                break;

            /* D */
            case KeyEvent.VK_D:
                highByte.or(0x01);
                lowByte.or(0x10);
                break;

            /* C */
            case KeyEvent.VK_C:
                highByte.or(0x01);
                lowByte.or(0x08);
                break;

            /* B */
            case KeyEvent.VK_B:
                highByte.or(0x01);
                lowByte.or(0x04);
                break;

            /* A */
            case KeyEvent.VK_A:
                highByte.or(0x01);
                lowByte.or(0x02);
                break;

            /* @ */
            case KeyEvent.VK_AT:
                highByte.or(0x01);
                lowByte.or(0x01);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        highByte.and(0x0);
        lowByte.and(0x0);
    }

    /**
     * Return the high byte from the keyboard.
     *
     * @return the high byte of the keyboard
     */
    public UnsignedByte getHighByte() {
        return highByte.inverse();
    }

    /**
     * Return the low byte of the keyboard.
     *
     * @return the low byte of the keyboard
     */
    public UnsignedByte getLowByte() {
        return lowByte.inverse();
    }
}
