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
        holder.txt2.setText(list_Cards.getThreeDigitNum());
        holder.txt3.setText(list_Cards.getXpDate());
        holder.txt4.setText(list_Cards.getCardNaming());
        holder.txt5.setText(list_Cards.getHouseAddr());
        holder.txt6.setText("Balance: "+String.valueOf(list_Cards.getBalance()));

    }

    public int getItemCount(){
        return listOfCards.size();
    }
}
