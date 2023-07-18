package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import sg.edu.np.mad.pennywise.models.Transaction;

public class AddTransaction extends AppCompatActivity {

    //Shared preference //
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_UID = "MyUID";
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // default current date
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String date = dateFormat.format(currentDate);
        TextView dateTextView = findViewById(R.id.selectDateText);
        dateTextView.setText(date);


        // Home icon to go back to Main Page //
        ImageView homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTransaction.this, MainActivity.class);
                startActivity(intent);
            }
        });

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
}