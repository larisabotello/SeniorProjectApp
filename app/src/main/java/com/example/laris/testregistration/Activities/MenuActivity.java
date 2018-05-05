package com.example.laris.testregistration.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laris.testregistration.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvProfile, tvRooms, tvPersonal, textViewSignIn;
    private FirebaseAuth firebaseAuth;
    ScrollView activity_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Menu");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        tvProfile = (TextView) findViewById(R.id.tv_profile);
        tvRooms = (TextView) findViewById(R.id.tv_list);
        tvPersonal = (TextView) findViewById(R.id.tv_personal);
        tvProfile.setOnClickListener(this);
        tvPersonal.setOnClickListener(this);
        tvRooms.setOnClickListener(this);

        activity_menu = (ScrollView) findViewById(R.id.activity_menu);


        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_menu, "SignedOut", Snackbar.LENGTH_SHORT).show();
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
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        MenuActivity.super.onBackPressed();
                    }
                }).create().show();
    }
    @Override
    public void onClick(View view) {
        if (view == tvPersonal) {
            Toast.makeText(MenuActivity.this, "Under Construction...", Toast.LENGTH_SHORT).show();
            //finish();
            //startActivity(new Intent(this, PersonalChats.class));
        }
        if (view == tvProfile) {
            //will open profile activity here
            finish();
            startActivity(new Intent(this, Profile.class));
        }
        if (view == tvRooms) {
            //will open chat room activity here
            finish();
            startActivity(new Intent(this, RoomsActivity.class));
        }
    }
}
