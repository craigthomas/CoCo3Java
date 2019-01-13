/*
 * Copyright (C) 2017 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Implements an MC6809E microprocessor.
 */
public class CPU
{
    /* CPU Internal Variables */
    private String opShortDesc;
    private String opLongDesc;
    private UnsignedWord lastPC;
    private UnsignedByte lastOperand;
    private String [] lastOpCodeInfo;
    private IOController io;
    private MemoryResult memoryResult;

    /* Software Interrupt Vectors */
    public final static UnsignedWord SWI3 = new UnsignedWord(0xFFF2);
    public final static UnsignedWord SWI2 = new UnsignedWord(0xFFF4);
    public final static UnsignedWord SWI = new UnsignedWord(0xFFFA);

    /* Interrupt request flags */
    private boolean fireIRQ;
    private boolean fireFIRQ;
    private boolean fireNMI;

    public CPU(IOController ioController) {
        io = ioController;
    }

    /**
     * Executes the instruction as indicated by the operand. Will return the
     * total number of ticks taken to execute the instruction.
     *
     * @return the number of ticks taken up by the instruction
     */
    public int executeInstruction() throws IllegalIndexedPostbyteException {
        int operationTicks = 0;
        UnsignedWord opWord = io.readWord(io.getWordRegister(Register.PC));
        UnsignedByte operand = opWord.getHigh();
        lastPC = io.getWordRegister(Register.PC).copy();
        lastOperand = operand.copy();
        io.incrementPC();
        int bytes;
        UnsignedWord tempWord;
        UnsignedByte a;
        UnsignedByte b;
        opShortDesc = "";
        opLongDesc = "";
        lastOpCodeInfo = OpcodeTable.getOpcodeInfo(opWord);

        /* Advance PC if we have a 16-bit opcode */
        if (lastOpCodeInfo[4].equals("16")) {
            io.incrementPC();
        }

        switch (lastOpCodeInfo[2]) {
            case "INH":
                break;

            case "IMM":
                memoryResult = lastOpCodeInfo[3].equals("2") ? io.getImmediateByte() : io.getImmediateWord();
                break;

            case "IND":
                memoryResult = io.getIndexed();
                break;

            case "DIR":
                memoryResult = io.getDirect();
                break;

            case "EXT":
                memoryResult = io.getExtended();
                break;

            default:
                break;
        }

        switch (operand.getShort()) {
            /* NEG - Negate M - Direct */
            case 0x00:
                operationTicks = 6;
                executeByteFunctionM(this::negate, memoryResult);
                break;

            /* COM - Complement M - Direct */
            case 0x03:
                operationTicks = 6;
                executeByteFunctionM(this::compliment, memoryResult);
                break;

            /* LSR - Logical Shift Right - Direct */
            case 0x04:
                operationTicks = 6;
                executeByteFunctionM(this::logicalShiftRight, memoryResult);
                break;

            /* ROR - Rotate Right - Direct */
            case 0x06:
                operationTicks = 6;
                executeByteFunctionM(this::rotateRight, memoryResult);
                break;

            /* ASR - Arithmetic Shift Right - Direct */
            case 0x07:
                operationTicks = 6;
                executeByteFunctionM(this::arithmeticShiftRight, memoryResult);
                break;

            /* ASL - Arithmetic Shift Left - Direct */
            case 0x08:
                operationTicks = 6;
                executeByteFunctionM(this::arithmeticShiftLeft, memoryResult);
                break;

            /* ROL - Rotate Left - Direct */
            case 0x09:
                operationTicks = 6;
                executeByteFunctionM(this::rotateLeft, memoryResult);
                break;

            /* DEC - Decrement - Direct */
            case 0x0A:
                operationTicks = 6;
                executeByteFunctionM(this::decrement, memoryResult);
                break;

            /* INC - Increment - Direct */
            case 0x0C:
                operationTicks = 6;
                executeByteFunctionM(this::increment, memoryResult);
                break;

            /* TST - Test - Direct */
            case 0x0D:
                operationTicks = 6;
                executeByteFunctionM(this::test, memoryResult);
                break;

            /* JMP - Jump - Direct */
            case 0x0E:
                operationTicks = 3;
                jump(memoryResult.get());
                break;

            /* CLR - Clear - Direct */
            case 0x0F:
                operationTicks = 6;
                executeByteFunctionM(this::clear, memoryResult);
                break;

            /* 0x10 - Extended Opcodes */
            case 0x10:
            {
                UnsignedByte extendedOp = opWord.getLow();

                switch(extendedOp.getShort()) {

                    /* LBRN - Long Branch Never */
                    case 0x21:
                        operationTicks = 5;
                        break;

                    /* LBHI - Long Branch on Higher */
                    case 0x22:
                        operationTicks = 5;
                        if (!io.ccCarrySet() && !io.ccZeroSet()) {
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                        }
                        break;

                    /* LBLS - Long Branch on Lower or Same */
                    case 0x23:
                        operationTicks = 5;
                        opLongDesc = "C=" + io.ccCarrySet() + ", Z=" + io.ccZeroSet() + ", not branching";
                        if (io.ccCarrySet() || io.ccZeroSet()) {
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                            opLongDesc = "C=" + io.ccCarrySet() + ", Z=" + io.ccZeroSet() + ", branching";
                        }
                        break;

                    /* LBCC - Long Branch on Carry Clear */
                    case 0x24:
                        operationTicks = 5;
                        opLongDesc = "C=true, Not Branching";
                        if (!io.ccCarrySet()) {
                            opLongDesc = "C=false, Branching";
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                        }
                        break;

                    /* LBCS - Long Branch on Carry Set */
                    case 0x25:
                        operationTicks = 5;
                        opLongDesc = "C=false, Not Branching";
                        if (io.ccCarrySet()) {
                            opLongDesc = "C=true, Branching";
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                        }
                        break;

                    /* LBNE - Long Branch on Not Equal */
                    case 0x26:
                        operationTicks = 5;
                        opLongDesc = "Z=" + io.ccZeroSet() + ", Not Branching";
                        if (!io.ccZeroSet()) {
                            opLongDesc = "Z=" + io.ccZeroSet() + ", Branching";
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                        }
                        break;

                    /* LBEQ - Long Branch on Equal */
                    case 0x27:
                        operationTicks = 5;
                        opLongDesc = "Z=" + io.ccZeroSet() + ", Not Branching";
                        if (io.ccZeroSet()) {
                            opLongDesc = "Z=" + io.ccZeroSet() + ", Branching";
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                        }
                        break;

                    /* LBVC - Long Branch on Overflow Clear */
                    case 0x28:
                        operationTicks = 5;
                        if (!io.ccOverflowSet()) {
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                        }
                        break;

                    /* LBVS - Long Branch on Overflow Set */
                    case 0x29:
                        operationTicks = 5;
                        if (io.ccOverflowSet()) {
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                        }
                        break;

                    /* LBPL - Long Branch on Plus */
                    case 0x2A:
                        operationTicks = 5;
                        if (!io.ccNegativeSet()) {
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                        }
                        break;

                    /* LBMI - Long Branch on Minus */
                    case 0x2B:
                        operationTicks = 5;
                        if (io.ccNegativeSet()) {
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                        }
                        break;

                    /* LBGE - Long Branch on Greater Than or Equal to Zero */
                    case 0x2C:
                        operationTicks = 5;
                        if (io.ccNegativeSet() == io.ccOverflowSet()) {
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                        }
                        break;

                    /* LBLT - Long Branch on Less Than or Equal to Zero */
                    case 0x2D:
                        operationTicks = 5;
                        if (io.ccNegativeSet() != io.ccOverflowSet()) {
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                        }
                        break;

                    /* LBGT - Long Branch on Greater Than Zero */
                    case 0x2E:
                        operationTicks = 5;
                        if (!io.ccZeroSet() && (io.ccNegativeSet() == io.ccOverflowSet())) {
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                        }
                        break;

                    /* LBLE - Long Branch on Less Than Zero */
                    case 0x2F:
                        operationTicks = 5;
                        if (io.ccZeroSet() || (io.ccNegativeSet() != io.ccOverflowSet())) {
                            branchLong(memoryResult.get());
                            operationTicks = 6;
                        }
                        break;

                    /* SWI3 - Software Interrupt 3 */
                    case 0x3F:
                        softwareInterrupt(SWI3);
                        operationTicks = 19;
                        break;

                    /* CMPD - Compare D - Immediate */
                    case 0x83:
                        compareWord(io.getWordRegister(Register.D), memoryResult.get());
                        operationTicks = 5;
                        break;

                    /* CMPY - Compare Y - Immediate */
                    case 0x8C:
                        compareWord(io.getWordRegister(Register.Y), memoryResult.get());
                        operationTicks = 5;
                        break;

                    /* LDY - Load Y - Immediate */
                    case 0x8E:
                        loadRegister(Register.Y, memoryResult.get());
                        operationTicks = 4;
                        break;

                    /* CMPD - Compare D - Direct */
                    case 0x93:
                        compareWord(io.getWordRegister(Register.D), io.readWord(memoryResult.get()));
                        operationTicks = 7;
                        break;

                    /* CMPY - Compare Y - Direct */
                    case 0x9C:
                        compareWord(io.getWordRegister(Register.Y), io.readWord(memoryResult.get()));
                        operationTicks = 7;
                        break;

                    /* LDY - Load Y - Direct */
                    case 0x9E:
                        loadRegister(Register.Y, io.readWord(memoryResult.get()));
                        operationTicks = 6;
                        break;

                    /* STY - Store Y - Direct */
                    case 0x9F:
                        storeWordRegister(Register.Y, memoryResult.get());
                        operationTicks = 6;
                        break;

                    /* CMPD - Compare D - Direct */
                    case 0xA3:
                        compareWord(io.getWordRegister(Register.D), io.readWord(memoryResult.get()));
                        operationTicks = 5 + memoryResult.getBytesConsumed();
                        break;

                    /* CMPY - Compare Y - Direct */
                    case 0xAC:
                        compareWord(io.getWordRegister(Register.Y), io.readWord(memoryResult.get()));
                        operationTicks = 5 + memoryResult.getBytesConsumed();
                        break;

                    /* LDY - Load Y - Indexed */
                    case 0xAE:
                        loadRegister(Register.Y, io.readWord(memoryResult.get()));
                        operationTicks = 4 + memoryResult.getBytesConsumed();
                        break;

                    /* STY - Store Y - Indexed */
                    case 0xAF:
                        storeWordRegister(Register.Y, memoryResult.get());
                        operationTicks = 4 + memoryResult.getBytesConsumed();
                        break;

                    /* CMPD - Compare D - Extended */
                    case 0xB3:
                        compareWord(io.getWordRegister(Register.D), io.readWord(memoryResult.get()));
                        operationTicks = 8;
                        break;

                    /* CMPY - Compare Y - Extended */
                    case 0xBC:
                        compareWord(io.getWordRegister(Register.Y), io.readWord(memoryResult.get()));
                        operationTicks = 8;
                        break;

                    /* LDY - Load Y - Extended */
                    case 0xBE:
                        loadRegister(Register.Y, io.readWord(memoryResult.get()));
                        operationTicks = 7;
                        break;

                    /* STY - Store Y - Extended */
                    case 0xBF:
                        storeWordRegister(Register.Y, memoryResult.get());
                        operationTicks = 7;
                        break;

                    /* LDS - Load S - Immediate */
                    case 0xCE:
                        loadRegister(Register.S, memoryResult.get());
                        operationTicks = 4;
                        break;

                    /* LDS - Load S - Direct */
                    case 0xDE:
                        loadRegister(Register.S, io.readWord(memoryResult.get()));
                        operationTicks = 6;
                        break;

                    /* STS - Store S - Direct */
                    case 0xDF:
                        storeWordRegister(Register.S, memoryResult.get());
                        operationTicks = 6;
                        break;

                    /* LDS - Load S - Indexed */
                    case 0xEE:
                        loadRegister(Register.S, io.readWord(memoryResult.get()));
                        operationTicks = 4 + memoryResult.getBytesConsumed();
                        break;

                    /* STS - Store S - Indexed */
                    case 0xEF:
                        storeWordRegister(Register.S, memoryResult.get());
                        operationTicks = 4 + memoryResult.getBytesConsumed();
                        break;

                    /* LDS - Load S - Extended */
                    case 0xFE:
                        loadRegister(Register.S, io.readWord(memoryResult.get()));
                        operationTicks = 7;
                        break;

                    /* STS - Store S - Extended */
                    case 0xFF:
                        storeWordRegister(Register.S, memoryResult.get());
                        operationTicks = 7;
                        break;
                }
                break;
            }

            /* 0x11 - Extended Opcodes */
            case 0x11: {
                UnsignedByte extendedOp = opWord.getLow();

                switch (extendedOp.getShort()) {
                    /* SWI2 - Software Interrupt 2 */
                    case 0x3F:
                        softwareInterrupt(SWI2);
                        operationTicks = 20;
                        break;

                    /* CMPU - Compare U - Immediate */
                    case 0x83:
                        compareWord(io.getWordRegister(Register.U), memoryResult.get());
                        operationTicks = 5;
                        break;

                    /* CMPS - Compare S - Immediate */
                    case 0x8C:
                        compareWord(io.getWordRegister(Register.S), memoryResult.get());
                        operationTicks = 5;
                        break;

                    /* CMPU - Compare U - Direct */
                    case 0x93:
                        compareWord(io.getWordRegister(Register.U), io.readWord(memoryResult.get()));
                        operationTicks = 7;
                        break;

                    /* CMPS - Compare S - Direct */
                    case 0x9C:
                        compareWord(io.getWordRegister(Register.S), io.readWord(memoryResult.get()));
                        operationTicks = 7;
                        break;

                    /* CMPU - Compare U - Indexed */
                    case 0xA3:
                        compareWord(io.getWordRegister(Register.U), io.readWord(memoryResult.get()));
                        operationTicks = 5 + memoryResult.getBytesConsumed();
                        break;

                    /* CMPS - Compare S - Indexed */
                    case 0xAC:
                        compareWord(io.getWordRegister(Register.S), io.readWord(memoryResult.get()));
                        operationTicks = 5 + memoryResult.getBytesConsumed();
                        break;

                    /* CMPU - Compare U - Extended */
                    case 0xB3:
                        compareWord(io.getWordRegister(Register.U), io.readWord(memoryResult.get()));
                        operationTicks = 8;
                        break;

                    /* CMPS - Compare S - Extended */
                    case 0xBC:
                        compareWord(io.getWordRegister(Register.S), io.readWord(memoryResult.get()));
                        operationTicks = 8;
                        break;
                }
                break;
            }

            /* NOP - No Operation - Inherent */
            case 0x12:
                operationTicks = 2;
                break;

            /* SYNC - Sync - Inherent */
            case 0x13:
                break;

            /* LBRA - Long Branch Always - Immediate */
            case 0x16:
                branchLong(memoryResult.get());
                operationTicks = 5;
                break;

            /* LBSR - Long Branch to Subroutine */
            case 0x17:
                io.pushStack(Register.S, io.getWordRegister(Register.PC));
                branchLong(memoryResult.get());
                operationTicks = 9;
                break;

            /* DAA - Decimal Addition Adjust */
            case 0x19:
                decimalAdditionAdjust();
                operationTicks = 2;
                break;

            /* ORCC - Logical OR on Condition Code Register */
            case 0x1A:
                opLongDesc = "CC=" + io.getByteRegister(Register.CC) + ", M=" + memoryResult.get().getHigh() + ", CC'=";
                io.getByteRegister(Register.CC).or(memoryResult.get().getHigh().getShort());
                opLongDesc += io.getByteRegister(Register.CC);
                operationTicks = 3;
                break;

            /* ANDCC - Logical AND on Condition Code Register */
            case 0x1C:
                io.getByteRegister(Register.CC).and(memoryResult.get().getHigh().getShort());
                operationTicks = 3;
                break;

            /* SEX - Sign Extend */
            case 0x1D:
                io.setA(io.getByteRegister(Register.B).isMasked(0x80) ? new UnsignedByte(0xFF) : new UnsignedByte());
                operationTicks = 2;
                break;

            /* EXG - Exchange Register */
            case 0x1E: {
                UnsignedByte extendedOp = memoryResult.get().getHigh();
                UnsignedWord temp = new UnsignedWord();
                UnsignedByte tempByte = new UnsignedByte();
                operationTicks = 8;
                switch (extendedOp.getShort()) {

                    /* A:B <-> X */
                    case 0x01:
                    case 0x10:
                        temp.set(io.getWordRegister(Register.X));
                        io.setX(io.getWordRegister(Register.D));
                        io.setD(temp);
                        break;

                    /* A:B <-> Y */
                    case 0x02:
                    case 0x20:
                        temp.set(io.getWordRegister(Register.Y));
                        io.setY(io.getWordRegister(Register.D));
                        io.setD(temp);
                        break;

                    /* A:B <-> U */
                    case 0x03:
                    case 0x30:
                        temp.set(io.getWordRegister(Register.U));
                        io.setU(io.getWordRegister(Register.D));
                        io.setD(temp);
                        break;

                    /* A:B <-> S */
                    case 0x04:
                    case 0x40:
                        temp.set(io.getWordRegister(Register.S));
                        io.setS(io.getWordRegister(Register.D));
                        io.setD(temp);
                        break;

                    /* A:B <-> PC */
                    case 0x05:
                    case 0x50:
                        temp.set(io.getWordRegister(Register.PC));
                        io.setPC(io.getWordRegister(Register.D));
                        io.setD(temp);
                        break;

                    /* X <-> Y */
                    case 0x12:
                    case 0x21:
                        temp.set(io.getWordRegister(Register.X));
                        io.setX(io.getWordRegister(Register.Y));
                        io.setY(temp);
                        break;

                    /* X <-> U */
                    case 0x13:
                    case 0x31:
                        temp.set(io.getWordRegister(Register.X));
                        io.setX(io.getWordRegister(Register.U));
                        io.setU(temp);
                        break;

                    /* X <-> S */
                    case 0x14:
                    case 0x41:
                        temp.set(io.getWordRegister(Register.X));
                        io.setX(io.getWordRegister(Register.S));
                        io.setS(temp);
                        break;

                    /* X <-> PC */
                    case 0x15:
                    case 0x51:
                        temp.set(io.getWordRegister(Register.X));
                        io.setX(io.getWordRegister(Register.PC));
                        io.setPC(temp);
                        break;

                    /* Y <-> U */
                    case 0x23:
                    case 0x32:
                        temp.set(io.getWordRegister(Register.Y));
                        io.setY(io.getWordRegister(Register.U));
                        io.setU(temp);
                        break;

                    /* Y <-> S */
                    case 0x24:
                    case 0x42:
                        temp.set(io.getWordRegister(Register.S));
                        io.setS(io.getWordRegister(Register.Y));
                        io.setY(temp);
                        break;

                    /* Y <-> PC */
                    case 0x25:
                    case 0x52:
                        temp.set(io.getWordRegister(Register.Y));
                        io.setY(io.getWordRegister(Register.PC));
                        io.setPC(temp);
                        break;

                    /* U <-> S */
                    case 0x34:
                    case 0x43:
                        temp.set(io.getWordRegister(Register.U));
                        io.setU(io.getWordRegister(Register.S));
                        io.setS(temp);
                        break;

                    /* U <-> PC */
                    case 0x35:
                    case 0x53:
                        temp.set(io.getWordRegister(Register.U));
                        io.setU(io.getWordRegister(Register.PC));
                        io.setPC(temp);
                        break;

                    /* S <-> PC */
                    case 0x45:
                    case 0x54:
                        temp.set(io.getWordRegister(Register.S));
                        io.setS(io.getWordRegister(Register.PC));
                        io.setPC(temp);
                        break;

                    /* A <-> B */
                    case 0x89:
                    case 0x98:
                        tempByte.set(io.getByteRegister(Register.A));
                        io.setA(io.getByteRegister(Register.B));
                        io.setB(tempByte);
                        break;

                    /* A <-> CC */
                    case 0x8A:
                    case 0xA8:
                        tempByte.set(io.getByteRegister(Register.A));
                        io.setA(io.getByteRegister(Register.CC));
                        io.setCC(tempByte);
                        break;

                    /* A <-> DP */
                    case 0x8B:
                    case 0xB8:
                        tempByte.set(io.getByteRegister(Register.A));
                        io.setA(io.getByteRegister(Register.DP));
                        io.setDP(tempByte);
                        break;

                    /* B <-> CC */
                    case 0x9A:
                    case 0xA9:
                        tempByte.set(io.getByteRegister(Register.B));
                        io.setB(io.getByteRegister(Register.CC));
                        io.setCC(tempByte);
                        break;

                    /* B <-> DP */
                    case 0x9B:
                    case 0xB9:
                        tempByte.set(io.getByteRegister(Register.B));
                        io.setB(io.getByteRegister(Register.DP));
                        io.setDP(tempByte);
                        break;

                    /* CC <-> DP */
                    case 0xAB:
                    case 0xBA:
                        tempByte.set(io.getByteRegister(Register.CC));
                        io.setCC(io.getByteRegister(Register.DP));
                        io.setDP(tempByte);
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
                UnsignedByte extendedOp = memoryResult.get().getHigh();
                operationTicks = 6;
                switch (extendedOp.getShort()) {

                    /* A:B -> X */
                    case 0x01:
                        io.setX(io.getWordRegister(Register.D));
                        opLongDesc = "D->X, X'=" + io.getWordRegister(Register.X);
                        break;

                    /* A:B -> Y */
                    case 0x02:
                        io.setY(io.getWordRegister(Register.D));
                        opLongDesc = "D->Y, Y'=" + io.getWordRegister(Register.Y);
                        break;

                    /* A:B -> U */
                    case 0x03:
                        io.setU(io.getWordRegister(Register.D));
                        opLongDesc = "D->U, U'=" + io.getWordRegister(Register.U);
                        break;

                    /* A:B -> S */
                    case 0x04:
                        io.setS(io.getWordRegister(Register.D));
                        opLongDesc = "D->S, S'=" + io.getWordRegister(Register.S);
                        break;

                    /* A:B -> PC */
                    case 0x05:
                        io.setPC(io.getWordRegister(Register.D));
                        opLongDesc = "D->PC, PC'=" + io.getWordRegister(Register.PC);
                        break;

                    /* X -> A:B */
                    case 0x10:
                        io.setD(io.getWordRegister(Register.X));
                        opLongDesc = "X->D, D'=" + io.getWordRegister(Register.D);
                        break;

                    /* X -> Y */
                    case 0x12:
                        io.setY(io.getWordRegister(Register.X));
                        opLongDesc = "X->Y, Y'=" + io.getWordRegister(Register.Y);
                        break;

                    /* X -> U */
                    case 0x13:
                        io.setU(io.getWordRegister(Register.X));
                        opLongDesc = "X->U, U'=" + io.getWordRegister(Register.U);
                        break;

                    /* X -> S */
                    case 0x14:
                        io.setS(io.getWordRegister(Register.X));
                        opLongDesc = "X->S, S'=" + io.getWordRegister(Register.S);
                        break;

                    /* X -> PC */
                    case 0x15:
                        io.setPC(io.getWordRegister(Register.X));
                        opLongDesc = "X->PC, PC'=" + io.getWordRegister(Register.PC);
                        break;

                    /* Y -> A:B */
                    case 0x20:
                        io.setD(io.getWordRegister(Register.Y));
                        opLongDesc = "Y->D, D'=" + io.getWordRegister(Register.D);
                        break;

                    /* Y -> X */
                    case 0x21:
                        io.setX(io.getWordRegister(Register.Y));
                        opLongDesc = "Y->X, X'=" + io.getWordRegister(Register.X);
                        break;

                    /* Y -> U */
                    case 0x23:
                        io.setU(io.getWordRegister(Register.Y));
                        opLongDesc = "Y->U, U'=" + io.getWordRegister(Register.U);
                        break;

                    /* Y -> S */
                    case 0x24:
                        io.setS(io.getWordRegister(Register.Y));
                        opLongDesc = "Y->S, S'=" + io.getWordRegister(Register.S);
                        break;

                    /* Y -> PC */
                    case 0x25:
                        io.setPC(io.getWordRegister(Register.Y));
                        opLongDesc = "Y->PC, PC'=" + io.getWordRegister(Register.PC);
                        break;

                    /* U -> A:B */
                    case 0x30:
                        io.setD(io.getWordRegister(Register.U));
                        opLongDesc = "U->D, D'=" + io.getWordRegister(Register.D);
                        break;

                    /* U -> X */
                    case 0x31:
                        io.setX(io.getWordRegister(Register.U));
                        opLongDesc = "U->X, X'=" + io.getWordRegister(Register.X);
                        break;

                    /* U -> Y */
                    case 0x32:
                        io.setY(io.getWordRegister(Register.U));
                        opLongDesc = "U->Y, Y'=" + io.getWordRegister(Register.Y);
                        break;

                    /* U -> S */
                    case 0x34:
                        io.setS(io.getWordRegister(Register.U));
                        opLongDesc = "U->S, S'=" + io.getWordRegister(Register.S);
                        break;

                    /* U -> PC */
                    case 0x35:
                        io.setPC(io.getWordRegister(Register.U));
                        opLongDesc = "U->PC, PC'=" + io.getWordRegister(Register.PC);
                        break;

                    /* S -> A:B */
                    case 0x40:
                        io.setD(io.getWordRegister(Register.S));
                        opLongDesc = "S->D, D'=" + io.getWordRegister(Register.D);
                        break;

                    /* S -> X */
                    case 0x41:
                        io.setX(io.getWordRegister(Register.S));
                        opLongDesc = "S->X, X'=" + io.getWordRegister(Register.X);
                        break;

                    /* S -> Y */
                    case 0x42:
                        io.setY(io.getWordRegister(Register.S));
                        opLongDesc = "S->Y, Y'=" + io.getWordRegister(Register.Y);
                        break;

                    /* S -> U */
                    case 0x43:
                        io.setU(io.getWordRegister(Register.S));
                        opLongDesc = "S->U, U'=" + io.getWordRegister(Register.U);
                        break;

                    /* S -> PC */
                    case 0x45:
                        io.setPC(io.getWordRegister(Register.S));
                        opLongDesc = "S->PC, PC'=" + io.getWordRegister(Register.PC);
                        break;

                    /* PC -> A:B */
                    case 0x50:
                        io.setD(io.getWordRegister(Register.PC));
                        opLongDesc = "PC->D, D'=" + io.getWordRegister(Register.D);
                        break;

                    /* PC -> X */
                    case 0x51:
                        io.setX(io.getWordRegister(Register.PC));
                        opLongDesc = "PC->X, X'=" + io.getWordRegister(Register.X);
                        break;

                    /* PC -> Y */
                    case 0x52:
                        io.setY(io.getWordRegister(Register.PC));
                        opLongDesc = "PC->Y, Y'=" + io.getWordRegister(Register.Y);
                        break;

                    /* PC -> U */
                    case 0x53:
                        io.setU(io.getWordRegister(Register.PC));
                        opLongDesc = "PC->U, U'=" + io.getWordRegister(Register.U);
                        break;

                    /* PC -> S */
                    case 0x54:
                        io.setS(io.getWordRegister(Register.PC));
                        opLongDesc = "PC->S, S'=" + io.getWordRegister(Register.S);
                        break;

                    /* A -> B */
                    case 0x89:
                        io.setB(io.getByteRegister(Register.A));
                        opLongDesc = "A->B, B'=" + io.getByteRegister(Register.B);
                        break;

                    /* A -> CC */
                    case 0x8A:
                        io.setCC(io.getByteRegister(Register.A));
                        opLongDesc = "A->CC, CC'=" + io.getByteRegister(Register.CC);
                        break;

                    /* A -> DP */
                    case 0x8B:
                        io.setDP(io.getByteRegister(Register.A));
                        opLongDesc = "A->DP, DP'=" + io.getByteRegister(Register.DP);
                        break;

                    /* B -> A */
                    case 0x98:
                        io.setA(io.getByteRegister(Register.B));
                        opLongDesc = "B->A, A'=" + io.getByteRegister(Register.A);
                        break;

                    /* B -> CC */
                    case 0x9A:
                        io.setCC(io.getByteRegister(Register.B));
                        opLongDesc = "B->CC, CC'=" + io.getByteRegister(Register.CC);
                        break;

                    /* B -> DP */
                    case 0x9B:
                        io.setDP(io.getByteRegister(Register.B));
                        opLongDesc = "B->DP, DP'=" + io.getByteRegister(Register.DP);
                        break;

                    /* CC -> A */
                    case 0xA8:
                        io.setA(io.getByteRegister(Register.CC));
                        opLongDesc = "CC->A, A'=" + io.getByteRegister(Register.A);
                        break;

                    /* CC -> B */
                    case 0xA9:
                        io.setB(io.getByteRegister(Register.CC));
                        opLongDesc = "CC->B, B'=" + io.getByteRegister(Register.B);
                        break;

                    /* CC -> DP */
                    case 0xAB:
                        io.setDP(io.getByteRegister(Register.CC));
                        opLongDesc = "CC->DP, DP'=" + io.getByteRegister(Register.DP);
                        break;

                    /* DP -> A */
                    case 0xB8:
                        io.setA(io.getByteRegister(Register.DP));
                        opLongDesc = "DP->A, A'=" + io.getByteRegister(Register.A);
                        break;

                    /* DP -> B */
                    case 0xB9:
                        io.setB(io.getByteRegister(Register.DP));
                        opLongDesc = "DP->B, B'=" + io.getByteRegister(Register.B);
                        break;

                    /* DP -> CC */
                    case 0xBA:
                        io.setCC(io.getByteRegister(Register.DP));
                        opLongDesc = "DP->CC, CC'=" + io.getByteRegister(Register.CC);
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
                        throw new RuntimeException("Illegal transfer " + extendedOp);
                }
            }
            break;

            /* BRA - Branch Always */
            case 0x20:
                branchShort(memoryResult.get().getHigh());
                operationTicks = 3;
                break;

            /* BRN - Branch Never */
            case 0x21:
                operationTicks = 3;
                break;

            /* BHI - Branch on Higher */
            case 0x22:
                if (!io.ccCarrySet() && !io.ccZeroSet()) {
                    opLongDesc = "C=" + io.ccCarrySet() + ", Z=" + io.ccZeroSet() + ", Branching";
                    branchShort(memoryResult.get().getHigh());
                } else {
                    opLongDesc = "C=" + io.ccCarrySet() + ", Z=" + io.ccZeroSet() + ", Not Branching";
                }
                operationTicks = 3;
                break;

            /* BLS - Branch on Lower or Same */
            case 0x23:
                if (io.ccCarrySet() || io.ccZeroSet()) {
                    opLongDesc = "C=" + io.ccCarrySet() + ", Z=" + io.ccZeroSet() + ", Branching";
                    branchShort(memoryResult.get().getHigh());
                } else {
                    opLongDesc = "C=" + io.ccCarrySet() + ", Z=" + io.ccZeroSet() + ", Not Branching";
                }
                operationTicks = 5;
                break;

            /* BCC - Branch on Carry Clear */
            case 0x24:
                if (!io.ccCarrySet()) {
                    branchShort(memoryResult.get().getHigh());
                    opLongDesc = "C=false, branching";
                } else {
                    opLongDesc = "C=true, not branching";
                }
                operationTicks = 3;
                break;

            /* BCS - Branch on Carry Set */
            case 0x25:
                if (io.ccCarrySet()) {
                    branchShort(memoryResult.get().getHigh());
                    opLongDesc = "C=" + io.ccCarrySet() + ", Branching";
                } else {
                    opLongDesc = "C=" + io.ccCarrySet() + ", Not Branching";

                }
                operationTicks = 3;
                break;

            /* BNE - Branch on Not Equal */
            case 0x26:
                if (!io.ccZeroSet()) {
                    branchShort(memoryResult.get().getHigh());
                    opLongDesc = "Z=" + io.ccZeroSet() + ", Branching";
                } else {
                    opLongDesc = "Z=" + io.ccZeroSet() + ", Not Branching";
                }
                operationTicks = 3;
                break;

            /* BEQ - Branch on Equal */
            case 0x27:
                if (io.ccZeroSet()) {
                    branchShort(memoryResult.get().getHigh());
                    opLongDesc = "Z=" + io.ccZeroSet() + ", Branching";
                } else {
                    opLongDesc = "Z=" + io.ccZeroSet() + ", Not Branching";
                }
                operationTicks = 3;
                break;

            /* BVC - Branch on Overflow Clear */
            case 0x28:
                if (!io.ccOverflowSet()) {
                    branchShort(memoryResult.get().getHigh());
                    opLongDesc = "V=" + io.ccOverflowSet() + ", Branching";
                } else {
                    opLongDesc = "V=" + io.ccOverflowSet() + ", Not Branching";
                }
                operationTicks = 3;
                break;

            /* BVS - Branch on Overflow Set */
            case 0x29:
                if (io.ccOverflowSet()) {
                    branchShort(memoryResult.get().getHigh());
                    opLongDesc = "V=" + io.ccOverflowSet() + ", Branching";
                } else {
                    opLongDesc = "V=" + io.ccOverflowSet() + ", Not Branching";
                }
                operationTicks = 3;
                break;

            /* BPL - Branch on Plus */
            case 0x2A:
                if (!io.ccNegativeSet()) {
                    branchShort(memoryResult.get().getHigh());
                    opLongDesc = "N=" + io.ccNegativeSet() + ", Branching";
                } else {
                    opLongDesc = "N=" + io.ccNegativeSet() + ", Not Branching";
                }
                operationTicks = 3;
                break;

            /* BMI - Branch on Minus */
            case 0x2B:
                if (io.ccNegativeSet()) {
                    opLongDesc = "N=" + io.ccNegativeSet() + ", Branching";
                    branchShort(memoryResult.get().getHigh());
                } else {
                    opLongDesc = "N=" + io.ccNegativeSet() + ", Not Branching";
                }
                operationTicks = 3;
                break;

            /* BGE - Branch on Greater Than or Equal to Zero */
            case 0x2C:
                if (io.ccNegativeSet() == io.ccOverflowSet()) {
                    opLongDesc = "N=" + io.ccNegativeSet() + ", V=" + io.ccOverflowSet() + ", Branching";
                    branchShort(memoryResult.get().getHigh());
                } else {
                    opLongDesc = "N=" + io.ccNegativeSet() + ", V=" + io.ccOverflowSet() + ", Not Branching";
                }
                operationTicks = 3;
                break;

            /* BLT - Branch on Less Than or Equal to Zero */
            case 0x2D:
                if (io.ccNegativeSet() != io.ccOverflowSet()) {
                    opLongDesc = "N=" + io.ccNegativeSet() + ", V=" + io.ccOverflowSet() + ", Branching";
                    branchShort(memoryResult.get().getHigh());
                } else {
                    opLongDesc = "N=" + io.ccNegativeSet() + ", V=" + io.ccOverflowSet() + ", Not Branching";
                }
                operationTicks = 5;
                break;

            /* BGT - Branch on Greater Than Zero */
            case 0x2E:
                if (!io.ccZeroSet() && (io.ccNegativeSet() == io.ccOverflowSet())) {
                    opLongDesc = "N=" + io.ccNegativeSet() + ", V=" + io.ccOverflowSet() + ", Z=" + io.ccZeroSet() + ", Branching";
                    branchShort(memoryResult.get().getHigh());
                } else {
                    opLongDesc = "N=" + io.ccNegativeSet() + ", V=" + io.ccOverflowSet() + ", Z=" + io.ccZeroSet() + ", Not Branching";
                }
                operationTicks = 3;
                break;

            /* BLE - Branch on Less Than Zero */
            case 0x2F:
                if (io.ccZeroSet() || (io.ccNegativeSet() != io.ccOverflowSet())) {
                    opLongDesc = "N=" + io.ccNegativeSet() + ", V=" + io.ccOverflowSet() + ", Z=" + io.ccZeroSet() + ", Branching";
                    branchShort(memoryResult.get().getHigh());
                } else {
                    opLongDesc = "N=" + io.ccNegativeSet() + ", V=" + io.ccOverflowSet() + ", Z=" + io.ccZeroSet() + ", Not Branching";
                }
                operationTicks = 3;
                break;

            /* LEAX - Load Effective Address into X register */
            case 0x30:
                loadEffectiveAddress(Register.X, memoryResult.get());
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* LEAY - Load Effective Address into Y register */
            case 0x31:
                loadEffectiveAddress(Register.Y, memoryResult.get());
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* LEAS - Load Effective Address into S register */
            case 0x32:
                loadEffectiveAddress(Register.S, memoryResult.get());
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* LEAU - Load Effective Address into U register */
            case 0x33:
                loadEffectiveAddress(Register.U, memoryResult.get());
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* PSHS - Push Registers onto S Stack */
            case 0x34:
                bytes = pushStack(Register.S, memoryResult.get().getHigh());
                operationTicks = 5 + bytes;
                break;

            /* PULS - Pull Registers from S Stack */
            case 0x35:
                bytes = popStack(Register.S, memoryResult.get().getHigh());
                operationTicks = 5 + bytes;
                break;

            /* PSHU - Push Registers onto U Stack */
            case 0x36:
                bytes = pushStack(Register.U, memoryResult.get().getHigh());
                operationTicks = 5 + bytes;
                break;

            /* PULU - Pull Registers from U Stack */
            case 0x37:
                bytes = popStack(Register.U, memoryResult.get().getHigh());
                operationTicks = 5 + bytes;
                break;

            /* RTS - Return from Subroutine */
            case 0x39:
                opLongDesc = "S=" + io.getWordRegister(Register.S);
                io.setPC(
                        new UnsignedWord(
                                io.popStack(Register.S),
                                io.popStack(Register.S)
                        )
                );
                opLongDesc += ", PC'=" + io.getWordRegister(Register.PC);
                operationTicks = 5;
                break;

            /* ABX - Add Accumulator B into X */
            case 0x3A:
                opLongDesc = "X=" + io.getWordRegister(Register.X);
                tempWord = new UnsignedWord(
                        new UnsignedByte(),
                        io.getByteRegister(Register.B)
                );
                io.setX(
                        io.binaryAdd(io.getWordRegister(Register.X), tempWord, false, false, false)
                );
                operationTicks = 3;
                opLongDesc += ", B=" + io.getByteRegister(Register.B) + ", X'=" + io.getWordRegister(Register.X);
                break;

            /* RTI - Return from Interrupt */
            case 0x3B:
                io.setCC(io.popStack(Register.S));
                if (io.ccEverythingSet()) {
                    operationTicks = 9;
                    io.setA(io.popStack(Register.S));
                    io.setB(io.popStack(Register.S));
                    io.setDP(io.popStack(Register.S));
                    io.setX(
                            new UnsignedWord(
                                    io.popStack(Register.S),
                                    io.popStack(Register.S)
                            )
                    );
                    io.setY(
                            new UnsignedWord(
                                    io.popStack(Register.S),
                                    io.popStack(Register.S)
                            )
                    );
                    io.setU(
                            new UnsignedWord(
                                    io.popStack(Register.S),
                                    io.popStack(Register.S)
                            )
                    );
                }
                io.setPC(
                        new UnsignedWord(
                                io.popStack(Register.S),
                                io.popStack(Register.S)
                        )
                );
                operationTicks += 6;
                break;

            /* CWAI - Call and Wait for Interrupt */
            case 0x3C:
                UnsignedByte cc = io.getByteRegister(Register.CC);
                cc.and(memoryResult.get().getHigh().getShort());
                cc.or(IOController.CC_E);
                io.pushStack(Register.S, io.getWordRegister(Register.PC));
                io.pushStack(Register.S, io.getWordRegister(Register.U));
                io.pushStack(Register.S, io.getWordRegister(Register.Y));
                io.pushStack(Register.S, io.getWordRegister(Register.X));
                io.pushStack(Register.S, io.getByteRegister(Register.DP));
                io.pushStack(Register.S, io.getByteRegister(Register.B));
                io.pushStack(Register.S, io.getByteRegister(Register.A));
                io.pushStack(Register.S, io.getByteRegister(Register.CC));
                operationTicks = 20;
                break;

            /* MUL - Multiply Unsigned */
            case 0x3D:
                a = io.getByteRegister(Register.A);
                b = io.getByteRegister(Register.B);
                opLongDesc += "A=" + a + ", B=" + b + ", ";
                UnsignedWord tempResult = new UnsignedWord(a.getShort() * b.getShort());
                io.setD(tempResult);
                opLongDesc += "D'=" + tempResult;
                cc = io.getCC();
                cc.and(~(IOController.CC_Z | IOController.CC_C));
                cc.or(tempResult.isZero() ? IOController.CC_Z : 0);
                cc.or(tempResult.isMasked(0x80) ? IOController.CC_C : 0);
                operationTicks = 11;
                break;

            /* SWI - Software Interrupt */
            case 0x3F:
                softwareInterrupt(SWI);
                operationTicks = 19;
                break;

            /* NEGA - Negate A */
            case 0x40:
                io.setA(negate(io.getByteRegister(Register.A)));
                operationTicks = 2;
                break;

            /* COMA - Compliment A */
            case 0x43:
                io.setA(compliment(io.getByteRegister(Register.A)));
                operationTicks = 2;
                break;

            /* LSRA - Logical Shift Right A */
            case 0x44:
                io.setA(logicalShiftRight(io.getByteRegister(Register.A)));
                operationTicks = 2;
                break;

            /* RORA - Rotate Right A */
            case 0x46:
                io.setA(rotateRight(io.getByteRegister(Register.A)));
                operationTicks = 2;
                break;

            /* ASRA - Arithmetic Shift Right A */
            case 0x47:
                io.setA(arithmeticShiftRight(io.getByteRegister(Register.A)));
                operationTicks = 2;
                break;

            /* ASLA - Arithmetic Shift Left A */
            case 0x48:
                io.setA(arithmeticShiftLeft(io.getByteRegister(Register.A)));
                operationTicks = 2;
                break;

            /* ROLA - Rotate Left A */
            case 0x49:
                io.setA(rotateLeft(io.getByteRegister(Register.A)));
                operationTicks = 2;
                break;

            /* DECA - Decrement A */
            case 0x4A:
                io.setA(decrement(io.getByteRegister(Register.A)));
                operationTicks = 2;
                break;

            /* INCA - Increment A */
            case 0x4C:
                io.setA(increment(io.getByteRegister(Register.A)));
                operationTicks = 2;
                break;

            /* TSTA - Test A */
            case 0x4D:
                io.setA(test(io.getByteRegister(Register.A)));
                operationTicks = 2;
                break;

            /* CLRA - Clear A */
            case 0x4F:
                io.setA(clear(io.getByteRegister(Register.A)));
                operationTicks = 2;
                break;

            /* NEGB - Negate B */
            case 0x50:
                io.setB(negate(io.getByteRegister(Register.B)));
                operationTicks = 2;
                break;

            /* COMB - Compliment B */
            case 0x53:
                io.setB(compliment(io.getByteRegister(Register.B)));
                operationTicks = 2;
                break;

            /* LSRB - Logical Shift Right B */
            case 0x54:
                io.setB(logicalShiftRight(io.getByteRegister(Register.B)));
                operationTicks = 2;
                break;

            /* RORB - Rotate Right B */
            case 0x56:
                io.setB(rotateRight(io.getByteRegister(Register.B)));
                operationTicks = 2;
                break;

            /* ASRB - Arithmetic Shift Right B */
            case 0x57:
                io.setB(arithmeticShiftRight(io.getByteRegister(Register.B)));
                operationTicks = 2;
                break;

            /* ASLB - Arithmetic Shift Left B */
            case 0x58:
                io.setB(arithmeticShiftLeft(io.getByteRegister(Register.B)));
                operationTicks = 2;
                break;

            /* ROLB - Rotate Left B */
            case 0x59:
                io.setB(rotateLeft(io.getByteRegister(Register.B)));
                operationTicks = 2;
                break;

            /* DECB - Decrement B */
            case 0x5A:
                io.setB(decrement(io.getByteRegister(Register.B)));
                operationTicks = 2;
                break;

            /* INCB - Increment B */
            case 0x5C:
                io.setB(increment(io.getByteRegister(Register.B)));
                operationTicks = 2;
                break;

            /* TSTB - Test B */
            case 0x5D:
                io.setB(test(io.getByteRegister(Register.B)));
                operationTicks = 2;
                break;

            /* CLRB - Clear B */
            case 0x5F:
                io.setB(clear(io.getByteRegister(Register.B)));
                operationTicks = 2;
                break;

            /* NEG - Negate M - Indexed */
            case 0x60:
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::negate, memoryResult);
                break;

            /* COM - Complement M - Indexed */
            case 0x63:
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::compliment, memoryResult);
                break;

            /* LSR - Logical Shift Right - Indexed */
            case 0x64:
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::logicalShiftRight, memoryResult);
                break;

