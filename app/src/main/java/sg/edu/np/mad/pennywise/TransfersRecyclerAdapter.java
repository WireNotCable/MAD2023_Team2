package sg.edu.np.mad.pennywise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.CDATASection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TransfersRecyclerAdapter extends RecyclerView.Adapter<TransfersRecyclerAdapter.TransferViewHolder>{
    private ArrayList<Transfers> transfersArrayList;

    private Context context;

    public TransfersRecyclerAdapter(ArrayList<Transfers> transfersArrayList, Context context) {
        this.transfersArrayList = transfersArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TransferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transcationcard,parent,false);

        return new TransferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransferViewHolder holder, int position) {
        if (Double.parseDouble(transfersArrayList.get(position).amount) < 0){
            holder.imageView.setImageResource(R.drawable.baseline_money_off_24);
        }
        else{
            holder.imageView.setImageResource(R.drawable.baseline_attach_money_24);
        }
        holder.datetransfer.setText(transfersArrayList.get(position).transferDate);
        holder.transfertype.setText(transfersArrayList.get(position).type);
        holder.amount.setText("$"+transfersArrayList.get(position).amount);
        holder.cardView.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return transfersArrayList.size();
    }

    public class TransferViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView datetransfer,transfertype,amount;
        private CardView cardView;

        public TransferViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            datetransfer = itemView.findViewById(R.id.datetransfer);
            transfertype = itemView.findViewById(R.id.transfertype);
            amount = itemView.findViewById(R.id.amount);
            cardView = itemView.findViewById(R.id.dashboardcardview);
        }
    }
}
