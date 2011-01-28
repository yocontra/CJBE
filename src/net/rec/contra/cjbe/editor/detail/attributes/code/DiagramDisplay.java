package net.rec.contra.cjbe.editor.detail.attributes.code;

import net.rec.contra.cjbe.editor.BrowserInternalFrame;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;
import org.apache.bcel.verifier.structurals.ControlFlowGraph;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.MethodInfo;

import javax.swing.*;


public class DiagramDisplay extends JEditorPane {

    BrowserInternalFrame internalFrame;


    DiagramDisplay(byte[] code, int methodIndex, ClassFile classFile, BrowserInternalFrame internalFrame) {
        this.internalFrame = internalFrame;
        setEditable(false);
        InstructionList il = new InstructionList(code);
        //MethodInfo mi = classFile.getMethods()[methodIndex];
        //ClassGen cg = new ClassGen(jc);
        //int access_flags, Type return_type, Type[] arg_types, String[] arg_names, String method_name, String class_name, InstructionList il, ConstantPoolGen
        //mi.getAccessFlags(), mi.
        //MethodGen mg = new MethodGen();
        //ControlFlowGraph cfg = new ControlFlowGraph(mg);
    }

}