package com.example.uofthacks.boop;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendMoneyActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback{

  private DataPoint messageData;
  private EditText moneyAmount;
  private TextView output;
  private String email;

  // Status codes:
  // 0: It is neutral; not doing anything
  // 1. It has a job to send the request through NFC
  // 2. It is sending the data to another device
  // 3. The data has been submitted.
  private int status = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_send_money);

    moneyAmount = (EditText) findViewById(R.id.moneyAmount);
    output = (TextView) findViewById(R.id.waitingForRecipientLabel);
    messageData = new DataPoint();
    messageData.setMain(this);
  }

  public void onButtonClick(View v){
    if (status == 0) {
      messageData.getEmail(new DialogInputInterface() {
        @Override
        public void onSelectEmail(String sentEmail) {
          email = sentEmail;
          sendNFCMessage();
        }
      });
    }
    else{
      Toast.makeText(this,
          "Please wait while the money is being sent to the reciever",
          Toast.LENGTH_LONG).show();
    }
  }

  private void sendNFCMessage() {
    NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
    if (mAdapter == null) {
      Toast.makeText(this, "NO NFC", Toast.LENGTH_SHORT).show();
      return;
    }

    if (!mAdapter.isEnabled()) {
      Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
    }
    mAdapter.setNdefPushMessageCallback(this, this);
    mAdapter.setOnNdefPushCompleteCallback(this, this);
    status = 1;
    output.setVisibility(View.VISIBLE);
    output.setText("Please bring your device close to another NFC device");
  }

  @Override
  public NdefMessage createNdefMessage(NfcEvent event) {

    MoneyTransfer transferInfo = new MoneyTransfer();
    transferInfo.setAmount(messageData.getAmount(moneyAmount));
    transferInfo.setCurrency(messageData.getCurrency());
    transferInfo.setEmail(email);
    transferInfo.setPhoneNumber("905 999 9999");

    NdefRecord ndefRecord = NdefRecord.createMime("text/plain", transferInfo.serialize().getBytes());

    Toast.makeText(this,
        "Sending transfer of " + messageData.getCurrency() +
            " " + messageData.getCurrency(), Toast.LENGTH_SHORT
    ).show();
    status = 2;
    output.setText(status);
    return new NdefMessage(ndefRecord);
  }

  @Override
  public void onNdefPushComplete(NfcEvent event) {
    status = 3;
    Toast.makeText(this,
        "Recipient received " + messageData.getCurrency() +
            " " + messageData.getCurrency(), Toast.LENGTH_SHORT
    ).show();
    output.setText("Recipient received " + messageData.getCurrency() +
        " " + messageData.getCurrency());
    status = 0;
  }
}
