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
package jreversepro.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class JErrorDlg extends JDialog
        implements ActionListener {

    JButton BtnDetails;
    JButton BtnOk;
    JScrollPane ScrTrace;
    JScrollPane ScrError;
    boolean details;

    static final int WIDTH = 450;
    static final int HEIGHT = 200;

    public JErrorDlg(JFrame parent, String FileName, Exception _ex) {
        super(parent, "Error : " + FileName, true);
        details = false;

        BtnDetails = new JButton("Details >>");
        BtnOk = new JButton("     Ok    ");
        ScrError = new JScrollPane(new JLabel(_ex.toString(), SwingConstants.CENTER));
        JTextArea trace = new JTextArea(getStackTrace(_ex));
        trace.setEditable(false);
        ScrTrace = new JScrollPane(trace);

        BtnDetails.addActionListener(this);
        BtnOk.addActionListener(this);

        setLocation(200, 200);
        setResizable(false);
        setSize(WIDTH, (int) (0.5 * HEIGHT));
        addComponents();
    }

    private void addComponents() {
        getContentPane().setLayout(null);

        ScrError.setBounds(0, 0, WIDTH, (int) (0.25 * HEIGHT));
        BtnOk.setBounds(0, (int) (0.25 * HEIGHT), (int) (0.3 * WIDTH),
                (int) (0.1 * HEIGHT));
        BtnDetails.setBounds((int) (0.6 * WIDTH), (int) (0.25 * HEIGHT),
                (int) (0.3 * WIDTH), (int) (0.1 * HEIGHT));
        ScrTrace.setBounds(0, (int) (0.4 * HEIGHT), WIDTH,
                (int) (0.45 * HEIGHT));
        getContentPane().add(ScrError);
        getContentPane().add(BtnOk);
        getContentPane().add(BtnDetails);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == BtnOk) {
            setVisible(false);
        } else if (e.getSource() == BtnDetails) {
            //Make the details available.
            if (details) {
                setSize(WIDTH, (int) (0.5 * HEIGHT));
                getContentPane().remove(ScrTrace);
                details = false;
                BtnDetails.setText("Details >>");
            } else {
                setSize(WIDTH, HEIGHT);
                getContentPane().add(ScrTrace);
                details = true;
                BtnDetails.setText("<< Details");
            }
            validate();
            repaint();
        }
    }

    private String getStackTrace(Exception _ex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        _ex.printStackTrace(new PrintStream(baos));
        return baos.toString().trim();
    }
}
