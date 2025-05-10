package turism.persistence.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import turism.model.Excursie;
import turism.persistence.ExcursieRepo;


import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ExcursieRepoDB implements ExcursieRepo {

    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");



    public ExcursieRepoDB(Properties props){
        logger.info("Initializing ExcursieDBRepository with properties: {} ",props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public List<Excursie> findByDestinationAndDate(String obiectiv, LocalDateTime date1, LocalDateTime date2) {
        logger.traceEntry("finding excursii with destination {} and date between {} and {}",obiectiv,date1,date2);
        Connection con = dbUtils.getConnection();
        List<Excursie> excursies = new ArrayList<>();
        try (var preStmt = con.prepareStatement("select * from excursii where obiectiv=? and dataPlecarii between ? and ?")) {
            preStmt.setString(1, obiectiv);
            preStmt.setString(2, date1.format(formatter));
            preStmt.setString(3, date2.format(formatter));

            try (var result = preStmt.executeQuery()) {
                while (result.next()) {
                    var id = result.getLong("id");
                    var obiectiv2 = result.getString("obiectiv");
                    var firmaTransport = result.getString("firmaTransport");
                    var dataPlecarii = result.getString("dataPlecarii");
                    var nrLocuriDisponibile = result.getInt("nrLocuriDisponibile");
                    var pret = result.getInt("pret");
                    LocalDateTime dataPlecarii2 = LocalDateTime.parse(dataPlecarii, formatter);
                    Excursie excursie = new Excursie(obiectiv2, firmaTransport, dataPlecarii2, nrLocuriDisponibile, pret);
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
        logger.traceEntry("finding all excursii");
        Connection con = dbUtils.getConnection();
        List<Excursie> excursies = new ArrayList<>();
        try (var preStmt = con.prepareStatement("select * from excursii");
             var result = preStmt.executeQuery()) {
            while (result.next()) {
                var id = result.getLong("id");
                var obiectiv = result.getString("obiectiv");
                var firmaTransport = result.getString("firmaTransport");
                var dataPlecarii = result.getString("dataPlecarii");
                var nrLocuriDisponibile = result.getInt("nrLocuriDisponibile");
                var pret = result.getInt("pret");
                LocalDateTime dataPlecarii2 = LocalDateTime.parse(dataPlecarii, formatter);
                Excursie excursie = new Excursie(obiectiv, firmaTransport, dataPlecarii2, nrLocuriDisponibile, pret);
                excursie.setId(id);
                excursies.add(excursie);
            }
            logger.traceExit(excursies);
            return excursies;
        } catch (Exception e) {
            System.out.println("Error DB " + e);
        }
        logger.traceExit("null");
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
