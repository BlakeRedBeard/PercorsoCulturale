package com.example.percorsoculturale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.percorsoculturale.databinding.ActivityQuizBinding;
import com.example.percorsoculturale.tables.Attivita;
import com.example.percorsoculturale.tables.Quiz;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class QuizActivity extends AppCompatActivity {


public Attivita attivita;
    //Configurazione db
    public static FirebaseFirestore db;

    ActivityQuizBinding binding;
    //static Quiz quiz;
Quiz quiz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz);



        readData();



        binding.Verifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioGroup checkedRadioGroup=binding.radioGroup;

                int selectId=checkedRadioGroup.getCheckedRadioButtonId();

                RadioButton radioButton= binding.radioGroup.findViewById(selectId);

                //print risposta selezionata if is true
                if(radioButton.getText()==quiz.getRisposta_corretta()) {
                    Toast.makeText(getBaseContext(), radioButton.getText(), Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });

        binding.btnBackAttivita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void readData() {



        db= FirebaseFirestore.getInstance();
        db.collection("Quiz")
                .document("1")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        quiz=documentSnapshot.toObject(Quiz.class);
                        binding.setQuiz(quiz);
                    }
                });

    }

    private void getQuizId(String quizId) {
        // Quiz quiz=new Quiz(attivita,domanda,risposta_corretta,risposta_errata1,risposta_errata2,risposta_errata3,10);
        binding.setQuiz(quiz);
    }


}