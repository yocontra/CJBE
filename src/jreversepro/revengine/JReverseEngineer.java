/*
 * @(#)JReverseEngineer.java
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
package jreversepro.revengine;

import jreversepro.parser.ClassParserException;

import java.io.IOException;

/**
 * Common interface to decompiler and disassembler
 *
 * @author Karthik Kumar
 */
public interface JReverseEngineer {

    /**
     * Message that appears when decompiling fails.
     */
    static final String DECOMPILE_FAILED_MSG =
            "\n\t\t/**\nDecompilation failed for the method. "
                    + "Thanks for reporting the bug to me at "
                    + "akkumar@users.sourceforge.net by attaching this "
                    + "class file .\n **/";

    /**
     * Generates the code for disassembler and decompiler.
     *
     * @throws IOException          Thrown in case of any i/o error.
     * @throws RevEngineException   Thrown in case of any code error in the
     *                              decompiling engine.
     * @throws ClassParserException Thrown in case of an invalid
     *                              constantpool reference.
     */
    void genCode() throws RevEngineException, IOException,
            ClassParserException;
}
