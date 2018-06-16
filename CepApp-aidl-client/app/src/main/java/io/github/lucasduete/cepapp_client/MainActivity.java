package io.github.lucasduete.cepapp_client;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import io.github.lucasduete.cepapp_server.AidlInterface;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="CEPAPP-CLIENT";

    private AidlInterface aidlInterface = null;
    private ServiceConnection serviceConnection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "Entrou em service connected");
                aidlInterface = AidlInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "Entrou em service Disconnected");
                aidlInterface = null;
            }
        };

        if (aidlInterface == null) {
            Intent intent = new Intent("cepapp_server.AIDL_SERVER");
            intent.setPackage("io.github.lucasduete.cepapp_server");
            Log.d(TAG, String.valueOf(bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)));
        }

        final EditText editText = (EditText) findViewById(R.id.editTextCep);
        final Context context = this;

        Button button = findViewById(R.id.button);
        button.setOnClickListener((view) -> {
            Log.d(TAG, "butaaao");
            try {
                if (aidlInterface == null)
                    throw new Exception("AIDL NULL");
                else
                    updateView(editText.getText().toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    private void updateView(String cep) {
        TextView textView;
        JSONObject object;

        try {
            Log.d(TAG, aidlInterface.getCep(cep).toString());
            object = new JSONObject(aidlInterface.getCep(cep).toString());

            textView = (TextView) findViewById(R.id.editTextLogradouro);
            textView.setText(object.getString("logradouro"));

            textView = (TextView) findViewById(R.id.editTextComplemento);
            textView.setText(object.getString("complemento"));

            textView = (TextView) findViewById(R.id.editTextBairro);
            textView.setText(object.getString("bairro"));

            textView = (TextView) findViewById(R.id.editTextCidade);
            textView.setText(object.getString("localidade"));

            textView = (TextView) findViewById(R.id.editTextEstado);
            textView.setText(object.getString("uf"));

        } catch (Exception ex) {
            Log.d(TAG, "Deu ruim ao preencher");
            ex.printStackTrace();
        }
    }
}
