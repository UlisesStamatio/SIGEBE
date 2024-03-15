package mx.edu.utez.sigebe.basecatalog.students.model;

import mx.edu.utez.sigebe.basecatalog.person.model.Person;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "enrollment", columnDefinition = "VARCHAR(30) NOT NULL")
    private String enrollment;
    @Column(name = "intern", columnDefinition = "BOOL")
    private boolean intern;
    @Column(name = "quarter", columnDefinition = "INT NOT NULL")
    private int quarter;
    @Column(name = "quarter_group", columnDefinition = "CHAR(1) NOT NULL")
    private String quarter_group;
    @Column(name = "create_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "status", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;
    @OneToOne
    private Person person;

    public Student() {
    }

    public Student(String enrollment, boolean intern, int quarter, String quarter_group, boolean status) {
        this.enrollment = enrollment;
        this.intern = intern;
        this.quarter = quarter;
        this.quarter_group = quarter_group;
        this.status = status;
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
        return quarter_group;
    }

    public void setGroup(String quarter_group) {
        this.quarter_group = quarter_group;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void asignValues(StudentDto dto) {
        this.enrollment = dto.getEnrollment();
        this.quarter_group = dto.getGroup();
        this.intern = dto.isIntern();
        this.quarter = dto.getQuarter();
    }
}
