package com.quickdraw.database.quickdrawdb;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import java.util.*;

import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    TextView balancebox;
    TextView namebox;
    private static final String TAG = "MainActivity";
    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;
    private String currentUser;

    private static final String FIREBASE_URL = "https://quickdraw-db.firebaseio.com/";

    private Firebase firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.RED);
            window.setStatusBarColor(Color.rgb(61, 61, 101));
        }
        setContentView(R.layout.activity_main);

        balancebox = (TextView)findViewById(R.id.balancebox);
        balancebox.setText("balance");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);

//        findViewById(R.id.enterButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) { sendMessage(); }
//        });

//        findViewById(R.id.dataButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) { getBalance(); }
//        });

        findViewById(R.id.wButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { withdraw(); }
        });

        findViewById(R.id.dButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { deposit(); }
        });


        namebox = (TextView)findViewById(R.id.namebox);
        currentUser = LoginActivity.getCurrentUser();
        namebox.setText(currentUser);

        getBalance();

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    public void getBalance() {
//        EditText nameInput2 = (EditText) findViewById(R.id.nameInput2);
//        String name = nameInput2.getText().toString();
//        if (!name.equals("")) {

//            namebox = (TextView)findViewById(R.id.namebox);
//            namebox.setText(name);

            firebaseRef.child(currentUser).addValueEventListener(new com.firebase.client.ValueEventListener() {
                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                    balancebox = (TextView)findViewById(R.id.balancebox);
                    User user = dataSnapshot.getValue(User.class);
                    balancebox.setText("$" + Float.toString(user.getBalance()));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

//            nameInput2.setText("");
//        }
    }

//    public void sendMessage() {
//        EditText nameInput = (EditText) findViewById(R.id.nameInput);
//        String name = nameInput.getText().toString();
//        EditText balanceInput = (EditText) findViewById(R.id.balanceInput);
//        String balanceStr = balanceInput.getText().toString();
//        float balance = Float.parseFloat((balanceStr));
//        Random r = new Random();
//        int pinint = r.nextInt(9999);
//        String pin = String.valueOf(pinint);
//        User user = new User(name, balance, pin);
//        if (!name.equals("")) {
//            firebaseRef.child(name).setValue(user);
//            nameInput.setText("");
//            balanceInput.setText("");
//        }
//    }

    public void withdraw() {
//        EditText nameInput2 = (EditText) findViewById(R.id.nameInput2);
//        String name = nameInput2.getText().toString();
//        if (!name.equals("")) {
//
//            namebox = (TextView) findViewById(R.id.namebox);
//            namebox.setText(name);

        firebaseRef.child(currentUser).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                float balance = user.getBalance();

                EditText amountInput = (EditText) findViewById(R.id.amountInput);
                String amount = amountInput.getText().toString();
                if (amount.equals("")) return;

                //Transfer to NFC activity
                Intent intent = new Intent(MainActivity.this, NFCActivity.class);
                intent.putExtra("Quickdraw Transaction Details", user.getName() + " Withdraw " + Float.parseFloat(amount));
                startActivity(intent);

                /*
                //ATM Code
                float fltamount = Float.parseFloat(amount);

                float newBalance = balance - fltamount;

                user.setBalance(newBalance);

                user.addTransaction(-fltamount);

                firebaseRef.child(user.getName()).setValue(user);
                amountInput.setText("");
                */
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        getBalance();

//
//            nameInput2.setText("");
//        }
    }

    public void deposit() {
//        EditText nameInput2 = (EditText) findViewById(R.id.nameInput2);
//        String name = nameInput2.getText().toString();
//        if (!name.equals("")) {
//
//            namebox = (TextView) findViewById(R.id.namebox);
//            namebox.setText(name);

            firebaseRef.child(currentUser).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    float balance = user.getBalance();

                    EditText amountInput = (EditText) findViewById(R.id.amountInput);
                    String amount = amountInput.getText().toString();
                    if (amount.equals("")) return;

                    Intent intent = new Intent(MainActivity.this, NFCActivity.class);
                    intent.putExtra("Quickdraw Transaction Details", user.getName() + " Deposit " + amount);
                    startActivity(intent);

                    /*
                    float fltamount = Float.parseFloat(amount);

                    float newBalance = balance + fltamount;

                    user.setBalance(newBalance);

                    user.addTransaction(fltamount);

                    firebaseRef.child(user.getName()).setValue(user);
                    amountInput.setText("");
                    */
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        getBalance();

//
//            nameInput2.setText("");
//        }
    }

}
