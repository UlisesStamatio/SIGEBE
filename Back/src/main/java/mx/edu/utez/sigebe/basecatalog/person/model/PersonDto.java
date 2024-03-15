package mx.edu.utez.sigebe.basecatalog.person.model;

import mx.edu.utez.sigebe.basecatalog.address.model.Address;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PersonDto {
    @NotNull(groups = {Modify.class, ChangeStatus.class})
    private Long id;
    @NotBlank(groups = {Modify.class, Register.class})
    private String name;
    @NotBlank(groups = {Modify.class, Register.class})
    private String surname;
    private String secondSurname;
    @NotBlank(groups = {Modify.class, Register.class})
    private String curp;
    private String rfc;
    @NotBlank(groups = {Modify.class, Register.class})
    private String phone;
    private String cellphone;
    @NotBlank(groups = {Modify.class, Register.class})
    private String sex;
    private String birthDate;
    private String nss;
    @NotNull(groups = {Modify.class, Register.class})
    private Address address;
    private boolean status;

    public PersonDto() {
    }

    public PersonDto(Person person) {
        this.id = person.getId();
        this.name = person.getName();
        this.surname = person.getSurname();
        this.secondSurname = person.getSurname();
        this.curp = person.getCurp();
        this.rfc = person.getRfc();
        this.phone = person.getPhone();
        this.cellphone = person.getCellphone();
        this.sex = person.getSex();
        this.birthDate = person.getBirthDate().toString();
        this.status = person.isStatus();
        this.nss = person.getNss();
        this.address = person.getAddress();
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

    public String getSecondSurname() {
        return secondSurname;
    }

    public void setSecondSurname(String secondSurname) {
        this.secondSurname = secondSurname;
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
        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City"));
            Date date = new Date(birthDate);
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getNss() {
        return nss;
    }

    public void setNss(String nss) {
        this.nss = nss;
    }


    public interface Register {
    }

    public interface Modify {
    }

    public interface ChangeStatus {
    }

    @Override
    public String toString() {
        return "PersonDto{" +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", secondSurname='" + secondSurname + '\'' +
                ", curp='" + curp + '\'' +
                ", rfc='" + rfc + '\'' +
                ", phone='" + phone + '\'' +
                ", cellphone='" + cellphone + '\'' +
                ", sex='" + sex + '\'' +
                ", birthDate=" + birthDate +
                ", nss='" + nss + '\'' +
                ", status=" + status +
                '}';
    }
}
