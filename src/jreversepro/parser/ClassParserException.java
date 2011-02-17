/*
 * @(#)ClassParserException.java
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

/**
 * Thrown if Class File does not follow the prescribed format.
 *
 * @author Karthik Kumar
 * @version 1.3
 */
public class ClassParserException extends Exception {

    /**
     * Exception message.
     */
    private String msg;

    /**
     * Constructor.
     *
     * @param aMsg Exception Message.
     */
    public ClassParserException(String aMsg) {
        msg = aMsg;
    }

    /**
     * Constructor.
     *
     * @param aMsg Exception Message as a StringBuffer.
     */
    public ClassParserException(StringBuffer aMsg) {
        msg = aMsg.toString();
    }

    /**
     * Serialized version.
     *
     * @return Returns a Stringified version of the string.
     */
    public String toString() {
        return "jreversepro.parser.ClassParserException : " + msg;
    }
}
