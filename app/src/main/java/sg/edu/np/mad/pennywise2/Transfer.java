package sg.edu.np.mad.pennywise2;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import sg.edu.np.mad.pennywise2.models.User;

public class Transfer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    Button confirm;
    TextView name,changelimit,limitamount,remaininggamount,mobile;
    LinearLayout amountLayout,limitLayout,commentLayout;
    EditText comment,amount,title;
    FirebaseAuth auth;
    FirebaseFirestore db;

    SharedPreferences sharedPreferences;
    public String GLOBAL_PREFS = "myPrefs";
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int REQUEST_CODE_CONTACTS_PERMISSION = 1001;


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    //oncreate setting views and checking permissions manifest

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_transfer);

        // Remove title in homepage
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        sharedPreferences = getSharedPreferences(GLOBAL_PREFS,MODE_PRIVATE);

        confirm  = findViewById(R.id.confirm);
        name = findViewById(R.id.Name);
        changelimit = findViewById(R.id.changelimit);
        amountLayout = findViewById(R.id.AmountLayout);
        limitLayout = findViewById(R.id.LimitLayout);
        commentLayout = findViewById(R.id.CommentLayout);
        comment = findViewById(R.id.comment);
        amount = findViewById(R.id.amount);
        mobile = findViewById(R.id.mobile);
        limitamount = findViewById(R.id.textView21);
        remaininggamount = findViewById(R.id.textView22);
