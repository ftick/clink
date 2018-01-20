import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.*;

public class Main {

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

    private static String generateRequestId(){
        return Integer.toString(rng.nextInt());
    }

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

    static Map<String,String> getContactHeaders(int maxResponse, String time, int offset,
                                                String sortBy, String orderBy) {
        Map<String,String> headers = addContactHeaders();

        headers.put("maxResponseItems",Integer.toString(maxResponse));
        headers.put("fromLastUpdatedDate",time);
        headers.put("offset",Integer.toString(offset));
        headers.put("sortBy",sortBy);
        headers.put("orderBy",orderBy);

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
        LocalDate daysAgo = today.plusDays(daysToSend);
        Date expiryDate = Date.from(daysAgo.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        return "{\"sourceMoneyRequestId\": \"sourceid" + generateRequestId() + "\","
                + "  \"requestedFrom\": " + contact + ","
                + "  \"amount\": " + Double.toString(amount) + ","
                + "  \"currency\": \"" + currency + "\","
                + "  \"editableFulfillAmount\": false,"
                + "  \"requesterMessage\": \"string\","
                + "  \"invoice\": {"
                + "    \"invoiceNumber\": \"string\","
                + "    \"dueDate\": \"" + dateFormat.format(expiryDate) + "\""
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

    public static String addContact(String name, String handle){
        String json = contactJson(name, handle);
        Map<String,String> map = addContactHeaders();

        try {
            return post(CONTACT_ENDPOINT, map, json);
        } catch (IOException e) {
            return "Failed to POST Contact";
        }
    }

    public static String getContact(int maxResponses, int daysPast, int offset, String sortBy, String orderBy){
        LocalDate dateBeforeNDays = LocalDate.now(ZoneId.of("America/New_York")).minusDays(daysPast);
        Date date = Date.from(dateBeforeNDays.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        String time = dateFormat.format(date);
        Map<String,String> map = getContactHeaders(maxResponses,time,offset,sortBy,orderBy);

        try {
            return get(CONTACT_ENDPOINT, map);
        } catch (IOException e) {
            return "Failed to GET Contact";
        }
    }

    public static String oneTimeRequest(String name, String handle, double amount, String currency){
        String json = oneJson(contactJson(name, handle), amount, currency, 10);
        Map<String,String> map = addContactHeaders();

        System.out.println(json);

        try {
            return post(REQUEST_ENDPOINT, map, json);
        } catch (IOException e) {
            return "Failed to POST Money";
        }
    }

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
            return stringBuffer.toString();//.substring(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Failed to import secret";
    }

    public static String generateAccessToken(String secret, String salt){
//        MessageDigest md = null;
//        try {
//            md = MessageDigest.getInstance("SHA-256");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        secret = new String(Base64.encodeBase64(md.digest((salt + ':' + secret).getBytes())));
        Map<String,String> map = new HashMap<String,String>();
        map.put("thirdPartyAccessId",ACCESS_ID);
        map.put("secretKey",SECRET_KEY);
        map.put("salt",salt);
        map.put("Content-Type","application/json");

        try {
            return get(TOKEN_ENDPOINT, map);
        } catch (IOException e) {
            return "Failed to GET Access Token";
        }
    }

    public static void main(String[] args) throws IOException {
        Main ex = new Main();

        String salt = "dank";
        String secret = importSecret("secret.txt");

        String accessToken = generateAccessToken(secret, salt);
        accessToken = accessToken.substring(accessToken.indexOf("token\":\"")+8);
        accessToken = accessToken.substring(0, accessToken.indexOf("\""));
        System.out.println("TOKEN: " + accessToken);
        ex.createBaseHeaders(accessToken);

        System.out.println(addContact("testName3", "2267917415"));
        //System.out.println(getContact(5,10,0,"contactName","desc"));
        //System.out.println(oneTimeRequest("Ian", "2267917415",100.00, "CAD"));
    }
}