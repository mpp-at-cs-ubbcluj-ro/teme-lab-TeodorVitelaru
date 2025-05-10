package turism.persistence;



import turism.model.Excursie;
import turism.model.Rezervare;

import java.util.List;

public interface RezervareRepo extends Repository<Long, Rezervare> {
    List<Rezervare> findByClientNume(String nume);
    List<Rezervare> findByExcursie(Excursie excursie);

}
