/*
 * @(#)JClassEditPanel.java
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
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * JClassEditPanel - is the Main Panel that appears in the
 * application main Frame.
 *
 * @author Karthik Kumar
 * @version 1.3
 */
public class JClassEditPanel extends JPanel {

    /**
     * This is the name of the Root element appearing
     * at the top of the hierarchy.
     */
    static final String TREE_ROOT = "Java_Lang_Object";

    /**
     * Default font of the font GUI components, including
     * the editor and the tree.
     */
    public static final String DEFAULT_FONT = "SansSerif";

    /**
     * GUI Component containing the source code.
     */
    private JJavaDocumentEditor mTxtJava;

    /**
     * Tree component containing the fields and the methods.
     */
    private JTree mTreeFieldMethod;

    /**
     * Root Node of the tree.
     */
    private DefaultMutableTreeNode mRoot;

    /**
     * Font of the GUI components.
     */
    private Font mAppFont;

    /**
     * Constructor.
     */
    public JClassEditPanel() {
        mTxtJava = new JJavaDocumentEditor();
        mAppFont = new Font(DEFAULT_FONT, Font.PLAIN, 12);

        // initTree
        mRoot = new DefaultMutableTreeNode(TREE_ROOT);
        mTreeFieldMethod = new JTree(mRoot);

        JScrollPane ScrDocument = new JScrollPane(mTxtJava);
        JScrollPane ScrTree = new JScrollPane(mTreeFieldMethod);

        ScrDocument.setPreferredSize(new Dimension(500, 200));
        ScrTree.setPreferredSize(new Dimension(500, 200));

        setSize(500, 200);

        //arrange Components
        JSplitPane mSplitter = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                false,
                ScrTree,
                ScrDocument);
        mSplitter.setDividerLocation(0.5);
        setLayout(new GridLayout(1, 1));
        add(mSplitter);
    }

    /**
     * Sets the Font of the GUI components.
     *
     * @param aFont New Font to be set.
     */
    public void setEditorFont(Font aFont) {
        mAppFont = aFont;
        mTxtJava.setFont(aFont);
        mTreeFieldMethod.setFont(aFont);
    }

    /**
     * Get the Font for the GUI components.
     *
     * @return Returns the current font of the GUI components.
     */
    public final Font getEditorFont() {
        return mAppFont;
    }

    /**
     * Writes the code present in aCode to the Editor.
     *
     * @param aCode Decompiled Java source code to be
     *              written onto the editor.
     */
    public void writeCode(String aCode) {
        mTxtJava.setText(aCode);
    }

    /**
     * Writes the contents of the editor onto the File marked by
     * aOutputFile.
     *
     * @param aOutputFile OutputFile onto which the code is to be writen.
     * @param aParent     Parent Frame of this component.
     * @return true, if file contents written.
     *         false, otherwise.
     */
    public boolean writeToFile(JFrame aParent, File aOutputFile) {
        try {
            FileInputStream FlTemp = new FileInputStream(aOutputFile);
            //File Exists
            if (!confirmOverwrite(aParent, aOutputFile)) {
                return false;
            }
            FlTemp.close();
        } catch (Exception _ex) {
        }
        try {
            FileOutputStream out = new FileOutputStream(aOutputFile);
            PrintStream PrOut = new PrintStream(out);
            PrOut.print(mTxtJava.getText());
            PrOut.close();
            out.close();
            PrOut = null;
            out = null;
            String Msg = "Contents written onto " +
                    aOutputFile.toString() + " successfully";
            JOptionPane.showMessageDialog(
                    aParent,
                    Msg,
                    "File Saved",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception _ex) {
            System.err.println(_ex);
            return false;
        }
    }


    /**
     * Creates the tree model of the GUI version.
     *
     * @param aParent   Parent Frame
     * @param aFileName Name of the File.
     * @param aChildren Children nodes of the tree root.
     * @param aMaxIndex Maximum Children of the tree root.
     */
    public void createModel(JFrame aParent, String aFileName,
                            List aChildren) {
        mRoot.removeAllChildren();
        DefaultMutableTreeNode ClassName =
                new DefaultMutableTreeNode(aFileName);

        for (int i = 0; i < aChildren.size(); i++) {
            ClassName.add(new DefaultMutableTreeNode(aChildren.get(i)));
        }
        mRoot.add(ClassName);
        SwingUtilities.updateComponentTreeUI(aParent);
        mTreeFieldMethod.expandRow(1);
    }

    /**
     * @param aParent     Parent Frame
     * @param aOutputFile File  which already exists.
     * @return true, if user prompts to overwrite file.
     *         false, otherwise.
     */
    private boolean confirmOverwrite(JFrame aParent, File aOutputFile) {
        int a = JOptionPane.showConfirmDialog(aParent,
                "OverWrite File " + aOutputFile.toString(),
                "Confirm Overwrite",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
        return (a == JOptionPane.YES_OPTION);
    }
}