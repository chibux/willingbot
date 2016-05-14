package servlets;

import org.json.JSONObject;

import java.util.Set;

/**
 * @author trierra
 * @date 5/12/16.
 */
public class JsonUtils {

    public static void main(String[] args) {
        String messageReq = "{entry: {\"id\":1562003140766937,\"time\":1463196328281,\"messaging\":[{\"sender\":{\"id\":1059287277465794},\"recipient\":" +
                "{\"id\":1562003140766937},\"message\":{\"attachments\":[{\"payload\":{\"coordinates\":{\"lat\":37.323133654289,\"long\":-121.98682162007}},\"title\":\"Oksana's Location\",\"type\":\"location\",\"url\":\"https://www.facebook.com/l.php?u=https%3A%2F%2Fwww.bing.com%2Fmaps%2Fdefault.aspx%3Fv%3D2%26pc%3DFACEBK%26mid%3D8100%26where1%3D37.323133654289%252C%2B-121.98682162007%26FORM%3DFBKPL1%26mkt%3Den-US&h=QAQGRtbZL&s=1&enc=AZPaqnL11Zms30vr8gy0argiiIx0CRSWeFYal2JuiFloPwee6BG_RxrOim7G14aaIg48FXtEiUMSZmFoawwBdD0TinA64YDHcko17_F3CIO_tw\"}]," +
                "\"mid\":\"mid.1463196327401:01068a02daa9c98654\",\"seq\":267},\"timestamp\":1463196328122}]}}\n";


        JSONObject jsonObject = new JSONObject(messageReq);
        JSONObject entry = jsonObject.getJSONObject("entry");

        JSONObject messaging = entry.getJSONArray("messaging").getJSONObject(0);
        Long time = entry.getLong("time");

        String senderId = messaging.getJSONObject("sender").get("id").toString();
        String recipientId = messaging.getJSONObject("recipient").get("id").toString();
        Set keyset = messaging.keySet();

        if (keyset.contains("message")){
            JSONObject message = messaging.getJSONObject("message");
            if(message.keySet().contains("text")){
                System.out.println(message.getString("text"));
            }
        }

        Long timestamp = messaging.getLong("timestamp");
        JSONObject attachment = messaging.getJSONObject("message").getJSONArray("attachments").getJSONObject(0);
        System.out.println(attachment);

    }

//    public static JSONObject getMessagingObject(JSONObject entry){
//
//    }
//
//    public static JSONObject getSenderObject(JSONObject entry){
//
//    }
//
//    public static JSONObject getMessageObject(JSONObject entry){
//
//    }
//
//    public static JSONObject getCoordinatesObject(JSONObject entry){
//
//    }
//
//    public static  JSONObject getPayloadObject(JSONObject entry){
//
//    }
//
//    public static JSONObject getLocationTitleObject(JSONObject entry){
//
//    }
//
//    public static JSONObject getLoocationUrlObject(JSONObject entry){
//
//    }
}
