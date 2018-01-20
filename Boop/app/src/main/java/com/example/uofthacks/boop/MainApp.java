package com.example.uofthacks.boop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainApp extends AppCompatActivity{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_app);
  }

  public void onSendButtonClick(View view) {
    Intent intent = new Intent(this, SendMoneyActivity.class);
    startActivity(intent);
  }

  public void recieveButtonClick(View view) {
    Intent intent = new Intent(this, RecieveMoneyActivity.class);
    startActivity(intent);
  }
}
