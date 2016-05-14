package servlets;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
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

    private static final String  SENDER = "sender";
    private static final String  MESSAGING = "messaging";
    private static final String  ENTRY = "entry";
    private static final String  RECIPIENT = "recipient";
    private static final String  MESSAGE = "message";
    private static final String  TEXT = "text";



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

        String textResponse = "Do you need assistance or can help?";

        JSONObject requestEntry = new JSONObject(requestMessage);
        System.out.println(requestEntry);

        JSONObject entry = requestEntry.getJSONArray(ENTRY).getJSONObject(0);
        JSONObject messaging = entry.getJSONArray(MESSAGING).getJSONObject(0);
        JSONObject senderId = messaging.getJSONObject(SENDER);

        if (!messaging.isNull(MESSAGE)) {
            JSONObject message = messaging.getJSONObject(MESSAGE);
            if (!message.isNull(TEXT) && message.get(TEXT).equals("hello"))
            sendTextResponse(senderId, textResponse);
        }
    }



    private void sendTextResponse(JSONObject senderId, String textResponse) {
        JSONObject response = new JSONObject();
        response.put(RECIPIENT, senderId);
        JSONObject message = new JSONObject();
        message.put(TEXT, textResponse);
        response.put(MESSAGE, message);

        try {
            sendPostResponseToUser(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendPostResponseToUser(String message) throws Exception {

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


}
