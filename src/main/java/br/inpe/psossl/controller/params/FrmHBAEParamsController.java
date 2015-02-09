package br.inpe.psossl.controller.params;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import br.inpe.psossl.Main;
import br.inpe.psossl.algorithm.HBAE;
import br.inpe.psossl.algorithm.OptimizationAlgorithm;
import br.inpe.psossl.controller.FrmMainController;
import br.inpe.psossl.model.Solution;
import br.inpe.psossl.ux.NumberTextField;

/**
 *
 * @author Gustavo Furtado
 */
public class FrmHBAEParamsController extends FrmParamsController {

	@FXML
	private NumberTextField	txtSeed;
	@FXML
	private NumberTextField	txtReinforce;
	@FXML
	private NumberTextField	txtDecay;
	@FXML
	private NumberTextField	txtMAX;
	@FXML
	private NumberTextField	txtAgents;
	@FXML
	private NumberTextField	txtLambda1;
	@FXML
	private NumberTextField	txtLambda2;
	@FXML
	private NumberTextField	txtExecucoes;
	@FXML
	private TextField		txtLogFolder;
	@FXML
	private Button			btnOK;
	@FXML
	private ColorPicker		cmbColor;

	@FXML
	protected void okEdiParams(ActionEvent event) throws IOException {
		FrmMainController.SEED = txtSeed.getNumber().intValue();
		HBAE.REINFORCE_RATE = txtReinforce.getNumber().doubleValue();
		HBAE.DECAY_RATE = txtDecay.getNumber().doubleValue();
		HBAE.MAX = txtMAX.getNumber().intValue();
		HBAE.AGENTS = txtAgents.getNumber().intValue();
		Solution.LAMBDA1 = txtLambda1.getNumber().doubleValue();
		Solution.LAMBDA2 = txtLambda2.getNumber().doubleValue();
		OptimizationAlgorithm.EXECUCOES = txtExecucoes.getNumber().intValue();
		Main.LOG_FOLDER = txtLogFolder.getText();
		stage.close();
	}

	@FXML
	protected void cancelEditParams(ActionEvent event) {
		this.stage.close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	public void setStage(Stage stage) {
		this.stage = stage;
		txtSeed.setNumber(new BigDecimal(FrmMainController.SEED));
		txtReinforce.setNumber(new BigDecimal(HBAE.REINFORCE_RATE));
		txtDecay.setNumber(new BigDecimal(HBAE.DECAY_RATE));
		txtMAX.setNumber(new BigDecimal(HBAE.MAX));
		txtAgents.setNumber(new BigDecimal(HBAE.AGENTS));
		txtLambda1.setNumber(new BigDecimal(Solution.LAMBDA1));
		txtLambda2.setNumber(new BigDecimal(Solution.LAMBDA2));
		txtExecucoes.setNumber(new BigDecimal(OptimizationAlgorithm.EXECUCOES));
		txtLogFolder.setText(Main.LOG_FOLDER);
	}

}
