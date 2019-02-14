/*
 * Clase abstracta para mantener las propiedades b'asicas de los receptores.

TODO: añadir el control temporal (aviso cuando pasen más de 3 segundos sin nuevos datos, etc...)
 */
package HeartsMonitor;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 *
 * @author aleba
 */
public abstract class Receptor implements Runnable {
    
    protected boolean ejecutarse; //Esta variable solo se modificará para ponerla en falso. Por tanto, no es necesario sincronizar su uso entre los hilos.
    protected int latidos; //Almacena los latidos. -1 indica que no hay datos fiables.
    protected int puerto;
    protected String nombre;
    
    //PLACEHOLDER
    protected Pane panel;
    protected Text texto;
    protected ProgressBar barra;

    public Receptor(int puerto, String nombre, Node panel, Node texto, Node barra) {
        ejecutarse = false;
        latidos = -1;
        this.puerto = puerto;
        this.nombre = nombre;
        
        //PLACEHOLDER
        this.panel = (Pane)panel;
        this.texto = (Text)texto;
        this.barra = (ProgressBar)barra;
    }
    
    
    //Devolver el número de latidos de esta conexión
    public int getLatidos(){
        return latidos;
    }
    
    //Devuelve el nombre de esta conexión
    public String getNombre(){
        return nombre;
    }
    
    public void setNombre(String valor){
        nombre = valor;
    }
    
    public boolean isEjecutando(){
        return ejecutarse;
    }
    
    //Código a ejecutar cuando cambia el número de latidos
    public void onLatidosChange(){
        //PLACEHOLDER
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                texto.setText(latidos+"");
                if(latidos == -1)
                    barra.setProgress(-1);
                else
                    barra.setProgress(latidos/255.0);
                
                //menos de 30, más de 150 y -1 --> rojo
                //31-49 y 121-150 --> amarillo
                //50-120 --> verde
                if(latidos<40 || latidos>150 || latidos==-1)
                    panel.setStyle("-fx-background-color:red;");
                else if(latidos<50 || latidos>120)
                    panel.setStyle("-fx-background-color:yellow;");
                else
                    panel.setStyle("-fx-background-color:green;");
            }
        });
    }
    
    //Ordena al receptor que se detenga.
    public abstract void detener();
}
