package sg.edu.np.mad.pennywise2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.np.mad.pennywise2.models.Goal;

public class Goal_Progress extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    RecyclerView recyclerView;
    Button createprogress;
    FirebaseFirestore db;
    FirebaseAuth auth;
    ArrayList<Goal> goalList;
    MainGoalProgressAdapter adapter;

    public static final String GLOBAL_PREFS = "myPrefs";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private static final int REQUEST_CODE_PERMISSIONS = 1001;

    //set up view and check permissions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_progress);

        recyclerView = findViewById(R.id.Mainprogressrecyclerviwe);
        recyclerView.setLayoutManager(new LinearLayoutManager(Goal_Progress.this));
        createprogress = findViewById(R.id.MainProgressCreate);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        goalList = new ArrayList<Goal>();

        //NAV BAR
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
        ActivityCompat.requestPermissions(Goal_Progress.this,
                new String[]{
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                REQUEST_CODE_PERMISSIONS);
        getData();
    }
    //get any data when the page is resumed
    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_goal);
        createprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });

    }
    //get goal progress data associated with the uid of the user
    private void getData() {
        goalList.clear();
        db.collection("users").document(auth.getUid()).collection("goals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult() != null){
                                QuerySnapshot list = task.getResult();
                                for (QueryDocumentSnapshot document : list){
                                    String name = document.getString("name");
                                    double amount = document.getDouble("amount");
                                    double current = document.getDouble("current");
                                    String uid = document.getId();
                                    goalList.add(new Goal(uid,name,amount,current));
                                }
                                adapter = new MainGoalProgressAdapter(goalList,Goal_Progress.this);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    }
                });

    }
    //pop up window to create a new goal
    private void createDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_progress_create, null);

        TextInputEditText name = dialogView.findViewById(R.id.CreateProgressName);
        TextInputEditText amount = dialogView.findViewById(R.id.CreateProgressAmount);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setTitle("Create Progress");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    String createname = String.valueOf(name.getText()).trim();
                    String createamount = amount.getText().toString();
                    if (!TextUtils.isEmpty(createname) && !TextUtils.isEmpty(createamount)){
                        checkname(createname, new OnQueryCompleteListener() {
                            @Override
                            public void onQueryComplete(boolean hasMatchingDocument) {
                                if (Double.parseDouble(createamount) > 0){
                                    if (hasMatchingDocument){
                                        DocumentReference ref = db.collection("users").document(auth.getUid()).collection("goals").document();
                                        HashMap<String,Object> data = new HashMap<>();
                                        data.put("name",createname);
                                        data.put("amount",Double.parseDouble(createamount));
                                        data.put("current",0);
                                        ref.set(data);
                                        Toast.makeText(Goal_Progress.this,"Progress created!", Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                        recreate();
                                    }
                                    else{
                                        Toast.makeText(Goal_Progress.this,"Progress name already exists!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                                else{
                                    Toast.makeText(Goal_Progress.this,"Goal amount must be > 0 ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialogInterface.dismiss();
                    }
                    else{
                        Toast.makeText(Goal_Progress.this,"Invalid input for name/amount", Toast.LENGTH_SHORT).show();
                    }

            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    // make sure no goal has the same name
    private void checkname(String input, OnQueryCompleteListener listener){
        db.collection("users").document(auth.getUid()).collection("goals")
                .whereEqualTo("name",input)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            boolean hasMatchingDocument = querySnapshot == null || querySnapshot.isEmpty();
                            listener.onQueryComplete(hasMatchingDocument);

                        }
                        else{
                            listener.onQueryComplete(false);
                        }
                    }
                });

    }
    //nav bar
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //navbar items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_add_transactions){
            Intent intent = new Intent(Goal_Progress.this, AddTransaction.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_view_transactions){
            Intent intent = new Intent(Goal_Progress.this, ViewAllTransactions.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_card){
            Intent intent = new Intent(Goal_Progress.this, ViewCard.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_profile){
            Intent intent = new Intent(Goal_Progress.this, Profile.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_about){
            Intent intent = new Intent(Goal_Progress.this, AboutUs.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_currency) {

            Intent intent = new Intent(Goal_Progress.this, Currency.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_set_limit) {

            Intent intent = new Intent(Goal_Progress.this, SetLimit.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_home){
            Intent intent = new Intent(Goal_Progress.this, MainActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_transfer){
            Intent intent = new Intent(Goal_Progress.this, Transfer.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_goal){
            Intent intent = new Intent(Goal_Progress.this, Goal_Progress.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_map){
            Intent intent = new Intent(Goal_Progress.this, Maps.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_stats){
            Intent intent = new Intent(Goal_Progress.this, Stats.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_cryptoTracker){
            Intent intent = new Intent(Goal_Progress.this, CryptoTracker.class);
            startActivity(intent);
        }

        else if (item.getItemId() == R.id.nav_map){
            Intent intent = new Intent(Goal_Progress.this, Maps.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_logout){
            SharedPreferences sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Goal_Progress.this,Login.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}