package sg.edu.np.mad.pennywise2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextInputEditText signupEmail,signupPassword,signupPhoneNum,signupName;
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

        View rootView = findViewById(R.id.signupLayOut);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_from_bottom);
        AnimationDrawable animationDrawable = (AnimationDrawable) rootView.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);

        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(rootView, "translationY", rootView.getHeight(), 0f);
        translationAnimator.setDuration(1500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationAnimator, ObjectAnimator.ofFloat(rootView, "alpha", 0f, 1f));

        animatorSet.start();
        rootView.startAnimation(fadeInAnimation);
        animationDrawable.start();

        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupPhoneNum = findViewById(R.id.signup_contact);
        signupName = findViewById(R.id.signup_name);

        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String phoneNum = signupPhoneNum.getText().toString();
                String name = signupName.getText().toString();

                // data validation
                if (user.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                if (password.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                }
                if (phoneNum.isEmpty()){
                    signupPhoneNum.setError("Contact number cannot be empty");
                }

                if (name.isEmpty()){
                    signupName.setError("Name cannot be empty");
                }
                else{
                    auth.createUserWithEmailAndPassword(user, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("UID",auth.getUid());
                                userData.put("email", user);
                                userData.put("password", password); //just cos we wan add
                                userData.put("phonenum", phoneNum);
                                userData.put("name", name);

                                db.collection("users").document(firebaseUser.getUid()).set(userData);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
