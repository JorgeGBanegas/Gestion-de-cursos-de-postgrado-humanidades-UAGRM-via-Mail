package proyectoviamail;

import proyectoviamail.datos.BDConexion;
import proyectoviamail.negocio.POP3;
import proyectoviamail.negocio.SMTP;

/**
 *
 * @author Jorge Banegas
 */
public class ProyectoViaMail {

    public static void main(String[] args) {
        // TODO code application logic here
        BDConexion conn = BDConexion.obtenerInstancia();
        conn.conectar();
    }

}
