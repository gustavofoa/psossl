package br.inpe.psossl.model;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.ArrayList;
import java.util.List;

import br.inpe.psossl.model.Constraint.Type;

public class Solution {

	private Container		container;
	private List<Equipment>	items;
	private double			massCenterX;
	private double			massCenterY;
	private double			massCenter;
	private double			momentOfInertia;
	public static double	LAMBDA1	= 1;
	public static double	LAMBDA2	= 1;

	public Solution(Container container, List<Equipment> items) {
		this.container = container;
		this.items = items;
	}

	public Solution(Container container, List<Equipment> items, boolean onlyFixeds) {
		this.container = container;
		this.items = new ArrayList<Equipment>();
		if (onlyFixeds)
			for (Equipment item : items)
				if (item.isFixed()) {
					this.items.add(item);
				}
	}

	public Container getContainer() {
		return container;
	}

	public List<Equipment> getItems() {
		return items;
	}

	public boolean validateParams() {

		if (container == null || items == null || items.isEmpty()) {
			return false;
		}

		if (!container.validateParams()) {
			return false;
		}

		for (Equipment equipment : items) {
			if (!equipment.validateParams()) {
				return false;
			}
		}

		return true;
	}

	public boolean validateAndAddItem(Equipment equipment, double x, double y, double angle, int face) {

		if (face != 1 && face != 2)
			return false;

		double h = equipment.getWidth(), v = equipment.getHeight();

		double teta = Math.toRadians(angle);

		// obtain the rectangle vertices
		double vertices[][] = new double[][] { new double[] { (cos(teta) * (-h / 2)) + (-sin(teta) * (-v / 2)) + x, (sin(teta) * (-h / 2)) + (cos(teta) * (-v / 2)) + y },
				new double[] { (cos(teta) * (h / 2)) + (-sin(teta) * (-v / 2)) + x, (sin(teta) * (h / 2)) + (cos(teta) * (-v / 2)) + y },
				new double[] { (cos(teta) * (h / 2)) + (-sin(teta) * (v / 2)) + x, (sin(teta) * (h / 2)) + (cos(teta) * (v / 2)) + y },
				new double[] { (cos(teta) * (-h / 2)) + (-sin(teta) * (v / 2)) + x, (sin(teta) * (-h / 2)) + (cos(teta) * (v / 2)) + y } };
		// validating if the rectangle is inside the container
		for (double[] vertice : vertices) {
			if (vertice[0] < 0 || vertice[1] < 0 || vertice[0] > container.getWidth() || vertice[1] > container.getHeight()) {
				return false;
			}
		}

		// validating intersection/overlap
		// how: finding a line that separates the rectangles
		for (Equipment item : items) {
			if (item.getFace() == face && !item.equals(equipment)) {
				double teta2 = Math.toRadians(item.getAngle());
				double v2 = item.getHeight(), h2 = item.getWidth();
				double vertices2[][] = new double[][] {
						new double[] { (cos(teta2) * (-h2 / 2)) + (-sin(teta2) * (-v2 / 2)) + item.getX(), (sin(teta2) * (-h2 / 2)) + (cos(teta2) * (-v2 / 2)) + item.getY() },
						new double[] { (cos(teta2) * (h2 / 2)) + (-sin(teta2) * (-v2 / 2)) + item.getX(), (sin(teta2) * (h2 / 2)) + (cos(teta2) * (-v2 / 2)) + item.getY() },
						new double[] { (cos(teta2) * (h2 / 2)) + (-sin(teta2) * (v2 / 2)) + item.getX(), (sin(teta2) * (h2 / 2)) + (cos(teta2) * (v2 / 2)) + item.getY() },
						new double[] { (cos(teta2) * (-h2 / 2)) + (-sin(teta2) * (v2 / 2)) + item.getX(), (sin(teta2) * (-h2 / 2)) + (cos(teta2) * (v2 / 2)) + item.getY() } };
				if (isPolygonsIntersecting(vertices, vertices2)) {
					return false;
				}

			}
		}

		// Constraints
		for (Constraint constraint : equipment.getConstraints()) {
			
			if (constraint.getType() == Type.Face && constraint.getFace() != face)
				return false;
			
			if(constraint.getType() == Type.Min){
				if(constraint.getEquipment1().equals(equipment) && constraint.getEquipment2().distanceTo(x, y) < constraint.getDistance())
					return false;
				if(constraint.getEquipment2().equals(equipment) && constraint.getEquipment1().distanceTo(x, y) < constraint.getDistance())
					return false;
			}

			if(constraint.getType() == Type.Max){
				if(constraint.getEquipment1().equals(equipment) && constraint.getEquipment2().distanceTo(x, y) > constraint.getDistance())
					return false;
				if(constraint.getEquipment2().equals(equipment) && constraint.getEquipment1().distanceTo(x, y) > constraint.getDistance())
					return false;
			}
			
		}

		Equipment equip = new Equipment(equipment.getId(), equipment.getWidth(), equipment.getHeight(), equipment.getMass(), equipment.getColor());
		equip.setX(x);
		equip.setY(y);
		equip.setAngle(angle);
		equip.setFace(face);
		equip.setFixed(equipment.isFixed());

		if (!this.getItems().contains(equip))
			this.getItems().add(equip);
		else {
			equipment.setX(x);
			equipment.setY(y);
			equipment.setAngle(angle);
			equipment.setFace(face);
		}

		return true;

	}

