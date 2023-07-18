package sg.edu.np.mad.pennywise.models;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import sg.edu.np.mad.pennywise.R;


public class Goal_Progress extends AppCompatActivity {
    ProgressBar progressBar;
    ViewPager2 viewPager;
    LinearLayout pagerDots;
    int NUM_PAGES = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_progress);
        /*int desiredProgress = 60;
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
        progressBar.setAnimation(animationSet);*/
        viewPager = findViewById(R.id.view_pager);
        pagerDots = findViewById(R.id.pager_dots);

        // Set up the ViewPager2 adapter
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), getLifecycle()));

        // Create the carousel buttons (page indicators)
        createCarouselButtons();

        // Set a listener to handle page changes and update carousel buttons accordingly
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateCarouselButtons(position);
            }
        });
    }

    private void createCarouselButtons() {
        for (int i = 0; i < NUM_PAGES; i++) {
            ImageView dot = new ImageView(this);
            int size = getResources().getDimensionPixelSize(R.dimen.carousel_button_size);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            int margin = getResources().getDimensionPixelSize(R.dimen.carousel_button_margin);
            params.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(params);
            dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.page_indicator_unselected));
            pagerDots.addView(dot);

            dot.setOnClickListener(new CarouselButtonClickListener(i));

        }


        // Set the first carousel button as selected by default
        updateCarouselButtons(0);
    }

    private void updateCarouselButtons(int selectedPosition) {
        for (int i = 0; i < pagerDots.getChildCount(); i++) {
            ImageView dot = (ImageView) pagerDots.getChildAt(i);
            dot.setImageDrawable(ContextCompat.getDrawable(this,
                    i == selectedPosition ? R.drawable.page_indicator_selected : R.drawable.page_indicator_unselected));
        }
    }
    private void loadFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new ProgressBarFragment();
                break;
            case 1:
                fragment = new LineChartFragment();
                break;
            case 2:
                fragment = new CardViewFragment();
                break;
            default:
                fragment = null;
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.view_pager, fragment)
                    .commit();
        }
    }
    private class CarouselButtonClickListener implements View.OnClickListener {
        private int position;

        public CarouselButtonClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(position, true);
            loadFragment(position); // Load the corresponding fragment when button clicked
        }
    }
}