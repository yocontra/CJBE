/*
 * @(#)JConstantPool.java
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

import jreversepro.common.Helper;
import jreversepro.common.KeyWords;
import jreversepro.parser.ClassParserException;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>JConstantPool</b> represents the abstraction of the ConstantPool.
 *
 * @author Karthik Kumar
 */
public class JConstantPool implements KeyWords {


    /**
     * STR_INVALID corresponds to an invalid entry in the
     * ConstantPool.
     */
    public static final String STR_INVALID = "Invalid String";

    /**
     * PTR_INVALID of a pointer of a ConstantPool tag means that
     * they are not applicable for that ConstantPool tag.
     */
    public static final int PTR_INVALID = -1;

    /**
     * TAG_NOTHING means that the ConstantPool Entry is invalid.
     */
    public static final int TAG_NOTHING = -1;

    /**
     * TAG_UTF8 corresponds to CONSTANT_UTF8
     */
    public static final int TAG_UTF8 = 1;

    /**
     * TAG_INTEGER corresponds to CONSTANT_INTEGER
     */
    public static final int TAG_INTEGER = 3;

    /**
     * TAG_FLOAT corresponds to CONSTANT_FLOAT
     */
    public static final int TAG_FLOAT = 4;

    /**
     * TAG_LONG corresponds to CONSTANT_LONG
     */
    public static final int TAG_LONG = 5;

    /**
     * TAG_DOUBLE corresponds to CONSTANT_DOUBLE
     */
    public static final int TAG_DOUBLE = 6;

    /**
     * TAG_CLASS corresponds to CONSTANT_CLASS
     */
    public static final int TAG_CLASS = 7;

    /**
     * TAG_STRING corresponds to CONSTANT_STRING
     */
    public static final int TAG_STRING = 8;

    /**
     * TAG_FIELDREF corresponds to CONSTANT_FIELDREF
     */
    public static final int TAG_FIELDREF = 9;

    /**
     * TAG_METHODREF corresponds to CONSTANT_METHODREF
     */
    public static final int TAG_METHODREF = 10;

    /**
     * TAG_INTERFACEREF corresponds to CONSTANT_INTERFACEREF
     */
    public static final int TAG_INTERFACEREF = 11;

    /**
     * TAG_NAMETYPE  corresponds to CONSTANT_NAMETYPE
     */
    public static final int TAG_NAMETYPE = 12;

    /**
     * listEntries contains the ConstantPool Entries.
     * The individual elements of the list are
     * JConstantPoolEntry.
     */
    private List listEntries;

    /**
     * Reference to importedClasses that contains the list
     * of imported classes.
     */
    private JImport importedClasses;

    /**
     * Constructor.
     *
     * @param cpMax Maximum size of the ConstantPool.
     */
    public JConstantPool(int cpMax) {
        listEntries = new ArrayList(cpMax);
        importedClasses = null;
        //Initially set to null.
    }


    /**
     * Returns the number of ConstantPool Entries.
     *
     * @return Returns the cp entry count.
     */
    public int getMaxCpEntry() {
        return listEntries.size();
    }

    /**
     * Returns the constantpool entries.
     * The individual elements are JConstantPoolEntry.
     *
     * @return Returns list of constantpool entries.
     */
    public List getEntries() {
        return listEntries;
    }

    /**
     * Adds a new TAG_FIELDREF entry to the constantpool.
     *
     * @param ptr1 Pointer to TAG_CLASS
     * @param ptr2 Pointer to TAG_NAMETYPE
     */
    public void addFieldRefEntry(int ptr1, int ptr2) {
        listEntries.add(
                new JConstantPoolEntry(
                        TAG_FIELDREF,
                        STR_INVALID,
                        ptr1,
                        ptr2));
    }

    /**
     * Adds a new TAG_METHODREF entry to the constantpool.
     *
     * @param ptr1 Pointer to TAG_CLASS
     * @param ptr2 Pointer to TAG_NAMETYPE
     */
    public void addMethodRefEntry(int ptr1, int ptr2) {
        listEntries.add(
                new JConstantPoolEntry(
                        TAG_METHODREF,
                        STR_INVALID,
                        ptr1,
                        ptr2));
    }

