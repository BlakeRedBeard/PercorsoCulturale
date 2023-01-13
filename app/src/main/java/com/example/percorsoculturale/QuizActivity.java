package com.example.percorsoculturale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.example.percorsoculturale.databinding.ActivityQuizBinding;
import com.example.percorsoculturale.tables.Attivita;
import com.example.percorsoculturale.tables.Quiz;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

public class QuizActivity extends AppCompatActivity {


public Attivita attivita;
    //Configurazione db
    public static FirebaseFirestore db;
    private int id = 0;
    private RadioButton risposta_corretta, risposta_errata1,
            risposta_errata2, risposta_errata3;
    private String searchQuiz;

    ActivityQuizBinding binding;
    //static Quiz quiz;
    Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchQuiz = "";

        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz);
        Bundle extra = getIntent().getExtras();

        System.out.println("search1"+searchQuiz);
        searchQuiz = extra.getString("id");
        id = extra.getInt("Idattrazioni");
        showQuiz(searchQuiz);

        Intent intent2 = new Intent(getApplicationContext(), MostraAttrazioni.class);
        intent2.putExtra("Idattrazione", id);


        binding.Verifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioGroup checkedRadioGroup=binding.radioGroup;

                int selectId=checkedRadioGroup.getCheckedRadioButtonId();

                RadioButton radioButton= binding.radioGroup.findViewById(selectId);

                //print risposta selezionata if is true
                if(radioButton.getText()==quiz.getRisposta_corretta()) {
                    Toast.makeText(getBaseContext(), radioButton.getText(), Toast.LENGTH_LONG).show();

                    QrcodeActivity.aggiungiPunti(quiz.getPunti());

                }
                MostraAttrazioni.setTrue(id);
                finish();
            }
        });



        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              finish();

            }
        });


    }

    private void showQuiz(String searchQuiz) {

        db= FirebaseFirestore.getInstance();
        db.collection("attività")
                .document(searchQuiz)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                        String language = pref.getString("My_Lang", "");
                        ArrayList<String> risposte = new ArrayList<>();
                        risposte.add(documentSnapshot.getString("risposta_corretta"+language));
                        risposte.add(documentSnapshot.getString("risposta_errata1"+language));
                        risposte.add(documentSnapshot.getString("risposta_errata2"+language));
                        risposte.add(documentSnapshot.getString("risposta_errata3"+language));
                        Collections.shuffle(risposte);
                        quiz = new Quiz(documentSnapshot.getString("domanda"+language),
                                        risposte.get(0),
                                        risposte.get(1),
                                        risposte.get(2),
                                        risposte.get(3),
                                        Integer.parseInt(documentSnapshot.getString("tempo")),
                                        Integer.parseInt(documentSnapshot.getString("punti")));
                        binding.setQuiz(quiz);
                    }
                });

    }

}