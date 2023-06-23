package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class addNewCard extends AppCompatActivity {
    Random random = new Random();
    EditText NameCard;

    TextView bankCardNumber,ExpiryDate,CSVNumber;
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_EMAIL = "MyEmail";

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_card);
        ImageView homeBtn = findViewById(R.id.makecardBtn);
        Button addNewCard = findViewById(R.id.add1);
        NameCard = findViewById(R.id.inputNameCard);
        bankCardNumber = findViewById(R.id.bankCardNum);
        ExpiryDate = findViewById(R.id.expiryDate1);
        CSVNumber = findViewById(R.id.CSVNum);
        String cardnum = String.valueOf((long) (random.nextDouble() * 9_000_000_000_000_000L) + 1_000_000_000_000_000L);
        bankCardNumber.setText(cardnum);//random.nextInt(100));//max - min + 1) + min
        String csvnum = String.valueOf(random.nextInt(900) + 100);
        CSVNumber.setText(csvnum);//random.nextInt(100));//999-100 + 1)+100
        ExpiryDate.setText(LocalDate.now().plusYears(5).format(DateTimeFormatter.ofPattern("MM/yy")));
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

            String CardName = NameCard.getText().toString();
           if (!CardName.isEmpty()){
                sharedPreferences = getSharedPreferences(GLOBAL_PREFS,MODE_PRIVATE);
                String sharedEmail = sharedPreferences.getString(MY_EMAIL,"");
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                Map<String,Object> cardData = new HashMap<>();
                cardData.put("number",bankCardNumber.getText().toString());
                cardData.put("exp",ExpiryDate.getText().toString());
                cardData.put("csv",CSVNumber.getText().toString());
                cardData.put("name",NameCard.getText().toString());
                String id = db.collection("users").document(auth.getUid()).collection("addCard").document().getId();
                db.collection("users").document(auth.getUid()).collection("addCard").document(id).set(cardData);
                Intent intent = new Intent(addNewCard.this,ViewCard.class);
                startActivity(intent);


           }
           else{
                NameCard.setError("Please input a name for your new card!");
           }


//                if(NameCard.length()==0){
//                    Toast.makeText(getApplicationContext(),"Enter A Name.",Toast.LENGTH_SHORT).show();
//                }
//                else if(bankCardNumber.length()!=16){
//                    Toast.makeText(getApplicationContext(),"Bank Card Number must be 16 digits.",Toast.LENGTH_SHORT).show();
//                }
//                else if(CSVNumber.length()!=3){
//                    Toast.makeText(getApplicationContext(),"CSV Number must be 3 digits.",Toast.LENGTH_SHORT).show();
//                }
//                else if(expiryDate.length()!=4){
//                    Toast.makeText(getApplicationContext(),"Expiry date must be 4 digits.",Toast.LENGTH_SHORT).show();
//                }
//                else if ((Integer.valueOf(year) < currentYear || (Integer.valueOf(year) == currentYear && Integer.valueOf(month) < currentMonth))) {
//                    Toast.makeText(getApplicationContext(), "This card has already expired.", Toast.LENGTH_SHORT).show();
//                }
//                else if(inputAddress.length()==0) {
//                    Toast.makeText(getApplicationContext(), "Enter a Billing Address.", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(getApplicationContext(),"Form Validate Successfully!", Toast.LENGTH_SHORT).show();
//                }

            }
        });
//        addNewCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get transaction data
//                EditText cardNumberTitle = findViewById(R.id.bankCardNum);
//                String Number = cardNumberTitle.getText().toString();
//                EditText CSVNumber = findViewById(R.id.CSVNum);
//                String CSV = CSVNumber.getText().toString();
//                EditText ExpiryDate = findViewById(R.id.expiryDate1);
//                String ExpDate = ExpiryDate.getText().toString();
//                EditText NameOnCard = findViewById(R.id.inputNameCard);
//                String CardName = NameOnCard.getText().toString();
//                EditText BillingAddress = findViewById(R.id.inputAddr);
//                String Address = BillingAddress.getText().toString();
//                String type = "";
//                if (Number!=null&&!Number.isEmpty() && !CSV.isEmpty() && !ExpDate.isEmpty() && !CardName.isEmpty() && !Address.isEmpty()){
//                    Card card = new Card(Number, ExpDate, CSV, CardName, Address);
//                    sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
//                    String sharedEmail = sharedPreferences.getString(MY_EMAIL, "");
//                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                    Map<String, Object> cardData = new HashMap<>();
//                    cardData.put("number", card.getNumCard());
//                    cardData.put("exp", card.getXpDate());
//                    cardData.put("csv", card.getThreeDigitNum());
//                    cardData.put("name", card.getCardNaming());
//                    cardData.put("address", card.getHouseAddr());
//                    String id = db.collection("users").document(sharedEmail).collection("addCard").document().getId();
//                    db.collection("users").document(sharedEmail).collection("addCard").document(id).set(cardData);
//                    Intent intent = new Intent(addNewCard.this, ViewCard.class);
//                    startActivity(intent);
//                }
//            }
//        });
    }
}