package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sg.edu.np.mad.pennywise.models.Card;

public class ViewCard extends AppCompatActivity {
    String title = "View Card";
    public String GLOBAL_PREFS = "myPrefs";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card);
        getCardDetails();

        ImageView homeBtn = findViewById(R.id.ViewCardBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewCard.this, MainActivity.class);
                startActivity(intent);
            }
        });
        Button AddBtn = findViewById(R.id.AddCardBtn);
        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewCard.this, addNewCard.class);
                startActivity(intent);
            }
        });



    }
    public void getCardDetails(){
        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        CollectionReference transactionRef = db.collection("users").document(auth.getUid()).collection("addCard");
        transactionRef.get().addOnCompleteListener(task -> {
            QuerySnapshot querySnapshot = task.getResult();
            List<DocumentSnapshot> documents = querySnapshot.getDocuments();
            ArrayList<Card> cardList = new ArrayList<>();
            for (DocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();

                //Extract data
                String Number = (String) data.get("number");
                String Exp = (String) data.get("exp");
                String CSV = (String) data.get("csv");
                String name = (String) data.get("name");
                String address = (String) data.get("address");
                double balance = document.getDouble("balance");
                Card card = new Card(Number, Exp, CSV, name, address,balance);
                cardList.add(card);
            }
            RecyclerView recyclerView = findViewById(R.id.CardViewing);
            viewCardAdapter ViewCardAdapter = new viewCardAdapter(cardList);
            LinearLayoutManager myLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(myLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(ViewCardAdapter);
        });

    }
}