package br.inpe.psossl;

import java.io.IOException;
import java.math.BigDecimal;
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
import br.inpe.psossl.model.Constraint;
import br.inpe.psossl.model.Constraint.Type;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.ux.MessageBox;
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

	private Equipment					equipment;

	private Constraint					constraint;

	@FXML
	protected void addConstraintAction(ActionEvent event) throws IOException {

		if (!validate())
			return;

		Type type = null;

		if (cmbTipo.getSelectionModel().getSelectedIndex() == 0)
			type = Type.Min;
		else if (cmbTipo.getSelectionModel().getSelectedIndex() == 1)
			type = Type.Max;
		else
			type = Type.Face;

		int face = cmbFace.getSelectionModel().getSelectedIndex() == 0 ? 1 : 2;

		if (constraint == null)
			this.equipment.addConstraint(cmbRerefence.getValue(), type, txtDistance.getNumber().doubleValue(), face);
		else {

			if (this.constraint.getEquipment1() != null)
				this.constraint.getEquipment1().getConstraints().remove(this.constraint);
			if (this.constraint.getEquipment2() != null)
				this.constraint.getEquipment2().getConstraints().remove(this.constraint);

			this.equipment.addConstraint(cmbRerefence.getValue(), type, txtDistance.getNumber().doubleValue(), face);

		}

		stage.close();

	}

	private boolean validate() {
		if (cmbTipo.getSelectionModel().getSelectedIndex() == -1) {
			MessageBox.show(stage, "Selecione o tipo de restrição!", "Tipo não selecionado.", MessageBox.ICON_ERROR | MessageBox.OK);
			return false;
		}

		if (cmbTipo.getSelectionModel().getSelectedIndex() == 0 || cmbTipo.getSelectionModel().getSelectedIndex() == 1) {

			if (cmbRerefence.getSelectionModel().getSelectedIndex() == -1) {
				MessageBox.show(stage, "Selecione um objeto de referência!", "Objeto referência não selecionado.", MessageBox.ICON_ERROR | MessageBox.OK);
				return false;
			}

			if (txtDistance.getNumber().equals(new BigDecimal(0))) {
				if (cmbTipo.getSelectionModel().getSelectedIndex() == 0)
					MessageBox.show(stage, "Informe a distância mínima entre os objetos!", "Distância não informada.", MessageBox.ICON_ERROR | MessageBox.OK);
				else
					MessageBox.show(stage, "Informe a distância máxima entre os objetos!", "Distância não informada.", MessageBox.ICON_ERROR | MessageBox.OK);
				return false;
			}

		} else {

			if (cmbFace.getSelectionModel().getSelectedIndex() == -1) {
				MessageBox.show(stage, "Selecione a face da prateleira que este objeto deve ficar!", "Face não selecionada.", MessageBox.ICON_ERROR | MessageBox.OK);
				return false;
			}

		}

		Equipment reference = cmbRerefence.getValue();

		for (Constraint constraint : this.equipment.getConstraints()) {
			if (constraint == this.constraint)
				continue;
			if (cmbTipo.getSelectionModel().getSelectedIndex() == 2) {
				if (constraint.getType() == Type.Face) {
					MessageBox.show(stage, "Já existe uma restrição de face para este objeto!", "Restrição existente.", MessageBox.ICON_ERROR | MessageBox.OK);
					return false;
				}
			} else if (constraint.getType() != Type.Face && (constraint.getEquipment1().equals(reference) || constraint.getEquipment2().equals(reference))) {
				if (cmbTipo.getSelectionModel().getSelectedIndex() == 0 && constraint.getType() == Type.Min) {
					MessageBox.show(stage, "Já existe uma restrição de distância mínima entre os objetos!", "Restrição existente.", MessageBox.ICON_ERROR | MessageBox.OK);
					return false;
				} else if (cmbTipo.getSelectionModel().getSelectedIndex() == 1 && constraint.getType() == Type.Max) {
					MessageBox.show(stage, "Já existe uma restrição de distância máxima entre os objetos!", "Restrição existente.", MessageBox.ICON_ERROR | MessageBox.OK);
					return false;
				} else if (cmbTipo.getSelectionModel().getSelectedIndex() == 2 && constraint.getType() == Type.Face) {
					MessageBox.show(stage, "Já existe uma restrição de face da prateleira para este objeto!", "Restrição existente.", MessageBox.ICON_ERROR | MessageBox.OK);
					return false;
				}

				if (constraint.getEquipment1().equals(reference) || constraint.getEquipment2().equals(reference)) {
					if (cmbTipo.getSelectionModel().getSelectedIndex() == 0 && constraint.getType() == Type.Max && constraint.getDistance() <= txtDistance.getNumber().doubleValue()) {
						MessageBox.show(stage, "Não é possível criar uma restrição mínima maior que uma restrição máxima existente!!", "Restrições conflitantes.", MessageBox.ICON_ERROR
								| MessageBox.OK);
						return false;
					} else if (cmbTipo.getSelectionModel().getSelectedIndex() == 1 && constraint.getType() == Type.Min && constraint.getDistance() >= txtDistance.getNumber().doubleValue()) {
						MessageBox.show(stage, "Não é possível criar uma restrição máxima menor que uma restrição mínima existente!!", "Restrições conflitantes.", MessageBox.ICON_ERROR
								| MessageBox.OK);
						return false;
					}
				}

			}
		}

		return true;

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
				cmbRerefence.setDisable(t1.equals(2));
				cmbFace.setDisable(t1.equals(0) || t1.equals(1));

				if (t1.equals(0) || t1.equals(1))
					cmbFace.getSelectionModel().clearSelection();

				if (t1.equals(2)) {
					txtDistance.setNumber(new BigDecimal(0));
					cmbRerefence.getSelectionModel().clearSelection();
				}

			}
		});

	}

	public void setStage(Stage stage) {
		this.stage = stage;

	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public void setReferences(ObservableList<Equipment> equipments) {

		if (references == null)
			references = FXCollections.observableArrayList();

		for (Equipment equipment : equipments)
			if (!equipment.equals(this.equipment))
				this.references.add(equipment);

		cmbRerefence.setItems(references);
	}

	void setConstraint(Constraint selectedConstraint) {
		this.constraint = selectedConstraint;
		if (constraint != null) {
			cmbTipo.getSelectionModel().select(this.constraint.getType() == Type.Min ? 0 : this.constraint.getType() == Type.Max ? 1 : 2);
			if (constraint.getType() != Type.Face) {
				cmbRerefence.setValue(constraint.getEquipment1() == equipment ? constraint.getEquipment2() : constraint.getEquipment2() == equipment ? constraint.getEquipment1() : null);
				txtDistance.setNumber(new BigDecimal(constraint.getDistance()));
			} else
				cmbFace.getSelectionModel().select(constraint.getFace() == 1 ? 0 : constraint.getFace() == 2 ? 1 : 0);
			btnAdd.setText("Salvar");
		}
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
