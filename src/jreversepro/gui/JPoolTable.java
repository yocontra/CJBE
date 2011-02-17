/*
 * @(#)JPoolTable.java
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
package jreversepro.gui;

import jreversepro.reflect.JConstantPool;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

/**
 * Provides a reusable JPoolTable that uses JTablePoolModel
 * used to provide Constant Pool Entries.
 * Provides the TableModel for the ConstantPool Model.
 */
public class JPoolTable extends JTable {
    public JPoolTable(JConstantPool RhsCpInfo) {
        super(new JPoolTableModel(RhsCpInfo));
    }
}

/*
*   Has the JPoolTableModel for the JTable.
*
*/
class JPoolTableModel extends AbstractTableModel {
    private int TotRows;
    private static final int MAX_COLUMNS = 5;

    String[] ColName;
    JConstantPool CpInfo;

    public JPoolTableModel(JConstantPool RhsCpInfo) {
        TotRows = RhsCpInfo.getMaxCpEntry();

        CpInfo = RhsCpInfo;
        initColumnNames();
    }

    public int getColumnCount() {
        return MAX_COLUMNS;
    }

    public int getRowCount() {
        return TotRows;
    }

    public Object getValueAt(int row, int col) {
        //Col : 0..4 index.
        if (row == 0) {
            return (Object) (new Integer(0));
        } else {
            switch (col) {
                case 0:
                    return String.valueOf(row);
                case 1:
                    return fillTagByte(row);
                case 2:
                    return fillValue(row);
                case 3:
                    return fillPtr1(row);
                case 4:
                    return fillPtr2(row);
                default:
                    return new Integer(0); //Error
            }
        }
    }

    // The default implementations of these methods in
    // AbstractTableModel would work, but we can refine them.
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public String getColumnName(int column) {
        return ColName[column];
    }

    private void initColumnNames() {
        ColName = new String[MAX_COLUMNS];

        ColName[0] = "Index";
        ColName[1] = "Tag Type";
        ColName[2] = "Tag Info";
        ColName[3] = "Pointer I";
        ColName[4] = "Pointer II";
    }

    //Private Methods

    private Object fillPtr1(int Index) {
        int Ptr = CpInfo.getPtr1(Index);
        if (Ptr == JConstantPool.PTR_INVALID) {
            return "PTR_INVALID";
        } else {
            return new Integer(Ptr);
        }
    }

    private Object fillPtr2(int Index) {
        int Ptr = CpInfo.getPtr2(Index);
        if (Ptr == JConstantPool.PTR_INVALID) {
            return "PTR_INVALID";
        } else {
            return new Integer(Ptr);
        }
    }

    private Object fillTagByte(int Index) {
        switch (CpInfo.getTagByte(Index)) {
            case JConstantPool.TAG_UTF8:
                return ("TAG_UTF8");
            case JConstantPool.TAG_INTEGER:
                return ("TAG_INTEGER");
            case JConstantPool.TAG_FLOAT:
                return ("TAG_FLOAT");
            case JConstantPool.TAG_LONG:
                return ("TAG_LONG");
            case JConstantPool.TAG_DOUBLE:
                return ("TAG_DOUBLE");
            case JConstantPool.TAG_CLASS:
                return ("TAG_CLASS");
            case JConstantPool.TAG_STRING:
                return ("TAG_STRING");
            case JConstantPool.TAG_FIELDREF:
                return ("TAG_FIELDREF");
            case JConstantPool.TAG_METHODREF:
                return ("TAG_METHOREF");
            case JConstantPool.TAG_INTERFACEREF:
                return ("TAG_INTERFACEREF");
            case JConstantPool.TAG_NAMETYPE:
                return ("TAG_NAMETYPE");
            case JConstantPool.TAG_NOTHING:
                return ("");
            default:
                return ("Invalid Tag");
        }
    }

    private Object fillValue(int Index) {
        return (CpInfo.getCpValue(Index));
    }

}
