package com.example.patrick.servico_principal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class TCPServer {
    public static void main(String argv[]) throws Exception
    {
        String clientSentence = "", aux;
        String capitalizedSentence;
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while(true)
        {
            clientSentence = "";
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            OutputStream outToClient = connectionSocket.getOutputStream();
            while(!(aux = inFromClient.readLine()).equals("FIM")){
                clientSentence += aux + "\n";
            }
            System.out.println("Received: " + clientSentence);
            capitalizedSentence = "Eu sou servidor\n";
            outToClient.write(capitalizedSentence.getBytes());
        }
    }
}