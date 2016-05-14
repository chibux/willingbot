package servlets;

import org.json.JSONObject;

/**
 * @author trierra
 * @date 5/12/16.
 */
public class JsonTest {
    public static void main(String[] args) {
        String messageReq = "{\"object\":\"page\",\"entry\":" +
                "[{\"id\":1562003140766937,\"time\":1463089116181,\"messaging\":" +
                "[{\"sender\":{\"id\":1059287277465794},\"recipient\":" +
                "{\"id\":1562003140766937},\"timestamp\":1463089115964,\"message\":" +
                "{\"mid\":\"mid.1463089115860:924f8507e8f4a16858\",\"seq\":24,\"text\":\"hey\"}}]}]}";


        JSONObject jsonObject = new JSONObject(messageReq);
        JSONObject entry = jsonObject.getJSONArray("entry").getJSONObject(0);
        System.out.println(entry);

        JSONObject messaging = entry.getJSONArray("messaging").getJSONObject(0);
        Long time = entry.getLong("time");
        System.out.println("time " + time);

        String senderId = messaging.getJSONObject("sender").get("id").toString();
        System.out.println("senderId " + senderId);
        String recipientId = messaging.getJSONObject("recipient").get("id").toString();
        System.out.println("recipientId " + recipientId);

        String message = messaging.getJSONObject("message").getString("text");
        System.out.println("message = " + message);
        Long timestamp = messaging.getLong("timestamp");
        System.out.println("timestamp " + timestamp);

    }
}