	private static boolean isPolygonsIntersecting(double[][] a, double[][] b) {
		Double minA, maxA, projected, minB, maxB;

		for (double[][] polygon : new double[][][] { a, b }) {

			// for each polygon, look at each edge of the polygon, and determine
			// if it separates
			// the two shapes
			for (int i1 = 0; i1 < polygon.length; i1++) {

				// grab 2 vertices to create an edge
				int i2 = (i1 + 1) % polygon.length;
				double[] p1 = polygon[i1];
				double[] p2 = polygon[i2];

				// find the line perpendicular to this edge
				double[] normal = new double[] { p2[1] - p1[1], p1[0] - p2[0] };

				// for each vertex in the first shape, project it onto the line
				// perpendicular to the edge
				// and keep track of the min and max of these values
				minA = maxA = null;
				for (int j = 0; j < a.length; j++) {
					projected = normal[0] * a[j][0] + normal[1] * a[j][1];
					if (minA == null || projected < minA) {
						minA = projected;
					}
					if (maxA == null || projected > maxA) {
						maxA = projected;
					}
				}

				// for each vertex in the second shape, project it onto the line
				// perpendicular to the edge
				// and keep track of the min and max of these values
				minB = maxB = null;
				for (int j = 0; j < b.length; j++) {
					projected = normal[0] * b[j][0] + normal[1] * b[j][1];
					if (minB == null || projected < minB) {
						minB = projected;
					}
					if (maxB == null || projected > maxB) {
						maxB = projected;
					}
				}

				// if there is no overlap between the projects, the edge we are
				// looking at separates the two
				// polygons, and we know there is no overlap
				if (maxA < minB || maxB < minA) {
					return false;
				}
			}
		}
		return true;
	}

	private Double	fitness;

	public double getFitness() {

		double sumMX = .0, sumMY = .0, sumM = .0;
		for (Equipment equipment : items) {
			sumMX += equipment.getMass() * (equipment.getX() - container.getWidth() / 2);
			sumMY += equipment.getMass() * (equipment.getY() - container.getHeight() / 2);
			sumM += equipment.getMass();
		}
		massCenterX = sumMX + container.getWidth() / 2;
		massCenterY = sumMY + container.getHeight() / 2;
		massCenter = Math.sqrt(Math.pow(sumMX / sumM, 2) + Math.pow(sumMY / sumM, 2));

		momentOfInertia = 0;
		double r;
		for (Equipment equipment : items) {
			r = Math.sqrt(Math.pow(equipment.getX() - container.getWidth() / 2, 2) + Math.pow(equipment.getY() - container.getHeight() / 2, 2));
			momentOfInertia += equipment.getMass() * Math.pow(r, 2);
		}

		fitness = LAMBDA2 * momentOfInertia - LAMBDA1 * massCenter;
		return fitness;
	}

	public double getMassCenterX() {
		return massCenterX;
	}

	public double getMassCenterY() {
		return massCenterY;
	}

	public double getMassCenter() {
		return massCenter;
	}

	public double getMomentOfInertia() {
		return momentOfInertia;
	}

	@Override
	public String toString() {
		return "Solution:[fitness=" + fitness + ", items:" + items + "]";
	}

}
