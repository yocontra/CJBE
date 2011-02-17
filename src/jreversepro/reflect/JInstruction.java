/**
 *  @(#)JInstruction.java
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
package jreversepro.reflect;

import jreversepro.common.Helper;
import jreversepro.common.JJvmOpcodes;
import jreversepro.common.JJvmSet;
import jreversepro.common.KeyWords;

/**
 * Abstraction of a JVM Opcode instruction.
 *
 * @author Karthik Kumar
 */
public class JInstruction
        implements JJvmOpcodes,
        KeyWords {

    /**
     * Invalid variable index.
     */
    public static final int INVALID_VAR_INDEX = -2;

    /**
     * Index of this instruction onto the byte array of the
     * method to be decompiled. zero-based index.
     */
    public final int index;

    /**
     * opcode of the JVM instruction.
     */
    public final int opcode;

    /**
     * Arguments to the  current opcode instruction.
     */
    public final byte[] args;

    /**
     * opcode name of the instruction.
     */
    public final String insName;

    /**
     * Length of the instruction in bytes.
     */
    public final int length;

    /**
     * If this is a wide instruction.
     */
    public final boolean wide;

    /**
     * Previous instruction in the method pool *
     */
    private JInstruction prev;

    /**
     * Next instruction in the method pool *
     */
    private JInstruction next;

    /**
     * Position of the method into the method pool *
     */
    public int position;

    /**
     * @param rhsIndex  Index of the instruction into the
     *                  method bytecode array.
     * @param rhsOpcode Opcode of the JVM instruction.
     * @param rhsArgs   Arguments to the JVM opcodes.
     * @param rhsWide   If the previous instruction was a wide
     *                  instruction.
     */
    public JInstruction(int rhsIndex,
                        int rhsOpcode, byte[] rhsArgs,
                        boolean rhsWide) {
        prev = null;
        next = null;
        position = 1;

        index = rhsIndex;
        opcode = rhsOpcode;
        args = rhsArgs;

        insName = JJvmSet.getIns(rhsOpcode);
        length = (rhsArgs == null) ? 1 : rhsArgs.length + 1;
        wide = rhsWide;
    }

    /**
     * Appends a instruction to this
     *
     * @param rhsNext Next Instruction to be appended to this.
     */
    public void append(JInstruction rhsNext) {
        this.next = rhsNext;
        rhsNext.prev = this;
        rhsNext.position = this.position + 1;
    }

    /**
     * @return Returns previous instruction *
     */
    public JInstruction prev() {
        return prev;
    }

    /**
     * @return Returns next instruction *
     */
    public JInstruction next() {
        return next;
    }

    /**
     * @param obj Object to be compared.
     * @return true if index is there.
     *         false, otherwise.
     */
    public boolean equals(Object obj) {
        if (obj instanceof JInstruction) {
            return ((JInstruction) obj).index == this.index;
        } else {
            return false;
        }
    }


    /**
     * @return Returns the length of the instruction. *
     */
    public int getLength() {
        return length;
    }

    /**
     * @return Returns the opcode name
     */
    public String getInsName() {
        return insName;
    }

    /**
     * @return Returns the next instruction index.
     */
    public int getNextIndex() {
        if (next != null) {
            return next.index;
        } else {
            //Last Index.
            return index + length;
        }
    }

    /**
     * In case this instruction is a load instruction,
     * ( that is loading a datatype onto the JVM stack ) ,
     * then that variable index is returned. If no
     * variable is referenced then INVALID_VAR_INDEX
     * is returned.
     *
     * @return index of the variable referred to.
     *         INVALID_VAR_INDEX, otherwise.
     */
    public int referredVariable() {
        switch (opcode) {
            case OPCODE_ILOAD_0:
            case OPCODE_FLOAD_0:
            case OPCODE_LLOAD_0:
            case OPCODE_DLOAD_0:
            case OPCODE_ALOAD_0:
                return 0;
            case OPCODE_ILOAD_1:
            case OPCODE_FLOAD_1:
            case OPCODE_LLOAD_1:
            case OPCODE_DLOAD_1:
            case OPCODE_ALOAD_1:
                return 1;
            case OPCODE_ILOAD_2:
            case OPCODE_FLOAD_2:
            case OPCODE_LLOAD_2:
            case OPCODE_DLOAD_2:
            case OPCODE_ALOAD_2:
                return 2;
            case OPCODE_ILOAD_3:
            case OPCODE_FLOAD_3:
            case OPCODE_LLOAD_3:
            case OPCODE_DLOAD_3:
            case OPCODE_ALOAD_3:
                return 3;
            case OPCODE_ILOAD:
            case OPCODE_FLOAD:
            case OPCODE_LLOAD:
            case OPCODE_DLOAD:
            case OPCODE_ALOAD:
                return getArgUnsignedWide();
            default:
                return INVALID_VAR_INDEX;
        }
    }

    /**
     * In case this instruction is a store instruction,
     * ( that is popping a local variable from  the JVM stack ) ,
     * then that variable index is returned. If no
     * variable is referenced then INVALID_VAR_INDEX
     * is returned.
     *
     * @return index of the variable referred to.
     *         INVALID_VAR_INDEX, otherwise.
     */
    public int isStoreInstruction() {
        switch (opcode) {
            case OPCODE_ISTORE_0:
            case OPCODE_FSTORE_0:
            case OPCODE_LSTORE_0:
            case OPCODE_DSTORE_0:
            case OPCODE_ASTORE_0:
                return 0;
            case OPCODE_ISTORE_1:
            case OPCODE_FSTORE_1:
            case OPCODE_LSTORE_1:
            case OPCODE_DSTORE_1:
            case OPCODE_ASTORE_1:
                return 1;
            case OPCODE_ISTORE_2:
            case OPCODE_FSTORE_2:
            case OPCODE_LSTORE_2:
            case OPCODE_DSTORE_2:
            case OPCODE_ASTORE_2:
                return 2;
            case OPCODE_ISTORE_3:
            case OPCODE_FSTORE_3:
            case OPCODE_LSTORE_3:
            case OPCODE_DSTORE_3:
            case OPCODE_ASTORE_3:
                return 3;
            case OPCODE_ISTORE:
            case OPCODE_FSTORE:
            case OPCODE_LSTORE:
            case OPCODE_DSTORE:
            case OPCODE_ASTORE:
                return getArgUnsignedWide();
            default:
                return INVALID_VAR_INDEX;
        }
    }

    /**
     * In case this instruction is a jump/branch instruction, this
     * instruction returns the offset mentioned in the two
     * bytes in the argument array. Works whether wide target or not.
     *
     * @return Returns the offset.
     */
    public int getTargetPc2() {
        if (!wide)
            return getTargetPc();
        else
            return getTargetPcW();
    }

    /**
     * In case this instruction is a jump/branch instruction, this
     * instruction returns the offset + index mentioned in the two
     * bytes in the argument array.
     *
     * @return Returns the offset + index.
     */
    public int getTargetPc() {
        return (getOffset() + index) & 0xffff;
    }

    /**
     * In case this instruction is a jump/branch instruction, this
     * instruction returns the offset mentioned in the two
     * bytes in the argument array.
     *
     * @return Returns the offset.
     */
    public int getOffset() {
        return getArgShort() & 0xffff;
    }

    /**
     * In case this instruction is a jump/branch instruction, this
     * instruction returns the offset mentioned in the two
     * bytes in the argument array. Also important to note is the
     * fact this is a 'wide' variant of the conventional jump
     * instructions.
     *
     * @return Returns the offset.
     */
    public int getTargetPcW() {
        int result = (getArgInt() + index) & 0xffff;
        return result;
    }

    /**
     * In case this instruction is a branch instruction
     * on some condition then the operator corresponding to the
     * operator is returned.
     * For eg, for OPCODE_IFEQ ,  = is returned.
     *
     * @return Returns conditional operator for the condition
     *         mentioned in the opcode.
     *         Empty string, if the opcode is not of branch-on-condition type.
     */
    public String getConditionalOperator() {
        switch (opcode) {
            case OPCODE_IFEQ:
            case OPCODE_IF_ICMPEQ:
            case OPCODE_IF_ACMPEQ:
            case OPCODE_IFNULL:
                return OPR_EQ;
            case OPCODE_IFNE:
            case OPCODE_IF_ICMPNE:
            case OPCODE_IF_ACMPNE:
            case OPCODE_IFNONNULL:
                return OPR_NE;
            case OPCODE_IFLT:
            case OPCODE_IF_ICMPLT:
                return OPR_LT;
            case OPCODE_IFGE:
            case OPCODE_IF_ICMPGE:
                return OPR_GE;
            case OPCODE_IFGT:
            case OPCODE_IF_ICMPGT:
                return OPR_GT;
            case OPCODE_IFLE:
            case OPCODE_IF_ICMPLE:
                return OPR_LE;
            default:
                return "";
        }
    }

    /**
     * Returns if this instruction is a switch instruction or not.
     *
     * @return true, if switch instruction. false. otherwise
     */
    public boolean isASwitchIns() {
        return opcode == OPCODE_TABLESWITCH
                || opcode == OPCODE_LOOKUPSWITCH;
    }

    /**
     * Returns if this instruction is an if instruction or not.
     *
     * @return true, if this is an 'if' instruction.
     *         false, otherwise.
     */
    public boolean isAnIfIns() {
        return (opcode >= OPCODE_IFEQ
                && opcode <= OPCODE_IF_ACMPNE)
                || opcode == OPCODE_IFNULL
                || opcode == OPCODE_IFNONNULL;
    }

    /**
     * To check if this instruction denotes the corresponding
     * end-of-line in the source code.
     *
     * @return Returns true, if this denoted end-of-line.
     *         false, otherwise.
     */
    public boolean isEndOfLine() {
        return (opcode >= OPCODE_ISTORE
                && opcode <= OPCODE_SASTORE)
                || opcode == OPCODE_IINC
                || opcode == OPCODE_RETURN
                || opcode == OPCODE_IRETURN
                || opcode == OPCODE_LRETURN
                || opcode == OPCODE_FRETURN
                || opcode == OPCODE_DRETURN
                || opcode == OPCODE_ARETURN
                || opcode == OPCODE_ATHROW
                || opcode == OPCODE_PUTSTATIC
                || opcode == OPCODE_PUTFIELD
                || opcode == OPCODE_POP
                || opcode == OPCODE_POP2;
    }

    /**
     * Denotes if this instruction invokes some other
     * method or interface or a constructor.
     *
     * @return Returns true, if this invokes any one mentioned above,
     *         false, otherwise
     */
    public boolean isInvokeIns() {
        return opcode == OPCODE_INVOKESPECIAL
                || opcode == OPCODE_INVOKEVIRTUAL
                || opcode == OPCODE_INVOKESTATIC
                || opcode == OPCODE_INVOKEINTERFACE;
    }

    public final boolean isEndOfCatch() {
        return opcode == OPCODE_ARETURN ||
                opcode == OPCODE_IRETURN ||
                opcode == OPCODE_LRETURN ||
                opcode == OPCODE_FRETURN ||
                opcode == OPCODE_DRETURN ||
                opcode == OPCODE_RETURN ||
                opcode == OPCODE_POP ||
//               opcode == OPCODE_GOTO ||
//               opcode == OPCODE_GOTOW ||
                opcode == OPCODE_ATHROW ||
                opcode == OPCODE_JSR ||
                opcode == OPCODE_JSRW;
    }

    /**
     * @return unsigned Arg Wide.
     */
    public int getArgUnsignedWide() {
        return getArgUnsignedWide(0);
    }

    /**
     * @param pos Position from which bytes are to be taken from stream
     *            and the value to be written.
     * @return unsigned arg wide.
     */
    public int getArgUnsignedWide(int pos) {
        if (wide) {
            if (pos != 0) {
                return getArgUnsignedShort(pos * 2);
            } else {
                return getArgUnsignedShort(0);
            }
        } else {
            return getArgUnsignedByte(pos);
        }
    }


    /**
     * @return unsigned byte.
     */
    public int getArgUnsignedByte() {
        return getArgUnsignedByte(0);
    }

    /**
     * @param pos Position from which bytes are to be taken from stream
     *            and the value to be written.
     * @return unsigned byte.
     */
    public int getArgUnsignedByte(int pos) {
        return Helper.signedToUnsigned(args[pos]);
    }


    public int getArgUnsignedShort() {
        return getArgUnsignedShort(0);
    }

    public final int getArgUnsignedShort(int pos) {
        int byte1 = Helper.signedToUnsigned(args[pos]);
        int byte2 = Helper.signedToUnsigned(args[pos + 1]);
        int result = (byte1 << 8) | byte2;
        return result;
    }

    public int getArgUnsignedInt() {
        return getArgUnsignedInt(0);
    }

    public int getArgUnsignedInt(int pos) {
        int byte1 = Helper.signedToUnsigned(args[pos]);
        int byte2 = Helper.signedToUnsigned(args[pos + 1]);
        int byte3 = Helper.signedToUnsigned(args[pos + 2]);
        int byte4 = Helper.signedToUnsigned(args[pos + 3]);
        int result = (byte1 << 24) | (byte2 << 16) | (byte3 << 8) | byte4;
        return result;
    }

    public int getArgWide() {
        return getArgWide(0);
    }

    public int getArgWide(int pos) {
        if (wide) {
            if (pos != 0) {
                return getArgShort(pos * 2);
            } else {
                return getArgShort(0);
            }
        } else {
            return getArgByte(pos);
        }
    }

    public int getArgByte() {
        return getArgByte(0);
    }

    public int getArgByte(int pos) {
        return args[pos];
    }

    public int getArgShort() {
        return getArgShort(0);
    }

    public int getArgShort(int pos) {
        int byte1 = args[pos];
        int byte2 = Helper.signedToUnsigned(args[pos + 1]);
        int result = (byte1 << 8) | byte2;
        return result;
    }

    public int getArgInt() {
        return getArgInt(0);
    }


    public int getArgInt(int pos) {
        int byte1 = args[pos];
        int byte2 = Helper.signedToUnsigned(args[pos + 1]);
        int byte3 = Helper.signedToUnsigned(args[pos + 2]);
        int byte4 = Helper.signedToUnsigned(args[pos + 3]);
        int result = (byte1 << 24) | (byte2 << 16) | (byte3 << 8) | byte4;
        return result;
    }

    /**
     * Stringified form of JInstruction
     *
     * @return String representation of JInstruction.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("");
        sb.append(" " + index + ": " + insName);
        if (args == null) {
            return sb.toString();
        }
        sb.append("[");
        for (int i = 0; i < args.length; i++) {
            sb.append(" " + getArgUnsignedByte(i));
        }
        sb.append("]");
        return sb.toString();
    }
}
