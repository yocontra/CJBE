/*
 * @(#)KeyWords.java
 *
 * JReversePro - Java Decompiler / Disassembler.
 * Copyright (C) 2000 2001 Karthik Kumar.
 * EMail: akkumar@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it , under the terms of the GNU General   License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General   License for more details.
 * You should have received a copy of the GNU General   License
 * along with this program.If not, write to
 *  The Free Software Foundation, Inc.,
 *  59 Temple Place - Suite 330,
 *  Boston, MA 02111-1307, USA.
 **/
package jreversepro.common;

public interface KeyWords {
    //Constants containing KeyWords
    String FOREIGN_CLASS = "<ForeignClass>";
    String FOREIGN_OBJ = "<ForeignObject>";

    String BREAK = "break";
    String CONTINUE = "continue";
    String DEFAULT = "default";

    String CASE = "case";
    String GOTO = "goto";

    /**
     * Default Package that is included when the JVM is
     * launched in the beginning.
     */
    String DEFAULT_PACKAGE = "java.lang";

    /**
     * JVM Representation of java.lang.String
     */
    String CLASS_STRING = "java/lang/String";

    String JVM_BOOLEAN = "Z";

    String JVM_CHAR = "C";

    String JVM_VOID = "V";

    /**
     * 'boolean' datatype.
     */
    String BOOLEAN = "Z";

    /**
     * 'char' datatype.
     */
    String CHAR = "C";

    /**
     * 'byte' datatype.
     */
    String BYTE = "B";

    /**
     * 'int' datatype.
     */
    String INT = "I";

    /**
     * 'short' datatype.
     */
    String SHORT = "S";

    /**
     * 'float' datatype.
     */
    String FLOAT = "F";

    /**
     * 'long' datatype.
     */
    String LONG = "J";

    /**
     * 'double' datatype.
     */
    String DOUBLE = "D";

    /**
     * datatype is a reference to an object
     */
    String REFERENCE = "reference";

    /**
     * 'null' datatype.
     */
    String NULL = "null";

    /**
     * datatype is of type returnaddress
     */
    String RET_ADDR = "returnaddress";

    /**
     * 'void' datatype.
     */
    String VOID = "void";

    /**
     * this pointer variable name
     */
    String THIS = "this";

    /**
     * Refers to the name of the current class type.
     */
    String THISCLASS = "**this_class**";

    /**
     * JVM representation of the method static {.. }
     */
    String CLINIT = "<clinit>";

    /**
     * JVM representation of the constructor method.
     */
    String INIT = "<init>";

    /**
     * JVM representation of the class java.lang.Object
     */
    String LANG_OBJECT = "java/lang/Object";

    /**
     * Exception Class of type 'any'.
     */
    String ANY = "<any>";


    String TRUE = "true";

    String FALSE = "false";

    String CLASS = "class";

    String INTERFACE = "interface";

    String LENGTH = "length";

    String THROW = "throw";

    String INSTANCEOF = "instanceof";

    String NEW = "new";

    char SPACE = ' ';

    String OPEN_BRACKET = "[";

    String CLOSE_BRACKET = "]";

    String EQUALTO = " = ";

    String RETURN = "return";

    String SWITCH = "switch";

    String STATIC = "static";

    String SUPER = "super";

    // Operators
    String OPR_GT = ">";
    String OPR_GE = ">=";
    String OPR_LT = "<";
    String OPR_LE = "<=";
    String OPR_NE = "!=";
    String OPR_EQ = "==";
    String OPR_NOT = "!";

    String COND_OR = "||";
    String COND_AND = "&&";
    String COND_NOT = "!";
}
