package sg.edu.np.mad.pennywise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import sg.edu.np.mad.pennywise.models.Transaction;

public class AddTransaction extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Shared preference //
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_UID = "MyUID";
    SharedPreferences sharedPreferences;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

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


        // default current date
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String date = dateFormat.format(currentDate);
        TextView dateTextView = findViewById(R.id.selectDateText);
        dateTextView.setText(date);


        // Home icon to go back to Main Page //


        // Date Picker //
        MaterialButton button = findViewById(R.id.datePicker);
        TextView textView = findViewById(R.id.selectDateText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date = new SimpleDateFormat("dd-MMM-yyy", Locale.getDefault()).format(new Date(selection));
                        textView.setText(date);

                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "tag");
            }
        });

        Button saveTrans = findViewById(R.id.saveTransBtn);

        // Send data to firestore //
        saveTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get transaction data from text views
                TextView dateTextView = findViewById(R.id.selectDateText);
                String date = dateTextView.getText().toString();
                EditText titleEt = findViewById(R.id.addTransDesc);
                String title = titleEt.getText().toString();
                EditText amtEt = findViewById(R.id.addTransAmount);
                String amountstr = amtEt.getText().toString();

                // data validation
                if (title.isEmpty()) {
                    titleEt.setError("Description cannot be empty!");
                    return;
                }
                double amount = 0.0;
                if (amountstr.isEmpty()) {
                    amtEt.setError("Description cannot be empty!");
                    return;
                }
                else{
                    try {
                        amount = Double.parseDouble(amountstr);
                    } catch (NumberFormatException e) {
                        amtEt.setError("Invalid amount!");
                        return;
                    }
                }
                String type = "";
                RadioGroup radioGroup = findViewById(R.id.typeRadio);
                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (selectedRadioButtonId == R.id.incomeSelected) {
                    type = "income";
                } else if (selectedRadioButtonId == R.id.expenseSelected) {
                    type = "expense";
                }
                else{
                    return;
                }

                // Send data to firestore
                if (date!=null&&!date.isEmpty() && !title.isEmpty() && !amountstr.isEmpty() && !type.isEmpty()){
                    Transaction transaction = new Transaction("", title, date, amount, type);
                    sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
                    String sharedUID = sharedPreferences.getString(MY_UID, "");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> transactionData = new HashMap<>();
                    transactionData.put("title", transaction.getTransTitle());
                    transactionData.put("date", transaction.getTransDate());
                    transactionData.put("amount", transaction.getTransAmt());
                    transactionData.put("type", transaction.getTransType());
                    //HERE
                    String id = db.collection("transaction").document(sharedUID).collection("alltransactions").document().getId();
                    db.collection("users").document(sharedUID).collection("alltransaction").document(id).set(transactionData);
                    Intent intent = new Intent(AddTransaction.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set the selected item every time the activity is brought to the foreground
        navigationView.setCheckedItem(R.id.nav_add_transactions);
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
            Intent intent = new Intent(AddTransaction.this, AddTransaction.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_view_transactions){
            Intent intent = new Intent(AddTransaction.this, ViewAllTransactions.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_card){
            Intent intent = new Intent(AddTransaction.this, ViewCard.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_profile){
            Intent intent = new Intent(AddTransaction.this, Profile.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_about){
            Intent intent = new Intent(AddTransaction.this, AboutUs.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_currency) {

            Intent intent = new Intent(AddTransaction.this, Currency.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_home){
            Intent intent = new Intent(AddTransaction.this, MainActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_transfer){
            Intent intent = new Intent(AddTransaction.this, Transfer.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_set_limit){
            Intent intent = new Intent(AddTransaction.this, SetLimit.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_friends){
            Intent intent = new Intent(AddTransaction.this, Users.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_logout){
            sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AddTransaction.this,Login.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}