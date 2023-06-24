package sg.edu.np.mad.pennywise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ObjectStreamException;
import java.util.HashMap;

public class AddUser extends AppCompatActivity {
    EditText name,detail;
    Button submit;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        name = findViewById(R.id.NewFriendName);
        detail = findViewById(R.id.NewFriendEmail);
        submit = findViewById(R.id.NewFriendSubmit);
        back = findViewById(R.id.AddFriendBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.getText().toString().isEmpty()){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String detailinput = detail.getText().toString().trim();
                    db.collection("users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (QueryDocumentSnapshot document : task.getResult()){
                                            String UID = document.getString("UID");
                                            String contact = document.getString("phonenum");
                                            String email = document.getString("email");
                                            String inputname = name.getText().toString().trim();
                                            if (email.equals(detailinput) || contact.equals(detailinput)){
                                                db.collection("users").document(auth.getUid()).collection("friendlist")
                                                        .whereEqualTo("UID",UID)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(AddUser.this,"User already exists in your friends",Toast.LENGTH_SHORT).show();

                                                                }else{
                                                                    String id = db.collection("users").document(auth.getUid()).collection("friendlist").document().getId();
                                                                    HashMap<String, Object> friendData = new HashMap<>();
                                                                    friendData.put("UID",UID);
                                                                    friendData.put("contact",contact);
                                                                    friendData.put("email",email);
                                                                    friendData.put("name",inputname);
                                                                    db.collection("users").document(auth.getUid()).collection("friendlist").document(id).set(friendData);
                                                                    Toast.makeText(AddUser.this,"New friend added!",Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                            }
                                            else{
                                                detail.setError("Invalid information added");
                                            }

                                        }
                                    }

                                }
                            });

                }
                else{
                    name.setError("Please input this field!");
                }

            }
        });

    }
}