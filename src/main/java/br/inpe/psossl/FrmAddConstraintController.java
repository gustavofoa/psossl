package br.inpe.psossl;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.ux.NumberTextField;

/**
 * 
 * @author Gustavo Furtado
 */
public class FrmAddConstraintController implements Initializable {

	private Stage						stage;

	@FXML
	private ComboBox<Equipment>			cmbRerefence;
	@FXML
	private ComboBox<String>			cmbTipo;
	@FXML
	private NumberTextField				txtDistance;
	@FXML
	private ComboBox<String>			cmbFace;
	@FXML
	private Button						btnAdd;

	private ObservableList<Equipment>	references;

	private Equipment					equipament;

	@FXML
	protected void addConstraintAction(ActionEvent event) throws IOException {

	}

	@FXML
	protected void cancelAddConstraintAction(ActionEvent event) {
		this.stage.close();
	}

	public void initialize(URL url, ResourceBundle rb) {

		cmbRerefence.setButtonCell(new EquipmentListCell());

		cmbRerefence.setCellFactory(new Callback<ListView<Equipment>, ListCell<Equipment>>() {

			@Override
			public ListCell<Equipment> call(ListView<Equipment> p) {
				final ListCell<Equipment> cell = new ListCell<Equipment>() {

					@Override
					protected void updateItem(Equipment t, boolean bln) {
						super.updateItem(t, bln);

						if (t != null) {
							setText(t.getId() + ": " + t.getWidth() + ", " + t.getHeight() + " - " + t.getMass());
						} else {
							setText(null);
						}
					}

				};
				return cell;
			}

		});

		cmbTipo.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				txtDistance.setDisable(t1.equals(2));
				cmbFace.setDisable(t1.equals(0) || t1.equals(1));
			}
		});

	}

	public void setStage(Stage stage) {
		this.stage = stage;

	}

	public void setEquipment(Equipment equipment) {
		this.equipament = equipment;
	}

	public void setReferences(ObservableList<Equipment> equipments) {

		if (references == null)
			references = FXCollections.observableArrayList();

		for (Equipment equipment : equipments)
			if (!equipment.equals(this.equipament))
				this.references.add(equipment);

		cmbRerefence.setItems(references);
	}

	class EquipmentListCell extends ListCell<Equipment> {
		@Override
		protected void updateItem(Equipment t, boolean empty) {
			super.updateItem(t, empty);
			if (t != null) {
				setText(t.getId() + ": " + t.getWidth() + ", " + t.getHeight() + " - " + t.getMass());
			}
		}
	}

}
