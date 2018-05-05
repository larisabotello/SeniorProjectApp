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

import com.example.laris.testregistration.HelperClasses.Message;
import com.example.laris.testregistration.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OneToOneActivity extends AppCompatActivity {

    RelativeLayout activity_personal_chats;
    private static int SIGN_IN_REQUEST_CODE = 1;
    FirebaseListAdapter<Message> adapter2;
    FloatingActionButton fab;
    private FirebaseDatabase database;
    private DatabaseReference dbRef, chatRef;
    String user_id, receiver, sender, type1, type2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        receiver = getIntent().getStringExtra("otherUSER");
        setTitle(receiver);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one);
        sender = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        chatRef = dbRef.child("PersonalChats");

        type1 = sender+"-"+receiver;
        type2 = receiver+"-"+sender;

        activity_personal_chats = (RelativeLayout) findViewById(R.id.activity_one_to_one);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);
                    final String temp = input.getText().toString();
                    //Message Save into Database
                    chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(type1)) {
                                dbRef.child("PersonalChats/" + type1 + "/Messages/").push().setValue(new Message(temp, FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                                displayMessage(type1);
                                adapter2.startListening();
                                // do something...
                            } else if (dataSnapshot.hasChild(type2)) {
                                //Toast.makeText(PersonalChats.this, "TYPE TWO", Toast.LENGTH_SHORT).show();
                                dbRef.child("PersonalChats/" + type2 + "/Messages/").push().setValue(new Message(temp, FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                                displayMessage(type1);
                                adapter2.startListening();
                                // do something...
                            } else {
                                dbRef.child("PersonalChats/" + sender + "-" + receiver + "/Messages/").push().setValue(new Message(temp, FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                                displayMessage(sender + "-" + receiver);
                                adapter2.startListening();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    input.setText("");

            }
        });
        //Check if not sign-in then navigate sign-in page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            //Snackbar.make(activity_rooms, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
        }
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
    //Signout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_personal_chats, "SignedOut", Snackbar.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        return true;
    }
    //Sign-In verification to display rooms
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //receiver = getIntent().getStringExtra("otherUSER");
                Snackbar.make(activity_personal_chats, "Success Signed In...Welcome!!", Snackbar.LENGTH_SHORT).show();
                chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(type1)) {
                            displayMessage(type1);
                            adapter2.startListening();
                            // do something...
                        } else if (dataSnapshot.hasChild(type2)) {
                            //Toast.makeText(PersonalChats.this, "TYPE TWO", Toast.LENGTH_SHORT).show();
                            displayMessage(type2);
                            adapter2.startListening();
                            // do something...
                        } else {
                            // do something...
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            } else {
                Snackbar.make(activity_personal_chats, "We couldn't sign you in...Try Again", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    private void displayMessage(String rc) {
        ListView listOfMessages = (ListView) findViewById(R.id.list_chats);
        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>()
                .setQuery(database.getReference().child("PersonalChats").child(rc).child("Messages"), Message.class).setLayout(R.layout.list_personal).build();
        adapter2 = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(View v, Message model, int position) {
                //Get refernces tot the views of list_personal.xml
                TextView messageText, messageUser, messageTime;
                messageText = (TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMsgText());
                messageUser.setText(model.getMsgUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("MM-dd-yyyy (HH:mm:ss)", model.getMsgTime()));
            }
        };
        listOfMessages.setAdapter(adapter2);
    }
    @Override
    protected void onStart() {
        super.onStart();
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(type1)) {
                    displayMessage(type1);
                    adapter2.startListening();
                    // do something...
                } else if (dataSnapshot.hasChild(type2)) {
                    //Toast.makeText(PersonalChats.this, "TYPE TWO", Toast.LENGTH_SHORT).show();
                    displayMessage(type2);
                    adapter2.startListening();
                    // do something...
                } else {
                    // do something...
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter2.stopListening();
    }
}
