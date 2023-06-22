package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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

public class ViewAllTransactions extends AppCompatActivity implements ViewTransRVInterface{
    //Shared preference
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_EMAIL = "MyEmail";
    SharedPreferences sharedPreferences;

    AppCompatRadioButton radioAllBtn, radioExpenseBtn, radioIncomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_transactions);

        radioAllBtn = findViewById(R.id.radioAllBtn);
        radioExpenseBtn = findViewById(R.id.radioExpenseBtn);
        radioIncomeBtn = findViewById(R.id.radioIncomeBtn);

        getTransData("all");

        // Search //
        SearchView searchView = findViewById(R.id.search);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocus();
        search();

        // Home icon to go back to Main Page
        ImageView homeBtn = findViewById(R.id.allHomeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAllTransactions.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    ArrayList<Transaction> transactionList = new ArrayList<>();
    // Get transaction data //
    public void getTransData(String typeSelected){
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String sharedEmail = sharedPreferences.getString(MY_EMAIL, "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference transactionRef = db.collection("users").document(sharedEmail).collection("alltransaction");
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
                radioExpenseBtn.setTextColor(Color.RED);
                radioIncomeBtn.setTextColor(Color.RED);
                getTransData("all");
                SearchView sv = findViewById(R.id.search);
                sv.setQuery("", false);
            }
        }
        else if (v.getId() == R.id.radioIncomeBtn){
            if(isSelected){
                radioExpenseBtn.setTextColor(Color.RED);
                radioAllBtn.setTextColor(Color.RED);
                radioIncomeBtn.setTextColor(Color.WHITE);
                getTransData("income");
                SearchView sv = findViewById(R.id.search);
                sv.setQuery("", false);
            }
        }
        else if (v.getId() == R.id.radioExpenseBtn){
            if(isSelected){

                radioExpenseBtn.setTextColor(Color.WHITE);
                radioAllBtn.setTextColor(Color.RED);
                radioIncomeBtn.setTextColor(Color.RED);
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
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        return null;
    }
}