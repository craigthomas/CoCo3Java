/*
 * Copyright (C) 2017-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import ca.craigthomas.yacoco3e.components.Emulator;
import ca.craigthomas.yacoco3e.components.PassthroughKeyboard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener that will set which keyboard type is active.
 */
public class SetPassthroughKeyboardActionListener extends AbstractFileChooserListener implements ActionListener
{
    private final Emulator emulator;
    private final JRadioButtonMenuItem emulated;
    private final JRadioButtonMenuItem passthrough;

    public SetPassthroughKeyboardActionListener(Emulator emulator, JRadioButtonMenuItem passthrough, JRadioButtonMenuItem emulated) {
        super();
        this.emulator = emulator;
        this.emulated = emulated;
        this.passthrough = passthrough;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setPassthroughKeyboard();
    }

    public void setPassthroughKeyboard() {
        emulator.switchKeyListener(new PassthroughKeyboard());
        emulated.setSelected(false);
        passthrough.setSelected(true);
    }
}
