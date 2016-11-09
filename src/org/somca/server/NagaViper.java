package org.somca.server;

/**
 * Created by overpex on 07/11/16.
 */

import java.net.*;
import java.io.*;

public class NagaViper extends Thread{

    private final static int DEFAULT_PORT = 3000;
    private final static int DEFAULT_TIMEOUT = 70000;
    private static boolean SERVER_STATE = false;
    private ServerSocket listenSocket;

    public NagaViper(int port, int timeOut) throws IOException {
        listenSocket = new ServerSocket(port);
        listenSocket.setSoTimeout(timeOut);
        SERVER_STATE = true;
    }

    public NagaViper() throws IOException {
        listenSocket = new ServerSocket(DEFAULT_PORT);
        listenSocket.setSoTimeout(DEFAULT_TIMEOUT);
        SERVER_STATE = true;
    }

    @Override
    public void run() {
        System.out.println("NagaViper Server Start ...");
        while (SERVER_STATE) {
            try {
                // Waiting for a client connection with the server.
                Socket server = listenSocket.accept();
                System.out.println(String.format("SERVER - Address :%s", server.getRemoteSocketAddress()));

                // Get data from the clients
                DataInputStream inStream = new DataInputStream(server.getInputStream());
                String signal = inStream.readUTF();
                System.out.println(String.format("SERVER - Data received : %s", signal));

                if (signal.equals("START")){
                    System.out.println("Calabash Start");
                    Thread tmp = new Thread()
                    {
                        @Override
                        public void run() {
                            for(int i = 0;i < 20; i++)
                            {
                                /**
                                 * Statements
                                 */
                            }
                        }
                    };
                    tmp.start();
                    tmp.join();
                }

                System.out.println("End the N run the next will start soon");


            }catch (SocketTimeoutException t)
            {
                SERVER_STATE = false;
                System.out.print("Time out");
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}