            /* ROR - Rotate Right - Indexed */
            case 0x66:
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::rotateRight, memoryResult);
                break;

            /* ASR - Arithmetic Shift Right - Indexed */
            case 0x67:
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::arithmeticShiftRight, memoryResult);
                break;

            /* ASL - Arithmetic Shift Left - Indexed */
            case 0x68:
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::arithmeticShiftLeft, memoryResult);
                break;

            /* ROL - Rotate Left - Indexed */
            case 0x69:
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::rotateLeft, memoryResult);
                break;

            /* DEC - Decrement - Indexed */
            case 0x6A:
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::decrement, memoryResult);
                break;

            /* INC - Increment - Indexed */
            case 0x6C:
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::increment, memoryResult);
                break;

            /* TST - Test - Indexed */
            case 0x6D:
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::test, memoryResult);
                break;

            /* JMP - Jump - Indexed */
            case 0x6E:
                operationTicks = 1 + memoryResult.getBytesConsumed();
                jump(memoryResult.get());
                break;

            /* CLR - Clear - Indexed */
            case 0x6F:
                operationTicks = 4 + memoryResult.getBytesConsumed();
                executeByteFunctionM(this::clear, memoryResult);
                break;

            /* NEG - Negate M - Extended */
            case 0x70:
                operationTicks = 7;
                executeByteFunctionM(this::negate, memoryResult);
                break;

            /* COM - Complement M - Extended */
            case 0x73:
                operationTicks = 7;
                executeByteFunctionM(this::compliment, memoryResult);
                break;

            /* LSR - Logical Shift Right - Extended */
            case 0x74:
                operationTicks = 7;
                executeByteFunctionM(this::logicalShiftRight, memoryResult);
                break;

            /* ROR - Rotate Right - Extended */
            case 0x76:
                operationTicks = 7;
                executeByteFunctionM(this::rotateRight, memoryResult);
                break;

            /* ASR - Arithmetic Shift Right - Extended */
            case 0x77:
                operationTicks = 7;
                executeByteFunctionM(this::arithmeticShiftRight, memoryResult);
                break;

            /* ASL - Arithmetic Shift Left - Extended */
            case 0x78:
                operationTicks = 7;
                executeByteFunctionM(this::arithmeticShiftLeft, memoryResult);
                break;

            /* ROL - Rotate Left - Extended */
            case 0x79:
                operationTicks = 7;
                executeByteFunctionM(this::rotateLeft, memoryResult);
                break;

            /* DEC - Decrement - Extended */
            case 0x7A:
                operationTicks = 7;
                executeByteFunctionM(this::decrement, memoryResult);
                break;

            /* INC - Increment - Extended */
            case 0x7C:
                operationTicks = 7;
                executeByteFunctionM(this::increment, memoryResult);
                break;

            /* TST - Test - Extended */
            case 0x7D:
                operationTicks = 7;
                executeByteFunctionM(this::test, memoryResult);
                break;

            /* JMP - Jump - Extended */
            case 0x7E:
                operationTicks = 4;
                jump(memoryResult.get());
                break;

            /* CLR - Clear - Extended */
            case 0x7F:
                operationTicks = 7;
                executeByteFunctionM(this::clear, memoryResult);
                break;

            /* SUBA - Subtract M from A - Immediate */
            case 0x80:
                subtractM(Register.A, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* CMPA - Compare A - Immediate */
            case 0x81:
                compareByte(io.getByteRegister(Register.A), memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* SBCA - Subtract M and C from A - Immediate */
            case 0x82:
                subtractMC(Register.A, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* SUBD - Subtract M from D - Immediate */
            case 0x83:
                subtractD(memoryResult.get());
                operationTicks = 4;
                break;

            /* ANDA - Logical AND A - Immediate */
            case 0x84:
                logicalAnd(Register.A, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* BITA - Test A - Immediate */
            case 0x85:
                test(new UnsignedByte(io.getByteRegister(Register.A).getShort() & memoryResult.get().getHigh().getShort()));
                operationTicks = 2;
                break;

            /* LDA - Load A - Immediate */
            case 0x86:
                loadByteRegister(Register.A, memoryResult.get().getHigh());
                operationTicks = 4;
                break;

            /* EORA - Exclusive OR A - Immediate */
            case 0x88:
                exclusiveOr(Register.A, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* ADCA - Add with Carry A - Immediate */
            case 0x89:
                addWithCarry(Register.A, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* ORA - Logical OR A - Immediate */
            case 0x8A:
                logicalOr(Register.A, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* ADDA - Add A - Immediate */
            case 0x8B:
                addByteRegister(Register.A, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* CMPX - Compare X - Immediate */
            case 0x8C:
                compareWord(io.getWordRegister(Register.X), memoryResult.get());
                operationTicks = 4;
                break;

            /* BSR - Branch to Subroutine - Immediate */
            case 0x8D:
                io.pushStack(Register.S, io.getWordRegister(Register.PC));
                branchShort(memoryResult.get().getHigh());
                operationTicks = 7;
                break;

            /* LDX - Load X - Immediate */
            case 0x8E:
                loadRegister(Register.X, memoryResult.get());
                operationTicks = 3;
                break;

            /* SUBA - Subtract M from A - Direct */
            case 0x90:
                subtractM(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* CMPA - Compare A - Direct */
            case 0x91:
                compareByte(io.getByteRegister(Register.A), io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* SBCA - Subtract M and C from A - Direct */
            case 0x92:
                subtractMC(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* SUBD - Subtract M from D - Direct */
            case 0x93:
                subtractD(io.readWord(memoryResult.get()));
                operationTicks = 6;
                break;

            /* ANDA - Logical AND A - Direct */
            case 0x94:
                logicalAnd(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* BITA - Test A - Direct */
            case 0x95:
                test(new UnsignedByte(io.getByteRegister(Register.A).getShort() & io.readByte(memoryResult.get()).getShort()));
                operationTicks = 4;
                break;

            /* LDA - Load A - Direct */
            case 0x96:
                loadByteRegister(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 2;
                break;

            /* STA - Store A - Direct */
            case 0x97:
                storeByteRegister(Register.A, memoryResult.get());
                operationTicks = 4;
                break;

            /* EORA - Exclusive OR A - Direct */
            case 0x98:
                exclusiveOr(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* ADCA - Add with Carry A - Direct */
            case 0x99:
                addWithCarry(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* ORA - Logical OR A - Direct */
            case 0x9A:
                logicalOr(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* ADDA - Add A - Direct */
            case 0x9B:
                addByteRegister(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* CMPX - Compare X - Direct */
            case 0x9C:
                compareWord(io.getWordRegister(Register.X), io.readWord(memoryResult.get()));
                operationTicks = 6;
                break;

            /* JSR - Jump to Subroutine - Direct */
            case 0x9D:
                jumpToSubroutine(memoryResult.get());
                operationTicks = 7;
                break;

            /* LDX - Load X - Direct */
            case 0x9E:
                loadRegister(Register.X, io.readWord(memoryResult.get()));
                operationTicks = 5;
                break;

            /* STX - Store X - Direct */
            case 0x9F:
                storeWordRegister(Register.X, memoryResult.get());
                operationTicks = 5;
                break;

            /* SUBA - Subtract M from A - Indexed */
            case 0xA0:
                subtractM(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* CMPA - Compare A - Indexed */
            case 0xA1:
                compareByte(io.getByteRegister(Register.A), io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* SBCA - Subtract M and C from A - Indexed */
            case 0xA2:
                subtractMC(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* SUBD - Subtract M from D - Indexed */
            case 0xA3:
                subtractD(io.readWord(memoryResult.get()));
                operationTicks = 4 + memoryResult.getBytesConsumed();
                break;

            /* ANDA - Logical AND A - Indexed */
            case 0xA4:
                logicalAnd(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* BITA - Test A - Indexed */
            case 0xA5:
                test(new UnsignedByte(io.getByteRegister(Register.A).getShort() & io.readByte(memoryResult.get()).getShort()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* LDA - Load A - Indexed */
            case 0xA6:
                loadByteRegister(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* STA - Store A - Indexed */
            case 0xA7:
                storeByteRegister(Register.A, memoryResult.get());
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* EORA - Exclusive OR A - Indexed */
            case 0xA8:
                exclusiveOr(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* ADCA - Add with Carry A - Indexed */
            case 0xA9:
                addWithCarry(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* ORA - Logical OR A - Indexed */
            case 0xAA:
                logicalOr(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* ADDA - Add A - Indexed */
            case 0xAB:
                addByteRegister(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* CMPX - Compare X - Indexed */
            case 0xAC:
                compareWord(io.getWordRegister(Register.X), io.readWord(memoryResult.get()));
                operationTicks = 4 + memoryResult.getBytesConsumed();
                break;

            /* JSR - Jump to Subroutine - Indexed */
            case 0xAD:
                jumpToSubroutine(memoryResult.get());
                operationTicks = 5 + memoryResult.getBytesConsumed();
                break;

            /* LDX - Load X - Indexed */
            case 0xAE:
                loadRegister(Register.X, io.readWord(memoryResult.get()));
                operationTicks = 3 + memoryResult.getBytesConsumed();
                break;

            /* STX - Store X - Indexed */
            case 0xAF:
                storeWordRegister(Register.X, memoryResult.get());
                operationTicks = 3 + memoryResult.getBytesConsumed();
                break;

            /* SUBA - Subtract M from A - Extended */
            case 0xB0:
                subtractM(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* CMPA - Compare A - Extended */
            case 0xB1:
                compareByte(io.getByteRegister(Register.A), io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* SBCA - Subtract M and C from A - Extended */
            case 0xB2:
                subtractMC(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* SUBD - Subtract M from D - Extended */
            case 0xB3:
                subtractD(io.readWord(memoryResult.get()));
                operationTicks = 7;
                break;

            /* ANDA - Logical AND A - Extended */
            case 0xB4:
                logicalAnd(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* BITA - Test A - Extended */
            case 0xB5:
                test(new UnsignedByte(io.getByteRegister(Register.A).getShort() & io.readByte(memoryResult.get()).getShort()));
                operationTicks = 5;
                break;

            /* LDA - Load A - Extended */
            case 0xB6:
                loadByteRegister(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* STA - Store A - Extended */
            case 0xB7:
                storeByteRegister(Register.A, memoryResult.get());
                operationTicks = 5;
                break;

            /* EORA - Exclusive A - Extended */
            case 0xB8:
                exclusiveOr(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* ADCA - Add with Carry A - Extended */
            case 0xB9:
                addWithCarry(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* ORA - Logical OR A - Extended */
            case 0xBA:
                logicalOr(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* ADDA - Add A - Extended */
            case 0xBB:
                addByteRegister(Register.A, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* CMPX - Compare X - Extended */
            case 0xBC:
                compareWord(io.getWordRegister(Register.X), io.readWord(memoryResult.get()));
                operationTicks = 7;
                break;

            /* JSR - Jump to Subroutine - Extended */
            case 0xBD:
                jumpToSubroutine(memoryResult.get());
                operationTicks = 8;
                break;

            /* LDX - Load X - Extended */
            case 0xBE:
                loadRegister(Register.X, io.readWord(memoryResult.get()));
                operationTicks = 6;
                break;

            /* STX - Store X - Extended */
            case 0xBF:
                storeWordRegister(Register.X, memoryResult.get());
                operationTicks = 6;
                break;

            /* SUBB - Subtract M from B - Immediate */
            case 0xC0:
                subtractM(Register.B, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* CMPB - Compare B - Immediate */
            case 0xC1:
                compareByte(io.getByteRegister(Register.B), memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* SBCB - Subtract M and C from B - Immediate */
            case 0xC2:
                subtractMC(Register.B, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* ADDD - Add D - Immediate */
            case 0xC3:
                addD(memoryResult.get());
                operationTicks = 4;
                break;

            /* ANDB - Logical AND B - Immediate */
            case 0xC4:
                logicalAnd(Register.B, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* BITB - Test B - Immediate */
            case 0xC5:
                test(new UnsignedByte(io.getByteRegister(Register.B).getShort() & memoryResult.get().getHigh().getShort()));
                operationTicks = 2;
                break;

            /* LDB - Load B - Immediate */
            case 0xC6:
                loadByteRegister(Register.B, memoryResult.get().getHigh());
                operationTicks = 4;
                break;

            /* EORB - Exclusive OR B - Immediate */
            case 0xC8:
                exclusiveOr(Register.B, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* ADCB - Add with Carry B - Immediate */
            case 0xC9:
                addWithCarry(Register.B, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* ORB - Logical OR B - Immediate */
            case 0xCA:
                logicalOr(Register.B, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* ADDB - Add B - Immediate */
            case 0xCB:
                addByteRegister(Register.B, memoryResult.get().getHigh());
                operationTicks = 2;
                break;

            /* LDD - Load D - Immediate */
            case 0xCC:
                loadRegister(Register.D, memoryResult.get());
                operationTicks = 3;
                break;

            /* LDU - Load U - Immediate */
            case 0xCE:
                loadRegister(Register.U, memoryResult.get());
                operationTicks = 3;
                break;

            /* SUBB - Subtract M from B - Direct */
            case 0xD0:
                subtractM(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* CMPB - Compare B - Direct */
            case 0xD1:
                compareByte(io.getByteRegister(Register.B), io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* SBCB - Subtract M and C from B - Direct */
            case 0xD2:
                subtractMC(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* ADDD - Add D - Direct */
            case 0xD3:
                addD(io.readWord(memoryResult.get()));
                operationTicks = 6;
                break;

            /* ANDB - Logical AND B - Direct */
            case 0xD4:
                logicalAnd(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* BITB - Test B - Direct */
            case 0xD5:
                test(new UnsignedByte(io.getByteRegister(Register.B).getShort() & io.readByte(memoryResult.get()).getShort()));
                operationTicks = 4;
                break;

            /* LDB - Load B - Direct */
            case 0xD6:
                loadByteRegister(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 2;
                break;

            /* STB - Store B - Direct */
            case 0xD7:
                storeByteRegister(Register.B, memoryResult.get());
                operationTicks = 2;
                break;

            /* EORB - Exclusive OR B - Direct */
            case 0xD8:
                exclusiveOr(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* ADCB - Add with Carry B - Direct */
            case 0xD9:
                addWithCarry(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* ORB - Logical OR B - Direct */
            case 0xDA:
                logicalOr(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* ADDB - Add B - Direct */
            case 0xDB:
                addByteRegister(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* LDD - Load - Direct */
            case 0xDC:
                loadRegister(Register.D, io.readWord(memoryResult.get()));
                operationTicks = 5;
                break;

            /* STD - Store D - Direct */
            case 0xDD:
                storeWordRegister(Register.D, memoryResult.get());
                operationTicks = 5;
                break;

            /* LDU - Load U - Direct */
            case 0xDE:
                loadRegister(Register.U, io.readWord(memoryResult.get()));
                operationTicks = 5;
                break;

            /* STU - Store U - Direct */
            case 0xDF:
                storeWordRegister(Register.U, memoryResult.get());
                operationTicks = 5;
                break;

            /* SUBB - Subtract M from B - Indexed */
            case 0xE0:
                subtractM(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* CMPB - Compare B - Indexed */
            case 0xE1:
                compareByte(io.getByteRegister(Register.B), io.readByte(memoryResult.get()));
                operationTicks = 4;
                break;

            /* SBCB - Subtract M and C from B - Indexed */
            case 0xE2:
                subtractMC(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* ADDD - Add D - Indexed */
            case 0xE3:
                addD(io.readWord(memoryResult.get()));
                operationTicks = 6 + memoryResult.getBytesConsumed();
                break;

            /* ANDB - Logical AND B - Indexed */
            case 0xE4:
                logicalAnd(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* BITB - Test B - Indexed */
            case 0xE5:
                test(new UnsignedByte(io.getByteRegister(Register.B).getShort() & io.readByte(memoryResult.get()).getShort()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* LDB - Load B - Indexed */
            case 0xE6:
                loadByteRegister(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* STB - Store B - Indexed */
            case 0xE7:
                storeByteRegister(Register.B, memoryResult.get());
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* EORB - Exclusive OR B - Indexed */
            case 0xE8:
                exclusiveOr(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* ADCB - Add with Carry B - Indexed */
            case 0xE9:
                addWithCarry(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* ORB - Logical OR B - Indexed */
            case 0xEA:
                logicalOr(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* ADDB - Add B - Indexed */
            case 0xEB:
                addByteRegister(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* LDD - Load D - Indexed */
            case 0xEC:
                loadRegister(Register.D, io.readWord(memoryResult.get()));
                operationTicks = 3 + memoryResult.getBytesConsumed();
                break;

            /* STD - Store D - Indexed */
            case 0xED:
                storeWordRegister(Register.D, memoryResult.get());
                operationTicks = 3 + memoryResult.getBytesConsumed();
                break;

            /* LDU - Load U - Indexed */
            case 0xEE:
                loadRegister(Register.U, io.readWord(memoryResult.get()));
                operationTicks = 3 + memoryResult.getBytesConsumed();
                break;

            /* STU - Store U - Indexed */
            case 0xEF:
                storeWordRegister(Register.U, memoryResult.get());
                operationTicks = 3 + memoryResult.getBytesConsumed();
                break;

            /* SUBB - Subtract M from B - Extended */
            case 0xF0:
                subtractM(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 2 + memoryResult.getBytesConsumed();
                break;

            /* CMPB - Compare B - Extended */
            case 0xF1:
                compareByte(io.getByteRegister(Register.B), io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* SBCB - Subtract M and C from B - Extended */
            case 0xF2:
                subtractMC(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* ADDD - Add D - Extended */
            case 0xF3:
                addD(io.readWord(memoryResult.get()));
                operationTicks = 7;
                break;

            /* ANDB - Logical AND B - Extended */
            case 0xF4:
                logicalAnd(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* BITB - Test B - Extended */
            case 0xF5:
                test(new UnsignedByte(io.getByteRegister(Register.B).getShort() & io.readByte(memoryResult.get()).getShort()));
                operationTicks = 5;
                break;

            /* LDB - Load B - Extended */
            case 0xF6:
                loadByteRegister(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* STB - Store B - Extended */
            case 0xF7:
                storeByteRegister(Register.B, memoryResult.get());
                operationTicks = 5;
                break;

            /* EORB - Exclusive OR B - Extended */
            case 0xF8:
                exclusiveOr(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* ADCB - Add with Carry B - Extended */
            case 0xF9:
                addWithCarry(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* ORB - Logical OR B - Extended */
            case 0xFA:
                logicalOr(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* ADDB - Add B - Extended */
            case 0xFB:
                addByteRegister(Register.B, io.readByte(memoryResult.get()));
                operationTicks = 5;
                break;

            /* LDD - Load D - Extended */
            case 0xFC:
                loadRegister(Register.D, io.readWord(memoryResult.get()));
                operationTicks = 6;
                break;

            /* STD - Store D - Extended */
            case 0xFD:
                storeWordRegister(Register.D, memoryResult.get());
                operationTicks = 6;
                break;

            /* LDU - Load U - Extended */
            case 0xFE:
                loadRegister(Register.U, io.readWord(memoryResult.get()));
                operationTicks = 6;
                break;

            /* STD - Store U - Extended */
            case 0xFF:
                storeWordRegister(Register.U, memoryResult.get());
                operationTicks = 6;
                break;

            default:
                throw new RuntimeException("Un-implemented OP code " + operand);
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
        UnsignedWord address = memoryResult.get();
        UnsignedByte tempByte = io.readByte(address);
        tempByte = function.apply(tempByte);
        io.writeByte(address, tempByte);
    }

    /**
     * Inverts all bits in the byte. Returns the complimented value as the
     * result.
     *
     * @param value the UnsignedByte to complement
     * @return the complimented value
     */
    public UnsignedByte compliment(UnsignedByte value) {
        opLongDesc += "R=" + value + ", ";
        UnsignedByte result = new UnsignedByte(~(value.getShort()));
        opLongDesc += "R'=" + result;
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_Z | IOController.CC_V));
        cc.or(IOController.CC_C);
        cc.or(result.isNegative() ? IOController.CC_N : 0);
        cc.or(result.isZero() ? IOController.CC_Z : 0);
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
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_Z | IOController.CC_V | IOController.CC_C));

        /* Exception cases */
        if (value.equals(new UnsignedByte(0x80))) {
            result = new UnsignedByte(0x80);
            cc.or(IOController.CC_V);
            cc.or(IOController.CC_N);
            cc.or(IOController.CC_C);
        } else if (value.equals(new UnsignedByte(0x0))) {
            result = new UnsignedByte(0x0);
            cc.or(IOController.CC_Z);
        } else {
            cc.or(result.isMasked(0x80) ? IOController.CC_V : 0);
            cc.or(result.isNegative() ? IOController.CC_N : 0);
            cc.or(IOController.CC_C);
        }
        return result;
    }

    /**
     * Shifts all the bits in the byte to the right by one bit. Returns the
     * result of the operation, while impacting the condition code register.
     * The lowest bit of the byte is shifted into the condition code carry
     * bit.
     *
     * @param value the UnsignedByte to operate on
     * @return the shifted byte value
     */
    public UnsignedByte logicalShiftRight(UnsignedByte value) {
        opLongDesc = "R=" + value + ", C=" + io.ccCarrySet() + ", ";
        UnsignedByte result = new UnsignedByte(value.getShort() >> 1);
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_Z | IOController.CC_C));
        cc.or(value.isMasked(0x1) ? IOController.CC_C : 0);
        cc.or(result.isZero() ? IOController.CC_Z : 0);
        opLongDesc += "R'=" + result + ", C'=" + io.ccCarrySet();
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
        opLongDesc = "M=" + value + ", C=" + io.ccCarrySet() + ", ";
        UnsignedByte result = new UnsignedByte(value.getShort() >> 1);
        UnsignedByte cc = io.getCC();
        result.add(io.ccCarrySet() ? 0x80 : 0x0);
        cc.and(~(IOController.CC_N | IOController.CC_Z | IOController.CC_C));
        cc.or(value.isMasked(0x1) ? IOController.CC_C : 0);
        cc.or(result.isZero() ? IOController.CC_Z : 0);
        cc.or(result.isNegative() ? IOController.CC_N : 0);
        opLongDesc += "M'=" + result + ", C'=" + io.ccCarrySet();
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
        UnsignedByte cc = io.getCC();
        result.add(value.isMasked(0x80) ? 0x80 : 0);
        cc.and(~(IOController.CC_N | IOController.CC_Z | IOController.CC_C));
        cc.or(value.isMasked(0x1) ? IOController.CC_C : 0);
        cc.or(result.isZero() ? IOController.CC_Z : 0);
        cc.or(result.isNegative() ? IOController.CC_N : 0);
        opLongDesc = "M=" + value + ", M'=" + result + ", C'=" + (io.ccCarrySet() ? 1 : 0);
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
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_Z | IOController.CC_V | IOController.CC_C));
        cc.or(value.isMasked(0x80) ? IOController.CC_C : 0);
        boolean bit7 = value.isMasked(0x80);
        boolean bit6 = value.isMasked(0x40);
        cc.or(bit7 ^ bit6 ? IOController.CC_V : 0);
        cc.or(result.isZero() ? IOController.CC_Z : 0);
        cc.or(result.isNegative() ? IOController.CC_N : 0);
        opLongDesc = "M=" + value + ", M'=" + result + ", C'=" + (io.ccCarrySet() ? 1 : 0);
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
        opLongDesc = "M=" + value + ", C=" + io.ccCarrySet() + ", ";
        UnsignedByte result = new UnsignedByte(value.getShort() << 1);
        UnsignedByte cc = io.getCC();
        result.add(io.ccCarrySet() ? 0x1 : 0x0);
        cc.and(~(IOController.CC_N | IOController.CC_Z | IOController.CC_C | IOController.CC_V));
        cc.or(value.isMasked(0x80) ? IOController.CC_C : 0);
        boolean bit7 = value.isMasked(0x80);
        boolean bit6 = value.isMasked(0x40);
        cc.or(IOController.CC_V);
        if (bit7 == bit6) {
            cc.and(~IOController.CC_V);
        }
        cc.or(result.isZero() ? IOController.CC_Z : 0);
        cc.or(result.isNegative() ? IOController.CC_N : 0);
        opLongDesc += "M'=" + result + ", C'=" + io.ccCarrySet();
        return result;
    }

    /**
     * Decrements the byte value by one.
     *
     * @param value the byte value to decrement
     * @return the decremented byte value
     */
    public UnsignedByte decrement(UnsignedByte value) {
        UnsignedByte result = io.binaryAdd(value, new UnsignedByte(0xFF), false, false, false);
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_Z | IOController.CC_V));
        cc.or(value.isZero() ? IOController.CC_V : 0);
        cc.or(result.isZero() ? IOController.CC_Z : 0);
        cc.or(result.isNegative() ? IOController.CC_N : 0);
        opLongDesc = "M'=" + result;
        return result;
    }

    /**
     * Increments the byte value by one.
     *
     * @param value the byte value to increment
     * @return the incremented byte value
     */
    public UnsignedByte increment(UnsignedByte value) {
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_Z | IOController.CC_V));
        UnsignedByte result = io.binaryAdd(value, new UnsignedByte(0x1), false, false, true);
        cc.or(result.isZero() ? IOController.CC_Z : 0);
        cc.or(result.isNegative() ? IOController.CC_N : 0);
        opLongDesc = "M'=" + result;
        return result;
    }

    /**
     * Tests the byte for zero condition or negative condition.
     *
     * @param value the byte value to test
     * @return the original byte value
     */
    public UnsignedByte test(UnsignedByte value) {
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_Z | IOController.CC_V));
        cc.or(value.isZero() ? IOController.CC_Z : 0);
        cc.or(value.isNegative() ? IOController.CC_N : 0);
        opLongDesc = "M=" + value + ", Z=" + (value.isZero() ? 1 : 0) + ", N=" + (value.isNegative() ? 1 : 0);
        return value;
    }

    /**
     * Jumps to the specified address.
     *
     * @param address the address to jump to
     */
    public void jump(UnsignedWord address) {
        io.getWordRegister(Register.PC).set(address);
        opLongDesc += "PC'=" + io.getWordRegister(Register.PC);
    }

    /**
     * Jumps to the specified address, pushing the value of the PC onto the S
     * stack before jumping.
     *
     * @param address the address to jump to
     */
    public void jumpToSubroutine(UnsignedWord address) {
        UnsignedWord pc = io.getWordRegister(Register.PC);
        opLongDesc = "S[" + io.getWordRegister(Register.S) + "]=" + pc + ", ";
        io.pushStack(Register.S, pc);
        pc.set(address);
        opLongDesc += "PC'=" + io.getWordRegister(Register.PC);
    }

    /**
     * Clears the specified byte.
     *
     * @param value the value to sg4ClearScreen
     * @return the cleared byte
     */
    public UnsignedByte clear(UnsignedByte value) {
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_C | IOController.CC_V));
        cc.or(IOController.CC_Z);
        opLongDesc = "M'=" + new UnsignedByte(0);
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
        io.getWordRegister(Register.PC).add(offset.isNegative() ? offset.getSignedInt() : offset.getInt());
        opLongDesc += ", PC'=" + io.getWordRegister(Register.PC);
    }

    /**
     * Increments (or decrements) the program counter by the specified amount.
     * Will interpret the UnsignedByte offset as a negative value if its high
     * bit is set.
     *
     * @param offset the amount to offset the program counter
     */
    public void branchShort(UnsignedByte offset) {
        io.getWordRegister(Register.PC).add(offset.isNegative() ? offset.getSignedShort() : offset.getShort());
        opLongDesc += "PC'=" + io.getWordRegister(Register.PC);
    }

    /**
     * Saves all registers to the stack, and jumps to the memory location
     * read at the specified address.
     *
     * @param offset the offset to read for a jump address
     */
    public void softwareInterrupt(UnsignedWord offset) {
        io.setCCEverything();
        UnsignedWord pc = io.getWordRegister(Register.PC);
        io.pushStack(Register.S, pc);
        io.pushStack(Register.S, io.getWordRegister(Register.U));
        io.pushStack(Register.S, io.getWordRegister(Register.Y));
        io.pushStack(Register.S, io.getWordRegister(Register.X));
        io.pushStack(Register.S, io.getByteRegister(Register.DP));
        io.pushStack(Register.S, io.getByteRegister(Register.B));
        io.pushStack(Register.S, io.getByteRegister(Register.A));
        io.pushStack(Register.S, io.getByteRegister(Register.CC));
        pc.set(io.readWord(offset));
    }

    /**
     * Performs an Interrupt Request (IRQ). Will save the PC, U, Y,
     * X, DP, B, A and CC registers on the stack, and jump to the address
     * stored at $FFF8.
     */
    public void interruptRequest() {
        UnsignedWord pc = io.getWordRegister(Register.PC);
        UnsignedByte cc = io.getCC();
        io.pushStack(Register.S, pc);
        io.pushStack(Register.S, io.getWordRegister(Register.U));
        io.pushStack(Register.S, io.getWordRegister(Register.Y));
        io.pushStack(Register.S, io.getWordRegister(Register.X));
        io.pushStack(Register.S, io.getByteRegister(Register.DP));
        io.pushStack(Register.S, io.getByteRegister(Register.B));
        io.pushStack(Register.S, io.getByteRegister(Register.A));
        cc.or(IOController.CC_E);
        io.pushStack(Register.S, cc);
        io.setCCInterrupt();
        io.setPC(io.readWord(new UnsignedWord(0xFFF8)));
    }

    /**
     * Performs a Fast Interrupt Request (FIRQ). Will save the PC and
     * CC registers on the stack, and jump to the address stored at
     * $FFF6.
     */
    public void fastInterruptRequest() {
        UnsignedWord pc = io.getWordRegister(Register.PC);
        UnsignedByte cc = io.getCC();
        io.pushStack(Register.S, pc);
        cc.and(~IOController.CC_E);
        io.pushStack(Register.S, cc);
        io.setCCFastInterrupt();
        io.setCCInterrupt();
        io.setPC(io.readWord(new UnsignedWord(0xFFF6)));
    }

    /**
     * Performs a Non Maskable Interrupt Request (NMI). Will save the PC, U, Y,
     * X, DP, B, A and CC registers on the stack, and jump to the address
     * stored at $FFFC.
     */
    public void nonMaskableInterruptRequest() {
        UnsignedWord pc = io.getWordRegister(Register.PC);
        UnsignedByte cc = io.getCC();
        io.pushStack(Register.S, pc);
        io.pushStack(Register.S, io.getWordRegister(Register.U));
        io.pushStack(Register.S, io.getWordRegister(Register.Y));
        io.pushStack(Register.S, io.getWordRegister(Register.X));
        io.pushStack(Register.S, io.getByteRegister(Register.DP));
        io.pushStack(Register.S, io.getByteRegister(Register.B));
        io.pushStack(Register.S, io.getByteRegister(Register.A));
        cc.or(IOController.CC_E);
        io.pushStack(Register.S, cc);
        io.setCCInterrupt();
        io.setCCFastInterrupt();
        io.setPC(io.readWord(new UnsignedWord(0xFFFC)));
    }

    /**
     * Compares the two words and sets the appropriate register sets.
     *
     * @param word1 the first word to compare
     * @param word2 the second word to compare
     */
    public UnsignedWord compareWord(UnsignedWord word1, UnsignedWord word2) {
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_Z | IOController.CC_V | IOController.CC_C));
        UnsignedWord result = io.binaryAdd(word1, word2.twosCompliment(), false, false, true);
        cc.or(word1.getInt() < word2.getInt() ? IOController.CC_C : 0);
        cc.or(result.isZero() ? IOController.CC_Z : 0);
        cc.or(result.isNegative() ? IOController.CC_N : 0);
        opLongDesc = word1 + " vs " + word2 + ", N=" + io.ccNegativeSet() + ", C=" + io.ccCarrySet() + ", V=" + io.ccOverflowSet();
        return result;
    }

    /**
     * Compares the two bytes and sets the appropriate register sets.
     *
     * @param byte1 the first byte to compare
     * @param byte2 the second byte to compare
     */
    public void compareByte(UnsignedByte byte1, UnsignedByte byte2) {
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_Z | IOController.CC_V | IOController.CC_C));
        UnsignedByte result = io.binaryAdd(byte1, byte2.twosCompliment(), false, false, true);
        cc.or(byte1.getShort() < byte2.getShort() ? IOController.CC_C : 0);
        cc.or(result.isZero() ? IOController.CC_Z : 0);
        cc.or(result.isNegative() ? IOController.CC_N : 0);
        opLongDesc = byte1 + " vs " + byte2;
    }

    /**
     * Loads the word into the specified register.
     *
     * @param registerFlag the register to load
     * @param value the value to load
     */
    public void loadRegister(Register registerFlag, UnsignedWord value) {
        UnsignedWord register = io.getWordRegister(registerFlag);
        UnsignedByte cc = io.getCC();

        /* D is a special register */
        if (registerFlag == Register.D) {
            io.setD(value);
            register = io.getWordRegister(Register.D);
        } else {
            register.set(value);
        }

        cc.and(~(IOController.CC_V | IOController.CC_N | IOController.CC_Z));
        cc.or(register.isZero() ? IOController.CC_Z : 0);
        cc.or(register.isNegative() ? IOController.CC_N : 0);
        opLongDesc = registerFlag + "'=" + value;
    }

    /**
     * Stores the register in the memory location.
     *
     * @param registerFlag the register to store
     * @param address the memory location to write to
     */
    public void storeWordRegister(Register registerFlag, UnsignedWord address) {
        UnsignedWord register = io.getWordRegister(registerFlag);
        UnsignedByte cc = io.getCC();
        io.writeWord(address, register);
        cc.and(~(IOController.CC_V | IOController.CC_N | IOController.CC_Z));
        cc.or(register.isZero() ? IOController.CC_Z : 0);
        cc.or(register.isNegative() ? IOController.CC_N : 0);
        opLongDesc = "M[" + address + "]'=" + register;
    }

    /**
     * Stores the byte register in the memory location.
     *
     * @param registerFlag the byte register to store
     * @param address the memory location to write to
     */
    public void storeByteRegister(Register registerFlag, UnsignedWord address) {
        UnsignedByte register = io.getByteRegister(registerFlag);
        UnsignedByte cc = io.getCC();
        io.writeByte(address, register);
        cc.and(~(IOController.CC_V | IOController.CC_N | IOController.CC_Z));
        cc.or(register.isZero() ? IOController.CC_Z : 0);
        cc.or(register.isNegative() ? IOController.CC_N : 0);
        opLongDesc = "M[" + address + "]'=" + register;
    }

    /**
     * Performs a correction to the A register to transform the value into
     * a proper BCD form.
     */
    public void decimalAdditionAdjust() {
        UnsignedByte cc = io.getByteRegister(Register.CC);
        UnsignedByte a = io.getByteRegister(Register.A);
        int value = io.getByteRegister(Register.A).getShort();
        int mostSignificantNibble = value & 0xF0;
        int leastSignificantNibble = value & 0x0F;
        int adjustment = 0;

        if (io.ccCarrySet() || mostSignificantNibble > 0x90 || (mostSignificantNibble > 0x80 && leastSignificantNibble > 0x09)) {
            adjustment |= 0x60;
        }

        if (io.ccHalfCarrySet() || leastSignificantNibble > 0x09) {
            adjustment |= 0x06;
        }

        mostSignificantNibble = cc.getShort() & IOController.CC_C;
        UnsignedByte result = new UnsignedByte(mostSignificantNibble + adjustment);

        a.set(io.binaryAdd(a, result, false, true, false));
        cc.and(~(IOController.CC_C | IOController.CC_N | IOController.CC_Z));
        cc.or(io.getByteRegister(Register.A).isZero() ? IOController.CC_Z : 0);
        cc.or(!result.isZero() ? IOController.CC_C : 0);
        cc.or(result.isNegative() ? IOController.CC_N : 0);
    }

    /**
     * Loads the specified value into the specified register.
     *
     * @param register the register to load into
     * @param value the value to load
     */
    public void loadEffectiveAddress(Register register, UnsignedWord value) {
        UnsignedWord reg = io.getWordRegister(register);
        UnsignedByte cc = io.getCC();
        reg.set(value);
        if (register == Register.X || register == Register.Y) {
            cc.and(~(IOController.CC_Z));
            cc.or(reg.isZero() ? IOController.CC_Z : 0);
        }
        opLongDesc = register + "'=" + value;
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
            io.pushStack(register, io.getWordRegister(Register.PC));
            bytes += 2;
            opLongDesc += "PC ";
        }

        if (postByte.isMasked(0x40)) {
            io.pushStack(register, io.getWordRegister(Register.U));
            bytes += 2;
            opLongDesc += "U ";
        }

        if (postByte.isMasked(0x20)) {
            io.pushStack(register, io.getWordRegister(Register.Y));
            bytes += 2;
            opLongDesc += "Y ";
        }

        if (postByte.isMasked(0x10)) {
            io.pushStack(register, io.getWordRegister(Register.X));
            bytes += 2;
            opLongDesc += "X ";
        }

        if (postByte.isMasked(0x08)) {
            io.pushStack(register, io.getByteRegister(Register.DP));
            bytes += 1;
            opLongDesc += "DP ";
        }

        if (postByte.isMasked(0x04)) {
            io.pushStack(register, io.getByteRegister(Register.B));
            bytes += 1;
            opLongDesc += "B ";
        }

        if (postByte.isMasked(0x02)) {
            io.pushStack(register, io.getByteRegister(Register.A));
            bytes += 1;
            opLongDesc += "A ";
        }

        if (postByte.isMasked(0x01)) {
            io.pushStack(register, io.getByteRegister(Register.CC));
            bytes += 1;
            opLongDesc += "CC ";
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
    public int  popStack(Register register, UnsignedByte postByte) {
        int bytes = 0;

        if (postByte.isMasked(0x01)) {
            UnsignedByte cc = io.getByteRegister(Register.CC);
            cc.set(io.popStack(register));
            bytes += 1;
            opLongDesc += "CC ";
        }

        if (postByte.isMasked(0x02)) {
            UnsignedByte a = io.getByteRegister(Register.A);
            a.set(io.popStack(register));
            bytes += 1;
            opLongDesc += "A ";
        }

        if (postByte.isMasked(0x04)) {
            UnsignedByte b = io.getByteRegister(Register.B);
            b.set(io.popStack(register));
            bytes += 1;
            opLongDesc += "B ";
        }

        if (postByte.isMasked(0x08)) {
            UnsignedByte dp = io.getByteRegister(Register.DP);
            dp.set(io.popStack(register));
            bytes += 1;
            opLongDesc += "DP ";
        }

        if (postByte.isMasked(0x10)) {
            UnsignedWord x = io.getWordRegister(Register.X);
            x.set(
                    new UnsignedWord(
                            io.popStack(register),
                            io.popStack(register)
                    )
            );
            bytes += 2;
            opLongDesc += "X ";
        }

        if (postByte.isMasked(0x20)) {
            UnsignedWord y = io.getWordRegister(Register.Y);
            y.set(
                    new UnsignedWord(
                            io.popStack(register),
                            io.popStack(register)
                    )
            );
            bytes += 2;
            opLongDesc += "Y ";
        }

        if (postByte.isMasked(0x40)) {
            UnsignedWord u = io.getWordRegister(Register.U);
            u.set(
                    new UnsignedWord(
                            io.popStack(register),
                            io.popStack(register)
                    )
            );
            bytes += 2;
            opLongDesc += "U ";
        }

        if (postByte.isMasked(0x80)) {
            UnsignedWord pc = io.getWordRegister(Register.PC);
            pc.set(
                    new UnsignedWord(
                            io.popStack(register),
                            io.popStack(register)
                    )
            );
            bytes += 2;
            opLongDesc += "PC";
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
        UnsignedByte reg = io.getByteRegister(register);
        UnsignedByte cc = io.getCC();
        opLongDesc = register + "=" + reg + ", M=" + value;
        cc.and(~(IOController.CC_N | IOController.CC_V | IOController.CC_Z | IOController.CC_C));
        cc.or(reg.getShort() < value.getShort() ? IOController.CC_C : 0);
        reg.set(io.binaryAdd(reg, value.twosCompliment(), false, false, true));
        cc.or(reg.isZero() ? IOController.CC_Z : 0);
        cc.or(reg.isNegative() ? IOController.CC_N : 0);
        opLongDesc += ", " + register + "'=" + reg + ", C'=" + (io.ccCarrySet() ? 1 : 0);
    }

    /**
     * Subtracts the word value from the D register.
     *
     * @param value the word value to subtract
     */
    public void subtractD(UnsignedWord value) {
        UnsignedWord d = io.getWordRegister(Register.D);
        UnsignedByte cc = io.getCC();
        opLongDesc = "D=" + d + ", M=" + value + ", D-M=";
        cc.and(~(IOController.CC_N | IOController.CC_V | IOController.CC_Z | IOController.CC_C));
        cc.or(d.getInt() < value.getInt() ? IOController.CC_C : 0);
        io.setD(io.binaryAdd(d, value.twosCompliment(), false, false, true));
        d = io.getWordRegister(Register.D);
        cc.or(d.isZero() ? IOController.CC_Z : 0);
        cc.or(d.isNegative() ? IOController.CC_N : 0);
        opLongDesc += d;
    }

    /**
     * Subtracts the byte value and sets carry if required.
     *
     * @param register the register to subtract from
     * @param value the byte value to subtract
     */
    public void subtractMC(Register register, UnsignedByte value) {
        UnsignedByte reg = io.getByteRegister(register);
        UnsignedByte cc = io.getCC();
        opLongDesc = register + "=" + reg + ", M=" + value + ", C=" + io.ccCarrySet() + ", " + register + "-M-C=";
        value.add(io.ccCarrySet() ? 1 : 0);
        cc.and(~(IOController.CC_N | IOController.CC_V | IOController.CC_Z | IOController.CC_C));
        cc.or(reg.getShort() < value.getShort() ? IOController.CC_C : 0);
        reg.set(io.binaryAdd(reg, value.twosCompliment(), false, false, true));
        cc.or(reg.isZero() ? IOController.CC_Z : 0);
        cc.or(reg.isNegative() ? IOController.CC_N : 0);
        opLongDesc += reg;
    }

    /**
     * Performs a logical AND of the byte register and the value.
     *
     * @param register the register to AND
     * @param value the byte value to AND
     */
    public void logicalAnd(Register register, UnsignedByte value) {
        UnsignedByte reg = io.getByteRegister(register);
        opLongDesc = register + "=" + reg + ", M=" + value + ", ";
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_V | IOController.CC_Z));
        reg.and(value.getShort());
        cc.or(reg.isZero() ? IOController.CC_Z : 0);
        cc.or(reg.isNegative() ? IOController.CC_N : 0);
        opLongDesc += register + "'=" + reg;
    }

    /**
     * Performs a logical OR of the byte register and the value.
     *
     * @param register the register to OR
     * @param value the value to OR
     */
    public void logicalOr(Register register, UnsignedByte value) {
        UnsignedByte reg = io.getByteRegister(register);
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_V | IOController.CC_Z));
        reg.or(value.getShort());
        cc.or(reg.isZero() ? IOController.CC_Z : 0);
        cc.or(reg.isNegative() ? IOController.CC_N : 0);
        opLongDesc = register + "'=" + reg;
    }

    /**
     * Performs an exclusive OR of the register and the byte value.
     *
     * @param register the register to XOR
     * @param value the byte value to XOR
     */
    public void exclusiveOr(Register register, UnsignedByte value) {
        UnsignedByte reg = io.getByteRegister(register);
        UnsignedByte cc = io.getCC();
        opLongDesc = register + "=" + reg + ", M=" + value + ", ";
        cc.and(~(IOController.CC_N | IOController.CC_V | IOController.CC_Z));
        reg.set(new UnsignedByte(reg.getShort() ^ value.getShort()));
        cc.or(reg.isZero() ? IOController.CC_Z : 0);
        cc.or(reg.isNegative() ? IOController.CC_N : 0);
        opLongDesc += register + "'=" + reg;
    }

    /**
     * Loads the specified register with the value.
     *
     * @param register the register to load
     * @param value the value to load
     */
    public void loadByteRegister(Register register, UnsignedByte value) {
        UnsignedByte reg = io.getByteRegister(register);
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_V | IOController.CC_Z));
        reg.set(value);
        cc.or(reg.isZero() ? IOController.CC_Z : 0);
        cc.or(reg.isNegative() ? IOController.CC_N : 0);
        opLongDesc = register + "'=" + value;
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
        UnsignedByte reg = io.getByteRegister(register);
        UnsignedByte cc = io.getCC();
        opLongDesc = register + "=" + reg + ", M=" + value + ", C=" + io.ccCarrySet() + ", " + register + "'=";
        boolean setCC = false;
        boolean setOverflow = false;
        int result = value.getShort() + (io.ccCarrySet() ? 1 : 0);
        cc.and(~(IOController.CC_N | IOController.CC_V | IOController.CC_Z | IOController.CC_C | IOController.CC_H));
        if (result > 255) {
            result &= 0xFF;
            setCC = true;
            setOverflow = true;
        }
        reg.set(io.binaryAdd(reg, new UnsignedByte(result), true, true, true));
        opLongDesc += reg;
        cc.or(setCC ? IOController.CC_C : 0);
        cc.or(reg.isZero() ? IOController.CC_Z : 0);
        cc.or(reg.isNegative() ? IOController.CC_N : 0);
        cc.or(setOverflow ? IOController.CC_V : 0);
    }

    /**
     * Adds the specified value to the specified register.
     *
     * @param register the register to add
     * @param value the value to add
     */
    public void addByteRegister(Register register, UnsignedByte value) {
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_V | IOController.CC_Z | IOController.CC_C | IOController.CC_H));
        UnsignedByte reg = io.getByteRegister(register);
        opLongDesc = register + "=" + reg + ", M=" + value + ", " + register + "'=";
        reg.set(io.binaryAdd(reg, value, true, true, true));
        opLongDesc += reg;
        cc.or(reg.isZero() ? IOController.CC_Z : 0);
        cc.or(reg.isNegative() ? IOController.CC_N : 0);
    }

    /**
     * Adds the specified value to the D register.
     *
     * @param value the value to add
     */
    public void addD(UnsignedWord value) {
        UnsignedWord d = io.getWordRegister(Register.D);
        opLongDesc = "D=" + d + ", M=" + value;
        UnsignedByte cc = io.getCC();
        cc.and(~(IOController.CC_N | IOController.CC_V | IOController.CC_Z | IOController.CC_C));
        io.setD(io.binaryAdd(d, value, false, true, true));
        d = io.getWordRegister(Register.D);
        cc.or(d.isZero() ? IOController.CC_Z : 0);
        cc.or(d.isNegative() ? IOController.CC_N : 0);
        opLongDesc += ", D'=" + d;
    }

    /**
     * Schedules an IRQ interrupt to occur.
     */
    public void scheduleIRQ() {
        fireIRQ = true;
    }

    public boolean IRQWaiting() {
        return fireIRQ;
    }

    public void clearIRQ() {
        fireIRQ = false;
    }

    public void serviceInterrupts() {
        if (fireIRQ) {
            interruptRequest();
            clearIRQ();
        }

        if (fireFIRQ) {
            fastInterruptRequest();
            clearFIRQ();
        }

        if (fireNMI) {
            nonMaskableInterruptRequest();
            clearNMI();
        }

    }

    /**
     * Schedules a fast interrupt to occur.
     */
    public void scheduleFIRQ() {
        fireFIRQ = true;
    }

    public boolean FIRQWaiting() {
        return fireFIRQ;
    }

    public void clearFIRQ() {
        fireFIRQ = false;
    }

    /**
     * Schedules a non-maskable interrupt to occur.
     */
    public void scheduleNMI() {
        fireNMI = true;
    }

    public boolean NMIWaiting() {
        return fireNMI;
    }

    public void clearNMI() {
        fireNMI = false;
    }

    public String getOpShortDesc() {
        String result = String.format("%-5s %s", lastOpCodeInfo[1], lastOpCodeInfo[2]);
        opShortDesc = result;
        if (!lastOpCodeInfo[3].equals("0")) {
            result += " [%04X]";
            opShortDesc = String.format(result, memoryResult.get().getInt());
        } else {
            opShortDesc = result + " [----]";
        }
        return opShortDesc;
    }

    public String getOpLongDesc() {
        return opLongDesc;
    }

    public UnsignedWord getLastPC() {
        return lastPC;
    }

    public UnsignedByte getLastOperand() {
        return lastOperand;
    }

    public void reset() {

    }
}
