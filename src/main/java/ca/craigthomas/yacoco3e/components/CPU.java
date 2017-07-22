/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

import java.util.function.Function;

public class CPU
{
    RegisterSet regs;
    Memory memory;
    String opShortDesc;
    String opLongDescription;
    public final static UnsignedWord SWI3 = new UnsignedWord(0xFFF2);
    public final static UnsignedWord SWI2 = new UnsignedWord(0xFFF4);
    public final static UnsignedWord SWI = new UnsignedWord(0xFFFA);

    public CPU(RegisterSet registerSet, Memory memory) {
        this.regs = registerSet;
        this.memory = memory;
    }

    public void setShortDesc(String string, MemoryResult value) {
        if (value != null) {
            opShortDesc = String.format(string, value.getResult().getInt());
        } else {
            opShortDesc = string;
        }
    }

    /**
     * Executes the instruction as indicated by the operand. Will return the
     * total number of ticks taken to execute the instruction.
     *
     * @return the number of ticks taken up by the instruction
     */
    int executeInstruction() {
        int operationTicks = 0;
        MemoryResult memoryResult;
        UnsignedByte operand = memory.getPCByte(regs);
        regs.incrementPC();
        int bytes = 0;
        UnsignedWord tempWord = new UnsignedWord();

        switch (operand.getShort()) {

            /* NEG - Negate M - Direct */
            case 0x00:
                memoryResult = memory.getDirect(regs);
                operationTicks = 6;
                executeByteFunctionM(this::negate, memoryResult);
                setShortDesc("NEGM, DIR [%04X]", memoryResult);
                break;

            /* COM - Complement M - Direct */
            case 0x03:
                memoryResult = memory.getDirect(regs);
                operationTicks = 6;
                executeByteFunctionM(this::compliment, memoryResult);
                setShortDesc("COMM, DIR [%04X]", memoryResult);
                break;

            /* LSR - Logical Shift Right - Direct */
            case 0x04:
                memoryResult = memory.getDirect(regs);
                operationTicks = 6;
                executeByteFunctionM(this::logicalShiftRight, memoryResult);
                setShortDesc("LSRM, DIR [%04X]", memoryResult);
                break;

            /* ROR - Rotate Right - Direct */
            case 0x06:
                memoryResult = memory.getDirect(regs);
                operationTicks = 6;
                executeByteFunctionM(this::rotateRight, memoryResult);
                setShortDesc("RORM, DIR [%04X]", memoryResult);
                break;

            /* ASR - Arithmetic Shift Right - Direct */
            case 0x07:
                memoryResult = memory.getDirect(regs);
                operationTicks = 6;
                executeByteFunctionM(this::arithmeticShiftRight, memoryResult);
                setShortDesc("ASRM, DIR [%04X]", memoryResult);
                break;

            /* ASL - Arithmetic Shift Left - Direct */
            case 0x08:
                memoryResult = memory.getDirect(regs);
                operationTicks = 6;
                executeByteFunctionM(this::arithmeticShiftLeft, memoryResult);
                setShortDesc("ASLM, DIR [%04X]", memoryResult);
                break;

            /* ROL - Rotate Left - Direct */
            case 0x09:
                memoryResult = memory.getDirect(regs);
                operationTicks = 6;
                executeByteFunctionM(this::rotateLeft, memoryResult);
                setShortDesc("ROLM, DIR [%04X]", memoryResult);
                break;

            /* DEC - Decrement - Direct */
            case 0x0A:
                memoryResult = memory.getDirect(regs);
                operationTicks = 6;
                executeByteFunctionM(this::decrement, memoryResult);
                setShortDesc("DECM, DIR [%04X]", memoryResult);
                break;

            /* INC - Increment - Direct */
            case 0x0C:
                memoryResult = memory.getDirect(regs);
                operationTicks = 6;
                executeByteFunctionM(this::increment, memoryResult);
                setShortDesc("INCM, DIR [%04X]", memoryResult);
                break;

            /* TST - Test - Direct */
            case 0x0D:
                memoryResult = memory.getDirect(regs);
                operationTicks = 6;
                executeByteFunctionM(this::test, memoryResult);
                setShortDesc("TSTM, DIR [%04X]", memoryResult);
                break;

            /* JMP - Jump - Direct */
            case 0x0E:
                memoryResult = memory.getDirect(regs);
                operationTicks = 3;
                jump(memoryResult.getResult());
                setShortDesc("JMP, DIR [%04X]", memoryResult);
                break;

            /* CLR - Clear - Direct */
            case 0x0F:
                memoryResult = memory.getDirect(regs);
                operationTicks = 6;
                executeByteFunctionM(this::clear, memoryResult);
                setShortDesc("CLRM, DIR [%04X]", memoryResult);
                break;

            /* 0x10 - Extended Opcodes */
            case 0x10:
            {
                UnsignedByte extendedOp = memory.getPCByte(regs);
                regs.incrementPC();

                switch(extendedOp.getShort()) {

                    /* LBRN - Long Branch Never */
                    case 0x21:
                        memoryResult = memory.getImmediateWord(regs);
                        operationTicks = 5;
                        setShortDesc("LBRN, REL [%04X]", memoryResult);
                        break;

                    /* LBHI - Long Branch on Higher */
                    case 0x22:
                        memoryResult = memory.getImmediateWord(regs);
                        if (!regs.ccCarrySet() && !regs.ccZeroSet()) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBHI, REL [%04X]", memoryResult);
                        break;

                    /* LBLS - Long Branch on Lower or Same */
                    case 0x23:
                        memoryResult = memory.getImmediateWord(regs);
                        if (regs.ccCarrySet() || regs.ccZeroSet()) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBLS, REL [%04X]", memoryResult);
                        break;

                    /* LBCC - Long Branch on Carry Clear */
                    case 0x24:
                        memoryResult = memory.getImmediateWord(regs);
                        if (!regs.ccCarrySet()) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBCC, REL [%04X]", memoryResult);
                        break;

                    /* LBCS - Long Branch on Carry Set */
                    case 0x25:
                        memoryResult = memory.getImmediateWord(regs);
                        if (regs.ccCarrySet()) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBCS, REL [%04X]", memoryResult);
                        break;

                    /* LBNE - Long Branch on Not Equal */
                    case 0x26:
                        memoryResult = memory.getImmediateWord(regs);
                        if (!regs.ccZeroSet()) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBNE, REL [%04X]", memoryResult);
                        break;

                    /* LBNE - Long Branch on Equal */
                    case 0x27:
                        memoryResult = memory.getImmediateWord(regs);
                        if (regs.ccZeroSet()) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBEQ, REL [%04X]", memoryResult);
                        break;

                    /* LBVC - Long Branch on Overflow Clear */
                    case 0x28:
                        memoryResult = memory.getImmediateWord(regs);
                        if (!regs.ccOverflowSet()) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBVC, REL [%04X]", memoryResult);
                        break;

                    /* LBVS - Long Branch on Overflow Set */
                    case 0x29:
                        memoryResult = memory.getImmediateWord(regs);
                        if (regs.ccOverflowSet()) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBVS, REL [%04X]", memoryResult);
                        break;

                    /* LBPL - Long Branch on Plus */
                    case 0x2A:
                        memoryResult = memory.getImmediateWord(regs);
                        if (!regs.ccNegativeSet()) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBPL, REL [%04X]", memoryResult);
                        break;

                    /* LBMI - Long Branch on Minus */
                    case 0x2B:
                        memoryResult = memory.getImmediateWord(regs);
                        if (regs.ccNegativeSet()) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBMI, REL [%04X]", memoryResult);
                        break;

                    /* LBGE - Long Branch on Greater Than or Equal to Zero */
                    case 0x2C:
                        memoryResult = memory.getImmediateWord(regs);
                        if (!regs.ccNegativeSet() ^ !regs.ccOverflowSet()) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBGE, REL [%04X]", memoryResult);
                        break;

                    /* LBLT - Long Branch on Less Than or Equal to Zero */
                    case 0x2D:
                        memoryResult = memory.getImmediateWord(regs);
                        if (regs.ccNegativeSet() ^ regs.ccOverflowSet()) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBLT, REL [%04X]", memoryResult);
                        break;

                    /* LBGT - Long Branch on Greater Than Zero */
                    case 0x2E:
                        memoryResult = memory.getImmediateWord(regs);
                        if (regs.ccZeroSet() && (regs.ccNegativeSet() ^ regs.ccOverflowSet())) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBGT, REL [%04X]", memoryResult);
                        break;

                    /* LBLE - Long Branch on Less Than Zero */
                    case 0x2F:
                        memoryResult = memory.getImmediateWord(regs);
                        if (regs.ccZeroSet() || (regs.ccNegativeSet() ^ regs.ccOverflowSet())) {
                            branchLong(memoryResult.getResult());
                            operationTicks = 6;
                        } else {
                            operationTicks = 5;
                        }
                        setShortDesc("LBLE, REL [%04X]", memoryResult);
                        break;

                    /* SWI3 - Software Interrupt 3 */
                    case 0x3F:
                        softwareInterrupt(SWI3);
                        operationTicks = 19;
                        setShortDesc("SWI3", null);
                        break;

                    /* CMPD - Compare D - Immediate */
                    case 0x83:
                        memoryResult = memory.getImmediateWord(regs);
                        compareWord(regs.getD(), memoryResult.getResult());
                        operationTicks = 5;
                        setShortDesc("CMPD, IMM", null);
                        break;

                    /* CMPY - Compare Y - Immediate */
                    case 0x8C:
                        memoryResult = memory.getImmediateWord(regs);
                        compareWord(regs.getY(), memoryResult.getResult());
                        operationTicks = 5;
                        setShortDesc("CMPY, IMM", null);
                        break;

                    /* LDY - Load Y - Immediate */
                    case 0x8E:
                        memoryResult = memory.getImmediateWord(regs);
                        loadRegister(Register.Y, memoryResult.getResult());
                        operationTicks = 4;
                        setShortDesc("LDY, IMM", memoryResult);
                        break;

                    /* CMPD - Compare D - Direct */
                    case 0x93:
                        memoryResult = memory.getDirect(regs);
                        compareWord(regs.getD(), memory.readWord(memoryResult.getResult()));
                        operationTicks = 7;
                        setShortDesc("CMPD, DIR", null);
                        break;

                    /* CMPY - Compare Y - Direct */
                    case 0x9C:
                        memoryResult = memory.getDirect(regs);
                        compareWord(regs.getY(), memory.readWord(memoryResult.getResult()));
                        operationTicks = 7;
                        setShortDesc("CMPY, DIR", null);
                        break;

                    /* LDY - Load Y - Direct */
                    case 0x9E:
                        memoryResult = memory.getDirect(regs);
                        loadRegister(Register.Y, memory.readWord(memoryResult.getResult()));
                        operationTicks = 6;
                        setShortDesc("LDY, DIR", memoryResult);
                        break;

                    /* STY - Store Y - Direct */
                    case 0x9F:
                        memoryResult = memory.getDirect(regs);
                        storeWordRegister(Register.Y, memory.readWord(memoryResult.getResult()));
                        operationTicks = 6;
                        setShortDesc("STY, DIR", memoryResult);
                        break;

                    /* CMPD - Compare D - Direct */
                    case 0xA3:
                        memoryResult = memory.getIndexed(regs);
                        compareWord(regs.getD(), memory.readWord(memoryResult.getResult()));
                        operationTicks = 5 + memoryResult.getBytesConsumed();
                        setShortDesc("CMPD, IND", null);
                        break;

                    /* CMPY - Compare Y - Direct */
                    case 0xAC:
                        memoryResult = memory.getIndexed(regs);
                        compareWord(regs.getY(), memory.readWord(memoryResult.getResult()));
                        operationTicks = 5 + memoryResult.getBytesConsumed();
                        setShortDesc("CMPY, IND", null);
                        break;

                    /* LDY - Load Y - Direct */
                    case 0xAE:
                        memoryResult = memory.getIndexed(regs);
                        loadRegister(Register.Y, memory.readWord(memoryResult.getResult()));
                        operationTicks = 4 + memoryResult.getBytesConsumed();
                        setShortDesc("LDY, IND", memoryResult);
                        break;

                    /* STY - Store Y - Indexed */
                    case 0xAF:
                        memoryResult = memory.getIndexed(regs);
                        storeWordRegister(Register.Y, memory.readWord(memoryResult.getResult()));
                        operationTicks = 4 + memoryResult.getBytesConsumed();
                        setShortDesc("STY, IND", memoryResult);
                        break;

                    /* CMPD - Compare D - Extended */
                    case 0xB3:
                        memoryResult = memory.getExtended(regs);
                        compareWord(regs.getD(), memory.readWord(memoryResult.getResult()));
                        operationTicks = 8;
                        setShortDesc("CMPD, EXT", null);
                        break;

                    /* CMPY - Compare Y - Extended */
                    case 0xBC:
                        memoryResult = memory.getExtended(regs);
                        compareWord(regs.getY(), memory.readWord(memoryResult.getResult()));
                        operationTicks = 8;
                        setShortDesc("CMPY, EXT", null);
                        break;

                    /* LDY - Load Y - Extended */
                    case 0xBE:
                        memoryResult = memory.getExtended(regs);
                        loadRegister(Register.Y, memory.readWord(memoryResult.getResult()));
                        operationTicks = 7;
                        setShortDesc("LDY, EXT", memoryResult);
                        break;

                    /* STY - Store Y - Extended */
                    case 0xBF:
                        memoryResult = memory.getExtended(regs);
                        storeWordRegister(Register.Y, memory.readWord(memoryResult.getResult()));
                        operationTicks = 7;
                        setShortDesc("STY, EXT", memoryResult);
                        break;

                    /* LDS - Load S - Immediate */
                    case 0xCE:
                        memoryResult = memory.getImmediateWord(regs);
                        loadRegister(Register.S, memoryResult.getResult());
                        operationTicks = 4;
                        setShortDesc("LDS, IMM", memoryResult);
                        break;

                    /* LDS - Load S - Direct */
                    case 0xDE:
                        memoryResult = memory.getDirect(regs);
                        loadRegister(Register.S, memory.readWord(memoryResult.getResult()));
                        operationTicks = 6;
                        setShortDesc("LDS, DIR", memoryResult);
                        break;

                    /* STS - Store S - Direct */
                    case 0xDF:
                        memoryResult = memory.getDirect(regs);
                        storeWordRegister(Register.S, memory.readWord(memoryResult.getResult()));
                        operationTicks = 6;
                        setShortDesc("STS, DIR", memoryResult);
                        break;

                    /* LDS - Load S - Indexed */
                    case 0xEE:
                        memoryResult = memory.getIndexed(regs);
                        loadRegister(Register.S, memory.readWord(memoryResult.getResult()));
                        operationTicks = 4 + memoryResult.getBytesConsumed();
                        setShortDesc("LDS, IND", memoryResult);
                        break;

                    /* STS - Store S - Indexed */
                    case 0xEF:
                        memoryResult = memory.getIndexed(regs);
                        storeWordRegister(Register.S, memory.readWord(memoryResult.getResult()));
                        operationTicks = 4 + memoryResult.getBytesConsumed();
                        setShortDesc("STS, IND", memoryResult);
                        break;

                    /* LDS - Load S - Extended */
                    case 0xFE:
                        memoryResult = memory.getExtended(regs);
                        loadRegister(Register.S, memory.readWord(memoryResult.getResult()));
                        operationTicks = 7;
                        setShortDesc("LDS, EXT", memoryResult);
                        break;

                    /* STS - Store S - Extended */
                    case 0xFF:
                        memoryResult = memory.getExtended(regs);
                        storeWordRegister(Register.S, memory.readWord(memoryResult.getResult()));
                        operationTicks = 7;
                        setShortDesc("STS, EXT", memoryResult);
                        break;
                }
                break;
            }

            /* 0x11 - Extended Opcodes */
            case 0x11: {
                UnsignedByte extendedOp = memory.getPCByte(regs);
                regs.incrementPC();

                switch (extendedOp.getShort()) {
                    /* SWI2 - Software Interrupt 2 */
                    case 0x3F:
                        softwareInterrupt(SWI2);
                        operationTicks = 20;
                        setShortDesc("SWI2", null);
                        break;

                    /* CMPU - Compare U - Immediate */
                    case 0x83:
                        memoryResult = memory.getImmediateWord(regs);
                        compareWord(regs.getU(), memoryResult.getResult());
                        operationTicks = 5;
                        setShortDesc("CMPU, IMM", null);
                        break;

                    /* CMPS - Compare S - Immediate */
                    case 0x8C:
                        memoryResult = memory.getImmediateWord(regs);
                        compareWord(regs.getS(), memoryResult.getResult());
                        operationTicks = 5;
                        setShortDesc("CMPS, IMM", null);
                        break;

                    /* CMPU - Compare U - Direct */
                    case 0x93:
                        memoryResult = memory.getDirect(regs);
                        compareWord(regs.getU(), memory.readWord(memoryResult.getResult()));
                        operationTicks = 7;
                        setShortDesc("CMPU, DIR", null);
                        break;

                    /* CMPS - Compare S - Direct */
                    case 0x9C:
                        memoryResult = memory.getDirect(regs);
                        compareWord(regs.getS(), memory.readWord(memoryResult.getResult()));
                        operationTicks = 7;
                        setShortDesc("CMPS, DIR", null);
                        break;

                    /* CMPU - Compare U - Direct */
                    case 0xA3:
                        memoryResult = memory.getIndexed(regs);
                        compareWord(regs.getU(), memory.readWord(memoryResult.getResult()));
                        operationTicks = 5 + memoryResult.getBytesConsumed();
                        setShortDesc("CMPU, IND", null);
                        break;

                    /* CMPS - Compare S - Direct */
                    case 0xAC:
                        memoryResult = memory.getIndexed(regs);
                        compareWord(regs.getS(), memory.readWord(memoryResult.getResult()));
                        operationTicks = 5 + memoryResult.getBytesConsumed();
                        setShortDesc("CMPS, IND", null);
                        break;

                    /* CMPU - Compare U - Extended */
                    case 0xB3:
                        memoryResult = memory.getExtended(regs);
                        compareWord(regs.getU(), memory.readWord(memoryResult.getResult()));
                        operationTicks = 8;
                        setShortDesc("CMPU, EXT", null);
                        break;

                    /* CMPS - Compare S - Extended */
                    case 0xBC:
                        memoryResult = memory.getExtended(regs);
                        compareWord(regs.getS(), memory.readWord(memoryResult.getResult()));
                        operationTicks = 8;
                        setShortDesc("CMPS, EXT", null);
                        break;
                }
                break;
            }

            /* NOP - No Operation - Inherent */
            case 0x12:
                operationTicks = 2;
                setShortDesc("NOP", null);
                break;

            /* SYNC - Sync - Inherent */
            case 0x13:
                setShortDesc("SYNC", null);
                break;

            /* LBRA - Long Branch Always - Immediate */
            case 0x16:
                memoryResult = memory.getImmediateWord(regs);
                branchLong(memoryResult.getResult());
                operationTicks = 5;
                setShortDesc("LBRA, IMM", null);
                break;

            /* DAA - Decimal Addition Adjust */
            case 0x19:
                decimalAdditionAdjust();
                operationTicks = 2;
                setShortDesc("DAA, IMM", null);
                break;

            /* ORCC - Logical OR on Condition Code Register */
            case 0x1A:
                memoryResult = memory.getImmediateByte(regs);
                regs.getCC().or(memoryResult.getResult().getHigh().getShort());
                operationTicks = 3;
                setShortDesc("ORCC, IMM", null);
                break;

            /* ANDCC - Logical AND on Condition Code Register */
            case 0x1C:
                memoryResult = memory.getImmediateByte(regs);
                regs.getCC().and(memoryResult.getResult().getHigh().getShort());
                operationTicks = 3;
                setShortDesc("ANDCC, IMM", null);
                break;

            /* SEX - Sign Extend */
            case 0x1D:
                regs.setA(regs.getB().isMasked(0x80) ? new UnsignedByte(0xFF) : new UnsignedByte());
                operationTicks = 2;
                setShortDesc("SEX, IMM", null);
                break;

            /* EXG - Exchange Register */
            case 0x1E: {
                memoryResult = memory.getImmediateByte(regs);
                UnsignedByte extendedOp = memoryResult.getResult().getHigh();
                UnsignedWord temp = new UnsignedWord();
                UnsignedByte tempByte = new UnsignedByte();
                setShortDesc("EXG, IMM", null);
                operationTicks = 8;
                switch (extendedOp.getShort()) {

                    /* A:B <-> X */
                    case 0x01:
                    case 0x10:
                        temp.set(regs.getX());
                        regs.setX(regs.getD());
                        regs.setD(temp);
                        break;

                    /* A:B <-> Y */
                    case 0x02:
                    case 0x20:
                        temp.set(regs.getY());
                        regs.setY(regs.getD());
                        regs.setD(temp);
                        break;

                    /* A:B <-> U */
                    case 0x03:
                    case 0x30:
                        temp.set(regs.getU());
                        regs.setU(regs.getD());
                        regs.setD(temp);
                        break;

                    /* A:B <-> S */
                    case 0x04:
                    case 0x40:
                        temp.set(regs.getS());
                        regs.setS(regs.getD());
                        regs.setD(temp);
                        break;

                    /* A:B <-> PC */
                    case 0x05:
                    case 0x50:
                        temp.set(regs.getPC());
                        regs.setPC(regs.getD());
                        regs.setD(temp);
                        break;

                    /* X <-> Y */
                    case 0x12:
                    case 0x21:
                        temp.set(regs.getX());
                        regs.setX(regs.getY());
                        regs.setY(temp);
                        break;

                    /* X <-> U */
                    case 0x13:
                    case 0x31:
                        temp.set(regs.getX());
                        regs.setX(regs.getU());
                        regs.setU(temp);
                        break;

                    /* X <-> S */
                    case 0x14:
                    case 0x41:
                        temp.set(regs.getX());
                        regs.setX(regs.getS());
                        regs.setS(temp);
                        break;

                    /* X <-> PC */
                    case 0x15:
                    case 0x51:
                        temp.set(regs.getX());
                        regs.setX(regs.getPC());
                        regs.setPC(temp);
                        break;

                    /* Y <-> U */
                    case 0x23:
                    case 0x32:
                        temp.set(regs.getY());
                        regs.setY(regs.getU());
                        regs.setU(temp);
                        break;

                    /* Y <-> S */
                    case 0x24:
                    case 0x42:
                        temp.set(regs.getS());
                        regs.setS(regs.getY());
                        regs.setY(temp);
                        break;

                    /* Y <-> PC */
                    case 0x25:
                    case 0x52:
                        temp.set(regs.getY());
                        regs.setY(regs.getPC());
                        regs.setPC(temp);
                        break;

                    /* U <-> S */
                    case 0x34:
                    case 0x43:
                        temp.set(regs.getU());
                        regs.setU(regs.getS());
                        regs.setS(temp);
                        break;

                    /* U <-> PC */
                    case 0x35:
                    case 0x53:
                        temp.set(regs.getU());
                        regs.setU(regs.getPC());
                        regs.setPC(temp);
                        break;

                    /* S <-> PC */
                    case 0x45:
                    case 0x54:
                        temp.set(regs.getS());
                        regs.setS(regs.getPC());
                        regs.setPC(temp);
                        break;

                    /* A <-> B */
                    case 0x89:
                    case 0x98:
                        tempByte.set(regs.getA());
                        regs.setA(regs.getB());
                        regs.setB(tempByte);
                        break;

                    /* A <-> CC */
                    case 0x8A:
                    case 0xA8:
                        tempByte.set(regs.getA());
                        regs.setA(regs.getCC());
                        regs.setCC(tempByte);
                        break;

                    /* A <-> DP */
                    case 0x8B:
                    case 0xB8:
                        tempByte.set(regs.getA());
                        regs.setA(regs.getDP());
                        regs.setDP(tempByte);
                        break;

                    /* B <-> CC */
                    case 0x9A:
                    case 0xA9:
                        tempByte.set(regs.getB());
                        regs.setB(regs.getCC());
                        regs.setCC(tempByte);
                        break;

                    /* B <-> DP */
                    case 0x9B:
                    case 0xB9:
                        tempByte.set(regs.getB());
                        regs.setB(regs.getDP());
                        regs.setDP(tempByte);
                        break;

                    /* CC <-> DP */
                    case 0xAB:
                    case 0xBA:
                        tempByte.set(regs.getCC());
                        regs.setCC(regs.getDP());
                        regs.setDP(tempByte);
                        break;

                    /* Self to self - ignored */
                    case 0x00:
                    case 0x11:
                    case 0x22:
                    case 0x33:
                    case 0x44:
                    case 0x55:
                    case 0x88:
                    case 0x99:
                    case 0xAA:
                    case 0xBB:
                        break;

                    default:
                        break;
                }
            }
            break;

            /* TFR - Transfer between registers */
            case 0x1F: {
                memoryResult = memory.getImmediateByte(regs);
                UnsignedByte extendedOp = memoryResult.getResult().getHigh();
                setShortDesc("TFR, IMM", null);
                operationTicks = 6;
                switch (extendedOp.getShort()) {

                    /* A:B -> X */
                    case 0x01:
                        regs.setX(regs.getD());
                        break;

                    /* A:B -> Y */
                    case 0x02:
                        regs.setY(regs.getD());
                        break;

                    /* A:B -> U */
                    case 0x03:
                        regs.setU(regs.getD());
                        break;

                    /* A:B -> S */
                    case 0x04:
                        regs.setS(regs.getD());
                        break;

                    /* A:B -> PC */
                    case 0x05:
                        regs.setPC(regs.getD());
                        break;

                    /* X -> A:B */
                    case 0x10:
                        regs.setD(regs.getX());
                        break;

                    /* X -> Y */
                    case 0x12:
                        regs.setY(regs.getX());
                        break;

                    /* X -> U */
                    case 0x13:
                        regs.setU(regs.getX());
                        break;

                    /* X -> S */
                    case 0x14:
                        regs.setS(regs.getX());
                        break;

                    /* X -> PC */
                    case 0x15:
                        regs.setPC(regs.getX());
                        break;

                    /* Y -> A:B */
                    case 0x20:
                        regs.setD(regs.getY());
                        break;

                    /* Y -> X */
                    case 0x21:
                        regs.setX(regs.getY());
                        break;

                    /* Y -> U */
                    case 0x23:
                        regs.setU(regs.getY());
                        break;

                    /* Y -> S */
                    case 0x24:
                        regs.setS(regs.getY());
                        break;

                    /* Y -> PC */
                    case 0x25:
                        regs.setPC(regs.getY());
                        break;

                    /* U -> A:B */
                    case 0x30:
                        regs.setD(regs.getU());
                        break;

                    /* U -> X */
                    case 0x31:
                        regs.setX(regs.getU());
                        break;

                    /* U -> Y */
                    case 0x32:
                        regs.setY(regs.getU());
                        break;

                    /* U -> S */
                    case 0x34:
                        regs.setS(regs.getU());
                        break;

                    /* U -> PC */
                    case 0x35:
                        regs.setPC(regs.getU());
                        break;

                    /* S -> A:B */
                    case 0x40:
                        regs.setD(regs.getS());
                        break;

                    /* S -> X */
                    case 0x41:
                        regs.setX(regs.getS());
                        break;

                    /* S -> Y */
                    case 0x42:
                        regs.setY(regs.getS());
                        break;

                    /* S -> U */
                    case 0x43:
                        regs.setU(regs.getS());
                        break;

                    /* S -> PC */
                    case 0x45:
                        regs.setPC(regs.getS());
                        break;

                    /* PC -> A:B */
                    case 0x50:
                        regs.setD(regs.getPC());
                        break;

                    /* PC -> X */
                    case 0x51:
                        regs.setX(regs.getPC());
                        break;

                    /* PC -> Y */
                    case 0x52:
                        regs.setY(regs.getPC());
                        break;

                    /* PC -> U */
                    case 0x53:
                        regs.setU(regs.getPC());
                        break;

                    /* PC -> S */
                    case 0x54:
                        regs.setS(regs.getPC());
                        break;

                    /* A -> B */
                    case 0x89:
                        regs.setB(regs.getA());
                        break;

                    /* A -> CC */
                    case 0x8A:
                        regs.setCC(regs.getA());
                        break;

                    /* A -> DP */
                    case 0x8B:
                        regs.setDP(regs.getA());
                        break;

                    /* B -> A */
                    case 0x98:
                        regs.setA(regs.getB());
                        break;

                    /* B -> CC */
                    case 0x9A:
                        regs.setCC(regs.getB());
                        break;

                    /* B -> DP */
                    case 0x9B:
                        regs.setDP(regs.getB());
                        break;

                    /* CC -> A */
                    case 0xA8:
                        regs.setA(regs.getCC());
                        break;

                    /* CC -> B */
                    case 0xA9:
                        regs.setB(regs.getCC());
                        break;

                    /* CC -> DP */
                    case 0xAB:
                        regs.setDP(regs.getCC());
                        break;

                    /* DP -> A */
                    case 0xB8:
                        regs.setA(regs.getDP());
                        break;

                    /* DP -> B */
                    case 0xB9:
                        regs.setB(regs.getDP());
                        break;

                    /* DP -> CC */
                    case 0xBA:
                        regs.setCC(regs.getDP());
                        break;

                    /* Self to self - ignored */
                    case 0x00:
                    case 0x11:
                    case 0x22:
                    case 0x33:
                    case 0x44:
                    case 0x55:
                    case 0x88:
                    case 0x99:
                    case 0xAA:
                    case 0xBB:
                        break;

                    default:
                        break;
                }
            }
            break;

            /* BRA - Branch Always */
            case 0x20:
                memoryResult = memory.getImmediateByte(regs);
                branchShort(memoryResult.getResult().getHigh());
                operationTicks = 3;
                setShortDesc("BRA, IMM", null);
                break;

            /* BRN - Branch Never */
            case 0x21:
                operationTicks = 3;
                setShortDesc("BRN, IMM", null);
                break;

            /* BHI - Branch on Higher */
            case 0x22:
                memoryResult = memory.getImmediateByte(regs);
                if (!regs.ccCarrySet() && !regs.ccZeroSet()) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 3;
                setShortDesc("BHI, REL [%04X]", memoryResult);
                break;

            /* BLE - Branch on Lower or Same */
            case 0x23:
                memoryResult = memory.getImmediateByte(regs);
                if (regs.ccCarrySet() || regs.ccZeroSet()) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 5;
                setShortDesc("BLE, REL [%04X]", memoryResult);
                break;

            /* BCC - Branch on Carry Clear */
            case 0x24:
                memoryResult = memory.getImmediateByte(regs);
                if (!regs.ccCarrySet()) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 3;
                setShortDesc("BCC, REL [%04X]", memoryResult);
                break;

            /* BCS - Branch on Carry Set */
            case 0x25:
                memoryResult = memory.getImmediateByte(regs);
                if (regs.ccCarrySet()) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 3;
                setShortDesc("BCS, REL [%04X]", memoryResult);
                break;

            /* BNE - Branch on Not Equal */
            case 0x26:
                memoryResult = memory.getImmediateByte(regs);
                if (!regs.ccZeroSet()) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 3;
                setShortDesc("LBNE, REL [%04X]", memoryResult);
                break;

            /* BEQ - Branch on Equal */
            case 0x27:
                memoryResult = memory.getImmediateByte(regs);
                if (regs.ccZeroSet()) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 3;
                setShortDesc("BEQ, REL [%04X]", memoryResult);
                break;

            /* BVC - Branch on Overflow Clear */
            case 0x28:
                memoryResult = memory.getImmediateByte(regs);
                if (!regs.ccOverflowSet()) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 3;
                setShortDesc("BVC, REL [%04X]", memoryResult);
                break;

            /* BVS - Branch on Overflow Set */
            case 0x29:
                memoryResult = memory.getImmediateByte(regs);
                if (regs.ccOverflowSet()) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 3;
                setShortDesc("BVS, REL [%04X]", memoryResult);
                break;

            /* BPL - Branch on Plus */
            case 0x2A:
                memoryResult = memory.getImmediateByte(regs);
                if (!regs.ccNegativeSet()) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 3;
                setShortDesc("BPL, REL [%04X]", memoryResult);
                break;

            /* BMI - Branch on Minus */
            case 0x2B:
                memoryResult = memory.getImmediateByte(regs);
                if (regs.ccNegativeSet()) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 3;
                setShortDesc("BMI, REL [%04X]", memoryResult);
                break;

            /* BGE - Branch on Greater Than or Equal to Zero */
            case 0x2C:
                memoryResult = memory.getImmediateByte(regs);
                if (!regs.ccNegativeSet() ^ !regs.ccOverflowSet()) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 3;
                setShortDesc("BGE, REL [%04X]", memoryResult);
                break;

            /* BLT - Branch on Less Than or Equal to Zero */
            case 0x2D:
                memoryResult = memory.getImmediateByte(regs);
                if (regs.ccNegativeSet() ^ regs.ccOverflowSet()) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 5;
                setShortDesc("BLT, REL [%04X]", memoryResult);
                break;

            /* BGT - Branch on Greater Than Zero */
            case 0x2E:
                memoryResult = memory.getImmediateByte(regs);
                if (regs.ccZeroSet() && (regs.ccNegativeSet() ^ regs.ccOverflowSet())) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 3;
                setShortDesc("BGT, REL [%04X]", memoryResult);
                break;

            /* BLE - Branch on Less Than Zero */
            case 0x2F:
                memoryResult = memory.getImmediateByte(regs);
                if (regs.ccZeroSet() || (regs.ccNegativeSet() ^ regs.ccOverflowSet())) {
                    branchShort(memoryResult.getResult().getHigh());
                }
                operationTicks = 3;
                setShortDesc("BLE, REL [%04X]", memoryResult);
                break;

            /* LEAX - Load Effective Address into X register */
            case 0x30:
                memoryResult = memory.getImmediateByte(regs);
                loadEffectiveAddress(Register.X, memory.readWord(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("LEAX, IMM [%04X]", memoryResult);
                break;

            /* LEAY - Load Effective Address into Y register */
            case 0x31:
                memoryResult = memory.getImmediateByte(regs);
                loadEffectiveAddress(Register.Y, memory.readWord(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("LEAY, IMM [%04X]", memoryResult);
                break;

            /* LEAS - Load Effective Address into S register */
            case 0x32:
                memoryResult = memory.getImmediateByte(regs);
                loadEffectiveAddress(Register.S, memory.readWord(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("LEAS, IMM [%04X]", memoryResult);
                break;

            /* LEAU - Load Effective Address into U register */
            case 0x33:
                memoryResult = memory.getImmediateByte(regs);
                loadEffectiveAddress(Register.U, memory.readWord(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("LEAU, IMM [%04X]", memoryResult);
                break;

            /* PSHS - Push Registers onto S Stack */
            case 0x34:
                memoryResult = memory.getImmediateByte(regs);
                bytes = pushStack(Register.S, memoryResult.getResult().getHigh());
                operationTicks = 5 + bytes;
                setShortDesc("PSHS, IMM", null);
                break;

            /* PULS - Pull Registers from S Stack */
            case 0x35:
                memoryResult = memory.getImmediateByte(regs);
                bytes = popStack(Register.S, memoryResult.getResult().getHigh());
                operationTicks = 5 + bytes;
                setShortDesc("PULS, IMM", null);
                break;

            /* PSHU - Push Registers onto U Stack */
            case 0x36:
                memoryResult = memory.getImmediateByte(regs);
                bytes = pushStack(Register.U, memoryResult.getResult().getHigh());
                operationTicks = 5 + bytes;
                setShortDesc("PSHU, IMM", null);
                break;

            /* PULU - Pull Registers from U Stack */
            case 0x37:
                memoryResult = memory.getImmediateByte(regs);
                bytes = popStack(Register.U, memoryResult.getResult().getHigh());
                operationTicks = 5 + bytes;
                setShortDesc("PULU, IMM", null);
                break;

            /* RTS - Return from Subroutine */
            case 0x39:
                regs.setPC(
                        new UnsignedWord(
                                memory.popStack(regs, Register.S),
                                memory.popStack(regs, Register.S)
                        )
                );
                operationTicks = 5;
                setShortDesc("RTS, IMM", null);
                break;

            /* ABX - Add Accumulator B into X */
            case 0x3A:
                tempWord = new UnsignedWord(
                        new UnsignedByte(),
                        regs.getB()
                );
                regs.setX(
                        regs.binaryAdd(regs.getX(), tempWord, false, false, false)
                );
                operationTicks = 3;
                setShortDesc("ABX, IMM", null);
                break;

            /* RTI - Return from Interrupt */
            case 0x3B:
                if (regs.ccEverythingSet()) {
                    operationTicks = 9;
                    regs.setCC(memory.popStack(regs, Register.S));
                    regs.setA(memory.popStack(regs, Register.S));
                    regs.setB(memory.popStack(regs, Register.S));
                    regs.setDP(memory.popStack(regs, Register.S));
                    regs.setX(
                            new UnsignedWord(
                                    memory.popStack(regs, Register.S),
                                    memory.popStack(regs, Register.S)
                            )
                    );
                    regs.setY(
                            new UnsignedWord(
                                    memory.popStack(regs, Register.S),
                                    memory.popStack(regs, Register.S)
                            )
                    );
                    regs.setU(
                            new UnsignedWord(
                                    memory.popStack(regs, Register.S),
                                    memory.popStack(regs, Register.S)
                            )
                    );
                }
                regs.setPC(
                        new UnsignedWord(
                                memory.popStack(regs, Register.S),
                                memory.popStack(regs, Register.S)
                        )
                );
                operationTicks += 6;
                setShortDesc("RTI, IMM", null);
                break;

            /* CWAI - Call and Wait for Interrupt */
            case 0x3C:
                memoryResult = memory.getImmediateByte(regs);
                regs.getCC().and(memoryResult.getResult().getHigh().getShort());
                regs.getCC().or(RegisterSet.CC_E);
                memory.pushStack(regs, Register.S, regs.getPC().getLow());
                memory.pushStack(regs, Register.S, regs.getPC().getHigh());
                memory.pushStack(regs, Register.S, regs.getU().getLow());
                memory.pushStack(regs, Register.S, regs.getU().getHigh());
                memory.pushStack(regs, Register.S, regs.getY().getLow());
                memory.pushStack(regs, Register.S, regs.getY().getHigh());
                memory.pushStack(regs, Register.S, regs.getX().getLow());
                memory.pushStack(regs, Register.S, regs.getX().getHigh());
                memory.pushStack(regs, Register.S, regs.getDP());
                memory.pushStack(regs, Register.S, regs.getB());
                memory.pushStack(regs, Register.S, regs.getA());
                memory.pushStack(regs, Register.S, regs.getCC());
                operationTicks = 20;
                setShortDesc("CWAI, IMM", null);
                break;

            /* MUL - Multiply Unsigned */
            case 0x3D:
                int tempResult = regs.getA().getShort() * regs.getB().getShort();
                tempResult &= 0xFFFF;
                regs.cc.and(~(RegisterSet.CC_Z | RegisterSet.CC_C));
                regs.cc.or(tempResult == 0 ? RegisterSet.CC_Z : 0);
                regs.cc.or(regs.getB().isNegative() ? RegisterSet.CC_C : 0);
                regs.setD(new UnsignedWord(tempResult));
                operationTicks = 11;
                setShortDesc("MUL, IMM", null);
                break;

            /* SWI - Software Interrupt */
            case 0x3F:
                softwareInterrupt(SWI);
                operationTicks = 19;
                setShortDesc("SWI", null);
                break;

            /* NEGA - Negate A */
            case 0x40:
                regs.setA(negate(regs.getA()));
                operationTicks = 2;
                setShortDesc("NEGA, IMM", null);
                break;

            /* COMA - Compliment A */
            case 0x43:
                regs.setA(compliment(regs.getA()));
                operationTicks = 2;
                setShortDesc("COMA, IMM", null);
                break;

            /* LSRA - Logical Shift Right A */
            case 0x44:
                regs.setA(logicalShiftRight(regs.getA()));
                operationTicks = 2;
                setShortDesc("LSRA, IMM", null);
                break;

            /* RORA - Rotate Right A */
            case 0x46:
                regs.setA(rotateRight(regs.getA()));
                operationTicks = 2;
                setShortDesc("RORA, IMM", null);
                break;

            /* ASRA - Arithmetic Shift Right A */
            case 0x47:
                regs.setA(arithmeticShiftRight(regs.getA()));
                operationTicks = 2;
                setShortDesc("ASRA, IMM", null);
                break;

            /* ASLA - Arithmetic Shift Left A */
            case 0x48:
                regs.setA(arithmeticShiftLeft(regs.getA()));
                operationTicks = 2;
                setShortDesc("ASLA, IMM", null);
                break;

            /* ROLA - Rotate Left A */
            case 0x49:
                regs.setA(rotateLeft(regs.getA()));
                operationTicks = 2;
                setShortDesc("ROLA, IMM", null);
                break;

            /* DECA - Decrement A */
            case 0x4A:
                regs.setA(decrement(regs.getA()));
                operationTicks = 2;
                setShortDesc("DECA, IMM", null);
                break;

            /* INCA - Increment A */
            case 0x4C:
                regs.setA(increment(regs.getA()));
                operationTicks = 2;
                setShortDesc("INCA, IMM", null);
                break;

            /* TSTA - Test A */
            case 0x4D:
                regs.setA(test(regs.getA()));
                operationTicks = 2;
                setShortDesc("TSTA, IMM", null);
                break;

            /* CLRA - Clear A */
            case 0x4F:
                regs.setA(clear(regs.getA()));
                operationTicks = 2;
                setShortDesc("CLRA, IMM", null);
                break;

            /* NEGB - Negate B */
            case 0x50:
                regs.setB(negate(regs.getB()));
                operationTicks = 2;
                setShortDesc("NEGB, IMM", null);
                break;

            /* COMB - Compliment B */
            case 0x53:
                regs.setB(compliment(regs.getB()));
                operationTicks = 2;
                setShortDesc("COMB, IMM", null);
                break;

            /* LSRB - Logical Shift Right B */
            case 0x54:
                regs.setB(logicalShiftRight(regs.getB()));
                operationTicks = 2;
                setShortDesc("LSRB, IMM", null);
                break;

            /* RORB - Rotate Right B */
            case 0x56:
                regs.setB(rotateRight(regs.getB()));
                operationTicks = 2;
                setShortDesc("RORB, IMM", null);
                break;

            /* ASRB - Arithmetic Shift Right B */
            case 0x57:
                regs.setB(arithmeticShiftRight(regs.getB()));
                operationTicks = 2;
                setShortDesc("ASRB, IMM", null);
                break;

            /* ASLB - Arithmetic Shift Left B */
            case 0x58:
                regs.setB(arithmeticShiftLeft(regs.getB()));
                operationTicks = 2;
                setShortDesc("ASLB, IMM", null);
                break;

            /* ROLB - Rotate Left B */
            case 0x59:
                regs.setB(rotateLeft(regs.getB()));
                operationTicks = 2;
                setShortDesc("ROLB, IMM", null);
                break;

            /* DECB - Decrement B */
            case 0x5A:
                regs.setB(decrement(regs.getB()));
                operationTicks = 2;
                setShortDesc("DECB, IMM", null);
                break;

            /* INCB - Increment B */
            case 0x5C:
                regs.setB(increment(regs.getB()));
                operationTicks = 2;
                setShortDesc("INCB, IMM", null);
                break;

            /* TSTB - Test B */
            case 0x5D:
                regs.setB(test(regs.getB()));
                operationTicks = 2;
                setShortDesc("TSTB, IMM", null);
                break;

            /* CLRB - Clear B */
            case 0x5F:
                regs.setB(clear(regs.getB()));
                operationTicks = 2;
                setShortDesc("CLRB, IMM", null);
                break;

            /* NEG - Negate M - Indexed */
            case 0x60:
                memoryResult = memory.getIndexed(regs);
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::negate, memoryResult);
                setShortDesc("NEGM, IND [%04X]", memoryResult);
                break;

            /* COM - Complement M - Indexed */
            case 0x63:
                memoryResult = memory.getIndexed(regs);
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::compliment, memoryResult);
                setShortDesc("COMM, IND [%04X]", memoryResult);
                break;

            /* LSR - Logical Shift Right - Indexed */
            case 0x64:
                memoryResult = memory.getIndexed(regs);
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::logicalShiftRight, memoryResult);
                setShortDesc("LSRM, IND [%04X]", memoryResult);
                break;

            /* ROR - Rotate Right - Indexed */
            case 0x66:
                memoryResult = memory.getIndexed(regs);
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::rotateRight, memoryResult);
                setShortDesc("RORM, IND [%04X]", memoryResult);
                break;

            /* ASR - Arithmetic Shift Right - Indexed */
            case 0x67:
                memoryResult = memory.getIndexed(regs);
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::arithmeticShiftRight, memoryResult);
                setShortDesc("ASRM, IND [%04X]", memoryResult);
                break;

            /* ASL - Arithmetic Shift Left - Indexed */
            case 0x68:
                memoryResult = memory.getIndexed(regs);
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::arithmeticShiftLeft, memoryResult);
                setShortDesc("ASLM, IND [%04X]", memoryResult);
                break;

            /* ROL - Rotate Left - Indexed */
            case 0x69:
                memoryResult = memory.getIndexed(regs);
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::rotateLeft, memoryResult);
                setShortDesc("ROLM, IND [%04X]", memoryResult);
                break;

            /* DEC - Decrement - Indexed */
            case 0x6A:
                memoryResult = memory.getIndexed(regs);
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::decrement, memoryResult);
                setShortDesc("DECM, IND [%04X]", memoryResult);
                break;

            /* INC - Increment - Indexed */
            case 0x6C:
                memoryResult = memory.getIndexed(regs);
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::increment, memoryResult);
                setShortDesc("INCM, IND [%04X]", memoryResult);
                break;

            /* TST - Test - Indexed */
            case 0x6D:
                memoryResult = memory.getIndexed(regs);
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::test, memoryResult);
                setShortDesc("TSTM, IND [%04X]", memoryResult);
                break;

