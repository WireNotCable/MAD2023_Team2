package sg.edu.np.mad.pennywise2.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import sg.edu.np.mad.pennywise2.R;


public class LineChartFragment extends Fragment {
    LineChart lineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_line_chart,container,false);
        lineChart = rootView.findViewById(R.id.lineChart);
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

        setLineChartData();
        animateDataPoints();
        animateAxis();
    }

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

    private void setLineChartData() {
        ArrayList<Entry> entries = new ArrayList<>();
        // Replace this with your data or use a loop to populate the entries

        // Add some example data
        entries.add(new Entry(0, 10));
        entries.add(new Entry(1, 20));
        entries.add(new Entry(2, 15));
        entries.add(new Entry(3, 25));
        entries.add(new Entry(4, 30));
        entries.add(new Entry(5, 30));
        entries.add(new Entry(6, 30));
        entries.add(new Entry(7, 35));
        entries.add(new Entry(8, 30));


        LineDataSet dataSet = new LineDataSet(entries, "Label for the dataset");
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