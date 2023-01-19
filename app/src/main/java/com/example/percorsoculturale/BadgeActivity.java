package com.example.percorsoculturale;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BadgeActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    TextView nienteBadge;
    private Animation anim=null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.badge_landscape);
        }else{
            setContentView(R.layout.badge);
        }

        TextView textview1 = (TextView) findViewById(R.id.textView8);

        ImageView badge1 = (ImageView) findViewById(R.id.imageView2);
        ImageView badge2 = (ImageView) findViewById(R.id.imageView3);
        ImageView badge3 = (ImageView) findViewById(R.id.imageView4);
        ImageView badge4 = (ImageView) findViewById(R.id.imageView5);

        anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.animazione);

        badge1.startAnimation(anim);
        badge2.startAnimation(anim);
        badge3.startAnimation(anim);
        badge4.startAnimation(anim);

        mostraBadgeSbloccati(badge1, badge2, badge3, badge4);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        Button badgeButton = (Button) findViewById(R.id.badgeButton);
        badgeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BadgeActivity.this, ListaBadge.class));
            }

        });

        //prendo la mail dell'utente loggato
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String mailUtente = firebaseUser.getEmail();

        noteRef = db.collection("utente").document(mailUtente);
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                textview1.setText(documentSnapshot.getString("nome"));

            }
        });

        nienteBadge = (TextView) findViewById(R.id.textView17);
        nienteBadge.setVisibility(View.INVISIBLE);



    }

    public void mostraBadgeSbloccati(ImageView badge1, ImageView badge2, ImageView badge3, ImageView badge4) {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String mailUtente = firebaseUser.getEmail();

        String percorsoCollezione = "utente";

        DocumentReference docRef = db.collection(percorsoCollezione).document(mailUtente);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());



                        int punti=0;
                        if(document.getLong("punti")!=null) {
                            punti = document.getLong("punti").intValue();
                        }

                        int valore = 0;
                        if (punti >= 50 ){
                            valore = 4;
                            controllaBadge(mailUtente, valore);
                            mostraBadge("5punti",badge1);
                            mostraBadge("10punti",badge2);
                            mostraBadge("25punti",badge3);
                            mostraBadge("50punti",badge4);
                        }
                        else {
                            if (punti >= 25 ){
                                valore = 3;
                                controllaBadge(mailUtente, valore);
                                mostraBadge("5punti",badge1);
                                mostraBadge("10punti",badge2);
                                mostraBadge("25punti",badge3);
                            }
                            else {
                                if (punti >= 10 ){
                                    valore = 2;
                                    controllaBadge(mailUtente, valore);
                                    mostraBadge("5punti",badge1);
                                    mostraBadge("10punti",badge2);
                                }
                                else {
                                    if (punti >= 5 ){
                                        valore = 1;
                                        controllaBadge(mailUtente, valore);
                                        mostraBadge("5punti",badge1);
                                    }
                                    else {
                                        nienteBadge.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



    }


    public void aggiungiBadgeCinquePunti(String mailUtente) {
        Map<String,Object> utenti = new HashMap<>();
        utenti.put("nome", "5punti");
        utenti.put("punti", 5);
        String uniqueID = UUID.randomUUID().toString();

        String percorsoCollezione = "utente/"+mailUtente+"/badge_ottenuti";

        //inserisco il badge percorsi
        db.collection(percorsoCollezione).document(uniqueID)
                .set(utenti)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void aggiungiBadgeDieciPunti(String mailUtente) {
        Map<String,Object> utenti = new HashMap<>();
        utenti.put("nome", "10punti");
        utenti.put("punti", 10);
        String uniqueID = UUID.randomUUID().toString();

        String percorsoCollezione = "utente/"+mailUtente+"/badge_ottenuti";

        db.collection(percorsoCollezione).document(uniqueID)
                .set(utenti)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
    public void aggiungiBadgeVenticinquePunti(String mailUtente) {
        Map<String,Object> utenti = new HashMap<>();
        utenti.put("nome", "25punti");
        utenti.put("punti", 25);
        String uniqueID = UUID.randomUUID().toString();

        String percorsoCollezione = "utente/"+mailUtente+"/badge_ottenuti";

        db.collection(percorsoCollezione).document(uniqueID)
                .set(utenti)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void aggiungiBadgeCinquantaPunti(String mailUtente) {
        Map<String,Object> utenti = new HashMap<>();
        utenti.put("nome", "50punti");
        utenti.put("punti", 50);
        String uniqueID = UUID.randomUUID().toString();

        String percorsoCollezione = "utente/"+mailUtente+"/badge_ottenuti";

        db.collection(percorsoCollezione).document(uniqueID)
                .set(utenti)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void controllaBadge(String mailUtente, int valore) {
        String percorsoCollezione = "utente/"+mailUtente+"/badge_ottenuti";

        db.collection(percorsoCollezione)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int cont1 = 0;
                            int cont2 = 0;
                            int cont3 = 0;
                            int cont4 = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String punti = document.getString("nome");

                                System.out.println("doc"+punti);

                                if(punti.equals("5punti")) {
                                    cont1 = cont1 + 1;
                                }
                                if(punti.equals("10punti")) {
                                    cont2 = cont2 + 1;
                                }
                                if(punti.equals("25punti")) {
                                    cont3 = cont3 + 1;
                                }
                                if(punti.equals("50punti")) {
                                    cont4 = cont4 + 1;
                                }
                            }
                            if (cont1 == 0 && valore == 1) {
                                aggiungiBadgeCinquePunti(mailUtente);
                            }
                            else if (cont2 == 0 && valore == 2) {
                                aggiungiBadgeDieciPunti(mailUtente);
                            }
                            else if (cont3 == 0 && valore == 3) {
                                aggiungiBadgeVenticinquePunti(mailUtente);
                            }
                            else if (cont4 == 0 && valore == 4) {
                                aggiungiBadgeCinquantaPunti(mailUtente);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void mostraBadge(String search,ImageView badge){
        db.collection("badge")
                .document(search)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("DEBUG", document.getId() + " => " + document.getData());
                        for(Map.Entry<String, Object> entry : document.getData().entrySet()){
                            if(entry.getKey().equals("immagine")){
                                StorageReference gsReference = storage.getReferenceFromUrl((String) entry.getValue());
                                final long ONE_MEGABYTE = 102 * 102;
                                gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // image retrieved
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        badge.setImageBitmap(bmp);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                        Log.w("DEBUG", "Error downloading image", task.getException());
                                    }
                                });
                            }
                        }
                    }
                });
    }


}