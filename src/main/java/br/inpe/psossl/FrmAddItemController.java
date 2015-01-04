package br.inpe.psossl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.ux.MessageBox;
import br.inpe.psossl.ux.NumberTextField;

/**
 * 
 * @author Gustavo Furtado
 */
public class FrmAddItemController implements Initializable {

	private Stage						stage;
	private ObservableList<Equipment>	items;
	private Equipment					item;

	@FXML
	private NumberTextField				txtWidth;
	@FXML
	private NumberTextField				txtHeight;
	@FXML
	private NumberTextField				txtMass;
	@FXML
	private Button						btnAdd;

	@FXML
	protected void addItemAction(ActionEvent event) throws IOException {
		double w = txtWidth.getNumber().doubleValue();
		double h = txtHeight.getNumber().doubleValue();
		double mass = txtMass.getNumber().doubleValue();
		if (item == null)
			item = new Equipment(w, h, mass);
		else
			item.updateParams(w, h, mass, Color.WHITE);

		if (!item.validateParams()) {
			MessageBox.show(stage, "Essa configuração não é válida!", "Objeto inválido", MessageBox.ICON_ERROR | MessageBox.OK);
			txtWidth.requestFocus();
			return;
		}
		if (items != null)
			items.add(item);
		stage.close();
	}

	@FXML
	protected void cancelAddItemAction(ActionEvent event) {
		this.stage.close();
	}

	public void initialize(URL url, ResourceBundle rb) {

	}

	void setStage(Stage stage) {
		this.stage = stage;
	}

	void setItemList(ObservableList<Equipment> items) {
		this.items = items;
	}

	void setItem(Equipment selectedItem) {
		this.item = selectedItem;
		if (item != null) {
			txtWidth.setNumber(new BigDecimal(item.getWidth()));
			txtHeight.setNumber(new BigDecimal(item.getHeight()));
			txtMass.setNumber(new BigDecimal(item.getMass()));
			btnAdd.setText("Salvar Alterações");
		}
	}

}
