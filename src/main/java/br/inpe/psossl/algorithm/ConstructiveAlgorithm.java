package br.inpe.psossl.algorithm;

import java.util.List;

import br.inpe.psossl.model.Container;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.model.EquipmentRelationship;
import br.inpe.psossl.model.Solution;

public abstract class ConstructiveAlgorithm extends OptimizationAlgorithm {
	
	ConstructiveAlgorithm(Container container, List<Equipment> items, Type type) {
		super(container, items);
		this.type = type;
	}
	
	protected Type	type;
	
	public enum Type {
		NORMAL, WITH_EQUIPMENT_RELATIONSHIP_I, WITH_EQUIPMENT_RELATIONSHIP_II
	};
	
	public void addEquipmentWithEquipmentRelationshipI(Solution solution, Equipment equipment) {
		if (solution.getItems().contains(equipment))
			return;
		if (solution.getItems().isEmpty()) {
			addEquipmentIntoSolutionInRandomPosition(solution, equipment);
			return;
		}
		
		double x, y, angle;
		int count = 0, count2 = 0, face = 0;
		
		do {
			// sorteia um nó já presente na solução
			Equipment reference = solution.getItems().get(OptimizationAlgorithm.RANDOM.nextInt(solution.getItems().size()));
			EquipmentRelationship relationship = null;
			for (EquipmentRelationship rel : equipment.getRelationships())
				if (rel.getEquipment().equals(reference))
					relationship = rel;
			if (relationship == null)
				throw new IllegalArgumentException("Equipamento não encontrado na matriz de relacionamentos.");
			double relX = relationship.getEquipment().getX() - solution.getContainer().getWidth() / 2;
			double relY = relationship.getEquipment().getY() - solution.getContainer().getHeight() / 2;
			do {
				
				double total = relationship.getOppositeQuadrant() + relationship.getSameQuadrant() + relationship.getNeighborQuadrant();
				double rand = OptimizationAlgorithm.RANDOM.nextDouble() * total;
				
				if (rand < relationship.getOppositeQuadrant()) {
					if (relX > 0)
						x = equipment.getWidth() / 2 + OptimizationAlgorithm.RANDOM.nextDouble()
								* (solution.getContainer().getWidth() / 2 - equipment.getWidth() / 2);
					else
						x = solution.getContainer().getWidth() / 2 + OptimizationAlgorithm.RANDOM.nextDouble()
								* (solution.getContainer().getWidth() / 2 - equipment.getWidth() / 2);
					if (relY > 0)
						y = equipment.getHeight() / 2 + OptimizationAlgorithm.RANDOM.nextDouble()
								* (solution.getContainer().getHeight() / 2 - equipment.getHeight() / 2);
					else
						y = solution.getContainer().getHeight() / 2 + OptimizationAlgorithm.RANDOM.nextDouble()
								* (solution.getContainer().getHeight() / 2 - equipment.getHeight() / 2);
				} else if (rand < relationship.getOppositeQuadrant() + relationship.getSameQuadrant()) {
					if (relX > 0)
						x = solution.getContainer().getWidth() / 2 + OptimizationAlgorithm.RANDOM.nextDouble()
								* (solution.getContainer().getWidth() / 2 - equipment.getWidth() / 2);
					else
						x = equipment.getWidth() / 2 + OptimizationAlgorithm.RANDOM.nextDouble()
								* (solution.getContainer().getWidth() / 2 - equipment.getWidth() / 2);
					if (relY > 0)
						y = solution.getContainer().getHeight() / 2 + OptimizationAlgorithm.RANDOM.nextDouble()
								* (solution.getContainer().getHeight() / 2 - equipment.getHeight() / 2);
					else
						y = equipment.getHeight() / 2 + OptimizationAlgorithm.RANDOM.nextDouble()
								* (solution.getContainer().getHeight() / 2 - equipment.getHeight() / 2);
				} else {
					x = OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getWidth() - equipment.getWidth())
							+ (equipment.getWidth() / 2);
					y = OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getHeight() - equipment.getHeight())
							+ (equipment.getHeight() / 2);
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
			addEquipmentIntoSolutionInRandomPosition(solution, equipment);
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
					if (rel.getEquipment().equals(reference))
						relationship = rel;
				if (relationship == null)
					throw new IllegalArgumentException("Equipamento não encontrado na matriz de relacionamentos.");
				
				double relX = relationship.getEquipment().getX() - container.getWidth() / 2;
				double relY = relationship.getEquipment().getY() - container.getHeight() / 2;
				
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
				x = equipment.getWidth() / 2 + OptimizationAlgorithm.RANDOM.nextDouble()
						* (solution.getContainer().getWidth() / 2 - equipment.getWidth() / 2);
				y = equipment.getHeight() / 2 + OptimizationAlgorithm.RANDOM.nextDouble()
						* (solution.getContainer().getHeight() / 2 - equipment.getHeight() / 2);
				
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
	
}