    /**
     * Adds a new TAG_INTERFACEREF entry to the constantpool.
     *
     * @param ptr1 Pointer to TAG_CLASS
     * @param ptr2 Pointer to TAG_NAMETYPE
     */
    public void addInterfaceRefEntry(int ptr1, int ptr2) {
        listEntries.add(
                new JConstantPoolEntry(
                        TAG_INTERFACEREF,
                        STR_INVALID,
                        ptr1,
                        ptr2));
    }

    /**
     * Adds a new TAG_NAMETYPE entry to the constantpool.
     *
     * @param ptr1 Pointer to TAG_UTF8
     * @param ptr2 Pointer to TAG_UTF8
     */
    public void addNameTypeEntry(int ptr1, int ptr2) {
        listEntries.add(
                new JConstantPoolEntry(
                        TAG_NAMETYPE,
                        STR_INVALID,
                        ptr1,
                        ptr2));
    }

    /**
     * Adds a NULL entry to the ConstantPool.
     * Mainly useful when we add long/ double.
     */
    public void addNullEntry() {
        listEntries.add(
                new JConstantPoolEntry(
                        TAG_NOTHING,
                        STR_INVALID,
                        PTR_INVALID,
                        PTR_INVALID));
    }

    /**
     * Adds a new TAG_UTF8 entry to the constantpool.
     *
     * @param value Value of the UTF8 String.
     */
    public void addUtf8Entry(String value) {
        listEntries.add(
                new JConstantPoolEntry(
                        TAG_UTF8,
                        value,
                        PTR_INVALID,
                        PTR_INVALID));
    }

    /**
     * Adds a new TAG_INTEGER entry to the constantpool.
     *
     * @param value Value of the integer.
     */
    public void addIntegerEntry(String value) {
        listEntries.add(
                new JConstantPoolEntry(
                        TAG_INTEGER,
                        value,
                        PTR_INVALID,
                        PTR_INVALID));
    }

    /**
     * Adds a new TAG_FLOAT entry to the constantpool.
     *
     * @param value Value of the float number.
     */
    public void addFloatEntry(String value) {
        listEntries.add(
                new JConstantPoolEntry(
                        TAG_FLOAT,
                        value,
                        PTR_INVALID,
                        PTR_INVALID));
    }

    /**
     * Adds a new TAG_DOUBLE entry to the constantpool.
     *
     * @param value Value of the double.
     */
    public void addDoubleEntry(String value) {
        listEntries.add(
                new JConstantPoolEntry(
                        TAG_DOUBLE,
                        value,
                        PTR_INVALID,
                        PTR_INVALID));
    }


    /**
     * Adds a new TAG_LONG entry to the constantpool.
     *
     * @param value Value of the Long.
     */
    public void addLongEntry(String value) {
        listEntries.add(
                new JConstantPoolEntry(
                        TAG_LONG,
                        value,
                        PTR_INVALID,
                        PTR_INVALID));
    }

    /**
     * Adds a new TAG_CLASS entry to the constantpool.
     *
     * @param classIndex Index to UTF8 string containing
     *                   class name.
     */
    public void addClassEntry(int classIndex) {
        listEntries.add(
                new JConstantPoolEntry(
                        TAG_CLASS,
                        STR_INVALID,
                        classIndex,
                        PTR_INVALID));
    }

    /**
     * Adds a new TAG_STRING entry to the constantpool.
     *
     * @param stringIndex Index to the UTF8 string
     *                    containing the stringvalue.
     */
    public void addStringEntry(int stringIndex) {
        listEntries.add(
                new JConstantPoolEntry(
                        TAG_STRING,
                        STR_INVALID,
                        stringIndex,
                        PTR_INVALID));
    }

    /**
     * Returns the data type of the given ConstantPool Index.
     *
     * @param index Index to the ConstantPool Entry.
     * @return long/double/String/integer appropriately
     */
    public String getDataType(int index) {
        switch (getTagByte(index)) {
            case TAG_LONG:
                return LONG;
            case TAG_DOUBLE:
                return DOUBLE;
            case TAG_STRING:
                return CLASS_STRING;
            case TAG_INTEGER:
                return INT;
            default:
                return NULL;
        }
    }

