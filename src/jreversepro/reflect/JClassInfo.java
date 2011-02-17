/*
 * @(#)JClassInfo.java
 *
 * JReversePro - Java Decompiler / Disassembler.
 * Copyright (C) 2000 Karthik Kumar.
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


import jreversepro.common.AppConstants;
import jreversepro.common.Helper;
import jreversepro.common.KeyWords;
import jreversepro.parser.ClassParserException;
import jreversepro.revengine.JDecompiler;
import jreversepro.revengine.JDisAssembler;
import jreversepro.revengine.JReverseEngineer;
import jreversepro.revengine.RevEngineException;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>JClassInfo</b> is the abstract representation of the Class File.
 * The names of the methods are self explanatory.
 *
 * @author Karthik Kumar
 */
public class JClassInfo implements KeyWords {

    /**
     * ACC_SUPER bit required to be set on all
     * modern classes.
     */
    public static final int ACC_SUPER = 0x0020;

    /**
     * ACC_INTERFACE bit required to be set if it is an
     * interface and not a class.
     */
    public static final int ACC_INTERFACE = 0x0200;

    //Generic Info about a class File.
    /**
     * Absolute path where the class' source file was located.
     */
    private String absPath;

    /**
     * Major number of the JVM version that this class file
     * is compiled for.
     */
    private short majorNumber;

    /**
     * Minor number of the JVM version that this class file
     * is compiled for.
     */
    private short minorNumber;


    /**
     * Name of the current class in the JVM format.
     * That is, if the class is String then the name would be
     * java/lang/String.
     */
    private String thisClass;


    /**
     * Name of the current class' superclass in the JVM format.
     * That is, if the class is String then the name would be
     * java/lang/String.
     */
    private String superClass;

    /**
     * Name of the package of the current class in the JVM format.
     * That is the fully qualified name of the class is
     * java.lang.String. then the package name would contain
     * java/lang.
     */
    private String packageName;

    /**
     * Name of the source file in which this class files' code
     * is present.
     */
    private String srcFile;

    /**
     * ConstantPool information contained in the class.
     */
    private JConstantPool cpInfo;

    /**
     * TRUE if the class was decompiled.
     * False if disasembled.
     */
    private boolean decompiled;

    /**
     * List of fields present in the class.
     * All the members in the list are JField.
     */
    private List memFields;

    /**
     * List of methods present in the class.
     * All the members in the list are JMethod.
     */
    private List memMethods;

    /**
     * List of interfaces present in the class.
     * All the members in the list are String.
     * For example if the class implements
     * java.awt.event.ActionListener then the list would
     * contain java/awt/event/ActionListener as its member.
     * The class file name would be in the JVM format as mentioned
     * above.
     */
    private List interfaces;

    /**
     * An integer referring to the access permission of the
     * class.
     * Like if a class is public static void main ()
     * then the accessflag would have appropriate bits
     * set to say if it public static.
     */
    private int accessFlag;

    /**
     * Empty constructor
     */
    public JClassInfo() {
        memFields = new ArrayList();
        memMethods = new ArrayList();
        interfaces = new ArrayList();
        cpInfo = new JConstantPool(2);

        decompiled = false;
    }

    /**
     * Adds a new interface that is implemented by this class.
     *
     * @param interfaceName Name of the interface.
     */
    public void addInterface(String interfaceName) {
        interfaces.add(interfaceName);
    }

    /**
     * Adds a new field present in the class.
     *
     * @param rhsField contains the field-related information.
     */
    public void addField(JField rhsField) {
        memFields.add(rhsField);
    }

    /**
     * Adds a new method present in the class.
     *
     * @param rhsMethod contains the method-related information.
     */
    public void addMethod(JMethod rhsMethod) {
        memMethods.add(rhsMethod);
    }

    /**
     * Sets the pathname of this class.
     *
     * @param classPath Path to this class.
     */
    public void setPathName(String classPath) {
        absPath = classPath;
    }

    /**
     * Sets the ConstantPool information of this class.
     *
     * @param cpInfo contains the constant pool information
     *               of this class.
     */
    public void setConstantPool(JConstantPool cpInfo) {
        this.cpInfo = cpInfo;
    }

    /**
     * Returns the constantpool reference
     *
     * @return Returns the ConstantPool reference.
     */
    public JConstantPool getConstantPool() {
        return this.cpInfo;
    }

    /**
     * Sets the major and minor number of the JVM
     * for which this class file is compiled for.
     *
     * @param rhsMajor Major number
     * @param rhsMinor Minor number
     */
    public void setMajorMinor(short rhsMajor, short rhsMinor) {
        majorNumber = rhsMajor;
        minorNumber = rhsMinor;
    }

