package Service;

import Model.Message;
import DAO.MessageDAO;
import java.util.*;

public class MessageService {
    public MessageDAO messageDAO;

    public MessageService() {
        this.messageDAO = new MessageDAO();
    }

    public Optional<Message> createMessage(Message message) {
        return messageDAO.createMessage(message);
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Optional<Message> getMessageById(int message_id) {
        return messageDAO.getMessageById(message_id);
    }

    public Optional<Message> updateMessageById(int message_id, String newMessage) {
        return messageDAO.updateMessageById(message_id, newMessage);
    }

    public Optional<Message> deleteMessageById(int message_id) {
        return messageDAO.deleteMessageById(message_id);
    }

    public List<Message> getMessageByUser(int posted_by) {
        return messageDAO.getMessageByUser(posted_by);
    }
}
