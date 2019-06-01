/*
 * Notas:
 * JavaFX tiene un hilo para la vista controlado por él y es extremadamente protectivo con este.
 * No solo no puedes controlar su código (solo el principio) sino que además no puedes tocar ningún objeto de la vista con otro hilo porque provoca errores y posibles fallos visuales.
 * Es por ello que hace falta usar Platform.runLater(Runnable). Este método le pasa un ejecutable al hilo de la vista, el cual lo ejecutará en el próximo espacio disponible (entre actualización y actualización de lo visual).
 * Estos ejecutables se ejecutarán por separado, siendo sus propios hilos. Aun así, creo que su código solo se ejecutará por el hilo JavaFX.


Utilizar tipos de datos pequeños (byte, short) no sirve para ahorrar espacio si la variable está sola:
 - Todos los campos de una clase ocupan al menos 1 "slot", cada uno de los cuales es de 32 bits (el tamaño de un int). Los tipos de dato largos (long, double) ocupan 2 slots.
 - La JVM solo puede hacer operaciones con int/float y long/double.
https://stackoverflow.com/questions/27122610/why-does-the-java-api-use-int-instead-of-short-or-byte#27123302

TODO: permitir cambiar un poco el tamaño de la ventana principal
TODO: bug de baja importancia. Si la conexion aún no tiene nombre y se intenta borrar podría borrarse una sin nombre distinta por el conflicto de ID (ambos nombres son "").
 */
package HeartsMonitor;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de JavaFX. Inicializa la aplicación.
 * 
 * @author Alejandro Balaguer Calderón
 */
public class Monitor extends Application{
    
    private ControladorFXML controlador;
    private Global global; // Al dejar aquí una referencia a global me aseguro de que el recolector de basura no borra sus datos.
    
    //Líneas ejecutadas por el hilo de la vista al iniciar la aplicación JavaFX.
    //A partir del final de start() el hilo de la vista pasa a controlar la vista y no puede usarse para la aplicación.
    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML.fxml"));
        Parent root = loader.load();
        controlador = loader.getController();
        Scene scene = new Scene(root);
        global = new Global();
               
        stage.setScene(scene);
        stage.setTitle("HeartsMonitor - Vista de conexiones [v2.0-alpha]");
        stage.setResizable(false);
        stage.show();
    }
    
    
    //Líneas ejecutadas por el hilo de la vista cuando la aplicación se queda sin ventanas
    @Override
    public void stop(){
        global.utils.guardarDatos();
        global.utils.detenerReceptores();
    }
    
    
    //No se utiliza en JavaFX, pero no ponerlo dará error en ciertos entornos, como al ejecutar en algunos IDE.
    public static void main(String[] args) {
        launch(args);
    }
}
