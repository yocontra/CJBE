/*
 * @(#)JLocalEntry.java
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * LocalEntry is a entry in the SymbolTable.
 *
 * @author Karthik Kumar
 */
public class JLocalEntry {

    /**
     * Opcode index when this was first stored / loaded into the
     * symbol table with a value. For arguments to method this value
     * is redundant.
     */
    final int storeIndex;

    /**
     * Index of the variable into the local symbol table.
     */
    int varIndex;

    /**
     * datatype of this local variable entry
     */
    String datatype;

    /**
     * Name of the variable in this
     */
    String name;

    /**
     * Set if the variable is going to be declared.
     */
    boolean declared;

    /**
     * opcode index when this variable was last referred to.
     */
    int lastReferredIndex;

    /**
     * set of opcode indexes when this variable was referred to.
     */
    Set references;

    /**
     * @param aVarIndex   Variable Index into the Symbol Table
     * @param aStoreIndex Opcode index into the method bytecode
     *                    array when this variable stored / first initialized.
     * @param datatype    datatype of the variable
     * @param aName       Name of the variable.
     * @param aDeclared   If this variable is declared or not.
     */
    public JLocalEntry(int aVarIndex, int aStoreIndex,
                       String datatype, String aName,
                       boolean aDeclared) {
        this.varIndex = aVarIndex;
        this.datatype = datatype;
        this.storeIndex = aStoreIndex;
        this.name = aName;
        this.declared = aDeclared;
        lastReferredIndex = 0;
        references = new HashSet();
    }

    /**
     * Setter method for name,
     *
     * @param name Name to be assigned.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the name of the variable.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the store index of this variable,
     */
    public int getStoreIndex() {
        return storeIndex;
    }

    /**
     * @return Returns Variable Index into the bytecode array
     *         for this method.
     */
    public int getVarIndex() {
        return varIndex;
    }

    /**
     * Sets the last ReferredIndex of this line.
     *
     * @param index Last ReferredIndex of the variable.
     */
    public void setLastReferredIndex(int index) {
        references.add(new Integer(index));
        lastReferredIndex = (lastReferredIndex > index)
                ? lastReferredIndex : index;
    }

    /**
     * @return Returns the last referred index of this variable.
     */
    public int getLastReferredIndex() {
        return lastReferredIndex;
    }

    /**
     * @return Returns if the variable is declared/not.
     */
    public boolean isDeclared() {
        return declared;
    }

    /**
     * Declare this local variable.
     */
    public void declareVariable() {
        declared = true;
    }

    /**
     * @return Returns the declaration type.
     */
    public String getDeclarationType() {
        return datatype;
    }

    /**
     * @param aType Declaration Type to be set.
     */
    public void setDeclarationType(String aType) {
        datatype = aType;
    }

    /**
     * In case a variable is not declared, this reassigns the type
     * of the variable.
     *
     * @param datatype New datatype to be reassigned.
     * @return Returns true, if datatype is reassigned,
     *         false if no change made to datatype and re-assignation ignored.
     */
    public boolean reassignType(String datatype) {
        if (!this.datatype.equals(datatype)
                && !declared) {
            this.datatype = datatype;
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param obj Object to be compared.
     * @return if both object are equal. false, otherwise.
     */
    public boolean equals(Object obj) {
        if (obj instanceof JLocalEntry) {
            JLocalEntry jle = (JLocalEntry) obj;
            return (this.datatype.equals(jle.datatype));
        } else {
            return false;
        }
    }

    /**
     * @return Hashcode of this method.
     */
    public int hashCode() {
        return datatype.hashCode();
    }

    /**
     * @return Returns the string fields form, add all references.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{ " + declared + "  "
                + datatype + "  " + name + "  "
                + storeIndex + " LastRef  "
                + lastReferredIndex + " }\n");
        Iterator i = references.iterator();

        if (i.hasNext()) {
            sb.append(" Ref [ ");
            while (i.hasNext()) {
                sb.append(((Integer) i.next()).intValue() + " ");
            }
            sb.append("]");
        }
        return sb.toString();
    }
}
