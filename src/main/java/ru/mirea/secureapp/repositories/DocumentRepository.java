package ru.mirea.secureapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mirea.secureapp.models.Document;
import ru.mirea.secureapp.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findById(Long id);
    List<Document> findDocumentByOwner(User user);
}
