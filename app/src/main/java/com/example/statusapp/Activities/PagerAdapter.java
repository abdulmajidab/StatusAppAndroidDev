package com.example.statusapp.Activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.example.statusapp.R;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> myFragments = new ArrayList<>();
    private final List<String> myFragmentTitles = new ArrayList<>();
    private Context context;

    private int[] imageResId = {
           /* R.drawable.todaypng,
            R.drawable.week,
            R.drawable.month,
            R.drawable.yearicon,*/
    };

    private int[] customImageResId = {
           /* R.drawable.todaypng,
            R.drawable.week,
            R.drawable.month,
            R.drawable.yearicon,*/
    };

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public void addFragment(Fragment fragment, String title) {
        myFragments.add(fragment);
        myFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return myFragments.get(position);
    }

    @Override
    public int getCount() {
        return myFragments.size();
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab_item.xml` with a TextView and ImageView
        View v = LayoutInflater.from(context).inflate(R.layout.custom_tab_item, null);
        TextView tv = (TextView) v.findViewById(R.id.tab_item_txt);
        tv.setText(myFragmentTitles.get(position));
        ImageView img = (ImageView) v.findViewById(R.id.tab_item_view);
        img.setImageResource(customImageResId[position]);
        return v;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //text only, use this
//            return myFragmentTitles.get(position);

        //add Tab with icon and text, use this
        Drawable image = context.getResources().getDrawable(imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString("   " + myFragmentTitles.get(position));
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}