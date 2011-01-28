
package net.rec.contra.cjbe.editor.detail.attributes.code.graph;

public interface MutableDirectedGraph<N> extends DirectedGraph<N> {
    
    public void addEdge(N start, N dest);
    
    public void removeEdge(N start, N dest);
    
    public boolean containsEdge(N start, N dest);
    
    public void addNode(N node);
    
    public void removeNode(N node);
    
    public boolean containsNode(N node);
}