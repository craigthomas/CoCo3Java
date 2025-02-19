/*
 * Copyright (C) 2022-2025 Craig Thomas
 * This project uses an MIT style license - see LICENSE for details.
 */
package ca.craigthomas.yacoco3e.components;

import ca.craigthomas.yacoco3e.datatypes.*;

import static ca.craigthomas.yacoco3e.datatypes.RegisterSet.*;

/**
 * Implements an MC6809E microprocessor.
 */
public class CPU
{
    /* CPU Internal Variables */
    private final IOController io;
    public Instruction instruction;

    /* Interrupt request flags */
    protected boolean fireIRQ;
    protected boolean fireFIRQ;
    protected boolean fireNMI;

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
        UnsignedWord op = io.readWord(io.regs.pc);
        instruction = InstructionTable.get(op);
        return instruction.execute(io);
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
        io.regs.pc.set(io.readWord(0xFFF8));
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
        io.regs.pc.set(io.readWord(0xFFF6));
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
        io.regs.pc.set(io.readWord(0xFFFC));
    }

    /**
     * Check to see if there was any interrupt request. If so, execute the code associated
     * with it.
     */
    public void serviceInterrupts() {
        if (fireIRQ | fireFIRQ | fireNMI) {
            io.waitForIRQ = false;
        }

        if (fireIRQ) {
            interruptRequest();
            fireIRQ = false;
        }

        if (fireFIRQ) {
            fastInterruptRequest();
            fireFIRQ = false;
        }

        if (fireNMI) {
            nonMaskableInterruptRequest();
            fireNMI = false;
        }
    }

    /**
     * Schedules an IRQ interrupt to occur.
     */
    public void scheduleIRQ() {
        fireIRQ = true;
    }

    /**
     * Schedules a fast interrupt to occur.
     */
    public void scheduleFIRQ() {
        fireFIRQ = true;
    }

    /**
     * Schedules a non-maskable interrupt to occur.
     */
    public void scheduleNMI() {
        fireNMI = true;
    }

    public void reset() {

    }
}
