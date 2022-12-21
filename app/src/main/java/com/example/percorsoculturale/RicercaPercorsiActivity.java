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
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.percorsoculturale.tables.Percorso;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

public class RicercaPercorsiActivity extends AppCompatActivity {

    private ListView listView;
    private android.widget.SearchView searchView;
    private ArrayList<String> id_percorsi;
    private ArrayList<String> id_attrazioni;
    private ArrayList<String> nomi_percorsi;
    private ArrayList<String> nomi_attrazioni;
    private ArrayAdapter<String> arrayAdapter;
    private FirebaseFirestore db;
    private LinearLayout mBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private BottomSheetBehavior BottomSheetBehavior;
    private FirebaseAuth firebaseAuth;



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

                try {
                    showJSON(s);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
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
                String messaggio = "La ricerca avviene per comune e attrazione";
                showMessage(messaggio);
            }
        });
        //TODO: AL CLICK DELL'INPUT TEXT DI SEARCH VIEW IMPOSTARE BottomSheetBehavior.STATE_HIDDEN

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String mailUtente = firebaseUser.getEmail();

        LinearLayout BProfile=findViewById(R.id.viewBottomSheet).findViewById(R.id.profilo);

        if (mailUtente.equals("user@guest.com")) {
            BProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String messaggio = "Non puoi accedere al profilo in quanto non hai effettuato l'accesso";
                    showMessage(messaggio);
                }
            });


        }
        else {
            BProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent IBProfile=new Intent(RicercaPercorsiActivity.this,ProfiloActivity.class);
                    startActivity(IBProfile);
                }
            });
        }

        LinearLayout BDisconnettiti=findViewById(R.id.viewBottomSheet).findViewById(R.id.disconnettiti);

        if (mailUtente.equals("user@guest.com")) {
            BDisconnettiti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String messaggio = "Non puoi disconnetterti in quanto non hai effettuato l'accesso";
                    showMessage(messaggio);
                }
            });


        }
        else {
            BDisconnettiti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String messaggio = "Hai effettuato la disconnessione";
                    showMessage(messaggio);
                    FirebaseAuth.getInstance().signOut();
                    Intent home=new Intent(RicercaPercorsiActivity.this,LoginActivity.class);
                    startActivity(home);
                }
            });
        }

    }

    private void showMessage(String messaggio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RicercaPercorsiActivity.this);

        builder.setMessage(messaggio)
                .setTitle("Info");

        AlertDialog dialog = builder.create();
        dialog.show();
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

    private void showJSON(String filter) throws FileNotFoundException{
        JSONParser parser = null;
        for(File fileLocale : getApplicationContext().getFilesDir().listFiles()) {
            if (fileLocale.getName().contains("Versione")) {
                parser = new JSONParser(fileLocale);
            }
        }
        if(parser != null) {
            id_percorsi.clear();
            nomi_percorsi.clear();
            for (Percorso percorso : parser.getFilteredPercorsi(filter)) {
                Log.i("DEBUG: contenuto Json filtrato ("+filter+")", percorso.toString());
                id_percorsi.add(percorso.getId());
                nomi_percorsi.add(percorso.getNome());
            }
            arrayAdapter.notifyDataSetChanged();
        }else{
            throw new FileNotFoundException();
        }
    }

}
