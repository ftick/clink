package com.example.uofthacks.boop;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainApp extends AppCompatActivity{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_app);
    Intent intent = getIntent();
    String action = intent.getAction();
    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
      processIntent(getIntent());
    }


  }

  public void onSendButtonClick(View view) {
    Intent intent = new Intent(this, SendMoneyActivity.class);
    startActivity(intent);
  }

  public void recieveButtonClick(View view) {
    Intent intent = new Intent(this, RecieveMoneyActivity.class);
    startActivity(intent);
  }

  void processIntent(Intent intent) {
    Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
            NfcAdapter.EXTRA_NDEF_MESSAGES);
    // only one message sent during the beam
    NdefMessage msg = (NdefMessage) rawMsgs[0];

    setContentView(R.layout.activity_recieve_money);
    String accountData = new String(msg.getRecords()[0].getPayload()); //String that contains all data (email, amount, currency)
      RecieveMoneyActivity controller = new RecieveMoneyActivity();
    controller.showData(accountData);

    Log.d("HELLO", new String(msg.getRecords()[0].getPayload()));

  }

}
