package sg.edu.np.mad.pennywise;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;


public class Goal_Progress extends AppCompatActivity {

    ProgressBar progressBar;
    ViewPager2 viewPager;
    LinearLayout pagerDots;
    int NUM_PAGES = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_progress);

        viewPager = findViewById(R.id.view_pager);
        pagerDots = findViewById(R.id.pager_dots);

        viewPager.setAdapter(new ProgressPagerAdapter(this));

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
            dot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = pagerDots.indexOfChild(v);
                    if (position >= 0) {
                        viewPager.setCurrentItem(position, true);
                    }
                }
            });
            pagerDots.addView(dot);



        }


        updateCarouselButtons(0);
    }

    private void updateCarouselButtons(int selectedPosition) {
        for (int i = 0; i < pagerDots.getChildCount(); i++) {
            ImageView dot = (ImageView) pagerDots.getChildAt(i);
            dot.setImageDrawable(ContextCompat.getDrawable(this,
                    i == selectedPosition ? R.drawable.page_indicator_selected : R.drawable.page_indicator_unselected));
        }
    }

    @Override
    public void onResume(){
        super.onResume();


    }




}