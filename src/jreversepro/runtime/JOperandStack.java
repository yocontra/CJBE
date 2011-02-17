/**
 * JOperandStack.java
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


import jreversepro.common.KeyWords;
import jreversepro.revengine.RevEngineException;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
 * Operand Stack is the abstraction of the Java Method Operand Stack.
 * The variables are pushed to and popped from, this stack.
 *
 * @author Karthik Kumar.
 */
public class JOperandStack extends Stack
        implements KeyWords,
        OperandConstants {
    /**
     * A Vector of constants. The  individual members of this
     * vector are 'String'. They are primarily used in the following
     * case. Lets say - we have a code called .
     * String [] args = { "run", "args", "args1" };
     * In this case all the individual members of the array
     * namely args come into the constants.
     */
    Vector constants;

    /**
     * Empty constructor
     */
    public JOperandStack() {
        constants = new Vector();
    }

    /**
     * Adds a new constant.
     *
     * @param constant New Constant to be added to the vector.
     */
    public void addConstant(String constant) {
        constants.add(constant);
    }

    /**
     * Deletes all the constants that were stored in the
     * vector already.
     */
    public void removeAllConstants() {
        constants.removeAllElements();
    }

    /**
     * @return List of constants that were stored.
     */
    public List getConstants() {
        return constants;
    }

    /**
     * Returns the values of the constants serialized such
     * that it corresponds to java syntax code too.
     *
     * @return Constant values.
     */
    public String getConstantValues() {
        StringBuffer result = new StringBuffer("{");
        for (int i = 0; i < constants.size(); i++) {
            if (i != 0) {
                result.append(",");
            }
            result.append(constants.get(i));
        }
        result.append("}");
        return result.toString();
    }

    /**
     * @param value      Value of the Operand - Stack element.
     *                   The Value is String.
     * @param datatype   Datatype of the stack element.
     * @param precedence precedence of the stack element.
     */
    public void push(String value, String datatype, int precedence) {
        super.push(new Operand(value, datatype, precedence));
    }

    /**
     * @param value      Value of the Operand - Stack element.
     *                   The Value is int.
     * @param datatype   Datatype of the stack element.
     * @param precedence precedence of the stack element.
     */
    public void push(int value, String datatype, int precedence) {
        super.push(new Operand(value, datatype, precedence));
    }

    /**
     * @return Returns the data type of the topmost content of the
     *         stack.
     */
    public String topDatatype() {
        return ((Operand) this.peek()).datatype;
    }

    /**
     * Lets us know if the top data type is foreign. Mainly this method
     * is necessary when we have a operand loaded onto the stack from
     * outside. ( Eg: an argument to the exception handler of the
     * exception type ).
     *
     * @return true, if the top type is foreign. false. otherwise.
     */
    public boolean isTopDatatypeForeign() {
        if (this.empty()) {
            return false;
        } else {
            String dataType = ((Operand) this.peek()).getDatatype();
            return dataType.equals(FOREIGN_CLASS)
                    || dataType.indexOf("<") != -1;
        }
    }

    /**
     * Merges the top two contents of the operand stack.
     * Eg, if the stack contents are
     * ab   String
     * cd   String ( in  reverse order - stack top first ).,
     * On merging it becomes
     * abcd  String ( in  reverse order - stack top first ).,
     * The precondition is that the datatypes of first top two elements
     * ought to be the same. Then only this is valid.
     *
     * @throws RevEngineException Thrown when there are less than 2 elements
     *                            in the stack.
     */
    public void mergeTopTwo()
            throws RevEngineException {
        if (this.size() < 2) {
            throw new
                    RevEngineException("Cannot merge ternary expressions");
        }
        Operand op1 = (Operand) this.pop();
        Operand op2 = (Operand) this.pop();

        push(new Operand(op2.getValue() + op1.getValue(),
                op2.getDatatype(), L_TERN));
    }
}