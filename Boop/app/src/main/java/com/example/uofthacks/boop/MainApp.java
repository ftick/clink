package com.example.uofthacks.boop;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainApp extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback{
  private static final String TAG = "NFC";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_app);
  }

  @Override
  protected void onResume() {
    super.onResume();
    receiveMoney();
  }

  public void onButtonClick(View v){
    sendFile();
  }

  private void receiveMoney(){
    Log.d(TAG, "enableForegroundMode");

    Intent intent = getIntent();
    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
      Parcelable[] rawMessages = intent.getParcelableArrayExtra(
          NfcAdapter.EXTRA_NDEF_MESSAGES);

      NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
      Toast.makeText(this, message.getRecords()[0].getPayload().toString(), Toast.LENGTH_SHORT).show();
    }
  }

  private void sendFile(){
    // initialize NFC
    NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

    if (!nfcAdapter.isEnabled()){
      startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
      Toast.makeText(this, "Please enable NFC", Toast.LENGTH_LONG).show();
    }
    else if(!nfcAdapter.isNdefPushEnabled()) {
      Toast.makeText(this, "Please enable Android Beam.", Toast.LENGTH_SHORT).show();
    }
    else{
      // SEND FILES HERE!
      Toast.makeText(this, "SENDING FILE!!", Toast.LENGTH_SHORT).show();
      TextView waitingForReceipientText = (TextView) findViewById(R.id.waitingForRecipientLabel);
      waitingForReceipientText.setVisibility(View.VISIBLE);
      nfcAdapter.setNdefPushMessageCallback(this, this);
      nfcAdapter.setOnNdefPushCompleteCallback(this, this);
    }
  }

  /**
   * It is called when the phones clinked.
   * @param event
   * @return
   */
  @Override
  public NdefMessage createNdefMessage(NfcEvent event) {
    String message = "SAMPLE DEMO";
    NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
    NdefMessage ndefMessage = new NdefMessage(ndefRecord);
    return ndefMessage;
  }

  /**
   * Is called when the sending of data to the other phone was successful.
   * @param event
   */
  @Override
  public void onNdefPushComplete(NfcEvent event) {
    Toast.makeText(this, "Money transfer sent", Toast.LENGTH_LONG).show();
    TextView waitingForReceipientText = (TextView) findViewById(R.id.waitingForRecipientLabel);
    waitingForReceipientText.setVisibility(View.INVISIBLE);
    event.nfcAdapter.setNdefPushMessage(null, this);
    event.nfcAdapter.setNdefPushMessageCallback(null, this);
    event.nfcAdapter.setOnNdefPushCompleteCallback(null, this);
  }
}
