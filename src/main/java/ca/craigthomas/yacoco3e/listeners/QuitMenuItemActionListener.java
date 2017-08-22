package ca.craigthomas.yacoco3e.listeners;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener that will quit the emulator.
 */
public class QuitMenuItemActionListener implements ActionListener
{
    public QuitMenuItemActionListener() {
        super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(
                (Component) null,
                "Are you sure you want to quit?",
                "Confirm Action",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            System.exit(0);
        }
    }
}
