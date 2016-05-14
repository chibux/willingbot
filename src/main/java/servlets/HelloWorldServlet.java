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
import java.util.HashMap;
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

    private static final String HELLO_MESSAGE = "hello";
    private static final String ASSISTANCE_MESSAGE = "assistance";
    private static final String PROVIDE_ASSISTANCE_MESSAGE = "provide assistance";
    private static final String ACCEPT_MESSAGE = "accept";
    private static final String REJECT_MESSAGE = "reject";

    private static final String BOT_TO_HELLO = "Do you need assistance or can help?";
    private static final String BOT_DESCRIBE_ASSISTANCE = "Describe, what kind of assistance you need";
    private static final String BOT_LIST_OF_PERSONS = "Here are a persons who need some assistance. Pick one.";
    private static final String BOT_REQUEST_ACCEPTED = "Your request accepted";
    private static final String BOT_REQUEST_MESSAGE = "You'll be notified when someone can help you";
    private static final String BOT_REQUEST_EXPIRATION_WARNING = "Your will be expired in 2 hours";

    Map <String, Person> providers = new HashMap<>();
    Map<String , Person> needers = new HashMap<>();




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

        String textResponse = "Hi there!";

        JSONObject requestEntry = new JSONObject(requestMessage);
        System.out.println(requestEntry);

        JSONObject entry = requestEntry.getJSONArray(ENTRY).getJSONObject(0);
        JSONObject messaging = entry.getJSONArray(MESSAGING).getJSONObject(0);
        JSONObject senderId = messaging.getJSONObject(SENDER);

        if (!messaging.isNull(MESSAGE)) {
            JSONObject message = messaging.getJSONObject(MESSAGE);

            if (!message.isNull(TEXT)){

                String text = message.get(TEXT).toString();
                switch (text){
                    case HELLO_MESSAGE:
                        sendOptions(senderId, textResponse);
                        break;
                    case ASSISTANCE_MESSAGE: sendOptions(senderId, BOT_DESCRIBE_ASSISTANCE);
                        break;
                    case PROVIDE_ASSISTANCE_MESSAGE: sendOptions(senderId, BOT_LIST_OF_PERSONS);
                        break;
                    default:
                        sendTextResponse(senderId, HELLO_MESSAGE);
                }
            }
            //sendTextResponse(senderId, textResponse);
        }
    }


    private void sendOptions(JSONObject senderId, String textResponse) {

            JSONObject recipient = new JSONObject();
            recipient.put("recipient", senderId);


            JSONObject attachmentObj = new JSONObject();
            attachmentObj.put("type", "template");

            JSONObject payloadObj = new JSONObject();
            payloadObj.put("template_type", "button");
            payloadObj.put("text", textResponse);

            JSONArray buttonArray = new JSONArray();

        JSONObject button1 = new JSONObject();
        button1.put("type", "postback");
        button1.put("payload", ASSISTANCE_MESSAGE);
        button1.put("title", "Need Assistance");

            JSONObject button2 = new JSONObject();
        button2.put("type", "postback");
        button2.put("payload", PROVIDE_ASSISTANCE_MESSAGE);
        button2.put("title", "Provide Assistance");

            buttonArray.put(button1);
            buttonArray.put(button2);
            payloadObj.put("buttons", buttonArray);

            attachmentObj.put("payload", payloadObj);

            JSONObject message = new JSONObject();
            message.put("attachment", attachmentObj);
            recipient.put("message", message);

            System.out.println(recipient.toString());

            try {
                 sendPostResponseToUser(recipient.toString());
            } catch (Exception e) {
                e.printStackTrace();
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
