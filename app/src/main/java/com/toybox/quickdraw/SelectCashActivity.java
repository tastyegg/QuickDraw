package com.toybox.quickdraw;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import static android.nfc.NdefRecord.createMime;

public class SelectCashActivity extends AppCompatActivity {
    NfcAdapter nfcAdpt;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    TextView numberText;
    TextView tv;
    char operation;
    float cashAmount;

    //Write Phone code here
    String writeMessage() {
        String message;
        if (operation == 'W') {
            message = "Withdraw " + cashAmount;
        } else if (operation == 'D') {
            message = "Deposit " + cashAmount;
        } else {
            message = "Blank Message";
        }
        return message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        operation = intent.getCharExtra("com.toybox.quickdraw.OP", '\0');

        setContentView(R.layout.activity_select_cash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv = (TextView) findViewById(R.id.selectCashText);
        numberText = (TextView) findViewById(R.id.numberCount);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetSendMessage();
            }
        });
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException npe) {}

        /* NFC */
        nfcAdpt = NfcAdapter.getDefaultAdapter(this);
        // Check if NFC is available on device, abort if not found
        if (nfcAdpt == null) {
            Toast.makeText(this, "Your NFC hardware is not supported", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Check if NFC is enabled
        while (!nfcAdpt.isNdefPushEnabled()) {
            Toast.makeText(getApplicationContext(), "Please activate NFC Android Beam", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        tv.setText("NFC Android Beam feature is enabled");

        tv.setText(tv.getText() + "\nCurrent Operation: " + operation);

        //Pending indent
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] {tagDetected};

        numberText.requestFocus();
        (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(numberText, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);

        numberText.addTextChangedListener(new TextWatcher() {
            private boolean mWasEdited = false;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mWasEdited) {
                    mWasEdited = false;
                    return;
                }
                try {
                    float amount = Float.valueOf(editable.toString());
                    if (amount * 100 % 1 > 0) {
                        String newValue = Float.toString((int) (amount * 100) / 100.0f);

                        mWasEdited = true;

                        editable.replace(0, editable.length(), newValue);
                    }
                } catch (NumberFormatException nfe) {}
            }
        });
        numberText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    SetSendMessage();
                    return true;
                }
                return false;
            }
        });
    }

    void SetSendMessage() {
        cashAmount = Float.valueOf(numberText.getText().toString());
        SetMessage(writeMessage());
        tv.setText("Message set to " + operation + " " + cashAmount);
    }

    void SetMessage(String text) {
        //append more ndefrecords
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] {
                        createMime("text/plain", text.getBytes())
                });
        nfcAdpt.setNdefPushMessage(msg, this);
    }
}
