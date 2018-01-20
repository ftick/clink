package com.example.uofthacks.boop;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SendMoneyActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback{

  private DataPoint messageData;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_send_money);

    messageData = new DataPoint();//
    messageData.setMain(this);
  }

  public void onButtonClick(View v){
    messageData.getEmail(new DialogInputInterface() {
      @Override
      public void onSelectEmail(String email){
        transferMoney(email);
      }
    });
  }

  private void transferMoney(String email) {
    NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
    if (mAdapter == null) {
      Toast.makeText(this, "NO NFC", Toast.LENGTH_SHORT).show();
      return;
    }

    if (!mAdapter.isEnabled()) {
      Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
    }

    mAdapter.setNdefPushMessageCallback(this, this);
  }

  @Override
  public NdefMessage createNdefMessage(NfcEvent event) {
    String message = "Hello World";
    NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
    NdefMessage ndefMessage = new NdefMessage(ndefRecord);
    return ndefMessage;
  }
}
