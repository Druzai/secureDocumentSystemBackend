package ru.mirea.secureapp.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class InputMessage {
    private String textMessage;
    private boolean toEncode;
}
