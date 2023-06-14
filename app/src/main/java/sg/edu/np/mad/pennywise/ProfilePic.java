package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ProfilePic extends AppCompatActivity {
    public String GLOBAL_PREFS = "myPrefs";

    public String MY_PASSWORD = "MyUserPassword";
    public String MY_USERNAME = "MyUserName";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilepic);
        TextView email = findViewById(R.id.profilepic_email);
//        SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
//        String getEmail = prefs.getString(MY_USERNAME,"");
//        email.setText(getEmail);


    }
}