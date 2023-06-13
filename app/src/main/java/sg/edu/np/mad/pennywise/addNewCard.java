package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
public class addNewCard extends AppCompatActivity {
    EditText NameCard,bankCardNumber,expiryDate,CSVNumber,inputAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_card);
        ImageView homeBtn = findViewById(R.id.makecardBtn);
        Button addNewCard = findViewById(R.id.add1);
        NameCard = findViewById(R.id.inputNameCard);
        bankCardNumber = findViewById(R.id.bankCardNum);
        expiryDate = findViewById(R.id.expiryDate1);
        CSVNumber = findViewById(R.id.CSVNum);
        inputAddress = findViewById(R.id.inputAddr);

        /*myValidation = new AwesomeValidation(ValidationStyle.BASIC);

        myValidation.addValidation(this,R.id.bankCardNum, "^(\\d{4}[- ]){3}\\d{4}|\\d{16}$",R.string.invalid_banknumber);
        myValidation.addValidation(this,R.id.CSVNum,".{3,}",R.string.invalid_CSV);
        myValidation.addValidation(this,R.id.expiryDate1,"^(0[1-9]|1[0-2])\\/([0-9]{2})$",R.string.invalid_expiryDate);
        myValidation.addValidation(this,R.id.inputAddr,RegexTemplate.NOT_EMPTY,R.string.invalid_Address);
        myValidation.addValidation(this,R.id.inputNameCard,RegexTemplate.NOT_EMPTY,R.string.invalid_Name);*/

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(addNewCard.this, MainActivity.class);
                startActivity(intent);
            }
        });
        addNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NameCard.length()==0){
                    Toast.makeText(getApplicationContext(),"Enter A Name.",Toast.LENGTH_SHORT).show();
                }
                else if(bankCardNumber.length()!=16){
                    Toast.makeText(getApplicationContext(),"Bank Card Number must be 16 digits.",Toast.LENGTH_SHORT).show();
                }
                else if(CSVNumber.length()!=3){
                    Toast.makeText(getApplicationContext(),"CSV Number must be 3 digits.",Toast.LENGTH_SHORT).show();
                }
                else if(expiryDate.length()!=4){
                    Toast.makeText(getApplicationContext(),"Expiry date must be 4 digits.",Toast.LENGTH_SHORT).show();
                }
                else if(inputAddress.length()==0){
                    Toast.makeText(getApplicationContext(),"Enter a Billing Address.",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Form Validate Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(addNewCard.this, ViewCard.class);
                    startActivity(intent);
                }

            }
        });
    }
}