package ro.mpp2024.repo.RepoDB;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.C;
import ro.mpp2024.domain.Client;
import ro.mpp2024.domain.JdbcUtils;
import ro.mpp2024.repo.ClientRepo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ClientRepoDB implements ClientRepo {
    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();

    public ClientRepoDB(Properties props) {
        logger.info("Initializing ClientRepoDB with properties: {} ",props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public List<Client> findByNume(String nume) {
        logger.traceEntry("finding clients with name {}",nume);
        Connection con = dbUtils.getConnection();
        List<Client> clients = new ArrayList<>();
        try (var preStmt = con.prepareStatement("select * from clienti where nume=?")) {
            preStmt.setString(1, nume);
            try (var result = preStmt.executeQuery()) {
                while (result.next()) {
                    var id = result.getLong("id");
                    var telefon = result.getString("telefon");
                    var client = new Client(nume, telefon);
                    client.setId(id);
                    clients.add(client);
                }
            }
        } catch (Exception e) {
            System.out.println("Error DB " + e);
        }
        logger.traceExit(clients);
        return clients;
    }

    @Override
    public List<Client> findByTelefon(String telefon) {
        logger.traceEntry("finding clients with phone number {}",telefon);
        Connection con = dbUtils.getConnection();
        List<Client> clients = new ArrayList<>();
        try (var preStmt = con.prepareStatement("select * from clienti where telefon=?")) {
            preStmt.setString(1, telefon);
            try (var result = preStmt.executeQuery()) {
                while (result.next()) {
                    var id = result.getLong("id");
                    var nume = result.getString("nume");
                    var client = new Client(nume, telefon);
                    client.setId(id);
                    clients.add(client);
                }
            }
        } catch (Exception e) {
            System.out.println("Error DB " + e);
        }
        logger.traceExit(clients);
        return clients;
    }

    @Override
    public Optional<Client> findOne(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Iterable<Client> findAll() {
        return null;
    }

    @Override
    public Optional<Client> save(Client entity) {
        logger.traceEntry("saving client {} ",entity);
        Connection con = dbUtils.getConnection();
        try (var preStmt = con.prepareStatement("insert into clienti (nume, telefon) values (?, ?)")) {
            preStmt.setString(1, entity.getNume());
            preStmt.setString(2, entity.getTelefon());
            preStmt.executeUpdate();
            logger.info("Saved client {} ",entity);
            return Optional.of(entity);
        } catch (Exception e) {
            System.out.println("Error DB " + e);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Optional<Client> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Client> update(Client entity) {
        return Optional.empty();
    }
}
