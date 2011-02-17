/*
 * @(#)JDlgAbout.java
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

import jreversepro.common.AppConstants;

import javax.swing.*;
import java.awt.*;

/**
 * This contains the definition of the About Dialog Box
 *
 * @author Karthik Kumar
 * @version 1.3
 */
public class JDlgAbout extends JDialog implements AppConstants {

    /**
     * @param aParent Parent Frame
     * @param aTitle  Title of the Dialog box.
     */
    public JDlgAbout(JFrame aParent, String aTitle) {
        super(aParent, aTitle, true);


        JTextArea TxtAbout = new JTextArea(GPL_INFO);
        JScrollPane ScrAbout = new JScrollPane(TxtAbout);


        TxtAbout.setEditable(false);


        getContentPane().setLayout(new GridLayout(1, 1));
        getContentPane().add(ScrAbout);

        setSize(400, 170);
        setLocation(150, 50);
        setResizable(false);
        setVisible(true);
        addWindowListener(new DlgClose());
    }

}

