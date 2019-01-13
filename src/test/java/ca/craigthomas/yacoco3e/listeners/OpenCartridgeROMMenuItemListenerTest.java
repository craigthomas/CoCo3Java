/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import ca.craigthomas.yacoco3e.components.Emulator;
import ca.craigthomas.yacoco3e.components.Memory;
import ca.craigthomas.yacoco3e.datatypes.MemoryType;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

import static org.mockito.Mockito.*;

public class OpenCartridgeROMMenuItemListenerTest
{
    private OpenCartridgeROMMenuItemActionListener listenerSpy;
    private ActionEvent mockItemEvent;
    private JFileChooser fileChooser;

    @Before
    public void setUp() {
        Emulator emulator = mock(Emulator.class);
        Memory memory = mock(Memory.class);
        when(memory.loadROM(anyString(), any(MemoryType.class))).thenReturn(true);

        fileChooser = mock(JFileChooser.class);
        when(fileChooser.getSelectedFile()).thenReturn(new File(""));

        OpenCartridgeROMMenuItemActionListener listener = new OpenCartridgeROMMenuItemActionListener(emulator);
        listenerSpy = spy(listener);
        ButtonModel buttonModel = mock(ButtonModel.class);
        when(buttonModel.isSelected()).thenReturn(true);
        AbstractButton button = mock(AbstractButton.class);
        when(button.getModel()).thenReturn(buttonModel);
        mockItemEvent = mock(ActionEvent.class);
        when(mockItemEvent.getSource()).thenReturn(button);
        when(listenerSpy.createFileChooser()).thenReturn(fileChooser);
        when(emulator.getMemory()).thenReturn(memory);
    }

    @Test
    public void testOpenCartridgeROMDialogShowsWhenClicked() {
        listenerSpy.actionPerformed(mockItemEvent);
        listenerSpy.actionPerformed(mockItemEvent);
        verify(listenerSpy, times(2)).createFileChooser();
        verify(fileChooser, times(2)).showOpenDialog(any());
    }
}
