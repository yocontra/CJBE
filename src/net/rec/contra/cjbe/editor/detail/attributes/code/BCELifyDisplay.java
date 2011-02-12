package net.rec.contra.cjbe.editor.detail.attributes.code;

import net.rec.contra.cjbe.editor.BrowserInternalFrame;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;

public class BCELifyDisplay extends JEditorPane {

    BrowserInternalFrame internalFrame;


    BCELifyDisplay(OutputStream out, int methodIndex, BrowserInternalFrame internalFrame) {
        this.internalFrame = internalFrame;
        setEditable(true);
        setText(out.toString());
        UnderlineHighlighter.UnderlineHighlightPainter highlighter = new UnderlineHighlighter.UnderlineHighlightPainter(Color.RED);
        WordSearcher s = new WordSearcher(this, highlighter);
        setCaretPosition(s.search("createMethod_" + methodIndex + "()"));
    }

}