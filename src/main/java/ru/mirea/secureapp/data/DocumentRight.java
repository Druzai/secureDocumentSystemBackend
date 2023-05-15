package ru.mirea.secureapp.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DocumentRight {
    private Long documentId;
    private Long roleId;
    private Long userId;
}
