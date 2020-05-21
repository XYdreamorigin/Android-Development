package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.helloworld.R.*;
public class Main3Activity extends AppCompatActivity {

    private RadioButton Y;
    private RadioButton N;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_page2);

         Y = (RadioButton) findViewById(id.Y);
         N = (RadioButton)findViewById(id.N);
        Button btn2 = findViewById(id.btn2);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if(Y.isChecked())
                {
                    intent.setClass(Main3Activity.this, Main4Activity.class);
                }
                else if(N.isChecked())
                {
                    intent.setClass(Main3Activity.this, Main5Activity.class);
                }
                else
                {
                    intent.setClass(Main3Activity.this, Main3Activity.class);
                }
                startActivity(intent);
            }
        });
    }
}
