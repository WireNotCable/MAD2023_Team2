package sg.edu.np.mad.pennywise.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Context;
import android.os.Parcelable;
import android.os.Vibrator;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import sg.edu.np.mad.pennywise.Contact;
import sg.edu.np.mad.pennywise.ContactAdapter;
import sg.edu.np.mad.pennywise.EditProfile;
import sg.edu.np.mad.pennywise.MainActivity;
import sg.edu.np.mad.pennywise.Profile;
import sg.edu.np.mad.pennywise.R;
import sg.edu.np.mad.pennywise.Transfer;


public class Mobile extends Fragment {
    public String GLOBAL_PREFS = "myPrefs";
    public String MY_EMAIL = "MyEmail";
    private static final int REQUEST_CONTACT_PERMISSION = 1;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    SharedPreferences sharedPreferences;
    AutoCompleteTextView MobileNumber;
    Button getContacts;
    EditText Amount;
    TextView MobileWarning;
    EditText Comment;
    TextView AmountWarning;
    Button Transfer;
    Vibrator vibrator;
    ArrayList<Contact> contactList = new ArrayList<>();
    DocumentReference docRef;









    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mobile, container, false);
        MobileNumber = rootView.findViewById(R.id.actv);
        Amount = rootView.findViewById(R.id.Amount);
        Comment = rootView.findViewById(R.id.Comments);
        Transfer = rootView.findViewById(R.id.button);

        if (checkPermission()) {
            readContacts();
        } else {
            requestPermission();
        }
        Transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ContactAdapter adapter = new ContactAdapter(requireContext(), contactList);
        MobileNumber.setAdapter(adapter);

        return rootView;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_CONTACTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSIONS_REQUEST_READ_CONTACTS);
    }

    private void readContacts() {
        Cursor cursor = requireContext().getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Contact contact = new Contact(name, phoneNumber);
                contactList.add(contact);
            }

            cursor.close();


        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContacts();
            } else {
                Toast.makeText(requireContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mobile, container, false);
        MobileNumber = rootView.findViewById(R.id.actv);
        getContacts = rootView.findViewById(R.id.myContacts);
        MobileWarning = rootView.findViewById(R.id.Warning);
        Amount = rootView.findViewById(R.id.Amount);
        AmountWarning = rootView.findViewById(R.id.Note);
        Comment = rootView.findViewById(R.id.Comments);
        Transfer = rootView.findViewById(R.id.button);
        //auth = FirebaseAuth.getInstance();
        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);

        //sharedPreferences = getContext().getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);


        contactList.add(new Contact("test1","12345678"));
        contactList.add(new Contact("test2","23456789"));
        contactList.add(new Contact("Test3","3456789"));
        contactList.add(new Contact("test4","34567890"));
        contactList.add(new Contact("test5","45678901"));
        contactList.add(new Contact("test6", "99999999"));

        try{
            getPhoneContacts();
        }
        catch (SecurityException e){
            e.printStackTrace();
        }

        ContactAdapter adapter = new ContactAdapter(requireContext(),contactList);
        MobileNumber.setAdapter(adapter);
        Transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //extra feature to add country code choosing
                Pattern pattern = Pattern.compile("\\([^()]*\\)");
                Matcher matcher = pattern.matcher( MobileNumber.getText().toString().trim());
                String mobile = matcher.replaceAll("");
                Double amount = Double.parseDouble(String.valueOf(Amount.getText()).trim());
                String comment = String.valueOf(Comment.getText()).trim();


                if (mobile == null || !mobile.matches("\\d+")){
                    MobileWarning.setText("Please input a valid number");
                    MobileWarning.setTextColor(Color.RED);
                   VibratePhone();
                }
                if (amount > 20000){
                    AmountWarning.setTextColor(Color.RED);
                    VibratePhone();
                }
                else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    sharedPreferences = getContext().getSharedPreferences(GLOBAL_PREFS,MODE_PRIVATE);
                    String sharedEmail = sharedPreferences.getString(MY_EMAIL, "");
                    Map<String, Object> transcationData = new HashMap<>();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Log.v("!@#$%^&*","HERE");
                    db.collection("users")
                            .whereEqualTo("phonenum", mobile)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if (task.isSuccessful()) {

                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            transcationData.put("toUID",document.getString("UID"));
                                            transcationData.put("fromUID",auth.getUid());
                                            transcationData.put("type","Mobile");
                                            transcationData.put("amount",amount);
                                            transcationData.put("comments",comment);
                                            transcationData.put("transferDate",new Date());


                                            db.collection("transcation").document(UUID.randomUUID().toString()).set(transcationData);
                                            Toast.makeText(getActivity(),"Transfer successful",Toast.LENGTH_SHORT).show();
                                            getActivity().finish();
                                        }
                                    }
                                    else{
                                        Toast.makeText(getActivity(),"User does not have a registered account with us",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });




                }
            }
        });


        return rootView;
    }





    private void getPhoneContacts(){
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_CONTACTS},0);
        }
        ContentResolver contentResolver = requireContext().getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor  = contentResolver.query(uri,null,null,null,null);
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                @SuppressLint("Range") String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactList.add(new Contact(contactName,contactNumber));
            }
        }
    }
    private void VibratePhone(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (vibrator != null && vibrator.hasVibrator()) {
                // Vibrate for 500 milliseconds
                vibrator.vibrate(100);
            }
        }
    }
}