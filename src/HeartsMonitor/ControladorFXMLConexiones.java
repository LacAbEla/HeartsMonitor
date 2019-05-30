/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HeartsMonitor;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author aleba
 */
public class ControladorFXMLConexiones implements Initializable {

    @FXML
    private TextField inputPuerto;
    @FXML
    private TextField inputNombre;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Nada
    }
    
    
    public void onBtnAnadirTCPClick(){
        if(anadirConexionTCP())
            limpiarEntrada();
    }
    
    public void onBtnAnadirUDPClick(){
        if(anadirConexionUDP())
            limpiarEntrada();
    }
    
    
    // Al pulsar el botón "Añadir TCP"
    public boolean anadirConexionTCP(){
        boolean exitoso = false;
        
        int puerto = getPuerto();
        if(puerto != -1){
            exitoso = Global.utils.anadirConexionTCP(puerto, inputNombre.getText());
        }
        
        return exitoso;
    }
    
    // Al pulsar el botón "Añadir UDP"
    public boolean anadirConexionUDP(){
        boolean exitoso = false;
        
        if(inputNombre.getText().length() == 0)
            ControlUtils.alertarError("Error al añadir", "Inserte un nombre para la conexión.");
        else{
            int puerto = getPuerto();
            if(puerto != -1){
                exitoso = Global.utils.anadirConexionUDP(puerto, inputNombre.getText());
            }
        }
        
        return exitoso;
    }
    
    // Devuelve el puerto introducido por el usuario. Devolverá -1 si el puerto no es válido.
    private int getPuerto(){
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
    
    // Vacía las dos casillas de introducción de datos.
    private void limpiarEntrada(){
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                inputPuerto.setText("");
                inputNombre.setText("");
            }
        });
    }
    
}
