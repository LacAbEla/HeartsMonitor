/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HeartsMonitor;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 *
 * @author Alejandro Balaguer Calderón
 */
public class HiloReceptor implements Runnable {
    
    boolean ejecutarse; //Esta variable solo se modificará para ponerla en falso. Por tanto, no es necesario sincronizar su uso entre los hilos.
    private int latidos;
    private String nombre;
    private final Socket conexion;

    public HiloReceptor(Socket conexion, String nombre) {
        ejecutarse = true;
        this.conexion = conexion;
        this.nombre = nombre;
        System.out.println("Hilo iniciado para " + nombre + ".");
    }
    
    
    @Override
    //Se esperará a recibir los datos de la conexión para cambiar el valor de latidos.
    public void run(){
        InputStream entrada;
        try{
            entrada = conexion.getInputStream();
            
            //Bucle de ejecución. Espera una respuesta y actualiza los latidos.
            while(ejecutarse){
                latidos = entrada.read();
                if(latidos == -1){
                    //La conexión se ha cerrado. Informa y detiene el hilo.
                    System.out.println("Conexión con " + nombre + " finalizada.");
                    ejecutarse = false;
                }
            }
            
            //Código cuando se ha ordenado el cierre del hilo
            System.out.println("Hilo de " + nombre + " cerrado.");

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    //Devolver el número de latidos de esta conexión
    public int getLatidos(){
        return latidos;
    }
    
    //Devuelve el nombre de esta conexión
    public String getNombre(){
        return nombre;
    }
    
    public void setNombre(String valor){
        nombre = valor;
    }
    
    public void detener(){
        try {
            conexion.close();
        } catch (IOException e) {
            System.out.println("\n\nError al cerrar la conexión de " + nombre + ".\n");
            e.printStackTrace();
        }
    }
}
