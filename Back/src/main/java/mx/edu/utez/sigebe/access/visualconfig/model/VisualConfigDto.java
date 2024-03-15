package mx.edu.utez.sigebe.access.visualconfig.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class VisualConfigDto {
    @NotNull(groups = {Modify.class, ChangeStatus.class})
    private Long id;
    @NotBlank(groups = {Modify.class, Register.class})
    private String backgroundHeader;
    @NotBlank(groups = {Modify.class, Register.class})
    private String textHeader;
    @NotBlank(groups = {Modify.class, Register.class})
    private String backgroundAside;
    @NotBlank(groups = {Modify.class, Register.class})
    private String textAside;
    @NotBlank(groups = {Modify.class, Register.class})
    private String logoLogin;
    @NotBlank(groups = {Modify.class, Register.class})
    private String logoTop;

    public VisualConfigDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBackgroundHeader() {
        return backgroundHeader;
    }

    public void setBackgroundHeader(String backgroundHeader) {
        this.backgroundHeader = backgroundHeader;
    }

    public String getTextHeader() {
        return textHeader;
    }

    public void setTextHeader(String textHeader) {
        this.textHeader = textHeader;
    }

    public String getBackgroundAside() {
        return backgroundAside;
    }

    public void setBackgroundAside(String backgroundAside) {
        this.backgroundAside = backgroundAside;
    }

    public String getTextAside() {
        return textAside;
    }

    public void setTextAside(String textAside) {
        this.textAside = textAside;
    }

    public String getLogoLogin() {
        return logoLogin;
    }

    public void setLogoLogin(String logoLogin) {
        this.logoLogin = logoLogin;
    }

    public String getLogoTop() {
        return logoTop;
    }

    public void setLogoTop(String logoTop) {
        this.logoTop = logoTop;
    }


    public interface Register {
    }

    public interface Modify {
    }

    public interface ChangeStatus {
    }
}
