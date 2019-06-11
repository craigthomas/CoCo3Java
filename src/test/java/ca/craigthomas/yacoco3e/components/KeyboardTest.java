/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.awt.event.KeyEvent;

import static org.junit.Assert.assertEquals;

public class KeyboardTest
{
    private Keyboard keyboard;
    private KeyEvent event;

    @Before
    public void setUp() {
        event = Mockito.mock(KeyEvent.class);
        keyboard = new Keyboard();
    }

    @Test
    public void testKeyboardKeyPressShift() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_SHIFT);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyPressF2() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_F2);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyPressF1() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_F1);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyPressCTRL() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_CONTROL);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyPressALT() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_ALT);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyPressESC() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_ESCAPE);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyPressHOME() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_HOME);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyPressENTER() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_ENTER);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyPressSlash() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_SLASH);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyPressPeriod() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_PERIOD);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyMinus() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_MINUS);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyPressComma() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_COMMA);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyPressSemicolon() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_SEMICOLON);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyPressQuote() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_QUOTE);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyPress9() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_9);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyPress8() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_8);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyPress7() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_7);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyPress6() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_6);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyPress5() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_5);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyPress4() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_4);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyPress3() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_3);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyPress2() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_2);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyPress1() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_1);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyPress0() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_0);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyPressSpace() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_SPACE);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xF7), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyPressRight() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_RIGHT);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xF7), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyPressLeft() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_LEFT);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xF7), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyPressDown() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_DOWN);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xF7), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyPressUp() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_UP);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xF7), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyPressZ() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_Z);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xF7), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyPressY() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_Y);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xF7), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyPressX() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_X);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xF7), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyPressW() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_W);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFB), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyPressV() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_V);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFB), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyPressU() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_U);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFB), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyPressT() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_T);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFB), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyPressS() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_S);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFB), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyPressR() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_R);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFB), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyPressQ() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_Q);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFB), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyPressP() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_P);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFB), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyPressO() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_O);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFD), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyPressN() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_N);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFD), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyPressM() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_M);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFD), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyPressL() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_L);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFD), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyPressK() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_K);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFD), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyPressJ() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_J);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFD), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyPressI() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_I);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFD), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyPressH() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_H);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFD), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyPressG() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_G);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFE), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyPressF() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_F);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFE), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyPressE() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_E);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFE), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyPressD() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_D);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFE), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyPressC() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_C);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFE), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyPressB() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_B);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFE), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyPressA() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_A);
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFE), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyReleasedShift() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_SHIFT);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyReleasedF2() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_F2);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyReleasedF1() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_F1);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyReleasedCTRL() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_CONTROL);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyReleasedALT() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_ALT);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyReleasedESC() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_ESCAPE);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyReleasedHOME() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_HOME);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyReleasedENTER() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_ENTER);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyReleasedSlash() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_SLASH);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyReleasedPeriod() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_PERIOD);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyReleasedMinus() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_MINUS);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyReleasedComma() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_COMMA);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyReleasedSemicolon() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_SEMICOLON);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyReleasedQuote() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_QUOTE);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyReleased9() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_9);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyReleased8() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_8);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyReleased7() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_7);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyReleased6() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_6);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyReleased5() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_5);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyReleased4() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_4);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyReleased3() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_3);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyReleased2() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_2);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyReleased1() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_1);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyReleased0() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_0);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyReleasedSpace() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_SPACE);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyReleasedRight() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_RIGHT);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyReleasedLeft() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_LEFT);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyReleasedDown() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_DOWN);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyReleasedUp() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_UP);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyReleasedZ() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_Z);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyReleasedY() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_Y);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyReleasedX() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_X);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyReleasedW() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_W);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyReleasedV() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_V);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyReleasedU() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_U);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyReleasedT() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_T);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyReleasedS() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_S);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyReleasedR() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_R);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyReleasedQ() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_Q);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyReleasedP() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_P);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyReleasedO() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_O);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyReleasedN() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_N);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyReleasedM() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_M);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyReleasedL() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_L);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyReleasedK() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_K);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyReleasedJ() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_J);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyReleasedI() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_I);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyReleasedH() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_H);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyReleasedG() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_G);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyReleasedF() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_F);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyReleasedE() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_E);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyReleasedD() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_D);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyReleasedC() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_C);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyReleasedB() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_B);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyReleasedA() {
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.VK_A);
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyPressColon() {
        Mockito.when(event.getKeyChar()).thenReturn(':');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyReleasedColon() {
        Mockito.when(event.getKeyChar()).thenReturn(':');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyPressExclaim() {
        Mockito.when(event.getKeyChar()).thenReturn('!');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0xFD)));
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyReleasedExclaim() {
        Mockito.when(event.getKeyChar()).thenReturn('!');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFD)));
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyPressAt() {
        Mockito.when(event.getKeyChar()).thenReturn('@');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xFE), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyReleasedAt() {
        Mockito.when(event.getKeyChar()).thenReturn('@');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyPressPound() {
        Mockito.when(event.getKeyChar()).thenReturn('#');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0xF7)));
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyReleasedPound() {
        Mockito.when(event.getKeyChar()).thenReturn('#');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xF7)));
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyPressDollar() {
        Mockito.when(event.getKeyChar()).thenReturn('$');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyReleasedDollar() {
        Mockito.when(event.getKeyChar()).thenReturn('$');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xEF)));
    }

    @Test
    public void testKeyboardKeyPressPercent() {
        Mockito.when(event.getKeyChar()).thenReturn('%');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyReleasedPercent() {
        Mockito.when(event.getKeyChar()).thenReturn('%');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyPressAmpersand() {
        Mockito.when(event.getKeyChar()).thenReturn('&');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyReleasedAmpersand() {
        Mockito.when(event.getKeyChar()).thenReturn('&');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xBF)));
    }

    @Test
    public void testKeyboardKeyPressLeftBracket() {
        Mockito.when(event.getKeyChar()).thenReturn('(');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyReleasedLeftBracket() {
        Mockito.when(event.getKeyChar()).thenReturn('(');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFE)));
    }

    @Test
    public void testKeyboardKeyPressRightBracket() {
        Mockito.when(event.getKeyChar()).thenReturn(')');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyReleasedRightBracket() {
        Mockito.when(event.getKeyChar()).thenReturn(')');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFD)));
    }

    @Test
    public void testKeyboardKeyPressAsterisk() {
        Mockito.when(event.getKeyChar()).thenReturn('*');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyReleasedAsterisk() {
        Mockito.when(event.getKeyChar()).thenReturn('*');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyPressPlus() {
        Mockito.when(event.getKeyChar()).thenReturn('+');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyReleasedPlus() {
        Mockito.when(event.getKeyChar()).thenReturn('+');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xF7)));
    }

    @Test
    public void testKeyboardKeyPressEquals() {
        Mockito.when(event.getKeyChar()).thenReturn('=');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xDF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyReleasedEquals() {
        Mockito.when(event.getKeyChar()).thenReturn('=');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xDF)));
    }

    @Test
    public void testKeyboardKeyPressDoubleQuote() {
        Mockito.when(event.getKeyChar()).thenReturn('"');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xBF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xEF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyReleasedDoubleQuote() {
        Mockito.when(event.getKeyChar()).thenReturn('"');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0xFB)));
    }

    @Test
    public void testKeyboardKeyPressSingleQuote() {
        Mockito.when(event.getKeyChar()).thenReturn('\'');
        keyboard.keyPressed(event);
        assertEquals(new UnsignedByte(0xAF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }

    @Test
    public void testKeyboardKeyReleasedSingleQuote() {
        Mockito.when(event.getKeyChar()).thenReturn('\'');
        keyboard.keyPressed(event);
        keyboard.keyReleased(event);
        assertEquals(new UnsignedByte(0xFF), keyboard.getHighByte(new UnsignedByte(0x7F)));
    }
}
