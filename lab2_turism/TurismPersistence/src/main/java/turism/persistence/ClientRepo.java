package turism.persistence;



import turism.model.Client;

import java.util.List;

public interface ClientRepo extends Repository<Long, Client> {
    List<Client> findByNume(String nume);
    List<Client> findByTelefon(String telefon);
    Client findByNumeAndTelefon(String nume, String telefon);
}
