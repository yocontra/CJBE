/**
 * @(#)AppConstants.java
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
package jreversepro.common;

import java.util.Date;

/**
 * @author Karthik Kumar
 */
public interface AppConstants {
    /**
     * Version of the software.
     */
    String VERSION = "1.4.1";

    /**
     * Name of property file.
     */
    String PROP_FILE = "jrev.ini";


    /**
     * Heading of property file.
     */
    String PROP_HEADING = "JReversePro - Java Decompiler / Disassembler";

    /**
     * DecompileFlag Property.
     */
    String DECOMPILE_FLAG = "Decompile";

    /**
     * XPosition of GUI window.
     */
    String XPOS = "XPos";

    /**
     * YPosition of GUI window.
     */
    String YPOS = "YPos";

    /**
     * Width of GUI window.
     */
    String XSIZE = "Width";

    /**
     * Height of GUI window.
     */
    String YSIZE = "Height";

    /**
     * Look And Feel of Window.
     */
    String L_AND_F = "LookAndFeel";

    /**
     * Font of GUI window.
     */
    String FONT = "Font";

    /**
     * Title of GUI window.
     */
    String TITLE = "JReversePro - Java Decompiler / Disassembler";

    /**
     * GPL Information.
     */
    String GPL_INFO = "// JReversePro v " + VERSION
            + " " + (new Date(System.currentTimeMillis()))
            + "\n// http://jrevpro.sourceforge.net"
            + "\n// Copyright (C)2000 2001 2002 Karthik Kumar."
            + "\n// JReversePro comes with ABSOLUTELY NO WARRANTY;"
            + "\n// This is free software, and you are welcome to redistribute"
            + "\n// it under certain conditions;See the File 'COPYING' for "
            + "more details.\n";


    /**
     * MAGIC corresponds to the Magic number appearing in
     * the beginning of class files.
     */
    int MAGIC = 0xCAFEBABE;
}
