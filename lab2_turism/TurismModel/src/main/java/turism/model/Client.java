package turism.model;

import java.io.Serializable;

public class Client extends  Entity<Long> implements Serializable {
    private Long id;
    private String nume;
    private String telefon;

    public Client(String nume, String telefon) {
        this.nume = nume;
        this.telefon = telefon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }


    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", nume='" + nume + '\'' +
                ", telefon='" + telefon + '\'' +
                '}';
    }
}
