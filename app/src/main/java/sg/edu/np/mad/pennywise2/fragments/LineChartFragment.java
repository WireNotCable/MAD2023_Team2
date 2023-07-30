package sg.edu.np.mad.pennywise2.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sg.edu.np.mad.pennywise2.models.IndivisualGoalI;
import sg.edu.np.mad.pennywise2.R;


public class LineChartFragment extends Fragment {
    String uid;
    LineChart lineChart;
    FirebaseAuth auth;
    FirebaseFirestore db;
    public LineChartFragment(String uid){
        this.uid = uid;
    }

    // getting view and setting line chart
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_line_chart,container,false);
        lineChart = rootView.findViewById(R.id.lineChart);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Description description = new Description();

        String yourDescriptionText = "Recent 6M";
        description.setText(yourDescriptionText);

        description.setTextSize(12f); // Set the text size of the description label
        description.setTextColor(R.color.darkblue); // Set the text color of the description label

        lineChart.setDescription(description);
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

        setLineChartData();
        animateDataPoints();
        animateAxis();
    }
    //animation for data points
    private void animateDataPoints() {
        // Get the data object from the LineChart
        LineData data = lineChart.getData();

        if (data != null) {
            // Get the first data set (assuming you have only one data set in the LineChart)
            ILineDataSet dataSet = data.getDataSetByIndex(0);

            if (dataSet != null) {
                // Animate each data entry from left to right
                for (int i = 0; i < dataSet.getEntryCount(); i++) {
                    Entry entry = dataSet.getEntryForIndex(i);
                    entry.setX(i); // Set the x-coordinate of the data point to its index
                }

                // Notify the chart that the data has changed
                data.notifyDataChanged();
                lineChart.notifyDataSetChanged();
            }
        }

        // Animate the chart data points with a delay to allow the chart to draw first
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                lineChart.animateX(500, Easing.EaseInOutCubic);
            }
        }, 0);
    }
    //animation for line
    private void animateAxis() {
        // Animate the X-axis with a delay to allow the chart to draw first
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                lineChart.animateX(500, Easing.EaseInOutCubic);
                lineChart.animateY(500, Easing.EaseInOutCubic);
            }
        }, 0);
    }
    //setting line chart data recent amount for recent month to the left and display the last 6M of savings into the goal
    private void setLineChartData() {
        ArrayList<IndivisualGoalI> progressList = new ArrayList<IndivisualGoalI>();
        double[] monthSum = new double[6];


        db.collection("users").document(auth.getUid()).collection("goals").document(uid).collection("savings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String name = document.getString("name");
                                String date = document.getString("date");
                                double amount = document.getDouble("amount");
                                String id = document.getId();
                                progressList.add(new IndivisualGoalI(name,amount,date,id));
                            }

                            Calendar calendar = Calendar.getInstance();
                            int currentYear = calendar.get(Calendar.YEAR);
                            int currentMonth = calendar.get(Calendar.MONTH);

                            for (IndivisualGoalI item : progressList) {
                                Calendar itemDate = Calendar.getInstance();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                try {
                                    Date date = dateFormat.parse(item.getDate());
                                    itemDate.setTime(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                int itemYear = itemDate.get(Calendar.YEAR);
                                int itemMonth = itemDate.get(Calendar.MONTH);

                                if (itemYear == currentYear && itemMonth >= (currentMonth - 6)) {
                                    int monthIndex = (currentMonth - itemMonth) % 6; // Calculate the index for the monthSum array
                                    monthSum[monthIndex] += item.getAmount();
                                }
                            }
                            ArrayList<Entry> entries = new ArrayList<>();
                            // Replace this with your data or use a loop to populate the entries

                            // Add some example data
                            for (int i = 0;i<6;i++){
                                entries.add(new Entry(i,Float.parseFloat(String.valueOf(monthSum[i]))));
                            }



                            LineDataSet dataSet = new LineDataSet(entries, "Amount");
                            dataSet.setColor(R.color.greenish);
                            dataSet.setCircleColor(R.color.reddish);
                            dataSet.setLineWidth(2f);
                            dataSet.setCircleRadius(5f);
                            dataSet.setDrawCircleHole(false);
                            dataSet.setValueTextSize(12f);
                            dataSet.setValueTextColor(R.color.red);

                            LineData lineData = new LineData(dataSet);
                            lineChart.setData(lineData);

                            // Customize the X-axis and Y-axis if needed
                            XAxis xAxis = lineChart.getXAxis();
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                            YAxis yAxisLeft = lineChart.getAxisLeft();
                            YAxis yAxisRight = lineChart.getAxisRight();
                            yAxisLeft.setAxisMinimum(0);
                            yAxisRight.setAxisMinimum(0);

                            // Refresh the chart
                            lineChart.invalidate();

                        }
                    }
                });





    }
}