package ro.mpp2024.repo;

import ro.mpp2024.domain.Rezervare;

import java.util.List;

public interface RezervareRepo extends Repository<Long, Rezervare> {
    List<Rezervare> findByExcursieId(Long excursieId);
    List<Rezervare> findByClientNume(String nume);
}
