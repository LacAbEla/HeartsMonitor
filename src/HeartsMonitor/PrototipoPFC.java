/*
 * Notas:
 * El prototipo usa el protocolo TCP. Para poder recuperar conexiones guardadas sin un proceso de
 * sincronización por parte de ambas partes que seguramente sea costoso de implementar es probable
 * que acabe migrándolo todo a UDP.

Utilizar tipos de datos pequeños (byte, short) no sirve para ahorrar espacio si la variable está sola:
 - Todos los campos de una clase ocupan al menos 1 "slot", cada uno de los cuales es de 32 bits (el tamaño de un int). Los tipos de dato largos (long, double) ocupan 2 slots.
 - La JVM solo puede hacer operaciones con int/float y long/double.
https://stackoverflow.com/questions/27122610/why-does-the-java-api-use-int-instead-of-short-or-byte#27123302

TODO: usar loggers en lugar de prints
TODO: usar loggers para indicar cuando se abre o cierra una conexion de HiloReceptor
 */
package HeartsMonitor;

/**
 *
 * @author Alejandro Balaguer Calderón
 */
public class PrototipoPFC {
    
    private static Controlador controlador;
    
    public static void main(String[] args) throws InterruptedException {
        controlador = new Controlador(12345);
        new Thread(controlador).start();
        
        //Pruebas
        for(int i=0; i<22222; i++){
            imprimirDatos();
            Thread.sleep(500);
        }
        
        cerrar();
    }
    
    //Método temporal para poder ver los datos sin interfaz visual.
    private static void imprimirDatos(){
        HiloReceptor[] conexiones = controlador.getConexiones();
        for(HiloReceptor conexion : conexiones){
            System.out.println(conexion.getNombre() + ": " + conexion.getLatidos() + ".");
        }
    }
    
    private static void cerrar(){
        controlador.detener();
    }
    
}
