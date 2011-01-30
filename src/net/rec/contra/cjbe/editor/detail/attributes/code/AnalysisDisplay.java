package net.rec.contra.cjbe.editor.detail.attributes.code;

import net.rec.contra.cjbe.editor.BrowserInternalFrame;
import org.gjt.jclasslib.structures.ClassFile;
import soot.*;
import soot.dava.DavaBody;
import soot.dava.DavaUnitPrinter;
import soot.grimp.Grimp;
import soot.grimp.GrimpBody;
import soot.javaToJimple.AbstractJimpleBodyBuilder;
import soot.javaToJimple.ClassDeclFinder;
import soot.javaToJimple.JimpleBodyBuilder;
import soot.jimple.JimpleMethodSource;
import soot.jimple.parser.JimpleAST;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.util.Iterator;


public class AnalysisDisplay extends JEditorPane {

    BrowserInternalFrame internalFrame;
    SootClass klass;

    SootMethod thisMethod;
    Body mBody;
    ExceptionalUnitGraph cGraph;

    AnalysisDisplay(int methodIndex, ClassFile classFile, BrowserInternalFrame internalFrame) {
        this.internalFrame = internalFrame;
        setEditorKitForContentType("text/html", new HTMLEditorKit());
        setContentType("text/html");
        try {
            setEditable(false);
            Scene.v().setSootClassPath(System.getProperty("java.class.path") + ";" + internalFrame.getClassPathString());
            Scene.v().setPhantomRefs(true);
            klass = Scene.v().loadClassAndSupport(classFile.getThisClassName());
            thisMethod = (SootMethod) klass.getMethods().toArray()[methodIndex];
            mBody = thisMethod.retrieveActiveBody();
            cGraph = new CompleteUnitGraph(mBody);
        } catch (Exception e) {
            e.printStackTrace();
            setText("Error: " + e + "<br>\nControl Flow Analysis Unavailable.<br>\nMake sure your classpath has all necessary dependencies.");
            return;
        }
        String pageText = "";
        Iterator<Unit> it = cGraph.iterator();
        while (it.hasNext()) {
            Unit u = it.next();
            pageText += u.toString();
            if (u.branches())
            {
                pageText += " <font color=\"red\">(Jumps)</font>";
            }
            if (u.getBoxesPointingToThis().size() > 1){
                pageText += " <font color=\"red\">(Jump Point)</font>";
            }
            if (it.hasNext()) pageText += "<br>\n";
        }
        if (!pageText.equals("")) {
            setCaretPosition(0);
            pageText = pageText.replaceAll("virutalinvoke", "<font color=\"orange\">virtualinvoke</font>");
            pageText = "<html>\n<body>\n<font size='3' face='monospaced'>\n" + pageText + "\n</font>\n</body>\n</html>";
            setText(pageText);
        } else {
            setText("Control Flow Analysis Unavailable.");
        }
    }

}