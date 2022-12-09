package com.example.percorsoculturale;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ValutazionePercorsoActivity extends AppCompatActivity {
    private TextView textview1;
    private TextView textview2;
    private Button btnContinua;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef;
    private DocumentReference noteRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.valutazione_percorso);

        textview1 = (TextView) findViewById(R.id.textView4);
        textview2 = (TextView) findViewById(R.id.textView6);

        String CHANNEL_ID = "My Notification";


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                //.setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("textTitle")
                .setContentText("textContent")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "This is my first Notification", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(notificationChannel);
        //notificationManager.notify(0,builderNotificationCompat.build());


        //Bundle dati = getIntent().getExtras();
        //int puntiAttivita = dati.getInt("PunteggioValore");
        //String stringaPunti = dati.getString("Punteggio");

        //legge da db
        //TODO cambiare nome utente con utente generico
        noteRef = db.collection("utente").document("blake99@live.it");
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                textview1.setText(documentSnapshot.getString("nome"));

            }
        });



        int puntiTotali = QrcodeActivity.getPunti();

        textview2.setText(Integer.toString(puntiTotali));


        //aggiorna punti utente
        noteRef
                .update("punti", FieldValue.increment(puntiTotali))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);
                    }
                });

        QrcodeActivity.resettaPunti();
        Button condividi = (Button) findViewById(R.id.button);


        condividi.setOnClickListener(new View.OnClickListener () {

            @Override
            public void onClick(View v){
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String nomePercorso = "a";
                String body = "Ho ottenuto " + textview2.getText().toString() + " punti completando il percorso " + nomePercorso;
                String sub = "Valutazione percorso";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
                myIntent.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));

            }
        });







        Button btnContinua = (Button)findViewById(R.id.continuaButton);
        btnContinua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ValutazionePercorsoActivity.this, BadgeActivity.class));
            }

        });

    }
}

