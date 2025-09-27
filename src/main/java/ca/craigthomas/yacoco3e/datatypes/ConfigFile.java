/*
 * Copyright (C) 2023-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.datatypes;

import ca.craigthomas.yacoco3e.common.IO;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class ConfigFile
{
    private String systemROM;
    private String cartridgeROM;
    private String drive0Image;
    private String drive1Image;
    private String drive2Image;
    private String drive3Image;
    private String cassetteROM;
    private String rightJoystick;
    private String leftJoystick;

    public ConfigFile() { }

    public ConfigFile(String system, String cartridge, String drive0, String drive1, String drive2,
                      String drive3, String cassette, String leftJoy, String rightJoy) {
        systemROM = system;
        cartridgeROM = cartridge;
        drive0Image = drive0;
        drive1Image = drive1;
        drive2Image = drive2;
        drive3Image = drive3;
        cassetteROM = cassette;
        leftJoystick = leftJoy;
        rightJoystick = rightJoy;
    }

    public ConfigFile(String system, String cartridge, String cassette) {
        systemROM = system;
        cartridgeROM = cartridge;
        cassetteROM = cassette;
    }

    public boolean hasSystemROM() {
        return systemROM != null;
    }

    public boolean hasCartridgeROM() {
        return cartridgeROM != null;
    }

    public boolean hasCassetteROM() {
        return cassetteROM != null;
    }

    public boolean isEmpty() {
        return (systemROM == null) && (cartridgeROM == null) && (cassetteROM == null) && (drive0Image == null) &&
                (drive1Image == null) && (drive2Image == null) && (drive3Image == null) && (leftJoystick == null) &&
                (rightJoystick == null);
    }

    public String getSystemROM() {
        return systemROM;
    }

    public void setSystemROM(String newSystemROM) {
        systemROM = newSystemROM;
    }

    public String getCartridgeROM() {
        return cartridgeROM;
    }

    public void setCartridgeROM(String cartridgeROM) {
        this.cartridgeROM = cartridgeROM;
    }

    public String getDrive0Image() {
        return drive0Image;
    }

    public void setDrive0Image(String drive0Image) {
        this.drive0Image = drive0Image;
    }

    public String getDrive1Image() {
        return drive1Image;
    }

    public void setDrive1Image(String drive1Image) {
        this.drive1Image = drive1Image;
    }

    public String getDrive2Image() {
        return drive2Image;
    }

    public void setDrive2Image(String drive2Image) {
        this.drive2Image = drive2Image;
    }

    public String getDrive3Image() {
        return drive3Image;
    }

    public void setDrive3Image(String drive3Image) {
        this.drive3Image = drive3Image;
    }

    public String getCassetteROM() {
        return cassetteROM;
    }

    public void setCassetteROM(String cassetteROM) {
        this.cassetteROM = cassetteROM;
    }

    public String getRightJoystick() {
        return rightJoystick;
    }

    public void setRightJoystick(String rightJoystick) {
        this.rightJoystick = rightJoystick;
    }

    public String getLeftJoystick() {
        return leftJoystick;
    }

    public void setLeftJoystick(String leftJoystick) {
        this.leftJoystick = leftJoystick;
    }

    /**
     * Parses a configuration file. Must contain valid YAML.
     *
     * @param filename the name of the configuration file to parse
     * @return a ConfigFile object
     */
    public static ConfigFile parseConfigFile(String filename) {
        if (filename == null) {
            return null;
        }

        Yaml configYaml = new Yaml(new Constructor(ConfigFile.class, new LoaderOptions()));
        InputStream stream = IO.openInputStream(filename);
        if (stream == null) {
            return null;
        }

        ConfigFile configFile = configYaml.load(stream);
        IO.closeStream(stream);
        return configFile;
    }
}
