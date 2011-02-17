/*
 * JDoWhileBlock.java
 *
 * Created on September 4, 2002, 2:55 PM
 */

package jreversepro.reflect.method;

import jreversepro.revengine.JBranchEntry;

/**
 * @author norpaul pnp@mn.rr.com -- Copyright 2002.
 */
public class JDoWhileBlock extends JBlockObject {

    /**
     * Associated Branch Entry
     */
    private JBranchEntry branch;

    /**
     * Contains first stringified var for do while expression
     */
    private String var1;

    /**
     * Contains stringified conditional operator for do while expression
     */
    private String oper;

    /**
     * Contains second stringified var for do while expression
     */
    private String var2;

    /**
     * Creates a new instance of JDoWhileBlock
     */
    public JDoWhileBlock(JBranchEntry _jbe) {
        this(_jbe, "", "", "");
    }

    /**
     * Creates a new instance of JDoWhileBlock
     */
    public JDoWhileBlock(JBranchEntry _jbe, String _var1, String _oper, String _var2) {
        branch = _jbe;
        var1 = _var1;
        oper = _oper;
        var2 = _var2;
    }

    /**
     * Set the var1 expression
     */
    public void setVar1(String _var1) {
        var1 = _var1;
    }

    /**
     * Set the oper expression
     */
    public void setOper(String _oper) {
        oper = _oper;
    }

    /**
     * Set the var2 expression
     */
    public void setVar2(String _var2) {
        var2 = _var2;
    }

    /**
     * Outputs any starting code to open the block
     */
    protected String getEntryCode() {
        return "do {\n";
    }

    /**
     * Outputs any terminating code to close the block
     */
    protected String getExitCode() {
        return "} while (" + var1 + " " + oper + " " + var2 + ");\n";
    }

}
