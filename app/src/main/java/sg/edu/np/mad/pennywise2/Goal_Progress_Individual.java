package sg.edu.np.mad.pennywise2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import sg.edu.np.mad.pennywise2.models.IndivisualGoalI;


public class Goal_Progress_Individual extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ProgressBar progressBar;
    ViewPager2 viewPager;
    LinearLayout pagerDots;
    EditText ititle;
    RecyclerView recyclerViewi;
    ImageView back;
    Button add;
    String value;
    FirebaseAuth auth;
    FirebaseFirestore db;
    Goal_Indivisual_Adapter adapter;
    ArrayList<IndivisualGoalI> progressList;
    public static final String GLOBAL_PREFS = "myPrefs";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    int NUM_PAGES = 3;
    private static final int REQUEST_CODE_PERMISSIONS = 1001;

    //setting item views and detecting if certain items are clicked
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_progress_indivisual);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove title in homepage
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);


        viewPager = findViewById(R.id.view_pager);
        pagerDots = findViewById(R.id.pager_dots);
        ititle = findViewById(R.id.ProgressTitle);
//        back = findViewById(R.id.Progress_Indiviual_back);
        recyclerViewi = findViewById(R.id.IndivisualProgressView);
        recyclerViewi.setLayoutManager(new LinearLayoutManager(this));
        add = findViewById(R.id.imageButton4);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        progressList = new ArrayList<IndivisualGoalI>();

        createCarouselButtons();

        // Set a listener to handle page changes and update carousel buttons accordingly
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateCarouselButtons(position);
            }
        });
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        //adding a new savings associated with the goal
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = new LinearLayout(Goal_Progress_Individual.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                getLayoutInflater().inflate(R.layout.dialog_progress_create, layout, true);

                TextInputEditText name = layout.findViewById(R.id.CreateProgressName);
                TextInputEditText amount = layout.findViewById(R.id.CreateProgressAmount);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Goal_Progress_Individual.this);
                alertDialogBuilder.setView(layout);
                alertDialogBuilder.setTitle("Create Progress");

                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    //add new data to firestore once values are validated
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (!value.isEmpty()) {
                            String inputname = String.valueOf(name.getText()).trim();
                            String inputamount = amount.getText().toString().trim();
                            if (!inputamount.isEmpty() && !inputname.isEmpty()) {
                                double finalamount = Double.parseDouble(inputamount);
                                HashMap<String, Object> data = new HashMap<>();
                                data.put("name", inputname);
                                data.put("amount", finalamount);
                                Date date = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                data.put("date", sdf.format(date));
                                db.collection("users").document(auth.getUid()).collection("goals").document(value).collection("savings").document().set(data);
                                db.collection("users").document(auth.getUid()).collection("goals").document(value).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot shot = task.getResult();
                                                    double current = Double.parseDouble(String.valueOf(shot.getLong("current")));
                                                    current += finalamount;
                                                    db.collection("users").document(auth.getUid()).collection("goals").document(value)
                                                            .update("current", current)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(getApplicationContext(), "New progress added", Toast.LENGTH_SHORT).show();

                                                                        recreate();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });

                            } else {
                                Toast.makeText(getApplicationContext(), "name/amount inputted invalid", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        // check if the goal has been fulfilled each time the page reloads
        // take screen shot and play confetti animation to share to social media
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("UID")) {
            value = intent.getStringExtra("UID");
            db.collection("users").document(auth.getUid()).collection("goals").document(value)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                String name = document.getString("name");
                                double amount = document.getDouble("amount");
                                double current = document.getDouble("current");
                                ititle.setText(name);
                                if (current >= amount){
                                    //animation
                                    AnimateConfetti();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (checkStoragePermission()) {
                                                takeScreenshotAndShare();
                                            } else {
                                                // Request permissions
                                                ActivityCompat.requestPermissions(Goal_Progress_Individual.this,
                                                        new String[]{
                                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                                Manifest.permission.READ_EXTERNAL_STORAGE
                                                        },
                                                        REQUEST_CODE_PERMISSIONS);
                                            }
                                        }
                                    }, 5000);


                                }
                            }
                        }
                    });

            getData();

        }
        viewPager.setAdapter(new ProgressPagerAdapter(this, value));

    }
    // check if the access to gallery is allowed
    private boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true; // Permission is granted
        } else {
            return false; // Permission is not granted
        }
    }
    // take screenshot
    private void takeScreenshotAndShare() {
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap screenshotBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        String textToAdd = "I have completed my goal!";

        // Add text to the screenshot
        if (!textToAdd.isEmpty()) {
            screenshotBitmap = addTextToBitmap(screenshotBitmap, textToAdd);
        }

        // Share the screenshot
        shareScreenshot(screenshotBitmap);
    }
    // add text to ss
    private Bitmap addTextToBitmap(Bitmap bitmap, String text) {
        // Add your code here to draw the text on the bitmap
        // You can use Canvas and Paint to draw the text on the bitmap
        // For simplicity, we'll just return the original bitmap for now
        return bitmap;
    }
    //create intent to share ss
    private void shareScreenshot(Bitmap bitmap) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        String imagePath = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                "screenshot",
                "Screenshot taken with text"
        );

        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath));
        startActivity(Intent.createChooser(shareIntent, "Share Screenshot"));
    }
    // get the goal data asscoiated to the user and goal id
    private void getData() {
        progressList.clear();
        db.collection("users").document(auth.getUid()).collection("goals").document(value).collection("savings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                String date = document.getString("date");
                                String uid = document.getId();
                                double amount = document.getDouble("amount");
                                progressList.add(new IndivisualGoalI(name, amount, date, uid));
                            }
                            adapter = new Goal_Indivisual_Adapter(progressList, Goal_Progress_Individual.this);
                            recyclerViewi.setAdapter(adapter);
                        }
                    }
                });


    }
    // viewpager carousell button creation
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
    // change page and color of carousell button when clicked
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
        navigationView.setCheckedItem(R.id.nav_goal);


    }
    //confetti animation
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
    // its in the name
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
    // nav bar to show
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    // nav bar items clicked
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_add_transactions) {
            Intent intent = new Intent(Goal_Progress_Individual.this, AddTransaction.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_view_transactions) {
            Intent intent = new Intent(Goal_Progress_Individual.this, ViewAllTransactions.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_card) {
            Intent intent = new Intent(Goal_Progress_Individual.this, ViewCard.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_profile) {
            Intent intent = new Intent(Goal_Progress_Individual.this, Profile.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_about) {
            Intent intent = new Intent(Goal_Progress_Individual.this, AboutUs.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_currency) {

            Intent intent = new Intent(Goal_Progress_Individual.this, Currency.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_set_limit) {

            Intent intent = new Intent(Goal_Progress_Individual.this, SetLimit.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(Goal_Progress_Individual.this, MainActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_transfer) {
            Intent intent = new Intent(Goal_Progress_Individual.this, Transfer.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_goal) {
            Intent intent = new Intent(Goal_Progress_Individual.this, Goal_Progress_Individual.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_map) {
            Intent intent = new Intent(Goal_Progress_Individual.this, Maps.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_stats) {
            Intent intent = new Intent(Goal_Progress_Individual.this, Stats.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_cryptoTracker) {
            Intent intent = new Intent(Goal_Progress_Individual.this, CryptoTracker.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_map) {
            Intent intent = new Intent(Goal_Progress_Individual.this, Maps.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Goal_Progress_Individual.this, Login.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}


