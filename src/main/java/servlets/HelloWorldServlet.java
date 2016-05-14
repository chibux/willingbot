package servlets;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Map;

/**
 * @author trierra
 * @date 5/10/16.
 */
@WebServlet("/webhook")
public class HelloWorldServlet extends HttpServlet {
    private static String PAGE_TOKEN = "EAAIbsSXN1U8BAM39eXNXAI8Hj5rUwaTG1zOw6Pfdiwfkc2QnAMD4ZBXJOkakDwaHbV5kuouSKZCPAIcZB3xDZBUaZCuuXXWuSt2vuMQRz6y2QDslJQZBI5ZC4ZBz1u8q4RaFzvX8PZCExsicgzvUd19K1cMwLNQgZC6ZAZBWGBFInXt1NQZDZD";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, String[]> requestMap = request.getParameterMap();
        if (!requestMap.isEmpty()) {
            if (requestMap.get("hub.verify_token")[0].equals("mytoken")) {
                PrintWriter writer = response.getWriter();
                writer.println(requestMap.get("hub.challenge")[0]);
            }
        } else {
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
        if (!messaging.isNull("message")) {
            sendMessage(senderId);
        }

    }

    private void sendPost(String message) throws Exception {

        String urlParameters = "access_token=" + PAGE_TOKEN;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpHost https = new HttpHost("graph.facebook.com", 443, "https");
            HttpPost request = new HttpPost();
            request.setURI(new URI("/v2.6/me/messages?" + urlParameters));
            request.setHeader(new BasicHeader("Content-Type", "application/json"));
            request.setEntity(new StringEntity(message));
            CloseableHttpResponse execute = httpClient.execute(https, request);
            System.out.print(execute.getStatusLine().getStatusCode());
            System.out.print(new java.util.Scanner(execute.getEntity().getContent(), "UTF-8").useDelimiter("\\A"));
        }
    }


    public static  void sendGenericMessageWithButton(JSONObject senderId) {

        JSONObject recipient = new JSONObject();
        recipient.put("recipient", senderId);


        JSONObject attachmentObj = new JSONObject();
        attachmentObj.put("type", "template");

        JSONObject payloadObj = new JSONObject();
        payloadObj.put("template_type", "button");
        payloadObj.put("text", "What do you want?");

        JSONArray buttonArray = new JSONArray();
        JSONObject button1 = new JSONObject();
        button1.put("type", "web_url");
        button1.put("url", "https://google.com");
        button1.put("title", "Google it");

        buttonArray.put(button1);

        JSONObject button2 = new JSONObject();
        button1.put("postback", "web_url");
        button1.put("payload", "ASK US");
        button1.put("title", "Ask us");

        buttonArray.put(button2);
        payloadObj.put("buttons", buttonArray);

        attachmentObj.put("payload", payloadObj);
        JSONObject message = new JSONObject();
        message.put("text", attachmentObj.toString());
        recipient.put("message", message);

        System.out.println(recipient.toString());

        try {
           // sendPost(recipient.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void sendMessage(JSONObject senderId) {

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

    }


    public static void main(String[] args) {
        JSONObject userId = new JSONObject();
        userId.put("sender", "USER_ID");
        sendGenericMessageWithButton(userId);
    }

}
