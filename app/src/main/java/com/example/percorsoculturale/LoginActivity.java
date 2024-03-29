package com.example.percorsoculturale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private TextInputLayout emailView;
    private TextInputLayout pwdView;
    private final String CONFIGURL = "gs://percorsoculturale.appspot.com/PortableDB";
    private final String JSONFILENAME = "Versione";
    public int loadImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        loadLocale();

        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        boolean isPrimoAvvio = pref.getBoolean("PrimoAvvio", true);
        if(isPrimoAvvio){
            Intent tutorial = new Intent(this, ActivityTutorial.class);
            startActivity(tutorial);
        }
        //impostazioni lingua

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.login_landscape);
        }else{
            setContentView(R.layout.login);
        }



        emailView = (TextInputLayout) findViewById(R.id.editTextTextEmailAddress);
        pwdView = (TextInputLayout) findViewById(R.id.editTextTextPassword);
        if(savedInstanceState != null){
            emailView.getEditText().setText(savedInstanceState.getString("email"));
            pwdView.getEditText().setText(savedInstanceState.getString("password"));
        }
        Button login = (Button) findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Aggiungere controlli sull'input
                String email = emailView.getEditText().getText().toString().trim();
                String password = pwdView.getEditText().getText().toString().trim();
                signIn(email, password);

            }
        });
        Button iscriviti = (Button) findViewById(R.id.iscrivitiButton);
        iscriviti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //passa alla sezione per la registrazione
                Intent intent = new Intent(getApplicationContext(), IscrivitiActivity.class);
                startActivity(intent);
            }
        });
        Button login_ospite = (Button) findViewById(R.id.accediComeOspite);
        login_ospite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //effettua il login come ospite con credenziali predefinite
                signIn("user@guest.com", "userguest");
            }
        });

        TextView changePwd = (TextView) findViewById(R.id.cambiaPassword);
        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Avviare la procedura di cambio password
                Intent i = new Intent(getApplicationContext(), RecuperaPassword.class);
                startActivity(i);
            }
        });

        FloatingActionButton lbtn = (FloatingActionButton) findViewById(R.id.bottoneLingua);
        lbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguage();
            }
        });
        if(savedInstanceState != null) {
            int flags = savedInstanceState.getInt("loadImage");
            FloatingActionButton lingua = findViewById(R.id.bottoneLingua);
            if (flags == 0) {
                lingua.setImageDrawable(getDrawable(R.drawable.england));
            } else if (flags == 1) {
                lingua.setImageDrawable(getDrawable(R.drawable.france));
            } else if (flags == 2) {
                lingua.setImageDrawable(getDrawable(R.drawable.spain));
            } else if (flags == 3) {
                lingua.setImageDrawable(getDrawable(R.drawable.italy));
            }
        }else{
            String language = pref.getString("My_Lang", "");
            FloatingActionButton lingua = findViewById(R.id.bottoneLingua);
            if (language.equals("en")) {
                lingua.setImageDrawable(getDrawable(R.drawable.england));
            } else if (language.equals("fr")) {
                lingua.setImageDrawable(getDrawable(R.drawable.france));
            } else if (language.equals("es")) {
                lingua.setImageDrawable(getDrawable(R.drawable.spain));
            } else{
                lingua.setImageDrawable(getDrawable(R.drawable.italy));
            }
        }

    }



    @Override
    public void onStart() {
        super.onStart();
        // TODO se l'utente è già loggato, andare direttamente alla homepage
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            updateUser(currentUser);
        }
        loadConfiguration();
        //cambiare schermata poiché l'utente ha già effettuato l'accesso
    }

    private void updateUser(FirebaseUser currentUser){
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("utente")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Intent i = new Intent(getApplicationContext(), RicercaPercorsiActivity.class);
                        startActivity(i);
                    }
                });
    }

    protected void signIn(String email, String password){
        if(email.equals("") || password.equals("")) {
            String messaggio = "Non hai inserito la password e/o la email";
            showMessage(messaggio);
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, "Hai eseguito l'accesso",
                                        Toast.LENGTH_SHORT).show();
                                //Aggiornare interfaccia, l'utente ha effettuato l'accesso
                                Intent intent = new Intent(getApplicationContext(), RicercaPercorsiActivity.class);
                                Bundle extra = getIntent().getExtras();
                                if(extra != null){
                                    if(extra.get("intent") != null){
                                        intent = (Intent) extra.get("intent");
                                    }
                                }
                                startActivity(intent);
                            } else {
                                String messaggio = "Credenziali errate";
                                showMessage(messaggio);
                            }
                        }
                    });
        }

    }
    //mostra le opzioni a disposizione
    public void showLanguage(){

        //Array che contiene le lingue previste per l'app
        final String list[] = {"Spanish", "French", "English","Italian"};
        AlertDialog.Builder mBulider = new AlertDialog.Builder(LoginActivity.this);
        mBulider.setTitle("Chose language");
        mBulider.setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                if(i == 0){

                    loadImage=2;
                    setLocale("es");
                    recreate();


                }else if(i == 1){
                    loadImage=1;
                    setLocale("fr");

                    recreate();

                }else if(i == 2){
                    loadImage=0;
                    setLocale("en");

                    recreate();

                }else if(i == 3){
                    loadImage=3;
                    setLocale("");

                    recreate();

                }

                dialog.dismiss();

            }
        });

        AlertDialog mDialog = mBulider.create();
        mDialog.show();
    }

    private void loadConfiguration(){
        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("My_Lang", "");
        JSONParser parser = new JSONParser();
        boolean fileExists = false;
        for(File fileLocale : getApplicationContext().getFilesDir().listFiles()){
            if(fileLocale.getName().contains(JSONFILENAME)){
                fileExists = true;
                storage = FirebaseStorage.getInstance();
                StorageReference reference = storage.getReferenceFromUrl(CONFIGURL);
                reference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ListResult> task) {
                        ListResult res = task.getResult();
                        for(StorageReference fileOnline : res.getItems()){
                            if(fileOnline.getName().contains(JSONFILENAME)){
                                if(parser.getFileLanguage(fileOnline.getName()).equals(language))
                                    if(parser.getFileVersion(fileLocale.getName()).compareTo(parser.getFileVersion(fileOnline.getName())) < 0 || !parser.getFileLanguage(fileLocale.getName()).equals(language)){   //se la versione del file salvato è inferiore sarà ritornato un numero negativo
                                        final long ONE_MEGABYTE = 1024 * 1024;
                                        fileOnline.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                String filename = fileOnline.getName();
                                                try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
                                                    fos.write(bytes);
                                                    fos.flush();
                                                    fileLocale.delete();
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                            }
                        }
                    }
                });


            }
        }
        if(!fileExists){
            storage = FirebaseStorage.getInstance();
            StorageReference reference = storage.getReferenceFromUrl(CONFIGURL);
            reference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                @Override
                public void onComplete(@NonNull Task<ListResult> task) {
                    ListResult res = task.getResult();
                    for(StorageReference fileOnline : res.getItems()){
                        if(fileOnline.getName().contains(JSONFILENAME) && parser.getFileLanguage(fileOnline.getName()).equals(language)){
                            final long ONE_GIGABYTE = 1024 * 1024 * 1024;
                            fileOnline.getBytes(ONE_GIGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    String filename = fileOnline.getName();
                                    try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
                                        fos.write(bytes);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    public void setLocale(String lang) {
        //oggetto che specifica la lingua di riferimento in base al contesto scelto
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale= locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
        loadConfiguration();
    }

    private void loadLocale() {
        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("My_Lang", "");
        //questa condizione serve se non si imposta alcuna lingua all'interno dell'app
        //e quindi viene utilizzata quella selezionata nelle impostazioni del dispositivo
        if (language != "") {
            setLocale(language);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("email", emailView.getEditText().getText().toString());
        savedInstanceState.putString("password", pwdView.getEditText().getText().toString());
        savedInstanceState.putInt("loadImage", loadImage);
    }

    private void showMessage(String messaggio) {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(messaggio)
                    .setTitle("Login");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.show();
        }
}