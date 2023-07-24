package sg.edu.np.mad.pennywise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Goal_Indivisual_Adapter extends RecyclerView.Adapter<Goal_Indivisual_Adapter.ProgressViewHolder> {
    private ArrayList<IndivisualGoalI> progressList;
    private Context context;

    public Goal_Indivisual_Adapter(ArrayList<IndivisualGoalI> progressList, Context context) {
        this.progressList = progressList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_individual_progress_history,parent,false);

        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        holder.name.setText(progressList.get(position).getName());
        DecimalFormat format = new DecimalFormat("#0.00");
        holder.amount.setText("$" + format.format(progressList.get(position).getAmount()));
        holder.date.setText(progressList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return progressList.size();
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder{
        private TextView name,date,amount;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.Indivisual_Card_Title);
            date = itemView.findViewById(R.id.Indivisual_Card_Date);
            amount = itemView.findViewById(R.id.Indivisual_Card_Amount);


        }
    }
}
