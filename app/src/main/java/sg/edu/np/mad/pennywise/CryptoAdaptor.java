package sg.edu.np.mad.pennywise;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import sg.edu.np.mad.pennywise.models.CryptoModel;

public class CryptoAdaptor extends RecyclerView.Adapter<CryptoViewholder> {

    private ArrayList<CryptoModel> CryptoModels;
    public CryptoAdaptor(ArrayList<CryptoModel> CryptoModels){
        this.CryptoModels = CryptoModels;
    }
    private Context context;
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public CryptoAdaptor(ArrayList<CryptoModel> cryptoModel, Context context) {
        this.CryptoModels = cryptoModel;
        this.context = context;
    }

    public void filterList(ArrayList<CryptoModel> filteredList) {
        CryptoModels = filteredList;
        notifyDataSetChanged();
    }


    public CryptoViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.crypto_recycler, parent, false);
        return new CryptoViewholder(view, CryptoModels);
    }

    public void onBindViewHolder(CryptoViewholder holder, int position) {
        CryptoModel modelCrypto = CryptoModels.get(position);
        holder.currencyName.setText(modelCrypto.getName());
        holder.symbol.setText(modelCrypto.getSymbol());
        holder.rate.setText("$ " + modelCrypto.getPrice());

    }

    @Override
    public int getItemCount() {
        return CryptoModels.size();
    }


}

