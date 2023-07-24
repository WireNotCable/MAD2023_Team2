package sg.edu.np.mad.pennywise;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;


public class Goal_Progress_Individual extends AppCompatActivity {
    ProgressBar progressBar;
    ViewPager2 viewPager;
    LinearLayout pagerDots;
    EditText ititle;
    RecyclerView recyclerViewi;
    ImageView back;
    ImageButton add;
    String value;
    FirebaseAuth auth;
    FirebaseFirestore db;
    Goal_Indivisual_Adapter adapter;
    ArrayList<IndivisualGoalI> progressList;
    int NUM_PAGES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_progress_indivisual);


        viewPager = findViewById(R.id.view_pager);
        pagerDots = findViewById(R.id.pager_dots);
        ititle = findViewById(R.id.ProgressTitle);
        back = findViewById(R.id.Progress_Indiviual_back);
        recyclerViewi = findViewById(R.id.IndivisualProgressView);
        recyclerViewi.setLayoutManager(new LinearLayoutManager(this));
        add = findViewById(R.id.imageButton4);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        progressList=  new ArrayList<IndivisualGoalI>();
        viewPager.setAdapter(new ProgressPagerAdapter(this,progressList));

        createCarouselButtons();

        // Set a listener to handle page changes and update carousel buttons accordingly
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateCarouselButtons(position);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(Goal_Progress_Individual.this);
                View dialogView = inflater.inflate(R.layout.dialog_progress_create, null);

                TextInputEditText name = dialogView.findViewById(R.id.CreateProgressName);
                TextInputEditText amount = dialogView.findViewById(R.id.CreateProgressAmount);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                alertDialogBuilder.setView(dialogView);
                alertDialogBuilder.setTitle("New Saving");

                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!value.isEmpty()){
                            String inputname = String.valueOf(name.getText()).trim();
                            String inputamount = amount.getText().toString().trim();
                            if (inputamount.isEmpty() && inputname.isEmpty()){
                                double finalamount = Double.parseDouble(inputamount);
                                HashMap<String,Object> data = new HashMap<>();
                                data.put("name",inputname);
                                data.put("amount",finalamount);
                                Date date = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                data.put("date",sdf.format(date));
                                db.collection("users").document(auth.getUid()).collection("goals").document(value).collection("savings").document().set(data);
                                Toast.makeText(getApplicationContext(),"New progress added",Toast.LENGTH_SHORT).show();
                                recreate();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"name/amount inputted invalid", Toast.LENGTH_SHORT).show();
                            }

                        }


                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                // Show the AlertDialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }
    private void getData(){
        progressList.clear();
        progressList.add(new IndivisualGoalI("test",8.299,"2/3/4","1234567"));
        adapter = new Goal_Indivisual_Adapter(progressList,Goal_Progress_Individual.this);
        recyclerViewi.setAdapter(adapter);
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
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("UID")) {
            value = intent.getStringExtra("UID");
            ititle.setText(value);
            getData();
        }


    }

    private void AnimateConfetti() {
        KonfettiView konfettiView = findViewById(R.id.konfettiView);
        Shape.DrawableShape drawableShape = new Shape.DrawableShape(AppCompatResources.getDrawable(this, R.drawable.ic_android_black_24dp), true);
        EmitterConfig emitterConfig = new Emitter(300, TimeUnit.MILLISECONDS).max(300);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .shapes(Shape.Circle.INSTANCE, Shape.Square.INSTANCE, drawableShape)
                        .spread(135)
                        .position(0f, 0f, 1f, 1f)
                        .sizes(new Size(8, 50, 10))
                        .timeToLive(15000)
                        .fadeOutEnabled(true)
                        .build()

        );
    }

    private int getScreenHeight() {
        WindowManager windowManager = getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private int getScreenWidth() {
        WindowManager windowManager = getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


}