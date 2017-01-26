package com.example.patrick.servico_principal;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by patrick on 23/01/17.
 */

public class ServicoParaGPS extends Service {



    private Calendar rightNow = Calendar.getInstance();

    final Handler handler = new Handler();

    //Obtém sua localizção atual
    final Localizador coordenadas = new Localizador(this);

    Runnable runnableCode;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Buscando Coordenadas", LENGTH_LONG).show();

        runnableCode = new Runnable() {

            private int contador = 0;

            @Override
            public void run() {

                Log.v("SERVICO PRINCPAL BOOT", "O serviço principal foi chamado no boot." + contador);



                coordenadas.getMyLocation();//Manda obter as corrdenadas.

                Log.v("1111111111111111", "111111111111111111");

                File arquivo = new File(Environment.getExternalStorageDirectory().toString() + "/" + "Latitude_Longitude_Home.txt");




                if(!coordenadas.coordenadas_atualizadas()){
                    handler.postDelayed(this, 1000);
                    Log.v("2222222222", "2222222222222");
                }else {
                    try {
                        FileWriter escritor = new FileWriter(arquivo, false);

                        Log.v("33333333333333", "333333333333333333");

                        escritor.write("" + coordenadas.getLatitude() + " \n" + coordenadas.getLongitude());//Seta a nova posição de home no arquivo de registro.
                        escritor.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    onDestroy();
                }




            }
        };

        handler.post(runnableCode);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        handler.removeCallbacks(runnableCode);
        Toast.makeText(this, "Nova Posição de Home setada", LENGTH_LONG).show();

    }

}
