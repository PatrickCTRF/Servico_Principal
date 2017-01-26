package com.example.patrick.servico_principal;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by patrick on 10/19/16.
 */
public class Localizador extends ContextWrapper implements LocationListener {

    private boolean registrou_manager;//Esta variável evita que fiquemos registrando o anager vaárias vezes a cada chamada de mgetmylocation.
                            //1 == registrado, 0 == nao registrado.
    private String myLocation;//Um string que guarda a nossa posição atual em forma de texto para facilitar a escrita em arquivo.
    private LocationManager locationManager;//Este é o manager que usaremos para solicitar acesso à localizaçãoes.
    private double latitude;
    private double longitude;
    private boolean aguardando_coordenadas;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean coordenadas_atualizadas() {//Retorna true se as coordenaadas estao atualizadas e false se nao.
        return !aguardando_coordenadas;
    }

    @Override
    public void onLocationChanged(Location location) {
       latitude = location.getLatitude();
       longitude = location.getLongitude();
        aguardando_coordenadas = false;//Nao esta mais esperando pra receber  as coodenadas.

        myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude() + " Incerteza = " + location.getAccuracy();

        //Ver os dados através do LOG.
        Log.e("LOCALIZAÇÃO ATUAL", myLocation);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public String getMyLocation() {

        
        if(!registrou_manager) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);//Solicita atualizações de localização por WiFi para este listener (o próprio  obeto instanciado a partir desta classe).
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);//Solicita atualizações de localização por GPS para este listener (o próprio  obeto instanciado a partir desta classe).
        }

        return myLocation;
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public Localizador(Context base) {//Este construtor já registra o próprio onjeto como locationListener.
        super(base);
        myLocation = "O valor da localização não está sendo alterado";
        aguardando_coordenadas = true;


    }
}
