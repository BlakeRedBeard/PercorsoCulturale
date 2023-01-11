package com.example.percorsoculturale;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.percorsoculturale.tables.ItemPercorsoFactory;
import com.example.percorsoculturale.tables.Percorso;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RicercaPercorsiActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private android.widget.SearchView searchView;
    private FirebaseFirestore db;
    private LinearLayout mBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private BottomSheetBehavior BottomSheetBehavior;
    private FirebaseAuth firebaseAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_percorsi);
        db = FirebaseFirestore.getInstance();

        tableLayout = findViewById(R.id.lista_percorsi);

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
                try {
                    showJSON(s);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        //Implementazione App bar
        Toolbar toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);
        //implementazione bottom sheet

        mBottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
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
        String email = firebaseUser.getEmail();


        LinearLayout BProfile = findViewById(R.id.viewBottomSheet).findViewById(R.id.profilo);

        if (email.equals("user@guest.com")) {
            BProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String messaggio = "Non puoi accedere al profilo in quanto non hai effettuato l'accesso";
                    showMessage(messaggio);
                }
            });


        } else {
            BProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent IBProfile = new Intent(RicercaPercorsiActivity.this, ProfiloActivity.class);
                    startActivity(IBProfile);
                }
            });
        }

        LinearLayout BDisconnettiti = findViewById(R.id.viewBottomSheet).findViewById(R.id.disconnettiti);

        BDisconnettiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String messaggio = "Hai effettuato la disconnessione";
                showMessage(messaggio);
                FirebaseAuth.getInstance().signOut();
                Intent home = new Intent(RicercaPercorsiActivity.this, LoginActivity.class);
                startActivity(home);
            }
        });

        //GPS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

    }


    private void getLocation() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                geocoder = new Geocoder(RicercaPercorsiActivity.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    //TODO Passare la regione a MostraPercorsi
                                    String regione = addresses.get(0).getAdminArea();
                                    showJSON(regione);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                new AlertDialog.Builder(RicercaPercorsiActivity.this)
                                        .setTitle("Attivare posizione")
                                        .setMessage("Per utilizzare le funzionalità del gps bisogna attivarlo").create().show();
                            }
                        }
                    });


        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permesso posizione")
                    .setMessage("Accettando il permesso cercheremo i percorsi vicini alla tua posizione, non è obbligatoria ma facilita l'utilizzo dell'app")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(RicercaPercorsiActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("Non accetto", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create().show();
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }


    }

    //gps
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permesso negato", Toast.LENGTH_SHORT).show();
            }
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

        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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

            case R.id.searchView:

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.searchView);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                item.setVisible(false);
                searchView.setVisibility(View.VISIBLE);
                return false;
            }
        });

        return true;
    }


    private void showJSON(String filter) throws FileNotFoundException {
        JSONParser parser = null;
        for (File fileLocale : getApplicationContext().getFilesDir().listFiles()) {
            if (fileLocale.getName().contains("Versione")) {
                parser = new JSONParser(fileLocale);
            }
        }
        if (parser != null) {
            tableLayout.removeAllViews();
            int columns = 0;
            ItemPercorsoFactory factory = new ItemPercorsoFactory(this);
            TableRow row = new TableRow(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.width = TableRow.LayoutParams.MATCH_PARENT;
            params.height = TableRow.LayoutParams.WRAP_CONTENT;
            for (Percorso percorso : parser.getFilteredPercorsi(filter)) {
                RelativeLayout item = factory.generateLayout(percorso.getImmagine(), percorso.getNome(), row);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MostraPercorsiActivity.class);
                        intent.putExtra("percorso", percorso.getId());
                        startActivity(intent);
                    }
                });
                row.setLayoutParams(params);
                row.addView(item);
                tableLayout.addView(row);
                row = new TableRow(this);
                /*
                if(columns<=0) {
                    columns = 1;
                    row = new TableRow(this);
                    row.addView(item);
                }else if(columns == 1){
                    columns = 0;
                    row.addView(item);
                    tableLayout.addView(row);
                }
                */
            }
            /*
            if(columns == 1){
                tableLayout.addView(row);
            }*/

        } else {
            throw new FileNotFoundException();
        }
    }

}
