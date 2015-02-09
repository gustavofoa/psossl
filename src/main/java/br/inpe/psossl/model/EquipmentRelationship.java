package br.inpe.psossl.model;

import br.inpe.psossl.algorithm.HBAE;

public final class EquipmentRelationship {

	private Equipment	equipment1;
	private Equipment	equipment2;

	private double		sameQuadrant;
	private double		oppositeQuadrant;
	private double		neighborQuadrant;

	public EquipmentRelationship(Equipment equipment1, Equipment equipment2) {
		this.equipment1 = equipment1;
		this.equipment2 = equipment2;
		clearRelationship();
	}

	public void clearRelationship() {
		sameQuadrant = oppositeQuadrant = neighborQuadrant = 1;
	}

	public Equipment getEquipment1() {
		return equipment1;
	}

	public void setEquipment1(Equipment equipment1) {
		this.equipment1 = equipment1;
	}

	public Equipment getEquipment2() {
		return equipment2;
	}

	public void setEquipment2(Equipment equipment2) {
		this.equipment2 = equipment2;
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
		this.sameQuadrant *= HBAE.REINFORCE_RATE;
	}

	public void reinforceOppositeQuadrant() {
		decayRelashionship();
		this.oppositeQuadrant *= HBAE.REINFORCE_RATE;
	}

	public void reinforceNeighborQuadrant() {
		decayRelashionship();
		this.neighborQuadrant *= (((HBAE.REINFORCE_RATE - 1) / 2) + 1);
		// this.neighborQuadrant *= REINFORCE_RATE;
	}

	private void decayRelashionship() {
		this.sameQuadrant *= HBAE.DECAY_RATE;
		this.oppositeQuadrant *= HBAE.DECAY_RATE;
		this.neighborQuadrant *= HBAE.DECAY_RATE;
	}

	public Equipment getEquipment(Equipment from) {
		if (equipment1.equals(from))
			return equipment2;
		else if (equipment2.equals(from))
			return equipment1;
		else
			return null;
	}

}
