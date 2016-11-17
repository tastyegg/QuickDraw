package com.quickdraw.database.quickdrawdb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.*;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static String currentUser;

    ImageView check;
    ImageView check2;
    ImageView xmark;
    ImageView xmark2;

    EditText nameInput;
    EditText pinInput;

    private static final String FIREBASE_URL = "https://quickdraw-db.firebaseio.com/";

    private Firebase firebaseRef;

//    TextView statusbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.RED);
            window.setStatusBarColor(Color.rgb(61, 61, 101));
        }
        setContentView(R.layout.activity_login);

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);

        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { login(); }
        });

        findViewById(R.id.dbCreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { createDB(); }
        });

        nameInput = (EditText) findViewById(R.id.nameInput);
        pinInput = (EditText) findViewById(R.id.pinInput);

        check = (ImageView)findViewById(R.id.check);
        check2 = (ImageView)findViewById(R.id.check2);
        check.setVisibility(View.INVISIBLE);
        check2.setVisibility(View.INVISIBLE);

        xmark = (ImageView)findViewById(R.id.xmark);
        xmark2 = (ImageView)findViewById(R.id.xmark2);
        xmark.setVisibility(View.INVISIBLE);
        xmark2.setVisibility(View.INVISIBLE);
    }

    public void createDB() {
        String name = "Anna";
        Random r = new Random();
        int balance = r.nextInt(1000);
        int pinint = r.nextInt(9999);
        String pin = String.valueOf(pinint);
        User user = new User(name, balance, pin);
        firebaseRef.child(name).setValue(user);

        name = "Bob";
        r = new Random();
        balance = r.nextInt(1000);
        pinint = r.nextInt(9999);
        pin = String.valueOf(pinint);
        user = new User(name, balance, pin);
        firebaseRef.child(name).setValue(user);

        name = "Chris";
        r = new Random();
        balance = r.nextInt(1000);
        pinint = r.nextInt(9999);
        pin = String.valueOf(pinint);
        user = new User(name, balance, pin);
        firebaseRef.child(name).setValue(user);

        name = "Dan";
        r = new Random();
        balance = r.nextInt(1000);
        pinint = r.nextInt(9999);
        pin = String.valueOf(pinint);
        user = new User(name, balance, pin);
        firebaseRef.child(name).setValue(user);

        name = "Eric";
        r = new Random();
        balance = r.nextInt(1000);
        pinint = r.nextInt(9999);
        pin = String.valueOf(pinint);
        user = new User(name, balance, pin);
        firebaseRef.child(name).setValue(user);
    }

    public void login() {
        xmark.setVisibility(View.INVISIBLE);
        xmark2.setVisibility(View.INVISIBLE);
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                statusbox = (TextView)findViewById(R.id.statusbox);
                String name = nameInput.getText().toString();
                if (!name.equals("")) {
                    if (dataSnapshot.hasChild(name)) {
                        check.setVisibility(View.VISIBLE);
                        firebaseRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String pin = pinInput.getText().toString();

                                User user = dataSnapshot.getValue(User.class);
                                if (pin.equals(user.getPin())) {
//                                    statusbox.setText("Welcome " + user.getName() + " !");
                                    check2.setVisibility(View.VISIBLE);
                                    currentUser = user.getName();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    pinInput.setText("");
//                                    statusbox.setText("Incorrect pin!");
                                    xmark2.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                    else {
//                        statusbox.setText("The user '" + name + "' does not exist");
                        xmark.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
    }

    public static String getCurrentUser() {
        return currentUser;
    }

}
