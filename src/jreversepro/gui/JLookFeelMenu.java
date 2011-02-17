/*
 * @(#)JLookFeelMenu.java
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JLookFeelMenu extends JMenu {

    JRadioButtonMenuItem MetalLookAndFeel;
    JRadioButtonMenuItem MotifLookAndFeel;
    JRadioButtonMenuItem WinLookAndFeel;

    ButtonGroup Btngroup;

    JFrame AppFrame;

    public static final String MOTIF_LF =
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    public static final String WINDOWS_LF =
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    public static final String METAL_LF =
            "javax.swing.plaf.metal.MetalLookAndFeel";

    String App_LF;
    static final String WINDOWS = "Win";
    static final String MOTIF = "Motif";
    static final String METAL = "Metal";

    public JLookFeelMenu(String title,
                         JFrame thisFrame) {
        super(title);

        AppFrame = thisFrame;

        ButtonGroup group = new ButtonGroup();

        MetalLookAndFeel = new JRadioButtonMenuItem("Metal", true);
        MotifLookAndFeel = new JRadioButtonMenuItem("Motif");
        WinLookAndFeel = new JRadioButtonMenuItem("Windows");

        group.add(MetalLookAndFeel);
        group.add(MotifLookAndFeel);
        group.add(WinLookAndFeel);

        add(MetalLookAndFeel);
        add(MotifLookAndFeel);
        add(WinLookAndFeel);

        addLookAndFeelListeners();
    }

    public JLookFeelMenu(JFrame thisFrame) {
        this("Look And Feel", thisFrame);
    }

    public final String getAppLookAndFeel() {
        return App_LF;
    }

    public void setAppLookAndFeel(String Rhs) {
        if (Rhs == null) setMetal_LF();
        else if (Rhs.compareTo(WINDOWS) == 0) setWin_LF();
        else if (Rhs.compareTo(MOTIF) == 0) setMotif_LF();
        else if (Rhs.compareTo(METAL) == 0) setMetal_LF();
        else setMetal_LF();
    }

    public void setDefaultLookAndFeel() {
        setMetal_LF();
    }

    private void setWin_LF() {
        try {
            UIManager.setLookAndFeel(WINDOWS_LF);
            SwingUtilities.updateComponentTreeUI(AppFrame);
            App_LF = WINDOWS;
        } catch (Exception _ex) {
        }
    }

    private void setMotif_LF() {
        try {
            UIManager.setLookAndFeel(MOTIF_LF);
            SwingUtilities.updateComponentTreeUI(AppFrame);
            App_LF = MOTIF;
        } catch (Exception _ex) {
        }
    }

    private void setMetal_LF() {
        try {
            UIManager.setLookAndFeel(METAL_LF);
            SwingUtilities.updateComponentTreeUI(AppFrame);
            App_LF = METAL;
        } catch (Exception _ex) {
        }
    }

    private void addLookAndFeelListeners() {
        MetalLookAndFeel.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setMetal_LF();
                    }
                }
        );


        MotifLookAndFeel.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setMotif_LF();
                    }
                }
        );

        WinLookAndFeel.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setWin_LF();
                    }
                }
        );

    }
}