/*
 *
 */
package net.rec.contra.cjbe.editor.detail.attributes.code;

import net.rec.contra.cjbe.editor.AbstractDetailPane;
import net.rec.contra.cjbe.editor.BrowserInternalFrame;
import net.rec.contra.cjbe.editor.BrowserServices;
import net.rec.contra.cjbe.editor.BrowserTreeNode;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.MethodInfo;
import org.gjt.jclasslib.structures.attributes.CodeAttribute;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;

// import javax.swing.undo.UndoManager;

public class AnalysisPane extends AbstractDetailPane implements FocusListener {

    private HashMap<String, AnalysisDisplay> editPanes = new HashMap<String, AnalysisDisplay>();


    private BrowserInternalFrame internalFrame;

    public AnalysisPane(BrowserServices services) {
        super(services);
        internalFrame = (BrowserInternalFrame) services;
        ClassFile classFile = services.getClassFile();
        MethodInfo[] methods = classFile.getMethods();
        this.setLayout(new CardLayout());
        for (int i = 0; i < methods.length; i++) {
            String methodIndex = Integer.toString(i);
            //Integer methodIndex = new Integer(i);
            for (int j = 0; j < methods[i].getAttributes().length; j++) {
                if (methods[i].getAttributes()[j] instanceof CodeAttribute) {
                    addEditPane(methodIndex, classFile);
                    break;
                }
            }
        }
    }

    private void addEditPane(String methodIndex, ClassFile classFile) {
        AnalysisDisplay editArea = new AnalysisDisplay(Integer.parseInt(methodIndex), internalFrame);
        //System.out.println(methodIndex);
        //Scrollbar
        JScrollPane scroll = new JScrollPane(editArea);
        LineNumberView view = new LineNumberView(editArea);
        scroll.setRowHeaderView(view);
        scroll.getVerticalScrollBar().setValue(10);
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
        for (int i = 0; i < methods.length; i++) {
            String methodIndex = Integer.toString(i);
            for (int j = 0; j < methods[i].getAttributes().length; j++) {
                if (methods[i].getAttributes()[j] instanceof CodeAttribute) {
                    addEditPane(methodIndex, classFile);
                    break;
                }
            }
            if (editPanes.get(methodIndex) == null) {
                addEditPane(methodIndex, classFile);
            }
            //editPanes.get(methodIndex).setText("dfgh");
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
