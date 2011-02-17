/*
 * @(#)JErrorDlg.java
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
 */
package jreversepro.awtui;

import jreversepro.gui.DlgClose;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class JErrorDlg extends Dialog
        implements ActionListener {

    Button BtnCancel;
    Button BtnOk;
    TextArea TxtDetails;

    static final int WIDTH = 450;
    static final int HEIGHT = 200;

    public JErrorDlg(Frame parent, String FileName, Exception _ex) {
        super(parent, "Error : " + FileName, true);

        BtnCancel = new Button("  Cancel  ");
        BtnOk = new Button("     Ok    ");

        TxtDetails = new TextArea(getStackTrace(_ex));
        TxtDetails.setEditable(false);


        BtnCancel.addActionListener(this);
        BtnOk.addActionListener(this);
        addWindowListener(new DlgClose());

        setLocation(200, 200);
        setResizable(false);
        setSize(WIDTH, HEIGHT);
        addComponents();
    }

    private void addComponents() {
        setLayout(null);

        TxtDetails.setBounds(0, (int) (0.25 * HEIGHT),
                WIDTH, (int) (0.5 * HEIGHT));
        BtnOk.setBounds(0, (int) (0.8 * HEIGHT), (int) (0.3 * WIDTH),
                (int) (0.1 * HEIGHT));
        BtnCancel.setBounds((int) (0.6 * WIDTH), (int) (0.8 * HEIGHT),
                (int) (0.3 * WIDTH), (int) (0.1 * HEIGHT));
        add(TxtDetails);
        add(BtnOk);
        add(BtnCancel);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == BtnOk ||
                e.getSource() == BtnCancel) {
            setVisible(false);
        }
    }

    private String getStackTrace(Exception _ex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        _ex.printStackTrace(new PrintStream(baos));
        return baos.toString().trim();
    }
}

