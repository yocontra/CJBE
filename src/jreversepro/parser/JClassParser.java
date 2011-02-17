/*
 * @(#)JClassParser.java
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

import jreversepro.common.AppConstants;
import jreversepro.reflect.JClassInfo;
import jreversepro.reflect.JConstantPool;
import jreversepro.reflect.JField;
import jreversepro.reflect.JMethod;

import java.io.*;
import java.net.URL;

//import jreversepro.common.Helper;


/**
 * @author Karthik Kumar
 */
public class JClassParser implements AppConstants {

    /**
     * DataInputStream containing the bytes of the class.
     */
    private DataInputStream mDis;

    /**
     * ConstantPool Information of the class being reverse engineered.
     */
    private JConstantPool mCpInfo;

    /**
     * Information about fields, methods of the class being reverse engineered.
     */
    private JClassInfo mInfoClass;


    /**
     * Parses the given byte array and creates the ClassInfo and
     * ConstantPool objects.
     *
     * @param bytes byte array to be parsed.
     * @throws ClassParserException Thrown if class file not in desired format.
     * @throws IOException          Thrown if error in stream of bytes containing the
     *                              class file.
     */
    public void parse(byte[] bytes)
            throws IOException,
            ClassParserException {

        parse(new ByteArrayInputStream(bytes),
                bytes.length,
                "");
        //Path not given.
    }


    /**
     * Parses a class file at the other side of a URL and
     * creates the ClassInfo and ConstantPool objects.
     *
     * @param url a url pointing to a class file to be parsed.
     * @throws ClassParserException Thrown if class file not in desired format.
     * @throws IOException          Thrown if error in stream of bytes containing the
     *                              class file.
     */
    public void parse(URL url)
            throws IOException,
            ClassParserException {

        parse(url.openConnection().getInputStream(),
                1024, // make up a guess
                url.getPath());
    }


    /**
     * Parses the given file and creates the ClassInfo and ConstantPool objects.
     *
     * @param aFile class file to be parsed.
     * @throws ClassParserException Thrown if class file not in desired format.
     * @throws IOException          Thrown if error in stream of bytes containing the
     *                              class file.
     */
    public void parse(File aFile)
            throws IOException,
            ClassParserException {

        if (aFile.getName().endsWith(".class")) {
            try {
                // Dangerous conversion of long to int, but nevertheless
                //all class file streams happen to come under this limit.
                parse(new FileInputStream(aFile),
                        (int) aFile.length(),
                        aFile.toString());
            } catch (FileNotFoundException eFNFE) {
                throw
                        new ClassParserException(
                                "Class file to reverse engineer "
                                        + aFile.toString() + " not found");
            }
        } else {
            throw new ClassParserException(aFile.toString()
                    + " does not have .class extension ");
        }
    }


    /**
     * Parses the given file and creates the ClassInfo and ConstantPool objects.
     *
     * @param is          InputStream from which bytes are taken.
     * @param length      Length of the bytecode stream.
     * @param pathToClass path to class.
     * @throws ClassParserException Thrown if class file not in desired format.
     * @throws IOException          Thrown if error in stream of bytes containing the
     *                              class file.
     */
    public void parse(InputStream is,
                      int length,
                      String pathToClass)
            throws IOException,
            ClassParserException {

        if (is instanceof ByteArrayInputStream) {
            parse((ByteArrayInputStream) is, pathToClass);
            //perform Explicit Casting - safely.
        } else {
            ByteArrayInputStream bais;
            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream(length);


            byte[] buf = new byte[length]; // length becomes just a guess
            int nread;
            while ((nread = is.read(buf, 0, buf.length)) > 0) {
                baos.write(buf, 0, nread);
            }

            bais = new ByteArrayInputStream(baos.toByteArray());
            parse(bais, pathToClass);
            //Convert to bytestream first since once brought to
            //memory operations will be easy to perform.
        }
    }


