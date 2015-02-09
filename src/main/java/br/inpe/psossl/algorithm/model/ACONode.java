package br.inpe.psossl.algorithm.model;

import br.inpe.psossl.model.EquipmentRelationship;
import br.inpe.psossl.model.Equipment;
import java.util.ArrayList;
import java.util.List;

public class ACONode implements Comparable<ACONode>{

    private Equipment equipment;
    private double desirability;//defini esta variável no nó de destino, pois ela só leva em consideração o nó de destino.
    private List<ACOEdge> edges;
    private boolean visited;
    
    public ACONode(Equipment equipment){
        this.equipment = equipment;
        desirability = equipment.getMass()*equipment.getWidth()*equipment.getHeight();
        edges = new ArrayList<ACOEdge>();
        visited=false;
        for(EquipmentRelationship relationship : equipment.getRelationships())
            relationship.clearRelationship();
    }

    public int compareTo(ACONode o2) {
        return desirability < o2.desirability? 1 : desirability==o2.desirability ? 0 : -1;
    }

    public void addEdge(ACONode node) {
        for(ACOEdge edge : edges)
            if(edge.getNode1() == node || edge.getNode2() == node)
                return;
        ACOEdge edge = new ACOEdge(this, node);
        addEdge(edge);
        node.addEdge(edge);
        
    }
    
    private void addEdge(ACOEdge edge) {
        edges.add(edge);
    }
    
    public Equipment getEquipment(){
        return equipment;
    }
    
    public double getDesirability(){
        return desirability;
    }
    
    public List<ACOEdge> getEdges(){
        return edges;
    }
    
    public boolean isVisited(){
        return visited;
    }
    
    public void setVisited(boolean visited){
        this.visited = visited;
    }
    
    @Override
    public String toString(){
        String txt = "Node:["+equipment+", Desirability:"+desirability+", Edges:\n[\n";
        for(ACOEdge edge : edges)
        txt+="\t"+edge+"\n";
        txt+="]";
        return txt;
    }

}
