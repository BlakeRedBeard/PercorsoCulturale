package com.example.percorsoculturale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;

import com.example.percorsoculturale.databinding.ActivityAttivitaBinding;

import com.example.percorsoculturale.databinding.ActivityQuizBinding;
import com.example.percorsoculturale.tables.Attivita;
import com.example.percorsoculturale.tables.Puzzle;

public class activity_attivita extends AppCompatActivity {

static  Attivita attivita;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAttivitaBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_attivita);

       binding.BtnForQuiz.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
           Intent intent=new Intent(activity_attivita.this,QuizActivity.class);
           startActivity(intent);
           }
       });

        binding.BtnForPuzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(activity_attivita.this,PuzzleActivity.class);
                startActivity(intent);



            }
        });
    }
}