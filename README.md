# Yet Another Color Computer 3 Emulator


[![Build Status](https://travis-ci.org/craigthomas/CoCo3Java.svg?branch=master&style=flat)](https://travis-ci.org/craigthomas/CoCo3Java) 
[![Coverage Status](https://codecov.io/gh/craigthomas/CoCo3Java/branch/master/graph/badge.svg)](https://codecov.io/gh/craigthomas/CoCo3Java)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/34856186055249429f2b80c9d286fc8d)](https://www.codacy.com/app/craig-thomas/CoCo3Java?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=craigthomas/CoCo3Java&amp;utm_campaign=Badge_Grade)
[![Dependency Status](https://dependencyci.com/github/craigthomas/CoCo3Java/badge)](https://dependencyci.com/github/craigthomas/CoCo3Java)

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

```
gradlew.bat build
```

The compiled Jar file will be placed in the `build/libs` directory.


## Running

#### Running a ROM

The command-line interface requires a single argument, which is the
path to a Color Computer 3 ROM file. The ROM may be either a 
Super-Extended Color Basic ROM, or a cartridge ROM. The syntax is as
follows:

```bash
java -jar build/libs/yacoco3e-1.0-all.jar /path/to/rom/file.rom
```

#### Trace Mode

It is possible to have the emulator provide a full disassembly of what
is running as the emulator is running it. Note however that the speed of
the emulator will be significantly slower:

```bash
java -jar build/libs/yacoco3e-1.0-all.jar file.rom --trace
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
java -jar build/libs/yacoco3e-1.0-all.jar file.rom --cassette /path/to/cas/file
```
 
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
a Disk Basic ROM (1.0 or 1.1) to be loaded on emulator startup with the `--diskbas`
switch:

```bash
java -jar build/libs/yacoco3e-1.0-all.jar super-ext-basic.rom --diskbas /path/to/disk/basic/rom
```

Four virtual disk drives are available by default (drive numbers 0-4). While all 
disk commands work (e.g. `DSKINI0`, `SAVE`, `DIR`, etc.), saving the actual contents
of a disk as a disk file (`.DSK` or `.DMK`) is not yet supported (nor is loading a
disk file from the host computer).


## Keyboard

The Color Computer 3 keyboard maps most keys to their traditional 
counterparts. However, there are some differences with shifted keys
as well as some regular keystrokes. These are listed below:

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

Note that these key mappings are likely to change in the future.


## Current Status

Below are a list of items that are currently working:

- Can run a Super Extended Color Basic ROM
- 6809 CPU full instruction set
- 512K physical memory
- Semigraphics Mode `SG4`
- Keyboard
- ROM/RAM Mapping  
- MMU Task PARs (`$FFA8` - `$FFAF`)
- MMU Executive PARs (`$FFA0` - `$FFA7`)
- Vertical Offset Registers (`$FF9D`, `$FF9E`)
- INIT 1 Register (`$FF91`)
- SAM Display Offset Registers (`$FFC6` - `$FFD3`)
- SAM TY Bits (`$FFDE`, `$FFDF`) 
- Cassette tape interface
- IRQ Interrupts (both PIA and GIME)
- Disk drive sub-system

Yet to be implemented:

- Disk GUI interface and saving (.DSK and .DMK images)
- Menu system
- Semigraphcs Modes `SG6`, `SG8`, `SG12`, `SG24`
- Graphics Modes `G1C`, `G1R`, `G2C`, `G2R`, `G3C`, `G3R`, `G6C`, `G6R`
- High resolution text modes (40, 64, 80 columns)
- High resolution graphic modes (192, 200, 255 x 64, 80, 128, 160, 256, 320, 512, 640)  
- VDG Register (`$FF22`)
- SAM Mode Register (`$FFC0` - `$FFC5`)
- Video Resolution Register (`$FF99`)
- Border Color Register (`$FF9A`)
- Vertical Scroll Register (`$FF9C`)
- Horizontal Offset Register (`$FF9F`)
- Palette Registers (`$FFB0` - `$FFBF`) 
- FIRQ and NMI interrupts
- PIA2 interface
- Sound