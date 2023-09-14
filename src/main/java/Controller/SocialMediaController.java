package Controller;

import io.javalin.Javalin;
import io.javalin.http.*;
import io.javalin.http.Handler;
//import io.javalin.http.HttpStatus;

import org.eclipse.jetty.http.HttpStatus;

import java.util.*;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */

    AccountService accountService;
    MessageService messageService;
    
    


    public SocialMediaController(){
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }
    
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registrationHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessageByUserHandler);
        
        //endpoint to 

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */


    private void registrationHandler(Context ctx)  throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Account account = objectMapper.readValue(ctx.body(), Account.class);
        System.out.println(account);
        Account registeredAccount = accountService.registration(account);
        System.out.println(registeredAccount);

        if (registeredAccount != null) {
            String jsonString = objectMapper.writeValueAsString(registeredAccount);
            ctx.status(HttpStatus.OK_200);
            ctx.json(jsonString);
        } else {
            ctx.status(HttpStatus.BAD_REQUEST_400);
        }
        
    }

    private void loginHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        Account account = objectMapper.readValue(ctx.body(), Account.class);
        Account loggedInAccount = accountService.login(account);

        if (loggedInAccount != null) {
            String jsonString = objectMapper.writeValueAsString(loggedInAccount);
            ctx.status(HttpStatus.OK_200);
            ctx.json(jsonString);
        } else {
            ctx.status(HttpStatus.UNAUTHORIZED_401);
        }
    }

    private void createMessageHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Message message = objectMapper.readValue(ctx.body(), Message.class);
        Message createdMessage = messageService.createMessage(message);
        if (createdMessage != null) {
            String jsonString = objectMapper.writeValueAsString(createdMessage);
            ctx.status(HttpStatus.OK_200);
            ctx.json(jsonString);
        } else {
            ctx.status(HttpStatus.BAD_REQUEST_400);
        }
    }

    private void getAllMessagesHandler(Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        if (!messages.isEmpty()) {
            ctx.status(HttpStatus.OK_200);
            ctx.json(messages);
        } else {
            ctx.status(HttpStatus.OK_200);
            ctx.json(Collections.emptyList());
        }
        //ctx.json(messages);
    }

    private void getMessageByIdHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.getMessageById(message_id);

        if (message != null) {
            String jsonString = objectMapper.writeValueAsString(message);
            ctx.status(HttpStatus.OK_200);
            ctx.json(jsonString);
        } else {
            ctx.status(HttpStatus.OK_200);
            ctx.result("");
        }
    }

    private void deleteMessageHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message deletedMessage = messageService.deleteMessageById(message_id);
        if (deletedMessage != null) {
            String jsonString = objectMapper.writeValueAsString(deletedMessage);
            ctx.json(jsonString);
        } else {
            ctx.status(200);
        }
    }

    private void updateMessageHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message msg = objectMapper.readValue(ctx.body(), Message.class);
        String newMessageText = msg.getMessage_text();
        Message updatedMessage = messageService.updateMessageById(message_id, newMessageText);
        if (updatedMessage != null) {
            String jsonString = objectMapper.writeValueAsString(updatedMessage);
            ctx.status(HttpStatus.OK_200);
            ctx.json(jsonString);
        } else {
            ctx.status(HttpStatus.BAD_REQUEST_400);
        }
    }

    private void getMessageByUserHandler(Context ctx) {
        ObjectMapper objectMapper = new ObjectMapper();
        int account_id = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> messages = messageService.getMessageByUser(account_id);
        if (!messages.isEmpty()) {
            ctx.status(HttpStatus.OK_200);
            ctx.json(messages);
        } else {
            ctx.status(HttpStatus.OK_200);
            ctx.json(Collections.emptyList());
        }
    }

}

