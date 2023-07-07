/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import java.awt.event.KeyEvent;

public class EmulatedKeyboard extends Keyboard
{
    public EmulatedKeyboard() {
        super();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyPressed = e.getKeyCode();

        if (e.getKeyChar() == '!') {
            column7.or(0x40);
            column1.or(0x10);
            return;
        }

        if (e.getKeyChar() == '#') {
            column7.or(0x40);
            column3.or(0x10);
            return;
        }

        if (e.getKeyChar() == '$') {
            column7.or(0x40);
            column4.or(0x10);
            return;
        }

        if (e.getKeyChar() == '%') {
            column7.or(0x40);
            column5.or(0x10);
            return;
        }

        if (e.getKeyChar() == ':') {
            column2.or(0x20);
            column7.and(~0x40);
            return;
        }

        if (e.getKeyChar() == '\'') {
            column7.or(0x40);
            column7.or(0x10);
            return;
        }

        if (e.getKeyChar() == '=') {
            column7.or(0x40);
            column5.or(0x20);
            return;
        }

        if (e.getKeyChar() == '@') {
            column0.or(0x01);
            column7.and(~0x40);
            return;
        }

        if (e.getKeyChar() == '"') {
            column7.or(0x40);
            column2.or(0x10);
            return;
        }

        if (e.getKeyChar() == '*') {
            column7.or(0x40);
            column2.or(0x20);
            return;
        }

        if (e.getKeyChar() == '&') {
            column7.or(0x40);
            column6.or(0x10);
            return;
        }

        if (e.getKeyChar() == '(') {
            column7.or(0x40);
            column0.or(0x20);
            return;
        }

        if (e.getKeyChar() == ')') {
            column7.or(0x40);
            column1.or(0x20);
            return;
        }

        if (e.getKeyChar() == '+') {
            column7.or(0x40);
            column3.or(0x20);
            return;
        }

        switch (keyPressed) {
            /* SHIFT */
            case KeyEvent.VK_SHIFT:
                column7.or(0x40);
                break;

            /* F2 */
            case KeyEvent.VK_F2:
                column6.or(0x40);
                break;

            /* F1 */
            case KeyEvent.VK_F1:
                column5.or(0x40);
                break;

            /* CTRL */
            case KeyEvent.VK_CONTROL:
                column4.or(0x40);
                break;

            /* ALT */
            case KeyEvent.VK_ALT:
                column3.or(0x40);
                break;

            /* BREAK */
            case KeyEvent.VK_ESCAPE:
                column2.or(0x40);
                break;

            /* CLR */
            case KeyEvent.VK_HOME:
                column1.or(0x40);
                break;

            /* ENTER */
            case KeyEvent.VK_ENTER:
                column0.or(0x40);
                break;

            /* / */
            case KeyEvent.VK_SLASH:
                column7.or(0x20);
                break;

            /* . */
            case KeyEvent.VK_PERIOD:
                column6.or(0x20);
                break;

            /* - */
            case KeyEvent.VK_MINUS:
                column5.or(0x20);
                break;

            /* , */
            case KeyEvent.VK_COMMA:
                column4.or(0x20);
                break;

            /* ; */
            case KeyEvent.VK_SEMICOLON:
                column3.or(0x20);
                break;

            /* : */
            case KeyEvent.VK_QUOTE:
                column2.or(0x20);
                break;

            /* 9 */
            case KeyEvent.VK_9:
                column1.or(0x20);
                break;

            /* 8 */
            case KeyEvent.VK_8:
                column0.or(0x20);
                break;

            /* 7 */
            case KeyEvent.VK_7:
                column7.or(0x10);
                break;

            /* 6 */
            case KeyEvent.VK_6:
                column6.or(0x10);
                break;

            /* 5 */
            case KeyEvent.VK_5:
                column5.or(0x10);
                break;

            /* 4 */
            case KeyEvent.VK_4:
                column4.or(0x10);
                break;

            /* 3 */
            case KeyEvent.VK_3:
                column3.or(0x10);
                break;

            /* 2 */
            case KeyEvent.VK_2:
                column2.or(0x10);
                break;

            /* 1 */
            case KeyEvent.VK_1:
                column1.or(0x10);
                break;

            /* 0 */
            case KeyEvent.VK_0:
                column0.or(0x10);
                break;

            /* SPACE */
            case KeyEvent.VK_SPACE:
                column7.or(0x08);
                break;

            /* RIGHT ARROW */
            case KeyEvent.VK_RIGHT:
                column6.or(0x08);
                break;

            /* LEFT ARROW */
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_BACK_SPACE:
                column5.or(0x08);
                break;

            /* DOWN ARROW */
            case KeyEvent.VK_DOWN:
                column4.or(0x08);
                break;

            /* UP ARROW */
            case KeyEvent.VK_UP:
                column3.or(0x08);
                break;

            /* Z */
            case KeyEvent.VK_Z:
                column2.or(0x08);
                break;

            /* Y */
            case KeyEvent.VK_Y:
                column1.or(0x08);
                break;

            /* X */
            case KeyEvent.VK_X:
                column0.or(0x08);
                break;

            /* W */
            case KeyEvent.VK_W:
                column7.or(0x04);
                break;

            /* V */
            case KeyEvent.VK_V:
                column6.or(0x04);
                break;

            /* U */
            case KeyEvent.VK_U:
                column5.or(0x04);
                break;

            /* T */
            case KeyEvent.VK_T:
                column4.or(0x04);
                break;

            /* S */
            case KeyEvent.VK_S:
                column3.or(0x04);
                break;

            /* R */
            case KeyEvent.VK_R:
                column2.or(0x04);
                break;

            /* Q */
            case KeyEvent.VK_Q:
                column1.or(0x04);
                break;

            /* P */
            case KeyEvent.VK_P:
                column0.or(0x04);
                break;

            /* O */
            case KeyEvent.VK_O:
                column7.or(0x02);
                break;

            /* N */
            case KeyEvent.VK_N:
                column6.or(0x02);
                break;

            /* M */
            case KeyEvent.VK_M:
                column5.or(0x02);
                break;

            /* L */
            case KeyEvent.VK_L:
                column4.or(0x02);
                break;

            /* K */
            case KeyEvent.VK_K:
                column3.or(0x02);
                break;

            /* J */
            case KeyEvent.VK_J:
                column2.or(0x02);
                break;

            /* I */
            case KeyEvent.VK_I:
                column1.or(0x02);
                break;

            /* H */
            case KeyEvent.VK_H:
                column0.or(0x02);
                break;

            /* G */
            case KeyEvent.VK_G:
                column7.or(0x01);
                break;

            /* F */
            case KeyEvent.VK_F:
                column6.or(0x01);
                break;

            /* E */
            case KeyEvent.VK_E:
                column5.or(0x01);
                break;

            /* D */
            case KeyEvent.VK_D:
                column4.or(0x01);
                break;

            /* C */
            case KeyEvent.VK_C:
                column3.or(0x01);
                break;

            /* B */
            case KeyEvent.VK_B:
                column2.or(0x01);
                break;

            /* A */
            case KeyEvent.VK_A:
                column1.or(0x01);
                break;

            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyPressed = e.getKeyCode();

        if (e.getKeyChar() == '!') {
            column7.and(~0x40);
            column1.and(~0x10);
            return;
        }

        if (e.getKeyChar() == '#') {
            column7.and(~0x40);
            column3.and(~0x10);
            return;
        }

        if (e.getKeyChar() == '$') {
            column7.and(~0x40);
            column4.and(~0x10);
            return;
        }

        if (e.getKeyChar() == '%') {
            column7.and(~0x40);
            column5.and(~0x10);
            return;
        }

        if (e.getKeyChar() == ':') {
            column2.and(~0x20);
            column7.and(~0x40);
            return;
        }

        if (e.getKeyChar() == '\'') {
            column7.and(~0x40);
            column7.and(~0x10);
            return;
        }

        if (e.getKeyChar() == '=') {
            column7.and(~0x40);
            column5.and(~0x20);
            return;
        }

        if (e.getKeyChar() == '@') {
            column0.and(~0x01);
            column7.and(~0x40);
            return;
        }

        if (e.getKeyChar() == '"') {
            column7.and(~0x40);
            column2.and(~0x10);
            return;
        }

        if (e.getKeyChar() == '*') {
            column7.and(~0x40);
            column2.and(~0x20);
            return;
        }

        if (e.getKeyChar() == '&') {
            column7.and(~0x40);
            column6.and(~0x10);
            return;
        }

        if (e.getKeyChar() == '(') {
            column7.and(~0x40);
            column0.and(~0x20);
            return;
        }

        if (e.getKeyChar() == ')') {
            column7.and(~0x40);
            column1.and(~0x20);
            return;
        }

        if (e.getKeyChar() == '+') {
            column7.and(~0x40);
            column3.and(~0x20);
            return;
        }

        switch (keyPressed) {

            /* SHIFT */
            case KeyEvent.VK_SHIFT:
                column7.and(~0x40);
                break;

            /* F2 */
            case KeyEvent.VK_F2:
                column6.and(~0x40);
                break;

            /* F1 */
            case KeyEvent.VK_F1:
                column5.and(~0x40);
                break;

            /* CTRL */
            case KeyEvent.VK_CONTROL:
                column4.and(~0x40);
                break;

            /* ALT */
            case KeyEvent.VK_ALT:
                column3.and(~0x40);
                break;

            /* BREAK */
            case KeyEvent.VK_ESCAPE:
                column2.and(~0x40);
                break;

            /* CLR */
            case KeyEvent.VK_HOME:
                column1.and(~0x40);
                break;

            /* ENTER */
            case KeyEvent.VK_ENTER:
                column0.and(~0x40);
                break;

            /* / */
            case KeyEvent.VK_SLASH:
                column7.and(~0x20);
                break;

            /* . */
            case KeyEvent.VK_PERIOD:
                column6.and(~0x20);
                break;

            /* - */
            case KeyEvent.VK_MINUS:
                column5.and(~0x20);
                break;

            /* , */
            case KeyEvent.VK_COMMA:
                column4.and(~0x20);
                break;

            /* ; */
            case KeyEvent.VK_SEMICOLON:
                column3.and(~0x20);
                break;

            /* : */
            case KeyEvent.VK_QUOTE:
                column2.and(~0x20);
                break;

            /* 9 */
            case KeyEvent.VK_9:
                column1.and(~0x20);
                break;

            /* 8 */
            case KeyEvent.VK_8:
                column0.and(~0x20);
                break;

            /* 7 */
            case KeyEvent.VK_7:
                column7.and(~0x10);
                break;

            /* 6 */
            case KeyEvent.VK_6:
                column6.and(~0x10);
                break;

            /* 5 */
            case KeyEvent.VK_5:
                column5.and(~0x10);
                break;

            /* 4 */
            case KeyEvent.VK_4:
                column4.and(~0x10);
                break;

            /* 3 */
            case KeyEvent.VK_3:
                column3.and(~0x10);
                break;

            /* 2 */
            case KeyEvent.VK_2:
                column2.and(~0x10);
                break;

            /* 1 */
            case KeyEvent.VK_1:
                column1.and(~0x10);
                break;

            /* 0 */
            case KeyEvent.VK_0:
                column0.and(~0x10);
                break;

            /* SPACE */
            case KeyEvent.VK_SPACE:
                column7.and(~0x08);
                break;

            /* RIGHT ARROW */
            case KeyEvent.VK_RIGHT:
                column6.and(~0x08);
                break;

            /* LEFT ARROW */
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_BACK_SPACE:
                column5.and(~0x08);
                break;

            /* DOWN ARROW */
            case KeyEvent.VK_DOWN:
                column4.and(~0x08);
                break;

            /* UP ARROW */
            case KeyEvent.VK_UP:
                column3.and(~0x08);
                break;

            /* Z */
            case KeyEvent.VK_Z:
                column2.and(~0x08);
                break;

            /* Y */
            case KeyEvent.VK_Y:
                column1.and(~0x08);
                break;

            /* X */
            case KeyEvent.VK_X:
                column0.and(~0x08);
                break;

            /* W */
            case KeyEvent.VK_W:
                column7.and(~0x04);
                break;

            /* V */
            case KeyEvent.VK_V:
                column6.and(~0x04);
                break;

            /* U */
            case KeyEvent.VK_U:
                column5.and(~0x04);
                break;

            /* T */
            case KeyEvent.VK_T:
                column4.and(~0x04);
                break;

            /* S */
            case KeyEvent.VK_S:
                column3.and(~0x04);
                break;

            /* R */
            case KeyEvent.VK_R:
                column2.and(~0x04);
                break;

            /* Q */
            case KeyEvent.VK_Q:
                column1.and(~0x04);
                break;

            /* P */
            case KeyEvent.VK_P:
                column0.and(~0x04);
                break;

            /* O */
            case KeyEvent.VK_O:
                column7.and(~0x02);
                break;

            /* N */
            case KeyEvent.VK_N:
                column6.and(~0x02);
                break;

            /* M */
            case KeyEvent.VK_M:
                column5.and(~0x02);
                break;

            /* L */
            case KeyEvent.VK_L:
                column4.and(~0x02);
                break;

            /* K */
            case KeyEvent.VK_K:
                column3.and(~0x02);
                break;

            /* J */
            case KeyEvent.VK_J:
                column2.and(~0x02);
                break;

            /* I */
            case KeyEvent.VK_I:
                column1.and(~0x02);
                break;

            /* H */
            case KeyEvent.VK_H:
                column0.and(~0x02);
                break;

            /* G */
            case KeyEvent.VK_G:
                column7.and(~0x01);
                break;

            /* F */
            case KeyEvent.VK_F:
                column6.and(~0x01);
                break;

            /* E */
            case KeyEvent.VK_E:
                column5.and(~0x01);
                break;

            /* D */
            case KeyEvent.VK_D:
                column4.and(~0x01);
                break;

            /* C */
            case KeyEvent.VK_C:
                column3.and(~0x01);
                break;

            /* B */
            case KeyEvent.VK_B:
                column2.and(~0x01);
                break;

            /* A */
            case KeyEvent.VK_A:
                column1.and(~0x01);
                break;

            default:
                break;
        }
    }
}
