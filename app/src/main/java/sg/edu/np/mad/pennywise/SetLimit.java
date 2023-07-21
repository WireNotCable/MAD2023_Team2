package sg.edu.np.mad.pennywise;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import sg.edu.np.mad.pennywise.models.LimitObject;

public class SetLimit extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String GLOBAL_PREFS = "myPrefs";
    public static final String MY_EMAIL = "MyEmail";
    public String MY_EXPENSE = "myExpense";
    public String MY_STARTDATE = "myStartDate";


    public String MY_UID = "MyUID";


    public String MY_ENDDATE = "myEndDate";
    SharedPreferences sharedPreferences;

    private ImageView homeBtn;
    private ImageView EditLimit;
    private TextView StartDate;
    private TextView EndDate;
    private ProgressBar progressBar;
    private TextView AvailableBalance;



    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_limit);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String sharedEmail = sharedPreferences.getString(MY_EMAIL, "");


        String uid = sharedPreferences.getString(MY_UID,"");
        String Expense = sharedPreferences.getString(MY_EXPENSE,"");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference LimitRef = db.collection("users").document(uid).collection("setlimit");
        LimitRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    LimitObject limit = new LimitObject(getTodaysDate("Start"), getTodaysDate("End"), 1500, 500);
                    for (DocumentSnapshot document : documents) {
                        Map<String, Object> data = document.getData();
                        if (data != null) {
                            String Getstartdate = (String) data.get("startdate");
                            String Getenddate = (String) data.get("enddate");
                             double getLimit = ((Number) data.get("limit")).doubleValue();
                             double getFallsBelow = ((Number) data.get("warning")).doubleValue();
                            limit = new LimitObject(Getstartdate, Getenddate, getLimit, getFallsBelow);
                        }
                    }
                    if (limit != null) {
                        StartDate.setText(limit.getStartdate());
                        EndDate.setText(limit.getEnddate());
//                        FallsBelow.setText("$" + String.valueOf(limit.getFallsbelow()));


                        GetTotalExpense(uid,limit.getStartdate(),limit.getEnddate(),limit.getSpendlimit(),limit.getFallsbelow());

                        SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(MY_STARTDATE, limit.getStartdate());
                        editor.putString(MY_ENDDATE, limit.getEnddate());
                        editor.apply();
                    }
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Set the selected item every time the activity is brought to the foreground
        navigationView.setCheckedItem(R.id.nav_set_limit);
    }


    @Override
    protected void onStart() {
        super.onStart();
        StartDate = findViewById(R.id.limit_startdate);
        EndDate = findViewById(R.id.limit_enddate);
        AvailableBalance = findViewById(R.id.balanceText);
        progressBar = findViewById(R.id.setlimit_progressBar);
        progressBar.setIndeterminate(false);




        EditLimit = findViewById(R.id.limit_icon);
        EditLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetLimit.this, EditSetLimit.class);
                intent.putExtra("StartDate", StartDate.getText().toString());
                intent.putExtra("EndDate", EndDate.getText().toString());

//                intent.putExtra("SpendLimit", SpendLimit);
//                intent.putExtra("FallsBelow", FallsBelow);
                startActivity(intent);
                finish();
            }
        });
    }

    public String getTodaysDate(String dateType) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth;

        ArrayList<String> monthList = new ArrayList<>(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));
        if (dateType.equals("Start")) {
            dayOfMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        } else if (dateType.equals("End")) {
            dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            return "";
        }

        String selectedDate = dayOfMonth + "-" + monthList.get(month) + "-" + year;
        return selectedDate;
    }

    private void GetTotalExpense(String uid, String StartDate, String EndDate, double spendlimit, double fallsbelow) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference transactionRef = db.collection("users").document(uid).collection("alltransaction");
        transactionRef.get().addOnCompleteListener(task -> {
            double totalSpend = 0.0;
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                for (DocumentSnapshot document : documents) {
                    Map<String, Object> data = document.getData();
                    double amount = (double) data.get("amount");
                    String type = (String) data.get("type");
                    String date = (String) data.get("date");
                    if (type.equals("expense")) {
                        if (date.compareTo(StartDate) >= 0 && date.compareTo(EndDate) <= 0) {
                            totalSpend += amount;
                        }
                    }
                }
            } else {
                Log.e("TotalExpense", "Error getting documents: ", task.getException());
            }
            int progress = (int)(Math.ceil(totalSpend/spendlimit *100 ) );
            progressBar.setProgress(progress);
            Log.v("Progress",String.valueOf(progress));
            double balance = spendlimit - totalSpend;
            if (balance < fallsbelow || totalSpend > spendlimit) {
                AvailableBalance.setTextColor(Color.RED);
                AvailableBalance.setError("You have reached your spending limit");
            }
            AvailableBalance.setText(String.valueOf(balance) + " ");
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_add_transactions){
            Intent intent = new Intent(SetLimit.this, AddTransaction.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_view_transactions){
            Intent intent = new Intent(SetLimit.this, ViewAllTransactions.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_card){
            Intent intent = new Intent(SetLimit.this, ViewCard.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_profile){
            Intent intent = new Intent(SetLimit.this, Profile.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_about){
            Intent intent = new Intent(SetLimit.this, AboutUs.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_currency) {

            Intent intent = new Intent(SetLimit.this, Currency.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_home){
            Intent intent = new Intent(SetLimit.this, MainActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_transfer){
            Intent intent = new Intent(SetLimit.this, Transfer.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_friends){
            Intent intent = new Intent(SetLimit.this, Users.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_logout){
            sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SetLimit.this,Login.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
