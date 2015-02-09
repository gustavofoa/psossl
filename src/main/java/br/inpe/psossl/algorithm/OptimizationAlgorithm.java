package br.inpe.psossl.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import br.inpe.psossl.model.Container;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.model.Solution;

public abstract class OptimizationAlgorithm extends Observable {

	public static int		SEED		= 0;
	public static int		EXECUCOES	= 25;
	public static Random	RANDOM		= new Random(SEED);

	public Container		container;
	public List<Equipment>	items;
	public Solution			bestSolution;
	public Solution			worstSolution;

	public String			SIGLA;
	public String			NOME;

	private int				i;
	private int				max;
	private String			msg;
	private boolean			cancelled;

	OptimizationAlgorithm(Container container, List<Equipment> items) {
		this.container = container;
		this.items = items;
	}

	public abstract void execute();

	public Solution getBestSolution() {
		return bestSolution;
	}

	public Solution getWorstSolution() {
		return worstSolution;
	}

	public void updateProgress(int i, int max) {
		this.i = i;
		this.max = max;
		setChanged();
		notifyObservers();
	}

	public void updateMessage(String msg) {
		this.msg = msg;
		setChanged();
		notifyObservers();
	}

	public int getI() {
		return i;
	}

	public int getMax() {
		return max;
	}

	public String getMsg() {
		return msg;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void cancel() {
		this.cancelled = true;
	}

	public void addEquipmentInRandomPosition(Solution solution, Equipment equipment) {

		if (solution.getItems().contains(equipment))
			return;
		double x, y, angle;
		int count = 0, face = 0;
		;
		do {
			x = OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getWidth() - equipment.getWidth()) + (equipment.getWidth() / 2);
			y = OptimizationAlgorithm.RANDOM.nextDouble() * (solution.getContainer().getHeight() - equipment.getHeight()) + (equipment.getHeight() / 2);
			angle = OptimizationAlgorithm.RANDOM.nextDouble() * 360;
			face = OptimizationAlgorithm.RANDOM.nextInt(2) + 1;
			count++;
			if (count >= 1000)
				throw new IllegalArgumentException("Não foi possível alocar o equipamento após " + count + " tentativas.");

		} while (!solution.validateAndAddItem(equipment, x, y, angle, face));

	}

	public Solution generateRandomSolution() {
		Solution solution = new Solution(container, items, true);

		List<Equipment> randomList = new ArrayList<Equipment>();
		for (Equipment equipment : items)
			randomList.add(equipment);
		Collections.shuffle(randomList);

		try {
			for (Equipment equipment : randomList)
				addEquipmentInRandomPosition(solution, equipment);
		} catch (IllegalArgumentException e) {
			return generateRandomSolution();
		}

		return solution;
	}

}
