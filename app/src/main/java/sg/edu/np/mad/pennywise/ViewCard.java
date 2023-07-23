package sg.edu.np.mad.pennywise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sg.edu.np.mad.pennywise.models.Card;

public class ViewCard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String title = "View Card";
    public String GLOBAL_PREFS = "myPrefs";
    SharedPreferences sharedPreferences;
    //calls get card details method, home button and add new card button

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card);
        getCardDetails();

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


        Button AddBtn = findViewById(R.id.AddCardBtn);
        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewCard.this, addNewCard.class);
                startActivity(intent);
            }
        });



    }
    //get the card details by accessing the firebase data
    public void getCardDetails(){
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        CollectionReference transactionRef = db.collection("users").document(auth.getUid()).collection("addCard");
        transactionRef.get().addOnCompleteListener(task -> {
            QuerySnapshot querySnapshot = task.getResult();
            List<DocumentSnapshot> documents = querySnapshot.getDocuments();
            ArrayList<Card> cardList = new ArrayList<>();
            for (DocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();

                //Extract data
                String Number = (String) data.get("number");
                String Exp = (String) data.get("exp");
                String CSV = (String) data.get("csv");
                String name = (String) data.get("name");
                String address = (String) data.get("address");
                double balance = document.getDouble("balance");
                Card card = new Card(Number, Exp, CSV, name, address,balance);
                cardList.add(card);
            }
            //configuring recycler view
            RecyclerView recyclerView = findViewById(R.id.CardViewing);
            viewCardAdapter ViewCardAdapter = new viewCardAdapter(cardList);
            LinearLayoutManager myLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(myLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(ViewCardAdapter);
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Set the selected item every time the activity is brought to the foreground
        navigationView.setCheckedItem(R.id.nav_card);
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
            Intent intent = new Intent(ViewCard.this, AddTransaction.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_view_transactions){
            Intent intent = new Intent(ViewCard.this, ViewAllTransactions.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_card){
            Intent intent = new Intent(ViewCard.this, ViewCard.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_profile){
            Intent intent = new Intent(ViewCard.this, Profile.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_about){
            Intent intent = new Intent(ViewCard.this, AboutUs.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_currency) {

            Intent intent = new Intent(ViewCard.this, Currency.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_home){
            Intent intent = new Intent(ViewCard.this, MainActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_transfer){
            Intent intent = new Intent(ViewCard.this, Transfer.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_set_limit){
            Intent intent = new Intent(ViewCard.this, SetLimit.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_goal){
            Intent intent = new Intent(ViewCard.this, Goal_Progress.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_map){
            Intent intent = new Intent(ViewCard.this, Maps.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_stats){
            Intent intent = new Intent(ViewCard.this, Stats.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_logout){
            sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ViewCard.this,Login.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}