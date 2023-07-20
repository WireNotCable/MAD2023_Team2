package sg.edu.np.mad.pennywise.fragments;

import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import sg.edu.np.mad.pennywise.R;


public class CardViewFragment extends Fragment {
    TextView currentProgressAmt,currentProgressLeft;
    double desiredProgress = 1682.55;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_view,container,false);
        currentProgressAmt = rootView.findViewById(R.id.CurrentProgressAmt);
        currentProgressLeft = rootView.findViewById(R.id.CurrentProgressLeft);

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

        animateTextViews();
    }
    private void animateTextViews() {
        // Format the desiredProgress as currency with locale-specific formatting
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String formattedDesiredProgress = numberFormat.format(desiredProgress);

        // Split the currency symbol and numeric value
        String[] parts = formattedDesiredProgress.split("\\d+");
        String currencySymbol = parts[0];
        String numericValue = parts[1].replaceAll("[^\\d.]", "");

        // Animate the CurrentProgressAmt TextView
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, (float) desiredProgress);
        valueAnimator.setDuration(500); // Animation duration in milliseconds

        valueAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();

            // Format the animated value as currency with locale-specific formatting
            String formattedValue = numberFormat.format(animatedValue);

            // Update the CurrentProgressAmt TextView with the animated value
            currentProgressAmt.setText(formattedValue);
        });

        valueAnimator.start();

        // Animate the CurrentProgressLeft TextView
        ValueAnimator valueAnimatorLeft = ValueAnimator.ofFloat(0f, (float) (10000 - desiredProgress));
        valueAnimatorLeft.setDuration(500); // Animation duration in milliseconds

        valueAnimatorLeft.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();

            // Format the remaining value as currency with locale-specific formatting
            String formattedRemainingValue = numberFormat.format(animatedValue);

            // Update the CurrentProgressLeft TextView with the remaining value
            currentProgressLeft.setText(formattedRemainingValue + " LEFT");
        });

        valueAnimatorLeft.start();
    }
}