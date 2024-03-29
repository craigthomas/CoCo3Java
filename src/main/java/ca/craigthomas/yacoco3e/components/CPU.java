/*
 * Copyright (C) 2022 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;
import static ca.craigthomas.yacoco3e.datatypes.AddressingMode.*;

/**
 * Implements an MC6809E microprocessor.
 */
public class CPU
{
    /* CPU Internal Variables */
    private UnsignedWord lastPC;
    private UnsignedByte lastOperand;
    private final IOController io;
    public Instruction instruction;

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
    public int executeInstruction() throws MalformedInstructionException {
        MemoryResult memoryResult;
        UnsignedWord opWord = io.readWord(io.regs.pc);
        UnsignedByte operand = opWord.getHigh();
        lastPC = io.regs.pc.copy();
        lastOperand = operand.copy();
        io.incrementPC();
        instruction = InstructionTable.get(opWord);

        if (instruction.opcodeValue > 255) {
            io.incrementPC();
        }

        switch (instruction.addressingMode) {
            case IMMEDIATE:
                memoryResult = instruction.immediateByte ? io.getImmediateByte() : io.getImmediateWord();
                break;

            case INDEXED:
                memoryResult = io.getIndexed();
                break;

            case DIRECT:
                memoryResult = io.getDirect();
                break;

            case EXTENDED:
                memoryResult = io.getExtended();
                break;

            default:
                memoryResult = new MemoryResult();
                break;
        }

        return instruction.call(memoryResult, io);
    }

    /**
     * Performs an Interrupt Request (IRQ). Will save the PC, U, Y,
     * X, DP, B, A and CC registers on the stack, and jump to the address
     * stored at $FFF8.
     */
    public void interruptRequest() {
        io.pushStack(Register.S, io.regs.pc);
        io.pushStack(Register.S, io.regs.u);
        io.pushStack(Register.S, io.regs.y);
        io.pushStack(Register.S, io.regs.x);
        io.pushStack(Register.S, io.regs.dp);
        io.pushStack(Register.S, io.regs.b);
        io.pushStack(Register.S, io.regs.a);
        io.regs.cc.or(CC_E);
        io.pushStack(Register.S, io.regs.cc);
        io.regs.cc.or(CC_I);
        io.regs.pc.set(io.readWord(new UnsignedWord(0xFFF8)));
    }

    /**
     * Performs a Fast Interrupt Request (FIRQ). Will save the PC and
     * CC registers on the stack, and jump to the address stored at
     * $FFF6.
     */
    public void fastInterruptRequest() {
        io.pushStack(Register.S, io.regs.pc);
        io.regs.cc.and(~CC_E);
        io.pushStack(Register.S, io.regs.cc);
        io.regs.cc.or(CC_F);
        io.regs.cc.or(CC_I);
        io.regs.pc.set(io.readWord(new UnsignedWord(0xFFF6)));
    }

    /**
     * Performs a Non Maskable Interrupt Request (NMI). Will save the PC, U, Y,
     * X, DP, B, A and CC registers on the stack, and jump to the address
     * stored at $FFFC.
     */
    public void nonMaskableInterruptRequest() {
        io.pushStack(Register.S, io.regs.pc);
        io.pushStack(Register.S, io.regs.u);
        io.pushStack(Register.S, io.regs.y);
        io.pushStack(Register.S, io.regs.x);
        io.pushStack(Register.S, io.regs.dp);
        io.pushStack(Register.S, io.regs.b);
        io.pushStack(Register.S, io.regs.a);
        io.regs.cc.or(CC_E);
        io.pushStack(Register.S, io.regs.cc);
        io.regs.cc.or(CC_I);
        io.regs.cc.or(CC_F);
        io.regs.pc.set(io.readWord(new UnsignedWord(0xFFFC)));
    }

    /**
     * Schedules an IRQ interrupt to occur.
     */
    public void scheduleIRQ() {
        fireIRQ = true;
    }

    public boolean irqWaiting() {
        return fireIRQ;
    }

    public void clearIRQ() {
        fireIRQ = false;
    }

    /**
     * Check to see if there was any interrupt request. If so, execute the code associated
     * with it.
     */
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

    public boolean firqWaiting() {
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

    public boolean nmiWaiting() {
        return fireNMI;
    }

    public void clearNMI() {
        fireNMI = false;
    }

    public void reset() {

    }
}
