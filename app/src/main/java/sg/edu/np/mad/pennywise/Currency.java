package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/*import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;*/

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Currency extends AppCompatActivity {

    TextView convertFromDropdownTextView, convertToDropdownTextView, conversionRateText;
    EditText amountToConvert;
    ArrayList<String> arrayList;
    Dialog fromDialog;
    Dialog toDialog;
    Button convertButton;
    String convertFromValue, convertToValue, conversionValue;
    String[] country = {
            "AFN", "EUR", "ALL", "DZD", "USD", "AOA", "XCD", "ARS", "AMD", "AWG", "AUD", "AZN", "BSD", "BHD", "BDT", "BBD", "BYN", "BZD", "XOF",
            "BMD", "BTN", "BOB", "BAM", "BWP", "BRL", "BND", "BGN", "XOF", "BIF", "CVE", "KHR", "XAF", "CAD", "KYD", "XAF", "CLP", "CNY", "COP",
            "KMF", "CDF", "NZD", "CRC", "HRK", "CUP", "ANG", "CZK", "DKK", "DJF", "XCD", "DOP", "EGP", "XCD", "ERN", "EEK", "ETB", "EUR", "FKP",
            "FJD", "EUR", "XPF", "GMD", "GEL", "EUR", "GHS", "GIP", "XCD", "GTQ", "GGP", "GNF", "XOF", "GYD", "HTG", "HNL", "HKD", "HUF", "ISK",
            "INR", "IDR", "XDR", "IRR", "IQD", "IMP", "ILS", "EUR", "JMD", "JPY", "JEP", "JOD", "KZT", "KES", "AUD", "KPW", "KRW", "KWD", "KGS",
            "LAK", "LVL", "LBP", "LSL", "LRD", "LYD", "CHF", "LTL", "EUR", "MOP", "MKD", "MGA", "MWK", "MYR", "MVR", "XOF", "EUR", "MRO", "MUR",
            "MXN", "MDL", "MNT", "EUR", "XCD", "MAD", "MZN", "MMK", "NAD", "AUD", "NPR", "EUR", "XPF", "NZD", "NIO", "XOF", "NGN", "NZD", "NOK",
            "OMR", "PKR", "PAB", "PGK", "PYG", "PEN", "PHP", "NZD", "PLN", "EUR", "QAR", "RON", "RUB", "RWF", "XCD", "EUR", "SHP", "XCD", "XCD",
            "WST", "EUR", "STD", "SAR", "XOF", "RSD", "SCR", "SLL", "SGD", "EUR", "SBD", "SOS", "ZAR", "GBP", "EUR"
    };

    private static final String API_KEY = "0675ebc65257b3241fe03de7d9759945";
    private static final String BASE_URL = "https://api.exchangeratesapi.io/latest";
//finding the id's from XML, adding the countries to an array list, home button
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        convertFromDropdownTextView = findViewById(R.id.convert_from_dropdown_menu);
        convertToDropdownTextView = findViewById(R.id.convert_to_dropdown_menu);
        convertButton = findViewById(R.id.conversionButton);
        conversionRateText = findViewById(R.id.conversionRateText);
        amountToConvert = findViewById(R.id.amountToConvertValueEditText);

        arrayList = new ArrayList<>();
        for (String i : country) {
            arrayList.add(i);
        }

        ImageView currencyBtn = findViewById(R.id.homeCurBtn);
        currencyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Currency.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //Allow dropdown list selection with search filter
        convertFromDropdownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDialog = new Dialog(Currency.this);
                fromDialog.setContentView(R.layout.from_currency);
                fromDialog.getWindow().setLayout(650, 800);
                fromDialog.show();

                EditText editText = fromDialog.findViewById(R.id.edit_text);
                ListView listView = fromDialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(Currency.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        convertFromDropdownTextView.setText(adapter.getItem(position));
                        fromDialog.dismiss();
                        convertFromValue = adapter.getItem(position);

                    }
                });
            }
        });
        //Same but for the Convert To
        convertToDropdownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDialog = new Dialog(Currency.this);
                toDialog.setContentView(R.layout.to_currency);
                toDialog.getWindow().setLayout(650, 800);
                toDialog.show();

                EditText editText = toDialog.findViewById(R.id.edit_text);
                ListView listView = toDialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(Currency.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        adapter.getFilter().filter(s);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        convertToDropdownTextView.setText(adapter.getItem(position));
                        toDialog.dismiss();
                        convertToValue = adapter.getItem(position);
                    }
                });
            }
        });
// Calls the get conversion rate method to convert currency
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Double amountToConvert = Double.valueOf(Currency.this.amountToConvert.getText().toString());
                    getConversionRate(convertFromValue, convertToValue, amountToConvert);
                } catch (Exception e) {

                }
            }
        });
    }
    //The API used for currency exchange and converting String to JSON and returning conversion amount
    public String getConversionRate(String convertFrom, String convertTo, Double amountToConvert) {
        String url = "https://v6.exchangerate-api.com/v6/" + "f28e6c11bf096c06cd4ee33b" + "/pair/" + convertFrom + "/" + convertTo + "/" + amountToConvert;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Double conversionRateValue = jsonObject.getDouble("conversion_rate");
                    Double convertedAmount = round((amountToConvert * conversionRateValue), 2);
                    conversionValue = String.valueOf(convertedAmount);
                    conversionRateText.setText(conversionValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Error", "Error");
            }
        });

        queue.add(stringRequest);
        return null;
    }


// Rounding off to 2 DP using Big Decimal
    public static double round(double value, int places){
        if(places<0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}







