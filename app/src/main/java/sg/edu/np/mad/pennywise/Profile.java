package sg.edu.np.mad.pennywise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Locale;

public class Profile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "ProfileActivity";
    private static final int REQUEST_IMAGE_GET = 1;

    private ImageView homeBtn;
    private ImageView EditButton;
    private ImageView ProfilePic;
    private Button ShowProfile;

    // Shared preferences
    private static final String GLOBAL_PREFS = "myPrefs";
    private static final String MY_EMAIL = "MyEmail";
    public String MY_UID = "MyUID";
    SharedPreferences sharedPreferences;
    private Uri selectedImageUri;
    private FirebaseAuth auth;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //FOR NAV BAR
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);



        EditButton = findViewById(R.id.Profile_Edit);
        ProfilePic = findViewById(R.id.profile_profilepic);
        ShowProfile = findViewById(R.id.profile_viewprofile);
        SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String uid = prefs.getString(MY_UID, "");
        //Setting profile pic
        String filename = uid + ".jpg";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("profilepic/" + filename);
        getDownloadUrl(imageRef);



        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Editing Profile!!");
                Intent intent = new Intent(Profile.this, EditProfile.class);
                startActivity(intent);
            }
        });

        ShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfile();
            }
        });

        ProfilePic.setOnClickListener(new View.OnClickListener() {// Launch Image Picker
            @Override
            public void onClick(View v) {
                launchImagePicker();
            }
        });
    }

    private void launchImagePicker() {// Open gallery
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {// Getting Image Selected
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                uploadImage(selectedImageUri);
            }
        }
    }

    private void uploadImage(Uri imageUri) {//Uploading Image
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Set a unique filename for the image
        SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String uid= prefs.getString(MY_UID, "");
        String filename = uid + ".jpg";
        StorageReference imageRef = storageRef.child("profilepic/" + filename);

        UploadTask uploadTask = imageRef.putFile(imageUri);

        // Optional: Listen to the upload progress or handle any errors
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image upload successful, retrieve the download URL
            getDownloadUrl(imageRef);
        }).addOnFailureListener(e -> {
            // Handle any errors
            Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void getDownloadUrl(StorageReference imageRef) {
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Handle the download URL, e.g., save it to a database or display the image
            String imageUrl = uri.toString();

            // Use Glide to load the image into the ImageView
            Glide.with(this)
                    .load(imageUrl)
                    .into(ProfilePic);

//            Toast.makeText(this, "Image upload successful. URL: " + imageUrl, Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Handle any errors
            Toast.makeText(this, "Failed to retrieve download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    private void showProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.profilepic, null);
        builder.setView(dialogView);

        // Retrieve the uid from the shared prefs
        SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        String uid = prefs.getString(MY_UID, "");




        //Pass Details
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        usersRef.whereEqualTo("UID", uid)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            //String email = documentSnapshot.getString("email");
                            String phoneNumber = documentSnapshot.getString("phonenum");
                            String email = prefs.getString(MY_EMAIL, "");
                            // Pass the name to the dialog view
                            TextView profilepic_name = dialogView.findViewById(R.id.profilepic_name);
                            String name = email.split("@")[0];
                            profilepic_name.setText(name);

                            // Pass the username to the dialog view
                            TextView profilepic_username = dialogView.findViewById(R.id.profilepic_email);
                            profilepic_username.setText(email.toLowerCase());

                            //Pass phonenumber to dialog view
                            TextView profilepic_hp = dialogView.findViewById(R.id.profilepic_phoneno);
                            profilepic_hp.setText(phoneNumber);
                            Log.v("Phone Number",phoneNumber);

                            // TODO: Use the retrieved data as needed
                        } else {
                            // No matching document found
                            Log.d(TAG, "No document found for the specified email");
                        }
                    } else {
                        // Error retrieving data
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });




        //profile pic
        ImageView profilepic = dialogView.findViewById(R.id.profilepic_profile);
        // Retrieve the uid from the shared prefs
        String filename = uid + ".jpg";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("profilepic/" + filename);
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Handle the download URL, e.g., save it to a database or display the image
            String imageUrl = uri.toString();

            // Use Glide to load the image into the ImageView
            Glide.with(dialogView)
                    .load(imageUrl)
                    .into(profilepic);

//            Toast.makeText(this, "Image upload successful. URL: " + imageUrl, Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Handle any errors
            Toast.makeText(this, "Failed to retrieve download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });



        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Set the selected item every time the activity is brought to the foreground
        navigationView.setCheckedItem(R.id.nav_profile);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_add_transactions) {
            Intent intent = new Intent(Profile.this, AddTransaction.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_view_transactions) {
            Intent intent = new Intent(Profile.this, ViewAllTransactions.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_card) {
            Intent intent = new Intent(Profile.this, ViewCard.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_about) {
            Intent intent = new Intent(Profile.this, AboutUs.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_currency) {
            Intent intent = new Intent(Profile.this, Currency.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(Profile.this, MainActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_transfer) {
            Intent intent = new Intent(Profile.this, Transfer.class);
            startActivity(intent);
        }  else if (item.getItemId() == R.id.nav_set_limit){
            Intent intent = new Intent(Profile.this, SetLimit.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_friends) {
            Intent intent = new Intent(Profile.this, Users.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_logout) {
            sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Profile.this, Login.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


}