    /**
     * Returns if the given index to the ConstantPool
     * is TAG_DOUBLE or not.
     *
     * @param index Index to ConstantPool
     * @return Returns true, if it is a TAG_DOUBLE.
     *         false, otherwise.
     */
    public boolean isTagDouble(int index) {
        return getTagByte(index) == TAG_DOUBLE;
    }

    /**
     * Returns the first pointer of the ConstantPool Data.
     *
     * @param index Index to ConstantPool
     * @return Returns the integer.
     */
    public int getPtr1(int index) {
        return ((JConstantPoolEntry) listEntries.get(index)).getPtr1();
    }

    /**
     * Returns the second pointer of the ConstantPool Data.
     *
     * @param index Index to ConstantPool
     * @return Returns the integer.
     */
    public int getPtr2(int index) {
        return ((JConstantPoolEntry) listEntries.get(index)).getPtr2();
    }

    /**
     * Returns the tagbyte of the ConstantPool.
     *
     * @param index Index to ConstantPool
     * @return Returns the tag byte.
     */
    public int getTagByte(int index) {
        return ((JConstantPoolEntry) listEntries.get(index)).getTagByte();
    }

    /**
     * Returns the ConstantPool value.
     *
     * @param index Index to ConstantPool
     * @return Returns the value of that cp entry.
     */
    public String getCpValue(int index) {
        return ((JConstantPoolEntry) listEntries.get(index)).getValue();
    }


    /**
     * Returns the Utf8 value pointed by the first pointer
     * of the index to the ConstantPool.
     * Say for example, if entry #6 has ptr1 to be #8
     * and the utf8 value of #8 is "MyString", this method
     * on given input 6 returns "MyString".
     *
     * @param index Index to ConstantPool
     * @return Returns a String a Utf8 value.
     */
    public String getFirstDirectName(int index) {
        int ptr1 =
                ((JConstantPoolEntry) listEntries.get(index)).
                        getPtr1();
        return ((JConstantPoolEntry) listEntries.get(ptr1)).
                getValue();
    }

    /**
     * Returns the Utf8 value pointed by the II pointer
     * of the index to the ConstantPool.
     * Say for example, if entry #6 has ptr2 to be #8
     * and the utf8 value of #8 is "MyString", this method
     * on given input 6 returns "MyString".
     *
     * @param index Index to ConstantPool
     * @return Returns a String a Utf8 value.
     */
    public String getSecondDirectName(int index) {
        int ptr2 =
                ((JConstantPoolEntry) listEntries.get(index)).
                        getPtr2();
        return ((JConstantPoolEntry) listEntries.get(ptr2)).
                getValue();
    }

    /**
     * Given an index to TAG_CLASS this returns the class
     * name pointed to by it. If the index is 0 then a
     * class of type ANY is returned.
     *
     * @param index Index to ConstantPool
     * @return Returns a String - class name
     */
    public String getClassName(int index) {
        return (index == 0) ? ANY : getFirstDirectName(index);
    }

    /**
     * Given an index to TAG_NAMETYPE this returns the
     * name of the member ( field / method )
     *
     * @param index Index to ConstantPool
     * @return Returns name of the member
     */
    public String getFieldName(int index) {
        return getFirstDirectName(index);
    }

    /**
     * Given an index to TAG_NAMETYPE this returns the
     * type of the member ( field / method )
     *
     * @param index Index to ConstantPool
     * @return Returns type of the member
     */
    public String getFieldType(int index) {
        return getSecondDirectName(index);
    }

    /**
     * Given an index to TAG_UTF8 this returns the
     * string value.
     *
     * @param index Index to ConstantPool
     * @return Returns value of that Cp Entry.
     */
    public String getUtf8String(int index) {
        return getCpValue(index);
    }

