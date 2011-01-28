
package net.rec.contra.cjbe.editor.detail.attributes.code.graph.cfg;

import java.util.*;

import net.rec.contra.cjbe.editor.detail.attributes.code.graph.DirectedGraph;
import org.apache.bcel.generic.*;

public class BlockGraph implements DirectedGraph<Block> {
    MethodGen method;
    InstructionList instList;
    List<Block> blocks;
    List<Block> heads = new ArrayList<Block>();
    List<Block> tails = new ArrayList<Block>();
    
    public BlockGraph(MethodGen mg, boolean exceptEdges) {
        new BlockGraph(new InstructionGraph(mg,  exceptEdges));
    }
    
    public BlockGraph(InstructionGraph instGraph) {
        method = instGraph.getMethod();
        instList = method.getInstructionList();
        Set<InstructionHandle> leaders = getLeaders(instGraph);
        buildBlocks(leaders, instGraph);
    }
    
    private Set<InstructionHandle> getLeaders(InstructionGraph instGraph) {
        
        if(instGraph.getMethod() != method) {
            throw new RuntimeException("BlockGraph: instGraph.getMethod() != method");
        }
        Set<InstructionHandle> leaders = new HashSet<InstructionHandle>();
        CodeExceptionGen[] exceptions = method.getExceptionHandlers();

        for (CodeExceptionGen exception : exceptions) {
            leaders.add(exception.getHandlerPC());
        }
        
        for(Iterator<InstructionHandle> instIt = instList.iterator(); instIt.hasNext(); ) {
            InstructionHandle ih = instIt.next();
            List<InstructionHandle> predecessors = instGraph.getPreds(ih);
            int predCount = predecessors.size();
            List<InstructionHandle> successors = instGraph.getSuccs(ih);
            int succCount = successors.size();
            
            if(predCount != 1) {  
                leaders.add(ih);
            }
            
            if(succCount > 1 || ih.getInstruction() instanceof BranchInstruction) {

                for (InstructionHandle successor : successors) {
                    leaders.add(successor);
                }
            }
        }
        return leaders;
    }
    
    private Map<InstructionHandle, Block> buildBlocks(Set<InstructionHandle> leaders,
            InstructionGraph instGraph) {
        List<Block> blockList = new ArrayList<Block>(leaders.size());
        Map<InstructionHandle, Block> instToBlock = new HashMap<InstructionHandle, Block>();
        InstructionHandle head = null;
        int len = 0;
        Iterator<InstructionHandle> it = instList.iterator();
        
        if(it.hasNext()) {
            head = it.next();
            
            if(!leaders.contains(head)) {
                throw new RuntimeException("BlockGraph: first instruction is not a leader");
            }
            len++;
        }
        InstructionHandle tail = head;
        int idx = 0;
        
        while(it.hasNext()) {
            InstructionHandle ih = it.next();
            
            if(leaders.contains(ih)) {
                addBlock(head, tail, idx, len, blockList, instToBlock);
                idx++;
                head = ih;
                len = 0;
            }
            tail = ih;
            len++;
        }
        if(len > 0) {
            addBlock(head, tail, idx, len, blockList, instToBlock);
        }
        
        for(it = instGraph.getHeads().iterator(); it.hasNext(); ) {
            head = it.next();
            Block headBlock = instToBlock.get(head);
            
            if(headBlock.getHead() == head) {
                heads.add(headBlock);
            } else {
                throw new RuntimeException("BlockGraph(): head instruction " +
                        "is not the first instruction in block");
            }
        }
        
        for(it = instGraph.getTails().iterator(); it.hasNext(); ) {
            tail = it.next();
            Block tailBlock = instToBlock.get(tail);
            
            if(tailBlock.getTail() == tail) {
                tails.add(tailBlock);
            } else {
                throw new RuntimeException("BlockGraph(): tail instruction " +
                        "is not the last instruction in block");
            }
        }

        for (Block block : blockList) {
            List<InstructionHandle> preds = instGraph.getPreds(block.getHead());
            List<Block> predBlocks = new ArrayList<Block>(preds.size());

            for (InstructionHandle predIh : preds) {
                Block predBlock = instToBlock.get(predIh);

                if (predBlock == null) {
                    throw new RuntimeException("BlockGraph(): block head mapped to null block");
                }
                predBlocks.add(predBlock);
            }

            if (predBlocks.size() == 0) {
                block.setPreds(Collections.EMPTY_LIST);
            } else {
                block.setPreds(Collections.unmodifiableList(predBlocks));

                if (block.getHead() == instList.getStart()) {
                    heads.add(block);
                }
            }
            List<InstructionHandle> succs = instGraph.getSuccs(block.getTail());
            List<Block> succBlocks = new ArrayList<Block>(succs.size());

            for (InstructionHandle succIh : succs) {
                Block succBlock = instToBlock.get(succIh);

                if (succBlock == null) {
                    throw new RuntimeException("BlockGraph(): block tail mapped to null block");
                }
                succBlocks.add(succBlock);
            }

            if (succBlocks.size() == 0) {
                block.setSuccs(Collections.EMPTY_LIST);

                if (!tails.contains(block)) {
                    throw new RuntimeException("BlockGraph(): block with no successors " +
                            "is not a tail: " + block.toString());
                }
            } else {
                block.setSuccs(Collections.unmodifiableList(succBlocks));
            }
        }
        blocks = Collections.unmodifiableList(blockList);
        heads = Collections.unmodifiableList(heads);
        
        if (tails.size() == 0) {
            tails = Collections.EMPTY_LIST;
        } else {
            tails = Collections.unmodifiableList(tails);
        }
        return instToBlock;
    }
    
    private void addBlock(InstructionHandle head, InstructionHandle tail, int idx, 
            int len, List<Block> blockList, Map<InstructionHandle, Block> instToBlock) {
        Block block = new Block(head, tail, method, idx, len, this);
        blockList.add(block);
        instToBlock.put(tail, block);
        instToBlock.put(head, block);
    }
    
    public MethodGen getMethod() {
        return method;
    }
    
    public List<Block> getBlocks() {
        return blocks;
    }
    
    public String toString() {
        Iterator<Block> it = blocks.iterator();
        StringBuilder sb = new StringBuilder();
        
        while(it.hasNext()) {
            Block block = it.next();
            sb.append(block.toString(true)).append("\n");
        }
        return sb.toString();
    }
    
    public List<Block> getHeads() {
        return heads;
    }
    
    public List<Block> getTails() {
        return tails;
    }
    
    public List<Block> getPreds(Block b) {
        return b.getPreds();
    }
    
    public List<Block> getSuccs(Block b) {
        return b.getSuccs();
    }
    
    public int size() {
        return blocks.size();
    }
    
    public Iterator<Block> iterator() {
        return blocks.iterator();
    }
}
