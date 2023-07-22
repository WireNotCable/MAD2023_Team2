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

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private EditText editEmail;
    private EditText editOldPassword;
    private EditText  editNewPassword;
    private  EditText editName;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        editEmail = findViewById(R.id.editprofile_email);
        editOldPassword = findViewById(R.id.editprofile_password);
        editNewPassword = findViewById(R.id.editprofile_newpassword);
        editName = findViewById(R.id.editprofile_name);

        // Retrieve the email and password from the shared prefs
        SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String email = prefs.getString(MY_EMAIL, "");
        String password = prefs.getString(MY_PASSWORD, "");
        editEmail.setText(email);
        // Retrieve the uid from the shared prefs
        SharedPreferences sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String UID = prefs.getString(MY_UID, "");

        //Pass Details
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        usersRef.whereEqualTo("UID", UID)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);


                            editName.setText(documentSnapshot.getString("name"));
                            editEmail.setText(email.toLowerCase());


                        } else {
                            // No matching document found
                            Log.d(TAG, "No document found for the specified email");
                        }
                    } else {
                        // Error retrieving data
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });

        Button EditProfile_Button = findViewById(R.id.editprofile_button);

        EditProfile_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                // Get the text from the EditText fields
                String newEmail = editEmail.getText().toString();
                String oldPassword = editOldPassword.getText().toString();
                String newPassword = editNewPassword.getText().toString();
                String newName = editName.getText().toString();
                if (!newName.isEmpty()) {
                    if (!newEmail.isEmpty()) {
                        if (isValidEmail(newEmail)) {
                            if (!oldPassword.isEmpty()) {
                                if (oldPassword.equals(password)) {
                                    if (!newPassword.isEmpty()) {
                                        showConfirmationDialog();

                                    } else {
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
                } else {
                    editName.setError("Please enter name");
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

    @Override
    protected void onStart() {
        super.onStart();


    }

    public boolean isValidEmail(String email) {//Validate Email
        String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"; // Regular expression
        Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
        return EMAIL_PATTERN.matcher(email).matches(); // validating
    }
    private void updateCredentials(){
        String newEmail = editEmail.getText().toString();
        String oldPassword = editOldPassword.getText().toString();
        String newPassword = editNewPassword.getText().toString();
        String newName = editName.getText().toString();
        // Get Firebase authentication instance and current user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Get Firebase Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Update user data in Firestore
        db.collection("users").document(auth.getUid()).update("email", newEmail); // Update Email
        db.collection("users").document(auth.getUid()).update("name", newName); // Update Name
        db.collection("users").document(auth.getUid()).update("password", newPassword); // Update Password

        // Update email in Firebase Authentication
        currentUser.updateEmail(newEmail);

        // Reauthenticate the user to update the password
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPassword);
        currentUser.reauthenticate(credential);
        currentUser.updatePassword(newPassword);

        // Update Shared Preferences
        SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(MY_EMAIL, newEmail);
        editor.putString(MY_PASSWORD, newPassword);
        editor.apply(); // Apply the changes to SharedPreferences
        // Navigate back to the Profile activity
        Intent intent = new Intent(EditProfile.this, Profile.class);
        startActivity(intent);
        Toast.makeText(EditProfile.this, "Profile Successfully Updated", Toast.LENGTH_SHORT).show();
    }
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Update");
        builder.setMessage("Are you sure you want to update your profile?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call the method to update the profile here
                updateCredentials();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing, just close the dialog
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
