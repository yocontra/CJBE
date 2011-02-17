/**
 * @(#)JBranchEntry.java
 *
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
package jreversepro.revengine;

import jreversepro.common.Helper;
import jreversepro.common.KeyWords;
import jreversepro.reflect.JInstruction;
import jreversepro.reflect.JMethod;
import jreversepro.reflect.method.*;
import jreversepro.runtime.JOperandStack;
import jreversepro.runtime.Operand;
import jreversepro.runtime.OperandConstants;

/**
 * JBranchEntry refers to a single conditional BranchEntry only.
 *
 * @author Karthik Kumar
 */
public class JBranchEntry implements KeyWords,
        BranchConstants,
        OperandConstants {

    /**
     * startPc is the start of the branch entry
     */
    int startPc;

    /**
     * targetPc is the targetPc of the branch entry
     */
    int targetPc;

    /**
     * nextPc is the NextPc of the branch entry.
     */
    int nextPc;

    /**
     * type of the branch type
     */
    int type;
    //Possible values.  TYPE_WHILE || TYPE_IF.

    /**
     * Operand 1
     */
    String opr1;

    /**
     * Operand 2.
     */
    String opr2;

    /**
     * Operator
     */
    String operator;

    /**
     * Written flag - to signify if the branch has been written on the source
     * code.
     */
    boolean written;

    /**
     * The method corresponding to the code being processed
     */
    JMethod method;

    /**
     * The JBlockObject associated with this JBranchEntry
     */
    JBlockObject block = null;


    /**
     * @param method   Method to which this branch entry belongs to.
     * @param startPc  StartPc
     * @param targetPc TargetPc
     * @param type     Type of the branch
     */
    public JBranchEntry(JMethod method,
                        int startPc, int targetPc, int type) {
        this(method, startPc, startPc, targetPc, type, "", "", "");
        if (type == TYPE_JSR) {
            startPc = targetPc;
        }
        written = false;
    }


    /**
     * @param method   Method to which this branch entry belongs to.
     * @param startPc  StartPc
     * @param nextPc   NextPc
     * @param targetPc TargetPc
     * @param type     Type of the branch
     * @param opr1     Operand 1
     * @param opr2     Operand 2
     * @param operator Operator Comparison of the branch.
     */
    public JBranchEntry(JMethod method,
                        int startPc, int nextPc, int targetPc,
                        int type, String opr1, String opr2,
                        String operator) {

        this.method = method;

        this.startPc = startPc;
        this.nextPc = nextPc;
        this.targetPc = targetPc;


        this.type = type;

        this.opr1 = opr1;
        this.opr2 = opr2;
        this.operator = operator;
    }

    /**
     * Sets the written flag.
     */
    public void setWrittenFlag() {
        written = true;
    }

    /**
     * Resets the written flag
     */
    public void resetWrittenFlag() {
        written = false;
    }

    /**
     * Returns if the branch mentioned can be a TYPE_WHILE.
     * One indication of a while loop is that the startPc > targetPc.
     *
     * @return true, if it is a while loop. false, otherwise.
     */
    public boolean isWhile() {
        return (startPc > targetPc);
    }

    /**
     * @return Returns the startPc
     */
    public int getStartPc() {
        return startPc;
    }

    /**
     * @return Returns the targetPc.
     */
    public int getTargetPc() {
        return targetPc;
    }

    /**
     * @return Returns the NextPc.
     */
    public int getNextPc() {
        return nextPc;
    }

    /**
     * @return Returns the type of the Branch.
     */
    public final int getType() {
        return type;
    }

    /**
     * @return Returns the operand 1.
     */
    public String getOpr1() {
        return opr1;
    }

    /**
     * Setter method for NextPc.
     *
     * @param nextPc Value for the nextPc.
     */
    public void setNextPc(int nextPc) {
        this.nextPc = nextPc;
    }

    /**
     * @param targetPc TargetPC of the block.
     */
    public void setTargetPc(int targetPc) {
        this.targetPc = targetPc;
    }

    /**
     * @param startPc StartPc of the branch.
     *                Setter method for startPc.
     */
    public void setStartPc(int startPc) {
        this.startPc = startPc;
    }

    /**
     * Setter method for type.
     *
     * @param type Type of the branch.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @param opr1 Operand 1.
     *             Setter method for operand 1.
     */
    public void setOpr1(String opr1) {
        this.opr1 = opr1;
    }

    /**
     * @param opr2 Operand 2.
     *             Setter method for operand 2.
     */
    public void setOpr2(String opr2) {
        this.opr2 = opr2;
    }

    /**
     * This is invoked under the following circumstances. First this
     * is recognized as a 'if' branch. Then afterwards recognized as
     * 'while' branch.
     */
    public void convertToWhile() {
        this.type = TYPE_WHILE;

        //Swap targetPc and NextPc.
        int temp = targetPc;
        targetPc = nextPc;
        nextPc = temp;
    }

    /**
     * Complements the conditional operator of the branch
     */
    public void complementOperator() {
        operator = getComplementOperator(operator);
    }

    /**
     * Case 1:
     * <p><br><code>
     * a:     x    y   z   <br>
     * b:     y    p1  p2 <br>
     * z:       <br><br></code>
     * This means either a 'IF OR '  or and 'WHILE AND' between the
     * statements.
     * <br>
     * Now the row 'b'  is invalidated , and contents of a become
     * <code><br>a: x p1 p2 <br></code>
     * The Operands are also changed accordingly.
     * <p/>
     * Case 2:
     * <p><br><code>
     * a:     x    y   z   <br>
     * b:      y   p1  z  OR <br>
     * <p/>
     * a:     x    y   z   <br>
     * z:      .....       <br>
     * b:      y   p1  p2 <br><br></code>
     * This means either a 'IF AND'  or 'WHILE OR' between the
     * statements.<br>+
     * Now the row 'b'  is invalidated , and contents of a become
     * <code><br>a: x p1 p2 <br></code>
     * The Operands are also changed accordingly.
     * <br></p>
     * <p/>
     * <br></p>
     *
     * @param case1  Merge case either case1 or case 2
     *               as mentioned above.
     * @param ifstat If we have to write a if statements.
     * @param entryB Other entry index for which we have to write the statement.
     */
    void writeCase(boolean case1,
                   boolean ifstat, JBranchEntry entryB) {
        this.type = entryB.type;
        entryB.type = TYPE_INVALID;
        //Invalidate entryB since that is irrelevant 
        //after merging.

        this.nextPc = entryB.nextPc;
        this.targetPc = entryB.targetPc;

        this.rewriteCondition(entryB,
                (ifstat ^ case1) ? COND_AND : COND_OR,
                ifstat);


    }

    /**
     * Collates a single statement , depending on if it is
     * a  IF / WHILE statement.
     * <p></p>
     *
     * @return true , if the entry is a 'if' branch .
     *         false , otherwise.
     */
    boolean collate() {
        if (type == TYPE_JSR) {
            return true;
        }
        if (startPc < targetPc) {
            complementOperator();
            return true;
        } else {
            if (type != TYPE_WHILE) {
                type = TYPE_DO_WHILE;
            }
            return false;
        }
    }

    /**
     * This method checks if this particular branch block starts with
     * the given Pc.
     *
     * @param rhsStartPc StartPc that is to be checked if a block
     *                   starts there.
     * @return true, if this block starts with the mentioned startPc.
     *         false, otherwise.
     * @throws RevEngineException Thrown when any error occurs.
     */
    public boolean doesStartWith(int rhsStartPc)
            throws RevEngineException {

        if (type == TYPE_INVALID) {
            return false;
        }
        return (getStartBlockPc() == rhsStartPc);
    }

    /**
     * Lets us know if the block is independent.
     * When we say independent, we refer to blocks that can start on its own,
     * Examples of the same - if, while, try, do..while. switch
     * Examples of dependent branch blocks are .
     * else_if , else, catch each dependent on one of the independent block
     * for is existence
     *
     * @return true, if the block is independent.
     *         false, otherwise.
     */
    public boolean independent() {
        return type == TYPE_IF || type == TYPE_WHILE
                || type == TYPE_TRY || type == TYPE_DO_WHILE
                || type == TYPE_SYNC || type == TYPE_SWITCH
                || type == TYPE_CASE || type == TYPE_TRY_ANY;
    }

    /**
     * Returns if the given Pc is enclosed in the mentioned block
     *
     * @param aPc the Pc for which the location is to be mentioned.
     * @return true, if the block mentioned contains this Pc.
     *         false, otherwise.
     * @throws RevEngineException Thrown in case any error occurs while
     *                            performing this operation.
     */
    public boolean doesContain(int aPc)
            throws RevEngineException {

        return (getStartBlockPc() <= aPc
                && getEndBlockPc() >= aPc);
    }

    /**
     * @return Returns the start pc of this block.
     * @throws RevEngineException Thrown in case of any error while geting
     *                            start block Pc.
     */
    public int getStartBlockPc()
            throws RevEngineException {

        switch (type) {
            case TYPE_IF:
            case TYPE_ELSE:
            case TYPE_ELSE_IF:
            case TYPE_TRY:
            case TYPE_TRY_ANY:
            case TYPE_CATCH:
            case TYPE_CATCH_ANY:
            case TYPE_JSR:
            case TYPE_SYNC:
            case TYPE_SWITCH:
            case TYPE_CASE:
                return startPc;
            case TYPE_WHILE:
            case TYPE_DO_WHILE:
                return targetPc;
            default:
                throw new RevEngineException("Invalid Branch Entry");
        }
    }

    /**
     * @return Returns the endpc of the block under consideration.
     * @throws RevEngineException Thrown in case there of any
     *                            problem getting End block pc.
     */
    public int getEndBlockPc()
            throws RevEngineException {

        switch (type) {
            case TYPE_IF:
            case TYPE_ELSE:
            case TYPE_ELSE_IF:
            case TYPE_TRY:
            case TYPE_TRY_ANY:
            case TYPE_CATCH:
            case TYPE_CATCH_ANY:
            case TYPE_JSR:
            case TYPE_SYNC:
            case TYPE_CASE:
            case TYPE_SWITCH:
                return targetPc;
            case TYPE_WHILE:
            case TYPE_DO_WHILE:
                return nextPc;
            default:
                throw new RevEngineException("Invalid Branch Entry ");
        }
    }

    /**
     * @return Returns the Pc from which the execution for the
     *         block under consideration.
     * @throws RevEngineException Thrown in case any error occurs.
     */
    public int getStartExecPc()
            throws RevEngineException {
        switch (type) {
            case TYPE_IF:
            case TYPE_ELSE:
            case TYPE_ELSE_IF:
            case TYPE_TRY:
            case TYPE_TRY_ANY:
            case TYPE_CATCH:
            case TYPE_CATCH_ANY:
            case TYPE_JSR:
            case TYPE_SYNC:
            case TYPE_CASE:
            case TYPE_SWITCH:
                return nextPc;
            case TYPE_WHILE:
            case TYPE_DO_WHILE:
                return targetPc;
            default:
                throw new RevEngineException("Invalid Branch Entry");
        }
    }

    /**
     * Append the code for the beginning of a block
     *
     * @param decomp Reference to decempiler.
     */
    public final void appendStartBlockStmtX(JDecompiler decomp) {
        JInstruction sIns = null;


        Helper.log("Branch Begins " + this);
        if (!(type == TYPE_CATCH_ANY
                || type == TYPE_TRY_ANY)) {
            switch (type) {
                case TYPE_IF:
                    String expr = getExpression();
                    decomp.setLastIns(nextPc);
                    decomp.setLastInsPos(
                            method.getInstruction(nextPc).position);
                    method.addBlock(new JIfBlock(this, expr));
                    break;
                case TYPE_ELSE_IF:
                    decomp.setLastIns(nextPc);
                    decomp.setLastInsPos(
                            method.getInstruction(nextPc).position);
                    method.addBlock(new JElseIfBlock(this, getExpression()));
                    break;
                case TYPE_ELSE:
                    decomp.setLastIns(nextPc);
                    decomp.setLastInsPos(
                            method.getInstruction(nextPc).position);
                    method.addBlock(new JElseBlock(this));
                    break;
                case TYPE_WHILE:
                    //This is a For loop 
                    //Create a reference so we can add the missing init
                    //& loop values later
                    block = new JForBlock(this, getExpression());
                    method.addBlock(block);
                    //Set next pc to be after the end of the for loop
                    decomp.setLastIns(this.nextPc);
                    //decomp.setLastInsPos(this.?);
                    break;
                case TYPE_DO_WHILE:
                    //Create a reference so we can add the operators later
                    block = new JDoWhileBlock(this);
                    method.addBlock(block);
                    break;
                case TYPE_TRY:
                    method.addBlock(new JTryBlock(this));
                    break;
                case TYPE_CATCH:
                    //Adjust start PC for executable instruction
                    //Set to next instruction
                    sIns = method.getNextInstruction(startPc);
                    decomp.setLastIns(sIns.index);
                    decomp.setLastInsPos(sIns.position);
                    method.addBlock(new JCatchBlock(this, opr1, opr2));
                    break;
                case TYPE_JSR:
                    method.addBlock(new JFinallyBlock(this));
                    break;
                case TYPE_SYNC:
                    method.addBlock(new JSynchBlock(this, opr1));
                    break;
                case TYPE_CASE:
                    Helper.log("CASE BLOCK :  " + opr1);
                    method.addBlock(new JCaseBlock(this, opr1));
                    break;
                case TYPE_SWITCH:
                    method.addBlock(new JSwitchBlock(this, opr1));
                    break;
                default:
            }
        }
    }


    /**
     * Appends end block statement for a branch entry.
     *
     * @param decomp Decompiler reference.
     * @param jos    Java Operand Stack reference.
     * @return Returns boolean.
     */
    public final boolean appendEndBlockStmt(
            JDecompiler decomp,
            JOperandStack jos) {
        boolean mergeStack = false;
        if (!(type == TYPE_CATCH_ANY
                || type == TYPE_TRY_ANY)) {
            Helper.log("Branch Ends " + this);
            switch (type) {
                case TYPE_IF:
                    if (written) {
                        method.closeBlock();
                        decomp.setLastIns(nextPc);
                        //Set the index ptr to the ins after the end of 
                        //this block
                        //JInstruction sIns = 
                        //              method.getInstruction(TargetPc); 
                        //decomp.setLastIns(sIns.getTargetPc2());
                        //decomp.setLastInsPos(
                        //  method.getInstruction(
                        //      sIns.getTargetPc2()).position);
                        //      
                        //System.out.println(
                        //  "End sync - setting end to: "+sIns.getTargetPc());

                        //decomp.setLastInsPos(?);
                    } else if (!jos.empty()) {
                        //Rollback (remove current IF block)
                        method.removeCurrentBlock();
                        Operand op1 = (Operand) jos.pop();
                        String expr = getExpression();
                        jos.push(new Operand(
                                "(" + expr + ") ? " + op1.getValueEx(L_TERN)
                                        + " : ",
                                op1.getDatatype(), L_TERN));
                    } else {
                        method.closeBlock();
                        //Set the index ptr to the ins after the end of this
                        //block
                        //JInstruction sIns = method.getInstruction(TargetPc); 
                        //decomp.setLastIns(sIns.getTargetPc2());
                        //decomp.setLastInsPos(
                        //  method.getInstruction(
                        //      sIns.getTargetPc2()).position);
                        //System.out.println("End sync - setting end to: "
                        //  + sIns.getTargetPc());                    
                        // System.out.println(method.getName()+":if2    
                        // StartPC="+StartPc+" NextPc="+NextPc+" TargetPc="
                        //  +TargetPc);
                        // decomp.setLastIns(NextPc);
                        // decomp.setLastInsPos(?);
                    }
                    break;
                case TYPE_ELSE:
                    if (written) {
                        method.closeBlock();
                        //Set the index ptr to the ins after the end of this
                        //block
                        //JInstruction sIns = method.getInstruction(TargetPc);
                        //decomp.setLastIns(sIns.getTargetPc2());
                        //decomp.setLastInsPos(
                        //  method.getInstruction(
                        //              sIns.getTargetPc2()).position);
                        //System.out.println("End sync - setting end to: "
                        //  + sIns.getTargetPc());
                        // System.out.println(method.getName()+":else    
                        //  StartPC="+StartPc+" NextPc="+NextPc+" TargetPc="
                        //  +TargetPc);
                        //decomp.setLastIns(NextPc);
                        //decomp.setLastInsPos(?);
                    } else {
                        if (!jos.empty()) {
                            //Rollback (remove current else block)
                            method.removeCurrentBlock();
                        }
                        mergeStack = true;
                    }
                    break;
                case TYPE_TRY:
                case TYPE_ELSE_IF:
                case TYPE_WHILE:
                case TYPE_JSR:
                case TYPE_SWITCH:
                case TYPE_CASE:
                    method.closeBlock();
                    //Set the index ptr to the ins after the end of this block
                    //JInstruction sIns = method.getInstruction(TargetPc); 
                    //decomp.setLastIns(sIns.getTargetPc2());
                    //decomp.setLastInsPos(method.getInstruction(
                    //                  sIns.getTargetPc2()).position);
                    //System.out.println("End sync - setting end to: "
                    //                      + sIns.getTargetPc());
                    ///System.out.println(method.getName()
                    //          + ":try/elseif...    StartPC="
                    //          + StartPc+" NextPc="+NextPc+" TargetPc="
                    //          + TargetPc);
                    //decomp.setLastIns(NextPc);
                    //decomp.setLastInsPos(?);
                    break;
                //Blocks that use "goto i" at targetPc to identify next
                //instruction
                case TYPE_CATCH:
                case TYPE_SYNC:
                    method.closeBlock();

                    //Set the index ptr to the ins after the end of this
                    //block, IF no other catch follows
/**
 *                  JInstruction sIns = method.getInstruction(targetPc); 
 if (targetPc > 0) {
 // Verified that this works for catch stmts
 decomp.setLastIns(sIns.index);
 decomp.setLastInsPos(sIns.position);
 //decomp.setLastIns(sIns.getTargetPc2());
 //decomp.setLastInsPos(method.getInstruction(
 //      sIns.getTargetPc2()).position);
 //
 }
 */
                    break;
                case TYPE_DO_WHILE:
                    method.closeBlock();
                    decomp.setLastIns(nextPc);
                    //decomp.setLastInsPos(?);
                    ((JDoWhileBlock) block).setVar1(opr1);
                    ((JDoWhileBlock) block).setOper(operator);
                    ((JDoWhileBlock) block).setVar2(opr2);
                    //Set the index ptr to the ins after the end of this block

                    //JInstruction sIns = method.getInstruction(TargetPc); 
                    //decomp.setLastIns(sIns.getTargetPc2());
                    //decomp.setLastInsPos(method.getInstruction(
                    //                  sIns.getTargetPc2()).position);
                    //System.out.println("End sync - setting end to: "
                    //                  +sIns.getTargetPc());
                    // System.out.println("do_while");
                    // System.out.println("do_while    StartPC="
                    //                  +StartPc+" NextPc="
                    // +NextPc+" TargetPc="+TargetPc);
                    break;
                default:
            }
        }
        return mergeStack;
    }


    /**
     * This merges the current condition represented by the
     * current JBranchEntry.
     *
     * @param nextEntry     Next Condition that is to be merged with
     *                      the current condition.
     * @param conditionType If OR or AND.
     * @param complement    if the current expression needs to be
     *                      complemented.
     */
    public void rewriteCondition(JBranchEntry nextEntry,
                                 String conditionType,
                                 boolean complement) {
        opr1 = this.getCondition(complement);
        operator = conditionType;
        opr2 = nextEntry.getCondition(true);
    }


    /**
     * Sets the end pc of this block.
     *
     * @param aNewPc New Pc that is to be set as the
     *               end of the block
     */
    public void setEndBlockPc(int aNewPc) {
        switch (type) {
            case TYPE_IF:
            case TYPE_ELSE:
            case TYPE_ELSE_IF:
            case TYPE_TRY:
            case TYPE_TRY_ANY:
            case TYPE_CATCH:
            case TYPE_CATCH_ANY:
            case TYPE_JSR:
            case TYPE_SYNC:
            case TYPE_CASE:
            case TYPE_SWITCH:
                targetPc = aNewPc;
                break;
            case TYPE_WHILE:
            case TYPE_DO_WHILE:
                nextPc = aNewPc;
                break;
        }
    }


    /**
     * Given the index to the entry in the BranchTable , this
     * returns the condition .
     * <p>
     * For example , if an entry has <br>
     * <code>4:  i  3 !=  </code> .<br>
     * Then <code>getCondition(4,true)</code> yields
     * <code>i != 3 </code>  and <br>
     * <code>getCondition(4,false)</code> yields
     * <code>i == 3 </code></p> .
     *
     * @param complement If flag is set the operator is replaced by its
     *                   complement. For eg, complement of >= is replaced by '<''.
     * @return Returns the condition.
     */
    private String getCondition(boolean complement) {
        if (!complement) {
            operator = getComplementOperator(operator);
        }
        return getExpression();
    }


    /**
     * Returns the complementary operator of the given operator.
     * <p/>
     *
     * @param rhs Operator for which complement is to be returned.
     * @return the complementary operator of <code>Rhs</code>
     */
    private String getComplementOperator(String rhs) {
        if (rhs.compareTo(OPR_GT) == 0) {
            return OPR_LE;
        } else if (rhs.compareTo(OPR_GE) == 0) {
            return OPR_LT;
        } else if (rhs.compareTo(OPR_LT) == 0) {
            return OPR_GE;
        } else if (rhs.compareTo(OPR_LE) == 0) {
            return OPR_GT;
        } else if (rhs.compareTo(OPR_EQ) == 0) {
            return OPR_NE;
        } else if (rhs.compareTo(OPR_NE) == 0) {
            return OPR_EQ;
        } else {
            return rhs;
        }
    }

    /**
     * Trims the expression for a condition here.
     * For eg, an expression of the form -
     * if ( a == true ) is converted to 'a' ( just 'a' ).
     * These small modifications improve the readability of the code.
     *
     * @return Returns the new code.
     */
    public String getExpression() {
        operator = operator.trim();
        opr2 = opr2.trim();

        if (opr2.equals(FALSE)) {
            if (operator.equals(OPR_EQ)) {
                return OPR_NOT + opr1;
            }
            return opr1;
        } else if (opr2.equals(TRUE)) {
            if (operator.equals(OPR_EQ)) {
                return opr1;
            }
            return OPR_NOT + opr1;
        } else {
            return opr1 + " " + operator + " " + opr2;
        }
    }

    /**
     * @return Returns the Stringified representation of the
     *         class.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("  ");
        sb.append(written + " ");
        sb.append(startPc + " " + nextPc + "  " + targetPc + " ");
        switch (type) {
            case TYPE_IF:
                sb.append("TYPE_IF");
                break;
            case TYPE_ELSE_IF:
                sb.append("TYPE_ELSE_IF");
                break;
            case TYPE_ELSE:
                sb.append("TYPE_ELSE");
                break;
            case TYPE_DO_WHILE:
                sb.append("TYPE_DO_WHILE");
                break;
            case TYPE_TRY:
                sb.append("TYPE_TRY");
                break;
            case TYPE_TRY_ANY:
                sb.append("TYPE_TRY_ANY");
                break;
            case TYPE_CATCH:
                sb.append("TYPE_CATCH");
                break;
            case TYPE_CATCH_ANY:
                sb.append("TYPE_CATCH_ANY");
                break;
            case TYPE_WHILE:
                sb.append("TYPE_WHILE");
                break;
            case TYPE_JSR:
                sb.append("TYPE_JSR");
                break;
            case TYPE_SYNC:
                sb.append("TYPE_SYNCHRONIZED");
                break;
            case TYPE_CASE:
                sb.append("TYPE_CASE");
                break;
            case TYPE_SWITCH:
                sb.append("TYPE_SWITCH");
                break;
            default:
                sb.append(type);
        }
        sb.append(" " + opr1 + " " + operator + " " + opr2);
        return sb.toString();
    }
}
