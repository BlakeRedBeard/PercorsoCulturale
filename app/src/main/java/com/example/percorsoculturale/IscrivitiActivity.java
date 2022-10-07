package com.example.percorsoculturale;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class IscrivitiActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button mDatePickerBtn ;
    //Date picker
    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.iscriviti); //classe R serve per istanziare risorse della cartella res


        //for date picker iscrizione
        initDatePicker();
        dateButton=findViewById(R.id.datePickerButton);
        //data di oggi
        //dateButton.setText(getTodaysDate());


        //linkLogin
        FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.returnButtonLogin);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IscrivitiActivity.this, LoginActivity.class));
            }

        });

    }
//per data di oggi
    private String getTodaysDate() {
        Calendar cal= Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        month=month + 1;
        int day=cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day,month,year);
    }


    //menu date picker
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
             month=month +1 ;
             String date=makeDateString(day,month,year);
            // dateButton.setText(date);
            }
        };

        Calendar cal= Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        int day=cal.get(Calendar.DAY_OF_MONTH);

       int style= AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog=new DatePickerDialog(this,style,dateSetListener,year,month,day);
    }

    ///per creare la stringa data preview button
    private String makeDateString(int day, int month, int year) {

        return getMonthFormat(month) +" "+ day +" " + year ;
    }

    //per attribuire il numero del mese al nome del mese alla preview button
    private String getMonthFormat(int month) {

        if(month ==1)
            return "GENNAIO";
        if(month==2)
            return "FEBBRAIO";
        if(month ==3)
            return "MARZO";
        if(month==4)
            return "APRILE";
        if(month ==5)
            return "MAGGIO";
        if(month==6)
            return "GIUGNO";
        if(month ==7)
            return "LUGLIO";
        if(month==8)
            return "AGOSTO";
        if(month ==9)
            return "SETTEMBRE";
        if(month==10)
            return "OTTOBE";
        if(month ==11)
            return "NOVEMBRE";
        if(month==12)
            return "DICEMBRE";

        return "GENNAIO";

    }


//start date picker
    public void openDatePicker(View view) {

        datePickerDialog.show();
    }

    //registrazione dell'utente a firebase
    protected void signUp(String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("DEBUG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(IscrivitiActivity.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            //Aggiornare interfaccia, l'utente si è registrato con successo
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("DEBUG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(IscrivitiActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //Aggiornare interfaccia, il sistema non è riuscito a registrare l'utente
                        }
                    }
                });
    }
}
