/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HeartsMonitor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 *
 * @author Alejandro Balaguer Calderón
 */
public class Controlador implements Runnable {

    private final int puerto;
    private ServerSocket servidor;
    private ArrayList<HiloReceptor> receptores;
    private Thread hiloControlador; //Es el hilo que ejecuta el run() de este controlador

    public Controlador(int puerto) {
        this.puerto = puerto;
        receptores = new ArrayList<HiloReceptor>();
    }
    
    
    //Para cerrar el controlador debe usarse thread.interrupt.
    @Override
    public void run() {
        try{
            
            hiloControlador = Thread.currentThread();
            servidor = new ServerSocket(puerto);
            
            //Bucle para aceptar peticiones
            while(true){
                Socket conexion = servidor.accept();
                HiloReceptor receptor = new HiloReceptor(conexion, "test");
                receptores.add(receptor);
                Thread hilo = new Thread(receptor);
                hilo.start();
                System.out.println("dep");
            }
            
        }catch(SocketException e){
            System.out.println("Error de socket/Servidor cerrado");
            e.printStackTrace();
        }catch(IOException e){
            System.out.println("\n\nError en el controlador.\n");
            e.printStackTrace();
        }
    }
    
    public HiloReceptor[] getConexiones(){
        return receptores.toArray(new HiloReceptor[0]);
    }
    
    //Detiene el controlador y cierra todos los hilos receptores.
    public void detener(){
        System.out.println("Deteniendo controlador...");
        
        //Cierra la conexión, provocando el cierre del hilo del controlador a lo bruto. TODO: Mejorar sistema de cierre.
        try{
            servidor.close();
        }catch(IOException e){
            System.out.println("Error al cerrar la conexión del servidor.");
            e.printStackTrace();
        }
        
        //Hacer que el hilo principal (el que está ejecutando detener()) espere a que el hilo del controlador se detenga.
        //Si no se detiene en 30 segundos continua igualmente. Si se da el caso seguramente el hilo controlador se haya atascado en algo y se puedan cerrar las conexiones sin riesgo.
        //Nota: esto no hara nada teniendo en cuenta el sistema de mierda que uso ahora mismo para cerrar el hilo
        try{
            hiloControlador.join(30000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        //Cerrar todos los hilos receptores
        for(HiloReceptor receptor : receptores){
            receptor.detener();
        }
        
        System.out.println("Controlador detenido.");
    }
}
