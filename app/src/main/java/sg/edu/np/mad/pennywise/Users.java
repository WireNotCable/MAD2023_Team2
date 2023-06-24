package sg.edu.np.mad.pennywise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import sg.edu.np.mad.pennywise.models.FriendClass;

public class Users extends AppCompatActivity {
    private RecyclerView recyclerView ;
    private UsersRecyclerAdapter recyclerViewAdapter;
    private EditText searchFriend;
    ImageView addFriend,backArrow;
    ArrayList<FriendClass> friendList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        recyclerView = findViewById(R.id.FriendRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(Users.this));
        addFriend = findViewById(R.id.AddFriend);
        searchFriend = findViewById(R.id.SearchFriends);
        backArrow = findViewById(R.id.BackArrowFriends);
        friendList.clear();

        searchFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString() != "") {
                    filter(s.toString());
                }

            }
            private void filter(String text){
                ArrayList<FriendClass> filteredList = new ArrayList<>();
                for (FriendClass item : friendList){
                    if (item.getName().toLowerCase().contains(text.toLowerCase())){
                        filteredList.add(item);
                    }
                }
                recyclerViewAdapter.filterList(filteredList);
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Users.this,AddUser.class);
                startActivity(intent);
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        friendList.clear();
        recyclerView = findViewById(R.id.FriendRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(Users.this));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.collection("users").document(auth.getUid()).collection("friendlist")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                friendList.add(new FriendClass(document.getString("UID"),document.getString("name"),document.getString("email"),document.getString("contact")));
                            }
                        }
                        else{
                            Toast.makeText(Users.this,"Unable to fetch user data, please contact support",Toast.LENGTH_SHORT).show();

                        }
                    }
                });



        recyclerViewAdapter = new UsersRecyclerAdapter(friendList,Users.this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

    }


}