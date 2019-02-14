/*
 * Notas:
 * JavaFX tiene un hilo para la vista controlado por él y es extremadamente protectivo con este.
 * No solo no puedes controlar su código (solo el principio) sino que además no puedes tocar ningún objeto de la vista con otro hilo porque se pone nervioso y te lo revienta (el hilo ajeno).
 * Es por ello que hace falta usar Platform.runLater(Runnable). Este método le pasa un ejecutable al hilo de la vista, el cual lo ejecutará en el próximo espacio disponible (entre actualización y actualización de lo visual).


Utilizar tipos de datos pequeños (byte, short) no sirve para ahorrar espacio si la variable está sola:
 - Todos los campos de una clase ocupan al menos 1 "slot", cada uno de los cuales es de 32 bits (el tamaño de un int). Los tipos de dato largos (long, double) ocupan 2 slots.
 - La JVM solo puede hacer operaciones con int/float y long/double.
https://stackoverflow.com/questions/27122610/why-does-the-java-api-use-int-instead-of-short-or-byte#27123302

TODO: usar loggers en lugar de prints
TODO: usar loggers para indicar cuando se abre o cierra una conexion de ReceptorTCP
 */
package HeartsMonitor;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Alejandro Balaguer Calderón
 */
public class Monitor extends Application{
    
    private static Controlador controlador;
    private static GridPane grid;
    
    @Override
    //Líneas ejecutadas por el hilo de la vista al iniciar la aplicación JavaFX.
    //A partir del final de start() el hilo de la vista pasa a controlar la vista y no puede usarse para la aplicación.
    public void start(Stage stage) throws IOException, InterruptedException {
        Parent root = FXMLLoader.load(getClass().getResource("FXML.fxml"));
        Scene scene = new Scene(root);
        grid = (GridPane)scene.lookup("#brepi");
        stage.setScene(scene);
        stage.show();
        
        controlador = new Controlador(this, scene);
        new Thread(controlador).start();
    }
    
    //Añade un panel al elemento principal TODO
    public void anadirPanel(Pane panel){
        ObservableList<Node> children = panel.getChildren();
        for(Node node : children)
            System.out.println(node.getId());
        Platform.runLater(new Runnable() {
            public void run() {
                grid.add(panel, 1, 0);
            }
        });
    }
    
    //Borra el panel ID del elemento principal TODO
    public void borrarPanel(String ID){
        Platform.runLater(new Runnable() {
            public void run() {
                //root.getChildren().remove(paneladsdads);
            }
        });
    }
    
    private static void cerrar(){
        controlador.detener();
    }
    
    
    //No se utiliza en JavaFX, pero no ponerlo dará error en ciertos entornos, como al ejecutar en el IDE.
    public static void main(String[] args) {
        launch(args);
    }
}
