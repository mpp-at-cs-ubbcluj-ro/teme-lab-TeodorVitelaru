package ro.mpp2024.repo;

import ro.mpp2024.domain.Excursie;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExcursieRepo extends Repository<Long, Excursie> {
    List<Excursie> findByDestinationAndDate(String destination, LocalDateTime date1, LocalDateTime date2);
    }
