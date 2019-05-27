/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HeartsMonitor;

/**
 * Clase para almacenar datos de ámbito global:
 *  - La clase de utilidades del programa.
 *  - Las opciones personalizables por el usuario.
 * 
 * @author Alejandro Balaguer Calderón
 */
public class Global {
    
    public static ControlUtils utils;
    public static Config config;
    
    public static final String FICHERO_DATOS = "config.dat";


    public Global() {
    }
    
}
