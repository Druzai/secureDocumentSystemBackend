package ru.mirea.secureapp.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InputMessage {
    private String text;
    private boolean toEncode;
}
