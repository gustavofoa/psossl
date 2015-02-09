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
import br.inpe.psossl.algorithm.ACO;
import br.inpe.psossl.algorithm.OptimizationAlgorithm;
import br.inpe.psossl.controller.FrmMainController;
import br.inpe.psossl.model.Solution;
import br.inpe.psossl.ux.NumberTextField;

/**
 *
 * @author Gustavo Furtado
 */
public class FrmACOParamsController extends FrmParamsController {

	@FXML
	private NumberTextField	txtSeed;
	@FXML
	private NumberTextField	txtAlpha;
	@FXML
	private NumberTextField	txtBeta;
	@FXML
	private NumberTextField	txtRO;
	@FXML
	private NumberTextField	txtQ0;
	@FXML
	private NumberTextField	txtMAX;
	@FXML
	private NumberTextField	txtM;
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
		ACO.ALPHA = txtAlpha.getNumber().doubleValue();
		ACO.BETA = txtBeta.getNumber().doubleValue();
		ACO.RO = txtRO.getNumber().doubleValue();
		ACO.Q0 = txtQ0.getNumber().doubleValue();
		ACO.MAX = txtMAX.getNumber().intValue();
		ACO.M = txtM.getNumber().intValue();
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
		txtAlpha.setNumber(new BigDecimal(ACO.ALPHA));
		txtBeta.setNumber(new BigDecimal(ACO.BETA));
		txtRO.setNumber(new BigDecimal(ACO.RO));
		txtQ0.setNumber(new BigDecimal(ACO.Q0));
		txtMAX.setNumber(new BigDecimal(ACO.MAX));
		txtM.setNumber(new BigDecimal(ACO.M));
		txtLambda1.setNumber(new BigDecimal(Solution.LAMBDA1));
		txtLambda2.setNumber(new BigDecimal(Solution.LAMBDA2));
		txtExecucoes.setNumber(new BigDecimal(OptimizationAlgorithm.EXECUCOES));
		txtLogFolder.setText(Main.LOG_FOLDER);
	}

}
