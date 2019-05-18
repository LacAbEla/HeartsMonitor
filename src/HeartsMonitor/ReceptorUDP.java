/*
 * Hilo encargado de mantener la comunicación UDP con un dispositivo de entrada.
 * Estará siempre escuchando.
 */
package HeartsMonitor;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * Clase para la recepción de datos mediante UDP
 *
 * @author Alejandro Balaguer Calderón
 */
public class ReceptorUDP extends Receptor {
    
    private DatagramSocket conexion;

    // TODO quitar la exception y mover el intento de conexion al bucle de ejecucion
    public ReceptorUDP(int puerto, String nombre, Pane panel, Text textoNombre, Text textoLatidos, ProgressBar barra)throws SocketException {
        super(puerto, nombre, panel, textoNombre, textoLatidos, barra);
    }
    
    
    @Override
    // Código del hilo
    public void run(){
        log("Hilo UDP iniciado.");
        ejecutarse = true;
        
        // Bucle de ejecución. Establece una conexión, espera una respuesta y actualiza los latidos. Se detendrá cuando se ordene el cierre del hilo.
        while(ejecutarse){
            onLatidosChanged();
            try{
                conexion = new DatagramSocket(puerto);
                conexion.setSoTimeout(timeoutConexion);
                log("Escucha UDP iniciada.");
                DatagramPacket paquete = new DatagramPacket(new byte[1], 1); //Nota: el buffer (new byte[1]) y paquete.getData() son lo mismo.)
                
                // Espera una respuesta y actualiza los latidos.
                while(ejecutarse){
                    try{
                        conexion.receive(paquete);
                        latidos = paquete.getData()[0]+128; // Al enviarse un byte por UDP en java se transmite con signo. Hace falta operar para eliminarlo.
                    }catch(SocketTimeoutException e){
                        log("El dispositivo externo está tardando demasiado en responder.");
                        latidos = -1;
                    }catch(IOException e){
                        log("Error de E/S al leer un paquete UDP.", e);
                        latidos = -1;
                    }
                    onLatidosChanged();
                }
                conexion.close();
            }catch(BindException e){
                //Puerto ocupado. El programa espera 5 segundos entre intentos de conexión.
                log("El puerto " + puerto + " está ocupado.", e);
                try {Thread.sleep(5000);} catch (InterruptedException ex) {}
            }catch(SocketException e){
                log("ERROR al establecer escucha UDP en el puerto " + puerto +  ".", e);
            }
            latidos = -1;
        }
        
        log("Hilo UDP cerrado.");
    }
}
