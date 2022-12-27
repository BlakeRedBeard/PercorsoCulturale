package com.example.percorsoculturale;

import static com.example.percorsoculturale.QuizActivity.db;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.percorsoculturale.tables.Attivita;
import com.example.percorsoculturale.tables.Puzzle;
import com.example.percorsoculturale.tables.Quiz;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Random;


public class PuzzleActivity extends AppCompatActivity {

    public static Attivita attivita;

    private static final int COLUMNS=3;
    private static final int DIMENSIONS=COLUMNS*COLUMNS;

    private static String[] tileList;

    private static GestureDetectGridView mGridView;

    private static int mColumnWidth;
    private static int mColumnHeight;
    public static final String up = "up";
    public static final String down = "down";
    public static final String left = "left";
    public static final String right = "right";
    //Configurazione db
    public static FirebaseStorage storage ;


    static String searchPuzzle;
    private int id = 0;
    //static  Puzzle puzzle1=new Puzzle(attivita,"Puzzle");
    Puzzle puzzle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchPuzzle="";

       setContentView(R.layout.activity_puzzle);

       storage= FirebaseStorage.getInstance();

       Bundle extra = getIntent().getExtras();

       System.out.println("search1"+searchPuzzle);
       searchPuzzle = extra.getString("id");

        id = extra.getInt("Idattrazioni");
        showPuzzle(searchPuzzle);



        Intent intent2 = new Intent(getApplicationContext(), MostraAttrazioni.class);
        intent2.putExtra("Idattrazione", id);


        init();

        scramble();

