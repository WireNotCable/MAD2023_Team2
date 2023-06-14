package sg.edu.np.mad.pennywise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class viewCardAdapter extends RecyclerView.Adapter<CardViewImgHolder>{
    private ArrayList<cardObject> list_objects;
    public viewCardAdapter(ArrayList<cardObject> obj){
        this.list_objects = obj;
    }

    public CardViewImgHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_view_card, parent, false);
        CardViewImgHolder holder = new CardViewImgHolder(view);
        return holder;
    }

    public void onBindViewHolder(CardViewImgHolder holder, int position) {
        cardObject list_items = list_objects.get(position);
        holder.txt.setText(list_items.getMyText());
        holder.image.setImageResource(list_items.getMyImageID());

    }

    public int getItemCount() {

        return list_objects.size();
    }
}
