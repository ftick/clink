package interac2;

import okhttp3.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class API {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String TOKEN_ENDPOINT = "https://gateway-web.beta.interac.ca/publicapi/api/v1/access-tokens";
    private static final String REQUEST_ENDPOINT = "https://gateway-web.beta.interac.ca/publicapi/api/v2/money-requests/send";

    private static final String ACCESS_ID = "CA1TAvtkKUWraZ5y";
    private static final String REGISTRATION_ID = "CA1ARQUfZMPeZZGs";
    private static String SALT_KEY = "dank";
    private static String SECRET_KEY = "SPPkRbDGP9UcVv6RPDHnnedgSnPctev5Y9vcNeTLxbs";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'");

    static OkHttpClient client = new OkHttpClient();
    private static Map<String,String> baseHeaders;
    static Random rng = new Random();

    private static boolean DEBUG = false;

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

    ///// TODO: REQUESTS

    public static String addRequest(String name, String handle, double amount, String currency){
        String json = oneJson(contactJson(name, handle), amount, currency);
        Map<String,String> map = addContactHeaders();

        if(DEBUG) System.out.println(json);

        try {
            return post(REQUEST_ENDPOINT, map, json);
        } catch (IOException e) {
            return "Failed to POST Money";
        }
    }

    public static String encrypt(String secret, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return new String(Base64.getEncoder().encode(md.digest(
                (salt + ':' + secret).getBytes())));
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
        interac2.API ex = new interac2.API();

        String secret = null;
        try {
            secret = encrypt(SECRET_KEY, SALT_KEY);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if(DEBUG) System.out.println("SECRET: " + secret);

        String accessToken = generateAccessToken(secret, SALT_KEY);
        if(DEBUG) System.out.println("TOKEN: " + accessToken);
        accessToken = findStr(accessToken, "access_token", false);
        if(DEBUG) System.out.println("TOKEN: Bearer " + accessToken);
        ex.createBaseHeaders(accessToken);
    }

    ///// TODO: MAIN

    public static void main(String[] args) throws IOException {
//        DEBUG = true;
        setup();

        String request = addRequest("Ian", "2267917415",100, "CAD");
    }
}