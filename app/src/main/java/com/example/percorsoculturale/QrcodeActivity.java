package com.example.percorsoculturale;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.percorsoculturale.tables.Quiz;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class QrcodeActivity extends AppCompatActivity {

    Button btn;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private static ArrayList<String> attrazioni;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

                IntentIntegrator intentIntegrator = new IntentIntegrator(QrcodeActivity.this);
                intentIntegrator.setPrompt("For flash use volume app");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(Capture.class);
                intentIntegrator.initiateScan();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(intentResult.getContents() != null){

            AlertDialog.Builder builder = new AlertDialog.Builder(QrcodeActivity.this);

            builder.setTitle("result");
            builder.setMessage(intentResult.getContents());

            String a = intentResult.getContents().toString();
            System.out.println("APOLLO"+a);


            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();

                }
            });

            if(a.equals("https://www.qrfy.com/z4UHo55")) {
                int id = 0;
                Bundle extra = getIntent().getExtras();



                id = extra.getInt("Idattrazione");
                attrazioni = extra.getStringArrayList("attrazioni");

                String c = String.valueOf(id);

                checkAttivita(attrazioni.get(id), id, attrazioni);
            }else{

            }
        }

        else{

            Toast.makeText(getApplicationContext(), "ciao", Toast.LENGTH_SHORT).show();

        }

    }

    private void checkAttivita(String check, int id, ArrayList<String> attrazioni) {

        db.collection("attrazione")
                .document(check)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                String tipoAttivita = document.getString("attivita");

                                if (tipoAttivita.equals("quiz")){

                                    String quiz = document.getString("NomeQuiz");
                                    Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                                    intent.putExtra("Idattrazione", id);
                                    intent.putExtra("attrazioni", attrazioni);
                                    intent.putExtra("quiz", quiz);
                                    System.out.println("attrazioni"+attrazioni);
                                    System.out.println("idddddddddd"+id);
                                    startActivity(intent);
                                }
                                /*else if (tipoAttivita == "puzzle"){
                                    String puzzle = document.getString("NomePercorso");
                                    Intent intent = new Intent(getApplicationContext(), PuzzleActivity.class);
                                    intent.putExtra("puzzle", puzzle);
                                    startActivity(intent);
                                }*/


                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }



    }



