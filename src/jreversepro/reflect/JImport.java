/*
 * @(#)JImport.java
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Describes the Set fof import statements.
 *
 * @author Karthik Kumar.
 */
public class JImport {

    /**
     * List of classes that are referenced by this class.
     * The elements of this list are 'String'.
     */
    private List classes;

    /**
     * no-arg constructor
     */
    public JImport() {
        this.classes = new ArrayList(5);
    }

    /**
     * Adds a new class to the list of classes
     * referenced by the current class.
     *
     * @param importClass name of new class.
     */
    public void addClass(String importClass) {
        if (!classes.contains(importClass)) {
            classes.add(importClass);
        }
    }


    /**
     * Returns the Class name alone from a fully qualified name.
     * <p/>
     * For Example , if <code>FullName = java/lang/StringBuffer,</code>
     * <br>then a call to <code>getClassName(arg)</code> returns the
     * value <code>StringBuffer </code>.
     * <p/>
     *
     * @param fullQualifiedName A Fully Qualified Name.
     * @return the class name , alone.
     */
    public static String getClassName(String fullQualifiedName) {
        String aFullName = fullQualifiedName.replace('/', '.');
        int dotIndex = aFullName.lastIndexOf(".");
        if (dotIndex != -1) {
            return aFullName.substring(dotIndex + 1);
        } else {
            return aFullName;
        }
    }

    /**
     * Returns a string that contains all the imported classes
     * in the proper format as written in code.
     * For eg, if the list contains p1.class1 , p2.class2 , this
     * generates a string with import statements for both of them.
     * classes belonging to the default package are excluded.
     * Also there is an option by which we can exclude the classes
     * that belong to a given package ( current package ).
     *
     * @param packageName current packagename and name for which
     *                    package name is to be excluded.
     * @return String containing the code mentioned.
     */
    public String getImportClasses(String packageName) {
        //Code to be written here.
        Collections.sort(classes);
        StringBuffer sb = new StringBuffer();
        List restrictPackages = new ArrayList(2);
        restrictPackages.add(packageName);
        restrictPackages.add(KeyWords.DEFAULT_PACKAGE);

        for (int i = 0; i < classes.size(); i++) {
            String currentClass = (String) classes.get(i);
            if (currentClass.indexOf('/') != -1) {
                String currentPackage = Helper.getPackageName(currentClass);
                if (!restrictPackages.contains(currentPackage)) {
                    currentClass = currentClass.replace('/', '.');
                    sb.append("import ");
                    sb.append(currentClass + ";\n");
                }
            }
        }
        return sb.toString();
    }
}