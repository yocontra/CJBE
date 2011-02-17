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
package jreversepro.awtui;

import java.awt.*;

public class MainMenu extends MenuBar {
    Menu OnFile;
    Menu OnEdit;
    Menu OnView;
    Menu OnOptions;
    Menu OnHelp;


    public MenuItem OnFileOpen;
    public MenuItem OnFileSave;
    public MenuItem OnFileExit;

    public MenuItem OnEditCut;
    public MenuItem OnEditCopy;

    public MenuItem OnViewCPool;

    public MenuItem OnOptFont;

    public CheckboxMenuItem OnDisAssembler;

    public MenuItem OnHelpAbout;

    Frame parent;

    public MainMenu(Frame owner) {

        Font FontObj = new Font("Serif", Font.PLAIN, 10);

        String openFileName;

        parent = owner;

        OnFile = new Menu("File");
        OnEdit = new Menu("Edit");
        OnView = new Menu("View");
        OnOptions = new Menu("Options");
        OnHelp = new Menu("Help");


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

        OnHelp.add(OnHelpAbout);

        add(OnFile);
        add(OnEdit);
        add(OnView);
        add(OnOptions);
        add(OnHelp);

        setFont(FontObj);
    }

    public void setFlag(String Rhs) {
        boolean value = Rhs.equals("true");
        OnDisAssembler.setState(!value);
    }

    private void initMenuItems() {
        OnFileOpen = new MenuItem("Open");
        OnFileSave = new MenuItem("Save");
        OnFileExit = new MenuItem("Exit");

        OnEditCut = new MenuItem("Cut");
        OnEditCopy = new MenuItem("Copy");

        OnViewCPool = new MenuItem("ConstantPool");

        OnOptFont = new MenuItem("Set Font ");

        OnDisAssembler = new CheckboxMenuItem("DisAssemble", true);

        OnHelpAbout = new MenuItem("About");
    }
}
