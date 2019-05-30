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
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author aleba
 */
public class ControladorFXMLOpciones implements Initializable {

    @FXML
    private TextField inputBajo;
    @FXML
    private TextField inputAlto;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                inputBajo.setText(Global.config.getLatidosBajo() + "");
                inputAlto.setText(Global.config.getLatidosAlto() + "");
            }
        });
    }    
    
    
    public void onBtnGuardar_Click(){
        if(guardarConfig())
            cerrarVentana();
    }
    
    public void onBtnCancelar_Click(){
        cerrarVentana();
    }
    
    
    public boolean guardarConfig(){
        boolean exitoso = false;
        
        try{
            int bajo = Integer.parseInt(inputBajo.getText());
            int alto = Integer.parseInt(inputAlto.getText());
            
            if(!(-1 < bajo && bajo < 256 && -1 < alto && alto < 256))
                ControlUtils.alertarError("Error en la entrada", "Introduzca números entre 0 y 255.");
            else if(alto < bajo)
                ControlUtils.alertarError("Error en la entrada", "El límite inferior no puede ser mayor que el límite superior.");
            else if(alto-20 < bajo)
                ControlUtils.alertarError("Error en la entrada", "Debe haber al menos 20 latidos de margen entre los límites inferior y superior.");
            else{
                Global.config.setLatidosBajo(bajo);
                Global.config.setLatidosAlto(alto);
                Global.utils.guardarDatos();
                exitoso = true;
            }
        }catch(NumberFormatException e){
            ControlUtils.alertarError("Error en la entrada", "La entrada solo puede contener números.");
        }
        return exitoso;
    }
    
    public void cerrarVentana(){
        Stage stage = (Stage)inputBajo.getScene().getWindow();
        stage.close();
    }
}
