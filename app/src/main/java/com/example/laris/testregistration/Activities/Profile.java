package com.example.laris.testregistration.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.laris.testregistration.Activities.LoginActivity;
import com.example.laris.testregistration.Activities.MenuActivity;
import com.example.laris.testregistration.HelperClasses.UserInformation;
import com.example.laris.testregistration.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Profile extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbRef, userRef, idRef, dobRef, expRef;
    private TextView dateofbirth, txtfullname, txtExp, txtdl_num, txtEmail;
    private ImageView ProfilePic;
    String mo, day, year, firstnm, email, FULL_NAME, abs, date;
    UserInformation user_info;
    FirebaseUser user;
    public static final int GET_FROM_GALLERY = 3;
    private static final int CAMERA_REQUEST = 1888;
    ScrollView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Profile");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        user = firebaseAuth.getCurrentUser();

        dateofbirth = (TextView) findViewById(R.id.textDOB);
        txtfullname = (TextView) findViewById(R.id.textFullName);
        txtEmail = (TextView) findViewById(R.id.textEmail);
        ProfilePic = (ImageView) findViewById(R.id.myImgButton);
        profile = (ScrollView) findViewById(R.id.profile);

        txtfullname.setText("Username: "+user.getDisplayName());
        txtEmail.setText("Email: "+user.getEmail());
        //Read Data from Database
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReferenceFromUrl("https://clicked-d4050.firebaseio.com/");
        userRef = dbRef.child("Users");
        idRef = userRef.child(user.getUid());
        dobRef = idRef.child("dob");
        dobRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String d = dataSnapshot.getValue(String.class);
                String [] date_num = d.split("(?<=\\G.{"+2+"})");
                mo = date_num[0];
                day = date_num[1];
                String [] date_num2 = day.split("(?<=\\G.{"+2+"})");
                day = date_num2[0];
                year = date_num2[1];
                int yr = Integer.parseInt(year);
                int m = Integer.parseInt(day);
                int dy = Integer.parseInt(mo);
                String age_now = getAge(yr, m, dy);
                dateofbirth.setText("Date Of Birth: "+mo+"/"+day+"/"+year+"\nAge:" + age_now);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void btn_imgUpload(View v) {
        startActivityForResult(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST);
        //startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(profile, "SignedOut", Snackbar.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ProfilePic.setImageBitmap(photo);
            //attempt to save imgurl
            //FirebaseDatabase.getInstance().getReference().child("users/" + user.getUid()).child("dob").setValue(getOutputMediaFile(CAMERA_REQUEST));
        }

    }
    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        age++;

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
    /** Create a File for saving an image or video */
//    private static File getOutputMediaFile(int type){
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "MyCameraApp");
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (! mediaStorageDir.exists()){
//            if (! mediaStorageDir.mkdirs()){
//                Log.d("MyCameraApp", "failed to create directory");
//                return null;
//            }
//        }
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//        if (type == CAMERA_REQUEST){
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_"+ timeStamp + ".jpg");
//        } else {
//            return null;
//        }
//        return mediaFile;
//    }
}