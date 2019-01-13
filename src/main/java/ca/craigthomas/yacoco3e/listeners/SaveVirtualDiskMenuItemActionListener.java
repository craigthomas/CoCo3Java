/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import ca.craigthomas.yacoco3e.components.Emulator;
import ca.craigthomas.yacoco3e.components.IOController;
import ca.craigthomas.yacoco3e.datatypes.JV1Disk;
import ca.craigthomas.yacoco3e.datatypes.VirtualDisk;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener that will save a virtual disk.
 */
public class SaveVirtualDiskMenuItemActionListener extends AbstractFileChooserListener implements ActionListener
{
    private Emulator emulator;
    private int drive;

    private static final String FILE_CHOOSER_TITLE = "Save Virtual Disk";
    private static final String DSK_FILE = "Virtual Disk Files (*.dsk)";
    private static final String DSK_FILE_EXTENSION = "dsk";
    private static final String FILE_SAVE_ERROR = "Error saving file.";
    private static final String FILE_ERROR_TITLE = "File Error";

    public SaveVirtualDiskMenuItemActionListener(int drive, Emulator emulator) {
        super();
        this.emulator = emulator;
        this.drive = drive;
        this.fileChooserTitle = FILE_CHOOSER_TITLE;
        this.fileFilter = new FileNameExtensionFilter(DSK_FILE, DSK_FILE_EXTENSION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        openSaveDialog();
    }

    public void openSaveDialog() {
        JFrame container = emulator.getContainer();
        JFileChooser chooser = createFileChooser();
        if (chooser.showSaveDialog(container) == JFileChooser.APPROVE_OPTION) {
            IOController ioController = emulator.getIOController();
            VirtualDisk virtualDisk = ioController.saveVirtualDisk(drive, new JV1Disk());
            if (!virtualDisk.saveToFile(chooser.getSelectedFile().toString(), virtualDisk)) {
                JOptionPane.showMessageDialog(container, FILE_SAVE_ERROR, FILE_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
