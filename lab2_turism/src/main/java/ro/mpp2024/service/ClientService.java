package ro.mpp2024.service;

import ro.mpp2024.domain.Client;
import ro.mpp2024.repo.ClientRepo;

import java.util.Optional;

public class ClientService {
    private ClientRepo clientRepo;

    public ClientService(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    public Optional<Client> addClient(String name, String phoneNumber) {
        try {
            return clientRepo.save(new Client(name, phoneNumber));
        } catch (Exception e) {
            System.out.println("Error adding client " + e);
        }
        return Optional.empty();
    }

    public Client findClientByNameAndPhoneNumber(String name, String phoneNumber) {
        try {
            return clientRepo.findByNumeAndTelefon(name, phoneNumber);
        } catch (Exception e) {
            System.out.println("Error finding client by name and phone number " + e);
        }
        return null;
    }
}
