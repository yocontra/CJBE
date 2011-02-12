/*
 *
 */
package net.rec.contra.cjbe.editor.detail.attributes.code;

import net.rec.contra.cjbe.editor.AbstractDetailPane;
import net.rec.contra.cjbe.editor.BrowserInternalFrame;
import net.rec.contra.cjbe.editor.BrowserServices;
import net.rec.contra.cjbe.editor.BrowserTreeNode;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.util.BCELifier;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.MethodInfo;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

// import javax.swing.undo.UndoManager;

public class BCELifyPane extends AbstractDetailPane implements FocusListener {

    private HashMap<String, BCELifyDisplay> editPanes = new HashMap<String, BCELifyDisplay>();


    private BrowserInternalFrame internalFrame;

    public BCELifyPane(BrowserServices services) {
        super(services);
        setLayout(new CardLayout());
        updateEditPanes();
    }

    private void addEditPane(OutputStream out, String methodIndex) {

        BCELifyDisplay editArea = new BCELifyDisplay(out, Integer.parseInt(methodIndex), internalFrame);
        JScrollPane scroll = new JScrollPane(editArea);
        //scroll.setRowHeaderView(new LineNumberView(editArea));
        scroll.getVerticalScrollBar().setValue(10);
        scroll.getHorizontalScrollBar().setValue(10);
        this.add(scroll, methodIndex);

        editPanes.put(methodIndex, editArea);
    }

    public void show(TreePath treePath) {
        if (internalFrame.isReloading()) {
            updateEditPanes();
        }
        //The panel's name is the index of the method in the panel
        //It cannot be the name of the method since this isnt necessarily unique

        String methodIndex = Integer.toString(((BrowserTreeNode) treePath.getParentPath().getLastPathComponent()).getIndex());
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, methodIndex);
    }

    private void updateEditPanes() {
        internalFrame = (BrowserInternalFrame) services;
        ClassFile classFile = services.getClassFile();
        MethodInfo[] methods = classFile.getMethods();
        JavaClass javaClass;
        try {
            javaClass = new ClassParser(internalFrame.getFileName()).parse();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        ClassGen cg = new ClassGen(javaClass);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BCELifier v = new BCELifier(javaClass, out);
        v.start();
        for (int i = 0; i < methods.length; i++) {
            String methodIndex = Integer.toString(i);
            addEditPane(out, methodIndex);
        }
    }


    protected void setupComponent() {

    }

    public void focusGained(FocusEvent arg0) {
    }

    public void focusLost(FocusEvent arg0) {

    }

    public HashMap getEditPanes() {
        return editPanes;
    }


}
