package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddTransaction extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    //Shared preference //
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_EMAIL = "MyEmail";
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
                if (amountstr.isEmpty()) {
                    return;
                }
                double amount = 0.0;
                if (TextUtils.isDigitsOnly(amountstr)) {
                    try {
                        amount = Double.parseDouble(amountstr);
                    } catch (NumberFormatException e) {
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

                // Send data to firestore
                if (date!=null&&!date.isEmpty() && !title.isEmpty() && !amountstr.isEmpty() && !type.isEmpty()){
                    Transaction transaction = new Transaction("", title, date, amount, type);
                    sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
                    String sharedEmail = sharedPreferences.getString(MY_EMAIL, "");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> transactionData = new HashMap<>();
                    transactionData.put("title", transaction.getTransTitle());
                    transactionData.put("date", transaction.getTransDate());
                    transactionData.put("amount", transaction.getTransAmt());
                    transactionData.put("type", transaction.getTransType());
                    //HERE
                    String id = db.collection("users").document(sharedEmail).collection("alltransactions").document().getId();
                    db.collection("users").document(sharedEmail).collection("alltransaction").document(id).set(transactionData);
                    Intent intent = new Intent(AddTransaction.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}