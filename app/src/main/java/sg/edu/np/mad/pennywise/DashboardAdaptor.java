package sg.edu.np.mad.pennywise;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DashboardAdaptor extends RecyclerView.Adapter<dashboardViewHolder> {
    ArrayList<Transaction>listOfTransactions;
    public DashboardAdaptor(ArrayList<Transaction>listOfTransactions){
        this.listOfTransactions = listOfTransactions;
    }

    public dashboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_item, parent, false);
        dashboardViewHolder holder = new dashboardViewHolder(view);
        return holder;
    }

    public void onBindViewHolder(dashboardViewHolder holder, int position){
        Transaction list_transactions = listOfTransactions.get(position);
        holder.txt1.setText(list_transactions.getTransTitle());
        holder.txt2.setText(list_transactions.getTransDate());
        holder.txt3.setText("$"+String.valueOf(list_transactions.getTransAmt()));

        if (list_transactions.getTransType().equals("income")){
            holder.txt3.setTextColor(Color.parseColor("#06A94D"));
        }
        else{
            holder.txt3.setTextColor(Color.parseColor("#FF0000"));
            holder.txt3.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    public int getItemCount(){
        return listOfTransactions.size();
    }
}
