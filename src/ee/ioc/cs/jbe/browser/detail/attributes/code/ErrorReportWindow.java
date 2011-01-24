/*
 *
 */
package ee.ioc.cs.jbe.browser.detail.attributes.code;

import javax.swing.*;
import java.awt.*;

public class ErrorReportWindow extends JDialog {

    public ErrorReportWindow(JFrame frame, String text, String label) {
        super(frame);
        //setModal(true);
        setTitle(label);
        JTextArea textArea = new JTextArea();
        textArea.setText(text);
        textArea.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(600, 300));
        this.add(scroll);

    }

}
