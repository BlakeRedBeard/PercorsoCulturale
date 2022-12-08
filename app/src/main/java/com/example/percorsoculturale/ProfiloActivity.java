package com.example.percorsoculturale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ProfiloActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);


        LinearLayout rl = (LinearLayout) findViewById(R.id.LinearProfile2);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfiloActivity.this, BadgeActivity.class));
            }
        });
   
   
    }

    public void backHome(View view) {

        Intent backIntent=new Intent(this,RicercaPercorsiActivity.class);
        startActivity(backIntent);

    }
}