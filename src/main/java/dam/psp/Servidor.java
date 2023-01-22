package dam.psp;
/*

Crea una aplicación de chat en tiempo real utilizando sockets TCP y UDP.
La aplicación debe permitir a los usuariosTCP conectarse al servidor y enviar mensajes
a todos los demás usuariosTCP conectados.

Utiliza el protocolo TCP para la conexión inicial entre el cliente y el servidor.
El cliente debe enviar su nombre de usuario al servidor y recibir una lista de todos
los usuariosTCP conectados actualmente.

Utiliza el protocolo UDP para el envío de mensajes. El servidor debe recibir los mensajes
 de los clientes y reenviarlos a todos los demás clientes conectados.



 */
/*
El cliente se conecta al servidor utilizando el protocolo TCP. En este momento,
 el cliente envía su nombre de usuario al servidor y recibe una lista de todos los usuariosTCP conectados.

Una vez conectado, el cliente comienza a escuchar por mensajes UDP en un puerto específico.
 El servidor también tiene una lista de los puertos asociados a cada cliente.

Cuando un cliente quiere enviar un mensaje a todos los usuariosTCP conectados, utiliza el protocolo UDP
para enviar el mensaje al servidor. El servidor recibe el mensaje y reenvía el mensaje a todos los clientes conectados,
utilizando los puertos asociados a cada cliente.

Para implementar la función de "privado", el cliente puede enviar un mensaje específico al servidor indicando
que el mensaje es privado y el destinatario del mensaje. El servidor puede entonces reenviar el mensaje al
destinatario específico utilizando el puerto asociado a ese cliente.

El servidor debe estar constantemente a la escucha de mensajes UDP y TCP, para poder recibir y reenviar
mensajes a los clientes conectados. */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Servidor {
    public static int puerto = 5000;
    private static Map<String, Socket> usuariosTCP = new HashMap<>();

    public static void main(String[] args) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket(puerto);
        ServerSocket serverSocket = new ServerSocket(puerto);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket socketConectado = serverSocket.accept();
                        String nombre = new BufferedReader(new InputStreamReader(socketConectado.getInputStream())).readLine();
                        synchronized (usuariosTCP) {
                            usuariosTCP.put(nombre, socketConectado);
                        }
                        PrintWriter pw = new PrintWriter(socketConectado.getOutputStream());
                        pw.println(usuariosTCP.keySet());
                        pw.flush();
                    } catch (IOException e) {
                        System.err.println("Error al conectar");
                    }
                }

            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, buffer.length);
                    try {
                        datagramSocket.receive(datagramPacket);
                        String mensaje = new String(datagramPacket.getData(), 0, datagramPacket.getLength(), StandardCharsets.UTF_8);
                        System.out.println(mensaje);
                        synchronized (usuariosTCP) {
                            for (Socket socket :
                                    usuariosTCP.values()) {
                                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                                pw.println(mensaje);
                                pw.flush();
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }


}