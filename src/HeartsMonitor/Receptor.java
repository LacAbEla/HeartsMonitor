/*
 * Clase abstracta para mantener las propiedades b'asicas de los receptores.
 */
package HeartsMonitor;

/**
 *
 * @author aleba
 */
public abstract class Receptor implements Runnable {
    
    protected boolean ejecutarse; //Esta variable solo se modificará para ponerla en falso. Por tanto, no es necesario sincronizar su uso entre los hilos.
    protected int latidos;
    protected int puerto;
    protected String nombre;

    public Receptor(int puerto, String nombre) {
        ejecutarse = false;
        latidos = -1;
        this.puerto = puerto;
        this.nombre = nombre;
    }
    
    
    //Devolver el número de latidos de esta conexión
    public int getLatidos(){
        return latidos;
    }
    
    //Devuelve el nombre de esta conexión
    public String getNombre(){
        return nombre;
    }
    
    public void setNombre(String valor){
        nombre = valor;
    }
    
    public boolean isEjecutando(){
        return ejecutarse;
    }
    
    //Ordena al receptor que se detenga.
    public abstract void detener();
}
