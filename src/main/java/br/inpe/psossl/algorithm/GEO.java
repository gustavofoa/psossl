package br.inpe.psossl.algorithm;

import br.inpe.psossl.model.Container;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.model.Solution;
import java.util.List;

public class GEO extends OptimizationAlgorithm{

    public GEO(Container container, List<Equipment> items){
        super(container, items);
    }
    
    @Override
    public void execute() {
        
        updateMessage("Gerando solução inicial...");
        bestSolution = generateRandomSolution();
        
        updateMessage("Executanto GEO...");
    }
    
}
