package mx.edu.utez.sigebe.basecatalog.documents.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class DocumentDto {

    @NotNull(groups = {ChangeStatus.class,Modify.class})
    private Long id;

    @NotBlank(groups = {Register.class,Modify.class})
    private String name;

    @NotBlank(groups = {Register.class,Modify.class})
    private String description;

    public DocumentDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public interface Register{}
    public interface Modify{}
    public interface ChangeStatus{}
}
