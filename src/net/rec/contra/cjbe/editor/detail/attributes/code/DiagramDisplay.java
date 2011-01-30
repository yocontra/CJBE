package net.rec.contra.cjbe.editor.detail.attributes.code;

import net.rec.contra.cjbe.editor.BrowserInternalFrame;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.MethodGen;
import soot.CoffiClassSource;
import soot.G;
import soot.dava.Dava;
import soot.dava.DavaBody;
import soot.tools.CFGViewer;
import soot.util.cfgcmd.CFGIntermediateRep;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;


public class DiagramDisplay extends JEditorPane {

    BrowserInternalFrame internalFrame;


    DiagramDisplay(int methodIndex, BrowserInternalFrame internalFrame) {
        this.internalFrame = internalFrame;
        setEditable(false);
        JavaClass javaClass;
        try {
            javaClass = new ClassParser(internalFrame.getFileName()).parse();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to analyze control flow. Skipping...");
            return;
        }
        ClassGen cg = new ClassGen(javaClass);
        MethodGen mg = new MethodGen(cg.getMethods()[methodIndex], cg.getClassName(), cg.getConstantPool());
        soot.tools.CFGViewer cfg = new CFGViewer();

        //soot.dava.Dava d = new Dava(G.);
    }

}