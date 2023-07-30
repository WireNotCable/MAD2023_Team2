package sg.edu.np.mad.pennywise2;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sg.edu.np.mad.pennywise2.models.LimitObject;

public class EditSetLimit extends AppCompatActivity {

    private TextView selectedStartDate;
    private TextView selectedEndDate;

    private EditText EtSpendLimit;

    private EditText EtFallsBelow;

    //Shared preference
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_EMAIL = "MyEmail";
    public String MY_UID = "MyUID";
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

        GetData();

        selectedStartDate.setOnClickListener(new View.OnClickListener() {//Date Picker
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
        selectedEndDate.setOnClickListener(new View.OnClickListener() {// Date Picker
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
                String startDate = selectedStartDate.getText().toString();
                String endDate = selectedEndDate.getText().toString();
                int compareDate = startDate.compareTo(endDate);
                double spendLimit = Double.parseDouble(EtSpendLimit.getText().toString());
                double fallsbelow = Double.parseDouble(EtFallsBelow.getText().toString());
                if (compareDate <= 0) {
                    if (!TextUtils.isEmpty(EtSpendLimit.getText())) {
                        if (!TextUtils.isEmpty(EtFallsBelow.getText())) {
                            if (spendLimit > 0) {
                                if (fallsbelow > 0) {
                                    sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
                                    String uid = sharedPreferences.getString(MY_UID, "");
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                    Map<String, Object> limitData = new HashMap<>();
                                    limitData.put("startdate", startDate);
                                    limitData.put("enddate", endDate);
                                    limitData.put("limit", spendLimit);
                                    limitData.put("warning", fallsbelow);
                                    String documentId = "wqeuqiueywue"; // Setting a default document Id
                                    DocumentReference documentRef = db.collection("users")
                                            .document(uid)
                                            .collection("setlimit")
                                            .document(documentId); // Override Id if exist

                                    documentRef.set(limitData, SetOptions.merge())
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(EditSetLimit.this, "Update Successful", Toast.LENGTH_SHORT).show(); // Show Message
                                                Intent intent = new Intent(EditSetLimit.this, SetLimit.class);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("Firestore", "Error setting document: " + e.getMessage());
                                            });
                                } else {
                                    EtFallsBelow.setError("Warning  must be greater than 0");
                                }
                            } else {
                                EtSpendLimit.setError("Spend Limit must be greater than 0");
                            }
                        } else {
                            EtFallsBelow.setError("Warning must not be empty");
                        }
                    } else {
                        EtSpendLimit.setError("Spend Limit must not be empty");
                    }
                } else if (compareDate > 0) {
                    selectedEndDate.setError("End Date must be greater than start date");
                }


            }
        });
    }
    private void GetData(){
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String sharedEmail = sharedPreferences.getString(MY_EMAIL, "");
        String uid = sharedPreferences.getString(MY_UID,"");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference LimitRef = db.collection("users").document(uid).collection("setlimit");
        LimitRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    LimitObject limit = new LimitObject(getTodaysDate("Start"), getTodaysDate("End"), 1500, 500);
                    for (DocumentSnapshot document : documents) {
                        Map<String, Object> data = document.getData();
                        if (data != null) {
                            String Getstartdate = (String) data.get("startdate");
                            String Getenddate = (String) data.get("enddate");
                            double getLimit = ((Number) data.get("limit")).doubleValue();
                            double getFallsBelow = ((Number) data.get("warning")).doubleValue();
                            limit = new LimitObject(Getstartdate, Getenddate, getLimit, getFallsBelow);
                        }
                    }
                    if (limit != null) {
                        selectedStartDate.setText(limit.getStartdate());
                        selectedEndDate.setText(limit.getEnddate());
                        EtSpendLimit.setText(String.valueOf(limit.getSpendlimit()));
                        EtFallsBelow.setText(String.valueOf(limit.getFallsbelow()));
                    }
                }
            }
        });
    }
    public String getTodaysDate(String dateType) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth;

        ArrayList<String> monthList = new ArrayList<>(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));
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

    private void SetLimit()
    {

    }


}
