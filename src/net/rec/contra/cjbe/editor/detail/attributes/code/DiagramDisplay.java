package net.rec.contra.cjbe.editor.detail.attributes.code;

import net.rec.contra.cjbe.editor.BrowserInternalFrame;
import net.rec.contra.cjbe.editor.detail.attributes.code.graph.cfg.InstructionGraph;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.verifier.structurals.ControlFlowGraph;
import org.gjt.jclasslib.structures.ClassFile;

import javax.swing.*;
import java.io.IOException;


public class DiagramDisplay extends JEditorPane {

    BrowserInternalFrame internalFrame;


    DiagramDisplay(int methodIndex, BrowserInternalFrame internalFrame) {
        this.internalFrame = internalFrame;
        setEditable(false);
        JavaClass javaClass;
        try {
            javaClass = new ClassParser(internalFrame.getFileName()).parse();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("Failed to analyze control flow. Skipping...");
            return;
        }
        ClassGen cg = new ClassGen(javaClass);
        MethodGen mg = new MethodGen(cg.getMethods()[methodIndex], cg.getClassName(), cg.getConstantPool());
        InstructionGraph ig = new InstructionGraph(mg, true);
        setText(ig.toString());
    }

}