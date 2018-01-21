package com.example.uofthacks.boop;

public class MoneyTransfer {

  private String email;
  private String phoneNumber;
  private double amount;
  private String currency;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setAmount(double amount){
    this.amount = amount;
  }

  public void setCurrency(String currency){
    this.currency = currency;
  }

  public double getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public String serialize(){
    return email + " | " + phoneNumber + " | " + amount + " | " + currency;
  }

  public static MoneyTransfer deserialize(String input){
    MoneyTransfer transfer = new MoneyTransfer();
    String[] tokens = input.split("|");
    transfer.setEmail(tokens[0].trim());
    transfer.setAmount((double) Double.parseDouble(tokens[2].trim()));
    transfer.setCurrency(tokens[3].trim());
    transfer.setPhoneNumber(null);
    return transfer;
  }
}
