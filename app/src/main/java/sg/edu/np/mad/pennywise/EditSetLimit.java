package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;



import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.ProxyFileDescriptorCallback;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Map;
import java.util.HashMap;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;



import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EditSetLimit extends AppCompatActivity {

    private TextView selectedStartDate;
    private TextView selectedEndDate;

    private EditText EtSpendLimit;

    private EditText EtFallsBelow;

    //Shared preference
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_EMAIL = "MyEmail";
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_set_limit);

        Button setButton= findViewById(R.id.editsetlimit_setbutton);
        ImageView closeButton = findViewById(R.id.editsetlimit_close);
        selectedStartDate = findViewById(R.id.editsetlimit_startdate);
        selectedEndDate = findViewById(R.id.editsetlimit_enddate);
        EtSpendLimit= findViewById(R.id.editsetlimit_spendlimit);
        EtFallsBelow = findViewById(R.id.editsetlimit_warning);

        Intent intent = getIntent();
        selectedStartDate.setText(intent.getStringExtra("StartDate"));
        selectedEndDate.setText(intent.getStringExtra("EndDate"));

        selectedStartDate.setOnClickListener(new View.OnClickListener() {
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
                        selectedStartDate.setText(date);
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "tag");
            }
        });
        selectedEndDate.setOnClickListener(new View.OnClickListener() {
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
                        selectedEndDate.setText(date);

                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "tag");
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditSetLimit.this,SetLimit.class);
                startActivity(intent);
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String startDate = selectedStartDate.getText().toString();
                String endDate = selectedEndDate.getText().toString();
                Log.v("startDate", startDate);
                Log.v("startDate", endDate);



                int compareDate = startDate.compareTo(endDate);
                double spendLimit = Double.parseDouble(EtSpendLimit.getText().toString());
                double fallsbelow = Double.parseDouble(EtFallsBelow.getText().toString());
                if(compareDate < 0){
                    if(!TextUtils.isEmpty(EtSpendLimit.getText()) && !TextUtils.isEmpty(EtFallsBelow.getText())) {
                        if (spendLimit > 0 && fallsbelow > 0) {
                            sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
                            String sharedEmail = sharedPreferences.getString(MY_EMAIL, "");

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            Map<String, Object> limitData = new HashMap<>();
                            limitData.put("startdate",startDate);
                            limitData.put("enddate",endDate);
                            limitData.put("limit",spendLimit);
                            limitData.put("warning",fallsbelow);
                            String id = db.collection("users").document(sharedEmail).collection("setlimit").document().getId();//Getting Document ID
                            db.collection("users").document(sharedEmail).collection("setlimit").document(id).set(limitData, SetOptions.merge());//Set Data to Document
                            Toast.makeText(EditSetLimit.this, "Update Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditSetLimit.this, SetLimit.class);
                            startActivity(intent);
                        }
                        else if (spendLimit <= 0) {
                            EtSpendLimit.setError("Spend Limit must be greater than 0");

                        } else if (fallsbelow <= 0) {
                            EtFallsBelow.setError("Warning  must be greater than 0");
                        }
                    }
                    else if(TextUtils.isEmpty(EtSpendLimit.getText())){
                        EtSpendLimit.setError("Spend Limit must not be empty");
                    }
                    else if(TextUtils.isEmpty(EtFallsBelow.getText())){
                        EtFallsBelow.setError("Warning must no be empty");
                    }
                }
                else if(compareDate > 0){
                    selectedEndDate.setError("End Date must be greater than start date");
                }
                else{
                    selectedEndDate.setError("End Date should not equal to start date");
                    selectedStartDate.setError("Start Date should not be equals to end date");
                }

            }
        });
    }

    public String getTodaysDate(String dateType) {
        Calendar calendar = Calendar.getInstance(); // Get current date
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth;

        ArrayList<String> monthList = new ArrayList<>(Arrays.asList(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));
        if (dateType.equals("Start")) {
            dayOfMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        } else if (dateType.equals("End")) {
            dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            return "";
        }

        String selectedDate = dayOfMonth + "-" + monthList.get(month) + "-" + year;
        return selectedDate;
    }

}
