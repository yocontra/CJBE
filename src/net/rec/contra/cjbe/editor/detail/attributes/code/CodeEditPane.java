/*
 *
 */
package net.rec.contra.cjbe.editor.detail.attributes.code;

import net.rec.contra.cjbe.editor.AbstractDetailPane;
import net.rec.contra.cjbe.editor.BrowserInternalFrame;
import net.rec.contra.cjbe.editor.BrowserServices;
import net.rec.contra.cjbe.editor.BrowserTreeNode;
import net.rec.contra.cjbe.editor.codeedit.CodeGenerator;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.MethodInfo;
import org.gjt.jclasslib.structures.attributes.CodeAttribute;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;

// import javax.swing.undo.UndoManager;

public class CodeEditPane extends AbstractDetailPane implements FocusListener {

    /**
     *
     */
    private static final long serialVersionUID = 3376865231556430458L;

    private HashMap<String, CodeEditArea> editPanes = new HashMap<String, CodeEditArea>();


    private BrowserInternalFrame internalFrame;

    public CodeEditPane(BrowserServices services) throws InvalidByteCodeException {
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
                    addEditPane(methodIndex, code, classFile);
                    break;
                }
            }
        }

    }

    private void addEditPane(String methodIndex, byte[] code, ClassFile classFile) {
        CodeEditArea editArea = new CodeEditArea(code, classFile, internalFrame);

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
        CodeGenerator cg = new CodeGenerator();
        for (int i = 0; i < methods.length; i++) {
            String methodIndex = Integer.toString(i);
            byte[] code = null;
            try {
                code = ((CodeAttribute) methods[i].getAttributes()[0]).getCode();
            } catch (Exception e) {
                if (methods[i].getAttributes().length > 1) {
                    code = ((CodeAttribute) methods[i].getAttributes()[1]).getCode();
                } else {
                    System.out.println("Couldn't locate code attribute for method. :(");
                }
            }
            if (editPanes.get(methodIndex) == null) {
                addEditPane(methodIndex, code, classFile);
            }
            editPanes.get(methodIndex).setText(cg.makeMethod(
                    code, classFile));
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
