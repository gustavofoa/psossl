package br.inpe.psossl.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FitnessCalculationTestCase {

	@Test
	public void twoEquipmentsWithSameMassAndOpositePosition() {
		
		//O zero é na ponta inferior esquerda 
		
		Equipment equipment1 = new Equipment(1, 1, 1);
		equipment1.setX(6);
		equipment1.setY(5);

		Equipment equipment2 = new Equipment(1, 1, 1);
		equipment2.setX(4);
		equipment2.setY(5);
		
		List<Equipment> items = new ArrayList<Equipment>();
		items.add(equipment1);
		items.add(equipment2);
		
		Solution solution = new Solution(new Container(10, 10), items);
		
		//Center of Mass = 0
		//Moment of Inertia = (1 * 1^2) * 2 = 2
		//Fitness = 2 - 0
		assertEquals(2, Math.round(solution.getFitness()));

		assertEquals(0, Math.round(solution.getMassCenter()));
		assertEquals(2, Math.round(solution.getMomentOfInertia()));
		
	}

	@Test
	public void fourEquipmentsWithSameMassAndOpositePosition() {
		
		//O zero é na ponta inferior esquerda 
		
		Equipment equipment1 = new Equipment(1, 1, 1);
		equipment1.setX(6);
		equipment1.setY(5);

		Equipment equipment2 = new Equipment(1, 1, 1);
		equipment2.setX(4);
		equipment2.setY(5);

		Equipment equipment3 = new Equipment(1, 1, 1);
		equipment3.setX(5);
		equipment3.setY(6);

		Equipment equipment4 = new Equipment(1, 1, 1);
		equipment4.setX(5);
		equipment4.setY(4);
		
		List<Equipment> items = new ArrayList<Equipment>();
		items.add(equipment1);
		items.add(equipment2);
		items.add(equipment3);
		items.add(equipment4);
		
		Solution solution = new Solution(new Container(10, 10), items);
		
		//Center of Mass = 0
		//Moment of Inertia = (1 * 1^2) * 4 = 2
		//Fitness = 4 - 0
		assertEquals(4, Math.round(solution.getFitness()));

		assertEquals(0, Math.round(solution.getMassCenter()));
		assertEquals(4, Math.round(solution.getMomentOfInertia()));
		
	}

	@Test
	public void treeEquipmentsWithSameMassAndOpositePosition() {
		
		//O zero é na ponta inferior esquerda 
		
		Equipment equipment1 = new Equipment(1, 1, 1);
		equipment1.setX(6);
		equipment1.setY(5);

		Equipment equipment2 = new Equipment(1, 1, 1);
		equipment2.setX(4);
		equipment2.setY(5);

		Equipment equipment3 = new Equipment(1, 1, 1);
		equipment3.setX(5);
		equipment3.setY(8);

		List<Equipment> items = new ArrayList<Equipment>();
		items.add(equipment1);
		items.add(equipment2);
		items.add(equipment3);
		
		Solution solution = new Solution(new Container(10, 10), items);
		
		//Center of Mass = 1
		//Moment of Inertia = (1 * 1^2) * 2 + (1 * 3^2) = 2 + 9
		//Fitness = 11 - 1
		assertEquals(10, Math.round(solution.getFitness()));

		assertEquals(1, Math.round(solution.getMassCenter()));
		assertEquals(11, Math.round(solution.getMomentOfInertia()));
		
	}

	@Test
	public void oneEquipmentsInCenter() {
		
		//O zero é na ponta inferior esquerda 
		
		Equipment equipment1 = new Equipment(1, 1, 1);
		equipment1.setX(5);
		equipment1.setY(5);

		List<Equipment> items = new ArrayList<Equipment>();
		items.add(equipment1);
		
		Solution solution = new Solution(new Container(10, 10), items);
		
		//Center of Mass = 0
		//Moment of Inertia = 0
		assertEquals(0, Math.round(solution.getFitness()));

		assertEquals(0, Math.round(solution.getMassCenter()));
		assertEquals(0, Math.round(solution.getMomentOfInertia()));
		
	}

	@Test
	public void oneEquipmentsNotInCenter() {
		
		//O zero é na ponta inferior esquerda 
		
		Equipment equipment1 = new Equipment(1, 1, 1);
		equipment1.setX(3);
		equipment1.setY(5);

		List<Equipment> items = new ArrayList<Equipment>();
		items.add(equipment1);
		
		Solution solution = new Solution(new Container(10, 10), items);
		
		//Center of Mass = 2
		//Moment of Inertia = 4
		assertEquals(2, Math.round(solution.getFitness()));

		assertEquals(2, Math.round(solution.getMassCenter()));
		assertEquals(4, Math.round(solution.getMomentOfInertia()));
		
	}

}
