/*
 * JCaseBlock.java
 *
 * Created on September 5, 2002, 4:55 PM
 */

package jreversepro.reflect.method;

import jreversepro.common.KeyWords;
import jreversepro.revengine.JBranchEntry;

import java.util.StringTokenizer;

/**
 * @author akkumar
 */
public class JCaseBlock extends JBlockObject implements KeyWords {

    /**
     * Contains stringified switch expression
     */
    private String expr;

    /**
     * Associated Branch Entry
     */
    private JBranchEntry branch;

    /**
     * Creates a new instance of JCaseBlock
     */
    public JCaseBlock(JBranchEntry _jbe, String _expr) {
        branch = _jbe;
        expr = _expr;
    }

    /**
     * Outputs any starting code to open the block
     */
    protected String getEntryCode() {
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(expr, ",");
        while (st.hasMoreTokens()) {
            String caseTarget = st.nextToken();
            sb.append("\n" + indent);
            if (!caseTarget.equals(DEFAULT)) {
                sb.append("case ");
            }
            sb.append(caseTarget + ": ");
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Outputs any terminating code to close the block
     */
    protected String getExitCode() {
        return "";
    }

}
