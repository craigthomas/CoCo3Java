# Yet Another Color Computer 3 Emulator

[![Build Status](https://img.shields.io/travis/craigthomas/CoCo3Java?style=flat-square)](https://travis-ci.com/craigthomas/CoCo3Java) 
[![Coverage Status](https://img.shields.io/codecov/c/github/craigthomas/CoCo3Java?style=flat-square)](https://codecov.io/gh/craigthomas/CoCo3Java)
[![Codacy Badge](https://img.shields.io/codacy/grade/34856186055249429f2b80c9d286fc8d?style=flat-square)](https://www.codacy.com/app/craig-thomas/CoCo3Java?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=craigthomas/CoCo3Java&amp;utm_campaign=Badge_Grade)
[![Dependency Status](https://img.shields.io/librariesio/github/craigthomas/CoCo3Java?style=flat-square)](https://libraries.io/github/craigthomas/CoCo3Java)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square)](https://opensource.org/licenses/MIT)

## Table of Contents

1. [What Is It?](#what-is-it)
2. [License](#license)
3. [Compiling](#compiling)
4. [Running](#running)
    1. [Specifying a System ROM](#specifying-a-system-rom)
    2. [Trace Mode](#trace-mode)
5. [Cassette Tapes](#cassette-tapes)
    1. [Reading](#reading)
    2. [Writing](#writing)
6. [Disk Drives](#disk-drives)
    1. [Loading a Disk Image](#loading-a-disk-image)
    2. [Saving a Disk Image](#saving-a-disk-image)
7. [Configuration File](#configuration-file)
8. [Keyboard](#keyboard)
    1. [Emulated Keyboard](#emulated-keyboard)
    2. [Pass-through Keyboard](#pass-through-keyboard)
9. [Current Status](#current-status)

## What Is It?

This project is a Color Computer 3 emulator written in Java. Note that I cannot 
distribute ROM files with the emulator, as they are copyright their 
respective owners.

The Color Computer 3 is the third incarnation of the Tandy Radio Shack 
Color Computer line (TRS-80). The CoCo 3 offered several improvements 
over the original CoCo 1 and CoCo 2, most notably the introduction of 
a memory management unit (MMU) and a new Advanced Color Video Chip 
(ACVC) - also known as the Graphics Interrupt Memory Enhancer (GIME).

While the official name of the computer was the TRS-80 Color Computer 
3, the Color Computer family was quite different from the line of 
business machines such as the TRS-80 Model I, II, III, and 4. While 
that family of computers used a Zilog Z80 microprocessor, the Color 
Computer family used a Motorola 6809E processor running at 0.89 MHz.


## License

This project makes use of an MIT style license. Please see the file 
called LICENSE for more information. Note that this project may make use
of other software that has separate license terms. See the section called
Third Party Licenses and Attributions below for more information on those
software components.


## Compiling

Simply copy the source files to a directory of your choice. In addition 
to the source, you will need the following required software packages:

- Java JDK 8 1.8.0 u141 or later

To build the project, switch to the root of the source directory, and type:
 
```bash
./gradlew build
```

On Windows, switch to the root of the source directory, and type:

```bash
gradlew.bat build
```

The compiled Jar file will be placed in the `build/libs` directory.


## Running

For the emulator to run, you will need to have the Java 8 Runtime Environment (JRE)
installed on your computer. See [Oracle's Java SE Runtime Environment Download](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
page for more information on installing the JRE. Alternatively, you can
use [OpenJDK](https://openjdk.java.net/install/).

Simply double-clicking the jar file will start the emulator running. By
default, the emulator will be in paused mode until you attach a system
ROM to it. You can do so by clicking *ROM*, *Load System ROM*. You can
also specify what ROM file to load via the command line (see [Specifying
a System ROM](#specifying-a-system-rom) section below on how to specify a ROM on the
command-line, and the [Configuration File](#configuration-file) section
below on how to create a configuration file so you don't have to specify
the system ROM each time you start the emulator).


#### Specifying a System ROM

The system ROM refers to the basic operating system of the machine.
Usually this is a Super Extended Color Basic ROM, or a cartridge ROM.
The syntax to specify the system ROM is as follows:

```bash
java -jar build/libs/yacoco3e-1.0-all.jar --system /path/to/rom/file.rom
```

See the section on [Configuration File](#configuration-file) for more
information on how to create a configuration file so that you don't
have to specify ROM files or switches on the command-line.

#### Trace Mode

It is possible to have the emulator provide a full disassembly of what
is running as the emulator is running it. Note however that the speed of
the emulator will be significantly slower:

```bash
java -jar build/libs/yacoco3e-1.0-all.jar --trace
```

## Cassette Tapes

#### Reading 

The emulator can read standard CAS files. To load a cassette file into
the input buffer, click on the menu item *Cassette*, *Open for Playback*.
You will be prompted to browse to a location on your computer where the
cassette file is located. You can then issue `CLOAD` or `CLOADM` commands
as usual in the emulator window.

You can also attach a cassette tape file to the emulator on startup with the 
following:

```bash
java -jar build/libs/yacoco3e-1.0-all.jar --cassette /path/to/cas/file
```

See the section on [Configuration File](#configuration-file) for more
information on how to create a configuration file so that you don't
have to specify ROM files or switches on the command-line.
 
#### Writing

To write to a cassette file, you must first create a new cassette file. You can 
do this by clicking the menu item *Cassette*, *New Cassette File*. You will be
prompted to create a new file on your computer where the cassette file contents
will be saved.

Once the cassette file has been created, you can perform a `CSAVE` or `CSAVEM`
as usual in the emulator window.

Once you have performed a `CSAVE` or `CSAVEM`, you must then flush the contents
of the tape buffer to the actual file. You can to that by clicking the menu item
*Cassette*, *Flush Buffer to File*.


## Disk Drives

The emulator has built-in support for disk drive systems, however, it requires
a Disk Basic ROM (1.0 or 1.1) to be loaded into the cartridge slot on the emulator
with the `--cartridge` switch:

```bash
java -jar build/libs/yacoco3e-1.0-all.jar --cartridge /path/to/disk/basic/rom
```

Four virtual disk drives are available by default (drive numbers 0-3).

See the section on [Configuration File](#configuration-file) for more
information on how to create a configuration file so that you don't
have to specify ROM files or switches on the command-line.


#### Loading a Disk Image

The emulator currently supports reading `JV1` style disk images from the host
computer. To load a disk image, click on the menu item *Disk Drives*,
select the drive number you want (*Drive 0*, *Drive 1*, *Drive 2*, or
*Drive 3*), and then select *Load Virtual Disk*. You will be prompted to
select a location on your computer where the disk file will be loaded
from. You can then use the disk associated with the drive you selected.
For example, `DIR 3` will list the directory contents of the disk in
drive 3, while `LOADM"2:EDTASM.BIN` will load the Edtasm binary from
drive 2.

#### Saving a Disk Image

The emulator currently support writing `JV1` style disk images to the
host computer. To save a disk image, click on the menu item *Disk Drives*,
select the drive number you want (*Drive 0*, *Drive 1*, *Drive 2*, or
*Drive 3*), and then select *Save Virtual Disk*. You will be prompted to
select a location on your computer where the disk file will be saved to.
Once entered, the contents of the drive will be saved to the virtual disk
file, and can be loaded from the host computer in a future session.


## Configuration File

The emulator allows you to create a simple configuration file so that
you do not have to specify arguments on the command line. The configuration
file is in YAML format, and supports the following keys:

* `systemROM` - the full path to the ROM file to be used as the system ROM (e.g.
Super Extended Color Basic ROM file).
* `cartridgeROM` - the full path to the ROM file plugged into the cartridge (e.g.
Megabug).
* `cassetteROM` - the full path to the ROM file used in the cassette recorder.
* `drive0Image` - the `DSK` image to be used in drive 0.
* `drive1Image` - the `DSK` image to be used in drive 0.
* `drive2Image` - the `DSK` image to be used in drive 0.
* `drive3Image` - the `DSK` image to be used in drive 0.

Leaving any one of the keys out will result in the emulator ignoring that particular
ROM image. An example YAML configuration file that specifies ROMs to use for the
system, cartridge slot, cassette, and drive 0 is as follows:

```
systemROM: "C:\Users\basic3.rom"
cartridgeROM: "C:\disk11.rom"
cassetteROM: "C:\Users\zaxxon.cas"
drive0Image: "C:\megabug.dsk"
```

If you start the emulator without command-line arguments, it will look for a configuration file named
`config.yml` in the current execution directory. This means you can just run the jar file or
double click it without specifying anything at the command-line. If you want to specify
a different configuration file to use, you must pass the `--config` option on the
command-line:

```
java -jar build/libs/yacoco3e-1.0-all.jar --config "C:\Users\my-emulator-config.yml"
```

The order in which the emulator will attempt to interpret ROMs is:

1. Command-line specified ROMs (e.g. the `--cartridge` switch)
2. Command-line specified configuration file (e.g. with the `--config` switch)
3. Looking for `config.yml` in the current directory

If none of the options above result in any valid ROMs to use for the system ROM,
then the emulator will start, but will be in a paused mode. You can then attach
ROM files manually using the menu system.

## Keyboard

There are two different types of keyboards that the emulator supports.

### Emulated Keyboard

The emulated keyboard will attempt to map multiple keystrokes into their 
corresponding Color Computer 3 key presses. This means for instance, if you
type a `"` (double-quote) character on your keyboard, it will mimic pressing
`SHIFT`-`2` on the Color Computer 3 keyboard (which corresponds to the 
double-quote character). 

The emulated keyboard maps just about every multi-key keypress to a proper Color Computer
3 counterpart. There is a single difference noted below:

| CoCo 3 Key | Keyboard Combination |
| :--------: | :------------------: |
| `BREAK`    | `ESCAPE`             |

The emulated keyboard is enabled by default, as it provides a more natural
mapping from a contemporary keyboard to the Color Computer 3 keyboard. This
behaviour however, may cause problems with programs that rely on certain other
keyboard presses and combinations. In which case, you may wish to use a pass-through
keyboard setting (see below).

To switch to the emulated keyboard if it is currently not enabled, click on the 
menu option *Keyboard*, *Emulated Keyboard*. The option will have a selected icon
next to it if it is enabled.

### Pass-through Keyboard

The pass-through keyboard will not attempt to interpret multiple key presses. Instead,
it will pass on key combinations as pressed on the keyboard. For instance, pressing
`SHIFT`-`2` will literally pass on `SHIFT` and `2` as the keyboard combination, and will
ultimately produce a double-quote character `"`. For those who are not familiar with the 
Color Computer 3 layout, this behaviour may be confusing, as key combinations on a 
contemporary keyboard are not the same as on the Color Computer 3. The differences
are noted below:

| CoCo 3 Key | Keyboard Combination |
| :--------: | :------------------: |
| `!`        | `SHIFT`-`1`          |
| `"`        | `SHIFT`-`2`          |
| `#`        | `SHIFT`-`3`          |
| `$`        | `SHIFT`-`4`          |
| `%`        | `SHIFT`-`5`          |
| `&`        | `SHIFT`-`6`          |
| `'`        | `SHIFT`-`7`          |
| `(`        | `SHIFT`-`8`          |
| `)`        | `SHIFT`-`9`          |
| `=`        | `SHIFT`-`-`          |
| `+`        | `SHIFT`-`:`          |
| `:`        | `'`                  |
| `*`        | `SHIFT`-`'`          |
| `@`        | `ALT`                |
| `BREAK`    | `ESCAPE`             |  

To switch to the pass-through keyboard if it is currently not enabled, click on the 
menu option *Keyboard*, *Pass-through Keyboard*. The option will have a selected icon
next to it if it is enabled.

## Current Status

Below are a list of items that are currently working:

- Can run a Super Extended Color Basic ROM
- 6809 CPU full instruction set
- 512K physical memory
- Semigraphics Mode `SG4`, `SG6`, `SG8`, `SG12`, `SG24`
- Graphics Modes `G1C`, `G1R`, `G2C`, `G2R`, `G3C`, `G3R`, `G6C`, `G6R`
- Keyboard
- ROM/RAM Mapping  
- MMU Task PARs (`$FFA8` - `$FFAF`)
- MMU Executive PARs (`$FFA0` - `$FFA7`)
- Vertical Offset Registers (`$FF9D`, `$FF9E`)
- INIT 1 Register (`$FF91`)
- SAM Display Offset Registers (`$FFC6` - `$FFD3`)
- SAM TY Bits (`$FFDE`, `$FFDF`) 
- SAM Mode Registers (`$FFC0` - `$FFC5`)
- VDG Register (`$FF22`)
- Cassette tape interface
- IRQ Interrupts (both PIA and GIME)
- Disk drive sub-system
- Disk GUI interface for loading virtual disks
- Disk GUI interface for saving virtual disks
- `JV1` style virtual disk drive support

Yet to be implemented:

- `JV3` style virtual disk drive support
- `DMK` style virtual disk drive support
- High resolution text modes (40, 64, 80 columns)
- High resolution graphic modes (192, 200, 255 x 64, 80, 128, 160, 256, 320, 512, 640)  
- Video Resolution Register (`$FF99`)
- Border Color Register (`$FF9A`)
- Vertical Scroll Register (`$FF9C`)
- Horizontal Offset Register (`$FF9F`)
- Palette Registers (`$FFB0` - `$FFBF`) 
- FIRQ and NMI interrupts
- PIA2 interface
- Sound

