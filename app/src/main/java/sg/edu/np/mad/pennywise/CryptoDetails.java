package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pennywise.models.CryptoModel;

public class CryptoDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_details);
        TextView Symbols= findViewById(R.id.Symbolic);
        TextView Name= findViewById(R.id.Namific);
        TextView Price= findViewById(R.id.Pricific);
        LineChart linechart = findViewById(R.id.lineChart);
        Intent ReceiveID= getIntent();
        CryptoModel cryptoObject =(CryptoModel) ReceiveID.getSerializableExtra("CryptoObject");
        Symbols.setText(cryptoObject.getSymbol());
        Name.setText(cryptoObject.getName());
        Price.setText(String.valueOf(cryptoObject.getPrice()));
        generateLineGraph(linechart,cryptoObject);

    }
    private void generateLineGraph(LineChart lineChart, CryptoModel cryptoObject) {
        // Create a list of entries for the LineChart
        List<Entry> entries = new ArrayList<>();
        double pecentage1h = cryptoObject.getPercentChange1h();
        double pecentage24h = cryptoObject.getPercentChange24h();
        double pecentage7d = cryptoObject.getPercentChange7d();
        double pecentage30d = cryptoObject.getPercentChange30d();
        double pecentage60d = cryptoObject.getPercentChange60d();
        double pecentage90d = cryptoObject.getPercentChange90d();
        pecentage1h = Math.abs(pecentage1h);
        pecentage24h = Math.abs(pecentage24h);
        pecentage7d = Math.abs(pecentage7d);
        pecentage30d = Math.abs(pecentage30d);
        pecentage60d = Math.abs(pecentage60d);
        pecentage90d = Math.abs(pecentage90d);
        Log.v("Test",String.valueOf(pecentage1h));


        // Add data points for percent change over different time intervals
        entries.add(new Entry(1, (float) cryptoObject.getPrice()*(100- (float) pecentage1h)));
        entries.add(new Entry(2, (float) cryptoObject.getPrice()*(100- (float) pecentage24h)));
        entries.add(new Entry(3, (float) cryptoObject.getPrice()*(100- (float) pecentage7d)));
        entries.add(new Entry(4, (float) cryptoObject.getPrice()*(100- (float) pecentage30d)));
        entries.add(new Entry(5, (float) cryptoObject.getPrice()*(100- (float) pecentage60d)));
        entries.add(new Entry(6, (float) cryptoObject.getPrice()*(100- (float) pecentage90d)));


        for (Entry entry : entries) {
            Log.v("Entry Values", "x: " + entry.getX() + ", y: " + entry.getY());
        }

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
        yAxisLeft.setAxisMinimum((float)cryptoObject.getPrice());
        yAxisRight.setAxisMinimum((float)cryptoObject.getPrice());

        // Refresh the chart
        lineChart.invalidate();
    }

}