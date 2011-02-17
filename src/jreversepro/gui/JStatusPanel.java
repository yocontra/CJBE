/*
 * @(#)JStatusPanel.java
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;


/**
 * Represents the Status Panel of the Decompiler
 */
public class JStatusPanel
        extends JPanel
        implements ActionListener {

    private JLabel LblTime;
    Timer SysTimer;
    Calendar Today;

    static final int INTERVAL = 1000;// milliseconds

    public JStatusPanel() {
        LblTime = new JLabel("",
                SwingConstants.RIGHT);

        //Right Justify Time
        setLayout(new GridLayout(1, 1));
        add(LblTime);


        Today = Calendar.getInstance();

        SysTimer = new Timer(INTERVAL, this);
        SysTimer.start();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == SysTimer) {
            StringBuffer formatTime = new StringBuffer("");
            LblTime.setText(new Date(System.currentTimeMillis()).toString());
        }
    }
}
