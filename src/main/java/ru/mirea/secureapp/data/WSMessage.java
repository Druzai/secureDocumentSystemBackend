package ru.mirea.secureapp.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;

@Setter
@Getter
@Data
public class WSMessage {
    private Integer documentId;
    private String fromUser;
    private String command;
    private ArrayList<WSContent> content;

    @JsonCreator
    public WSMessage(@JsonProperty("documentId") Integer documentId,
                     @JsonProperty("fromUser") String fromUser,
                     @JsonProperty("command") String command,
                     @JsonProperty("content") ArrayList<WSContent> content) {
        super();
        this.documentId = documentId;
        this.fromUser = fromUser;
        this.command = command;
        this.content = content;
    }
}

