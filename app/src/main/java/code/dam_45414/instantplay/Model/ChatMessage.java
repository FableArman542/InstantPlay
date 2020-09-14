package code.dam_45414.instantplay.Model;

import java.util.Date;

public class ChatMessage {

    private String messageId;
    private String messageText;
    private String sender;
    private String receiver;
    private long messageTime;

    public ChatMessage(String messageId, String messageText, String sender, String receiver) {
        this.messageId = messageId;
        this.messageText = messageText;
        this.sender = sender;
        this.receiver = receiver;
        this.messageTime = new Date().getTime();
    }

    public ChatMessage () { }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
