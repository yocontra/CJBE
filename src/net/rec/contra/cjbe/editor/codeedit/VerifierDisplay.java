package net.rec.contra.cjbe.editor.codeedit;

import net.rec.contra.cjbe.editor.BrowserMDIFrame;
import org.apache.commons.bcel6.Repository;
import org.apache.commons.bcel6.classfile.ClassFormatException;
import org.apache.commons.bcel6.classfile.ClassParser;
import org.apache.commons.bcel6.classfile.JavaClass;
import org.apache.commons.bcel6.classfile.Method;
import org.apache.commons.bcel6.verifier.VerificationResult;
import org.apache.commons.bcel6.verifier.Verifier;
import org.apache.commons.bcel6.verifier.VerifierFactory;
import org.gjt.jclasslib.util.GUIHelper;
import org.gjt.jclasslib.util.ProgressDialog;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.IOException;


public class VerifierDisplay extends JDialog {

    public VerifierDisplay(BrowserMDIFrame frame, String fileName) {
        super(frame);
        setModal(true);

        JTextPane textPane = new JTextPane();


        setTitle("Verification results");
        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setPreferredSize(new Dimension(700, 400));
        this.add(scroll);
        pack();

        GUIHelper.centerOnParentWindow(this, frame);

        VerifyThread vr = new VerifyThread(textPane, fileName);
        ProgressDialog progressDialog = new ProgressDialog(this, null, "Verifying method...");

        progressDialog.setRunnable(vr);
        progressDialog.setVisible(true);


    }

    private class VerifyThread implements Runnable {
        private String fileName;
        private JTextPane textPane;

        VerifyThread(JTextPane textPane, String fileName) {
            this.textPane = textPane;
            this.fileName = fileName;
        }

        public void run() {

            addVerifierResults(textPane, fileName);

        }
    }

    private void addVerifierResults(JTextPane textPane, String fileName) {


        StyledDocument doc = textPane.getStyledDocument();
        Style def = StyleContext.getDefaultStyleContext().getStyle(
                StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("green", regular);
        StyleConstants.setForeground(s, new Color(0, 200, 0));

        s = doc.addStyle("red", regular);
        StyleConstants.setForeground(s, Color.RED);

        s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);

        try {
            JavaClass javaClass = new ClassParser(fileName).parse();
            Repository.clearCache();
            //SyntheticRepository a = Repository.getRepository();
            Repository.addClass(javaClass);

            Verifier verifier = VerifierFactory.getVerifier(javaClass
                    .getClassName());
            VerificationResult verificationResult = verifier.doPass1();
            Style currentStyle;
            if (verificationResult.getStatus() == VerificationResult.VERIFIED_OK) {
                currentStyle = doc.getStyle("green");
            } else {
                currentStyle = doc.getStyle("red");
            }
            doc.insertString(doc.getLength(), "Pass 1: ", doc.getStyle("bold"));
            doc.insertString(doc.getLength(), verificationResult.getMessage()
                    + "\n", currentStyle);
            verificationResult = verifier.doPass2();
            if (verificationResult.getStatus() == VerificationResult.VERIFIED_OK) {
                currentStyle = doc.getStyle("green");
            } else {
                currentStyle = doc.getStyle("red");
            }
            doc.insertString(doc.getLength(), "Pass 2: ", doc.getStyle("bold"));
            doc.insertString(doc.getLength(), verificationResult.getMessage()
                    + "\n", currentStyle);

            Method[] methods = javaClass.getMethods();
            doc.insertString(doc.getLength(), "Pass 3a: \n", doc
                    .getStyle("bold"));
            for (int i = 0; i < methods.length; i++) {
                verificationResult = verifier.doPass3a(i);
                if (verificationResult.getStatus() == VerificationResult.VERIFIED_OK) {
                    currentStyle = doc.getStyle("green");
                } else {
                    currentStyle = doc.getStyle("red");
                }
                doc.insertString(doc.getLength(), "Verifying method: "
                        + methods[i].getName() + " \n", doc.getStyle("bold"));
                doc.insertString(doc.getLength(), verificationResult
                        .getMessage()
                        + "\n", currentStyle);
            }
            doc.insertString(doc.getLength(), "Pass 3b: \n", doc
                    .getStyle("bold"));
            for (int i = 0; i < methods.length; i++) {
                verificationResult = verifier.doPass3b(i);
                if (verificationResult.getStatus() == VerificationResult.VERIFIED_OK) {
                    currentStyle = doc.getStyle("green");
                } else {
                    currentStyle = doc.getStyle("red");
                }
                doc.insertString(doc.getLength(), "Verifying method: "
                        + methods[i].getName() + " \n", doc.getStyle("bold"));
                doc.insertString(doc.getLength(), verificationResult
                        .getMessage()
                        + "\n", currentStyle);
            }

            System.out.println(verificationResult.getMessage());

        } catch (ClassFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }


    }


}
