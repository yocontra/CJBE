package ee.ioc.cs.jbe.viewer.detail;

import ee.ioc.cs.jbe.viewer.AbstractDetailPane;
import ee.ioc.cs.jbe.viewer.BrowserInternalFrame;
import ee.ioc.cs.jbe.viewer.BrowserServices;

import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class AttributeGeneralPane extends AbstractDetailPane implements
        ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 3907856125379924109L;
    private BrowserInternalFrame internalFrame;

    public AttributeGeneralPane(BrowserServices services) {
        super(services);
        internalFrame = (BrowserInternalFrame) services;
    }

    public void show(TreePath treePath) {

    }

    protected void setupComponent() {
    }

    public void actionPerformed(ActionEvent event) {


    }

}
