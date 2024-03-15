package mx.edu.utez.sigebe.basecatalog.students.model;

import mx.edu.utez.sigebe.basecatalog.person.model.Person;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class StudentDto {
    @NotNull(groups = {Modify.class,ChangeStatus.class})
    private Long id;
    @NotBlank(groups = {Register.class, Modify.class})
    private String enrollment;
    @NotNull(groups = {Register.class,Modify.class})
    private boolean intern;
    @PositiveOrZero(groups = {Register.class, Modify.class})
    private int quarter;
    @NotBlank(groups = {Register.class, Modify.class})
    private String group;
    @NotNull(groups = {Register.class, Modify.class})
    private Person person;

    public StudentDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public boolean isIntern() {
        return intern;
    }

    public void setIntern(boolean intern) {
        this.intern = intern;
    }

    public int getQuarter() {
        return quarter;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public interface Register{}
    public interface Modify{}
    public interface ChangeStatus{}
}
