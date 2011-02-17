/*
 * @(#)JDlgFont.java
 *
 * JReversePro - Java Decompiler / Disassembler.
 * Copyright (C) 2000 Karthik Kumar.
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

package jreversepro.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.TreeSet;


public class JDlgFont
        extends JDialog
        implements ItemListener, ActionListener {

    //Static constants
    public static final int SELECTED = 1;
    public static final int CANCELLED = 2;

    public static final int PREVIEW_SIZE = 20;
    public static final int OPTIMUM_SIZE = 14;

    private JComboBox faces;

    private JButton BtnOk;
    private JButton BtnCancel;

    private JLabel LblTest;


    private int Selection;

    private Font CurFont;


    public JDlgFont(Frame owner, String title) {
        super(owner, title, true);

        faces = new JComboBox(getFontObjects());
        faces.addItemListener(this);


        BtnOk = new JButton("         Ok           ");
        BtnCancel = new JButton("     Cancel        ");

        LblTest = new JLabel("AaBbCc123", SwingConstants.LEFT);
        LblTest.setFont(new Font("SansSerif", Font.PLAIN,
                OPTIMUM_SIZE));

        GridBagConstraints c = new GridBagConstraints();

        getContentPane().setLayout(new GridBagLayout());

        c.gridheight = 2;
        c.weighty = 1.0;
        c.weightx = 3.0;
        getContentPane().add(faces, c);

        c.weighty = 0.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.weightx = 0.0;
        getContentPane().add(BtnOk, c);
        getContentPane().add(BtnCancel, c);

        c.gridwidth = 1; //reset to the default
        getContentPane().add(LblTest, c);

        setSize(500, 120);
        setLocation(100, 200);
        setResizable(false);

        BtnOk.addActionListener(this);
        BtnCancel.addActionListener(this);
    }

    public Font getChosenFont() {
        return CurFont;
    }


    public int showFontDialog() {
        setVisible(true);
        return Selection;
    }

    public void itemStateChanged(ItemEvent e) {
        Font PreviewFont = new Font((String) (faces.getSelectedItem()),
                Font.PLAIN,
                PREVIEW_SIZE);

        CurFont = new Font((String) (faces.getSelectedItem()),
                Font.PLAIN,
                OPTIMUM_SIZE);
        LblTest.setFont(PreviewFont);
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == BtnOk) Selection = SELECTED;
        else Selection = CANCELLED;
        setVisible(false);
    }


    //Get Font Objects
    private Object[] getFontObjects() {
        Object[] result;

        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();

        FontSet Fs = new FontSet(ge.getAllFonts());
        return Fs.getFonts();
    }
}// End of class

/**
 *
 **/
class FontSet extends TreeSet {

    public FontSet(Font[] FontList) {
        for (int i = 0; i < FontList.length; i++) {
            add((Object) (FontList[i].getFamily()));
        }
    }

    /**
     * @return the fonts present in the collection.
     */
    public Object[] getFonts() {
        Object[] result = new Object[this.size()];
        Iterator iter = this.iterator();
        int i = 0;
        while (iter.hasNext()) {
            result[i++] = iter.next();
        }
        return result;
    }
}