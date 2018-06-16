package io.github.lucasduete.cepapp_server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyAidlService extends Service {

    private static final String TAG ="CEPAPP-SERVER";

    public MyAidlService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Chegou no service");
        return myBinder;
    }

    private final AidlInterface.Stub myBinder = new AidlInterface.Stub() {

        private String cep = null;

        private String getCep() {
            return cep;
        }

        private void setCep(String cep) {
            this.cep = cep;
        }

        @Override
        public String getCep(String cepString) throws RemoteException {
            new Thread(() -> {
                Log.d("CEPAPP", "Chegou " + cepString);
                final String stringUrl = String.format(
                        "https://viacep.com.br/ws/%s/json", cepString
                );

                try {
                    URL urlRequest = new URL(stringUrl);
                    HttpURLConnection connection = (HttpURLConnection) urlRequest.openConnection();
                    connection.connect();

                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String linha;
                    StringBuffer buffer = new StringBuffer();
                    while ((linha = reader.readLine()) != null) {
                        buffer.append(linha);
                    }

                    Log.d(TAG, buffer.toString());
                    try {
                        setCep(buffer.toString());
                        Log.d(TAG, "Objeto:");
                        Log.d(TAG, getCep());

                    } catch (Exception ex) {
                        Log.d(TAG, "Deu pau na obtençao de json");
                        Log.d(TAG, ex.toString());
                    }

                    connection.disconnect();

                } catch (IOException ex) {
                    Log.d(TAG, ex.getMessage());
                }
            }).start();

            while (getCep() == null)
                Log.d(TAG, "Buscando");

            Log.d(TAG, "O cep encontado foi: " + getCep());
            return getCep();
        }
    };
}
