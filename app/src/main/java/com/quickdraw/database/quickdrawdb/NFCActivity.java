package com.quickdraw.database.quickdrawdb;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

import static android.nfc.NdefRecord.createMime;

public class NFCActivity extends AppCompatActivity {
    NfcAdapter nfcAdpt;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    TextView tv;
    ImageButton ib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nfc);

        tv = (TextView) findViewById(R.id.NFCDescription);
        ib = (ImageButton) findViewById(R.id.NFCBackButton);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /* NFC */
        nfcAdpt = NfcAdapter.getDefaultAdapter(this);
        // Check if NFC is available on device, abort if not found
        if (nfcAdpt == null) {
            Toast.makeText(this, "Your NFC hardware is not supported", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        CheckNFCEnabled();

        //Pending indent
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] {tagDetected};

        SetSendMessage();
    }

    //Perform Checks during this transmission
    void SetSendMessage() {
        String message = getIntent().getStringExtra("Quickdraw Transaction Details");
        SetMessage(message);

        tv.setText(message + "\nPlease use Android Beam with your closest ATM");
    }

    void SetMessage(String text) {
        //append more ndefrecords
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] {
                        createMime("text/plain", text.getBytes())
                });
        nfcAdpt.setNdefPushMessage(msg, this);
    }

    protected void onResume() {
        super.onResume();

        CheckNFCEnabled();
    }

    void CheckNFCEnabled() {
        // Check if NFC is enabled
        if (!nfcAdpt.isEnabled() || !nfcAdpt.isNdefPushEnabled()) {
            Toast.makeText(getApplicationContext(), "QuickDraw says\n\"Please activate NFC Android Beam\"", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
    }
}
