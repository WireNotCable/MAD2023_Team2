package sg.edu.np.mad.pennywise2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sg.edu.np.mad.pennywise2.models.CryptoModel;

public class CryptoTracker extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText searchEdt;
    private RecyclerView currenciesRV;
    private ArrayList<CryptoModel> modelCryptos;
    private CryptoAdaptor adapterCrypto;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private long backPressedTime;
    public static final String GLOBAL_PREFS = "myPrefs";
    SharedPreferences sharedPreferences;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar nav_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_tracker);
        //NAV BAR
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        nav_toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(nav_toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, nav_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        Toolbar toolbar = findViewById(R.id.crypto_toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        searchEdt = findViewById(R.id.idEdtSearch);
        currenciesRV = findViewById(R.id.idRVCurrencies);
        progressBar = findViewById(R.id.progressBar);
        modelCryptos = new ArrayList<>();
        adapterCrypto = new CryptoAdaptor(modelCryptos,this);
        currenciesRV.setLayoutManager(new LinearLayoutManager(this));
        currenciesRV.setAdapter(adapterCrypto);
        getCurrencyData();

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterCurrencies(s.toString());
            }
        });

    }

    private void filterCurrencies(String currency) {
        ArrayList<CryptoModel> filteredList = new ArrayList<>();
        for (CryptoModel item : modelCryptos) {
            if (item.getName().toLowerCase().contains(currency.toLowerCase())) {
                filteredList.add(item);
            }
            adapterCrypto.filterList(filteredList);
        }

    }

    private void getCurrencyData() {
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONArray dataArray = response.getJSONArray("data");
                    for (int i=0; i<dataArray.length(); i++) {
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        String name = dataObj.getString("name");
                        String symbol = dataObj.getString("symbol");
                        JSONObject quote = dataObj.getJSONObject("quote");
                        JSONObject USD = quote.getJSONObject("USD");
                        double price = USD.getDouble("price");
                        DecimalFormat df = new DecimalFormat("#.##");
                        String formattedPrice = df.format(price);
                        double percentChange1h = USD.getDouble("percent_change_1h");
                        DecimalFormat df2 = new DecimalFormat("#.##");
                        String formattedpercentChange1h = df2.format(percentChange1h);
                        double percentChange24h = USD.getDouble("percent_change_24h");
                        DecimalFormat df3 = new DecimalFormat("#.##");
                        String formattedpercentChange24h = df3.format(percentChange24h);

                        double percentChange7d = USD.getDouble("percent_change_7d");
                        DecimalFormat df4 = new DecimalFormat("#.##");
                        String formattedpercentChange7d = df4.format(percentChange7d);

                        double percentChange30d = USD.getDouble("percent_change_30d");
                        DecimalFormat df5 = new DecimalFormat("#.##");
                        String formattedpercentChange30d = df5.format(percentChange30d);

                        double percentChange60d = USD.getDouble("percent_change_60d");
                        DecimalFormat df6 = new DecimalFormat("#.##");
                        String formattedpercentChange60d = df6.format(percentChange60d);

                        double percentChange90d = USD.getDouble("percent_change_90d");
                        DecimalFormat df7 = new DecimalFormat("#.##");
                        String formattedpercentChange90d = df7.format(percentChange90d);
                        modelCryptos.add(new CryptoModel(name, symbol, Double.valueOf(formattedPrice), Double.valueOf(formattedpercentChange1h), Double.valueOf(formattedpercentChange24h), Double.valueOf(formattedpercentChange7d), Double.valueOf(formattedpercentChange30d), Double.valueOf(formattedpercentChange60d), Double.valueOf(formattedpercentChange90d)));
                    }
                    adapterCrypto.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CryptoTracker.this, "Fail to extract json data...", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CryptoTracker.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("X-CMC_PRO_API_KEY","2df2c2a9-5e14-457c-9bab-618931401512");
                return header;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Set the selected item every time the activity is brought to the foreground
        navigationView.setCheckedItem(R.id.nav_cryptoTracker);
    }
    //onBackPressed
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true);
            System.exit(0);
        } else {
            Toast.makeText(this, "Do to again to quit the application.", Toast.LENGTH_SHORT).show();
        }
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
        super.onBackPressed();
    }
    // NAVBAR //
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_add_transactions){
            Intent intent = new Intent(CryptoTracker.this, AddTransaction.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_view_transactions){
            Intent intent = new Intent(CryptoTracker.this, ViewAllTransactions.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_card){
            Intent intent = new Intent(CryptoTracker.this, ViewCard.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_profile){
            Intent intent = new Intent(CryptoTracker.this, Profile.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_currency) {
            Intent intent = new Intent(CryptoTracker.this, Currency.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_set_limit){
            Intent intent = new Intent(CryptoTracker.this, SetLimit.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_transfer){
            Intent intent = new Intent(CryptoTracker.this, Transfer.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_cryptoTracker){
            Intent intent = new Intent(CryptoTracker.this, CryptoTracker.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_stats){
            Intent intent = new Intent(CryptoTracker.this,Stats.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_goal){
            Intent intent = new Intent(CryptoTracker.this, Goal_Progress.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_map){
            Intent intent = new Intent(CryptoTracker.this, Maps.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_about){
            Intent intent = new Intent(CryptoTracker.this, AboutUs.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_logout){
            sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(CryptoTracker.this,Login.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

