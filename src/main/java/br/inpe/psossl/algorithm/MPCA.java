package br.inpe.psossl.algorithm;

import br.inpe.psossl.algorithm.model.Particle;
import br.inpe.psossl.model.Container;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.model.Solution;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MPCA extends OptimizationAlgorithm {
	
	public static int	MAX	= 1000;
	public static int	M	= 100;
	
	public MPCA(Container container, List<Equipment> items) {
		super(container, items);
		SIGLA = "MPCA";
		NOME = "Multi-Particle Collision Algorithm";
	}
	
	@Override
	public void execute() {
		
		List<Particle> particles = new ArrayList<Particle>();
		
		for (int i = 0; i < M; i++) {
			Particle particle = new Particle(i + 1, new Random(RANDOM.nextInt()), this);
			particle.start();
			particles.add(particle);
		}
		
		updateMessage("Parâmetro utilizados:" + "\n              SEED = " + SEED + "\n         ITERAÇÕES = " + MAX + "\n        PARTÍCULAS = " + M
				+ "\n          LAMBDA1  = " + Solution.LAMBDA1 + "\n          LAMBDA2  = " + Solution.LAMBDA2 + "\nMelhor solução encontrada: "
				+ bestSolution);
		updateProgress(MAX, MAX);
		try {
			Thread.sleep(500);
		} catch (InterruptedException ex) {
			Logger.getLogger(ACOAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
		}
		updateMessage(String.format(
				"Execução finalizada! <-> Melhor solução %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.cm2}",
				bestSolution.getFitness(), bestSolution.getMassCenter(), bestSolution.getMassCenterX() - bestSolution.getContainer().getWidth() / 2,
				bestSolution.getMassCenterY() - bestSolution.getContainer().getHeight() / 2, bestSolution.getMomentOfInertia()));
		
	}
	
}
