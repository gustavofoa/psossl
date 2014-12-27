package br.inpe.psossl;

import br.inpe.psossl.algorithm.*;
import br.inpe.psossl.model.*;
import java.io.BufferedReader;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Main extends Application {

    public static int EXECUCOES = 25;
    public static String LOG_FOLDER = "C:\\temp";
    
    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Plataforma de busca de soluções otimizadas para o problema de alocação de experiementos em satélites");
        
        //INITIALIZE MAXIMIZED
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FrmMain.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        FrmMainController myController = fxmlLoader.getController();
        myController.setPrimaryStage(stage);

        Scene scene = new Scene(root);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream(
                    "/Config.json")));

            StringBuilder strJson = new StringBuilder();
            String textoLinha = null;

            while ((textoLinha = reader.readLine()) != null) {
                strJson.append(textoLinha);
            }

            reader.close();

            JSONObject jsonObject = new JSONObject(strJson.toString());

            double w = jsonObject.getJSONObject("container").getDouble("width");
            double h = jsonObject.getJSONObject("container").getDouble("height");
            myController.setContainer(new Container(w, h));

            List<Equipment> items = new ArrayList<Equipment>();
            JSONArray array = jsonObject.getJSONArray("items");
            if (array != null && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    double width = ((JSONObject) array.get(i)).getDouble("width");
                    double heigth = ((JSONObject) array.get(i)).getDouble("height");
                    double mass = ((JSONObject) array.get(i)).getDouble("mass");
                    items.add(new Equipment(width, heigth, mass, Color.WHITE));
                }
            }
            
            //cria matriz de afinidades
            for(Equipment equipment : items)
                for(Equipment equipment2 : items)
                    equipment.getRelationships().add(new EquipmentRelationship((equipment2)));
            
            ACOAlgorithm.ALPHA = jsonObject.getJSONObject("ACOParams").getDouble("ALPHA");
            ACOAlgorithm.BETA = jsonObject.getJSONObject("ACOParams").getDouble("BETA");
            ACOAlgorithm.RO = jsonObject.getJSONObject("ACOParams").getDouble("TAXA_DECAIMENTO");
            ACOAlgorithm.Q0 = jsonObject.getJSONObject("ACOParams").getDouble("Q0");
            ACOAlgorithm.MAX = jsonObject.getJSONObject("ACOParams").getInt("ITERACOES");
            ACOAlgorithm.M = jsonObject.getJSONObject("ACOParams").getInt("FORMIGAS");
            
            FrmMainController.SEED = jsonObject.getInt("SEED");
            Solution.LAMBDA1 = jsonObject.getInt("LAMBDA1");
            Solution.LAMBDA2 = jsonObject.getInt("LAMBDA2");
            EXECUCOES = jsonObject.getInt("EXECUCOES");
            LOG_FOLDER = jsonObject.getString("LOG_FOLDER");
            String defaultAlgorithm = jsonObject.getString("defaultAlgorithm");

            myController.setItems(items);
            myController.setDefaultAlgorithm(defaultAlgorithm);
            
            stage.setScene(scene);
            stage.show();

        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
            if (reader != null) {
                reader.close();
            }
        }

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
