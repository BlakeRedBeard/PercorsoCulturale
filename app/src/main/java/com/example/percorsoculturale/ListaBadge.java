package com.example.percorsoculturale;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class ListaBadge extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private Animation anim=null;
    private ArrayList<Bitmap> bmp;
    private ArrayList<String> nomi;
    private int i = 0;
    ImageView cinquePunti;
    ImageView dieciPunti;
    ImageView venticinquePunti;
    ImageView cinquantaPunti;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();

        bmp = new ArrayList<>(3);
        nomi = new ArrayList<>(3);

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.lista_badge_landscape);
        }else{
            setContentView(R.layout.lista_badge);
        }

        cinquePunti = (ImageView) findViewById(R.id.imageView10);
        dieciPunti = (ImageView) findViewById(R.id.imageView15);
        venticinquePunti = (ImageView) findViewById(R.id.imageView16);
        cinquantaPunti = (ImageView) findViewById(R.id.imageView11);

        TextView descrizioneB1 = (TextView) findViewById(R.id.textView10);
        TextView descrizioneB2 = (TextView) findViewById(R.id.textView11);
        TextView descrizioneB3 = (TextView) findViewById(R.id.textView12);
        TextView descrizioneB4 = (TextView) findViewById(R.id.textView13);

        anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.animazione);

        cinquePunti.setBackgroundResource(android.R.drawable.ic_menu_gallery);
        dieciPunti.setBackgroundResource(android.R.drawable.ic_menu_gallery);
        venticinquePunti.setBackgroundResource(android.R.drawable.ic_menu_gallery);
        cinquantaPunti.setBackgroundResource(android.R.drawable.ic_menu_gallery);

        cinquePunti.startAnimation(anim);
        dieciPunti.startAnimation(anim);
        venticinquePunti.startAnimation(anim);
        cinquantaPunti.startAnimation(anim);

        mostraTuttiBadge(cinquePunti, dieciPunti, venticinquePunti, cinquantaPunti, descrizioneB1, descrizioneB2, descrizioneB3, descrizioneB4);


        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    public void mostraTuttiBadge(ImageView badge1, ImageView badge2, ImageView badge3, ImageView badge4, TextView descrizioneB1, TextView descrizioneB2, TextView descrizioneB3, TextView descrizioneB4) {

        //prendo la mail dell'utente loggato

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String mailUtente = firebaseUser.getEmail();


        String percorsoCollezione = "utente";

        //verifico se l'utente ha sbloccato il badge da 5 punti
        DocumentReference docRef = db.collection(percorsoCollezione).document(mailUtente);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());


                        int puntiVeri=0;
                        if(document.getLong("punti")!=null) {
                            puntiVeri = document.getLong("punti").intValue();
                        }

                        if (puntiVeri >= 50 ){
                            salvaBadge("5punti",descrizioneB1);
                            salvaBadge("10punti",descrizioneB2);
                            salvaBadge("25punti",descrizioneB3);
                            salvaBadge("50punti",descrizioneB4);
                        }
                        else {
                            salvaBadge("50puntigrigio",descrizioneB4);
                            if (puntiVeri >= 25 ){
                                salvaBadge("5punti",descrizioneB1);
                                salvaBadge("10punti",descrizioneB2);
                                salvaBadge("25punti",descrizioneB3);
                            }
                            else {
                                salvaBadge("25puntigrigio",descrizioneB3);
                                if (puntiVeri >= 10 ){
                                    salvaBadge("5punti",descrizioneB1);
                                    salvaBadge("10punti",descrizioneB2);
                                }
                                else {
                                    salvaBadge("10puntigrigio",descrizioneB2);
                                    if (puntiVeri >= 5 ){
                                        salvaBadge("5punti",descrizioneB1);
                                    }
                                    else {
                                        salvaBadge("5puntigrigio",descrizioneB1);
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

    public void salvaBadge(String search,TextView descrizione){
        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("My_Lang", "");
        db.collection("badge")
                .document(search)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("DEBUG", document.getId() + " => " + document.getData());
                        for(Map.Entry<String, Object> entry : document.getData().entrySet()){
                            if(entry.getKey().equals("descrizione"+language)){
                                descrizione.setText((String) entry.getValue());
                            }
                            else if(entry.getKey().equals("immagine")){
                                StorageReference gsReference = storage.getReferenceFromUrl((String) entry.getValue());
                                final long ONE_MEGABYTE = 1024 * 1024;
                                gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // image retrieved
                                        Bitmap bmp2 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        bmp.add(i,bmp2);
                                        nomi.add(search);
                                        i = i + 1;
                                        if(bmp.size() == 4) {
                                            mostraBadge();
                                        }
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

    public void mostraBadge() {
        int j;
        for(j=0; j<nomi.size(); j++) {
            if((nomi.get(j).equals("5punti")) || (nomi.get(j).equals("5puntigrigio"))) {
                cinquePunti.clearAnimation();
                cinquePunti.setBackgroundColor(Color.rgb(249,249,251));
                cinquePunti.setImageBitmap(bmp.get(j));
            }

            if((nomi.get(j).equals("10punti")) || (nomi.get(j).equals("10puntigrigio"))) {
                dieciPunti.clearAnimation();
                dieciPunti.setBackgroundColor(Color.rgb(249,249,251));
                dieciPunti.setImageBitmap(bmp.get(j));
            }

            if((nomi.get(j).equals("25punti")) || (nomi.get(j).equals("25puntigrigio"))) {
                venticinquePunti.clearAnimation();
                venticinquePunti.setBackgroundColor(Color.rgb(249,249,251));
                venticinquePunti.setImageBitmap(bmp.get(j));
            }

            if((nomi.get(j).equals("50punti")) || (nomi.get(j).equals("50puntigrigio"))) {
                cinquantaPunti.clearAnimation();
                cinquantaPunti.setBackgroundColor(Color.rgb(249,249,251));
                cinquantaPunti.setImageBitmap(bmp.get(j));
            }
        }

    }
}

