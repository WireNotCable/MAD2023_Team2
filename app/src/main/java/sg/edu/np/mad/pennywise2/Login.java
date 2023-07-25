package sg.edu.np.mad.pennywise2;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextInputEditText loginEmail, loginPassword;
    private CardView cardView;
    private LinearLayout loginLayout;
    private TextView signupRedirectText;
    private Button loginButton;

    // Shared preferences
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_EMAIL = "MyEmail";
    public String MY_UID = "MyUID";
    public String MY_PASSWORD = "MyPassword";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        loginLayout = findViewById(R.id.login_Layout);
        cardView = findViewById(R.id.card);
        // When login button is clicked
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();
                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    if(!password.isEmpty()){
                        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                // save email into shared preference
                                sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(MY_EMAIL, email.toLowerCase());
                                editor.putString(MY_PASSWORD, password);
                                editor.putString(MY_UID,auth.getUid());
                                editor.commit();
                                //Redirect to login
                                Toast.makeText(Login.this,"Login Successful",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this,MainActivity.class));

                                finish();
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this,"Login Failed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        loginPassword.setError("Password cannot be empty");
                    }
                }
                else if(email.isEmpty()){
                    loginEmail.setError("Email cannot be empty");
                }
                else{
                    loginEmail.setError("Please enter a valid email");
                }
            }
        });

        // Redirect to sign up
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, SignUp.class));
            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onResume(){
        super.onResume();
        animateCardViewAndItems();
        createBackgroundAnimation();
    }
    private void animateCardViewAndItems() {
        cardView.setTranslationY(1000f);
        cardView.setAlpha(0f);

        ObjectAnimator cardViewAnimator = ObjectAnimator.ofFloat(cardView, "translationY", 0f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(cardView, "alpha", 1f);

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.setDuration(1500);

        animatorSet.playTogether(cardViewAnimator, alphaAnimator);

        animatorSet.start();
    }
    private void createBackgroundAnimation() {
        AnimationDrawable animationDrawable = (AnimationDrawable) loginLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
    }
}