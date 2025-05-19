package turism.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "excursii")
public class Excursie extends MyEntity<Long> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "obiectiv")
    private String obiectiv;

    @Column(name = "firmaTransport")
    private String firmaTransport;

    @Column(name = "dataPlecarii")
    @Convert(converter = LocalDateTimeStringConverter.class)
    private LocalDateTime oraPlecarii;

    @Column(name = "nrLocuriDisponibile")
    private int numarLocuri;

    @Column(name = "pret")
    private int pret;

    public Excursie() {

    }

    public Excursie(String obiectiv, String firmaTransport, LocalDateTime dataPlecarii, Integer nrLocuriDisponibile, int pret) {
        this.obiectiv = obiectiv;
        this.firmaTransport = firmaTransport;
        this.oraPlecarii = dataPlecarii;
        this.numarLocuri = nrLocuriDisponibile;
        this.pret = pret;
    }

    public Excursie(String obiectiv, int pret, String firmaTransport, LocalDateTime oraPlecarii, int numarLocuri)
    {
        this.obiectiv = obiectiv;
        this.firmaTransport = firmaTransport;
        this.oraPlecarii = oraPlecarii;
        this.numarLocuri = numarLocuri;
        this.pret = pret;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObiectiv() {
        return obiectiv;
    }

    public void setObiectiv(String obiectiv) {
        this.obiectiv = obiectiv;
    }

    public String getFirmaTransport() {
        return firmaTransport;
    }

    public void setFirmaTransport(String firmaTransport) {
        this.firmaTransport = firmaTransport;
    }

    public LocalDateTime getDataPlecarii() {
        return oraPlecarii;
    }

    public void setDataPlecarii(LocalDateTime dataPlecarii) {
        this.oraPlecarii = dataPlecarii;
    }

    public Integer getNrLocuriDisponibile() {
        return numarLocuri;
    }

    public void setNrLocuriDisponibile(Integer nrLocuriDisponibile) {
        this.numarLocuri = nrLocuriDisponibile;
    }

    public int getPret() {
        return pret;
    }

    public void setPret(int pret) {
        this.pret = pret;
    }

    @Override
    public String toString() {
        return "Excursie{" +
                "id=" + id +
                ", obiectiv='" + obiectiv + '\'' +
                ", firmaTransport='" + firmaTransport + '\'' +
                ", dataPlecarii=" + oraPlecarii +
                ", nrLocuriDisponibile=" + numarLocuri +
                ", pret=" + pret +
                '}';
    }
}