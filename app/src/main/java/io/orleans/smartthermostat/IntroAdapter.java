package io.orleans.smartthermostat;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

public class IntroAdapter extends FragmentPagerAdapter {

    private Context context;

    public IntroAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return IntroFragment.newInstance(ContextCompat.getColor(context, R.color.history), position); // blue
            case 1:
                return IntroFragment.newInstance(ContextCompat.getColor(context, R.color.neutral), position); // green
            case 2:
                return IntroFragment.newInstance(ContextCompat.getColor(context, R.color.schedule), position); // green
            case 3: default:
                return IntroFragment.newInstance(ContextCompat.getColor(context, R.color.settings), position); // green
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
    
}