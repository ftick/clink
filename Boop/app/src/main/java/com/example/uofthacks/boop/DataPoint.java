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
    private double moneyAmount;


    public DataPoint() {

    }

    public static String getCurrency() {
        return currency;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {

        //This will be the only way to get an email out of this class
        String[] possibleEmails = getEmails();
        email = setPrimary(possibleEmails);
        setEmail(email);
        return email;
    }

    public double getAmount(EditText edit) {
        moneyAmount = Double.parseDouble(edit.getText().toString());
        return moneyAmount;
    }


    public void setMain(MainApp main) {
        this.main = main;
    }

    private String[] getEmails() {
        Account[] accounts = {};
        int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(main,
                android.Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(main, new String[]{android.Manifest.permission.GET_ACCOUNTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        String possibleEmails[] = {};
        try {

            accounts = AccountManager.get(main).getAccountsByType("com.google");
            possibleEmails = new String[accounts.length];
            int i = 0;
            for (Account account : accounts) {
                possibleEmails[i] = account.name;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.i("EMAILS", " " + accounts.length);
        return possibleEmails;
    }

    //Alert Window for selecting which email to use
    private String setPrimary(String[] accounts) {

        final CharSequence[] items = {
                "Rajesh", "Mahesh", "Vijayakumar"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setTitle("Make your selection");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                //mDoneButton.setText(items[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        return "hello";

    }
    

}
