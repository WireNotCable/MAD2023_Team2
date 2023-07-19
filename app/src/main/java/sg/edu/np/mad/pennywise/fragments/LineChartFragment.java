package sg.edu.np.mad.pennywise.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import sg.edu.np.mad.pennywise.R;


public class LineChartFragment extends Fragment {
    LineChart lineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_line_chart,container,false);
        lineChart = rootView.findViewById(R.id.lineChart);
        setLineChartData();
        return rootView;
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