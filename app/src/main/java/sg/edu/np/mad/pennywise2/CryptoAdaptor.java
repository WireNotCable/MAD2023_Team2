package sg.edu.np.mad.pennywise2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import sg.edu.np.mad.pennywise2.models.CryptoModel;

public class CryptoAdaptor extends RecyclerView.Adapter<CryptoAdaptor.ViewHolder> {

    private ArrayList<CryptoModel> CryptoModels;
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

    @NonNull
    @Override
    public CryptoAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.crypto_recycler, parent, false);
        return new ViewHolder(view); // Pass the inflated view to ViewHolder constructor
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoAdaptor.ViewHolder holder, int position) {
        CryptoModel modelCrypto = CryptoModels.get(position);
        holder.currencyName.setText(modelCrypto.getName());
        holder.symbol.setText(modelCrypto.getSymbol());
        holder.rate.setText("$ " + df2.format(modelCrypto.getPrice()));
    }

    @Override
    public int getItemCount() {
        return CryptoModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView currencyName, symbol, rate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyName = itemView.findViewById(R.id.CurrencyName);
            symbol = itemView.findViewById(R.id.Symbol);
            rate = itemView.findViewById(R.id.CurrencyRate);
        }
    }
}
