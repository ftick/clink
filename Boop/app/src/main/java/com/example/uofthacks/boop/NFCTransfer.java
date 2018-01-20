package com.example.uofthacks.boop;

import static android.nfc.NdefRecord.createMime;

import android.app.Activity;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.nio.charset.Charset;

public class NFCTransfer implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

  private static final String TAG = "NFC";
  private Activity activity;
  private MoneyTransfer transfer;

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
      Toast.makeText(activity, "SENDING TRANSFER", Toast.LENGTH_SHORT).show();
      TextView waitingForReceipientText = (TextView) activity.findViewById(R.id.waitingForRecipientLabel);
      waitingForReceipientText.setVisibility(View.VISIBLE);
      nfcAdapter.setNdefPushMessageCallback(this, activity);
      nfcAdapter.setOnNdefPushCompleteCallback(this, activity);
      return true;
    }
    return false;
  }

  public boolean receiveMoney(MoneyTransfer transfer){
    this.transfer = transfer;
    Log.d(TAG, "enableForegroundMode");

    Intent intent = activity.getIntent();
    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
      Parcelable[] rawMessages = intent.getParcelableArrayExtra(
          NfcAdapter.EXTRA_NDEF_MESSAGES);

      if (rawMessages.length >= 1) {
        NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
        String text = new String(message.getRecords()[0].getPayload());
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
        return true;
      }
    }
    return false;
  }

  @Override
  public NdefMessage createNdefMessage(NfcEvent event) {
    NdefMessage ndefMessage = null;
    if (transfer != null) {
      String[] contents = {
          transfer.getEmail(),
          transfer.getPhoneNumber(),
          transfer.getCurrency(),
          transfer.getAmount() + ""
      };
      NdefRecord[] ndefRecords = createRecords(contents);
      ndefMessage = new NdefMessage(ndefRecords);
      transfer = null;
    }
    else{
      try {
        ndefMessage = new NdefMessage("empty".getBytes());
      } catch (FormatException e) {
        e.printStackTrace();
      }
    }
    return ndefMessage;
  }

  private NdefRecord[] createRecords(String[] messagesToSendArray){
    NdefRecord[] records = new NdefRecord[messagesToSendArray.length];

    for (int i = 0; i < messagesToSendArray.length; i++){

      byte[] payload = messagesToSendArray[i].getBytes(Charset.forName("UTF-8"));

      NdefRecord record = new NdefRecord(
          NdefRecord.TNF_WELL_KNOWN,  //Our 3-bit Type name format
          NdefRecord.RTD_TEXT,        //Description of our payload
          new byte[0],                //The optional id for our Record
          payload);                   //Our payload for the Record

      records[i] = record;
    }
    return records;
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
