package com.example.uofthacks.boop;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NFCTransfer implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

  private static final String TAG = "NFC";
  private Activity activity;

  public NFCTransfer(Activity activity){
    this.activity = activity;
  }

  public boolean transferMoney(MoneyTransfer transfer){
    // initialize NFC
    NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(activity);

    if (!nfcAdapter.isEnabled()){
      activity.startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
      Toast.makeText(activity, "Please enable NFC", Toast.LENGTH_LONG).show();
    }
    else if(!nfcAdapter.isNdefPushEnabled()) {
      Toast.makeText(activity, "Please enable Android Beam.", Toast.LENGTH_SHORT).show();
    }
    else{
      // SEND FILES HERE!
      Toast.makeText(activity, "SENDING FILE!!", Toast.LENGTH_SHORT).show();
      TextView waitingForReceipientText = (TextView) activity.findViewById(R.id.waitingForRecipientLabel);
      waitingForReceipientText.setVisibility(View.VISIBLE);
      //nfcAdapter.setNdefPushMessageCallback(this, this);
      //nfcAdapter.setOnNdefPushCompleteCallback(this, this);
      return true;
    }
    return false;
  }

  public boolean receiveMoney(MoneyTransfer transfer){
    Log.d(TAG, "enableForegroundMode");

    Intent intent = activity.getIntent();
    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
      Parcelable[] rawMessages = intent.getParcelableArrayExtra(
          NfcAdapter.EXTRA_NDEF_MESSAGES);

      NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
      String text = new String(message.getRecords()[0].getPayload());
      Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
      return true;
    }
    return false;
  }

  @Override
  public NdefMessage createNdefMessage(NfcEvent event) {
    String message = "SAMPLE DEMO";
    NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
    NdefMessage ndefMessage = new NdefMessage(ndefRecord);
    return ndefMessage;
  }

  @Override
  public void onNdefPushComplete(NfcEvent event) {
    Toast.makeText(activity, "Money transfer sent", Toast.LENGTH_LONG).show();
    TextView waitingForReceipientText = (TextView) activity.findViewById(R.id.waitingForRecipientLabel);
    waitingForReceipientText.setVisibility(View.INVISIBLE);
    event.nfcAdapter.setNdefPushMessage(null, activity);
    event.nfcAdapter.setNdefPushMessageCallback(null, activity);
    event.nfcAdapter.setOnNdefPushCompleteCallback(null, activity);
  }
}
