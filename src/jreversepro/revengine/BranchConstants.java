/*
 * @(#)BranchConstants.java
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
package jreversepro.revengine;

/**
 * This interface contains the constants used by branch types.
 *
 * @author Karthik Kumar
 */
public interface BranchConstants {

    /**
     * Invalid Type.
     */
    int TYPE_INVALID = 0;

    /**
     * A goto branch.
     */
    int TYPE_GOTO = 1;

    /**
     * A conditinal branch.
     */
    int TYPE_BRANCH = 2;

    /**
     * Branch of Jump Sub Routine type.
     */
    int TYPE_JSR = 3;

    /**
     * Branch signifying return type.
     */
    int TYPE_RET = 4;

    /**
     * If branch.
     */
    int TYPE_IF = 10;

    /**
     * Else branch.
     */
    int TYPE_ELSE = 11;

    /**
     * Else..If branch.
     */
    int TYPE_ELSE_IF = 12;

    /**
     * while branch.
     */
    int TYPE_WHILE = 13;

    /**
     * Do..while branch
     */
    int TYPE_DO_WHILE = 14;

    /**
     * try branch.
     */
    int TYPE_TRY = 15;

    /**
     * try branch that contains one implicit catch any block.
     * for synchronized and finally these kind of branches appear.
     */
    int TYPE_TRY_ANY = 16;

    /**
     * Branch signifying catch block
     */
    int TYPE_CATCH = 17;

    /**
     * Branch signifying catch block whose catch datatype /
     * handler type is 'ANY'.
     */
    int TYPE_CATCH_ANY = 18;

    /**
     * Branch signifying 'synchronized' block.
     */
    int TYPE_SYNC = 19;

    /**
     * Branch signifiying switch block.
     */
    int TYPE_SWITCH = 20;

    /**
     * Branch signifiying case block
     */
    int TYPE_CASE = 21;
}
