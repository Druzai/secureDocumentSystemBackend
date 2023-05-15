package ru.mirea.secureapp.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AnswerBase {
    private String error = null;
    private Object result = null;
}
