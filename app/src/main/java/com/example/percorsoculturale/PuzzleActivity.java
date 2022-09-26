package com.example.percorsoculturale;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.example.percorsoculturale.GestureDetectGridView;
import com.example.percorsoculturale.tables.Attivita;
import com.example.percorsoculturale.tables.Puzzle;

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


    static Puzzle puzzle1=new Puzzle(attivita,"puzzle");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setContentView(R.layout.activity_puzzle);



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
ArrayList<Button> buttons=new ArrayList<>();
Button button;

for(int i=0;i<tileList.length;i++){

    button=new Button(context);


    Context c = button.getContext();

    if(tileList[i].equals("0")){

       Drawable img= GetImage(c,puzzle1.getDirectory()+"_1");
        button.setBackgroundDrawable(img);


    }else if (tileList[i].equals("1")){
        Drawable img= GetImage(c,puzzle1.getDirectory()+"_2");
        button.setBackgroundDrawable(img);
    }else if (tileList[i].equals("2")){
        Drawable img= GetImage(c,puzzle1.getDirectory()+"_3");
        button.setBackgroundDrawable(img);
    }else if (tileList[i].equals("3")){
        Drawable img= GetImage(c,puzzle1.getDirectory()+"_4");
        button.setBackgroundDrawable(img);
    }else if (tileList[i].equals("4")){
        Drawable img= GetImage(c,puzzle1.getDirectory()+"_5");
        button.setBackgroundDrawable(img);
    }else if (tileList[i].equals("5")){
        Drawable img= GetImage(c,puzzle1.getDirectory()+"_6");
        button.setBackgroundDrawable(img);
    }else if (tileList[i].equals("6")){
        Drawable img= GetImage(c,puzzle1.getDirectory()+"_7");
        button.setBackgroundDrawable(img);
    }else if (tileList[i].equals("7")){
        Drawable img= GetImage(c,puzzle1.getDirectory()+"_8");
        button.setBackgroundDrawable(img);
    }else if (tileList[i].equals("8")){
        Drawable img= GetImage(c,puzzle1.getDirectory()+"_9");
        button.setBackgroundDrawable(img);
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

        if (isSolved()) Toast.makeText(context, "YOU WIN!", Toast.LENGTH_SHORT).show();
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

//prende un drawable attraverso una stringa
    public static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }
}
