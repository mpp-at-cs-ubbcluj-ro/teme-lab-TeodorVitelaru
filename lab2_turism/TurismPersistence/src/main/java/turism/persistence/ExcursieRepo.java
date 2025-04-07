package turism.persistence;



import turism.model.Excursie;

import java.time.LocalDateTime;
import java.util.List;

public interface ExcursieRepo extends Repository<Long, Excursie> {
    List<Excursie> findByDestinationAndDate(String destination, LocalDateTime date1, LocalDateTime date2);
    }
