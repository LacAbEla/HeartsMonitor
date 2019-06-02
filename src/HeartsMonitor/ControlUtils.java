package HeartsMonitor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
 * Clase de utilidades.
 * 
 * // TODO: mejorar el sistema que organiza el gridpane de conexiones.
 *
 * @author Alejandro Balaguer Calderón
 */
public class ControlUtils {

    private final GridPane panelConexiones;
    private final ArrayList<Receptor> receptores;
    
    private int panelX;
    private int panelY;
    
    public ControlUtils(GridPane panel) {
        panelConexiones = panel;
        receptores = new ArrayList<Receptor>();
        
        panelX = 0;
        panelY = 0;
    }
    
    
    
// Control de conexiones
    // Añade una conexión TCP o UDP en función de la variable booleana. También cra y muestra el panel de datos.
    public boolean anadirConexion(int puerto, String nombre, boolean esTCP){
        boolean exitoso = false;
        boolean puertoRepetido = false;
        boolean nombreRepetido = false;
        Receptor receptor;
        Pane panel;
        Text textoNombre = null;
        Text textoLatidos = null;
        ProgressBar barra = null;
        
        for(Receptor conexion : receptores){
            if(conexion.getPuerto() == puerto){
                puertoRepetido = true;
                break;
            }else if(conexion.getNombre().equals(nombre) && !nombre.isEmpty()){
                nombreRepetido = true;
                break;
            } // Nota para JA: breaks con función muy evidente que mejoran la eficiencia.
        }
        
        if(!puertoRepetido && !nombreRepetido){
            try {
                // Crear y añadir panel
                panel = FXMLLoader.load(getClass().getResource("FXMLPanel.fxml"));
                panel.setId(nombre);
                for(Node nodo : panel.getChildren()){
                    if(nodo.getId().equals("txtNombre"))
                        textoNombre = (Text)nodo;
                    else if(nodo.getId().equals("txtLatidos"))
                        textoLatidos = (Text)nodo;
                    else if(nodo.getId().equals("barLatidos"))
                        barra = (ProgressBar)nodo;
                }
                panelConexiones.add(panel, panelX, panelY);
                panelX++;
                if(panelX == 4){ // TODO. 
                    panelX = 0;
                    panelY++;
                }

                // Crear y añadir hilo
                if(esTCP)
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
        }
        else{
            String mensaje = "Dos conexiones no pueden tener el mismo ";
            if(puertoRepetido)
                mensaje += "puerto";
            else
                mensaje += "nombre";
            mensaje += ". Introduce otro.";
            
            ControlUtils.alertarError("Error en la entrada", mensaje);
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
    
    
    // Detiene y elimina una conexión.
    public void borrarConexion(String nombre){
        Receptor receptorABorrar = null;
        
        for(Receptor conexion : receptores){
            if(conexion.getNombre().equals(nombre)){
                conexion.detener();
                panelConexiones.getChildren().remove(conexion.getPanel());
                receptorABorrar = conexion;
                break; // Nota para JA: break que mejora la eficiencia. También evitará que se borren 2 conexiones si de alguna forma llegan a haber 2 con el mismo nombre.
                       // Además su función es bastante obvia a simple vista, por lo que no es código espagueti. Este break no es malvado, ámalo.
            }
        }
        if(receptorABorrar != null)
            receptores.remove(receptorABorrar);
        
        guardarDatos();
    }
    

    // Cierra todos los hilos receptores.
    public void detenerReceptores(){
        log("Deteniendo conexiones...");
        
        // Detiene todos los hilos receptores
        for(Receptor hilo : receptores)
            hilo.detener();
            
        // Dar tiempo de cerrarse a los hilos.
        try {Thread.sleep(Receptor.TIMEOUT + 500);} catch (InterruptedException e) {}
        
        log("Conexiones cerradas.");
    }
    
    
    
    
// Carga y guardado de datos
    // Carga a la aplicación los datos del fichero de configuración.
    public boolean cargarDatos(){
        boolean exitoso = false;
        
        try{
            ObjectInputStream entrada = new ObjectInputStream(new BufferedInputStream(new FileInputStream(Global.FICHERO_DATOS)));
            
            Global.config = (Config)entrada.readObject();
            
            ReceptorSerializable receptor;
            while((receptor = (ReceptorSerializable)entrada.readObject()) != null)
                Global.utils.anadirConexion(receptor.getPuerto(), receptor.getNombre(), receptor.esTCP());
            
            entrada.close();
            exitoso = true;
            log("datos cargados con éxito.");
        }catch (FileNotFoundException e){
            ControlUtils.log("Archivo de configuración no encontrado.");
        }catch (IOException e){
            ControlUtils.log("Error de E/S al leer la configuración.", e);
        }catch (ClassNotFoundException e) {
            ControlUtils.log("Error al leer el contenido del archivo de configuración.", e);
        }
        
        return exitoso;
    }
    
    // Guarda los datos del programa (configuración y conexiones)
    public void guardarDatos(){
        try{
            ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(Global.FICHERO_DATOS));
            
            salida.writeObject(Global.config);
            for(Receptor receptor : receptores)
                salida.writeObject(new ReceptorSerializable(receptor.getPuerto(), receptor.getNombre(), receptor instanceof ReceptorTCP));
            salida.writeObject(null); // Marca el final del archivo
            
            salida.close();
            log("datos guardados con éxito.");
        }catch(IOException e){
            ControlUtils.log("Error de E/S al guardar la configuración.", e);
        }
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
