package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ViewCard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card);

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