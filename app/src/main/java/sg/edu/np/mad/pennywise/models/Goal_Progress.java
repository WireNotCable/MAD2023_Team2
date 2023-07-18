package sg.edu.np.mad.pennywise.models;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ProgressBar;

import sg.edu.np.mad.pennywise.R;


public class Goal_Progress extends AppCompatActivity {
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_progress);
        int desiredProgress = 60;
        progressBar = findViewById(R.id.progressBar);

        progressBar.setProgress(0);

        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar,"progress",0,desiredProgress);
        AnimationSet animationSet = new AnimationSet(true);
        Animation fadeInAnimation = new AlphaAnimation(0,1);
        Animation translateAnimation = new TranslateAnimation(-progressBar.getWidth(),0,0,0);

        progressAnimator.setDuration(1000);
        fadeInAnimation.setDuration(1500);
        translateAnimation.setDuration(2000);

        animationSet.addAnimation(fadeInAnimation);
        animationSet.addAnimation(translateAnimation);

        progressAnimator.start();
        progressBar.setAnimation(animationSet);
    }
}