package ru.mirea.secureapp.data;

import lombok.*;
import ru.mirea.secureapp.models.Document;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentInfo {
    private Long id;
    private String name;
    private String lastEditBy;
    private UserInfo owner;

    public DocumentInfo(Document document){
        this.id = document.getId();
        this.name = document.getName();
        this.lastEditBy = document.getLastEditBy();
        var user = document.getOwner();
        this.owner = new UserInfo(user.getId().intValue(), user.getUsername());
    }
}
