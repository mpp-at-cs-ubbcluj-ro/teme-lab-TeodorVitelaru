package ro.mpp2024.repo.RepoDB;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.domain.*;
import ro.mpp2024.repo.RezervareRepo;

import java.sql.Connection;
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
    public List<Rezervare> findByExcursieId(Long excursieId) {
        return List.of();
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
                "       e.id, e.obiectiv, e.firmaTransport, e.dataPlecarii, e.nrLocuriDisponibile, \n" +
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

                    var clientId = result.getLong(7);
                    var nume = result.getString(8);
                    var telefon = result.getString(9);

                    var nrBilete = result.getInt(10);

                    var userId = result.getLong(11);
                    var username = result.getString(12);
                    var password = result.getString(13);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime dataPlecariiLocal = LocalDateTime.parse(dataPlecarii, formatter);
                    var excursie = new Excursie(obiectiv, firmaTransport, dataPlecariiLocal, nrLocuriDisponibile);
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
        Connection con = dbUtils.getConnection();
        try (var preStmt = con.prepareStatement("insert into rezervari (idExcursie, idClient, nrBilete, idUser) values (?,?,?,?)")) {
            preStmt.setLong(1, entity.getIdExcursie());
            preStmt.setLong(2, entity.getIdClient());
            preStmt.setInt(3, entity.getNrBilete());
            preStmt.setLong(4, entity.getIdUser());
            preStmt.executeUpdate();
            return Optional.of(entity);
        } catch (Exception e) {
            System.out.println("Error DB " + e);
        }
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
}
