/*
 * @(#)JDisAssembler.java
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


import jreversepro.common.JJvmOpcodes;
import jreversepro.parser.ClassParserException;
import jreversepro.reflect.JConstantPool;
import jreversepro.reflect.JImport;
import jreversepro.reflect.JInstruction;
import jreversepro.reflect.JMethod;
import jreversepro.runtime.JSymbolTable;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

//import jreversepro.common.Helper;
//import jreversepro.common.JJvmSet;

/**
 * <b>JDisAssembler</b> writes out the assebly byte codes.
 *
 * @author Karthik Kumar
 */
public class JDisAssembler implements JReverseEngineer, JJvmOpcodes {

    /**
     * byteIns is the Vector of instructions. Individual elements are
     * JInstruction.
     */
    List byteIns;

    /**
     * ConstantPool Information.
     */
    JConstantPool cpInfo;

    /**
     * Reference to current method.
     */
    JMethod curMethod;

    /**
     * @param rhsMethod Method for which the disassembler is going to act.
     * @param rhsCpInfo ConstantPool Information.
     */
    public JDisAssembler(JMethod rhsMethod,
                         JConstantPool rhsCpInfo) {
        byteIns = rhsMethod.getInstructions();
        cpInfo = rhsCpInfo;
        curMethod = rhsMethod;
    }

    /**
     * Generates the disassembled code.
     *
     * @throws IOException          Thrown incase we encounter any i/o erro
     *                              while disassembling.
     * @throws RevEngineException   Thrown in case we encounter any
     *                              logical error while trying to disasseble.
     * @throws ClassParserException Thrown in case of an invalid
     *                              constantpool reference.
     */
    public void genCode()
            throws RevEngineException,
            IOException,
            ClassParserException {

        StringBuffer assembly = new StringBuffer("\t{");
        //assembly.append(JDecompiler.getMethodHeaders(curMethod, null));
        assembly.append(curMethod.getLocalStackInfo());

        try {
            JImport importInfo = cpInfo.getImportedClasses();
            curMethod.setSymbolTable(
                    new JSymbolTable(curMethod, importInfo));

            Enumeration
            enum=Collections.enumeration(byteIns);
            while (
            enum.hasMoreElements()){
                JInstruction thisIns = (JInstruction)
                enum.nextElement();
                if (thisIns.opcode == OPCODE_TABLESWITCH) {
                    JSwitchTable switches = new JSwitchTable(
                            curMethod, thisIns, null);
                    assembly.append("\n\t\t" + thisIns.index + ": ");
                    assembly.append("tableswitch ");
                    assembly.append(switches.disassemble());
                } else if (thisIns.opcode == OPCODE_LOOKUPSWITCH) {
                    JSwitchTable switches = new JSwitchTable(
                            curMethod, thisIns, null);
                    assembly.append("\n\t\t" + thisIns.index + ": ");
                    assembly.append("lookupswitch ");
                    assembly.append(switches.disassemble());
                } else if (thisIns.opcode == OPCODE_WIDE) {
                    //Handling to be done here.
                } else {
                    dealDefault(assembly, thisIns);
                }
            }
        } catch (RevEngineException ree) {
            assembly.append(DECOMPILE_FAILED_MSG);
        }
        assembly.append("\n\t}");
        curMethod.setStringifiedBytecode(assembly.toString());
        //curmethod.setSymbolTable(symTable);
        // donno why -- doesnt seem to be initialized.
    }

    /**
     * Method to generate code for normal instructions.
     * Switch and wide instructions need special care.
     * See JSwitchTable for more details.
     * Wide instructions are not supported as of now.
     *
     * @param assembly StringBuffer containing the code generated.
     * @param thisIns  Current Instruction.
     * @throws ClassParserException Thrown in case of an invalid
     *                              constantpool reference.
     */
    private void dealDefault(StringBuffer assembly, JInstruction thisIns)
            throws ClassParserException {
        assembly.append("\n\t\t" + thisIns.index + ": ");
        String ins = thisIns.getInsName();
        assembly.append(ins);
        if (thisIns.args == null) {
            return;
        }
        int len = thisIns.args.length;
        if (len == 1) {
            int thisByte = thisIns.getArgUnsignedByte();
            if (thisIns.opcode >= OPCODE_LDC
                    && thisIns.opcode <= OPCODE_LDC2_W) {

                assembly.append(" #");
                //Indicates an index to the Constant Pool
                assembly.append(thisByte + " <");
                assembly.append(cpInfo.getTagDescriptor(thisByte));
                assembly.append(" >");
            } else {
                assembly.append(" " + thisByte);
            }
        } else if (len == 2) {
            int value = thisIns.getArgUnsignedShort();
            assembly.append(" ");
            if (ins.indexOf("if") != -1
                    || thisIns.opcode == OPCODE_GOTO
                    || thisIns.opcode == OPCODE_JSR) {

                assembly.append(thisIns.getTargetPc());
            } else if (thisIns.opcode == OPCODE_IINC) {
                assembly.append(thisIns.getArgUnsignedByte() + " "
                        + thisIns.getArgUnsignedByte(1));
            } else if (thisIns.opcode == OPCODE_SIPUSH) {
                assembly.append(value);
            } else {
                assembly.append("#");
                //Indicates an index to the Constant Pool
                assembly.append(value + " <");
                assembly.append(cpInfo.getTagDescriptor(value));
                assembly.append(" >");
            }
        } else if (len == 4) {
            int value = thisIns.getArgUnsignedShort();
            assembly.append(" #");
            //Indicates an index to the Constant Pool
            assembly.append(value + " <");
            assembly.append(cpInfo.getTagDescriptor(value));
            assembly.append(" >");
        }
    }
}
