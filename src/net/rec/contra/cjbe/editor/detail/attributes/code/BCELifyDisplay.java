package net.rec.contra.cjbe.editor.detail.attributes.code;

import net.rec.contra.cjbe.editor.BrowserInternalFrame;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.util.BCELifier;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BCELifyDisplay extends JEditorPane {

    BrowserInternalFrame internalFrame;


    BCELifyDisplay(int methodIndex, BrowserInternalFrame internalFrame) {
        this.internalFrame = internalFrame;
        setEditable(true);
        JavaClass javaClass;
        try {
            javaClass = new ClassParser(internalFrame.getFileName()).parse();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load class. Skipping...");
            setText("Failed to load class file.");
            return;
        }
        ClassGen cg = new ClassGen(javaClass);
        MethodGen mg = new MethodGen(cg.getMethods()[methodIndex], cg.getClassName(), cg.getConstantPool());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BCELifier v = new BCELifier(javaClass, out);
        v.visitMethod(mg.getMethod());
        setText(out.toString());
    }

}