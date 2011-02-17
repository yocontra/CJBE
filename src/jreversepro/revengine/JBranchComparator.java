/**
 * @(#)JBranchComparator.java
 * JReversePro - Java Decompiler / Disassembler.
 * Copyright (C) 2000 2001 2002 Karthik Kumar.
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

import java.util.Comparator;

/**
 * Comparator for comparing two branch entries.
 *
 * @author Karthik Kumar.
 */
public class JBranchComparator implements Comparator {

    /**
     * Method to compare two JBranchComparator objects
     *
     * @param o1 First Object to be compared.
     * @param o2 Second object to be compared.
     * @return -1 if First branch overlaps Second branch,
     *         1 if first branch doesnt overlap and exactly outside the second
     *         branch.
     */
    public int compare(Object o1, Object o2) {
        if (!(o1 instanceof JBranchEntry)
                || !(o2 instanceof JBranchEntry)) {
            System.err.println("Not a BranchEntry");
            return 0;
        }
        try {
            JBranchEntry e1 = (JBranchEntry) o1;
            JBranchEntry e2 = (JBranchEntry) o2;
            int exec1 = e1.getStartExecPc();
            int exec2 = e2.getStartExecPc();
            int end1 = e1.getEndBlockPc();
            int end2 = e2.getEndBlockPc();

            if (exec1 < exec2) {
                return -1;
            } else if (exec1 == exec2) {
                if (end1 > end2) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        } catch (RevEngineException ree) {
            return -1;
        }

    }

    /**
     * @param obj R.H.S object to be compared.
     * @return true, if both are equal.
     *         false, otherwise.
     */
    public boolean equals(Object obj) {
        return (obj instanceof JBranchEntry);
    }
}