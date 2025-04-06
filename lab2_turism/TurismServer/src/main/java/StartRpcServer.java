import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import turism.network.utils.TurismRpcConcurrentServer;
import turism.persistence.ClientRepo;
import turism.persistence.ExcursieRepo;
import turism.persistence.RezervareRepo;
import turism.persistence.UserRepo;
import turism.persistence.repository.ClientRepoDB;
import turism.persistence.repository.ExcursieRepoDB;
import turism.persistence.repository.RezervareRepoDB;
import turism.persistence.repository.UserRepoDB;
import turism.server.TurismServicesImpl;
import turism.services.ITurismServices;
import turism.network.utils.AbstractServer;

import java.io.File;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort = 55555;
    private static Logger logger = LogManager.getLogger(StartRpcServer.class);

    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/turismserver.properties"));
            logger.info("Server properties set {}", serverProps);
        } catch (Exception e) {
            logger.error("Error starting server: " + e.getMessage());
            logger.debug("Looking into folder {}", new File(".").getAbsolutePath());
            return;
        }
        UserRepo userRepo = new UserRepoDB(serverProps);
        ClientRepo clientRepo = new ClientRepoDB(serverProps);
        ExcursieRepo excursieRepo = new ExcursieRepoDB(serverProps);
        RezervareRepo rezervareRepo = new RezervareRepoDB(serverProps);

        ITurismServices server = new TurismServicesImpl(clientRepo, excursieRepo, rezervareRepo, userRepo);
        int serverPort = defaultPort;
        try{
            serverPort = Integer.parseInt(serverProps.getProperty("turism.server.port"));
        } catch (NumberFormatException e) {
            logger.error("Wrong port number " + serverProps.getProperty("turism.server.port"));
            logger.debug("Using default port {}", defaultPort);
        }
        logger.info("Starting server on port {}", serverPort);

        AbstractServer server1 = new TurismRpcConcurrentServer(serverPort, server);
        try {
            server1.start();
        } catch (Exception e) {
            logger.error("Error starting the server " + e.getMessage());
        } finally {
            try {
                server1.stop();
            } catch (Exception e) {
                logger.error("Error stopping server " + e.getMessage());
            }
        }

    }
}
