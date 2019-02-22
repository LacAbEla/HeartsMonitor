/*
 * Hilo encargado de mantener la comunicación UDP con un dispositivo de entrada.
 * Estará siempre escuchando.
 */
package HeartsMonitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 *
 * @author Alejandro Balaguer Calderón
 */
public class ReceptorUDP extends Receptor {
    
    private DatagramSocket conexion;

    //TODO quitar la exception y mover el intento de conexion al bucle de ejecucion
    public ReceptorUDP(int puerto, String nombre, Pane panel, Text textoNombre, Text textoLatidos, ProgressBar barra)throws SocketException {
        super(puerto, nombre, panel, textoNombre, textoLatidos, barra);
        System.out.println("Hilo UDP iniciado para " + nombre + " en puerto " + puerto + ".");
    }
    
    
    @Override
    //Código del hilo
    public void run(){
        ejecutarse = true;
        
        //Bucle de ejecución. Establece una conexión, espera una respuesta y actualiza los latidos.
        while(ejecutarse){
            onLatidosChanged();
            try{
                conexion = new DatagramSocket(puerto);
                System.out.println("Escucha UDP iniciada en " + puerto + " para " + nombre + ".");
                DatagramPacket paquete = new DatagramPacket(new byte[1], 1); //Nota: el buffer (new byte[1]) y paquete.getData() son lo mismo.)
                
                try{
                    //Espera una respuesta y actualiza los latidos.
                    while(ejecutarse){
                        conexion.receive(paquete);
                        latidos = paquete.getData()[0]+128; //Al enviarse un byte por UDP en java se transmite con signo. Hace falta operar para eliminarlo.
                        onLatidosChanged();
                    }
                }catch(SocketException e){
                    //TODO hacer que esto solo ignore la excepción causada por "socket closed"
                }catch(IOException e){
                    System.out.println("\n\nError al leer un paquete UDP de " + nombre + ".\n");
                    e.printStackTrace();
                }
            }catch(SocketException e){
                //Puerto ocupado. El programa espera 5 segundos entre intentos de conexión.
                System.out.println("\n\nEl puerto (" + puerto + ") para " + nombre +  " está ocupado.\n");
                e.printStackTrace();
                try {Thread.sleep(5000);} catch (InterruptedException ex) {}
            }
        }
        
        //Código cuando se ha ordenado el cierre del hilo
        System.out.println("Hilo UDP de " + nombre + " cerrado.");
    }
    
    @Override
    //Detiene el receptor
    public void detener(){
        ejecutarse = false;
        conexion.close();
    }
}
