package app;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class Product extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public UUID id;

    @Size(max = 100, min = 2)
    @Column(length = 100, nullable = false, unique = true)
    public String title;

    public static Optional<Product> findByIdOptional(UUID id) {
        return find("id", id).firstResultOptional();
    }

    public static boolean exists(Product entity) {
        return find("title", entity.title).count() > 0;
    }

    public static boolean exists(Product entity, UUID id) {
        return find("title = ?1 AND id <> ?2", entity.title, id).count() > 0;
    }
}
