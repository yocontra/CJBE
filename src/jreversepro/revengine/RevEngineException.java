/**
 * @(#)RevEngineException.java
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


/**
 * Exception class thrown in case of any error while
 * decompiling/ disassembling the .class file.
 *
 * @author Karthik Kumar.
 */
public class RevEngineException extends Exception {

    /**
     * @param msg Exception message.
     */
    public RevEngineException(String msg) {
        super(msg);
    }


    /**
     * @param msg Exception message.
     * @param ex  Parent Exception reference.
     */
    public RevEngineException(String msg, Exception ex) {
        super(msg, ex);
    }


    /**
     * @return Return Stringified representation of the
     *         Exception class.
     */
    public String toString() {
        return (super.toString());
    }
}
