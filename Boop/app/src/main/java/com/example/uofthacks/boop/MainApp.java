package com.example.uofthacks.boop;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.regex.Pattern;

public class MainApp extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {
    private static final String TAG = "NFC";
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private Button sendPayment;
    private DataPoint dataMessage = new DataPoint();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        sendPayment = findViewById(R.id.confirmButton);
        dataMessage.setMain(this);

        // initialize NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "enableForegroundMode");

    /*

    // foreground mode gives the current active application priority for reading scanned tags
    IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
    IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
    nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    */
        super.onResume();
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
            Toast.makeText(this, message.getRecords()[0].getPayload().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onButtonClick(View v) {
        sendFile();
        Account[] accounts = dataMessage.getEmails();
        dataMessage.setPrimary(accounts);
        String gmail = null;


    }

    private void sendFile() {
        dataMessage.getEmails();
        if (!nfcAdapter.isEnabled()) {
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
            Toast.makeText(this, "Please enable NFC", Toast.LENGTH_LONG).show();
        } else if (!nfcAdapter.isNdefPushEnabled()) {
            Toast.makeText(this, "Please enable Android Beam.", Toast.LENGTH_SHORT).show();
        } else {
            // SEND FILES HERE!
            Toast.makeText(this, "SENDING FILE!!", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String message = "SAMPLE DEMO";


        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        return ndefMessage;
    }






  }


