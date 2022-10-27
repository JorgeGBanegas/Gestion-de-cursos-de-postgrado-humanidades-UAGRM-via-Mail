package proyectoviamail.negocio;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class POP3 {

    //se establecen las cadenas con informacion del servidor, usuario receptor y emisor del email y puerto de conexion
    private final String servidor;
    private String usuario;
    private String contrasenia;
    private String comando;
    private final int puerto;
    private Socket socket;
    private BufferedReader entrada;
    private DataOutputStream salida;

    public POP3(String servidor) {
        this.servidor = servidor;
        this.usuario = "";
        this.contrasenia = "";
        this.puerto = 110;
        this.comando = "";
        this.socket = null;
        this.entrada = null;
        this.salida = null;

    }

    public void conectar() {
        try {
            this.socket = new Socket(this.servidor, this.puerto);
            this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.salida = new DataOutputStream(socket.getOutputStream());
            System.out.println("Conexion Exitosa");
        } catch (IOException ex) {
            System.out.println("Ocurrio un error al conectarse al servidor: " + ex.getMessage());
        }
    }

    public void iniciarSesion(String usuario, String contrasenia) {
        try {
            this.usuario = usuario;
            this.contrasenia = contrasenia;
            System.out.println("S : " + this.entrada.readLine() + "\r\n");
            this.comando = "USER " + this.usuario + "\r\n";
            System.out.print("C : " + this.comando);
            this.salida.writeBytes(this.comando);
            System.out.println("S : " + this.entrada.readLine() + "\r\n");

            this.comando = "PASS " + this.contrasenia + "\r\n";
            System.out.print("C : " + this.comando);
            this.salida.writeBytes(this.comando);
            System.out.println("S : " + this.entrada.readLine() + "\r\n");
        } catch (IOException ex) {
            System.out.println("Ah ocurrido un error al iniciar sesion: " + ex.getMessage());
        }
    }

    public void cerrarSesion() {
        try {
            this.comando = "QUIT\r\n";
            System.out.print("C : " + this.comando);
            this.salida.writeBytes(this.comando);
            System.out.println("S : " + this.entrada.readLine() + "\r\n");
        } catch (IOException ex) {
            System.out.println("Ah ocurrido un error al cerrar sesion");
        }
    }

    public String obtenerListasDeEmails() {
        try {
            this.comando = "LIST \r\n";
            System.out.print("C : " + this.comando);
            this.salida.writeBytes(this.comando);
            String listaEmails = getMultiline(this.entrada);
            System.out.println("S : " + listaEmails + "\r\n");
            return listaEmails;
        } catch (IOException ex) {
            System.out.println("Ah ocurrido un error al tratar de obtener los emails");
        }
        return null;
    }

    public String obtenerEmail(int email) {
        try {
            this.comando = "RETR " + email + "\n";
            System.out.print("C : " + this.comando);
            this.salida.writeBytes(this.comando);
            String emailObtenido = getMultiline(this.entrada);
            System.out.println("S : " + emailObtenido + "\r\n");
            return emailObtenido;
        } catch (IOException ex) {
            System.out.println("Error al obtener el email: " + ex.getMessage());
        }
        return null;
    }

    public int obtenerCantidadDeEmails() {
        int cantidad = 0;
        try {
            comando = "STAT \n";
            System.out.print("C : " + this.comando);
            this.salida.writeBytes(this.comando);
            String stat = this.entrada.readLine().trim();
            String [] aStat = stat.split("\\s+");
            System.out.println("S : " + stat + "\r\n");
            cantidad = Integer.parseInt(aStat[1]);
        } catch (IOException ex) {
            System.out.println("Ah ocurrido un error al tratar de obtener la cantidad de emails");
        }
        return cantidad;
    }

    public void desconectar(){
        try {
            this.entrada.close();
            this.salida.close();
            this.socket.close();
            System.out.println("Desconectado");
        } catch (IOException ex) {
            System.out.println("Hubo un error al tratar de desconectarte "+ex.getMessage());
        }
    }
    
    public String obtenerSubject(String mail){
        boolean encontrado = false;
        String linea = "";
        String [] lineas = mail.split("\\n");
        for(String l : lineas){
            if(l.contains("Subject:")){
                encontrado = true;
                linea = l;
                break;
            }
        }
        
        if(encontrado){
            String [] lineaSubject = linea.split(":");
            linea = lineaSubject[1];
        }
        System.out.println("el subject es: "+linea);
        return linea;
    }
  
    protected String getMultiline(BufferedReader in) throws IOException {
        StringBuilder lines = new StringBuilder();
        while (true) {
            String line = in.readLine();
            if (line == null) {
                // Server closed connection
                throw new IOException(" S : Server unawares closed the connection.");
            }
            if (line.equals(".")) {
                // No more lines in the server response
                break;
            }
            if ((line.length() > 0) && (line.charAt(0) == '.')) {
                // The line starts with a "." - strip it off.
                line = line.substring(1);
            }
            // Add read line to the list of lines
            lines.append("\n").append(line);
        }
        return lines.toString();
    }

}

