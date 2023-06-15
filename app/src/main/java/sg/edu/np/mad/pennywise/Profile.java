package sg.edu.np.mad.pennywise;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class Profile extends AppCompatActivity {
    // Shared preferences
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_EMAIL = "MyEmail";
    public String MY_PASSWORD = "MyPassword";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView homeBtn = findViewById(R.id.Profile_Home);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ImageView EditButton = findViewById(R.id.Profile_Edit);
        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Editting Profile!!");
                Intent intent = new Intent(Profile.this, EditProfile.class);
                startActivity(intent);

            }
        });

        ImageView ProfilePic = findViewById(R.id.profile_profilepic);
        ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfile();
            }
        });
    }
    private void showProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.profilepic, null);
        builder.setView(dialogView);

        // Retrieve the username from the shared prefs
        SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String username = prefs.getString(MY_EMAIL, "");
        Log.v("Usernmae",username);

        // Pass the name to the dialovview
        TextView profilepic_name = dialogView.findViewById(R.id.profilepic_name);
        String name = username.split("@")[0];
        profilepic_name.setText(name);
        // Pass the username to the dialogView
        TextView profilepic_username= dialogView.findViewById(R.id.profilepic_email);
        profilepic_username.setText(username.toLowerCase(Locale.ROOT));

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
