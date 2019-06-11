/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import ca.craigthomas.yacoco3e.components.Emulator;
import ca.craigthomas.yacoco3e.components.IOController;
import ca.craigthomas.yacoco3e.datatypes.JV1Disk;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * An ActionListener that will load a virtual disk.
 */
public class LoadVirtualDiskMenuItemActionListener extends AbstractFileChooserListener implements ActionListener
{
    private Emulator emulator;
    private int drive;

    private static final String FILE_CHOOSER_TITLE = "Open Virtual Disk";
    private static final String DSK_FILE = "Virtual Disk Files (*.dsk)";
    private static final String DSK_FILE_EXTENSION = "dsk";
    private static final String FILE_OPEN_ERROR = "Error opening file.";
    private static final String FILE_ERROR_TITLE = "File Error";

    private final static Logger LOGGER = Logger.getLogger(LoadVirtualDiskMenuItemActionListener.class.getName());

    public LoadVirtualDiskMenuItemActionListener(int drive, Emulator emulator) {
        super();
        this.emulator = emulator;
        this.drive = drive;
        this.fileChooserTitle = FILE_CHOOSER_TITLE;
        this.fileFilter = new FileNameExtensionFilter(DSK_FILE, DSK_FILE_EXTENSION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        openVirtualDiskFileDialog();
    }

    /**
     * Loads a virtual disk into the specified drive.
     */
    public void openVirtualDiskFileDialog() {
        JFrame container = emulator.getContainer();
        JFileChooser chooser = createFileChooser();
        if (chooser.showOpenDialog(container) == JFileChooser.APPROVE_OPTION) {
            JV1Disk jv1Disk = new JV1Disk();
            if (jv1Disk.isCorrectFormat(chooser.getSelectedFile())) {
                boolean loaded = jv1Disk.loadFile(chooser.getSelectedFile().toString());
                if (!loaded) {
                    JOptionPane.showMessageDialog(container, FILE_OPEN_ERROR, FILE_ERROR_TITLE,
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    IOController ioController = emulator.getIOController();
                    ioController.loadVirtualDisk(drive, jv1Disk);
                    LOGGER.info("Drive " + drive + ": loaded file [" + chooser.getSelectedFile().toString() + "]");
                }
            }
        }
    }
}
