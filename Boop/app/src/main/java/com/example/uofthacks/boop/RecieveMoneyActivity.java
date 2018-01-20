package com.example.uofthacks.boop;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class RecieveMoneyActivity extends AppCompatActivity {

    private TextView textBox;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recieve_money);
    textBox = findViewById(R.id.textbox);


  }

  public void onResume() {
      super.onResume();
      Intent intent = getIntent();
      String action = intent.getAction();
      if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
          processIntent(getIntent());
      }

  }

  void processIntent(Intent intent){
      Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
              NfcAdapter.EXTRA_NDEF_MESSAGES);
      // only one message sent during the beam
      NdefMessage msg = (NdefMessage) rawMsgs[0];
      // record 0 contains the MIME type, record 1 is the AAR, if present
      textBox.setText(new String(msg.getRecords()[0].getPayload()));

  }

}
