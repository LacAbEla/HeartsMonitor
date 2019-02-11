/*
 * To change this license header, choose License Headers in Project Properties.

Para cerrar el controlador debe usarse thread.interrupt. Esto pide al hilo que se cierre, pero no lo fuerza.
 */
package HeartsMonitor;

import java.net.SocketException;
import java.util.ArrayList;

/**
 *
 * @author Alejandro Balaguer Calderón
 */
public class Controlador {

    private ArrayList<Receptor> receptores;

    public Controlador() {
        receptores = new ArrayList<Receptor>();
    }
    
    
    public Receptor[] getConexiones(){
        return receptores.toArray(new Receptor[0]);
    }
    
    public void anadirConexionTCP(int puerto, String nombre){
        ReceptorTCP receptor = new ReceptorTCP(puerto, nombre);
        receptores.add(receptor);
        Thread hilo = new Thread(receptor);
        hilo.start();
        System.out.println("Hilo TCP iniciado.");
    }
    
    public void anadirConexionUDP(int puerto, String nombre){
        ReceptorUDP receptor;
        try {
            receptor = new ReceptorUDP(puerto, nombre);
            receptores.add(receptor);
            Thread hilo = new Thread(receptor);
            hilo.start();
            System.out.println("Hilo UDP iniciado.");
        } catch (SocketException e) {
            System.out.println("\n\nError al añadir una conexin UDP.\n");
            e.printStackTrace();
        }
    }
    
    //Detiene el controlador y cierra todos los hilos receptores.
    public void detener(){
        System.out.println("Deteniendo controlador...");
        
        //Detiene todos los hilos receptores
        for(Receptor hilo : receptores)
            hilo.detener();
        
        
            //Hacer que el hilo principal (el que está ejecutando detener()) espere a que el hilo del controlador se detenga.
            //Si no se detiene en 30 segundos continua igualmente. Si se da el caso seguramente el hilo controlador se haya atascado en algo y se puedan cerrar las conexiones sin riesgo.
            //Nota: esto no hara nada teniendo en cuenta el sistema de mierda que uso ahora mismo para cerrar el hilo
            //try{
            //    hiloControlador.join(30000);
            //}catch (InterruptedException e) {
            //    e.printStackTrace();
            //}
            
        //se podria esperar 5 segundos o algo asi para que se cierre todo seguro
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {}
        
        System.out.println("Controlador detenido.");
    }
}
