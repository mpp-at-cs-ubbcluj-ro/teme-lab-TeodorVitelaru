package ro.mpp2024.repo.RepoDB;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.domain.Excursie;
import ro.mpp2024.domain.JdbcUtils;
import ro.mpp2024.repo.ExcursieRepo;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ExcursieRepoDB implements ExcursieRepo {

    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();


    public ExcursieRepoDB(Properties props){
        logger.info("Initializing ExcursieDBRepository with properties: {} ",props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public List<Excursie> findByDestinationAndDate(String destination, LocalDateTime date1, LocalDateTime date2) {
        logger.traceEntry("finding excursii with destination {} and date between {} and {}",destination,date1,date2);
        Connection con = dbUtils.getConnection();
        List<Excursie> excursies = new ArrayList<>();
        try (var preStmt = con.prepareStatement("select * from excursii where destinatie=? and data between ? and ?")) {
            preStmt.setString(1, destination);
            preStmt.setObject(2, date1);
            preStmt.setObject(3, date2);
            try (var result = preStmt.executeQuery()) {
                while (result.next()) {
                    var id = result.getLong("id");
                    var obiectiv = result.getString("destinatie");
                    var firmaTransport = result.getString("firmaTransport");
                    var dataPlecarii = result.getObject("data", LocalDateTime.class);
                    var nrLocuriDisponibile = result.getInt("nrLocuriDisponibile");
                    Excursie excursie = new Excursie(obiectiv, firmaTransport, dataPlecarii, nrLocuriDisponibile);
                    excursie.setId(id);
                    excursies.add(excursie);
                }
            }
        } catch (Exception e) {
            System.out.println("Error DB " + e);
        }
        logger.traceExit(excursies);
        return excursies;
    }

    @Override
    public Optional<Excursie> findOne(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Iterable<Excursie> findAll() {
        return null;
    }

    @Override
    public Optional<Excursie> save(Excursie entity) {
        return Optional.empty();
    }

    @Override
    public Optional<Excursie> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Excursie> update(Excursie entity) {
        return Optional.empty();
    }
}
