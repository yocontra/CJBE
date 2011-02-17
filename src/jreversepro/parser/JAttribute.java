/**
 * @(#)JAttribute.java
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
package jreversepro.parser;

import jreversepro.reflect.JConstantPool;
import jreversepro.reflect.JMethod;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>JAttribute</b> has the ability to read the 'ATTRIBUTES' of the
 * Field , Method and the Class as a whole.
 *
 * @author Karthik Kumar
 * @version 1.00,
 */
public final class JAttribute {

    /**
     * ConstantValue attribute of a Method.
     */
    public static final String CONSTANT_VALUE = "ConstantValue";

    /**
     * Deprecated attribute of a Method.
     */
    public static final String DEPRECATED = "Deprecated";

    /**
     * Synthetic attribute of a Method.
     */
    public static final String SYNTHETIC = "Synthetic";

    /**
     * Code attribute of a Method.
     */
    public static final String CODE = "Code";

    /**
     * Exceptions attribute of a Method.
     */
    public static final String EXCEPTIONS = "Exceptions";

    /**
     * LineNumberTable attribute of a Method.
     */
    public static final String LINENUMBERTABLE = "LineNumberTable";

    /**
     * LocalVariableTable attribute of a Method.
     */
    public static final String LOCALVARIABLETABLE = "LocalVariableTable";

    /**
     * SourceFile attribute of a Method.
     */
    public static final String SOURCEFILE = "SourceFile";

    /**
     * Manipulates the 'ConstantValue' attribute of the Fields.
     * <p>
     * <b>ConstantValue</b> attribute <br>
     * u2 attribute_name_index; <br>
     * u4 attribute_length;<br>
     * u2 constantvalue_index;<br>
     * </p>
     *
     * @param aDis    DataInputStream containing the bytes of the class.
     * @param aCpInfo ConstantPool Information.
     * @return a String containing the Constant value, of the field.
     * @throws IOException          Error in Class Stream of bytes.
     * @throws ClassParserException Thrown in case of any wrong
     *                              constantpool reference.
     */
    public static String manipConstantValue(
            DataInputStream aDis,
            JConstantPool aCpInfo) throws IOException,
            ClassParserException {

        int len = aDis.readInt();
        short index = aDis.readShort();
        return (aCpInfo.getBasicDataTypeValue(index));
    }

    /**
     * Manipulates the 'Deprecated' attribute of the Fields.
     * <p>
     * <b>Deprecated</b> attribute <br>
     * u2 attribute_name_index; <br>
     * u4 attribute_length;<br>
     * </p>
     *
     * @param aDis DataInputStream containing the bytes of the class.
     * @throws IOException Error in Class Stream of bytes.
     */
    public static void manipDeprecated(DataInputStream aDis)
            throws IOException {
        int len = aDis.readInt();//len must be zero.
    }

    /**
     * Manipulates the 'Synthetic' attribute of the Fields.
     * <p>
     * <b>Synthetic</b> attribute <br>
     * u2 attribute_name_index; <br>
     * u4 attribute_length;<br>
     * </p>
     *
     * @param aDis DataInputStream containing the bytes of the class.
     * @throws IOException Error in Class Stream of bytes.
     */
    public static void manipSynthetic(DataInputStream aDis)
            throws IOException {
        int len = aDis.readInt();//len must be zero.
    }


    /**
     * Manipulates the 'Code' attribute of the Methods.
     * <p>
     * <b>Code</b> attribute <br>
     * u2 attribute_name_index;<br>
     * u4 attribute_length;<br>
     * u2 max_stack; <br>
     * u2 max_locals; <br>
     * u4 code_length; <br>
     * u1 code[code_length];<br>
     * u2 exception_table_length;<br>
     * { <br>
     * u2 start_pc; u2 end_pc; u2 handler_pc; u2 catch_type;<br>
     * } exception_table[exception_table_length]; <br>
     * u2 attributes_count;<br>
     * attribute_info attributes[attributes_count]; <br>
     * </p>
     *
     * @param aDis         DataInputStream containing the bytes of the class.
     * @param aCpInfo      ConstantPool Information.
     * @param aLocalMethod Reference to the current method for which the code
     *                     is to be manipulated.
     * @throws IOException Error in Class Stream of bytes.
     */
    public static void manipCode(DataInputStream aDis,
                                 JMethod aLocalMethod,
                                 JConstantPool aCpInfo)
            throws IOException {

        int len = aDis.readInt();
        //Attribute length

        short maxStack = aDis.readShort();
        aLocalMethod.setMaxStack(maxStack);

        short maxLocals = aDis.readShort();
        aLocalMethod.setMaxLocals(maxLocals);

        int codeLen = aDis.readInt();

        byte[] btArray = new byte[codeLen];
        aDis.readFully(btArray);
        aLocalMethod.setBytes(btArray);

        short excLen = aDis.readShort();

        for (int i = 0; i < excLen; i++) {
            short startPc = aDis.readShort();
            short endPc = aDis.readShort();
            short handlerPc = aDis.readShort();
            short catchType = aDis.readShort();

            //If type of class caught is any,  then CatchType is 0.
            aLocalMethod.addExceptionBlock(
                    startPc, endPc, handlerPc,
                    aCpInfo.getClassName(catchType));
        }

        short attrCount = aDis.readShort();

        for (int i = 0; i < attrCount; i++) {
            readCodeAttributes(aDis, aCpInfo);
        }
    }


