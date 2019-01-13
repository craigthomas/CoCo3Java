/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import ca.craigthomas.yacoco3e.components.Emulator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener that will quit the emulator.
 */
public class QuitMenuItemActionListener implements ActionListener
{
    private Emulator emulator;

    public QuitMenuItemActionListener(Emulator emulator) {
        super();
        this.emulator = emulator;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to quit?",
                "Confirm Quit",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            emulator.shutdown();
        }
    }
}
