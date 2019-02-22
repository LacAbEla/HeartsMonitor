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
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 *
 * @author Alejandro Balaguer Calderón
 */
public class ReceptorTCP extends Receptor {
    
    private ServerSocket servidor;
    private Socket conexion;
    private String nombreAMostrar;

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
        }
        
        //Bucle de ejecución.
        while(ejecutarse){
            onLatidosChanged();
            try{
                conexion = establecerConexion();
                //Check para asegurarse de que aun hay que seguir
                if(ejecutarse && conexion != null){
                    entrada = conexion.getInputStream();
                    System.out.println("Conexión TCP aceptada para " + nombre + ".");

                    //Espera una respuesta y actualiza los latidos mientras la conexión esté activa.
                    do{
                        latidos = entrada.read();
                        onLatidosChanged();
                    }while(latidos != -1);

                    System.out.println("Conexión con " + nombre + " finalizada.");
                }
            }catch(IOException e){
                System.out.println("\n\nERROR: La conexión con " + nombre + " ha caído.\n");
                e.printStackTrace();
            }//TODO controlar SocketException causada por "socket closed" para que no se muestre, pues es normal al cerrar el hilo
            latidos = -1;
        }
        
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
    protected void onNombreChanged(){
        if(nombreAMostrar != null)
            textoNombre.setText(nombreAMostrar);
        else
            textoNombre.setText(nombre);
    }
    
    @Override
    //Detiene el receptor
    public void detener(){
        ejecutarse = false;
        try {
            if(latidos != -1) //TODO existe la absurdamente pequeña posibilidad de que una conexión se cierre antes de que los latidos se pongan en -1.
                conexion.close();
            if(servidor != null) //TODO? que sepa no existe la posibilidad de que un ServerSocket se cierre por si solo, por lo que esto no debería dar problemas.
                servidor.close();
        } catch (IOException e) {
            System.out.println("\n\nError al cerrar la conexión de " + nombre + ".\n");
            e.printStackTrace();
        }
    }
}
