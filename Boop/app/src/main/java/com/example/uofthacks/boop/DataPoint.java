package com.example.uofthacks.boop;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.EditText;


public class DataPoint {
    private String email;
    //private String phoneNumber; todo implement phonenumber later
    private MainApp main;
    private static final String currency = "CAD";
    private Double moneyAmount;


    private void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {

        //This will be the only way to get an email out of this class
        Account [] possibleEmails = getEmails();
        email = setPrimary(possibleEmails);
        setEmail(email);
        return email;
    }
    public Double getAmount(EditText edit){
        moneyAmount = Double.parseDouble(edit.getText().toString());
        return moneyAmount;
    }


    public void setMain(MainApp main) {
        this.main = main;
    }

    private Account[] getEmails() {
        Account[] accounts = null;
        int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(main,
                android.Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(main, new String[]{android.Manifest.permission.GET_ACCOUNTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        String possibleEmail = "";
        try {

            accounts = AccountManager.get(main).getAccountsByType("com.google");
            for (Account account : accounts) {

                possibleEmail += " --> " + account.name + " : " + account.type + " , \n";


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.i("EMAILS", " " + accounts.length);
        return accounts;
    }

    //Alert Window for selecting which email to use
    private String setPrimary(Account[] accounts) {
        final String[] primary = {""};
        CharSequence[] values = new CharSequence[accounts.length];
        int i = 0; //counter

        //Grabs all the email addresses from array of accounts
        for (Account account : accounts) {
            CharSequence accountName = account.name;
            values[i] = accountName;
            i++;
        }

        final CharSequence[] items = values;


        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setTitle("Select Primary Email");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //Sets email address to be sent over NSF contact
                primary[0] = (String) items[item];

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        return primary[0];

    }



}
