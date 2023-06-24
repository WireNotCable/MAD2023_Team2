package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class EditTransaction extends AppCompatActivity {

    //Shared preference
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_UID = "MyUID";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        // redirect to main //
        ImageView homeBtn = findViewById(R.id.editTHomeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTransaction.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // pre fill values //
        Intent receivingEnd = getIntent();
        String id = receivingEnd.getStringExtra("Id");

        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String sharedEmail = sharedPreferences.getString(MY_UID, "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference transactionRef = db.collection("users").document(sharedEmail).collection("alltransaction").document(id);

        transactionRef.get().addOnCompleteListener(task ->{
            DocumentSnapshot document = task.getResult();
            Map<String, Object> data = document.getData();

            //Extract data//
            String title = (String) data.get("title");
            String date = (String) data.get("date");
            double amount = (double) data.get("amount");
            String type = (String) data.get("type");

            // set title
            EditText etDesc = findViewById(R.id.editTransDesc);
            etDesc.setText(title);
            // set amount
            EditText etAmt = findViewById(R.id.editTransAmount);
            etAmt.setText(String.valueOf(amount));
            // set date
            TextView tvDate = findViewById(R.id.selectDate);
            tvDate.setText(date);
            // set type
            if (type.equals("income")){
                RadioButton incomeSel = findViewById(R.id.incomeSelectedEdit);
                incomeSel.setChecked(true);
            }
            else{
                RadioButton expenseSel = findViewById(R.id.expenseSelectedEdit);
                expenseSel.setChecked(true);
            }
        });

        // send edited values to firestore //

        Button saveTrans = findViewById(R.id.saveEditBtn);
        saveTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get transaction data from text views
                TextView dateTextView = findViewById(R.id.selectDate);
                String date = dateTextView.getText().toString();
                EditText titleEt = findViewById(R.id.editTransDesc);
                String title = titleEt.getText().toString();
                EditText amtEt = findViewById(R.id.editTransAmount);
                String amountstr = amtEt.getText().toString();
                double amount = Double.parseDouble(amountstr);
                String type = "";
                RadioGroup radioGroup = findViewById(R.id.typeRadioEdit);
                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (selectedRadioButtonId == R.id.incomeSelectedEdit) {
                    type = "income";
                } else if (selectedRadioButtonId == R.id.expenseSelectedEdit) {
                    type = "expense";
                }
                if (date!=null&&!date.isEmpty() && !title.isEmpty() && !amountstr.isEmpty() && !type.isEmpty()){
                    Transaction transaction = new Transaction("", title, date, amount, type);
                    sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
                    String sharedEmail = sharedPreferences.getString(MY_UID, "");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> transactionData = new HashMap<>();
                    transactionData.put("title", transaction.getTransTitle());
                    transactionData.put("date", transaction.getTransDate());
                    transactionData.put("amount", transaction.getTransAmt());
                    transactionData.put("type", transaction.getTransType());
                    db.collection("users").document(sharedEmail).collection("alltransaction").document(id).set(transactionData);
                    Intent intent = new Intent(EditTransaction.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });


    }
}