/*
 * @(#)MainMenu.java
 *
 * JReversePro - Java Decompiler / Disassembler.
 * Copyright (C) 2000 2001  Karthik Kumar.
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

public class MainMenu extends JMenuBar {
    static final int MAX_THEMES = 3;

    JMenu OnFile;
    JMenu OnEdit;
    JMenu OnView;
    JMenu OnOptions;
    public JLookFeelMenu OnLookFeel;
    JMenu OnHelp;


    public JMenuItem OnFileOpen;
    public JMenuItem OnFileSave;
    public JMenuItem OnFileExit;

    public JMenuItem OnEditCut;
    public JMenuItem OnEditCopy;

    public JMenuItem OnViewCPool;

    public JMenuItem OnOptFont;

    public JRadioButtonMenuItem OnDisAssembler;
    public JRadioButtonMenuItem OnDecompiler;

    public JMenuItem OnHelpAbout;

    ButtonGroup group;
    JFrame parent;

    public MainMenu(JFrame owner) {

        Font FontObj = new Font("Serif", Font.PLAIN, 10);

        String openFileName;

        parent = owner;

        OnFile = new JMenu("File");
        OnEdit = new JMenu("Edit");
        OnView = new JMenu("View");
        OnOptions = new JMenu("Options");
        OnHelp = new JMenu("Help");


        OnLookFeel = new JLookFeelMenu("Look And Feel", parent);

        initMenuItems();

        OnFile.add(OnFileOpen);
        OnFile.add(OnFileSave);
        OnFile.add(OnFileExit);

        OnEdit.add(OnEditCut);
        OnEdit.add(OnEditCopy);

        OnView.add(OnViewCPool);

        OnOptions.add(OnOptFont);
        OnOptions.addSeparator();
        OnOptions.add(OnDisAssembler);
        OnOptions.add(OnDecompiler);

        OnHelp.add(OnHelpAbout);

        add(OnFile);
        add(OnEdit);
        add(OnView);
        add(OnOptions);
        add(OnLookFeel);
        add(OnHelp);

        setFont(FontObj);
    }

    public void setFlag(String Rhs) {
        boolean value = false;
        if (Rhs.compareTo("true") == 0) value = true;
        else value = false;
        OnDecompiler.setSelected(value);
        OnDisAssembler.setSelected(!value);
    }

    private void initMenuItems() {
        OnFileOpen = new JMenuItem("Open");
        OnFileSave = new JMenuItem("Save");
        OnFileExit = new JMenuItem("Exit");

        OnEditCut = new JMenuItem("Cut");
        OnEditCopy = new JMenuItem("Copy");

        OnViewCPool = new JMenuItem("ConstantPool");

        OnOptFont = new JMenuItem("Set Font ");

        OnDisAssembler = new JRadioButtonMenuItem("DisAssemble", true);
        OnDecompiler = new JRadioButtonMenuItem("Decompile");

        group = new ButtonGroup();
        group.add(OnDisAssembler);
        group.add(OnDecompiler);

        OnHelpAbout = new JMenuItem("About");
    }
}
