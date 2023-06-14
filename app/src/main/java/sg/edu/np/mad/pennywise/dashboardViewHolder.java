package sg.edu.np.mad.pennywise;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class dashboardViewHolder extends RecyclerView.ViewHolder{
    TextView txt1;
    TextView txt2;
    TextView txt3;

    public dashboardViewHolder(View itemView){
        super(itemView);
        txt1 = itemView.findViewById(R.id.dashTitle);
        txt2 = itemView.findViewById(R.id.dashDate);
        txt3 = itemView.findViewById(R.id.dashAmt);
    }

}
