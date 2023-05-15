package ru.mirea.secureapp.data;

import lombok.*;
import ru.mirea.secureapp.models.Document;
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

    public DocumentInfo(Document document){
        this.id = document.getId();
        this.name = document.getName();
        this.lastEditBy = document.getLastEditBy();
        this.owner = document.getOwner();
    }
}
