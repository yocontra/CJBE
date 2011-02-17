/**
 * @(#)JBranchTable.java
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
import jreversepro.common.JJvmOpcodes;
import jreversepro.reflect.JException;
import jreversepro.reflect.JInstruction;
import jreversepro.reflect.JMethod;

import java.util.*;

//import jreversepro.common.KeyWords;

/**
 * JBranchTable manages the objects of JGotoEntry and JBranchEntry.
 *
 * @author Karthik Kumar
 */
public class JBranchTable implements BranchConstants, JJvmOpcodes {

    /**
     * Branches is a list, with the individual elemens containing
     * 'JBranchEntry'.
     */
    List branches;

    /**
     * gotos
     * Key - StartPc ( java.lang.Integer )
     * Value - TargetPc ( Absolute target -java.lang.Integer).
     */
    Map gotos;

    /**
     * List of switch instructions. Individual members are
     * JInstruction.
     */
    List switches;

    /**
     * It is a Vector of 'Integer's.
     * The integers are the target of the jump sub routine instruction.
     */
    Vector mJSRTarget;

    /**
     * Map of monitor instructions.
     */
    Map mMonitor;

    /**
     * Method reference *
     */
    JMethod method;

    /**
     * @param method Method reference.
     */
    public JBranchTable(JMethod method) {
        mJSRTarget = new Vector();
        mMonitor = new HashMap();
        branches = new Vector();
        switches = new Vector();
        gotos = new HashMap();
        this.method = method;
    }

    /**
     * Setter method for the branch tables.
     *
     * @param aBranches Branches to be added.
     */
    public void setTables(List aBranches) {
        branches.addAll(aBranches);
    }

    /**
     * Getter method for goto tables.
     *
     * @return Map of goto table entries.
     *         key - goto pc.
     *         value - target of that goto table.
     */
    public Map getGotoTable() {
        return gotos;
    }

    /**
     * Adds a Goto entry to the internal data structure.
     *
     * @param startPc  StartPc of the goto statement.
     * @param targetPc TargetPc of the goto statement.
     */
    public void addGotoEntry(int startPc, int targetPc) {
        gotos.put(new Integer(startPc),
                new Integer(targetPc));
    }

    /**
     * Finalizer method.
     */
    protected void finalize() {
        branches = null;
        gotos = null;
    }

    /**
     * Adds a new branch entry to the list of branches.
     *
     * @param ent branch entry to be added.
     */
    public void add(JBranchEntry ent) {
        branches.add(ent);
    }

    /**
     * Checks if the Pc passed as argument is the target for
     * any JSR instructions.
     *
     * @param currPc Pc for which it is to be checked if it is the
     *               target for any JSR instruction.
     * @return true, if there exists a JSR instruction with its target
     *         currPc. false, otherwise.
     */
    public boolean isJSRTarget(int currPc) {
        return mJSRTarget.contains(new Integer(currPc));
    }

    /**
     * This adds the pc given as input as a JSR target.
     *
     * @param targetPc TargetPc for a JSR instruction that is to be
     *                 added to the internal data structure ( list ).
     */
    public void addJSRPc(int targetPc) {
        Integer intPc = new Integer(targetPc);
        if (!mJSRTarget.contains(intPc)) {
            mJSRTarget.add(intPc);
        }
    }

    /**
     * When a RET instruction is encountered we add a branch with the
     * last element of the JSR target lists.
     * JSR instructions signify 'synchronized' and catch..all blocks.
     *
     * @param retPc PC of the instruction which is a RET.
     */
    public void addRetPc(int retPc) {
        Object obj = mJSRTarget.lastElement();
        int startPc = ((Integer) obj).intValue();
        branches.add(new JBranchEntry(
                method, startPc,
                startPc, retPc,
                TYPE_JSR, "", "", ""));
    }

    /**
     * This sorts the list containing branches such that no
     * branch overlaps with the one previously existing.
     * See JBranchComparator for more details.
     */
    public void sort() {
        Collections.sort(branches, new JBranchComparator());
    }

    /**
     * Adds a monitor Pc.
     *
     * @param aMonitorPc Pc that is monitorenter.
     * @param aMonObject Object that is 'monitored'. In the sense
     *                   object for which lock is obtained before entering a
     *                   'synchronized' object.
     */
    public void addMonitorPc(int aMonitorPc, String aMonObject) {
        mMonitor.put(new Integer(aMonitorPc), aMonObject);
    }

