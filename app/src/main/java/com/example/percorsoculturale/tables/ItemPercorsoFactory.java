package com.example.percorsoculturale.tables;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.percorsoculturale.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ItemPercorsoFactory extends View {

    private Context context;
    private LayoutInflater inflater;

    public ItemPercorsoFactory(Context context){
        super(context);
        this.context = context;

    }



    public RelativeLayout generateLayout(Bitmap immagine, String nome){
        RelativeLayout bottone = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        bottone = (RelativeLayout) inflater.inflate(R.layout.item_percorso, null);
        ((ImageView) bottone.findViewById(R.id.item_percorso_image)).setImageBitmap(immagine);
        ((TextView) bottone.findViewById(R.id.item_percorso_name)).setText(nome);
        return bottone;
    }

    public RelativeLayout generateLayout(String idImmagine, String nome, TableRow row){
        RelativeLayout bottone = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        bottone = (RelativeLayout) inflater.inflate(R.layout.item_percorso, row, false);
        StorageReference storage = FirebaseStorage.getInstance().getReferenceFromUrl(idImmagine);
        int ONE_MEGABYTE = 1024*1024;
        ImageView immagine = bottone.findViewById(R.id.item_percorso_image);
        storage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bpm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                immagine.setImageBitmap(bpm);
            }
        });
        ((TextView) bottone.findViewById(R.id.item_percorso_name)).setText(nome);
        return bottone;
    }


}
