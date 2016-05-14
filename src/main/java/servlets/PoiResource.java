package servlets;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author trierra
 * @date 5/11/16.
 */

@Path("/poi")
public class PoiResource {

    private static String PAGE_TOKEN = "EAAIbsSXN1U8BAM39eXNXAI8Hj5rUwaTG1zOw6Pfdiwfkc2QnAMD4ZBXJOkakDwaHbV5kuouSKZCPAIcZB3xDZBUaZCuuXXWuSt2vuMQRz6y2QDslJQZBI5ZC4ZBz1u8q4RaFzvX8PZCExsicgzvUd19K1cMwLNQgZC6ZAZBWGBFInXt1NQZDZD";


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String webHook(@QueryParam("hub.verify_token") String myVefiryToken,
                         @QueryParam("hub.challenge") String hubChallenge) {
        if (myVefiryToken.equals("myawesomebot")){
            return hubChallenge;
        }
        return "error";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response messageListener(){
//        formParams.get(0);
        return Response.status(200).entity("ok").build();
    }

    public static class TestJson {
        String name;
        String id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}