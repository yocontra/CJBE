/*
 * @(#)JCaseEntry.java
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
 */
package jreversepro.revengine;

import jreversepro.common.KeyWords;

import java.util.List;
import java.util.Vector;


/**
 * <b>JCaseEntry</b> is the abstract representation of a case entry
 *
 * @author Karthik Kumar
 */
public class JCaseEntry implements KeyWords {
    /**
     * List of case targets that have similar target.
     * Ordinarily they have just one entry. But sometimes they
     * may have more than one entry.
     * For eg.
     * <p/>
     * case 12:
     * case 13:
     * case 18:
     * <<do Something>>
     * <p/>
     * In this case there will be three entries in the list.
     */
    List values;

    /**
     * Target of this group of case entry.
     */
    int target;

    /**
     * End Pc of this case target and the beginnin of the next target.
     */
    int endTarget;

    /**
     * Empty constructor.
     */
    public JCaseEntry() {
        values = new Vector();
    }

    /**
     * @param name     Name of the case target.
     * @param targetPc PC of the corresponding handler for this
     *                 case target.
     */
    public JCaseEntry(String name, int targetPc) {
        target = targetPc;
        values = new Vector();
        values.add(name);
    }

    /**
     * Adds another case target.
     *
     * @param name Name of the case target.
     */
    public void addValue(String name) {
        values.add(name);
    }

    /**
     * @return Returns the List of case targets.
     *         Members are 'String'.
     */
    public List getValues() {
        return values;
    }

    /**
     * @return Returns the targetPc of the beginning branch
     */
    public int getTarget() {
        return target;
    }

    /**
     * Setter method for TargetPc
     *
     * @param targetPc TargetPc
     */
    public void setTarget(int targetPc) {
        target = targetPc;
    }

    /**
     * @return Returns End of the target for this case block.
     */
    public int getEndTarget() {
        return endTarget;
    }

    /**
     * Setter method for endTarget.
     *
     * @param endTarget End Target for this case group block.
     */
    public void setEndTarget(int endTarget) {
        this.endTarget = endTarget;
    }

    /**
     * Returns a string with the case targets and the
     * correpoding branch PCs listed.
     *
     * @return Disassembled piece of code.
     */
    public String disAssemble() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i) + ":\n\t\t\t");
        }
        sb.append("\n\t\t\t\t" + GOTO + " " + target);
        return sb.toString();
    }

    /**
     * @return Returns a Stringified form of the class.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(disAssemble() + " upto " + endTarget);
        return sb.toString();
    }
}
