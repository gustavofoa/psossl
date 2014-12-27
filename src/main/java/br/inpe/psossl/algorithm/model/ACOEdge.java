package br.inpe.psossl.algorithm.model;

import br.inpe.psossl.algorithm.ACOAlgorithm;

public class ACOEdge {
    
    private ACONode node1;
    private ACONode node2;
    private double pheromoneFromNode1;
    private double pheromoneFromNode2;
    
    public ACOEdge(ACONode node1, ACONode node2){
        this.node1 = node1;
        this.node2 = node2;
        this.pheromoneFromNode1 = (double) 1/ACOAlgorithm.n;
        this.pheromoneFromNode2 = (double) 1/ACOAlgorithm.n;
    }
    
    public void decayPheromoneFromNode(ACONode node){
        if(node == node1){
            pheromoneFromNode1 = verifyLimiting(ACOAlgorithm.RO * pheromoneFromNode1);
        }else if(node == node2){
            pheromoneFromNode2 = verifyLimiting(ACOAlgorithm.RO * pheromoneFromNode2);
        }
    }
    
    public void reinforcePheromoneFromNode(ACONode node){
        
        if(node == node1){//calcular o depósito de feromônio conforme [9] e [10]
            pheromoneFromNode1 += verifyLimiting((1 - ACOAlgorithm.RO) * pheromoneFromNode1);
        }else if(node == node2){
            pheromoneFromNode2 += verifyLimiting((1 - ACOAlgorithm.RO) * pheromoneFromNode1);
        }
        
    }

    private double verifyLimiting(double value){
        
        if(value < (double) 0.1/ACOAlgorithm.n)
            return (double) 0.1/ACOAlgorithm.n;
        if(value > (double) 10/ACOAlgorithm.n)
            return (double) 10/ACOAlgorithm.n;
        return value;
    }
    
    public ACONode getNode1() {
        return node1;
    }

    public void setNode1(ACONode node1) {
        this.node1 = node1;
    }

    public ACONode getNode2() {
        return node2;
    }

    public void setNode2(ACONode node2) {
        this.node2 = node2;
    }

    @Override
    public String toString(){
        return "Node1:"+node1.getDesirability()+", Node2:"+node2.getDesirability()+
                ", PheromoneToNode1:"+pheromoneFromNode1+", PheromoneToNode2:"+pheromoneFromNode2+"]";
    }

    public double getProbabilityFrom(ACONode lastNode) {
        if(node1==lastNode && node2.isVisited() || node2==lastNode && node1.isVisited()) 
            return 0;
        return 0.1;//calcular a probabilidade conforme eq. 8 dos chineses
    }
    
}
