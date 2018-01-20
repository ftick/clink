package com.example.uofthacks.boop;

import android.accounts.Account;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainApp extends AppCompatActivity{

  private DataPoint messageData;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_app);

    //methods to get data, attach this to NFCTransfer Message Creator method
    messageData = new DataPoint();
    messageData.setMain(this);

    messageData.getEmail();

  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  public void onButtonClick(View v){
  }
}
