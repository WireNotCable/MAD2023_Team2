package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class ViewCard extends AppCompatActivity {
    ArrayList<cardObject> myList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card);
        for(int i = 0; i<10; i++){
            cardObject obj = new cardObject();
            obj.setMyImageID(R.drawable.card_icon);
            obj.setMyText(String.valueOf(i));
            myList.add(obj);
        }
        RecyclerView recyclerView = findViewById(R.id.CardViewing);
        viewCardAdapter adapter = new viewCardAdapter(myList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

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
}