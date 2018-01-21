package com.example.uofthacks.boop;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RecieveMoneyActivity extends AppCompatActivity {

  OkHttpClient client = new OkHttpClient();

  private TextView textBox;
  private MoneyTransfer transfer;
  private String bigResponse;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recieve_money);
    textBox = findViewById(R.id.textbox);
    String data = getIntent().getStringExtra("account_info");
    textBox.setText(data);
    transfer = MoneyTransfer.deserialize(data);
    char[] rando = new char[5];
    Random rand = new Random();
    int i = 0;
    for(char charc: rando){
      int num = rand.nextInt(255) + 0;
      rando[i] = (char) num;
      i += 1;
    }
    String key ="" + rando[0] + "" + rando[1] + "" + rando[2] + "" + rando[3] + "" + rando[4];


    addRequest(key, transfer.getEmail(), transfer.getAmount(), transfer.getCurrency());
  }

  public void showData(String data){
    textBox.setText(data);
  }

  void doGetRequest(String url, Map<String,String> header) throws IOException{
    Headers headerbuild = Headers.of(header);
    Request request = new Request.Builder()
        .url(url)
        .headers(headerbuild)
        .build();

    client.newCall(request)
        .enqueue(new Callback() {
          @Override
          public void onFailure(final Call call, IOException e) {
            // Error

            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                // For the example, you can show an error dialog or a toast
                // on the main UI thread
                Log.d(" ERR ", "RUN");
              }
            });
          }

          @Override
          public void onResponse(Call call, final Response response) throws IOException {
            String accessToken = response.body().string();
            accessToken = findStr(accessToken, "access_token", false);
            createBaseHeaders(accessToken);

            lastPart();
          }
        });
  }

  void doPostRequest(String url, Map<String,String> header, String json) throws IOException{
    RequestBody body = RequestBody.create(JSON, json);
    Headers headerbuild = Headers.of(header);
    Request request = new Request.Builder()
        .url(url)
        .headers(headerbuild)
        .post(body)
        .build();

    client.newCall(request)
        .enqueue(new Callback() {
          @Override
          public void onFailure(final Call call, IOException e) {
            // Error

            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                // For the example, you can show an error dialog or a toast
                // on the main UI thread
                Log.d(" ERR ", " NUR ");
              }
            });
          }

          @Override
          public void onResponse(Call call, final Response response) throws IOException {
            String actualResponse = response.body().string();
            Log.d("QWER", actualResponse);
            String url = findStr(actualResponse, "Url", false);
            Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse(url));
            startActivity(intent);
          }
        });
  }

  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  private static final String TOKEN_ENDPOINT = "https://gateway-web.beta.interac.ca/publicapi/api/v1/access-tokens";
  private static final String REQUEST_ENDPOINT = "https://gateway-web.beta.interac.ca/publicapi/api/v2/money-requests/send";

  private static final String ACCESS_ID = "CA1TAhYwnHFsDUqW";
  private static final String REGISTRATION_ID = "CA1ARQBYFM6VFvde";
  private static String SALT_KEY = "dank";
  private static String SECRET_KEY = "vX-k2i-xvhP2bqavGrZEql-fE3XA9odJzsjV18axIsM";

  private static String xName;
  private static String xHandle;
  private static double xAmount;
  private static String xCurrency;

  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'");

  private static Map<String,String> baseHeaders;
  static Random rng = new Random();

  private static boolean DEBUG = false;

  private static String generateRequestId(){
    return Integer.toString(rng.nextInt());
  }

  public static String findStr(String sourceStr, String searchStr, boolean isNum){
    sourceStr = sourceStr.replaceAll(" ","");
    sourceStr = sourceStr.substring(sourceStr.indexOf(searchStr) + searchStr.length()+2);
    if(isNum) {
      String returnThis = "";
      try {
        returnThis = sourceStr.substring(0, sourceStr.indexOf(","));
      } catch(StringIndexOutOfBoundsException a) {
        try {
          returnThis = sourceStr.substring(0, sourceStr.indexOf("}"));
        } catch (StringIndexOutOfBoundsException b) {
          b.printStackTrace();
        }
      }
      return returnThis;
    }
    return sourceStr.substring(1, sourceStr.substring(1).indexOf("\"")+1);
  }

  ///// TODO: STRUCTURES

  void createBaseHeaders(String accessToken){
    baseHeaders = new HashMap<String,String>();
    baseHeaders.put("accessToken", "Bearer " + accessToken);
    baseHeaders.put("thirdPartyAccessId",ACCESS_ID);
    baseHeaders.put("deviceId","deviceId123");
    baseHeaders.put("applicationId","boop");
    baseHeaders.put("Content-Type","application/json");
  }

  static Map<String,String> addContactHeaders() {
    Map<String,String> headers = baseHeaders;
    headers.put("requestId",generateRequestId());
    headers.put("apiRegistrationId", REGISTRATION_ID);
    return headers;
  }

  static String contactJson(String name, String handle) {
    String type = "sms";
    if(handle.contains("@")) type = "email";

    return "{\"contactName\": \"" + name + "\",\n"
        + "  \"language\": \"en\",\n"
        + "  \"notificationPreferences\": [{\n"
        + "      \"handle\": \"" + handle + "\",\n"
        + "      \"handleType\": \"" + type + "\",\n"
        + "      \"active\": true\n"
        + "}]}";
  }

  static String oneJson(String contact, double amount, String currency){
    return "{\n" +
        "  \"sourceMoneyRequestId\": \"sourceid" + generateRequestId() + "\",\n" +
        "  \"requestedFrom\": " + contact + ",\n" +
        "  \"amount\": " + Double.toString(amount) + ",\n" +
        "  \"currency\": \"" + currency + "\",\n" +
        "  \"editableFulfillAmount\": false,\n" +
        "  \"expiryDate\": \"2018-02-10T00:00:00.000Z\",\n" +
        "  \"supressResponderNotifications\": false\n" +
        "}";
  }

  ///// TODO: REST METHODS

  ///// TODO: REQUESTS

  public void addRequest(String name, String handle, double amount, String currency){
    xName = name;
    xHandle = handle;
    xAmount = amount;
    xCurrency = currency;

    String secret = null;
    try {
      secret = encrypt(SECRET_KEY, SALT_KEY);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    if(DEBUG) System.out.println("SECRET: " + secret);

    Map<String, String> mapToken = new HashMap<String, String>();
    mapToken.put("thirdPartyAccessId", ACCESS_ID);
    mapToken.put("secretKey", secret);
    mapToken.put("salt", SALT_KEY);
    mapToken.put("Content-Type", "application/json");

    try{
      Log.d("GETTEM","START");
      doGetRequest(TOKEN_ENDPOINT, mapToken);
      Log.d("GETTEM","DONE");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void lastPart(){
    String json = oneJson(contactJson(xName, xHandle), xAmount, xCurrency);
    Map<String,String> mapRequest = addContactHeaders();

    if(DEBUG) System.out.println(json);

    try {
      Log.d("POSTTEM","START");
      doPostRequest(REQUEST_ENDPOINT, mapRequest, json);
      Log.d("POSTTEM","DONE");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String encrypt(String secret, String salt) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] data = md.digest((salt + ':' + secret).getBytes(StandardCharsets.UTF_8));
    return Base64.encodeToString(data, Base64.DEFAULT);
  }
}
