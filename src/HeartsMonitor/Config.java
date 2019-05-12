/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HeartsMonitor;

/**
 * Clase para almacenar las opciones personalizables por el usuario.
 * 
 * @author aleba
 */
public class Config {
    
    private static int latidosBajo; // Límite inferior seguro de latidos.
    private static int latidosAlto; // Límite superior seguro de latidos.

    public Config() {
    }

    
    public static int getLatidosBajo() {
        return latidosBajo;
    }

    public void setLatidosBajo(int latidosBajo) {
        Config.latidosBajo = latidosBajo;
    }

    public static int getLatidosAlto() {
        return latidosAlto;
    }

    public void setLatidosAlto(int latidosAlto) {
        Config.latidosAlto = latidosAlto;
    }
    
}
