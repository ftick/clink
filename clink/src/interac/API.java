package interac;

import okhttp3.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class API {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String TOKEN_ENDPOINT = "https://gateway-web.beta.interac.ca/publicapi/api/v1/access-tokens";
    private static final String CONTACT_ENDPOINT = "https://gateway-web.beta.interac.ca/publicapi/api/v2/contacts";
    private static final String REQUEST_ENDPOINT = "https://gateway-web.beta.interac.ca/publicapi/api/v2/money-requests/send";

    private static final String ACCESS_ID = "CA1TAvtkKUWraZ5y";
    private static final String REGISTRATION_ID = "CA1ARQUfZMPeZZGs";
    private static final String SECRET_KEY = "2HQXvdnVigQLqiNoF0kN+ikJhwv4WB9r7ZwToK4GLrE=";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'");

    static OkHttpClient client = new OkHttpClient();
    private static Map<String,String> baseHeaders;
    static Random rng = new Random();

    public static boolean DEBUG = false;

    ///// TODO:

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

    static Map<String,String> getContactHeaders(int maxResponse, String time) {
        Map<String,String> headers = addContactHeaders();

        headers.put("maxResponseItems",Integer.toString(maxResponse));
        headers.put("fromLastUpdatedDate",time);

        return headers;
    }

    static String contactJson(String name, String handle) {
        String type = "sms";
        if(handle.contains("@")) type = "email";

        return "{\"contactName\": \"" + name + "\","
                + "  \"language\": \"en\","
                + "  \"notificationPreferences\": [{"
                + "      \"handle\": \"" + handle + "\","
                + "      \"handleType\": \"" + type + "\","
                + "      \"active\": true"
                + "}]}";
    }

    static String oneJson(String contact, double amount, String currency, int daysToSend){
        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));
        Date creationDate = Date.from(today.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        LocalDate daysLater = today.plusDays(daysToSend);
        Date dueDate = Date.from(daysLater.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        LocalDate daysAgo = daysLater.plusDays(daysToSend);
        Date expiryDate = Date.from(daysAgo.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        return "{\"sourceMoneyRequestId\": \"sourceid" + generateRequestId() + "\","
                + "  \"requestedFrom\": " + contact + ","
                + "  \"amount\": " + Double.toString(amount) + ","
                + "  \"currency\": \"" + currency + "\","
                + "  \"editableFulfillAmount\": false,"
                + "  \"requesterMessage\": \"string\","
                + "  \"invoice\": {"
                + "    \"invoiceNumber\": \"" + Integer.toString(rng.nextInt(999999999)) + "\","
                + "    \"dueDate\": \"" + dateFormat.format(dueDate) + "\""
                + "  },"
                + "  \"expiryDate\": \"" + dateFormat.format(expiryDate) + "\","
                + "  \"supressResponderNotifications\": true,"
                + "  \"returnURL\": \"string\",\n"
                + "  \"creationDate\": \"" + dateFormat.format(creationDate) + "\","
                + "  \"status\": 0,"
                + "  \"fulfillAmount\": 20,"
                + "  \"responderMessage\": \"string\","
                + "  \"notificationStatus\": 0}";
    }

    ///// TODO: REST METHODS

    private static String get(String url, Map<String, String> header) throws IOException {
        Headers headerbuild = Headers.of(header);
        Request request = new Request.Builder()
                .url(url)
                .headers(headerbuild)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private static String post(String url, Map<String, String> header, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Headers headerbuild = Headers.of(header);
        Request request = new Request.Builder()
                .url(url)
                .headers(headerbuild)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private static int delete(String url, Map<String, String> header) throws IOException {
        Headers headerbuild = Headers.of(header);
        Request request = new Request.Builder()
                .url(url)
                .headers(headerbuild)
                .delete()
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.code();
        }
    }

    ///// TODO: CONTACTS

    public static String addContact(String name, String handle){
        String json = contactJson(name, handle);
        Map<String,String> map = addContactHeaders();

        try {
            return post(CONTACT_ENDPOINT, map, json);
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to POST Contact";
        }
    }

    public static String getContact(int items, int daysPast){
        LocalDate dateBeforeNDays = LocalDate.now(ZoneId.of("America/New_York")).minusDays(daysPast);
        Date date = Date.from(dateBeforeNDays.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        String time = dateFormat.format(date);
        Map<String,String> map = getContactHeaders(items,time);

        try {
            return get(CONTACT_ENDPOINT, map);
        } catch (IOException e) {
            return "Failed to GET Contact";
        }
    }

    public static boolean removeContact(String contactId){
        Map<String,String> map = addContactHeaders();
        map.put("contactId",contactId);

        try {
            return delete(CONTACT_ENDPOINT+'/'+contactId, map) == 204;
        } catch (IOException e) {
            System.out.println("Failed to DELETE Contact");
            e.printStackTrace();
        }
        return false;
    }

    public static void deleteAllContacts(){
        String contacts = getContact(5,10);
        String firstId = contacts.substring(contacts.indexOf(":")+2);
        firstId = firstId.substring(0,firstId.indexOf("\""));
        while(removeContact(firstId)) {
            if(DEBUG) System.out.println("DELETE " + firstId);
            contacts = getContact(5, 10);
            System.out.println(contacts);
            if(contacts.equals("[]")) break;
            firstId = contacts.substring(contacts.indexOf(":") + 2);
            firstId = firstId.substring(0, firstId.indexOf("\""));
        }
    }

    ///// TODO: REQUESTS

    public static String addRequest(String name, String handle, double amount, String currency){
        String json = oneJson(contactJson(name, handle), amount, currency, 10);
        Map<String,String> map = addContactHeaders();

        if(DEBUG) System.out.println(json);

        try {
            return post(REQUEST_ENDPOINT, map, json);
        } catch (IOException e) {
            return "Failed to POST Money";
        }
    }

    public static String getRequest(String referenceNumber) {
        Map<String,String> map = addContactHeaders();

        try {
            return get(REQUEST_ENDPOINT + "?referenceNumber=" + referenceNumber, map);
        } catch (IOException e) {
            return "Failed to GET Request";
        }
    }

    public static String getRequest(int items, int daysAgo) {
        Map<String,String> map = addContactHeaders();
        LocalDate dateBeforeNDays = LocalDate.now(ZoneId.of("America/New_York")).minusDays(daysAgo);
        Date date = Date.from(dateBeforeNDays.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        map.put("maxResponseItems",Integer.toString(items));
        map.put("fromDate", dateFormat.format(date));
        map.put("toDate", dateFormat.format(new Date()));

        try {
            return get(REQUEST_ENDPOINT, map);
        } catch (IOException e) {
            return "Failed to GET Request";
        }

}

    // TODO: REMOVE Request
    public static boolean removeRequest(){
        return false;
    }

    ///// TODO: SETUP

    public static String importSecret(String filepath){
        try {
            File file = new File(filepath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            fileReader.close();
            if(DEBUG) System.out.println("NOENCRYPT: " + stringBuffer.toString());
            return stringBuffer.toString();//.substring(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Failed to import secret";
    }

    public static String encrypt(String secret, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return new String(Base64.getEncoder().encode(md.digest(
                (salt + ':' + secret.substring(0,secret.indexOf("\n"))).getBytes())));
    }

    public static String generateAccessToken(String secret, String salt){
        Map<String,String> map = new HashMap<String,String>();
        map.put("thirdPartyAccessId",ACCESS_ID);
        map.put("secretKey",secret);
        map.put("salt",salt);
        map.put("Content-Type","application/json");

        try {
            return get(TOKEN_ENDPOINT, map);
        } catch (IOException e) {
            return "Failed to GET Access Token";
        }
    }

    public static void setup(){
        API ex = new API();

        String salt = "dank";
        String secret = null;
        try {
            secret = encrypt(importSecret("secret.txt"), salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if(DEBUG) System.out.println("SECRET: " + secret);

        String accessToken = generateAccessToken(secret, salt);
        if(DEBUG) System.out.println("TOKEN: " + accessToken);
        accessToken = findStr(accessToken, "access_token", false);
        if(DEBUG) System.out.println("TOKEN: Bearer " + accessToken);
        ex.createBaseHeaders(accessToken);
    }

    ///// TODO: MAIN

    public static void main(String[] args) throws IOException {
//        DEBUG = true;
        setup();

//        System.out.println(addContact("testName1", "2267917415"));
//        System.out.println(addRequest("Ian", "2267917415",100, "CAD");
//        deleteAllContacts();

//        String response = addRequest("Ian", "2267917415",100, "CAD");
//        String refNum = findStr(response, "referenceNumber", false);
//        String url = findStr(response, "Url", false);
//        System.out.println(getRequest(refNum));

//        System.out.println(response);
//        System.out.println(refNum);
//        System.out.println(url);
    }
}