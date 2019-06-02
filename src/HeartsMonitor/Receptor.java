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
    
    private static final String CSS_PANEL = "-fx-border-style: solid; -fx-border-color: #F4F4F4;";
    public static final int TIMEOUT = 6000; // Tiempo de respuesta máximo (en milisegundos) para la recepción de datos.
    
    protected boolean ejecutarse; // Esta variable solo se modificará para ponerla en falso. Por tanto, no es necesario sincronizar su uso entre los hilos.
    protected int latidos; // Latidos por minuto del paciente. -1 indica que no hay datos fiables.
    protected int puerto;
    protected String nombre;
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
        mostrarAlerta = false; // Se mantendrá false hasta que se establezca la conexión por primera vez.
        
        // Si el Clip para el sonido no ha sido iniciado, hacerlo.
        if(sonido == null){
            try{
                sonido = (Clip)AudioSystem.getLine(new Line.Info(Clip.class));
                sonido.open(AudioSystem.getAudioInputStream(new File(Global.FICHERO_AUDIO)));
            }catch(Exception e){
                sonido = null;
                ControlUtils.alertarError("Error de sonido", "Ha habido un error al intentar añadir la función de alerta sonora.\nInformación para el desarrollador:\n" + e.toString());
            }
        }
        
        this.panel = panel;
        this.textoNombre = textoNombre;
        this.textoLatidos = textoLatidos;
        this.barra = barra;
        
        onNombreChanged();
    }
    
    
    
    
// Código a ejecutar cuando cambia el número de latidos/minuto
    protected void onLatidosChanged(){
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                // Ejecutar código en función del estado de los latidos
                if(latidos<Global.config.getLatidosBajo() || latidos>Global.config.getLatidosAlto() || latidos==-1)
                    onLatidosEmergencia();
                else if(latidos<Global.config.getLatidosBajo()+10 || latidos>Global.config.getLatidosAlto()-15)
                    onLatidosAviso();
                else
                    onLatidosBien();
                
                //Actualizar número de latidos y barra
                if(latidos == -1){
                    textoLatidos.setText("¡DESCONECTADO!");
                    barra.setProgress(-1);
                }else{
                    textoLatidos.setText(latidos+"");
                    if(latidos < 71)
                        barra.setProgress(latidos/130.0);
                    else
                        barra.setProgress(0.54 + ((latidos-70)/2.0)/256);
                }
            }
        });
    }
    
    protected void onLatidosEmergencia(){
        panel.setStyle(CSS_PANEL + "-fx-background-color: red;");
        alertar();
    }
    
    protected void onLatidosAviso(){
        panel.setStyle(CSS_PANEL + "-fx-background-color: yellow;");
    }
    
    protected void onLatidosBien(){
        panel.setStyle(CSS_PANEL + "-fx-background-color: green;");
        mostrarAlerta = true; // Vuelve a permitir mostrar una alerta después de entrar en un periodo de valores anormales
                              // (eso evita que aparezca la alerta cada vez que se ejecuta onLatidosChanged).
    }
    
    
    
    
    // Ejecuta las acciones de alerta
    // SOLO LLAMAR DESDE HILOS JavaFX (usar Platform.runLater).
    protected void alertar(){
        boolean tieneSonido = false; // Indica si este diálogo utiliza el sonido. El sonido solo se utilizará por un diálogo a la vez.
        
        if(ejecutarse && mostrarAlerta){
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
