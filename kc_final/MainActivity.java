package com.example.irene.kc_final;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button Button_p,Button_d,Button_g,Button_v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button_p = (Button)findViewById(R.id.button);
        Button_d = (Button)findViewById(R.id.button2);
        Button_g = (Button)findViewById(R.id.button3);
        Button_v = (Button)findViewById(R.id.button9);

        Button_p.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                System.out.println("11111");
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(MainActivity.this, photoActivity.class);
                    MainActivity.this.startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        Button_v.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                System.out.println("11111");
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(MainActivity.this, voiceActivity.class);
                    MainActivity.this.startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        Button_d.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                System.out.println("5555555");
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(MainActivity.this, directionActivity.class);
                    MainActivity.this.startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        Button_g.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                System.out.println("33333");
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(MainActivity.this, OrientationActivity.class);
                    MainActivity.this.startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }
}
