package com.toybox.quickdraw;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    NfcAdapter nfcAdpt;
    Context thisCxt;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        thisCxt = this;

        Button withdrawButton = (Button) findViewById(R.id.withdraw);
        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetOperationActivity('W');
            }
        });
        Button depositButton = (Button) findViewById(R.id.deposit);
        depositButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetOperationActivity('D');
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

        // Check if NFC is enabled
        CheckNFCEnabled();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    void SetOperationActivity(char operation) {
        Intent intent = new Intent(thisCxt, SelectCashActivity.class);
        intent.putExtra("com.toybox.quickdraw.OP", operation);
        startActivity(intent);
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
