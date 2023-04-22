package com.example.serversocket_r;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
    EditText etPort,etName;
    Button btnConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etPort = findViewById(R.id.etPort);
        btnConnect = findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("cName",etName.getText().toString());
                Bundle bundle1 = new Bundle();
                bundle1.putString("cPort",etPort.getText().toString());
                Intent it = new Intent();
                it.putExtras(bundle);
                it.putExtras(bundle1);
                it.setClass(MainActivity.this,ChatBox.class);
                startActivity(it);



            }

        });
    }

}