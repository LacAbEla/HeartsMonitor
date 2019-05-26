/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

// TODO: si se añade una TCP sin nombre para recibir uno remoto no se comprueba que el nombre no este repetido. arreglar.
// TODO: arreglar la organización visual del gridpane de conexiones
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
public class ControladorFXML implements Initializable {

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
        Global.setUtils(utils);

        
        //Pruebas
        //utils.anadirConexionUDP(12345, "testUDP");
        //utils.anadirConexionTCP(12346, "testTCP");
        
        
    }
    
    // Al pulsar el botón "Añadir TCP"
    public void anadirConexionTCP(){
        int puerto = getPuerto();
        if(puerto != -1)
            utils.anadirConexionTCP(puerto, inputNombre.getText());
    }
    
    // Al pulsar el botón "Añadir UDP"
    public void anadirConexionUDP(){
        if(inputNombre.getText().length() == 0)
            ControlUtils.alertarError("Error al añadir", "Inserte un nombre para la conexión.");
        else{
            int puerto = getPuerto();
            if(puerto != -1)
                utils.anadirConexionUDP(puerto, inputNombre.getText());
        }
    }
    
    // Devuelve el puerto introducido por el usuario. Devolverá -1 si el puerto no es válido.
    public int getPuerto(){
        int resultado = -1;
        
        if(inputPuerto.getText().equals(""))
            ControlUtils.alertarError("Error al añadir", "Inserte un puerto en el que escuchar.");
        else{
            try{
                int puerto = Integer.parseInt(inputPuerto.getText());
                if(1023 < puerto && puerto < 65536)
                    resultado = puerto;
                else
                    ControlUtils.alertarError("Error al añadir: puerto de uso habitual", "Inserte un puerto entre 1024 y 65535.");
            }catch(NumberFormatException e){
                ControlUtils.alertarError("Error al añadir", "El puerto debe ser un número.");
            }
        }
        
        return resultado;
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
