package com.example.irene.kc_final;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;


public class OrientationActivity extends AppCompatActivity{
/*
    private BluetoothAdapter myBluetoothAdapter2;
    private BluetoothSocket myBluetoothSocket2;
    private BluetoothDevice myDevice2;
    final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String carAddr = "20:15:06:24:02:70";
    private OutputStream os2;
    public void sendMessage2(String msg) {
        try {
            this.setTitle(""+msg);
            os2.write(msg.getBytes());
        } catch (IOException e) {
            Toast.makeText(this, "Message send failed", Toast.LENGTH_SHORT).show();
            //finish();
        }
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
*/
    private SensorManager sm;
    //需要两个Sensor
    private Sensor aSensor;
    private Sensor mSensor;
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];
    private float numcount=0;
    private static final String TAG ="sensor";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation);
/*
        myBluetoothAdapter2 = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter2 == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }
        myDevice2 =myBluetoothAdapter2.getRemoteDevice(carAddr);
        try {
            myBluetoothSocket2 = myDevice2.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Toast.makeText(this, "Connected  device failed", Toast.LENGTH_SHORT).show();
        }
        myBluetoothAdapter2.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            myBluetoothSocket2.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                Toast.makeText(this, "Connected socket failed", Toast.LENGTH_SHORT).show();
                myBluetoothSocket2.close();
            } catch (IOException closeException) {}
            //finish();
        }

        try {
            os2 = myBluetoothSocket2.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(this, "Connected output stream failed", Toast.LENGTH_SHORT).show();
            //finish();
        }
        Toast.makeText(this, carAddr, Toast.LENGTH_LONG).show();
*/
        TextView textleft = (TextView) findViewById(R.id.text1);
        sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(myListener, mSensor,SensorManager.SENSOR_DELAY_NORMAL);
        //更新显示数据的方法
        float[] values = new float[3];
        float[] R = new float[9];
        float destin;
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);


        // 要经过一次数据格式的转换，转换为度
        values[0] = (float) Math.toDegrees(values[0]);
        Log.i(TAG, values[0]+"");
        //values[1] = (float) Math.toDegrees(values[1]);
        //values[2] = (float) Math.toDegrees(values[2]);
        if(numcount==0) {numcount=values[0];}
        destin=values[0]-numcount;
        if(destin >= -45 && destin < 45){
            Log.i(TAG, "正北");
            textleft.setText("前进");
            //sendMessage2("F#");
        }
        else if(destin >= 45 && destin < 135){
            Log.i(TAG, "东北");
            textleft.setText("右转");
            //sendMessage2("R#");
        }

        else if((destin >= 135 && destin <= 180) || (destin) >= -180 && destin < -135){
            Log.i(TAG, "正南");
            textleft.setText("后退");
            //sendMessage2("B#");

        }
        else if(destin >= -135 && destin <-45){
            Log.i(TAG, "西南");
            textleft.setText("左转");
            //sendMessage2("L#");

        }
    }

    //再次强调：注意activity暂停的时候释放
    public void onPause(){
        sm.unregisterListener(myListener);
        super.onPause();
    }

    final SensorEventListener myListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                magneticFieldValues = sensorEvent.values;
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                accelerometerValues = sensorEvent.values;
            TextView textleft1 = (TextView) findViewById(R.id.text1);
            float[] values = new float[3];
            float[] R = new float[9];
            float destin;
            SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
            SensorManager.getOrientation(R, values);


            // 要经过一次数据格式的转换，转换为度
            values[0] = (float) Math.toDegrees(values[0]);
            Log.i(TAG, values[0]+"");
            //values[1] = (float) Math.toDegrees(values[1]);
            //values[2] = (float) Math.toDegrees(values[2]);
            if(numcount==0) {numcount=values[0];}
            destin=values[0]-numcount;
            if(destin >= -45 && destin < 45){
                Log.i(TAG, "正北");
                textleft1.setText("前进");
                //sendMessage2("F#");
            }
            else if(destin >= 45 && destin < 135){
                Log.i(TAG, "东北");
                textleft1.setText("右转");
                //sendMessage2("R#");
            }

            else if((destin >= 135 && destin <= 180) || (destin) >= -180 && destin < -135){
                Log.i(TAG, "正南");
                textleft1.setText("后退");
                //sendMessage2("B#");

            }
            else if(destin >= -135 && destin <-45){
                Log.i(TAG, "西南");
                textleft1.setText("左转");
                //sendMessage2("L#");

            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

}