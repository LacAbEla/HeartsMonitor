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
 * Clase base para la recepción de datos
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
    
    private static volatile Clip sonido; // Volatile es como synchronized pero para variables, aunque no funcionan exactamente igual.
    
    //posible PLACEHOLDER
    protected Pane panel;
    protected Text textoNombre, textoLatidos;
    protected ProgressBar barra;
    
    public Receptor(int puerto, String nombre, Pane panel, Text textoNombre, Text textoLatidos, ProgressBar barra) {
        ejecutarse = true; // Se mantiene true para indicarle al hilo que se crea más adelante que debe continuar funcionando.
        latidos = -1;
        this.puerto = puerto;
        this.nombre = nombre;
        timeoutConexion = 5000;
        mostrarAlerta = false; // Se mantendrá false hasta que se establezca la conexión por primera vez.
        
        // TODO: incluir un sonido con el programa
        // Si el Clip para el sonido no ha sido iniciado, hacerlo.
        if(sonido == null){
            try{
                sonido = (Clip)AudioSystem.getLine(new Line.Info(Clip.class));
                sonido.open(AudioSystem.getAudioInputStream(new File("C:\\Windows\\media\\chord.wav")));
            }catch(Exception e){
                sonido = null;
                ControlUtils.alertarError("Error de sonido", "Ha habido un error al intentar añadir la función de alerta sonora.\nInformación para el desarrollador:\n" + e.toString());
            }
        }
        
        // posible PLACEHOLDER
        this.panel = panel;
        this.textoNombre = textoNombre;
        this.textoLatidos = textoLatidos;
        this.barra = barra;
        
        onNombreChanged();
    }
    
    
    
    
// Código a ejecutar cuando cambia el número de latidos/minuto
    protected void onLatidosChanged(){
        // PLACEHOLDER
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                // Ejecutar código en función del estado de los latidos
                if(latidos<Global.getLatidosBajo() || latidos>Global.getLatidosAlto() || latidos==-1)
                    onLatidosEmergencia();
                else if(latidos<Global.getLatidosBajo()+10 || latidos>Global.getLatidosAlto()-15)
                    onLatidosAviso();
                else
                    onLatidosBien();
                
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
    
    protected void onLatidosEmergencia(){
        panel.setStyle("-fx-background-color:red;");
        alertar();
    }
    
    protected void onLatidosAviso(){
        panel.setStyle("-fx-background-color:yellow;");
    }
    
    protected void onLatidosBien(){
        panel.setStyle("-fx-background-color:green;");
        mostrarAlerta = true; // Vuelve a permitir mostrar una alerta después de entrar en un periodo de valores anormales
                              // (eso evita que aparezca la alerta cada vez que se ejecuta onLatidosChanged).
    }
    
    
    
    
    // Ejecuta las acciones de alerta
    // SOLO LLAMAR DESDE HILOS JavaFX (usar Platform.runLater).
    protected void alertar(){
        boolean tieneSonido = false; // Indica si este diálogo utiliza el sonido. El sonido solo se utilizará por un diálogo a la vez.
        
        if(mostrarAlerta){
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("¡EMERGENCIA!");
            if(latidos == -1)
                alerta.setHeaderText("¡El dispositivo " + nombre + " se ha desconectado o no responde!");
            else if(latidos<45)
                alerta.setHeaderText("¡Las pulsaciones de " + nombre + " son demasiado bajas!");
            else
                alerta.setHeaderText("¡Las pulsaciones de " + nombre + " son demasiado altas!");

            // Reinicia el estado del sonido y comienza a reproducirlo. No lo hace si el sonido ya ha sido iniciado por otro diálogo.
            // De esta forma el sonido siempre se controla por el primer diálogo. El showAndWait se aplica a toda la aplicación, por lo que
            // el diálogo del sonido estará bloqueado (no se podrá cerrar) hasta que no se cierren todos diálogos que haya por encima.
            if(!sonido.isRunning()){
                sonido.setFramePosition(0);
                sonido.loop(Clip.LOOP_CONTINUOUSLY);
                tieneSonido = true;
            }

            mostrarAlerta = false;
            alerta.showAndWait();
            
            // Detiene el sonido solamente si ha sido iniciado por este hilo.
            if(tieneSonido)
                sonido.stop();
        }
    }
    
    
    
    
    protected void onNombreChanged(){
        textoNombre.setText(nombre);
    }
    
    // Ordena al receptor que se detenga.
    public void detener(){
        ejecutarse = false;
    }
    
    // Mostrar por la consola información sobre el funcionamiento interno de la conexión
    protected void log(String texto){
        ControlUtils.logBasico("LOG en " + this.toString() + ": " + texto);
    }
    protected void log(String texto, Exception e){
        ControlUtils.logBasico("LOG en " + this.toString() + ": " + texto, e);
    }
    
    @Override
    public String toString(){
        return "'" + nombre + "' (" + puerto + ")";
    }
    
    
    
// Setters, getters y similares
    public int getLatidos(){
        return latidos;
    }
    
    public int getPuerto(){
        return puerto;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public void setNombre(String valor){
        nombre = valor;
        onNombreChanged();
    }
    
    public Pane getPanel(){
        return panel;
    }
    
    public boolean isEjecutando(){
        return ejecutarse;
    }
}
