package br.inpe.psossl;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import br.inpe.psossl.algorithm.OptimizationAlgorithm;
import br.inpe.psossl.model.Solution;

import javafx.concurrent.Task;

public class ProgressObserver extends Task<Solution> implements Observer {
	
	private OptimizationAlgorithm	optimizationAlgorithm;
	private Long					duration;
	
	public Long getDuration() {
		return duration;
	}
	
	public ProgressObserver(OptimizationAlgorithm optimizationAlgorithm) {
		this.optimizationAlgorithm = optimizationAlgorithm;
		optimizationAlgorithm.addObserver(this);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		this.updateProgress(optimizationAlgorithm.getI(), optimizationAlgorithm.getMax());
		this.updateMessage(optimizationAlgorithm.getMsg());
	}
	
	@Override
	protected Solution call() throws Exception {
		Long initial = Calendar.getInstance().getTimeInMillis();
		OptimizationAlgorithm.RANDOM = new Random(OptimizationAlgorithm.SEED);
		optimizationAlgorithm.execute();
		duration = Calendar.getInstance().getTimeInMillis() - initial;
		return optimizationAlgorithm.getBestSolution();
	}
	
}
