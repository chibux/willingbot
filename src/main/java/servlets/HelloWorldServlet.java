package servlets;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.Map;

/**
 * @author trierra
 * @date 5/10/16.
 */
@WebServlet("/webhook")
public class HelloWorldServlet extends HttpServlet {
    private static String PAGE_TOKEN = "EAAIbsSXN1U8BAM39eXNXAI8Hj5rUwaTG1zOw6Pfdiwfkc2QnAMD4ZBXJOkakDwaHbV5kuouSKZCPAIcZB3xDZBUaZCuuXXWuSt2vuMQRz6y2QDslJQZBI5ZC4ZBz1u8q4RaFzvX8PZCExsicgzvUd19K1cMwLNQgZC6ZAZBWGBFInXt1NQZDZD";

    private final String USER_AGENT = "Mozilla/5.0";



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, String[]> requestMap = request.getParameterMap();
        if (!requestMap.isEmpty()){
            if(requestMap.get("hub.verify_token")[0].equals("mytoken")){
                PrintWriter writer = response.getWriter();
                writer.println(requestMap.get("hub.challenge")[0]);
            }
        }else {
            PrintWriter writer = response.getWriter();
            writer.println("error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestMessage = request.getReader().readLine();


        JSONObject jsonObject = new JSONObject(requestMessage);
        JSONObject entry = jsonObject.getJSONArray("entry").getJSONObject(0);
        System.out.println(entry);

        JSONObject messaging = entry.getJSONArray("messaging").getJSONObject(0);
        Long time = entry.getLong("time");
        System.out.println("time " + time);

        JSONObject senderId = messaging.getJSONObject("sender");
//        System.out.println("senderId " + senderId);
//        String recipientId = messaging.getJSONObject("recipient").get("id").toString();
//        System.out.println("recipientId " + recipientId);
//
////        String message = messaging.getJSONObject("message").getString("text");
////        System.out.println("message = " + message);
//        Long timestamp = messaging.getLong("timestamp");
//        System.out.println("timestamp " + timestamp);

        sendMessage(senderId);

    }
    private void sendPost(String message) throws Exception {

        String url = "https://graph.facebook.com/v2.6/me/messages?";

        //add request header
        //con.setRequestProperty("User-Agent", USER_AGENT);
        //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "access_token="+ PAGE_TOKEN;
        url = url.concat(urlParameters);
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.write(message.getBytes("UTF-8"));
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }


    private void sendMessage(JSONObject senderId){

        JSONObject recipient = new JSONObject();
        recipient.put("recipient", senderId);
        JSONObject message = new JSONObject();

        message.put("text", "hello world!");
        recipient.put("message", message);

        System.out.println(recipient.toString());
        try {
            sendPost(recipient.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


//        ClientConfig config = new DefaultClientConfig();
//        SSLContext ctx = null;
//        try {
//            ctx = SSLContext.getInstance("SSL");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        ctx.init(null, myTrustManager, null);
//        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(hostnameVerifier, ctx));
//        Client client = Client.create(config);
//
//
//
//
////        WebResource resource = client.resource("https://graph.facebook.com/v2.6/me/messages").queryParam("access_token", PAGE_TOKEN);

//        ClientResponse response = service.accept("application/json").post(ClientResponse.class, recipient.toString());



//        System.out.println(response.getStatus());
    }


}