//        title = findViewById(R.id.editTextText3);
//        title.setFocusable(false);
//        title.setFocusableInTouchMode(false);

        //FOR NAV BAR
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        mobile.setText("Mobile");
        amount.setText("0.00");
        comment.setText("Amount Transfer");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_CONTACTS_PERMISSION);
        }

    }

    //extra code for onresume
    @Override
    protected void onResume(){
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_transfer);
        name.setVisibility(View.GONE);
        amountLayout.setVisibility(View.GONE);
        limitLayout.setVisibility(View.GONE);
        changelimit.setVisibility(View.GONE);
        commentLayout.setVisibility(View.GONE);
        confirm.setVisibility(View.GONE);
        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserListDialog();
            }
        });
        changelimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Transfer.this,SetLimit.class);
                startActivity(intent);
            }
        });
        //double check user exists and adding data to receiver and sender
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phonenum = String.valueOf(mobile.getText());
                double transferamt = Double.parseDouble(String.valueOf(amount.getText()));
                if (transferamt == 0){
                    Toast.makeText(getApplicationContext(),"Amount cannot be 0",Toast.LENGTH_SHORT).show();
                }
                else {
                    String transfercomment = String.valueOf(comment.getText());
                    checkUserExists(phonenum, new UserExistsCallBack() {
                        @Override
                        public void onUserExists(boolean exists, String name, String uid) {
                            if (exists) {
                                CollectionReference alltranscationref = db.collection("users").document(uid).collection("alltransaction");
                                DocumentReference newTranscationRef = alltranscationref.document();
                                Map<String, Object> transcationData = new HashMap<>();
                                transcationData.put("amount", transferamt);
                                Date currentDate = new Date();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                transcationData.put("date", dateFormat.format(currentDate));
                                transcationData.put("title", transfercomment);
                                transcationData.put("type", "income");
                                newTranscationRef.set(transcationData);

                                CollectionReference fromtranscationref = db.collection("users").document(auth.getUid()).collection("alltransaction");
                                DocumentReference fromTranscationref = fromtranscationref.document();
                                Map<String, Object> transcationData2 = new HashMap<>();
                                transcationData2.put("amount", transferamt);
                                transcationData2.put("date", dateFormat.format(currentDate));
                                transcationData2.put("title", transfercomment);
                                transcationData2.put("type", "expense");
                                fromTranscationref.set(transcationData2);
                                Toast.makeText(getApplicationContext(),"Transfer success",Toast.LENGTH_SHORT).show();
                                recreate();
                            }
                        }
                    });
                }
                //access manifest to gain user contact
            }
        });
        //make sure amount entered always 2dp
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action needed before text changed.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = charSequence.toString();

                int decimalIndex = input.indexOf(".");
                if (decimalIndex != -1 && input.length() - decimalIndex > 3) {
                    input = input.substring(0, decimalIndex + 3);
                    amount.setText(input);
                    amount.setSelection(input.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed after text changed.
            }
        });

    }
    //show pop up of user contacts + 3 fake contacts from user phone list
    private void showUserListDialog(){
        ArrayList<User> userList= getUserList();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_transfer, null);
        builder.setView(dialogView);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_users);
        EditText searchbar = dialogView.findViewById(R.id.editTextSearch);
        AlertDialog alertDialog = builder.create();
        UserAdapter userAdapter = new UserAdapter(userList, this);
        userAdapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                mobile.setText(user.getNumber());
                checkUserExists(user.getNumber(), new UserExistsCallBack() {
                    @Override
                    public void onUserExists(boolean exists, String Name, String uid) {
                        if (exists) {
                            name.setText(Name);
                            getLimit(new SetLimitCallback() {
                                @Override
                                public void onSetLimitData(String startDate, String endDate, double limit) {
                                     if (limit != Double.MAX_VALUE){
                                        limitamount.setText(String.valueOf(limit));
                                        getRemaining(startDate,endDate, new RemainingCallback(){
                                            @Override
                                            public void onSetLimitData(double remain) {
                                                if (remain != Double.MAX_VALUE){
                                                    if (limit - remain > 0){
                                                        remaininggamount.setText(String.valueOf(limit-remain));
                                                    }
                                                    else{
                                                        remaininggamount.setText(String.valueOf(0));
                                                    }
                                                }
                                            }
                                        });
                                     }
                                     else{
                                         limitamount.setText("No Limit");
                                         remaininggamount.setText("Unlimited");

                                     }
                                }
                            });
                            name.setVisibility(View.VISIBLE);
                            amountLayout.setVisibility(View.VISIBLE);
                            limitLayout.setVisibility(View.VISIBLE);
                            changelimit.setVisibility(View.VISIBLE);
                            commentLayout.setVisibility(View.VISIBLE);
                            confirm.setVisibility(View.VISIBLE);



                        } else {
                           showConfirmationDialog();
                        }
                    }
                });
                alertDialog.dismiss();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(Transfer.this));
        recyclerView.setAdapter(userAdapter);
        //search bar to filter all contacts
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                userAdapter.filterList(query);
            }
        });
        alertDialog.show();



    }
    //make sure the amount transfered is not over the limit set from another function
    private void getLimit(SetLimitCallback callback){
        db.collection("users").document(auth.getUid()).collection("setlimit")
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if  (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()){
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                String startDate = documentSnapshot.getString("startdate");
                                String endDate = documentSnapshot.getString("enddate");
                                double limit = documentSnapshot.getDouble("limit");
                                callback.onSetLimitData(startDate,endDate,limit);
                            }
                            else{
                                callback.onSetLimitData(null,null,Double.MAX_VALUE);
                            }
                        }
                        else{
                            callback.onSetLimitData(null,null,Double.MAX_VALUE);
                        }
                    }
                });
    }
    // make sure the user does not go over the spending limit set within the date range
    private void getRemaining(String startdate,String enddate,RemainingCallback callback){
        Date start = null;
        Date end = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        try{
            start = dateFormat.parse(startdate);
            end = dateFormat.parse(enddate);
        }
        catch (ParseException e){
            callback.onSetLimitData(Double.MAX_VALUE);
        }
        Date finalStart = start;
        Date finalEnd = end;
        db.collection("users").document(auth.getUid()).collection("alltransaction")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            double amount = 0;
                            for (QueryDocumentSnapshot document : task.getResult()){
                                Date date = new Date();
                                String type = document.getString("type");
                                double transcationamt = document.getDouble("amount");
                                try{
                                    date = dateFormat.parse(document.getString("date"));
                                }
                                catch (ParseException e){
                                    callback.onSetLimitData(Double.MAX_VALUE);
                                }
                                if (date != null && date.compareTo(finalStart) >= 0 && date.compareTo(finalEnd) <= 0 && type == "expense") {
                                    amount += transcationamt;
                                }

                            }
                            callback.onSetLimitData(amount);
                        }
                        else{
                            callback.onSetLimitData(Double.MAX_VALUE);
                        }
                    }
                });

    }
    // if user transferring to does not have a registered account pop up show to share the app
    private void showConfirmationDialog() {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("User 404");
        builder2.setMessage("This user does not have an account with us, would you like to share our app?");
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Download Pennywise! https://play.google.com/store/apps/details?id=sg.edu.np.mad.pennywise2");
                intent.setType("text/plain");

                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
            }
        });
        builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder2.setCancelable(false);
        builder2.show();
    }
    //get the user list by checking for permissions to access contacts and then adding to userList (3 fake data for testing)
    private ArrayList<User> getUserList(){
        ArrayList<User> userList = new ArrayList<User>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            // Read the contacts
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // Get the contact name
                    String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    // Get the contact phone number (if available)
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))},
                                null
                        );

                        if (phoneCursor != null && phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            // Add the contact name and phone number to the userList
                            userList.add(new User(contactName, phoneNumber));
                        }

                        if (phoneCursor != null) {
                            phoneCursor.close();
                        }
                    }
                }
            }

            if (cursor != null) {
                cursor.close();
            }
        }
        userList.add(new User("test1","12345678"));
        userList.add(new User("test2","23456789"));
        userList.add(new User("kohwenbin777@gmail.com","98578287"));
        return userList;
    }
    //check if user exists in the firebase via contact number
    private boolean checkUserExists(String number,UserExistsCallBack callback){
        db.collection("users")
                .whereEqualTo("phonenum",number)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            boolean exists = !task  .getResult().isEmpty();
                            if (exists){
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                String name = document.getString("name");
                                String uid = document.getString("UID");
                                callback.onUserExists(true,name,uid);
                            }
                            else{
                                callback.onUserExists(false,null,null);
                            }

                        }
                        else{
                            callback.onUserExists(false,null,null);
                        }

                    }
                });
        return false;
    }

    // for nav bar to show

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //when certain nav bar item is clicked
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
        else if (item.getItemId() == R.id.nav_stats){
            Intent intent = new Intent(Transfer.this, Stats.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_goal){
            Intent intent = new Intent(Transfer.this, Goal_Progress.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_cryptoTracker){
            Intent intent = new Intent(Transfer.this, CryptoTracker.class);
            startActivity(intent);
        }

        else if (item.getItemId() == R.id.nav_map){
            Intent intent = new Intent(Transfer.this, Maps.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_map){
            Intent intent = new Intent(Transfer.this, Maps.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_stats){
            Intent intent = new Intent(Transfer.this, Stats.class);
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