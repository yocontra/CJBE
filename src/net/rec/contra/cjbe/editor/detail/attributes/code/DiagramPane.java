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

public class DiagramPane extends AbstractDetailPane implements FocusListener {

    private HashMap<String, DiagramDisplay> editPanes = new HashMap<String, DiagramDisplay>();


    private BrowserInternalFrame internalFrame;

    public DiagramPane(BrowserServices services) {
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
                    byte[] code = ((CodeAttribute) methods[i]
                            .getAttributes()[j]).getCode();
                    addEditPane(code, methodIndex, classFile);
                    break;
                }
            }
        }
    }

    private void addEditPane(byte[] code, String methodIndex, ClassFile classFile) {
        DiagramDisplay editArea = new DiagramDisplay(code, Integer.parseInt(methodIndex), classFile, internalFrame);
        //System.out.println(methodIndex);
        //Scrollbar
        JScrollPane scroll = new JScrollPane(editArea);
        scroll.setRowHeaderView(new LineNumberView(editArea));
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
                    byte[] code = ((CodeAttribute) methods[i]
                            .getAttributes()[j]).getCode();
                    addEditPane(code, methodIndex, classFile);
                    break;
                }
            }
            if (editPanes.get(methodIndex) == null) {
                addEditPane(null, methodIndex, classFile);
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
