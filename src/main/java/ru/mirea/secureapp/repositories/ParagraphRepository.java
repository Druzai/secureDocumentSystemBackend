package ru.mirea.secureapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mirea.secureapp.models.Paragraph;

@Repository
public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {
}
