package sg.edu.np.mad.pennywise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, signupPhoneNum, signupNRIC;
    private Button signupButton;
    private TextView loginRedirectText;

    //Shared preference //
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_EMAIL = "MyEmail";
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupPhoneNum = findViewById(R.id.signup_contact);
        signupNRIC = findViewById(R.id.signup_NRIC);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String phoneNum = signupPhoneNum.getText().toString();
                String NRIC = signupNRIC.getText().toString();
                if (user.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                if (password.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                }
                if (phoneNum.isEmpty()){
                    signupPhoneNum.setError("Contact number cannot be empty");
                }
                if (NRIC.isEmpty()){
                    signupNRIC.setError("NRIC cannot be empty");
                }
                if (NRIC.length() != 9 ){
                    signupNRIC.setError("Invalid NRIC input, please input a valid NRIC");
                }

                else{
                    auth.createUserWithEmailAndPassword(user, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                //Log.v("TEST",firebaseUser.getEmail());
                                User userdata = new User(user,password,NRIC,phoneNum);
                                sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
                                String sharedEmail = sharedPreferences.getString(MY_EMAIL, "");
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("UID", firebaseUser.getUid());
                                userData.put("email", user);
                                userData.put("password", password); //just cos we wan add
                                userData.put("phonenum", phoneNum);
                                userData.put("nric", NRIC);
                                db.collection("users").document(user).set(userData);


                                Toast.makeText(SignUp.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUp.this, Login.class));
                            }
                            else{
                                Toast.makeText(SignUp.this, "Sign Up Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });




                    loginRedirectText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(SignUp.this, Login.class));
                        }
                    });
                }
            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, Login.class));
            }
        });
    }
}