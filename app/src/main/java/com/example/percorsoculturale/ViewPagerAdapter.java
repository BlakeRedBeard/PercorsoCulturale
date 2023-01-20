package com.example.percorsoculturale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;

    int images[] = {

            R.drawable.tutoriallogin_screen,
            R.drawable.tutorialhome_screen,
            R.drawable.tutorialricerca_screen,
            R.drawable.tutorialpercorso_screen,
            R.drawable.tutorialattrazione_screen


    };

    int headings[] = {

            R.string.tutorialhead1,
            R.string.tutorialhead2,
            R.string.tutorialhead3,
            R.string.tutorialhead4,
            R.string.tutorialhead5
    };

    int description[] = {

            R.string.tutorialdescr1,
            R.string.tutorialdescr2,
            R.string.tutorialdescr3,
            R.string.tutorialdescr4,
            R.string.tutorialdescr5
    };

    public ViewPagerAdapter(Context context){

        this.context = context;

    }

    @Override
    public int getCount() {
        return  headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout,container,false);

        ImageView slidetitleimage = view.findViewById(R.id.titleImage);
        TextView slideHeading = view.findViewById(R.id.texttitle);
        TextView slideDesciption = view.findViewById(R.id.textdeccription);

        slidetitleimage.setImageResource(images[position]);
        slideHeading.setText(headings[position]);
        slideDesciption.setText(description[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout)object);

    }
}
