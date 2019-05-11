/*
 * Clase abstracta para mantener las propiedades b'asicas de los receptores.

TODO: añadir el control temporal (aviso cuando pasen más de 3 segundos sin nuevos datos, etc...)
 */
package HeartsMonitor;

import java.io.File;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;

/**
 *
 * @author aleba
 */
public abstract class Receptor implements Runnable {
    
    protected boolean ejecutarse; // Esta variable solo se modificará para ponerla en falso. Por tanto, no es necesario sincronizar su uso entre los hilos.
    protected int latidos; // Latidos por minuto del paciente. -1 indica que no hay datos fiables.
    protected int puerto;
    protected String nombre;
    protected int timeoutConexion; // Tiempo de respuesta máximo (en milisegundos) para la recepción de datos.
    protected boolean mostrarAlerta; // Se utiliza para evitar mostrar un aviso de desconexión/latidos anormales mientras se espera a establecer la conexión por primera vez.
    
    private Clip sonido;
    
    //posible PLACEHOLDER
    protected Pane panel;
    protected Text textoNombre, textoLatidos;
    protected ProgressBar barra;

    
    public Receptor(int puerto, String nombre, Pane panel, Text textoNombre, Text textoLatidos, ProgressBar barra) {
        ejecutarse = true; // Se mantiene true para indicarle al hilo que se crea más adelante que debe continuar funcionando.
        latidos = -1;
        this.puerto = puerto;
        this.nombre = nombre;
        timeoutConexion = 3000;
        mostrarAlerta = false; // Se mantendrá false hasta que se establezca la conexión por primera vez.
        
        // TODO MOVER A VARIABLES GLOBALES O ALGO
        // TODO: incluir un sonido con el programa
        try{
            sonido = (Clip)AudioSystem.getLine(new Line.Info(Clip.class));
            sonido.open(AudioSystem.getAudioInputStream(new File("C:\\Windows\\media\\chord.wav")));
        }catch(Exception e){
            
        }
        
        // posible PLACEHOLDER
        this.panel = panel;
        this.textoNombre = textoNombre;
        this.textoLatidos = textoLatidos;
        this.barra = barra;
        
        onNombreChanged();
    }
    
    
    public int getLatidos(){
        return latidos;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public void setNombre(String valor){
        nombre = valor;
        onNombreChanged();
    }
    
    public boolean isEjecutando(){
        return ejecutarse;
    }
    
    // Código a ejecutar cuando cambia el número de latidos/minuto
    protected void onLatidosChanged(){
        // PLACEHOLDER
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                // Actualizar color:
                // menos de 30, más de 150 y -1 --> rojo
                // 31-49 y 121-150 --> amarillo
                // 50-120 --> verde
                if(latidos<45 || latidos>150 || latidos==-1){
                    panel.setStyle("-fx-background-color:red;");
                    alertar();
                }else if(latidos<50 || latidos>120)
                    panel.setStyle("-fx-background-color:yellow;");
                else{
                    panel.setStyle("-fx-background-color:green;");
                    if(!mostrarAlerta)
                        mostrarAlerta = true; // Vuelve a permitir mostrar una alerta después de entrar en un periodo de valores anormales
                }                             // (eso evita que aparezca la alerta cada vez que se ejecuta onLatidosChanged).
                
                //Actualizar número de latidos y barra
                if(latidos == -1){
                    textoLatidos.setText("¡DESCONECTADO!");
                    barra.setProgress(-1);
                }else{
                    textoLatidos.setText(latidos+"");
                    barra.setProgress(latidos/255.0);
                }
            }
        });
    }
    
    protected void onNombreChanged(){
        textoNombre.setText(nombre);
    }
    
    // Ejecuta las acciones de alerta
    // SOLO LLAMAR DESDE EL HILO JavaFX (usar Platform.runLater).
    protected void alertar(){
        if(mostrarAlerta){
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("¡EMERGENCIA!");
            if(latidos == -1) // TODO: esto deberia mostrar el nombre de mostrar si es una conexion tcp. O lo sobreescribo o me cargo ese tipo de nombre. me da que lo sobreescribire.
                alerta.setHeaderText("¡El dispositivo " + nombre + " se ha desconectado o no responde!");
            else if(latidos<45)
                alerta.setHeaderText("¡Las pulsaciones de " + nombre + " son demasiado bajas!");
            else
                alerta.setHeaderText("¡Las pulsaciones de " + nombre + " son demasiado altas!");

            sonido.setFramePosition(0);
            sonido.loop(Clip.LOOP_CONTINUOUSLY);
            mostrarAlerta = false;
            alerta.showAndWait();
            sonido.stop();
        }
    }
    
    // Ordena al receptor que se detenga.
    public abstract void detener();
}
