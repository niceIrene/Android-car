package com.example.irene.kc_final;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;


public class photoActivity extends AppCompatActivity{
    private Button ButtonF1,ButtonL1,ButtonR1,ButtonS1;
    private BluetoothAdapter myBluetoothAdapter;
    private BluetoothSocket myBluetoothSocket;
    private BluetoothDevice myDevice;
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String carAddr = "20:15:06:24:02:70";
    private OutputStream os;

    private ServerSocket cameraSocket;
    private ServerSocket messageSocket;
    private Socket fromServer;

    private Bitmap bitmap;

    public static Handler handler;

    public String serverIP;
    public static final int CAMERA_PORT = 8686;
    public static final int SERVER_PORT = 8080;

    private Button connectWifiButton;
    private Button imageGetButton;
    private ImageView receivedImageView;
    private EditText clientIpEditText;
    private TextView textResponse;


    public void sendMessage(String msg) {
        try {
            this.setTitle(""+msg);
            os.write(msg.getBytes());
        } catch (IOException e) {
            Toast.makeText(this, "Message send failed", Toast.LENGTH_SHORT).show();
            //finish();
        }
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ButtonF1 = (Button)findViewById(R.id.button18);
        ButtonL1 = (Button)findViewById(R.id.button20);
        ButtonR1 = (Button)findViewById(R.id.button21);
        ButtonS1 = (Button)findViewById(R.id.button19);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        Toast.makeText(this, carAddr, Toast.LENGTH_LONG).show();


        ButtonF1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    sendMessage("F#");
                    Toast.makeText(photoActivity.this, "Forward", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });
        ButtonS1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    sendMessage("P#");
                    Toast.makeText(photoActivity.this, "Forward", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });
        ButtonL1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    sendMessage("L#");
                    Toast.makeText(photoActivity.this, "Forward", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });
        ButtonR1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    sendMessage("R#");
                    Toast.makeText(photoActivity.this, "Forward", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(bitmap!=null && msg.arg1 == 123)
                    receivedImageView.setImageBitmap(bitmap);
                super.handleMessage(msg);
            }
        };

        connectWifiButton = (Button) findViewById(R.id.connect_wifi);
        imageGetButton = (Button) findViewById(R.id.get_image);
        clientIpEditText = (EditText) findViewById(R.id.get_client_ip);
        clientIpEditText.setVisibility(View.VISIBLE);
        textResponse = (TextView) findViewById(R.id.wifi_state);
        receivedImageView = (ImageView) findViewById(R.id.image_receive);

        Thread videoThread = new ReceiveVideo();
        videoThread.start();

        connectWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverIP = clientIpEditText.getText().toString();
                ConnectClientTask connectClientTask = new ConnectClientTask(
                        serverIP,
                        SERVER_PORT,
                        textResponse);
                connectClientTask.execute();
                clientIpEditText.setVisibility(View.GONE);
            }
        });



        imageGetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "GetImage";
                SendClientTask sendMessage = new SendClientTask(serverIP, 8080, message);
                sendMessage.execute();
            }
        });
    }


    class ReceiveVideo extends Thread{
        private int length = 0;
        private int num = 0;
        private byte[] buffer = new byte[2048];
        private byte[] data = new byte[204800];

        @Override
        public void run(){
            try{
                cameraSocket = new ServerSocket(CAMERA_PORT);
                while(true){
                    Socket socket = cameraSocket.accept();
                    try{
                        InputStream input = socket.getInputStream();
                        Log.d("Image","GetImage!!!!");
                        num = 0;
                        do{
                            length = input.read(buffer);
                            if(length >= 0){
                                System.arraycopy(buffer,0,data,num,length);
                                num += length;
                            }
                        }while(length >= 0);

                        new setImageThread(data,num).start();
                        input.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        socket.close();
                    }
                }

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    class setImageThread extends Thread{
        private byte[]data;
        private int num;
        public setImageThread(byte[] data, int num){
            this.data = data;
            this.num = num;
        }
        @Override
        public void run(){
            bitmap = BitmapFactory.decodeByteArray(data, 0, num);
            Message msg=new Message();
            msg.arg1 = 123;
            handler.sendMessage(msg);
        }
    }


    public class ConnectClientTask extends AsyncTask<Void, Void, Void> {

        private TextView textResponse;
        String dstAddress;
        int dstPort = 8080;
        String response = "Connected";

        ConnectClientTask(String addr, int port, TextView tResponse) {
            dstAddress = addr;
            dstPort = port;
            textResponse = tResponse;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
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

    public class SendClientTask extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String message = "";
        String response = "";

        SendClientTask(String addr, int port, String msg) {
            dstAddress = addr;
            dstPort = port;
            message = msg;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                Log.e("ActivityDemo", "Sending message " + message);

                OutputStream outputStream = socket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(message);
                printStream.close();


            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        myDevice = myBluetoothAdapter.getRemoteDevice(carAddr);

        try {
            myBluetoothSocket = myDevice.createRfcommSocketToServiceRecord(MY_UUID);
            //Toast.makeText(this,"sssssssssssss",Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Toast.makeText(this, "Connected failed", Toast.LENGTH_SHORT).show();
        }
        myBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            myBluetoothSocket.connect();

        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                Toast.makeText(this, "Connected socket failed", Toast.LENGTH_SHORT).show();
                myBluetoothSocket.close();
            } catch (IOException closeException) {}
            //finish();
        }

        try {
            os = myBluetoothSocket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(this, "Connected output failed", Toast.LENGTH_SHORT).show();
            //finish();
        }
    }
}