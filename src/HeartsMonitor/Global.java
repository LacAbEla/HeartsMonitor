/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HeartsMonitor;

/**
 * Clase para almacenar datos de ámbito global para un fácil acceso desde cualquier parte de la aplicación:
 *  - La clase de utilidades del programa.
 *  - Las opciones personalizables por el usuario.
 *  - Nobres de archivos.
 * 
 * @author Alejandro Balaguer Calderón
 */
public class Global {
    
    public static ControlUtils utils;
    public static Config config;
    
    public static final String FICHERO_DATOS = "config.dat";
    public static final String FICHERO_AUDIO = "alerta.wav";


    public Global() {
    }
    
}
