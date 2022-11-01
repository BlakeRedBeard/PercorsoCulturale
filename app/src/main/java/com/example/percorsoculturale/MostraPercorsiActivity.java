package com.example.percorsoculturale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class MostraPercorsiActivity extends AppCompatActivity {

    private TextView nomePercorso,
                     descrizionePercorso,
                     regionePercorso,
                     comunePercorso;
    private ImageView immaginePercorso;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    Button btnTakePicture, btnScanBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        setContentView(R.layout.activity_mostra_percorso);
        nomePercorso = (TextView) findViewById(R.id.nomePercorso);
        descrizionePercorso = (TextView) findViewById(R.id.descrizionePercorso);
        regionePercorso = (TextView) findViewById(R.id.regionePercorso);
        comunePercorso = (TextView) findViewById(R.id.comunePercorso);
        immaginePercorso = (ImageView) findViewById(R.id.immaginePercorso);

        if(savedInstanceState == null){
            Bundle extra = getIntent().getExtras();
            if(extra != null){
                showPercorso(extra.getString("percorso"));
            }else {
                showPercorso("Bari");
            }
        }else {
            showPercorso((String) savedInstanceState.getSerializable("percorso"));
        }

        Button avvia = (Button) findViewById(R.id.avviaButton);

        avvia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //passa alla sezione fotocamera (qrCode)
                Intent intent = new Intent(getApplicationContext(), QrcodeActivity.class);
                startActivity(intent);
            }
        });

    }


    public void showPercorso(String search){
        db.collection("percorso")
                .document(search)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("DEBUG", document.getId() + " => " + document.getData());
                        for(Map.Entry<String, Object> entry : document.getData().entrySet()){
                            if(entry.getKey().equals("nome")){
                                nomePercorso.setText((String) entry.getValue());
                            }else if(entry.getKey().equals("descrizione")){
                                descrizionePercorso.setText((String) entry.getValue());
                            }else if(entry.getKey().equals("regione")){
                                regionePercorso.setText((String) entry.getValue());
                            }else if(entry.getKey().equals("comune")){
                                comunePercorso.setText((String) entry.getValue());
                            }else if(entry.getKey().equals("immagine")){
                                StorageReference gsReference = storage.getReferenceFromUrl((String) entry.getValue());
                                final long ONE_MEGABYTE = 1024 * 1024;
                                gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // image retrieved
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        immaginePercorso.setImageBitmap(bmp);
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