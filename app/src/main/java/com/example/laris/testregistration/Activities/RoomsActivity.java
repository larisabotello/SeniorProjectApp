package com.example.laris.testregistration.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.laris.testregistration.HelperClasses.ChatRoom;
import com.example.laris.testregistration.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RoomsActivity extends AppCompatActivity {
    private static int SIGN_IN_REQUEST_CODE = 1;
    FirebaseListAdapter<ChatRoom> rooms_list;
    RelativeLayout activity_rooms;
    FloatingActionButton fab;
    private FirebaseDatabase database;
    private DatabaseReference dbRef, user_Ref;
    String user_id;


    //Signout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_rooms, "SignedOut", Snackbar.LENGTH_SHORT).show();
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

    //Attempt to return to previous activity
    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    //Sign-In verification to display rooms
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_rooms, "Success Signed In...Welcome!!", Snackbar.LENGTH_SHORT).show();
                displayRooms();
            } else {
                Snackbar.make(activity_rooms, "We couldn't sign you in...Try Again", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    ////On Create Method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("List of Rooms");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();

        activity_rooms = (RelativeLayout) findViewById(R.id.activity_rooms);
        fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input2);
                    //Message Save into Database
                    //FirebaseDatabase.getInstance().getReference().push().setValue(new Message(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                    //FirebaseDatabase.getInstance().getReference().child("Rooms/").setValue(new ChatRoom(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                    FirebaseDatabase.getInstance().getReference().child("Rooms/" + input.getText().toString() + "/").setValue(new ChatRoom(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                    input.setText("");
            }
        });
        //Check if not sign-in then navigate sign-in page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            //Snackbar.make(activity_rooms, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
        }
        //Display Rooms
        displayRooms();
    }
    private void displayRooms() {
        ListView listOfRooms = (ListView) findViewById(R.id.list_of_rooms);
        FirebaseListOptions<ChatRoom> options = new FirebaseListOptions.Builder<ChatRoom>()
                .setQuery(database.getReference().child("Rooms"), ChatRoom.class).setLayout(R.layout.list_rooms).build();
        rooms_list = new FirebaseListAdapter<ChatRoom>(options) {
            @Override
            protected void populateView(View v, ChatRoom s, int position) {
                final TextView RoomNm, UserNm, dateR;
                RoomNm = (TextView) v.findViewById(R.id.name_r);
                UserNm = (TextView) v.findViewById(R.id.user_r);
                dateR = (TextView) v.findViewById(R.id.date_r);
                RoomNm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_c = new Intent(getApplicationContext(), GroupChatActivity.class);
                    intent_c.putExtra("chatNAME", RoomNm.getText().toString());
                    finish();
                    startActivity(intent_c);
                }
            });
                RoomNm.setText(s.getRname());
                UserNm.setText(s.getUser());

                // Format the date before showing it
                dateR.setText(DateFormat.format("MM-dd-yyyy (HH:mm:ss)", s.getNow()));
            }
        };
        listOfRooms.setAdapter(rooms_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        rooms_list.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        rooms_list.stopListening();
    }
}
