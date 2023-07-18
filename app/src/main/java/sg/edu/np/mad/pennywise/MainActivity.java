package sg.edu.np.mad.pennywise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sg.edu.np.mad.pennywise.models.Transaction;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ViewTransRVInterface{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView dashboardRecyclerView;
    Toolbar toolbar;
    //Shared preference
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_UID = "MyUID";

    public String MY_EXPENSE= "myExpense";

    public String MY_STARTDATE = "myStartDate";

    public String MY_ENDDATE = "myEndDate";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        dashboardRecyclerView = findViewById(R.id.dashboardRecyclerView);
        dashboardRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        setSupportActionBar(toolbar);

        // Remove title in homepage
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Nav Drawer
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //TextView monthText = findViewById(R.id.sbtextView);
        //String currentMonth = getCurrentMonthMMM();
        //monthText.setText("Your Balance : "+"("+currentMonth+")");

        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String sharedUID = sharedPreferences.getString(MY_UID, "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference nameRef = db.collection("users").document(sharedUID);
        nameRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                TextView name = findViewById(R.id.home_name);
                Object nameText = document.get("name");
                name.setText(nameText.toString());
            }
        });



        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        threeMiddleButtons();
        getBalance();
        getDashboardItems();
        viewAllTrans();
    }

    public void threeMiddleButtons(){
        ImageButton addtrans = findViewById(R.id.home_addtran);
        addtrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTransaction.class);
                startActivity(intent);
            }
        });
        ImageButton setlimit = findViewById(R.id.home_setlimit);
        setlimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SetLimit.class);
                startActivity(intent);
            }
        });
        ImageButton transfer = findViewById(R.id.home_transfer);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Transfer.class);
                startActivity(intent);
            }
        });
    }

    ArrayList<Transaction> transactionList = new ArrayList<>();
    // Get dashboard items for recycler view //
    public void getDashboardItems(){
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String sharedUID = sharedPreferences.getString(MY_UID, "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference transactionRef = db.collection("users").document(sharedUID).collection("alltransaction");
        transactionRef.get().addOnCompleteListener(task -> {
            QuerySnapshot querySnapshot = task.getResult();
            List<DocumentSnapshot> documents = querySnapshot.getDocuments();
            for (DocumentSnapshot document : documents){
                Map<String, Object> data = document.getData();

                //Extract data
                String id = (String) document.getId();
                String title = (String) data.get("title");
                String date = (String) data.get("date");
                double amount = (double) data.get("amount");
                String type = (String) data.get("type");
                String currentMonth = getCurrentMonthMMM();
                String extractMonth = extractMonthFromDate(date);
                String currentYear = getCurrentYear();
                String extractYear = extractYearFromDate(date);
                Transaction transaction = new Transaction(id, title, date, amount, type);
                if (currentMonth.equals(extractMonth) && currentYear.equals(extractYear)){
                    transactionList.add(transaction);
                }
            }
            // Sort transactionList based on date
            Collections.sort(transactionList, new Comparator<Transaction>() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

                @Override
                public int compare(Transaction t1, Transaction t2) {
                    try {
                        Date date1 = dateFormat.parse(t1.getTransDate());
                        Date date2 = dateFormat.parse(t2.getTransDate());
                        return date2.compareTo(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
            RecyclerView recyclerView = findViewById(R.id.dashboardRecyclerView);
            DashboardAdaptor dashboardAdaptor = new DashboardAdaptor(transactionList, this);
            LinearLayoutManager myLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(myLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(dashboardAdaptor);
        });
    }


    // Get balance
    public void getBalance(){
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String sharedUID = sharedPreferences.getString(MY_UID, "");
        String StartDate = sharedPreferences.getString(MY_STARTDATE,"");
        String EndDate = sharedPreferences.getString(MY_ENDDATE,"");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference transactionRef = db.collection("users").document(sharedUID).collection("alltransaction");
        transactionRef.get().addOnCompleteListener(task -> {
            QuerySnapshot querySnapshot = task.getResult();
            List<DocumentSnapshot> documents = querySnapshot.getDocuments();
            double totalBalance = 0;
            double TotalSpend = 0;
            for (DocumentSnapshot document : documents){
                Map<String, Object> data = document.getData();
                double amount = (double) data.get("amount");
                String type = (String) data.get("type");
                String date = (String) data.get("date");
                String currentMonth = getCurrentMonthMMM();
                String extractMonth = extractMonthFromDate(date);
                String currentYear = getCurrentYear();
                String extractYear = extractYearFromDate(date);
                if (currentMonth.equals(extractMonth) && currentYear.equals(extractYear)){
                    if (type.equals("income")){
                        totalBalance += amount;
                    }
                    else{
                        totalBalance -= amount;
                        if(date.compareTo(StartDate) >= 0 && date.compareTo(EndDate) <=0)
                        {
                            TotalSpend+=amount;
                        }
                    }
                }
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String roundedBalance = decimalFormat.format(totalBalance);
            SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(MY_EXPENSE,String.valueOf(TotalSpend));
            editor.commit(); // Apply the changes to SharedPreferences
            TextView balanceTxt = findViewById(R.id.balanceText);
            balanceTxt.setText("$ " + roundedBalance);
        });
    }

    // Get month in MMM format (e.g. Jun)
    private String getCurrentMonthMMM() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    // Get year in yyyy format (e.g 2023)
    private String getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    // Extract month in MMM format from the transaction date
    private String extractMonthFromDate(String dateString) {
        DateFormat inputDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        DateFormat outputDateFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        try {
            Date date = inputDateFormat.parse(dateString);
            return outputDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Extract year in yyyy format from the transaction date
    private String extractYearFromDate(String dateString) {
        DateFormat inputDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        DateFormat outputDateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        try {
            Date date = inputDateFormat.parse(dateString);
            return outputDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Redirect to View All Transaction Page
    public void viewAllTrans(){
        TextView tv = findViewById(R.id.viewAllTrans);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewAllTransactions.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }


    // NAVBAR //
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_add_transactions){
            Intent intent = new Intent(MainActivity.this, AddTransaction.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_view_transactions){
            Intent intent = new Intent(MainActivity.this, ViewAllTransactions.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_card){
            Intent intent = new Intent(MainActivity.this, ViewCard.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_profile){
            Intent intent = new Intent(MainActivity.this, Profile.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_about){
            Intent intent = new Intent(MainActivity.this, AboutUs.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_currency) {

            Intent intent = new Intent(MainActivity.this, Currency.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_set_limit){
            Intent intent = new Intent(MainActivity.this, SetLimit.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_transfer){
            Intent intent = new Intent(MainActivity.this, Transfer.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_friends){
            Intent intent = new Intent(MainActivity.this, Users.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_logout){
            sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this,Login.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Click to view individual transaction
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, ViewTransaction.class);
        intent.putExtra("From","Main");
        intent.putExtra("Id",transactionList.get(position).getTransId());
        intent.putExtra("Title",transactionList.get(position).getTransTitle());
        intent.putExtra("Amount",transactionList.get(position).getTransAmt());
        intent.putExtra("Date",transactionList.get(position).getTransDate());
        intent.putExtra("Type",transactionList.get(position).getTransType());
        startActivity(intent);
    }
}