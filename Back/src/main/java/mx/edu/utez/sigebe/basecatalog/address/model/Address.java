package mx.edu.utez.sigebe.basecatalog.address.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import mx.edu.utez.sigebe.basecatalog.person.model.Person;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "settlement", columnDefinition = "VARCHAR(150)")
    private String settlement;
    @Column(name = "town", columnDefinition = "VARCHAR(150)")
    private String town;
    @Column(name = "cp", columnDefinition = "INT")
    private int cp;
    @Column(name = "street", columnDefinition = "VARCHAR(150)")
    private String street;
    @Column(name = "internal_number", columnDefinition = "VARCHAR(10)")
    private String internalNumber;
    @Column(name = "external_number", columnDefinition = "VARCHAR(10)")
    private String externalNumber;
    @Column(name = "create_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "status", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;

    @OneToOne(mappedBy = "address")
    @JsonIgnore
    private Person person;

    public Address() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSettlement() {
        return settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getInternalNumber() {
        return internalNumber;
    }

    public void setInternalNumber(String internalNumber) {
        this.internalNumber = internalNumber;
    }

    public String getExternalNumber() {
        return externalNumber;
    }

    public void setExternalNumber(String externalNumber) {
        this.externalNumber = externalNumber;
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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setValues(Address address) {
        this.settlement = address.getSettlement();
        this.cp = address.getCp();
        this.street = address.getStreet();
        this.internalNumber = address.getInternalNumber();
        this.externalNumber = address.getExternalNumber();
        this.status = address.isStatus();
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", settlement='" + settlement + '\'' +
                ", cp=" + cp +
                ", street='" + street + '\'' +
                ", internalNumber=" + internalNumber +
                ", externalNumber=" + externalNumber +
                ", createdAt=" + createdAt +
                ", status=" + status +
                '}';
    }
}
