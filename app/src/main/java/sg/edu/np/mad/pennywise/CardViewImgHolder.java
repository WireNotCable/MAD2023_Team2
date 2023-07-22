package sg.edu.np.mad.pennywise;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CardViewImgHolder extends RecyclerView.ViewHolder {
    TextView txt1;
    TextView txt2;
    TextView txt3;
    TextView txt4;
    TextView txt5;
    TextView txt6;
    ImageView image;

    public CardViewImgHolder(View itemView){
        super(itemView);
        txt1 = itemView.findViewById(R.id.cardinfo);
        txt2 = itemView.findViewById(R.id.adapDate);
        txt3 = itemView.findViewById(R.id.adapCSV);
        txt4 = itemView.findViewById(R.id.adapCardName);
        txt5 = itemView.findViewById(R.id.adapAddress);
        txt6 = itemView.findViewById(R.id.balance);
//        image = itemView.findViewById(R.id.cardimg);

    }
}
