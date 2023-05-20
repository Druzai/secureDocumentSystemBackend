package ru.mirea.secureapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.secureapp.data.WSMessage;
import ru.mirea.secureapp.models.*;
import ru.mirea.secureapp.repositories.DocumentRepository;
import ru.mirea.secureapp.repositories.DocumentUserRightsRepository;
import ru.mirea.secureapp.repositories.ParagraphRepository;
import ru.mirea.secureapp.repositories.RoleRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ParagraphRepository paragraphRepository;

    @Autowired
    private DocumentUserRightsRepository documentUserRightsRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CipherService cipherService;

    @Transactional
    public Document createDocument(User owner, String documentName) {
        var document = new Document();
        document.setOwner(owner);
        document.setName(documentName);
        document.setCryptKey(cipherService.getBase64Key());
        documentUserRightsRepository.save(document.addDocumentToUser(owner, new Role(4L, "ROLE_EDITOR")));
        documentRepository.save(document);
        return document;
    }

    public List<Document> getUserDocuments(User user) {
        var docs = documentRepository.findAll();
        return docs.stream().filter(d -> d.getOwner() == user ||
                !d.getDocumentUserRightsList().stream().filter(u -> u.getUser() == user).toList().isEmpty()).toList();
    }

    public List<Document> getDocumentsOfCurrentUser(User user) {
        return documentRepository.findDocumentByOwner(user);
    }

    @Transactional
    public Document getDocument(Long id) {
        var docs = documentRepository.findById(id);
        return docs.orElse(null);
    }

    @Transactional
    public void editDocument(WSMessage wsMessage) {
        var document = documentRepository.findById(wsMessage.getDocumentId().longValue()).get();
        var parList = wsMessage.getContent().stream()
                .map(item -> new Paragraph(item.getNumber(), item.getData(), item.getAlign()))
                .collect(Collectors.toList());
        if (Objects.equals(wsMessage.getCommand(), "create")) {
            document.addParagraphs(parList);
            paragraphRepository.saveAll(parList);
        }
        if (Objects.equals(wsMessage.getCommand(), "delete")) {
            paragraphRepository.deleteAll(document.deleteParagraphs(parList));
        }
        if (Objects.equals(wsMessage.getCommand(), "edit")) {
            paragraphRepository.saveAll(document.editParagraphs(parList));
        }
        if (!Objects.equals(document.getLastEditBy(), wsMessage.getFromUser()))
            document.setLastEditBy(wsMessage.getFromUser());
        documentRepository.save(document);
    }

    @Transactional
    public void checkDocumentRights(Long documentId, User user, Long roleId) {
        var document = documentRepository.findById(documentId).get();
        var role = roleRepository.findById(roleId).get();
        if (document.getDocumentUserRightsList().stream().filter(i -> i.getUser() == user).toList().isEmpty())
            this.addDocumentRights(document, user, role);
        else
            this.editDocumentRights(document, user, role);
    }

    private void addDocumentRights(Document document, User user, Role role) {
        documentUserRightsRepository.save(document.addDocumentToUser(user, role));
        documentRepository.save(document);
    }

    private void editDocumentRights(Document document, User user, Role role) {
        var documentUserRights = document.getDocumentUserRightsList().stream()
                .filter(i -> i.getUser() == user).toList().get(0);
        documentUserRights.setRole(role);
        documentUserRightsRepository.save(documentUserRights);
        documentRepository.save(document);
    }
}
