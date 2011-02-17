/*
 * @(#)JMember.java
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
package jreversepro.reflect;


/**
 * Describes a class member ( both field and method ).
 *
 * @author Karthik Kumar.
 */
public class JMember {

    /**
     * Public access specifier.
     */
    public static final int ACC_PUBLIC = 0x0001;

    /**
     * Private access specifier.
     */
    public static final int ACC_PRIVATE = 0x0002;

    /**
     * Protected access specifier.
     */
    public static final int ACC_PROTECTED = 0x0004;

    /**
     * Qualifer 'static'
     */
    public static final int ACC_STATIC = 0x0008;

    /**
     * Qualifier 'final'
     */
    public static final int ACC_FINAL = 0x0010;

    /**
     * Qualifer 'synchronized'
     */
    public static final int ACC_SYNCHRONIZED = 0x0020;

    /**
     * Qualifer 'native'
     */
    public static final int ACC_NATIVE = 0x0100;

    /**
     * Qualifer 'abstract'
     */
    public static final int ACC_ABSTRACT = 0x0400;

    /**
     * Qualifer 'strictfp'
     */
    public static final int ACC_STRICT = 0x0800;

    /**
     * Qualifer 'volatile'.
     * This qualifer is valid for fields only.
     */
    public static final int ACC_VOLATILE = 0x0040;

    /**
     * Qualifer 'transient'
     * This qualifer is valid for fields only.
     */
    public static final int ACC_TRANSIENT = 0x0080;

    /**
     * This field contains the datatype of the member.
     */
    protected String datatype;

    /**
     * This field contains the name of the member ( field/ method).
     */
    protected String name;

    /**
     * This contains the integer representation of the
     * qualifier of the member with the appropriate combination
     * of bits set to know the qualifier string.
     */
    protected int qualifier;

    /**
     * Setter method for datatype.
     *
     * @param rhsType data type value.
     */
    public void setDatatype(String rhsType) {
        datatype = rhsType;
    }

    /**
     * Setter method for name
     *
     * @param rhsName New Name
     */
    public void setName(String rhsName) {
        name = rhsName;
    }

    /**
     * Setter method for qualifiers.
     *
     * @param rhsQualify qualifier value
     */
    public void setQualifier(int rhsQualify) {
        qualifier = rhsQualify;
    }

    /**
     * Returns the String Representation of the qualifier.
     * Certain qualifiers like volatile, transient, sync.
     * are applicable only for methods and fields. and not
     * classes. To identify them separately, we also pass
     * another parameter called memberOnly. Only if this is set
     * then those bits are checked for. Else they are ignored, since
     * for a class/interface they may not be applicable.
     *
     * @param rhsQualifier Qualifier byte with the bits set.
     * @param memberOnly   Only if this is set then the bits
     *                     relevant to fields and methods only are checked for.
     *                     Else ignored.
     * @return String Containing the representation.
     */
    public static String getStringRep(int rhsQualifier,
                                      boolean memberOnly) {

        StringBuffer access = new StringBuffer("");
        int qualifier = rhsQualifier;
        if ((rhsQualifier & ACC_PUBLIC) != 0) {
            access.append("public ");
        } else if ((rhsQualifier & ACC_PRIVATE) != 0) {
            access.append("private ");
        } else if ((rhsQualifier & ACC_PROTECTED) != 0) {
            access.append("protected ");
        }

        if ((rhsQualifier & ACC_STATIC) != 0) {
            access.append("static ");
        }
        if ((rhsQualifier & ACC_FINAL) != 0) {
            access.append("final ");
        }
        if ((rhsQualifier & ACC_ABSTRACT) != 0) {
            access.append("abstract ");
        }

        if (memberOnly) {

            //Fields only
            if ((rhsQualifier & ACC_VOLATILE) != 0) {
                access.append("volatile ");
            }
            if ((rhsQualifier & ACC_TRANSIENT) != 0) {
                access.append("transient ");
            }

            // Methods only
            if ((rhsQualifier & ACC_SYNCHRONIZED) != 0) {
                access.append("synchronized ");
            }
            if ((rhsQualifier & ACC_NATIVE) != 0) {
                access.append("native ");
            }
            if ((rhsQualifier & ACC_STRICT) != 0) {
                access.append("strictfp ");
            }
        }
        return access.toString();
    }

    /**
     * Getter method for name
     *
     * @return Returns name of the member.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter method for the qualifier.
     *
     * @return Returns the qualifier integer.
     */
    public int getQualifier() {
        return qualifier;
    }

    /**
     * @return Returns the qualifier in
     *         string format.
     */
    public String getQualifierName() {
        return getStringRep(qualifier, true);
    }

    /**
     * @return Returns the data type
     */
    public String getDatatype() {
        return datatype;
    }

    /**
     * Returns if this member is a 'final' one or not.
     *
     * @return Returns true, if final. false, otherwise.
     */
    public boolean isFinal() {
        return (qualifier & ACC_FINAL) != 0;
    }

    /**
     * Returns if this member is a 'static' one or not.
     *
     * @return Returns true, if static. false, otherwise.
     */
    public boolean isStatic() {
        return (qualifier & ACC_STATIC) != 0;
    }
}