package ru.mirea.secureapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mirea.secureapp.models.DocumentUserRights;
import ru.mirea.secureapp.models.User;

import java.util.List;

@Repository
public interface DocumentUserRightsRepository extends JpaRepository<DocumentUserRights, Long> {
    List<DocumentUserRights> findDocumentUserRightsByUser(User user);
}
