/*
 * @(#)JConstantPoolEntry.java
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
 */
package jreversepro.reflect;


/**
 * JConstantPoolEntry is an Entry of the CONSTANT POOL data structure
 * the class file.
 *
 * @author Karthik Kumar.
 */
public class JConstantPoolEntry {

    /**
     * ptr1 is the first pointer from this ConstantPoolEntry.
     * An example could be TAG_CLASS. TagClass would have an entry
     * pointing to a TAG_UTF8 that contains the class name.
     * In that case the ptr1 of TAG_CLASS entry would give
     * lead to TAG_UTF8.
     */
    int ptr1;

    /**
     * ptr2 is the second pointer from this ConstantPoolEntry.
     * An example could be TAG_FIELDTYPE. TagFieldType will have
     * two entries - the first one to TAG_CLASS and the second one
     * to TAG_NAMETYPE.In this case ptr2 would have the number of
     * TAG_NAMETYPE index.
     */
    int ptr2;

    /**
     * This is applicable to TAG_UTF8 TAG_INTEGER
     * TAG_FLOAT TAG_DOUBLE TAG_LONG that contains the
     * actual value of the tag.
     */
    String value;

    /**
     * Tag Byte tells us about what tag it is.
     * It can be one of the following.
     * TAG_UTF8 corresponds to CONSTANT_UTF8
     * TAG_INTEGER corresponds to CONSTANT_INTEGER
     * TAG_FLOAT corresponds to CONSTANT_FLOAT
     * TAG_LONG corresponds to CONSTANT_LONG
     * TAG_DOUBLE corresponds to CONSTANT_DOUBLE
     * TAG_CLASS corresponds to CONSTANT_CLASS
     * TAG_STRING corresponds to CONSTANT_STRING
     * TAG_FIELDREF corresponds to CONSTANT_FIELDREF
     * TAG_METHODREF corresponds to CONSTANT_METHODREF
     * TAG_INTERFACEREF corresponds to CONSTANT_INTERFACEREF
     * TAG_NAMETYPE  corresponds to CONSTANT_NAMETYPE
     */
    int tagByte;

    /**
     * Constructor
     *
     * @param tagByte Tag Byte
     * @param value   Value
     * @param ptr1    Pointer 1
     * @param ptr2    Pointer 2
     */
    public JConstantPoolEntry(int tagByte, String value,
                              int ptr1, int ptr2) {
        this.tagByte = tagByte;
        this.value = value;
        this.ptr1 = ptr1;
        this.ptr2 = ptr2;
    }

    /**
     * @return Returns Pointer 1.
     */
    public int getPtr1() {
        return ptr1;
    }

    /**
     * @return Returns Pointer 2.
     */
    public int getPtr2() {
        return ptr2;
    }

    /**
     * @return Returns value of this ConstantPoolEntry.
     */
    public String getValue() {
        return value;
    }

    /**
     * @return Returns Tag Byte
     */
    public int getTagByte() {
        return tagByte;
    }

    /**
     * @return Returns stringified form of this class.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(JConstantPool.getTagName(tagByte));
        sb.append(",");
        if (ptr1 == -1) {
            sb.append(" - ");
        } else {
            sb.append(ptr1);
        }
        sb.append(",");
        if (ptr2 == -1) {
            sb.append(" - ");
        } else {
            sb.append(ptr2);
        }
        sb.append(",");
        if (tagByte == JConstantPool.TAG_UTF8) {
            sb.append(value);
        } else {
            sb.append(" - ");
        }
        return sb.toString();
    }
}