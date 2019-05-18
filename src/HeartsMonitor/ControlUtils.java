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
        boolean exitoso = false;
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
            exitoso = true;
        } catch (IOException e) {
            log("Error de E/S al leer FXMLPanel.fxml.", e);
        }
        return exitoso;
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
        log("Deteniendo conexiones...");
        
        // Detiene todos los hilos receptores
        for(Receptor hilo : receptores)
            hilo.detener();
            
        // Espera de 5,5 segundos para dar tiempo de cerrarse a los hilos.
        try {Thread.sleep(5500);} catch (InterruptedException e) {}
        
        log("Conexiones cerradas.");
    }
    
    // Es, y solo es, posible que se haya vuelto inutil
    public Receptor[] getConexiones(){
        return receptores.toArray(new Receptor[0]);
    }
    
    
    
    
// Métodos estáticos
    // Muestra un mensaje de error al usuario
    public static void alertarError(String titulo, String mensaje){
        Platform.runLater(new Runnable(){
            @Override
            public void run(){
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle(titulo);
                alerta.setHeaderText(mensaje);
                alerta.show();
            }
        });
    }
    
    // Mostrar por la consola información sobre el funcionamiento interno de la aplicación
    public static void log(String texto){
        System.out.println("LOG: " + texto + "\n");
    }
    public static void log(String texto, Exception e){
        System.out.println("LOG: " + texto);
        e.printStackTrace();
        System.out.println();
    }
    
    // Versión básica de log(), pensada para ser la base de otros comandos log()
    public static void logBasico(String texto){
        System.out.println(texto + "\n");
    }
    public static void logBasico(String texto, Exception e){
        System.out.println(texto);
        e.printStackTrace();
        System.out.println();
    }
}
