package com.example.uofthacks.boop;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class RecieveMoneyActivity extends AppCompatActivity {

    private TextView textBox;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recieve_money);
    textBox = findViewById(R.id.textbox);
    String data = getIntent().getStringExtra("account_info");
    textBox.setText(data);
  }

  public void onResume() {
      super.onResume();

  }

  public void showData(String data){
      textBox.setText(data);
          

  }

}
