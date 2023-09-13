package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
// import io.javalin.http.HttpStatus;

import org.eclipse.jetty.http.HttpStatus;

import java.util.*;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;

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
        // app.get("example-endpoint", this::exampleHandler);

        app.post("/register", registrationHandler);
        app.post("/login", loginHandler);
        app.post("/messages", createMessageHandler);
        app.get("/messages", getAllMessagesHandler);
        app.get("/messages/:message_id", getMessageByIdHandler);
        app.delete("/messages/:message_id", deleteMessageByIdHandler);
        app.patch("/messages/:message_id", updateMessageByIdHandler);
        app.get("/accounts/account_id/messages", getMessageByUserHandler);
        
        //endpoint to 

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    // private void exampleHandler(Context context) {
    //     context.json("sample text");
    // }

    private Handler registrationHandler = ctx -> {
        handleRegistration(ctx);
    };

    private Handler loginHandler = ctx -> {
        handleLogin(ctx);
    };

    private Handler createMessageHandler = ctx -> {
        handleCreateMessage(ctx);
    };

    private Handler getAllMessagesHandler = ctx -> {
        handleGetAllMessages(ctx);
    };

    private Handler getMessageByIdHandler = ctx -> {
        handleGetMessageById(ctx);
    };

    private Handler deleteMessageByIdHandler = ctx -> {
        handleDeleteMessage(ctx);
    };

    private Handler updateMessageByIdHandler = ctx -> {
        handleUpdateMessage(ctx);
    };

    private Handler getMessageByUserHandler = ctx -> {
        handleGetMessageByUser(ctx);
    };

    private void handleRegistration(Context ctx){
        Account account = ctx.bodyAsClass(Account.class);
        Optional<Account> registeredAccount = accountService.registration(account);

        if (registeredAccount.isPresent()) {
            ctx.status(HttpStatus.OK_200);
            ctx.json(registeredAccount.get());
        } else {
            ctx.status(HttpStatus.BAD_REQUEST_400);
        }
        
    }

    private void handleLogin(Context ctx) {
        Account account = ctx.bodyAsClass(Account.class);
        Optional<Account> loggedInAccount = accountService.login(account.getUsername(), account.getPassword());

        if (loggedInAccount.isPresent()) {
            ctx.status(HttpStatus.OK_200);
            ctx.json(loggedInAccount.get());
        } else {
            ctx.status(HttpStatus.UNAUTHORIZED_401);
        }
    }

    private void handleCreateMessage(Context ctx) {
        Message message = ctx.bodyAsClass(Message.class);
        Optional<Message> createdMessage = messageService.createMessage(message);

        if (createdMessage.isPresent()) {
            ctx.status(HttpStatus.OK_200);
            ctx.json(createdMessage.get());
        } else {
            ctx.status(HttpStatus.BAD_REQUEST_400);
        }
    }

    private void handleGetAllMessages(Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    private void handleGetMessageById(Context ctx) {
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        Optional<Message> message = messageService.getMessageById(message_id);

        if (message.isPresent()) {
            ctx.status(HttpStatus.OK_200);
            ctx.json(message.get());
        } else {
            ctx.status(HttpStatus.NOT_FOUND_404);
        }
    }

    private void handleDeleteMessage(Context ctx) {
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        Optional<Message> deletedMessage = messageService.deleteMessageById(message_id);

        if (deletedMessage.isPresent()) {
            ctx.json(deletedMessage.get());
        } else {
            ctx.status(200);
        }
    }

    private void handleUpdateMessage(Context ctx) {
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        String newMessageText = ctx.body();
        Optional<Message> updatedMessage = messageService.updateMessageById(message_id, newMessageText);

        if (updatedMessage.isPresent()) {
            ctx.status(HttpStatus.OK_200);
            ctx.json(updatedMessage.get());
        } else {
            ctx.status(HttpStatus.BAD_REQUEST_400);
        }
    }

    private void handleGetMessageByUser(Context ctx) {
        int account_id = Integer.parseInt(ctx.pathParam("account_id"));
        ctx.json(messageService.getMessageByUser(account_id));
    }



}