package br.inpe.psossl;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import br.inpe.psossl.ux.NumberTextField;

/**
 * 
 * @author Gustavo Furtado
 */
public class FrmAddConstraintController implements Initializable {

	private Stage				stage;

	// @FXMLprivate ComboBox<Equipment> cmbRerefence;
	@FXML
	private ComboBox<String>	cmbTipo;
	@FXML
	private NumberTextField		txtDistance;
	@FXML
	private ComboBox<String>	cmbFace;
	@FXML
	private Button				btnAdd;

	@FXML
	protected void addConstraintAction(ActionEvent event) throws IOException {

	}

	@FXML
	protected void cancelAddConstraintAction(ActionEvent event) {
		this.stage.close();
	}

	public void initialize(URL url, ResourceBundle rb) {

	}

	public void setStage(Stage stage) {
		this.stage = stage;
		
	}

}
