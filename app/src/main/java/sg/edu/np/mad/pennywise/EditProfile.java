package sg.edu.np.mad.pennywise;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        EditText editEmail = findViewById(R.id.editprofile_email);
        EditText editOldPassword = findViewById(R.id.editprofile_password);
        EditText editNewPassword = findViewById(R.id.editprofile_newpassword);
        Button EditProfile_Button = findViewById(R.id.editprofile_button);



        EditProfile_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the EditText field
                String email = editEmail.getText().toString();
                String OldPassword = editOldPassword.getText().toString();
                String NewPassword = editNewPassword.getText().toString();
                if(!OldPassword.isEmpty())
                {
                    if(OldPassword.matches(OldPassword))
                    {
                        if(!NewPassword.isEmpty())
                        {
                            Toast.makeText(EditProfile.this,"Profile Sucessfully Updated",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfile.this,Profile.class));
                            finish();
                        }
                        else
                        {
                            editNewPassword.setError("PLease Enter New Password");
                        }
                    }
                    else
                    {
                        editOldPassword.setError("Password does not match");
                    }
                }
                else
                {
                    editOldPassword.setError("Please Enter Old Password");
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
                Log.v(TAG, "On Destroy");

            }
        });




    }

}