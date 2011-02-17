/**
 * @(#)JCollatingTable.java
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

import jreversepro.common.KeyWords;
import jreversepro.reflect.JInstruction;
import jreversepro.reflect.JMethod;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * JCollating table is responsible for collating the table objects.
 *
 * @author Karthik Kumar
 */
public class JCollatingTable implements BranchConstants,
        KeyWords {


    /**
     * List of branches. The individual members are JBranchEntry.
     */
    private List branches;

    /**
     * List of entries in an array format.
     */
    private JBranchEntry[] entries;

    /**
     * Current method in which the branch entry resides.
     */
    private JMethod method;

    /**
     * @param method Method in which this collating
     *               table entry is present,
     */
    public JCollatingTable(JMethod method) {
        this.method = method;
        branches = new Vector();
        entries = null;
    }

    /**
     * Finalizer.
     */
    protected void finalize() {
        branches = null;
        entries = null;
    }

    /**
     * This method prunes the entries, removes all those branches
     * whose type are TYPE_INVALID.
     *
     * @return List of branch of entries all of which are significant.
     *         The members of the entries are all - JBranchEntry.
     */
    public List getEffectiveBranches() {
        List listBranches = new Vector();
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].getType() != TYPE_INVALID) {
                listBranches.add(entries[i]);
            }
        }
        return listBranches;
    }


    /**
     * @param thisIns Instruction - usually a if_xyz opcode.
     * @param startPc StartPc of the conditional branch.
     * @param type    Type of the branch
     * @param opr1    Operand 1.
     * @param opr2    Operand 2.
     */
    public void addConditionalBranch(JInstruction thisIns,
                                     int startPc, int type,
                                     String opr1, String opr2) {
        JBranchEntry thisent =
                new JBranchEntry(
                        method,
                        startPc,
                        thisIns.index + 3,
                        thisIns.getTargetPc(), type,
                        opr1, opr2, thisIns.getConditionalOperator());
        branches.add(thisent);
    }


    /**
     * Sorts the branches - List.
     */
    public void sort() {
        Collections.sort(branches, new JBranchComparator());
    }

    /**
     * Copies the elements in Vector list to the array
     * of JBranchEntry.
     *
     * @return Returns the number of elements in the Vector.
     */
    private int convertToObjects() {
        int size = branches.size();
        entries = new JBranchEntry[size];
        for (int i = 0; i < size; i++) {
            entries[i] = (JBranchEntry) branches.get(i);
        }
        return size;
    }


    /**
     * This collates the information of the BranchTable to the
     * Java-compiler Readable branches.
     * <p>
     * StartPc  TargetPc NextPc   in that order<br>
     * x   y    z<br>
     * z   y    p  Case1 <br><br>
     * <p/>
     * x   y    z<br>
     * z   p    q  Case2 <br>
     * </p>
     */
    public void collate() {
        int numBranches = convertToObjects();
        if (numBranches == 0) {
            return;
            // No Branches at All. So return.
        }
        boolean ifBranch;
        for (int i = numBranches - 1; i > 0;) {
            ifBranch = entries[i].collate();
            int j = i - 1;
            for (j = i - 1; j >= 0; j--) {
                if (entries[j].getNextPc() != entries[j + 1].getStartPc()) {
                    break;
                }
                //End of a successive related branch.
                if (entries[j].getType() == TYPE_JSR) {
                    break;
                }

                if (checkCase1(j, i)) {
                    entries[j].writeCase(true,
                            ifBranch,
                            entries[j + 1]);
                } else if (checkCase2(j, i)) {
                    entries[j].writeCase(false,
                            ifBranch,
                            entries[j + 1]);
                } else {
                    break;
                }
            }
            i = j;//ReAssign i
        }
        //Collate the First entry.
        entries[0].collate();
    }

    /**
     * Checks for Case 1  type collate
     * <p><br><code>
     * a:     x    y   z   <br>
     * b:      y   p1  p2 <br>
     * z:       <br><br></code>
     * This means either a 'IF OR '  or 'WHILE AND' between the
     * statements.</p>
     *
     * @param a Entry index 1
     * @param b entry index 2
     * @return Returns true, if this corresponds to case 2 as mentioned
     *         above. false, otherwise.
     */
    private boolean checkCase1(int a, int b) {
        return (entries[a].getTargetPc() == entries[b].getNextPc());
    }

    /**
     * @param a Entry index 1
     * @param b entry index 2
     * @return Returns true, if this corresponds to case 2 as mentioned
     *         above. false, otherwise.
     *         <p/>
     *         Checks for Case 2  type collate
     *         <p><br><code>
     *         a:     x    y   z   <br>
     *         b:      y   p1  z  OR <br>
     *         <p/>
     *         a:     x    y   z   <br>
     *         z:      .....       <br>
     *         b:      y   p1  p2 <br><br></code>
     *         This means either a 'IF AND'  or 'WHILE OR' between the
     *         statements.</p>
     */
    private boolean checkCase2(int a, int b) {
        if (entries[a].getTargetPc() == entries[b].getTargetPc()) {
            return true;
        } else {
            if ((b - a) > 1) {
                for (int k = b; k > a; k--) {
                    if (entries[a].getTargetPc() == entries[k].getStartPc()) {
                        return true;
                    }
                }
                return false;
            } else {
                return false;
            }
        }
    }

    /**
     * Identifies the while loop in the list of branches mentioned.
     *
     * @param mapGotos Map containing the goto entries in the
     *                 method.
     */
    public void identifyWhileLoops(Map mapGotos) {
        for (int i = 0; i < branches.size(); i++) {
            JBranchEntry ent = (JBranchEntry) branches.get(i);
            if (ent.getType() == TYPE_IF) {
                int targetPc = ent.getTargetPc();
                int startPc = ent.getStartPc();
                Object obj = mapGotos.get(new Integer(targetPc - 3));
                if (obj != null) {
                    int gotoTarget = ((Integer) obj).intValue();
                    if (startPc == gotoTarget) {
                        ent.convertToWhile();
                    }
                }
            }
        }
    }

    /**
     * @return Returns a Stringified format of the class.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("\n");
        sb.append(branches + "\n");
        return sb.append("\n").toString();
    }
}
