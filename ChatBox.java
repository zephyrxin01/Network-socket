package com.example.serversocket_r;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ChatBox extends AppCompatActivity {
    private static ServerSocket serverSocket;
    Thread serverThread = null;
    TextView tvIP, tvPort;
    TextView tvMessages;
    TextView tvConnectionStatus;
    EditText etMessage;
    Button btnSend;
    Button btnRefresh;
    private static int count = 0; //計算有幾個 Client 端連線
    public static String SERVER_IP = "";
    public static int SERVER_PORT ;
    String message;
    String mode = "  ----------------SERVER MODE------------------";
    private static ArrayList clients = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        tvIP = findViewById(R.id.tvIP);
        tvPort = findViewById(R.id.tvPort);
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnRefresh = findViewById(R.id.btnRefresh);
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus);

        SERVER_IP = getLocalIpAddress();
        //init value from main
        Intent it = this.getIntent();
        if (it != null) {
            Bundle bundle = it.getExtras();
            if (bundle != null) {
                SERVER_PORT = Integer.parseInt(bundle.getString("cPort"));
            }
        }

        //create server thread
        serverThread = new Thread(new serverThread());
        serverThread.start();

        //send message
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = etMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    //new Thread(new sendThread(message,"server")).start();
                    new Thread(new castMsg(message,"server")).start();
                }
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    serverSocket.close();
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface interface1 = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddress = interface1.getInetAddresses(); enumIpAddress.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddress.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    private DataOutputStream output;
    private BufferedReader input;

    class serverThread implements Runnable {
        @Override
        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMessages.setText("Not connected\n");
                        tvIP.setText("IP: " + SERVER_IP);
                        tvPort.setText("Port: " + String.valueOf(SERVER_PORT));
                    }
                });
                while(!serverSocket.isClosed()){
                    waitNewClient();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void waitNewClient(){//delete static
        try{
            Socket socket = serverSocket.accept();
            ++ count;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvConnectionStatus.setText(mode + "\n" +"Client Connection:" + count);
                    tvMessages.append("Connected \n");
                }
            });
            // 呼叫加入新的 Client 端
            new Thread(new addNewClient(socket)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class addNewClient implements Runnable {
        private Socket socket;
        addNewClient(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run(){
            // 取得網路串流
            try {
                clients.add(socket);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                new Thread(new readThread(socket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally{
                // 移除客戶端
                if(!socket.isConnected()){
                    clients.remove(socket);
                    --count;
                    tvConnectionStatus.setText(mode + "\n" +"Client Connection:" + count);
                }
            }
        }
    }

    private class readThread implements Runnable {
        String str = null;
        private Socket socket;
        readThread(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            while (true) {
                try {
                    str = input.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if(str == null){
                        //disconnect
                        clients.remove(socket);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvMessages.append("disconnected\n");
                                --count;
                                tvConnectionStatus.setText(mode + "\n" +"Client Connection:" + count);
                            }
                        });
                        return;
                    }
                    JSONObject jsonObj = new JSONObject(str); //轉JSON物件
                    String name = jsonObj.getString("name");
                    String msg = jsonObj.getString("message");
                    new Thread(new castMsg(msg,name)).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class castMsg implements Runnable {
        private String message;
        private String sender;
        castMsg(String message, String sender) {
            this.message = message;
            this.sender = sender;
        }
        @Override
        public void run() {
            // 創造socket陣列
            Socket[] clientArrays =new Socket[clients.size()];
            // 將 clients 轉換成陣列存入 clientArrays
            clients.toArray(clientArrays);
            String msgPrint = sender + ": " + message +"\n";
            Map map = new HashMap();
            map.put("name", sender);
            map.put("message", message);
            JSONObject jsonMsgSend = new JSONObject(map);
            byte[] jsonByte = (jsonMsgSend.toString() + "\n").getBytes();
            for (Socket socket : clientArrays ) {
                if(!socket.isConnected()){
                    continue;
                }
                try {
                    output = new DataOutputStream(socket.getOutputStream());
                    output.write(jsonByte);//to test
                    output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMessages.append(msgPrint);
                    etMessage.setText("");
                }
            });
            //new Thread(new readThread()).start();
        }
    }
}