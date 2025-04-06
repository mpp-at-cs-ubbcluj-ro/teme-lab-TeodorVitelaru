package turism.services;

import turism.model.Client;
import turism.model.Excursie;
import turism.model.Rezervare;
import turism.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ITurismServices {
    public Client addClient(Client client) throws Exception;
    public Client findClientByNameAndPhoneNumber(String name, String phoneNumber) throws Exception;
    public List<Excursie> getAllExcursieByDestinationAndDate(String destination, LocalDateTime date1, LocalDateTime date2) throws Exception;
    public List<Excursie> getAllExcursie() throws Exception;
    public List<Rezervare> getRezervariByExcursie(Excursie excursie) throws Exception;
    List<Rezervare> getAllRezervari() throws Exception;
    public int getLocuriOcupateForExcursie(Excursie excursie) throws Exception;
    Rezervare addRezervare(Excursie excursie, Client client, int nrBilete, User user) throws Exception;
    public User login(User user, ITurismObserver client) throws Exception;
    void logout(User user, ITurismObserver client) throws Exception;
}
