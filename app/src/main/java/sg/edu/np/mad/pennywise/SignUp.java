package sg.edu.np.mad.pennywise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.C;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword,signupContactNumber,signupNRIC;
    private Button signupButton;
    private TextView loginRedirectText;
    private DatabaseReference DBRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        signupContactNumber = findViewById(R.id.signup_contact);
        signupNRIC = findViewById(R.id.signup_NRIC);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        DBRef = FirebaseDatabase.getInstance().getReference().child("Users");

        MyDBHandler dbHandler = new MyDBHandler(this,null,null,1);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String contact = signupContactNumber.getText().toString().trim();
                String NRIC = signupNRIC.getText().toString().trim();
                if (user.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                if (password.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                }
                if (contact.isEmpty()){
                    signupContactNumber.setError("Contact number cannot be empty");
                }
                if (NRIC.isEmpty()){
                    signupNRIC.setError("NRIC cannot be empty");
                }
                if (NRIC.length() != 9 && NRIC.substring(1,7).chars().allMatch(Character :: isDigit)){
                    signupNRIC.setError("Invalid NRIC input, please input a valid NRIC");
                }

                else{
                    auth.createUserWithEmailAndPassword(user, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                User userdata = new User(user,password,NRIC,contact);
                                dbHandler.addUser(userdata);
                                InputDataDetails details = new InputDataDetails(dbHandler.getUID(user),NRIC,contact );
                                DBRef.push().setValue(details);
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