    /**
     * Sets the access flag of the class.
     *
     * @param rhsAccess Access flag of the class.
     */
    public void setAccess(int rhsAccess) {
        accessFlag = rhsAccess;
    }

    /**
     * Sets the name of the current class.
     *
     * @param rhsName Name of this class.
     */
    public void setThisClass(String rhsName) {
        thisClass = rhsName;
    }

    /**
     * Sets the name of the current class' superclass.
     *
     * @param rhsName Name of this class; superclass.
     */
    public void setSuperClass(String rhsName) {
        superClass = rhsName;
    }

    /**
     * Sets the package to which this class belongs to.
     *
     * @param packageName name of the package to be set.
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Sets the name of the source file to which this
     * was contained in.
     *
     * @param rhsSrcFile Name of the source file.
     */
    public void setSourceFile(String rhsSrcFile) {
        srcFile = rhsSrcFile;
    }


    /**
     * Returns the path name of this class.
     *
     * @return Absolute path of this class.
     */
    public String getPathName() {
        return absPath;
    }

    /**
     * Returns the major number of the JVM.
     *
     * @return JVM
     */
    public int getMajor() {
        return majorNumber;
    }

    /**
     * Returns the minor number of the JVM.
     *
     * @return JVM minor version
     */
    public int getMinor() {
        return minorNumber;
    }

    /**
     * @param fullyQualified Parameter to indicate if to return
     *                       the fully qualified name.
     *                       Yes - Fully qualified name along with the package name.
     *                       No - Just the class name only.
     * @return Returns Thisclass name only.
     */
    public String getThisClass(boolean fullyQualified) {
        if (fullyQualified) {
            return thisClass;
        } else {
            int lastIndex = thisClass.lastIndexOf('/');
            if (lastIndex != -1) {
                return thisClass.substring(lastIndex + 1);
            } else {
                return thisClass;
            }
        }
    }

    /**
     * Returns the class name of this class.
     *
     * @return name of the current class.
     */
    public String getThisClass() {
        return thisClass;
    }

    /**
     * Returns the class name of this class' super class.
     *
     * @return name of the current class' super-class.
     */
    public String getSuperClass() {
        return superClass;
    }

    /**
     * Returns the source file of the current class.
     *
     * @return source file of the current class.
     */
    public String getSourceFile() {
        return srcFile;
    }

    /**
     * Returns the List of interfaces of the current class.
     *
     * @return interfaces of the current class.
     */
    public List getInterfaces() {
        return interfaces;
    }

    /**
     * Returns the fields present in the class.
     *
     * @return Returns a List of JField
     */
    public List getFields() {
        return memFields;
    }

    /**
     * Returns the methods of this class.
     *
     * @return Returns a list of JMethods
     */
    public List getMethods() {
        return memMethods;
    }

    /**
     * Returns the access string of this class.
     *
     * @return Returns the access string of this class.
     */
    public String getAccessString() {
        StringBuffer accString = new StringBuffer();
        accString.append(JMember.getStringRep(accessFlag, false));

        if (isClass()) {
            accString.append(CLASS);
        } else {
            accString.append(INTERFACE);
        }
        return accString.toString();
    }

    /**
     * Returns if this is a class or an interface
     *
     * @return Returns true if this is a class,
     *         false, if this is an interface.
     */
    public boolean isClass() {
        return ((accessFlag & ACC_INTERFACE) == 0);
    }

