package br.inpe.psossl.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SolutionValidation {
	
	@Test
	public void mustAcceptValidSolution() {
		
		Container container = new Container(10, 10);
		
		Equipment equipment1 = new Equipment(1, 1, 1);
		equipment1.setX(-1);
		equipment1.setY(-1);
		
		Equipment equipment2 = new Equipment(1, 1, 1);
		equipment1.setX(-1);
		equipment1.setY(-1);
		
		List<Equipment> items = new ArrayList<Equipment>();
		items.add(equipment1);
		items.add(equipment2);
		
		Solution solution = new Solution(container, items);
		assertTrue("Must accept valid solution.", solution.validateAndAddItem(equipment1, 2, 2, 0, 1));
		
	}
	
	@Test
	public void mustNotAcceptItemOutOfContainer() {
		
		Container container = new Container(10, 10);
		
		Equipment equipment1 = new Equipment(1, 1, 1);
		equipment1.setX(8);
		equipment1.setY(8);
		
		Equipment equipment2 = new Equipment(1, 1, 1);
		equipment2.setX(4);
		equipment2.setY(4);
		
		List<Equipment> items = new ArrayList<Equipment>();
		items.add(equipment1);
		items.add(equipment2);
		
		Solution solution = new Solution(container, items);
		
		assertFalse("Must not accept item with negative X.", solution.validateAndAddItem(equipment1, -1, 2, 0, 1));
		assertFalse("Must not accept item with negative Y.", solution.validateAndAddItem(equipment1, 1, -1, 0, 2));
		assertFalse("Must not accept item int (0,0) position.", solution.validateAndAddItem(equipment1, 0, 0, 0, 2));
		assertFalse("Must not accept item passing the container boundaries.", solution.validateAndAddItem(equipment1, 0.5, 0.4, 0, 1));
		assertFalse("Must not accept item passing the container boundaries.", solution.validateAndAddItem(equipment1, 0.4, 1, 0, 1));
		assertFalse("Must not accept item passing the container boundaries.", solution.validateAndAddItem(equipment1, 1, 9.6, 0, 2));
		assertFalse("Must not accept touching the boundary, but inclinated.", solution.validateAndAddItem(equipment1, 9.5, 9.5, 1, 1));
		assertFalse("Must not accept item passing the container boundaries.", solution.validateAndAddItem(equipment1, 0.7, 5, 45, 2));
		
	}

	@Test
	public void mustNotAcceptItemOverOther() {
		
		Container container = new Container(10, 10);
		
		Equipment equipment1 = new Equipment(1, 1, 1);
		equipment1.setX(5);
		equipment1.setY(5);
		
		Equipment equipment2 = new Equipment(1, 1, 1);
		equipment2.setX(4);
		equipment2.setY(4);
		
		List<Equipment> items = new ArrayList<Equipment>();
		items.add(equipment1);
		items.add(equipment2);
		
		Solution solution = new Solution(container, items);
		
		assertFalse("Must not accept item touching.", solution.validateAndAddItem(equipment2, 4, 4, 0, 1));
		
		assertTrue("Must accept item almost touching. Inclinated 1º.", solution.validateAndAddItem(equipment2, 4, 4, 1, 1));
		
	}

	@Test
	public void mustNotAcceptItemOutOfTwoFaces() {
		
		Container container = new Container(10, 10);
		
		Equipment equipment1 = new Equipment(1, 1, 1);
		equipment1.setX(5);
		equipment1.setY(5);
		
		Equipment equipment2 = new Equipment(1, 1, 1);
		equipment2.setX(4);
		equipment2.setY(4);
		
		List<Equipment> items = new ArrayList<Equipment>();
		items.add(equipment1);
		items.add(equipment2);
		
		Solution solution = new Solution(container, items);
		
		assertFalse("Must not accept item in face 0.", solution.validateAndAddItem(equipment2, 2, 2, 0, 0));
		
		assertFalse("Must not accept item in face 3.", solution.validateAndAddItem(equipment2, 2, 2, 0, 3));
		
		assertTrue("Must accept item in face 1.", solution.validateAndAddItem(equipment2, 2, 2, 0, 1));
		
		assertTrue("Must accept item in face 2.", solution.validateAndAddItem(equipment2, 2, 2, 0, 2));
		
	}

	@Test
	public void mustNotAcceptItemsOverlap() {
		
		Container container = new Container(100, 100);
		
		Equipment equipment1 = new Equipment(10, 8, 2);
		equipment1.setX(88.02897618378704);
		equipment1.setY(87.26589008739934);
		equipment1.setAngle(94.38259271000504);
		equipment1.setFace(2);
		
		Equipment equipment2 = new Equipment(8, 13, 1.5);
		equipment2.setX(80.65598867693316);
		equipment2.setY(92.4935420688271);
		equipment2.setAngle(11.899662790764648);
		equipment2.setFace(2);
		
		List<Equipment> items = new ArrayList<Equipment>();
		items.add(equipment1);
		//items.add(equipment2);
		
		Solution solution = new Solution(container, items);
		
		assertFalse("Must not accept item overlaping.", solution.validateAndAddItem(equipment2, 80.65598867693316, 92.4935420688271, 11.899662790764648, 2));
		
	}
	
}
