package com.example.laris.testregistration.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laris.testregistration.HelperClasses.UserInformation;
import com.example.laris.testregistration.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microblink.activity.Pdf417ScanActivity;
import com.microblink.recognizers.blinkbarcode.pdf417.Pdf417RecognizerSettings;
import com.microblink.recognizers.settings.RecognitionSettings;
import com.microblink.recognizers.settings.RecognizerSettings;
import com.microblink.recognizers.BaseRecognitionResult;
import com.microblink.recognizers.RecognitionResults;
import com.microblink.recognizers.blinkbarcode.pdf417.Pdf417ScanResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText username, pass_verify, editTextEmail, editTextPassword;
    private TextView textViewSignIn, tvDOB, tvF_nm;
    String dob, uname;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase clickedDatabase;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference usersRef = rootRef.child("User");

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            setTitle("User Sign Up");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            //profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        }


        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        username = (EditText) findViewById(R.id.editTextUser_nm);
        pass_verify = (EditText) findViewById(R.id.editTextPassVerify);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        tvDOB = (TextView) findViewById(R.id.DateOfBirth);
        tvF_nm = (TextView) findViewById(R.id.Full_Name);
        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);

        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);

        //Listener to check for
    }
    //////////////////////////////////////////////////////////////////////////////////
    private void registerUser() {
        final String user_nm = username.getText().toString().trim();
        final String pass_ver = pass_verify.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        if (TextUtils.isEmpty(user_nm)) {
            //username is empty
            Toast.makeText(this, "Please Enter a Unique Username", Toast.LENGTH_SHORT).show();
            //stopping the function to execute further
            return;
        }
        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
            //stopping the function to execute further
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            //stopping the function to execute further
            return;
        }
        if (TextUtils.isEmpty(pass_ver)) {
            //password verify is empty
            Toast.makeText(this, "Please Verify Password", Toast.LENGTH_SHORT).show();
            //stopping the function to execute further
            return;
        }
        if (!(pass_ver.equals(password))) {
            pass_verify.setText("");
            editTextPassword.setText("");
            //passwords dont match
            Toast.makeText(this, "Please Re-Enter Password", Toast.LENGTH_SHORT).show();
            //stopping the function to execute further
            return;
        }
        if (tvDOB.getText().equals("")) {
            //dob is empty
            Toast.makeText(this, "Please Scan ID", Toast.LENGTH_SHORT).show();
            //stopping the function to execute further
            return;
        }
        if (tvF_nm.getText().equals("")) {
            //full name is empty
            Toast.makeText(this, "Please Scan ID", Toast.LENGTH_SHORT).show();
            //stopping the function to execute further
            return;
       }
//        usersRef.orderByChild("username").equalTo(user_nm).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot ds) {
//                Toast.makeText(MainActivity.this, "not unique USERNAME", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //CODE TO SAVE INFO
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user_nm).build();

                            user.updateProfile(profileUpdates);

                            UserInformation user_info = new UserInformation(email, user_nm, uname, dob);
                            FirebaseDatabase.getInstance().getReference().child("Users/" + user.getUid()).setValue(user_info);
                            username.setText("");
                            editTextEmail.setText("");
                            pass_verify.setText("");
                            editTextPassword.setText("");
                            finish();
                            //jump into menu
                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Could not Register...please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
        ///////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////
        private static final String LICENSE_KEY = "BVJGRJW6-7GEDL43U-OOKATGGE-7GHNZAC4-N3PP6ZVZ-75TLT73G-XH7WNOP7-M2425ACY";

        private static final int MY_REQUEST_CODE = 1337;

        public void btnScan_click(View v) {
            Intent intent = new Intent(this, Pdf417ScanActivity.class);

            intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, LICENSE_KEY);

            RecognitionSettings settings = new RecognitionSettings();

            Pdf417RecognizerSettings pdf417RecognizerSettings = new Pdf417RecognizerSettings();

            pdf417RecognizerSettings.setNullQuietZoneAllowed(true);
            settings.setRecognizerSettingsArray(new RecognizerSettings[]{pdf417RecognizerSettings});
            intent.putExtra(Pdf417ScanActivity.EXTRAS_RECOGNITION_SETTINGS, settings);
            intent.putExtra(Pdf417ScanActivity.EXTRAS_SHOW_DIALOG_AFTER_SCAN, false);
            // if you want to enable pinch to zoom gesture, add following extra to intent
            intent.putExtra(Pdf417ScanActivity.EXTRAS_ALLOW_PINCH_TO_ZOOM, true);
            // if you want Pdf417ScanActivity to display rectangle where camera is focusing,
            intent.putExtra(Pdf417ScanActivity.EXTRAS_SHOW_FOCUS_RECTANGLE, true);
            startActivityForResult(intent, MY_REQUEST_CODE);
        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == MY_REQUEST_CODE && resultCode == Pdf417ScanActivity.RESULT_OK) {

                RecognitionResults results = data.getParcelableExtra(Pdf417ScanActivity.EXTRAS_RECOGNITION_RESULTS);

                BaseRecognitionResult[] resultArray = results.getRecognitionResults();

                StringBuilder sb = new StringBuilder();

                for(BaseRecognitionResult res : resultArray) {
                    if(res instanceof Pdf417ScanResult) { // check if scan result is result of Pdf417 recognizer
                        Pdf417ScanResult result = (Pdf417ScanResult) res;
                        // getStringData getter will return the string version of barcode contents
                        String barcodeData = result.getStringData();
                        // isUncertain getter will tell you if scanned barcode contains some uncertainties
                        boolean uncertainData = result.isUncertain();

                        //sb.append("PDF417 scan data");
                        if (uncertainData) {
                            sb.append("This scan data is uncertain!\n\n");
                        }
                        String [] x = barcodeData.split("\n");
                        for (String curVal : x){
//                            if (curVal.contains("DBA")){
//                                //dob
//                                exp = curVal;
//                            }
                            if (curVal.contains("DBB")){
                                //expiration
                                dob = curVal;
                            }
                            if (curVal.contains("DCS")){
                                //first name
                                uname = curVal;
                            }
                            if (curVal.contains("DCT")){
                                //last name
                                uname = curVal+ " " + uname;
                            }
                        }
                        dob = dob.replace("DBB","");
                        String temp = dob;
                        String d, m, y;
                        String [] sep = temp.split("(?<=\\G.{"+2+"})");
                        m = sep[0];
                        d = sep[1];
                        String [] sep2 = d.split("(?<=\\G.{"+2+"})");
                        d = sep2[0];
                        y = sep2[1];
                        temp = m+"/"+d+"/"+y;
                        //exp = exp.replace("DBA","");
                        uname = uname.replace("DCT","");
                        uname = uname.replace("DCS","");
                        tvF_nm.setText(uname);
                        tvDOB.setText(temp);
                    }}
            }
        }
        //////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onClick(View view) {
        if (view == buttonRegister) {
            registerUser();
        }
        if (view == textViewSignIn) {
            //will open log in activity here
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

}

