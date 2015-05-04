package br.inpe.psossl.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import br.inpe.psossl.Main;
import br.inpe.psossl.ProgressObserver;
import br.inpe.psossl.algorithm.*;
import br.inpe.psossl.algorithm.HBAE.Type;
import br.inpe.psossl.controller.params.FrmParamsController;
import br.inpe.psossl.model.*;
import br.inpe.psossl.ux.*;

/**
 * 
 * @author Gustavo Furtado
 */
public class FrmMainController implements Initializable {

	public static int				SEED;
	private static Random			RANDOM;

	private int						execution		= 0;
	private List<Solution>			solutions		= new ArrayList<Solution>();
	private List<Long>				durations		= new ArrayList<Long>();
	private Stage					primaryStage;
	private Solution				solution;
	private Solution				worstSolution;
	private OptimizationAlgorithm	optimizationAlgorithm;
	private ChangeListener<Number>	resizeListener	= new ChangeListener<Number>() {
														public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
															drawSolution();
														}
													};
	private boolean					running			= false;
	@FXML
	private TableView<Equipment>	itemTable;
	@FXML
	private NumberTextField			txtWidth;
	@FXML
	private NumberTextField			txtHeight;
	@FXML
	private ComboBox<String>		cmbAlgorithm;
	@FXML
	private Button					btnAddItem;
	@FXML
	private Button					btnEditItem;
	@FXML
	private Button					btnRemoveItem;
	@FXML
	private Button					btnAlgorithmParams;
	@FXML
	private Button					btnStart;
	@FXML
	private Button					btnViewSolution;
	@FXML
	private Label					lblInfo;
	@FXML
	private AnchorPane				drawArea1;
	@FXML
	private AnchorPane				drawArea2;
	@FXML
	private ProgressBar				progressBar;
	@FXML
	private TextArea				txtLog;
	@FXML
	private Slider					sliderX;
	@FXML
	private Slider					sliderY;
	@FXML
	private Slider					sliderAngle;
	@FXML
	private CheckBox				chkFixar;
	@FXML
	private ComboBox<String>		cmbFace;
	@FXML
	private Button					btnAddConstraint;
	@FXML
	private Button					btnEditConstraint;
	@FXML
	private Button					btnRemoveConstraint;
	@FXML
	private TableView<Constraint>	constraintsTable;

	@FXML
	protected void addItemAction(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/br/inpe/psossl/FrmAddItem.fxml"));
		Parent root = (Parent) fxmlLoader.load();
		FrmAddItemController myController = fxmlLoader.getController();
		Scene scene = new Scene(root);

		Stage dialogStage = new Stage();
		dialogStage.setTitle("Adicionar Item");
		dialogStage.setResizable(false);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(primaryStage);
		dialogStage.setScene(scene);
		myController.setStage(dialogStage);
		myController.setItemList(itemTable.getItems());
		dialogStage.showAndWait();
	}

	@FXML
	protected void editItemAction(ActionEvent event) throws IOException {

		if (itemTable.getSelectionModel().getSelectedItem() == null) {
			MessageBox.show(primaryStage, "Selecione um objeto para editar!", "Não há objeto selecionado", MessageBox.ICON_ERROR | MessageBox.OK);
			return;
		}

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/br/inpe/psossl/FrmAddItem.fxml"));
		Parent root = (Parent) fxmlLoader.load();
		FrmAddItemController myController = fxmlLoader.getController();
		Scene scene = new Scene(root);

		Stage dialogStage = new Stage();
		dialogStage.setTitle("Editar Item");
		dialogStage.setResizable(false);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(primaryStage);
		dialogStage.setScene(scene);
		myController.setStage(dialogStage);
		myController.setItem(itemTable.getSelectionModel().getSelectedItem());
		if (solution != null) {
			for (Equipment equipment : solution.getItems()) {
				if (itemTable.getSelectionModel().getSelectedItem().equals(equipment)) {
					myController.setItem(equipment);
				}
			}
		}
		dialogStage.showAndWait();
		// refresh table
		if (solution != null) {
			itemTable.getItems().clear();
			itemTable.getItems().addAll(solution.getItems());
		}
		itemTable.getColumns().get(0).setVisible(false);
		itemTable.getColumns().get(0).setVisible(true);

	}

	@FXML
	protected void removeItemAction(ActionEvent event) {

		if (itemTable.getSelectionModel().getSelectedItem() == null) {
			MessageBox.show(primaryStage, "Selecione um objeto para remover!", "Não há objeto selecionado", MessageBox.ICON_ERROR | MessageBox.OK);
			return;
		}

		itemTable.getItems().remove(itemTable.getSelectionModel().getSelectedItem());

	}

	@FXML
	protected void configAlgorithmParamsAction(ActionEvent event) throws IOException {

		if (cmbAlgorithm.getSelectionModel().getSelectedIndex() == -1) {
			MessageBox.show(primaryStage, "Selecione um algoritmo!", "Algoritmo não selecionado", MessageBox.ICON_ERROR | MessageBox.OK);
			cmbAlgorithm.requestFocus();
			return;
		}

		FXMLLoader fxmlLoader = null;
		
		switch (cmbAlgorithm.getSelectionModel().getSelectedItem()) {
		case "Aleatório":
			fxmlLoader = new FXMLLoader(getClass().getResource("/br/inpe/psossl/params/FrmRandomParams.fxml"));
			break;
		case "ACO":
		case "ACO-d":
			fxmlLoader = new FXMLLoader(getClass().getResource("/br/inpe/psossl/params/FrmACOParams.fxml"));
			break;
		case "HBAE-I":
		case "HBAE-II":
			fxmlLoader = new FXMLLoader(getClass().getResource("/br/inpe/psossl/params/FrmHBAEParams.fxml"));
			break;
		case "MPCA":
		case "HBAE-I + MPCA":
		case "HBAE-II + MPCA":
			fxmlLoader = new FXMLLoader(getClass().getResource("/br/inpe/psossl/params/FrmMPCAParams.fxml"));
			break;
		case "ACO + HBAE-I":
		case "ACO + HBAE-II":
		case "ACO-d + HBAE-I":
		case "ACO-d + HBAE-II":
			fxmlLoader = new FXMLLoader(getClass().getResource("/br/inpe/psossl/params/FrmACOHBAEParams.fxml"));
			break;
		case "ACO + MPCA":
			fxmlLoader = new FXMLLoader(getClass().getResource("/br/inpe/psossl/params/FrmACOMPCAParams.fxml"));
			break;
		case "ACO + HBAE-I + MPCA":
		case "ACO + HBAE-II + MPCA":
		case "ACO-d + HBAE-I + MPCA":
		case "ACO-d + HBAE-II + MPCA":
			fxmlLoader = new FXMLLoader(getClass().getResource("/br/inpe/psossl/params/FrmACOHBAEMPCAParams.fxml"));
			break;
		}

		Parent root = (Parent) fxmlLoader.load();
		FrmParamsController myController = fxmlLoader.getController();
		myController.setPrimaryStage(primaryStage);
		Scene scene = new Scene(root);

		Stage dialogStage = new Stage();
		dialogStage.setTitle("Editar Parâmetros");
		dialogStage.setResizable(false);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(primaryStage);
		dialogStage.setScene(scene);
		myController.setStage(dialogStage);
		dialogStage.showAndWait();
	}

	@FXML
	protected void viewSolution(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/br/inpe/psossl/FrmViewSolution.fxml"));
		Parent root = (Parent) fxmlLoader.load();
		FrmViewSolutionController myController = fxmlLoader.getController();
		Scene scene = new Scene(root);

		Stage dialogStage = new Stage();
		dialogStage.setTitle("Solução à visualizar");
		dialogStage.setResizable(false);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(primaryStage);
		dialogStage.setScene(scene);
		myController.setStage(dialogStage);
		myController.setContainer(new Container(txtWidth.getNumber().doubleValue(), txtHeight.getNumber().doubleValue()));
		dialogStage.showAndWait();
		if(myController.getSolution() != null){
			solution = myController.getSolution();
			lblInfo.setText(String.format("Solução %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.m2}", solution.getFitness(),
					solution.getMassCenter(), solution.getMassCenterX() - solution.getContainer().getWidth() / 2, solution.getMassCenterY() - solution.getContainer().getHeight()
					/ 2, solution.getMomentOfInertia()));
			this.drawSolution();
		}
	}

	@FXML
	public void startAction(ActionEvent event) {

		if (execution == 0) {
			OptimizationAlgorithm.SEED = FrmMainController.SEED;
			FrmMainController.RANDOM = new Random(FrmMainController.SEED);
		} else {
			OptimizationAlgorithm.SEED = FrmMainController.RANDOM.nextInt(1000000000);
		}

		execution++;
		running = !running;
		if (running) {
			sliderX.setMax(txtWidth.getNumber().doubleValue());
			sliderY.setMax(txtHeight.getNumber().doubleValue());
			if (!validate()) {
				return;
			}

			btnStart.setText("Parar");
			txtLog.clear();
			final ProgressObserver progressObserver = new ProgressObserver(optimizationAlgorithm);

			progressBar.progressProperty().bind(progressObserver.progressProperty());
			progressObserver.messageProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					solution = optimizationAlgorithm.getBestSolution();
					if (solution != null) {

						if (newValue.length() >= 11 && !newValue.substring(0, 11).equals("nologscreen")) {
							txtLog.appendText(newValue + "\n");
							lblInfo.setText("Execução " + execution + " | " + newValue);
						} else {
							txtLog.appendText(newValue.substring(11) + "\n");
						}
						drawSolution();
					}
				}
			});
			progressObserver.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t) {
					btnStart.setText("Iniciar Execução");
					running = !running;
					txtHeight.setDisable(running);
					txtWidth.setDisable(running);
					cmbAlgorithm.setDisable(running);
					btnAddItem.setDisable(running);
					btnEditItem.setDisable(running);
					btnRemoveItem.setDisable(running);
					btnAlgorithmParams.setDisable(running);
					btnViewSolution.setDisable(running);
					chkFixar.setDisable(running);
					sliderX.setDisable(chkFixar.isSelected());
					sliderY.setDisable(chkFixar.isSelected());
					sliderAngle.setDisable(chkFixar.isSelected());
					cmbFace.setDisable(chkFixar.isSelected());

					btnAddConstraint.setDisable(running);
					btnEditConstraint.setDisable(running);
					btnRemoveConstraint.setDisable(running);

					PrintWriter fileWriter = null;

					File dir = new File(Main.LOG_FOLDER);

					File file = new File(Main.LOG_FOLDER + "/" + Calendar.getInstance().getTimeInMillis() + "-execucao " + execution + ".log");

					try {
						dir.mkdirs();
						file.createNewFile();
						fileWriter = new PrintWriter(file);
						for (String line : txtLog.getText().split("\n")) {
							fileWriter.println(line);
						}
						fileWriter.close();
					} catch (IOException ex) {
						Logger.getLogger(FrmMainController.class.getName()).log(Level.SEVERE, null, ex);
					}
					solutions.add(solution);
					if (worstSolution == null || worstSolution.getFitness() > optimizationAlgorithm.getWorstSolution().getFitness()) {
						worstSolution = optimizationAlgorithm.getWorstSolution();
					}
					if (execution < OptimizationAlgorithm.EXECUCOES) {
						durations.add(progressObserver.getDuration());
						startAction(null);
					} else {

						try {
							fileWriter = new PrintWriter(new File(Main.LOG_FOLDER + "/" + Calendar.getInstance().getTimeInMillis() + " - Resultado  - " + optimizationAlgorithm.SIGLA + ".log"));

							fileWriter.println("Algoritmo \"" + optimizationAlgorithm.NOME + "\" após " + execution + " execuções.");
							fileWriter.println();

							double totalFitness = 0;
							double bestFitness = Double.MIN_VALUE;
							double worstFitness = Double.MAX_VALUE;
							Solution bestSolution = null;
							Solution worstSolution = null;
							double fitness;
							int i = 0;
							StringBuffer strSolutions = new StringBuffer();
							for (Solution solution : solutions) {
								strSolutions.append((++i) + " - " + solution);
								fitness = solution.getFitness();
								totalFitness += fitness;
								if (fitness > bestFitness || bestSolution==null) {
									bestFitness = fitness;
									bestSolution = solution;
								}
								if (fitness < worstFitness || worstSolution==null) {
									worstFitness = fitness;
									worstSolution = solution;
								}
							}

							double media = totalFitness / solutions.size();
							fileWriter.println("Média: " + media);
							double desvioQuadrado = 0;
							for (Solution solution : solutions) {
								desvioQuadrado += Math.pow(solution.getFitness() - media, 2);
							}
							double variancia = desvioQuadrado / solutions.size();
							double desvioPadrao = Math.sqrt(variancia);
							fileWriter.println("Desvio padrão: " + desvioPadrao);

							long totalDuration = 0;
							for (Long duration : durations) {
								totalDuration += duration;
							}
							long avgDuration = totalDuration / durations.size();
							fileWriter.println("Tempo médio: " + (avgDuration / 1000) + " segundos");
							fileWriter.println();
							
							fileWriter.println();
							fileWriter.println("Melhor Solução: " + bestFitness);
							fileWriter.println("Melhor Momento de inércia: " + bestSolution.getMomentOfInertia());
							fileWriter.println("Melhor Centro de Massa: " + bestSolution.getMassCenter());

							fileWriter.println();
							fileWriter.println("Pior Solução: " + worstFitness);
							fileWriter.println("Pior Momento de inércia: " + worstSolution.getMomentOfInertia());
							fileWriter.println("Pior Centro de Massa: " + worstSolution.getMassCenter());
							fileWriter.println();

							fileWriter.println("Soluções:");
							fileWriter.println(strSolutions);

							fileWriter.close();
						} catch (IOException ex) {
							Logger.getLogger(FrmMainController.class.getName()).log(Level.SEVERE, null, ex);
						}

						solutions.clear();
						worstSolution = null;
						durations.clear();
						execution = 0;

					}
				}
			});
			new Thread(progressObserver).start();
		} else {
			optimizationAlgorithm.cancel();
			lblInfo.setText("Busca de solução cancelada!");
			btnStart.setText("Iniciar Execução");
			txtLog.appendText("A execução do algoritmo foi interrompida!\n");
			execution = 0;
		}

		txtHeight.setDisable(running);
		txtWidth.setDisable(running);
		cmbAlgorithm.setDisable(running);
		btnAddItem.setDisable(running);
		btnEditItem.setDisable(running);
		btnRemoveItem.setDisable(running);
		btnAlgorithmParams.setDisable(running);
		btnViewSolution.setDisable(running);
		chkFixar.setDisable(running);
		sliderX.setDisable(chkFixar.isSelected());
		sliderY.setDisable(chkFixar.isSelected());
		sliderAngle.setDisable(chkFixar.isSelected());
		cmbFace.setDisable(chkFixar.isSelected());

		btnAddConstraint.setDisable(running);
		btnEditConstraint.setDisable(running);
		btnRemoveConstraint.setDisable(running);
	}

	private void drawSolution() {

		if (solution == null) {
			return;
		}

		drawArea1.getChildren().clear();
		drawArea2.getChildren().clear();

		double ws = drawArea1.getWidth() - 20;
		double hs = drawArea1.getHeight() - 20;
		double w = solution.getContainer().getWidth();
		double h = solution.getContainer().getHeight();
		double wTela, hTela, x, y;
		double wEquipTela, hEquipTela, xEquip, yEquip;

		if (w / ws > h / hs) {
			// usar horizontal como limite do retângulo maior
			wTela = ws;
			hTela = h * ws / w;
			x = 10;
			y = hs / 2 - hTela / 2 + 10;
		} else {
			// usar vertical como limite do retângulo maior
			wTela = w * hs / h;
			hTela = hs;
			x = ws / 2 - wTela / 2 + 10;
			y = 10;
		}
		Rectangle container1 = new Rectangle(x, y, wTela, hTela);
		container1.setFill(Color.gray(.93));
		container1.setStrokeWidth(2);
		container1.setStroke(Color.BLACK);
		drawArea1.getChildren().add(container1);

		Rectangle container2 = new Rectangle(x, y, wTela, hTela);
		container2.setFill(Color.gray(.93));
		container2.setStrokeWidth(2);
		container2.setStroke(Color.BLACK);
		drawArea2.getChildren().add(container2);
		
		List<Rectangle> otherFaceArea1 = new ArrayList<Rectangle>();
		List<Rectangle> otherFaceArea2 = new ArrayList<Rectangle>();

		for (Equipment equipment : solution.getItems()) {
			wEquipTela = equipment.getWidth() * wTela / w;
			hEquipTela = equipment.getHeight() * hTela / h;
			xEquip = x + equipment.getX() * wTela / w - wEquipTela / 2;
			yEquip = y + (h - equipment.getY()) * hTela / h - hEquipTela / 2;

			Rectangle rect = new Rectangle(xEquip, yEquip, wEquipTela, hEquipTela);
			rect.setRotate(-equipment.getAngle());
			rect.setFill(equipment.getColor());
			rect.setStrokeWidth(1);

			Rectangle rect_back = new Rectangle(xEquip, yEquip, wEquipTela, hEquipTela);
			rect_back.setRotate(-equipment.getAngle());
			rect_back.setFill(Color.TRANSPARENT);
			rect_back.setStrokeWidth(1);
			rect_back.getStrokeDashArray().add(15d);
			
			if (itemTable.getSelectionModel().getSelectedIndex() != -1 && itemTable.getSelectionModel().getSelectedItem().equals(equipment)) {
				rect.setStroke(Color.RED);
				rect.setStrokeWidth(2);
				rect_back.setStroke(Color.RED);
				rect_back.setStrokeWidth(2);
				

				if (running) {
					sliderX.setValue(equipment.getX());
					sliderY.setValue(equipment.getY());
					sliderAngle.setValue(equipment.getAngle());
					chkFixar.setSelected(equipment.isFixed());
					cmbFace.setValue(equipment.getFace() == 2 ? "Face 2" : "Face 1");
				}
			} else {
				rect.setStroke(Color.BLACK);
				rect_back.setStroke(Color.GRAY);
			}
			if (equipment.getFace() == 1){
				drawArea1.getChildren().add(rect);
				otherFaceArea1.add(rect_back);
			} else if (equipment.getFace() == 2){
				drawArea2.getChildren().add(rect);
				otherFaceArea2.add(rect_back);
			}
		}
		
		for (Rectangle rect_back : otherFaceArea1)
			drawArea2.getChildren().add(rect_back);
		for (Rectangle rect_back : otherFaceArea2)
			drawArea1.getChildren().add(rect_back);

		// centro de massa
		double cX = x + (solution.getMassCenterX()) * wTela / w;
		double cY = y + (h - solution.getMassCenterY()) * hTela / h;

		Circle centroDeMassa1 = new Circle(cX, cY, 6, Paint.valueOf("#FF3333"));
		drawArea1.getChildren().add(centroDeMassa1);
		Circle centroDeMassa2 = new Circle(cX, cY, 6, Paint.valueOf("#FF3333"));
		drawArea2.getChildren().add(centroDeMassa2);

	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	public void setPrimaryStage(Stage stage) {
		this.primaryStage = stage;
	}

	public void setContainer(Container container) {
		if (container == null) {
			return;
		}
		txtWidth.setNumber(new BigDecimal(container.getWidth()));
		txtHeight.setNumber(new BigDecimal(container.getHeight()));
		sliderX.setMax(txtWidth.getNumber().doubleValue());
		sliderY.setMax(txtHeight.getNumber().doubleValue());
		sliderAngle.setMax(360);
		drawArea1.widthProperty().addListener(resizeListener);
		drawArea1.heightProperty().addListener(resizeListener);
		itemTable.getSelectionModel().selectedIndexProperty().addListener(resizeListener);

		itemTable.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				if (!running && solution != null) {
					for (Equipment equipment : solution.getItems()) {
						if (itemTable.getSelectionModel().getSelectedIndex() != -1 && itemTable.getSelectionModel().getSelectedItem().equals(equipment)) {
							running = true;
							sliderX.setValue(equipment.getX());
							sliderY.setValue(equipment.getY());
							sliderAngle.setValue(equipment.getAngle());
							chkFixar.setSelected(equipment.isFixed());
							cmbFace.setValue(equipment.getFace() == 2 ? "Face 2" : "Face 1");

							sliderX.setDisable(equipment.isFixed());
							sliderY.setDisable(equipment.isFixed());
							sliderAngle.setDisable(equipment.isFixed());
							cmbFace.setDisable(equipment.isFixed());

							running = false;
							break;
						}
					}
				}

				if (!running && itemTable.getSelectionModel().getSelectedIndex() != -1)
					refreshConstraintsTable();

				btnAddConstraint.setDisable(!running && itemTable.getSelectionModel().getSelectedIndex() == -1);
				btnEditConstraint.setDisable(!running && itemTable.getSelectionModel().getSelectedIndex() == -1);
				btnRemoveConstraint.setDisable(!running && itemTable.getSelectionModel().getSelectedIndex() == -1);

			}

		});

		sliderX.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				if (running || solution == null) {
					return;
				}
				for (Equipment equipment : solution.getItems()) {
					if (itemTable.getSelectionModel().getSelectedIndex() != -1 && itemTable.getSelectionModel().getSelectedItem().equals(equipment)) {
						if (solution.validateAndAddItem(equipment, t1.doubleValue(), equipment.getY(), equipment.getAngle(), equipment.getFace())) {
							drawSolution();
							lblInfo.setText(String.format("Solução alterada manualmente: %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.m2}", solution.getFitness(),
									solution.getMassCenter(), solution.getMassCenterX() - solution.getContainer().getWidth() / 2, solution.getMassCenterY() - solution.getContainer().getHeight() / 2,
									solution.getMomentOfInertia()));
						}
					}
				}
			}
		});

		sliderY.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				if (running || solution == null) {
					return;
				}
				for (Equipment equipment : solution.getItems()) {
					if (itemTable.getSelectionModel().getSelectedIndex() != -1 && itemTable.getSelectionModel().getSelectedItem().equals(equipment)) {
						if (solution.validateAndAddItem(equipment, equipment.getX(), t1.doubleValue(), equipment.getAngle(), equipment.getFace())) {
							drawSolution();
							lblInfo.setText(String.format("Solução alterada manualmente: %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.m2}", solution.getFitness(),
									solution.getMassCenter(), solution.getMassCenterX() - solution.getContainer().getWidth() / 2, solution.getMassCenterY() - solution.getContainer().getHeight() / 2,
									solution.getMomentOfInertia()));
						}
					}
				}
			}
		});

		sliderAngle.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				if (running || solution == null) {
					return;
				}
				for (Equipment equipment : solution.getItems()) {
					if (itemTable.getSelectionModel().getSelectedIndex() != -1 && itemTable.getSelectionModel().getSelectedItem().equals(equipment)) {
						if (solution.validateAndAddItem(equipment, equipment.getX(), equipment.getY(), t1.doubleValue(), equipment.getFace())) {
							equipment.setAngle(t1.doubleValue());
							drawSolution();
							lblInfo.setText(String.format("Solução alterada manualmente: %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.m2}", solution.getFitness(),
									solution.getMassCenter(), solution.getMassCenterX() - solution.getContainer().getWidth() / 2, solution.getMassCenterY() - solution.getContainer().getHeight() / 2,
									solution.getMomentOfInertia()));
						}
					}
				}
			}
		});

		chkFixar.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				if (running || solution == null) {
					return;
				}
				for (Equipment equipment : solution.getItems()) {
					if (itemTable.getSelectionModel().getSelectedIndex() != -1 && itemTable.getSelectionModel().getSelectedItem().equals(equipment)) {
						itemTable.getSelectionModel().getSelectedItem().setX(equipment.getX());
						itemTable.getSelectionModel().getSelectedItem().setY(equipment.getY());
						itemTable.getSelectionModel().getSelectedItem().setAngle(equipment.getAngle());
						equipment.setFixed(t1.booleanValue());
						itemTable.getSelectionModel().getSelectedItem().setFixed(t1.booleanValue());
					}
				}
				sliderX.setDisable(t1.booleanValue());
				sliderY.setDisable(t1.booleanValue());
				sliderAngle.setDisable(t1.booleanValue());
				cmbFace.setDisable(t1.booleanValue());
			}
		});

		cmbFace.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov, String t, String t1) {
				if (running || solution == null) {
					return;
				}
				for (Equipment equipment : solution.getItems()) {
					if (itemTable.getSelectionModel().getSelectedIndex() != -1 && itemTable.getSelectionModel().getSelectedItem().equals(equipment)) {
						if (solution.validateAndAddItem(equipment, equipment.getX(), equipment.getY(), equipment.getAngle(), t1.equals("Face 2") ? 2 : 1)) {

							drawSolution();
							lblInfo.setText(String.format("Solução alterada manualmente: %.3f {Centro de Massa = %.3f (x = %.2f, y = %.2f), Momento de Inércia = %.2f Kg.m2}", solution.getFitness(),
									solution.getMassCenter(), solution.getMassCenterX() - solution.getContainer().getWidth() / 2, solution.getMassCenterY() - solution.getContainer().getHeight() / 2,
									solution.getMomentOfInertia()));
						}
					}
				}
			}
		});

	}

	public void setItems(List<Equipment> items) {
		itemTable.getItems().addAll(items);
	}

	private boolean validate() {
		Container container = new Container(txtWidth.getNumber().doubleValue(), txtHeight.getNumber().doubleValue());
		if (!container.validateParams()) {
			MessageBox.show(primaryStage, "As dimensões da superfície são inválidas!", "Superfície Inválida", MessageBox.ICON_ERROR | MessageBox.OK);
			txtWidth.requestFocus();
			return false;
		}
		if (itemTable.getItems().size() == 0) {
			MessageBox.show(primaryStage, "É necessário ao menos um objeto para alocação!", "Lista de Objetos vazia", MessageBox.ICON_ERROR | MessageBox.OK);
			btnAddItem.requestFocus();
			return false;
		}
		solution = new Solution(container, itemTable.getItems());
		if (!solution.validateParams()) {
			MessageBox.show(primaryStage, "Os parâmetros da superfícies são incompatíveis com os objetos.", "Parâmetros inválidos", MessageBox.ICON_ERROR | MessageBox.OK);
			txtWidth.requestFocus();
			return false;
		}

		if (cmbAlgorithm.getSelectionModel().getSelectedIndex() == -1) {
			MessageBox.show(primaryStage, "Selecione um algoritmo!", "Algoritmo não selecionado", MessageBox.ICON_ERROR | MessageBox.OK);
			cmbAlgorithm.requestFocus();
			return false;
		}
		
		switch (cmbAlgorithm.getSelectionModel().getSelectedItem()) {
		case "Aleatório":
			optimizationAlgorithm = new RandomAlgorithm(container, itemTable.getItems());
			break;
		case "ACO":
			optimizationAlgorithm = new ACO(container, itemTable.getItems());
			break;
		case "HBAE-I":
			optimizationAlgorithm = new HBAE(container, itemTable.getItems(), Type.I);
			break;
		case "HBAE-II":
			optimizationAlgorithm = new HBAE(container, itemTable.getItems(), Type.II);
			break;
		case "MPCA":
			optimizationAlgorithm = new MPCA(container, itemTable.getItems());
			break;
		case "ACO + HBAE-I":
			optimizationAlgorithm = new ACOHBAE(container, itemTable.getItems(), Type.I);
			break;
		case "ACO + HBAE-II":
			optimizationAlgorithm = new ACOHBAE(container, itemTable.getItems(), Type.II);
			break;
		case "ACO + MPCA":
			optimizationAlgorithm = new ACOMPCA(container, itemTable.getItems());
			break;
		case "HBAE-I + MPCA":
			optimizationAlgorithm = new HBAEMPCA(container, itemTable.getItems(), Type.I);
			break;
		case "HBAE-II + MPCA":
			optimizationAlgorithm = new HBAEMPCA(container, itemTable.getItems(), Type.II);
			break;
		case "ACO + HBAE-I + MPCA":
			optimizationAlgorithm = new ACOHBAEMPCA(container, itemTable.getItems(), Type.I);
			break;
		case "ACO + HBAE-II + MPCA":
			optimizationAlgorithm = new ACOHBAEMPCA(container, itemTable.getItems(), Type.II);
			break;
		}

		// configura a cor em escala de cinza de acordo com o peso do
		// equipamento
		double menorMassa = 0;
		double maiorMassa = 0;
		for (Equipment item : itemTable.getItems()) {
			if (menorMassa == 0 || menorMassa > item.getMass()) {
				menorMassa = item.getMass();
			}
			if (maiorMassa < item.getMass()) {
				maiorMassa = item.getMass();
			}
		}

		for (Equipment item : itemTable.getItems()) {
			double escala = item.getMass() / maiorMassa;
			item.setColor(new Color(1 - escala, 1 - escala, 1 - escala, 1));
		}

		return true;
	}

	public void setDefaultAlgorithm(String defaultAlgorithm) {
		cmbAlgorithm.setValue(defaultAlgorithm);
	}

	@FXML
	protected void addConstraintAction(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/br/inpe/psossl/FrmAddConstraint.fxml"));
		Parent root = (Parent) fxmlLoader.load();
		FrmAddConstraintController myController = fxmlLoader.getController();
		Scene scene = new Scene(root);

		myController.setEquipment(itemTable.getSelectionModel().getSelectedItem());
		myController.setReferences(itemTable.getItems());

		Stage dialogStage = new Stage();
		dialogStage.setTitle("Adicionar Restrição");
		dialogStage.setResizable(false);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(primaryStage);
		dialogStage.setScene(scene);
		myController.setStage(dialogStage);
		dialogStage.showAndWait();

		refreshConstraintsTable();

	}

	@FXML
	protected void editConstraintAction(ActionEvent event) throws IOException {

		if (constraintsTable.getSelectionModel().getSelectedItem() == null) {
			MessageBox.show(primaryStage, "Selecione uma restrição para editar!", "Não há restrição selecionada", MessageBox.ICON_ERROR | MessageBox.OK);
			return;
		}

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/br/inpe/psossl/FrmAddConstraint.fxml"));
		Parent root = (Parent) fxmlLoader.load();
		FrmAddConstraintController myController = fxmlLoader.getController();
		Scene scene = new Scene(root);

		myController.setEquipment(itemTable.getSelectionModel().getSelectedItem());
		myController.setReferences(itemTable.getItems());

		Stage dialogStage = new Stage();
		dialogStage.setTitle("Editar Restrição");
		dialogStage.setResizable(false);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(primaryStage);
		dialogStage.setScene(scene);
		myController.setStage(dialogStage);
		myController.setConstraint(constraintsTable.getSelectionModel().getSelectedItem());

		dialogStage.showAndWait();
		// refresh table
		refreshConstraintsTable();

	}

	@FXML
	protected void removeConstraintAction(ActionEvent event) {

		if (constraintsTable.getSelectionModel().getSelectedItem() == null) {
			MessageBox.show(primaryStage, "Selecione uma restrição para remover!", "Não há restrição selecionada", MessageBox.ICON_ERROR | MessageBox.OK);
			return;
		}

		Equipment equipment = itemTable.getSelectionModel().getSelectedItem();
		equipment.removeConstraint(constraintsTable.getSelectionModel().getSelectedItem());

		refreshConstraintsTable();

	}

	private void refreshConstraintsTable() {
		Equipment equipment = itemTable.getSelectionModel().getSelectedItem();

		for (Constraint constraint : equipment.getConstraints())
			constraint.setMainEquipment(equipment);

		constraintsTable.getItems().clear();
		constraintsTable.getItems().addAll(equipment.getConstraints());
	}

}
