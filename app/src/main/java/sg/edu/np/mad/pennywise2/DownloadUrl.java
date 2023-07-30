package sg.edu.np.mad.pennywise2;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Retrieve locations data from URL
public class DownloadUrl {
    public String retrieveUrl(String url) throws IOException{
        String urlData = ""; // Store the retrieved data
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try{
            URL getUrl = new URL(url.replace("http://", "https://"));
            httpURLConnection = (HttpURLConnection) getUrl.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = bufferedReader.readLine())!=null){
                sb.append(line); // Append each line of data to the StringBuffer
            }
            urlData = sb.toString();
            bufferedReader.close();
        } catch (Exception e){
            Log.d("Exception", e.toString());
        }finally{
            // Ensure that the InputStream and HttpURLConnection are closed properly
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return urlData;
    }
}
