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
import android.widget.Toast;

import com.example.laris.testregistration.HelperClasses.Message;
import com.example.laris.testregistration.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GroupChatActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    FirebaseListAdapter<Message> adapter;
    RelativeLayout activity_groupchat;
    FloatingActionButton fab;
    private FirebaseDatabase database;
    private DatabaseReference dbRef, user_Ref;
    String user_id, chat_nm;

    //Signout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            remove_from_database(dbRef.child("Rooms/"+chat_nm+"/OnlineUsers/"+user_id));
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_groupchat, "SignedOut", Snackbar.LENGTH_SHORT).show();

                    finish();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        if (item.getItemId() == R.id.list_users) {
            finish();
            Intent intent = new Intent(getApplicationContext(), List_Users.class);
            intent.putExtra("chatNAME", chat_nm);
            startActivity(intent);
        }
        return true;
    }
    //Attempt to return to previous activity
    @Override
    public void onBackPressed() {
        remove_from_database(dbRef.child("Rooms/"+chat_nm+"/OnlineUsers/"+user_id));
        finish();
        Intent intent = new Intent(this, RoomsActivity.class);
        startActivity(intent);
    }

    //Sign-In verification to display rooms
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                chat_nm = getIntent().getStringExtra("chatNAME");
                Snackbar.make(activity_groupchat, "Success Signed In...Welcome!!", Snackbar.LENGTH_SHORT).show();
                displayChatMessage(chat_nm);
            } else {
                Snackbar.make(activity_groupchat, "We couldn't sign you in...Try Again", Snackbar.LENGTH_SHORT).show();
                remove_from_database(dbRef.child("Rooms/"+chat_nm+"/OnlineUsers/"+user_id));
                finish();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_menu, menu);
        return true;
    }
    ////On Create Method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Grab the extra stuff from intent
        chat_nm = getIntent().getStringExtra("chatNAME");
        setTitle(chat_nm);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);

        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();

        //Add username to room online users' list
        dbRef.child("Rooms/"+chat_nm+"/OnlineUsers/"+user_id).setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        activity_groupchat = (RelativeLayout) findViewById(R.id.activity_groupchat);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);
                //Message Save into Database
                FirebaseDatabase.getInstance().getReference().child("Rooms/"+ chat_nm +"/Messages/").push().setValue(new Message(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
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
        displayChatMessage(chat_nm);
    }
    private void displayChatMessage(String child_name) {
        ListView listOfMessages = (ListView) findViewById(R.id.list_of_messages);
        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>()
                .setQuery(database.getReference().child("Rooms").child(child_name).child("Messages"), Message.class).setLayout(R.layout.list_item).build();

        adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(View v, Message model, int position) {
                //Get refrences tot he views of list_item.xml
                final TextView messageText, messageUser, messageTime;
                messageText = (TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        remove_from_database(dbRef.child("Rooms/"+chat_nm+"/OnlineUsers/"+user_id));
                        //Toast.makeText(GroupChatActivity.this, "Under Construction...", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), OneToOneActivity.class);
                        intent.putExtra("otherUSER", messageUser.getText().toString());
                        startActivity(intent);
                    }
                });

                messageText.setText(model.getMsgText());
                messageUser.setText(model.getMsgUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("MM-dd-yyyy (HH:mm:ss)", model.getMsgTime()));
            }
        };
        listOfMessages.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    public void remove_from_database(DatabaseReference x)
    {
        x.setValue(null);
    }
}