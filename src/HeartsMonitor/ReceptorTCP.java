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
        System.out.println("Hilo TCP iniciado para " + nombre + " en puerto " + puerto + ".");
    }
    
    
    @Override
    //Código del hilo
    public void run(){
        ejecutarse = true;
        InputStream entrada;
        
        //Bucle para adquirir el puerto
        while(servidor == null){
            try{
                servidor = new ServerSocket(puerto);
            }catch(BindException e){
                //Puerto ocupado. El programa espera 5 segundos entre intentos de conexión.
                System.out.println("ERROR: El puerto (" + puerto + ") para " + nombre +  " está ocupado.");
                try {Thread.sleep(5000);} catch (InterruptedException ex) {}
            }catch(IOException e){
                System.out.println("\n\nERROR al intentar aceptar conexiones en el puerto " + puerto + ".\n");
                e.printStackTrace();
            }
        } //TODO: si el puerto esta ocupado podria borrarse el dispositivo y listo
        // o casi mejor mostrarlo porque eso chocaría con lo de guardar dispositivos
        
        //Bucle de ejecución.
        while(ejecutarse){
            onLatidosChanged();
            try{
                conexion = establecerConexion();
                // Check para asegurarse de que aun hay que seguir
                if(ejecutarse && conexion != null){
                    entrada = conexion.getInputStream();
                    System.out.println("Conexión TCP aceptada para " + nombre + ".");

                    // Espera una respuesta y actualiza los latidos mientras la conexión esté activa.
                    do{
                        try{
                            latidos = entrada.read();
                        }catch(SocketTimeoutException e){
                            System.out.println("\n" + nombre + " está tardando demasiado en responder.");
                            latidos = -1;
                        }catch(IOException e){
                            System.out.println("\n\nError E/S al leer un paquete TCP de " + nombre + ".\n");
                            e.printStackTrace();
                            latidos = -1;
                        }
                        onLatidosChanged();
                    }while(latidos != -1 && ejecutarse); //TODO por que usé un do-while en lugar de while?
                    System.out.println("Conexión con " + nombre + " finalizada.");
                }
            }catch(IOException e){
                System.out.println("\n\nERROR al establecer conexión con " + nombre + ".\n");
                e.printStackTrace();
            }
            latidos = -1;
        }//TODO: testear que todo en este método (run()) funciona correctamente
        
        //Código cuando se ha ordenado el cierre del hilo
        System.out.println("Hilo TCP de " + nombre + " cerrado.");
    }
    
    //Establece la conexion con un dispositivo
    //TODO/NOTA: cuando se establece una conexion TCP el dispositivo envia una ID textual y luego los datos de latidos cada 500ms (no obligatorio).
    //Esta ID se utilizara para saber si el dispositivo es el adecuado o no.
    //Si ID esperada == null entonces se acepta cualquiera.
    private Socket establecerConexion() throws IOException{
        while(true){
            //Se ignora SocketException en este ámbito porque es causada por "socket closed" al cerrar el hilo
            //posible TODO: controlar que la SocketException sea causada por "socket closed" y hacerlo en el run(). Nota: e.getCause() no sirve para esto.
            //podria controlarlo con getMessage() --> si el mensaje empiexa por...
            try{
                conexion = servidor.accept();
            }catch(SocketException e){
                return null;
            }
            System.out.println("LOG: Conexión TCP establecida");
            conexion.setSoTimeout(timeoutConexion);
            if(nombre == null){
                //No hay nombre. Se adopta el de la conexión y se acepta.
                nombre = leerNombre(conexion.getInputStream());
                onNombreChanged();
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
}
