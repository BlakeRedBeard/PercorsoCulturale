package com.example.percorsoculturale;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class IscrivitiActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button mDatePickerBtn;
    //Date picker
    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mAuth = FirebaseAuth.getInstance();
        //setContentView(R.layout.iscriviti); //classe R serve per istanziare risorse della cartella res

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.iscriviti_landscape);
        } else{
            setContentView(R.layout.iscriviti);
        }
        if(savedInstanceState != null){
            ((TextInputLayout) findViewById(R.id.iscrizioneCognome)).getEditText().setText(savedInstanceState.getString("cognome"));
            ((TextInputLayout) findViewById(R.id.iscrizioneNome)).getEditText().setText(savedInstanceState.getString("nome"));
            ((TextInputLayout) findViewById(R.id.iscrizioneEmail)).getEditText().setText(savedInstanceState.getString("email"));
            ((TextInputEditText) findViewById(R.id.iscrivitiPassword)).setText(savedInstanceState.getString("password"));

        }
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
                finish();
            }

        });


        Button iscriviti = (Button) findViewById(R.id.iscrivitiBtn);
        iscriviti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidSub()) {
                    String mail = ((TextInputEditText) findViewById(R.id.iscrivitiEmail)).getText().toString().trim().toLowerCase();
                    String password = ((TextInputEditText) findViewById(R.id.iscrivitiPassword)).getText().toString();
                    signUp(mail, password);
                }
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
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;


        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        cal.set(2010,1,1);
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());

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
                            Toast.makeText(IscrivitiActivity.this, "Registration failed",
                                    Toast.LENGTH_SHORT).show();
                            //Aggiornare interfaccia, il sistema non è riuscito a registrare l'utente
                        }
                    }
                });
    }

    private boolean isValidSub(){
        boolean result = false;
        String cognome = ((TextInputEditText) findViewById(R.id.iscrivitiCognome)).getText().toString();
        String nome = ((TextInputEditText) findViewById(R.id.iscrivitiNome)).getText().toString();
        String email = ((TextInputEditText) findViewById(R.id.iscrivitiEmail)).getText().toString().trim().toLowerCase();
        String password = ((TextInputEditText) findViewById(R.id.iscrivitiPassword)).getText().toString();
        String confermaPassword = ((TextInputEditText) findViewById(R.id.iscrivitiConfermaPassword)).getText().toString();

        final String regPwd = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$";
        final String regMail = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        final String regName = "[^0-9\\.\\,\\\"\\?\\!\\;\\:\\#\\$\\%\\&\\(\\)\\*\\+\\-\\/\\<\\>\\=\\@\\[\\]\\\\\\^\\_\\{\\}\\|\\~]+";

        if(cognome != "" && cognome.matches(regName)) {

            if (nome != "" && nome.matches(regName)) {

                if (email != "" && email.matches(regMail)) {
                    if (password != "" && password.matches(regPwd)) {
                        if(confermaPassword != "" && password.equals(confermaPassword)) {
                            result = true;
                        }
                        else if(confermaPassword.equals("")){
                            String messaggio = "Ripeti password non può essere vuota";
                            showMessage(messaggio);
                        }
                        else if(!password.equals(confermaPassword)){
                            String messaggio = "Le 2 password non coincidono";
                            showMessage(messaggio);
                        }

                    }
                    else if(password.equals("")){
                        String messaggio = "La password non può essere vuota";
                        showMessage(messaggio);
                    }
                    else if(!password.matches(regPwd)){
                        String messaggio = "La password deve essere lunga almeno 8 caratteri, contenere almeno una lettera maiuscola e almeno un numero";
                        showMessage(messaggio);
                    }

                }
                else if(email.equals("")){
                    String messaggio = "La mail non può essere vuota";
                    showMessage(messaggio);
                }
                else if(!email.matches(regMail)){
                    String messaggio = "La mail non contiene un dominio valido";
                    showMessage(messaggio);
                }

            }
            else if(nome.equals("")){
                String messaggio = "Il nome non può essere vuoto";
                showMessage(messaggio);
            }
            else if(!nome.matches(regName)){
                String messaggio = "Il nome non può contenere caratteri speciali e/o numeri";
                showMessage(messaggio);
            }

        }
        else if(cognome.equals("")){
            String messaggio = "Il cognome non può essere vuoto";
            showMessage(messaggio);
        }
        else if(!cognome.matches(regName)){
            String messaggio = "Il cognome non può contenere caratteri speciali e/o numeri";
            showMessage(messaggio);
        }



        return result;
    }

    private void addInfo() {
        db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        String mail = mAuth.getCurrentUser().getEmail();

        user.put("nome", ((TextInputLayout) findViewById(R.id.iscrizioneNome)).getEditText().getText().toString());
        user.put("cognome", ((TextInputLayout) findViewById(R.id.iscrizioneCognome)).getEditText().getText().toString());
        user.put("data_di_nascita", getTodayTimestamp());
        user.put("email", ((TextInputLayout) findViewById(R.id.iscrizioneEmail)).getEditText().getText().toString().trim().toLowerCase());
        user.put("punti", 0);


        db.collection("utente").document(mail)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //una volta registrato, l'utente deve rieffettuare l'accesso
                        finish();
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        String  cognome = ((TextInputLayout) findViewById(R.id.iscrizioneCognome)).getEditText().getText().toString(),
                nome = ((TextInputLayout) findViewById(R.id.iscrizioneNome)).getEditText().getText().toString(),
                email = ((TextInputLayout) findViewById(R.id.iscrizioneEmail)).getEditText().getText().toString(),
                password = ((TextInputEditText) findViewById(R.id.iscrivitiPassword)).getText().toString();

        savedInstanceState.putString("cognome", cognome != null ? cognome : "");
        savedInstanceState.putString("nome", nome  != null ? nome : "");
        savedInstanceState.putString("email", email != null ? email : "");
        savedInstanceState.putString("password", password != null ? password : "");

    }
    private void showMessage(String messaggio) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(IscrivitiActivity.this);
        builder.setMessage(messaggio)
                .setTitle("Dati errati");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

}