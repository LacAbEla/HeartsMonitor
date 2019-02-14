/*
 * Hilo encargado de mantener la comunicación UDP con un dispositivo de entrada.
 * Estará siempre escuchando.
 */
package HeartsMonitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import javafx.scene.Node;

/**
 *
 * @author Alejandro Balaguer Calderón
 */
public class ReceptorUDP extends Receptor {
    
    private final DatagramSocket conexion;

    public ReceptorUDP(int puerto, String nombre, Node panel, Node texto, Node barra) throws SocketException {
        super(puerto, nombre, panel, texto, barra);
        this.conexion = new DatagramSocket(puerto);
        System.out.println("Hilo iniciado para " + nombre + ".");
    }
    
    
    @Override
    //Código del hilo
    public void run(){
        ejecutarse = true;
        
        //Bucle de ejecución. Espera una respuesta y actualiza los latidos.
        while(ejecutarse){
            onLatidosChange();
            try{
                //Nota: el buffer (new byte[1]) y paquete.getData() son lo mismo.)
                DatagramPacket paquete = new DatagramPacket(new byte[1], 1);
                conexion.receive(paquete);
                latidos = paquete.getData()[0]+128; //Al enviarse un byte por UDP en java se transmite con signo. Hace falta operar para eliminarlo.
                onLatidosChange();
            }catch(IOException e){
                System.out.println("Error al leer un paquete UDP de " + nombre + ".");
                e.printStackTrace();
            }
        }
    }
    
    @Override
    //Detiene el receptor
    public void detener(){
        ejecutarse = false;
        conexion.close();
        System.out.println("Hilo de " + nombre + " cerrado.");
    }
}
