package turism.persistence.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import turism.model.Client;
import turism.model.Excursie;
import turism.model.Rezervare;
import turism.model.User;
import turism.persistence.RezervareRepo;


import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RezervareRepoDB implements RezervareRepo {

    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();


    public RezervareRepoDB(Properties props) {
        logger.info("Initializing RezervareDBRepository with properties: {} ",props);
        dbUtils = new JdbcUtils(props);
    }


    @Override
    public List<Rezervare> findByClientNume(String nume) {
        return List.of();
    }

    @Override
    public Optional<Rezervare> findOne(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Iterable<Rezervare> findAll() {
        logger.traceEntry("finding all rezervari");
        Connection con = dbUtils.getConnection();
        List<Rezervare> rezervari = new ArrayList<>();
        String queryString = "select r.id, \n" +
                "       e.id, e.obiectiv, e.firmaTransport, e.dataPlecarii, e.nrLocuriDisponibile e.pret, \n" +
                "       c.id, c.nume, c.telefon,\n" +
                "       r.nrBilete,\n" +
                "       u.id, u.username, u.password\n" +
                "       from rezervari as r\n" +
                "inner join clienti as c on r.id_client = c.id\n" +
                "inner join excursii as e on r.id_excursie = e.id\n" +
                "inner join users as u on r.id_user = u.id";
        try (var preStmt = con.prepareStatement(queryString)) {
            try (var result = preStmt.executeQuery()) {
                while (result.next()) {
                    var rezervareId = result.getLong(1);

                    var excursieId = result.getLong(2);
                    var obiectiv = result.getString(3);
                    var firmaTransport = result.getString(4);
                    var dataPlecarii = result.getString(5);
                    var nrLocuriDisponibile = result.getInt(6);
                    var pret = result.getInt(7);

                    var clientId = result.getLong(8);
                    var nume = result.getString(9);
                    var telefon = result.getString(10);

                    var nrBilete = result.getInt(11);

                    var userId = result.getLong(12);
                    var username = result.getString(13);
                    var password = result.getString(14);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime dataPlecariiLocal = LocalDateTime.parse(dataPlecarii, formatter);
                    var excursie = new Excursie(obiectiv, firmaTransport, dataPlecariiLocal, nrLocuriDisponibile, pret);
                    excursie.setId(excursieId);

                    var client = new Client(nume, telefon);
                    client.setId(clientId);

                    var user = new User(username, password);
                    user.setId(userId);

                    var rezervare = new Rezervare(excursie, client, nrBilete, user);
                    rezervare.setId(rezervareId);
                    rezervari.add(rezervare);
                }
            }
        } catch (Exception e) {
            System.out.println("Error DB " + e);
        }
        return rezervari;
    }

    @Override
    public Optional<Rezervare> save(Rezervare entity) {
        logger.traceEntry("saving rezervare {}", entity);
        Connection con = dbUtils.getConnection();
        try (var preStmt = con.prepareStatement("insert into rezervari (id_excursie, id_client, nrBilete, id_user) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            preStmt.setLong(1, entity.getIdExcursie());
            preStmt.setLong(2, entity.getIdClient());
            preStmt.setInt(3, entity.getNrBilete());
            preStmt.setLong(4, entity.getIdUser());
            preStmt.executeUpdate();

            try (var result = preStmt.getGeneratedKeys()) {
                if (result.next()) {
                    var id = result.getLong(1);
                    System.out.println("rezervare id: " + id);
                    entity.setId(id);
                }
            }
            logger.traceExit(entity);
            return Optional.of(entity);
        } catch (Exception e) {
            System.out.println("Error DB " + e);
        }
        logger.traceExit("null");
        return Optional.empty();
    }

    @Override
    public Optional<Rezervare> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Rezervare> update(Rezervare entity) {
        return Optional.empty();
    }

    @Override
    public List<Rezervare> findByExcursie(Excursie excursie) {
        Connection con = dbUtils.getConnection();
        List<Rezervare> rezervari = new ArrayList<>();
        String queryString = "select r.id, \n" +
                "       c.id, c.nume, c.telefon,\n" +
                "       r.nrBilete,\n" +
                "       u.id, u.username, u.password\n" +
                "       from rezervari as r\n" +
                "inner join clienti as c on r.id_client = c.id\n" +
                "inner join excursii as e on r.id_excursie = e.id\n" +
                "inner join users as u on r.id_user = u.id\n" +
                "where e.id = ?";
        try (var preStmt = con.prepareStatement(queryString)) {
            preStmt.setLong(1, excursie.getId());
            try (var result = preStmt.executeQuery()) {
                while (result.next()) {
                    var rezervareId = result.getLong(1);

                    var clientId = result.getLong(2);
                    var nume = result.getString(3);
                    var telefon = result.getString(4);

                    var nrBilete = result.getInt(5);

                    var userId = result.getLong(6);
                    var username = result.getString(7);
                    var password = result.getString(8);

                    var client = new Client(nume, telefon);
                    client.setId(clientId);

                    var user = new User(username, password);
                    user.setId(userId);

                    var rezervare = new Rezervare(excursie, client, nrBilete, user);
                    rezervare.setId(rezervareId);
                    rezervari.add(rezervare);
                }
                return rezervari;
            }
        } catch (Exception e) {
            System.out.println("Error DB " + e);
        }
        return null;
    }
}
