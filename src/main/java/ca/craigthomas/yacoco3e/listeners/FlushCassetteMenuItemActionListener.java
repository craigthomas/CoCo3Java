/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import ca.craigthomas.yacoco3e.components.Cassette;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener that will flush cassette data to the cassette file.
 */
public class FlushCassetteMenuItemActionListener implements ActionListener
{
    private Cassette cassette;

    public FlushCassetteMenuItemActionListener(Cassette cassette) {
        super();
        this.cassette = cassette;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        cassette.flushBufferToFile();
    }
}
