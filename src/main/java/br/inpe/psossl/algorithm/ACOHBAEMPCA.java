package br.inpe.psossl.algorithm;

import java.util.List;

import br.inpe.psossl.model.Container;
import br.inpe.psossl.model.Equipment;

public class ACOHBAEMPCA extends HBAE {

	public static int	MAX	= 10000;

	public ACOHBAEMPCA(Container container, List<Equipment> items, Type type) {
		super(container, items, type);
		SIGLA = "ACO+MPCA";
		NOME = "Ant Colony Optimization with Multi-Particle Collision Algorithm and HBAE";

		// Perturbar o equipamento num espaço de 5% do container
		MPCA.PerturbationXLimit = container.getWidth() * 0.01;
		MPCA.PerturbationYLimit = container.getHeight() * 0.01;
		// Perturbar o equipamento num espaço de 1% do container
		MPCA.SmallPerturbationXLimit = container.getWidth() * 0.001;
		MPCA.SmallPerturbationYLimit = container.getHeight() * 0.001;

	}

	@Override
	public void execute() {

		updateMessage("Inicializando busca aleatória por soluções.");

	}

}
