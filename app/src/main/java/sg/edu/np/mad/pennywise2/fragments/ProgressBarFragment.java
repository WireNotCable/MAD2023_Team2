package sg.edu.np.mad.pennywise.fragments;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import sg.edu.np.mad.pennywise.R;


public class ProgressBarFragment extends Fragment {
    ProgressBar progressBar;
    TextView progressValue;
    FirebaseAuth auth;
    FirebaseFirestore db;
    int desiredProgress = 60;
    String uid;
    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/
    public ProgressBarFragment(String uid){
        this.uid = uid;
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_progress_bar, container,false);

        progressBar = rootView.findViewById(R.id.progressBar);
        progressValue = rootView.findViewById(R.id.progressValue);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        progressBar.setProgress(0);




        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
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
                                desiredProgress = (int)(current/amount*100);
                                startAnimation();

                            }
                        }
                    }
                });
    }

    private void startAnimation() {
        // Animate ProgressBar and TextView with fadeIn and translate animations
        AnimationSet animationSet = new AnimationSet(true);
        Animation fadeInAnimation = new AlphaAnimation(0, 1);

        fadeInAnimation.setDuration(400);

        animationSet.addAnimation(fadeInAnimation);

        progressBar.startAnimation(animationSet);
        progressValue.startAnimation(animationSet);

        // Animate ProgressBar from 0% to desired progress in 1 second
        ValueAnimator progressAnimator = ValueAnimator.ofInt(0, desiredProgress);
        progressAnimator.setDuration(700);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                progressBar.setProgress(progress);

                // Update TextView with the progress value
                progressValue.setText(progress + "%");
            }
        });
        progressAnimator.start();
    }

}