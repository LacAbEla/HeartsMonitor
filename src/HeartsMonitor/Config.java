/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HeartsMonitor;

import java.io.Serializable;

/**
 * Clase para almacenar la configuración personalizable por el usuario
 * 
 * @author Alejandro Balaguer Calderón
 */
public class Config implements Serializable {
    
    private int latidosBajo; // Límite inferior seguro de latidos.
    private int latidosAlto; // Límite superior seguro de latidos.
    
    public Config(int latidosBajo, int latidosAlto){
        this.latidosBajo = latidosBajo;
        this.latidosAlto = latidosAlto;
    }
    
    
    public int getLatidosBajo() {
        return latidosBajo;
    }

    public void setLatidosBajo(int latidosBajo) {
        this.latidosBajo = latidosBajo;
    }

    public int getLatidosAlto() {
        return latidosAlto;
    }

    public void setLatidosAlto(int latidosAlto) {
        this.latidosAlto = latidosAlto;
    }
}
