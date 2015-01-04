package br.inpe.psossl.model;

public class Constraint {

	private Equipment	equipment1;
	private Equipment	equipment2;
	private Type		type;
	private double		distance;
	private int			face;
	
	private Equipment mainEquipment;

	public enum Type {
		Min, Max, Face
	}

	public Constraint(Equipment equipment, Equipment reference, Type type, double distance, int face) {
		this.equipment1 = equipment;
		this.equipment2 = reference;
		this.type = type;
		this.distance = distance;
		this.face = face;
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getFace() {
		return face;
	}

	public void setFace(int face) {
		this.face = face;
	}

	public String getValue() {
		if (type == Type.Min)
			return "> " + distance;
		else if (type == Type.Max)
			return "< " + distance;
		else if (type == Type.Face)
			return "Face " + face;
		else
			return "";
	}
	
	public String getReference(){
		if(type == Type.Face)
			return " - ";
		if(equipment1.equals(mainEquipment))
			return equipment2.getId() + ": " + equipment2.getWidth() + ", " + equipment2.getHeight() + " - " + equipment2.getMass();
		else if(equipment2.equals(mainEquipment))
			return equipment1.getId() + ": " + equipment1.getWidth() + ", " + equipment1.getHeight() + " - " + equipment1.getMass();
		return "";
	}

	public void setMainEquipment(Equipment equipment) {
		this.mainEquipment = equipment;
	}

}