    /**
     * Parses the given file and creates the ClassInfo and ConstantPool objects.
     *
     * @param is          InputStream containing the bytes.
     * @param pathToClass path to the class.
     * @throws ClassParserException Thrown if class file not in desired format.
     * @throws IOException          Thrown if error in stream of bytes containing the
     *                              class file.
     */
    public void parse(ByteArrayInputStream is,
                      String pathToClass)
            throws IOException,
            ClassParserException {
        mCpInfo = null;
        mInfoClass = null;
        mInfoClass = new JClassInfo();

        mDis = new DataInputStream(is);
        mInfoClass.setPathName(pathToClass);
        readMagic();
        readVersion();
        readConstantPool();
        readAccess();
        fillThisClass();
        fillSuperClass();
        readInterfaces();
        readFields();
        readMethods();
        readAttributes();
        mDis.close();
        mInfoClass.setConstantPool(mCpInfo);
    }

    /**
     * @return Returns the ConstantPool Information of the class.
     */
    public final JConstantPool getCpInfo() {
        return mCpInfo;
    }


    /**
     * @return Returns the data Information of the class.
     */
    public final JClassInfo getClassInfo() {
        return mInfoClass;
    }


    /**
     * Reads the Magic number.
     *
     * @throws ClassParserException Thrown if class file not in desired format.
     * @throws IOException          Thrown if error in stream of bytes containing the
     *                              class file.
     */
    private void readMagic() throws ClassParserException,
            IOException {
        int magic = mDis.readInt();
        if (magic != MAGIC) {
            throw new ClassParserException("Invalid Magic Number");
        }
    }


    /**
     * Reads the Version of the class file.
     *
     * @throws ClassParserException Thrown if class file not in desired format.
     * @throws IOException          Thrown if error in stream of bytes containing the
     *                              class file.
     */
    private void readVersion() throws ClassParserException,
            IOException {
        short minor = mDis.readShort();
        short major = mDis.readShort();

        if (!supportedMajorMinor(major, minor)) {
            throw new ClassParserException("(Major: " + major
                    + ", Minor: " + minor
                    + ") version not supported");
        }

        mInfoClass.setMajorMinor(major, minor);
    }

    /**
     * @param major Major version of the JVM.
     * @param minor Minor version of the JVM.
     *              test whether or not the supplied major/minor class
     *              versions are acceptable.  (NOTE: we should probably
     *              have a runtime switch to accept all versions since
     *              even though the versions may be incremented, there are
     *              typically no changes to the form of the class files)
     *              <p/>
     *              45.3+ is java 1.1
     *              46.0  is java 1.2
     *              47.0  is java 1.3
     *              48.0  is java 1.4
     * @return true if the major/minor versions are acceptable
     */
    private boolean supportedMajorMinor(short major,
                                        short minor) {
        /*
        if (major == 45) {
            return (minor >= 3);
        }

        if (major >= 46 && major <= 48) {
            return true;
        }

        return false;     */
        //TODO: FIX THIS FOR NEWER JAVA VERSIONS
        return true;
    }


    /**
     * Reads the ConstantPool.
     *
     * @throws ClassParserException Thrown if class file not in desired format.
     * @throws IOException          Thrown if error in stream of bytes containing the
     *                              class file.
     */
    private void readConstantPool() throws IOException,
            ClassParserException {
        int numCpEntry = mDis.readShort();
        mCpInfo = new JConstantPool(numCpEntry);
        readCpEntries(numCpEntry);
    }