            /* JMP - Jump - Indexed */
            case 0x6E:
                memoryResult = memory.getIndexed(regs);
                operationTicks = 1 + memoryResult.getBytesConsumed();
                jump(memoryResult.getResult());
                setShortDesc("JMP, IND [%04X]", memoryResult);
                break;

            /* CLR - Clear - Indexed */
            case 0x6F:
                memoryResult = memory.getIndexed(regs);
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::clear, memoryResult);
                setShortDesc("CLRM, IND [%04X]", memoryResult);
                break;

            /* NEG - Negate M - Extended */
            case 0x70:
                memoryResult = memory.getExtended(regs);
                operationTicks = 7;
                executeByteFunctionM(this::negate, memoryResult);
                setShortDesc("NEGM, EXT [%04X]", memoryResult);
                break;

            /* COM - Complement M - Extended */
            case 0x73:
                memoryResult = memory.getExtended(regs);
                operationTicks = 7;
                executeByteFunctionM(this::compliment, memoryResult);
                setShortDesc("COMM, EXT [%04X]", memoryResult);
                break;

            /* LSR - Logical Shift Right - Extended */
            case 0x74:
                memoryResult = memory.getExtended(regs);
                operationTicks = 7;
                executeByteFunctionM(this::logicalShiftRight, memoryResult);
                setShortDesc("LSRM, EXT [%04X]", memoryResult);
                break;

            /* ROR - Rotate Right - Extended */
            case 0x76:
                memoryResult = memory.getExtended(regs);
                operationTicks = 7;
                executeByteFunctionM(this::rotateRight, memoryResult);
                setShortDesc("RORM, EXT [%04X]", memoryResult);
                break;

            /* ASR - Arithmetic Shift Right - Extended */
            case 0x77:
                memoryResult = memory.getExtended(regs);
                operationTicks = 7;
                executeByteFunctionM(this::arithmeticShiftRight, memoryResult);
                setShortDesc("ASRM, EXT [%04X]", memoryResult);
                break;

            /* ASL - Arithmetic Shift Left - Extended */
            case 0x78:
                memoryResult = memory.getExtended(regs);
                operationTicks = 7;
                executeByteFunctionM(this::arithmeticShiftLeft, memoryResult);
                setShortDesc("ASLM, EXT [%04X]", memoryResult);
                break;

            /* ROL - Rotate Left - Extended */
            case 0x79:
                memoryResult = memory.getExtended(regs);
                operationTicks = 7;
                executeByteFunctionM(this::rotateLeft, memoryResult);
                setShortDesc("ROLM, EXT [%04X]", memoryResult);
                break;

            /* DEC - Decrement - Extended */
            case 0x7A:
                memoryResult = memory.getExtended(regs);
                operationTicks = 7;
                executeByteFunctionM(this::decrement, memoryResult);
                setShortDesc("DECM, EXT [%04X]", memoryResult);
                break;

            /* INC - Increment - Extended */
            case 0x7C:
                memoryResult = memory.getExtended(regs);
                operationTicks = 7;
                executeByteFunctionM(this::increment, memoryResult);
                setShortDesc("INCM, EXT [%04X]", memoryResult);
                break;

            /* TST - Test - Extended */
            case 0x7D:
                memoryResult = memory.getExtended(regs);
                operationTicks = 7;
                executeByteFunctionM(this::test, memoryResult);
                setShortDesc("TSTM, EXT [%04X]", memoryResult);
                break;

            /* JMP - Jump - Extended */
            case 0x7E:
                memoryResult = memory.getExtended(regs);
                operationTicks = 4;
                jump(memoryResult.getResult());
                setShortDesc("JMP, EXT [%04X]", memoryResult);
                break;

            /* CLR - Clear - Extended */
            case 0x7F:
                memoryResult = memory.getExtended(regs);
                operationTicks = 7;
                executeByteFunctionM(this::clear, memoryResult);
                setShortDesc("CLRM, EXT [%04X]", memoryResult);
                break;

            /* SUBA - Subtract M from A - Immediate */
            case 0x80:
                memoryResult = memory.getImmediateByte(regs);
                subtractM(Register.A, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("SUBA, IMM", null);
                break;

            /* CMPA - Compare A - Immediate */
            case 0x81:
                memoryResult = memory.getImmediateByte(regs);
                compareByte(regs.getA(), memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("CMPA, IMM", null);
                break;

            /* SBCA - Subtract M and C from A - Immediate */
            case 0x82:
                memoryResult = memory.getImmediateByte(regs);
                subtractMC(Register.A, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("SBCA, IMM", null);
                break;

            /* SUBD - Subtract M from D - Immediate */
            case 0x83:
                memoryResult = memory.getImmediateWord(regs);
                subtractD(memoryResult.getResult());
                operationTicks = 4;
                setShortDesc("SUBD, IMM", null);
                break;

            /* ANDA - Logical AND A - Immediate */
            case 0x84:
                memoryResult = memory.getImmediateByte(regs);
                logicalAnd(Register.A, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("ANDA, IMM", null);
                break;

            /* BITA - Test A - Immediate */
            case 0x85:
                memoryResult = memory.getImmediateByte(regs);
                test(new UnsignedByte(regs.getA().getShort() & memoryResult.getResult().getHigh().getShort()));
                operationTicks = 2;
                setShortDesc("BITA, IMM", null);
                break;

            /* LDA - Load A - Immediate */
            case 0x86:
                memoryResult = memory.getImmediateByte(regs);
                loadByteRegister(Register.A, memoryResult.getResult().getHigh());
                operationTicks = 4;
                setShortDesc("LDA, IMM", null);
                break;

            /* EORA - Exclusive OR A - Immediate */
            case 0x88:
                memoryResult = memory.getImmediateByte(regs);
                exclusiveOr(Register.A, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("EORA, IMM", null);
                break;

            /* ADCA - Add with Carry A - Immediate */
            case 0x89:
                memoryResult = memory.getImmediateByte(regs);
                addWithCarry(Register.A, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("ADCA, IMM", null);
                break;

            /* ORA - Logical OR A - Immediate */
            case 0x8A:
                memoryResult = memory.getImmediateByte(regs);
                logicalOr(Register.A, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("ORA, IMM", null);
                break;

            /* ADDA - Add A - Immediate */
            case 0x8B:
                memoryResult = memory.getImmediateByte(regs);
                addByteRegister(Register.A, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("ADDA, IMM", null);
                break;

            /* CMPX - Compare X - Immediate */
            case 0x8C:
                memoryResult = memory.getImmediateWord(regs);
                compareWord(regs.getX(), memoryResult.getResult());
                operationTicks = 4;
                setShortDesc("CMPX, IMM [%04X]", memoryResult);
                break;

            /* LDX - Load X - Immediate */
            case 0x8E:
                memoryResult = memory.getImmediateWord(regs);
                loadRegister(Register.X, memoryResult.getResult());
                operationTicks = 3;
                setShortDesc("LDX, IMM [%04X]", memoryResult);
                break;

            /* SUBA - Subtract M from A - Direct */
            case 0x90:
                memoryResult = memory.getDirect(regs);
                subtractM(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("SUBA, DIR", null);
                break;

            /* CMPA - Compare A - Direct */
            case 0x91:
                memoryResult = memory.getDirect(regs);
                compareByte(regs.getA(), memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("CMPA, DIR", null);
                break;

            /* SBCA - Subtract M and C from A - Direct */
            case 0x92:
                memoryResult = memory.getDirect(regs);
                subtractMC(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("SBCA, IMM", null);
                break;

            /* SUBD - Subtract M from D - Direct */
            case 0x93:
                memoryResult = memory.getDirect(regs);
                subtractD(memory.readWord(memoryResult.getResult()));
                operationTicks = 6;
                setShortDesc("SUBD, DIR", null);
                break;

            /* ANDA - Logical AND A - Direct */
            case 0x94:
                memoryResult = memory.getDirect(regs);
                logicalAnd(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("ANDA, DIR", null);
                break;

            /* BITA - Test A - Direct */
            case 0x95:
                memoryResult = memory.getDirect(regs);
                test(new UnsignedByte(regs.getA().getShort() & memory.readByte(memoryResult.getResult()).getShort()));
                operationTicks = 4;
                setShortDesc("BITA, DIR", null);
                break;

            /* LDA - Load A - Direct */
            case 0x96:
                memoryResult = memory.getDirect(regs);
                loadByteRegister(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 2;
                setShortDesc("LDA, DIR", null);
                break;

            /* STA - Store A - Direct */
            case 0x97:
                memoryResult = memory.getDirect(regs);
                storeByteRegister(Register.A, memoryResult.getResult());
                operationTicks = 4;
                setShortDesc("STA, DIR", null);
                break;

            /* EORA - Exclusive OR A - Direct */
            case 0x98:
                memoryResult = memory.getDirect(regs);
                exclusiveOr(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("EORA, DIR", null);
                break;

            /* ADCA - Add with Carry A - Direct */
            case 0x99:
                memoryResult = memory.getDirect(regs);
                addWithCarry(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("ADCA, DIR", null);
                break;

            /* ORA - Logical OR A - Direct */
            case 0x9A:
                memoryResult = memory.getDirect(regs);
                logicalOr(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("ORA, DIR", null);
                break;

            /* ADDA - Add A - Direct */
            case 0x9B:
                memoryResult = memory.getDirect(regs);
                addByteRegister(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("ADDA, DIR", null);
                break;

            /* CMPX - Compare X - Direct */
            case 0x9C:
                memoryResult = memory.getDirect(regs);
                compareWord(regs.getX(), memory.readWord(memoryResult.getResult()));
                operationTicks = 6;
                setShortDesc("CMPX, DIR", null);
                break;

            /* JSR - Jump to Subroutine - Direct */
            case 0x9D:
                memoryResult = memory.getDirect(regs);
                jumpToSubroutine(memoryResult.getResult());
                operationTicks = 7;
                setShortDesc("JSR, DIR", null);
                break;

            /* LDX - Load X - Direct */
            case 0x9E:
                memoryResult = memory.getDirect(regs);
                loadRegister(Register.X, memory.readWord(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("LDX, DIR", null);
                break;

            /* STX - Store X - Direct */
            case 0x9F:
                memoryResult = memory.getDirect(regs);
                storeWordRegister(Register.X, memoryResult.getResult());
                operationTicks = 5;
                setShortDesc("STX, EXT", memoryResult);
                break;

            /* SUBA - Subtract M from A - Indexed */
            case 0xA0:
                memoryResult = memory.getIndexed(regs);
                subtractM(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("SUBA, IND", null);
                break;

            /* CMPA - Compare A - Indexed */
            case 0xA1:
                memoryResult = memory.getIndexed(regs);
                compareByte(regs.getA(), memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("CMPA, IND", null);
                break;

            /* SBCA - Subtract M and C from A - Indexed */
            case 0xA2:
                memoryResult = memory.getIndexed(regs);
                subtractMC(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("SBCA, IND", null);
                break;

            /* SUBD - Subtract M from D - Indexed */
            case 0xA3:
                memoryResult = memory.getIndexed(regs);
                subtractD(memory.readWord(memoryResult.getResult()));
                operationTicks = 4 + memoryResult.getBytesConsumed();
                setShortDesc("SUBD, IND", null);
                break;

            /* ANDA - Logical AND A - Indexed */
            case 0xA4:
                memoryResult = memory.getIndexed(regs);
                logicalAnd(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("ANDA, IND", null);
                break;

            /* BITA - Test A - Indexed */
            case 0xA5:
                memoryResult = memory.getIndexed(regs);
                test(new UnsignedByte(regs.getA().getShort() & memory.readByte(memoryResult.getResult()).getShort()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("BITA, IND", null);
                break;

            /* LDA - Load A - Indexed */
            case 0xA6:
                memoryResult = memory.getIndexed(regs);
                loadByteRegister(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("LDA, IND", null);
                break;

            /* STA - Store A - Indexed */
            case 0xA7:
                memoryResult = memory.getIndexed(regs);
                storeByteRegister(Register.A, memoryResult.getResult());
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("LDA, IND", null);
                break;

            /* EORA - Exclusive OR A - Indexed */
            case 0xA8:
                memoryResult = memory.getIndexed(regs);
                exclusiveOr(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("EORA, IND", null);
                break;

            /* ADCA - Add with Carry A - Indexed */
            case 0xA9:
                memoryResult = memory.getIndexed(regs);
                addWithCarry(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("ADCA, IND", null);
                break;

            /* ORA - Logical OR A - Indexed */
            case 0xAA:
                memoryResult = memory.getIndexed(regs);
                logicalOr(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("ORA, IND", null);
                break;

            /* ADDA - Add A - Indexed */
            case 0xAB:
                memoryResult = memory.getIndexed(regs);
                addByteRegister(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("ADDA, IND", null);
                break;

            /* CMPX - Compare X - Indexed */
            case 0xAC:
                memoryResult = memory.getIndexed(regs);
                compareWord(regs.getX(), memory.readWord(memoryResult.getResult()));
                operationTicks = 4 + memoryResult.getBytesConsumed();
                setShortDesc("CMPX, IND", null);
                break;

            /* JSR - Jump to Subroutine - Indexed */
            case 0xAD:
                memoryResult = memory.getIndexed(regs);
                jumpToSubroutine(memoryResult.getResult());
                operationTicks = 5 + memoryResult.getBytesConsumed();
                setShortDesc("JSR, IND", null);
                break;

            /* LDX - Load X - Indexed */
            case 0xAE:
                memoryResult = memory.getIndexed(regs);
                loadRegister(Register.X, memoryResult.getResult());
                operationTicks = 3 + memoryResult.getBytesConsumed();
                setShortDesc("LDX, IND", memoryResult);
                break;

            /* STX - Store X - Indexed */
            case 0xAF:
                memoryResult = memory.getIndexed(regs);
                storeWordRegister(Register.X, memoryResult.getResult());
                operationTicks = 3 + memoryResult.getBytesConsumed();
                setShortDesc("STX, IND", memoryResult);
                break;

            /* SUBA - Subtract M from A - Extended */
            case 0xB0:
                memoryResult = memory.getExtended(regs);
                subtractM(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("SUBA, EXT", null);
                break;

            /* CMPA - Compare A - Extended */
            case 0xB1:
                memoryResult = memory.getExtended(regs);
                compareByte(regs.getA(), memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("CMPA, EXT", null);
                break;

            /* SBCA - Subtract M and C from A - Extended */
            case 0xB2:
                memoryResult = memory.getDirect(regs);
                subtractMC(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("SBCA, EXT", null);
                break;

            /* SUBD - Subtract M from D - Extended */
            case 0xB3:
                memoryResult = memory.getExtended(regs);
                subtractD(memory.readWord(memoryResult.getResult()));
                operationTicks = 7;
                setShortDesc("SUBD, EXT [%04X]", memoryResult);
                break;

            /* ANDA - Logical AND A - Extended */
            case 0xB4:
                memoryResult = memory.getExtended(regs);
                logicalAnd(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("ANDA, EXT", null);
                break;

            /* BITA - Test A - Extended */
            case 0xB5:
                memoryResult = memory.getExtended(regs);
                test(new UnsignedByte(regs.getA().getShort() & memory.readByte(memoryResult.getResult()).getShort()));
                operationTicks = 5;
                setShortDesc("BITA, EXT", null);
                break;

            /* LDA - Load A - Extended */
            case 0xB6:
                memoryResult = memory.getExtended(regs);
                loadByteRegister(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("LDA, EXT", null);
                break;

            /* STA - Store A - Extended */
            case 0xB7:
                memoryResult = memory.getExtended(regs);
                storeByteRegister(Register.A, memoryResult.getResult());
                operationTicks = 5;
                setShortDesc("STA, EXT", null);
                break;

            /* EORA - Exclusive A - Extended */
            case 0xB8:
                memoryResult = memory.getExtended(regs);
                exclusiveOr(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("EORA, EXT", null);
                break;

            /* ADCA - Add with Carry A - Extended */
            case 0xB9:
                memoryResult = memory.getExtended(regs);
                addWithCarry(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("ADCA, EXT", null);
                break;

            /* ORA - Logical OR A - Extended */
            case 0xBA:
                memoryResult = memory.getExtended(regs);
                logicalOr(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("ORA, EXT", null);
                break;

            /* ADDA - Add A - Extended */
            case 0xBB:
                memoryResult = memory.getExtended(regs);
                addByteRegister(Register.A, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("ADDA, EXT", null);
                break;

            /* CMPX - Compare X - Extended */
            case 0xBC:
                memoryResult = memory.getExtended(regs);
                compareWord(regs.getX(), memory.readWord(memoryResult.getResult()));
                operationTicks = 7;
                setShortDesc("CMPX, EXT", null);
                break;

            /* JSR - Jump to Subroutine - Extended */
            case 0xBD:
                memoryResult = memory.getExtended(regs);
                jumpToSubroutine(memoryResult.getResult());
                operationTicks = 8;
                setShortDesc("JSR, EXT", null);
                break;

            /* LDX - Load X - Extended */
            case 0xBE:
                memoryResult = memory.getExtended(regs);
                loadRegister(Register.X, memoryResult.getResult());
                operationTicks = 6;
                setShortDesc("LDX, EXT", memoryResult);
                break;

            /* STX - Store X - Extended */
            case 0xBF:
                memoryResult = memory.getExtended(regs);
                storeWordRegister(Register.X, memoryResult.getResult());
                operationTicks = 6;
                setShortDesc("STX, EXT", memoryResult);
                break;

            /* SUBB - Subtract M from B - Immediate */
            case 0xC0:
                memoryResult = memory.getImmediateByte(regs);
                subtractM(Register.B, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("SUBB, IMM", null);
                break;

            /* CMPB - Compare B - Immediate */
            case 0xC1:
                memoryResult = memory.getImmediateByte(regs);
                compareByte(regs.getB(), memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("CMPB, IMM", null);
                break;

            /* SBCB - Subtract M and C from B - Immediate */
            case 0xC2:
                memoryResult = memory.getImmediateByte(regs);
                subtractMC(Register.B, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("SBCB, IMM", null);
                break;

            /* ADDD - Add D - Immediate */
            case 0xC3:
                memoryResult = memory.getImmediateByte(regs);
                addD(memoryResult.getResult());
                operationTicks = 4;
                setShortDesc("ADDD, IMM", null);
                break;

            /* ANDB - Logical AND B - Immediate */
            case 0xC4:
                memoryResult = memory.getImmediateByte(regs);
                logicalAnd(Register.B, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("ANDB, IMM", null);
                break;

            /* BITB - Test B - Immediate */
            case 0xC5:
                memoryResult = memory.getImmediateByte(regs);
                test(new UnsignedByte(regs.getB().getShort() & memoryResult.getResult().getHigh().getShort()));
                operationTicks = 2;
                setShortDesc("BITB, IMM", null);
                break;

            /* LDB - Load B - Immediate */
            case 0xC6:
                memoryResult = memory.getImmediateByte(regs);
                loadByteRegister(Register.B, memoryResult.getResult().getHigh());
                operationTicks = 4;
                setShortDesc("LDB, IMM", null);
                break;

            /* EORB - Exclusive OR B - Immediate */
            case 0xC8:
                memoryResult = memory.getImmediateByte(regs);
                exclusiveOr(Register.B, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("EORB, IMM", null);
                break;

            /* ADCB - Add with Carry B - Immediate */
            case 0xC9:
                memoryResult = memory.getImmediateByte(regs);
                addWithCarry(Register.B, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("ADCB, IMM", null);
                break;

            /* ORB - Logical OR B - Immediate */
            case 0xCA:
                memoryResult = memory.getImmediateByte(regs);
                logicalOr(Register.B, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("ORB, IMM", null);
                break;

            /* ADDB - Add B - Immediate */
            case 0xCB:
                memoryResult = memory.getImmediateByte(regs);
                addByteRegister(Register.B, memoryResult.getResult().getHigh());
                operationTicks = 2;
                setShortDesc("ADDB, IMM", null);
                break;

            /* LDD - Load D - Immediate */
            case 0xCC:
                memoryResult = memory.getImmediateWord(regs);
                loadRegister(Register.D, memoryResult.getResult());
                operationTicks = 3;
                setShortDesc("LDD, IMM [%04X]", memoryResult);
                break;

            /* LDU - Load U - Immediate */
            case 0xCE:
                memoryResult = memory.getImmediateWord(regs);
                loadRegister(Register.U, memoryResult.getResult());
                operationTicks = 3;
                setShortDesc("LDU, IMM [%04X]", memoryResult);
                break;

            /* SUBB - Subtract M from B - Direct */
            case 0xD0:
                memoryResult = memory.getDirect(regs);
                subtractM(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("SUBB, DIR", null);
                break;

            /* CMPB - Compare B - Direct */
            case 0xD1:
                memoryResult = memory.getDirect(regs);
                compareByte(regs.getB(), memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("CMPB, DIR", null);
                break;

            /* SBCB - Subtract M and C from B - Direct */
            case 0xD2:
                memoryResult = memory.getDirect(regs);
                subtractMC(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("SBCB, IMM", null);
                break;

            /* ADDD - Add D - Direct */
            case 0xD3:
                memoryResult = memory.getDirect(regs);
                addD(memory.readWord(memoryResult.getResult()));
                operationTicks = 6;
                setShortDesc("ADDD, DIR", null);
                break;

            /* ANDB - Logical AND B - Direct */
            case 0xD4:
                memoryResult = memory.getDirect(regs);
                logicalAnd(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("ANDB, DIR", null);
                break;

            /* BITB - Test B - Direct */
            case 0xD5:
                memoryResult = memory.getDirect(regs);
                test(new UnsignedByte(regs.getB().getShort() & memory.readByte(memoryResult.getResult()).getShort()));
                operationTicks = 4;
                setShortDesc("BITB, DIR", null);
                break;

            /* LDB - Load B - Direct */
            case 0xD6:
                memoryResult = memory.getDirect(regs);
                loadByteRegister(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 2;
                setShortDesc("LDB, DIR", null);
                break;

            /* STB - Store B - Direct */
            case 0xD7:
                memoryResult = memory.getDirect(regs);
                storeByteRegister(Register.B, memoryResult.getResult());
                operationTicks = 2;
                setShortDesc("STB, DIR", null);
                break;

            /* EORB - Exclusive OR B - Direct */
            case 0xD8:
                memoryResult = memory.getDirect(regs);
                exclusiveOr(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("EORB, DIR", null);
                break;

            /* ADCB - Add with Carry B - Direct */
            case 0xD9:
                memoryResult = memory.getDirect(regs);
                addWithCarry(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("ADCB, DIR", null);
                break;

            /* ORB - Logical OR B - Direct */
            case 0xDA:
                memoryResult = memory.getDirect(regs);
                logicalOr(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("ORB, DIR", null);
                break;

            /* ADDB - Add B - Direct */
            case 0xDB:
                memoryResult = memory.getDirect(regs);
                addByteRegister(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("ADDB, DIR", null);
                break;

            /* LDD - Load - Direct */
            case 0xDC:
                memoryResult = memory.getDirect(regs);
                loadRegister(Register.D, memory.readWord(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("LDD, DIR", null);
                break;

            /* STD - Store D - Direct */
            case 0xDD:
                memoryResult = memory.getDirect(regs);
                storeWordRegister(Register.D, memoryResult.getResult());
                operationTicks = 5;
                setShortDesc("STD, DIR", memoryResult);
                break;

            /* LDU - Load U - Direct */
            case 0xDE:
                memoryResult = memory.getDirect(regs);
                loadRegister(Register.U, memory.readWord(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("LDU, DIR", null);
                break;

            /* STU - Store U - Direct */
            case 0xDF:
                memoryResult = memory.getDirect(regs);
                storeWordRegister(Register.U, memoryResult.getResult());
                operationTicks = 5;
                setShortDesc("STU, DIR", memoryResult);
                break;

            /* SUBB - Subtract M from B - Indexed */
            case 0xE0:
                memoryResult = memory.getIndexed(regs);
                subtractM(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("SUBB, IND", null);
                break;

            /* CMPB - Compare B - Indexed */
            case 0xE1:
                memoryResult = memory.getIndexed(regs);
                compareByte(regs.getB(), memory.readByte(memoryResult.getResult()));
                operationTicks = 4;
                setShortDesc("CMPB, IND", null);
                break;

            /* SBCB - Subtract M and C from B - Indexed */
            case 0xE2:
                memoryResult = memory.getIndexed(regs);
                subtractMC(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("SBCB, IND", null);
                break;

            /* ADDD - Add D - Indexed */
            case 0xE3:
                memoryResult = memory.getIndexed(regs);
                addD(memory.readWord(memoryResult.getResult()));
                operationTicks = 6 + memoryResult.getBytesConsumed();
                setShortDesc("ADDD, IND", null);
                break;

            /* ANDB - Logical AND B - Indexed */
            case 0xE4:
                memoryResult = memory.getIndexed(regs);
                logicalAnd(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("ANDB, IND", null);
                break;

            /* BITB - Test B - Indexed */
            case 0xE5:
                memoryResult = memory.getIndexed(regs);
                test(new UnsignedByte(regs.getB().getShort() & memory.readByte(memoryResult.getResult()).getShort()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("BITB, IND", null);
                break;

            /* LDB - Load B - Indexed */
            case 0xE6:
                memoryResult = memory.getIndexed(regs);
                loadByteRegister(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("LDB, IND", null);
                break;

            /* STB - Store B - Indexed */
            case 0xE7:
                memoryResult = memory.getIndexed(regs);
                storeByteRegister(Register.B, memoryResult.getResult());
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("STB, IND", null);
                break;

            /* EORB - Exclusive OR B - Indexed */
            case 0xE8:
                memoryResult = memory.getIndexed(regs);
                exclusiveOr(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("EORB, IND", null);
                break;

            /* ADCB - Add with Carry B - Indexed */
            case 0xE9:
                memoryResult = memory.getIndexed(regs);
                addWithCarry(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("ADCB, IND", null);
                break;

            /* ORB - Logical OR B - Indexed */
            case 0xEA:
                memoryResult = memory.getIndexed(regs);
                logicalOr(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("ORB, IND", null);
                break;

            /* ADDB - Add B - Indexed */
            case 0xEB:
                memoryResult = memory.getIndexed(regs);
                addByteRegister(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("ADDB, IND", null);
                break;

            /* LDD - Load D - Indexed */
            case 0xEC:
                memoryResult = memory.getIndexed(regs);
                loadRegister(Register.D, memory.readWord(memoryResult.getResult()));
                operationTicks = 3 + memoryResult.getBytesConsumed();
                setShortDesc("LDD, IND", null);
                break;

            /* STD - Store D - Indexed */
            case 0xED:
                memoryResult = memory.getIndexed(regs);
                storeWordRegister(Register.D, memoryResult.getResult());
                operationTicks = 3 + memoryResult.getBytesConsumed();
                setShortDesc("STD, IND", memoryResult);
                break;

            /* LDU - Load U - Indexed */
            case 0xEE:
                memoryResult = memory.getIndexed(regs);
                loadRegister(Register.U, memory.readWord(memoryResult.getResult()));
                operationTicks = 3 + memoryResult.getBytesConsumed();
                setShortDesc("LDU, IND", null);
                break;

            /* STU - Store U - Indexed */
            case 0xEF:
                memoryResult = memory.getIndexed(regs);
                storeWordRegister(Register.U, memoryResult.getResult());
                operationTicks = 3 + memoryResult.getBytesConsumed();
                setShortDesc("STU, IND", memoryResult);
                break;

            /* SUBB - Subtract M from B - Extended */
            case 0xF0:
                memoryResult = memory.getExtended(regs);
                subtractM(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                setShortDesc("SUBB, EXT", null);
                break;

            /* CMPB - Compare B - Extended */
            case 0xF1:
                memoryResult = memory.getExtended(regs);
                compareByte(regs.getB(), memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("CMPB, EXT", null);
                break;

            /* SBCB - Subtract M and C from B - Extended */
            case 0xF2:
                memoryResult = memory.getExtended(regs);
                subtractMC(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("SBCB, EXT", null);
                break;

            /* ADDD - Add D - Extended */
            case 0xF3:
                memoryResult = memory.getExtended(regs);
                addD(memory.readWord(memoryResult.getResult()));
                operationTicks = 7;
                setShortDesc("ADDD, EXT", null);
                break;

            /* ANDB - Logical AND B - Extended */
            case 0xF4:
                memoryResult = memory.getExtended(regs);
                logicalAnd(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("ANDB, EXT", null);
                break;

            /* BITB - Test B - Extended */
            case 0xF5:
                memoryResult = memory.getExtended(regs);
                test(new UnsignedByte(regs.getB().getShort() & memory.readByte(memoryResult.getResult()).getShort()));
                operationTicks = 5;
                setShortDesc("BITB, EXT", null);
                break;

            /* LDB - Load B - Extended */
            case 0xF6:
                memoryResult = memory.getExtended(regs);
                loadByteRegister(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("LDB, EXT", null);
                break;

            /* STB - Store B - Extended */
            case 0xF7:
                memoryResult = memory.getExtended(regs);
                storeByteRegister(Register.B, memoryResult.getResult());
                operationTicks = 5;
                setShortDesc("STB, EXT", null);
                break;

            /* EORB - Exclusive OR B - Extended */
            case 0xF8:
                memoryResult = memory.getExtended(regs);
                exclusiveOr(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("EORB, EXT", null);
                break;

            /* ADCB - Add with Carry B - Extended */
            case 0xF9:
                memoryResult = memory.getExtended(regs);
                addWithCarry(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("ADCB, EXT", null);
                break;

            /* ORB - Logical OR B - Extended */
            case 0xFA:
                memoryResult = memory.getExtended(regs);
                logicalOr(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("ORB, EXT", null);
                break;

            /* ADDB - Add B - Extended */
            case 0xFB:
                memoryResult = memory.getExtended(regs);
                addByteRegister(Register.B, memory.readByte(memoryResult.getResult()));
                operationTicks = 5;
                setShortDesc("ADDB, EXT", null);
                break;

            /* LDD - Load D - Extended */
            case 0xFC:
                memoryResult = memory.getExtended(regs);
                loadRegister(Register.D, memory.readWord(memoryResult.getResult()));
                operationTicks = 6;
                setShortDesc("LDD, EXT", null);
                break;

            /* STD - Store D - Extended */
            case 0xFD:
                memoryResult = memory.getExtended(regs);
                storeWordRegister(Register.D, memoryResult.getResult());
                operationTicks = 6;
                setShortDesc("STD, EXT", memoryResult);
                break;

            /* LDU - Load U - Extended */
            case 0xFE:
                memoryResult = memory.getExtended(regs);
                loadRegister(Register.U, memory.readWord(memoryResult.getResult()));
                operationTicks = 6;
                setShortDesc("LDU, EXT", null);
                break;

            /* STD - Store U - Extended */
            case 0xFF:
                memoryResult = memory.getExtended(regs);
                storeWordRegister(Register.U, memoryResult.getResult());
                operationTicks = 6;
                setShortDesc("STU, EXT", memoryResult);
                break;
        }

        return operationTicks;
    }

    /**
     * Executes a byte function on the memory location M, and writes the
     * resultant byte back to the memory location.
     *
     * @param function the function to execute
     * @param memoryResult the MemoryResult where the address is located
     */
    public void executeByteFunctionM(Function<UnsignedByte, UnsignedByte> function,
                                     MemoryResult memoryResult) {
        UnsignedWord address = memoryResult.getResult();
        UnsignedByte tempByte = memory.readByte(address);
        tempByte = function.apply(tempByte);
        memory.writeByte(address, tempByte);
    }

    /**
     * Inverts all bits in the byte. Returns the complimented value as the
     * result.
     *
     * @param value the UnsignedByte to complement
     * @return the complimented value
     */
    public UnsignedByte compliment(UnsignedByte value) {
        UnsignedByte result = new UnsignedByte(~(value.getShort()));
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_Z | RegisterSet.CC_V));
        regs.cc.or(RegisterSet.CC_C);
        regs.cc.or(result.isNegative() ? RegisterSet.CC_N : 0);
        regs.cc.or(result.isZero() ? RegisterSet.CC_Z : 0);
        return result;
    }

    /**
     * Applies the two's compliment value to the contents in the specified
     * memory address.
     *
     * @param value the byte to negate
     * @return the negated byte
     */
    public UnsignedByte negate(UnsignedByte value) {
        UnsignedByte result = value.twosCompliment();
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_Z | RegisterSet.CC_V | RegisterSet.CC_C));
        regs.cc.or(result.isMasked(0x80) ? RegisterSet.CC_V : 0);
        regs.cc.or(result.isZero() ? RegisterSet.CC_Z | RegisterSet.CC_N : 0);
        regs.cc.or(result.isNegative() ? RegisterSet.CC_N : 0);
        return result;
    }

    /**
     * Shifts all the bits in the byte to the left by one bit. Returns the
     * result of the operation, while impacting the condition code register.
     * The lowest bit of the byte is shifted into the condition code carry
     * bit.
     *
     * @param value the UnsignedByte to operate on
     * @return the shifted byte value
     */
    public UnsignedByte logicalShiftRight(UnsignedByte value) {
        UnsignedByte result = new UnsignedByte(value.getShort() >> 1);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_Z | RegisterSet.CC_C));
        regs.cc.or(value.isMasked(0x1) ? RegisterSet.CC_C : 0);
        regs.cc.or(result.isZero() ? RegisterSet.CC_Z : 0);
        return result;
    }

    /**
     * Rotates the bits of a byte one place to the right. Will rotate the
     * carry bit into the highest bit of the byte if set.
     *
     * @param value the value to rotate right
     * @return the rotated value
     */
    public UnsignedByte rotateRight(UnsignedByte value) {
        UnsignedByte result = new UnsignedByte(value.getShort() >> 1);
        result.add(regs.ccCarrySet() ? 0x80 : 0x0);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_Z | RegisterSet.CC_C));
        regs.cc.or(value.isMasked(0x1) ? RegisterSet.CC_C : 0);
        regs.cc.or(result.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(result.isNegative() ? RegisterSet.CC_N : 0);
        return result;
    }

    /**
     * Shifts the bits of a byte one place to the right. Will maintain a copy
     * of bit 7 in the 7th bit. Bit 0 will be shifted into the carry bit.
     *
     * @param value the value to shift right
     * @return the shifted value
     */
    public UnsignedByte arithmeticShiftRight(UnsignedByte value) {
        UnsignedByte result = new UnsignedByte(value.getShort() >> 1);
        result.add(value.isMasked(0x80) ? 0x80 : 0);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_Z | RegisterSet.CC_C));
        regs.cc.or(value.isMasked(0x1) ? RegisterSet.CC_C : 0);
        regs.cc.or(result.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(result.isNegative() ? RegisterSet.CC_N : 0);
        return result;
    }

    /**
     * Shifts the bits of a byte one place to the left. Bit 0 will be filled
     * with a zero, while bit 7 will be shifted into the carry bit.
     *
     * @param value the value to shift left
     * @return the shifted value
     */
    public UnsignedByte arithmeticShiftLeft(UnsignedByte value) {
        UnsignedByte result = new UnsignedByte(value.getShort() << 1);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_Z | RegisterSet.CC_V | RegisterSet.CC_C));
        regs.cc.or(value.isMasked(0x80) ? RegisterSet.CC_C : 0);
        regs.cc.or(value.isMasked(0xC0) ? RegisterSet.CC_V : 0);
        regs.cc.or(result.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(result.isNegative() ? RegisterSet.CC_N : 0);
        return result;
    }

    /**
     * Rotates the bits of a byte one place to the left. Will rotate the
     * carry bit into the lowest bit of the byte if set.
     *
     * @param value the value to rotate left
     * @return the rotated value
     */
    public UnsignedByte rotateLeft(UnsignedByte value) {
        UnsignedByte result = new UnsignedByte(value.getShort() << 1);
        result.add(regs.ccCarrySet() ? 0x1 : 0x0);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_Z | RegisterSet.CC_C | RegisterSet.CC_V));
        regs.cc.or(value.isMasked(0x80) ? RegisterSet.CC_C : 0);
        regs.cc.or(value.isMasked(0xC0) ? RegisterSet.CC_V : 0);
        regs.cc.or(result.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(result.isNegative() ? RegisterSet.CC_N : 0);
        return result;
    }

    /**
     * Decrements the byte value by one.
     *
     * @param value the byte value to decrement
     * @return the decremented byte value
     */
    public UnsignedByte decrement(UnsignedByte value) {
        UnsignedByte result = regs.binaryAdd(value, new UnsignedByte(0xFF), false, false, false);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_Z | RegisterSet.CC_V));
        regs.cc.or(value.isZero() ? RegisterSet.CC_V : 0);
        regs.cc.or(result.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(result.isNegative() ? RegisterSet.CC_N : 0);
        return result;
    }

    /**
     * Increments the byte value by one.
     *
     * @param value the byte value to increment
     * @return the incremented byte value
     */
    public UnsignedByte increment(UnsignedByte value) {
        UnsignedByte result = regs.binaryAdd(value, new UnsignedByte(0x1), false, false, false);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_Z | RegisterSet.CC_V));
        regs.cc.or(value.isMasked(0x7F) ? RegisterSet.CC_V : 0);
        regs.cc.or(result.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(result.isNegative() ? RegisterSet.CC_N : 0);
        return result;
    }

    /**
     * Tests the byte for zero condition or negative condition.
     *
     * @param value the byte value to test
     * @return the original byte value
     */
    public UnsignedByte test(UnsignedByte value) {
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_Z | RegisterSet.CC_V));
        regs.cc.or(value.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(value.isNegative() ? RegisterSet.CC_N : 0);
        return value;
    }

    /**
     * Jumps to the specified address.
     *
     * @param address the address to jump to
     */
    public void jump(UnsignedWord address) {
        regs.setPC(address);
    }

    /**
     * Jumps to the specified address, pushing the value of the PC onto the S
     * stack before jumping.
     *
     * @param address the address to jump to
     */
    public void jumpToSubroutine(UnsignedWord address) {
        memory.pushStack(regs, Register.S, regs.getPC().getLow());
        memory.pushStack(regs, Register.S, regs.getPC().getHigh());
        regs.setPC(address);
    }

    /**
     * Clears the specified byte.
     *
     * @param value the value to clear
     * @return the cleared byte
     */
    public UnsignedByte clear(UnsignedByte value) {
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_C | RegisterSet.CC_V));
        regs.cc.or(RegisterSet.CC_Z);
        return new UnsignedByte(0);
    }

    /**
     * Increments (or decrements) the program counter by the specified amount.
     * Will interpret the UnsignedWord offset as a negative value if the setHigh
     * bit is set.
     *
     * @param offset the amount to offset the program counter
     */
    public void branchLong(UnsignedWord offset) {
        regs.getPC().add(offset.isNegative() ? offset.getSignedInt() : offset.getInt());
    }

    /**
     * Increments (or decrements) the program counter by the specified amount.
     * Will interpret the UnsignedByte offset as a negative value if its high
     * bit is set.
     *
     * @param offset the amount to offset the program counter
     */
    public void branchShort(UnsignedByte offset) {
        regs.getPC().add(offset.isNegative() ? offset.getSignedShort() : offset.getShort());
    }

    /**
     * Saves all registers to the stack, and jumps to the memory location
     * read at the specified address.
     *
     * @param offset the offset to read for a jump address
     */
    public void softwareInterrupt(UnsignedWord offset) {
        regs.setCCEverything();
        memory.pushStack(regs, Register.S, regs.getPC().getLow());
        memory.pushStack(regs, Register.S, regs.getPC().getHigh());
        memory.pushStack(regs, Register.S, regs.getU().getLow());
        memory.pushStack(regs, Register.S, regs.getU().getHigh());
        memory.pushStack(regs, Register.S, regs.getY().getLow());
        memory.pushStack(regs, Register.S, regs.getY().getHigh());
        memory.pushStack(regs, Register.S, regs.getX().getLow());
        memory.pushStack(regs, Register.S, regs.getX().getHigh());
        memory.pushStack(regs, Register.S, regs.getDP());
        memory.pushStack(regs, Register.S, regs.getB());
        memory.pushStack(regs, Register.S, regs.getA());
        memory.pushStack(regs, Register.S, regs.getCC());
        regs.setPC(memory.readWord(offset));
    }

    /**
     * Compares the two words and sets the appropriate register sets.
     *
     * @param word1 the first word to compare
     * @param word2 the second word to compare
     */
    public UnsignedWord compareWord(UnsignedWord word1, UnsignedWord word2) {
        UnsignedWord result = regs.binaryAdd(word1, word2.twosCompliment(), false, true, true);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_Z | RegisterSet.CC_V | RegisterSet.CC_C));
        regs.cc.or(result.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(result.isNegative() ? RegisterSet.CC_N : 0);
        return result;
    }

    /**
     * Compares the two bytes and sets the appropriate register sets.
     *
     * @param byte1 the first byte to compare
     * @param byte2 the second byte to compare
     */
    public void compareByte(UnsignedByte byte1, UnsignedByte byte2) {
        UnsignedByte result = regs.binaryAdd(byte1, byte2.twosCompliment(), false, true, true);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_Z | RegisterSet.CC_V | RegisterSet.CC_C));
        regs.cc.or(result.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(result.isNegative() ? RegisterSet.CC_N : 0);
    }

    /**
     * Loads the word into the specified register.
     *
     * @param registerFlag the register to load
     * @param value the value to load
     */
    public void loadRegister(Register registerFlag, UnsignedWord value) {
        UnsignedWord register = regs.getWordRegister(registerFlag);
        register.set(value);
        regs.cc.and(~(RegisterSet.CC_V | RegisterSet.CC_N | RegisterSet.CC_Z));
        regs.cc.or(register.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(register.isNegative() ? RegisterSet.CC_N : 0);
    }

    /**
     * Stores the register in the memory location.
     *
     * @param registerFlag the register to store
     * @param address the memory location to write to
     */
    public void storeWordRegister(Register registerFlag, UnsignedWord address) {
        UnsignedWord register = regs.getWordRegister(registerFlag);
        memory.writeWord(address, register);
        regs.cc.and(~(RegisterSet.CC_V | RegisterSet.CC_N | RegisterSet.CC_Z));
        regs.cc.or(register.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(register.isNegative() ? RegisterSet.CC_N : 0);
    }

    /**
     * Stores the byte register in the memory location.
     *
     * @param registerFlag the byte register to store
     * @param address the memory location to write to
     */
    public void storeByteRegister(Register registerFlag, UnsignedWord address) {
        UnsignedByte register = regs.getByteRegister(registerFlag);
        memory.writeByte(address, register);
        regs.cc.and(~(RegisterSet.CC_V | RegisterSet.CC_N | RegisterSet.CC_Z));
        regs.cc.or(register.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(register.isNegative() ? RegisterSet.CC_N : 0);
    }

    /**
     * Performs a correction to the A register to transform the value into
     * a proper BCD form.
     */
    public void decimalAdditionAdjust() {
        int value = regs.getA().getShort();
        int byte1 = (value & 0xF0) >> 4;
        int byte2 = value & 0x0F;
        int byte3 = 0;

        if (regs.ccCarrySet() || byte1 > 9 || (byte1 > 8 && byte2 > 9)) {
            byte3 += 6;
        }

        byte3 = byte3 << 4;

        if (regs.ccCarrySet() || byte2 > 9) {
            byte3 += 6;
        }

        byte1 = regs.getCC().getShort() & RegisterSet.CC_C;
        UnsignedByte result = new UnsignedByte(byte1);

        regs.setA(regs.binaryAdd(regs.getA(), result, false, true, false));
        regs.cc.and(~(RegisterSet.CC_C | RegisterSet.CC_N | RegisterSet.CC_Z));
        regs.cc.or(regs.getA().isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(!result.isZero() ? RegisterSet.CC_C : 0);
        regs.cc.or(result.isNegative() ? RegisterSet.CC_N : 0);
    }

    /**
     * Loads the specified value into the specified register.
     *
     * @param register the register to load into
     * @param value the value to load
     */
    public void loadEffectiveAddress(Register register, UnsignedWord value) {
        UnsignedWord reg = regs.getWordRegister(register);
        reg.set(value);
        regs.cc.and(~(RegisterSet.CC_Z));
        regs.cc.or(reg.isZero() ? RegisterSet.CC_Z : 0);
    }

    /**
     * Pushes the values of one or more registers onto the specified stack
     * according to the post byte that is passed. Will return the number
     * of bytes that were pushed onto the stack.
     *
     * @param register the register to use as a stack pointer
     * @param postByte the postbyte containing the registers to push
     * @return the number of bytes pushed
     */
    public int pushStack(Register register, UnsignedByte postByte) {
        int bytes = 0;
        if (postByte.isMasked(0x80)) {
            memory.pushStack(regs, register, regs.getPC().getLow());
            memory.pushStack(regs, register, regs.getPC().getHigh());
            bytes += 2;
        }

        if (postByte.isMasked(0x40)) {
            memory.pushStack(regs, register, regs.getU().getLow());
            memory.pushStack(regs, register, regs.getU().getHigh());
            bytes += 2;
        }

        if (postByte.isMasked(0x20)) {
            memory.pushStack(regs, register, regs.getY().getLow());
            memory.pushStack(regs, register, regs.getY().getHigh());
            bytes += 2;
        }

        if (postByte.isMasked(0x10)) {
            memory.pushStack(regs, register, regs.getX().getLow());
            memory.pushStack(regs, register, regs.getX().getHigh());
            bytes += 2;
        }

        if (postByte.isMasked(0x08)) {
            memory.pushStack(regs, register, regs.getDP());
            bytes += 1;
        }

        if (postByte.isMasked(0x04)) {
            memory.pushStack(regs, register, regs.getB());
            bytes += 1;
        }

        if (postByte.isMasked(0x02)) {
            memory.pushStack(regs, register, regs.getA());
            bytes += 1;
        }

        if (postByte.isMasked(0x01)) {
            memory.pushStack(regs, register, regs.getCC());
            bytes += 1;
        }

        return bytes;
    }

    /**
     * Pops bytes from a stack back into registers based on a postbyte
     * value. Will return the number of bytes popped from the stack.
     *
     * @param register the register to use as a stack pointer
     * @param postByte the post byte encoding what registers to use
     * @return the number of bytes popped
     */
    public int popStack(Register register, UnsignedByte postByte) {
        int bytes = 0;

        if (postByte.isMasked(0x01)) {
            regs.setCC(memory.popStack(regs, register));
            bytes += 1;
        }

        if (postByte.isMasked(0x02)) {
            regs.setA(memory.popStack(regs, register));
            bytes += 1;
        }

        if (postByte.isMasked(0x04)) {
            regs.setB(memory.popStack(regs, register));
            bytes += 1;
        }

        if (postByte.isMasked(0x08)) {
            regs.setDP(memory.popStack(regs, register));
            bytes += 1;
        }

        if (postByte.isMasked(0x10)) {
            regs.setX(
                    new UnsignedWord(
                            memory.popStack(regs, register),
                            memory.popStack(regs, register)
                    )
            );
            bytes += 2;
        }

        if (postByte.isMasked(0x20)) {
            regs.setY(
                    new UnsignedWord(
                            memory.popStack(regs, register),
                            memory.popStack(regs, register)
                    )
            );
            bytes += 2;
        }

        if (postByte.isMasked(0x40)) {
            regs.setU(
                    new UnsignedWord(
                            memory.popStack(regs, register),
                            memory.popStack(regs, register)
                    )
            );
            bytes += 2;
        }

        if (postByte.isMasked(0x80)) {
            regs.setPC(
                    new UnsignedWord(
                            memory.popStack(regs, register),
                            memory.popStack(regs, register)
                    )
            );
            bytes += 2;
        }

        return bytes;
    }

    /**
     * Subtracts the byte value from the specified register.
     *
     * @param register the register to subtract from
     * @param value the byte value to subtract
     */
    public void subtractM(Register register, UnsignedByte value) {
        UnsignedByte reg = regs.getByteRegister(register);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_V | RegisterSet.CC_Z | RegisterSet.CC_C));
        reg.set(regs.binaryAdd(reg, value.twosCompliment(), false, true, true));
        regs.cc.or(reg.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(reg.isNegative() ? RegisterSet.CC_N : 0);
    }

    /**
     * Subtracts the word value from the D register.
     *
     * @param value the word value to subtract
     */
    public void subtractD(UnsignedWord value) {
        UnsignedWord reg = regs.getD();
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_V | RegisterSet.CC_Z | RegisterSet.CC_C));
        regs.setD(regs.binaryAdd(reg, value.twosCompliment(), false, true, true));
        regs.cc.or(regs.getD().isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(regs.getD().isNegative() ? RegisterSet.CC_N : 0);
    }

    /**
     * Subtracts the byte value and sets carry if required.
     *
     * @param register the register to subtract from
     * @param value the byte value to subtract
     */
    public void subtractMC(Register register, UnsignedByte value) {
        UnsignedByte reg = regs.getByteRegister(register);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_V | RegisterSet.CC_Z | RegisterSet.CC_C));
        reg.set(regs.binaryAdd(reg, value.twosCompliment(), true, true, true));
        regs.cc.or(reg.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(reg.isNegative() ? RegisterSet.CC_N : 0);
        if (regs.cc.isMasked(RegisterSet.CC_C)) {
            regs.cc.and(~(RegisterSet.CC_C));
        } else {
            regs.cc.or(RegisterSet.CC_C);
        }
    }

    /**
     * Performs a logical AND of the byte register and the value.
     *
     * @param register the register to AND
     * @param value the byte value to AND
     */
    public void logicalAnd(Register register, UnsignedByte value) {
        UnsignedByte reg = regs.getByteRegister(register);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_V | RegisterSet.CC_Z));
        reg.and(value.getShort());
        regs.cc.or(reg.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(reg.isNegative() ? RegisterSet.CC_N : 0);
    }

    /**
     * Performs a logical OR of the byte register and the value.
     *
     * @param register the register to OR
     * @param value the value to OR
     */
    public void logicalOr(Register register, UnsignedByte value) {
        UnsignedByte reg = regs.getByteRegister(register);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_V | RegisterSet.CC_Z));
        reg.or(value.getShort());
        regs.cc.or(reg.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(reg.isNegative() ? RegisterSet.CC_N : 0);
    }

    /**
     * Performs an exclusive OR of the register and the byte value.
     *
     * @param register the register to XOR
     * @param value the byte value to XOR
     */
    public void exclusiveOr(Register register, UnsignedByte value) {
        UnsignedByte reg = regs.getByteRegister(register);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_V | RegisterSet.CC_Z));
        reg.set(new UnsignedByte(reg.getShort() ^ value.getShort()));
        regs.cc.or(reg.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(reg.isNegative() ? RegisterSet.CC_N : 0);
    }

    /**
     * Loads the specified register with the value.
     *
     * @param register the register to load
     * @param value the value to load
     */
    public void loadByteRegister(Register register, UnsignedByte value) {
        UnsignedByte reg = regs.getByteRegister(register);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_V | RegisterSet.CC_Z));
        reg.set(value);
        regs.cc.or(reg.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(reg.isNegative() ? RegisterSet.CC_N : 0);
    }

    /**
     * Performs an addition of the specified register and the value together,
     * plus the value of the carry bit (0 or 1). Stores the result in the
     * specified register.
     *
     * @param register the register to perform the addition with
     * @param value the value to add
     */
    public void addWithCarry(Register register, UnsignedByte value) {
        UnsignedByte reg = regs.getByteRegister(register);
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_V | RegisterSet.CC_Z | RegisterSet.CC_C | RegisterSet.CC_H));
        UnsignedByte tempByte = new UnsignedByte(regs.ccCarrySet() ? 1 : 0);
        reg.set(regs.binaryAdd(reg, value, true, true, true));
        reg.set(regs.binaryAdd(reg, tempByte, true, true, true));
        regs.cc.or(reg.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(reg.isNegative() ? RegisterSet.CC_N : 0);
    }

    /**
     * Adds the specified value to the specified register.
     *
     * @param register the register to add
     * @param value the value to add
     */
    public void addByteRegister(Register register, UnsignedByte value) {
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_V | RegisterSet.CC_Z | RegisterSet.CC_C | RegisterSet.CC_H));
        UnsignedByte reg = regs.getByteRegister(register);
        reg.set(regs.binaryAdd(reg, value, true, true, true));
        regs.cc.or(reg.isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(reg.isNegative() ? RegisterSet.CC_N : 0);
    }

    /**
     * Adds the specified value to the D register.
     *
     * @param value the value to add
     */
    public void addD(UnsignedWord value) {
        regs.cc.and(~(RegisterSet.CC_N | RegisterSet.CC_V | RegisterSet.CC_Z | RegisterSet.CC_C));
        regs.setD(regs.binaryAdd(regs.getD(), value, false, true, true));
        regs.cc.or(regs.getD().isZero() ? RegisterSet.CC_Z : 0);
        regs.cc.or(regs.getD().isNegative() ? RegisterSet.CC_N : 0);
    }
}
