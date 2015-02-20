package br.inpe.psossl.algorithm.model;

import static br.inpe.psossl.algorithm.MPCA.MAX;

import java.util.ArrayList;
import java.util.Random;

import br.inpe.psossl.algorithm.MPCA;
import br.inpe.psossl.algorithm.OptimizationAlgorithm;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.model.Solution;

public class Particle extends Thread {

	private Random					RANDOM;
	private Solution				oldSolution;
	private Solution				newSolution;
	private Solution				bestSolution;
	private Solution				worstSolution;
	private OptimizationAlgorithm	mpca;

	public Particle(Random random, OptimizationAlgorithm mpca) {
		this.RANDOM = random;
		this.mpca = mpca;
	}

	@Override
	public void run() {

		bestSolution = mpca.bestSolution;
		worstSolution = mpca.worstSolution;

		oldSolution = mpca.generateRandomSolution(RANDOM);
		if (bestSolution == null)
			bestSolution = oldSolution;
		if (worstSolution == null)
			worstSolution = oldSolution;
		if (mpca.bestSolution == null)
			mpca.bestSolution = oldSolution;
		if (mpca.worstSolution == null)
			mpca.worstSolution = oldSolution;

		perturbation();
		if (newSolution.getFitness() > oldSolution.getFitness()) {
			if (newSolution.getFitness() > bestSolution.getFitness()) {
				bestSolution = newSolution;
			}
			oldSolution = newSolution;
			exploration();
		} else {
			if (newSolution.getFitness() < mpca.worstSolution.getFitness()) {
				mpca.worstSolution = newSolution;
			}
			scattering();
		}

		if (bestSolution.getFitness() > mpca.bestSolution.getFitness()) {
			mpca.bestSolution = bestSolution;
			mpca.updateMessage("Solução Encontrada! " + mpca.bestSolution);
		}
		if (worstSolution.getFitness() < mpca.worstSolution.getFitness()) {
			mpca.worstSolution = worstSolution;
		}

	}

	private void exploration() {
		for (int i = 0; i < MAX; i++) {
			smallPerturbation();
			if (newSolution.getFitness() > bestSolution.getFitness())
				bestSolution = newSolution;
			if (newSolution.getFitness() > oldSolution.getFitness())
				oldSolution = newSolution;
		}
	}

	private void scattering() {
		double pScattering = 1 - newSolution.getFitness() / mpca.bestSolution.getFitness();
		if (pScattering > RANDOM.nextDouble())
			oldSolution = mpca.generateRandomSolution(RANDOM);
		else
			exploration();
	}

	private void smallPerturbation() {

		newSolution = new Solution(mpca.getContainer(), new ArrayList<Equipment>());
		for (Equipment equipment : oldSolution.getItems()) {

			Equipment equip = new Equipment(equipment.getId(), equipment.getWidth(), equipment.getHeight(), equipment.getMass(), equipment.getColor(), equipment.getRelationships());
			equip.setX(equipment.getX());
			equip.setY(equipment.getY());
			equip.setAngle(equipment.getAngle());
			equip.setFace(equipment.getFace());
			equip.setFixed(equipment.isFixed());
			equip.getConstraints().addAll(equipment.getConstraints());
			newSolution.getItems().add(equip);

		}

		for (Equipment equipment : newSolution.getItems()) {
			smallPerturbationAlocate(equipment);
		}

	}

	private void smallPerturbationAlocate(Equipment equipment) {
		double x, y, angle;
		int face;

		x = (RANDOM.nextDouble() * 2 * MPCA.SmallPerturbationXLimit - MPCA.SmallPerturbationXLimit) + equipment.getX();
		y = (RANDOM.nextDouble() * 2 * MPCA.SmallPerturbationYLimit - MPCA.SmallPerturbationYLimit) + equipment.getY();
		angle = equipment.getAngle();
		face = equipment.getFace();

		newSolution.validateAndAddItem(equipment, x, y, angle, face);
	}

	private void perturbation() {

		newSolution = new Solution(mpca.getContainer(), new ArrayList<Equipment>());
		for (Equipment equipment : oldSolution.getItems()) {

			Equipment equip = new Equipment(equipment.getId(), equipment.getWidth(), equipment.getHeight(), equipment.getMass(), equipment.getColor(), equipment.getRelationships());
			equip.setX(equipment.getX());
			equip.setY(equipment.getY());
			equip.setAngle(equipment.getAngle());
			equip.setFace(equipment.getFace());
			equip.setFixed(equipment.isFixed());
			equip.getConstraints().addAll(equipment.getConstraints());
			newSolution.getItems().add(equip);

		}

		for (Equipment equipment : newSolution.getItems()) {
			perturbationAlocate(equipment);
		}

	}

	private void perturbationAlocate(Equipment equipment) {
		double x, y, angle;
		int face = 0;

		x = (RANDOM.nextDouble() * 2 * MPCA.PerturbationXLimit - MPCA.PerturbationXLimit) + equipment.getX();
		y = (RANDOM.nextDouble() * 2 * MPCA.PerturbationYLimit - MPCA.PerturbationYLimit) + equipment.getY();
		angle = RANDOM.nextDouble() * 360;
		face = RANDOM.nextInt(2) + 1;

		if (!newSolution.validateAndAddItem(equipment, x, y, angle, face))
			smallPerturbationAlocate(equipment);
	}

}