    /**
     * Given an Cp index this returns the proper
     * constant pool value depending on the tag type.
     * If TAG_INTEGER, returned as such.
     * Else if TAG_STRING all the escape characters are
     * properly quoted and returned.
     *
     * @param index Index to ConstantPool
     * @return Returns value of that Cp Entry.
     * @throws ClassParserException Thrown in case of an wrong
     *                              constantpool index being referrred.
     */
    public String getLdcString(int index)
            throws ClassParserException {
        String result = STR_INVALID;
        int tagByte = getTagByte(index);

        if (tagByte == TAG_STRING) {
            String strVal = getFirstDirectName(index);
            strVal = Helper.replaceEscapeChars(strVal);
            result = "\"" + strVal + "\"";
        } else if (tagByte == TAG_INTEGER) {
            result = getCpValue(index);
        } else {
            throw new ClassParserException(
                    "Referring invalid constantpool index "
                            + index);
        }
        return result;
    }

    /**
     * The constantpool is the one and only source that
     * contains the references to external types. Hence all
     * the entries are analyzed for all external types referred to by
     * this class and are consolidated into a class called JImport.
     * This JImport can further be used to get the short form of
     * classname etc.
     *
     * @return Reference to JImport.
     */
    public JImport getImportedClasses() {
        if (importedClasses != null) {
            return importedClasses;
        } else {
            importedClasses = new JImport();
            for (int i = 0; i < listEntries.size(); i++) {
                JConstantPoolEntry ent
                        = (JConstantPoolEntry) listEntries.get(i);
                switch (ent.getTagByte()) {
                    case TAG_CLASS:
                        importedClasses.addClass(
                                Helper.getJavaDataType(
                                        getFirstDirectName(i), true));
                        break;

                    case TAG_FIELDREF:
                        String type = getType(ent);
                        importedClasses.addClass(
                                Helper.getJavaDataType(type, true));
                        break;

                    case TAG_METHODREF:
                    case TAG_INTERFACEREF:
                        String methodType = getType(ent);
                        List args = Helper.getArguments(methodType);
                        for (int j = 0; j < args.size(); j++) {
                            String argtype = (String) args.get(j);
                            importedClasses.addClass(
                                    Helper.getJavaDataType(argtype, true));
                        }
                        importedClasses.addClass(
                                Helper.getJavaDataType(
                                        Helper.getReturnType(methodType), true));
                        break;
                }
            }
            return importedClasses;
        }
    }

    /**
     * Returns the value for the ConstantPool Entries
     * according to their types.
     *
     * @param index Index to ConstantPool
     * @return Returns value of that Cp Entry.
     * @throws ClassParserException Throws in case the referred
     *                              constantpool index does not represent a basic data type index.
     */
    public String getBasicDataTypeValue(int index)
            throws ClassParserException {
        int tagByte = getTagByte(index);
        switch (tagByte) {
            case TAG_LONG:
            case TAG_FLOAT:
            case TAG_DOUBLE:
            case TAG_INTEGER:
                return getCpValue(index);
            case TAG_STRING:
                StringBuffer sb = new StringBuffer("\"");
                sb.append(Helper.replaceEscapeChars(
                        getFirstDirectName(index)) + "\"");
                return sb.toString();
            default:
                throw new ClassParserException(
                        "ConstantPool Index #" + index
                                + " doesnt represent a basic datatype ");
        }
    }


    /**
     * Returns the actual name of the tag .
     * according to their types.
     *
     * @param tagByte Tag Byte value
     * @return Returns the string representation of the
     *         integer tagByte
     */
    public static String getTagName(int tagByte) {
        switch (tagByte) {
            case TAG_UTF8:
                return ("TAG_UTF8");

            case TAG_INTEGER:
                return ("TAG_INTEGER");

            case TAG_FLOAT:
                return ("TAG_FLOAT");

            case TAG_LONG:
                return ("TAG_LONG");

            case TAG_DOUBLE:
                return ("TAG_DOUBLE");

            case TAG_CLASS:
                return ("TAG_CLASS");

            case TAG_STRING:
                return ("TAG_STRING");

            case TAG_FIELDREF:
                return ("TAG_FIELDREF");

            case TAG_METHODREF:
                return ("TAG_METHODREF");

            case TAG_INTERFACEREF:
                return ("TAG_INTERFACEREF");

            case TAG_NAMETYPE:
                return ("TAG_NAMETYPE");

            case TAG_NOTHING:
                return ("TAG_NOTHING");

            default:
                return ("Invalid Tag");
        }
    }


