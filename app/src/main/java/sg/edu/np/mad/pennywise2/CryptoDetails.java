package sg.edu.np.mad.pennywise2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pennywise2.models.CryptoModel;

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

        // Add data points for percent change over different time intervals
        entries.add(new Entry(6, (float) cryptoObject.getPercentChange1h()));
        entries.add(new Entry(5, (float) cryptoObject.getPercentChange24h()));
        entries.add(new Entry(4, (float) cryptoObject.getPercentChange7d()));
        entries.add(new Entry(3, (float) cryptoObject.getPercentChange30d()));
        entries.add(new Entry(2, (float) cryptoObject.getPercentChange60d()));
        entries.add(new Entry(1, (float) cryptoObject.getPercentChange90d()));

        LineDataSet dataSet = new LineDataSet(entries, cryptoObject.getName());
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextColor(Color.BLACK);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        // Create a LineData object from the datasets
        LineData lineData = new LineData(dataSets);

        // Set the LineData to the LineChart
        lineChart.setData(lineData);

        // Customize the LineChart as needed
        // For example, set labels, legend, etc.
        // ...
    }
}