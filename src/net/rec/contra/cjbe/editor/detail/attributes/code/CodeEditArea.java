package net.rec.contra.cjbe.editor.detail.attributes.code;

import net.rec.contra.cjbe.editor.BrowserInternalFrame;
import net.rec.contra.cjbe.editor.codeedit.CodeGenerator;
import org.gjt.jclasslib.structures.ClassFile;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;


public class CodeEditArea extends JEditorPane {
    public UndoManager undo = new UndoManager();
    public UnderlineHighlighter.UnderlineHighlightPainter highlighter;
    public WordSearcher searcher;

    private static final long serialVersionUID = -5922040899795041012L;
    BrowserInternalFrame internalFrame;

    CodeEditArea(byte[] code, ClassFile classFile, BrowserInternalFrame internalFrame) {
        this.internalFrame = internalFrame;
        setFont(new Font("monospaced", Font.PLAIN, 14));
        setCaretPosition(0);
        setEditable(true);
        CodeGenerator cg = new CodeGenerator();
        setText(cg.makeMethod(code, classFile));
        highlighter = new UnderlineHighlighter.UnderlineHighlightPainter(Color.RED);
        searcher = new WordSearcher(this, highlighter);

        getDocument().addUndoableEditListener(new UndoRedoListener());
        getActionMap().put("Undo", new AbstractAction("Undo") {
            public void actionPerformed(ActionEvent evt) {
                doUndo();
            }
        });

        // Bind the undo action to ctl-Z
        getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

        // Create a redo action and add it to the text component
        getActionMap().put("Redo", new AbstractAction("Redo") {
            public void actionPerformed(ActionEvent evt) {
                doRedo();
            }

        });

        // Bind the redo action to ctl-Y
        getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

    }

    public void doUndo() {
        try {
            if (undo.canUndo()) {
                undo.undo();
            }
            if (undo.canUndo()) {
                internalFrame.getParentFrame().actionUndo.setEnabled(true);
            } else {
                internalFrame.getParentFrame().actionUndo.setEnabled(false);
            }
            if (undo.canRedo()) {
                internalFrame.getParentFrame().actionRedo.setEnabled(true);
            } else {
                internalFrame.getParentFrame().actionRedo.setEnabled(false);
            }
        } catch (CannotUndoException ignored) {
        }
    }

    public void doRedo() {
        try {
            if (undo.canRedo()) {
                undo.redo();
            }
            if (undo.canUndo()) {
                internalFrame.getParentFrame().actionUndo.setEnabled(true);
            } else {
                internalFrame.getParentFrame().actionUndo.setEnabled(false);
            }
            if (undo.canRedo()) {
                internalFrame.getParentFrame().actionRedo.setEnabled(true);
            } else {
                internalFrame.getParentFrame().actionRedo.setEnabled(false);
            }
        } catch (CannotRedoException ignored) {
        }
    }

    class UndoRedoListener implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            // Remember the edit and update the menus
            undo.addEdit(e.getEdit());
            if (undo.canUndo()) {
                internalFrame.getParentFrame().actionUndo.setEnabled(true);
            } else {
                internalFrame.getParentFrame().actionUndo.setEnabled(false);
            }
            if (undo.canRedo()) {
                internalFrame.getParentFrame().actionRedo.setEnabled(true);
            } else {
                internalFrame.getParentFrame().actionRedo.setEnabled(false);
            }
            /*
                * internalFrame.getParentFrame(). undoAction.updateUndoState();
                * redoAction.updateRedoState();
                */

        }
    }
}

class UnderlineHighlighter extends DefaultHighlighter {
    public UnderlineHighlighter(Color c) {
        painter = (c == null ? sharedPainter : new UnderlineHighlightPainter(c));
    }

    // Convenience method to add a highlight with
    // the default painter.
    public Object addHighlight(int p0, int p1) throws BadLocationException {
        return addHighlight(p0, p1, painter);
    }

    public void setDrawsLayeredHighlights(boolean newValue) {
        // Illegal if false - we only support layered highlights
        if (!newValue) {
            throw new IllegalArgumentException(
                    "UnderlineHighlighter only draws layered highlights");
        }
        super.setDrawsLayeredHighlights(true);
    }

    // Painter for underlined highlights
    public static class UnderlineHighlightPainter extends
            LayeredHighlighter.LayerPainter implements Highlighter.HighlightPainter {
        public UnderlineHighlightPainter(Color c) {
            color = c;
        }

        public void paint(Graphics g, int offs0, int offs1, Shape bounds,
                          JTextComponent c) {
            // Do nothing: this method will never be called
        }

        public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds,
                                JTextComponent c, View view) {
            g.setColor(color == null ? c.getSelectionColor() : color);

            Rectangle alloc;
            if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
                if (bounds instanceof Rectangle) {
                    alloc = (Rectangle) bounds;
                } else {
                    alloc = bounds.getBounds();
                }
            } else {
                try {
                    Shape shape = view.modelToView(offs0,
                            Position.Bias.Forward, offs1,
                            Position.Bias.Backward, bounds);
                    alloc = (shape instanceof Rectangle) ? (Rectangle) shape
                            : shape.getBounds();
                } catch (BadLocationException e) {
                    return null;
                }
            }

            FontMetrics fm = c.getFontMetrics(c.getFont());
            int baseline = alloc.y + alloc.height - fm.getDescent() + 1;
            g.drawLine(alloc.x, baseline, alloc.x + alloc.width, baseline);
            g.drawLine(alloc.x, baseline + 1, alloc.x + alloc.width,
                    baseline + 1);

            return alloc;
        }

        protected Color color; // The color for the underline
    }

    // Shared painter used for default highlighting
    protected static final Highlighter.HighlightPainter sharedPainter = new UnderlineHighlightPainter(
            null);

    // Painter used for this highlighter
    protected Highlighter.HighlightPainter painter;
}
