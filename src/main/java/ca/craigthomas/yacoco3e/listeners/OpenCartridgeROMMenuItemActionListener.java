/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import ca.craigthomas.yacoco3e.components.Emulator;
import ca.craigthomas.yacoco3e.components.Memory;
import ca.craigthomas.yacoco3e.datatypes.EmulatorStatus;
import ca.craigthomas.yacoco3e.datatypes.MemoryType;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener that will quit the emulator.
 */
public class OpenCartridgeROMMenuItemActionListener extends AbstractFileChooserListener implements ActionListener
{
    private Emulator emulator;

    private static final String FILE_CHOOSER_TITLE = "Open Cartridge ROM File";
    private static final String ROM_FILE = "ROM Files (*.rom)";
    private static final String ROM_FILE_EXTENSION = "rom";
    private static final String FILE_OPEN_ERROR = "Error opening file.";
    private static final String FILE_ERROR_TITLE = "File Error";

    public OpenCartridgeROMMenuItemActionListener(Emulator emulator) {
        super();
        this.emulator = emulator;
        this.fileChooserTitle = FILE_CHOOSER_TITLE;
        this.fileFilter = new FileNameExtensionFilter(ROM_FILE, ROM_FILE_EXTENSION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        openCartridgeROMFileDialog();
    }

    /**
     * Opens a dialog prompting the user to select a file to use as the
     * cartridge ROM.
     */
    public void openCartridgeROMFileDialog() {
        JFrame container = emulator.getContainer();
        JFileChooser chooser = createFileChooser();
        if (chooser.showOpenDialog(container) == JFileChooser.APPROVE_OPTION) {
            Memory memory = emulator.getMemory();
            if (!memory.loadROM(chooser.getSelectedFile().toString(), MemoryType.CARTRIDGE)) {
                JOptionPane.showMessageDialog(container, FILE_OPEN_ERROR, FILE_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            } else {
                emulator.reset();
                emulator.setStatus(EmulatorStatus.RUNNING);
            }
        }
    }
}
