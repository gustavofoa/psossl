package br.inpe.psossl.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import br.inpe.psossl.model.Container;
import br.inpe.psossl.model.Equipment;
import br.inpe.psossl.model.Solution;
import br.inpe.psossl.ux.MessageBox;

/**
 * 
 * @author Gustavo Furtado
 */
public class FrmViewSolutionController implements Initializable {

	private Stage		stage;
	private Solution	solution;
	private Container   container;

	@FXML
	private TextArea	txtSolution;
	@FXML
	private Button		btnView;

	@FXML
	protected void viewAction(ActionEvent event) throws IOException {

		try{
		String serializedSolution = txtSolution.getText();
		List<Equipment> items = new ArrayList<Equipment>();
		
		String[] equipments = serializedSolution.split("Equipment - ");
		
		for (int i = 1; i<equipments.length;i++){
			String equipment = equipments[i];
			String id = equipment.substring(0, equipment.indexOf(":"));
			String width = equipment.substring(equipment.indexOf("Width:")+6,  equipment.indexOf(","));
			equipment = equipment.substring(equipment.indexOf(",")+1);
			String height = equipment.substring(equipment.indexOf("Height:")+7,  equipment.indexOf(","));
			equipment = equipment.substring(equipment.indexOf(",")+1);
			String mass = equipment.substring(equipment.indexOf("Mass:")+5,  equipment.indexOf(","));
			equipment = equipment.substring(equipment.indexOf(",")+1);
			String x = equipment.substring(6, equipment.indexOf(","));
			equipment = equipment.substring(equipment.indexOf(",")+1);
			String y = equipment.substring(1, equipment.indexOf(","));
			equipment = equipment.substring(equipment.indexOf(",")+1);
			String angle = equipment.substring(1, equipment.indexOf(","));
			equipment = equipment.substring(equipment.indexOf(",")+1);
			String face = equipment.substring(1, 2);
			Equipment equip = new Equipment(
					Integer.parseInt(id),
					Double.parseDouble(width),
					Double.parseDouble(height),
					Double.parseDouble(mass)
					);
			equip.setColor(Color.WHITE);
			equip.setX(Double.parseDouble(x));
			equip.setY(Double.parseDouble(y));
			equip.setAngle(Double.parseDouble(angle));
			equip.setFace(Integer.parseInt(face));
			items.add(equip);
			System.out.println(equip);
		}
		

		// configura a cor em escala de cinza de acordo com o peso do
		// equipamento
		double menorMassa = 0;
		double maiorMassa = 0;
		for (Equipment item : items) {
			if (menorMassa == 0 || menorMassa > item.getMass()) {
				menorMassa = item.getMass();
			}
			if (maiorMassa < item.getMass()) {
				maiorMassa = item.getMass();
			}
		}

		for (Equipment item : items) {
			double escala = item.getMass() / maiorMassa;
			item.setColor(new Color(1 - escala, 1 - escala, 1 - escala, 1));
		}

		
		solution = new Solution(container, items);
		
		stage.close();
		} catch (Exception e){
			MessageBox.show(stage, "Este texto não representa uma solução válida!", "Solução inválida", MessageBox.ICON_ERROR | MessageBox.OK);
			e.printStackTrace();
		}
		
		
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
	
	public Solution getSolution(){
		return this.solution;
	}
	
	public void setContainer(Container container){
		this.container = container;
	}

}
