/*
 * JBlockObject.java
 *
 * Created on September 4, 2002, 2:50 PM
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


package jreversepro.reflect.method;

import jreversepro.reflect.JLineOfCode;

import java.util.Vector;

/**
 * @author pazandak@objs.com -- Copyright 2002.
 */
public abstract class JBlockObject {

    /**
     * Contains list of all blocks and stmts contained within this block
     */
    private Vector blocksNstmts;

    /**
     * Default indentation
     */
    private String defaultIndent = "     ";

    /**
     * Directed indentation
     */
    protected String indent = "";


    /**
     * Creates a new instance of BranchObject
     */
    public JBlockObject() {
        blocksNstmts = new Vector();
    }

    /**
     * Returns TRUE if block has only one stmt or block & does not need
     * bracketing, otherwise returns FALSE.
     */
    protected boolean isSimpleBlock() {
        return blocksNstmts.size() == 1;
    }

    /**
     * Called to add a sub block
     */
    public void addBlock(JBlockObject _jbo) {
        blocksNstmts.add(_jbo);
    }

    /**
     * Called to remove last block
     */
    public boolean removeLastBlock() {
        if (blocksNstmts.size() > 0)
            blocksNstmts.removeElementAt(blocksNstmts.size() - 1);
        else return false;
        return true;
    }


    /**
     * Called to add a line of code
     */
    public void addStatement(JLineOfCode _loc) {
        blocksNstmts.add(_loc);
    }


    /**
     * Called to remove the last line of code added
     */
    public JLineOfCode removeLastStatement() {
        if (blocksNstmts.size() > 0)
            return (JLineOfCode) blocksNstmts.remove(blocksNstmts.size() - 1);
        else
            return null;
    }


    /**
     * Returns any starting code to open the block as a String
     */
    protected String getEntryCode() {
        return "";
    }

    /**
     * Returns any terminating code to close the block as a String
     */
    protected String getExitCode() {
        return "";
    }

    /**
     * Returns any starting code to open the block as a JLineOfCode
     */
    protected JLineOfCode getEntryLineOfCode() {
        return new JLineOfCode(indent + getEntryCode(), this, JLineOfCode.ENTRY);
    }

    /**
     * Returns any terminating code to close the block as a JLineOfCode
     */
    protected JLineOfCode getExitLineOfCode() {
        return new JLineOfCode(indent + getExitCode(), this, JLineOfCode.EXIT);
    }


    /**
     * Outputs the method code contained in this block (and sub-blocks) as a string
     */
    public String toString(String _indent) {

        indent = _indent;

        StringBuffer sb = new StringBuffer();

        //Print block entry code
        sb.append(indent + getEntryCode());

        //Print code inside block
        //System.out.println("[numBlocks="+blocksNstmts.size());        
        for (int i = 0; i < blocksNstmts.size(); i++) {
            Object o = blocksNstmts.get(i);
            if (o instanceof JBlockObject) {
                sb.append(((JBlockObject) o).toString(indent + defaultIndent));
            } else if (o instanceof JLineOfCode) {
                sb.append(((JLineOfCode) o).toString(indent + defaultIndent));
            }
        }

        //Print block exit code
        sb.append(indent + getExitCode());

        return sb.toString();
    }


    /**
     * Outputs the method code contained in this block (and sub-blocks) as a vector
     * of JLineOfCode objects
     */
    public Vector getFlattenedCode(String _indent) {

        indent = _indent;
        Vector locs = new Vector();

        //Adds block entry code as a JLineOfCode
        locs.add(getEntryLineOfCode());

        //Print code inside block
        for (int i = 0; i < blocksNstmts.size(); i++) {
            Object o = blocksNstmts.get(i);
            if (o instanceof JBlockObject)
                locs.add(((JBlockObject) o).getFlattenedCode(_indent + defaultIndent));
            else if (o instanceof JLineOfCode)
                locs.add(((JLineOfCode) o).toString(_indent + defaultIndent));
        }

        //Adds block exit code as a JLineOfCode
        locs.add(getExitLineOfCode());


        return locs;
    }
}
