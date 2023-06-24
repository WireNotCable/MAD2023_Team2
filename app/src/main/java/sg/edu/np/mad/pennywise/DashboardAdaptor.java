package sg.edu.np.mad.pennywise;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.edu.np.mad.pennywise.models.Transaction;

public class DashboardAdaptor extends RecyclerView.Adapter<dashboardViewHolder> {
    ViewTransRVInterface rvInterface;
    ArrayList<Transaction>listOfTransactions;

    public DashboardAdaptor(ArrayList<Transaction>listOfTransactions, ViewTransRVInterface rvInterface){
        this.listOfTransactions = listOfTransactions;
        this.rvInterface = rvInterface;
    }

    public dashboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_item, parent, false);
        dashboardViewHolder holder = new dashboardViewHolder(view, rvInterface);
        return holder;
    }

    public void onBindViewHolder(dashboardViewHolder holder, int position){
        Transaction list_transactions = listOfTransactions.get(position);
        holder.txtid.setText(list_transactions.getTransId());
        holder.txt1.setText(list_transactions.getTransTitle());
        holder.txt2.setText(list_transactions.getTransDate());
        holder.txt3.setText("$"+String.valueOf(list_transactions.getTransAmt()));

        if (list_transactions.getTransType().equals("income")){
            holder.txt3.setTextColor(Color.parseColor("#06A94D"));
        }
        else{
            holder.txt3.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    public int getItemCount(){
        return listOfTransactions.size();
    }
}
