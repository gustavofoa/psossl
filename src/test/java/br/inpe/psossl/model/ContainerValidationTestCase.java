package br.inpe.psossl.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class ContainerValidationTestCase {

	@Test
	public void mustAcceptCorrectParams() {
		Container container = new Container(10, 10);
		assertTrue("Positive values for width and height must be valid!", container.validateParams());
	}

	@Test
	public void negativeWidthMustBeInvalid() {
		Container container = new Container(-1, 10);
		assertFalse("Negative value for width must be invalid!", container.validateParams());
	}

	@Test
	public void negativeHeightMustBeInvalid() {
		Container container = new Container(2,-1);
		assertFalse("Negative value for height must be invalid!", container.validateParams());
	}

	@Test
	public void zeroWidthMustBeInvalid() {
		Container container = new Container(0, 10);
		assertFalse("Negative value for width must be invalid!", container.validateParams());
	}

	@Test
	public void zeroHeightMustBeInvalid() {
		Container container = new Container(1, 0);
		assertFalse("Negative value for height must be invalid!", container.validateParams());
	}

}
