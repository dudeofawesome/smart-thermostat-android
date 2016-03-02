package io.orleans.smartthermostat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.os.Handler;

public class IntroFragment extends Fragment {

    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String PAGE = "page";

    private int mBackgroundColor, mPage;

    public static IntroFragment newInstance(int backgroundColor, int page) {
        IntroFragment frag = new IntroFragment();
        Bundle b = new Bundle();
        b.putInt(BACKGROUND_COLOR, backgroundColor);
        b.putInt(PAGE, page);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(BACKGROUND_COLOR))
            throw new RuntimeException("Fragment must contain a \"" + BACKGROUND_COLOR + "\" argument!");
        mBackgroundColor = getArguments().getInt(BACKGROUND_COLOR);

        if (!getArguments().containsKey(PAGE))
            throw new RuntimeException("Fragment must contain a \"" + PAGE + "\" argument!");
        mPage = getArguments().getInt(PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Select a layout based on the current page
        int layoutResId;
        switch (mPage) {
            case 0:
                layoutResId = R.layout.intro_fragment_layout_1;
                break;
            case 1:
                layoutResId = R.layout.intro_fragment_layout_2;
                break;
            case 2:
                layoutResId = R.layout.intro_fragment_layout_3;
                break;
            case 3: default:
                layoutResId = R.layout.intro_fragment_layout_4;
        }

        // Inflate the layout resource file
        final View view = getActivity().getLayoutInflater().inflate(layoutResId, container, false);

        // Set the current page index as the View's tag (useful in the PageTransformer)
        view.setTag(mPage);

        if (layoutResId == R.layout.intro_fragment_layout_1) {
            ((ImageButton) view.findViewById(R.id.computer)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {

                }
            });
        } else if (layoutResId == R.layout.intro_fragment_layout_2) {
            Screwit.mainView = view;

            final TextView txtTemp = (TextView) view.findViewById(R.id.temperature);

            final Handler h = new Handler();
            h.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    txtTemp.setText(((int) IntroActivity.current_temperature) + "°");

                    if (Screwit.serial != null) {
                        byte[] relay = new byte[1];
                        relay[0] = 0x000039;
                        Screwit.serial.write(relay);
                    }

                    h.postDelayed(this, 1000);
                };
            }, 1000);

            ((ImageButton) view.findViewById(R.id.btnUp)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    IntroActivity.target_temperature += 1;
                    ((TextView) view.findViewById(R.id.targetTemp)).setText("Targeting " + ((int) IntroActivity.target_temperature) + "°");
                    IntroActivity.sendTemp();
                    view.setBackgroundColor(Screwit.tempColor);
                }
            });

            ((ImageButton) view.findViewById(R.id.btnDown)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    IntroActivity.target_temperature -= 1;
                    ((TextView) view.findViewById(R.id.targetTemp)).setText("Targeting " + ((int) IntroActivity.target_temperature) + "°");
                    IntroActivity.sendTemp();
                    view.setBackgroundColor(Screwit.tempColor);
                }
            });
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the background color of the root view to the color specified in newInstance()
        View background = view.findViewById(R.id.intro_background);
        background.setBackgroundColor(mBackgroundColor);
    }
    
}