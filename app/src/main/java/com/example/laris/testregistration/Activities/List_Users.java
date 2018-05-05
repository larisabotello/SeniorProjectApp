package com.example.laris.testregistration.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.laris.testregistration.HelperClasses.ChatRoom;
import com.example.laris.testregistration.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class List_Users extends AppCompatActivity {
    private static int SIGN_IN_REQUEST_CODE = 1;
    FirebaseListAdapter<String> list_online_users;
    RelativeLayout activity_list_users;
    private String room_nm;
    String user_id;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("List of Online Users");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__users);
        room_nm = getIntent().getStringExtra("chatNAME");
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference();

        activity_list_users = (RelativeLayout) findViewById(R.id.activity_list_users);

        //Check if not sign-in then navigate sign-in page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            //Snackbar.make(activity_rooms, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
        }
        //Display Rooms
        displayUsers(room_nm);

    }
    @Override
    public void onBackPressed() {
        //handle info sharing to return to approproate chat
        finish();
        Intent intent = new Intent(this, GroupChatActivity.class);
        intent.putExtra("chatNAME", room_nm);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                room_nm = getIntent().getStringExtra("chatNAME");
                Snackbar.make(activity_list_users, "Success Signed In...Welcome!!", Snackbar.LENGTH_SHORT).show();
                displayUsers(room_nm);
            } else {
                Snackbar.make(activity_list_users, "We couldn't sign you in...Try Again", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    private void displayUsers(String x) {
        ListView list_users = (ListView) findViewById(R.id.online_users);
        FirebaseListOptions<String> options = new FirebaseListOptions.Builder<String>()
                .setQuery(dbRef.child("Rooms/"+x+"/OnlineUsers/"), String.class).setLayout(R.layout.list_online_users).build();
        list_online_users = new FirebaseListAdapter<String>(options) {
            @Override
            protected void populateView(View v, String s, int position) {
                TextView ON_user = (TextView) v.findViewById(R.id.user_name);
                /*ON_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent_c = new Intent(getApplicationContext(), GroupChatActivity.class);
                        intent_c.putExtra("chatNAME", RoomNm.getText().toString());
                        finish();
                        startActivity(intent_c);
                    }
                });*/
               ON_user.setText(s);
            }
        };
        list_users.setAdapter(list_online_users);
    }

    @Override
    protected void onStart() {
        super.onStart();
        list_online_users.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        list_online_users.stopListening();
    }
}
