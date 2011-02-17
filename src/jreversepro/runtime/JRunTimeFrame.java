/**
 * @(#)JRunTimeFrame.java
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
import jreversepro.common.JJvmOpcodes;
import jreversepro.common.KeyWords;
import jreversepro.parser.ClassParserException;
import jreversepro.reflect.JConstantPool;
import jreversepro.reflect.JImport;
import jreversepro.reflect.JInstruction;
import jreversepro.revengine.BranchConstants;
import jreversepro.revengine.RevEngineException;

import java.util.ArrayList;
import java.util.List;

/**
 * This contains the Stack operations of the JVM opcodes.
 *
 * @author Karthik Kumar
 */
public class JRunTimeFrame implements KeyWords,
        OperandConstants,
        BranchConstants,
        JJvmOpcodes {

    /**
     * ConstantPool reference
     */
    final JConstantPool cpInfo;

    /**
     * SymbolTable reference.
     */
    JSymbolTable symLocal;

    /**
     * Information related to imported classes.
     */
    JImport importInfo;

    /**
     * Return type of the method that is decompiled
     * currently
     */
    String returnType;


    /**
     * Appends the source code that is necessary for this.
     */
    String statement;

    /**
     * Precedence
     */
    int precedence;

    /**
     * One of the operands in case of a if condition.
     */
    String strOp1;

    /**
     * Second operand in case of a if condition.
     */
    String strOp2;

    /**
     * Opcode that was previously examined in this frame.
     */
    int prevCode;

    /**
     * Operand of the object invoked. aMainly in the case
     * of invokestatic invokespecial invokeinterface
     * invokevirtual opcodes.
     */
    Operand invokedObject;

    /**
     * @param rhsCpInfo     ConstantPool Information
     * @param rhsSymTable   Symbol Table reference.
     * @param rhsReturnType Return type of the method.
     */
    public JRunTimeFrame(JConstantPool rhsCpInfo,
                         JSymbolTable rhsSymTable,
                         String rhsReturnType) {
        cpInfo = rhsCpInfo;
        symLocal = rhsSymTable;
        returnType = rhsReturnType;
        this.importInfo = rhsCpInfo.getImportedClasses();

    }

    /**
     * @return Returns Operand 1
     */
    public String getOpr1() {
        return strOp1;
    }

    /**
     * @return Returns Operand 2.
     */
    public String getOpr2() {
        return strOp2;
    }

    /**
     * @return Statement that is being appended inside.
     */
    public String getStatement() {
        return statement;
    }

    /**
     * @return Returns object that is invoked.
     */
    public Operand getInvokedObject() {
        return invokedObject;
    }


    /**
     * @param thisIns Current Instruction that is to be
     *                operated on the JVM stack.
     * @param myStack The JVM OperandStack of the current method
     *                that is under reference.
     * @throws RevEngineException   Thrown in case we get any error
     *                              while operating the current instruction on the current
     *                              JVM stack.
     * @throws ClassParserException Thrown in case of an invalid
     *                              constantpool reference.
     */
    public void operateStack(final JInstruction thisIns,
                             JOperandStack myStack)
            throws RevEngineException,
            ClassParserException {

        int opcode = thisIns.opcode;
        if (opcode >= 0 && opcode < 32) {
            opr0to31(thisIns, myStack);
        } else if (opcode >= 32 && opcode < 54) {
            opr32to53(thisIns, myStack);
        } else if (opcode >= 54 && opcode < 87) {
            opr54to86(thisIns, myStack);
        } else if (opcode >= 87 && opcode < 101) {
            opr87to100(thisIns, myStack);
        } else if (opcode >= 101 && opcode < 128) {
            opr101to127(thisIns, myStack);
        } else if (opcode >= 128 && opcode < 148) {
            opr128to147(thisIns, myStack);
        } else if (opcode >= 148 && opcode < 172) {
            opr148to171(thisIns, myStack);
        } else if (opcode >= 172 && opcode < 187) {
            opr172to186(thisIns, myStack);
        } else if (opcode >= 187 && opcode < 202) {
            opr187to201(thisIns, myStack);
        } else if (opcode >= 202) {
            opr202to255(thisIns, myStack);
        }
        prevCode = opcode;
    }

    /**
     * Executes the JVM Opcodes from 0 - 31
     *
     * @param thisIns Current Instruction that is to be
     *                operated on the JVM stack.
     * @param myStack The JVM OperandStack of the current method
     *                that is under reference.
     * @throws RevEngineException   Thrown in case we get any error
     *                              while operating the current instruction on the current
     *                              JVM stack.
     * @throws ClassParserException Thrown in case of any error
     *                              in constantpool reference.
     */
    void opr0to31(final JInstruction thisIns, JOperandStack myStack)
            throws RevEngineException,
            ClassParserException {

        switch (thisIns.opcode) {
            case 0: { // null
                // Doesn't affect stack.
                break;
            }

            case 1: { // aconst_null
                myStack.push(NULL, REFERENCE, VALUE);
                break;
            }

            case 2: // iconst_ml
            case 3: // iconst_0
            case 4: // iconst_1
            case 5: // iconst_2
            case 6: // iconst_3
            case 7: // iconst_4
            case 8: // iconst_5
            {
                int val = thisIns.opcode - 3;
                myStack.push(val, INT, VALUE);
                break;
            }

            case 9:  // lconst_0
            case 10: // lconst_1
            {
                int val = thisIns.opcode - 9;
                myStack.push(val, LONG, VALUE);
                break;
            }

            case 11: { //fconst_0
                myStack.push("0.0", FLOAT, VALUE);
                break;
            }
            case 12: { //fconst_1
                myStack.push("1.0", FLOAT, VALUE);
                break;
            }
            case 13: { //fconst_2
                myStack.push("2.0", FLOAT, VALUE);
                break;
            }
            case 14: { // dconst_0
                myStack.push("0.0", DOUBLE, VALUE);
                break;
            }
            case 15: { //dconst_1
                myStack.push("1.0", DOUBLE, VALUE);
                break;
            }
            case 16: { //bipush
                myStack.push(thisIns.getArgByte(), BYTE, VALUE);
                break;
            }
            case 17: { //sipush
                //Sign Extend This
                myStack.push(thisIns.getArgShort(), SHORT, VALUE);
                break;
            }
            case 18: { //ldc
                // Utf8 Value is referred to here.
                int ldcIndex = thisIns.getArgUnsignedByte();
                String ldcString = cpInfo.getLdcString(ldcIndex);
                myStack.push(ldcString,
                        cpInfo.getDataType(ldcIndex), VALUE);
                break;
            }
            case 19: { //ldc_w
                int ldcIndex = thisIns.getArgUnsignedShort();
                myStack.push(cpInfo.getLdcString(ldcIndex),
                        CLASS_STRING, VALUE);
                break;
            }
            case 20: { // ldc2_w
                int ldcIndex = thisIns.getArgUnsignedShort();
                myStack.push(cpInfo.getCpValue(ldcIndex),
                        cpInfo.getDataType(ldcIndex), VALUE);
                break;
            }
            case 21: { //iload
                String localVar = symLocal.getName(
                        thisIns.getArgUnsignedWide(),
                        thisIns.index);

                String dataType = symLocal.getDataType(
                        thisIns.getArgUnsignedWide(),
                        thisIns.index);

                if (dataType == null) {
                    dataType = INT;
                }

                Operand op1 = (myStack.size() == 0)
                        ? null : (Operand) myStack.peek();

                if (op1 != null
                        && (op1.getValue().equals("++")
                        || op1.getValue().equals("--"))) {

                    op1 = (Operand) myStack.pop();
                    localVar = op1.getValue() + localVar;
                    myStack.push(localVar, dataType, L_UNARY);
                } else {
                    myStack.push(localVar, dataType, VALUE);
                }
                break;
            }
            case 22: { //lload
                String localVar = symLocal.getName(thisIns.getArgUnsignedWide(),
                        thisIns.index);
                myStack.push(localVar, LONG, VALUE);
                break;
            }
            case 23: { //fload
                String localVar = symLocal.getName(thisIns.getArgUnsignedWide(),
                        thisIns.index);
                myStack.push(localVar, FLOAT, VALUE);
                break;
            }
            case 24: { //dload
                String localVar = symLocal.getName(thisIns.getArgUnsignedWide(),
                        thisIns.index);
                myStack.push(localVar, DOUBLE, VALUE);
                break;
            }
            case 25: { //aload
                String localVar = symLocal.getName(
                        thisIns.getArgUnsignedWide(),
                        thisIns.index);

                String localType =
                        symLocal.getDataType(thisIns.getArgUnsignedWide(),
                                thisIns.index);

                myStack.push(localVar, localType, VALUE);
                break;
            }

            case 26: // iload_0
            case 27: // iload_1
            case 28: // iload_2
            case 29: // iload_3
            {
                int regnum = thisIns.opcode - 26;
                String localVar = symLocal.getName(regnum, thisIns.index);
                String datatype = symLocal.getDataType(regnum, thisIns.index);
                if (datatype == null) {
                    datatype = INT;
                }

                Operand op1 = (myStack.size() == 0)
                        ? null : (Operand) myStack.peek();

                if (op1 != null
                        && (op1.getValue().equals("++")
                        || op1.getValue().equals("--"))) {
                    op1 = (Operand) myStack.pop();
                    localVar = op1.getValue() + localVar;
                    myStack.push(localVar, datatype, L_UNARY);
                } else {
                    myStack.push(localVar, datatype, VALUE);
                }
                break;
            }

            case 30: // lload_0
            case 31: // lload_1
            {
                int regnum = thisIns.opcode - 30;
                String localVar = symLocal.getName(regnum, thisIns.index);
                myStack.push(localVar, LONG, VALUE);
                break;
            }
        }
    }


    /**
     * Executes the JVM Opcodes from 32 - 53
     *
     * @param thisIns Current Instruction that is to be
     *                operated on the JVM stack.
     * @param myStack The JVM OperandStack of the current method
     *                that is under reference.
     * @throws RevEngineException Thrown in case we get any error
     *                            while operating the current instruction on the current
     *                            JVM stack.
     */
    void opr32to53(final JInstruction thisIns, JOperandStack myStack)
            throws RevEngineException {
        switch (thisIns.opcode) {
            case 32: { //lload_2
                String localVar = symLocal.getName(2, thisIns.index);
                myStack.push(localVar, LONG, VALUE);
                break;
            }
            case 33: { //lload_3
                String localVar = symLocal.getName(3, thisIns.index);
                myStack.push(localVar, LONG, VALUE);
                break;
            }
            case 34: { //fload_0
                String localVar = symLocal.getName(0, thisIns.index);
                myStack.push(localVar, FLOAT, VALUE);
                break;
            }
            case 35: { //fload_1
                String localVar = symLocal.getName(1, thisIns.index);
                myStack.push(localVar, FLOAT, VALUE);
                break;
            }
            case 36: { //fload_2
                String localVar = symLocal.getName(2, thisIns.index);
                myStack.push(localVar, FLOAT, VALUE);
                break;
            }
            case 37: { //fload_3
                String localVar = symLocal.getName(3, thisIns.index);
                myStack.push(localVar, FLOAT, VALUE);
                break;
            }
            case 38: { //dload_0
                String localVar = symLocal.getName(0, thisIns.index);
                myStack.push(localVar, DOUBLE, VALUE);
                break;
            }
            case 39: { //dload_1
                String localVar = symLocal.getName(1, thisIns.index);
                myStack.push(localVar, DOUBLE, VALUE);
                break;
            }
            case 40: { //dload_2
                String localVar = symLocal.getName(2, thisIns.index);
                myStack.push(localVar, DOUBLE, VALUE);
                break;
            }
            case 41: { //dload_3
                String localVar = symLocal.getName(3, thisIns.index);
                myStack.push(localVar, DOUBLE, VALUE);
                break;
            }
            case 42: { //aload_0
                String localVar = symLocal.getName(0, thisIns.index);
                String localType = symLocal.getDataType(0, thisIns.index);
                myStack.push(localVar, localType, VALUE);
                break;
            }
            case 43: { //aload_1
                String localVar = symLocal.getName(1, thisIns.index);
                String localType = symLocal.getDataType(1, thisIns.index);
                myStack.push(localVar, localType, VALUE);
                break;
            }
            case 44: { //aload_2
                String localVar = symLocal.getName(2, thisIns.index);
                String localType = symLocal.getDataType(2, thisIns.index);
                myStack.push(localVar, localType, VALUE);
                break;
            }
            case 45: { //aload_3
                String localVar = symLocal.getName(3, thisIns.index);
                String localType = symLocal.getDataType(3, thisIns.index);
                myStack.push(localVar, localType, VALUE);
                break;
            }
            case 46: { //iaload
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValue() + "[" + op2.getValue()
                        + "]", INT, VALUE);
                break;
            }
            case 47: { //laload
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValue() + OPEN_BRACKET
                        + op2.getValue() + CLOSE_BRACKET, LONG, VALUE);
                break;
            }
            case 48: { //faload
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValue() + OPEN_BRACKET + op2.getValue()
                        + CLOSE_BRACKET, FLOAT, VALUE);
                break;
            }
            case 49: { //daload
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValue() + OPEN_BRACKET + op2.getValue()
                        + CLOSE_BRACKET, DOUBLE, VALUE);
                break;
            }
            case 50: { //aaload
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValue() + OPEN_BRACKET + op2.getValue()
                        + CLOSE_BRACKET, REFERENCE, VALUE);
                break;
            }
            case 51: { //baload
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValue() + OPEN_BRACKET + op2.getValue()
                        + CLOSE_BRACKET, BOOLEAN, VALUE);
                // byte only
                break;
            }
            case 52: { //caload
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValue() + OPEN_BRACKET + op2.getValue()
                        + CLOSE_BRACKET, CHAR, VALUE);
                break;
            }
            case 53: { //saload
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValue() + OPEN_BRACKET + op2.getValue()
                        + CLOSE_BRACKET, SHORT, VALUE);
                break;
            }
        }
    }

    /**
     * Executes the JVM Opcodes from 54 - 86
     *
     * @param thisIns Current Instruction that is to be
     *                operated on the JVM stack.
     * @param myStack The JVM OperandStack of the current method
     *                that is under reference.
     * @throws RevEngineException Thrown in case we get any error
     *                            while operating the current instruction on the current
     *                            JVM stack.
     */
    void opr54to86(final JInstruction thisIns, JOperandStack myStack)
            throws RevEngineException {
        switch (thisIns.opcode) {
            case 54: // istore
            {
                Operand op1 = (Operand) myStack.pop();
                dealIntegerStore(op1.getValue(),
                        thisIns.getArgUnsignedWide(),
                        thisIns.index);
                break;
            }
            case 55: // lstore
            case 56: // fstore
            case 57: // dstore
            {
                String localVar = symLocal.getName(thisIns.getArgUnsignedWide(),
                        thisIns.index);
                Operand op1 = (Operand) myStack.pop();
                statement = localVar + " = " + op1.getValue();
                precedence = L_EVAL;
                break;
            }
            case 58: // astore
            {
                Operand op1 = (Operand) myStack.pop();
                String localVar = symLocal.getName(
                        thisIns.getArgUnsignedWide(),
                        thisIns.index);

                String varValue = op1.getValue();
                if (op1.getDatatype().indexOf("[") != -1) {
                    List constants = myStack.getConstants();
                    if (constants.size() != 0) {
                        varValue = myStack.getConstantValues();
                        myStack.removeAllConstants();
                    }
                }
                statement = localVar + " = " + varValue;
                precedence = L_EVAL;
                break;
            }
            case 59: // istore_0
            {
                Operand op1 = (Operand) myStack.pop();
                dealIntegerStore(op1.getValue(), 0, thisIns.index);
                break;
            }
            case 63: // lstore_0
            case 67: // fstore_0
            case 71: // dstore_0
            case 75: // astore_0
            {
                String localVar = symLocal.getName(0, thisIns.index);
                Operand op1 = (Operand) myStack.pop();
                statement = localVar + " = " + op1.getValue();
                precedence = L_EVAL;
                break;
            }
            case 60: // istore_1
            {
                Operand op1 = (Operand) myStack.pop();
                dealIntegerStore(op1.getValue(), 1, thisIns.index);
                break;
            }
            case 64: // lstore_1
            case 68: // fstore_1
            case 72: // dstore_1
            case 76: // astore_1
            {
                String localVar = symLocal.getName(1, thisIns.index);
                Operand op1 = (Operand) myStack.pop();
                statement = localVar + " = " + op1.getValue();
                precedence = L_EVAL;
                break;
            }
            case 61: // istore_2
            {
                Operand op1 = (Operand) myStack.pop();
                dealIntegerStore(op1.getValue(), 2, thisIns.index);
                break;
            }
            case 65: // lstore_2
            case 69: // fstore_2
            case 73: // dstore_2
            case 77: // astore_2
            {
                String localVar = symLocal.getName(2, thisIns.index);
                Operand op1 = (Operand) myStack.pop();
                statement = localVar + " = " + op1.getValue();
                precedence = L_EVAL;
                break;
            }
            case 62: // istore_3
            {
                Operand op1 = (Operand) myStack.pop();
                dealIntegerStore(op1.getValue(), 3, thisIns.index);
                break;
            }
            case 66: // lstore_3
            case 70: // fstore_3
            case 74: // dstore_3
            case 78: // astore_3
            {
                String localVar = symLocal.getName(3, thisIns.index);
                Operand op1 = (Operand) myStack.pop();
                statement = localVar + " = " + op1.getValue();
                precedence = L_EVAL;
                break;
            }
            case 79: // iastore
            case 80: // lastore
            case 81: // fastore
            case 82: // dastore
            case 83: // aastore
            case 84: // bastore
            case 85: // castore
            case 86: // sastore
            {
                Operand op3 = (Operand) myStack.pop();
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                String value = op3.getValue();
                String index = op2.getValue();
                String arrayRef = op1.getValue();

                if (arrayRef.indexOf("new") == -1) {
                    statement = arrayRef
                            + OPEN_BRACKET + index
                            + CLOSE_BRACKET + EQUALTO + value;
                } else {
                    statement = "";
                    myStack.addConstant(value);
                }
                precedence = L_EVAL;
                break;
            }
        }
    }

    /**
     * Executes the JVM Opcodes from 87 - 100
     *
     * @param thisIns Current Instruction that is to be
     *                operated on the JVM stack.
     * @param myStack The JVM OperandStack of the current method
     *                that is under reference.
     * @throws RevEngineException Thrown in case we get any error
     *                            while operating the current instruction on the current
     *                            JVM stack.
     */
    void opr87to100(final JInstruction thisIns, JOperandStack myStack)
            throws RevEngineException {
        switch (thisIns.opcode) {
            case OPCODE_POP: { //pop
                Operand op1 = (Operand) myStack.pop();
                String popVal = op1.getValue();
                if (popVal != null && !popVal.equals(FOREIGN_OBJ)) {
                    statement = popVal;
                    precedence = op1.getPrecedence();
                } else {
                    statement = "";
                    precedence = VALUE;
                }
                break;
            }
            case OPCODE_POP2: { //pop2
                //To Assess its use properly.
                Operand op1 = (Operand) myStack.pop();
                if (op1.isCategory1()) {
                    op1 = (Operand) myStack.pop();
                }
                break;
            }
            case OPCODE_DUP: { //dup
                myStack.push((Operand) myStack.peek());
                break;
            }
            case OPCODE_DUP_X1: { //dup_x1
                Operand op1 = (Operand) myStack.pop();
                Operand op2 = (Operand) myStack.pop();
                myStack.push(op1);
                myStack.push(op2);
                myStack.push(op1);
                break;
            }
            case OPCODE_DUP_X2: { // dup_x2
                Operand op1 = (Operand) myStack.pop();
                Operand op2 = (Operand) myStack.pop();
                if (op2.isCategory1()) {
                    // Cat.1
                    Operand op3 = (Operand) myStack.pop();
                    myStack.push(op1);
                    myStack.push(op3);
                } else {
                    // Cat.2
                    myStack.push(op1);
                }
                myStack.push(op2);
                myStack.push(op1);
                break;
            }
            case 92: { // dup2
                Operand op1 = (Operand) myStack.pop();
                if (op1.isCategory1()) {
                    // Cat.1
                    Operand op2 = (Operand) myStack.pop();
                    myStack.push(op2);
                    myStack.push(op1);
                    myStack.push(op2);
                } else {
                    // Cat.2
                    myStack.push(op1);
                }
                myStack.push(op1);
                break;
            }
            case 93: { //dup2_x1
                Operand op1 = (Operand) myStack.pop();
                Operand op2 = (Operand) myStack.pop();
                if (op1.isCategory1()) {
                    // Cat.1
                    Operand op3 = (Operand) myStack.pop();
                    myStack.push(op2);
                    myStack.push(op1);
                    myStack.push(op3);
                } else {
                    // Cat.2
                    myStack.push(op1);
                }
                myStack.push(op2);
                myStack.push(op1);
                break;
            }
            case 94: { //dup2_x2
                Operand op1 = (Operand) myStack.pop();
                Operand op2 = (Operand) myStack.pop();
                if (op1.isCategory1()) {
                    // value1-Cat1
                    Operand op3 = (Operand) myStack.pop();
                    if (op2.isCategory1()) {
                        // value2-Cat1
                        Operand op4 = (Operand) myStack.pop();
                        myStack.push(op2);
                        myStack.push(op1);
                        myStack.push(op4);
                        // Form 1.
                    } else {
                        // value2-Cat2
                        myStack.push(op2);
                        myStack.push(op1);
                        // Form. 3
                    }
                    myStack.push(op3);
                } else {
                    // value1-Cat2
                    if (op2.isCategory1()) {
                        // value2-Cat1
                        Operand op3 = (Operand) myStack.pop();
                        myStack.push(op1);
                        myStack.push(op3);
                        // Form.  2
                    } else {
                        // value2-Cat2
                        myStack.push(op1);
                        // Form 4.
                    }
                }
                myStack.push(op2);
                myStack.push(op1);
                break;
            }
            case 95: { //swap
                Operand op1 = (Operand) myStack.pop();
                Operand op2 = (Operand) myStack.pop();
                myStack.push(op1);
                myStack.push(op2);
                break;
            }
            case 96: { //iadd
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_ADD) + " + "
                        + op2.getValueEx(L_ADD), INT, L_ADD);
                break;
            }
            case 97: { //ladd
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_ADD) + " + "
                        + op2.getValueEx(L_ADD), LONG, L_ADD);
                break;
            }
            case 98: { //fadd
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_ADD) + " + "
                        + op2.getValueEx(L_ADD), FLOAT, L_ADD);
                break;
            }
            case 99: { //dadd
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_ADD) + " + "
                        + op2.getValueEx(L_ADD), DOUBLE, L_ADD);
                break;
            }
            case 100: { //isub
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_SUB) + " - "
                        + op2.getValueEx(L_SUB + 1), INT, L_SUB);
                break;
            }
        }
    }

    /**
     * Executes the JVM Opcodes from 101 - 127
     *
     * @param thisIns Current Instruction that is to be
     *                operated on the JVM stack.
     * @param myStack The JVM OperandStack of the current method
     *                that is under reference.
     * @throws RevEngineException Thrown in case we get any error
     *                            while operating the current instruction on the current
     *                            JVM stack.
     */
    void opr101to127(final JInstruction thisIns,
                     JOperandStack myStack)
            throws RevEngineException {
        switch (thisIns.opcode) {
            case 101: { //lsub
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_SUB) + " - "
                        + op2.getValueEx(L_SUB + 1), LONG, L_SUB);
                break;
            }
            case 102: { //fsub
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_SUB) + " - "
                        + op2.getValueEx(L_SUB + 1), FLOAT, L_SUB);
                break;
            }
            case 103: { //dsub
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_SUB) + " - "
                        + op2.getValueEx(L_SUB + 1), DOUBLE, L_SUB);
                break;
            }
            case 104: { //imul
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_MUL) + " * "
                        + op2.getValueEx(L_MUL), INT, L_MUL);
                break;
            }
            case 105: { //lmul
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_MUL) + " * "
                        + op2.getValueEx(L_MUL), LONG, L_MUL);
                break;
            }
            case 106: { //fmul
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_MUL) + " * "
                        + op2.getValueEx(L_MUL), FLOAT, L_MUL);
                break;
            }
            case 107: { //dmul
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_MUL) + " * "
                        + op2.getValueEx(L_MUL), DOUBLE, L_MUL);
                break;
            }
            case 108: { //idiv
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_DIV) + " / "
                        + op2.getValueEx(L_DIV), INT, L_DIV);
                break;
            }
            case 109: { //ldiv
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_DIV) + " / "
                        + op2.getValueEx(L_DIV), LONG, L_DIV);
                break;
            }
            case 110: { //fdiv
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_DIV) + " / "
                        + op2.getValueEx(L_DIV), FLOAT, L_DIV);
                break;
            }
            case 111: { //ddiv
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_DIV) + " / "
                        + op2.getValueEx(L_DIV), DOUBLE, L_DIV);
                break;
            }
            case 112: { //irem
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_MOD) + " % "
                        + op2.getValueEx(L_MOD), INT, L_MOD);
                break;
            }
            case 113: { //lrem
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_MOD) + " % "
                        + op2.getValueEx(L_MOD), LONG, L_MOD);
                break;
            }
            case 114: { //frem
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_MOD) + " % "
                        + op2.getValueEx(L_MOD), FLOAT, L_MOD);
                break;
            }
            case 115: { //drem
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_MOD) + " % "
                        + op2.getValueEx(L_MOD), DOUBLE, L_MOD);
                break;
            }
            case 116: { //ineg
                Operand op1 = (Operand) myStack.pop();
                myStack.push("-" + op1.getValueEx(L_UNARY), INT, L_UNARY);
                break;
            }
            case 117: { //lneg
                Operand op1 = (Operand) myStack.pop();
                myStack.push("-" + op1.getValueEx(L_UNARY), LONG, L_UNARY);
                break;
            }
            case 118: { //fneg
                Operand op1 = (Operand) myStack.pop();
                myStack.push("-" + op1.getValueEx(L_UNARY), FLOAT, L_UNARY);
                break;
            }
            case 119: { //dneg
                Operand op1 = (Operand) myStack.pop();
                myStack.push("-" + op1.getValueEx(L_UNARY), DOUBLE, L_UNARY);
                break;
            }
            case 120: { // ishl
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_SHIFT) + "<<"
                        + op2.getValueEx(L_SHIFT), INT, L_SHIFT);
                break;
            }
            case 121: { // lshl
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_SHIFT) + "<<"
                        + op2.getValueEx(L_SHIFT), LONG, L_SHIFT);
                break;
            }
            case 122:  // ishr
            case 124:  // iushr
            {
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_SHIFT) + ">>"
                        + op2.getValueEx(L_SHIFT), INT, L_SHIFT);
                break;
            }
            case 123: // lshr
            case 125: // lushr
            {
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                myStack.push(op1.getValueEx(L_SHIFT) + ">>"
                        + op2.getValueEx(L_SHIFT), LONG, L_SHIFT);
                break;
            }
            case 126: { //iand
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValueEx(L_BITAND) + " & "
                        + op2.getValueEx(L_BITAND), INT, L_BITAND);
                break;
            }
            case 127: { //land
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValueEx(L_BITAND) + " & "
                        + op2.getValueEx(L_BITAND), LONG, L_BITAND);
                break;
            }
        }
    }

    /**
     * Executes the JVM Opcodes from 128 - 147
     *
     * @param thisIns Current Instruction that is to be
     *                operated on the JVM stack.
     * @param myStack The JVM OperandStack of the current method
     *                that is under reference.
     * @throws RevEngineException Thrown in case we get any error
     *                            while operating the current instruction on the current
     *                            JVM stack.
     */
    void opr128to147(final JInstruction thisIns,
                     JOperandStack myStack)
            throws RevEngineException {
        switch (thisIns.opcode) {
            case 128: { //ior
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValueEx(L_BITOR) + " | "
                        + op2.getValueEx(L_BITOR), INT, L_BITOR);
                break;
            }
            case 129: { //lor
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValueEx(L_BITOR) + " | "
                        + op2.getValueEx(L_BITOR), LONG, L_BITOR);
                break;
            }
            case 130: { //ixor
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValueEx(L_BITXOR) + " ^ "
                        + op2.getValueEx(L_BITXOR), INT, L_BITXOR);
                break;
            }
            case 131: { //lxor
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValueEx(L_BITXOR) + " ^ "
                        + op2.getValueEx(L_BITXOR), LONG, L_BITXOR);
                break;
            }

            case 132: //iinc
                String localVar = symLocal.getName(
                        thisIns.getArgUnsignedWide(),
                        thisIns.index);
                int constant = thisIns.getArgWide(1);

                if (constant == -1) {
                    Operand op1 = (myStack.size() == 0)
                            ? null : (Operand) myStack.peek();
                    if (op1 != null) {
                        if (op1.getValue().equals(localVar)) {
                            op1 = (Operand) myStack.pop();
                            myStack.push(op1.getValueEx(L_UNARY) + "--",
                                    INT, L_UNARY);
                        } else {
                            myStack.push("--", INT, L_UNARY);
                        }
                        statement = "";
                        precedence = VALUE;
                    } else {
                        statement = localVar + "--";
                        precedence = L_UNARY;
                    }
                } else if (constant == 1) {
                    Operand op1 = (myStack.size() == 0)
                            ? null : (Operand) myStack.peek();
                    if (op1 != null) {
                        if (op1.getValue().equals(localVar)) {
                            op1 = (Operand) myStack.pop();
                            myStack.push(op1.getValueEx(L_UNARY) + "++",
                                    INT, L_UNARY);
                        } else {
                            myStack.push("++", INT, L_UNARY);
                        }
                        statement = "";
                        precedence = VALUE;
                    } else {
                        statement = localVar + "++";
                        precedence = L_UNARY;
                    }
                } else if (constant < 0) {
                    statement = localVar + " -= " + (-constant);
                    precedence = L_EVAL;
                } else {
                    statement = localVar + " += " + String.valueOf(constant);
                    precedence = L_EVAL;
                }
                break;
            case 133:  // i2l
            case 140:  // f2l
            case 143:  // d2l
            {
                Operand op1 = (Operand) myStack.pop();
                myStack.push("(long)" + op1.getValueEx(L_CAST),
                        LONG, L_CAST);
                break;
            }
            case 134:  // i2f
            case 137:  // l2f
            case 144:  // d2f
            {
                Operand op1 = (Operand) myStack.pop();
                myStack.push("(float)" + op1.getValueEx(L_CAST),
                        FLOAT, L_CAST);
                break;
            }
            case 135:  // i2d
            case 138:  // l2d
            case 141:  // f2d
            {
                Operand op1 = (Operand) myStack.pop();
                myStack.push("(double)" + op1.getValueEx(L_CAST),
                        DOUBLE, L_CAST);
                break;
            }
            case 136:  // l2i
            case 139:  // f2i
            case 142:  // d2i
            {
                Operand op1 = (Operand) myStack.pop();
                myStack.push("(int)" + op1.getValueEx(L_CAST),
                        INT, L_CAST);
                break;
            }
            case 145: // i2b
            {
                Operand op1 = (Operand) myStack.pop();
                myStack.push("(byte)" + op1.getValueEx(L_CAST),
                        BYTE, L_CAST);
                break;
            }
            case 146: // i2c
            {
                Operand op1 = (Operand) myStack.pop();
                myStack.push("(char)" + op1.getValueEx(L_CAST),
                        CHAR, L_CAST);
                break;
            }
            case 147: { // i2s
                Operand op1 = (Operand) myStack.pop();
                myStack.push("(short)" + op1.getValueEx(L_CAST),
                        SHORT, L_CAST);
                break;
            }
        }
    }

    /**
     * Executes the JVM Opcodes from 148 - 171
     *
     * @param thisIns Current Instruction that is to be
     *                operated on the JVM stack.
     * @param myStack The JVM OperandStack of the current method
     *                that is under reference.
     * @throws RevEngineException Thrown in case we get any error
     *                            while operating the current instruction on the current
     *                            JVM stack.
     */
    void opr148to171(final JInstruction thisIns,
                     JOperandStack myStack)
            throws RevEngineException {
        switch (thisIns.opcode) {
            case 148: // lcmp
            case 149: // fcmpl
            case 150: // fcmpg
            case 151: // dcmpl
            case 152: // dcmpg
            {
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();

                strOp1 = op1.getValue();
                strOp2 = op2.getValue();
                myStack.push("1", INT, VALUE);
                // Value being pushed here is irrelevant
                // But the data Type is.
                break;
            }
            case 153: { // if_eq
                Operand op1 = (Operand) myStack.pop();
                statement = OPR_EQ;
                precedence = L_LOGEQ;
                if (prevCode < 148 || prevCode > 152) {
                    // To be compared with 0
                    strOp1 = op1.getValue();
                    strOp2 = Helper.getValue("0", op1.getDatatype());
                }
                break;
            }
            case 154: { // if_ne
                Operand op1 = (Operand) myStack.pop();
                statement = OPR_NE;
                precedence = L_LOGNEQ;
                if (prevCode < 148 || prevCode > 152) {
                    // To be compared with 0
                    strOp1 = op1.getValue();
                    strOp2 = Helper.getValue("0", op1.getDatatype());
                }
                break;
            }
            case 155: { // if_lt
                Operand op1 = (Operand) myStack.pop();
                statement = OPR_LT;
                precedence = L_LOGREL;
                if (prevCode < 148 || prevCode > 152) {
                    // To be compared with 0
                    strOp1 = op1.getValue();
                    strOp2 = "0";
                }
                break;
            }
            case 156: { // if_ge
                Operand op1 = (Operand) myStack.pop();
                statement = OPR_GE;
                precedence = L_LOGREL;
                if (prevCode < 148 || prevCode > 152) {
                    // To be compared with 0
                    strOp1 = op1.getValue();
                    strOp2 = "0";
                }
                break;
            }
            case 157: { // if_gt
                Operand op1 = (Operand) myStack.pop();
                statement = OPR_GT;
                precedence = L_LOGREL;
                if (prevCode < 148 || prevCode > 152) {
                    // To be compared with 0
                    strOp1 = op1.getValue();
                    strOp2 = "0";
                }
                break;
            }
            case 158: { // if_le
                Operand op1 = (Operand) myStack.pop();
                statement = OPR_LE;
                precedence = L_LOGREL;
                if (prevCode < 148 || prevCode > 152) {
                    // To be compared with 0
                    strOp1 = op1.getValue();
                    strOp2 = "0";
                }
                break;
            }
            case 159: // if_icmpeq
            {
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                strOp1 = op1.getValue();
                strOp2 = Helper.getValue(op2.getValue(), op1.getDatatype());
                statement = OPR_EQ;
                precedence = L_LOGEQ;
                break;
            }
            case 165: // if_acmpeq
            {
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                strOp2 = op2.getValue();
                strOp1 = op1.getValue();
                statement = OPR_EQ;
                precedence = L_LOGEQ;
                break;
            }
            case 160: // if_icmpne
            {
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                strOp1 = op1.getValue();
                strOp2 = Helper.getValue(op2.getValue(), op1.getDatatype());
                statement = OPR_NE;
                precedence = L_LOGNEQ;
                break;
            }
            case 166: // if_acmpne
            {
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                strOp2 = op2.getValue();
                strOp1 = op1.getValue();
                statement = OPR_NE;
                precedence = L_LOGNEQ;
                break;
            }
            case 161: { // if_icmplt
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                strOp2 = op2.getValue();
                strOp1 = op1.getValue();
                statement = OPR_LT;
                precedence = L_LOGREL;
                break;
            }
            case 162: { // if_icmpge
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                strOp2 = op2.getValue();
                strOp1 = op1.getValue();
                statement = OPR_GE;
                precedence = L_LOGREL;
                break;
            }
            case 163: { // if_icmpgt
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                strOp2 = op2.getValue();
                strOp1 = op1.getValue();
                statement = OPR_GT;
                precedence = L_LOGREL;
                break;
            }
            case 164: { // if_icmple
                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                strOp2 = op2.getValue();
                strOp1 = op1.getValue();
                statement = OPR_LE;
                precedence = L_LOGREL;
                break;
            }
            case 167: // goto
                //No Change to stack
                break;
            case 168: { // jsr
                // Address of the immediately following instruction
/**
 myStack.push(String.valueOf(thisIns.index +3) ,
 RET_ADDR);
 **/
                // Represents finally
                break;
            }
            case 169: { // ret
                // No change
                break;
            }
            case 170: // tableswitch
            case 171: // lookupswitch
            {
                Operand op1 = (Operand) myStack.pop();
                strOp1 = op1.getValue();
                break;
            }
        }
    }

    /**
     * Executes the JVM Opcodes from 172- 186
     *
     * @param thisIns Current Instruction that is to be
     *                operated on the JVM stack.
     * @param myStack The JVM OperandStack of the current method
     *                that is under reference.
     * @throws RevEngineException Thrown in case we get any error
     *                            while operating the current instruction on the current
     *                            JVM stack.
     */
    void opr172to186(final JInstruction thisIns,
                     JOperandStack myStack)
            throws RevEngineException {
        switch (thisIns.opcode) {
            case 172: //ireturn
            {
                Operand op1 = (Operand) myStack.pop();
                statement = RETURN + " ("
                        + Helper.getValue(op1.getValue(), returnType) + ")";
                precedence = VALUE;
                break;
            }
            case 173: //lreturn
            case 174: //freturn
            case 175: //dreturn
            case 176: //areturn
            {
                Operand op1 = (Operand) myStack.pop();
                statement = RETURN + " (" + op1.getValue() + ")";
                precedence = VALUE;
                break;
            }
            case 177: { //return
                // Make the stack empty here
                statement = RETURN;
                precedence = VALUE;
                break;
            }
            case 178: { //getstatic
                int offset = thisIns.getArgUnsignedShort();

                int classPtr = cpInfo.getPtr1(offset);
                int fieldPtr = cpInfo.getPtr2(offset);

                String classType = Helper.getJavaDataType(
                        cpInfo.getClassName(classPtr), false);

                classType = importInfo.getClassName(classType);

                String fieldName = cpInfo.getFieldName(fieldPtr);
                String fieldType = cpInfo.getFieldType(fieldPtr);

//              FieldType = Helper.formatDataType(FieldType);
                myStack.push(classType + "." + fieldName, fieldType, VALUE);
                break;
            }
            case 179: { // putstatic
                int offset = thisIns.getArgUnsignedShort();

                int classPtr = cpInfo.getPtr1(offset);
                int fieldPtr = cpInfo.getPtr2(offset);

                String classType = Helper.getJavaDataType(
                        cpInfo.getClassName(classPtr), false);

                classType = importInfo.getClassName(classType);

                String fieldName = cpInfo.getFieldName(fieldPtr);

                Operand op1 = (Operand) myStack.pop();
                statement = classType + "." + fieldName + " = "
                        + op1.getValueEx(L_EVAL);

                precedence = L_EVAL;
                break;
            }
            case 180: {  // getfield
                Operand op1 = (Operand) myStack.pop();
                String objName = op1.getValue();

                int offset = thisIns.getArgUnsignedShort();

                int fieldPtr = cpInfo.getPtr2(offset);

                String fieldName = cpInfo.getFieldName(fieldPtr);
                String fieldType = cpInfo.getFieldType(fieldPtr);

                //Field Name and Type respectively
                if (objName.equals(THIS)) {
                    myStack.push(fieldName, fieldType, VALUE);
                } else {
                    myStack.push(objName + "." + fieldName, fieldType, VALUE);
                }
                break;
            }
            case 181: { // putfield
                // End of an instruction

                Operand op2 = (Operand) myStack.pop();
                Operand op1 = (Operand) myStack.pop();
                String fieldValue = op2.getValue();
                String objname = op1.getValue();

                int offset = thisIns.getArgUnsignedShort();

                int fieldPtr = cpInfo.getPtr2(offset);
                String fieldName = cpInfo.getFieldName(fieldPtr);
                String fieldType = cpInfo.getFieldType(fieldPtr);

                if (objname.equals(THIS)) {
                    statement = fieldName + " = "
                            + Helper.getValue(fieldValue, fieldType);
                } else {
                    statement = objname + "." + fieldName + " = "
                            + Helper.getValue(fieldValue, fieldType);
                }
                precedence = L_EVAL;
                break;
            }
            case 182: // invokevirtual
            case 185: // invokeinterface
            {
                processInvokeInstruction(thisIns, myStack, false);
                break;
            }
            case 183: {   // invokespecial
                processInvokeInstruction(thisIns, myStack, true);
                break;
            }
            case 184: { //invokestatic
                int offset = thisIns.getArgUnsignedShort();
                int classIndex = cpInfo.getPtr1(offset);
                String classType = importInfo.getClassName(
                        cpInfo.getClassName(classIndex));

                // GetMethodName
                int nameIndex = cpInfo.getPtr2(offset);
                String methodName = cpInfo.getFirstDirectName(nameIndex);

                // Get No: of arguments
                int argsIndex = cpInfo.getPtr2(nameIndex);
                String argsList = cpInfo.getCpValue(argsIndex);
                List args = Helper.getArguments(argsList);
                int popMax = args.size();

                String methodType = Helper.getReturnType(argsList);
                // Get Return type

                List argValues = new ArrayList(popMax);
                for (int i = popMax - 1; i >= 0; i--) {
                    argValues.add(0, ((Operand) myStack.pop()).getValue());
                }

                statement = classType + "." + methodName
                        + getArgValues(args, argValues);
                precedence = L_REF;

                if (!methodType.equals(JVM_VOID)) {
                    myStack.push(statement, methodType, VALUE);
                }
                break;
            }
            case 186: {
                // UnUsed
                throw new RevEngineException("Opcode 186 unused");
            }
        }
    }

    /**
     * Executes the JVM Opcodes from 187 - 201
     *
     * @param thisIns Current Instruction that is to be
     *                operated on the JVM stack.
     * @param myStack The JVM OperandStack of the current method
     *                that is under reference.
     * @throws RevEngineException Thrown in case we get any error
     *                            while operating the current instruction on the current
     *                            JVM stack.
     */
    void opr187to201(final JInstruction thisIns,
                     JOperandStack myStack)
            throws RevEngineException {

        switch (thisIns.opcode) {
            case 187: { //new
                int offset = thisIns.getArgUnsignedShort();

                String classType = cpInfo.getClassName(offset);
                String className = importInfo.getClassName(
                        Helper.getJavaDataType(classType, false));

                myStack.push("new " + className, classType, L_REF);
                break;
            }
            case 188: { // newarray
                int atype = thisIns.getArgByte();
                Operand op1 = (Operand) myStack.pop();
                String count = op1.getValue();

                switch (atype) {
                    case 4: {
                        myStack.push(
                                "new boolean[" + count + "]", "[Z", L_REF);
                        break;
                    }
                    case 5: {
                        myStack.push(
                                "new char[" + count + "]", "[C", L_REF);
                        break;
                    }
                    case 6: {
                        myStack.push(
                                "new float[" + count + "]", "[F", L_REF);
                        break;
                    }
                    case 7: {
                        myStack.push(
                                "new double[" + count + "]", "[D", L_REF);
                        break;
                    }
                    case 8: {
                        myStack.push(
                                "new byte[" + count + "]", "[B", L_REF);
                        break;
                    }
                    case 9: {
                        myStack.push(
                                "new short[" + count + "]", "[S", L_REF);
                        break;
                    }
                    case 10: {
                        myStack.push(
                                "new int[" + count + "]", "[I", L_REF);
                        break;
                    }
                    case 11: {
                        myStack.push(
                                "new long[" + count + "]", "[J", L_REF);
                        break;
                    }
                }
                break;
            }
            case 189: { // anewarray

                int offset = thisIns.getArgUnsignedShort();

                String classType = cpInfo.getClassName(offset);
                String className =
                        importInfo.getClassName(
                                Helper.getJavaDataType(classType, true));

                //Get Class Name
                Operand op1 = (Operand) myStack.pop();
                String count = op1.getValue();

                myStack.push(
                        "new " + className + "[" + count + "]",
                        classType, L_REF);
                break;
            }
            case 190: { //arraylength
                Operand op1 = (Operand) myStack.pop();
                myStack.push(op1.getValueEx(L_REF) + "." + LENGTH, INT, VALUE);
                break;
            }
            case 191: { //athrow
                Operand op1 = (Operand) myStack.pop();
                statement = THROW + " " + op1.getValueEx(L_REF);
                precedence = L_REF;
                // No Change
                break;
            }
            case 192: { //checkcast
                int offset = thisIns.getArgUnsignedShort();

                //Get Class Name
                Operand op1 = (Operand) myStack.pop();

                String castType = cpInfo.getClassName(offset);

                String javaCastType =
                        importInfo.getClassName(
                                Helper.getJavaDataType(
                                        castType, false));

                String value = new String("(" + javaCastType + ")");
                value = value + op1.getValueEx(L_CAST);
                myStack.push(value, castType, L_CAST);
                //No Change to JVM Stack
                break;
            }
            case 193: { // instanceof
                Operand op1 = (Operand) myStack.pop();

                int offset = thisIns.getArgUnsignedShort();

                // Class Type found here
                String classType = Helper.getJavaDataType(
                        cpInfo.getClassName(offset), false);

                classType = importInfo.getClassName(classType);

                myStack.push(op1.getValue() + SPACE + INSTANCEOF
                        + SPACE + classType, BOOLEAN, L_LOGIOF);
                break;
            }
            case 194: // monitorenter
            case 195: // monitorexit
            {
                Operand op1 = (Operand) myStack.pop();
                statement = op1.getValue();
                precedence = VALUE;
                break;
            }
            case 196: { //wide  -doesn't affect stack
                // wide ignore it
                break;
            }
            case 197: { //multianewarray
                int offset = thisIns.getArgUnsignedShort();

                // Dimensions. Max 255.
                int dimensions = thisIns.getArgUnsignedByte(2);
                String strIndex[] = new String[dimensions];

                // ClassType
                String classType = Helper.getJavaDataType(
                        cpInfo.getClassName(offset), false);

                classType = importInfo.getClassName(classType);


                // For Output
                for (int i = dimensions - 1; i >= 0; i--) {
                    strIndex[i] = ((Operand) myStack.pop()).getValue();
                }

                StringBuffer arrayIndex = new StringBuffer();
                arrayIndex.append(NEW + SPACE + classType);
                StringBuffer arrayType = new StringBuffer();
                for (int i = 0; i < dimensions; i++) {
                    arrayIndex.append("[ " + strIndex[i] + " ]");
                    arrayType.append("[");
                }

                statement = arrayIndex.toString();
                precedence = L_REF;
                myStack.push(arrayIndex.toString(),
                        arrayType.append("L" + classType).toString(),
                        L_REF);
                break;
            }
            case 198: {  //ifnull
                Operand op1 = (Operand) myStack.pop();

                strOp1 = op1.getValue();
                strOp2 = NULL;
                statement = OPR_EQ;
                precedence = L_LOGEQ;
                break;
            }
            case 199: { //ifnonnull
                Operand op1 = (Operand) myStack.pop();

                strOp1 = op1.getValue();
                strOp2 = NULL;
                statement = OPR_NE;
                precedence = L_LOGNEQ;
                break;
            }
            case 200: { //goto_w
                //No Change to stack
                break;
            }
            case 201: { //jsr_w
                // Represents finally
                break;
            }
        }// End of switch
    }

    /**
     * Executes the JVM Opcodes from 200- 255.
     * Currently not supported by JVM spec since this contains
     * quick variables and hence not to be supported.
     *
     * @param thisIns Current Instruction that is to be
     *                operated on the JVM stack.
     * @param myStack The JVM OperandStack of the current method
     *                that is under reference.
     * @throws RevEngineException Thrown in case we get any error
     *                            while operating the current instruction on the current
     *                            JVM stack.
     */
    void opr202to255(final JInstruction thisIns, JOperandStack myStack)
            throws RevEngineException {

        throw new RevEngineException(
                "OpCode not allowed in a valid class file");
    }

    /**
     * @param popValue Value popped from stack
     * @param intIndex Index of the integer variable onto the
     *                 symbol table.
     * @param insIndex Instruction Index
     * @throws RevEngineException Thrown in case of any error.
     */
    private void dealIntegerStore(String popValue, int intIndex,
                                  int insIndex)
            throws RevEngineException {

        String localVar = symLocal.getName(intIndex, insIndex);
        String localType = symLocal.getDataType(intIndex, insIndex);
        statement = localVar + " = " + Helper.getValue(popValue, localType);
        precedence = L_EVAL;
    }

    /**
     * Processes an invoke instruction -
     * invokespecial, invokestatic, invokeinterface, invokevirtual.
     *
     * @param aCurIns            Current Instruction that is to be
     *                           operated on the JVM stack.
     * @param aJos               The JVM OperandStack of the current method
     *                           that is under reference.
     * @param aInvokeSpecialFlag if this instruction is invokespecial.
     */
    private void processInvokeInstruction(JInstruction aCurIns,
                                          JOperandStack aJos,
                                          boolean aInvokeSpecialFlag) {
        int offset = aCurIns.getArgUnsignedShort();
        int classIndex = cpInfo.getPtr1(offset);

        int nameIndex = cpInfo.getPtr2(offset);
        String methodName = cpInfo.getFirstDirectName(nameIndex);
        String className = cpInfo.getClassName(classIndex);
        String argsList = cpInfo.getCpValue(cpInfo.getPtr2(nameIndex));

        List args = Helper.getArguments(argsList);
        int popMax = args.size();
        //Equals Number of Arguments

        String methodType = Helper.getReturnType(argsList);

        //System.out.println(className);
        List argValues = new ArrayList(popMax);
        for (int i = popMax - 1; i >= 0; i--) {
            // add arguments in reverse order
            argValues.add(0, ((Operand) aJos.pop()).getValue());
        }

        String objRef = ((Operand) aJos.pop()).getValue();


        /* Takes care of modifying the input */
        if (objRef.compareTo(THIS) != 0) {
            if (aInvokeSpecialFlag && methodName.equals(INIT)) {
                statement = objRef; //Constructor
            } else {
                statement = objRef + "." + methodName;
            }
        } else {
            if (aInvokeSpecialFlag && methodName.equals(INIT)) {
                if ((className.equals(LANG_OBJECT))) {
                    statement = ""; // filter out the default Object() constructor.
                    return;
                } else {
                    statement = SUPER;
                    //Code for super constructor here.
                }
            } else {
                statement = methodName;
            }
        }
        precedence = L_REF;
        statement += getArgValues(args, argValues);

        if (methodType.compareTo(JVM_VOID) == 0) {
            if (aInvokeSpecialFlag && !aJos.empty()) {
                Operand op1 = (Operand) aJos.pop();
                aJos.push(new Operand(statement,
                        op1.getDatatype(), precedence));
            }
        } else {
            aJos.push(statement, methodType, precedence);
        }
        invokedObject = new Operand(objRef, className, L_REF);
    }

    /**
     * @param args      Argument - members are String.
     * @param argValues Argument - members are String.
     * @return Returns a String.
     */
    private String getArgValues(List args, List argValues) {
        StringBuffer result = new StringBuffer("(");
        for (int i = 0; i < args.size(); i++) {
            String value =
                    Helper.getValue((String) argValues.get(i),
                            (String) args.get(i));
            if (i != 0) {
                result.append(" , ");
            }
            result.append(value);
        }
        result.append(")");
        return result.toString();
    }
}/*  End of class */
