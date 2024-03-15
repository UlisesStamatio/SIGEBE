package mx.edu.utez.sigebe.access.visualconfig.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "visual_config")
public class VisualConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "background_header", columnDefinition = "VARCHAR(50) NOT NULL")
    private String backgroundHeader;
    @Column(name = "text_header", columnDefinition = "VARCHAR(50) NOT NULL")
    private String textHeader;
    @Column(name = "background_aside", columnDefinition = "VARCHAR(50) NOT NULL")
    private String backgroundAside;
    @Column(name = "text_aside", columnDefinition = "VARCHAR(50) NOT NULL")
    private String textAside;
    @Column(name = "logoLogin", columnDefinition = "TEXT NOT NULL")
    private String logoLogin;
    @Column(name = "logoTop", columnDefinition = "TEXT NOT NULL")
    private String logoTop;

    @Column(name = "status", columnDefinition = "BOOLEAN DEFAULT true")
    private boolean status = true;
    @Column(name = "create_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public VisualConfig() {
    }

    public VisualConfig(String backgroundHeader, String textHeader, String backgroundAside, String textAside, String logoLogin, String logoTop,boolean status) {
        this.backgroundHeader = backgroundHeader;
        this.textHeader = textHeader;
        this.backgroundAside = backgroundAside;
        this.textAside = textAside;
        this.logoLogin = logoLogin;
        this.logoTop = logoTop;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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


    public void assign(VisualConfigDto dto) {
        this.backgroundHeader = dto.getBackgroundHeader();
        this.textHeader = dto.getTextHeader();
        this.backgroundAside = dto.getBackgroundAside();
        this.textAside = dto.getTextAside();
        this.logoLogin = dto.getLogoLogin();
        this.logoTop = dto.getLogoTop();
    }
}
