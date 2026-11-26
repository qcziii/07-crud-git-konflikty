package pl.kurs.library;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DatabaseCleaner implements TestExecutionListener {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void clean() {
        List<String> tables = entityManager.getMetamodel()
                .getEntities()
                .stream()
                .map(EntityType::getName)
                .map(this::toSnakeCase)
                .toList();

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        try {
            for (String table : tables) {
                entityManager.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
            }
        } finally {
            entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        }
    }

    private String toSnakeCase(String name) {
        return name.replaceAll("([a-z][A-Z])", "$1_$2".toLowerCase());
    }
}
