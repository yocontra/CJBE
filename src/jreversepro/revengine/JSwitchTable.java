/**
 * @(#)JSwitchTable.java
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
import jreversepro.common.JJvmOpcodes;
import jreversepro.common.KeyWords;
import jreversepro.reflect.JInstruction;
import jreversepro.reflect.JMethod;
import jreversepro.runtime.Operand;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;


/**
 * JSwitchTable represents the 'switch' statement as entry pairs as
 * follows.
 * Pair = { CaseValues, Targets }.
 *
 * @author Karthik Kumar
 */
public class JSwitchTable implements KeyWords,
        JJvmOpcodes {
    /**
     * Instruction index of the switch statement.
     */
    int insIndex;

    /**
     * List of cases that are available.
     * Individual elements are JCaseEntry.
     */
    List cases;

    /**
     * DefaultByte for this switch statement.
     */
    int defaultByte;

    /**
     * Max Target from this switch statement group *
     */
    int maxTarget;

    /**
     * Name of the variable that is put under switch statement.
     */
    String varName;

    /**
     * Datatype of the variable for which switch is used.
     */
    String datatype;

    /**
     * Reference to method to which this
     * switch table belongs.
     */
    JMethod method;

    /**
     * @param method Reference to current method.
     * @param ins    Instruction that corresponds to a
     *               tableswitch or a  lookupswitch instruction.
     * @param gotos  Map of goto statements.
     * @throws IOException        thrown in case of any error.
     * @throws RevEngineException if the instruction passed is not
     *                            a switch opcode.
     */
    public JSwitchTable(JMethod method, JInstruction ins, Map gotos)
            throws RevEngineException,
            IOException {

        this.method = method;
        insIndex = ins.index;
        cases = new Vector();

        if (ins.opcode == OPCODE_TABLESWITCH) {
            createTableSwitch(ins.args, ins.index, gotos);
        } else if (ins.opcode == OPCODE_LOOKUPSWITCH) {
            createLookupSwitch(ins.args, ins.index, gotos);
        } else {
            throw new RevEngineException("Not a switch statement");
        }
        datatype = null;
    }

    /**
     * @param method Reference to current method.
     * @param ins    Instruction that corresponds to a
     *               tableswitch or a  lookupswitch instruction.
     * @param op1    Operand that is to be used inside the switch statement.
     * @param gotos  Map of goto statements.
     * @throws IOException        thrown in case of any error.
     * @throws RevEngineException if the instruction passed is not
     *                            a switch opcode.
     */
    public JSwitchTable(JMethod method, JInstruction ins, Operand op1,
                        Map gotos)
            throws RevEngineException,
            IOException {
        this.datatype = op1.getDatatype();
        this.varName = op1.getValue();

        //Copy - paste from prev. constructor.
        this.method = method;
        insIndex = ins.index;
        cases = new Vector();

        if (ins.opcode == OPCODE_TABLESWITCH) {
            createTableSwitch(ins.args, ins.index, gotos);
        } else if (ins.opcode == OPCODE_LOOKUPSWITCH) {
            createLookupSwitch(ins.args, ins.index, gotos);
        } else {
            throw new RevEngineException("Not a switch statement");
        }
        Helper.log("switch datatype " + datatype);
    }


    /**
     * @return Returns the default byte of this switch block.
     */
    public int getDefaultByte() {
        return defaultByte;
    }

    /**
     * For 'tableswitch' opcode this fills the data structure -
     * JSwitchTable.
     *
     * @param entries Bytecode entries that contain the case values
     *                and their target opcodes.
     * @param offset  offset is the index of the current tableswitch
     *                instruction into the method bytecode array.
     * @param gotos   Map of goto statements.
     * @throws IOException Thrown in case of an i/o error when reading
     *                     from the bytes.
     */
    private void createTableSwitch(byte[] entries, int offset, Map gotos)
            throws IOException {
        DataInputStream dis = new DataInputStream(
                new ByteArrayInputStream(entries));
        defaultByte = dis.readInt() + offset;
        int lowVal = dis.readInt();
        int highVal = dis.readInt();

        Map mapCases = new HashMap();
        for (int i = lowVal; i <= highVal; i++) {
            int curTarget = dis.readInt() + offset;
            String value = Helper.getValue(String.valueOf(i),
                    this.datatype);
            Object obj = mapCases.get(new Integer(curTarget));
            if (obj == null) {
                mapCases.put(new Integer(curTarget),
                        new JCaseEntry(value, curTarget));
            } else {
                JCaseEntry ent = (JCaseEntry) obj;
                ent.addValue(value);
            }
        }
        cases = new Vector(mapCases.values());
        dis.close();
        processData(gotos);
    }

    /**
     * For 'lookupswitch' opcode this fills the data structure -
     * JSwitchTable.
     *
     * @param entries Bytecode entries that contain the case values
     *                and their target opcodes.
     * @param offset  offset is the index of the current lookupswitch
     *                instruction into the method bytecode array.
     * @param gotos   Map of goto statements.
     * @throws IOException Thrown in case of an i/o error when reading
     *                     from the bytes.
     */
    private void createLookupSwitch(byte[] entries, int offset, Map gotos)
            throws IOException {
        DataInputStream dis = new DataInputStream(
                new ByteArrayInputStream(entries));

        defaultByte = dis.readInt() + offset;
        int numVal = dis.readInt();

        Map mapCases = new HashMap();

        for (int i = 0; i < numVal; i++) {
            String value =
                    Helper.getValue(String.valueOf(dis.readInt()),
                            datatype);
            int curTarget = dis.readInt() + offset;

            Object obj = mapCases.get(new Integer(curTarget));
            if (obj == null) {
                mapCases.put(
                        new Integer(curTarget),
                        new JCaseEntry(value, curTarget));
            } else {
                JCaseEntry ent = (JCaseEntry) obj;
                ent.addValue(value);
            }
        }
        cases = new Vector(mapCases.values());
        dis.close();
        processData(gotos);
    }

    /**
     * @return Returns the list of cases.
     *         Individual elements are JCaseEntry.
     */
    public List getCases() {
        return cases;
    }

    /**
     * @return Returns the disassembled string for this switch
     *         statement block.
     */
    public String disassemble() {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < cases.size(); i++) {
            sb.append("\n\t\t\t" + cases.get(i));
        }
        sb.append("\n\t\t\tDefault Byte " + defaultByte);
        return sb.toString();
    }

    /**
     * @param rhsType  Type of the switch variable of this block.
     * @param rhsValue Value ( name ) of the switch variable for this
     *                 block.
     */
    public void setTypeValue(String rhsType, String rhsValue) {
        varName = rhsValue;
        datatype = rhsType;
        //dataType could be either int or char.
    }

    /**
     * Process the bytecode stream of case values and targets to
     * individual case blocks.
     *
     * @param gotos Map of goto statements.
     */
    public void processData(Map gotos) {
        maxTarget = defaultByte;
        if (gotos != null) {
            for (int i = 0; i < cases.size() - 1; i++) {
                JCaseEntry ent = (JCaseEntry) cases.get(i);
                Object obj = gotos.get(new Integer(ent.getTarget() - 3));
                if (obj != null) {
                    int tempVal = ((Integer) obj).intValue();
                    maxTarget = (maxTarget > tempVal) ? maxTarget : tempVal;
                }
            }
            if (maxTarget > defaultByte) {
                boolean targetPresent = false;
                for (int i = 0; i < cases.size() - 1; i++) {
                    JCaseEntry ent = (JCaseEntry) cases.get(i);
                    if (ent.getTarget() == defaultByte) {
                        ent.addValue(DEFAULT);
                        //ent.setTarget(defa);
                        targetPresent = true;
                    }
                }
                if (!targetPresent) {
                    cases.add(new JCaseEntry(DEFAULT, defaultByte));
                }
            }
        }

        //Sort the entries
        Collections.sort(cases, new JCaseComparator());

        //Assign endTargets for all of them.
        int i = 0;
        for (; i < cases.size() - 1; i++) {
            JCaseEntry ent = (JCaseEntry) cases.get(i);
            JCaseEntry entNext = (JCaseEntry) cases.get(i + 1);
            ent.setEndTarget(entNext.getTarget());
        }
        JCaseEntry entLast = (JCaseEntry) cases.get(i);
        entLast.setEndTarget(maxTarget);
    }

    /**
     * @return Returns a branch entry for this switch statement.
     */
    public JBranchEntry getBranchEntry() {
        return new JBranchEntry(method,
                insIndex, maxTarget, maxTarget,
                BranchConstants.TYPE_SWITCH,
                varName, "", "");
    }

    /**
     * Inserts a CaseEntry in the list.
     *
     * @param caseEntry Case Entry to be appended.
     */
    public void addCaseEntry(JCaseEntry caseEntry) {
        cases.add(caseEntry);
    }

    /**
     * @return Returns the Stringified version of this class.
     */
    public String toString() {
        return cases.toString();
    }
}
