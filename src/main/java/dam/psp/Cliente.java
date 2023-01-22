package dam.psp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Objects;
import java.util.Scanner;

public class Cliente {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5000);
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter pw = new PrintWriter(socket.getOutputStream());


        System.out.println("Bienvenido al chat del amor, introduce tu nombre:");
        String nombre = sc.next();

        pw.println(nombre);
        pw.flush();

        String nombreDeUsuarios = br.readLine();
        System.out.println(nombreDeUsuarios);

        DatagramSocket datagramSocket = new DatagramSocket();
        //  escucharMensajesUDP(datagramSocket);
        enviarMensajesUDP(datagramSocket);
        escucharMensajesTCP(socket);

    }

    private static void escucharMensajesTCP(Socket socket) throws IOException {
        while (true) {
            System.out.println(new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine());
        }
    }

    private static void enviarMensajesUDP(DatagramSocket datagramSocket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("Introduce el mensaje a enviar o (s) para salir:");
                    String mensaje = sc.next();
                    if (Objects.equals(mensaje, "s")) {
                        return;
                    }
                    byte[] buffer = mensaje.getBytes();
                    SocketAddress socketAddress = new InetSocketAddress("localhost", Servidor.puerto);
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, buffer.length, socketAddress);

                    try {
                        datagramSocket.send(datagramPacket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

  /*  private static void escucharMensajesUDP(DatagramSocket datagramSocket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket datagramPacket = new DatagramPacket(buffer,
                            0, buffer.length, datagramSocket.getInetAddress(), Servidor.puerto);
                    try {
                        datagramSocket.receive(datagramPacket);
                    } catch (IOException e) {
                        System.err.println("Se ha cerrado la conexión");
                    }
                    String mensaje = new String(datagramPacket.getData(), 0, datagramPacket.getLength(), StandardCharsets.UTF_8);
                    System.out.println(mensaje);
                }
            }
        }).start();
    }*/
}