    /**
     * Describes the tag in brief. If a tag is relative tag
     * like TAG_FIELDREF or TAG_METHODREF then it lists out the
     * related tag information too,
     *
     * @param index Index to the ConstantPool.
     * @return Returns a string describing the tag entry.
     * @throws ClassParserException Thrown in case of an invalid
     *                              constantpool reference.
     */
    public String getTagDescriptor(int index)
            throws ClassParserException {
        StringBuffer result = new StringBuffer("");
        int tagByte = getTagByte(index);
        switch (tagByte) {
            case TAG_METHODREF:
            case TAG_FIELDREF:
            case TAG_INTERFACEREF:
                JConstantPoolEntry ent =
                        (JConstantPoolEntry) listEntries.get(index);
                result.append(getType(ent) + "," + getName(ent));
                break;
            case TAG_STRING:
                result.append(getLdcString(index));
                break;
            case TAG_CLASS:
                result.append(getClassName(index));
                break;
        }
        return result.toString();
    }

    /**
     * Returns the name pointed to by this JConstantPoolEntry.
     * Usually this tag happens to be one of
     * TAG_FIELDREF, TAG_METHODREF or TAG_INTERFACEREF.
     *
     * @param ent Constant Pool Entry
     * @return Returns name of the member (field/method/interface)
     *         pointed to by the Constant Pool Entry.
     */
    public String getName(JConstantPoolEntry ent) {
        JConstantPoolEntry entNameType =
                (JConstantPoolEntry)
                        listEntries.get(ent.getPtr2());
        JConstantPoolEntry entName =
                (JConstantPoolEntry)
                        listEntries.get(entNameType.getPtr1());
        return entName.getValue();
    }

    /**
     * Returns the type pointed to by this JConstantPoolEntry.
     * Usually this tag happens to be one of
     * TAG_FIELDREF, TAG_METHODREF or TAG_INTERFACEREF.
     *
     * @param ent Constant Pool Entry
     * @return Returns type of the member (field/method/interface)
     *         pointed to by the Constant Pool Entry.
     */
    public String getType(JConstantPoolEntry ent) {
        JConstantPoolEntry entNameType =
                (JConstantPoolEntry)
                        listEntries.get(ent.getPtr2());
        JConstantPoolEntry entType =
                (JConstantPoolEntry)
                        listEntries.get(entNameType.getPtr2());
        return entType.getValue();
    }

    /**
     * Returns the whole ConstantPool info in a formatter manner.
     *
     * @return Returns a string.
     */
    public String getEntryInfo() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < listEntries.size(); i++) {
            sb.append("\n" + i + " : " + listEntries.get(i));
        }
        return sb.toString();
    }


    /**
     * Returns the name pointed to by this JConstantPoolEntry.
     * Usually this tag happens to be one of
     * TAG_FIELDREF, TAG_METHODREF or TAG_INTERFACEREF.
     *
     * @param cpIndex Index to Constant Pool
     * @return Returns name of the member (field/method/interface)
     *         pointed to by the Constant Pool Entry.
     */
    public String getEntryInfo(int cpIndex) {
        StringBuffer sb = new StringBuffer();
        getSingleEntryInfo(sb, cpIndex);
        return sb.toString();
    }

    /**
     * Describes the tag in brief. If a tag is relative tag
     * like TAG_FIELDREF or TAG_METHODREF then it continues to
     * report the dependent tag information until it reaches a
     * tag which is not dependent on anything else.
     * Usually the final tag happens to a TAG_UTF8.
     *
     * @param sb      StringBuffer containing the info of tags dependent on this
     *                already ( cpIndex ).
     * @param cpIndex Index to ConstantPool.
     */
    private void getSingleEntryInfo(StringBuffer sb, int cpIndex) {
        if (cpIndex >= 1 && cpIndex < listEntries.size()) {
            JConstantPoolEntry ent = (JConstantPoolEntry)
                    listEntries.get(cpIndex);
            sb.append("\n" + cpIndex + " : " + ent);
            getSingleEntryInfo(sb, ent.getPtr1());
            getSingleEntryInfo(sb, ent.getPtr2());
        }
    }

}