    /**
     * Reads the ConstantPool Entries.
     *
     * @param aNumEntry Number of constant pool entries.
     * @throws ClassParserException Thrown if class file not in desired format.
     * @throws IOException          Thrown if error in stream of bytes containing the
     *                              class file.
     */
    private void readCpEntries(int aNumEntry) throws IOException,
            ClassParserException {
        mCpInfo.addNullEntry();

        for (int i = 1; i < aNumEntry; i++) {
            byte tagByte = mDis.readByte();

            switch (tagByte) {
                case JConstantPool.TAG_UTF8:
                    readTagUtf8(i);
                    break;
                case JConstantPool.TAG_INTEGER:
                    readTagInteger(i);
                    break;
                case JConstantPool.TAG_FLOAT:
                    readTagFloat(i);
                    break;
                case JConstantPool.TAG_LONG:
                    readTagLong(i);
                    //Long takes two ConstantPool Entries.
                    i++;
                    break;
                case JConstantPool.TAG_DOUBLE:
                    readTagDouble(i);
                    //Double takes two ConstantPool Entries.
                    i++;
                    break;
                case JConstantPool.TAG_CLASS:
                    readTagClass(i);
                    break;
                case JConstantPool.TAG_STRING:
                    readTagString(i);
                    break;
                case JConstantPool.TAG_FIELDREF:
                    readTagFieldRef(i);
                    break;
                case JConstantPool.TAG_METHODREF:
                    readTagMethodRef(i);
                    break;
                case JConstantPool.TAG_INTERFACEREF:
                    readTagInterfaceRef(i);
                    break;
                case JConstantPool.TAG_NAMETYPE:
                    readTagNameType(i);
                    break;
                default:
                    throw new ClassParserException(
                            "TagByte " + tagByte
                                    + " Invalid for ConstantPool Entry #" + i);
            }
        }
    }

    /**
     * Reads an UTF8 entry.
     *
     * @param aIndex Index of a ConstantPool Entry.
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void readTagUtf8(int aIndex) throws IOException {
        String utfString = mDis.readUTF();
        mCpInfo.addUtf8Entry(utfString);
    }

    /**
     * Reads an integer entry.
     *
     * @param aIndex Index of a ConstantPool Entry.
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void readTagInteger(int aIndex) throws IOException {
        int intValue = mDis.readInt();

        mCpInfo.addIntegerEntry(String.valueOf(intValue));
    }

    /**
     * Reads an float entry.
     *
     * @param aIndex Index of a ConstantPool Entry.
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void readTagFloat(int aIndex) throws IOException {
        float floatValue = mDis.readFloat();

        mCpInfo.addFloatEntry(String.valueOf(floatValue) + "f");
    }

    /**
     * Reads a long entry.
     *
     * @param aIndex Index of a ConstantPool Entry.
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void readTagLong(int aIndex) throws IOException {

        long longValue = mDis.readLong();

        mCpInfo.addLongEntry(String.valueOf(longValue) + "L");
        mCpInfo.addNullEntry();
    }

    /**
     * Reads a double entry.
     *
     * @param aIndex Index of a ConstantPool Entry.
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void readTagDouble(int aIndex) throws IOException {

        double doubleValue = mDis.readDouble();

        mCpInfo.addDoubleEntry(String.valueOf(doubleValue));
        mCpInfo.addNullEntry();
    }

    /**
     * Reads an TAG_CLASS entry.
     * <p>u1 tag; <br>
     * u2 name_index;<br>
     * </p>
     * Reads a class entry.
     *
     * @param aIndex Index of a ConstantPool Entry.
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void readTagClass(int aIndex) throws IOException {
        int classIndex = mDis.readShort();

        mCpInfo.addClassEntry(classIndex);
    }


    /**
     * Reads a string entry.
     *
     * @param aIndex Index of a ConstantPool Entry.
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void readTagString(int aIndex) throws IOException {
        int stringIndex = mDis.readShort();
        mCpInfo.addStringEntry(stringIndex);
    }

    /**
     * Reads a TAG_FIELDREF entry.
     * <p>
     * u1 tag; <br>
     * u2 class_index; <br>
     * u2 name_and_type_index; <br></p>
     *
     * @param aIndex Index of a ConstantPool Entry.
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void readTagFieldRef(int aIndex) throws IOException {
        int classIndex = mDis.readShort();
        int nameType = mDis.readShort();

        mCpInfo.addFieldRefEntry(classIndex, nameType);
    }

    /**
     * Reads a TAG_METHODREF entry.
     * <p>
     * u1 tag; <br>
     * u2 class_index; <br>
     * u2 name_and_type_index; <br></p>
     *
     * @param aIndex Index of a ConstantPool Entry.
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void readTagMethodRef(int aIndex) throws IOException {
        int classIndex = mDis.readShort();
        int nameType = mDis.readShort();

        mCpInfo.addMethodRefEntry(classIndex, nameType);
    }

    /**
     * Reads a TAG_INTERFACEREF entry.
     * <p>
     * u1 tag;<br>
     * u2 class_index;<br>
     * u2 name_and_type_index;<br></p>
     *
     * @param aIndex Index of a ConstantPool Entry.
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void readTagInterfaceRef(int aIndex) throws IOException {
        int classIndex = mDis.readShort();
        int nameType = mDis.readShort();

        mCpInfo.addInterfaceRefEntry(classIndex, nameType);
    }

    /**
     * Reads a TAG_NAMETYPE entry.
     * <p>u1 tag; <br>
     * u2 name_index;<br>
     * u2 descriptor_index;<br></p>
     *
     * @param aIndex Index of a ConstantPool Entry.
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void readTagNameType(int aIndex) throws IOException {
        int nameIndex = mDis.readShort();
        int descIndex = mDis.readShort();

        mCpInfo.addNameTypeEntry(nameIndex, descIndex);
    }

    /**
     * Reads the access specifier of the class.
     *
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void readAccess() throws IOException {
        mInfoClass.setAccess(mDis.readShort());
    }


    /**
     * Reads the fully qualified name of the current class.
     * <p> For Example , a class by name <code>JClassParser</code>
     * in the package <code>Heart</code> would be read as:
     * <code>Heart/JClassParser</code></p>.
     *
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void fillThisClass() throws IOException {
        mInfoClass.setThisClass(
                mCpInfo.getClassName(mDis.readShort()));
    }

    /**
     * Reads the fully qualified name of the super class.
     * <p> For Example , a class by name <code>JClassParser</code>
     * in the package <code>Heart</code> would be read as:
     * <code>Heart/JClassParser</code></p>.
     *
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     */
    private void fillSuperClass() throws IOException {
        mInfoClass.setSuperClass(
                mCpInfo.getClassName(mDis.readShort()));
    }

