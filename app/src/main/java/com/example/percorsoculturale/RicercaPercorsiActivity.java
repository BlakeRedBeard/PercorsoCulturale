package com.example.percorsoculturale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class RicercaPercorsiActivity extends AppCompatActivity {

    private ListView listView;
    private android.widget.SearchView searchView;
    private ArrayList<String> id_percorsi;
    private ArrayList<String> nomi_percorsi;
    private ArrayAdapter<String> arrayAdapter;
    private FirebaseFirestore db;
    private LinearLayout mBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private BottomSheetBehavior BottomSheetBehavior;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_percorsi);
        db = FirebaseFirestore.getInstance();

        listView = findViewById(R.id.listview);
        searchView = findViewById(R.id.searchView);
        //imposta la casella di ricerca fissa
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                showPercorsi(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        id_percorsi = new ArrayList<String>();
        nomi_percorsi = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nomi_percorsi);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MostraPercorsiActivity.class);
                intent.putExtra("percorso", id_percorsi.get(i));
                startActivity(intent);
            }
        });

        //Implementazione App bar
        Toolbar toolbar=findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);
        //implementazione bottom sheet

        mBottomSheet=findViewById(R.id.bottom_sheet);
        BottomSheetBehavior=BottomSheetBehavior.from(mBottomSheet);
        BottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        //TODO: AL CLICK DELL'INPUT TEXT DI SEARCH VIEW IMPOSTARE BottomSheetBehavior.STATE_HIDDEN

        LinearLayout BProfile=findViewById(R.id.viewBottomSheet).findViewById(R.id.profilo);
        BProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent IBProfile=new Intent(RicercaPercorsiActivity.this,ProfiloActivity.class);
                startActivity(IBProfile);
            }
        });

    }



    //Implementazione App bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item){
        mBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);

        switch (item.getItemId()) {

            case R.id.menuIcon:
                Toast.makeText(this, "Hai cliccato il menu", Toast.LENGTH_SHORT).show();


                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public void showPercorsi(String search){
        db.collection("percorso")
                .whereEqualTo("comune", search)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            id_percorsi.clear();
                            nomi_percorsi.clear();
                            //istanze del database ottenute con successo
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DEBUG", document.getId() + " => " + document.getData());
                                id_percorsi.add(document.getId());
                                for(Map.Entry<String, Object> entry : document.getData().entrySet()){

                                    if(entry.getKey().equals("nome"))
                                        nomi_percorsi.add((String) entry.getValue());
                                }
                            }
                            arrayAdapter.notifyDataSetChanged();
                        } else {
                            Log.w("DEBUG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


}
