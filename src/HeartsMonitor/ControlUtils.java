/*
 * To change this license header, choose License Headers in Project Properties.

Para cerrar el controlador debe usarse thread.interrupt. Esto pide al hilo que se cierre, pero no lo fuerza.
 */
package HeartsMonitor;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 *
 * @author Alejandro Balaguer Calderón
 */
public class ControlUtils {

    private final GridPane panelConexiones;
    private final ArrayList<Receptor> receptores;
    
    // PLACEHOLDER
    private int panelX;
    private int panelY;
    
    public ControlUtils(GridPane panel) {
        panelConexiones = panel;
        receptores = new ArrayList<Receptor>();
        
        panelX = 0;
        panelY = 0;
    }
    
    // Añade una conexión TCP o UDP en función de la variable booleana. (true -> TCP, false -> UDP)
    private boolean anadirConexion(int puerto, String nombre, boolean TCP){
        Receptor receptor;
        Pane panel;
        Text textoNombre = null;
        Text textoLatidos = null;
        ProgressBar barra = null;
        
        if(nombre.equals(""))
            nombre = null;
        
        // PLACEHOLDER, es una versión poco pulida de lo que (seguramente) será en el futuro
        try {
            // Crear y añadir panel
            panel = FXMLLoader.load(getClass().getResource("FXMLPanel.fxml"));
            panel.setId(nombre);
            for(Node nodo : panel.getChildren()){
                //Nota: el primer nodo de tipo texto es el nombre del dispositivo.
                //el segundo es el numero de latidos
                if(nodo instanceof Text){
                    if(textoNombre == null)
                        textoNombre = (Text)nodo;
                    else
                        textoLatidos = (Text)nodo;
                }
                else if(nodo instanceof ProgressBar)
                    barra = (ProgressBar)nodo;
            }
            panelConexiones.add(panel, panelX, panelY);
            panelX++;
            if(panelX == 2){
                panelX = 0;
                panelY++;
            }
            
            // Crear y añadir hilo
            if(TCP)
                receptor = new ReceptorTCP(puerto, nombre, panel, textoNombre, textoLatidos, barra);
            else
                receptor = new ReceptorUDP(puerto, nombre, panel, textoNombre, textoLatidos, barra);
            receptores.add(receptor);
            Thread hilo = new Thread(receptor);
            hilo.start();
            System.out.println("Hilo iniciado.");
            return true;
        } catch (IOException e) {
            System.out.println("\n\nError al leer FXMLPanel.fxml.\n");
            e.printStackTrace();
        }
        return false;
    }
    
    // Añade una conexión TCP.
    public boolean anadirConexionTCP(int puerto, String nombre){
        return anadirConexion(puerto, nombre, true);
    }
    
    // Añade una conexión UDP.
    public boolean anadirConexionUDP(int puerto, String nombre){
        return anadirConexion(puerto, nombre, false);
    }

    // Cierra todos los hilos receptores.
    public void detenerReceptores(){
        System.out.println("Deteniendo conexiones...");
        
        // Detiene todos los hilos receptores
        for(Receptor hilo : receptores)
            hilo.detener();
            
        // Espera de 3 segundos para dar tiempo a los hilos de cerrarse.
        // TODO tener en cuenta que algunos hilos podrían estar en la espera de 5 segundos por puerto ocupado.
        // Habría que interrumpirlos, esperar más tiempo o hacer que esperen menos entre intenros.
        try {Thread.sleep(3000);} catch (InterruptedException e) {}
        
        System.out.println("Conexiones cerradas.");
    }
    
    // Es, y solo es, posible que se haya vuelto inutil
    public Receptor[] getConexiones(){
        return receptores.toArray(new Receptor[0]);
    }
    
    // Muestra un mensaje de error al usuario
    public static void alertarError(String titulo, String mensaje){
        Platform.runLater(new Runnable(){
            public void run(){
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle(titulo);
                alerta.setHeaderText(mensaje);
                alerta.show();
            }
        });
    }
}
