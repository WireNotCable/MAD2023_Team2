package sg.edu.np.mad.pennywise;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class dashboardViewHolder extends RecyclerView.ViewHolder{
    TextView txtid;
    TextView txt1;
    TextView txt2;
    TextView txt3;

    public dashboardViewHolder(View itemView, ViewTransRVInterface rvInterface){
        super(itemView);
        txtid = itemView.findViewById(R.id.dashId);
        txt1 = itemView.findViewById(R.id.dashTitle);
        txt2 = itemView.findViewById(R.id.dashDate);
        txt3 = itemView.findViewById(R.id.dashAmt);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rvInterface != null){
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        rvInterface.onItemClick(pos);
                    }
                }
            }
        });
    }

}
