/*
 * Hilo encargado de mantener la comunicación UDP con un dispositivo de entrada
 */
package HeartsMonitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 *
 * @author Alejandro Balaguer Calderón
 */
public class ReceptorUDP extends Receptor {
    
    private final DatagramSocket conexion;

    public ReceptorUDP(int puerto, String nombre) throws SocketException {
        super(puerto, nombre);
        this.conexion = new DatagramSocket(puerto);
        System.out.println("Hilo iniciado para " + nombre + ".");
    }
    
    
    @Override
    //Código del hilo
    public void run(){
        ejecutarse = true;
        try{
            //Bucle de ejecución. Espera una respuesta y actualiza los latidos.
            while(ejecutarse){
                byte[] buffer = new byte[1];
                DatagramPacket paquete = new DatagramPacket(buffer, 1);
                conexion.receive(paquete);
                latidos = paquete.getData()[0]+128; //Al enviarse un byte por UDP en java se transmite con signo. Hace falta operar para eliminarlo.
            }
        }catch(IOException e){
            e.printStackTrace();
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
