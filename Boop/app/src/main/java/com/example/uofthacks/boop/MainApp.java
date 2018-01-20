package com.example.uofthacks.boop;

import android.accounts.Account;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainApp extends AppCompatActivity{

  private DataPoint messageData;
  private NFCTransfer transferEngine = new NFCTransfer(this);

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
    transferEngine.receiveMoney(null);
  }

  public void onButtonClick(View v){
    MoneyTransfer transferInfo = new MoneyTransfer();
    transferInfo.setEmail(messageData.getEmail());
    transferInfo.setAmount(messageData.getAmount((EditText) findViewById(R.id.moneyAmount)));
    transferInfo.setCurrency(messageData.getCurrency());
    transferEngine.transferMoney(transferInfo);
  }
}
