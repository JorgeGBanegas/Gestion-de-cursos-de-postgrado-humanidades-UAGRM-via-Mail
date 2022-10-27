

package proyectoviamail.datos;

import java.sql.*;
/**
 * 
 * @author Jorge Banegas
 */
public class BDConexion {
    
    private final String SERVIDOR = "localhost";
    private final String USUARIO = "postgres";
    private final String CLAVE = "password";
    private final String BD = "db_postgrado_humanidades";
    private final String DRIVER = "org.postgresql.Driver";
    private final int PUERTO = 5432;
    private final String URL = "jdbc:postgresql://" + SERVIDOR + ":" + PUERTO + "/" + BD;
  
    private Connection conexion = null;

    private static BDConexion instancia = null;
    
    private BDConexion(){
        try {
            Class.forName(DRIVER);
            System.out.println("Driver registrado con exito");
        } catch (ClassNotFoundException e) {
            System.out.println("Error al regsitrar el driver "+e.getMessage());
        }
    }
    
    public static BDConexion obtenerInstancia(){
        if(instancia == null){
            instancia = new BDConexion();
        }
        return instancia;
    }
    
    public void conectar(){
        try {
            this.conexion = DriverManager.getConnection(URL,USUARIO, CLAVE);
            System.out.println("Conexion Exitosa");
        } catch (SQLException e) {
            System.out.println("Error al conectar, intente nuevamente "+e.getMessage());
        }
    }
    
    public void desconectar(){
        try {
            this.conexion.close();
            System.out.println("Desconectado");
        } catch (SQLException e) {
            System.out.println("Error al conectar, intente nuevamente "+e.getMessage());
        }
    }
    
    public Connection obtenerConexion(){
        return this.conexion;
    }
}
