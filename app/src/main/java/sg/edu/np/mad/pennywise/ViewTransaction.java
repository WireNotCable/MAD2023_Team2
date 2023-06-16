package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ViewTransaction extends AppCompatActivity {

    //Shared preference
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_EMAIL = "MyEmail";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transaction);
        getData();

        TextView canceltv = findViewById(R.id.closeIndiv);

        Intent receivingEnd = getIntent();
        String from = receivingEnd.getStringExtra("From");

        // call cancel method //
        canceltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Redirect(from);
            }
        });

        // call delete method //
        TextView deletetv = findViewById(R.id.transDelete);
        deletetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
                Redirect(from);
            }
        });

        // call edit method //
        TextView edittv = findViewById(R.id.transEdit);
        edittv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
                Redirect(from);
            }
        });

        // return to home
        ImageView homeBtn = findViewById(R.id.viewTHomeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewTransaction.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Get transaction details
    public void getData(){
        Intent receivingEnd = getIntent();
        TextView titleTV = findViewById(R.id.transTitle);
        titleTV.setText(receivingEnd.getStringExtra("Title"));
        TextView amtTV = findViewById(R.id.transAmt);
        amtTV.setText("$"+String.valueOf(receivingEnd.getDoubleExtra("Amount",0.0)));
        TextView dateTV = findViewById(R.id.transDate);
        dateTV.setText(receivingEnd.getStringExtra("Date"));
        TextView typeTV = findViewById(R.id.transType);
        typeTV.setText(receivingEnd.getStringExtra("Type"));
        if (typeTV.getText().toString().equals("income")){
            amtTV.setTextColor(Color.parseColor("#06A94D"));
        }
        else{
            amtTV.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    public void delete(){
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String sharedEmail = sharedPreferences.getString(MY_EMAIL, "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent receivingEnd = getIntent();
        String id = receivingEnd.getStringExtra("Id");

        DocumentReference transactionRef = db.collection("users").document(sharedEmail).collection("alltransaction").document(id);
        transactionRef.delete().addOnCompleteListener(task -> {
        });
    }

    public void edit(){
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String sharedEmail = sharedPreferences.getString(MY_EMAIL, "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent receivingEnd = getIntent();
        String id = receivingEnd.getStringExtra("Id");
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("title", receivingEnd.getStringExtra("Title"));
        transactionData.put("date", receivingEnd.getStringExtra("Date"));
        transactionData.put("amount", receivingEnd.getStringExtra("Amount"));
        transactionData.put("type", receivingEnd.getStringExtra("Type"));
        db.collection("users").document(sharedEmail).collection("alltransaction").document(id).set(transactionData);
    }

    public void Redirect(String from){
        if (from.equals("Main")){
            Intent intent = new Intent(ViewTransaction.this, MainActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(ViewTransaction.this, ViewAllTransactions.class);
            startActivity(intent);
        }
    }



}