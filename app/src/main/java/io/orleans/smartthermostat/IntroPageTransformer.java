package io.orleans.smartthermostat;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class IntroPageTransformer implements ViewPager.PageTransformer {

    Context context;

    static int backgroundColor = 0;

    static GestureDetectorCompat mDetector = null;

    public IntroPageTransformer (Context context) {
        this.context = context;
    }

    @Override
    public void transformPage(View page, float position) {

        // Get the page index from the tag. This makes
        // it possible to know which page index you're
        // currently transforming - and that can be used
        // to make some important performance improvements.
        int pagePosition = (int) page.getTag();

        if (pagePosition == 1) {

        }

        // Here you can do all kinds of stuff, like get the
        // width of the page and perform calculations based
        // on how far the user has swiped the page.
        int pageWidth = page.getWidth();
        float pageWidthTimesPosition = pageWidth * position;
        float absPosition = Math.abs(position);

        // Now it's time for the effects
        if (position <= -1.0f || position >= 1.0f) {

            // The page is not visible. This is a good place to stop
            // any potential work / animations you may have running.

        } else if (position == 0.0f) {

            // The page is selected. This is a good time to reset Views
            // after animations as you can't always count on the PageTransformer
            // callbacks to match up perfectly.

        } else {

            // The page is currently being scrolled / swiped. This is
            // a good place to show animations that react to the user's
            // swiping as it provides a good user experience.

            // Now the description. We also want this one to
            // fade, but the animation should also slowly move
            // down and out of the screen
            View description = page.findViewById(R.id.description);
            description.setTranslationY(-pageWidthTimesPosition / 2f);
            description.setAlpha(1.0f - absPosition);

            // Now, we want the image to move to the right,
            // i.e. in the opposite direction of the rest of the
            // content while fading out
            View history = page.findViewById(R.id.computer);

            // We're attempting to create an effect for a View
            // specific to one of the pages in our ViewPager.
            // In other words, we need to check that we're on
            // the correct page and that the View in question
            // isn't null.
            if (pagePosition == 0 && history != null) {
                history.setAlpha(1.0f - absPosition);
                history.setTranslationX(-pageWidthTimesPosition * 0.5f);
            }


            View schedule = page.findViewById(R.id.tv);
            if (pagePosition == 2 && schedule != null) {
                schedule.setAlpha(1.0f - absPosition);
                schedule.setTranslationX(-pageWidthTimesPosition * 0.5f);
            }

            // Finally, it can be useful to know the direction
            // of the user's swipe - if we're entering or exiting.
            // This is quite simple:
            if (position < 0) {
                // Create your out animation here
            } else {
                // Create your in animation here
            }

//            System.out.println(page.getBackground());
            int color;
            float[] from = new float[3];
            float[] to = new float[3];;
            switch (pagePosition) {
                case 0:
                    if (position < 0) {
                        color = ContextCompat.getColor(context, R.color.schedule);
                        Color.colorToHSV(color, from);
                        color = Screwit.tempColor;
                        Color.colorToHSV(color, to);
                    } else {
                        color = Screwit.tempColor;
                        Color.colorToHSV(color, from);
                        color = ContextCompat.getColor(context, R.color.schedule);
                        Color.colorToHSV(color, to);
                    }
                    break;
                case 1:
                    if (position < 0) {
                        color = Screwit.tempColor;
                        Color.colorToHSV(color, from);
                        color = ContextCompat.getColor(context, R.color.schedule);
                        Color.colorToHSV(color, to);
                    } else {
                        color = Screwit.tempColor;
                        Color.colorToHSV(color, from);
                        color = ContextCompat.getColor(context, R.color.history);
                        Color.colorToHSV(color, to);
                    }
                    break;
                case 2:
                    if (position < 0) {
                        color = Screwit.tempColor;
                        Color.colorToHSV(color, from);
                        color = ContextCompat.getColor(context, R.color.history);
                        Color.colorToHSV(color, to);
                    } else {
                        color = ContextCompat.getColor(context, R.color.history);
                        Color.colorToHSV(color, from);
                        color = Screwit.tempColor;
                        Color.colorToHSV(color, to);
                    }
                    break;
                case 3: default:
                    if (position < 0) {
                        color = ContextCompat.getColor(context, R.color.schedule);
                        Color.colorToHSV(color, from);
                        color = ContextCompat.getColor(context, R.color.settings);
                        Color.colorToHSV(color, to);
                    } else {
                        color = ContextCompat.getColor(context, R.color.settings);
                        Color.colorToHSV(color, from);
                        color = ContextCompat.getColor(context, R.color.schedule);
                        Color.colorToHSV(color, to);
                    }
                    break;
            }

            if (backgroundColor == 0) {
                float inverseRatio = 1f - absPosition;
                backgroundColor = Color.HSVToColor(new float[]{
                    from[0] * inverseRatio + to[0] * absPosition,
                    from[1] * inverseRatio + to[1] * absPosition,
                    from[2] * inverseRatio + to[2] * absPosition
                });
                page.setBackgroundColor(backgroundColor);
            } else {
                page.setBackgroundColor(backgroundColor);
                backgroundColor = 0;
            }
        }
    }

}