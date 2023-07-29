package sg.edu.np.mad.pennywise2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainGoalProgressAdapter extends RecyclerView.Adapter<MainGoalProgressAdapter.MainProgressViewHolder> {
    private ArrayList<Goal> goalList;
    private Context context;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public MainGoalProgressAdapter(ArrayList<Goal> goalList, Context context) {
        this.goalList = goalList;
        this.context = context;
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MainProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_progresscard,parent,false);
        return new MainProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainProgressViewHolder holder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        double amount = goalList.get(position).getAmount();
        double current = goalList.get(position).getCurrent();
        String percentage =  decimalFormat.format(current/amount*100);
        holder.title.setText(goalList.get(position).getName());
        holder.value.setText(decimalFormat.format(goalList.get(position).getCurrent()) + " / " + decimalFormat.format(goalList.get(position).getAmount()));
        holder.percentage.setText(percentage + "%");
        holder.bar.setProgress((int)(current/amount*100));
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                View customView = LayoutInflater.from(context).inflate(R.layout.dialog_progress_menu, null);
                alertDialogBuilder.setView(customView);
                AlertDialog alertDialog = alertDialogBuilder.create();
                ImageView cancel = customView.findViewById(R.id.MenuProgressCancel);
                TextView delete = customView.findViewById(R.id.MenuProgressDelete);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection("users").document(auth.getUid()).collection("goals").document(goalList.get(position).getUid())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Progress deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Progress not deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        alertDialog.dismiss();
                        ((Activity) context).recreate();
                    }
                });
                alertDialog.show();
            }
        });
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Goal_Progress_Individual.class);
                intent.putExtra("UID",goalList.get(position).getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    public class MainProgressViewHolder extends RecyclerView.ViewHolder{
        private TextView title,value,percentage;
        private ProgressBar bar;
        private ImageButton menu;
        private CardView card;


        public MainProgressViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.MainProgressCardTitle);
            value = itemView.findViewById(R.id.MainProgressCardValue);
            percentage = itemView.findViewById(R.id.MainProgressCardPercentage);
            bar = itemView.findViewById(R.id.MainProgressCardBar);
            menu = itemView.findViewById(R.id.MainProgressCardMenu);
            card = itemView.findViewById(R.id.MainProgressCard);

        }
    }
}
