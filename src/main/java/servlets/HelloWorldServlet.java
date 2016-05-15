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
import java.util.*;

/**
 * @author trierra
 * @date 5/10/16.
 */
@WebServlet("/webhook")
public class HelloWorldServlet extends HttpServlet {
    private static final String SENDER = "sender";
    private static final String MESSAGING = "messaging";
    private static final String ENTRY = "entry";
    private static final String RECIPIENT = "recipient";
    private static final String MESSAGE = "message";
    private static final String TEXT = "text";
    private static final String POSTBACK = "postback";
    private static final String PAYLOAD = "payload";
    private static final String ATTACHMENTS = "attachments";
    private static final String TRANSFER = "transfer";



    private static final String HELLO_MESSAGE = "hello";
    private static final String ASSISTANCE_NEEDED = "assistance";
    private static final String PROVIDE_ASSISTANCE_MESSAGE = "provide assistance";
    private static final String ACCEPT_MESSAGE = "accept";
    private static final String REJECT_MESSAGE = "reject";

    private static final String BOT_TO_HELLO = "Do you need assistance or can help?";
    private static final String BOT_SEND_LOCATION = "Please, send your location and we'll find somebody to assist you";
    private static final String BOT_DESCRIBE_ASSISTANCE = "Describe, what kind of assistance you need";
    private static final String BOT_LIST_OF_PERSONS = "Here are a persons who need some assistance. Pick one.";
    private static final String BOT_REQUEST_ACCEPTED = "Your request accepted";
    private static final String BOT_REQUEST_MESSAGE = "You'll be notified when someone can help you";
    private static final String BOT_REQUEST_EXPIRATION_WARNING = "Your request will be expired in 2 hours";
    private static final String BOT_GENERAL_MESSAGE = "Hi there! What would you like to do?";

    private static String PAGE_TOKEN = "EAAIbsSXN1U8BAM39eXNXAI8Hj5rUwaTG1zOw6Pfdiwfkc2QnAMD4ZBXJOkakDwaHbV5kuouSKZCPAIcZB3xDZBUaZCuuXXWuSt2vuMQRz6y2QDslJQZBI5ZC4ZBz1u8q4RaFzvX8PZCExsicgzvUd19K1cMwLNQgZC6ZAZBWGBFInXt1NQZDZD";
    Map<Long, Person> providers = new HashMap<>();
    Map<Long, Person> needers = new HashMap<>();


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

//        String textResponse = "Hi there!";

        JSONObject requestEntry = new JSONObject(requestMessage);
        System.out.println(requestEntry);

        JSONObject entry = requestEntry.getJSONArray(ENTRY).getJSONObject(0);
        JSONObject messaging = entry.getJSONArray(MESSAGING).getJSONObject(0);
        JSONObject senderId = messaging.getJSONObject(SENDER);
        Long id = senderId.getLong("id");

