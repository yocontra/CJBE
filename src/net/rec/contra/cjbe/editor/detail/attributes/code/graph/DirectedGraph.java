
package net.rec.contra.cjbe.editor.detail.attributes.code.graph;

import java.util.Iterator;
import java.util.List;

public interface DirectedGraph<N> {
    
    public List<N> getHeads();

    public List<N> getTails();

    public List<N> getPreds(N node);

    public List<N> getSuccs(N node);

    public int size();

    public Iterator<N> iterator();
}
