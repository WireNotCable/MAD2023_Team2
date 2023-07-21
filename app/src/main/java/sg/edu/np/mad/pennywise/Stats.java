package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Stats extends AppCompatActivity {
    public static final String GLOBAL_PREFS = "myPrefs";
    public static final String MY_EMAIL = "MyEmail";

    public String MY_UID = "MyUID";
    SharedPreferences sharedPreferences;

    BarChart monthChart;
    ArrayList monthlySpendArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        monthChart = findViewById(R.id.monthly_bar);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String sharedEmail = sharedPreferences.getString(MY_EMAIL, "");
        String uid = sharedPreferences.getString(MY_UID, "");

        getMonthData(uid);
    }

    private void getMonthData(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference transactionRef = db.collection("users").document(uid).collection("alltransaction");
        transactionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                monthlySpendArrayList = new ArrayList();

                for (int i = 0; i < 12; i++) {
                    double monthSpend = 0.0;

                    for (DocumentSnapshot document : documents) {
                        Map<String, Object> data = document.getData();
                        double amount = (double) data.get("amount");
                        String type = (String) data.get("type");
                        String dateString = (String) data.get("date");
                        Log.v("Monthas", String.valueOf(dateString));
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

                        try {
                            Date date = sdf.parse(dateString);
                            Log.v("Monthas", String.valueOf(dateString));
                            // Use i + 1 since Date's getMonth() returns months starting from 0

                            if (type.equals("expense") && (date.getMonth() + 1) == i + 1) {
                                monthSpend += amount;
                            }
                        } catch (ParseException e) {

                            e.printStackTrace(); // Handle or log the exception as needed
                        }
                    }
                    Log.v("MHEHasdghUEWH", String.valueOf(i));
                    monthlySpendArrayList.add(new BarEntry(i,(float) monthSpend));
                }


                // Once the data is retrieved, update the chart
                updateChart();
            } else {
                Log.e("TotalExpense", "Error getting documents: ", task.getException());
            }
        });
    }

    private void updateChart() {
        BarDataSet monthDataSet = new BarDataSet(monthlySpendArrayList, "Months");
        BarData barData = new BarData(monthDataSet);
        monthChart.setData(barData);

        // Label the X-axis with your desired labels
        String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        XAxis xAxis = monthChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        // Set the position of the X-axis labels to "BOTTOM"
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        // Customize the Y-axis appearance
        YAxis leftYAxis = monthChart.getAxisLeft();
        leftYAxis.setDrawGridLines(false); // Remove the horizontal grid lines

        monthDataSet.setValueTextColor(Color.BLUE);
        monthDataSet.setValueTextSize(15f);
        monthChart.getDescription().setEnabled(true);
        monthDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // Refresh the chart
        monthChart.invalidate();
    }

}
