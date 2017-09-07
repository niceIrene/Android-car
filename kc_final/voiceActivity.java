package com.example.irene.kc_final;


        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothSocket;
        import android.content.Context;
        import android.content.Intent;
        import android.speech.tts.Voice;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.gson.Gson;
        import com.google.gson.JsonParser;
        import com.iflytek.cloud.RecognizerResult;
        import com.iflytek.cloud.SpeechConstant;
        import com.iflytek.cloud.SpeechError;
        import com.iflytek.cloud.SpeechUtility;
        import com.iflytek.cloud.ui.RecognizerDialog;
        import com.iflytek.cloud.ui.RecognizerDialogListener;

        import java.io.IOException;
        import java.io.OutputStream;
        import java.util.ArrayList;
        import java.util.UUID;

public class voiceActivity extends AppCompatActivity {

    TextView tv;

    private BluetoothAdapter myBluetoothAdapter;
    private BluetoothSocket myBluetoothSocket;
    private BluetoothDevice myDevice;
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String carAddr = "20:15:06:24:02:70";
    private OutputStream os;

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
        setContentView(R.layout.activity_voice);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        Toast.makeText(this, carAddr, Toast.LENGTH_LONG).show();
        tv = (TextView) findViewById(R.id.tv);
        // 将“12345678”替换成您申请的 APPID
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5924e400");
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
    public void open(View view) {
        initSpeech(this);
    }

    /**
     * 初始化语音识别
     */
    public void initSpeech(final Context context) {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(context, null);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (!isLast) {
                    //解析语音
                    String result = parseVoice(recognizerResult.getResultString());
                    tv.setText(result);
                }
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    /**
     * 解析语音json
     */
    public String parseVoice(String resultString) {
        TextView textleft = (TextView) findViewById(R.id.text2);
        Gson gson = new Gson();
        Voice voiceBean = gson.fromJson(resultString, Voice.class);

        StringBuffer sb = new StringBuffer();
        ArrayList<Voice.WSBean> ws = voiceBean.ws;
        for (Voice.WSBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }
        String text= sb.toString();
        String stringA = "前进";
        String stringB = "后退";
        String stringC = "停止";
        String stringD = "左转";
        String stringE = "右转";
        if (text.equals(stringA)){

            textleft.setText("f");
            sendMessage("F#");
        }
        if (text.equals(stringB)){

            textleft.setText("b");
            sendMessage("B#");
        }
        if (text.equals(stringC)){

            textleft.setText("s");
            sendMessage("P#");
        }
        if (text.equals(stringD)){

            textleft.setText("l");
            sendMessage("L#");
        }
        if (text.equals(stringE)){

            textleft.setText("r");
            sendMessage("R#");
        }
        return sb.toString();
    }


    /**
     * 语音对象封装
     */
    public class Voice {

        public ArrayList<WSBean> ws;

        public class WSBean {
            public ArrayList<CWBean> cw;
        }

        public class CWBean {
            public String w;
        }
    }
}