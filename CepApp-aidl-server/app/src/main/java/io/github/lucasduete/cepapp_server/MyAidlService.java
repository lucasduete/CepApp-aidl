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
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "CRIIIOUU");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Chegou no service");
        return myBinder;
    }

    private final AidlInterface.Stub myBinder = new AidlInterface.Stub() {

        private String cep;

        private String getCep() {
            return cep;
        }

        private void setCep(String cep) {
            this.cep = cep;
        }

        @Override
        public String getCep(String cepString) throws RemoteException {
            new Thread(() -> {
                Log.d("CEPAPP", "Alooooooooo");
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
                        JSONObject object = new JSONObject(buffer.toString());
                        Log.d(TAG, "Objeto:");
                        Log.d(TAG, object.toString());

                        setCep(object.toString());

                    } catch (Exception ex) {
                        Log.d(TAG, "Deu pau na conversao de json");
                        Log.d(TAG, ex.toString());
                    }

                    connection.disconnect();

                } catch (IOException ex) {
                    Log.d(TAG, ex.getMessage());
                }
            }).start();

            Log.d(TAG, "O cep encontado foi:");
            Log.d(TAG, getCep());
            return getCep();
        }
    };
}
