/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

// TODO: si se añade una TCP sin nombre para recibir uno remoto no se comprueba que el nombre no este repetido.
 */
package HeartsMonitor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Alejandro Balaguer Calderón
 */
public class ControladorFXML implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private GridPane panelConexiones;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Global.utils = new ControlUtils(panelConexiones);

        // Cargar datos
        if(!Global.utils.cargarDatos()){
            ControlUtils.log("Error al cargar datos. Cargados datos por defecto.");
            Global.config = new Config(45, 150);
        }
    }
    
    
    // Muestra un menú
    public void abrirMenu(String FXMLMenu, String tituloVentana){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLMenu));
            Stage stage = new Stage();
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            stage.setTitle(tituloVentana);
            stage.setResizable(false);
            stage.show();
        } catch (IOException ex) {
            ControlUtils.alertarError("Error E/S", "Error de E/S al cargar el menú de " + tituloVentana.toLowerCase() + ".");
            ControlUtils.log("ERROR al cargar " + FXMLMenu, ex);
        }
    }
    
    // Muestra el menú de añadido de conexiones
    public void abrirMenuConexiones(){
        abrirMenu("FXMLConexiones.fxml", "Añadido de conexiones");
    }
    
    // Muestra el menú de opciones
    public void abrirMenuOpciones(){
        abrirMenu("FXMLOpciones.fxml", "Opciones");
    }
}
