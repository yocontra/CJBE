
package net.rec.contra.cjbe.editor.detail.attributes.code.graph.cfg;

import java.util.*;

import net.rec.contra.cjbe.editor.detail.attributes.code.graph.DirectedGraph;
import org.apache.bcel.generic.*;

public class InstructionGraph implements DirectedGraph<InstructionHandle> {
    private List<InstructionHandle> heads;
    private List<InstructionHandle> tails;
    private Map<InstructionHandle, List<InstructionHandle>> instToSuccs;
    private Map<InstructionHandle, List<InstructionHandle>> instToPreds;
    private MethodGen method;
    private InstructionList instList;
    
    public InstructionGraph(MethodGen method, boolean exceptEdges) {
        this.method = method;
        instList = method.getInstructionList();
        instToSuccs = new HashMap<InstructionHandle, List<InstructionHandle>>();
        instToPreds = new HashMap<InstructionHandle, List<InstructionHandle>>();
        buildUnexceptionalEdges(instToSuccs, instToPreds);
        
        if(exceptEdges) {
            buildExceptionalEdges(instToSuccs, instToPreds);
        }
        buildHeadsAndTails();
    }
    
    
    private void buildUnexceptionalEdges(
            Map<InstructionHandle, List<InstructionHandle>> instToSuccs,
            Map<InstructionHandle, List<InstructionHandle>> instToPreds) {
        Iterator<InstructionHandle> instIt = instList.iterator();
        
        while(instIt.hasNext()) {
            instToPreds.put(instIt.next(), new ArrayList<InstructionHandle>());
        }
        instIt = instList.iterator();
        InstructionHandle currentIh;
        InstructionHandle nextIh = instIt.hasNext() ? instIt.next() : null;
        
        while(nextIh != null) {
            currentIh = nextIh;
            nextIh = instIt.hasNext() ? instIt.next() : null;
            List<InstructionHandle> successors = new ArrayList<InstructionHandle>();
            
            if(fallsThrough(currentIh)) {
                
                if(nextIh != null) {
                    successors.add(nextIh);
                    instToPreds.get(nextIh).add(currentIh);
                }
            }
            
            if(branches(currentIh)) {
                InstructionHandle target = ((BranchHandle) currentIh).getTarget();
                
                if(!successors.contains(target)) {
                    successors.add(target);
                    instToPreds.get(target).add(currentIh);
                }
            }
            instToSuccs.put(currentIh, successors);
        }
    }
    
    private void buildExceptionalEdges(
            Map<InstructionHandle, List<InstructionHandle>> instToSuccs,
            Map<InstructionHandle, List<InstructionHandle>> instToPreds) {
        CodeExceptionGen[] exceptions = method.getExceptionHandlers();

        for (CodeExceptionGen e : exceptions) {
            InstructionHandle first = e.getStartPC();
            InstructionHandle last = e.getEndPC().getPrev();
            InstructionHandle handler = e.getHandlerPC();
            //Iterator<InstructionHandle> it = instList.iterator();
            for(InstructionHandle ih : instList.getInstructionHandles()){
                if(first.getPosition() >= ih.getPosition() && ih.getPosition() >= last.getPosition()){
                    addEdge(instToSuccs, instToPreds, ih, handler);
                }
            }
            /*
            while (it.hasNext()) {
                InstructionHandle trapped = it.next();
                addEdge(instToSuccs, instToPreds, trapped, handler);
            }     */
        }
    }
    
    private boolean branches(InstructionHandle ih) {
        Instruction inst = ih.getInstruction();

        return inst instanceof BranchInstruction;
    }
    
    private boolean fallsThrough(InstructionHandle ih) {
        Instruction inst = ih.getInstruction();

        return !(inst instanceof GotoInstruction || inst instanceof ReturnInstruction);
    }
    
    private void buildHeadsAndTails() {
        List<InstructionHandle> tailList = new ArrayList<InstructionHandle>();
        List<InstructionHandle> headList = new ArrayList<InstructionHandle>();
        
        for(Iterator<InstructionHandle> it = instList.iterator(); it.hasNext(); ) {
            InstructionHandle ih = it.next();
            List<InstructionHandle> succs = instToSuccs.get(ih);
            
            if(succs.size() == 0) {
                tailList.add(ih);
            }
            List<InstructionHandle> preds = instToPreds.get(ih);
            
            if(preds.size() == 0) {
                headList.add(ih);
            }
        }
        InstructionHandle entry = instList.getStart();
        
        if(!headList.contains(entry)) {
            headList.add(entry);
        }
        tails = Collections.unmodifiableList(tailList);
        heads = Collections.unmodifiableList(headList);
    }
    
    private static void makeMappedListsUnmodifiable(Map<InstructionHandle, List<InstructionHandle>> map) {

        for (Map.Entry<InstructionHandle, List<InstructionHandle>> instructionHandleListEntry : map.entrySet()) {
            List<InstructionHandle> value = instructionHandleListEntry.getValue();

            if (value.size() == 0) {
                instructionHandleListEntry.setValue(Collections.EMPTY_LIST);
            } else {
                instructionHandleListEntry.setValue(Collections.unmodifiableList(value));
            }
        }
    }
    
    private void addEdge(
            Map<InstructionHandle, List<InstructionHandle>> instToSuccs, 
            Map<InstructionHandle, List<InstructionHandle>> instToPreds,
            InstructionHandle head, InstructionHandle tail) {
        List<InstructionHandle> headSuccs = instToSuccs.get(head);
        
        if(headSuccs == null) {
            headSuccs = new ArrayList<InstructionHandle>();
            instToSuccs.put(head, headSuccs);
        }
        
        if(!headSuccs.contains(tail)) {
            headSuccs.add(tail);
            List<InstructionHandle> tailPreds = instToPreds.get(tail);
            
            if (tailPreds == null) {
                tailPreds = new ArrayList<InstructionHandle>();
                instToPreds.put(tail, tailPreds);
            }
            tailPreds.add(head);
        }
    }
    
    public MethodGen getMethod() {
        return method;
    }
    
    public List<InstructionHandle> getHeads() {
        return heads;
    }
    
    public List<InstructionHandle> getTails() {
        return tails;
    }
    
    public List<InstructionHandle> getPreds(InstructionHandle ih) {
        
        if(!instToSuccs.containsKey(ih)) {
            throw new RuntimeException("graph does not contain " + ih);
        }
        return instToPreds.get(ih);
    }
    
    public List<InstructionHandle> getSuccs(InstructionHandle ih) {
        
        if(!instToSuccs.containsKey(ih)) {
            throw new RuntimeException("graph does not contain " + ih);
        }
        return instToSuccs.get(ih);
    }
    
    public int size() {
        return instList.size();
    }
    
    public Iterator<InstructionHandle> iterator() {
        return instList.iterator();
    }
    
    public String toString() {
        Iterator<InstructionHandle> it = instList.iterator();
        StringBuilder sb = new StringBuilder();
        
        while(it.hasNext()) {
            InstructionHandle ih = it.next();
            sb.append("// preds: ").append(getPreds(ih)).append("\n");
            sb.append(ih.toString()).append("\n");
            sb.append("// succs ").append(getSuccs(ih)).append("\n");
        }
        return sb.toString();
    }
}