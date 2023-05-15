package ru.mirea.secureapp.data;

import lombok.*;
import ru.mirea.secureapp.models.User;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentInfo {
    private Long id;
    private String name;
    private String lastEditBy;
    private User owner;
}
