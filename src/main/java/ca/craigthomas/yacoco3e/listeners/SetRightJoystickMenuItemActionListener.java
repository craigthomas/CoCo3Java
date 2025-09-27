/*
 * Copyright (C) 2017-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import ca.craigthomas.yacoco3e.components.Emulator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener that will set which keyboard type is active.
 */
public class SetRightJoystickMenuItemActionListener extends AbstractFileChooserListener implements ActionListener
{
    private final Emulator emulator;
    private final JRadioButtonMenuItem [] options;
    private final int selection;

    public SetRightJoystickMenuItemActionListener(Emulator emulator, JRadioButtonMenuItem [] options, int selection) {
        super();
        this.emulator = emulator;
        this.options = options;
        this.selection = selection;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setRightJoystick();
    }

    public void setRightJoystick() {
        for (int i=0; i < options.length; i++) {
            options[i].setSelected(false);
            if (i == selection) {
                options[i].setSelected(true);
                emulator.switchRightJoystick(i);
            }
        }
    }
}