    /**
     * Manipulates the 'SourceFile' attribute of the Fields.
     * <p>
     * <b> SourceFile </b> attribute <br>
     * u2 attribute_name_index; <br>
     * u4 attribute_length;<br>
     * u2 sourcefile_index;
     * </p>
     *
     * @param aDis    DataInputStream containing the bytes of the class.
     * @param aCpInfo ConstantPool Information.
     * @return NO_STRING
     * @throws IOException Error in Class Stream of bytes.
     */
    public static String manipSourceFile(DataInputStream aDis,
                                         JConstantPool aCpInfo)

            throws IOException {

        aDis.readInt();
        short srcIndex = aDis.readShort();
        String srcName = aCpInfo.getUtf8String(srcIndex);
        return srcName;
    }

    /**
     * Reads the possible attributes of Code.
     * <p>
     * Possible attributes of Code are
     * <b> LineNumberTable </b> and  <b> LocalVariableTable </b>
     * </p>
     *
     * @param aDis    DataInputStream containing the bytes of the class.
     * @param aCpInfo ConstantPool Information.
     * @throws IOException Error in Class Stream of bytes.
     */
    private static void readCodeAttributes(DataInputStream aDis,
                                           JConstantPool aCpInfo)
            throws IOException {

        short attrNameIndex = aDis.readShort();
        String attrName = aCpInfo.getUtf8String(attrNameIndex);

        if (attrName.equals(LINENUMBERTABLE)) {
            manipLineNumberTable(aDis);
        } else if (attrName.equals(LOCALVARIABLETABLE)) {
            manipLocalVariableTable(aDis);
        }
    }

    /**
     * Manipulates the LineNumberTable attribute.
     * <p>
     * <b> LineNumberTable_attribute  </b> <br>
     * u2 attribute_name_index; <br>
     * u4 attribute_length; <br>
     * u2 line_number_table_length; { <br>
     * u2 start_pc; <br>
     * u2 line_number;<br>
     * } line_number_table[line_number_table_length]; <br>
     * </p>
     *
     * @param aDis DataInputStream containing the bytes of the class.
     * @throws IOException Error in Class Stream of bytes.
     */
    private static void manipLineNumberTable(DataInputStream aDis)
            throws IOException {
        int len = aDis.readInt();
        byte[] btRead = new byte[len];
        //TODONew: Do some manipulation with the LineNumberTable
        // attribute..
        aDis.readFully(btRead);
        btRead = null;
    }

    /**
     * Manipulates the LocalVariableTable attribute.
     * <p>
     * <b> LocalVariableTable_attribute  </b> <br>
     * u2 local_variable_table_length;  <br>
     * { <br> u2 start_pc; <br>
     * u2 length; <br>
     * u2 name_index; <br>
     * u2 descriptor_index; <br>
     * u2 index; <br>
     * } local_variable_table[local_variable_table_length]; <br>
     * u2 attribute_name_index; <br>
     * u4 attribute_length; <br>
     * u2 line_number_table_length; { <br>
     * u2 start_pc; <br>
     * u2 line_number;<br>
     * } line_number_table[line_number_table_length]; <br>
     * </p>
     *
     * @param aDis DataInputStream containing the bytes of the class.
     * @throws IOException Error in Class Stream of bytes.
     */
    private static void manipLocalVariableTable(DataInputStream aDis)
            throws IOException {
        int len = aDis.readInt();
        byte[] btRead = new byte[len];
        aDis.readFully(btRead);
        btRead = null;
    }

    /**
     * Manipulates the Exceptions attribute.
     * <p>
     * <b> Exceptions_attribute  </b> <br>
     * u2 attribute_name_index;<br>
     * u4 attribute_length;<br>
     * u2 number_of_exceptions;<br>
     * u2 exception_index_table[number_of_exceptions]; <br>
     * <p/>
     * Present , if there is a throws clause in the declaration of
     * the method.<br></p>
     *
     * @param aDis    DataInputStream containing the bytes of the class.
     * @param aCpInfo ConstantPool Information.
     * @return Set of classes present in the 'throws' clause statement.
     * @throws IOException Error in Class Stream of bytes.
     */
    public static List manipExceptions(DataInputStream aDis,
                                       JConstantPool aCpInfo) throws IOException {
        //Responsible for the 'throws' clause
        List classes = new ArrayList(2);
        aDis.readInt();
        short numException = aDis.readShort();
        for (int i = 0; i < numException; i++) {
            short classIndex = aDis.readShort();
            classes.add(aCpInfo.getClassName(classIndex));
        }
        return classes;
    }
}
