package sg.edu.np.mad.pennywise;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CardViewImgHolder extends RecyclerView.ViewHolder {
    TextView txt;
    ImageView image;

    public CardViewImgHolder(View itemView){
        super(itemView);
        txt = itemView.findViewById(R.id.cardinfo);
        image = itemView.findViewById(R.id.cardimg);

    }
}
