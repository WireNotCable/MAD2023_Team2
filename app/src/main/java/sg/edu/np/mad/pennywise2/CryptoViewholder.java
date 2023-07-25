package sg.edu.np.mad.pennywise2;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.edu.np.mad.pennywise2.models.CryptoModel;

public class CryptoViewholder extends RecyclerView.ViewHolder {

    TextView currencyName, symbol, rate;

    public CryptoViewholder(@NonNull View itemView, ArrayList<CryptoModel> CryptoModels) {
        super(itemView);
        currencyName = itemView.findViewById(R.id.CurrencyName);
        symbol = itemView.findViewById(R.id.Symbol);
        rate = itemView.findViewById(R.id.CurrencyRate);


        symbol.setOnClickListener(new View.OnClickListener(){
                                      public void onClick(View v){
                                          Intent profile = new Intent(itemView.getContext(),CryptoDetails.class);
                                          CryptoModel cryptoObject = CryptoModels.get(getAdapterPosition());
                                          profile.putExtra("CryptoObject",cryptoObject);
                                          itemView.getContext().startActivity(profile);
                                      }

                                  }
        );
    }
}
