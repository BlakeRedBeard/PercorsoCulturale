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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.percorsoculturale.tables.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.internal.InternalTokenProvider;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class IscrivitiActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button mDatePickerBtn;
    //Date picker
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    EditText nomeR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.iscriviti); //classe R serve per istanziare risorse della cartella res


        //for date picker iscrizione
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
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
        TextInputLayout mail = (TextInputLayout) findViewById(R.id.iscrizioneEmail);
        TextInputLayout password = (TextInputLayout) findViewById(R.id.iscrizionePassword);

        Button iscriviti = (Button) findViewById(R.id.iscrivitiBtn);
        iscriviti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO controllo su password ed email
                //Consiglio: firebase effettua già dei controlli di base sulla password e sull'email e restituisce un errore.
                signUp(mail.getEditText().getText().toString(), password.getEditText().getText().toString());
            }
        });
    }

    private Timestamp getTodayTimestamp() {
        int day = datePickerDialog.getDatePicker().getDayOfMonth(),
                month = datePickerDialog.getDatePicker().getMonth(),
                year = datePickerDialog.getDatePicker().getYear();
        Calendar cal = new Calendar.Builder().setCalendarType(Calendar.getInstance().getCalendarType()).setFields(Calendar.YEAR, year,
                Calendar.MONTH, month,
                Calendar.DAY_OF_MONTH, day).build();
        Timestamp date = new Timestamp(cal.getTime());
        return date;
    }

    //per data di oggi
    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day, month, year);
    }


    //menu date picker
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                // dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    ///per creare la stringa data preview button
    private String makeDateString(int day, int month, int year) {

        return getMonthFormat(month) + " " + day + " " + year;
    }

    //per attribuire il numero del mese al nome del mese alla preview button
    private String getMonthFormat(int month) {

        if (month == 1)
            return "GENNAIO";
        if (month == 2)
            return "FEBBRAIO";
        if (month == 3)
            return "MARZO";
        if (month == 4)
            return "APRILE";
        if (month == 5)
            return "MAGGIO";
        if (month == 6)
            return "GIUGNO";
        if (month == 7)
            return "LUGLIO";
        if (month == 8)
            return "AGOSTO";
        if (month == 9)
            return "SETTEMBRE";
        if (month == 10)
            return "OTTOBE";
        if (month == 11)
            return "NOVEMBRE";
        if (month == 12)
            return "DICEMBRE";

        return "GENNAIO";

    }


    //start date picker
    public void openDatePicker(View view) {

        datePickerDialog.show();
    }

    //registrazione dell'utente a firebase
    protected void signUp(String email, String password) {

        if ((email != "") && (password !="")) {
            addInfo();
        }
        else {
            // If sign in fails, display a message to the user.
            //Log.w("DEBUG", "createUserWithEmail:failure", task.getException());
            Toast.makeText(IscrivitiActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
            //Aggiornare interfaccia, il sistema non è riuscito a registrare l'utente
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //dopo aver inserito le informazioni di registrazione, i dati vengono salvati sul database
                            addInfo();
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

    private void addInfo() {
        db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        String mail = mAuth.getCurrentUser().getEmail();
        //nomeR = findViewById(R.id.iscrizioneNome);
        //String nome = nomeR.getText().toString();
        user.put("nome", ((TextInputLayout) findViewById(R.id.iscrizioneNome)).getEditText().getText().toString());
        user.put("cognome", ((TextInputLayout) findViewById(R.id.iscrizioneCognome)).getEditText().getText().toString());
        user.put("data_di_nascita", getTodayTimestamp());

        db.collection("utente").document(mail)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //una volta registrato, l'utente deve rieffettuare l'accesso
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseAuth.getInstance().getCurrentUser().delete();
                        Toast.makeText(IscrivitiActivity.this, "Registrazione fallita.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean nomeValido(String nome) {


        // Regex per controllare se il nome è valdio.
        String regex = "^[A-Za-z]{3,29}$";

        // Compila il ReGex
        Pattern p = Pattern.compile(regex);

        // se il nome è vuoto
        // return false
        if (nome == null) {
            return false;
        }

        // Pattern class contiene il metodo matcher()
        //per trovare la corrispondenza tra un dato e il Nome
        Matcher m = p.matcher(nome);

        // Return se il nome corrisponde con la stringa Regex
        return m.matches();
    }
}