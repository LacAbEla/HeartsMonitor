/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HeartsMonitor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author aleba
 */
public class FXMLController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private GridPane panelConexiones;
    @FXML
    private Pane paneltcp;
    @FXML
    private Pane paneludp;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //Controlador controlador = new Controlador();
        //new Thread(controlador).start();
        //controlador.anadirConexionUDP(12345, "testUDP");
        System.out.println(panelConexiones);
        //controlador.anadirConexionTCP(12345, "testTCP", panelConexiones);
        
        /*
        //Esto funciona
        Pane panel;
        try {
            panel = FXMLLoader.load(getClass().getResource("FXMLPanel.fxml"));
            panel.setId("cacas");
            panelConexiones.add(panel, 1, 1);
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
    
    //PLACEHOLDER
    public void borrarTCP(){
        panelConexiones.getChildren().remove(paneltcp);
    }
    //PLACEHOLDER
    public void borrarUDP(){
        panelConexiones.getChildren().remove(paneludp);
    }
    
}
