package com.example.percorsoculturale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.percorsoculturale.databinding.ActivityQuizBinding;
import com.example.percorsoculturale.tables.Attivita;
import com.example.percorsoculturale.tables.Quiz;

public class QuizActivity extends AppCompatActivity {


public Attivita attivita;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityQuizBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz);

        Quiz quiz=new Quiz(attivita,"Emanuele è finocchio?","Si","No","Anche","Forse",10);
        binding.setQuiz(quiz);


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


}