    /**
     * Returns the monitor type for the monitor that begins with
     * Pc.
     *
     * @param monitorBeginPc Pc that begins with the monitor.
     * @return monitor object associated with this branch.
     */
    public String doesMonitorBegin(int monitorBeginPc) {
        return (String) mMonitor.get(new Integer(monitorBeginPc));
    }

    /**
     * Identifies the else..if and else branches.
     * Identifies catch.. branches.
     *
     * @throws RevEngineException Thrown in case of any error.
     */
    public void identifyMoreBranches()
            throws RevEngineException {

        for (int i = 0; i < branches.size(); i++) {
            JBranchEntry jbe = (JBranchEntry) branches.get(i);
            int gotoStartPc = jbe.getEndBlockPc() - 3;
            int gotoNextPc = gotoStartPc + 3;
            Object obj = gotos.get(new Integer(gotoStartPc));
            switch (jbe.getType()) {
                case TYPE_IF:
                case TYPE_ELSE_IF:
                    if (obj != null) {
                        //Before adding else, check for else if.
                        int gotoTargetPc = ((Integer) obj).intValue();
                        if (gotoTargetPc - gotoStartPc == 3) {
                            break;
                        }
                        JBranchEntry elsif =
                                contains(startsWith(gotoNextPc),
                                        TYPE_IF);

                        if (elsif == null) {
                            JBranchEntry caseEntry =
                                    contains(startsWith(gotoNextPc),
                                            TYPE_CASE);
                            if (caseEntry == null) {
                                JBranchEntry elseEntry =
                                        new JBranchEntry(
                                                method,
                                                gotoNextPc,
                                                gotoNextPc,
                                                gotoTargetPc,
                                                TYPE_ELSE,
                                                jbe.opr1,
                                                jbe.opr2,
                                                jbe.operator);
                                branches.add(elseEntry);
                            }
                        } else {
                            elsif.setType(TYPE_ELSE_IF);
                        }
                    }
                    break;
                case TYPE_DO_WHILE:
                    if (gotos.containsValue(new Integer(jbe.startPc))) {
                        jbe.setType(TYPE_WHILE);
                    }
                    break;
            }
        }
    }

    /**
     * Adds the switch entries and the case entries under the same to the
     * branch table.
     *
     * @param switchEntry switch table containing entries about switch
     *                    statements.
     */
    public void addSwitch(JSwitchTable switchEntry) {
        int defaultByte = switchEntry.getDefaultByte();
        int maxTarget = defaultByte;
        List enumCases = switchEntry.getCases();

        Helper.log("No: Case Entries " + enumCases.size());
        for (int j = 0; j < enumCases.size(); j++) {
            JCaseEntry singleCase = (JCaseEntry) enumCases.get(j);
            int caseTarget = singleCase.getTarget();
            int endCase = singleCase.getEndTarget();

            List caseValues = singleCase.getValues();
            StringBuffer sb = new StringBuffer();
            for (int k = 0; k < caseValues.size(); k++) {
                sb.append(caseValues.get(k) + ",");
            }
            JBranchEntry ent = new JBranchEntry(
                    method,
                    caseTarget, caseTarget,
                    endCase, TYPE_CASE,
                    sb.toString(), "", "");
            branches.add(ent);
        }
        branches.add(switchEntry.getBranchEntry());
    }

    /**
     * List of JException entries.
     *
     * @param excTryTable Individual entries being JException.
     */
    public void addTryBlocks(List excTryTable) {
        Helper.log("Number of Try..blocks " + excTryTable.size());
        for (int i = 0; i < excTryTable.size(); i++) {
            JException exc = (JException) excTryTable.get(i);
            int insIndex = exc.getStartPc();
            if (insIndex == -1) {
                continue;
            }

            int endPc = exc.getEffectiveEndPc(method.getInstructions());
            String syncLock = doesMonitorBegin(insIndex - 1);
            if (syncLock != null) {
                branches.add(new JBranchEntry(
                        method,
                        insIndex, insIndex, endPc,
                        TYPE_SYNC, syncLock, "", ""));
            } else {
                branches.add(new JBranchEntry(
                        method,
                        insIndex, insIndex, endPc,
                        (exc.isAny()) ? TYPE_TRY_ANY : TYPE_TRY,
                        "", "", ""));
            }
        }
    }