        setDimension();


    }
    private void init(){
        mGridView=(GestureDetectGridView)findViewById(R.id.grid);
        mGridView.setNumColumns(COLUMNS);
        tileList = new String[DIMENSIONS];
        for(int i=0;i<DIMENSIONS;i++){
            tileList[i]=String.valueOf(i);
        }
    }
    private void scramble(){
        int index;
        String temp;
        Random random=new Random();

        for (int i=tileList.length-1;i>0;i--){
            index= random.nextInt(i+1);
            temp=tileList[index];
            tileList[index]=tileList[i];
            tileList[i]=temp;
        }
    }


    private void setDimension() {
        ViewTreeObserver vto=mGridView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth=mGridView.getMeasuredWidth();
                int displayHeight=mGridView.getMeasuredHeight();

                  int statusbarHeight=getStatusBarHeight(getApplicationContext());
                int requiredHeight = displayHeight - statusbarHeight;

                mColumnWidth = displayWidth / COLUMNS;
                mColumnHeight = requiredHeight / COLUMNS;

                display(getApplicationContext());
            }
        });

    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    private static void display(Context context){
        //firebase db
       StorageReference storageRef = storage.getReference().child("immagini puzzle/"+ searchPuzzle);
        ArrayList<Button> buttons=new ArrayList<>();
        Button button;



        for(int i=0;i<tileList.length;i++){

    button=new Button(context);




    if(tileList[i].equals("0")){

        Button finalButton = button;
      storageRef.child(searchPuzzle+ "_1.png").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ByteArrayInputStream is=new ByteArrayInputStream(bytes);
              Drawable  img = Drawable.createFromStream(is, "articleImage");
                finalButton.setBackgroundDrawable(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });




    }else if (tileList[i].equals("1")){
        Button finalButton = button;
        storageRef.child(searchPuzzle+ "_2.png").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ByteArrayInputStream is=new ByteArrayInputStream(bytes);
                Drawable  img = Drawable.createFromStream(is, "articleImage");
                finalButton.setBackgroundDrawable(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }else if (tileList[i].equals("2")){
        Button finalButton = button;
        storageRef.child(searchPuzzle+ "_3.png").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ByteArrayInputStream is=new ByteArrayInputStream(bytes);
                Drawable  img = Drawable.createFromStream(is, "articleImage");
                finalButton.setBackgroundDrawable(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }else if (tileList[i].equals("3")){
        Button finalButton = button;
        storageRef.child(searchPuzzle+ "_4.png").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ByteArrayInputStream is=new ByteArrayInputStream(bytes);
                Drawable  img = Drawable.createFromStream(is, "articleImage");
                finalButton.setBackgroundDrawable(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }else if (tileList[i].equals("4")){
        Button finalButton = button;
        storageRef.child(searchPuzzle+ "_5.png").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ByteArrayInputStream is=new ByteArrayInputStream(bytes);
                Drawable  img = Drawable.createFromStream(is, "articleImage");
                finalButton.setBackgroundDrawable(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }else if (tileList[i].equals("5")){
        Button finalButton = button;
        storageRef.child(searchPuzzle+ "_6.png").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ByteArrayInputStream is=new ByteArrayInputStream(bytes);
                Drawable  img = Drawable.createFromStream(is, "articleImage");
                finalButton.setBackgroundDrawable(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }else if (tileList[i].equals("6")){
        Button finalButton = button;
        storageRef.child(searchPuzzle+ "_7.png").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ByteArrayInputStream is=new ByteArrayInputStream(bytes);
                Drawable  img = Drawable.createFromStream(is, "articleImage");
                finalButton.setBackgroundDrawable(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }else if (tileList[i].equals("7")){
        Button finalButton = button;
        storageRef.child(searchPuzzle+ "_8.png").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ByteArrayInputStream is=new ByteArrayInputStream(bytes);
                Drawable  img = Drawable.createFromStream(is, "articleImage");
                finalButton.setBackgroundDrawable(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }else if (tileList[i].equals("8")){
        Button finalButton = button;
        storageRef.child(searchPuzzle+ "_9.png").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ByteArrayInputStream is=new ByteArrayInputStream(bytes);
                Drawable  img = Drawable.createFromStream(is, "articleImage");
                finalButton.setBackgroundDrawable(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }




    buttons.add(button);


}

mGridView.setAdapter(new CustomAdapter(buttons,mColumnWidth,mColumnHeight));
    }


    private static void swap(Context context, int currentPosition, int swap) {
        String newPosition = tileList[currentPosition + swap];
        tileList[currentPosition + swap] = tileList[currentPosition];
        tileList[currentPosition] = newPosition;
        display(context);

        if (isSolved()){
            //PUNTI
            Toast.makeText(context, "Hai vinto!!", Toast.LENGTH_SHORT).show();
            QrcodeActivity.aggiungiPunti(10);

        }
    }

    public static void moveTiles(Context context, String direction, int position) {

        // Upper-left-corner tile
        if (position == 0) {

            if (direction.equals(right)) swap(context, position, 1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Upper-center tiles
        } else if (position > 0 && position < COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Upper-right-corner tile
        } else if (position == COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Left-side tiles
        } else if (position > COLUMNS - 1 && position < DIMENSIONS - COLUMNS &&
                position % COLUMNS == 0) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Right-side AND bottom-right-corner tiles
        } else if (position == COLUMNS * 2 - 1 || position == COLUMNS * 3 - 1) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) {

                // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                // right-corner tile.
                if (position <= DIMENSIONS - COLUMNS - 1) swap(context, position,
                        COLUMNS);
                else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Bottom-left corner tile
        } else if (position == DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Bottom-center tiles
        } else if (position < DIMENSIONS - 1 && position > DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Center tiles
        } else {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(right)) swap(context, position, 1);
            else swap(context, position, COLUMNS);
        }
    }

    //Se è stato risolto
    private static boolean isSolved() {
        boolean solved = false;

        for (int i = 0; i < tileList.length; i++) {
            if (tileList[i].equals(String.valueOf(i))) {
                solved = true;
            } else {
                solved = false;
                break;
            }
        }

        return solved;
    }
    private void showPuzzle(String searchPuzzle){
        db= FirebaseFirestore.getInstance();
        db.collection("attività")
                .document(searchPuzzle)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //isSolved();

                    }
                });
    }
//prende un drawable attraverso una stringa
    public static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }
}
