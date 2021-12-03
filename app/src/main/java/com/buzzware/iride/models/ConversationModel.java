package com.buzzware.iride.models;

public class ConversationModel {
    String conversationID, id, name, image, lastMessage;

    public ConversationModel() {
    }

    public ConversationModel(String conversationID, String id, String name, String image, String lastMessage) {
        this.conversationID = conversationID;
        this.id = id;
        this.name = name;
        this.image = image;
        this.lastMessage = lastMessage;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
