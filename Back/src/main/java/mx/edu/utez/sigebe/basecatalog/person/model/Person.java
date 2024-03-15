package mx.edu.utez.sigebe.basecatalog.person.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import mx.edu.utez.sigebe.basecatalog.address.model.Address;
import mx.edu.utez.sigebe.basecatalog.students.model.Student;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "people")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", columnDefinition = "VARCHAR(50) NOT NULL")
    private String name;
    @Column(name = "surname", columnDefinition = "VARCHAR(50) NOT NULL")
    private String surname;
    @Column(name = "curp", columnDefinition = "VARCHAR(18) NOT NULL UNIQUE")
    private String curp;
    @Column(name = "rfc", columnDefinition = "VARCHAR(13)")
    private String rfc;
    @Column(name = "phone", columnDefinition = "VARCHAR(13) NOT NULL")
    private String phone;
    @Column(name = "cellphone", columnDefinition = "VARCHAR(13)")
    private String cellphone;
    @Column(name = "sex", columnDefinition = "CHAR(1) NOT NULL")
    private String sex;
    @Column(name = "birth_date", columnDefinition = "TIMESTAMP NULL")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthDate;

    @Column(name = "nss", columnDefinition = "VARCHAR(11)")
    private String nss;

    @OneToOne
    private Address address;

    @Column(name = "create_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "status", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;

    @OneToOne(mappedBy = "person")
    @JsonIgnore
    private Student student;

    public Person() {
    }

    public Person(String name, String surname, String cellphone, String phone, String rfc, String curp, String sex, Date birthDate, boolean status) {
        this.name = name;
        this.surname = surname;
        this.curp = curp.toUpperCase();
        this.rfc = rfc.toUpperCase();
        this.phone = phone;
        this.cellphone = cellphone;
        this.sex = sex.toUpperCase();
        this.birthDate = birthDate;
        this.status = status;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getNss() {
        return nss;
    }

    public void setNss(String nss) {
        this.nss = nss;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void asignValues(PersonDto dto) {
        this.name = dto.getName();
        this.surname = dto.getSurname();
        this.curp = dto.getCurp();
        this.rfc = dto.getRfc();
        this.phone = dto.getPhone();
        this.cellphone = dto.getCellphone();
        this.sex = dto.getSex();
        this.birthDate = dto.getBirthDate();
    }

    public void asignValuesRegister(PersonDto dto) {
        asignValues(dto);
        this.status = true;
    }

    public void asignValuesModify(PersonDto dto) {
        this.id = dto.getId();
        asignValues(dto);
        this.status = dto.isStatus();
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", curp='" + curp + '\'' +
                ", rfc='" + rfc + '\'' +
                ", phone='" + phone + '\'' +
                ", cellphone='" + cellphone + '\'' +
                ", sex='" + sex + '\'' +
                ", birthDate=" + birthDate +
                ", nss='" + nss + '\'' +
                ", createdAt=" + createdAt +
                ", status=" + status +
                '}';
    }
}
