package sg.edu.np.mad.pennywise;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class viewCardAdapter extends RecyclerView.Adapter<CardViewImgHolder>{
    ArrayList<Card>listOfCards;
    public viewCardAdapter(ArrayList<Card>listOfCards){
        this.listOfCards = listOfCards;
    }

    public CardViewImgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_view_card, parent, false);
        CardViewImgHolder holder = new CardViewImgHolder(view);
        return holder;
    }

    public void onBindViewHolder(CardViewImgHolder holder, int position){
        Card list_Cards = listOfCards.get(position);
        holder.txt1.setText(list_Cards.getNumCard());
        String Date = list_Cards.getXpDate();
        String[] DateString = Date.split("");
        holder.txt2.setText(DateString[0]+DateString[1]+" / "+ DateString[2]+DateString[3]);
        holder.txt3.setText(list_Cards.getThreeDigitNum());
        holder.txt4.setText(list_Cards.getCardNaming());
        holder.txt5.setText(list_Cards.getHouseAddr());

    }

    public int getItemCount(){
        return listOfCards.size();
    }
}
