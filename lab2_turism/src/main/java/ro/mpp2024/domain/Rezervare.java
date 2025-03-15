package ro.mpp2024.domain;

public class Rezervare extends Entity<Long> {
    private Long id;
    private Excursie excursie;
    private Client client;
    private int nrBilete;
    private User user;

    public Rezervare(Excursie excursie, Client client, Integer nrBilete, User user) {
        this.excursie = excursie;
        this.client = client;
        this.nrBilete = nrBilete;
        this.user = user;
    }

    public Excursie getExcursie() {
        return excursie;
    }

    public void setExcursie(Excursie excursie) {
        this.excursie = excursie;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNrBilete() {
        return nrBilete;
    }

    public void setNrBilete(Integer nrBilete) {
        this.nrBilete = nrBilete;
    }

    public Long getIdExcursie() {
        return excursie.getId();
    }

    public Long getIdClient() {
        return client.getId();
    }

    public Long getIdUser() {
        return user.getId();
    }

    @Override
    public String toString() {
        return "Rezervare{" +
                "id=" + id +
                ", excursie=" + excursie +
                ", client=" + client +
                ", nrBilete=" + nrBilete +
                ", user=" + user +
                '}';
    }
}
