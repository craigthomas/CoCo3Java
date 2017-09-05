/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.RegisterSet;
import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import ca.craigthomas.yacoco3e.datatypes.UnsignedWord;
import ca.craigthomas.yacoco3e.listeners.QuitMenuItemActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.TimerTask;
import java.util.Timer;
import java.util.logging.Logger;

public class Emulator
{
    /* The main emulator components */
    private Screen screen;
    private Memory memory;
    private Keyboard keyboard;
    private CPU cpu;
    private IOController ioController;

    // The Canvas on which all the drawing will take place
    private Canvas canvas;

    // The main Emulator container
    private JFrame container;
    private JMenuBar menuBar;

    /* A logger for the emulator */
    private final static Logger LOGGER = Logger.getLogger(Emulator.class.getName());


    public Emulator(int scaleFactor, String romFile) {
        memory = new Memory();
        keyboard = new Keyboard();
        ioController = new IOController(memory, new RegisterSet(), keyboard);
        screen = new Screen(ioController, scaleFactor);
        cpu = new CPU(ioController);
        screen.sg4ClearScreen();

        initEmulatorJFrame();

        // Attempt to load specified ROM file
        if (romFile != null) {
            InputStream romFileStream = openStream(romFile);
            if (!memory.loadROM(memory.loadStream(romFileStream))) {
                LOGGER.severe("Could not load ROM file [" + romFile + "]");
            }
            closeStream(romFileStream);
        } else {
            LOGGER.severe("no ROM file specified");
            System.exit(1);
        }
    }

    /**
     * Initializes the JFrame that the emulator will use to draw onto. Will set up the menu system and
     * link the action listeners to the menu items. Returns the JFrame that contains all of the emulator
     * screen elements.
     */
    private void initEmulatorJFrame() {
        container = new JFrame("Yet Another CoCo 3 Emulator");
        menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem quitFile = new JMenuItem("Quit", KeyEvent.VK_Q);
        quitFile.addActionListener(new QuitMenuItemActionListener(cpu));
        fileMenu.add(quitFile);
        menuBar.add(fileMenu);

        attachCanvas();
    }

    /**
     * Generates the canvas of the appropriate size and attaches it to the
     * main jFrame for the emulator.
     */
    private void attachCanvas() {
        int scaleFactor = screen.getScale();
        int scaledWidth = screen.getWidth() * scaleFactor;
        int scaledHeight = screen.getHeight() * scaleFactor;

        JPanel panel = (JPanel) container.getContentPane();
        panel.removeAll();
        panel.setPreferredSize(new Dimension(scaledWidth, scaledHeight));
        panel.setLayout(null);

        canvas = new Canvas();
        canvas.setBounds(0, 0, scaledWidth, scaledHeight);
        canvas.setIgnoreRepaint(true);

        panel.add(canvas);

        container.setJMenuBar(menuBar);
        container.pack();
        container.setResizable(false);
        container.setVisible(true);
        container.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        canvas.createBufferStrategy(2);
        canvas.setFocusable(true);
        canvas.requestFocus();

        canvas.addKeyListener(keyboard);
    }

    /**
     * Will redraw the contents of the screen to the emulator window. Optionally, if
     * isInTraceMode is True, will also draw the contents of the overlayScreen to the screen.
     */
    private void refreshScreen()
    {
        Graphics2D graphics = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();
        graphics.drawImage(screen.getBackBuffer(), null, 0, 0);
        graphics.dispose();
        canvas.getBufferStrategy().show();
    }

    /**
     * Starts the main emulator loop running. Fires at the rate of 60Hz,
     * will repaint the screen and listen for any debug key presses.
     */
    public void start() {
        ioController.reset();
        cpu.start();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                screen.refreshScreen();
                memory.writeByte(new UnsignedWord(0x0200), new UnsignedByte(0x1));
                refreshScreen();
            }
        };
        timer.scheduleAtFixedRate(task, 0L, 33L);
    }

    /**
     * Attempts to open the specified filename as an InputStream. Will return null if there is
     * an error.
     *
     * @param filename The String containing the full path to the filename to open
     * @return An opened InputStream, or null if there is an error
     */
    private InputStream openStream(String filename) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(filename));
            return inputStream;
        } catch (FileNotFoundException e) {
            LOGGER.severe("Error opening file");
            LOGGER.severe(e.getMessage());
            return null;
        }
    }

    /**
     * Closes an open InputStream.
     *
     * @param stream the Input Stream to close
     */
    private void closeStream(InputStream stream) {
        try {
            stream.close();
        } catch (IOException e) {
            LOGGER.severe("Error closing stream");
            LOGGER.severe(e.getMessage());
        }
    }
}
