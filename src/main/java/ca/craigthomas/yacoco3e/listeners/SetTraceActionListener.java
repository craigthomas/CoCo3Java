/*
 * Copyright (C) 2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import ca.craigthomas.yacoco3e.components.Emulator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener that will start or stop debug tracing.
 */
public class SetTraceActionListener implements ActionListener
{
    private Emulator emulator;
    private JRadioButtonMenuItem traceMenuItem;

    public SetTraceActionListener(Emulator emulator, JRadioButtonMenuItem traceMenuItem) {
        super();
        this.emulator = emulator;
        this.traceMenuItem = traceMenuItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setTrace();
    }

    public void setTrace() {
        emulator.trace = !emulator.trace;
        traceMenuItem.setSelected(emulator.trace);
    }

}
