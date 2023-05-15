package ru.mirea.secureapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name = null;

    private String lastEditBy = null;

    private String cryptKey;

    @OneToOne
    private User owner;

    @OneToMany
    private List<DocumentUserRights> documentUserRightsList = new ArrayList<>();

    @OneToMany
    private List<Paragraph> paragraphList = new ArrayList<>();

    public void addParagraphs(List<Paragraph> paragraphs) {
        paragraphList.addAll(paragraphs);
    }

    public List<Paragraph> editParagraphs(List<Paragraph> paragraphs) {
        var paragraphNumbs = paragraphs.stream().map(Paragraph::getNumber).toList();
        for (Paragraph paragraph : paragraphList) {
            if (paragraphNumbs.contains(paragraph.getNumber())) {
                var parId = paragraphNumbs.indexOf(paragraph.getNumber());
                paragraph.setContent(paragraphs.get(parId).getContent());
                paragraph.setAlign(paragraphs.get(parId).getAlign());
            }
        }
        return this.paragraphList;
    }

    public List<Paragraph> deleteParagraphs(List<Paragraph> paragraphs) {
        var paragraphNumbs = paragraphs.stream().map(Paragraph::getNumber).toList();
        var paragraphToDelete = paragraphList.stream()
                .filter(par -> paragraphNumbs.contains(par.getNumber())).collect(Collectors.toList());
        paragraphList.removeIf(par -> paragraphNumbs.contains(par.getNumber()));
        return paragraphToDelete;
    }

    public DocumentUserRights addDocumentToUser(User user, Role role) {
        var docToUsr = new DocumentUserRights(user, role);
        documentUserRightsList.add(docToUsr);
        return docToUsr;
    }

    public void deleteDocumentToUser(User user, Role role) {
        documentUserRightsList.removeIf(doc -> doc.getUser() == user && doc.getRole() == role);
    }
}