    /**
     * Reads the fully qualified name of the interfaces
     * <code>implemented</code> by the Current class.
     *
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     * @see JClassInfo#addInterface(String)
     */
    private void readInterfaces() throws IOException {
        short count = mDis.readShort();
        for (int i = 0; i < count; i++) {
            String interfaceadd = mCpInfo.getClassName(
                    mDis.readShort());

            mInfoClass.addInterface(interfaceadd);
        }
    }

    /**
     * Reads the fields <code>defined</code> in the Current class.
     *
     * @throws IOException          Thrown if error in stream of bytes containing the
     *                              class file.
     * @throws ClassParserException Thrown in case of an invalid
     *                              constantpool reference.
     * @see JField
     */
    private void readFields() throws IOException,
            ClassParserException {
        short count = mDis.readShort();

        for (int i = 0; i < count; i++) {
            JField curField = new JField();

            short accessFlags = mDis.readShort();
            short nameIndex = mDis.readShort();
            short descIndex = mDis.readShort();

            String name = mCpInfo.getUtf8String(nameIndex);
            String descriptor = mCpInfo.getUtf8String(descIndex);

            curField.setName(name);
            curField.setDatatype(descriptor);
            curField.setQualifier(accessFlags);

            short attrCount = mDis.readShort();
            for (int j = 0; j < attrCount; j++) {
                readFieldAttributes(curField);
            }
            mInfoClass.addField(curField);
        }
    }

