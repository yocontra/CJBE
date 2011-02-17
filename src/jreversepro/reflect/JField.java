/*
 * @(#)JField.java  1.00 00/12/09
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

/**
 * Abstraction for the field in a class.
 *
 * @author Karthik Kumar.
 */
public class JField extends JMember {

    /**
     * Initial value of field. Applicable only if
     * the qualifier has a 'final' keyword in it.
     */
    private String value;

    /**
     * no-arg constructor.
     */
    public JField() {
        value = "";
    }

    /**
     * Setter method for value.
     *
     * @param rhsValue New Value
     */
    public void setValue(String rhsValue) {
        value = rhsValue;
    }

    /**
     * Getter method for value.
     *
     * @return Returns value
     */
    public String getValue() {
        return value;
    }
}