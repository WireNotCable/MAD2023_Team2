package sg.edu.np.mad.pennywise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Transfer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    TextView balance,limit;
    AutoCompleteTextView chooseuser;
    EditText amount,comment;
    Button transfer;
    ImageView homeButton,userIcon;
    public Double userbalance = Double.valueOf(0);
    public Double userlimit;

    ArrayList<String> friendList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    public String GLOBAL_PREFS = "myPrefs";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_transfer);
        balance = findViewById(R.id.DisplayBalance);
        limit = findViewById(R.id.DisplayLimit);
        chooseuser = findViewById(R.id.DropdownChoice);
        amount = findViewById(R.id.InputAmount);
        comment = findViewById(R.id.InputComments);
        transfer = findViewById(R.id.TransferButton);

        userIcon = findViewById(R.id.UserIcon);

        userbalance = Double.valueOf(0);
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS,MODE_PRIVATE);

        getLimit();
        getBalance();

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






        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Transfer.this,Users.class);
                startActivity(intent);
            }
        });

        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendList.contains(chooseuser.getText().toString())){
                    if (Double.parseDouble(amount.getText().toString()) > 0 && Double.parseDouble(amount.getText().toString()) <= userbalance && Double.parseDouble(amount.getText().toString()) <= userlimit){
                        DecimalFormat df = new DecimalFormat("#.##");
                        df.setRoundingMode(RoundingMode.HALF_UP);
                        Double roundedValue = Double.parseDouble(df.format(Double.parseDouble(amount.getText().toString())));

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                        Date currentDate = new Date();
                        String formattedDate = dateFormat.format(currentDate);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseAuth auth = FirebaseAuth.getInstance();

                        HashMap<String,Object> transcationData = new HashMap<>();
                        transcationData.put("amount",roundedValue);
                        transcationData.put("date",formattedDate);
                        transcationData.put("title",comment.getText().toString());
                        if(comment.getText().toString().isEmpty()){
                            transcationData.put("title","Transfer");
                        }
                        transcationData.put("type","expense");
                        String Fromid = db.collection("users").document(auth.getUid()).collection("alltransaction").document().getId();
                        db.collection("users").document(auth.getUid()).collection("alltransaction").document(Fromid).set(transcationData);

                        HashMap<String,Object> transcationData2 = new HashMap<>();
                        transcationData2.put("amount",roundedValue);
                        transcationData2.put("date",formattedDate);
                        transcationData2.put("title",comment.getText().toString());
                        if(comment.getText().toString().isEmpty()){
                            transcationData2.put("title","Transfer");
                        }
                        transcationData2.put("type","income");
                        String ToUID = chooseuser.getText().toString().trim().split(",")[1];
                        String Toid = db.collection("users").document(ToUID).collection("alltransaction").document().getId();
                        db.collection("users").document(ToUID).collection("alltransaction").document(Toid).set(transcationData2);
                        Toast.makeText(Transfer.this,"Transfer successful",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Transfer.this, MainActivity.class);
                        startActivity(intent);

                    }
                    else{
                        Toast.makeText(Transfer.this,"Invalid amount entered, check if requirements fulfilled",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(Transfer.this,"Invalid friend choice",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void getFriendList() {
        friendList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.collection("users").document(auth.getUid()).collection("friendlist")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot document : task.getResult()){
                                friendList.add(document.getString("name")+","+document.getString("UID"));
                            }
                        }else{
                            chooseuser.setError("Unable to fetch users, please add some friends");
                        }

                        ArrayAdapter adapter = new ArrayAdapter(Transfer.this, android.R.layout.simple_list_item_1,friendList);
                        chooseuser.setAdapter(adapter);
                    }
                });
    }
    private void getLimit(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.collection("users").document(auth.getUid()).collection("setlimit")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot document : task.getResult()){
                                userlimit = document.getDouble("limit");
                                return;
                            }
                            if (userlimit == null){
                                limit.setText("Limit : No limit");
                                userlimit = Double.MAX_VALUE;
                            }
                            else{
                                limit.setText("Limit : $"+userlimit);
                            }
                        }
                        else{
                        }

                    }
                });
    }
    private void getBalance(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.collection("users").document(auth.getUid()).collection("alltransaction")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            double total = 0.0;
                            for(QueryDocumentSnapshot document : task.getResult()){

                                if ("expense".equals(document.getString("type"))){
                                    total -= document.getDouble("amount");


                                }
                                else if ("income".equals(document.getString("type"))){
                                    Double number = document.getDouble("amount");
                                    total += number;

                                }
                            }
                            balance = findViewById(R.id.DisplayBalance);
                            balance.setText("Balance : $"+total);
                            userbalance = total;
                        }
                    }
                });
    }
    @Override
    protected void onResume(){
        friendList.clear();
        super.onResume();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.collection("users").document(auth.getUid()).collection("friendlist")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot document : task.getResult()){
                                friendList.add(document.getString("name")+","+document.getString("UID"));
                            }
                        }else{
                            chooseuser.setError("Unable to fetch users, please add some friends");
                        }

                        ArrayAdapter adapter = new ArrayAdapter(Transfer.this, android.R.layout.simple_list_item_1,friendList);
                        chooseuser.setAdapter(adapter);
                    }
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
            Intent intent = new Intent(Transfer.this, AddTransaction.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_view_transactions){
            Intent intent = new Intent(Transfer.this, ViewAllTransactions.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_card){
            Intent intent = new Intent(Transfer.this, ViewCard.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_profile){
            Intent intent = new Intent(Transfer.this, Profile.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_about){
            Intent intent = new Intent(Transfer.this, AboutUs.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_currency) {

            Intent intent = new Intent(Transfer.this, Currency.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_home){
            Intent intent = new Intent(Transfer.this, MainActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_transfer){
            Intent intent = new Intent(Transfer.this, Transfer.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_set_limit){
            Intent intent = new Intent(Transfer.this, SetLimit.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_friends){
            Intent intent = new Intent(Transfer.this, Users.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_logout){
            sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Transfer.this,Login.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }






}