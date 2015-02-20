package br.inpe.psossl.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.inpe.psossl.algorithm.model.Particle;
import br.inpe.psossl.model.Container;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.model.Solution;

public class MPCA extends OptimizationAlgorithm {

	public static int		MAX						= 1000;
	public static int		M						= 10;

	public static double	PerturbationXLimit		= 5;
	public static double	PerturbationYLimit		= 5;
	public static double	SmallPerturbationXLimit	= 1;
	public static double	SmallPerturbationYLimit	= 1;

	public MPCA(Container container, List<Equipment> items) {
		super(container, items);
		SIGLA = "MPCA";
		NOME = "Multi-Particle Collision Algorithm";

		// Perturbar o equipamento num espaço de 5% do container
		MPCA.PerturbationXLimit = container.getWidth() * 0.01;
		MPCA.PerturbationYLimit = container.getHeight() * 0.01;
		// Perturbar o equipamento num espaço de 1% do container
		MPCA.SmallPerturbationXLimit = container.getWidth() * 0.001;
		MPCA.SmallPerturbationYLimit = container.getHeight() * 0.001;

	}

	@Override
	public void execute() {
		

		int[][] seeds = new int[MAX][M];
		for (int i = 0; i < MAX; i++)
			for (int j = 0; j < M; j++)
				seeds[i][j] = RANDOM.nextInt();
		

		for (int i = 0; i < MAX; i++) {

			List<Particle> particles = new ArrayList<Particle>();
			

			for (int j = 0; j < M; j++) {
				Particle particle = new Particle(new Random(seeds[i][j]), this);
				particle.start();
				particles.add(particle);
			}

			for (Particle particle : particles) {
				try {
					particle.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			updateProgress(i, MAX);
			updateMessage(String.format("Iteração %d de %d <-> Melhor solução %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.m2}", i, MAX, bestSolution.getFitness(),
					bestSolution.getMassCenter(), bestSolution.getMassCenterX() - bestSolution.getContainer().getWidth() / 2, bestSolution.getMassCenterY() - bestSolution.getContainer().getHeight()
							/ 2, bestSolution.getMomentOfInertia()));
		}

		updateMessage("nologscreenParâmetro utilizados:" + "\n              SEED = " + SEED + "\n         ITERAÇÕES = " + MAX + "\n        PARTÍCULAS = " + MPCA.M + "\n          LAMBDA1  = "
				+ Solution.LAMBDA1 + "\n          LAMBDA2  = " + Solution.LAMBDA2 + "\nMelhor solução encontrada: " + bestSolution);
		updateProgress(MAX, MAX);
		try {
			Thread.sleep(500);
		} catch (InterruptedException ex) {
			Logger.getLogger(ACO.class.getName()).log(Level.SEVERE, null, ex);
		}
		updateMessage(String.format("Execução finalizada! <-> Melhor solução %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.cm2}", bestSolution.getFitness(),
				bestSolution.getMassCenter(), bestSolution.getMassCenterX() - bestSolution.getContainer().getWidth() / 2, bestSolution.getMassCenterY() - bestSolution.getContainer().getHeight() / 2,
				bestSolution.getMomentOfInertia()));

	}

}
