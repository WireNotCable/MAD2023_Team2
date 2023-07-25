package sg.edu.np.mad.pennywise2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewTransaction extends AppCompatActivity {

    //Shared preference
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_UID = "MyUID";
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewTransaction.this);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Are you sure you want to delete?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call the delete method here after the user confirms the deletion
                        delete();
                        Redirect(from);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing when the user cancels the deletion
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // call edit method //
        TextView edittv = findViewById(R.id.transEdit);
        edittv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
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

    // Delete transaction
    public void delete(){
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String sharedEmail = sharedPreferences.getString(MY_UID, "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent receivingEnd = getIntent();
        String id = receivingEnd.getStringExtra("Id");

        DocumentReference transactionRef = db.collection("users").document(sharedEmail).collection("alltransaction").document(id);
        transactionRef.delete().addOnCompleteListener(task -> {
        });
    }

    // Edit transaction
    public void edit(){
        Intent receivingEnd = getIntent();
        String id = receivingEnd.getStringExtra("Id");
        Intent intent = new Intent(ViewTransaction.this, EditTransaction.class);
        intent.putExtra("Id", id);
        startActivity(intent);
    }

    // Go back to Main page or View All Transaction page
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