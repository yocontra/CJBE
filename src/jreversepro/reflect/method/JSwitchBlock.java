/*
 * JSwitchBlock.java
 *
 * Created on September 5, 2002, 4:56 PM
 */

package jreversepro.reflect.method;

import jreversepro.revengine.JBranchEntry;

/**
 * @author pazandak@objs.com,  Copyright 2002.
 */
public class JSwitchBlock extends JBlockObject {

    /**
     * Contains stringified switch expression
     */
    private String expr;

    /**
     * Associated Branch Entry
     */
    private JBranchEntry branch;

    /**
     * Creates a new instance of JSwitchBlock
     */
    public JSwitchBlock(JBranchEntry _jbe, String _expr) {
        branch = _jbe;
        expr = _expr;
    }

    /**
     * Outputs any starting code to open the block
     */
    protected String getEntryCode() {
        return "switch (" + expr + ") {\n";
    }

    /**
     * Outputs any terminating code to close the block
     */
    protected String getExitCode() {
        return "\n" + indent + "}\n";
    }

}