    /**
     * Process the methods.
     *
     * @param getBytecode TRUE - disassemble.
     *                    FALSE - disassemble.
     */
    public void processMethods(boolean getBytecode) {

        for (int i = 0; i < this.getMethods().size(); i++) {

            JMethod method = (JMethod) this.getMethods().get(i);
            JReverseEngineer jre;

            jre = getBytecode
                    ?
                    (JReverseEngineer)
                            new JDisAssembler(method,
                                    this.getConstantPool())
                    :
                    (JReverseEngineer)
                            new JDecompiler(
                                    method,
                                    this.getConstantPool());
            try {
                jre.genCode();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Returns the stringified disassembled/decompiled class.
     *
     * @param getBytecode If TRUE, returns the disassembled code
     *                    IF the class has already been disassembled. If FALSE,
     *                    returns the decompiled code IF the class has been
     *                    decompiled. Otherwise, returns null;
     * @return Stringified class
     */
    public String getStringifiedClass(boolean getBytecode) {
        return getStringifiedClass(getBytecode, false);
    }

    /**
     * Returns the stringified disassembled/decompiled class, optionally with
     * metadata.
     *
     * @param getBytecode     If TRUE, returns the disassembled code
     *                        IF the class has already been disassembled. If FALSE,
     *                        returns the decompiled code IF the class has been
     *                        decompiled. Otherwise, returns null;
     * @param includeMetadata - TRUE if method stack & exception data should be
     *                        output.
     * @return Stringified class
     */
    public String getStringifiedClass(boolean getBytecode,
                                      boolean includeMetadata) {

        StringBuffer sb = new StringBuffer();

        sb.append(getHeaders());
        sb.append(getPackageImports());
        sb.append(getThisSuperClasses());

        sb.append(getStringifiedInterfaces() + "{");
        sb.append(getStringifiedFields());
        sb.append(getStringifiedMethods(getBytecode, includeMetadata));

        sb.append("\n}");
        return sb.toString();
    }

    /**
     * Returns the stringified disassembled/decompiled method.
     *
     * @param getBytecode     If TRUE, returns the disassembled code
     *                        IF the method has already been disassembled. If FALSE,
     *                        returns the decompiled code IF the method has been
     *                        decompiled. Otherwise, returns null;
     * @param includeMetadata - TRUE if method stack & exception data should be
     *                        output
     * @return Stringified methods in this class
     */
    public String getStringifiedMethods(boolean getBytecode,
                                        boolean includeMetadata) {

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < this.getMethods().size(); i++) {
            JMethod method = (JMethod) this.getMethods().get(i);
            sb.append(method.getMethodAsString(getBytecode, includeMetadata));
        }
        return sb.toString();
    }


    /**
     * @return Returns a StringBuffer containing the headers for the reverse
     *         engineered code.
     */
    private StringBuffer getHeaders() {
        StringBuffer init = new StringBuffer();
        init.append("// Decompiled by JReversePro " + AppConstants.VERSION);
        init.append("\n// Home : http://jrevpro.sourceforge.net ");
        init.append("\n// JVM VERSiON: "
                + majorNumber + "."
                + minorNumber);
        init.append("\n// SOURCEFILE: "
                + srcFile);
        return init;
    }

    /**
     * @return Returns a StringBuffer containing the package and import
     *         information of the .class file.
     */
    private StringBuffer getPackageImports() {
        StringBuffer result = new StringBuffer();
        String packageName = Helper.getPackageName(thisClass);

        if (packageName.length() != 0) {
            result.append("\npackage "
                    + packageName + ";");
        }

        result.append("\n\n"
                + cpInfo.getImportedClasses().getImportClasses(packageName));
        return result;
    }

    /**
     * @return Returns a StringBuffer containing the current class name
     *         and the super class name.
     */
    private StringBuffer getThisSuperClasses() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n\n" + getAccessString() + " ");

        sb.append(cpInfo.getImportedClasses().
                getClassName(
                        thisClass));


        if (!superClass.equals(LANG_OBJECT)) {
            sb.append(" extends ");
            sb.append(
                    cpInfo.getImportedClasses().
                            getClassName(superClass) + "   ");
        }
        return sb;
    }

    /**
     * @return Returns a StringBuffer containing the information
     *         of the interfaces implemented by the class.
     */
    private StringBuffer getStringifiedInterfaces() {
        StringBuffer sb = new StringBuffer();
        if (interfaces.size() != 0) {
            sb.append("\n\t\timplements ");
            for (int i = 0; i < interfaces.size(); i++) {
                if (i != 0) {
                    sb.append(" ,");
                }
                sb.append(
                        cpInfo.getImportedClasses().
                                getClassName(
                                        (String) interfaces.get(i)));
            }
        }
        return sb;
    }

    /**
     * @return Returns a StringBuffer containing the information
     *         of fields present in this class.
     */
    private StringBuffer getStringifiedFields() {
        StringBuffer sb = new StringBuffer("\n");
        for (int i = 0; i < memFields.size(); i++) {
            JField field = (JField) memFields.get(i);
            String datatype =
                    cpInfo.getImportedClasses().
                            getClassName(
                                    Helper.getJavaDataType(
                                            field.getDatatype(), false));

            String access = field.getQualifierName();

            sb.append("\n\t" + access);
            sb.append(datatype);
            sb.append(" " + field.getName());
            String val = field.getValue();
            if (field.isFinal() && val.length() != 0) {
                sb.append(" = " + val);
            }
            sb.append(";");
        }
        return sb;
    }

    /**
     * Reverse Engineer the Class file.
     *
     * @param getBytecode True disassembler, false - decompile.
     * @throws ClassParserException Thrown if class file not in proper format.
     * @throws RevEngineException   Thrown if error occured in reverse
     *                              engineering file.
     */
    public void reverseEngineer(boolean getBytecode)
            throws ClassParserException,
            RevEngineException {
        //Reverse Engineer here
        processMethods(getBytecode);
    }
}
