package br.inpe.psossl.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.inpe.psossl.model.Container;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.model.EquipmentRelationship;
import br.inpe.psossl.model.Solution;

public class HBAE extends OptimizationAlgorithm {

	public static int		MAX				= 10000;
	public static double	REINFORCE_RATE	= 1.01;
	public static double	DECAY_RATE		= 0.99;
	public static int		AGENTS			= 10;

	public HBAE(Container container, List<Equipment> items, Type type) {
		super(container, items);
		this.type = type;
		SIGLA = "HBAE";
		NOME = "Heurística de Balanceamento por Afinidade Espacial";
	}

	@Override
	public void execute() {

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

			Solution bestGenerationSolution = null;

			for (int j = 0; j < AGENTS; j++) {

				Solution solution = generateHBAEBasedSolution();

				if (worstSolution == null || worstSolution.getFitness() > solution.getFitness()) {
					worstSolution = solution;
				}

				if (bestSolution == null || bestSolution.getFitness() < solution.getFitness()) {
					bestSolution = solution;
					updateMessage("Solução Encontrada! " + bestSolution);
				}

				if (bestGenerationSolution == null || bestGenerationSolution.getFitness() < solution.getFitness()) {
					bestGenerationSolution = solution;
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

		// limpa matriz de afinidades
		for (Equipment equipment : items)
			equipment.getRelationships().clear();

		updateMessage("nologscreenParâmetro utilizados:" + "\n              SEED = " + SEED + "\n   TAXA DE REFORÇO = " + REINFORCE_RATE + "\nTAXA DE DECAIMENTO = " + DECAY_RATE
				+ "\n              SEED = " + SEED + "\n         ITERAÇÕES = " + MAX + "\n          LAMBDA1  = " + Solution.LAMBDA1 + "\n          LAMBDA2  = " + Solution.LAMBDA2
				+ "\nMelhor solução encontrada: " + bestSolution);
		updateProgress(MAX, MAX);
		try {
			Thread.sleep(500);
		} catch (InterruptedException ex) {
			Logger.getLogger(HBAE.class.getName()).log(Level.SEVERE, null, ex);
		}
		updateMessage(String.format("Execução finalizada! <-> Melhor solução %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.cm2}", bestSolution.getFitness(),
				bestSolution.getMassCenter(), bestSolution.getMassCenterX() - bestSolution.getContainer().getWidth() / 2, bestSolution.getMassCenterY() - bestSolution.getContainer().getHeight() / 2,
				bestSolution.getMomentOfInertia()));

	}

	protected Type	type;

	public enum Type {
		I, II
	}

	public void addEquipmentWithEquipmentRelationshipI(Solution solution, Equipment equipment) {
		if (solution.getItems().contains(equipment))
			return;
		if (solution.getItems().isEmpty()) {
			addEquipmentInRandomPosition(solution, equipment);
			return;
		}

		double x, y, angle;
		int count = 0, count2 = 0, face = 0;

		do {
			// sorteia um equipamento já presente na solução
			Equipment reference = solution.getItems().get(OptimizationAlgorithm.RANDOM.nextInt(solution.getItems().size()));
			EquipmentRelationship relationship = null;
			for (EquipmentRelationship rel : equipment.getRelationships())
				if (rel.getEquipment(equipment).equals(reference)) {
					relationship = rel;
					break;
				}
			if (relationship == null)
				throw new IllegalArgumentException("Equipamento não encontrado na matriz de relacionamentos.");
			double relX = relationship.getEquipment(equipment).getX() - solution.getContainer().getWidth() / 2;
			double relY = relationship.getEquipment(equipment).getY() - solution.getContainer().getHeight() / 2;
			do {

				double total = relationship.getOppositeQuadrant() + relationship.getSameQuadrant() + relationship.getNeighborQuadrant();
				double rand = OptimizationAlgorithm.RANDOM.nextDouble() * total;

				if (rand < relationship.getOppositeQuadrant()) {
					if (relX > 0)
						x = equipment.getWidth() / 2 + OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getWidth() / 2 - equipment.getWidth() / 2);
					else
						x = solution.getContainer().getWidth() / 2 + OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getWidth() / 2 - equipment.getWidth() / 2);
					if (relY > 0)
						y = equipment.getHeight() / 2 + OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getHeight() / 2 - equipment.getHeight() / 2);
					else
						y = solution.getContainer().getHeight() / 2 + OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getHeight() / 2 - equipment.getHeight() / 2);
				} else if (rand < relationship.getOppositeQuadrant() + relationship.getSameQuadrant()) {
					if (relX > 0)
						x = solution.getContainer().getWidth() / 2 + OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getWidth() / 2 - equipment.getWidth() / 2);
					else
						x = equipment.getWidth() / 2 + OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getWidth() / 2 - equipment.getWidth() / 2);
					if (relY > 0)
						y = solution.getContainer().getHeight() / 2 + OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getHeight() / 2 - equipment.getHeight() / 2);
					else
						y = equipment.getHeight() / 2 + OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getHeight() / 2 - equipment.getHeight() / 2);
				} else {
					x = OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getWidth() - equipment.getWidth()) + (equipment.getWidth() / 2);
					y = OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getHeight() - equipment.getHeight()) + (equipment.getHeight() / 2);
				}

				angle = OptimizationAlgorithm.RANDOM.nextDouble() * 360;
				face = OptimizationAlgorithm.RANDOM.nextInt(2) + 1;
				count++;
				if (count >= 1000)
					break;

			} while (!solution.validateAndAddItem(equipment, x, y, angle, face));

			count2++;
			if (count2 >= 100)
				throw new IllegalArgumentException("Não foi possível alocar o equipamento após " + count + " tentativas.");

		} while (!solution.validateAndAddItem(equipment, x, y, angle, face));

	}

	public void addEquipmentWithEquipmentRelationshipII(Solution solution, Equipment equipment) {
		if (solution.getItems().contains(equipment))
			return;
		if (solution.getItems().isEmpty()) {
			addEquipmentInRandomPosition(solution, equipment);
			return;
		}

		double x, y, angle;
		int count = 0, count2 = 0, face = 0;

		double q1 = 0, q2 = 0, q3 = 0, q4 = 0;

		do {
			// obtém a soma das probabilidades de todos os quadrantes baseado
			// nos relacionamentos
			// itera nos equipamentos presentes na solução
			for (Equipment reference : solution.getItems()) {
				EquipmentRelationship relationship = null;
				for (EquipmentRelationship rel : equipment.getRelationships())
					if (rel.getEquipment(equipment).equals(reference))
						relationship = rel;
				if (relationship == null)
					throw new IllegalArgumentException("Equipamento não encontrado na matriz de relacionamentos.");

				double relX = relationship.getEquipment(equipment).getX() - container.getWidth() / 2;
				double relY = relationship.getEquipment(equipment).getY() - container.getHeight() / 2;

				if (relX >= 0 && relY >= 0) {// Objeto referencia é localizado
												// no primeiro quadrante (Q1)
					q1 += relationship.getSameQuadrant();
					q2 += relationship.getNeighborQuadrant();
					q3 += relationship.getOppositeQuadrant();
					q4 += relationship.getNeighborQuadrant();
				} else if (relX < 0 && relY >= 0) {// Objeto referencia é
													// localizado no segundo
													// quadrante (Q2)
					q1 += relationship.getNeighborQuadrant();
					q2 += relationship.getSameQuadrant();
					q3 += relationship.getNeighborQuadrant();
					q4 += relationship.getOppositeQuadrant();
				} else if (relX < 0 && relY < 0) {// Objeto referencia é
													// localizado no terceiro
													// quadrante (Q3)
					q1 += relationship.getOppositeQuadrant();
					q2 += relationship.getNeighborQuadrant();
					q3 += relationship.getSameQuadrant();
					q4 += relationship.getNeighborQuadrant();
				} else {// Objeto referencia é localizado no quarto quadrante
						// (Q4)
					q1 += relationship.getNeighborQuadrant();
					q2 += relationship.getOppositeQuadrant();
					q3 += relationship.getNeighborQuadrant();
					q4 += relationship.getSameQuadrant();
				}

			}

			do {

				double total = q1 + q2 + q3 + q4;
				double rand = OptimizationAlgorithm.RANDOM.nextDouble() * total;

				// sorteia uma posição para um quadrante
				x = equipment.getWidth() / 2 + OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getWidth() / 2 - equipment.getWidth() / 2);
				y = equipment.getHeight() / 2 + OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getHeight() / 2 - equipment.getHeight() / 2);

				// posiciona o objeto no quadrante selecionado relativamente
				// aleatório
				if (rand < q1) {// alocar objeto no primeiro quadrante
					x += solution.getContainer().getWidth() / 2;
					y += solution.getContainer().getHeight() / 2;
				} else if (rand < (q1 + q2)) {// alocar objeto no segundo
												// quadrante
					y += solution.getContainer().getHeight() / 2;
				} else if (rand < (q1 + q2 + q3)) {// alocar objeto no terceiro
													// quadrante
					;
				} else {// alocar objeto no quarto quadrante
					x += solution.getContainer().getWidth() / 2;
				}
				angle = OptimizationAlgorithm.RANDOM.nextDouble() * 360;
				face = OptimizationAlgorithm.RANDOM.nextInt(2) + 1;
				count++;
				if (count >= 1000)
					break;

			} while (!solution.validateAndAddItem(equipment, x, y, angle, face));

			count2++;
			if (count2 >= 100)
				throw new IllegalArgumentException("Não foi possível alocar o equipamento após " + count + " tentativas.");

		} while (!solution.validateAndAddItem(equipment, x, y, angle, face));

	}

	public Solution generateHBAEBasedSolution() {
		Solution solution = new Solution(container, items, true);

		List<Equipment> randomList = new ArrayList<Equipment>();
		for (Equipment equipment : items)
			randomList.add(equipment);
		Collections.shuffle(randomList, OptimizationAlgorithm.RANDOM);

		try {
			for (Equipment equipment : randomList)
				if (type == Type.I)
					addEquipmentWithEquipmentRelationshipI(solution, equipment);
				else if (type == Type.II)
					addEquipmentWithEquipmentRelationshipII(solution, equipment);
				else
					addEquipmentInRandomPosition(solution, equipment);
		} catch (IllegalArgumentException e) {
			return generateHBAEBasedSolution();
		}

		return solution;
	}

}
