/*
 * JLineOfCode.java
 *
 * Created on August 14, 2002, 1:14 PM
 * JReversePro - Java Decompiler / Disassembler.
 * Copyright (C) 2002 pazandak@objs.com
 * EMail: pazandak@objs.com
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

import jreversepro.reflect.method.JBlockObject;

/**
 * Represents a single line of code.
 *
 * @author pazandak@objs.com - Copyright 2002.
 */
public class JLineOfCode {

    /**
     * Entry of block *
     */
    public static final int ENTRY = 1;

    /**
     * Inside a block *
     */
    public static final int INBLOCK = 0;

    /**
     * Exiting out of a block *
     */
    public static final int EXIT = 2;

    /**
     * TRUE if line is not directly associated with any bytecode.
     * E.g. "try" or catch", or "{" or "}",  etc.
     */
    private boolean decoration;

    /**
     * Bytecode addr start *
     */
    private int start;

    /**
     * Bytecode addr end *
     */
    private int end;

    /**
     * From Index *
     */
    private int fromIndex;

    /**
     * End of Index *
     */
    private int toIndex;

    /**
     * Statement *
     */
    private String stmt;

    /**
     * ??? **
     */
    private boolean p = false;

    /**
     * ??? *
     */
    private String viz = null;

    /**
     * State defines the position in the block (ENTRY, INBLOCK, or EXIT code)
     */
    private int state = INBLOCK;

    /**
     * Immediate JBlockObject in which this code exists
     */
    JBlockObject block = null;


    /**
     * Creates a new instance of JLineOfCode
     *
     * @param sbo   - start byte offset
     * @param ebo   - end byte offset
     * @param fromI - from instruction # (index into instruction array)
     * @param toI   - to instruction #
     * @param stmt  - stringified line of code
     */
    public JLineOfCode(int sbo, int ebo, int fromI, int toI, String stmt) {
        start = sbo;
        end = ebo;
        fromIndex = fromI;
        toIndex = toI;
        this.stmt = stmt;
        viz = stmt; //can be modified so it returns offsets too.
        state = INBLOCK;
        decoration = false;
    }

    /**
     * Creates a new instance of JLineOfCode
     * This JLineOfCode is either block entry or block exit code
     * since no offsets are provided.
     *
     * @param stmt  Stringified line of code
     * @param jbo   the JBlockObject this line is associated with
     * @param state ENTRY | EXIT
     */
    public JLineOfCode(String stmt, JBlockObject jbo, int state) {
        this(-1, -1, -1, -1, stmt);
        this.state = state;
        this.block = jbo;
        decoration = true;
    }

    /**
     * Sets the LineOfCode's containing block
     *
     * @param jbo BlockObject
     */
    public void setBlock(JBlockObject jbo) {
        block = jbo;
    }

    /**
     * @return Returns the LineOfCode's containing block
     */
    public JBlockObject getBlock() {
        return block;
    }

    /**
     * @return Returns TRUE if line is entry code, FALSE otherwise
     */
    public boolean isEntryCode() {
        return state == this.ENTRY;
    }

    /**
     * @return Returns TRUE if line is exit code, FALSE otherwise
     */
    public boolean isExitCode() {
        return state == this.EXIT;
    }

    /**
     * @return Returns TRUE if line is in block code, FALSE otherwise
     */
    public boolean isInBlockCode() {
        return state == this.INBLOCK;
    }

    /**
     * @return Returns TRUE if line has not bytecode offsets, FALSE otherwise
     */
    public boolean isDecoration() {
        return decoration;
    }

    /**
     * @return Outputs the stmt as a string
     */
    public String toString() {
        return viz;
    }

    /**
     * @param indent Indentation level.
     * @return Outputs the stmt as a string, prefixed with _indent
     */
    public String toString(String indent) {
        return indent + viz;
    }

    /**
     * @param indent ??
     * @param debug  Not Used - ???
     * @return Outputs the stmt as a string, prefixed with _indent
     */
    public String toString(String indent, boolean debug) {
        return "[" + start + "-" + end + " ]" + indent + viz;
    }
}
