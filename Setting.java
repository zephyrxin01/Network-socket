package com.example.clientsocket_r;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("SetTextI18n")
public class Setting extends MainActivity {
    Thread clientThread = null;
    Thread readTH ;
    EditText etIP, etPort;
    TextView tvMessages;
    TextView Welcome;
    EditText etMessage;
    Button btnSend;
    Button btnConnect;
    Button btnLeave;
    String SERVER_IP;
    String CLIENT_NAME;
    int SERVER_PORT;
    int connected = 0;
    //new ref
    //private String tmp;
    //private JSONObject jsonWrite, jsonRead;
    Socket socket;//it is client socket
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        etIP = findViewById(R.id.etIP);
        etPort = findViewById(R.id.etPort);
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnConnect = findViewById(R.id.btnConnect);
        btnLeave = findViewById(R.id.btnLeave);
        Welcome = findViewById(R.id.Welcome);

        Intent it = this.getIntent();
        if (it != null) {
            Bundle bundle = it.getExtras();
            if (bundle != null) {
                Welcome.setText("Welcome! " + bundle.getString("cName"));
                CLIENT_NAME = bundle.getString("cName");
                etIP.setText(bundle.getString("cIP"));
                etPort.setText(bundle.getString("cPort"));
            }
        }

        SERVER_IP = etIP.getText().toString().trim();
        SERVER_PORT = Integer.parseInt(etPort.getText().toString().trim());
        clientThread = new Thread(new clientThread());
        clientThread.start();

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //not knowing how to close socket
                tvMessages.setText("");
                SERVER_IP = etIP.getText().toString().trim();
                SERVER_PORT = Integer.parseInt(etPort.getText().toString().trim());
                clientThread = new Thread(new clientThread());
                clientThread.start();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    new Thread(new sendThread(message)).start();
                }
            }
        });

        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                if(connected == 1){
                    String message = "bye";
                    //new Thread(new sendThread(message)).start();
                    connected = 0;
                    input = null;
                    output = null;
                    /* will happen looping*/
                    try {
                        readTH.interrupt();
                        socket.close();
                        System.out.println("closed success");
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("closed failed");
                    }
                }
                finish();
            }
        });

    }
    private DataOutputStream output;
    //private BufferedWriter output;
    private BufferedReader input;

    class clientThread implements Runnable {
        @Override
        public void run() {
            //Socket socket;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                connected = 1;
                output = new DataOutputStream(socket.getOutputStream());
                //output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMessages.setText("Connected \n");
                    }
                });
                /* JSON 處理
                while(socket.isConnected()){
                    tmp = input.readLine();
                    if(tmp!=null){
                        tmp = tmp.substring(tmp.indexOf("{"),tmp.lastIndexOf("}"));
                        jsonRead = new JSONObject(tmp);
                    }
                }

                 */
                readTH = new Thread(new readThread());
                readTH.start();


            } catch (IOException e) {//ADD JSON JSONException|
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMessages.setText("Not Connected \n");
                    }
                });
                e.printStackTrace();
            }
        }
    }
    class readThread implements Runnable {
        String str = null;
        @Override
        public void run() {
            while (true) {
                if(input == null){
                    break;
                }
                try {
                    str = input.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if(str == null){
                        break;
                    }
                    JSONObject jsonObj = new JSONObject(str); //轉JSON物件
                    String name = jsonObj.getString("name");
                    String msg = jsonObj.getString("message");
                    //tvMessages.append( name +": "+ msg +" \n");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvMessages.append( name +": "+ msg +" \n");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class sendThread implements Runnable {
        //sendMessage
        private String message;
        sendThread(String message) {
            this.message = message;
        }
        @Override
        public void run() {
            //message = message + "\n";//not knowing if this bug occur in JAVA
            String msgPrint = CLIENT_NAME + ": " + message +"\n";
            Map map = new HashMap();
            map.put("name", CLIENT_NAME);
            map.put("message", message);
            JSONObject jsonMsgSend = new JSONObject(map);
            byte[] jsonByte = (jsonMsgSend.toString() + "\n").getBytes();
            try {
                output.write(jsonByte);//to test
                output.flush();//error
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //tvMessages.append("client: " + message + " \n");
                    etMessage.setText("");
                }
            });
        }
    }
}