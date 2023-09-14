package Service;

import Model.Message;
import DAO.APIDAO;
import java.util.*;

public class MessageService {
    public APIDAO apiDAO;

    public MessageService() {
        this.apiDAO = new APIDAO();
    }

    public Message createMessage(Message message) {
        if(message.getMessage_text().isBlank()){
            System.out.println("message should not be blank");
            return null;
        }
        if(message.getMessage_text().length() >= 255){
            System.out.println("message should have less than 255 characters");
            return null;
        }
        if(apiDAO.getAccountById(message.getPosted_by()) == null){
            System.out.println("user does not exist");
            return null;
        }
        return apiDAO.createMessage(message);
    }

    public List<Message> getAllMessages() {
        return apiDAO.getAllMessages();
    }

    public Message getMessageById(int message_id) {
        return apiDAO.getMessageById(message_id);
    }

    public Message updateMessageById(int message_id, String newMessage) {
        if(apiDAO.getMessageById(message_id) == null){
            System.out.println("msg doesnt exist");
            return null;
        }
        if (newMessage.isBlank()){
            System.out.println("msg cannot be blank");
            return null;
        }
        if(newMessage.length() >= 255){
            System.out.println("msg length should be shorter than 255");
            return null;
        }
        return apiDAO.updateMessageById(message_id, newMessage);
    }

    public Message deleteMessageById(int message_id) {
        Message msg = getMessageById(message_id);
        apiDAO.deleteMessageById(message_id);
        return msg;
    }

    public List<Message> getMessageByUser(int posted_by) {
        return apiDAO.getMessageByUser(posted_by);
    }
}
