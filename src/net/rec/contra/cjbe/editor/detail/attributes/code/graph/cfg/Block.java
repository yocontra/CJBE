
package net.rec.contra.cjbe.editor.detail.attributes.code.graph.cfg;

import java.util.*;
import org.apache.bcel.generic.*;

public class Block {
    private InstructionHandle head;
    private InstructionHandle tail;
    private MethodGen mg;
    private List<Block> preds;
    private List<Block> succs;
    private int len = 0;
    private int idx = 0;
    private BlockGraph graph;

    public Block(InstructionHandle head, InstructionHandle tail, MethodGen mg,
            int idx, int len, BlockGraph graph) {
        this.head = head;
        this.tail = tail;
        this.mg = mg;
        this.idx = idx;
        this.len = len;
        this.graph = graph;
    }
    
    public MethodGen getMethod() {
        return mg;
    }
    
    public Iterator iterator() {
        return mg.getInstructionList().iterator();
    }
    
    public void setIndex(int idx) {
        this.idx = idx;
    }
    
    public int getIndex() {
        return idx;
    }
    
    public InstructionHandle getHead() {
        return head;
    }
    
    public InstructionHandle getTail() {
        return tail;
    }
    
    public void setPreds(List<Block> preds) {
        this.preds = preds;
    }
    
    public List<Block> getPreds() {
        return preds;
    }
    
    public void setSuccs(List<Block> succs) {
        this.succs = succs;
    }
    
    public List<Block> getSuccs() {
        return succs;
    }
    
    public String toString() {
        return "block #" + idx;
    }
    
    public String toString(boolean verbose) {
        
        if(!verbose) {
            return toString();
        } else {
            StringBuilder sb = new StringBuilder("block " + idx + ":\n");
            sb.append("[preds: ");
            Iterator<Block> it;
            
            if(preds != null) {
                it = preds.iterator();
                
                while(it.hasNext()) {
                    sb.append(it.next().getIndex()).append(" ");
                }
            }
            sb.append("] [succs: ");
            
            if(succs != null) {
                it = succs.iterator();
                
                while(it.hasNext()) {
                    sb.append(it.next().getIndex()).append(" ");
                }
            }
            sb.append("]\n");
            Iterator<InstructionHandle> it2 = iterator();
            
            if(it2.hasNext()) {
                InstructionHandle ih = it2.next();
                sb.append(ih.toString()).append(";\n");
                
                while(it2.hasNext()){
                    ih = it2.next();
                    
                    if(ih == tail) {
                        break;
                    }
                    sb.append(ih.toString()).append(";\n");
                }
                ih = tail;
                
                if(tail == null) {
                    sb.append("null tail; block length: ").append(len).append("\n");
                } else if(head != tail) {
                    sb.append(ih.toString()).append(";\n");
                }
            }  else {
                System.out.println("no basic blocks found, must be interface or abstract class");
            }
            return sb.toString();
        }
    }
}