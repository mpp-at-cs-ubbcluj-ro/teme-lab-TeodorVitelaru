package turism.persistence.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import turism.model.User;
import turism.persistence.UserRepo;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserRepoDB implements UserRepo {
    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();


    public UserRepoDB(Properties props) {
        logger.info("Initializing UserDBRepository with properties: {} ",props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        logger.traceEntry("finding user with username {} and password {}",username,password);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Users where username=? and password=?")) {
            preStmt.setString(1, username);
            preStmt.setString(2, password);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Long id = result.getLong("id");
                    User user = new User(username, password);
                    user.setId(id);
                    logger.traceExit(user);
                    return user;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }
        logger.traceExit("null");
        return null;
    }

    @Override
    public User findByUsername(String username) {
        logger.traceEntry("finding user with username {}",username);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Users where username=?")) {
            preStmt.setString(1, username);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Long id = result.getLong("id");
                    String password = result.getString("password");
                    User user = new User(username, password);
                    user.setId(id);
                    logger.traceExit(user);
                    return user;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }
        logger.traceExit("null");
        return null;
    }

    @Override
    public Optional<User> findOne(Long aLong) {
        logger.traceEntry("finding user with id {}",aLong);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Users where id=?")) {
            preStmt.setLong(1, aLong);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Long id = result.getLong("id");
                    String username = result.getString("username");
                    String password = result.getString("password");
                    User user = new User(username, password);
                    user.setId(id);
                    logger.traceExit(user);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }
        logger.traceExit("null");
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        logger.traceEntry("finding all users");
        Connection con = dbUtils.getConnection();
        List<User> users = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from users")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String username = result.getString("username");
                    String password = result.getString("password");
                    User user = new User(username, password);
                    user.setId(id);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }
        logger.traceExit(users);
        return users;
    }

    @Override
    public Optional<User> save(User entity) {
        return Optional.empty();
    }

    @Override
    public Optional<User> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User entity) {
        return Optional.empty();
    }
}
