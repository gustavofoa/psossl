package br.inpe.psossl.algorithm;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.inpe.psossl.model.Container;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.model.Solution;

public class ACOHBAEMPCA extends HBAE {

	public static int	MAX	= 10000;

	public ACOHBAEMPCA(Container container, List<Equipment> items, Type type) {
		super(container, items, type);
	}

	@Override
	public void execute() {

		updateMessage("Inicializando busca aleatória por soluções.");

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
			Logger.getLogger(ACOHBAEMPCA.class.getName()).log(Level.SEVERE, null, ex);
		}
		updateMessage(String.format("Execução finalizada! <-> Melhor solução %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.cm2}", bestSolution.getFitness(),
				bestSolution.getMassCenter(), bestSolution.getMassCenterX() - bestSolution.getContainer().getWidth() / 2, bestSolution.getMassCenterY() - bestSolution.getContainer().getHeight() / 2,
				bestSolution.getMomentOfInertia()));

	}

}
