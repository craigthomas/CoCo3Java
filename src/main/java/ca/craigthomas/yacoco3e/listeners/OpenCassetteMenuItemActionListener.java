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
 * An ActionListener that will quit the emulator.
 */
public class OpenCassetteMenuItemActionListener implements ActionListener
{
    private Cassette cassette;
    private Emulator emulator;

    public OpenCassetteMenuItemActionListener(Emulator emulator, Cassette cassette) {
        super();
        this.emulator = emulator;
        this.cassette = cassette;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        cassette.play();
        emulator.openCassetteFileDialog();
    }
}
