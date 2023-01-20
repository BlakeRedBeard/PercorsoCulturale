package com.example.percorsoculturale;

import static com.example.percorsoculturale.PuzzleActivity.storage;
import static com.google.android.material.internal.ContextUtils.getActivity;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.percorsoculturale.tables.ItemPercorsoFactory;
import com.example.percorsoculturale.tables.Percorso;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RicercaPercorsiActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private SearchView searchView;
    private FirebaseFirestore db;
    private LinearLayout mBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private BottomSheetBehavior BottomSheetBehavior;
    private FirebaseAuth firebaseAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;
    private final String JSONFILENAME = "Versione";
    private final String CONFIGURL = "gs://percorsoculturale.appspot.com/PortableDB";
    public int loadImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_percorsi);
        db = FirebaseFirestore.getInstance();

        tableLayout = findViewById(R.id.lista_percorsi);

        mBottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        BottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        searchView = findViewById(R.id.searchView);
        loadLocale();
        //imposta la casella di ricerca fissa
        searchView.setIconifiedByDefault(false);
        SearchView.SearchAutoComplete e = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        e.setBackgroundColor(getColor(R.color.color_primary_allodole));
        e.setTextColor(Color.WHITE);
        e.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
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

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });

        //Implementazione App bar
        Toolbar toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);
        //implementazione bottom sheet



        //TODO: AL CLICK DELL'INPUT TEXT DI SEARCH VIEW IMPOSTARE BottomSheetBehavior.STATE_HIDDEN

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String email = firebaseUser.getEmail();

        LinearLayout bLingua = findViewById(R.id.lingua);
        FloatingActionButton lbtn = findViewById(R.id.btnLingua);

        View.OnClickListener onClickLanguage = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguage();
            }
        };
        lbtn.setOnClickListener(onClickLanguage);
        bLingua.setOnClickListener(onClickLanguage);
        if(savedInstanceState != null) {
            int flags = savedInstanceState.getInt("loadImage");
            FloatingActionButton lingua = findViewById(R.id.btnLingua);
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
            SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            String language = pref.getString("My_Lang", "");
            FloatingActionButton lingua = findViewById(R.id.btnLingua);
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

        LinearLayout BProfile = findViewById(R.id.viewBottomSheet).findViewById(R.id.profilo);
        FloatingActionButton btnProfile = findViewById(R.id.viewBottomSheet).findViewById(R.id.btnProfilo);

        if (email.equals("user@guest.com")) {
            View.OnClickListener onClickProfile = new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String messaggio = getResources().getString(R.string.nonPuoiAccedereAlProfilo);
                    showMessage(messaggio);
                }
            };
            BProfile.setOnClickListener(onClickProfile);
            btnProfile.setOnClickListener(onClickProfile);


        } else {
            View.OnClickListener onClickProfile = new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent IBProfile = new Intent(RicercaPercorsiActivity.this, ProfiloActivity.class);
                    startActivity(IBProfile);
                }
            };
            BProfile.setOnClickListener(onClickProfile);
            btnProfile.setOnClickListener(onClickProfile);
        }
        View.OnClickListener onClickLogOut = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String messaggio = getResources().getString(R.string.vuoiDisconnettere);
                showMessage(messaggio);

            }

        };


        LinearLayout BDisconnettiti = findViewById(R.id.viewBottomSheet).findViewById(R.id.disconnettiti);
        FloatingActionButton btnDisconnettiti = findViewById(R.id.viewBottomSheet).findViewById(R.id.btnDisconnettiti);

        BDisconnettiti.setOnClickListener(onClickLogOut);
        btnDisconnettiti.setOnClickListener(onClickLogOut);


        LinearLayout bAiuto = findViewById(R.id.viewBottomSheet).findViewById(R.id.aiuto);
        FloatingActionButton btnAiuto = findViewById(R.id.viewBottomSheet).findViewById(R.id.btnAiuto);

        View.OnClickListener onClickAiuto = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent tutorial = new Intent(getApplicationContext(), ActivityTutorial.class);
                startActivity(tutorial);

            }

        };

        bAiuto.setOnClickListener(onClickAiuto);
        btnAiuto.setOnClickListener(onClickAiuto);

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
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(RicercaPercorsiActivity.this);
                                View mView = getLayoutInflater().inflate(R.layout.dialog, null);
                                CheckBox mCheckBox = mView.findViewById(R.id.checkBox);
                                mBuilder.setTitle("Attivare posizione");
                                mBuilder.setMessage("Per utilizzare le funzionalità del gps bisogna attivarlo");
                                mBuilder.setView(mView);
                                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                AlertDialog mDialog = mBuilder.create();
                                mDialog.show();
                                mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                        if(compoundButton.isChecked()){
                                            storeDialogStatus(true);
                                        }else{
                                            storeDialogStatus(false);
                                        }
                                    }
                                });

                                if(getDialogStatus()){
                                    mDialog.hide();
                                }else{
                                    mDialog.show();
                                }
                                //
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

    private void storeDialogStatus(boolean isChecked){
        SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putBoolean("item", isChecked);
        mEditor.apply();
    }

    private boolean getDialogStatus(){
        SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("item", false);
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
        if(messaggio.equals(getResources().getString(R.string.nonPuoiAccedereAlProfilo))){
            AlertDialog.Builder builder = new AlertDialog.Builder(RicercaPercorsiActivity.this);
            builder.setMessage(messaggio)
                    .setTitle(getResources().getString(R.string.nonAccedutoTitle));
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RicercaPercorsiActivity.this);
            builder.setMessage(messaggio)
                    .setTitle(getResources().getString(R.string.Disconnetti));
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    Intent home = new Intent(RicercaPercorsiActivity.this, LoginActivity.class);
                    startActivity(home);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

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
                                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                                } else {
                                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                }
                                return true;

            case R.id.searchView:
                                    findViewById(R.id.toolbartxt).setVisibility(AppCompatTextView.GONE);
                                    if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                                    }
                                    return true;


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
            }


        } else {
            throw new FileNotFoundException();
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
                                        final long ONE_KILOBYTE = 1024 * 1024 * 1024;
                                        fileOnline.getBytes(ONE_KILOBYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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


    public void showLanguage(){

        //Array che contiene le lingue previste per l'app
        final String list[] = {"Spanish", "French", "English","Italian"};
        android.app.AlertDialog.Builder mBulider = new android.app.AlertDialog.Builder(RicercaPercorsiActivity.this);
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

        android.app.AlertDialog mDialog = mBulider.create();
        mDialog.show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("loadImage", loadImage);
    }
}
