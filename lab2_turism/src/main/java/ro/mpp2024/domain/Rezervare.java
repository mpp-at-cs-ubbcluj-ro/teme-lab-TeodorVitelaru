package ro.mpp2024.domain;

public class Rezervare extends Entity<Long> {
    private Long id;
    private Long idExcursie;
    private Long idClient;
    private int nrBilete;
    private Long idUser;

    public Rezervare(Long idExcursie, Long idClient, Integer nrBilete, Long idUser) {
        this.idExcursie = idExcursie;
        this.idClient = idClient;
        this.nrBilete = nrBilete;
        this.idUser = idUser;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdExcursie() {
        return idExcursie;
    }

    public void setIdExcursie(Long idExcursie) {
        this.idExcursie = idExcursie;
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    public Integer getNrBilete() {
        return nrBilete;
    }

    public void setNrBilete(Integer nrBilete) {
        this.nrBilete = nrBilete;
    }
}
