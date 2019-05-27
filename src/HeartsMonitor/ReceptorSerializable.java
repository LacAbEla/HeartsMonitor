/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HeartsMonitor;

import java.io.Serializable;

/**
 *
 * @author aleba
 */
public class ReceptorSerializable implements Serializable {
    
    private int puerto;
    private String nombre;
    private boolean esTCP;

    public ReceptorSerializable(int puerto, String nombre, boolean esTCP) {
        this.puerto = puerto;
        this.nombre = nombre;
        this.esTCP = esTCP;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean esTCP() {
        return esTCP;
    }

    public void setEsTCP(boolean esTCP) {
        this.esTCP = esTCP;
    }
}
