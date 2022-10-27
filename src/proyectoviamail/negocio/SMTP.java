

package proyectoviamail.negocio;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SMTP {

    
    private final String servidor;
    private String usuarioReceptor;
    private String usuarioEmisor;
    private String comando;
    private final int puerto;
    private Socket socket;
    private BufferedReader entrada;
    private DataOutputStream salida;

    public SMTP(String servidor) {
        this.servidor = servidor;
        this.usuarioReceptor = null;
        this.usuarioEmisor = null;
        this.comando = "";
        this.socket = null;
        this.puerto = 25;
        this.entrada = null;
        this.salida = null;
    }

    public void conectar() {
        try {
            this.socket = new Socket(this.servidor, this.puerto);
            this.entrada = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.salida = new DataOutputStream(this.socket.getOutputStream());
            System.out.println("Conexion exitosa");
        } catch (IOException ex) {
            System.out.println("Ocurrio un error al conectarse al servidor: " + ex.getMessage());
        }
    }

    public void iniciarSesion() {
        try {
            System.out.println("S : " + this.entrada.readLine());
            this.comando = "EHLO " + this.servidor + " \r\n";
            System.out.print("C : " + this.comando);
            this.salida.writeBytes(this.comando);
            System.out.println("S : " + getMultiline(this.entrada));
        } catch (IOException ex) {
            System.out.println("Ah ocurrido un error al iniciar sesion: " + ex.getMessage());
        }
    }

    public void cerrarSesion() {
        try {
            this.comando = "QUIT\r\n";
            System.out.print("C : " + this.comando);
            this.salida.writeBytes(this.comando);
            System.out.println("S : " + this.entrada.readLine());
        } catch (IOException ex) {
            System.out.println("Ah ocurrido un error al cerrar sesion");
        }

    }

    public void desconectar() {
        try {
            this.socket.close();
            this.entrada.close();
            this.salida.close();
            System.out.println("Desconectado");
        } catch (IOException ex) {
            System.out.println("Hubo un error al tratar de desconectarte " + ex.getMessage());
        }
    }

    public void enviarMail(String subject, String emisor, String recptTo, String message) {
        try {
            this.usuarioEmisor = emisor;
            this.usuarioReceptor = recptTo;
            this.comando = "MAIL FROM : " + this.usuarioEmisor + " \r\n";
            System.out.print("C : " + this.comando);
            this.salida.writeBytes(this.comando);
            System.out.println("S : " + this.entrada.readLine());

            this.comando = "RCPT TO : " + this.usuarioReceptor + " \r\n";
            System.out.print("C : " + this.comando);
            this.salida.writeBytes(this.comando);
            System.out.println("S : " + this.entrada.readLine());

            this.comando = "DATA\n";
            System.out.print("C : " + this.comando);
            this.salida.writeBytes(this.comando);
            System.out.println("S : " + getMultiline(this.entrada));

            this.comando = "Subject:"+subject +"\n"+message+"\n"+".\n";            
            System.out.print("C : " + this.comando);
            this.salida.writeBytes(this.comando);
            System.out.println("S : " + this.entrada.readLine());
        } catch (IOException ex) {
            System.out.println("Error al tratar de enviar el correo: " + ex.getMessage());
        }
    }

    public void reiniciar() {
        try {
            this.comando = "RESET \r\n";
            this.salida.writeBytes(this.comando);
            System.out.println("S :" + this.entrada.readLine());
        } catch (IOException ex) {
            System.out.println("Ah ocurrido un error: "+ex.getMessage());
        }
    }
//Permite Leer multiples líneas del Protocolo SMTP

    protected String getMultiline(BufferedReader in) throws IOException {
        StringBuilder lines = new StringBuilder();
        while (true) {
            String line = in.readLine();
            if (line == null) {
                // Server closed connection
                throw new IOException(" S : Server unawares closed the connection.");
            }
            if (line.charAt(3) == ' ') {
                lines.append("\n").append(line);
                // No more lines in the server response
                break;
            }
            // Add read line to the list of lines
            lines.append("\n").append(line);
        }
        return lines.toString();
    }

}

