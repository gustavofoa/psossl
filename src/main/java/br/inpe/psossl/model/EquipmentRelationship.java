
package br.inpe.psossl.model;

public final class EquipmentRelationship {
    
    private Equipment equipment;
    
    private double sameQuadrant;
    private double oppositeQuadrant;
    private double neighborQuadrant;
    public static double REINFORCE_RATE = 0.01;
    public static double DECAY_RATE = 0.01;
    
    public EquipmentRelationship(Equipment equipment){
        this.equipment = equipment;
        clearRelationship();
    }
    
    public void clearRelationship(){
        sameQuadrant = oppositeQuadrant = neighborQuadrant = ((double)1)/((double)3);
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public double getSameQuadrant() {
        return sameQuadrant;
    }

    public double getOppositeQuadrant() {
        return oppositeQuadrant;
    }

    public double getNeighborQuadrant() {
        return neighborQuadrant;
    }

    public void reinforceSameQuadrant() {
        decayRelashionship();
        this.sameQuadrant += REINFORCE_RATE;        
    }

    public void reinforceOppositeQuadrant() {
        decayRelashionship();
        this.oppositeQuadrant += REINFORCE_RATE;
    }

    public void reinforceNeighborQuadrant() {
        decayRelashionship();
        this.neighborQuadrant += REINFORCE_RATE;
    }
    
    private void decayRelashionship(){
        this.sameQuadrant -= DECAY_RATE;
        this.oppositeQuadrant -= DECAY_RATE;
        this.neighborQuadrant -= DECAY_RATE;
    }
    
}