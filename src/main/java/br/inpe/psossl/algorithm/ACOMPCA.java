package br.inpe.psossl.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.inpe.psossl.algorithm.model.ACONode;
import br.inpe.psossl.algorithm.model.Particle;
import br.inpe.psossl.model.Container;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.model.Solution;

public class ACOMPCA extends MPCA {

	private List<ACONode>	nodes;
	public static int		n;
	public static double	ALPHA	= 1;
	public static double	BETA	= 1;
	public static double	RO		= 0.9;
	public static double	Q0		= 0.2;

	public ACOMPCA(Container container, List<Equipment> items) {
		super(container, items);
		SIGLA = "ACO+MPCA";
		NOME = "Ant Colony Optimization with Multi-Particle Collision Algorithm";

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
		
		

		updateMessage("Inicializando parâmetros e a matriz de feromônio.");
		nodes = new ArrayList<ACONode>();
		for (Equipment equipment : items)// Cria os vértices do grafo
		{
			nodes.add(new ACONode(equipment));
		}
		Collections.sort(nodes);// ordena os vértices do grafo por
								// desejabilidade
		n = nodes.size();
		for (ACONode node1 : nodes)// cria as arestas do grafo
		{
			for (ACONode node2 : nodes) {
				if (node1 != node2) {
					node1.addEdge(node2);
				}
			}
		}

		

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

		for (int i = 0; i < MAX; i++) {
			if (isCancelled()) {
				return;
			}

			Solution solution = this.generateRandomSolution();

			if (worstSolution == null || worstSolution.getFitness() > solution.getFitness()) {
				worstSolution = solution;
			}

			if (bestSolution == null || bestSolution.getFitness() < solution.getFitness()) {
				bestSolution = solution;
				updateMessage("Solução Encontrada! " + bestSolution);
			}

			updateProgress(i, MAX);
			updateMessage(String.format("Iteração %d de %d <-> Melhor solução %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.m2}", i, MAX, bestSolution.getFitness(),
					bestSolution.getMassCenter(), bestSolution.getMassCenterX() - bestSolution.getContainer().getWidth() / 2, bestSolution.getMassCenterY() - bestSolution.getContainer().getHeight()
							/ 2, bestSolution.getMomentOfInertia()));
		}

		updateMessage("nologscreenParâmetro utilizados:" + "\n              SEED = " + SEED + "\n         ITERAÇÕES = " + MAX + "\n          LAMBDA1  = " + Solution.LAMBDA1
				+ "\n          LAMBDA2  = " + Solution.LAMBDA2 + "\nMelhor solução encontrada: " + bestSolution);
		updateProgress(MAX, MAX);
		try {
			Thread.sleep(500);
		} catch (InterruptedException ex) {
			Logger.getLogger(ACOMPCA.class.getName()).log(Level.SEVERE, null, ex);
		}
		updateMessage(String.format("Execução finalizada! <-> Melhor solução %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.cm2}", bestSolution.getFitness(),
				bestSolution.getMassCenter(), bestSolution.getMassCenterX() - bestSolution.getContainer().getWidth() / 2, bestSolution.getMassCenterY() - bestSolution.getContainer().getHeight() / 2,
				bestSolution.getMomentOfInertia()));

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
