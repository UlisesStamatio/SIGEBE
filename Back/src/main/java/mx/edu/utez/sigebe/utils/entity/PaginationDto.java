package mx.edu.utez.sigebe.utils.entity;

import javax.validation.constraints.NotNull;

public class PaginationDto {
    private String value;
    @NotNull(groups = {StateGet.class})
    private PaginationType paginationType;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PaginationType getPaginationType() {
        return paginationType;
    }

    public void setPaginationType(PaginationType paginationType) {
        this.paginationType = paginationType;
    }

    @Override
    public String toString() {
        return "PaginationDto{" +
                "value='" + value + '\'' +
                ", paginationType=" + paginationType.toString() +
                '}';
    }

    public interface StateGet {
    }


}
