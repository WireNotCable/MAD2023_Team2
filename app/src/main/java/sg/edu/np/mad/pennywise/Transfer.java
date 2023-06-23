package sg.edu.np.mad.pennywise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

public class Transfer extends AppCompatActivity {
    TextView balance,limit;
    AutoCompleteTextView chooseuser;
    EditText amount,comment;
    Button transfer;
    ImageView homeButton,userIcon;
    Double userbalance;
    Double userlimit;

    ArrayList<String> friendList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_EMAIL = "MyEmail";
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
        homeButton = findViewById(R.id.ButtonHome);
        userIcon = findViewById(R.id.UserIcon);

        userbalance = Double.valueOf(0);
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS,MODE_PRIVATE);

        getFriendList();
        getLimit();
        getBalance();

        if (userlimit == null){
            limit.setText("Limit : No limit");
            userlimit = Double.MAX_VALUE;
        }
        else{
            limit.setText("Limit : $"+userlimit);
        }
        balance.setText("Balance : $"+userbalance);


        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Transfer.this,Users.class);
                startActivity(intent);
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Date currentDate = new Date();
                        String formattedDate = dateFormat.format(currentDate);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        HashMap<String,Object> transcationData = new HashMap<>();
                        transcationData.put("amount",roundedValue);
                        transcationData.put("date",formattedDate);
                        transcationData.put("title",comment.getText().toString());
                        transcationData.put("type","expense");

                        Toast.makeText(Transfer.this,"Transfer successful",Toast.LENGTH_SHORT).show();
                        finish();

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.collection("users").document(auth.getUid()).collection("friendslist")
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
                        }
                        else{
                        }

                    }
                });
    }
    private void getBalance(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.collection("users").document(auth.getUid()).collection("alltranscation")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                if (document.getString("type") == "expense"){
                                    userbalance -= document.getDouble("amount");
                                }
                                else if (document.getString("type") == "income"){
                                    userbalance += document.getDouble("amount");
                                }
                            }
                        }
                    }
                });
    }





}