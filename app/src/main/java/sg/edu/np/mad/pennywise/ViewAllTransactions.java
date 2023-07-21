package sg.edu.np.mad.pennywise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import sg.edu.np.mad.pennywise.models.Transaction;

public class ViewAllTransactions extends AppCompatActivity implements ViewTransRVInterface, NavigationView.OnNavigationItemSelectedListener{
    //Shared preference
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_UID = "MyUID";
    SharedPreferences sharedPreferences;

    AppCompatRadioButton radioAllBtn, radioExpenseBtn, radioIncomeBtn;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_transactions);

        //FOR NAV BAR
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


        radioAllBtn = findViewById(R.id.radioAllBtn);
        radioExpenseBtn = findViewById(R.id.radioExpenseBtn);
        radioIncomeBtn = findViewById(R.id.radioIncomeBtn);

        getTransData("all");

        // Search //
        SearchView searchView = findViewById(R.id.search);
        searchView.setFocusable(true);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        search();

        // Home icon to go back to Main Page

    }

    ArrayList<Transaction> transactionList = new ArrayList<>();
    // Get transaction data //
    public void getTransData(String typeSelected){
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String sharedUID = sharedPreferences.getString(MY_UID, "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference transactionRef = db.collection("users").document(sharedUID).collection("alltransaction");
        transactionRef.get().addOnCompleteListener(task -> {
            QuerySnapshot querySnapshot = task.getResult();
            List<DocumentSnapshot> documents = querySnapshot.getDocuments();
            for (DocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();

                //Extract data
                String id = (String) document.getId();
                String title = (String) data.get("title");
                String date = (String) data.get("date");
                double amount = (double) data.get("amount");
                String type = (String) data.get("type");

                // check for type selected
                if (typeSelected.equals("expense")){
                    if (type.equals("expense")){
                        Transaction transaction = new Transaction(id, title, date, amount, type);
                        transactionList.add(transaction);
                    }
                }
                else if (typeSelected.equals("income")){
                    if (type.equals("income")){
                        Transaction transaction = new Transaction(id, title, date, amount, type);
                        transactionList.add(transaction);
                    }
                }
                else{
                    Transaction transaction = new Transaction(id, title, date, amount, type);
                    transactionList.add(transaction);
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
                recycler(transactionList);
            }
        });
    }

    // Recycler View
    public void recycler(ArrayList<Transaction> transactionList){
        RecyclerView recyclerView = findViewById(R.id.viewAllTransRecycler);
        DashboardAdaptor dashboardAdaptor = new DashboardAdaptor(transactionList, this);
        LinearLayoutManager myLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(myLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(dashboardAdaptor);
    }

    // Radio Buttons
    public void onRadioButtonClicked(View v){
        transactionList.clear();
        boolean isSelected = ((AppCompatRadioButton)v).isChecked();
        if (v.getId() == R.id.radioAllBtn) {
            if(isSelected){
                radioAllBtn.setTextColor(Color.WHITE);
                radioExpenseBtn.setTextColor(Color.parseColor("#4465DA"));
                radioIncomeBtn.setTextColor(Color.parseColor("#4465DA"));
                getTransData("all");
                SearchView sv = findViewById(R.id.search);
                sv.setQuery("", false);
            }
        }
        else if (v.getId() == R.id.radioIncomeBtn){
            if(isSelected){
                radioExpenseBtn.setTextColor(Color.parseColor("#4465DA"));
                radioAllBtn.setTextColor(Color.parseColor("#4465DA"));
                radioIncomeBtn.setTextColor(Color.WHITE);
                getTransData("income");
                SearchView sv = findViewById(R.id.search);
                sv.setQuery("", false);
            }
        }
        else if (v.getId() == R.id.radioExpenseBtn){
            if(isSelected){

                radioExpenseBtn.setTextColor(Color.WHITE);
                radioAllBtn.setTextColor(Color.parseColor("#4465DA"));
                radioIncomeBtn.setTextColor(Color.parseColor("#4465DA"));
                getTransData("expense");
                SearchView sv = findViewById(R.id.search);
                sv.setQuery("", false);
            }
        }
    }

    // Click to view individual transaction //
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(ViewAllTransactions.this, ViewTransaction.class);
        intent.putExtra("From","ViewAll");
        intent.putExtra("Id",transactionList.get(position).getTransId());
        intent.putExtra("Title",transactionList.get(position).getTransTitle());
        intent.putExtra("Amount",transactionList.get(position).getTransAmt());
        intent.putExtra("Date",transactionList.get(position).getTransDate());
        intent.putExtra("Type",transactionList.get(position).getTransType());
        Log.v("hmm","Item clicked, Intent send from ViewAllTransactions");
        startActivity(intent);
    }

    // Filter data based on query //
    private void filter(String text){
        ArrayList<Transaction> filterList = new ArrayList<>();
        for (Transaction trans : transactionList){
            if(trans.getTransTitle().toLowerCase().contains(text.toLowerCase())){
                filterList.add(trans);
            }
        }
        recycler(filterList);
    }

    // Listen to query //
    private Boolean search(){
        SearchView sv = findViewById(R.id.search);
        sv.clearFocus();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        return null;
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Set the selected item every time the activity is brought to the foreground
        navigationView.setCheckedItem(R.id.nav_view_transactions);
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
            Intent intent = new Intent(ViewAllTransactions.this, AddTransaction.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_view_transactions){
            Intent intent = new Intent(ViewAllTransactions.this, ViewAllTransactions.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_card){
            Intent intent = new Intent(ViewAllTransactions.this, ViewCard.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_profile){
            Intent intent = new Intent(ViewAllTransactions.this, Profile.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_about){
            Intent intent = new Intent(ViewAllTransactions.this, AboutUs.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_currency) {

            Intent intent = new Intent(ViewAllTransactions.this, Currency.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_home){
            Intent intent = new Intent(ViewAllTransactions.this, MainActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_transfer){
            Intent intent = new Intent(ViewAllTransactions.this, Transfer.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_set_limit){
            Intent intent = new Intent(ViewAllTransactions.this, SetLimit.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_friends){

        }
        else if (item.getItemId() == R.id.nav_logout){
            sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ViewAllTransactions.this,Login.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


}