package ru.mirea.secureapp.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class AnswerBase {
    private String error = null;
    private Object result = null;
}
