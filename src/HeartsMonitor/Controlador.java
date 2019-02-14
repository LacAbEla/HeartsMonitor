/*
 * To change this license header, choose License Headers in Project Properties.

Para cerrar el controlador debe usarse thread.interrupt. Esto pide al hilo que se cierre, pero no lo fuerza.
 */
package HeartsMonitor;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

/**
 *
 * @author Alejandro Balaguer Calderón
 */
public class Controlador implements Runnable {

    private static Monitor vista; //Elemento base de la vista
    private static Scene scene;
    private ArrayList<Receptor> receptores;

    public Controlador(Monitor vista,Scene scene) {
        this.vista = vista;
        this.scene = scene;
        receptores = new ArrayList<Receptor>();
    }
    
    @Override
    //Hilo controlador
    public void run(){
        //Pruebas
        anadirConexionUDP(12345, "testUDP");
        anadirConexionTCP(12345, "testTCP");
        for(int i=0; i<22222; i++){
            imprimirDatos();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                System.out.println("PERO BUAT DE FAC");
            }
        }
    }
    //Método temporal para poder ver los datos sin interfaz visual.
    private void imprimirDatos(){
        //Receptor[] conexiones = getConexiones();
        //for(Receptor conexion : conexiones)
        //    System.out.println(conexion.getNombre() + ": " + conexion.getLatidos() + ".");
        
        //hacer que por cada hilo haya un cuadro mostrando los datos
        //aqui deberia actualizarse
        //otra opcion seria que cada hilo receptor actualize el campo por si mismo, mas eficiente pero menos encapsulado
        //otro problema es como le indico a cada parte que su dato es su dato
        //un puto lio en general
        
    }
    
    //Es, y solo es, posible que se ha vuelto inutil
    public Receptor[] getConexiones(){
        return receptores.toArray(new Receptor[0]);
    }
    
    public boolean anadirConexionTCP(int puerto, String nombre){
        ReceptorTCP receptor = new ReceptorTCP(puerto, nombre, scene.lookup("#paneltcp"), scene.lookup("#textotcp"), scene.lookup("#barratcp"));
        receptores.add(receptor);
        Thread hilo = new Thread(receptor);
        hilo.start();
        System.out.println("Hilo TCP iniciado.");
        
        /*
        //TODO
        try {
            Pane panel = FXMLLoader.load(getClass().getResource("FXMLPanel.fxml"));
            panel.setId(nombre);
            vista.anadirPanel(panel);
            return true;
        } catch (IOException e) {
            System.out.println("\n\nError al leer FXMLPanel.fxml.\n");
            e.printStackTrace();
        }
        */
        return false;
        
    }
    
    public void anadirConexionUDP(int puerto, String nombre){
        ReceptorUDP receptor;
        try {
            receptor = new ReceptorUDP(puerto, nombre, scene.lookup("#paneludp"), scene.lookup("#textoudp"), scene.lookup("#barraudp"));
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
