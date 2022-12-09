package com.example.percorsoculturale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.percorsoculturale.databinding.ActivityQuizBinding;
import com.example.percorsoculturale.tables.Attivita;
import com.example.percorsoculturale.tables.Quiz;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {


public Attivita attivita;
    //Configurazione db
    public static FirebaseFirestore db;
    private static ArrayList<String> attrazioni;
    private int id = 0;
    private RadioButton risposta_corretta, risposta_errata1,
            risposta_errata2, risposta_errata3;


    ActivityQuizBinding binding;
    //static Quiz quiz;
    Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String searchQuiz = "";

        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz);
        Bundle extra = getIntent().getExtras();

        System.out.println("search1"+searchQuiz);
        searchQuiz = extra.getString("id");
        id = extra.getInt("Idattrazioni");
        attrazioni = extra.getStringArrayList("attrazioni");
        showQuiz(searchQuiz);

        Intent intent2 = new Intent(getApplicationContext(), MostraAttrazioni.class);
        System.out.println("La second Bkinn Scarlatt"+id);
        intent2.putExtra("Idattrazione", id);
        intent2.putExtra("attrazioni", attrazioni);


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

                startActivity(intent2);

            }
        });

        binding.btnBackAttivita.setOnClickListener(new View.OnClickListener() {
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