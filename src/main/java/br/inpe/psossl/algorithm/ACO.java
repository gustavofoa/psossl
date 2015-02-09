package br.inpe.psossl.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.inpe.psossl.algorithm.model.ACOEdge;
import br.inpe.psossl.algorithm.model.ACONode;
import br.inpe.psossl.model.Container;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.model.Solution;

public class ACO extends OptimizationAlgorithm {

	private List<ACONode>	nodes;
	public static int		n;
	public static double	ALPHA	= 1;
	public static double	BETA	= 1;
	public static double	RO		= 0.9;
	public static double	Q0		= 0.2;
	public static int		MAX		= 1000;
	public static int		M		= 100;

	public ACO(Container container, List<Equipment> items) {
		super(container, items);
		SIGLA = "ACO";
		NOME = "Ant Colony Optimization";
	}

	@Override
	public void execute() {

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
			if (isCancelled()) {
				return;
			}

			Solution[] solutions = new Solution[M];
			List<ACONode>[] solutionsNodes = new ArrayList[M];

			for (int k = 0; k < M; k++) {

				// cria uma solução para a formiga k trabalhar
				solutions[k] = new Solution(container, items, true);
				List<ACONode> solutionNodes = solutionsNodes[k] = new ArrayList<ACONode>();
				ACONode firstNode = nodes.get(OptimizationAlgorithm.RANDOM.nextInt(n));
				firstNode.setVisited(true);
				solutionNodes.add(firstNode);// aloca a formiga em um nó
												// aleatoriamente
				addEquipmentInRandomPosition(solutions[k], firstNode.getEquipment());

				while (solutionNodes.size() < n) {

					ACONode lastNode = solutionNodes.get(solutionNodes.size() - 1);

					// obtém as probabilidades
					List<Double> probabilities = new ArrayList<Double>();
					int biggestProbIndex = 0;
					double biggestProb = 0;
					for (int j = 0; j < lastNode.getEdges().size(); j++) {
						double p = lastNode.getEdges().get(j).getProbabilityFrom(lastNode);
						if (p > biggestProb) {
							biggestProbIndex = j;
							biggestProb = p;
						}
						probabilities.add(p);
					}

					ACOEdge selectedEdge = null;

					// verifica o q0
					if (OptimizationAlgorithm.RANDOM.nextDouble() < Q0) {
						selectedEdge = lastNode.getEdges().get(biggestProbIndex);
					} else {
						double total = 0;
						for (Double prob : probabilities) {
							total += prob;
						}
						double rand = OptimizationAlgorithm.RANDOM.nextDouble() * total;
						for (int j = 0; j < probabilities.size(); j++) {
							if (rand > probabilities.get(j)) {
								rand -= probabilities.get(j);
								continue;
							}
							selectedEdge = lastNode.getEdges().get(j);
							break;
						}
					}

					// Caminha para o nó selecionado
					ACONode nextNode = selectedEdge.getNode1() == lastNode ? selectedEdge.getNode2() : selectedEdge.getNode1();
					solutionNodes.add(nextNode);
					nextNode.setVisited(true);

					// adicionar equipamento na solução
					try {
						addEquipmentInRandomPosition(solutions[k], nextNode.getEquipment());
					} catch (Exception e) {
						for (ACONode node : solutionNodes) {
							node.setVisited(false);
						}
						// cria uma solução para a formiga k trabalhar
						solutions[k] = new Solution(container, items, true);
						solutionNodes = solutionsNodes[k] = new ArrayList<ACONode>();
						firstNode = nodes.get(OptimizationAlgorithm.RANDOM.nextInt(n));
						firstNode.setVisited(true);
						solutionNodes.add(firstNode);// aloca a formiga em um nó
														// aleatoriamente
						addEquipmentInRandomPosition(solutions[k], firstNode.getEquipment());
					}

				}

				for (ACONode node : solutionNodes) {
					node.setVisited(false);
				}

			}

			// Update the pheromone
			for (ACONode node : solutionsNodes[0]) {
				for (ACOEdge edge : node.getEdges()) {
					edge.decayPheromoneFromNode(node);
				}
			}
			Solution iterationBestSolution = null;
			int k = 0;
			for (int z = 0; z < M; z++) {
				if (iterationBestSolution == null || iterationBestSolution.getFitness() < solutions[z].getFitness()) {
					iterationBestSolution = solutions[z];
					k = z;
				}
				if (worstSolution == null || worstSolution.getFitness() > solutions[z].getFitness()) {
					worstSolution = iterationBestSolution;
				}
			}
			if (bestSolution == null || bestSolution.getFitness() < iterationBestSolution.getFitness()) {
				bestSolution = iterationBestSolution;
				updateMessage("Solução Encontrada! " + bestSolution);
			}
			// deposita feromônio na melhor solução
			for (int j = 0; j < solutionsNodes[k].size() - 1; j++) {
				ACONode atual = solutionsNodes[k].get(j);
				ACONode nextNode = solutionsNodes[k].get(j + 1);
				for (ACOEdge edge : atual.getEdges()) {
					if (edge.getNode1() == nextNode || edge.getNode2() == nextNode) {
						edge.reinforcePheromoneFromNode(nextNode, false);
					}
				}
			}

			updateProgress(i, MAX);
			updateMessage(String.format("Iteração %d de %d <-> Melhor solução %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.m2}", i, MAX, bestSolution.getFitness(),
					bestSolution.getMassCenter(), bestSolution.getMassCenterX() - bestSolution.getContainer().getWidth() / 2, bestSolution.getMassCenterY() - bestSolution.getContainer().getHeight()
							/ 2, bestSolution.getMomentOfInertia()));
		}

		updateMessage("nologscreenParâmetro utilizados:" + "\n              SEED = " + SEED + "\n             ALPHA = " + ALPHA + "\n              BETA = " + BETA + "\nTaxa de Decaimento = " + RO
				+ "\n                q0 = " + Q0 + "\n         ITERAÇÕES = " + MAX + "\n          FORMIGAS = " + M + "\n          LAMBDA1  = " + Solution.LAMBDA1 + "\n          LAMBDA2  = "
				+ Solution.LAMBDA2 + "\nMelhor solução encontrada: " + bestSolution);
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
