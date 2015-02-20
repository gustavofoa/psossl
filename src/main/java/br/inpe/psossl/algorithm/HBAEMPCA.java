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

public class HBAEMPCA extends HBAE {

	public HBAEMPCA(Container container, List<Equipment> items, Type type) {
		super(container, items, type);
		SIGLA = "HBAE+MPCA";
		NOME = "Multi-Particle Collision Algorithm With HBAE";

		// Perturbar o equipamento num espaço de 1% do container
		MPCA.PerturbationXLimit = container.getWidth() * 0.01;
		MPCA.PerturbationYLimit = container.getHeight() * 0.01;
		// Perturbar o equipamento num espaço de 0,1% do container
		MPCA.SmallPerturbationXLimit = container.getWidth() * 0.001;
		MPCA.SmallPerturbationYLimit = container.getHeight() * 0.001;

	}

	@Override
	public void execute() {
		

		// HBAE
		// limpa matriz de afinidades
		for (Equipment equipment : items)
			equipment.getRelationships().clear();
		// cria matriz de afinidades
		for (Equipment equipment1 : items)
			for (Equipment equipment2 : items)
				if (equipment1 != equipment2)
					equipment1.addRelationship(equipment2);


		for (int i = 0; i < MAX; i++) {

			List<Particle> particles = new ArrayList<Particle>();

			for (int j = 0; j < MPCA.M; j++) {
				Particle particle = new Particle(new Random(RANDOM.nextInt()), this);
				particle.start();
				particles.add(particle);
			}

			for (Particle particle : particles) {
				try {
					particle.join();
				} catch (InterruptedException e) {
				}
			}
			

			// atualiza afinidade entre equipamentos da melhor solução if
			for (Equipment equipment : bestSolution.getItems())
				equipment.updateEquipmentRelationship(container);


			updateProgress(i, MAX);
			updateMessage(String.format("Iteração %d de %d <-> Melhor solução %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.m2}", 
					i, MAX, bestSolution.getFitness(),
					bestSolution.getMassCenter(), bestSolution.getMassCenterX() - bestSolution.getContainer().getWidth() / 2, 
					bestSolution.getMassCenterY() - bestSolution.getContainer().getHeight() / 2, 
					bestSolution.getMomentOfInertia()));
		}

		updateMessage("nologscreenParâmetro utilizados:" + 
		"\n              SEED = " + SEED + 
		"\n         ITERAÇÕES = " + MAX + 
		"\n        PARTÍCULAS = " + MPCA.M + 
		"\n          LAMBDA1  = " + Solution.LAMBDA1 + 
		"\n          LAMBDA2  = " + Solution.LAMBDA2 +
		"\nMelhor solução encontrada: " + bestSolution);
		updateProgress(MAX, MAX);
		try {
			Thread.sleep(500);
		} catch (InterruptedException ex) {
			Logger.getLogger(ACO.class.getName()).log(Level.SEVERE, null, ex);
		}
		updateMessage(String.format("Execução finalizada! <-> Melhor solução %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.cm2}", 
				bestSolution.getFitness(),
				bestSolution.getMassCenter(), 
 bestSolution.getMassCenterX() - bestSolution.getContainer().getWidth() / 2, bestSolution.getMassCenterY() - bestSolution.getContainer().getHeight() / 2,
				bestSolution.getMomentOfInertia()));

	}
	
	public Solution generateRandomSolution(){
		return this.generateHBAEBasedSolution();
	}

}
