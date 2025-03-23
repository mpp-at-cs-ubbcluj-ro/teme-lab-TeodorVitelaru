package ro.mpp2024.service;

import ro.mpp2024.domain.Client;
import ro.mpp2024.domain.Excursie;
import ro.mpp2024.domain.Rezervare;
import ro.mpp2024.domain.User;
import ro.mpp2024.repo.RezervareRepo;
import ro.mpp2024.utils.events.ChangeEventType;
import ro.mpp2024.utils.events.EntityChangeEvent;
import ro.mpp2024.utils.observer.Observable;
import ro.mpp2024.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class RezervareService  implements Observable<EntityChangeEvent> {
    private RezervareRepo rezervareRepo;
    private List<Observer<EntityChangeEvent>> observers = new ArrayList<>();


    @Override
    public void addObserver(Observer<EntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<EntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(EntityChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }

    public RezervareService(RezervareRepo rezervareRepo) {
        this.rezervareRepo = rezervareRepo;
    }

    public List<Rezervare> findRezervareByExcursie(Excursie excursie) {
        try {
            return rezervareRepo.findByExcursie(excursie);
        } catch (Exception e) {
            System.out.println("Error finding rezervare by excursie " + e);
        }
        return null;
    }

    public int getLocuriOcupateForExcursie(Excursie excursie) {
        int rezervari = 0;
        try {
            List<Rezervare> rezervariList = rezervareRepo.findByExcursie(excursie);
            for (Rezervare rezervare : rezervariList) {
                rezervari += rezervare.getNrBilete();
            }
            return rezervari;
        } catch (Exception e) {
            System.out.println("Error getting locuri ocupate for excursie " + e);
        }
        return 0;
    }

    public void addRezervare(Excursie excursie, Client client, int nrBilete, User user) {
        try {
            Rezervare rezervare = new Rezervare(excursie, client, nrBilete, user);
            rezervareRepo.save(rezervare);
            EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.ADD, rezervare);
            notifyObservers(event);
        } catch (Exception e) {
            System.out.println("Error adding rezervare " + e);
        }
    }
}
