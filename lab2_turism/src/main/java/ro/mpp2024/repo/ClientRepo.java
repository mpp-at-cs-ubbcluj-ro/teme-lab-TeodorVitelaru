package ro.mpp2024.repo;

import ro.mpp2024.domain.Client;

import java.util.List;

public interface ClientRepo extends Repository<Long, Client> {
    List<Client> findByNume(String nume);
    List<Client> findByTelefon(String telefon);
    Client findByNumeAndTelefon(String nume, String telefon);
}
