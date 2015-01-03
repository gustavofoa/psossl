package br.inpe.psossl.model;

public class Constraint {

	private Equipment	equipment1;
	private Equipment	equipment2;
	private Type		type;
	private double		distance;
	private int			face;

	public enum Type {
		Min, Max, Face
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

}
