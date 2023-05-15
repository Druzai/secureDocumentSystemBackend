package ru.mirea.secureapp.models;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "paragraphs")
@Getter
@Setter
public class Paragraph {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;

    @Lob
    @Column
    private String content;

    private String align;

    public Paragraph(Integer number, String content, String align) {
        this.number = number;
        this.content = content;
        this.align = align;
    }

    public Paragraph() {
    }
}
