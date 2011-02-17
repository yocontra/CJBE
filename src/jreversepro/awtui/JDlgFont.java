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

package jreversepro.awtui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class JDlgFont
        extends Dialog
        implements ItemListener, ActionListener {

    //Static constants
    public static final int SELECTED = 1;
    public static final int CANCELLED = 2;

    public static final int PREVIEW_SIZE = 20;
    public static final int OPTIMUM_SIZE = 14;

    private Choice faces;

    private Button BtnOk;
    private Button BtnCancel;

    private Label LblTest;

    int Selection;

    int fontIndex;
    String[] fontNames;

    public JDlgFont(Frame owner, String title) {
        super(owner, title, true);

        faces = new Choice();
        fillFontObjects();

        faces.addItemListener(this);


        BtnOk = new Button("         Ok           ");
        BtnCancel = new Button("     Cancel        ");

        LblTest = new Label("AaBbCc123", Label.LEFT);
        LblTest.setFont(new Font("SansSerif", Font.PLAIN,
                OPTIMUM_SIZE));

        GridBagConstraints c = new GridBagConstraints();

        setLayout(new GridBagLayout());

        c.gridheight = 2;
        c.weighty = 1.0;
        c.weightx = 3.0;
        add(faces, c);

        c.weighty = 0.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.weightx = 0.0;
        add(BtnOk, c);
        add(BtnCancel, c);

        c.gridwidth = 1; //reset to the default
        add(LblTest, c);

        setSize(500, 120);
        setLocation(100, 200);
        setResizable(false);

        BtnOk.addActionListener(this);
        BtnCancel.addActionListener(this);
    }

    public Font getChosenFont() {
        return new Font(fontNames[fontIndex],
                Font.PLAIN, OPTIMUM_SIZE);
    }


    public int showFontDialog() {
        setVisible(true);
        return Selection;
    }

    public void itemStateChanged(ItemEvent e) {
        fontIndex = faces.getSelectedIndex();
        LblTest.setText("");
        Font PreviewFont = new Font(fontNames[fontIndex],
                Font.PLAIN,
                PREVIEW_SIZE);

        System.out.println("Setting font " +
                PreviewFont.toString());
        LblTest.setFont(PreviewFont);
        LblTest.setText("AaBb123");
        PreviewFont = null;
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == BtnOk) Selection = SELECTED;
        else Selection = CANCELLED;
        setVisible(false);
    }


    //Get Font Objects
    private void fillFontObjects() {
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();

        fontNames = ge.getAvailableFontFamilyNames();
        System.out.println("Array Length " + fontNames.length);

        for (int i = 0; i < fontNames.length; i++) {
            faces.add(fontNames[i]);
        }
    }
}// End of class
