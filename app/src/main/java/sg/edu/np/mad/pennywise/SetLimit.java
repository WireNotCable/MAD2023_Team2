package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SetLimit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_limit);


        ImageView homeBtn = findViewById(R.id.limit_home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetLimit.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ImageView EditLimit = findViewById(R.id.setlimit_icon);
        EditLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetLimit.this, EditSetLimit.class);
                startActivity(intent);
            }
        });
    }
}