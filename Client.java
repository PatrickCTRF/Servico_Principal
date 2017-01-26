package com.example.patrick.servico_principal;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

public class Client extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    String response = "";
    TextView textResponse;
    final AquisicaoSensores info;
    Calendar calendario = Calendar.getInstance();

    Client(String addr, int port, TextView textResponse, AquisicaoSensores info) {//addr é relativo a address = endereço web.
        dstAddress = addr;
        dstPort = port;
        this.textResponse = textResponse;
        this.info = info;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];

            String bytesRead = null;
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            File arquivo = new File(Environment.getExternalStorageDirectory().toString() + "/teste_Servidor.txt");
            FileWriter escritor = new FileWriter(arquivo, true);

            calendario = Calendar.getInstance();
            escritor.write("\n\nTempo do Celular no início da transmissao: " + calendario.get(Calendar.HOUR) + ":" + calendario.get(Calendar.MINUTE) + ":" + calendario.get(Calendar.SECOND) + "," + calendario.get(Calendar.MILLISECOND) + "\n");
            socket.getOutputStream().write(("" + calendario.get(Calendar.HOUR) + ":" + calendario.get(Calendar.MINUTE) + ":" + calendario.get(Calendar.SECOND) + "," + calendario.get(Calendar.MILLISECOND) + "\n").getBytes());

            calendario = Calendar.getInstance();
            escritor.write("\n\nTempo no Celular antes de enviar os dados: " + calendario.get(Calendar.HOUR) + ":" + calendario.get(Calendar.MINUTE) + ":" + calendario.get(Calendar.SECOND) + "," + calendario.get(Calendar.MILLISECOND) + "\n");
            socket.getOutputStream().write((info.getInfo() + "\nFIM\n").getBytes());

            calendario = Calendar.getInstance();
            escritor.write("\n\nTempo no Celular depois de enviar os dados: " + calendario.get(Calendar.HOUR) + ":" + calendario.get(Calendar.MINUTE) + ":" + calendario.get(Calendar.SECOND) + "," + calendario.get(Calendar.MILLISECOND) + "\n");
            escritor.write("\n\n-------------------------\n\n");


                    //while ((bytesRead = inputStream.readLine()) != null) {
            bytesRead = inputStream.readLine();
            //socket.getOutputStream().write(get,0,"GET".length());//socket.getOutputStream().write(buffer, 0, bytesRead);
            //inputStream.read(buffer);
            //byteArrayOutputStream.write(buffer, 0, bytesRead);
            response += bytesRead;
            //byteArrayOutputStream.toString("UTF-8");
           // }

            Log.d("GOOGLE GOOGLE says:", response);

            //response += "sem retorno\n";

            escritor.close();

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }  finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        textResponse.setText(response);
        super.onPostExecute(result);
    }

}