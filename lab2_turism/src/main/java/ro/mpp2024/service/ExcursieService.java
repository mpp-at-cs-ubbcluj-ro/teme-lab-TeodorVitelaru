package ro.mpp2024.service;

import ro.mpp2024.domain.Excursie;
import ro.mpp2024.repo.ExcursieRepo;
import ro.mpp2024.utils.events.EntityChangeEvent;
import ro.mpp2024.utils.observer.Observable;
import ro.mpp2024.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExcursieService {
    private ExcursieRepo excursieRepo;
    private List<Observer<EntityChangeEvent>> observers = new ArrayList<>();


    public ExcursieService(ExcursieRepo excursieRepo) {
        this.excursieRepo = excursieRepo;
    }

    public List<Excursie> getAllExcursieByDestinationAndDate(String destination, LocalDateTime date1, LocalDateTime date2) {
        try {
            return excursieRepo.findByDestinationAndDate(destination, date1, date2);
        } catch (Exception e) {
            System.out.println("Error finding excursie by destination " + e);
        }
        return null;
    }




}
