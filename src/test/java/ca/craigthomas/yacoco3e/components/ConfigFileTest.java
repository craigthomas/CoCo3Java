/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.ConfigFile;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ConfigFileTest
{
    private ConfigFile configFile;

    @Before
    public void setUp() {
        configFile = new ConfigFile();
    }

    @Test
    public void testHasSystemROMFalseOnEmpty() {
        assertFalse(configFile.hasSystemROM());
    }

    @Test
    public void testHasSystemROMTrueWhenSet() {
        configFile.setSystemROM("not empty");
        assertTrue(configFile.hasSystemROM());
    }

    @Test
    public void testHasCartridgeROMFalseOnEmpty() {
        assertFalse(configFile.hasCartridgeROM());
    }

    @Test
    public void testHasCartridgeROMTrueWhenSet() {
        configFile.setCartridgeROM("not empty");
        assertTrue(configFile.hasCartridgeROM());
    }

    @Test
    public void testHasCassetteROMFalseOnEmpty() {
        assertFalse(configFile.hasCassetteROM());
    }

    @Test
    public void testHasCassetteROMTrueWhenSet() {
        configFile.setCassetteROM("not empty");
        assertTrue(configFile.hasCassetteROM());
    }

    @Test
    public void testSettersAndGetters() {
        configFile.setSystemROM("system");
        configFile.setCartridgeROM("cartridge");
        configFile.setCassetteROM("cassette");
        configFile.setDrive0Image("0");
        configFile.setDrive1Image("1");
        configFile.setDrive2Image("2");
        configFile.setDrive3Image("3");
        assertEquals("system", configFile.getSystemROM());
        assertEquals("cartridge", configFile.getCartridgeROM());
        assertEquals("cassette", configFile.getCassetteROM());
        assertEquals("0", configFile.getDrive0Image());
        assertEquals("1", configFile.getDrive1Image());
        assertEquals("2", configFile.getDrive2Image());
        assertEquals("3", configFile.getDrive3Image());
    }

    @Test
    public void testIsEmptyTrueWhenAllEmpty() {
        assertTrue(configFile.isEmpty());
    }

    @Test
    public void testIsEmptyTrueWhenAtLeastOneExists() {
        configFile.setSystemROM("not empty");
        assertFalse(configFile.isEmpty());
        configFile = new ConfigFile();

        configFile.setCartridgeROM("not empty");
        assertFalse(configFile.isEmpty());
        configFile = new ConfigFile();

        configFile.setCassetteROM("not empty");
        assertFalse(configFile.isEmpty());
        configFile = new ConfigFile();

        configFile.setDrive0Image("not empty");
        assertFalse(configFile.isEmpty());
        configFile = new ConfigFile();

        configFile.setDrive1Image("not empty");
        assertFalse(configFile.isEmpty());
        configFile = new ConfigFile();

        configFile.setDrive2Image("not empty");
        assertFalse(configFile.isEmpty());
        configFile = new ConfigFile();

        configFile.setDrive3Image("not empty");
        assertFalse(configFile.isEmpty());
        configFile = new ConfigFile();
    }

    @Test
    public void testConstructor1() {
        configFile = new ConfigFile("system", "cartridge", "cassette");
        configFile.setSystemROM("system");
        configFile.setCartridgeROM("cartridge");
        configFile.setCassetteROM("cassette");
    }

    @Test
    public void testConstructor2() {
        configFile = new ConfigFile("system", "cartridge", "0", "1", "2", "3", "cassette");
        configFile.setSystemROM("system");
        configFile.setCartridgeROM("cartridge");
        configFile.setCassetteROM("cassette");
        assertEquals("0", configFile.getDrive0Image());
        assertEquals("1", configFile.getDrive1Image());
        assertEquals("2", configFile.getDrive2Image());
        assertEquals("3", configFile.getDrive3Image());
    }

    @Test
    public void testParseConfigFileCorrectFormat() throws NullPointerException {
        File resourceFile = new File(getClass().getClassLoader().getResource("test_config.yml").getFile());
        configFile = ConfigFile.parseConfigFile(resourceFile.getPath());
        assertEquals("system", configFile.getSystemROM());
        assertEquals("cartridge", configFile.getCartridgeROM());
        assertEquals("cassette", configFile.getCassetteROM());
        assertEquals("0", configFile.getDrive0Image());
        assertEquals("1", configFile.getDrive1Image());
        assertEquals("2", configFile.getDrive2Image());
        assertEquals("3", configFile.getDrive3Image());
    }

    @Test
    public void testParseConfigFileReturnsNullOnNullInput() {
        configFile = ConfigFile.parseConfigFile(null);
        assertNull(configFile);
    }

    @Test
    public void testParseConfigFileReturnsNullOnBadFilename() {
        configFile = ConfigFile.parseConfigFile("/this_directory_does_not_exist/this_file_does_not_exist.yml");
        assertNull(configFile);
    }
}
