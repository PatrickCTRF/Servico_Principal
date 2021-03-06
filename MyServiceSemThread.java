package com.example.patrick.servico_principal;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import static android.widget.Toast.LENGTH_LONG;
import static java.lang.Double.parseDouble;

/*

Para o serviço poder ser invocado e gerar arquivos a partir do boot,
ele deve ter sido chamado manualmente pelo usuário ao menos uma vez.
Nao sei ao certo se o serviço nao roda caso nao tenha sido chamado manualmente
ou se o FileWriter nao executa se nao tiver sido chamado manualmente ao menos uma vez,
mas se executarmos ele manualmente no mínimo uma vez, dá certo.

Talvez seja necessário habilitar também o envio deste ara algum Log.v para o console. Nao sei pq é necessário haver um log, mas isto ofaz funcionar no BOOT. Talvez seja uma solicitação para começar a rodar, uma conexão ou algo assim.

*/

public class MyServiceSemThread extends Service {

    final Handler handler = new Handler();
    final AquisicaoSensores info = new AquisicaoSensores(this);
    /*final EditText editTextAddress1 = MainActivity.editTextAddress;
    final EditText editTextPort1 = MainActivity.editTextPort;
    final TextView response1 = MainActivity.response;
    EditText editTextAddress = editTextAddress1;
    EditText editTextPort = editTextPort1;
    TextView response = response1;
    boolean atualiza_home = MainActivity.atualiza_home;*/
    double home_latitude = 0, home_longitude = 0;
    double margemDeErroHome = 2;
    String ip = "192.168.0.105", aux = null, aux2 = null;
    int porta = 6789;



    //Obtém sua localizção atual
    final Localizador locationListener = new Localizador(this);

    Runnable runnableCode;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service Started", LENGTH_LONG).show();

        runnableCode = new Runnable() {

            private int contador = 0;

            @Override
            public void run() {

                Log.v("SERVICO PRINCPAL BOOT", "O serviço principal foi chamado." + contador);




                /*if(MainActivity.atualiza_home){
                    //if(MainActivity.atualiza_home != null){//   VERIFICAR SE ISTO DÁ ERRADO COM O MAINACTIVITY FECHADO. QUALQUER COISA, SALVA ESTA INFO NUM TXT.
                        MainActivity.atualiza_home = false;

                        home_latitude = locationListener.getLatitude();
                        home_longitude = locationListener.getLongitude();

                        Log.v("HOME LATITUDE LONGITUDE", "" + home_latitude + " " + home_longitude);
                    //}
                }

                if(editTextAddress.getText().toString().equals(null) || editTextPort.getText().toString().equals(null)) {//Se a entrada nao for nula, crie um cliente para o servidor e execute-o.
                    Log.v("STRING SERVIDOR", "String nao nula recebida" + editTextAddress.getText().toString() + " fim");
                    Client myClient = new Client(editTextAddress.getText().toString(), Integer.parseInt(editTextPort.getText().toString()), response, info);//Cria o cliente.
                    myClient.execute();//Executa cliente.

                }else{
                    Log.v("STRING SERVIDOR", "String NULA recebida" + editTextAddress.getText().toString() + " fim");
                }*/

                Calendar calendario = Calendar.getInstance();

                File arquivoDados = new File(Environment.getExternalStorageDirectory().toString() + "/" + "_InformacoesDaVidaDoUsuario.txt");

                File arquivoHome = new File(Environment.getExternalStorageDirectory().toString() + "/" + "Latitude_Longitude_Home.txt");

                try {

                    BufferedReader bufferLeitura = new BufferedReader(new FileReader(arquivoHome));

                    home_latitude = parseDouble(bufferLeitura.readLine());
                    home_longitude = parseDouble(bufferLeitura.readLine());
                    bufferLeitura.close();

                    FileWriter escritor;

                    if(Math.pow(margemDeErroHome, 2) >= Math.pow((locationListener.getLatitude()-home_latitude),2)+Math.pow((locationListener.getLongitude()-home_longitude),2)) {
                    //Se houver condição de enviar os dados ao servidor, envie todos os dados disponíveis.
                        Log.v("HOMEinfo", "ESTÁ NA HOME");

                        BufferedReader leituraDados = new BufferedReader(new FileReader(arquivoDados));

                        Client myClient;
                        if ((aux = leituraDados.readLine()) != null) {//Se o arquivo nao estiver vazio...

                            Log.v("SERVIDOR", "DADOS SALVOS ENVIANDO");

                            while((aux2 = leituraDados.readLine()) != null){//Leia tudo que está no arquivo.
                                aux = "\n" + aux2;
                                aux2 = null;
                            }

                            myClient = new Client(ip, porta, aux + info.getInfo());//Envie para o servidor os dados que estavam salvos.
                            myClient.execute();
                            //myClient.setInfo(info.getInfo());//Agora envie os dados atuais.
                            //myClient.execute();

                            escritor = new FileWriter(arquivoDados, false);//apaga o buffer de dados e o fecha.
                            escritor.write("");
                            escritor.close();


                        }else{//Se o arquivo estiver vazio...

                            Log.v("SERVIDOR", "DADOS ENVIANDO");

                            myClient = new Client(ip, porta, info.getInfo());//Envia somente os dados atuais.
                            myClient.execute();

                        }


                    }else{//Se nao houver condições de enviar ao servidor, guarde os dados num arquivo.
                        Log.v("HOMEinfo", "NÃO ESTÁ NA HOME");

                        escritor = new FileWriter(arquivoDados, true);
                        escritor.write("\n\nTempo atual: " + calendario.get(Calendar.HOUR) + ":" + calendario.get(Calendar.MINUTE) + ":" + calendario.get(Calendar.SECOND) + "," + calendario.get(Calendar.MILLISECOND) + "\n" + info.getInfo() + "\n\n" + locationListener.getMyLocation() + "\n----------------\n");
                        escritor.close();

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(++contador<100) handler.postDelayed(this, 1000);
                else onDestroy();
            }
        };

        handler.post(runnableCode);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        locationListener.removeListener();//Deixa de requisitar atualizações ao sistema e remove este listener. Economiza energia.
        info.onDestroy();//Deixa de requisitar atualizações ao sistema e remove os listener. Economiza energia e evita relatório de erros.
        handler.removeCallbacks(runnableCode);//Retira todas as chamadas agendadas deste serviço.
        Toast.makeText(this, "Service Destroyed", LENGTH_LONG).show();
        super.onDestroy();
    }

}
