/*
 * Hilo encargado de mantener la comunicacin TCP con un dispositivo de entrada
 * Si cae la conexión tratará de volver a conectarse.
 * Esta clase distingue entre nombre (nombre interno, a modo de ID) y nombre a mostrar (que sobreescribira al interno en la vista si esta configurado)
 */
package HeartsMonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.scene.Node;

/**
 *
 * @author Alejandro Balaguer Calderón
 */
public class ReceptorTCP extends Receptor {
    
    private Socket conexion;
    private String nombreAMostrar;

    public ReceptorTCP(int puerto, String nombre, Node panel, Node texto, Node barra) {
        super(puerto, nombre, panel, texto, barra);
        nombreAMostrar = null;
        System.out.println("Hilo iniciado para " + nombre + ".");
    }
    
    
    @Override
    //Código del hilo
    public void run(){
        ejecutarse = true;
        InputStream entrada;
        
        //Bucle de ejecución.
        while(ejecutarse){
            onLatidosChange();
            try{
                conexion = establecerConexion();
                entrada = conexion.getInputStream();
                System.out.println("Conexión TCP aceptada para " + nombre + ".");

                //Espera una respuesta y actualiza los latidos mientras la conexión esté activa.
                do{
                    latidos = entrada.read();
                    onLatidosChange();
                }while(latidos != -1);
                
                System.out.println("Conexión con " + nombre + " finalizada.");
            }catch(BindException e){
                //Puerto ocupado. El programa espera 10 segundos entre intentos de conexión. TODO
                //esperar 10 seg
                System.out.println("\n\nEl puerto para " + nombre + " está ocupado.\n");
                e.printStackTrace();
            }catch(IOException e){
                System.out.println("\n\nLa conexión con " + nombre + " ha caído.\n");
                e.printStackTrace();
            }
            latidos = -1;
        }
        
        //Código cuando se ha ordenado el cierre del hilo
        System.out.println("Hilo de " + nombre + " cerrado.");
    }
    
    //Establece la conexion con un dispositivo
    //TODO/NOTA: cuando se establece una conexion TCP el dispositivo envia una ID textual y luego los datos de latidos cada 500ms (no obligatorio).
    //Esta ID se utilizara para saber si el dispositivo es el adecuado o no.
    //Si ID esperada == null entonces se acepta cualquiera.
    private Socket establecerConexion() throws IOException{
        ServerSocket servidor = new ServerSocket(puerto);
        Socket conexion;
        while(true){
            conexion = servidor.accept();
                System.out.println("LOG: Conexión TCP establecida");
            if(nombre == null){
                //No hay nombre. Se adopta el de la conexión y se acepta.
                nombre = leerNombre(conexion.getInputStream());
                return conexion;
            }else if(leerNombre(conexion.getInputStream()).equals(nombre)){
                //Hay nombre. Se acepta la conexión al coincidir el nombre.
                return conexion;
            }else{
            //Hay nombre. Este no coincide y la conexión se rechaza.
            conexion.close();
            System.out.println("LOG: Conexión TCP rechazada.");
            }
        }
    }
    
    //Lee el nombre (ID) enviado por el dispositivo.
    private String leerNombre(InputStream is) throws IOException{
        BufferedReader entrada = new BufferedReader(new InputStreamReader(is));
        return entrada.readLine();
    }
    
    //Devuelve el nombre a mostrar por el objeto.
    public String getNombreAMostrar(){
        if(nombreAMostrar != null)
            return nombreAMostrar;
        else
            return nombre;
    }
    
    //Cambiar nombre a mostrar. null -> nombre normal mostrado
    public void setNombreAMostrar(String nombre){
        nombreAMostrar = nombre;
    }
    
    @Override
    //Detiene el receptor
    public void detener(){
        ejecutarse = false;
        try {
            conexion.close();
        } catch (IOException e) {
            System.out.println("\n\nError al cerrar la conexión de " + nombre + ".\n");
            e.printStackTrace();
        }
    }
}
