package turism.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Excursie extends Entity<Long> implements Serializable {
    private Long id;
    private String obiectiv;
    private String firmaTransport;
    private LocalDateTime dataPlecarii;
    private Integer nrLocuriDisponibile;
    private int pret;

    public Excursie(String obiectiv, String firmaTransport, LocalDateTime dataPlecarii, Integer nrLocuriDisponibile, int pret) {
        this.obiectiv = obiectiv;
        this.firmaTransport = firmaTransport;
        this.dataPlecarii = dataPlecarii;
        this.nrLocuriDisponibile = nrLocuriDisponibile;
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
        return dataPlecarii;
    }

    public void setDataPlecarii(LocalDateTime dataPlecarii) {
        this.dataPlecarii = dataPlecarii;
    }

    public Integer getNrLocuriDisponibile() {
        return nrLocuriDisponibile;
    }

    public void setNrLocuriDisponibile(Integer nrLocuriDisponibile) {
        this.nrLocuriDisponibile = nrLocuriDisponibile;
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
                ", dataPlecarii=" + dataPlecarii +
                ", nrLocuriDisponibile=" + nrLocuriDisponibile +
                '}';
    }
}
