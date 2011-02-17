/**
 * @(#)JSerializer.java
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
package jreversepro.revengine;

import jreversepro.common.KeyWords;
import jreversepro.parser.ClassParserException;
import jreversepro.parser.JClassParser;
import jreversepro.reflect.JClassInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Serializes the Classes to a string buffer.
 *
 * @author Karthik Kumar
 */
public class JSerializer
        implements KeyWords {

    /**
     * Name of the current class to be reverse engineered.
     */
    String currentClass;

    /**
     * Reference to a .classfile parser.
     */
    static JClassParser classParser;

    /**
     * infoClass contains the information of the class.
     * field, methods, interfaces, ConstantPool etc.
     */
    JClassInfo infoClass;

    /**
     * static initializer.
     **/
    static {
        classParser = new JClassParser();
    }

    /**
     * @param file File reference pointing to the .class file to be
     *             reverse engineered.
     * @return Reference of type JClassInfo containing information of the
     *         class.
     * @throws IOException          Thrown in case of any i/o error while parsing
     *                              class file.
     * @throws ClassParserException thrown in case of any class file format
     *                              error or not a class file.
     */
    public JClassInfo loadClass(File file)
            throws ClassParserException,
            IOException {
        classParser.parse(file);
        infoClass = classParser.getClassInfo();

        String thisClass = file.getName();
        currentClass = thisClass.substring(0, thisClass.indexOf('.'));
        return infoClass;
    }

    /**
     * @param url URL reference pointing to the .class file to be
     *            reverse engineered.
     * @return Reference of type JClassInfo containing information of the
     *         class.
     * @throws IOException          Thrown in case of any i/o error while parsing
     *                              class file.
     * @throws ClassParserException thrown in case of any class file format
     *                              error or not a class file.
     */
    public JClassInfo loadClass(URL url)
            throws ClassParserException,
            IOException {
        classParser.parse(url);


        infoClass = classParser.getClassInfo();

        // xxx - bad hack.  we need to get the class name from somewhere else
        String thisClass = url.getPath();
        thisClass = thisClass.substring(thisClass.lastIndexOf('/') + 1);
        currentClass = thisClass.substring(0, thisClass.indexOf('.'));

        return infoClass;
    }
}
