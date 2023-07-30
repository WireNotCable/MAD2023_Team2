package sg.edu.np.mad.pennywise2;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FetchRouteData extends AsyncTask<String, Void, String> {
    private GoogleMap mMap;

    public FetchRouteData(GoogleMap map) {
        mMap = map;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Read data from the API response
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);
        if (jsonData != null) {
            try {
                // Parse the JSON response to get the route information
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                JSONObject routeObject = routesArray.getJSONObject(0);
                JSONObject polylineObject = routeObject.getJSONObject("overview_polyline");
                String points = polylineObject.getString("points");

                // Decode the polyline points and draw the route on the map
                List<LatLng> decodedPolyline = PolyUtil.decode(points);
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(decodedPolyline)
                        .width(8f)
                        .color(Color.BLUE);
                mMap.addPolyline(polylineOptions);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
