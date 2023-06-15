package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class SetLimit extends AppCompatActivity {
    // Shared preference
    public static final String GLOBAL_PREFS = "myPrefs";
    public static final String MY_EMAIL = "MyEmail";
    public String MY_EXPENSE= "myExpense";

    public String MY_STARTDATE = "myStartDate";

    public String MY_ENDDATE = "myEndDate";
    SharedPreferences sharedPreferences;
    private ImageView homeBtn;
    private ImageView EditLimit;
    private TextView StartDate;
    private TextView EndDate;
    private TextView SpendLimit;
    private TextView FallsBelow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_limit);

        StartDate = findViewById(R.id.limit_startdate);
        EndDate = findViewById(R.id.limit_enddate);
        SpendLimit = findViewById(R.id.limit_spendlimit);
        FallsBelow = findViewById(R.id.limit_spend);

        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String sharedEmail = sharedPreferences.getString(MY_EMAIL, "");
        String Expense = sharedPreferences.getString(MY_EXPENSE,"");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference LimitRef = db.collection("users").document(sharedEmail).collection("setlimit");
        LimitRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    LimitObject limit = new LimitObject(getTodaysDate("Start"),getTodaysDate("End"),1500,500);
                    int index = 0;
                    ArrayList<LimitObject> limit_list = new ArrayList<>();
                    for (DocumentSnapshot document : documents) {
                        Map<String, Object> data = document.getData();
                        if (data != null) {
                            index+=1;
                            // Extract data
                            String Getstartdate = (String) data.get("startdate");
                            String Getenddate = (String) data.get("enddate");
                            double Getlimit = ((Number) data.get("limit")).doubleValue();
                            double Getwarning = ((Number) data.get("warning")).doubleValue();
                            limit = new LimitObject(Getstartdate, Getenddate, Getlimit, Getwarning);
                            limit_list.add(limit);
//                            Log.v("START DATE",limit.getStartdate());
                        }
                    }
                    if (limit != null) {
                        limit = limit_list.get(index-1);
                        Log.v("START DATE",limit.getStartdate());
                        StartDate.setText(limit.getStartdate());
                        EndDate.setText(limit.getEnddate());
                        SpendLimit.setText(Expense +"/"+ String.valueOf(limit.getSpendlimit()));
                        FallsBelow.setText(String.valueOf(limit.getFallsbelow()));
                        SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(MY_STARTDATE,limit.getStartdate());
                        editor.putString(MY_ENDDATE,limit.getEnddate());
                        editor.apply(); // Apply the changes to SharedPreferences
                    }

                }
            }
        });

        homeBtn = findViewById(R.id.limit_home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetLimit.this, MainActivity.class);
                startActivity(intent);
            }
        });

        EditLimit = findViewById(R.id.setlimit_icon);
        EditLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetLimit.this, EditSetLimit.class);
                intent.putExtra("StartDate", StartDate.getText().toString());
                intent.putExtra("EndDate",EndDate.getText().toString());
                intent.putExtra("SpendLimit",SpendLimit.getText().toString());
                intent.putExtra("FallsBelow",FallsBelow.getText().toString());
                startActivity(intent);
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
