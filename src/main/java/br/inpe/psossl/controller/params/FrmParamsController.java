package br.inpe.psossl.controller.params;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

/**
 *
 * @author Gustavo Furtado
 */
public abstract class FrmParamsController implements Initializable {
    
    protected Stage primaryStage;
    protected Stage stage;
    
    @FXML
    protected void cancelEditParams(ActionEvent event) {
        this.stage.close();
    }
    
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    public void setStage(Stage stage){
        this.stage = stage;
    }
}
