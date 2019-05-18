/*
 * Hilo encargado de mantener la comunicacin TCP con un dispositivo de entrada
 * Si cae la conexión tratará de volver a conectarse.
 * Esta clase distingue entre nombre (nombre interno, a modo de ID) y nombre a mostrar (que sobreescribira al interno en la vista si esta configurado).
 */
package HeartsMonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * Clase para la recepción de datos mediante TCP
 * 
 * @author Alejandro Balaguer Calderón
 */
public class ReceptorTCP extends Receptor {
    
    private ServerSocket servidor;
    private Socket conexion;

    public ReceptorTCP(int puerto, String nombre, Pane panel, Text textoNombre, Text textoLatidos, ProgressBar barra) {
        super(puerto, nombre, panel, textoNombre, textoLatidos, barra);
    }
    
    
    @Override
    //Código del hilo
    public void run(){
        log("Hilo TCP iniciado.");
        ejecutarse = true;
        InputStream entrada;
        
        
        //Bucle para adquirir el puerto
        while(ejecutarse && servidor == null){
            try{
                servidor = new ServerSocket(puerto);
                servidor.setSoTimeout(timeoutConexion); // No estar más de X tiempo esperando peticiones de conexión.
            }catch(BindException e){
                //Puerto ocupado. El programa espera 5 segundos entre intentos de conexión.
                log("ERROR: El puerto " + puerto + " está ocupado.");
                try {Thread.sleep(5000);} catch (InterruptedException ex) {}
            }catch(IOException e){
                log("ERROR al intentar aceptar conexiones en el puerto " + puerto + ".", e);
            }
        } //TODO: si el puerto esta ocupado podria borrarse el dispositivo y listo
        // o casi mejor mostrarlo porque eso chocaría con lo de guardar dispositivos
        // mejor pensado: no vale la pena mostrarlo porque no lo entenderían si no tienen a alguien que sepa del tema.
        // mejor lo dejo en -1 y que llamen al soporte, que de todas formas es un fallo que no debería ser común
        
        
        //Bucle de ejecución. Establece una conexión, espera una respuesta y actualiza los latidos. Se detendrá cuando se ordene el cierre del hilo.
        while(ejecutarse){
            onLatidosChanged();
            try{
                conexion = establecerConexion();
                
                if(conexion != null){ // Check para asegurarse de que aún hay que seguir
                    entrada = conexion.getInputStream();

                    // Espera una respuesta y actualiza los latidos mientras se deba seguir ejecutando y la conexión no llegue a fallar 6 veces seguidas.
                    for(int errores = 0; ejecutarse && errores < 6; errores++){// Si hay más de 5 errores seguidos la conexión se considerará inestable y se reconectará.
                        try{
                            latidos = entrada.read();
                            errores = 0;
                        }catch(SocketTimeoutException e){
                            log("El dispositivo externo está tardando demasiado en responder.");
                            latidos = -1;
                        }catch(IOException e){
                            log("Error E/S al leer un paquete TCP.", e);
                            latidos = -1;
                        }
                        onLatidosChanged();
                    }
                    
                    // Cerrar la conexión
                    try{
                        conexion.close();
                    }catch(Exception e){
                        log("ERROR al cerrar la conexión TCP.", e);
                    }
                    log("Conexión finalizada.");
                }
            }catch(IOException e){
                log("ERROR al establecer conexión.", e);
            }
            latidos = -1;
        }//TODO: testear que todo en este método (run()) funciona correctamente
        
        log("Hilo TCP cerrado.");
    }
    
    //Establece la conexion con un dispositivo
    //NOTA: cuando se establece una conexion TCP el dispositivo envia una ID textual y luego los datos de latidos cada 500ms (no obligatorio).
    //Esta ID se utilizará para saber si el dispositivo es el adecuado o no. Si la ID esperada == null se adoptará la remota.
    private Socket establecerConexion() throws IOException{
        Socket nuevaConexion = null;
        
        // Intenta conseguir una conexión hasta que lo consigue o se ordena el cierre del hilo
        while(ejecutarse){
            try{
                // Intentar establecer conexión.
                nuevaConexion = servidor.accept();
                nuevaConexion.setSoTimeout(timeoutConexion);// No estar más de X tiempo esperando respuesta.
                log("Conexión TCP establecida.");
                
                // Aceptar o rechazar en función del nombre
                if(nombre == null){
                    //No hay nombre. Se adopta el de la conexión y se acepta.
                    nombre = leerNombre(nuevaConexion.getInputStream());
                    onNombreChanged();
                    log("Conexión TCP aceptada.");
                    return nuevaConexion;
                }else if(leerNombre(nuevaConexion.getInputStream()).equals(nombre)){
                    //Hay nombre. Se acepta la conexión al coincidir el nombre.
                    log("Conexión TCP aceptada.");
                    return nuevaConexion;
                }else{
                    //Hay nombre. Este no coincide y la conexión se rechaza.
                    nuevaConexion.close();
                    log("Conexión TCP rechazada.");
                }
            }catch(SocketTimeoutException e){
                // No hacer nada, pues es normal. TODO? podria meterlo en el log como informacion
            }catch(SocketException e){
                log("ERROR al establecer una conexión TCP.", e);
            }
        }
        log("Establecimiento de conexión TCP cancelado.");
        return null;
    }
    
    //Lee el nombre (ID) enviado por el dispositivo.
    private String leerNombre(InputStream is) throws IOException{
        BufferedReader entrada = new BufferedReader(new InputStreamReader(is));
        return entrada.readLine();
    }
}
