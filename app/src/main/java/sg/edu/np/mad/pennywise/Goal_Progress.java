package sg.edu.np.mad.pennywise;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Goal_Progress extends AppCompatActivity {
    RecyclerView recyclerGoal;
    ImageView addProgress;
    FirebaseAuth auth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_progress);

        recyclerGoal = findViewById(R.id.progressview);
        addProgress = findViewById(R.id.AddProgress);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        addProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProgressDialog();
                recreate();
            }
        });
    }

    private void AddProgressDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Custom Dialog");
        dialogBuilder.setView(R.layout.dialog_new_progress);

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Dialog dialog = (Dialog) dialogInterface;
                EditText editText = dialog.findViewById(R.id.inputprogresstitle);
                EditText numberEditText = dialog.findViewById(R.id.inputprogressamt);

                String text = editText.getText().toString();
                int number = Integer.parseInt(numberEditText.getText().toString());
                
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.setCancelable(false);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}