package br.inpe.psossl.algorithm.model;

import static br.inpe.psossl.algorithm.MPCA.MAX;

import java.util.Random;

import br.inpe.psossl.algorithm.MPCA;
import br.inpe.psossl.algorithm.OptimizationAlgorithm;
import br.inpe.psossl.model.Solution;

public class Particle extends Thread {

	private Random		RANDOM;
	private Solution	oldSolution;
	private Solution	newSolution;
	private int			numero;
	private MPCA		mpca;

	public Particle(int numero, Random random, MPCA mpca) {
		this.numero = numero;
		this.RANDOM = random;
		this.mpca = mpca;
	}

	@Override
	public void run() {
		mpca.updateMessage("Gerando solução inicial...");
		mpca.worstSolution = mpca.bestSolution = oldSolution = mpca.generateRandomSolution();

		for (int i = 0; i < MAX; i++) {
			perturbation();
			if (newSolution.getFitness() > oldSolution.getFitness()) {
				if (newSolution.getFitness() > mpca.bestSolution.getFitness()) {
					mpca.bestSolution = newSolution;
					mpca.updateMessage("Solução Encontrada! " + mpca.bestSolution);
				}
				oldSolution = newSolution;
				exploration();
			} else {
				if (newSolution.getFitness() < mpca.worstSolution.getFitness()) {
					mpca.worstSolution = newSolution;
				}
				scattering();
			}

			// updateProgress(i, MAX);
			mpca.updateMessage(String.format("Iteração %d de %d Partícula=%d <-> Melhor solução %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.m2}", i, MAX,
					this.numero, mpca.bestSolution.getFitness(), mpca.bestSolution.getMassCenter(), mpca.bestSolution.getMassCenterX() - mpca.bestSolution.getContainer().getWidth() / 2,
					mpca.bestSolution.getMassCenterY() - mpca.bestSolution.getContainer().getHeight() / 2, mpca.bestSolution.getMomentOfInertia()));
		}
	}

	private void smallPerturbation() {

	}

	private void perturbation() {

	}

	private void exploration() {
		for (int i = 0; i < MAX; i++) {
			smallPerturbation();
			if (newSolution.getFitness() > oldSolution.getFitness())
				oldSolution = newSolution;
		}
	}

	private void scattering() {
		double pScattering = 1 - newSolution.getFitness() / mpca.bestSolution.getFitness();
		if (pScattering > OptimizationAlgorithm.RANDOM.nextDouble())
			oldSolution = mpca.generateRandomSolution();
		else
			exploration();
	}

}
