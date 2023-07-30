package sg.edu.np.mad.pennywise2.fragments;

import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Locale;

import sg.edu.np.mad.pennywise2.OnQueryCompleteListener;
import sg.edu.np.mad.pennywise2.R;


public class CardViewFragment extends Fragment {
    TextView currentProgressAmt,currentProgressLeft;
    String uid;
    FirebaseFirestore db;
    FirebaseAuth auth;
    double desiredProgress = 0;
    double currentProgress = 0;
    public CardViewFragment(String uid) {
        this.uid = uid;
    }
    //getting data to show current and remaining amount for goal
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_view,container,false);
        currentProgressAmt = rootView.findViewById(R.id.CurrentProgressAmt);
        currentProgressLeft = rootView.findViewById(R.id.CurrentProgressLeft);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(auth.getUid()).collection("goals").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                double amount = document.getDouble("amount");
                                double current = document.getDouble("current");
                                desiredProgress = amount;
                                currentProgress = current;
                            }
                        }
                    }
                });

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

        animateTextViews();
    }
    // card number values animation
    private void animateTextViews() {
        // Format the desiredProgress as currency with locale-specific formatting
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String formattedDesiredProgress = numberFormat.format(desiredProgress);

        // Split the currency symbol and numeric value
        String[] parts = formattedDesiredProgress.split("\\d+");
        String currencySymbol = parts[0];
        String numericValue = parts[1].replaceAll("[^\\d.]", "");

        // Animate the CurrentProgressAmt TextView
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, (float) currentProgress);
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
        ValueAnimator valueAnimatorLeft = ValueAnimator.ofFloat(0f, (float) (desiredProgress - currentProgress));
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