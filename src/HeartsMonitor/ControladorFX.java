/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

// TODO que el botón de cerrar lo cierre todo
// TODO que se puedan borrar conexiones
 */
package HeartsMonitor;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author Alejandro Balaguer Calderón
 */
public class ControladorFX implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private GridPane panelConexiones;
    @FXML
    private TextField inputPuerto;
    @FXML
    private TextField inputNombre;
    
    //private Scene scene = null;
    private ControlUtils utils;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        utils = new ControlUtils(panelConexiones);
        
        //Pruebas
        //utils.anadirConexionUDP(12345, "testUDP");
        //utils.anadirConexionTCP(12345, "testTCP");
    }
    
    public void anadirConexionTCP(){
        utils.anadirConexionTCP(Integer.parseInt(inputPuerto.getText()), inputNombre.getText());
    }
    
    public void anadirConexionUDP(){
        utils.anadirConexionUDP(Integer.parseInt(inputPuerto.getText()), inputNombre.getText());
    }
    
    //Código a ejecutar para detener el controlador. Ejecutado por la clase principal.
    public void detener(){
        utils.detenerReceptores();
    }
    
    
    /*
    Conservado por si hace falta en un futuro.
    public void setScene(Scene scene){
        this.scene = scene;
        utils.setScene(scene);
    }
    */
    
}
