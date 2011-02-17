/**
 * @(#)JRunTimeContext.java
 *
 * JReversePro - Java Decompiler / Disassembler.
 * Copyright (C) 2000 2001 Karthik Kumar.
 * EMail: akkumar@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it , under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.If not, write to
 *  The Free Software Foundation, Inc.,
 *  59 Temple Place - Suite 330,
 *  Boston, MA 02111-1307, USA.
 **/
package jreversepro.runtime;

import jreversepro.common.Helper;
import jreversepro.common.KeyWords;
import jreversepro.parser.ClassParserException;
import jreversepro.reflect.JInstruction;
import jreversepro.reflect.JLineOfCode;
import jreversepro.reflect.JMethod;
import jreversepro.revengine.*;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

/**
 * This contains the RunTime context.
 *
 * @author Karthik Kumar
 */
public class JRunTimeContext
        implements KeyWords, BranchConstants {

    /**
     * Operand Stack reference.
     */
    JOperandStack jos;

    /**
     * This Stack contains the individual control blocks.
     * Individual elements are BranchEntry.
     */
    Stack jcs;

    /**
     * Runtime Frame contains SymbolTable and ConstantPool.
     */
    JRunTimeFrame rtf;

    /**
     * Contains the list  of branchentries ( control blocks of source
     * code ).
     */
    JBranchTable branches;

    //preserves the context.

    /**
     * If set then while executing instruction the contents of
     * stack are merged. Used for the case of ternary expressions.
     */
    boolean mergeStack;

    /**
     * If the code is written for the block
     */
    boolean writtenCode;

    /**
     * previous control block that was popped from the Control Stack.
     * See member jcs - Control Stack implementation.
     */
    JBranchEntry prevBlock;

    /**
     * Control block that is on top of the Control Block stack.
     */
    JBranchEntry currBlock;

    /**
     * Method reference
     */
    JMethod method;

    /**
     * Decompiler reference
     */
    JDecompiler decomp;

    /**
     * @param meth     Method reference.
     * @param decomp   Decompiler reference.
     * @param rtf      RuntimeFrame input
     * @param jos      OperandStack reference
     * @param branches BranchReference.
     */
    public JRunTimeContext(JDecompiler decomp,
                           JMethod meth,
                           JRunTimeFrame rtf,
                           JOperandStack jos,
                           JBranchTable branches) {
        this.decomp = decomp;
        this.method = meth;
        this.jos = jos;
        this.rtf = rtf;
        this.branches = branches;

        mergeStack = false;
        writtenCode = false;
        this.jcs = new Stack();
    }

    /**
     * Get JVM Operand stack.
     *
     * @return Returns Operand Stack.
     */
    public JOperandStack getOperandStack() {
        return jos;
    }

    /**
     * Process JVM instruction.
     *
     * @param ins Instruction that is to be executed.
     * @throws RevEngineException   thrown in case
     *                              any problem occurs while executing the instruction.
     * @throws ClassParserException Thrown in case of
     *                              an invalid reference to Constantpool.
     */
    public void executeInstruction(JInstruction ins)
            throws RevEngineException,
            ClassParserException {
        if (mergeStack) {
            jos.mergeTopTwo();
        }
        rtf.operateStack(ins, jos);
        mergeStack = false;
    }

    /**
     * After all the processing of the method is over, this
     * code generated the final block statement, if any.
     */
    public void getFinalBlockStmt() {
        while (!jcs.empty()) {
            prevBlock = (JBranchEntry) jcs.pop();
            prevBlock.appendEndBlockStmt(decomp, jos);
        }
    }

    /**
     * Close current branch block regularly.
     * For a given end-point of one or more control-blocks,
     * this appends the End-Of-Block statements.
     *
     * @param insIndex Instruction Index.
     * @throws RevEngineException Encountered while
     *                            trying to find the end statement.
     */
    public void getEndStmt(int insIndex)
            throws RevEngineException {
        while (currBlock != null
                && currBlock.getEndBlockPc() == insIndex) {
            mergeStack |= currBlock.appendEndBlockStmt(decomp, jos);
            writtenCode = false;
            prevBlock = (JBranchEntry) jcs.pop();
            currBlock = (jcs.empty())
                    ? null : (JBranchEntry) jcs.peek();
        }
    }


    /**
     * @param listBranches List of branches
     * @param insIndex     Instruction Index
     * @param symTable     SymbolTable.
     * @throws RevEngineException thrown while generating the
     *                            beginning statement for the given block.
     */
    public void getBeginStmt(List listBranches,
                             int insIndex,
                             JSymbolTable symTable)
            throws RevEngineException {

        for (int i = 0; i < listBranches.size(); i++) {
            JBranchEntry ent = (JBranchEntry) listBranches.get(i);
            validatePairings(ent);
            writeVariableDeclarations(ent, symTable);
            ent.appendStartBlockStmtX(decomp);
            setBlockWrittenFlag();
            pushControlEntry(ent);
            if (ent.getType() == TYPE_IF) {
                JBranchEntry loop = getImmediateOuterLoop();
                if (loop != null) {
                    int target = ent.getTargetPc();
                    if (target == loop.getNextPc()) {
                        //PP *** Need to identify offsets for next line ***
                        method.addLineOfCode(
                                new JLineOfCode(
                                        -1, -1, -1, -1,
                                        KeyWords.BREAK + ";\n"));
                        ent.appendEndBlockStmt(decomp, jos);
                    } else if (target == loop.getStartPc()) {
                        //PP *** Need to identify offsets for next line ***
                        method.addLineOfCode(new JLineOfCode(
                                -1, -1, -1, -1,
                                KeyWords.CONTINUE + ";\n"));
                        ent.appendEndBlockStmt(decomp, jos);
                    }
                }
            }
        }
    }


    /**
     * At any given stage this method returns the reference to
     * while/ do..while/ switch statement that enclosed this, to the
     * outermost reference. This method is mainly useful in case of
     * break and continue.. statements.
     *
     * @return Returns a reference to control block of outermost loop.
     */
    public JBranchEntry getImmediateOuterLoop() {
        for (int i = jcs.size() - 1; i >= 0; i--) {
            JBranchEntry ent = (JBranchEntry) jcs.get(i);
            if (ent.getType() == TYPE_DO_WHILE
                    || ent.getType() == TYPE_WHILE
                    || ent.getType() == TYPE_SWITCH) {
                return ent;
            }
        }
        return null;
    }

    /**
     * @param newent NewEntry of the control block that is to be pushed
     *               onto the stack.
     * @throws RevEngineException thrown if the end block of the entry
     *                            of the content to be pushed overlaps with the end of the entry existing
     *                            already on top of control block stack.
     */
    public void pushControlEntry(JBranchEntry newent)
            throws RevEngineException {

        if (!jcs.empty()) {
            if (newent.getEndBlockPc() == -1) {
                JBranchEntry topEnt = (JBranchEntry) jcs.peek();
                newent.setEndBlockPc(topEnt.getEndBlockPc());
            } else if (newent.getEndBlockPc() > currBlock.getEndBlockPc()) {
                Helper.log("Block overlap "
                        + newent.toString() + " with  " + currBlock.toString());
                newent.setEndBlockPc(currBlock.getEndBlockPc());
            }
        }
        jcs.push(newent);
        currBlock = newent;
    }

    /**
     * @param ent      Entry ( Control Block ) for which all the
     *                 variable declarations are supposed to be in place.
     * @param symTable Reference to symbol table that is
     *                 necessary to get to know the names and datatypes of variables.
     * @throws RevEngineException Thrown in case of any problem
     *                            writing variable names in the table.
     */
    public void writeVariableDeclarations(JBranchEntry ent,
                                          JSymbolTable symTable)
            throws RevEngineException {
        Enumeration
        enume=Collections.enumeration(
                symTable.defineVariable(
                        ent.getEndBlockPc()));
        while (
        enume.hasMoreElements()){
            String varDec =
            enume.nextElement().toString();
            //PP *** Need to identify offsets for next line ***
            method.addLineOfCode(new JLineOfCode(
                    -1, -1, -1,
                    -1, varDec + ";\n"));
        }
    }

    /**
     * @param startPc  Startpc
     * @param targetPc TargetPc
     */
    public void processBreakContinue(int startPc,
                                     int targetPc) {

        JBranchEntry loopEntry = getImmediateOuterLoop();
        if (loopEntry == null) {
            return;
        }
        Helper.log("Outer loop " + loopEntry);
        if (targetPc == loopEntry.getNextPc()) {
            //PP *** Need to identify offsets for next line ***
            method.addLineOfCode(
                    new JLineOfCode(
                            -1, -1, -1,
                            -1, BREAK + ";\n"));

            branches.deleteElse(startPc + 3);
        } else if (targetPc == loopEntry.getStartPc()
                && startPc != loopEntry.getNextPc() - 3) {
            //PP *** Need to identify offsets for next line ***
            method.addLineOfCode(new JLineOfCode(
                    -1, -1, -1,
                    -1, CONTINUE + ";\n"));

            branches.deleteElse(startPc + 3);
        }
    }

    /**
     * Check correct branch pairings for IF/ELSEIF/ELSE/TRY/CATCH/FINALLY.
     *
     * @param ent This is the entry that needs to be evaluated.
     *            If a branch of type TYPE_ELSE / TYPE_ELSE_IF is to be ended then
     *            the last ended must be TYPE_IF.
     *            If a branch ent is of type TYPE_CATCH then last poppped entry
     *            must be TYPE_TRY.
     * @throws RevEngineException Thrown if these validations are not
     *                            satisfied.
     */
    public void validatePairings(JBranchEntry ent)
            throws RevEngineException {
        if (ent.independent()) {
            return;
        }
        if (prevBlock == null) {
            Helper.log("Matching block for " + ent + " is null");
            return;
        }
        int entryStartPc = ent.getStartPc();
        int cType = ent.getType();
        int pType = prevBlock.getType();
        boolean pairExistsFlag = false;
        if (cType == TYPE_ELSE || cType == TYPE_ELSE_IF) {
            // can be only after if or else if
            pairExistsFlag = (pType == TYPE_IF
                    || pType == TYPE_ELSE_IF)
                    && entryStartPc == prevBlock.getEndBlockPc();

        } else if (cType == TYPE_CATCH) {
            // can be only after try or catch
            pairExistsFlag =
                    (pType == TYPE_TRY || pType == TYPE_CATCH) && !writtenCode;
        } else if (cType == TYPE_CATCH_ANY) {
            // can be only after try (any) or catch
            pairExistsFlag =
                    (pType == TYPE_TRY_ANY || pType == TYPE_CATCH) && !writtenCode;
        } else if (cType == TYPE_JSR) {
            // can be only after catch (any)
            pairExistsFlag = (pType == TYPE_CATCH_ANY);
        }
        if (!pairExistsFlag) {
/**
 Helper.log("Last popped " + prevBlock
 + "\n\t\tis not a valid preceding entry for " +  ent);
 **/
        }
    }


    /**
     * Adds a line of code with byteoffset information
     *
     * @param sOffset Offset information
     * @param eOffset ??? Extended offset
     * @param fromPos FromPosition
     * @param toPos   To Position
     * @param type    ?? type.
     */
    public void addCode(int sOffset, int eOffset, int fromPos,
                        int toPos, String type) {
        setBlockWrittenFlag();
        writtenCode = true;
        method.addLineOfCode(new JLineOfCode(sOffset, eOffset,
                fromPos, toPos,
                type + rtf.getStatement() + ";\n"));
    }

    /**
     * Adds a line of code with byteoffset information
     *
     * @param sOffset Offset information
     * @param eOffset ??? Extended offset
     * @param fromPos FromPosition
     * @param toPos   To Position
     */
    public void addCode(int sOffset, int eOffset, int fromPos, int toPos) {
        addCode(sOffset, eOffset, fromPos, toPos, "");//No type information.
    }


    /**
     * Add text line to output code with indenting.
     *
     * @param txt Text to be added.
     */
    public void addTextCode(String txt) {
        //PP *** Need to identify offsets for next line ***
        method.addLineOfCode(new JLineOfCode(
                -1, -1, -1,
                -1, txt + "\n"));
    }

    /**
     * In case a statement has been identified for writing
     * for the current topmost block then that is set
     * the written flag.
     */
    public void setBlockWrittenFlag() {
        if (!jcs.empty()) {
            currBlock.setWrittenFlag();
        }
    }

    /**
     * @return String representation of the class.
     */
    public String toString() {
        return jcs.toString();
    }
}
