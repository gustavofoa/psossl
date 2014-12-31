package br.inpe.psossl.model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

public class Equipment {
	
	// constant attributes
	private double						width;
	private double						height;
	private double						mass;
	// variable attributes
	private double						x;
	private double						y;
	private double						angle;
	private int							face;
	private boolean						fixed;
	private Color						color;
	private int							id;
	private static int					maxID	= 0;
	private List<EquipmentRelationship>	relationships;
	
	public Equipment(int id, double width, double height, double mass) {
		super();
		relationships = new ArrayList<EquipmentRelationship>();
		updateParams(width, height, mass, null);
		
		this.id = id;
	}
	
	public Equipment(double width, double height, double mass) {
		this(++maxID, width, height, mass);
	}
	
	public Equipment(int id, double width, double height, double mass, Color color) {
		super();
		relationships = new ArrayList<EquipmentRelationship>();
		updateParams(width, height, mass, color);
		this.id = id;
	}
	
	public Equipment(double width, double height, double mass, Color color) {
		this(++maxID, width, height, mass, color);
	}
	
	public final void updateParams(double width, double height, double mass, Color color) {
		this.width = width;
		this.height = height;
		this.mass = mass;
		this.color = color;
		if(face==0)
			face = 1;
	}
	
	public List<EquipmentRelationship> getRelationships() {
		return this.relationships;
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		if (!fixed) {
			this.x = x;
		}
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		if (!fixed) {
			this.y = y;
		}
	}
	
	public double getAngle() {
		return angle;
	}
	
	public void setAngle(double angle) {
		if (!fixed) {
			this.angle = angle;
		}
	}
	
	public int getFace() {
		return face;
	}
	
	public void setFace(int face) {
		if (!fixed) {
			this.face = face;
		}
	}
	
	public double getWidth() {
		return width;
	}
	
	public double getHeight() {
		return height;
	}
	
	public double getMass() {
		return mass;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public boolean isFixed() {
		return fixed;
	}
	
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	
	public boolean validateParams() {
		
		if (width <= 0 || height <= 0) {
			return false;
		}
		
		if (mass <= 0) {
			return false;
		}
		
		return true;
		
	}
	
	@Override
	public String toString() {
		return "Equipment - " + id + ":[Width:" + width + ", Height:" + height + ", Mass:" + mass + ", Pos=[" + x + ", " + y + ", " + angle + ", "
				+ face + "]]";
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + (int) (Double.doubleToLongBits(this.width) ^ (Double.doubleToLongBits(this.width) >>> 32));
		hash = 37 * hash + (int) (Double.doubleToLongBits(this.height) ^ (Double.doubleToLongBits(this.height) >>> 32));
		hash = 37 * hash + (int) (Double.doubleToLongBits(this.mass) ^ (Double.doubleToLongBits(this.mass) >>> 32));
		hash = 37 * hash + this.id;
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Equipment other = (Equipment) obj;
		if (Double.doubleToLongBits(this.width) != Double.doubleToLongBits(other.width)) {
			return false;
		}
		if (Double.doubleToLongBits(this.height) != Double.doubleToLongBits(other.height)) {
			return false;
		}
		if (Double.doubleToLongBits(this.mass) != Double.doubleToLongBits(other.mass)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		return true;
	}
	
	public int getID() {
		return this.id;
	}
}
