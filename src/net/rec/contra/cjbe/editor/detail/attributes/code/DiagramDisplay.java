package net.rec.contra.cjbe.editor.detail.attributes.code;

import net.rec.contra.cjbe.editor.BrowserInternalFrame;
import net.rec.contra.cjbe.editor.detail.attributes.code.graph.cfg.Block;
import net.rec.contra.cjbe.editor.detail.attributes.code.graph.cfg.BlockGraph;
import net.rec.contra.cjbe.editor.detail.attributes.code.graph.cfg.InstructionGraph;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.MethodGen;

import javax.swing.*;
import java.io.IOException;
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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("Failed to analyze control flow. Skipping...");
            return;
        }
        ClassGen cg = new ClassGen(javaClass);
        MethodGen mg = new MethodGen(cg.getMethods()[methodIndex], cg.getClassName(), cg.getConstantPool());
        InstructionGraph ig = new InstructionGraph(mg, true);
        BlockGraph blockGraph = new BlockGraph(ig);
        String pageText = "";
        Iterator<Block> it = blockGraph.iterator();
        while(it.hasNext()){
            Block bk = it.next();
            pageText += "Block " + bk.getIndex() + ":\n";
            if(bk.getSuccs() != null){
                pageText += " Hops to";
                for (Block bz : bk.getSuccs()){
                    pageText += "\n - Block " + bz.getIndex();
                }
                pageText += "\n";
            }
            InstructionHandle[] handles = bk.getMethod().getInstructionList().getInstructionHandles();
            for(int i = bk.getHead().getPosition(); i <= bk.getTail().getPosition(); i++){
                if(i < handles.length){
                    pageText += handles[i].toString() + "\n";
                } else {
                    pageText += " INVALID\n";
                }
                /*
                if(bk.getTail().getPosition() > mg.getInstructionList().getInstructionHandles().length){
                    System.out.println(mg.getName() + " had invalid unit data.");
                    pageText += "INVALID UNIT DATA\n";
                    break;
                } else {
                    pageText += handles[i].toString() + "\n";
                } */
            }
        }
        setText(pageText);
    }

}