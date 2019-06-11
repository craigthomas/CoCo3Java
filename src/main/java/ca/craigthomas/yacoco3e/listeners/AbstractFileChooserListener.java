/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * An abstract class that adds additional helpers to Listeners.
 */
public class AbstractFileChooserListener
{
    protected String fileChooserTitle;
    protected FileFilter fileFilter;

    /**
     * Creates a JFileChooser object with the specified title.
     *
     * @return the file chooser object
     */
    public JFileChooser createFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setDialogTitle(fileChooserTitle);
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(fileFilter);
        return fileChooser;
    }
}
