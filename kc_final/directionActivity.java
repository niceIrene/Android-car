package com.example.irene.kc_final;

import android.support.v7.app.AppCompatActivity;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class directionActivity extends AppCompatActivity {

    private Button ButtonF,ButtonB,ButtonL,ButtonR,ButtonS,ButtonC;
    private BluetoothAdapter myBluetoothAdapter;
    private BluetoothSocket myBluetoothSocket;
    private BluetoothDevice myDevice;
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String carAddr = "20:15:06:24:02:70";
    private OutputStream os;
    public int ii=0;

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

    private void delay(int ms){
        try {
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void chaoche(){
        if(ii==0) {
            Toast.makeText(this,"左前",Toast.LENGTH_LONG);
            sendMessage("L#");
            delay(1500);
            ii++;
        }
        if(ii==1) {
            Toast.makeText(this,"右前",Toast.LENGTH_LONG);
            sendMessage("R#");
            delay(1500);
            ii++;
        }
        if(ii==2) {
            Toast.makeText(this,"加速",Toast.LENGTH_LONG);
            sendMessage("F#");
            delay(500);
            sendMessage("F#");
            delay(500);
            sendMessage("F#");
            delay(500);
            sendMessage("F#");
            delay(500);
            sendMessage("F#");
            delay(500);


            ii++;
        }
        if(ii==3) {
            Toast.makeText(this,"右前",Toast.LENGTH_LONG);
            sendMessage("R#");
            delay(1200);
            ii++;
        }
        if(ii==4) {
            Toast.makeText(this,"左前",Toast.LENGTH_LONG);
            sendMessage("L#");
            delay(1500);
            ii++;
        }
        if(ii>=5) {
            Toast.makeText(this,"前进",Toast.LENGTH_LONG);
            sendMessage("F#");
            delay(3000);
            ii=0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        ButtonF = (Button)findViewById(R.id.button4);
        ButtonB = (Button)findViewById(R.id.button6);
        ButtonL = (Button)findViewById(R.id.button7);
        ButtonR = (Button)findViewById(R.id.button8);
        ButtonS = (Button)findViewById(R.id.button5);
        ButtonC = (Button)findViewById(R.id.button10);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        Toast.makeText(this, carAddr, Toast.LENGTH_LONG).show();

        ButtonF.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    sendMessage("F#");
                    Toast.makeText(directionActivity.this, "Forward", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });

        ButtonC.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    chaoche();
                    ii=0;
                    return true;
                }
                return false;
            }
        });
        ButtonB.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    sendMessage("B#");
                    Toast.makeText(directionActivity.this, "Backward", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });

        ButtonL.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    sendMessage("L#");
                    Toast.makeText(directionActivity.this, "Turn left", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });

        ButtonR.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    sendMessage("R#");
                    Toast.makeText(directionActivity.this, "Turn right", Toast.LENGTH_LONG).show();
                    return true;
                }

                return false;
            }
        });

        ButtonS.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    sendMessage("P#");
                    Toast.makeText(directionActivity.this, "Stop", Toast.LENGTH_LONG).show();
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        myDevice = myBluetoothAdapter.getRemoteDevice(carAddr);
        try {
            myBluetoothSocket = myDevice.createRfcommSocketToServiceRecord(MY_UUID);
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
        }
        try {
            os = myBluetoothSocket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(this, "Connected output failed", Toast.LENGTH_SHORT).show();
            //finish();
        }
    }
}
