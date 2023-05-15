package ru.mirea.secureapp.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class WSContent {
    private Integer number;
    private String data;
    private String align;

    @JsonCreator
    public WSContent(@JsonProperty("number") Integer number,
                     @JsonProperty("data") String data,
                     @JsonProperty("align") String align) {
        super();
        this.number = number;
        this.data = data;
        this.align = align;
    }
}