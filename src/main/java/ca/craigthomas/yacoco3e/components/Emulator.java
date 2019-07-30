/*
 * Copyright (C) 2017-2019 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;
import ca.craigthomas.yacoco3e.listeners.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.TimerTask;
import java.util.Timer;
import java.util.logging.Logger;

import javax.swing.UIManager.*;

public class Emulator extends Thread
{
    /* The main emulator components */
    private Screen screen;
    private Keyboard keyboard;
    private CPU cpu;
    private IOController ioController;
    private Cassette cassette;
    private Memory memory;

    // The Canvas on which all the drawing will take place
    private Canvas canvas;

    // The main Emulator container
    private JFrame container;
    private JMenuBar menuBar;

    /* State variables */
    private boolean trace;
    private boolean verbose;
    private volatile EmulatorStatus status;
    private Timer timer;
    private TimerTask timerTask;

    /* A logger for the emulator */
    private final static Logger LOGGER = Logger.getLogger(Emulator.class.getName());

    public static class Builder {
        private int scale;
        private String systemROM;
        private String cartridgeROM;
        private String cassetteFile;
        private String configFile;
        private boolean trace;
        private boolean verbose;

        public Builder() {
            scale = 1;
        }

        public Builder setScale(int newScale) {
            scale = newScale;
            return this;
        }

        public Builder setSystemROM(String filename) {
            systemROM = filename;
            return this;
        }

        public Builder setCartridgeROM(String filename) {
            cartridgeROM = filename;
            return this;
        }

        public Builder setCassetteFile(String filename) {
            cassetteFile = filename;
            return this;
        }

        public Builder setTrace(boolean newTrace) {
            trace = newTrace;
            return this;
        }

        public Builder setVerbose(boolean newVerbose) {
            verbose = newVerbose;
            return this;
        }

        public Builder setConfigFile(String newConfigFile) {
            configFile = newConfigFile;
            return this;
        }

        public Emulator build() {
            return new Emulator(this);
        }
    }

    private Emulator(Builder builder) {
        memory = new Memory();
        keyboard = new EmulatedKeyboard();
        screen = new Screen(builder.scale);
        cassette = new Cassette();
        ioController = new IOController(memory, new RegisterSet(), keyboard, screen, cassette);
        cpu = new CPU(ioController);
        ioController.setCPU(cpu);
        trace = builder.trace;
        verbose = builder.verbose;
        status = EmulatorStatus.STOPPED;

        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Nimbus LAF not available");
        }

        initEmulatorJFrame();

        // Check to see if we specified a configuration file
        ConfigFile builderConfig = ConfigFile.parseConfigFile(builder.configFile);

        // Set the configuration based on the command line arguments
        ConfigFile commandLineConfig = new ConfigFile(builder.systemROM, builder.cartridgeROM, builder.cassetteFile);

        // Attempt to load the assets for the emulator
        loadAssets(builderConfig, commandLineConfig);
    }

    /**
     * Loads assets into the emulator based. Will attempt to use command-line
     * specified assets first, then use configuration file specified assets.
     *
     * @param builderConfig the configuration specified by the emulator builder
     * @param commandLineConfig the configuration specified by the command line
     */
    public void loadAssets(ConfigFile builderConfig, ConfigFile commandLineConfig) {
        if (!commandLineConfig.isEmpty()) {
            loadFromConfigFile(commandLineConfig);
            return;
        }

        if (builderConfig != null && !builderConfig.isEmpty()) {
            loadFromConfigFile(builderConfig);
            return;
        }

        // Load a configuration from the environment
        ConfigFile environmentConfig = ConfigFile.parseConfigFile("config.yml");
        loadFromConfigFile(environmentConfig);
    }

    /**
     * Attempts to load emulator memory based upon a ConfigFile specification.
     *
     * @param config the ConfigFile to load
     */
    public void loadFromConfigFile(ConfigFile config) {
        if (config == null) {
            return;
        }

        if (config.hasSystemROM()) {
            if (memory.loadROM(config.getSystemROM(), MemoryType.ROM)) {
                setStatus(EmulatorStatus.RUNNING);
            }
        }

        if (config.hasCartridgeROM()) {
            if (!memory.loadROM(config.getCartridgeROM(), MemoryType.CARTRIDGE)) {
                setStatus(EmulatorStatus.PAUSED);
            }
        }

        if (config.hasCassetteROM()) {
            if (!cassette.openFile(config.getCassetteROM())) {
                LOGGER.severe("Could not load cassette file [" + config.getCassetteROM() + "]");
            } else {
                LOGGER.info("Loaded cassette file [" + config.getCassetteROM() + "]");
            }
        }

        String drive0 = config.getDrive0Image();
        if (drive0 != null) {
            JV1Disk disk = new JV1Disk();
            if (disk.loadFile(drive0)) {
                ioController.disk[0].loadFromVirtualDisk(disk);
            }
        }
    }

    /**
     * Resets the emulator.
     */
    public void reset() {
        memory.resetMemory();
        ioController.reset();
        cpu.reset();
    }

    /**
     * Initializes the JFrame that the emulator will use to draw onto. Will set up the menu system and
     * link the action listeners to the menu items. Returns the JFrame that contains all of the emulator
     * screen elements.
     */
    private void initEmulatorJFrame() {
        container = new JFrame("Yet Another CoCo 3 Emulator");
        menuBar = new JMenuBar();

        // Emulator menu
        JMenu emulatorMenu = new JMenu("Emulator");
        emulatorMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem pauseEmulatorItem = new JMenuItem("Pause", KeyEvent.VK_P);
        emulatorMenu.add(pauseEmulatorItem);

        JMenuItem resetEmulatorItem = new JMenuItem("Reset", KeyEvent.VK_R);
        emulatorMenu.add(resetEmulatorItem);

        emulatorMenu.addSeparator();

        JMenuItem quitFile = new JMenuItem("Quit", KeyEvent.VK_Q);
        quitFile.addActionListener(new QuitMenuItemActionListener(this));
        emulatorMenu.add(quitFile);
        menuBar.add(emulatorMenu);

        // ROM menu
        JMenu romMenu = new JMenu("ROM");
        romMenu.setMnemonic(KeyEvent.VK_R);

        JMenuItem openROMItem = new JMenuItem("Load System ROM", KeyEvent.VK_S);
        openROMItem.addActionListener(new OpenSystemROMMenuItemActionListener(this));
        romMenu.add(openROMItem);

        JMenuItem openCartROMItem = new JMenuItem("Load Cartridge ROM", KeyEvent.VK_C);
        openCartROMItem.addActionListener(new OpenCartridgeROMMenuItemActionListener(this));
        romMenu.add(openCartROMItem);
        menuBar.add(romMenu);

        // Cassette menu
        JMenu cassetteMenu = new JMenu("Cassette");
        cassetteMenu.setMnemonic(KeyEvent.VK_C);

        JMenuItem newCassetteItem = new JMenuItem("New Cassette File", KeyEvent.VK_N);
        newCassetteItem.addActionListener(new RecordCassetteMenuItemActionListener(this));
        cassetteMenu.add(newCassetteItem);

        JMenuItem flushItem = new JMenuItem("Flush Buffer to File", KeyEvent.VK_F);
        flushItem.addActionListener(new FlushCassetteMenuItemActionListener(cassette));
        cassetteMenu.add(flushItem);

        cassetteMenu.addSeparator();

        JMenuItem openCassetteItem = new JMenuItem("Open for Playback", KeyEvent.VK_O);
        openCassetteItem.addActionListener(new OpenCassetteMenuItemActionListener(this));
        cassetteMenu.add(openCassetteItem);

        menuBar.add(cassetteMenu);

        // Disk drive menu
        JMenu diskMenu = new JMenu("Disk Drives");
        diskMenu.setMnemonic(KeyEvent.VK_D);

        // Drive 0
        JMenu drive0MenuItem = new JMenu("Drive 0");
        drive0MenuItem.setMnemonic(KeyEvent.VK_0);
        JMenuItem drive0LoadDiskMenuItem = new JMenuItem("Load Virtual Disk", KeyEvent.VK_L);
        drive0LoadDiskMenuItem.addActionListener(new LoadVirtualDiskMenuItemActionListener(0,this));
        drive0MenuItem.add(drive0LoadDiskMenuItem);

        JMenuItem drive0SaveDiskMenuItem = new JMenuItem("Save Virtual Disk", KeyEvent.VK_S);
        drive0SaveDiskMenuItem.addActionListener(new SaveVirtualDiskMenuItemActionListener(0,this));
        drive0MenuItem.add(drive0SaveDiskMenuItem);
        diskMenu.add(drive0MenuItem);

        // Drive 1
        JMenu drive1MenuItem = new JMenu("Drive 1");
        drive1MenuItem.setMnemonic(KeyEvent.VK_1);
        JMenuItem drive1LoadDiskMenuItem = new JMenuItem("Load Virtual Disk", KeyEvent.VK_L);
        drive1LoadDiskMenuItem.addActionListener(new LoadVirtualDiskMenuItemActionListener(1,this));
        drive1MenuItem.add(drive1LoadDiskMenuItem);

        JMenuItem drive1SaveDiskMenuItem = new JMenuItem("Save Virtual Disk", KeyEvent.VK_S);
        drive1SaveDiskMenuItem.addActionListener(new SaveVirtualDiskMenuItemActionListener(1,this));
        drive1MenuItem.add(drive1SaveDiskMenuItem);
        diskMenu.add(drive1MenuItem);

        // Drive 2
        JMenu drive2MenuItem = new JMenu("Drive 2");
        drive2MenuItem.setMnemonic(KeyEvent.VK_2);
        JMenuItem drive2LoadDiskMenuItem = new JMenuItem("Load Virtual Disk", KeyEvent.VK_L);
        drive2LoadDiskMenuItem.addActionListener(new LoadVirtualDiskMenuItemActionListener(2,this));
        drive2MenuItem.add(drive2LoadDiskMenuItem);

        JMenuItem drive2SaveDiskMenuItem = new JMenuItem("Save Virtual Disk", KeyEvent.VK_S);
        drive2SaveDiskMenuItem.addActionListener(new SaveVirtualDiskMenuItemActionListener(2,this));
        drive2MenuItem.add(drive2SaveDiskMenuItem);
        diskMenu.add(drive2MenuItem);

        // Drive 3
        JMenu drive3MenuItem = new JMenu("Drive 3");
        drive3MenuItem.setMnemonic(KeyEvent.VK_3);
        JMenuItem drive3LoadDiskMenuItem = new JMenuItem("Load Virtual Disk", KeyEvent.VK_L);
        drive3LoadDiskMenuItem.addActionListener(new LoadVirtualDiskMenuItemActionListener(3,this));
        drive3MenuItem.add(drive3LoadDiskMenuItem);

        JMenuItem drive3SaveDiskMenuItem = new JMenuItem("Save Virtual Disk", KeyEvent.VK_S);
        drive3SaveDiskMenuItem.addActionListener(new SaveVirtualDiskMenuItemActionListener(3,this));
        drive3MenuItem.add(drive3SaveDiskMenuItem);
        diskMenu.add(drive3MenuItem);

        menuBar.add(diskMenu);

        // Keyboard menu
        JMenu keyboardMenu = new JMenu("Keyboard");
        keyboardMenu.setMnemonic(KeyEvent.VK_K);

        JRadioButtonMenuItem emulatedKeyboardMenuItem = new JRadioButtonMenuItem("Emulated Keyboard");
        emulatedKeyboardMenuItem.setSelected(true);
        keyboardMenu.add(emulatedKeyboardMenuItem);

        JRadioButtonMenuItem passthroughKeyboardMenuItem = new JRadioButtonMenuItem("Pass-through Keyboard");
        keyboardMenu.add(passthroughKeyboardMenuItem);

        emulatedKeyboardMenuItem.addActionListener(new SetEmulatedKeyboardActionListener(this, passthroughKeyboardMenuItem, emulatedKeyboardMenuItem));
        passthroughKeyboardMenuItem.addActionListener(new SetPassthroughKeyboardActionListener(this, passthroughKeyboardMenuItem, emulatedKeyboardMenuItem));

        menuBar.add(keyboardMenu);

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
     * Switches out the existing keyboard for a new one (emulated or pass-through).
     *
     * @param newKeyboard the new Keyboard to add as a keyListener
     */
    public void switchKeyListener(Keyboard newKeyboard) {
        canvas.removeKeyListener(keyboard);
        canvas.addKeyListener(newKeyboard);
        keyboard = newKeyboard;
        ioController.setKeyboard(keyboard);
    }

    /**
     * Will redraw the contents of the screen to the emulator window. Optionally, if
     * isInTraceMode is True, will also draw the contents of the overlayScreen to the screen.
     */
    private void refreshScreen()
    {
        if (screen.getResolutionChanged()) {
            attachCanvas();
            screen.clearResolutionChanged();
        }

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
        this.reset();
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                screen.refreshScreen();
                refreshScreen();
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0L, 33L);
        run();
    }

    /**
     * Runs the main emulator loop until the emulator is killed.
     */
    public void run() {
        int operationTicks = 0;

        while (status != EmulatorStatus.KILLED) {
            while (status == EmulatorStatus.RUNNING) {
                try {
                    operationTicks = cpu.executeInstruction();
                } catch (IllegalIndexedPostbyteException e) {
                    System.out.println(e.getMessage());
                    status = EmulatorStatus.PAUSED;
                }

                /* Increment timers if necessary */
                ioController.timerTick(operationTicks);

                /* Fire interrupts if set */
                cpu.serviceInterrupts();

                /* Check to see if we should trace the output */
                if (trace) {
                    System.out.print("PC:" + cpu.getLastPC() + " | OP:"
                            + cpu.getLastOperand() + " | " + cpu.getOpShortDesc());
                    if (verbose) {
                        System.out.print(" | " + ioController.regs);
                    }
                    System.out.print(" | " + cpu.getOpLongDesc());
                    System.out.println();
                }
            }
        }
        this.shutdown();
    }

    /**
     * Shuts down the emulator by killing any timer tasks, and removing the
     * main container.
     */
    public void shutdown() {
        timer.cancel();
        timer.purge();
        timerTask.cancel();
        container.dispose();
    }

    public void setStatus(EmulatorStatus status) {
        this.status = status;
    }

    public JFrame getContainer() {
        return container;
    }

    public Memory getMemory() {
        return memory;
    }

    public IOController getIOController() {
        return ioController;
    }

    public Cassette getCassette() {
        return cassette;
    }
}
