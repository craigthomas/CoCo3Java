/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import ca.craigthomas.yacoco3e.components.Cassette;
import ca.craigthomas.yacoco3e.components.Emulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener that will load a virtual disk.
 */
public class LoadVirtualDiskMenuItemActionListener implements ActionListener
{
    private Emulator emulator;

    public LoadVirtualDiskMenuItemActionListener(Emulator emulator) {
        super();
        this.emulator = emulator;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        emulator.openVirtualDiskFileDialog();
    }
}