    /**
     * Reads the ATTRIBUTES of the field defined in the Current class.
     * <p> The possible attributes here are
     * ConstantValue , Deprecated , Synthetic. </p>.
     *
     * @param aRhsField Reads attributes into this field.
     * @throws IOException          Thrown if error in stream of bytes containing the
     *                              class file.
     * @throws ClassParserException Thrown in case of an invalid
     *                              constantpool reference.
     * @see JAttribute
     */
    private void readFieldAttributes(JField aRhsField)
            throws IOException,
            ClassParserException {

        String attrName = mCpInfo.getUtf8String(
                mDis.readShort());

        if (attrName.compareTo(JAttribute.CONSTANT_VALUE) == 0) {
            aRhsField.setValue
                    (JAttribute.manipConstantValue(mDis, mCpInfo));
        } else if (attrName.compareTo(JAttribute.SYNTHETIC) == 0) {
            JAttribute.manipSynthetic(mDis);
        } else if (attrName.compareTo(JAttribute.DEPRECATED) == 0) {
            JAttribute.manipDeprecated(mDis);
        }
    }

    /**
     * Reads the methods <code>defined</code> in the Current class.
     *
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     * @see JMethod
     */
    private void readMethods() throws IOException {
        short count = mDis.readShort();

        for (int i = 0; i < count; i++) {
            JMethod curMethod = new JMethod(mInfoClass);

            short accessFlags = mDis.readShort();
            short nameIndex = mDis.readShort();
            short descIndex = mDis.readShort();
            String name = mCpInfo.getUtf8String(nameIndex);
            String descriptor = mCpInfo.getUtf8String(descIndex);

            curMethod.setName(name);
            curMethod.setSignature(descriptor);
            curMethod.setQualifier(accessFlags);

            short attrCount = mDis.readShort();
            for (int j = 0; j < attrCount; j++) {
                readMethodAttributes(curMethod);
            }
            mInfoClass.addMethod(curMethod);
        }
    }


    /**
     * Reads the ATTRIBUTES of the method defined in the Current class.
     * <p> The possible attributes here are
     * Deprecated , Synthetic , <b> Code </b> and Exceptions </p>.
     *
     * @param aRhsMethod Reads attributes into this method.
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     * @see JAttribute
     */
    private void readMethodAttributes(JMethod aRhsMethod)
            throws IOException {
        String attrName =
                mCpInfo.getUtf8String(mDis.readShort());

        if (attrName.compareTo(JAttribute.CODE) == 0) {
            JAttribute.manipCode(mDis, aRhsMethod, mCpInfo);
        } else if (attrName.compareTo(JAttribute.EXCEPTIONS) == 0) {
            // counterpart to 'throws' clause in the source code.
            aRhsMethod.setThrowsClasses(
                    JAttribute.manipExceptions(mDis, mCpInfo));
        } else if (attrName.compareTo(JAttribute.SYNTHETIC) == 0) {
            JAttribute.manipSynthetic(mDis);
        } else if (attrName.compareTo(JAttribute.DEPRECATED) == 0) {
            JAttribute.manipDeprecated(mDis);
        }
    }

    /**
     * Reads the ATTRIBUTES of the fields and methods.
     * <p> The possible attributes here are
     * Code, LineNumberTable, Exception </p>.
     *
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     * @see JAttribute
     */
    private void readAttributes() throws IOException {
        short attrCount = mDis.readShort();

        for (int i = 0; i < attrCount; i++) {
            readClassAttributes();
        }
    }

    /**
     * Reads the ATTRIBUTES of the whole class itself.
     * <p> The possible attributes here are
     * Deprecated , SourceFile</p>.
     *
     * @throws IOException Thrown if error in stream of bytes containing the
     *                     class file.
     * @see JAttribute
     */
    private void readClassAttributes() throws IOException {
        int attrIndex = mDis.readShort();

        String attrName = mCpInfo.getUtf8String(attrIndex);

        if (attrName.compareTo(JAttribute.SOURCEFILE) == 0) {
            mInfoClass.setSourceFile(
                    JAttribute.manipSourceFile(mDis, mCpInfo));
        } else if (attrName.compareTo(JAttribute.DEPRECATED) == 0) {
            JAttribute.manipDeprecated(mDis);
        } else {
//          System.out.println("Attribute "  + AttrName );
        }
    }
}
