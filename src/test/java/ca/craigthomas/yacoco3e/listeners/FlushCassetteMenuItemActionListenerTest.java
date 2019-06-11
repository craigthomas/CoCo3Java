/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.listeners;

import ca.craigthomas.yacoco3e.components.Cassette;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.mockito.Mockito.*;

public class FlushCassetteMenuItemActionListenerTest
{
    private Cassette cassette;
    private FlushCassetteMenuItemActionListener listener;
    private ActionEvent mockItemEvent;

    @Before
    public void setUp() {
        cassette = mock(Cassette.class);
        listener = new FlushCassetteMenuItemActionListener(cassette);
        ButtonModel buttonModel = mock(ButtonModel.class);
        Mockito.when(buttonModel.isSelected()).thenReturn(true);
        AbstractButton button = mock(AbstractButton.class);
        Mockito.when(button.getModel()).thenReturn(buttonModel);
        mockItemEvent = mock(ActionEvent.class);
        Mockito.when(mockItemEvent.getSource()).thenReturn(button);
    }

    @Test
    public void testFlushCassetteDialogShowsWhenClicked() {
        listener.actionPerformed(mockItemEvent);
        listener.actionPerformed(mockItemEvent);
        verify(cassette, times(2)).flushBufferToFile();
    }
}
