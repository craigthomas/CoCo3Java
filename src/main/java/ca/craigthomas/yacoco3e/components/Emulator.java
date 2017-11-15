/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.common.IO;
import ca.craigthomas.yacoco3e.datatypes.RegisterSet;
import ca.craigthomas.yacoco3e.datatypes.UnsignedByte;
import ca.craigthomas.yacoco3e.datatypes.UnsignedWord;
import ca.craigthomas.yacoco3e.listeners.FlushCassetteMenuItemActionListener;
import ca.craigthomas.yacoco3e.listeners.OpenCassetteMenuItemActionListener;
import ca.craigthomas.yacoco3e.listeners.QuitMenuItemActionListener;
import ca.craigthomas.yacoco3e.listeners.RecordCassetteMenuItemActionListener;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
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
    private Cassette cassette;

    // The Canvas on which all the drawing will take place
    private Canvas canvas;

    // The main Emulator container
    private JFrame container;
    private JMenuBar menuBar;

    /* A logger for the emulator */
    private final static Logger LOGGER = Logger.getLogger(Emulator.class.getName());

    public Emulator(int scaleFactor, String romFile, boolean trace, String cassetteFile) {
        memory = new Memory();
        keyboard = new Keyboard();
        screen = new Screen(scaleFactor);
        cassette = new Cassette();
        ioController = new IOController(memory, new RegisterSet(), keyboard, screen, cassette);
        cpu = new CPU(ioController);
        cpu.setTrace(trace);
        ioController.setCPU(cpu);

        initEmulatorJFrame();

        // Attempt to load specified ROM file
        if (romFile != null) {
            InputStream romFileStream = IO.openInputStream(romFile);
            if (!memory.loadROM(IO.loadStream(romFileStream))) {
                LOGGER.severe("Could not load ROM file [" + romFile + "]");
            }
            IO.closeStream(romFileStream);
        } else {
            LOGGER.severe("no ROM file specified");
            System.exit(1);
        }

        // Attempt to load specified cassette file
        if (cassetteFile != null) {
            if (!cassette.openFile(cassetteFile)) {
                LOGGER.severe("Could not load cassette file [" + cassetteFile + "]");
            } else {
                LOGGER.info("Loaded cassette file [" + cassetteFile + "]");
            }
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

        // Cassette menu
        JMenu cassetteMenu = new JMenu("Cassette");
        cassetteMenu.setMnemonic(KeyEvent.VK_C);

        JMenuItem newCassetteItem = new JMenuItem("New Cassette File", KeyEvent.VK_N);
        newCassetteItem.addActionListener(new RecordCassetteMenuItemActionListener(this, cassette));
        cassetteMenu.add(newCassetteItem);

        JMenuItem flushItem = new JMenuItem("Flush Buffer to File", KeyEvent.VK_F);
        flushItem.addActionListener(new FlushCassetteMenuItemActionListener(this, cassette));
        cassetteMenu.add(flushItem);

        cassetteMenu.addSeparator();

        JMenuItem openCassetteItem = new JMenuItem("Open for Playback", KeyEvent.VK_O);
        openCassetteItem.addActionListener(new OpenCassetteMenuItemActionListener(this, cassette));
        cassetteMenu.add(openCassetteItem);

        menuBar.add(cassetteMenu);
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
                refreshScreen();
            }
        };
        timer.scheduleAtFixedRate(task, 0L, 33L);
    }

    /**
     * Opens a dialog box to prompt the user to choose a cassette file
     * to open for playback.
     */
    public void openCassetteFileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter1 = new FileNameExtensionFilter("Cassette Files (*.cas)", "cas");
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Open Cassette File");
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(filter1);
        if (fileChooser.showOpenDialog(container) == JFileChooser.APPROVE_OPTION) {
            if (!cassette.openFile(fileChooser.getSelectedFile().toString())) {
                JOptionPane.showMessageDialog(container, "Error opening file.", "File Open Problem",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveCassetteFileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter1 = new FileNameExtensionFilter("Cassette Files (*.cas)", "cas");
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Create Cassette File");
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(filter1);
        if (fileChooser.showSaveDialog(container) == JFileChooser.APPROVE_OPTION) {
            if (!cassette.openFile(fileChooser.getSelectedFile().toString())) {
                JOptionPane.showMessageDialog(container, "Error opening file.", "File Open Problem",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
