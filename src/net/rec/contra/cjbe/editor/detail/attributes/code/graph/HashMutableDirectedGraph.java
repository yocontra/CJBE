
package net.rec.contra.cjbe.editor.detail.attributes.code.graph;

import java.util.*;

public class HashMutableDirectedGraph<N> implements MutableDirectedGraph<N> {
    
    protected HashMap<N, List<N>> nodeToPreds = new HashMap<N, List<N>>();
    protected HashMap<N, List<N>> nodeToSuccs = new HashMap<N, List<N>>();
    
    protected HashSet<N> heads = new HashSet<N>();
    protected HashSet<N> tails = new HashSet<N>();
    
    public HashMutableDirectedGraph() { }
    
    public void clearAll() {
        nodeToPreds = new HashMap<N, List<N>>();
        nodeToSuccs = new HashMap<N, List<N>>();
        heads = new HashSet<N>();
        tails = new HashSet<N>();
    }
    
    public List<N> getHeads() {
        ArrayList<N> list = new ArrayList<N>();
        list.addAll(heads);
        return Collections.unmodifiableList(list);
    }
    
    public List<N> getTails() {
        ArrayList<N> list = new ArrayList<N>();
        list.addAll(tails);
        return Collections.unmodifiableList(list);
    }
    
    public List<N> getPreds(N node) {
        
        List<N> list = nodeToPreds.get(node);
        
        if (list != null) {
            return Collections.unmodifiableList(list);
        } else {
            throw new RuntimeException(node + " is not in graph");
        }
    }
    
    public List<N> getSuccs(N node) {
        List<N> list = nodeToSuccs.get(node);
        
        if (list != null) {
            return Collections.unmodifiableList(list);
        } else {
            throw new RuntimeException(node + " is not in graph");
        }
    }
    
    public int size() {
        return nodeToPreds.keySet().size();
    }
    
    public Iterator<N> iterator() {
        return nodeToPreds.keySet().iterator();
    }
    
    public void addEdge(N start, N dest) {
        
        if (start == null || dest == null) {
            throw new RuntimeException("edge cannot contain null beginning or end");
        }
        
        if (containsEdge(start, dest)) {
            return;
        }
        List<N> succsList = nodeToSuccs.get(start);
        
        if (succsList == null) {
            throw new RuntimeException(start + " not in graph");
        }
        List<N> predsList = nodeToPreds.get(dest);
        
        if (predsList == null) {
            throw new RuntimeException(dest + " not in graph");
        }
        
        if (heads.contains(dest)) {
            heads.remove(dest);
        }
        
        if (tails.contains(start)) {
            tails.remove(start);
        }
        succsList.add(dest);
        predsList.add(start);
    }
    
    public void removeEdge(N start, N dest) {
        
        if (!containsEdge(start, dest)) {
            return;
        }
        List<N> succsList = nodeToSuccs.get(start);
        
        if (succsList == null) {
            throw new RuntimeException(start + " not in graph");
        }
        List<N> predsList = nodeToPreds.get(dest);
        
        if (predsList == null) {
            throw new RuntimeException(dest + " not in graph");
        }
        succsList.remove(dest);
        predsList.remove(start);
        
        if (succsList.isEmpty()) {
            tails.add(start);
        }
        
        if (predsList.isEmpty()) {
            heads.add(dest);
        }
        nodeToSuccs.put(start, succsList);
        nodeToPreds.put(dest, predsList);
    }
    
    public boolean containsEdge(N start, N dest) {
        List<N> succs = nodeToSuccs.get(start);

        return succs != null && succs.contains(dest);
    }
    
    public boolean containsNode(N node) {
        return nodeToPreds.keySet().contains(node);
    }
    
    public List<N> getNodes() {
        return Arrays.asList((N[]) nodeToPreds.keySet().toArray());
    }
    
    public void addNode(N node) {
        
        if (containsNode(node)) {
            throw new RuntimeException("node " + node + "already in graph");
        }
        nodeToSuccs.put(node, new ArrayList<N>());
        nodeToPreds.put(node, new ArrayList<N>());
        heads.add(node);
        tails.add(node);
    }
    
    public void removeNode(N node) {
        List<N> succs = nodeToSuccs.get(node);
        
        for (N succNode: succs) {
            removeEdge(node, succNode);
        }
        nodeToSuccs.remove(node);
        List<N> preds = nodeToPreds.get(node);
        
        for (N predNode: preds) {
            removeEdge(predNode, node);
        }
        nodeToPreds.remove(node);
        
        if (heads.contains(node)) {
            heads.remove(node);
        }
        
        if (tails.contains(node)) {
            tails.remove(node);
        }
    }
    
    public void printGraph() {
        
        for (N node: nodeToPreds.keySet()) {
            System.out.println("Node = " + node);
            System.out.println("Preds:");
            
            for (N predNode: getPreds(node)) {
                System.out.print("     ");
                System.out.println(predNode);
            }
            System.out.println("Succs:");
            
            for (N succNode: getSuccs(node)) {
                System.out.print("     ");
                System.out.println(succNode);
            }
        }
    }
}

