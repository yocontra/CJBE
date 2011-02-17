package net.rec.contra.cjbe.editor.detail.attributes.code;

import net.rec.contra.cjbe.editor.BrowserInternalFrame;

import javax.swing.*;
import java.awt.*;

public class BCELifyDisplay extends JEditorPane {

    BrowserInternalFrame internalFrame;


    BCELifyDisplay(String contents, int methodIndex, BrowserInternalFrame internalFrame) {
        this.internalFrame = internalFrame;
        setEditable(true);
        setText(contents);
        UnderlineHighlighter.UnderlineHighlightPainter highlighter = new UnderlineHighlighter.UnderlineHighlightPainter(Color.RED);
        WordSearcher s = new WordSearcher(this, highlighter);
        int i = s.search("createMethod_" + methodIndex + "()");
        if (i >= 0) {
            setCaretPosition(s.search("createMethod_" + methodIndex + "()"));
        }
    }

}