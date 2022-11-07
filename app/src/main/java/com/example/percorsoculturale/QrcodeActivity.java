package com.example.percorsoculturale;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import javax.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class QrcodeActivity extends AppCompatActivity {

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(QrcodeActivity.this);

                intentIntegrator.setPrompt("For flash use volume app");

                intentIntegrator.setBeepEnabled(true);

                intentIntegrator.setOrientationLocked(true);

                intentIntegrator.setCaptureActivity(Capture.class);

                intentIntegrator.initiateScan();

            }

        });

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(intentResult.getContents() != null){

            AlertDialog.Builder builder = new AlertDialog.Builder(QrcodeActivity.this);

            builder.setTitle("result");
            builder.setMessage(intentResult.getContents());
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();

                }
            });

            builder.show();
        }

        else{

            Toast.makeText(getApplicationContext(), "ciao", Toast.LENGTH_SHORT).show();

        }

    }

}
