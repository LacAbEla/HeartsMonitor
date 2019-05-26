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
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author Alejandro Balaguer Calderón
 */
public class ControladorFXMLPanel implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Text txtNombre;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    public void btnBorrar_Click(){
        Global.getUtils().borrarConexion(txtNombre.getText());
    }
    
}
