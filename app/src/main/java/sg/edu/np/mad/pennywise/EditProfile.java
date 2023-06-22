package sg.edu.np.mad.pennywise;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EditProfile extends AppCompatActivity {

    public String GLOBAL_PREFS = "myPrefs";
    public String MY_EMAIL = "MyEmail";
    public String MY_PASSWORD = "MyPassword";
    public String MY_UID = "MyUID";
    SharedPreferences sharedPreferences;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        EditText editEmail = findViewById(R.id.editprofile_email);
        EditText editOldPassword = findViewById(R.id.editprofile_password);
        EditText editNewPassword = findViewById(R.id.editprofile_newpassword);
        // Retrieve the email and password from the shared prefs
        SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String email = prefs.getString(MY_EMAIL, "");
        String password = prefs.getString(MY_PASSWORD, "");
        String uid = prefs.getString(MY_UID,"");
        editEmail.setText(email);


        Button EditProfile_Button = findViewById(R.id.editprofile_button);

        EditProfile_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                // Get the text from the EditText fields
                String newEmail = editEmail.getText().toString();
                String oldPassword = editOldPassword.getText().toString();
                String newPassword = editNewPassword.getText().toString();
                if (!newEmail.isEmpty()) {
                    if (isValidEmail(newEmail)) {

                            if (!oldPassword.isEmpty()) {
                                if (oldPassword.equals(password)) {
                                    if (!newPassword.isEmpty()) {

                                        FirebaseAuth auth = FirebaseAuth.getInstance();
                                        FirebaseUser currentUser = auth.getCurrentUser();
                                        currentUser.updateEmail(newEmail);
                                        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPassword);
                                        currentUser.reauthenticate(credential);
                                        currentUser.updatePassword(newPassword);
//                                        PhoneAuthCredential credentials = PhoneAuthProvider.getCredential("","");
//                                        currentUser.updatePhoneNumber(credentials);
//                                              Fields updated successfully
//                                              Update Shared Preferences
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putString(MY_EMAIL, newEmail);
                                                editor.putString(MY_PASSWORD, newPassword);
                                                editor.apply(); // Apply the changes to SharedPreferences
                                                Toast.makeText(EditProfile.this, "Profile Successfully Updated", Toast.LENGTH_SHORT).show();


                                    }else {
                                        editNewPassword.setError("Please Enter New Password");
                                    }
                                } else {
                                    editOldPassword.setError("Password does not match");
                                }
                            } else {
                                editOldPassword.setError("Please Enter Old Password");
                            }
                        } else {
                            editEmail.setError("Please enter a valid email");
                        }

                } else {
                    editEmail.setError("Please enter email");
                }
            }
        });

        ImageView backButton = findViewById(R.id.editprofile_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public boolean isValidEmail(String email) {
        String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"; // Regular expression
        Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
        return EMAIL_PATTERN.matcher(email).matches(); // validating
    }
}
