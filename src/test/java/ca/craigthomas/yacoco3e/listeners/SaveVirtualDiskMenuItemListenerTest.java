/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import ca.craigthomas.yacoco3e.components.Emulator;
import ca.craigthomas.yacoco3e.components.IOController;
import ca.craigthomas.yacoco3e.datatypes.JV1Disk;
import ca.craigthomas.yacoco3e.datatypes.VirtualDisk;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

import static org.mockito.Mockito.*;

public class SaveVirtualDiskMenuItemListenerTest
{
    private SaveVirtualDiskMenuItemActionListener listener0spy;
    private SaveVirtualDiskMenuItemActionListener listener1spy;
    private ActionEvent mockItemEvent;
    private JFileChooser fileChooser;

    @Before
    public void setUp() {
        Emulator emulator = mock(Emulator.class);
        IOController ioController = mock(IOController.class);
        VirtualDisk virtualDisk = mock(JV1Disk.class);

        fileChooser = mock(JFileChooser.class);
        when(fileChooser.getSelectedFile()).thenReturn(new File(""));

        SaveVirtualDiskMenuItemActionListener listener0 = new SaveVirtualDiskMenuItemActionListener(0, emulator);
        listener0spy = spy(listener0);
        SaveVirtualDiskMenuItemActionListener listener1 = new SaveVirtualDiskMenuItemActionListener(1, emulator);
        listener1spy = spy(listener1);
        ButtonModel buttonModel = mock(ButtonModel.class);
        when(buttonModel.isSelected()).thenReturn(true);
        AbstractButton button = mock(AbstractButton.class);
        when(button.getModel()).thenReturn(buttonModel);
        mockItemEvent = mock(ActionEvent.class);
        when(mockItemEvent.getSource()).thenReturn(button);
        when(listener0spy.createFileChooser()).thenReturn(fileChooser);
        when(listener1spy.createFileChooser()).thenReturn(fileChooser);
        when(emulator.getIOController()).thenReturn(ioController);
        when(ioController.saveVirtualDisk(anyInt(), any())).thenReturn(virtualDisk);
        when(virtualDisk.saveToFile(anyString(), any())).thenReturn(true);
    }

    @Test
    public void testSaveVirtualDiskDialogShowsWhenClicked() {
        listener0spy.actionPerformed(mockItemEvent);
        listener0spy.actionPerformed(mockItemEvent);
        listener1spy.actionPerformed(mockItemEvent);
        verify(listener0spy, times(2)).createFileChooser();
        verify(listener1spy, times(1)).createFileChooser();
        verify(fileChooser, times(3)).showSaveDialog(any());
    }
}
