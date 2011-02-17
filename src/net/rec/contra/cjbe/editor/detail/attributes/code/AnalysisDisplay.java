package net.rec.contra.cjbe.editor.detail.attributes.code;

import jreversepro.reflect.JClassInfo;
import net.rec.contra.cjbe.editor.BrowserInternalFrame;

import javax.swing.*;

public class AnalysisDisplay extends JEditorPane {

    BrowserInternalFrame internalFrame;

    AnalysisDisplay(int methodIndex, BrowserInternalFrame internalFrame) {
        this.internalFrame = internalFrame;
        try {
            JClassInfo ci = internalFrame.getParser().getClassInfo();
            setText(ci.getStringifiedClass(false, false));
        } catch (Exception e) {
            setText("Analysis Unavailable.\n" + e.getMessage());
        }
    }

}