/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package net.rec.contra.cjbe.editor.detail.elementvalues;

import net.rec.contra.cjbe.editor.BrowserServices;
import net.rec.contra.cjbe.editor.BrowserTreeNode;
import net.rec.contra.cjbe.editor.detail.FixedListDetailPane;
import org.gjt.jclasslib.structures.elementvalues.EnumElementValue;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
 * Class for showing element value entry of type Enum.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.4 $ $Date: 2006/09/25 16:00:58 $
 */
public class EnumElementValueEntryDetailPane extends FixedListDetailPane {

    private ExtendedJLabel lblTypeNameIndex;
    private ExtendedJLabel lblTypeNameIndexVerbose;
    private ExtendedJLabel lblConstNameIndex;
    private ExtendedJLabel lblConstNameIndexVerbose;

    public EnumElementValueEntryDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupLabels() {
        addDetailPaneEntry(normalLabel("Type name:"),
                lblTypeNameIndex = linkLabel(),
                lblTypeNameIndexVerbose = highlightLabel());
        addDetailPaneEntry(normalLabel("Const name:"),
                lblConstNameIndex = linkLabel(),
                lblConstNameIndexVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {
        EnumElementValue eeve = (EnumElementValue)
                ((BrowserTreeNode) treePath.getLastPathComponent()).getElement();

        constantPoolHyperlink(lblTypeNameIndex,
                lblTypeNameIndexVerbose,
                eeve.getTypeNameIndex());

        constantPoolHyperlink(lblConstNameIndex,
                lblConstNameIndexVerbose,
                eeve.getConstNameIndex());

        super.show(treePath);
    }

}