        if (!messaging.isNull(MESSAGE)) {
            JSONObject message = messaging.getJSONObject(MESSAGE);

            if (!message.isNull(TEXT)) {

                String text = message.get(TEXT).toString();
                switch (text) {
                    case HELLO_MESSAGE:
                        sendOptions(senderId, BOT_GENERAL_MESSAGE);
                        persistNeeder(id);
                        break;

                    case TRANSFER:
                        contactPersonInNeed(1059287277465794L);
                        break;

                    default:
                        if (needers.containsKey(id)) {
                            //person have to describe what he needs
                            Person person = needers.get(id);
                            person.setAssistanceMessage(message.getString(TEXT));
                            sendTextResponse(senderId, BOT_SEND_LOCATION);
                        } else {
                            sendOptions(senderId, BOT_GENERAL_MESSAGE);
                        }
                }
            } else if (!message.isNull(ATTACHMENTS)) { // if person who needs help sends location, final message to him
                JSONObject attachment = message.getJSONArray(ATTACHMENTS).getJSONObject(0);
                if (!attachment.getJSONObject(PAYLOAD).isNull("coordinates")) {
                    sendTextResponse(senderId, BOT_REQUEST_ACCEPTED);
                    sendTextResponse(senderId, BOT_REQUEST_EXPIRATION_WARNING);
                }
            }
            //sendTextResponse(senderId, textResponse);
        } else if (!messaging.isNull(POSTBACK)) {
            String userPicked = messaging.getJSONObject(POSTBACK).getString(PAYLOAD);
            Long userId = 0l;

            if (userPicked.startsWith(TRANSFER)) {
                int index = userPicked.indexOf(":");
                String user = userPicked.substring(index+1);
                userId = Long.parseLong(user);
                userPicked = userPicked.substring(0, index);
            }
            System.out.println(PAYLOAD + " = ==== == == " + userPicked);
            switch (userPicked) {
                case ASSISTANCE_NEEDED:
                    respondToNeedAssistance(senderId);
                    break;
                case PROVIDE_ASSISTANCE_MESSAGE:
                    respondToProviderAssistance(senderId);
                    break;
                case TRANSFER:
                    contactPersonInNeed(userId);
                    break;

            }
        }
    }


    private List<Person> getMockList() {
        List<Person> personLst = new ArrayList<Person>();

        Person person1 = new Person();
        Long userId1 = 10153722561071553l;
        person1.setId(userId1);
        person1.setAssistanceMessage("Help me with fb bot");
        person1.setImage("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xpf1/v/t1.0-1/s200x200/11903929_10153180684406553_7027855584857045261_n.jpg?oh=d8fc2f68e2336814e4cb3d50f5c721dc&oe=57E2927F&__gda__=1474784519_a80a82f0ed90f0859e8c09430c96aef2");
        person1.setName("Kavitha");


        Person person2 = new Person();
        Long userId2 = 10209410947580516l;
        person2.setId(userId2);
        person2.setAssistanceMessage("Car pool, anyone");
        person2.setImage("https://scontent.xx.fbcdn.net/v/t1.0-1/p200x200/11139377_10209100092089323_54657112189060039_n.jpg?oh=4c5c7a38cbc73df9782a017c5f54c8b7&oe=57E06C55");
        person2.setName("Anne");

        personLst.add(person1);
        personLst.add(person2);

        return personLst;
    }


    private void respondToProviderAssistance(JSONObject senderId) {

        //Build mock data set
        List<Person> personLst = getMockList();

        JSONObject recipient = new JSONObject();
        recipient.put("recipient", senderId);


        JSONObject attachmentObj = new JSONObject();
        attachmentObj.put("type", "template");

        JSONObject payloadObj = new JSONObject();
        payloadObj.put("template_type", "generic");


        JSONArray elementObjArray = new JSONArray();
        JSONObject userObj;
        for (Person person : personLst) {
            userObj = buildUserObj(person);
            elementObjArray.put(userObj);
        }

        payloadObj.put("elements", elementObjArray);

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

    private static JSONObject  buildUserObj(Person person) {

        JSONObject obj = new JSONObject();

        obj.put("title", person.getName());
        obj.put("subtitle", person.getAssistanceMessage());
        obj.put("image_url", person.getImage());


        String userId = TRANSFER + ":" + person.getId();

        JSONArray buttonArray = new JSONArray();
        JSONObject button1 = new JSONObject();
        button1.put("type", "postback");
        button1.put("payload", userId);
        button1.put("title", "Select Task");
        buttonArray.put(button1);

        obj.put("buttons", buttonArray);

        return obj;
    }

    private void respondToNeedAssistance(JSONObject senderId) {
        sendTextResponse(senderId, BOT_DESCRIBE_ASSISTANCE);

    }

    private void persistNeeder(Long senderId) {
        Person person = new Person();
        person.setId(senderId);
        needers.put(senderId, person);
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
        button1.put("payload", ASSISTANCE_NEEDED);
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

    private void contactPersonInNeed(Long idPersonInNeed) {
        JSONObject sender = new JSONObject();
        sender.put("id", idPersonInNeed);

        sendTextResponse(sender, "I can provide help: " +
                "https://www.facebook.com/profile.php?id=100001535955409");
    }

}
