/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import ca.craigthomas.yacoco3e.components.EmulatedKeyboard;
import ca.craigthomas.yacoco3e.components.Emulator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener that will quit the emulator.
 */
public class SetEmulatedKeyboardActionListener extends AbstractFileChooserListener implements ActionListener
{
    private Emulator emulator;
    private JRadioButtonMenuItem passthrough;
    private JRadioButtonMenuItem emulated;

    public SetEmulatedKeyboardActionListener(Emulator emulator, JRadioButtonMenuItem passthrough, JRadioButtonMenuItem emulated) {
        super();
        this.emulator = emulator;
        this.passthrough = passthrough;
        this.emulated = emulated;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setEmulatedKeyboard();
    }

    /**
     * Opens a dialog box to prompt the user to choose a cassette file
     * to open for playback.
     */
    public void setEmulatedKeyboard() {
        emulator.switchKeyListener(new EmulatedKeyboard());
        passthrough.setSelected(false);
        emulated.setSelected(true);
    }

}
