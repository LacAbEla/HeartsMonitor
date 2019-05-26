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
    
    private static ControlUtils utils;
    private static int latidosBajo; // Límite inferior seguro de latidos.
    private static int latidosAlto; // Límite superior seguro de latidos.

    public Global() {
    }


    public static ControlUtils getUtils() {
        return utils;
    }

    public static void setUtils(ControlUtils utils) {
        Global.utils = utils;
    }
    
    public static int getLatidosBajo() {
        return latidosBajo;
    }

    public void setLatidosBajo(int latidosBajo) {
        Global.latidosBajo = latidosBajo;
    }

    public static int getLatidosAlto() {
        return latidosAlto;
    }

    public void setLatidosAlto(int latidosAlto) {
        Global.latidosAlto = latidosAlto;
    }
    
}