    /**
     * For the given pc return the target of the instruction.
     * The instruction is a goto statement.
     *
     * @param startPc Start Pc.
     * @return the TargetPc for the goto instruction at the
     *         startPc
     */
    public int findGotoTarget(int startPc) {
        Object obj = gotos.get(
                new Integer(startPc));
        if (obj == null) {
            return -1;
        } else {
            return ((Integer) obj).intValue();
        }
    }

    /**
     * Returns the list of branches that starts with the
     * mentioned aInsIndex.
     *
     * @param aInsIndex Instruction index.
     * @return List of JBranchEntry -
     *         list of branches that starts with the mentioned
     *         instruction index.
     * @throws RevEngineException thrown in case of an error.
     */
    public List startsWith(int aInsIndex)
            throws RevEngineException {

        List branchEntries = new Vector();
        for (int i = 0; i < branches.size(); i++) {
            JBranchEntry jbe = (JBranchEntry) branches.get(i);
            if (jbe.doesStartWith(aInsIndex)) {
                branchEntries.add(jbe);
            }
        }
        return branchEntries;
    }

    /**
     * Delete the branch that corresponds to a else ..
     * branch starting with the given Pc
     *
     * @param startElse PC for which the else statement
     *                  is to be deleted.
     */
    public void deleteElse(int startElse) {
        for (int i = 0; i < branches.size(); i++) {
            JBranchEntry jbe = (JBranchEntry) branches.get(i);
            if (jbe.getType() == TYPE_ELSE
                    && jbe.getStartPc() == startElse) {
                branches.remove(i);
            }
        }
    }

    /**
     * Returns the first branch in the mentioned branchlist
     * that matches the particular type.
     *
     * @param listBranchEntries list of branch entries.
     * @param type              Type that is to be searched for.
     * @return first branch entry that matches the type mentioned
     *         in the list given.
     */
    public static JBranchEntry contains(List listBranchEntries,
                                        int type) {
        if (listBranchEntries.size() == 0) {
            return null;
        }
        for (int i = 0; i < listBranchEntries.size(); i++) {
            JBranchEntry ent = (JBranchEntry) listBranchEntries.get(i);
            if (ent.getType() == type) {
                return ent;
            }
        }
        return null;
    }

    /**
     * @param byteIns BytecodeInstruction List.
     * @param start   StartPc.
     * @param end     EndPc.
     * @return Returns a JInstruction reference.
     */
    public JInstruction findGotoIns(List byteIns, int start, int end) {
        int i;
        for (i = 0; i < byteIns.size(); i++) {
            if (((JInstruction) byteIns.get(i)).index == start) {
                break;
            }
        }
        JInstruction ins = (JInstruction) byteIns.get(i);
        JInstruction curIns = ins;
        while (curIns != null
                && curIns.opcode != OPCODE_GOTO
                && curIns.opcode != OPCODE_GOTOW) {
            if (curIns.index == end) {
                curIns = null;
                break;
            } else if (curIns.opcode == OPCODE_RETURN) {
                curIns = curIns.next();
                break;
            }
            curIns = curIns.next();
        }
        return curIns;
    }

    /**
     * Stringifies the braches alone.
     *
     * @return Returns a Stringifed version of the branches alone.
     */
    public String branchesToString() {
        StringBuffer sb = new StringBuffer("");
        int size = branches.size();
        if (size > 0) {
            sb.append("Branches:\n");
            for (int i = 0; i < size; i++) {
                sb.append((JBranchEntry) branches.get(i) + "\n");
            }
        }
        return sb.toString();
    }

    /**
     * @return Stringified form of the class
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("");
        sb.append(branchesToString());
        int size = gotos.size();
        if (size > 0) {
            sb.append("Gotos:\n");
            Iterator it = gotos.entrySet().iterator();
            for (int i = 0; i < size; i++) {
                sb.append(it.next() + "\n");
            }
        }
        size = mJSRTarget.size();
        if (size > 0) {
            sb.append("JSRTargets:\n");
            for (int i = 0; i < size; i++) {
                sb.append(mJSRTarget.get(i) + "\n");
            }
        }
        return sb.toString();
    }
}
