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

public class ACOHBAE extends HBAE {

	private List<ACONode>	nodes;

	public ACOHBAE(Container container, List<Equipment> items, Type type) {
		super(container, items, type);
		SIGLA = "ACO+HBAE";
		NOME = "Ant Colony Optimization with HBAE";
	}

	@Override
	public void execute() {

		// ACO
		updateMessage("Inicializando parâmetros e a matriz de feromônio.");
		nodes = new ArrayList<ACONode>();
		for (Equipment equipment : items)// Cria os vértices do grafo
		{
			nodes.add(new ACONode(equipment));
		}
		Collections.sort(nodes);// ordena os vértices do grafo por
								// desejabilidade
		ACO.n = nodes.size();
		for (ACONode node1 : nodes)// cria as arestas do grafo
		{
			for (ACONode node2 : nodes) {
				if (node1 != node2) {
					node1.addEdge(node2);
				}
			}
		}

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
			if (isCancelled()) {
				return;
			}

			Solution[] solutions = new Solution[ACO.M];
			List<ACONode>[] solutionsNodes = new ArrayList[ACO.M];

			// ANT == AGENT
			for (int k = 0; k < ACO.M; k++) {

				// cria uma solução para a formiga k trabalhar
				solutions[k] = new Solution(container, items, true);
				List<ACONode> solutionNodes = solutionsNodes[k] = new ArrayList<ACONode>();
				ACONode firstNode = nodes.get(OptimizationAlgorithm.RANDOM.nextInt(ACO.n));
				firstNode.setVisited(true);
				solutionNodes.add(firstNode);// aloca a formiga em um nó
												// aleatoriamente

				addEquipmentInRandomPosition(solutions[k], firstNode.getEquipment());

				while (solutionNodes.size() < ACO.n) {

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
					if (OptimizationAlgorithm.RANDOM.nextDouble() < ACO.Q0) {
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
						if (type == Type.I)
							addEquipmentWithEquipmentRelationshipI(solutions[k], nextNode.getEquipment());
						else if (type == Type.II)
							addEquipmentWithEquipmentRelationshipII(solutions[k], nextNode.getEquipment());
						else
							addEquipmentInRandomPosition(solutions[k], nextNode.getEquipment());
					} catch (Exception e) {
						for (ACONode node : solutionNodes) {
							node.setVisited(false);
						}
						// cria uma solução para a formiga k trabalhar
						solutions[k] = new Solution(container, items, true);
						solutionNodes = solutionsNodes[k] = new ArrayList<ACONode>();
						firstNode = nodes.get(OptimizationAlgorithm.RANDOM.nextInt(ACO.n));
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
			Solution bestGenerationSolution = null;
			int k = 0;
			for (int z = 0; z < ACO.M; z++) {
				if (bestGenerationSolution == null || bestGenerationSolution.getFitness() < solutions[z].getFitness()) {
					bestGenerationSolution = solutions[z];
					k = z;
				}
				if (worstSolution == null || worstSolution.getFitness() > solutions[z].getFitness()) {
					worstSolution = bestGenerationSolution;
				}
			}
			if (bestSolution == null || bestSolution.getFitness() < bestGenerationSolution.getFitness()) {
				bestSolution = bestGenerationSolution;
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

			// atualiza afinidade entre equipamentos da melhor solução if
			for (Equipment equipment : bestGenerationSolution.getItems())
				equipment.updateEquipmentRelationship(container);

			updateProgress(i, MAX);
			updateMessage(String.format("Iteração %d de %d <-> Melhor solução %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.m2}", i, MAX, bestSolution.getFitness(),
					bestSolution.getMassCenter(), bestSolution.getMassCenterX() - bestSolution.getContainer().getWidth() / 2, bestSolution.getMassCenterY() - bestSolution.getContainer().getHeight()
							/ 2, bestSolution.getMomentOfInertia()));
		}

		updateMessage("nologscreenParâmetro utilizados:" + "\n              SEED = " + SEED + "\n             ALPHA = " + ACO.ALPHA + "\n              BETA = " + ACO.BETA + "\nTaxa de Decaimento = "
				+ ACO.RO + "\n                q0 = " + ACO.Q0 + "\n         ITERAÇÕES = " + MAX + "\n          FORMIGAS = " + ACO.M + "\n          LAMBDA1  = " + Solution.LAMBDA1
				+ "\n          LAMBDA2  = " + Solution.LAMBDA2 + "\nMelhor solução encontrada: " + bestSolution);
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
