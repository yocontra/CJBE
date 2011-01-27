package net.rec.contra.cjbe.editor.detail.attributes.code;

import net.rec.contra.cjbe.editor.BrowserInternalFrame;
import org.gjt.jclasslib.structures.ClassFile;
import soot.*;
import soot.toolkits.graph.CompleteUnitGraph;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;


public class DiagramDisplay extends JEditorPane {

    BrowserInternalFrame internalFrame;
    SootClass klass;

    SootMethod thisMethod;
    Body mBody;
    CompleteUnitGraph cGraph;


    DiagramDisplay(int methodIndex, ClassFile classFile, BrowserInternalFrame internalFrame) {
        this.internalFrame = internalFrame;
        try {
            this.setEditable(false);
            Scene tz = Scene.v();
            tz.setSootClassPath(System.getProperty("java.class.path") + ";" + internalFrame.getClassPathString());
            tz.setPhantomRefs(true);
            this.klass = tz.loadClassAndSupport(classFile.getThisClassName());
            this.thisMethod = (SootMethod) klass.getMethods().toArray()[methodIndex];
            this.mBody = thisMethod.retrieveActiveBody();
            this.cGraph = new CompleteUnitGraph(mBody);
        } catch (Exception e) {
            e.printStackTrace();
            setText("Error: " + e + "\nControl Flow Analysis Unavailable.\nMake sure your classpath has all necessary dependencies.");
            return;
        }
        Iterator<Unit> it = cGraph.iterator();
        String pageText = "";
        while(it.hasNext()){
            pageText += it.next().toString();
            if(it.hasNext()) pageText += "\n";
        }
        setFont(new Font("monospaced", Font.PLAIN, 14));
        setCaretPosition(0);
        setEditable(true);
        if(!pageText.equals("")){
            setText(pageText);
        } else {
            setText("Control Flow Analysis Unavailable.");
        }
    }

}