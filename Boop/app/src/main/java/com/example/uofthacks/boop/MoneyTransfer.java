package com.example.uofthacks.boop;

import android.util.Log;

/**
 * A class used to represent info about a money transfer
 * from a person.
 * @version 2.0.
 */
public class MoneyTransfer {

  private String email;
  private String phoneNumber;
  private double amount;
  private String currency;

  /**
   * Returns the email of the source.
   * @return The email, or null.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email of the source.
   * @param email The new email.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Returns the phone number of the source.
   * @return the phone number of the source
   */
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * Sets the phone number of the source.
   * @param phoneNumber the phone number of the source.
   */
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  /**
   * Sets the amount of money to transfer.
   * @param amount the amount of money.
   */
  public void setAmount(double amount){
    this.amount = amount;
  }

  /**
   * Set the currency of the money to transfer.
   * @param currency the currency of the transfer money.
   */
  public void setCurrency(String currency){
    this.currency = currency;
  }

  /**
   * Return the amount of money to transfer.
   * @return the amount to transfer.
   */
  public double getAmount() {
    return amount;
  }

  /**
   * Returns the currency of the transfer money.
   * @return the currency of the transfer money.
   */
  public String getCurrency() {
    return currency;
  }

  /**
   * Serializes the data in this object into a single
   * string object.
   * To deserialize it, pass the serialized string into
   * MoneyTransfer.deserialize()
   * @return the serialized data of this object.
   */
  public String serialize(){
    return email + " | " + phoneNumber + " | " + amount + " | " + currency;
  }

  /**
   * Deserializes a MoneyTransfer object from string to object format
   * @param input The serialized MoneyTransfer object
   * @return The object form of the MoneyTransfer input.
   */
  public static MoneyTransfer deserialize(String input){
    MoneyTransfer transfer = new MoneyTransfer();
    String[] tokens = input.split(" | ");
    transfer.setEmail(tokens[0].trim());
    try {
      transfer.setAmount((double) Double.parseDouble(tokens[4].trim()));
    } catch(NumberFormatException n) {
      Log.d("sad",tokens[0]);
      Log.d("sad",tokens[2]);
    }
    transfer.setCurrency(tokens[6].trim());
    transfer.setPhoneNumber(null);
    return transfer;
  }
}
