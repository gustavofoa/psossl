package br.inpe.psossl.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class EquipmentValidationTestCase {

	@Test
	public void mustAcceptCorrectParams() {
		Equipment equipment = new Equipment(10, 10, 2);
		assertTrue("Positive values for width, height and mass must be valid!", equipment.validateParams());
	}

	@Test
	public void negativeWidthMustBeInvalid() {
		Equipment equipment = new Equipment(-1, 10, 2);
		assertFalse("Negative value for width must be invalid!", equipment.validateParams());
	}

	@Test
	public void negativeHeightMustBeInvalid() {
		Equipment equipment = new Equipment(10, -10, 2);
		assertFalse("Negative value for height must be invalid!", equipment.validateParams());
	}

	@Test
	public void negativeMassMustBeInvalid() {
		Equipment equipment = new Equipment(10, 10, -2);
		assertFalse("Negative value for mass must be invalid!", equipment.validateParams());
	}

	@Test
	public void zeroWidthMustBeInvalid() {
		Equipment equipment = new Equipment(0, 10, 2);
		assertFalse("Negative value for width must be invalid!", equipment.validateParams());
	}

	@Test
	public void zeroHeightMustBeInvalid() {
		Equipment equipment = new Equipment(10, 0, 2);
		assertFalse("Negative value for height must be invalid!", equipment.validateParams());
	}

	@Test
	public void zeroMassMustBeInvalid() {
		Equipment equipment = new Equipment(10, 2, 0);
		assertFalse("Negative value for mass must be invalid!", equipment.validateParams());
	}

}
