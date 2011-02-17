/**
 *  @(#)JMethod.java
 *
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
package jreversepro.reflect;

import jreversepro.common.Helper;
import jreversepro.common.JJvmOpcodes;
import jreversepro.common.JJvmSet;
import jreversepro.common.KeyWords;
import jreversepro.reflect.method.JBlockObject;
import jreversepro.reflect.method.JMethodBlock;
import jreversepro.revengine.RevEngineException;
import jreversepro.runtime.JSymbolTable;

import java.util.*;

/**
 * <b>JMethod</b> is the abstract representation of a method in
 * the class method..
 *
 * @author Karthik Kumar
 */
public class JMethod extends JMember
        implements KeyWords {

    /**
     * Signature of a method.
     * For example for a method as follows.
     * "public void doSomething( int ,int ) {"
     * the signature will be (II)V
     */
    private String signature;

    /**
     * Throws classes is a List containing String of
     * the java data types that are thrown by this
     * method.
     */
    private List throwsClasses;

    /**
     * Maximum size that the JVM stack will
     * occupy on execution of all instructions
     * present in this method.
     */
    private int maxStack;

    /**
     * Maximum number of local variables present in
     * the local variable table at any time.
     */
    private int maxLocals;

    /**
     * This contains the bytecodes that are present for
     * the method.
     */
    private byte[] bytecodes;

    /**
     * This contains the stringifed (disassembled) bytecodes for
     * the method.
     */
    private String bytecodeAsString;


    /**
     * This list contains the exception tables that are
     * defined for this method.
     * The members of this list are -
     * JException
     */
    private List exceptionBlocks; // exception table

    /**
     * This list contains the exception tables that are
     * defined for this method.
     * The members of this list are -
     * JException
     */
    private List instructions; //  Instructions here.

    /**
     * This contains the LineNumberTable that may be
     * compiled for this method. Nevertheless for decompiling
     * purposes this cant be used since this is optional information
     * in a class file. If debugging is turned off then the
     * compiler wont bother to generate this LineNumberTable.
     */
    private JLineNumberTable lineTable;

    /**
     * Structure containing code
     */
    private JMethodBlock methodBlock;

    /**
     * Symbol table
     */
    private JSymbolTable symbolTable;

    /**
     * Structure used during method building containing stack of
     * code blocks currently open
     */
    private Stack blockStack;


    /**
     * Information about fields, methods of the class being reverse engineered.
     */
    private JClassInfo mInfoClass;

    /**
     * First instruction in the method pool *
     */
    JInstruction firstIns;

    /**
     * @param info The JSymbolTable associated with this class
     *             Creates a new JMethod
     */
    public JMethod(JClassInfo info) {

        mInfoClass = info;

        blockStack = new Stack();
        methodBlock = new JMethodBlock();
        blockStack.push(methodBlock);

        exceptionBlocks = new ArrayList();

        throwsClasses = new ArrayList(2);
        firstIns = null;
    }


    /**
     * @return The method block for this method
     */
    public JMethodBlock getBlock() {
        return methodBlock;
    }


    /**
     * Add a new JBlockObject to the stack - indicates that a block
     * is opening
     *
     * @param jbo JBlockObject to be added.
     */
    public void addBlock(JBlockObject jbo) {
        JBlockObject peekjbo = (JBlockObject) blockStack.peek();
        peekjbo.addBlock(jbo);
        blockStack.push(jbo); //push new block as the current open block
    }

    /**
     * Close the current JBlockObject -- pop it from the stack
     * Called when an end of block is reached.
     */
    public void closeBlock() {
        blockStack.pop();
    }


    /**
     * Adds a line of code to the current block
     *
     * @param loc Line Of Code to be added.
     */
    public void addLineOfCode(JLineOfCode loc) {
        JBlockObject jbo = (JBlockObject) blockStack.peek();
        if (jbo == null) {
            //yikes! Should NEVER happen, here for debugging
            System.out.println("*** No blocks on the stack -- "
                    + " cannot add code!!! ***");
            return;
        }
        jbo.addStatement(loc);   //add the LineOfCode to the current block
        loc.setBlock(jbo);       //set the LineOfCode's containing block
    }

    /**
     * Removes and returns the last line of code added from the current block
     *
     * @return Returns the last line of code added to the current block.
     */
    public JLineOfCode removeLastLineOfCode() {
        JBlockObject jbo = (JBlockObject) blockStack.peek();
        if (jbo == null) {
            //yikes! Should NEVER happen, here for debugging
            System.out.println("*** No blocks on the stack -- "
                    + " cannot add code!!! ***");
            return null;
        }
        return jbo.removeLastStatement();
        //add the LineOfCode to the current block
    }

    /**
     * Removes and returns the last block added
     *
     * @return Returns the last block added.
     */
    public JBlockObject removeCurrentBlock() {

        JBlockObject remove = (JBlockObject) blockStack.pop();
        //POP block off stack

        JBlockObject jbo = (JBlockObject) blockStack.peek();
        //Get new current block

        jbo.removeLastBlock();//remove old (sub) block from current block
        return remove;
    }


    /**
     * @param st The JSymbolTable associated with this class
     *           Set the Symbol table
     */
    public void setSymbolTable(JSymbolTable st) {
        symbolTable = st;
    }

    /**
     * Setter method for Signature
     *
     * @param rhsSign Signature field value.
     */
    public void setSignature(String rhsSign) {
        signature = rhsSign;
    }

    /**
     * Add an exception block.
     *
     * @param startPc   Start of the try block
     * @param endPc     End of try block
     * @param handlerPc Beginning of handler block
     * @param datatype  Type of the class that the
     *                  handler is going to  handle.
     */
    public void addExceptionBlock(
            int startPc, int endPc,
            int handlerPc, String datatype) {

        JException exc = new JException(startPc, endPc,
                handlerPc, datatype);

        //Probably some changes to the keys put in the list.
        int tryIndex = exceptionBlocks.indexOf(exc);
        if (tryIndex == -1) {
            exceptionBlocks.add(exc);
        } else {
            JException oldTry = (JException) exceptionBlocks.get(tryIndex);
            oldTry.addCatchBlock(handlerPc, datatype);
        }
    }

    /**
     * Sets the line number table.
     *
     * @param rhsLineTable Line number Table that is created
     *                     by the compiler if the debugging option is on.
     */
    public void setLineTable(JLineNumberTable rhsLineTable) {
        lineTable = rhsLineTable;
    }

    /**
     * Returns the return type of the method.
     *
     * @return Returns the return type of the method.
     */
    public String getReturnType() {
        return Helper.getReturnType(signature);
    }

    /**
     * Returns the signature of the method.
     *
     * @return Returns the signature of the method.
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Returns the argument list. The members of the list
     * are String.
     *
     * @return Returns the argument list.
     */
    public List getArgList() {
        return Helper.getArguments(signature);
    }

    /**
     * Returns the .list of classes thrown by this
     * method. The individual members are string.
     *
     * @return Returns the list of thrown classes
     */
    public List getThrowsClasses() {
        return throwsClasses;
    }

    /**
     * Returns the maximum local members of this method.
     *
     * @return Returns the Max Locals
     */
    public int getMaxLocals() {
        return maxLocals;
    }

    /**
     * Returns the max. stack
     *
     * @return Returns max stack.
     */
    public int getMaxStack() {
        return maxStack;
    }

    /**
     * Sets the .list of classes thrown by this
     * method. The individual members are string.
     *
     * @param throwsClasses Classes Thrown
     */
    public void setThrowsClasses(List throwsClasses) {
        this.throwsClasses = throwsClasses;
    }

    /**
     * Sets the max. local variable field
     *
     * @param maxLocals Max Locals
     */
    public void setMaxLocals(int maxLocals) {
        this.maxLocals = maxLocals;
    }

    /**
     * Sets the max. stack variable field.
     *
     * @param maxStack Max. Stack length
     */
    public void setMaxStack(int maxStack) {
        this.maxStack = maxStack;
    }

    /**
     * Returns the bytecode array of the method
     *
     * @return bytecode array.
     */
    protected byte[] getBytes() {
        return bytecodes;
    }

    /**
     * Sets the bytecode array.
     *
     * @param bytecodes the bytecode array input.
     */
    public void setBytes(byte[] bytecodes) {
        this.bytecodes = bytecodes;
    }

    /**
     * Returns the exception table. The individual members
     * contain JException.
     *
     * @return Returns the exception table.
     */
    public List getexceptionBlocks() {
        return exceptionBlocks;
    }

    /**
     * Returns a map
     *
     * @return Returns a map of exception tables.
     */
    public Map getAllCatchJExceptions() {
        Map newMap = new HashMap();
        for (int i = 0; i < exceptionBlocks.size(); i++) {
            newMap.putAll(
                    ((JException) exceptionBlocks.get(i)).excCatchTable);
        }
        return newMap;
    }

    /**
     * Returns the list of bytecodes in the method.
     * The individual members of the list are
     * JInstruction.
     *
     * @return Returns the bytecodes in the method.
     */
    public List getInstructions() {
        if (instructions == null) {
            normalize();
        }
        return instructions;
    }

    /**
     * Returns the JInstruction having the specified
     * byte offset
     *
     * @param ind Index of the instruction.
     * @return Returns the JInstruction in the method.
     */
    public JInstruction getInstruction(int ind) {
        if (instructions == null) {
            return null;
        }

        //now search for the ins
        JInstruction tIns = null;
        for (int i = 0; i < instructions.size(); i++) {
            JInstruction sIns = (JInstruction) instructions.get(i);
            if (sIns.index == ind) {
                tIns = sIns;
                break;
            }
        }
        return tIns;
    }

    /**
     * Returns the JInstruction following the instruction
     * having the specified byte offset
     *
     * @param ind Index of the instruction.
     * @return Returns the JInstruction in the method.
     */
    public JInstruction getNextInstruction(int ind) {
        if (instructions == null) {
            return null;
        }

        //now search for the ins
        JInstruction tIns = null;
        for (int i = 0; i < instructions.size(); i++) {
            JInstruction sIns = (JInstruction) instructions.get(i);
            if (sIns.index == ind) {
                i++;
                //now make sure it's not the last ins
                if (i < instructions.size()) {
                    tIns = (JInstruction) instructions.get(i);
                }
                break;
            }
        }
        return tIns;
    }


    /**
     * Returns the LineNumberTable of the method.
     *
     * @return Returns the LineNumberTable of the method.
     */
    public JLineNumberTable getLineTable() {
        return lineTable;
    }

    /**
     * Returns the initial code / information about the method.
     * It consists of maximum local information
     * maximum stack information
     * and the exception class handler information.
     *
     * @return Returns the code/information about the method.
     */
    public String getLocalStackInfo() {
        StringBuffer result = new StringBuffer("");
        result.append("\n\t  // Max Locals " + maxLocals);
        result.append("  , Max Stack " + maxStack);
        if (exceptionBlocks != null) {
            Enumeration
            enum=Collections.enumeration(exceptionBlocks);
            if (
            enum.hasMoreElements()){
                result.append("\n\n\t  /**");
                result.append("\n\t\tFrom  To  Handler\tClass\n");
                while (
                enum.hasMoreElements()){
                    JException exc = (JException)
                    enum.nextElement();
                    result.append(exc.toString());
                }
                result.append("\t  **/\n");
            }
        }
        //appending the Exception table info. to be done.
        return result.toString();
    }

    /**
     * Returns ifthis method is static.
     *
     * @return Returns true, if this is a static method.
     *         false, otherwise.
     */
    public boolean isStatic() {
        return (super.isStatic() || name.equals(CLINIT));
    }

    /**
     * Normalization of the bytecodes into JVM codes.
     * This method is responsible for converting the bytecode
     * stream of the method into JVM opcodes.
     * By default all JVM opcodes are of fixed length in all contexts,
     * except some.
     * tableswitch and lookupswitch are basically variable length
     * opcodes and their length primarily depend on the number
     * of switch legs in the source code since they are one
     * componsite statement that contains information about
     * all the possible targets.
     */
    public void normalize() {
        instructions = new ArrayList();
        if (bytecodes == null) {
            return;
        }
        int maxCode = bytecodes.length;
        int index = 0;
        int nextPc = -1;
        int startPc = -1;
        boolean bWide = false;
        JInstruction curIns = firstIns;

        while (index < maxCode) {
            int curByte = Helper.signedToUnsigned(bytecodes[index]);
            startPc = index + 1;
            if (curByte == JJvmOpcodes.OPCODE_TABLESWITCH) {
                startPc = index + 4 - (index % 4);
                nextPc = dealTableSwitch(startPc);
            } else if (curByte == JJvmOpcodes.OPCODE_LOOKUPSWITCH) {
                startPc = index + 4 - (index % 4);
                nextPc = dealLookupSwitch(startPc);
            } else {
                nextPc = index + JJvmSet.getInsLen(curByte, bWide);
            }
            JInstruction thisIns =
                    new JInstruction(index, curByte,
                            getArgArray(startPc, nextPc),
                            bWide);
            if (firstIns == null) {
                firstIns = thisIns;
            } else {
                curIns.append(thisIns);
            }
            curIns = thisIns;

            instructions.add(thisIns);
            bWide = (curByte == JJvmOpcodes.OPCODE_WIDE);
            index = nextPc;
        }
    }

    /**
     * Get First Instruction.
     *
     * @return Returns the first instruction.
     */
    public JInstruction getFirstIns() {
        return firstIns;
    }

    /**
     * This method returns the length of the variable
     * instruction tableswitch.
     *
     * @param index beginning index of the tableswitch statement
     *              into the bytecode stream of the method.
     *              The format is as follows.
     *              The first 4 bytes are for defaultbyte.
     *              The next 4 for the lowest value in the case leg values.
     *              The next 4 for the highest value in the case high values.
     *              Then we will have ( high - low ) * 4 bytes - all
     *              containing the target index to be junmped into
     *              relative to the index of the current switch instruction.
     * @return Returns the integer that is Index + length of
     *         the instruction so that the next JVM opcode can proceed from
     *         there.
     */
    protected int dealTableSwitch(int index) {
        //Read Default Byte
        index += 4;

        int lowByte = readTargetSwitch(index);
        index += 4;

        int highByte = readTargetSwitch(index);
        index += 4;

        index += (4 * (highByte - lowByte + 1));
        return index;
    }

    /**
     * This method returns the length of the variable
     * instruction lookupswitch.
     *
     * @param index beginning index of the lookupswitch statement
     *              into the bytecode stream of the method.
     *              The format is as follows.
     *              The first 4 bytes are for defaultbyte.
     *              The next 4 for the number of pairs of ( case leg value, target)
     *              that will appear as follows, say N.
     *              Then there will be (2 * N)  * 4 bytes , ( N pairs of integers ),
     *              with each pair containing the case leg value first and the
     *              second integer letting us know the target of the case leg
     *              relative to the index of the method.
     * @return Returns the integer that is Index + length of
     *         the instruction so that the next JVM opcode can proceed from
     *         there.
     */
    protected int dealLookupSwitch(int index) {
        //Read Default Byte
        index += 4;

        int nPairs = readTargetSwitch(index);
        index += 4;

        index += (8 * nPairs);

        return index;
    }

    /**
     * This method is used to return the signed integer
     * value of the next four bytes, if given an bytecode index,
     *
     * @param newIndex Index from which the integer array starts.
     *                 This is in big-endian order.
     * @return Returns the integer value.
     */
    private int readTargetSwitch(int newIndex) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int thisByte =
                    Helper.signedToUnsigned(bytecodes[newIndex + i]);
            result += (int) (Math.pow(256, 3 - i)) * thisByte;
        }
        if (result < 0) {
            return result + 256;
        } else {
            return result;
        }
    }

    /**
     * Given the start index and the end index this method
     * returns a byte array that contain the byte values
     * with the start array value included and end array
     * excluded.
     *
     * @param start start index into the byte array.
     * @param end   end index into the byte array.
     * @return Returns a byte array containing the bytes
     *         from start to end , end exclusive and start inclusive.
     */
    protected byte[] getArgArray(int start, int end) {
        if (start == end) {
            return null;
        } else {
            byte[] result = new byte[end - start];
            for (int i = start; i < end; i++) {
                result[i - start] = bytecodes[i];
            }
            return result;
        }
    }


    /**
     * returns a throws clause for the method
     *
     * @param importInfo containing the import information.
     * @return Returns a string that contains the code
     *         representation.
     */
    public String getThrowsClause(JImport importInfo) {
        StringBuffer sb = new StringBuffer();
        int size = throwsClasses.size();
        if (size != 0) {
            sb.append("\n\t\tthrows ");
            for (int i = 0; i < size; i++) {
                String thrownClass = (String) throwsClasses.get(i);
                if (i > 0) {
                    sb.append(" ,");
                }
                sb.append(importInfo.getClassName(thrownClass));
            }
        }
        return sb.toString();
    }

    /**
     * Returns the headers for the method.
     *
     * @param includeStackInfo Denotes if the stack information
     *                         like maximum local variables, stack information are supposed
     *                         to be included in the reverse engineered code.
     *                         Moved from JDecompiler  by pazandak@objs.com
     * @return Returns the method header information.
     */
    public String getMethodHeader(boolean includeStackInfo) {

        StringBuffer sb = new StringBuffer();
        String returnType =
                mInfoClass.getConstantPool().
                        getImportedClasses().
                        getClassName(
                                Helper.getJavaDataType(
                                        this.getReturnType(), false));

        String name = this.getName();
        sb.append("\n\n    ");

        if (name.compareTo(CLINIT) == 0) {
            sb.append("static");
        } else if (name.compareTo(INIT) == 0) {
            sb.append(this.getQualifierName());
            sb.append(mInfoClass.getThisClass(false));
        } else {
            sb.append(this.getQualifierName());
            sb.append(returnType);
            sb.append(" " + this.getName());
        }

        try {
            List args = this.getArgList();
            int symIndex = 1;
            if (this.isStatic()) {
                symIndex = 0;
            }

            if (this.getName().compareTo(CLINIT) != 0) {
                sb.append("(");
                for (int i = 0; i < args.size(); i++) {
                    if (i != 0) {
                        sb.append(" ,");
                    }
                    String jvmArgType = (String) args.get(i);
                    String argType =
                            mInfoClass.getConstantPool().
                                    getImportedClasses().
                                    getClassName(
                                            Helper.getJavaDataType(
                                                    jvmArgType, false));

                    sb.append(argType);
                    if (symbolTable != null) {
                        sb.append(" "
                                + symbolTable.getName(symIndex++,
                                JSymbolTable.ARG_INDEX));
                    }
                    if (jvmArgType.equals(LONG)
                            || jvmArgType.equals(DOUBLE)) {
                        symIndex++;
                    }
                }
                sb.append(")");
            }

            sb.append(this.getThrowsClause(
                    mInfoClass.getConstantPool().
                            getImportedClasses()));

        } catch (RevEngineException ree) {
            //Should not occur
        }
        return sb.toString() + "\n"
                + (includeStackInfo
                ? getLocalStackInfo() + "\n" : "");
    }

    /**
     * @param getBytecode      TRUE if bytecode is to be returned, FALSE if
     *                         decompiled code should be returned.
     * @param includeStackInfo TRUE if stack & exception info should be
     *                         output
     * @return Returns the stringified method
     */
    public String getMethodAsString(boolean getBytecode,
                                    boolean includeStackInfo) {

        StringBuffer sb = new StringBuffer(
                getMethodHeader(includeStackInfo));


        sb.append(
                getBytecode
                        ?
                        getStringifiedBytecode()
                        :
                        getBlock().toString());

        return sb.toString();
    }

    /**
     * @return Returns the bytecode for this method;
     */
    public String getStringifiedBytecode() {
        return bytecodeAsString;
    }

    /**
     * Sets the bytecode for this method;
     *
     * @param str Bytecode disassembled string representation.
     */
    public void setStringifiedBytecode(String str) {
        bytecodeAsString = str;
    }
}
