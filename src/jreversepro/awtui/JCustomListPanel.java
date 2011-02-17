/*
 * @(#)JCustomListPanel.java
 *
 * JReversePro - Java Decompiler / Disassembler.
 * Copyright (C) 2000 Karthik Kumar.
 * EMail: akarthikkumar@hotmail.com
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
package jreversepro.awtui;

import javax.swing.*;
import java.awt.*;


/**
 * Provides a Custom List Panel.
 *
 * @author Karthik Kumar
 */
public class JCustomListPanel extends JPanel {
    /**
     * Corresponds to the Label 'Constant PoolTable'
     */
    private JLabel mLblList;

    /**
     * Corresponds to the Label 'Goto Index'.
     */
    private JLabel mLblGoto;

    /**
     * Gets Index of the ConstantPool as input from the user.
     */
    public JTextField mTxtIndex;

    /**
     * Goto button.
     */
    public JButton mBtnGoto;

    /**
     * Text to search to in the ConstantPool.
     */
    public JTextField mTxtSearch;

    /**
     * List of categories available for searching.
     */
    public JComboBox mChooseType;

    /**
     * Find button.
     */
    public JButton mBtnFind;

    /**
     * @param aMaxEntries Maximum Entries of the ConstantPool Table.
     */
    public JCustomListPanel(int aMaxEntries) {
        mLblList = new JLabel("Constant Pool Table:   " +
                "Total Entries " +
                String.valueOf(aMaxEntries),
                SwingConstants.CENTER);

        mLblGoto = new JLabel("Goto Index ",
                SwingConstants.CENTER);

        mTxtIndex = new JTextField(10);
        mBtnGoto = new JButton("Goto");

        Object[] items = {"ConstantPool Values",
                "Pointer I",
                "Pointer II"
        };

        mChooseType = new JComboBox(items);
        mTxtSearch = new JTextField(10);
        mBtnFind = new JButton("Find Next");

        setLayout(new GridBagLayout());
        setSize(100, 75);
        addComponents();
    }

    /**
     * Adds the components.
     */
    private void addComponents() {
        GridBagLayout grid = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        c.gridwidth = GridBagConstraints.REMAINDER;
        add(mLblList, c);

        c.gridwidth = 1;
        add(mLblGoto, c);
        add(mTxtIndex, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        add(mBtnGoto, c);

        c.gridwidth = 1;
        add(mTxtSearch, c);
        add(mChooseType, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        add(mBtnFind, c);

    }
}
