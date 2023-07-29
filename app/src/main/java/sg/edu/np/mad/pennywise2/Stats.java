package sg.edu.np.mad.pennywise2;

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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stats extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final String GLOBAL_PREFS = "myPrefs";
    public static final String MY_EMAIL = "MyEmail";

    public String MY_UID = "MyUID";
    SharedPreferences sharedPreferences;

    BarChart monthChart;
    PieChart pieChart;
    ArrayList monthlySpendArrayList;

    ArrayList<PieEntry> CategoryData;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        monthChart = findViewById(R.id.monthly_bar);
        pieChart = findViewById(R.id.category_piechart);

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

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String uid = sharedPreferences.getString(MY_UID, "");

        getMonthData(uid);

        getCurrentMonthData(uid);
    }
    private void getCurrentMonthData(String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference transactionRef = db.collection("users").document(uid).collection("alltransaction");
        transactionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> documents = querySnapshot.getDocuments();


                // Create a nested HashMap to store categories and their amounts
                HashMap<String, Double> categoryMap = new HashMap<>();
                double monthSpend = 0.0;

                for (DocumentSnapshot document : documents) {
                    Map<String, Object> data = document.getData();
                    double amount = (double) data.get("amount");
                    String type = (String) data.get("type");
                    String CTitle = (String) data.get("title");
                    String dateString = (String) data.get("date");

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

                    try {
                        Date date = sdf.parse(dateString);
                        Calendar current = Calendar.getInstance();
                        if (type.equals("expense") && date.getMonth() == current.get(Calendar.MONTH)) {
                            if (!categoryMap.containsKey(CTitle)) {
                                categoryMap.put(CTitle, amount);
                            } else {
                                double currentAmount = categoryMap.get(CTitle);
                                categoryMap.put(CTitle, currentAmount + amount);
                            }
                            monthSpend += amount;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace(); // Handle or log the exception as needed
                    }
                }

                // Once the data is retrieved, update the chart
                CategoryData = new ArrayList();
                for (String category : categoryMap.keySet()) {
                    double totalAmount = categoryMap.get(category);
                    Log.v("HEUWHE",category);
//                    CategoryData.add(new PieEntry((float)(totalAmount/monthSpend * 100),category));
                    CategoryData.add(new PieEntry((float)(totalAmount/monthSpend * 100), category));

                    Log.v("HEUWHE",String.valueOf(totalAmount +"/" +monthSpend));
                    Log.v("HEUWHE",String.valueOf((float)(totalAmount/monthSpend * 360)));

                }
                // Once the data is retrieved, update the chart
                updatePieChart();
            } else {
                Log.e("TotalExpense", "Error getting documents: ", task.getException());
            }
        });
    }
    private void getMonthData(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference transactionRef = db.collection("users").document(uid).collection("alltransaction");
        transactionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                monthlySpendArrayList = new ArrayList();

                for (int i = 0; i < 12; i++) {
                    double monthSpend = 0.0;

                    for (DocumentSnapshot document : documents) {
                        Map<String, Object> data = document.getData();
                        double amount = (double) data.get("amount");
                        String type = (String) data.get("type");
                        String dateString = (String) data.get("date");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

                        try {
                            Date date = sdf.parse(dateString);
                            Log.v("Monthas", String.valueOf(dateString));
                            // Use i + 1 since Date's getMonth() returns months starting from 0

                            if (type.equals("expense") && (date.getMonth() + 1) == i + 1) {

                                monthSpend += amount;
                            }
                        } catch (ParseException e) {

                            e.printStackTrace(); // Handle or log the exception as needed
                        }
                    }
                    if(monthSpend!=0){

                    }
                    monthlySpendArrayList.add(new BarEntry(i,(float) monthSpend));
                }



            } else {
                Log.e("TotalExpense", "Error getting documents: ", task.getException());
            }
            // Once the data is retrieved, update the chart
            updateChart();
        });
    }

    private void updateChart() {
        BarDataSet monthDataSet = new BarDataSet(monthlySpendArrayList, "Months");
        BarData barData = new BarData(monthDataSet);
        monthChart.setData(barData);

        // Label the X-axis with your desired labels
        String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        XAxis xAxis = monthChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        // Set the position of the X-axis labels to "BOTTOM"
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // Customize the Y-axis appearance
        YAxis leftYAxis = monthChart.getAxisLeft();
        leftYAxis.setDrawGridLines(false); // Remove the horizontal grid lines

        //Design the chart
        monthDataSet.setValueTextColor(Color.BLUE);
        monthDataSet.setValueTextSize(15f);
        monthChart.getDescription().setEnabled(false);
        monthChart.animateXY(1000, 1000);
        monthDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        // Refresh the chart
        monthChart.invalidate();




    }
    private void updatePieChart() {
//        Log.v("LENGTH",String.valueOf(CategoryData.size()));

        //Set Data
        PieDataSet pieDataSet = new PieDataSet(CategoryData, "Category");
        if (CategoryData.isEmpty()) {
            CategoryData.add(new PieEntry(100, "No Data"));
        }

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        //Design
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Set the selected item every time the activity is brought to the foreground
        navigationView.setCheckedItem(R.id.nav_stats);
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
        if (item.getItemId() == R.id.nav_add_transactions) {
            Intent intent = new Intent(Stats.this, AddTransaction.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_view_transactions) {
            Intent intent = new Intent(Stats.this, ViewAllTransactions.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_card) {
            Intent intent = new Intent(Stats.this, ViewCard.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_about) {
            Intent intent = new Intent(Stats.this, AboutUs.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_currency) {
            Intent intent = new Intent(Stats.this, Currency.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(Stats.this, MainActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_transfer) {
            Intent intent = new Intent(Stats.this, Transfer.class);
            startActivity(intent);
        }  else if (item.getItemId() == R.id.nav_set_limit){
            Intent intent = new Intent(Stats.this, SetLimit.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_stats){
            Intent intent = new Intent(Stats.this, Stats.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_profile){
            Intent intent = new Intent(Stats.this, Profile.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.nav_goal){
            Intent intent = new Intent(Stats.this, Profile.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_cryptoTracker){
            Intent intent = new Intent(Stats.this, CryptoTracker.class);
            startActivity(intent);
        }

        else if (item.getItemId() == R.id.nav_map){
            Intent intent = new Intent(Stats.this, Maps.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.nav_goal){
            Intent intent = new Intent(Stats.this, Goal_Progress.class);
            startActivity(intent);
        }

         else if (item.getItemId() == R.id.nav_logout) {
            sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Stats.this, Login.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
