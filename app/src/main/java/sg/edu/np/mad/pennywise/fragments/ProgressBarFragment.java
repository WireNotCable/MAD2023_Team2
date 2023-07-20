package sg.edu.np.mad.pennywise.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import sg.edu.np.mad.pennywise.R;


public class ProgressBarFragment extends Fragment {
    ProgressBar progressBar;
    TextView progressValue;
    int desiredProgress = 60;
    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_progress_bar, container,false);

        progressBar = rootView.findViewById(R.id.progressBar);
        progressValue = rootView.findViewById(R.id.progressValue);

        progressBar.setProgress(0);



        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        startAnimation();
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